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
package org.eclipse.persistence.jpa.rs.features.fieldsfiltering;

import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.resources.common.AbstractResource;

import javax.ws.rs.core.UriInfo;
import java.util.Map;

/**
 * Fields filtering feature validator/processor.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public final class FieldsFilteringValidator {
    public static final String FIELDS_PARAM_NAME = "fields";
    public static final String EXCL_FIELDS_PARAM_NAME = "excludeFields";

    private final Map<String, Object> queryParameters;
    private FieldsFilter filter;

    /**
     * Creates the validator.
     *
     * @param uri request URI.
     */
    public FieldsFilteringValidator(UriInfo uri) {
        queryParameters = AbstractResource.getQueryParameters(uri);
    }

    /**
     * Checks if request is valid.
     *
     * @return true if request is valid and feature is supported, false if request is valid but feature is not supported.
     * @throws org.eclipse.persistence.jpa.rs.exceptions.JPARSException in case of any validation errors.
     */
    public boolean isFeatureApplicable() throws JPARSException {
        if (!queryParameters.containsKey(FIELDS_PARAM_NAME) && !queryParameters.containsKey(EXCL_FIELDS_PARAM_NAME)) {
            return false;
        }

        // Throw exception if both 'fields' and 'excludeFields' present
        if (queryParameters.containsKey(FIELDS_PARAM_NAME) && queryParameters.containsKey(EXCL_FIELDS_PARAM_NAME)) {
            throw JPARSException.fieldsFilteringBothParametersPresent();
        }

        if (queryParameters.containsKey(FIELDS_PARAM_NAME)) {
            filter = new FieldsFilter(FieldsFilterType.INCLUDE, ((String) queryParameters.get(FIELDS_PARAM_NAME)));
        } else {
            filter = new FieldsFilter(FieldsFilterType.EXCLUDE, ((String) queryParameters.get(EXCL_FIELDS_PARAM_NAME)));
        }

        return true;
    }

    /**
     * {@link FieldsFilter} object containing a list of fields to filter.
     *
     * @return FieldsFilter
     */
    public FieldsFilter getFilter() {
        return filter;
    }
}
