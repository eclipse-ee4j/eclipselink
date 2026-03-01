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
package org.eclipse.persistence.platform.database.oracle.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import jakarta.persistence.PersistenceException;

import oracle.sql.json.OracleJsonFactory;
import oracle.sql.json.OracleJsonValue;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.json.JsonPlatform;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

/**
 * Oracle 21c JSON database platform.
 */
public class Oracle21JsonPlatform extends JsonPlatform {

    // Default Oracle 21c type for JSON data.
    private static final String JSON_DEFAULT_TYPE = "JSON";

    // Oracle JSON factory.
    private final OracleJsonFactory factory;

    public Oracle21JsonPlatform() {
        super();
        this.factory = new OracleJsonFactory();
    }

    /**
     * Update the mapping of Oracle 21c database types to class types for the schema framework.
     *
     * @param classTypeMapping {@code Map} with mappings to be updated.
     */
    @Override
    public void updateClassTypes(Map<String, Class<?>> classTypeMapping) {
        classTypeMapping.put(JSON_DEFAULT_TYPE, jakarta.json.JsonValue.class);
    }

    /**
     * Update the mapping of JSON class types to Oracle 21c database types for the schema framework.
     *
     * @param fieldTypeMapping {@code Map} with mappings to be updated.
     */
    @Override
    public void updateFieldTypes(Map<Class<?>, FieldDefinition.DatabaseType> fieldTypeMapping) {
        fieldTypeMapping.put(jakarta.json.JsonObject.class, new FieldDefinition.DatabaseType(JSON_DEFAULT_TYPE));
        fieldTypeMapping.put(jakarta.json.JsonArray.class, new FieldDefinition.DatabaseType(JSON_DEFAULT_TYPE));
        fieldTypeMapping.put(jakarta.json.JsonValue.class, new FieldDefinition.DatabaseType(JSON_DEFAULT_TYPE));
    }

    /**
     * INTERNAL:
     * Convert JSON value field to JDBC statement type.
     * Postgres JSON storage type is {@code JSONB} and target Java type is {@code PGobject}.
     *
     * @param <T> classification type
     * @param jsonValue source JSON value field
     * @return converted JDBC statement type
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T convertJsonValueToDataValue(final JsonValue jsonValue) throws PersistenceException {
        if (jsonValue == null) {
            return null;
        }
        final StringWriter sw = new StringWriter(128);
        try (final JsonWriter jw = Json.createWriter(sw)) {
            jw.write(jsonValue);
        }
        try (final Reader jr = new StringReader(sw.toString())) {
            return (T) factory.createJsonTextValue(jr);
        } catch (IOException e) {
            throw new PersistenceException(ExceptionLocalization.buildMessage("json_ora21c_jsonvalue_to_oraclevalue"), e);
        }
    }

    /**
     * INTERNAL:
     * Convert JDBC {@code ResultSet} type to JSON value field.
     *
     * @param jdbcValue source classification type value from JDBC
     * @return converted JSON field value
     */
    @Override
    public JsonValue convertDataValueToJsonValue(Object jdbcValue) {
        if (jdbcValue == null) {
            return null;
        }
        // FIXME: This will be used when ojdbc adds support for getObject(columnNumber, JsonValue.class)
        if (jdbcValue instanceof JsonValue) {
            return (JsonValue) jdbcValue;
        }
        // FIXME/PERF: Remove after ojdbc adds support for getObject(columnNumber, JsonValue.class)
        if (jdbcValue instanceof OracleJsonValue) {
            // Depends on javax.json, not jakarta.json!
            //    return ((OracleJsonValue) jdbcValue).wrap(JsonValue.class);
            try (final JsonReader jr = Json.createReader(new StringReader(jdbcValue.toString()))) {
                return jr.readValue();
            }
        }
        throw new PersistenceException(ExceptionLocalization.buildMessage("json_ora21c_resultset_to_jsonvalue"));
    }

    /**
     * Retrieve JSON data from JDBC {@code ResultSet}.
     * JSON data retrieved from Postgres JDBC {@code ResultSet} are returned as {@code OracleJsonValue} instance.
     * {@code JsonTypeConverter} will convert {@code OracleJsonValue} to {@code JsonValue}.
     *
     * @param resultSet source JDBC {@code ResultSet}
     * @param columnNumber index of column in JDBC {@code ResultSet}
     * @param type target class to return, this class will be used to cast returned value
     * @param <T> target type to return
     * @return JSON data from JDBC {@code ResultSet} as {@code String} to be parsed by common {@code JsonTypeConverter}
     * @throws SQLException if data could not be retrieved
     */
    @Override
    public <T> T getJsonDataFromResultSet(final ResultSet resultSet, final int columnNumber, final Class<T> type) throws SQLException {
        // FIXME: Use JsonValue.class when ojdbc adds supoprt for it (planned in next release)
        return type.cast(resultSet.getObject(columnNumber, OracleJsonValue.class));
    }

}
