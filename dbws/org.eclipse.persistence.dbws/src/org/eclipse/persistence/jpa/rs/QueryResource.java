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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.jpa.rs.response.QueryResultList;
import org.eclipse.persistence.jpa.rs.response.QueryResultListItem;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;

@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Path("/{context}/query/")
public class QueryResource extends AbstractResource {

    @POST
    @Path("{name}")
    public Response namedQueryUpdate(@PathParam("context") String persistenceUnit, @PathParam("name") String name, @Context HttpHeaders hh, @Context UriInfo ui) {
        return namedQueryUpdateInternal(persistenceUnit, name, hh, ui);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Response namedQueryUpdateInternal(String persistenceUnit, String name, HttpHeaders hh, UriInfo ui) {
        PersistenceContext app = getPersistenceFactory().get(persistenceUnit, ui.getBaseUri(), null);
        if (app == null) {
            JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            return Response.status(Status.NOT_FOUND).build();
        }
        int result = app.queryExecuteUpdate(getMatrixParameters(ui, persistenceUnit), name, getMatrixParameters(ui, name), getQueryParameters(ui));
        JAXBElement jaxbElement = new JAXBElement(new QName(StreamingOutputMarshaller.NO_ROUTE_JAXB_ELEMENT_LABEL), new Integer(result).getClass(), result);
        return Response.ok(new StreamingOutputMarshaller(app, jaxbElement, hh.getAcceptableMediaTypes())).build();
    }
    
    @GET
    @Path("{name}")
    public Response namedQuery(@PathParam("context") String persistenceUnit, @PathParam("name") String name, @Context HttpHeaders hh, @Context UriInfo ui) {
        return namedQueryInternal(persistenceUnit, name, hh, ui);
    }
    
    @SuppressWarnings("unchecked")
    protected Response namedQueryInternal(String persistenceUnit, String name, HttpHeaders hh, UriInfo ui) {
        PersistenceContext app = getPersistenceFactory().get(persistenceUnit, ui.getBaseUri(), null);
        if (app == null) {
            JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            return Response.status(Status.NOT_FOUND).build();
        }
        Query query = app.buildQuery(getMatrixParameters(ui, persistenceUnit), name, getMatrixParameters(ui, name), getQueryParameters(ui));
        DatabaseQuery dbQuery = ((EJBQueryImpl<?>) query).getDatabaseQuery();
        if (dbQuery instanceof ReportQuery) {
            // simple types selected : select u.name, u.age from employee
            List<ReportItem> reportItems = ((ReportQuery) dbQuery).getItems();
            List<Object[]> results = app.queryMultipleResults(query);
            QueryResultList resultList = populateReportQueryResponse(results, reportItems);
            if (resultList == null) {
                return null;
            } else {
                return Response.ok(new StreamingOutputMarshaller(app, resultList, hh.getAcceptableMediaTypes())).build();
            }
        } else if (dbQuery instanceof ReadAllQuery) {
            // only domain object selected: SELECT u FROM EmployeeAddress u
            // we will return list of domain objects
            List<Object> results = app.queryMultipleResults(query);
            return Response.ok(new StreamingOutputMarshaller(app, results, hh.getAcceptableMediaTypes())).build();
        } else if (dbQuery instanceof ReadObjectQuery) {
            // one or more contained domain objects (such as  u.address, u.project in this example) and
            // some other simple fields (u.age, u.lastname) are selected : SELECT u.address, u.project, u.age, u.lastname FROM Employee  
            List<Object> results = app.queryMultipleResults(query);
            return Response.ok(new StreamingOutputMarshaller(app, results, hh.getAcceptableMediaTypes())).build();
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private QueryResultList populateReportQueryResponse(List<Object[]> results, List<ReportItem> reportItems) {
        QueryResultList response = new QueryResultList();
        for (Object result : results) {
            QueryResultListItem queryResultListItem = new QueryResultListItem();
            List<JAXBElement> jaxbFields = createShellJAXBElementList(reportItems);
            if (jaxbFields == null) {
                return null;
            }
            if (result instanceof Object[]) {
                for (int i = 0; i < ((Object[]) result).length; i++) {
                    jaxbFields.get(i).setValue(((Object[]) result)[i]);
                }
            } else if (result instanceof Object) {
                jaxbFields.get(0).setValue(((Object) result));
            }
            queryResultListItem.setFields(jaxbFields);
            response.addItem(queryResultListItem);
        }
        return response;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<JAXBElement> createShellJAXBElementList(List<ReportItem> reportItems) {
        List<JAXBElement> jaxbElements = new ArrayList<JAXBElement>(reportItems.size());
        if ((reportItems != null) && (reportItems.size() > 0)) {
            for (ReportItem reportItem : reportItems) {
                String reportItemName = reportItem.getName();
                Class resultType = reportItem.getResultType();
                if (resultType == null) {
                    DatabaseMapping dbMapping = reportItem.getMapping();
                    if (dbMapping != null) {
                        resultType = dbMapping.getAttributeClassification();
                    } else {
                        ClassDescriptor desc = reportItem.getDescriptor();
                        if (desc != null) {
                            resultType = desc.getJavaClass();
                        } else {
                            return null;
                        }
                    }
                }
                JAXBElement element = new JAXBElement(new QName(reportItemName), resultType, null);
                jaxbElements.add(reportItem.getResultIndex(), element);
            }
        }
        return jaxbElements;
    }
}
