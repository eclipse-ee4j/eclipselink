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

import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.resources.common.AbstractResource;
import org.eclipse.persistence.mappings.DatabaseMapping;

import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Fields filtering feature validator/processor.
 *
 * @author Dmitry Kornilov
 */
public final class FieldsFilteringValidator {
    public static final String FIELDS_PARAM_NAME = "fields";
    public static final String EXCL_FIELDS_PARAM_NAME = "excludeFields";

    private final Object entity;
    private final PersistenceContext context;
    private final Map<String, Object> queryParameters;
    private List<String> fields;

    /**
     * Creates the validator.
     *
     * @param uri request URI.
     */
    public FieldsFilteringValidator(PersistenceContext context, UriInfo uri, Object entity) {
        queryParameters = AbstractResource.getQueryParameters(uri);
        this.entity = entity;
        this.context = context;
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
            // fields
            fields = Arrays.asList(((String) queryParameters.get(FIELDS_PARAM_NAME)).split("\\s*,\\s*"));
        } else {
            // excludeFields
            List<String> excludeFields = Arrays.asList(((String) queryParameters.get(EXCL_FIELDS_PARAM_NAME)).split("\\s*,\\s*"));

            // Get attribute mappings from class descriptor. We just need an attributes list actually
            List<DatabaseMapping> mappings = context.getServerSession().getProject().getDescriptors().get(entity.getClass()).getMappings();

            // Build fields list as all fields without the excluded ones
            fields = new ArrayList<String>();
            for (DatabaseMapping mapping : mappings) {
                if (!excludeFields.contains(mapping.getAttributeName())) {
                    fields.add(mapping.getAttributeName());
                }
            }
        }

        return true;
    }

    /**
     * Gets a list of fields need to be returned in response. Has to be called after isFeatureApplicable() method.
     *
     * @return A list of entity attributes which have to be included in response.
     */
    public List<String> getFields() {
        return fields;
    }
}
