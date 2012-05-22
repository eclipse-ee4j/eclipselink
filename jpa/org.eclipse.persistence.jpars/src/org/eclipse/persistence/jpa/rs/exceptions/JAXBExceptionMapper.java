package org.eclipse.persistence.jpa.rs.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jpa.rs.util.JPARSLogger;

@Provider
public class JAXBExceptionMapper implements ExceptionMapper<JAXBException>{

    public Response toResponse(JAXBException exception){
        JPARSLogger.exception("jpars_caught_exception", new Object[]{}, exception);
        return Response.status(Status.NOT_FOUND).build();
    }
}
