/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.platform.database.oracle;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import oracle.jdbc.OracleType;
import oracle.sql.json.OracleJsonValue;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

/**
 * <p><b>Purpose:</b>
 * Supports certain new Oracle 21c data types, and usage of certain Oracle JDBC specific APIs.
 * <p> Supports Oracle JSON data type.
 * <p> Supports Oracle OracleJsonValue derived Java types.
 */
public class Oracle21Platform extends Oracle19Platform {

    /**
     * Creates an instance of Oracle 21c database platform.
     */
    public Oracle21Platform() {
        super();
    }

    /**
     * Build the mapping of Oracle 21c database types to class types for the schema framework.
     *
     * @return database types to class types {@code Map} for the schema framework
     */
    @Override
    protected Map<String, Class<?>> buildJavaTypes() {
        final Map<String, Class<?>> classTypeMapping = super.buildJavaTypes();
        // Mapping for JSON type.
        getJsonPlatform().updateClassTypes(classTypeMapping);
        return classTypeMapping;
    }

    /**
     * Build the mapping of class types to Oracle 21c database types for the schema framework.
     *
     * @return {@code Map} mapping class types to database types for the schema framework
     */
    @Override
    protected Map<Class<?>, FieldDefinition.DatabaseType> buildDatabaseTypes() {
        Map<Class<?>, FieldDefinition.DatabaseType>fieldTypeMapping = super.buildDatabaseTypes();
        // Mapping for JSON type.
        getJsonPlatform().updateFieldTypes(fieldTypeMapping);
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
        if (parameter instanceof OracleJsonValue) {
            statement.setObject(index, parameter, OracleType.JSON);
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
        if (parameter instanceof OracleJsonValue) {
            statement.setObject(name, parameter, OracleType.JSON);
        } else {
            super.setParameterValueInDatabaseCall(parameter, statement, name, session);
        }
    }

    @Override
    public boolean supportsFractionalTime() {
        return true;
    }

}
