/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs;

import java.util.List;
import java.util.Map;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

public class Paging {
    private String offset = null;
    private String limit = null;

    @SuppressWarnings("unused")
    private Paging() {

    }

    /**
     * Instantiates a new paging.
     * 
     * @param queryParameters the query parameters
     * @param dbQuery the db query
     */
    public Paging(Map<String, Object> queryParameters, DatabaseQuery dbQuery, PersistenceContext context) {
        if (!context.isPagingSupported()) {
            return;
        }

        boolean hasOrderBy = false;

        if ((dbQuery instanceof ObjectLevelReadQuery) || (dbQuery instanceof ReportQuery)) {
            List<Expression> orderBy = null;
            if (dbQuery instanceof ReportQuery) {
                orderBy = ((ReportQuery) dbQuery).getOrderByExpressions();
            } else if (dbQuery instanceof ObjectLevelReadQuery) {
                orderBy = ((ObjectLevelReadQuery) dbQuery).getOrderByExpressions();
            } else {
                return;
            }

            if ((orderBy != null) && (!orderBy.isEmpty())) {
                hasOrderBy = true;
            }
        }

        if (hasOrderBy) {
            String paramLimit = (String) queryParameters.get(QueryParameters.JPARS_PAGING_LIMIT);
            String paramOffset = (String) queryParameters.get(QueryParameters.JPARS_PAGING_OFFSET);

            if ((paramLimit == null) && (paramOffset == null)) {
                return;
            }

            if (paramOffset != null) {
                offset = paramOffset;
            } else {
                offset = Integer.toString(SystemDefaults.PAGING_DEFAULT_PAGE_OFFSET);
            }

            if (paramLimit != null) {
                limit = paramLimit;
            } else {
                limit = Integer.toString(SystemDefaults.PAGING_DEFAULT_PAGE_LIMIT);
            }
        }
    }

    /**
     * Checks if paging is requested.
     *
     * @return true, if is requested
     */
    public boolean isRequested() {
        if ((offset != null) && (limit != null)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if is query param set valid.
     *
     * @return true, if is query param set valid
     */
    public boolean isQueryParamSetValid() {
        try {
            if ((offset != null) && (limit != null)) {
                if ((Integer.parseInt(offset) >= 0) && (Integer.parseInt(limit) > 0)) {
                    return true;
                }
            }
        } catch (NumberFormatException ex) {
            //TODO: Log it!
        }
        return false;
    }
}