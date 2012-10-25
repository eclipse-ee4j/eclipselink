/**
 * 
 */
package org.eclipse.persistence.jpa.rs.exceptions;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.persistence.jpa.rs.util.JPARSLogger;

/**
 * @author gonural
 *
 */
@Provider
public class EntityNotFoundExceptionMapper implements ExceptionMapper<EntityNotFoundException> {

    public Response toResponse(EntityNotFoundException exception) {
        JPARSLogger.exception("jpars_caught_exception", new Object[] {}, exception);
        return Response.status(Status.NOT_FOUND).build();
    }
}
