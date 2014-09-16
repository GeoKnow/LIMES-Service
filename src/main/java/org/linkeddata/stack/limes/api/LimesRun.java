package org.linkeddata.stack.limes.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.linkeddata.stack.limes.LimesConfig;

/**
 * API implementation class LIMESERVE
 */

@Path("/run")
public class LimesRun {

    private static final Logger log = Logger.getLogger(LimesRun.class);
    private static String filePath;

    // Options for writing the config file
    static String configFilePath;
    static String configTemplate;
    static String outputFormat;
    static String execType;

    /**
     * Initialize Limes Service
     * 
     * @param context
     */
    public LimesRun(@Context ServletContext context) {

	filePath = context.getRealPath(File.separator);
	// configFilePath = filePath + "config" + File.separator + "config.xml";
	configFilePath = filePath + "config" + File.separator;
	configTemplate = filePath + "config" + File.separator + "default.xml";

	log.info("Result directory: " + filePath);

	File resultDir = new File(filePath + "result");
	if (!resultDir.exists()) {
	    resultDir.mkdirs();
	}
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response run(LimesConfig config) {

	log.debug(config.toString());

	try {
	    String uuid = UUID.randomUUID().toString();

	    config.setUuid(uuid);
	    config.setConfigfilepath(configFilePath + uuid + "_config.xml");
	    config.setAcceptancefilepath(filePath + "result/" + uuid
		    + "_accepted.nt");
	    config.setReviewfilepath(filePath + "result/" + uuid
		    + "_reviewme.nt");

	    String fileName = writeConfig(config);
	    executeLimes(fileName);

	    return Response.status(Response.Status.CREATED)
		    .header("Access-Control-Allow-Origin", "*")
		    .header("Access-Control-Allow-Methods", "PUT")
		    .entity(config.toString()).build();

	} catch (Exception e) {
	    log.error(e);
	    e.printStackTrace();
	    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
		    .entity(e.getMessage())
		    .header("Access-Control-Allow-Origin", "*")
		    .header("Access-Control-Allow-Methods", "PUT").build();
	}

    }

    private static synchronized String writeConfig(LimesConfig config)
	    throws IOException {

	Element root = new Element("LIMES");
	DocType dtype = new DocType("LIMES", null, filePath + "config"
		+ File.separator + "limes.dtd");
	Document doc = new Document(root, dtype);

	for (Map.Entry<String, String> entry : config.getPrefixes().entrySet()) {
	    Element prefix = new Element("PREFIX");
	    Element ns = new Element("NAMESPACE");
	    Element label = new Element("LABEL");
	    ns.setText(entry.getValue());
	    label.setText(entry.getKey());
	    prefix.addContent(ns);
	    prefix.addContent(label);
	    root.addContent(prefix);
	}

	Element sourceTag = new Element("SOURCE");
	sourceTag.addContent(new Element("ID").setText("Source"));
	sourceTag.addContent(new Element("ENDPOINT").setText(config
		.getSourceserviceuri()));
	sourceTag.addContent(new Element("GRAPH").setText(config
		.getSourcegraph()));
	sourceTag.addContent(new Element("VAR").setText(config.getSourcevar()));
	sourceTag.addContent(new Element("PAGESIZE").setText(config
		.getSourcesize()));
	sourceTag.addContent(new Element("RESTRICTION").setText(config
		.getSourcerestr()));
	Iterator<String> props = config.getSourceprop().iterator();
	while (props.hasNext())
	    sourceTag.addContent(new Element("PROPERTY").setText(props.next()));
	root.addContent(sourceTag);

	Element targetTag = new Element("TARGET");
	targetTag.addContent(new Element("ID").setText("Target"));
	targetTag.addContent(new Element("ENDPOINT").setText(config
		.getTargetserviceuri()));
	targetTag.addContent(new Element("GRAPH").setText(config
		.getTargetgraph()));
	targetTag.addContent(new Element("VAR").setText(config.getTargetvar()));
	targetTag.addContent(new Element("PAGESIZE").setText(config
		.getTargetsize()));
	targetTag.addContent(new Element("RESTRICTION").setText(config
		.getTargetrestr()));

	props = config.getTargetprop().iterator();
	while (props.hasNext())
	    targetTag.addContent(new Element("PROPERTY").setText(props.next()));

	root.addContent(targetTag);
	root.addContent(new Element("METRIC").setText(config.getMetric()));

	Element accTag = new Element("ACCEPTANCE");
	accTag.addContent(new Element("THRESHOLD").setText(config
		.getAcceptthresh()));
	accTag.addContent(new Element("FILE").setText(config
		.getAcceptancefilepath()));
	accTag.addContent(new Element("RELATION").setText(config
		.getAcceptrelation()));
	root.addContent(accTag);

	Element revTag = new Element("REVIEW");
	revTag.addContent(new Element("THRESHOLD").setText(config
		.getReviewthresh()));
	revTag.addContent(new Element("FILE").setText(config
		.getReviewfilepath()));
	revTag.addContent(new Element("RELATION").setText(config
		.getReviewrelation()));
	root.addContent(revTag);

	root.addContent(new Element("EXECUTION").setText(execType));
	root.addContent(new Element("GRANULARITY").setText(config
		.getGranularity()));
	root.addContent(new Element("OUTPUT").setText(outputFormat));

	XMLOutputter xmlOutput = new XMLOutputter();
	Format format = Format.getPrettyFormat();
	format.setExpandEmptyElements(true);
	format.setIndent("  ");
	format.setTextMode(Format.TextMode.TRIM);
	xmlOutput.setFormat(format);
	xmlOutput.output(doc, new FileWriter(config.getConfigfilepath()));

	log.info("Configuration writen at " + config.getConfigfilepath());
	return config.getConfigfilepath();
    }

    // Start LIMES with the configfile
    /**
     * 
     * @param configFile
     * @throws IOException
     */
    private static void executeLimes(String configFile) throws Exception {

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
	    log.debug(line);
	}
	input = new BufferedReader(new InputStreamReader(err));
	while ((line = input.readLine()) != null) {
	    log.debug(line);
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
