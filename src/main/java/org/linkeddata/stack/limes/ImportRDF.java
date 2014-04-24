package org.linkeddata.stack.limes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.*;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;


public class ImportRDF extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String rdfUrl;
	private String rdfFile;
	private String endpoint;
	private static String uriBase;
//	private String graph;
//	private String rdfQuery;
//	private String rdfQueryEndpoint;
	private String jobName;
	private String reviewGraph;
	private String acceptedGraph;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
	      doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		response.setContentType("application/json");
	    
		PrintWriter out = response.getWriter();
		
		// get the paht where files were uploaded 
		String filePath = getServletContext().getRealPath(File.separator);
		JsonResponse res = new JsonResponse();
	    ObjectMapper mapper = new ObjectMapper();
	    
	    uriBase   = request.getParameter("uriBase");
		rdfUrl   = request.getParameter("rdfUrl");
		endpoint = request.getParameter("endpoint");
//	    graph    = request.getParameter("graph");
//	    rdfFile = request.getParameter("rdfFile");
	    //rdfQuery    = request.getParameter("rdfQuery");
	    //rdfQueryEndpoint = request.getParameter("rdfQueryEndpoint");
	    
	    String review = filePath+"result"+File.separator+"reviewme.nt";
    	String accepted = filePath+"result"+File.separator+"accepted.nt";
    	
    	try {
    		accepted = "file:///"+accepted.replace("\\", "/");
			review = "file:///"+review.replace("\\", "/");
			System.out.println("import    " +accepted + " " + review);
			
			Model modelre = ModelFactory.createDefaultModel() ; 
			RDFReader readerre = modelre.getReader("N3");
			readerre.read(modelre, review);
			
			Model model = ModelFactory.createDefaultModel() ; 
			RDFReader reader = model.getReader("N3");
			reader.read(model, accepted);
			
			jobName = "LIMES";
			UUID idReview = UUID.randomUUID();
		    UUID idAccepted = UUID.randomUUID();
		    reviewGraph = uriBase +idReview+"-"+jobName+"-review";
		    acceptedGraph = uriBase +idAccepted+"-"+jobName+"-accepted";

		    String message ="";
		    
		   if(model.isEmpty()!=true){
		    	httpCreate(endpoint, acceptedGraph);
		    	int inserted = httpUpdate(endpoint, acceptedGraph , model);
 				res.setStatus("SUCCESS");
 				res.addResult(acceptedGraph);
 				message += "Imported "+inserted+" accepted triples into the graph: "+acceptedGraph + "\n";
		    }
		   
		   
		    if(!modelre.isEmpty()){
		    	httpCreate(endpoint, reviewGraph);
		    	int inserted = httpUpdate(endpoint, reviewGraph , modelre);
 				res.setStatus("SUCCESS");
 				res.addResult(reviewGraph);
 				message += "Imported "+inserted+" triples for reviewing into the graph: "+reviewGraph;
		    }
		    
		   res.setMessage(message);
	
    	
 		} catch (Exception e) {
				res.setStatus("FAIL");
				res.setMessage(e.getMessage());
				e.printStackTrace();
		}    
        	
    	mapper.writeValue(out, res);
	    out.close();
	 }

//	private static void jdbcUpdate(String file, String graph) throws ClassNotFoundException, SQLException{
//		UpdateJDBC ujdbc = new UpdateJDBC(jdbcConnection, jdbcUser, jdbcPassword);
//		ujdbc.loadLocalFile(file, graph);
//	}

	private static int queryImport(String destEndpoint, String graph, String sourceEndpoint, String sparql) throws Exception{
		 Query query = QueryFactory.create(sparql);
	     QueryExecution qexec = QueryExecutionFactory.sparqlService(sourceEndpoint, query);
	     Model model = qexec.execConstruct();
	     int inserted = httpUpdate(destEndpoint, graph, model);
	     qexec.close() ;
	     return inserted;
	}
	
	private static int httpUpdate(String endpoint, String graph, Model model) throws Exception{
		
		// generate queries of 100 lines each
		StmtIterator stmts = model.listStatements();
		int linesLimit=100, linesCount=0, total=0;
		HashMap<String, String> blancNodes = new HashMap<String,String>();
		
		Model tmpModel = ModelFactory.createDefaultModel();
		
		while (stmts.hasNext()){
		
			if(linesCount < linesLimit){
				
				Statement stmt = stmts.next();
				Resource subject = null;
				RDFNode object = null;
				// find bnodes to skolemise them
				if(stmt.getSubject().isAnon()){
					String oldBN = stmt.getSubject().asNode().getBlankNodeLabel();
					if(blancNodes.containsKey(oldBN)){
						subject = tmpModel.getResource(blancNodes.get(oldBN));
					}
					else{
						String newBN = uriBase+"bnode#"+UUID.randomUUID();
						blancNodes.put(oldBN, newBN);
						subject = tmpModel.createResource(newBN);
					}
				}
				else
					subject =  stmt.getSubject();
				
				
				if(stmt.getObject().isAnon()){
					String oldBN = stmt.getObject().asNode().getBlankNodeLabel();
					if(blancNodes.containsKey(oldBN)){
						object =  tmpModel.getResource(blancNodes.get(oldBN));
					}
					else{
						String newBN = uriBase+"bnode#"+UUID.randomUUID();
						blancNodes.put(oldBN, newBN);
						object = tmpModel.createResource(newBN);
					}
				}
				else
					object =  stmt.getObject();
				
				tmpModel.add(subject, stmt.getPredicate(), object);
				linesCount++;
			}
			else{
				
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				tmpModel.write(os, "TURTLE");
				String queryString = "INSERT { " + os.toString("UTF-8") + "}";
				System.out.println(queryString);
				os.close();
				
				HttpSPARQLUpdate p = new HttpSPARQLUpdate();
		        p.setEndpoint(endpoint);
		        p.setGraph(graph);
		        p.setUpdateString(queryString);
		        
		        if (! p.execute())  throw new Exception("UPDATE/SPARQL failed: " + queryString);
				
				total += linesCount;
				linesCount = 0;			
				tmpModel.removeAll();
			}
		
		}
		
		if(!tmpModel.isEmpty()){
		
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			tmpModel.write(os, "N-TRIPLES");
			String queryString = "INSERT {  " + os.toString() + "}";
			os.close();			

			HttpSPARQLUpdate p = new HttpSPARQLUpdate();
	        p.setEndpoint(endpoint);
	        p.setGraph(graph);
	        p.setUpdateString(queryString);
	        
	        if (!p.execute())  throw new Exception("UPDATE/SPARQL failed: " + queryString);
			
	        total += linesCount;
	        
		}
       
		return total;
     
	}
	
	private static void httpCreate(String endpoint, String graph) throws Exception{
				
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				String queryString = "CREATE GRAPH <" + graph + ">";
				os.close();
				
				HttpSPARQLCreateGraph p = new HttpSPARQLCreateGraph();
		        p.setEndpoint(endpoint);
		        p.setUpdateString(queryString);
		        
		        if (! p.execute())  throw new Exception("Creating graph failed: " + queryString);
     
	}

}
