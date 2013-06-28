/*******************************************************************************
 * Copyright (c) 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.exceptions;

import java.net.URI;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.persistence.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.resources.common.AbstractResource;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;

public abstract class AbstractExceptionMapper {
    @Context
    private HttpHeaders headers;

    @Context
    UriInfo uriInfo;

    // An absolute URI that identifies the problem type.  When dereferenced, it SHOULD provide human-readable documentation for the problem type (e.g., using HTML)."
    private static final String PROBLEM_TYPE = "http://www.eclipse.org/eclipselink/documentation/";

    protected Response buildResponse(JPARSException exception) {
        String path = null;

        if (uriInfo != null) {
            URI requestURI = uriInfo.getRequestUri();
            if (requestURI != null) {
                path = requestURI.getPath();
            }
        }

        if ((path != null) && (path.contains(AbstractResource.SERVICE_VERSION_2_0))) {
            ErrorResponse errorResponse = new ErrorResponse(PROBLEM_TYPE, exception.getMessage(), String.valueOf(exception.getErrorCode()));
            errorResponse.setRequestUniqueId(exception.getRequestId());
            errorResponse.setHttpStatus(exception.getHttpStatusCode());
            return Response.status(exception.getHttpStatusCode()).entity(errorResponse).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        }
        return Response.status(exception.getHttpStatusCode()).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
    }
}
