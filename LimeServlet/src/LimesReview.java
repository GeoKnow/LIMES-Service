import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.util.FileManager;

import de.uni_leipzig.simba.controller.Controller;

/**
 * Servlet implementation class LIMESERVE
 */
public class LimesReview extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static String filePath;
	
	// Options for writing the config file
	static String configFile;
	static String configTemplate;
	static String outputFormat;
	static String execType;
	   

	   public void init( ){
		  System.setProperty("file.encoding","UTF-8");
	      filePath = getServletContext().getRealPath("/");
	      System.out.println(filePath);
	   }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Hello World!</TITLE>"
		+ "</HEAD><BODY>Hello Reviewer!!!</BODY></HTML>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	 Model model = ModelFactory.createDefaultModel();

	   	 RDFReader r = model.getReader( "N-TRIPLE");
	   	 r.read( model, "file:///"+filePath+"result\\reviewme.nt" );
	   	 
	   	 PrintWriter out = response.getWriter();
	   	 System.out.println(model);
	     model.write(out, "N-TRIPLE");
    	}
}
