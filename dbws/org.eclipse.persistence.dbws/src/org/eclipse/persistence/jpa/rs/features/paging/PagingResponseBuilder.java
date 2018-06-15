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
//     gonural - initial implementation
//     2014-09-01-2.6.0 Dmitry Kornilov
//       - JPARS 2.0 related changes
package org.eclipse.persistence.jpa.rs.features.paging;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.ItemLinks;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.QueryParameters;
import org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilderImpl;
import org.eclipse.persistence.jpa.rs.features.ItemLinksBuilder;
import org.eclipse.persistence.jpa.rs.util.HrefHelper;
import org.eclipse.persistence.jpa.rs.util.IdHelper;
import org.eclipse.persistence.jpa.rs.util.list.PageableCollection;
import org.eclipse.persistence.jpa.rs.util.list.ReadAllQueryResultCollection;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultCollection;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultListItem;

/**
 * FeatureResponseBuilder implementation used for pageable collections. Used in JPARS 2.0.
 *
 * @author gonural
 * @since EclipseLink 2.6.0.
 */
public class PagingResponseBuilder extends FeatureResponseBuilderImpl {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object buildReadAllQueryResponse(PersistenceContext context, Map<String, Object> queryParams, List<Object> items, UriInfo uriInfo) {
        ReadAllQueryResultCollection response = new ReadAllQueryResultCollection();
        for (Object item : items) {
            response.addItem(populatePagedReadAllQueryItemLinks(context, item));
        }

        return populatePagedCollectionLinks(queryParams, uriInfo, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object buildReportQueryResponse(PersistenceContext context, Map<String, Object> queryParams, List<Object[]> results, List<ReportItem> items, UriInfo uriInfo) {
        return populatePagedReportQueryCollectionLinks(queryParams, results, items, uriInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object buildAttributeResponse(PersistenceContext context, Map<String, Object> queryParams, String attribute, Object results, UriInfo uriInfo) {
        if (results instanceof List) {
            ReadAllQueryResultCollection response = new ReadAllQueryResultCollection();
            response.setItems((List<Object>) results);
            return populatePagedCollectionLinks(queryParams, uriInfo, response);
        }
        return results;
    }

    private Object populatePagedReadAllQueryItemLinks(PersistenceContext context, Object result) {
        // populate links for the entity
        ClassDescriptor descriptor = context.getJAXBDescriptorForClass(result.getClass());
        if ((result instanceof PersistenceWeavedRest) && (descriptor != null)) {
            final PersistenceWeavedRest entity = (PersistenceWeavedRest) result;
            final String href = HrefHelper.buildEntityHref(context, descriptor.getAlias(), IdHelper.stringifyId(result, descriptor.getAlias(), context));

            final ItemLinks itemLinks = (new ItemLinksBuilder())
                    .addSelf(href)
                    .addCanonical(href)
                    .build();

            entity._persistence_setLinks(itemLinks);
            return entity;
        }
        return result;
    }

    private PageableCollection<?> populatePagedCollectionLinks(Map<String, Object> queryParams, UriInfo uriInfo, PageableCollection<?> resultCollection) {
        // populate links for entire response
        final ItemLinksBuilder itemLinksBuilder = new ItemLinksBuilder();

        final int limit = Integer.parseInt((String) queryParams.get(QueryParameters.JPARS_PAGING_LIMIT));
        final int offset = Integer.parseInt((String) queryParams.get(QueryParameters.JPARS_PAGING_OFFSET));

        final UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri());

        if (resultCollection.getItems() != null) {
            final int actualCount = resultCollection.getItems().size();
            if (actualCount > limit) {
                // Remove the last item from collection. It was artificially added to indicate there are more records or not.
                resultCollection.getItems().remove(actualCount - 1);
                resultCollection.setCount(actualCount - 1);

                // next link
                // The uri might have other query/matrix parameters, just replace the limit and offset
                // for next and prev links and leave the rest untouched
                uriBuilder.replaceQueryParam(QueryParameters.JPARS_PAGING_OFFSET, String.valueOf(limit + offset));
                itemLinksBuilder.addNext(uriBuilder.build().toString());
                resultCollection.setHasMore(true);
            } else {
                resultCollection.setHasMore(false);
                resultCollection.setCount(actualCount);
            }
        } else {
            resultCollection.setCount(0);
        }

        if (offset != 0) {
            if (offset > limit) {
                uriBuilder.replaceQueryParam(QueryParameters.JPARS_PAGING_OFFSET, String.valueOf(offset - limit));
            } else {
                uriBuilder.replaceQueryParam(QueryParameters.JPARS_PAGING_OFFSET, "0");
            }

            if (resultCollection.getItems() != null && !resultCollection.getItems().isEmpty()) {
                itemLinksBuilder.addPrev(uriBuilder.build().toString());
            }
        }

        itemLinksBuilder.addSelf(uriInfo.getRequestUri().toString());

        resultCollection.setLinks(itemLinksBuilder.build().getLinks());
        resultCollection.setOffset(offset);
        resultCollection.setLimit(limit);

        return resultCollection;
    }

    /**
     * Populate paged report query collection links.
     *
     * @param queryParams the query params
     * @param results the results
     * @param reportItems the report items
     * @param uriInfo the uri info
     * @return the pageable collection
     */
    @SuppressWarnings({ "rawtypes" })
    private PageableCollection populatePagedReportQueryCollectionLinks(Map<String, Object> queryParams, List<Object[]> results, List<ReportItem> reportItems, UriInfo uriInfo) {
        ReportQueryResultCollection response = new ReportQueryResultCollection();
        for (Object result : results) {
            ReportQueryResultListItem queryResultListItem = new ReportQueryResultListItem();
            List<JAXBElement<?>> jaxbFields = createShellJAXBElementList(reportItems, result);
            // We don't have a way of determining self links for the report query responses
            // so, no links array will be inserted into individual items in the response
            queryResultListItem.setFields(jaxbFields);
            response.addItem(queryResultListItem);
        }

        response.setCount(results.size());
        return populatePagedCollectionLinks(queryParams, uriInfo, response);
    }
}
