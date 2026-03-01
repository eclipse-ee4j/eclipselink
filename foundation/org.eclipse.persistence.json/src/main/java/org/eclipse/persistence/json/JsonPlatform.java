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
//     13/01/2022-4.0.0 Tomas Kraus
//       - 1391: JSON support in JPA
package org.eclipse.persistence.json;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Map;

import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;

import org.eclipse.persistence.internal.databaseaccess.DatabaseJsonPlatform;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

/**
 * Default JSON database platform.
 */
public class JsonPlatform implements DatabaseJsonPlatform {

    // Default size of JSON data storage as VARCHAR type.
    private static final int JSON_VARCHAR_SIZE = 2048;

    /**
     * Check whether provided {@code Type} instance is JSON type.
     *
     * @param type type to be checked
     * @return value of {@code true} when provided value is JSON type or {@code false} otherwise
     */
    public boolean isJsonType(final Type type) {
        return switch (type.getTypeName()) {
            case "jakarta.json.JsonValue", "jakarta.json.JsonArray", "jakarta.json.JsonObject" -> true;
            default -> false;
        };
    }

    /**
     * Update the mapping of JSON class types to database types for the schema framework.
     *
     * @param fieldTypeMapping {@code Map} with mappings to be updated.
     */
    @Override
    public void updateFieldTypes(final Map<Class<?>, FieldDefinition.DatabaseType> fieldTypeMapping) {
        fieldTypeMapping.put(jakarta.json.JsonObject.class, new FieldDefinition.DatabaseType("VARCHAR", JSON_VARCHAR_SIZE));
        fieldTypeMapping.put(jakarta.json.JsonArray.class, new FieldDefinition.DatabaseType("VARCHAR", JSON_VARCHAR_SIZE));
        fieldTypeMapping.put(jakarta.json.JsonValue.class, new FieldDefinition.DatabaseType("VARCHAR", JSON_VARCHAR_SIZE));
    }

    // Common JSON types support:
    // Stores JsonValue instances as VARCHAR.
    /**
     * INTERNAL:
     * Convert JSON value field to JDBC statement type.
     * Common JSON storage type is {@code VARCHAR} so target Java type is {@code String}.
     *
     * @param <T> classification type
     * @param jsonValue source JSON value field
     * @return converted JDBC statement type
     */
    @SuppressWarnings("unchecked")
    public <T> T convertJsonValueToDataValue(final JsonValue jsonValue) {
        if (jsonValue == null) {
            return null;
        }
        final StringWriter sw = new StringWriter(128);
        try (final JsonWriter jw = Json.createWriter(sw)) {
            jw.write(jsonValue);
        }
        return (T) sw.toString();
    }

    /**
     * Convert JDBC {@code ResultSet} type to JSON value field.
     * This method consumes value returned by {@link Object getJsonDataFromResultSet(ResultSet, int)}.
     * Both methods must be overwritten by platform specific code when jdbcValue is not String.
     *
     * @param jdbcValue source classification type value from JDBC
     * @return converted JSON field value
     */
    public JsonValue convertDataValueToJsonValue(Object jdbcValue) {
        if (jdbcValue == null) {
            return null;
        }
        try (final JsonReader jr = Json.createReader(new StringReader((String)jdbcValue))) {
            return jr.readValue();
        }
    }

}
