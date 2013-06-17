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
package org.eclipse.persistence.jpa.rs.features.clientinitiated.paging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.ItemLinks;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.QueryParameters;
import org.eclipse.persistence.jpa.rs.ReservedWords;
import org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilderImpl;
import org.eclipse.persistence.jpa.rs.util.IdHelper;
import org.eclipse.persistence.jpa.rs.util.list.PageableCollection;
import org.eclipse.persistence.jpa.rs.util.list.ReadAllQueryResultCollection;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultCollection;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultListItem;

public class PagingResponseBuilder extends FeatureResponseBuilderImpl {
    private static String NO_PREVIOUS_CHUNK = "-1";

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilderImpl#buildReadAllQueryResponse(org.eclipse.persistence.jpa.rs.PersistenceContext, java.util.Map, java.util.List, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Object buildReadAllQueryResponse(PersistenceContext context, Map<String, Object> queryParams, List<Object> items, UriInfo uriInfo) {
        ReadAllQueryResultCollection response = new ReadAllQueryResultCollection();
        for (Object item : items) {
            response.addItem(populatePagedReadAllQueryItemLinks(context, item));
        }

        response.setCount(items.size());
        return populatePagedCollectionLinks(queryParams, uriInfo, response);
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilderImpl#buildReportQueryResponse(org.eclipse.persistence.jpa.rs.PersistenceContext, java.util.Map, java.util.List, java.util.List, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Object buildReportQueryResponse(PersistenceContext context, Map<String, Object> queryParams, List<Object[]> results, List<ReportItem> items, UriInfo uriInfo) {
        return populatePagedReportQueryCollectionLinks(queryParams, results, items, uriInfo);
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilderImpl#buildAttributeResponse(org.eclipse.persistence.jpa.rs.PersistenceContext, java.util.Map, java.lang.String, java.lang.Object, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Object buildAttributeResponse(PersistenceContext context, Map<String, Object> queryParams, String attribute, Object results, UriInfo uriInfo) {
        if (results instanceof Collection) {
            if (containsDomainObjects(results)) {
                ReadAllQueryResultCollection collection = (ReadAllQueryResultCollection) results;
                if (collection != null) {
                    List<Object> items = collection.getItems();
                    if ((items != null) && (!items.isEmpty())) {
                        ReadAllQueryResultCollection response = new ReadAllQueryResultCollection();
                        response.setItems(items);
                        response.setCount(collection.getItems().size());
                        return populatePagedCollectionLinks(queryParams, uriInfo, response);
                    }
                }
            }
        }
        return results;
    }

    @SuppressWarnings("rawtypes")
    private boolean containsDomainObjects(Object object) {
        Collection collection = (Collection) object;
        for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
            Object collectionItem = iterator.next();
            if (PersistenceWeavedRest.class.isAssignableFrom(collectionItem.getClass())) {
                return true;
            }
        }
        return false;
    }

    private Object populatePagedReadAllQueryItemLinks(PersistenceContext context, Object result) {
        // populate links for the entity
        ClassDescriptor descriptor = context.getJAXBDescriptorForClass(result.getClass());
        if ((result instanceof PersistenceWeavedRest) && (descriptor != null) && (context != null)) {
            ItemLinks itemLinks = new ItemLinks();
            PersistenceWeavedRest entity = (PersistenceWeavedRest) result;
            String entityId = IdHelper.stringifyId(result, descriptor.getAlias(), context);
            String href = context.getBaseURI() + context.getVersion() + "/" + context.getName() + "/entity/" + descriptor.getAlias() + "/" + entityId;
            itemLinks.addItem(new LinkV2(ReservedWords.JPARS_REL_SELF, href));
            entity._persistence_setLinks(itemLinks);
            return entity;
        }
        return result;
    }

    private PageableCollection populatePagedCollectionLinks(Map<String, Object> queryParams, UriInfo uriInfo, PageableCollection resultCollection) {
        // populate links for entire response
        List<LinkV2> links = new ArrayList<LinkV2>();
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
                links.add(new LinkV2(ReservedWords.JPARS_REL_NEXT, uriBuilder.build().toString()));
                resultCollection.setHasMore(true);
            } else {
                resultCollection.setHasMore(false);
            }
        }

        if (!NO_PREVIOUS_CHUNK.equals(prevOffset)) {
            // prev link
            uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri());
            uriBuilder.replaceQueryParam(QueryParameters.JPARS_PAGING_OFFSET, prevOffset);
            links.add(new LinkV2(ReservedWords.JPARS_REL_PREV, uriBuilder.build().toString()));
        }

        links.add(new LinkV2(ReservedWords.JPARS_REL_SELF, uriInfo.getRequestUri().toString()));

        resultCollection.setLinks(links);
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
        return populatePagedCollectionLinks(queryParams, uriInfo, response);
    }
}
