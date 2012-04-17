package org.eclipse.persistence.jpa.rs.exceptions;

import java.lang.reflect.InvocationTargetException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvocationTargetExceptionMapper implements ExceptionMapper<InvocationTargetException> {
    public Response toResponse(InvocationTargetException exception){
        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
}
