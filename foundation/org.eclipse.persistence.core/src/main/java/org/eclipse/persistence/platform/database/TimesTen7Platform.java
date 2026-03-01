/*
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//
// @author Kyle Chen
//
package org.eclipse.persistence.platform.database;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 *    <p><b>Purpose</b>: Provides TimesTen 7 specific behavior.
 *
 */

public class TimesTen7Platform extends TimesTenPlatform {

    /**
     * Default constructor.
     */
    public TimesTen7Platform() {
        super();
    }

    /**
     * Return the mapping of class types to database types for the schema framework.
     */
    @Override
    protected Map<Class<?>, FieldDefinition.DatabaseType> buildDatabaseTypes() {
        Map<Class<?>, FieldDefinition.DatabaseType> fieldTypeMapping = new HashMap<>();
        fieldTypeMapping.put(Boolean.class, new FieldDefinition.DatabaseType("TT_TINYINT", false));

        fieldTypeMapping.put(Integer.class, new FieldDefinition.DatabaseType("TT_INTEGER", false));
        fieldTypeMapping.put(Long.class, new FieldDefinition.DatabaseType("TT_BIGINT", false));
        fieldTypeMapping.put(Float.class, new FieldDefinition.DatabaseType("FLOAT", false));
        fieldTypeMapping.put(Double.class, new FieldDefinition.DatabaseType("DOUBLE", false));
        fieldTypeMapping.put(Short.class, new FieldDefinition.DatabaseType("TT_SMALLINT", false));
        fieldTypeMapping.put(Byte.class, new FieldDefinition.DatabaseType("TT_TINYINT", false));
        fieldTypeMapping.put(BigInteger.class, new FieldDefinition.DatabaseType("TT_BIGINT", false));
        fieldTypeMapping.put(BigDecimal.class, new FieldDefinition.DatabaseType("DECIMAL(38)", false));
        fieldTypeMapping.put(Number.class, new FieldDefinition.DatabaseType("DECIMAL(38)", false));

        fieldTypeMapping.put(String.class, new FieldDefinition.DatabaseType("VARCHAR", 255));
        fieldTypeMapping.put(Character.class, new FieldDefinition.DatabaseType("CHAR", 1));

        fieldTypeMapping.put(Byte[].class, new FieldDefinition.DatabaseType("TT_VARBINARY", 64000));
        fieldTypeMapping.put(Character[].class, new FieldDefinition.DatabaseType("VARCHAR", 64000));
        fieldTypeMapping.put(byte[].class, new FieldDefinition.DatabaseType("TT_VARBINARY", 64000));
        fieldTypeMapping.put(char[].class, new FieldDefinition.DatabaseType("VARCHAR", 64000));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldDefinition.DatabaseType("TT_VARBINARY", 64000));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldDefinition.DatabaseType("VARCHAR", 64000));

        fieldTypeMapping.put(java.sql.Date.class, new FieldDefinition.DatabaseType("DATE", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldDefinition.DatabaseType("TIME", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldDefinition.DatabaseType("TIMESTAMP", false));
        return fieldTypeMapping;
    }

    @Override
    public boolean isTimesTen7() {
        return true;
    }
}
