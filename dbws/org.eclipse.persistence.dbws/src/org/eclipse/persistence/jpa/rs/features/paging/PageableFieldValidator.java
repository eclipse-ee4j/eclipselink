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
import org.eclipse.persistence.jpa.rs.annotations.RestPageable;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;

import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Field;

/**
 * Finds out if an entity field is pageable and validates paging parameters.
 *
 * @author Dmitry Kornilov
 */
public final class PageableFieldValidator extends AbstractPagingValidator {
    private final Class entityClass;
    private final String fieldName;

    /**
     * Creates the validator.
     *
     * @param entityClass entity class containing a field to validate.
     * @param fieldName   field name to validate.
     * @param uri         request URI.
     */
    public PageableFieldValidator(Class entityClass, String fieldName, UriInfo uri) {
        super(uri);
        this.entityClass = entityClass;
        this.fieldName = fieldName;
    }

    /**
     * Checks if request is valid.
     *
     * @return true if request is valid and supports pagination, false if request is valid but doesn't support pagination.
     * @throws org.eclipse.persistence.jpa.rs.exceptions.JPARSException in case of any validation errors.
     */
    @Override
    public boolean isFeatureApplicable() throws JPARSException {
        final RestPageable paginationData = getPaginationData();
        if (paginationData != null) {
            // Field supports pagination, do parameters check
            checkParameters(paginationData.limit());
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

    /**
     * Returns {@link RestPageable} annotation of entityClass.fieldName field or null if it doesn't exist.
     *
     * @return RestPageable or null if it doesn't exist.
     */
    private RestPageable getPaginationData() {
        try {
            final Field fld = entityClass.getDeclaredField(fieldName);
            if (fld.isAnnotationPresent(RestPageable.class)) {
                return fld.getAnnotation(RestPageable.class);
            }
            return null;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
