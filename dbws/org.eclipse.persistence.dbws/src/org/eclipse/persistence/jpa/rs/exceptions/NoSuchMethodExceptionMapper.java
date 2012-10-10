package org.eclipse.persistence.jpa.rs.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.persistence.jpa.rs.util.JPARSLogger;

@Provider
public class NoSuchMethodExceptionMapper implements ExceptionMapper<NoSuchMethodException> {
    public Response toResponse(NoSuchMethodException exception){
        JPARSLogger.exception("jpars_caught_exception", new Object[]{}, exception);
        return Response.status(Status.BAD_REQUEST).build();
    }
}
