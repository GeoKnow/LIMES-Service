package org.linkeddata.limes.rest;

import java.io.File;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.linkeddata.limes.LimesConfig;
import org.linkeddata.limes.LimesMain;

/**
 * API for LimesMain
 */

@Path("")
public class LimesService {

    private static final Logger log = Logger.getLogger(LimesService.class);

    private String workingPath;

    @Context
    ServletContext context;

    @GET
    public Response sayHello() {
	return Response.ok("hello", MediaType.TEXT_PLAIN).build();
    }

    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.APPLICATION_JSON)
    public Response run(LimesConfig config) {

	// TODO: verify what parameters are mandatory

	String filePath = context.getRealPath(File.separator);
	log.info("context directory: " + filePath);

	workingPath = filePath + "results";

	File resultDir = new File(workingPath);
	if (!resultDir.exists()) {
	    resultDir.mkdirs();
	}

	try {
	    String uuid = UUID.randomUUID().toString();
	    config.setUuid(uuid);
	    config.setConfigurationfile(workingPath + File.separator + uuid
		    + "_config.xml");
	    config.getAcceptance().setFile(
		    workingPath + File.separator + uuid + "_accepted."
			    + config.getOutput().toLowerCase());
	    config.getReview().setFile(
		    workingPath + File.separator + uuid + "_review."
			    + config.getOutput().toLowerCase());

	    LimesMain.executeLimes(config);

	    // if the output format is RDF (not TAB),
	    // and the save endpoint is not empty
	    // save results in the save endpoint
	    if (config.getOutput() != "TAB"
		    && !config.getSaveendpoint().equals(""))
		LimesMain.saveResults(config);

	    return Response.status(Response.Status.CREATED)
		    .header("Access-Control-Allow-Origin", "*")
		    .header("Access-Control-Allow-Methods", "POST")
		    .type(MediaType.APPLICATION_JSON).entity(config.toString())
		    .build();

	} catch (Exception e) {
	    log.error(e);
	    e.printStackTrace();
	    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
		    .type(MediaType.APPLICATION_JSON).entity(e.getMessage())
		    .build();
	}
    }
}
