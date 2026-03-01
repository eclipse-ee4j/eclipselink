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
package org.eclipse.persistence.platform.database;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 *    <p><b>Purpose</b>: Provides CloudScape DBMS specific behavior.
 *
 * @since TOPLink/Java 3.0
 */
public class CloudscapePlatform extends DatabasePlatform {

    /**
     * Default constructor.
     */
    public CloudscapePlatform() {
        super();
    }

    /**
     * seems compatible with informix
     */
    @Override
    protected Map<Class<?>, FieldDefinition.DatabaseType> buildDatabaseTypes() {
        Map<Class<?>, FieldDefinition.DatabaseType> fieldTypeMapping = new HashMap<>();
        fieldTypeMapping.put(Boolean.class, new FieldDefinition.DatabaseType("SMALLINT default 0", false));

        fieldTypeMapping.put(Integer.class, new FieldDefinition.DatabaseType("INTEGER", false));
        fieldTypeMapping.put(Long.class, TYPE_NUMERIC.ofSize(19));
        fieldTypeMapping.put(Float.class, new FieldDefinition.DatabaseType("FLOAT(16)", false));
        fieldTypeMapping.put(Double.class, new FieldDefinition.DatabaseType("FLOAT(32)", false));
        fieldTypeMapping.put(Short.class, new FieldDefinition.DatabaseType("SMALLINT", false));
        fieldTypeMapping.put(Byte.class, new FieldDefinition.DatabaseType("SMALLINT", false));
        fieldTypeMapping.put(BigInteger.class, new FieldDefinition.DatabaseType("DECIMAL", 32));
        fieldTypeMapping.put(BigDecimal.class, new FieldDefinition.DatabaseType("DECIMAL", 32, 0, 32, -19, 19));
        fieldTypeMapping.put(Number.class, new FieldDefinition.DatabaseType("DECIMAL", 32, 0, 32, -19, 19));

        fieldTypeMapping.put(String.class, new FieldDefinition.DatabaseType("VARCHAR", DEFAULT_VARCHAR_SIZE));
        fieldTypeMapping.put(Character.class, new FieldDefinition.DatabaseType("CHAR", 1));
        fieldTypeMapping.put(Byte[].class, new FieldDefinition.DatabaseType("BYTE", false));
        fieldTypeMapping.put(Character[].class, new FieldDefinition.DatabaseType("TEXT", false));
        fieldTypeMapping.put(byte[].class, new FieldDefinition.DatabaseType("BYTE", false));
        fieldTypeMapping.put(char[].class, new FieldDefinition.DatabaseType("TEXT", false));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldDefinition.DatabaseType("BYTE", false));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldDefinition.DatabaseType("TEXT", false));

        fieldTypeMapping.put(java.sql.Date.class, new FieldDefinition.DatabaseType("DATE", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldDefinition.DatabaseType("DATETIME HOUR TO SECOND", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldDefinition.DatabaseType("DATETIME YEAR TO FRACTION(5)", false));

        return fieldTypeMapping;
    }

    /**
     * Answers whether platform is CloudScape
     */
    @Override
    public boolean isCloudscape() {
        return true;
    }

    /**
     * JDBC defines an outer join syntax which many drivers do not support. So we normally avoid it.
     */
    @Override
    public boolean shouldUseJDBCOuterJoinSyntax() {
        return false;// not sure if cloudscape likes this or not. Still investigating.
    }
}
