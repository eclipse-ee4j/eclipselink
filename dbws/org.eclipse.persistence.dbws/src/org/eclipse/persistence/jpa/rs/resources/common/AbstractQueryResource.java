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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.jpa.rs.Paging;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.QueryParameters;
import org.eclipse.persistence.jpa.rs.ReservedWords;
import org.eclipse.persistence.jpa.rs.util.IdHelper;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;
import org.eclipse.persistence.jpa.rs.util.list.PagedCollection;
import org.eclipse.persistence.jpa.rs.util.list.ReadAllQueryResultCollection;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultCollection;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultList;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultListItem;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * @author gonural
 *
 */
public abstract class AbstractQueryResource extends AbstractResource {
    private static String NO_PREVIOUS_CHUNK = "-1";

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
        PersistenceContext context = getPersistenceContext(persistenceUnit, uriInfo.getBaseUri(), version, null);
        if (context == null) {
            JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            return Response.status(Status.NOT_FOUND).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        }
        int result = context.queryExecuteUpdate(getMatrixParameters(uriInfo, persistenceUnit), name, getMatrixParameters(uriInfo, name), getQueryParameters(uriInfo));
        JAXBElement jaxbElement = new JAXBElement(new QName(StreamingOutputMarshaller.NO_ROUTE_JAXB_ELEMENT_LABEL), new Integer(result).getClass(), result);
        return Response.ok(new StreamingOutputMarshaller(context, jaxbElement, headers.getAcceptableMediaTypes())).build();
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
        PersistenceContext context = getPersistenceContext(persistenceUnit, uriInfo.getBaseUri(), version, null);
        if (context == null) {
            JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            return Response.status(Status.NOT_FOUND).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        }

        Query query = context.buildQuery(getMatrixParameters(uriInfo, persistenceUnit), name, getMatrixParameters(uriInfo, name), getQueryParameters(uriInfo));

        DatabaseQuery dbQuery = ((EJBQueryImpl<?>) query).getDatabaseQuery();

        if (context.isPagingSupported()) {
            Paging paging = new Paging(getQueryParameters(uriInfo), dbQuery, context);
            if (paging.isRequested()) {
                if (paging.isQueryParamSetValid()) {
                    return pagedResponse(context, dbQuery, query, headers, getQueryParameters(uriInfo), uriInfo);
                }
                // some query parameters for paging are not valid!
                return Response.status(Status.BAD_REQUEST).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
            }
        }
        return nonPagedResponse(context, dbQuery, query, headers, uriInfo);
    }

    @SuppressWarnings("unchecked")
    private Response nonPagedResponse(PersistenceContext context, DatabaseQuery dbQuery, Query query, HttpHeaders headers, UriInfo uriInfo) {
        if (context.isVersionGreaterOrEqualTo(SERVICE_VERSION_2_0)) {
            // TODO: Add self links to each item (readAll query results only)
            //       Divide response into two sections, items and links 
            //       Add self link to entire response
            return nonPagedResponseWithSelfLinks(context, dbQuery, query, headers, uriInfo);
        } else {
            // version is null OR SERVICE_VERSION_1_0
            if (dbQuery instanceof ReportQuery) {
                // simple types selected : select u.name, u.age from employee
                List<ReportItem> reportItems = ((ReportQuery) dbQuery).getItems();
                List<Object[]> queryResults = query.getResultList();
                if ((queryResults != null) && (!queryResults.isEmpty())) {
                    ReportQueryResultList list = populateReportQueryResultList(queryResults, reportItems);
                    if (list != null) {
                        return Response.ok(new StreamingOutputMarshaller(context, list, headers.getAcceptableMediaTypes())).build();
                    } else {
                        // something wrong with the descriptors
                        return Response.status(Status.INTERNAL_SERVER_ERROR).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
                    }
                }
                return Response.ok(new StreamingOutputMarshaller(context, queryResults, headers.getAcceptableMediaTypes())).build();
            }

            // read all queries
            List<Object> results = query.getResultList();
            return Response.ok(new StreamingOutputMarshaller(context, results, headers.getAcceptableMediaTypes())).build();
        }
    }

    @SuppressWarnings("unchecked")
    private Response nonPagedResponseWithSelfLinks(PersistenceContext context, DatabaseQuery dbQuery, Query query, HttpHeaders headers, UriInfo uriInfo) {
        if (dbQuery instanceof ReportQuery) {
            // simple types selected : select u.name, u.age from employee
            List<ReportItem> reportItems = ((ReportQuery) dbQuery).getItems();
            List<Object[]> queryResults = query.getResultList();
            if ((queryResults != null) && (!queryResults.isEmpty())) {
                ReportQueryResultCollection list = populateNonPagedReportQueryResultListWithSelfLinks(queryResults, reportItems, uriInfo);
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
            ReadAllQueryResultCollection list = populateNonPagedReadAllQueryResultListWithSelfLinks(context, results, uriInfo);
            return Response.ok(new StreamingOutputMarshaller(context, list, headers.getAcceptableMediaTypes())).build();
        }
        return Response.ok(new StreamingOutputMarshaller(context, results, headers.getAcceptableMediaTypes())).build();
    }

    private ReadAllQueryResultCollection populateNonPagedReadAllQueryResultListWithSelfLinks(PersistenceContext context, List<Object> items, UriInfo uriInfo) {
        ReadAllQueryResultCollection response = new ReadAllQueryResultCollection();
        for (Object item : items) {
            response.addItem(populateNonPagedReadAllQueryResultListItemLinks(context, item));
        }

        response.addLink(new Link(ReservedWords.JPARS_REL_SELF, null, uriInfo.getRequestUri().toString()));
        return response;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private JAXBElement populateNonPagedReadAllQueryResultListItemLinks(PersistenceContext context, Object result) {
        // populate links for the entity
        JAXBElement item = new JAXBElement(new QName(ReservedWords.JPARS_LIST_ITEMS_NAME), result.getClass(), result);
        ClassDescriptor descriptor = context.getJAXBDescriptorForClass(result.getClass());
        if ((item.getValue() instanceof PersistenceWeavedRest) && (descriptor != null) && (context != null)) {
            PersistenceWeavedRest entity = (PersistenceWeavedRest) item.getValue();
            if (entity._persistence_getLinks() == null) {
                entity._persistence_setLinks(new ArrayList<Link>());
            }
            String entityId = IdHelper.stringifyId(result, descriptor.getAlias(), context);
            String href = context.getBaseURI() + context.getVersion() + "/" + context.getName() + "/entity/" + descriptor.getAlias() + "/" + entityId;
            entity._persistence_getLinks().add(new Link(ReservedWords.JPARS_REL_SELF, null, href));
        }
        return item;
    }

    @SuppressWarnings("rawtypes")
    private ReportQueryResultCollection populateNonPagedReportQueryResultListWithSelfLinks(List<Object[]> results, List<ReportItem> reportItems, UriInfo uriInfo) {
        ReportQueryResultCollection response = new ReportQueryResultCollection();
        for (Object result : results) {
            ReportQueryResultListItem queryResultListItem = new ReportQueryResultListItem();
            List<JAXBElement> jaxbFields = createShellJAXBElementList(reportItems, result);
            if (jaxbFields == null) {
                return null;
            }
            // We don't have a way of determining self links for the report query responses
            // so, no links array will be inserted into individual items in the response
            queryResultListItem.setFields(jaxbFields);
            response.addItem(queryResultListItem);
        }

        List<Link> links = new ArrayList<Link>();
        links.add(new Link(ReservedWords.JPARS_REL_SELF, null, uriInfo.getRequestUri().toString()));
        response.addLink(new Link(ReservedWords.JPARS_REL_SELF, null, uriInfo.getRequestUri().toString()));
        return response;
    }

    @SuppressWarnings({ "rawtypes" })
    private ReportQueryResultList populateReportQueryResultList(List<Object[]> results, List<ReportItem> reportItems) {
        ReportQueryResultList response = new ReportQueryResultList();
        for (Object result : results) {
            ReportQueryResultListItem queryResultListItem = new ReportQueryResultListItem();
            List<JAXBElement> jaxbFields = createShellJAXBElementList(reportItems, result);
            if (jaxbFields == null) {
                return null;
            }
            queryResultListItem.setFields(jaxbFields);
            response.addItem(queryResultListItem);
        }
        return response;
    }

    @SuppressWarnings("unchecked")
    private Response pagedResponse(PersistenceContext context, DatabaseQuery dbQuery, Query query, HttpHeaders headers, Map<String, Object> queryParams, UriInfo uriInfo) {
        // set limit and offset
        query.setFirstResult(Integer.parseInt(((String) queryParams.get(QueryParameters.JPARS_PAGING_OFFSET))));
        query.setMaxResults(Integer.parseInt(((String) queryParams.get(QueryParameters.JPARS_PAGING_LIMIT))));

        if (dbQuery instanceof ReportQuery) {
            // simple types selected : select u.name, u.age from employee
            List<ReportItem> reportItems = ((ReportQuery) dbQuery).getItems();
            List<Object[]> queryResults = query.getResultList();
            if ((queryResults != null) && (!queryResults.isEmpty())) {
                ReportQueryResultCollection list = populatePagedReportQueryResultList(queryParams, queryResults, reportItems, uriInfo);
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
            ReadAllQueryResultCollection list = populatePagedReadAllQueryResultList(context, queryParams, results, uriInfo);
            return Response.ok(new StreamingOutputMarshaller(context, list, headers.getAcceptableMediaTypes())).build();
        }
        return Response.ok(new StreamingOutputMarshaller(context, results, headers.getAcceptableMediaTypes())).build();
    }

    private ReadAllQueryResultCollection populatePagedReadAllQueryResultList(PersistenceContext context, Map<String, Object> queryParams, List<Object> items, UriInfo uriInfo) {
        ReadAllQueryResultCollection response = new ReadAllQueryResultCollection();
        for (Object item : items) {
            response.addItem(populatePagedReadAllQueryResultListItemLinks(context, item));
        }

        response.setCount(items.size());
        return (ReadAllQueryResultCollection) populatePagingLinks(queryParams, uriInfo, response);
    }

    @SuppressWarnings({ "rawtypes" })
    private ReportQueryResultCollection populatePagedReportQueryResultList(Map<String, Object> queryParams, List<Object[]> results, List<ReportItem> reportItems, UriInfo uriInfo) {
        ReportQueryResultCollection response = new ReportQueryResultCollection();
        for (Object result : results) {
            ReportQueryResultListItem queryResultListItem = new ReportQueryResultListItem();
            List<JAXBElement> jaxbFields = createShellJAXBElementList(reportItems, result);
            if (jaxbFields == null) {
                return null;
            }
            // We don't have a way of determining self links for the report query responses
            // so, no links array will be inserted into individual items in the response
            queryResultListItem.setFields(jaxbFields);
            response.addItem(queryResultListItem);
        }

        response.setCount(results.size());
        return (ReportQueryResultCollection) populatePagingLinks(queryParams, uriInfo, response);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private JAXBElement populatePagedReadAllQueryResultListItemLinks(PersistenceContext context, Object result) {
        // populate links for the entity
        JAXBElement item = new JAXBElement(new QName(ReservedWords.JPARS_LIST_ITEMS_NAME), result.getClass(), result);
        ClassDescriptor descriptor = context.getJAXBDescriptorForClass(result.getClass());
        if ((item.getValue() instanceof PersistenceWeavedRest) && (descriptor != null) && (context != null)) {
            PersistenceWeavedRest entity = (PersistenceWeavedRest) item.getValue();
            if (entity._persistence_getLinks() == null) {
                entity._persistence_setLinks(new ArrayList<Link>());
            }
            String entityId = IdHelper.stringifyId(result, descriptor.getAlias(), context);
            String href = context.getBaseURI() + context.getVersion() + "/" + context.getName() + "/entity/" + descriptor.getAlias() + "/" + entityId;
            entity._persistence_getLinks().add(new Link(ReservedWords.JPARS_REL_SELF, null, href));
        }
        return item;
    }

    private PagedCollection populatePagingLinks(Map<String, Object> queryParams, UriInfo uriInfo, PagedCollection resultCollection) {
        // populate links for entire response
        List<Link> links = new ArrayList<Link>();
        int limit = Integer.parseInt((String) queryParams.get(QueryParameters.JPARS_PAGING_LIMIT));
        int offset = Integer.parseInt((String) queryParams.get(QueryParameters.JPARS_PAGING_OFFSET));
        String nextOffset = null;
        String prevOffset = null;
        if (limit > offset) {
            nextOffset = String.valueOf(limit);
            prevOffset = NO_PREVIOUS_CHUNK;
        } else {
            if (limit == offset) {
                prevOffset = "0";
            }
            nextOffset = String.valueOf(limit + offset);
            prevOffset = String.valueOf(offset - limit);
        }

        UriBuilder uriBuilder;

        if (resultCollection.getCount() != null) {
            int actualCount = resultCollection.getCount();
            if (actualCount >= limit) {
                // next link
                // The uri might have other query/matrix parameters, just replace the limit and offset 
                // for next and prev links and leave the rest untouched
                uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri());
                uriBuilder.replaceQueryParam(QueryParameters.JPARS_PAGING_OFFSET, nextOffset);
                links.add(new Link(ReservedWords.JPARS_REL_NEXT, null, uriBuilder.build().toString()));
                resultCollection.setHasMore(true);
            } else {
                resultCollection.setHasMore(false);
            }
        }

        if (!NO_PREVIOUS_CHUNK.equals(prevOffset)) {
            // prev link
            uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri());
            uriBuilder.replaceQueryParam(QueryParameters.JPARS_PAGING_OFFSET, prevOffset);
            links.add(new Link(ReservedWords.JPARS_REL_PREV, null, uriBuilder.build().toString()));
        }

        links.add(new Link(ReservedWords.JPARS_REL_SELF, null, uriInfo.getRequestUri().toString()));

        resultCollection.setLinks(links);
        resultCollection.setOffset(offset);
        resultCollection.setLimit(limit);

        return resultCollection;
    }
}