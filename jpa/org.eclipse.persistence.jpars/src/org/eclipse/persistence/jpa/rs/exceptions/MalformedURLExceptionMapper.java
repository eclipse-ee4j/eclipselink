package org.eclipse.persistence.jpa.rs.exceptions;

import java.net.MalformedURLException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MalformedURLExceptionMapper implements ExceptionMapper<MalformedURLException>{

    public Response toResponse(MalformedURLException exception){
        return Response.status(Status.BAD_REQUEST).build();
    }
}
