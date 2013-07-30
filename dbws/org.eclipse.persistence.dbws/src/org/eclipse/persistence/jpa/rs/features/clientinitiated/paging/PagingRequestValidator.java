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

import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.ws.rs.core.UriInfo;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.jpa.rs.QueryParameters;
import org.eclipse.persistence.jpa.rs.SystemDefaults;
import org.eclipse.persistence.jpa.rs.features.FeatureRequestValidatorImpl;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadQuery;

public class PagingRequestValidator extends FeatureRequestValidatorImpl {
    private String offset = null;
    private String limit = null;
    public static String DB_QUERY = "dbQuery";
    public static String QUERY = "query";

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureRequestValidatorImpl#isRequestValid(javax.ws.rs.core.UriInfo, java.util.Map)
     */
    @Override
    public boolean isRequestValid(UriInfo uri, Map<String, Object> additionalParams) {
        Query query = null;
        ReadQuery dbQuery = null;

        if ((additionalParams != null) && (!additionalParams.isEmpty())) {
            dbQuery = (ReadQuery) additionalParams.get(DB_QUERY);
            query = (Query) additionalParams.get(QUERY);

            if ((dbQuery != null) && (query != null) && ((dbQuery instanceof ObjectLevelReadQuery) || (dbQuery instanceof ReadAllQuery))) {
                List<Expression> orderBy = null;
                if (dbQuery instanceof ReadAllQuery) {
                    orderBy = ((ReadAllQuery) dbQuery).getOrderByExpressions();
                } else if (dbQuery instanceof ObjectLevelReadQuery) {
                    orderBy = ((ObjectLevelReadQuery) dbQuery).getOrderByExpressions();
                }

                if ((orderBy == null) || (orderBy.isEmpty())) {
                    JPARSLogger.warning("no_orderby_clause_for_paging", new Object[] { query.toString() });
                }
            }
        }

        Map<String, Object> queryParameters = getQueryParameters(uri);

        String paramLimit = (String) queryParameters.get(QueryParameters.JPARS_PAGING_LIMIT);
        String paramOffset = (String) queryParameters.get(QueryParameters.JPARS_PAGING_OFFSET);

        if ((paramLimit == null) && (paramOffset == null)) {
            return false;
        }

        if (paramOffset != null) {
            offset = paramOffset;
        } else {
            offset = Integer.toString(SystemDefaults.JPARS_DEFAULT_PAGE_OFFSET);
        }

        if (paramLimit != null) {
            limit = paramLimit;
        } else {
            limit = Integer.toString(SystemDefaults.JPARS_DEFAULT_PAGE_LIMIT);
        }

        try {
            if ((offset != null) && (limit != null)) {
                int intOffset = Integer.parseInt(offset);
                int intLimit = Integer.parseInt(limit);
                if ((intOffset >= 0) && (intLimit > 0)) {
                    if (query != null) {
                        setOfsetAndLimit(query, intOffset, intLimit);
                        return true;
                    }

                    if (dbQuery != null) {
                        setOfsetAndLimit(dbQuery, intOffset, intLimit);
                        return true;
                    }
                }
                return false;
            }
        } catch (NumberFormatException ex) {
            //TODO: Log it!
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureRequestValidatorImpl#isRequested(javax.ws.rs.core.UriInfo, java.util.Map)
     */
    @Override
    public boolean isRequested(UriInfo uri, Map<String, Object> additionalParams) {
        Map<String, Object> queryParameters = getQueryParameters(uri);
        String paramLimit = (String) queryParameters.get(QueryParameters.JPARS_PAGING_LIMIT);
        String paramOffset = (String) queryParameters.get(QueryParameters.JPARS_PAGING_OFFSET);

        if ((paramLimit != null) || (paramOffset != null)) {
            return true;
        }
        return false;
    }

    private void setOfsetAndLimit(Query query, int offset, int limit) {
        query.setFirstResult(offset);
        query.setMaxResults(limit);
    }

    private void setOfsetAndLimit(ReadQuery query, int offset, int limit) {
        query.setFirstResult(offset);
        int maxRows = limit + ((offset >= 0) ? offset : 0);
        query.setMaxRows(maxRows);
    }
}