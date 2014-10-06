package org.linkeddata.limes;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.linkeddata.utils.QueryChunks;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;

import de.uni_leipzig.simba.controller.PPJoinController;

public class LimesMain {

    private static final Logger log = Logger.getLogger(LimesMain.class);
    private static String encoding = "UTF-8";

    public static void executeLimes(LimesConfig config) throws Exception {

	log.debug(config);

	FileWriter configFile = new FileWriter(config.getConfigurationfile(),
		false);

	JAXBContext cont = JAXBContext.newInstance(LimesConfig.class);

	log.debug(LimesMain.class.getClassLoader().getResource("limes.dtd")
		.getPath());
	StringWriter writer = new StringWriter();
	writer.append("<?xml version=\"1.0\"?>");
	writer.append("<!DOCTYPE LIMES SYSTEM \""
		+ LimesMain.class.getClassLoader().getResource("limes.dtd")
			.getPath() + "\">");

	Marshaller marshaller = cont.createMarshaller();
	marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

	marshaller.marshal(config, writer);

	log.debug(writer.toString());

	configFile.write(writer.toString());
	configFile.close();

	log.info("configuration file created: " + config.getConfigurationfile());
	PPJoinController.run(config.getConfigurationfile());
    }

    public static void saveResults(LimesConfig config) throws Exception {

	log.info("Saving results to " + config.getSaveendpoint());

	if (config.getSaveendpoint() == null || config.getSaveendpoint() == "")
	    throw new NullPointerException(
		    "Undefined endpoint for saving results");

	String reviewFile = config.getReview().getFile();
	String acceptedFile = config.getAcceptance().getFile();

	// read the file created with the first output format
	Model modelReview = ModelFactory.createDefaultModel();
	RDFReader readerre = modelReview.getReader("N3");
	readerre.read(modelReview, new File(reviewFile).toURI().toURL()
		.toString());

	Model modelAccepted = ModelFactory.createDefaultModel();
	RDFReader reader = modelAccepted.getReader("N3");

	reader.read(modelAccepted, new File(acceptedFile).toURI().toURL()
		.toString());

	if (modelAccepted.isEmpty() != true)
	    insertResults(config.getSaveendpoint(), config.getAcceptgraph(),
		    config.getUribase(), modelAccepted);

	if (!modelReview.isEmpty())
	    insertResults(config.getSaveendpoint(), config.getReviewgraph(),
		    config.getUribase(), modelReview);

    }

    private static void insertResults(String endpoint, String graph,
	    String uriBase, Model model) throws Exception {
	List<String> insertqueries = QueryChunks.generateInsertChunks(graph,
		model, uriBase);
	Iterator<String> it = insertqueries.iterator();

	while (it.hasNext()) {
	    String q = it.next();
	    HttpURLConnection conn = executeSpqrql(endpoint, q);
	    if (conn.getResponseCode() != 200) {
		log.error(conn.getResponseMessage());
		log.error("Aptempted to execute - " + q);
		// get more information abut the error
		BufferedReader in = new BufferedReader(new InputStreamReader(
			conn.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
		    log.debug(inputLine);
		}
		in.close();
	    }
	}
    }

    private static HttpURLConnection executeSpqrql(String endpoint,
	    String sparqlQuery) throws Exception {
	String format = URLEncoder.encode("application/sparql-results+json",
		encoding);
	String urlParameters = "format=" + format + "&query="
		+ URLEncoder.encode(sparqlQuery, encoding);
	return httpPost(endpoint, urlParameters);
    }

    private static HttpURLConnection httpPost(String url, String urlParameters)
	    throws IOException {

	URL targetURL = new URL(url);
	HttpURLConnection connection = (HttpURLConnection) targetURL
		.openConnection();
	connection.setDoOutput(true);
	connection.setDoInput(true);
	connection.setInstanceFollowRedirects(false);
	connection.setRequestMethod("POST");
	connection.setRequestProperty("Content-Type",
		"application/x-www-form-urlencoded");
	connection.setRequestProperty("charset", "utf-8");
	connection.setRequestProperty("Content-Length",
		"" + Integer.toString(urlParameters.getBytes().length));
	connection.setUseCaches(false);
	DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
	wr.writeBytes(urlParameters);
	wr.flush();
	wr.close();
	return connection;
    }
}
