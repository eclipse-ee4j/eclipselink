package org.eclipse.persistence.jpa.rs.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ClassNotFoundExceptionMapper implements ExceptionMapper<ClassNotFoundException> {
    public Response toResponse(ClassNotFoundException exception){
        return Response.status(Status.BAD_REQUEST).build();
    }
}
