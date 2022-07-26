/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     13/01/2022-4.0.0 Tomas Kraus - 1391: JSON support in JPA
package org.eclipse.persistence.json;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.sessions.Session;

/**
 * Default JSON field value to JDBC data type converter.
 */
public class JsonTypeConverter implements Converter {

    /** JSON database platform. */
    JsonPlatform platform;

    /**
     * Creates an instance of default JSON field value to JDBC data type converter.
     */
    public JsonTypeConverter() {
        platform = null;
    }

    /**
     * Converts JSON field value to String.
     *
     * @param jsonValue source JSON field value
     * @param session current database session
     * @return target String to be stored as JDBC VARCHAR
     */
    @Override
    public Object convertObjectValueToDataValue(Object jsonValue, Session session) {
        if (jsonValue instanceof JsonValue) {
            return platform.convertJsonValueToDataValue((JsonValue)jsonValue);
        }
        throw new IllegalArgumentException("Source object is not an instance of JsonValue");
    }

    /**
     * Converts String from JDBC VARCHAR parameter to JSON field value.
     *
     * @param jdbcValue source String from JDBC VARCHAR
     * @param session current database session
     * @return target JSON field value
     */
    @Override
    public Object convertDataValueToObjectValue(Object jdbcValue, Session session) {
        return platform.convertDataValueToJsonValue(jdbcValue);
    }

    /**
     * JSON values and String are immutable.
     *
     * @return value of {@code false}
     */
    @Override
    public boolean isMutable() {
        return false;
    }

    /**
     * Initialize mapping for JDBC data type.
     *
     * @param mapping field database mapping
     * @param session current database session
     */
    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
        this.platform = session.getPlatform().getJsonPlatform().unwrap(JsonPlatform.class);
        if (mapping.isDirectToFieldMapping()) {
            if (((AbstractDirectMapping)mapping).getFieldClassification() == null) {
                final AbstractDirectMapping directMapping = AbstractDirectMapping.class.cast(mapping);
                final Class<?> attributeClassification = mapping.getAttributeClassification();
                if (attributeClassification.isInstance(JsonObject.class)) {
                    directMapping.setFieldClassification(JsonObject.class);
                } else if (attributeClassification.isInstance(JsonArray.class)) {
                    directMapping.setFieldClassification(JsonArray.class);
                } else {
                    ((AbstractDirectMapping) mapping).setFieldClassification(JsonValue.class);
                }
            }
        }
    }
}
