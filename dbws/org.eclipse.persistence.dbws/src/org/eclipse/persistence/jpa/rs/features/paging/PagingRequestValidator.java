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

package org.eclipse.persistence.jpa.rs.features.paging;

import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.ws.rs.core.UriInfo;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.jpa.rs.QueryParameters;
import org.eclipse.persistence.jpa.rs.SystemDefaults;
import org.eclipse.persistence.jpa.rs.features.FeatureRequestValidator;
import org.eclipse.persistence.jpa.rs.features.FeatureRequestValidatorUtil;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

public class PagingRequestValidator extends FeatureRequestValidatorUtil implements FeatureRequestValidator {
    private String offset = null;
    private String limit = null;
    public static String DB_QUERY = "dbQuery";
    public static String QUERY = "query";

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureRequestValidator#isRequestUriValid(javax.ws.rs.core.UriInfo)
     */
    @Override
    public boolean isRequestValid(UriInfo uri, Map<String, Object> additionalParams) {
        boolean hasOrderBy = false;

        Object dbQuery = additionalParams.get(DB_QUERY);
        if (dbQuery == null) {
            return false;
        }

        Object query = additionalParams.get(QUERY);
        if (query == null) {
            return false;
        }

        if (!(query instanceof Query)) {
            return false;
        }

        if ((dbQuery instanceof ObjectLevelReadQuery) || (dbQuery instanceof ReportQuery)) {
            List<Expression> orderBy = null;
            if (dbQuery instanceof ReportQuery) {
                orderBy = ((ReportQuery) dbQuery).getOrderByExpressions();
            } else {
                orderBy = ((ObjectLevelReadQuery) dbQuery).getOrderByExpressions();
            }

            if ((orderBy != null) && (!orderBy.isEmpty())) {
                hasOrderBy = true;
            }
        } else {
            return false;
        }

        Map<String, Object> queryParameters = getQueryParameters(uri);

        if (hasOrderBy) {
            String paramLimit = (String) queryParameters.get(QueryParameters.JPARS_PAGING_LIMIT);
            String paramOffset = (String) queryParameters.get(QueryParameters.JPARS_PAGING_OFFSET);

            if ((paramLimit == null) && (paramOffset == null)) {
                return false;
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

        try {
            if ((offset != null) && (limit != null)) {
                if ((Integer.parseInt(offset) >= 0) && (Integer.parseInt(limit) > 0)) {
                    ((Query) query).setFirstResult((Integer.parseInt(offset)));
                    ((Query) query).setMaxResults((Integer.parseInt(limit)));
                    return true;
                }
            }
        } catch (NumberFormatException ex) {
            //TODO: Log it!
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.jpa.rs.features.FeatureRequestValidator#isRequested(javax.ws.rs.core.UriInfo, java.util.Map)
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
}