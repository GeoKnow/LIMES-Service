package org.linkeddata.stack.limes;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
	Object[]config = new Object[5];

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	 
    	 response.setHeader("Access-Control-Allow-Origin", "*");
    	 
    	 String filePath = request.getSession().getServletContext().getRealPath("/");
    	 filePath = filePath.replace("LimeServlet\\", "");
    	 configFile = filePath+"generator"+File.separator+"uploads"+File.separator+request.getParameter("file");
    	 System.out.println("LoadFile: " + configFile);
    	 readConfig(configFile);
    	 
    	 Gson gson = new Gson();
	     String json = gson.toJson(config);
	     response.setContentType("application/json");
	     response.setCharacterEncoding("UTF-8");
	     response.getWriter().write(json);
    	}
    
    private void readConfig(String configFile){
    	
    	 String[] sourceArray     = new String[5];
    	 String[] targetArray     = new String[5];
    	 String   metric          = null;
    	 String[] acceptanceArray = new String[2];
    	 String[] reviewArray 	  = new String[2];
    	 
    	 try {
	    		 
	    			File fXmlFile = new File(configFile);
	    			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    			dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
	    			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    			Document doc = dBuilder.parse(fXmlFile);
	    	
	    			doc.getDocumentElement().normalize();
	    		 
	    			NodeList source = doc.getElementsByTagName("SOURCE");
	    		 
	    			for (int temp = 0; temp < source.getLength(); temp++) {
	    		 
	    				Node nNode = source.item(temp);
	    		 
	    				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	    		 
	    					Element eElement = (Element) nNode;
	    		 
	    					sourceArray[0] = eElement.getElementsByTagName("ENDPOINT").item(0).getTextContent();
	    					sourceArray[1] = eElement.getElementsByTagName("VAR").item(0).getTextContent();
	    					sourceArray[2] = eElement.getElementsByTagName("PAGESIZE").item(0).getTextContent();
	    					sourceArray[3] = eElement.getElementsByTagName("RESTRICTION").item(0).getTextContent();
	    					sourceArray[4] = eElement.getElementsByTagName("PROPERTY").item(0).getTextContent();
			    				}
			    			}
	    			
	    			NodeList target = doc.getElementsByTagName("TARGET");
	    			
	    			for (int temp = 0; temp < target.getLength(); temp++) {
	   	    		 
	    				Node nNode = target.item(temp);
	    		 
	    				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	    		 
	    					Element eElement = (Element) nNode;
	    		 
	    					targetArray[0] = eElement.getElementsByTagName("ENDPOINT").item(0).getTextContent();
	    					targetArray[1] = eElement.getElementsByTagName("VAR").item(0).getTextContent();
	    					targetArray[2] = eElement.getElementsByTagName("PAGESIZE").item(0).getTextContent();
	    					targetArray[3] = eElement.getElementsByTagName("RESTRICTION").item(0).getTextContent();
	    					targetArray[4] = eElement.getElementsByTagName("PROPERTY").item(0).getTextContent();
			    				}
			    			}
	    			
	    			NodeList metricNode = doc.getElementsByTagName("METRIC");
    	 			Node mNode = metricNode.item(0);
    	 			Element mElement = (Element) mNode;
    	 			metric = mElement.getTextContent();
    	 			
    	 			NodeList acceptance = doc.getElementsByTagName("ACCEPTANCE");
	    			
	    			for (int temp = 0; temp < acceptance.getLength(); temp++) {
	   	    		 
	    				Node nNode = acceptance.item(temp);
	    		 
	    				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	    		 
	    					Element eElement = (Element) nNode;
	    		 
	    					acceptanceArray[0] = eElement.getElementsByTagName("THRESHOLD").item(0).getTextContent();
	    					acceptanceArray[1] = eElement.getElementsByTagName("RELATION").item(0).getTextContent();
			    				}
			    			}
	    			
	    			NodeList review = doc.getElementsByTagName("REVIEW");
	    			
	    			for (int temp = 0; temp < review.getLength(); temp++) {
	   	    		 
	    				Node nNode = review.item(temp);
	    		 
	    				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	    		 
	    					Element eElement = (Element) nNode;
	    		 
	    					reviewArray[0] = eElement.getElementsByTagName("THRESHOLD").item(0).getTextContent();
	    					reviewArray[1] = eElement.getElementsByTagName("RELATION").item(0).getTextContent();
			    				}
			    			}
	    			
		    		    	} catch (Exception e) {
		    		    		e.printStackTrace();
		    		    	}
    	 
    	 config[0] = sourceArray;
    	 config[1] = targetArray;
    	 config[2] = metric;
    	 config[3] = acceptanceArray;
    	 config[4] = reviewArray;
    	 
    }
    
}
