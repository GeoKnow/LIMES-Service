package org.linkeddata.stack.limes.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * API implementation class LIMESERVE
 */

@Path("/run")
public class LimesRun {

    private static final Logger log = Logger.getLogger(LimesRun.class);
    private static String filePath;

    // Options for writing the config file
    static String configFile;
    static String configTemplate;
    static String outputFormat;
    static String execType;

    // Prefixes
    static String[] prefixes = { "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
	    "rdf", "http://www.w3.org/2000/01/rdf-schema#", "rdfs",
	    "http://xmlns.com/foaf/0.1/", "foaf",
	    "http://www.w3.org/2002/07/owl#", "owl",
	    "http://www.opengis.net/ont/geosparql#", "geos",
	    "http://dbpedia.org/ontology/", "dbpedia",
	    "http://geovocab.org/geometry#", "geom",
	    "http://linkedgeodata.org/ontology/", "lgdo",
	    "http://dbpedia.org/resource/", "dbresource",
	    "http://purl.org/dc/terms/", "dc", "http://geoknow.eu/geodata#",
	    "geoknow", "http://dbpedia.org/property/", "dbpedia2",
	    "http://www.w3.org/2004/02/skos/core#", "skos",
	    "http://wiktionary.dbpedia.org/terms/", "wktrm",
	    "http://lexvo.org/ontology#", "lexvo",
	    "http://dbpedia.org/ontology/", "dbpedia-owl",
	    "http://ld.geoknow.eu/flights/ontology/", "ld",
	    "http://purl.org/acco/ns#", "cco",
	    "http://www.w3.org/2003/01/geo/wgs84_pos#", "geo" };

    static List<String> source = new ArrayList<String>();
    static List<String> target = new ArrayList<String>();
    // Properties
    static String metric;
    static String granularity;
    // Advanced settings
    static String[] acceptance = { "", "", "" };
    static String[] review = { "", "", "" };

    /**
     * Initialize Limes Service
     * 
     * @param context
     */
    public LimesRun(@Context ServletContext context) {

	filePath = context.getRealPath(File.separator);
	configFile = filePath + "config" + File.separator + "config.xml";
	configTemplate = filePath + "config" + File.separator + "default.xml";

	log.info("Result directory: " + filePath);

	File resultDir = new File(filePath + "result");
	if (!resultDir.exists()) {
	    resultDir.mkdirs();
	}
    }

    @POST
    public Response run(@Context UriInfo uriInfo) {

	source.clear();
	target.clear();
	source.add("Source");
	target.add("Target");

	MultivaluedMap<String, String> queryParams = uriInfo
		.getQueryParameters();

	try {

	    log.info(queryParams.getFirst("numberOfProps"));
	    String numberOfPropsString = queryParams.getFirst("numberOfProps");
	    int numberOfProps = Integer.parseInt(numberOfPropsString);

	    source.add(queryParams.getFirst("SourceServiceURI"));
	    target.add(queryParams.getFirst("TargetServiceURI"));
	    source.add(queryParams.getFirst("SourceGraph"));
	    target.add(queryParams.getFirst("TargetGraph"));
	    source.add(queryParams.getFirst("SourceVar"));
	    target.add(queryParams.getFirst("TargetVar"));
	    source.add(queryParams.getFirst("SourceSize"));
	    target.add(queryParams.getFirst("TargetSize"));
	    source.add(queryParams.getFirst("SourceRestr"));
	    target.add(queryParams.getFirst("TargetRestr"));

	    for (int i = 0; i < numberOfProps; i++) {
		source.add(queryParams.getFirst("SourceProp" + i));
		target.add(queryParams.getFirst("TargetProp" + i));
	    }

	    metric = queryParams.getFirst("Metric");
	    granularity = queryParams.getFirst("Granularity");
	    outputFormat = queryParams.getFirst("OutputFormat");
	    execType = queryParams.getFirst("ExecType");
	    acceptance[0] = queryParams.getFirst("AcceptThresh");
	    review[0] = queryParams.getFirst("ReviewThresh");
	    acceptance[1] = filePath + "result/accepted.nt";
	    review[1] = filePath + "result/reviewme.nt";
	    acceptance[2] = queryParams.getFirst("AcceptRelation");
	    review[2] = queryParams.getFirst("ReviewRelation");

	    writeConfig();
	    executeLimes(configFile);
	    return Response.status(Response.Status.CREATED).build();

	} catch (Exception e) {
	    log.error(e);
	    e.printStackTrace();
	    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
		    .entity(e.getMessage())
		    .header("Access-Control-Allow-Origin", "*").build();
	}

    }

    private static synchronized void writeConfig() throws IOException {

	Element root = new Element("LIMES");
	DocType dtype = new DocType("LIMES", null, filePath + "config"
		+ File.separator + "limes.dtd");
	Document doc = new Document(root, dtype);

	for (int i = 0; i < prefixes.length; i++) {
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

	Element[] sourcetags = { sid, sep, sgr, svar, sps, srestr, sprop };
	Element[] targettags = { tid, tep, tgr, tvar, tps, trestr, tprop };

	Element sourceTag = new Element("SOURCE");

	for (int i = 0; i < source.size(); i++) {
	    Element tag = null;
	    if (i >= sourcetags.length) {
		tag = new Element("PROPERTY");
	    } else {
		tag = sourcetags[i];
	    }
	    tag.setText(source.get(i));
	    sourceTag.addContent(tag);
	}

	root.addContent(sourceTag);

	Element targetTag = new Element("TARGET");

	for (int i = 0; i < target.size(); i++) {
	    Element tag = null;
	    if (i >= targettags.length) {
		tag = new Element("PROPERTY");
	    } else {
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

	Element[] acctags = { athr, afile, arel };
	Element[] revtags = { rthr, rfile, rrel };

	Element accTag = new Element("ACCEPTANCE");

	for (int i = 0; i < acceptance.length; i++) {
	    Element tag = acctags[i];
	    tag.setText(acceptance[i]);
	    accTag.addContent(tag);
	}

	root.addContent(accTag);

	Element revTag = new Element("REVIEW");

	for (int i = 0; i < review.length; i++) {
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

	log.info("Configuration writen at " + configFile);

    }

    // Start LIMES with the configfile
    /**
     * 
     * @param configFile
     * @throws IOException
     */
    private static void executeLimes(String configFile) throws IOException {

	// TODO: test if we can run more than one limes at the time?

	log.info("java -jar " + filePath + "WEB-INF" + File.separator + "lib"
		+ File.separator + "limes-0.6.5.jar " + configFile);
	Process proc = Runtime.getRuntime().exec(
		"java -jar " + filePath + "WEB-INF" + File.separator + "lib"
			+ File.separator + "limes-0.6.5.jar " + configFile);
	InputStream in = proc.getInputStream();
	InputStream err = proc.getErrorStream();
	String line;
	BufferedReader input = new BufferedReader(new InputStreamReader(in));
	while ((line = input.readLine()) != null) {
	    log.info(line);
	}
	input = new BufferedReader(new InputStreamReader(err));
	while ((line = input.readLine()) != null) {
	    log.info(line);
	}
	input.close();

	// Controller.run(configFile);
    }

    /*
     * private static String getValidParameter(HttpServletRequest request,
     * String param_name){ if(request.getParameter(param_name)!=null) return
     * request.getParameter(param_name); else{ //throw new
     * IllegalArgumentException("Missing parameter "+ param_name); return
     * request.getParameter(param_name); }
     * 
     * }
     */

}
