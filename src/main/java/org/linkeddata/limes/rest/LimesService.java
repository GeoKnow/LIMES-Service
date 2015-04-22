package org.linkeddata.limes.rest;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.linkeddata.limes.LimesConfig;
import org.linkeddata.limes.LimesMain;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * API for LimesMain
 */

@Path("")
public class LimesService {

  private static final Logger log = Logger.getLogger(LimesService.class);


  @Context
  ServletContext context;

  private File getWorkingDir(@Context ServletContext context) {

    String filePath = context.getRealPath(File.separator);
    log.info("context directory: " + filePath);

    String workingPath = filePath + "results";

    File resultDir = new File(workingPath);
    if (!resultDir.exists()) {
      resultDir.mkdirs();
    }
    return resultDir;
  }


  /**
   * Provides a list of all configuration files processed by the service.
   * 
   * @param context
   * @return a JSON array of UUIDs
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllConfigurations(@Context ServletContext context) {

    File workingDir = getWorkingDir(context);
    String[] ext = {"xml"};
    Collection<File> fileIterator = FileUtils.listFiles(workingDir, ext, false);
    List<String> results = new ArrayList<String>();
    for (File f : fileIterator)
      results.add(f.getName().replace(".xml", ""));

    Gson gson = new Gson();
    String json = gson.toJson(results);

    return Response.ok().entity(json).header("Access-Control-Allow-Origin", "*")
        .header("Access-Control-Allow-Methods", "GET").build();
  }

  @GET
  @Path("{uuid}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response review(@PathParam("uuid") String uuid, @Context ServletContext context) {

    String workingPath = getWorkingDir(context).getAbsolutePath();

    String configFile = workingPath + File.separator + uuid + ".json";
    log.debug(configFile);

    try {
      JsonReader reader = new JsonReader(new FileReader(configFile));
      Gson gson = new Gson();
      LimesConfig config = gson.fromJson(reader, LimesConfig.class);
      String json = gson.toJson(config);

      return Response.ok().header("Access-Control-Allow-Origin", "*")
          .header("Access-Control-Allow-Methods", "GET").type(MediaType.APPLICATION_JSON)
          .entity(json).build();

    } catch (IOException e) {
      e.printStackTrace();
      return Response.status(Response.Status.NOT_FOUND).header("Access-Control-Allow-Origin", "*")
          .header("Access-Control-Allow-Methods", "GET").type(MediaType.APPLICATION_JSON).build();
    }

  }

  @POST
  @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Produces(MediaType.APPLICATION_JSON)
  public Response run(LimesConfig config, @Context ServletContext context) {

    // TODO: verify what parameters are mandatory

    String workingPath = getWorkingDir(context).getAbsolutePath();

    try {
      String uuid = UUID.randomUUID().toString();
      config.setUuid(uuid);
      config.setConfigurationfile(workingPath + File.separator + uuid + "_config.xml");

      // if a Endpoint to save results is provided the format files is set
      // to N3
      if (!config.getSaveendpoint().equals(""))
        config.setOutput("N3");

      config.getAcceptance().setFile(
          workingPath + File.separator + uuid + "_accepted." + config.getOutput().toLowerCase());
      config.getReview().setFile(
          workingPath + File.separator + uuid + "_review." + config.getOutput().toLowerCase());

      LimesMain.executeLimes(config);

      // if save endpoint is not empty
      // save results in the save endpoint
      if (!config.getSaveendpoint().equals(""))
        LimesMain.saveResults(config);

      // write JSON config file with resulting triples
      FileWriter jsonFile =
          new FileWriter(config.getConfigurationfile().replace(".xml", ".json"), false);
      jsonFile.append(config.toString());
      jsonFile.close();

      return Response.status(Response.Status.CREATED).header("Access-Control-Allow-Origin", "*")
          .header("Access-Control-Allow-Methods", "POST").type(MediaType.APPLICATION_JSON)
          .entity(config.toString()).build();

    } catch (Exception e) {
      log.error(e);
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .type(MediaType.APPLICATION_JSON).entity(e.getMessage())
          .header("Access-Control-Allow-Origin", "*")
          .header("Access-Control-Allow-Methods", "POST").build();
    }
  }
}
