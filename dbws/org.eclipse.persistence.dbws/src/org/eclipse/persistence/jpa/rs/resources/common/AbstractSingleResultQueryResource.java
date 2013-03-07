/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.resources.common;

import java.net.URI;
import java.util.List;

import javax.persistence.Query;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;
import org.eclipse.persistence.jpa.rs.util.list.SingleResultQueryList;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * @author gonural
 *
 */
public abstract class AbstractSingleResultQueryResource extends AbstractResource {
    @SuppressWarnings("rawtypes")
    protected Response namedQuerySingleResult(String version, String persistenceUnit, String name, HttpHeaders hh, UriInfo ui, URI baseURI) {
        PersistenceContext app = getPersistenceContext(persistenceUnit, baseURI, version, null);
        if (app == null) {
            JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            return Response.status(Status.NOT_FOUND).type(StreamingOutputMarshaller.getResponseMediaType(hh)).build();
        }

        Query query = app.buildQuery(getMatrixParameters(ui, persistenceUnit), name, getMatrixParameters(ui, name), getQueryParameters(ui));
        DatabaseQuery dbQuery = ((EJBQueryImpl<?>) query).getDatabaseQuery();
        if (dbQuery instanceof ReportQuery) {
            List<ReportItem> reportItems = ((ReportQuery) dbQuery).getItems();
            Object queryResults = query.getSingleResult();
            SingleResultQueryList list = populateReportQueryResponse(queryResults, reportItems);
            if (list != null) {
                List<JAXBElement> item = list.getFields();
                if ((item != null) && (item.size() == 1)) {
                    // Fix for Bug 393320 - JPA-RS: Respect the Accept Header for a singleResultQuery 
                    // If there is only one item in the select clause and if value of that item is binary, we will create a response with 
                    // that binary data without converting its to Base64.
                    JAXBElement element = item.get(0);
                    Object elementValue = element.getValue();
                    if (elementValue instanceof byte[]) {
                        List<MediaType> acceptableMediaTypes = hh.getAcceptableMediaTypes();
                        if (acceptableMediaTypes.contains(MediaType.APPLICATION_OCTET_STREAM_TYPE)) {
                            return Response.ok(new StreamingOutputMarshaller(app, elementValue, hh.getAcceptableMediaTypes())).build();
                        }
                    }
                }
                return Response.ok(new StreamingOutputMarshaller(app, list, hh.getAcceptableMediaTypes())).build();
            } else {
                // something went wrong with the descriptors, return error
                return Response.status(Status.INTERNAL_SERVER_ERROR).type(StreamingOutputMarshaller.getResponseMediaType(hh)).build();
            }
        }
        Object queryResults = query.getSingleResult();
        return Response.ok(new StreamingOutputMarshaller(app, queryResults, hh.getAcceptableMediaTypes())).build();
    }

    @SuppressWarnings({ "rawtypes" })
    private SingleResultQueryList populateReportQueryResponse(Object result, List<ReportItem> reportItems) {
        SingleResultQueryList response = new SingleResultQueryList();
        List<JAXBElement> fields = createShellJAXBElementList(reportItems, result);
        if (fields == null) {
            return null;
        }
        response.setFields(fields);
        return response;
    }
}
