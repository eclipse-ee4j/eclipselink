/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      gonural - initial implementation
package org.eclipse.persistence.jpa.rs.exceptions;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jpa.rs.DataStorage;
import org.eclipse.persistence.jpa.rs.features.ServiceVersion;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;

import javax.naming.NamingException;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.QueryTimeoutException;
import javax.persistence.RollbackException;
import javax.persistence.TransactionRequiredException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractExceptionMapper {
    @Context
    private HttpHeaders headers;

    @Context
    UriInfo uriInfo;

    private static final Map<String, Status> HTTP_STATUS_CODE_MAPPING =
            Collections.unmodifiableMap(new HashMap<String, Status>() {
                {
                    put(ClassNotFoundException.class.getName(), Status.BAD_REQUEST);
                    put(ConversionException.class.getName(), Status.BAD_REQUEST);
                    put(DatabaseException.class.getName(), Status.INTERNAL_SERVER_ERROR);
                    put(EntityExistsException.class.getName(), Status.CONFLICT);
                    put(EntityNotFoundException.class.getName(), Status.NOT_FOUND);
                    put(IOException.class.getName(), Status.BAD_REQUEST);
                    put(IllegalAccessException.class.getName(), Status.BAD_REQUEST);
                    put(IllegalArgumentException.class.getName(), Status.BAD_REQUEST);
                    put(IllegalStateException.class.getName(), Status.BAD_REQUEST);
                    put(InvocationTargetException.class.getName(), Status.INTERNAL_SERVER_ERROR);
                    put(JAXBException.class.getName(), Status.NOT_FOUND); // TODO: we might want to change this http status in v2.0
                    put(MalformedURLException.class.getName(), Status.BAD_REQUEST);
                    put(NamingException.class.getName(), Status.BAD_REQUEST);
                    put(NoResultException.class.getName(), Status.NOT_FOUND);
                    put(NoSuchMethodException.class.getName(), Status.BAD_REQUEST);
                    put(NonUniqueResultException.class.getName(), Status.NOT_FOUND);
                    put(NumberFormatException.class.getName(), Status.BAD_REQUEST);
                    put(OptimisticLockException.class.getName(), Status.INTERNAL_SERVER_ERROR);
                    put(PersistenceException.class.getName(), Status.INTERNAL_SERVER_ERROR);
                    put(PessimisticLockException.class.getName(), Status.INTERNAL_SERVER_ERROR);
                    put(QueryTimeoutException.class.getName(), Status.BAD_REQUEST);// TODO: we might want to change this http status in v2.0
                    put(RollbackException.class.getName(), Status.BAD_REQUEST);
                    put(TransactionRequiredException.class.getName(), Status.INTERNAL_SERVER_ERROR);
                }
            });

    // An absolute URI that identifies the problem type.  When dereferenced, it SHOULD provide human-readable documentation for the problem type (e.g., using HTML)."
    private static final String PROBLEM_TYPE = "http://www.eclipse.org/eclipselink/documentation/";

    protected Response buildResponse(JPARSException exception) {
        String path = null;
        exception.setHttpStatusCode(getHttpStatusCode(exception.getCause()));

        if (uriInfo != null) {
            URI requestURI = uriInfo.getRequestUri();
            if (requestURI != null) {
                path = requestURI.getPath();
            }
        }

        if ((path != null) && (path.contains(ServiceVersion.VERSION_2_0.getCode()) || path.contains(ServiceVersion.LATEST.getCode()))) {
            ErrorResponse errorResponse = new ErrorResponse(PROBLEM_TYPE, exception.getMessage(), String.valueOf(exception.getErrorCode()));
            errorResponse.setRequestId((String) DataStorage.get(DataStorage.REQUEST_ID));
            errorResponse.setHttpStatus(exception.getHttpStatusCode().getStatusCode());

            String error = marshallErrorResponse(errorResponse, StreamingOutputMarshaller.getResponseMediaType(headers).toString());
            if (error != null) {
                return Response.status(exception.getHttpStatusCode()).entity(error).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
            }
        }
        return Response.status(exception.getHttpStatusCode()).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
    }

    private static Status getHttpStatusCode(Throwable throwable) {
        if (throwable != null) {
            Status httpStatusCode = HTTP_STATUS_CODE_MAPPING.get(throwable.getClass().getName());
            if (throwable instanceof RollbackException) {
                Throwable cause = throwable.getCause();
                if (cause != null) {
                    if (cause instanceof DatabaseException) {
                        //  409 Conflict ("The request could not be completed due to a conflict with the current state of the resource.")
                        httpStatusCode = Status.CONFLICT;
                    }
                }
            }
            if (httpStatusCode != null) {
                return httpStatusCode;
            }
        }
        return Status.BAD_REQUEST;
    }

    private String marshallErrorResponse(ErrorResponse errorResponse, String mediaType) {
        try {
            JAXBContext context = (JAXBContext) JAXBContextFactory.createContext(new Class[] { ErrorResponse.class }, null);
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, mediaType);
            marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, Boolean.FALSE);
            marshaller.setProperty(MarshallerProperties.JSON_REDUCE_ANY_ARRAYS, true);

            StringWriter writer = new StringWriter();
            marshaller.marshal(errorResponse, writer);
            return writer.toString();
        } catch (Exception ex) {
            JPARSLogger.exception(ex.getMessage(), null, ex);
        }
        return null;
    }
}
