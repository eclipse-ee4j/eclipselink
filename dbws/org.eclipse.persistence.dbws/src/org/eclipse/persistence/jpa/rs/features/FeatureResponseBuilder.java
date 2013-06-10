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
package org.eclipse.persistence.jpa.rs.features;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.jpa.rs.PersistenceContext;

public interface FeatureResponseBuilder {

    /**
     * Builds the read all query response.
     *
     * @param context the context
     * @param queryParams the query params
     * @param items the items
     * @param uriInfo the uri info
     * @return the object
     */
    Object buildReadAllQueryResponse(PersistenceContext context, Map<String, Object> queryParams, List<Object> items, UriInfo uriInfo);

    /**
     * Builds the report query response.
     *
     * @param context the context
     * @param queryParams the query params
     * @param results the results
     * @param items the items
     * @param uriInfo the uri info
     * @return the object
     */
    Object buildReportQueryResponse(PersistenceContext context, Map<String, Object> queryParams, List<Object[]> results, List<ReportItem> items, UriInfo uriInfo);

    /**
     * Builds the single entity response.
     *
     * @param context the context
     * @param queryParams the query params
     * @param result the result
     * @param uriInfo the uri info
     * @return the object
     */
    Object buildSingleEntityResponse(PersistenceContext context, Map<String, Object> queryParams, Object result, UriInfo uriInfo);

    /**
     * Builds the attribute response.
     *
     * @param context the context
     * @param queryParams the query params
     * @param attribute the attribute
     * @param results the results
     * @param uriInfo the uri info
     * @return the object
     */
    Object buildAttributeResponse(PersistenceContext context, Map<String, Object> queryParams, String attribute, Object results, UriInfo uriInfo);
}
