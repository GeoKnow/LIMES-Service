package org.linkeddata.stack.limes;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;

import com.google.gson.Gson;

/**
 * Servlet implementation class LIMESERVE
 */
public class LoadFile extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	// Options for writing the config file
	static String configFile;
	static String configTemplate;
	static String outputFormat;
	static String execType;
	Object[]config = new Object[10];
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	 
    	 response.setHeader("Access-Control-Allow-Origin", "*");
    	 Boolean valid = true;
    	 
    	 String filePath = request.getSession().getServletContext().getRealPath("/");
    	 String filePathUpload = filePath.replace("Limes-Service"+File.separator, "");
    	 configFile = filePathUpload+"generator"+File.separator+"uploads"+File.separator+request.getParameter("file");
    	 configTemplate = filePath+File.separator+"config"+File.separator+"limes.dtd";
    	 System.out.println("LoadFile: " + configFile);
    	 try {
			validateXML();
		 } catch (JDOMException e) {
			 response.setContentType("application/json");
		     response.setCharacterEncoding("UTF-8");
		     response.setStatus(500);
		     response.getWriter().write(e.getMessage());
		     valid = false;
		 }
    	 System.out.println(valid);
    	 if(valid == true){
	    	 readConfig(configFile);
	    	 
	    	 Gson gson = new Gson();
		     String json = gson.toJson(config);
		     response.setContentType("application/json");
		     response.setCharacterEncoding("UTF-8");
		     response.getWriter().write(json);
	    	 }
    	}
    
    private void readConfig(String configFile) throws IOException{
    	
    	 List<String> sourceArray 		= new ArrayList<String>();
    	 List<String> targetArray 		= new ArrayList<String>();
    	 List<String> sourceProps 		= new ArrayList<String>();
    	 List<String> targetProps 		= new ArrayList<String>();
    	 String metric            		= null;
    	 String output             		= null;
    	 String granularity       		= null;
    	 String execution       		= null;
    	 List<String> acceptanceArray 	= new ArrayList<String>();
    	 List<String> reviewArray 	  	= new ArrayList<String>();
    	 
    	 try {
    		 		SAXBuilder builder = new SAXBuilder(XMLReaders.DTDVALIDATING);
    		 		Document document = builder.build(configFile);
	    			Element rootNode = document.getRootElement();
	    			
	    			Element sourceNode = rootNode.getChild("SOURCE");
	    			List<Element> sourcelist = sourceNode.getChildren();
	    			
	    			for (int i = 0; i < sourcelist.size(); i++) {
	    				   Element node = (Element) sourcelist.get(i);
	    				   if(node.getName() == "ENDPOINT"){
	    					   sourceArray.add(0, node.getText());
	    				   }
	    				   if(node.getName() == "VAR"){
	    					   sourceArray.add(1, node.getText());
	    				   }
	    				   if(node.getName() == "PAGESIZE"){
	    					   sourceArray.add(2, node.getText());
	    				   }
	    				   if(node.getName() == "RESTRICTION"){
	    					   sourceArray.add(3, node.getText());
	    				   }
	    				   if(node.getName() == "PROPERTY"){
	    					   sourceProps.add(node.getText());
	    				   }
	    				}
	    			
	    			Element targetNode = rootNode.getChild("TARGET");
	    			List<Element> targetlist = targetNode.getChildren();
	    			
	    			for (int i = 0; i < targetlist.size(); i++) {
	    				   Element node = (Element) targetlist.get(i);
	    				   if(node.getName() == "ENDPOINT"){
	    					   targetArray.add(0, node.getText());
	    				   }
	    				   if(node.getName() == "VAR"){
	    					   targetArray.add(1, node.getText());
	    				   }
	    				   if(node.getName() == "PAGESIZE"){
	    					   targetArray.add(2, node.getText());
	    				   }
	    				   if(node.getName() == "RESTRICTION"){
	    					   targetArray.add(3, node.getText());
	    				   }
	    				   if(node.getName() == "PROPERTY"){
	    					   targetProps.add(node.getText());
	    				   }
	    				}
	    			
	    			Element acceptance = rootNode.getChild("ACCEPTANCE");
	    			List<Element> acceptancelist = acceptance.getChildren();
	    			
	    			for (int i = 0; i < acceptancelist.size(); i++) {
	    				   Element node = (Element) acceptancelist.get(i);
	    				   if(node.getName() != "FILE"){
	    					   acceptanceArray.add(node.getText());
	    				   }
	    				}
	    			
	    			Element review = rootNode.getChild("REVIEW");
	    			List<Element> reviewlist = review.getChildren();
	    			
	    			for (int i = 0; i < reviewlist.size(); i++) {
	    				   Element node = (Element) reviewlist.get(i);
	    				   if(node.getName() != "FILE"){
	    					   reviewArray.add(node.getText());
	    				   }
	    				}
	    			
	    			Element metricNode = rootNode.getChild("METRIC");
	    			metric = metricNode.getText();
	    			
	    			Element outputNode = rootNode.getChild("OUTPUT");
	    			output = outputNode.getText();
	    			
	    			Element executionNode = rootNode.getChild("EXECUTION");
	    			execution = executionNode.getText();
	    			
	    			Element granNode = rootNode.getChild("GRANULARITY");
	    			if(granNode != null){
	    				granularity = granNode.getText();
	    			}
	    			
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    		throw new IllegalArgumentException("Invalid LIMES Configration");
		    		}
    	 
    	 config[0] = sourceArray;
    	 config[1] = targetArray;
    	 config[2] = metric;
    	 config[3] = acceptanceArray;
    	 config[4] = reviewArray;
    	 config[5] = output;
    	 config[6] = granularity;
    	 config[7] = execution;
    	 config[8] = sourceProps;
    	 config[9] = targetProps;
    	 
    }
    
    public void validateXML() throws JDOMException, IOException{
    	SAXBuilder builder = new SAXBuilder(XMLReaders.DTDVALIDATING);
    	Document document = builder.build(configFile);
    	System.out.println(document.getDocType());
    }
    
}
