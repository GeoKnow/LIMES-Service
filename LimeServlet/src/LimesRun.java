import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import de.uni_leipzig.simba.controller.Controller;

/**
 * Servlet implementation class LIMESERVE
 */
public class LimesRun extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static String filePath;
	
	// Options for writing the config file
	static String configFile;
	static String configTemplate;
	static String outputFormat;
	static String execType;
	
	// Prefixes
	static String[] prefixes = {"http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf", "http://www.w3.org/2000/01/rdf-schema#", 
		"rdfs", "http://xmlns.com/foaf/0.1/", "foaf", "http://www.w3.org/2002/07/owl#", "owl", "http://www.opengis.net/ont/geosparql#", "geos",
		"http://dbpedia.org/ontology/", "dbpedia", "http://geovocab.org/geometry#", "geom", "http://linkedgeodata.org/ontology/", "lgdo",
		"http://dbpedia.org/resource/", "dbresource", "http://purl.org/dc/terms/", "dc", 
		"http://dbpedia.org/property/", "dbpedia2", "http://www.w3.org/2004/02/skos/core#", "skos"};
	
	static String[] source = {"Source", "", "", "", "", ""};
	static String[] target = {"Target", "", "", "", "", ""};
	// Properties
	static String metric;
	// Advanced settings
	static String[] acceptance = {"", "", ""};
	static String[] review = {"", "", ""};
	   

	   public void init( ){
		   System.setProperty("file.encoding","UTF-8");
	      filePath = getServletContext().getRealPath("/");
	      configFile = filePath+"/config/config.xml";
	  	  configTemplate = filePath+"/config/default.xml";
	   }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Hello World!</TITLE>"
		+ "</HEAD><BODY>Hello World!!!</BODY></HTML>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	source[1] = request.getParameter("SourceServiceURI");
    	target[1] = request.getParameter("TargetServiceURI");
    	source[2] = request.getParameter("SourceVar");
    	target[2] = request.getParameter("TargetVar");
    	source[3]  = request.getParameter("SourceSize");
    	target[3] = request.getParameter("TargetSize");
    	source[4]  = request.getParameter("SourceRestr");
    	target[4] = request.getParameter("TargetRestr");
    	source[5]  = request.getParameter("SourceProp");
    	target[5] = request.getParameter("TargetProp");
    	metric = request.getParameter("Metric");
    	outputFormat = request.getParameter("OutputFormat");
    	execType = request.getParameter("ExecType");
    	acceptance[0] = request.getParameter("AcceptThresh");
    	review[0] = request.getParameter("ReviewThresh");
    	acceptance[1] = filePath+"/result/accepted.nt";
    	review[1] = filePath+"/result/reviewme.nt";
    	acceptance[2] = request.getParameter("AcceptRelation");
    	review[2] = request.getParameter("ReviewRelation");
    	
    	writeConfig();
    	executeLimes(configFile);
    	
	}
    
    public static void writeConfig(){
		  try {
			  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			  DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			  // create doc
			  Document doc = docBuilder.newDocument();
			  DOMImplementation domImpl = doc.getImplementation();
			  DocumentType doctype = domImpl.createDocumentType("LIMES_SYSTEM", null, filePath+"\\config\\limes.dtd");
			  doc.appendChild(doctype);
			  Element limes = doc.createElement("LIMES");
			  doc.appendChild(limes);
			  
			  for(int i=0; i<prefixes.length; i++){
					    Element prefix = doc.createElement("PREFIX");
					    Element ns = doc.createElement("NAMESPACE");
					    Element label = doc.createElement("LABEL");
				  		ns.setTextContent(prefixes[i]);
				  		i++;
				  		label.setTextContent(prefixes[i]);
				  		prefix.appendChild(ns);
				  		prefix.appendChild(label);
				  		limes.appendChild(prefix);
			  			}
			  
			  Element sid = doc.createElement("ID");
			  Element sep = doc.createElement("ENDPOINT");
			  Element svar = doc.createElement("VAR");
			  Element sps = doc.createElement("PAGESIZE");
			  Element srestr = doc.createElement("RESTRICTION");
			  Element sprop = doc.createElement("PROPERTY");
			  
			  Element tid = doc.createElement("ID");
			  Element tep = doc.createElement("ENDPOINT");
			  Element tvar = doc.createElement("VAR");
			  Element tps = doc.createElement("PAGESIZE");
			  Element trestr = doc.createElement("RESTRICTION");
			  Element tprop = doc.createElement("PROPERTY");
			  
			  Element[] sourcetags = {sid, sep, svar, sps, srestr, sprop};
			  Element[] targettags = {tid, tep, tvar, tps, trestr, tprop};
			  
			  Element sourceTag = doc.createElement("SOURCE");
			  
			  for(int i=0; i<source.length; i++){
				  Element tag = sourcetags[i];
				  tag.setTextContent(source[i]);
				  sourceTag.appendChild(tag);
			  }

			  limes.appendChild(sourceTag);
			  
			  Element targetTag = doc.createElement("TARGET");
			  
			  for(int i=0; i<target.length; i++){
				  Element tag = targettags[i];
				  tag.setTextContent(target[i]);
				  targetTag.appendChild(tag);
			  }

			  limes.appendChild(targetTag);
			  
			  Element metricTag = doc.createElement("METRIC");
			  metricTag.setTextContent(metric);
			  limes.appendChild(metricTag);
			  
			  Element athr = doc.createElement("THRESHOLD");
			  Element afile = doc.createElement("FILE");
			  Element arel = doc.createElement("RELATION");
			  
			  Element rthr = doc.createElement("THRESHOLD");
			  Element rfile = doc.createElement("FILE");
			  Element rrel = doc.createElement("RELATION");
			  
			  Element[] acctags = {athr, afile, arel};
			  Element[] revtags = {rthr, rfile, rrel};
			  
			  Element accTag = doc.createElement("ACCEPTANCE");
			  
			  for(int i=0; i<acceptance.length; i++){
				  Element tag = acctags[i];
				  tag.setTextContent(acceptance[i]);
				  accTag.appendChild(tag);
			  }

			  limes.appendChild(accTag);
			  
			  Element revTag = doc.createElement("REVIEW");
			  
			  for(int i=0; i<review.length; i++){
				  Element tag = revtags[i];
				  tag.setTextContent(review[i]);
				  revTag.appendChild(tag);
			  }

			  limes.appendChild(revTag);
			  
			  // emit
			  //System.out.println(((DOMImplementationLS) domImpl).createLSSerializer()
			  //    .writeToString(doc));
			  
			  // write the content into xml file
			  DOMSource source = new DOMSource(doc);
		      StreamResult result = new StreamResult(new File(configFile));
		         
		      TransformerFactory transformerFactory = TransformerFactory.newInstance();
		      Transformer transformer = transformerFactory.newTransformer();
		      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		      
		      // Preserve doctype declaration
		      if(doctype != null) {
		            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
		        }
		         
		      transformer.transform(source, result);
		 
			  //System.out.println("Done");
			  
			   } catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			   } catch (TransformerException tfe) {
				tfe.printStackTrace();
			   } 
			}
    
    // Start LIMES with the configfile
 	public static void executeLimes(String configFile){
 		Controller.run(configFile);
 	}

}
