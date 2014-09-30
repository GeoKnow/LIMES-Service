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
    private String outputformat = "N3";

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
	    config.getOutputformat().clear();
	    // only ttl
	    config.getOutputformat().add(outputformat.toUpperCase());

	    config.setConfigurationFile(workingPath + File.separator + uuid
		    + "_config.xml");
	    config.getAcceptance().setAcceptancefilepath(
		    workingPath + File.separator + uuid + "_accepted.n3");
	    config.getReview().setReviewfilepath(
		    workingPath + File.separator + uuid + "_review.n3");

	    LimesMain.executeLimes(config);

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
