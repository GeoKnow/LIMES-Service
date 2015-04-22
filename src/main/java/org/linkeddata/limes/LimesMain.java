package org.linkeddata.limes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
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

    FileWriter configFile = new FileWriter(config.getConfigurationfile(), false);

    JAXBContext cont = JAXBContext.newInstance(LimesConfig.class);

    log.debug(LimesMain.class.getClassLoader().getResource("limes.dtd").getPath());
    StringWriter writer = new StringWriter();
    writer.append("<?xml version=\"1.0\"?>");
    writer.append("<!DOCTYPE LIMES SYSTEM \""
        + LimesMain.class.getClassLoader().getResource("limes.dtd").getPath() + "\">");

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
      throw new NullPointerException("Undefined endpoint for saving results");

    String reviewFile = config.getReview().getFile();
    String acceptedFile = config.getAcceptance().getFile();

    // read the file created with the first output format
    Model modelReview = ModelFactory.createDefaultModel();
    RDFReader readerre = modelReview.getReader("N3");
    readerre.read(modelReview, new File(reviewFile).toURI().toURL().toString());

    Model modelAccepted = ModelFactory.createDefaultModel();
    RDFReader reader = modelAccepted.getReader("N3");

    reader.read(modelAccepted, new File(acceptedFile).toURI().toURL().toString());

    if (modelAccepted.isEmpty() != true)
      insertResults(config.getSaveendpoint(), config.getAcceptgraph(), config.getUribase(),
          modelAccepted);

    if (!modelReview.isEmpty())
      insertResults(config.getSaveendpoint(), config.getReviewgraph(), config.getUribase(),
          modelReview);

  }

  private static void insertResults(String endpoint, String graph, String uriBase, Model model)
      throws Exception {
    List<String> insertqueries = QueryChunks.generateInsertChunks(graph, model, uriBase);
    Iterator<String> it = insertqueries.iterator();
    log.info(" into " + graph);

    CloseableHttpClient httpClient = HttpClients.createDefault();

    while (it.hasNext()) {
      String q = it.next();

      HttpPost proxyMethod = new HttpPost(endpoint);

      ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
      postParameters.add(new BasicNameValuePair("query", q));
      postParameters.add(new BasicNameValuePair("format", "application/sparql-results+json"));
      proxyMethod.setEntity(new UrlEncodedFormEntity(postParameters));

      final CloseableHttpResponse response = httpClient.execute(proxyMethod);

      BufferedReader in =
          new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        log.debug(inputLine);
      }
      in.close();

      if (response.getStatusLine().getStatusCode() != 200) {
        throw new IOException("Could not insert data: " + endpoint + " "
            + response.getStatusLine().getReasonPhrase());
      }

    }
    httpClient.close();
  }

}
