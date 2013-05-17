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
package org.eclipse.persistence.jpa.rs.features.generic;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilder;
import org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilderUtil;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultList;
import org.eclipse.persistence.jpa.rs.util.list.ReportQueryResultListItem;

public class GenericResponseBuilder extends FeatureResponseBuilderUtil implements FeatureResponseBuilder {

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilder#buildReadAllQueryResponse(org.eclipse.persistence.jpa.rs.PersistenceContext, java.util.Map, java.util.List, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Object buildReadAllQueryResponse(PersistenceContext context, Map<String, Object> queryParams, List<Object> items, UriInfo uriInfo) {
        return items;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilder#buildReportQueryResponse(org.eclipse.persistence.jpa.rs.PersistenceContext, java.util.Map, java.util.List, java.util.List, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Object buildReportQueryResponse(PersistenceContext context, Map<String, Object> queryParams, List<Object[]> results, List<ReportItem> items, UriInfo uriInfo) {
        if ((results != null) && (!results.isEmpty())) {
            ReportQueryResultList list = populateReportQueryResultList(results, items);
            return list;
        } else {
            return results;
        }
    }

    @SuppressWarnings("rawtypes")
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
}
