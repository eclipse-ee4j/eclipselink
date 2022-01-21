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

import java.util.Hashtable;
import java.util.Map;

import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;

/**
 * <p>
 * <b>Purpose</b>: Provides Postgres 10 specific behavior.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li>Native JSON support added in version 10.
 * </ul>
 */
public class PostgreSQL10Platform extends PostgreSQLPlatform {

//    /**
//     * Grants access to {@code org.postgresql.util.PGobject} class without adding dependency.
//     * Inner static class also grants thread safe lazy initialization.
//     */
//    private static final class PgObjectAccessor {
//
//        /** Holds {@code PGobject} class reference. */
//        private static final Class<?> PG_OBJECT_CLASS = initPgObjectClass();
//
//        // Initialize PGobject class reference when this class is available on classpath.
//        private static Class<?> initPgObjectClass() {
//            try {
//                return Class.forName("org.postgresql.util.PGobject");
//            } catch (ClassNotFoundException e) {
//                return null;
//            }
//        }
//
//    }

    /**
     * Build the mapping of database types to class types for the schema framework.
     *
     * @return database types to class types {@code Map} for the schema framework
     */
    @Override
    protected Map<String, Class<?>> buildClassTypes() {
        Map<String, Class<?>> classTypeMapping = super.buildClassTypes();
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
        Hashtable<Class<?>, FieldTypeDefinition>fieldTypeMapping = super.buildFieldTypes();
        // Mapping for JSON type set in JsonTypeConverter#initialize. Default size set to handle large JSON values.
        fieldTypeMapping.put(jakarta.json.JsonObject.class, new FieldTypeDefinition("JSONB"));
        fieldTypeMapping.put(jakarta.json.JsonArray.class, new FieldTypeDefinition("JSONB"));
        fieldTypeMapping.put(jakarta.json.JsonValue.class, new FieldTypeDefinition("JSONB"));
        return fieldTypeMapping;
    }

//    /**
//     * INTERNAL
//     * Set the parameter in the JDBC statement at the given index.
//     * This support a wide range of different parameter types, and is heavily optimized for common types.
//     * Handles Postgres specific PGobject instances.
//     *
//     * @param parameter the parameter to set
//     * @param statement target {@code PreparedStatement} instance
//     * @param index index of the parameter in the statement
//     * @param session current database session
//     */
//    @Override
//    public void setParameterValueInDatabaseCall(
//            final Object parameter, final PreparedStatement statement,
//            final int index, final AbstractSession session
//    ) throws SQLException {
//        if (PG_OBJECT_CLASS == null) {
//            throw new PersistenceException("Class org.postgresql.util.PGobject was not found");
//        }
//        if (PG_OBJECT_CLASS.isInstance(parameter)) {
//            statement.setObject(index, parameter);
//        } else {
//            super.setParameterValueInDatabaseCall(parameter, statement, index, session);
//        }
//    }
//
//    /**
//     * INTERNAL
//     * Set the parameter in the JDBC statement at the given index.
//     * This support a wide range of different parameter types, and is heavily optimized for common types.
//     * Handles Postgres specific PGobject instances.
//     *
//     * @param parameter the parameter to set
//     * @param statement target {@code CallableStatement} instance
//     * @param name name of the parameter in the statement
//     * @param session current database session
//     */
//    @Override
//    public void setParameterValueInDatabaseCall(
//            Object parameter, CallableStatement statement, String name, AbstractSession session
//    ) throws SQLException {
//        if (PG_OBJECT_CLASS == null) {
//            throw new PersistenceException("Class org.postgresql.util.PGobject was not found");
//        }
//        if (PG_OBJECT_CLASS.isInstance(parameter)) {
//            statement.setObject(name, parameter);
//        } else {
//            super.setParameterValueInDatabaseCall(parameter, statement, name, session);
//        }
//    }
//
//    // Postgres specific JSON types support:
//    // Stores JsonValue instances as JSONB.
//    /**
//     * INTERNAL:
//     * Convert JSON value field to JDBC statement type.
//     * Postgres JSON storage type is {@code JSONB} and target Java type is {@code PGobject}.
//     *
//     * @param <T> classification type
//     * @param jsonValue source JSON value field
//     * @return converted JDBC statement type
//     */
//    @Override
//    @SuppressWarnings("unchecked")
//    public <T> T convertJsonValueToDataValue(final JsonValue jsonValue) throws PersistenceException {
//        if (PG_OBJECT_CLASS == null) {
//            throw new PersistenceException("Class org.postgresql.util.PGobject was not found");
//        }
//        final String jsonAsString = super.convertJsonValueToDataValue(jsonValue);
//        // Following code is called trough reflection to avoid org.postgresql.util.PGobject dependency
//        //        PGobject pgObject = new PGobject();
//        //        pgObject.setValue(jsonAsString); //json string
//        //        pgObject.setType("jsonb");
//        try {
//            Constructor<?> pgConstructor = PG_OBJECT_CLASS.getConstructor();
//            Object pgObject = pgConstructor.newInstance();
//            Method setValue = PG_OBJECT_CLASS.getDeclaredMethod("setValue", String.class);
//            Method setType = PG_OBJECT_CLASS.getDeclaredMethod("setType", String.class);
//            setValue.invoke(pgObject, jsonAsString);
//            setType.invoke(pgObject, "jsonb");
//            return (T) pgObject;
//        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//            throw new PersistenceException("JSON value to database type conversion failed.", e);
//        }
//    }
//
//    /**
//     * Retrieve JSON data from JDBC {@code ResultSet}.
//     * JSON data retrieved from Postgres JDBC {@code ResultSet} are returned as {@code PGobject} instance.
//     * It must be converted to {@code String} first to be accepted by common {@code JsonTypeConverter}.
//     *
//     * @param resultSet source JDBC {@code ResultSet}
//     * @param columnNumber index of column in JDBC {@code ResultSet}
//     * @return JSON data from JDBC {@code ResultSet} as {@code String} to be parsed by common {@code JsonTypeConverter}
//     * @throws SQLException if data could not be retrieved
//     */
//    @Override
//    public Object getJsonDataFromResultSet(ResultSet resultSet, int columnNumber) throws SQLException {
//        if (PG_OBJECT_CLASS == null) {
//            throw new PersistenceException("Class org.postgresql.util.PGobject was not found");
//        }
//        final Object rawData = resultSet.getObject(columnNumber);
//        if (PG_OBJECT_CLASS.isInstance(rawData)) {
//            // Following code is called trough reflection to avoid org.postgresql.util.PGobject dependency
//            //        ((PGobject)rawData).getValue();
//            try {
//                Method getValue = PG_OBJECT_CLASS.getDeclaredMethod("getValue");
//                return String.class.cast(getValue.invoke(rawData));
//            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//                throw new PersistenceException("Database PGobject conversion failed.", e);
//            }
//        } else if (rawData instanceof String) {
//            return String.class.cast(rawData);
//        }
//        throw new PersistenceException("Unknown JSON type returned from database");
//    }

}
