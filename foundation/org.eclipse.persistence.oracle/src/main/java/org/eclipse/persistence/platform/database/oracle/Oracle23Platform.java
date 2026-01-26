/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.platform.database.oracle;

import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * <p><b>Purpose:</b>
 * Supports certain new Oracle 23c data types, and usage of certain Oracle JDBC specific APIs.
 * <p> Supports Oracle JSON data type.
 * <p> Supports Oracle OracleJsonValue derived Java types.
 */
public class Oracle23Platform extends Oracle21Platform {

    /**
     * Creates an instance of Oracle 23c database platform.
     */
    public Oracle23Platform() {
        super();
    }

    /**
     * INTERNAL:
     * Check whether current platform is Oracle 23c or later.
     * @return Always returns {@code true} for instances of Oracle 23c platform.
     * @since 4.0.2
     */
    @Override
    public boolean isOracle23() {
        return true;
    }

    @Override
    protected Map<Class<?>, FieldDefinition.DatabaseType> buildDatabaseTypes() {
        Map<Class<?>, FieldDefinition.DatabaseType> fieldTypes = super.buildDatabaseTypes();
        fieldTypes.put(java.time.LocalDateTime.class, new FieldDefinition.DatabaseType("TIMESTAMP", 9));
        fieldTypes.put(java.time.LocalTime.class, new FieldDefinition.DatabaseType("TIMESTAMP", 9));
        return fieldTypes;
    }

    /**
     * INTERNAL:
     * Allow for conversion from the Oracle type to the Java type. Used in cases when DB connection is needed like BLOB, CLOB.
     */
    @Override
    public <T> T convertObject(Object sourceObject, Class<T> javaClass, CoreSession<?, ?, ? ,?, ?> session) throws ConversionException, DatabaseException {
        //Handle special case when empty String ("") is passed from the entity into CLOB type column
        if (ClassConstants.CLOB.equals(javaClass) && sourceObject instanceof String && "".equals(sourceObject)) {
            Connection connection = ((AbstractSession) session).getAccessor().getConnection();
            Clob clob = null;
            try {
                clob = connection.createClob();
                clob.setString(1, (String)sourceObject);
            } catch (SQLException e) {
                throw ConversionException.couldNotBeConvertedToClass(sourceObject, ClassConstants.CLOB, e);
            }
            return (T) clob;
        }
        return super.convertObject(sourceObject, javaClass);
    }
}
