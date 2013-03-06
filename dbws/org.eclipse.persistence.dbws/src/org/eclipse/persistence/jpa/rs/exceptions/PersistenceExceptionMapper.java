package org.eclipse.persistence.jpa.rs.exceptions;

import javax.persistence.PersistenceException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.persistence.jpa.rs.util.JPARSLogger;

@Provider
public class PersistenceExceptionMapper implements ExceptionMapper<PersistenceException> {
    public Response toResponse(PersistenceException exception){
        JPARSLogger.exception("jpars_caught_exception", new Object[]{}, exception);
        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
}
