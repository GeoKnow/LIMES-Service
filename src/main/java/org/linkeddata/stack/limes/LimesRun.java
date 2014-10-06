package org.linkeddata.stack.limes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.Document;  
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;


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
		"http://dbpedia.org/resource/", "dbresource", "http://purl.org/dc/terms/", "dc", "http://geoknow.eu/geodata#", "geoknow",
		"http://dbpedia.org/property/", "dbpedia2", "http://www.w3.org/2004/02/skos/core#", "skos",
		"http://wiktionary.dbpedia.org/terms/", "wktrm", "http://lexvo.org/ontology#", "lexvo",
		"http://dbpedia.org/ontology/", "dbpedia-owl", "http://ld.geoknow.eu/flights/ontology/", "ld",
		"http://purl.org/acco/ns#", "cco", "http://www.w3.org/2003/01/geo/wgs84_pos#", "geo",
		"http://data.admin.ch/vocab/", "gz", "http://www.ontos.com/ch/", "on",
		"http://data.admin.ch/bfs/class/1.0/", "gzp"};

	static List<String> source = new ArrayList<String>();
	static List<String> target = new ArrayList<String>();
	// Properties
	static String metric;
	static String granularity;
	// Advanced settings
	static String[] acceptance = {"", "", ""};
	static String[] review = {"", "", ""};


	public void init( ){
		filePath = getServletContext().getRealPath(File.separator);
		configFile = filePath+"config"+File.separator+"config.xml";
		configTemplate = filePath+"config"+File.separator+"default.xml";

		File resultDir = new File(filePath + "result");
		if (!resultDir.exists()) {
			resultDir.mkdirs();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws  IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Hello World!</TITLE>"
				+ "</HEAD><BODY>Hello World!!!</BODY></HTML>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws  IOException {
		
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		ObjectMapper mapper = new ObjectMapper();
		
		source.clear();
		target.clear();
		source.add("Source");
		target.add("Target");
		
		JsonResponse res = new JsonResponse();
		String numberOfPropsString = request.getParameter("numberOfProps");
		int numberOfProps = Integer.parseInt(numberOfPropsString);
		
		try{
			source.add(request.getParameter("SourceServiceURI"));
			target.add(request.getParameter("TargetServiceURI"));
			source.add(request.getParameter("SourceGraph"));
			target.add(request.getParameter("TargetGraph"));
			source.add(request.getParameter("SourceVar"));
			target.add(request.getParameter("TargetVar"));
			source.add(request.getParameter("SourceSize"));
			target.add(request.getParameter("TargetSize"));
			source.add(request.getParameter("SourceRestr"));
			target.add(request.getParameter("TargetRestr"));
			
			for(int i=0; i<numberOfProps; i++){
				source.add(request.getParameter("SourceProp"+i));
				target.add(request.getParameter("TargetProp"+i));
			}
			
			metric = request.getParameter("Metric");
			granularity = request.getParameter("Granularity");
			outputFormat = request.getParameter("OutputFormat");
			execType = request.getParameter("ExecType");
			acceptance[0] = request.getParameter("AcceptThresh");
			review[0] = request.getParameter("AcceptThresh");
			acceptance[1] = filePath+"result"+File.separator+"accepted.nt";
			review[1] = filePath+"result"+File.separator+"reviewme.nt";
			acceptance[2] = request.getParameter("AcceptRelation");
			review[2] = request.getParameter("AcceptRelation");

			writeConfig();
			executeLimes(configFile);
			res.setStatus("SUCCESS");
			res.setMessage("LIMES process ended succesfully");

		} catch (Exception e) {
			res.setStatus("FAIL");
			res.setMessage(e.getMessage());
			e.printStackTrace();
		} 
		mapper.writeValue(out, res);
	    out.close();
	}

	public static void writeConfig(){
		try {
			
			Element root = new Element("LIMES");
			DocType dtype = new DocType("LIMES", null, filePath+"config"+File.separator+"limes.dtd");
			Document doc = new Document(root, dtype);


			for(int i=0; i<prefixes.length; i++){
				Element prefix = new Element("PREFIX");
				Element ns = new Element("NAMESPACE");
				Element label = new Element("LABEL");
				ns.setText(prefixes[i]);
				i++;
				label.setText(prefixes[i]);
				prefix.addContent(ns);
				prefix.addContent(label);
				root.addContent(prefix);
			}

			Element sid = new Element("ID");
			Element sep = new Element("ENDPOINT");
			Element sgr = new Element("GRAPH");
			Element svar = new Element("VAR");
			Element sps = new Element("PAGESIZE");
			Element srestr = new Element("RESTRICTION");
			Element sprop = new Element("PROPERTY");

			Element tid = new Element("ID");
			Element tep = new Element("ENDPOINT");
			Element tgr = new Element("GRAPH");
			Element tvar = new Element("VAR");
			Element tps = new Element("PAGESIZE");
			Element trestr = new Element("RESTRICTION");
			Element tprop = new Element("PROPERTY");

			Element[] sourcetags = {sid, sep, sgr, svar, sps, srestr, sprop};
			Element[] targettags = {tid, tep, tgr, tvar, tps, trestr, tprop};

			Element sourceTag = new Element("SOURCE");

			for(int i=0; i<source.size(); i++){
				Element tag = null;
				if(i >= sourcetags.length){
					tag = new Element("PROPERTY");
				}else{
					tag = sourcetags[i];
				}
				tag.setText(source.get(i));
				sourceTag.addContent(tag);
			}

			root.addContent(sourceTag);

			Element targetTag = new Element("TARGET");

			for(int i=0; i<target.size(); i++){
				Element tag = null;
				if(i >= targettags.length){
					tag = new Element("PROPERTY");
				}else{
					tag = targettags[i];
				}
				tag.setText(target.get(i));
				targetTag.addContent(tag);
			}

			root.addContent(targetTag);

			Element metricTag = new Element("METRIC");
			metricTag.setText(metric);
			root.addContent(metricTag);

			Element athr = new Element("THRESHOLD");
			Element afile = new Element("FILE");
			Element arel = new Element("RELATION");

			Element rthr = new Element("THRESHOLD");
			Element rfile = new Element("FILE");
			Element rrel = new Element("RELATION");

			Element[] acctags = {athr, afile, arel};
			Element[] revtags = {rthr, rfile, rrel};

			Element accTag = new Element("ACCEPTANCE");

			for(int i=0; i<acceptance.length; i++){
				Element tag = acctags[i];
				tag.setText(acceptance[i]);
				accTag.addContent(tag);
			}

			root.addContent(accTag);

			Element revTag = new Element("REVIEW");

			for(int i=0; i<review.length; i++){
				Element tag = revtags[i];
				tag.setText(review[i]);
				revTag.addContent(tag);
			}

			root.addContent(revTag);
			
			Element execTypeTag = new Element("EXECUTION");
			execTypeTag.setText(execType);
			root.addContent(execTypeTag);
			
			Element granTag = new Element("GRANULARITY");
			granTag.setText(granularity);
			root.addContent(granTag);
			
			Element outputFormatTag = new Element("OUTPUT");
			outputFormatTag.setText(outputFormat);
			root.addContent(outputFormatTag);
			
			XMLOutputter xmlOutput = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setExpandEmptyElements(true);
			format.setIndent("  ");
			format.setTextMode(Format.TextMode.TRIM);
			xmlOutput.setFormat(format);
			xmlOutput.output(doc, new FileWriter(configFile));

			System.out.println("Done");

		} catch (IOException io) {
			System.out.println(io.getMessage());
		}
	}

	// Start LIMES with the configfile
	public static void executeLimes(String configFile) throws IOException{
		
		System.out.println("java -jar \""+filePath+"WEB-INF"+File.separator+
 				"lib"+File.separator+"limes-0.6.5.jar\" \""+configFile+"\"");
 		Process proc = Runtime.getRuntime().exec("java -jar \""+filePath+"WEB-INF"+File.separator+
 				"lib"+File.separator+"limes-0.6.5.jar\" \""+configFile+"\"");
 		InputStream in = proc.getInputStream();
	 	InputStream err = proc.getErrorStream();
	 	String line;
	 	BufferedReader input = new BufferedReader(new InputStreamReader(in));
	 	  while ((line = input.readLine()) != null) {
	 	    System.out.println(line);
	 	  }
	 	input = new BufferedReader(new InputStreamReader(err));
	 	  while ((line = input.readLine()) != null) {
	 	    System.out.println(line);
	 	  }
	 	input.close();
 		
		//Controller.run(configFile);
	}
	
	/*
	private static String getValidParameter(HttpServletRequest request, String param_name){
		if(request.getParameter(param_name)!=null)
				return request.getParameter(param_name);
			else{
				//throw new IllegalArgumentException("Missing parameter "+ param_name);
				return request.getParameter(param_name);
		} 
			
	}
	*/

}
