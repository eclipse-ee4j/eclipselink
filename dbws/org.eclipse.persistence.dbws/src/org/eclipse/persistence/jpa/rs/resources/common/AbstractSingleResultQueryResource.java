/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      gonural - Initial implementation
//      2014-09-01-2.6.0 Dmitry Kornilov
//        - JPARS v2.0 related changes
package org.eclipse.persistence.jpa.rs.resources.common;

import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilder;
import org.eclipse.persistence.jpa.rs.features.FeatureSet.Feature;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;
import org.eclipse.persistence.jpa.rs.util.list.SingleResultQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReportQuery;

import javax.persistence.Query;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import java.util.List;

/**
 * Base class for all single result query resources.
 *
 * @author gonural
 */
public abstract class AbstractSingleResultQueryResource extends AbstractResource {
    private static final String CLASS_NAME = AbstractSingleResultQueryResource.class.getName();

    /**
     * Named query single result.
     *
     * @param version the version
     * @param persistenceUnit the persistence unit
     * @param queryName the query name
     * @param headers the http headers
     * @param uriInfo the uriInfo
     * @return the response
     */
    @SuppressWarnings("rawtypes")
    protected Response namedQuerySingleResultInternal(String version, String persistenceUnit, String queryName, HttpHeaders headers, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "namedQuerySingleResultInternal", new Object[] { "GET", version, persistenceUnit, queryName, uriInfo.getRequestUri().toASCIIString() });
        try {
            final PersistenceContext context = getPersistenceContext(persistenceUnit, null, uriInfo.getBaseUri(), version, null);
            final Query query = context.buildQuery(getMatrixParameters(uriInfo, persistenceUnit), queryName, getMatrixParameters(uriInfo, queryName), getQueryParameters(uriInfo));
            final DatabaseQuery dbQuery = ((EJBQueryImpl<?>) query).getDatabaseQuery();
            final FeatureResponseBuilder responseBuilder = context.getSupportedFeatureSet().getResponseBuilder(Feature.NO_PAGING);

            if (dbQuery instanceof ReportQuery) {
                final List<ReportItem> reportItems = ((ReportQuery) dbQuery).getItems();
                final Object queryResults = query.getSingleResult();
                final Object response = responseBuilder.buildSingleResultQueryResponse(context, getQueryParameters(uriInfo), queryResults, reportItems, uriInfo);

                if (response != null && response instanceof SingleResultQuery) {
                    final SingleResultQuery singleResultQuery = (SingleResultQuery) response;
                    final List<JAXBElement<?>> item = singleResultQuery.getFields();
                    if ((item != null) && (item.size() == 1)) {
                        // Fix for Bug 393320 - JPA-RS: Respect the Accept Header for a singleResultQuery
                        // If there is only one item in the select clause and if value of that item is binary, we will create a response with
                        // that binary data without converting its to Base64.
                        JAXBElement element = item.get(0);
                        Object elementValue = element.getValue();
                        if (elementValue instanceof byte[]) {
                            List<MediaType> acceptableMediaTypes = headers.getAcceptableMediaTypes();
                            if (acceptableMediaTypes.contains(MediaType.APPLICATION_OCTET_STREAM_TYPE)) {
                                return Response.ok(new StreamingOutputMarshaller(context, elementValue, headers.getAcceptableMediaTypes())).build();
                            }
                        }
                    }

                    return Response.ok(new StreamingOutputMarshaller(context, response, headers.getAcceptableMediaTypes())).build();
                } else {
                    // something went wrong with the descriptors, return error
                    throw JPARSException.responseCouldNotBeBuiltForNamedQueryRequest(queryName, context.getName());
                }
            }

            final Object queryResult = query.getSingleResult();
            return Response.ok(new StreamingOutputMarshaller(context, responseBuilder.buildSingleEntityResponse(context, getQueryParameters(uriInfo), queryResult, uriInfo), headers.getAcceptableMediaTypes())).build();
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred(ex);
        }
    }
}
