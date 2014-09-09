package org.linkeddata.stack.limes.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class SayHello {
    /**
     * Just to know that the service is running
     * 
     * @return hello
     */
    @GET
    public Response hello() {
	return Response.ok("hello", MediaType.TEXT_PLAIN).build();
    }
}
