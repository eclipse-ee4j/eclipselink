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

import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.QueryParameters;
import org.eclipse.persistence.jpa.rs.ReservedWords;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilder;
import org.eclipse.persistence.jpa.rs.features.FeatureSet;
import org.eclipse.persistence.jpa.rs.features.FeatureSet.Feature;
import org.eclipse.persistence.jpa.rs.features.core.selflinks.SelfLinksResponseBuilder;
import org.eclipse.persistence.jpa.rs.features.fieldsfiltering.FieldsFilter;
import org.eclipse.persistence.jpa.rs.features.fieldsfiltering.FieldsFilteringValidator;
import org.eclipse.persistence.jpa.rs.features.paging.PageableQueryValidator;
import org.eclipse.persistence.jpa.rs.features.paging.PagingResponseBuilder;
import org.eclipse.persistence.jpa.rs.util.HrefHelper;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * Base class for query resource.
 *
 * @author gonural
 */
public abstract class AbstractQueryResource extends AbstractResource {
    private static final String CLASS_NAME = AbstractQueryResource.class.getName();

    /**
     * Named query update internal.
     *
     * @param version the version
     * @param persistenceUnit the persistence unit
     * @param queryName the name
     * @param headers the http headers
     * @param uriInfo the uri info
     * @return the response
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Response namedQueryUpdateInternal(String version, String persistenceUnit, String queryName, HttpHeaders headers, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "namedQueryUpdateInternal", new Object[] { "POST", version, persistenceUnit, queryName, uriInfo.getRequestUri().toASCIIString() });
        try {
            PersistenceContext context = getPersistenceContext(persistenceUnit, null, uriInfo.getBaseUri(), version, null);
            int result = context.queryExecuteUpdate(getMatrixParameters(uriInfo, persistenceUnit), queryName, getMatrixParameters(uriInfo, queryName), getQueryParameters(uriInfo));
            JAXBElement jaxbElement = new JAXBElement(new QName(ReservedWords.NO_ROUTE_JAXB_ELEMENT_LABEL), Integer.class, result);
            return Response.ok(new StreamingOutputMarshaller(context, jaxbElement, headers.getAcceptableMediaTypes())).build();
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred(ex);
        }
    }

    /**
     * Executes given named query.
     *
     * @param version the version
     * @param persistenceUnit the persistence unit
     * @param queryName named query to execute
     * @param headers the http headers
     * @param uriInfo the uri info
     * @return the response
     */
    protected Response namedQueryInternal(String version, String persistenceUnit, String queryName, HttpHeaders headers, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "namedQueryInternal", new Object[] { "GET", version, persistenceUnit, queryName, uriInfo.getRequestUri().toASCIIString() });
        try {
            final PersistenceContext context = getPersistenceContext(persistenceUnit, null, uriInfo.getBaseUri(), version, null);
            final Query query = context.buildQuery(getMatrixParameters(uriInfo, persistenceUnit), queryName, getMatrixParameters(uriInfo, queryName), getQueryParameters(uriInfo));
            final DatabaseQuery dbQuery = ((EJBQueryImpl<?>) query).getDatabaseQuery();
            final FeatureSet featureSet = context.getSupportedFeatureSet();

            final Response response;
            if (featureSet.isSupported(Feature.PAGING)) {
                response = processPageableQuery(context, queryName, dbQuery, query, headers, uriInfo);
            } else {
                response = namedQueryResponse(context, queryName, dbQuery, query, headers, uriInfo, featureSet.getResponseBuilder(Feature.NO_PAGING));
            }

            return response;
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred(ex);
        }
    }

    protected Response buildQueryOptionsResponse(String version, String persistenceUnit, String queryName, HttpHeaders httpHeaders, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "buildQueryOptionsResponse", new Object[]{"GET", version, persistenceUnit, queryName, uriInfo.getRequestUri().toASCIIString()});
        final PersistenceContext context = getPersistenceContext(persistenceUnit, null, uriInfo.getBaseUri(), version, null);

        // We need to make sure that query with given name exists
        final DatabaseQuery query = context.getServerSession().getQuery(queryName);
        if (query == null) {
            JPARSLogger.error(context.getSessionLog(), "jpars_could_not_find_query", new Object[] {queryName, persistenceUnit});
            throw JPARSException.responseCouldNotBeBuiltForNamedQueryRequest(queryName, context.getName());
        }

        final String linkValue = "<" + HrefHelper.buildQueryMetadataHref(context, queryName) + ">; rel=describedby";
        httpHeaders.getRequestHeaders().putSingle("Link", linkValue);

        return Response.ok()
                .header("Link", linkValue)
                .build();
    }

    private Response processPageableQuery(PersistenceContext context, String queryName, DatabaseQuery dbQuery, Query query, HttpHeaders headers, UriInfo uriInfo) {
        final PageableQueryValidator validator = new PageableQueryValidator(context, queryName, uriInfo);
        if (validator.isFeatureApplicable()) {
            // Do pagination
            query.setFirstResult(validator.getOffset());

            // Extra one is added to the limit value to check are there more rows or not.
            // It will be removed later on in the response builder.
            query.setMaxResults(validator.getLimit() + 1);

            return namedQueryResponse(context, queryName, dbQuery, query, headers, uriInfo, new PagingResponseBuilder());
        } else {
            // No pagination
            return namedQueryResponse(context, queryName, dbQuery, query, headers, uriInfo, new SelfLinksResponseBuilder());
        }
    }

    @SuppressWarnings("unchecked")
    private Response namedQueryResponse(PersistenceContext context, String queryName, DatabaseQuery dbQuery, Query query, HttpHeaders headers, UriInfo uriInfo, FeatureResponseBuilder responseBuilder) {
        // We need to add limit and offset to query parameters because request builder reads it from there
        final Map<String, Object> queryParams = getQueryParameters(uriInfo);
        if (query.getMaxResults() != Integer.MAX_VALUE) {
            queryParams.put(QueryParameters.JPARS_PAGING_LIMIT, String.valueOf(query.getMaxResults() - 1));
            queryParams.put(QueryParameters.JPARS_PAGING_OFFSET, String.valueOf(query.getFirstResult()));
        }

        // Fields filtering
        FieldsFilter fieldsFilter = null;
        if (context.getSupportedFeatureSet().isSupported(Feature.FIELDS_FILTERING)) {
            final FieldsFilteringValidator fieldsFilteringValidator = new FieldsFilteringValidator(uriInfo);
            if (fieldsFilteringValidator.isFeatureApplicable()) {
                fieldsFilter = fieldsFilteringValidator.getFilter();
            }
        }

        if (dbQuery instanceof ReportQuery) {
            // simple types selected : select u.name, u.age from employee
            List<ReportItem> reportItems = ((ReportQuery) dbQuery).getItems();
            List<Object[]> queryResults = query.getResultList();
            if ((queryResults != null) && (!queryResults.isEmpty())) {
                Object list = responseBuilder.buildReportQueryResponse(context, queryParams, queryResults, reportItems, uriInfo);
                if (list != null) {
                    return Response.ok(new StreamingOutputMarshaller(context, list, headers.getAcceptableMediaTypes(), fieldsFilter)).build();
                } else {
                    // something is wrong with the descriptors
                    throw JPARSException.responseCouldNotBeBuiltForNamedQueryRequest(queryName, context.getName());
                }
            }
            return Response.ok(new StreamingOutputMarshaller(context, queryResults, headers.getAcceptableMediaTypes(), fieldsFilter)).build();
        }

        List<Object> results = query.getResultList();
        if (results != null) {
            Object list = responseBuilder.buildReadAllQueryResponse(context, queryParams, results, uriInfo);
            return Response.ok(new StreamingOutputMarshaller(context, list, headers.getAcceptableMediaTypes(), fieldsFilter)).build();
        }
        return Response.ok(new StreamingOutputMarshaller(context, null, headers.getAcceptableMediaTypes(), fieldsFilter)).build();
    }
}
