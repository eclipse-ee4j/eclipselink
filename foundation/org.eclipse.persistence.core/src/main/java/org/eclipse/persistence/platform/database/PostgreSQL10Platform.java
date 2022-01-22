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
package org.eclipse.persistence.platform.database;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

import jakarta.json.JsonValue;
import jakarta.persistence.PersistenceException;

import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * Postgres 10 database platform extension.
 * <p>
 * <b>Purpose</b>: Provides Postgres 10 specific behavior.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li>Native JSON support added in version 10.
 * </ul>
 * This class requires Postgres JDBC driver on the classpath.
 */
public class PostgreSQL10Platform extends PostgreSQLPlatform {

    // Access to org.postgresql.util.PGobject trough reflection to hide dependency on Postgres JDBC driver.
    // Singleton instance in inner class also serves as thread safe lazy initialization.
    private static final class PgObjectAccessor {

        // PGobject fully qualified name.
        private static final String PG_OBJECT_CLASS_NAME = "org.postgresql.util.PGobject";
        // PGobject accessor singleton instance.
        private static final PgObjectAccessor PG_OBJECT_ACCESSOR = new PgObjectAccessor();
        // Default Postgres 10 type for JSON data.
        private static final String JSON_DEFAULT_TYPE = "jsonb";

        private final String className;
        private Class<?> classReference;
        private Constructor<?> constructor;
        private Method setValue;
        private Method setType;
        private Method getValue;

        private PgObjectAccessor() {
            // Some tests will fail when static PG_OBJECT_CLASS_NAME is used directly as Class.forName argument.
            this.className = PG_OBJECT_CLASS_NAME;
            try {
                this.classReference = Class.forName(className);
                this.constructor = classReference.getConstructor();
                this.setValue = classReference.getDeclaredMethod("setValue", String.class);
                this.setType = classReference.getDeclaredMethod("setType", String.class);
                this.getValue = classReference.getDeclaredMethod("getValue");
            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                this.classReference = null;
                this.constructor = null;
                this.setValue = null;
                this.setType = null;
                this.getValue = null;
            }
        }

        private boolean isInstance(final Object o) {
            if (classReference == null) {
                throw new PersistenceException("Missing " + this.className + " on classpath");
            }
            return classReference.isInstance(o);
        }

        private Object newPgObject(final String value)
                throws InvocationTargetException, IllegalAccessException, InstantiationException {
            if (classReference == null) {
                throw new PersistenceException("Missing " + this.className + " on classpath");
            }
            final Object pgObject = constructor.newInstance();
            setType.invoke(pgObject, JSON_DEFAULT_TYPE);
            setValue.invoke(pgObject, value);
            return pgObject;
        }

        private String getValue(final Object pgObject) throws InvocationTargetException, IllegalAccessException {
            return (String) getValue.invoke(pgObject);
        }

    }

    /**
     * Crerates an instance of Postgres 10 platform.
     */
    public PostgreSQL10Platform() {
        super();
    }

    /**
     * Build the mapping of database types to class types for the schema framework.
     *
     * @return database types to class types {@code Map} for the schema framework
     */
    @Override
    protected Map<String, Class<?>> buildClassTypes() {
        final Map<String, Class<?>> classTypeMapping = super.buildClassTypes();
        classTypeMapping.put("JSONB", jakarta.json.JsonValue.class);
        return classTypeMapping;
    }

    /**
     * Build the mapping of class types to database types for the schema framework.
     *
     * @return {@code Hashtable} mapping class types to database types for the schema framework
     */
    @Override
    protected Hashtable<Class<?>, FieldTypeDefinition> buildFieldTypes() {
        final Hashtable<Class<?>, FieldTypeDefinition>fieldTypeMapping = super.buildFieldTypes();
        // Mapping for JSON type is set in JsonTypeConverter#initialize.
        fieldTypeMapping.put(jakarta.json.JsonObject.class, new FieldTypeDefinition("JSONB"));
        fieldTypeMapping.put(jakarta.json.JsonArray.class, new FieldTypeDefinition("JSONB"));
        fieldTypeMapping.put(jakarta.json.JsonValue.class, new FieldTypeDefinition("JSONB"));
        return fieldTypeMapping;
    }

    /**
     * INTERNAL
     * Set the parameter in the JDBC statement at the given index.
     * This support a wide range of different parameter types, and is heavily optimized for common types.
     * Handles Postgres specific PGobject instances.
     *
     * @param parameter the parameter to set
     * @param statement target {@code PreparedStatement} instance
     * @param index index of the parameter in the statement
     * @param session current database session
     */
    @Override
    public void setParameterValueInDatabaseCall(
            final Object parameter, final PreparedStatement statement,
            final int index, final AbstractSession session
    ) throws SQLException {
        // Instance check is called through reflection to avoid PGobject dependency
        if (PgObjectAccessor.PG_OBJECT_ACCESSOR.isInstance(parameter)) {
            statement.setObject(index, parameter);
        } else {
            super.setParameterValueInDatabaseCall(parameter, statement, index, session);
        }
    }

    /**
     * INTERNAL
     * Set the parameter in the JDBC statement at the given index.
     * This support a wide range of different parameter types, and is heavily optimized for common types.
     * Handles Postgres specific PGobject instances.
     *
     * @param parameter the parameter to set
     * @param statement target {@code CallableStatement} instance
     * @param name name of the parameter in the statement
     * @param session current database session
     */
    @Override
    public void setParameterValueInDatabaseCall(
            final Object parameter, final CallableStatement statement,
            final String name, final AbstractSession session
    ) throws SQLException {
        // Instance check is called through reflection to avoid PGobject dependency
        if (PgObjectAccessor.PG_OBJECT_ACCESSOR.isInstance(parameter)) {
            statement.setObject(name, parameter);
        } else {
            super.setParameterValueInDatabaseCall(parameter, statement, name, session);
        }
    }

    // Postgres specific JSON types support:
    // Stores JsonValue instances as JSONB.
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
        final String jsonAsString = super.convertJsonValueToDataValue(jsonValue);
        // Following code is called through reflection to avoid PGobject dependency
        try {
            return (T) PgObjectAccessor.PG_OBJECT_ACCESSOR.newPgObject(jsonAsString);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new PersistenceException("JSON value to database type conversion failed.", e);
        }
    }

    // Default resultSet.getObject(columnNumber); call works too.
    /**
     * Retrieve JSON data from JDBC {@code ResultSet}.
     * JSON data retrieved from Postgres JDBC {@code ResultSet} are returned as {@code PGobject} instance.
     * It must be converted to {@code String} first to be accepted by common {@code JsonTypeConverter}.
     *
     * @param resultSet source JDBC {@code ResultSet}
     * @param columnNumber index of column in JDBC {@code ResultSet}
     * @return JSON data from JDBC {@code ResultSet} as {@code String} to be parsed by common {@code JsonTypeConverter}
     * @throws SQLException if data could not be retrieved
     */
    @Override
    public Object getJsonDataFromResultSet(final ResultSet resultSet, final int columnNumber) throws SQLException {
        // ResultSet returns an instance of PGobject.
        final Object rawData = resultSet.getObject(columnNumber);
        // Following code is called through reflection to avoid PGobject dependency.
        if (PgObjectAccessor.PG_OBJECT_ACCESSOR.isInstance(rawData)) {
            try {
                return PgObjectAccessor.PG_OBJECT_ACCESSOR.getValue(rawData);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new PersistenceException("Database PGobject conversion failed.", e);
            }
        // Fallback option when String value is returned.
        } else if (rawData instanceof String) {
            return rawData;
        }
        throw new PersistenceException("Unknown JSON type returned from database");
    }

}
