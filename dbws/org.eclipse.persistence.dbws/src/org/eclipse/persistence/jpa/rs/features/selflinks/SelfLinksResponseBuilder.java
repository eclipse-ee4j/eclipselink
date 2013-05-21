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
package org.eclipse.persistence.jpa.rs.features.selflinks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.ReservedWords;
import org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilderImpl;
import org.eclipse.persistence.jpa.rs.util.IdHelper;
import org.eclipse.persistence.jpa.rs.util.list.ReadAllQueryResultCollection;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultCollection;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultListItem;

public class SelfLinksResponseBuilder extends FeatureResponseBuilderImpl {

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilder#buildReadAllQueryResponse(org.eclipse.persistence.jpa.rs.PersistenceContext, java.util.Map, java.util.List, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Object buildReadAllQueryResponse(PersistenceContext context, Map<String, Object> queryParams, List<Object> items, UriInfo uriInfo) {
        return nonPagedResponseWithSelfLinks(context, items, uriInfo);
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilder#buildReportQueryResponse(org.eclipse.persistence.jpa.rs.PersistenceContext, java.util.Map, java.util.List, java.util.List, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Object buildReportQueryResponse(PersistenceContext context, Map<String, Object> queryParams, List<Object[]> results, List<ReportItem> items, UriInfo uriInfo) {
        return populateNonPagedReportQueryResultListWithSelfLinks(results, items, uriInfo);
    }

    private Object nonPagedResponseWithSelfLinks(PersistenceContext context, List<Object> results, UriInfo uriInfo) {
        if ((results != null) && (!results.isEmpty())) {
            return populateNonPagedReadAllQueryResultListWithSelfLinks(context, results, uriInfo);
        }
        return results;
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
}
