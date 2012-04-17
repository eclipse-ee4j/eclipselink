package org.eclipse.persistence.jpa.rs.exceptions;

import javax.naming.NamingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NamingExceptionMapper implements ExceptionMapper<NamingException> {
    public Response toResponse(NamingException exception){
        return Response.status(Status.BAD_REQUEST).build();
    }
}
