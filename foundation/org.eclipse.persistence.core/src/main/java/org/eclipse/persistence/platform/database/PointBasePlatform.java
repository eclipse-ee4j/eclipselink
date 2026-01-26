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

import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * Support the Pointbase database.
 */
public class PointBasePlatform extends DatabasePlatform {

    /**
     * Default constructor.
     */
    public PointBasePlatform() {
        super();
    }

    /**
     * Appends a Boolean value as true/false instead of 0/1
     */
    @Override
    protected void appendBoolean(Boolean bool, Writer writer) throws IOException {
        writer.write(bool.toString());
    }

    /**
     * Write a Time in PointBase specific format.
     */
    @Override
    protected void appendTime(java.sql.Time time, Writer writer) throws IOException {
        writer.write("TIME '" + time + "'");
    }

    /**
     * Write a Date in PointBase specific format.
     */
    @Override
    protected void appendDate(java.sql.Date date, Writer writer) throws IOException {
        writer.write("DATE '" + date + "'");
    }

    /**
     * Write a TimeStamp in PointBase specific format.
     */
    @Override
    protected void appendTimestamp(java.sql.Timestamp timestamp, Writer writer) throws IOException {
        writer.write("TIMESTAMP '" + timestamp + "'");
    }

    @Override
    protected Map<String, Class<?>> buildJavaTypes() {
        Map<String, Class<?>> classTypeMapping = super.buildJavaTypes();

        classTypeMapping.put("FLOAT", Double.class);
        classTypeMapping.put("DOUBLE PRECISION", Double.class);
        classTypeMapping.put("CHARACTER", String.class);
        classTypeMapping.put("CLOB", Character[].class);
        classTypeMapping.put("BLOB", Byte[].class);
        classTypeMapping.put("BOOLEAN", Boolean.class);

        return classTypeMapping;
    }

    @Override
    protected Map<Class<?>, FieldDefinition.DatabaseType> buildDatabaseTypes() {
        Map<Class<?>, FieldDefinition.DatabaseType> fieldTypeMapping = super.buildDatabaseTypes();
        fieldTypeMapping.put(Boolean.class, new FieldDefinition.DatabaseType("BOOLEAN"));

        fieldTypeMapping.put(Long.class, TYPE_NUMERIC.ofSize(19));
        fieldTypeMapping.put(Byte.class, TYPE_NUMERIC.ofSize(3));
        fieldTypeMapping.put(BigInteger.class, TYPE_NUMERIC.ofSize(19));

        fieldTypeMapping.put(Integer.class, new FieldDefinition.DatabaseType("INTEGER", false));
        fieldTypeMapping.put(Float.class, new FieldDefinition.DatabaseType("REAL", false));
        fieldTypeMapping.put(Double.class, new FieldDefinition.DatabaseType("DOUBLE", false));
        fieldTypeMapping.put(Short.class, new FieldDefinition.DatabaseType("SMALLINT", false));
        fieldTypeMapping.put(BigDecimal.class, new FieldDefinition.DatabaseType("DECIMAL"));
        fieldTypeMapping.put(Number.class, new FieldDefinition.DatabaseType("DECIMAL"));

        fieldTypeMapping.put(Character.class, new FieldDefinition.DatabaseType("CHARACTER"));

        return fieldTypeMapping;
    }

    @Override
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        addOperator(ExpressionOperator.simpleMath(ExpressionOperator.Concat, "||"));
    }

    @Override
    public boolean isPointBase() {
        return true;
    }
}
