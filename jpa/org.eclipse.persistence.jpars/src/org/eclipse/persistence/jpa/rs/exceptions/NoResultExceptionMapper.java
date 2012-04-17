package org.eclipse.persistence.jpa.rs.exceptions;

import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NoResultExceptionMapper implements ExceptionMapper<NoResultException> {
    public Response toResponse(NoResultException exception){
        return Response.status(Status.NOT_FOUND).build();
    }
}
