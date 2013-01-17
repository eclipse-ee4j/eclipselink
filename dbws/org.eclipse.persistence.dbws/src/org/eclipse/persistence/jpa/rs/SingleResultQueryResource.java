/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs;

import java.net.URI;
import java.util.List;

import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;
import org.eclipse.persistence.jpa.rs.util.list.SingleResultQueryList;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReportQuery;

@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Path("/{context}/singleResultQuery/")
public class SingleResultQueryResource extends AbstractResource {

    @GET
    @Path("{name}")
    @Produces(MediaType.WILDCARD)
    public Response namedQuerySingleResult(@PathParam("context") String persistenceUnit, @PathParam("name") String name, @Context HttpHeaders hh, @Context UriInfo ui) {
        return namedQuerySingleResult(persistenceUnit, name, hh, ui, ui.getBaseUri());
    }

    protected Response namedQuerySingleResult(String persistenceUnit, String name, HttpHeaders hh, UriInfo ui, URI baseURI) {
        PersistenceContext app = getPersistenceFactory().get(persistenceUnit, baseURI, null);
        if (app == null) {
            JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            return Response.status(Status.NOT_FOUND).build();
        }

        Query query = app.buildQuery(getMatrixParameters(ui, persistenceUnit), name, getMatrixParameters(ui, name), getQueryParameters(ui));
        DatabaseQuery dbQuery = ((EJBQueryImpl<?>) query).getDatabaseQuery();
        if (dbQuery instanceof ReportQuery) {
            List<ReportItem> reportItems = ((ReportQuery) dbQuery).getItems();
            Object[] queryResults = (Object[]) query.getSingleResult();
            SingleResultQueryList list = populateReportQueryResponse(queryResults, reportItems);
            if (list != null) {
                return Response.ok(new StreamingOutputMarshaller(app, list, hh.getAcceptableMediaTypes())).build();
            } else {
                // something wrong with the descriptors
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        } 
        Object queryResults = query.getSingleResult();
        return Response.ok(new StreamingOutputMarshaller(app, queryResults, hh.getAcceptableMediaTypes())).build();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private SingleResultQueryList populateReportQueryResponse(Object[] result, List<ReportItem> reportItems) {
        SingleResultQueryList response = new SingleResultQueryList();
        List<JAXBElement> fields = createShellJAXBElementList(reportItems);
        if (fields == null) {
            return null;
        }
        for (int i = 0; i<result.length; i++) {
            Object resultItem = result[i];
            fields.get(i).setValue(((Object) resultItem));
        }
        response.setFields(fields);
        return response;
    }
}
