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
import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
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
            response.addItem(populatePagedReadAllQueryResultListItemLinks(context, item));
        }

        response.setCount(items.size());
        return (ReadAllQueryResultCollection) populatePagingLinks(queryParams, uriInfo, response);
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilderImpl#buildReportQueryResponse(org.eclipse.persistence.jpa.rs.PersistenceContext, java.util.Map, java.util.List, java.util.List, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Object buildReportQueryResponse(PersistenceContext context, Map<String, Object> queryParams, List<Object[]> results, List<ReportItem> items, UriInfo uriInfo) {
        return populatePagedReportQueryResultList(queryParams, results, items, uriInfo);
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilderImpl#buildCollectionAttributeResponse(org.eclipse.persistence.jpa.rs.PersistenceContext, java.util.Map, java.lang.String, java.lang.Object, javax.ws.rs.core.UriInfo)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object buildCollectionAttributeResponse(PersistenceContext context, Map<String, Object> queryParams, String attribute, Object results, UriInfo uriInfo) {
        if (results instanceof Collection) {
            if (containsDomainObjects(results)) {
                Collection collection = (Collection) results;
                ReadAllQueryResultCollection response = new ReadAllQueryResultCollection();
                for (Object item : collection) {
                    response.addItem(populatePagedReadAllQueryResultListItemLinks(context, item));
                }

                response.setCount(collection.size());
                return (ReadAllQueryResultCollection) populatePagingLinks(queryParams, uriInfo, response);
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private JAXBElement populatePagedReadAllQueryResultListItemLinks(PersistenceContext context, Object result) {
        // populate links for the entity
        JAXBElement item = new JAXBElement(new QName(ReservedWords.JPARS_LIST_ITEMS_NAME), result.getClass(), result);
        ClassDescriptor descriptor = context.getJAXBDescriptorForClass(result.getClass());
        if ((item.getValue() instanceof PersistenceWeavedRest) && (descriptor != null) && (context != null)) {
            PersistenceWeavedRest entity = (PersistenceWeavedRest) item.getValue();
            entity._persistence_setLinks(new ArrayList<Link>());
            String entityId = IdHelper.stringifyId(result, descriptor.getAlias(), context);
            String href = context.getBaseURI() + context.getVersion() + "/" + context.getName() + "/entity/" + descriptor.getAlias() + "/" + entityId;
            entity._persistence_getLinks().add(new Link(ReservedWords.JPARS_REL_SELF, null, href));
        }
        return item;
    }

    private PageableCollection populatePagingLinks(Map<String, Object> queryParams, UriInfo uriInfo, PageableCollection resultCollection) {
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

    @SuppressWarnings({ "rawtypes" })
    private Object populatePagedReportQueryResultList(Map<String, Object> queryParams, List<Object[]> results, List<ReportItem> reportItems, UriInfo uriInfo) {
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

}
