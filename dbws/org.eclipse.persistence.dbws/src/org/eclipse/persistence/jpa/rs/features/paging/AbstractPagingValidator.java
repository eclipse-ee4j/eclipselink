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

import org.eclipse.persistence.jpa.rs.QueryParameters;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.resources.common.AbstractResource;
import javax.ws.rs.core.UriInfo;
import java.util.Map;

/**
 * Base class for all pageable request validators.
 *
 * @author Dmitry Kornilov
 */
abstract class AbstractPagingValidator {

    private int offset;
    private int limit;

    final Map<String, Object> queryParameters;

    /**
     * Creates the validator.
     *
     * @param uri request URI.
     */
    AbstractPagingValidator(UriInfo uri) {
        queryParameters = AbstractResource.getQueryParameters(uri);
    }

    /**
     * Checks if request is valid.
     *
     * @return true if request is valid and supports pagination, false if request is valid but doesn't support pagination.
     * @throws org.eclipse.persistence.jpa.rs.exceptions.JPARSException in case of any validation errors.
     */
    abstract boolean isFeatureApplicable() throws JPARSException;

    /**
     * Checks pagination query parameters. Initializes 'limit' and 'offset' class properties.
     * Throws {@link org.eclipse.persistence.jpa.rs.exceptions.JPARSException} in case of errors.
     *
     * @param defaultLimit The value of limit if no 'limit' query parameter is present.
     */
    void checkParameters(int defaultLimit) {
        // Read query parameters
        final String paramLimit = (String) queryParameters.get(QueryParameters.JPARS_PAGING_LIMIT);
        final String paramOffset = (String) queryParameters.get(QueryParameters.JPARS_PAGING_OFFSET);

        // Check limit
        try {
            if (paramLimit != null) {
                final int intLimit = Integer.parseInt(paramLimit);

                if (intLimit <= 0) {
                    throw JPARSException.invalidParameter("limit", paramLimit);
                }

                if (intLimit > defaultLimit) {
                    limit = defaultLimit;
                } else {
                    limit = intLimit;
                }
            } else {
                limit = defaultLimit;
            }
        } catch (NumberFormatException ex) {
            throw JPARSException.invalidParameter("limit", paramLimit);
        }

        // Check offset
        try {
            if (paramOffset != null) {
                offset = Integer.parseInt(paramOffset);
            }

            if (offset < 0) {
                throw JPARSException.invalidParameter("offset", paramOffset);
            }
        } catch (NumberFormatException ex) {
            throw JPARSException.invalidParameter("offset", paramOffset);
        }
    }

    /**
     * Returns a value of Offset paging parameter. The value is available only after calling checkParameters method.
     *
     * @return the Offset value.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Returns a value of Limit paging parameter. The value is available only after calling checkParameters method.
     *
     * @return the Limit value.
     */
    public int getLimit() {
        return limit;
    }
}
