/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.exceptions;

import javax.persistence.RollbackException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;

@Provider
public class RollbackExceptionMapper implements ExceptionMapper<RollbackException> {
    @Context
    private HttpHeaders headers;
    public Response toResponse(RollbackException exception) {
        JPARSLogger.exception("jpars_caught_exception", new Object[] {}, exception);
        if (exception != null) {
            Throwable cause = exception.getCause();
            if (cause != null) {
                if (cause instanceof DatabaseException) {
                    //  409 Conflict ("The request could not be completed due to a conflict with the current state of the resource.")
                    return Response.status(Status.CONFLICT).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
                }
            }
        }
        return Response.status(Status.BAD_REQUEST).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
    }
}
