/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//      gonural - initial implementation
//      2014-09-01-2.6.0 Dmitry Kornilov
//        - Added buildSingleResultQueryResponse method.
package org.eclipse.persistence.jpa.rs.features;

import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.jpa.rs.PersistenceContext;

import jakarta.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;

/**
 * Common interface for all response builders.
 *
 * @author gonural
 */
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

    /**
     * Builds the single result query response.
     *
     * @param context the context
     * @param queryParams the query params
     * @param result the result
     * @param items the report items (result of ReportQuery)
     * @param uriInfo the uri info
     * @return the response
     */
    Object buildSingleResultQueryResponse(PersistenceContext context, Map<String, Object> queryParams, Object result, List<ReportItem> items, UriInfo uriInfo);
}
