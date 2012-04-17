package org.eclipse.persistence.jpa.rs.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalAccessExceptionMapper implements ExceptionMapper<IllegalAccessException> {
    public Response toResponse(IllegalAccessException exception){
        return Response.status(Status.BAD_REQUEST).build();
    }
}
