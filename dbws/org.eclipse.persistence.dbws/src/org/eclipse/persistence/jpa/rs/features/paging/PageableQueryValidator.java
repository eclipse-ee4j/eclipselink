/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Dmitry Kornilov - initial implementation

package org.eclipse.persistence.jpa.rs.features.paging;

import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.QueryParameters;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;

import javax.ws.rs.core.UriInfo;

/**
 * Finds out if a named query is pageable and validates paging parameters.
 *
 * @author Dmitry Kornilov
 */
public final class PageableQueryValidator extends AbstractPagingValidator {
    private final PersistenceContext context;
    private final String queryName;

    /**
     * Creates a validator.
     *
     * @param context    persistence context
     * @param queryName  query name to validate
     * @param uri        request uri
     */
    public PageableQueryValidator(PersistenceContext context, String queryName, UriInfo uri) {
        super(uri);
        this.context = context;
        this.queryName = queryName;
    }

    /**
     * Checks if request is valid.
     *
     * @return true if request is valid and supports pagination, false if request is valid but doesn't support pagination
     * @throws org.eclipse.persistence.jpa.rs.exceptions.JPARSException in case of any validation errors
     */
    @Override
    public boolean isFeatureApplicable() throws JPARSException {
        if (context.isQueryPageable(queryName)) {
            // Query supports pagination, do parameters check
            checkParameters(context.getPageableQuery(queryName).limit());
            return true;
        } else {
            // Pagination is not supported by query. Check that there are no pagination related query parameters.
            if (queryParameters.containsKey(QueryParameters.JPARS_PAGING_LIMIT)
                    || queryParameters.containsKey(QueryParameters.JPARS_PAGING_OFFSET)) {
                throw JPARSException.paginationParameterForNotPageableResource();
            }
            return false;
        }
    }
}
