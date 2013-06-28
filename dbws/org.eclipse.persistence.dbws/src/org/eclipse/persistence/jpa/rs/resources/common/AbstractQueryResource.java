/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.JPARSException;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.jpa.rs.DataStorage;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.ReservedWords;
import org.eclipse.persistence.jpa.rs.features.FeatureRequestValidator;
import org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilder;
import org.eclipse.persistence.jpa.rs.features.FeatureSet;
import org.eclipse.persistence.jpa.rs.features.FeatureSet.Feature;
import org.eclipse.persistence.jpa.rs.features.clientinitiated.paging.PagingRequestValidator;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * @author gonural
 *
 */
public abstract class AbstractQueryResource extends AbstractResource {

    /**
     * Named query update internal.
     *
     * @param version the version
     * @param persistenceUnit the persistence unit
     * @param name the name
     * @param headers the http headers
     * @param uriInfo the uri info
     * @return the response
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Response namedQueryUpdateInternal(String version, String persistenceUnit, String name, HttpHeaders headers, UriInfo uriInfo) {
        try {
            PersistenceContext context = getPersistenceContext(persistenceUnit, uriInfo.getBaseUri(), version, null);
            if (context == null) {
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { (String) DataStorage.get(DataStorage.REQUEST_UNIQUE_ID), persistenceUnit });
                return Response.status(Status.NOT_FOUND).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
            }
            int result = context.queryExecuteUpdate(getMatrixParameters(uriInfo, persistenceUnit), name, getMatrixParameters(uriInfo, name), getQueryParameters(uriInfo));
            JAXBElement jaxbElement = new JAXBElement(new QName(ReservedWords.NO_ROUTE_JAXB_ELEMENT_LABEL), Integer.class, result);
            return Response.ok(new StreamingOutputMarshaller(context, jaxbElement, headers.getAcceptableMediaTypes())).build();
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred((String) DataStorage.get(DataStorage.REQUEST_UNIQUE_ID), getHttpStatusCode(ex), ex);
        }
    }

    /**
     * Named query internal.
     *
     * @param version the version
     * @param persistenceUnit the persistence unit
     * @param name the name
     * @param headers the http headers
     * @param uriInfo the uri info
     * @return the response
     */
    protected Response namedQueryInternal(String version, String persistenceUnit, String name, HttpHeaders headers, UriInfo uriInfo) {
        try {
            PersistenceContext context = getPersistenceContext(persistenceUnit, uriInfo.getBaseUri(), version, null);
            if (context == null) {
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { (String) DataStorage.get(DataStorage.REQUEST_UNIQUE_ID), persistenceUnit });
                return Response.status(Status.NOT_FOUND).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
            }

            Query query = context.buildQuery(getMatrixParameters(uriInfo, persistenceUnit), name, getMatrixParameters(uriInfo, name), getQueryParameters(uriInfo));

            DatabaseQuery dbQuery = ((EJBQueryImpl<?>) query).getDatabaseQuery();

            FeatureSet featureSet = context.getSupportedFeatureSet();
            if (featureSet.isSupported(Feature.PAGING)) {
                FeatureRequestValidator requestValidator = featureSet.getRequestValidator(Feature.PAGING);
                if (requestValidator.isRequested(uriInfo, null)) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(PagingRequestValidator.DB_QUERY, dbQuery);
                    map.put(PagingRequestValidator.QUERY, query);
                    if (!requestValidator.isRequestValid(uriInfo, map)) {
                        // some query parameters for paging are invalid or the named query doesn't have orderBy clause !
                        return Response.status(Status.BAD_REQUEST).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
                    }
                    return response(context, dbQuery, query, headers, uriInfo, featureSet.getResponseBuilder(Feature.PAGING));
                }
            }
            return response(context, dbQuery, query, headers, uriInfo, featureSet.getResponseBuilder(Feature.NO_PAGING));
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred((String) DataStorage.get(DataStorage.REQUEST_UNIQUE_ID), getHttpStatusCode(ex), ex);
        }
    }

    @SuppressWarnings("unchecked")
    private Response response(PersistenceContext context, DatabaseQuery dbQuery, Query query, HttpHeaders headers, UriInfo uriInfo, FeatureResponseBuilder responseBuilder) {
        Map<String, Object> queryParams = getQueryParameters(uriInfo);

        if (dbQuery instanceof ReportQuery) {
            // simple types selected : select u.name, u.age from employee
            List<ReportItem> reportItems = ((ReportQuery) dbQuery).getItems();
            List<Object[]> queryResults = query.getResultList();
            if ((queryResults != null) && (!queryResults.isEmpty())) {
                Object list = responseBuilder.buildReportQueryResponse(context, queryParams, queryResults, reportItems, uriInfo);
                if (list != null) {
                    return Response.ok(new StreamingOutputMarshaller(context, list, headers.getAcceptableMediaTypes())).build();
                } else {
                    // something is wrong with the descriptors
                    return Response.status(Status.INTERNAL_SERVER_ERROR).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
                }
            }
            return Response.ok(new StreamingOutputMarshaller(context, queryResults, headers.getAcceptableMediaTypes())).build();
        }

        List<Object> results = query.getResultList();
        if ((results != null) && (!results.isEmpty())) {
            Object list = responseBuilder.buildReadAllQueryResponse(context, queryParams, results, uriInfo);
            return Response.ok(new StreamingOutputMarshaller(context, list, headers.getAcceptableMediaTypes())).build();
        }
        return Response.ok(new StreamingOutputMarshaller(context, results, headers.getAcceptableMediaTypes())).build();
    }
}