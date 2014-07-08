/*******************************************************************************
 * Copyright (c) 2014 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Dmitry Kornilov - initial implementation
 ******************************************************************************/

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

    private final UriInfo uri;

    final Map<String, Object> queryParameters;

    /**
     * Creates the validator.
     *
     * @param uri request URI.
     */
    AbstractPagingValidator(UriInfo uri) {
        queryParameters = AbstractResource.getQueryParameters(uri);
        this.uri = uri;
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
     * Throws {@link java.lang.IllegalArgumentException} in case of errors.
     *
     * @param defaultLimit The value of limit if no 'limit' query parameter is present.
     */
    void checkParameters(int defaultLimit) {
        // Read query parameters
        String paramLimit = (String) queryParameters.get(QueryParameters.JPARS_PAGING_LIMIT);
        String paramOffset = (String) queryParameters.get(QueryParameters.JPARS_PAGING_OFFSET);

        // Check limit
        try {
            if (paramLimit != null) {
                int intLimit = Integer.parseInt(paramLimit);
                if (intLimit > defaultLimit) {
                    limit = defaultLimit;
                } else {
                    limit = intLimit;
                }
            } else {
                limit = defaultLimit;
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(String.format("Invalid 'limit' parameter value (limit=%s).", paramLimit));
        }

        // Check offset
        try {
            if (paramOffset != null) {
                offset = Integer.parseInt(paramOffset);
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(String.format("Invalid 'offset' parameter value (offset=%s).", paramOffset));
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
