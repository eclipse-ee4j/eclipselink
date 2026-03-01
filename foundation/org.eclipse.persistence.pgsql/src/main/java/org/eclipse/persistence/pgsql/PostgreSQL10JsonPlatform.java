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
package org.eclipse.persistence.pgsql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import jakarta.json.JsonValue;
import jakarta.persistence.PersistenceException;

import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.json.JsonPlatform;
import org.eclipse.persistence.platform.database.PostgreSQL10Platform;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.postgresql.util.PGobject;

/**
 * PostgreSQL 10 JSON database platform.
 */
public class PostgreSQL10JsonPlatform extends JsonPlatform implements PostgreSQL10Platform.PostgreSQL10JsonExtension {

    // Default Postgres 10 type for JSON data.
    private static final String JSON_DEFAULT_TYPE = "JSONB";

    /**
     * Update the mapping of Postgres 10 database types to class types for the schema framework.
     *
     * @param classTypeMapping {@code Map} with mappings to be updated.
     */
    @Override
    public void updateClassTypes(Map<String, Class<?>> classTypeMapping) {
        classTypeMapping.put(JSON_DEFAULT_TYPE, jakarta.json.JsonValue.class);
    }

    /**
     * Update the mapping of JSON class types to Postgres 10 database types for the schema framework.
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
        try {
            final PGobject pgObject = new PGobject();
            pgObject.setType(JSON_DEFAULT_TYPE);
            pgObject.setValue(super.convertJsonValueToDataValue(jsonValue));
            return (T) pgObject;
        } catch (SQLException e) {
            throw new PersistenceException(ExceptionLocalization.buildMessage("json_pgsql_jsonvalue_to_database_type"), e);
        }
    }

    // Default resultSet.getString(columnNumber); call works too.
    /**
     * Retrieve JSON data from JDBC {@code ResultSet}.
     * JSON data retrieved from Postgres JDBC {@code ResultSet} are returned as {@code PGobject} instance.
     * It must be converted to {@code String} first to be accepted by common {@code JsonTypeConverter}.
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
        // ResultSet returns an instance of PGobject.
        final Object rawData = resultSet.getObject(columnNumber);
        if (rawData instanceof PGobject) {
            return type.cast(((PGobject) rawData).getValue());
        } else if (rawData instanceof String) {
            return type.cast(rawData);
        }
        throw new PersistenceException(ExceptionLocalization.buildMessage("json_pgsql_unknown_type"));
    }

    /**
     * Check whether provided instance is an instance of {@code PGobject}.
     *
     * @param parameter an instance to check
     * @return value of {@code true} when provided instance is an instance
     *         of {@code PGobject} or {@code false} otherwise
     */
    @Override
    public boolean isPgObjectInstance(final Object parameter) {
        return parameter instanceof PGobject;
    }

}
