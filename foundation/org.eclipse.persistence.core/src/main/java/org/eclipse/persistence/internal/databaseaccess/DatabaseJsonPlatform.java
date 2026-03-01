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
package org.eclipse.persistence.internal.databaseaccess;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

/**
 * Database platform JSON extension.
 * Provides an interface to implement JSON specific features.
 */
public interface DatabaseJsonPlatform {

    /**
     * Retrieve JSON data from JDBC {@code ResultSet}.
     *
     * @param resultSet source JDBC {@code ResultSet}
     * @param columnNumber index of column in JDBC {@code ResultSet}
     * @param type target class to return, this class will be used to cast returned value
     * @param <T> target type to return
     * @return JSON data from JDBC {@code ResultSet} as {@code String} to be parsed by {@code JsonTypeConverter}
     * @throws SQLException if data could not be retrieved
     */
    default <T> T getJsonDataFromResultSet(final ResultSet resultSet, final int columnNumber, final Class<T> type) throws SQLException {
        return type.cast(resultSet.getString(columnNumber));
    }

    /**
     * Update the mapping of database types to class types for the schema framework.
     *
     * @param fieldTypeMapping {@code Map} with mappings to be updated.
     */
    default void updateFieldTypes(final Map<Class<?>, FieldDefinition.DatabaseType> fieldTypeMapping) {}

    /**
     * Update the mapping of class types to database types for the schema framework.
     *
     * @param classTypeMapping {@code Map} with mappings to be updated.
     */
    default void updateClassTypes(final Map<String, Class<?>> classTypeMapping) {}

    /**
     * Check whether provided {@link Type} is JSON type.
     * JSON type is any class that implements {@code jakarta.json.JsonValue} interface.
     * Default implementation always returns {@code false}, because {@code jakarta.json.JsonValue} interface
     * is not supported in core module without extensions.
     *
     * @param type tyoe to be checked
     * @return value of {@code true} when provide type implements {@code jakarta.json.JsonValue} interface
     *         or {@code false} otherwose
     */
    default boolean isJsonType(final Type type) {
        return false;
    }

    /**
     * JSON parameter marker in SQL expression of {@code PreparedStatement}.
     * Default value is SQL parameter placeholder character {@code ?}.
     *
     * @return JSON parameter marker in SQL expression for current database platform.
     */
    default String customParameterMarker() {
        return "?";
    }

    /**
     * Unwrap this {@link DatabaseJsonPlatform} instance as provided class.
     *
     * @param type target class to unwrap, this class will be used to cast returned value
     * @param <T> target type to unwrap
     * @return this {@link DatabaseJsonPlatform} instance as provided class
     * @throws IllegalArgumentException when unwrap of this {@link DatabaseJsonPlatform} instance
     *                                  is not possible
     */
    default <T> T unwrap(final Class<T> type) {
        if (type.isAssignableFrom(getClass())) {
            return type.cast(this);
        }
        throw new IllegalArgumentException("Cannot unwrap to " + type.getName());
    }

}
