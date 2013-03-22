/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.platform.database;

import java.io.*;
import java.util.*;

import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;

/**
 * Support the Pointbase database.
 */
public class PointBasePlatform extends org.eclipse.persistence.platform.database.DatabasePlatform {

    /**
     * Appends a Boolean value as true/false instead of 0/1
     */
    protected void appendBoolean(Boolean bool, Writer writer) throws IOException {
        writer.write(bool.toString());
    }

    /**
     * Write a Time in PointBase specific format.
     */
    protected void appendTime(java.sql.Time time, Writer writer) throws IOException {
        writer.write("TIME '" + time + "'");
    }

    /**
     * Write a Date in PointBase specific format.
     */
    protected void appendDate(java.sql.Date date, Writer writer) throws IOException {
        writer.write("DATE '" + date + "'");
    }

    /**
     * Write a TimeStamp in PointBase specific format.
     */
    protected void appendTimestamp(java.sql.Timestamp timestamp, Writer writer) throws IOException {
        writer.write("TIMESTAMP '" + timestamp + "'");
    }

    protected Map<String, Class> buildClassTypes() {
        Map<String, Class> classTypeMapping = super.buildClassTypes();

        classTypeMapping.put("FLOAT", Double.class);
        classTypeMapping.put("DOUBLE PRECISION", Double.class);
        classTypeMapping.put("CHARACTER", String.class);
        classTypeMapping.put("CLOB", Character[].class);
        classTypeMapping.put("BLOB", Byte[].class);
        classTypeMapping.put("BOOLEAN", Boolean.class);

        return classTypeMapping;
    }

    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypeMapping;

        fieldTypeMapping = super.buildFieldTypes();
        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("BOOLEAN"));

        fieldTypeMapping.put(Long.class, new FieldTypeDefinition("NUMERIC", 19));
        fieldTypeMapping.put(Short.class, new FieldTypeDefinition("NUMERIC", 5));
        fieldTypeMapping.put(Byte.class, new FieldTypeDefinition("NUMERIC", 3));
        fieldTypeMapping.put(java.math.BigInteger.class, new FieldTypeDefinition("NUMERIC", 19));

        fieldTypeMapping.put(Integer.class, new FieldTypeDefinition("INTEGER", false));
        fieldTypeMapping.put(Float.class, new FieldTypeDefinition("REAL", false));
        fieldTypeMapping.put(Double.class, new FieldTypeDefinition("DOUBLE", false));
        fieldTypeMapping.put(Short.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(java.math.BigDecimal.class, new FieldTypeDefinition("DECIMAL"));
        fieldTypeMapping.put(Number.class, new FieldTypeDefinition("DECIMAL"));

        fieldTypeMapping.put(Character.class, new FieldTypeDefinition("CHARACTER"));

        return fieldTypeMapping;
    }

    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        addOperator(ExpressionOperator.simpleMath(ExpressionOperator.Concat, "||"));
    }

    public boolean isPointBase() {
        return true;
    }
}
