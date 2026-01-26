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

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 *    <p><b>Purpose</b>: Provides Microsoft Access specific behavior.
 *
 * @since TOPLink/Java 1.0
 */
public class AccessPlatform extends DatabasePlatform {

    /**
     * Default constructor.
     */
    public AccessPlatform() {
        super();
    }

    @Override
    protected Map<String, Class<?>> buildJavaTypes() {
        Map<String, Class<?>> classTypeMapping = super.buildJavaTypes();

        // In access LONG means numeric not CLOB like in Oracle
        classTypeMapping.put("LONG", Long.class);
        classTypeMapping.put("TEXT", String.class);

        return classTypeMapping;
    }

    @Override
    protected Map<Class<?>, FieldDefinition.DatabaseType> buildDatabaseTypes() {
        Map<Class<?>, FieldDefinition.DatabaseType> fieldTypeMapping = new HashMap<>();
        fieldTypeMapping.put(Boolean.class, new FieldDefinition.DatabaseType("BIT", false));

        fieldTypeMapping.put(Integer.class, new FieldDefinition.DatabaseType("LONG", false));
        fieldTypeMapping.put(Long.class, new FieldDefinition.DatabaseType("DOUBLE", false));
        fieldTypeMapping.put(Float.class, new FieldDefinition.DatabaseType("DOUBLE", false));
        fieldTypeMapping.put(Double.class, new FieldDefinition.DatabaseType("DOUBLE", false));
        fieldTypeMapping.put(Short.class, new FieldDefinition.DatabaseType("SHORT", false));
        fieldTypeMapping.put(Byte.class, new FieldDefinition.DatabaseType("BYTE", false));
        fieldTypeMapping.put(BigInteger.class, new FieldDefinition.DatabaseType("DOUBLE", false));
        fieldTypeMapping.put(BigDecimal.class, new FieldDefinition.DatabaseType("DOUBLE", false));
        fieldTypeMapping.put(Number.class, new FieldDefinition.DatabaseType("DOUBLE", false));

        fieldTypeMapping.put(String.class, new FieldDefinition.DatabaseType("TEXT", DEFAULT_VARCHAR_SIZE));
        fieldTypeMapping.put(Character.class, new FieldDefinition.DatabaseType("TEXT", 1));
        fieldTypeMapping.put(Byte[].class, new FieldDefinition.DatabaseType("LONGBINARY", false));
        fieldTypeMapping.put(Character[].class, new FieldDefinition.DatabaseType("MEMO", false));
        fieldTypeMapping.put(byte[].class, new FieldDefinition.DatabaseType("LONGBINARY", false));
        fieldTypeMapping.put(char[].class, new FieldDefinition.DatabaseType("MEMO", false));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldDefinition.DatabaseType("LONGBINARY", false));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldDefinition.DatabaseType("MEMO", false));

        fieldTypeMapping.put(java.sql.Date.class, new FieldDefinition.DatabaseType("DATETIME", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldDefinition.DatabaseType("DATETIME", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldDefinition.DatabaseType("DATETIME", false));

        return fieldTypeMapping;
    }

    /**
     * INTERNAL:
     * returns the maximum number of characters that can be used in a field
     * name on this platform.
     */
    @Override
    public int getMaxFieldNameSize() {
        return 64;
    }

    /**
     * Access do not support millisecond well, truncate the millisecond from the timestamp
     */
    @Override
    public java.sql.Timestamp getTimestampFromServer(AbstractSession session, String sessionName) {
        if (getTimestampQuery() == null) {
            java.sql.Timestamp currentTime = new java.sql.Timestamp(System.currentTimeMillis());
            currentTime.setNanos(0);
            return currentTime;
        } else {
            getTimestampQuery().setSessionName(sessionName);
            return (java.sql.Timestamp)session.executeQuery(getTimestampQuery());
        }
    }

    /**
     * Initialize any platform-specific operators
     */
    @Override
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();

        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.ToUpperCase, "UCASE"));
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.ToLowerCase, "LCASE"));
    }

    @Override
    public boolean isAccess() {
        return true;
    }

    /**
     * Builds a table of maximum numeric values keyed on java class. This is used for type testing but
     * might also be useful to end users attempting to sanitize values.
     * <p><b>NOTE</b>: BigInteger {@literal &} BigDecimal maximums are dependent upon their precision {@literal &} Scale
     */
    @Override
    public Map<Class<? extends Number>, ? super Number> maximumNumericValues() {
        Map<Class<? extends Number>, ? super Number> values = new HashMap<>();

        values.put(Integer.class, Integer.MAX_VALUE);
        values.put(Long.class, Long.MAX_VALUE);
        values.put(Double.class, Double.MAX_VALUE);
        values.put(Short.class, Short.MAX_VALUE);
        values.put(Byte.class, Byte.MAX_VALUE);
        values.put(Float.class, 123456789F);
        values.put(BigInteger.class, new BigInteger("999999999999999"));
        values.put(BigDecimal.class, new BigDecimal("99999999999999999999.9999999999999999999"));
        return values;
    }

    /**
     * Builds a table of minimum numeric values keyed on java class. This is used for type testing but
     * might also be useful to end users attempting to sanitize values.
     * <p><b>NOTE</b>: BigInteger {@literal &} BigDecimal minimums are dependent upon their precision {@literal &} Scale
     */
    @Override
    public Map<Class<? extends Number>, ? super Number> minimumNumericValues() {
        Map<Class<? extends Number>, ? super Number> values = new HashMap<>();

        values.put(Integer.class, Integer.MIN_VALUE);
        values.put(Long.class, Long.MIN_VALUE);
        values.put(Double.class, Double.MIN_VALUE);
        values.put(Short.class, Short.MIN_VALUE);
        values.put(Byte.class, Byte.MIN_VALUE);
        values.put(Float.class, (float) -123456789);
        values.put(BigInteger.class, new BigInteger("-999999999999999"));
        values.put(BigDecimal.class, new BigDecimal("-9999999999999999999.9999999999999999999"));
        return values;
    }

    /**
     * This is used as some databases create the primary key constraint differently, i.e. Access.
     */
    @Override
    public boolean requiresNamedPrimaryKeyConstraints() {
        return true;
    }

    /**
     * JDBC defines and outer join syntax, many drivers do not support this. So we normally avoid it.
     */
    @Override
    public boolean shouldUseJDBCOuterJoinSyntax() {
        return false;
    }

    /**
     * INTERNAL: Build the identity query for native sequencing.
     */
    @Override
    public ValueReadQuery buildSelectQueryForIdentity() {
        ValueReadQuery selectQuery = new ValueReadQuery();
        StringWriter writer = new StringWriter();
        writer.write("SELECT @@IDENTITY");
        selectQuery.setSQLString(writer.toString());
        return selectQuery;
    }

    /** Append the receiver's field 'identity' constraint clause to a writer. */
    @Override
    public void printFieldIdentityClause(Writer writer)    throws ValidationException {
        try {
            writer.write(" COUNTER");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * INTERNAL: Indicates whether the platform supports identity. Sybase does
     * through IDENTITY field types. This method is to be used *ONLY* by
     * sequencing classes
     */
    @Override
    public boolean supportsIdentity() {
        return true;
    }

    @Override
    public void printFieldTypeSize(Writer writer, FieldDefinition field,FieldDefinition.DatabaseType fieldType, boolean shouldPrintFieldIdentityClause) throws IOException {
        if (!shouldPrintFieldIdentityClause) {
            super.printFieldTypeSize(writer, field, fieldType,
                    shouldPrintFieldIdentityClause);
        }
    }

    @Override
    public void printFieldUnique(Writer writer,    boolean shouldPrintFieldIdentityClause) throws IOException {
        if (!shouldPrintFieldIdentityClause) {
            super.printFieldUnique(writer, shouldPrintFieldIdentityClause);
        }
    }


}
