/*
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *    <p><b>Purpose</b>: Provides DBase specific behavior.
 *    <p><b>Responsibilities</b>:<ul>
 *    <li> Writing Time {@literal &} Timestamp as strings since they are not supported.
 *    </ul>
 *
 * @since TOPLink/Java 1.0
 */
public class DBasePlatform extends DatabasePlatform {

    /**
     * Default constructor.
     */
    public DBasePlatform() {
        super();
    }

    @Override
    protected Map<Class<?>, FieldDefinition.DatabaseType> buildDatabaseTypes() {
        Map<Class<?>, FieldDefinition.DatabaseType> fieldTypeMapping = new HashMap<>();
        fieldTypeMapping.put(Boolean.class, TYPE_BOOLEAN);

        fieldTypeMapping.put(Integer.class, new FieldDefinition.DatabaseType("NUMBER", 11));
        fieldTypeMapping.put(Long.class, TYPE_LONG);
        fieldTypeMapping.put(Float.class, new FieldDefinition.DatabaseType("NUMBER", 12, 5, 19, 0, 19));
        fieldTypeMapping.put(Double.class, new FieldDefinition.DatabaseType("NUMBER", 10, 5, 19, 0, 19));
        fieldTypeMapping.put(Short.class, new FieldDefinition.DatabaseType("NUMBER", 6));
        fieldTypeMapping.put(Byte.class, new FieldDefinition.DatabaseType("NUMBER", 4));
        fieldTypeMapping.put(BigInteger.class, TYPE_LONG);
        fieldTypeMapping.put(BigDecimal.class, new FieldDefinition.DatabaseType("NUMBER", 19, 0, 19, 0, 9));
        fieldTypeMapping.put(Number.class, new FieldDefinition.DatabaseType("NUMBER", 19, 0, 19, 0, 9));

        fieldTypeMapping.put(String.class, new FieldDefinition.DatabaseType("CHAR", DEFAULT_VARCHAR_SIZE));
        fieldTypeMapping.put(Character.class, new FieldDefinition.DatabaseType("CHAR", 1));

        fieldTypeMapping.put(Byte[].class, new FieldDefinition.DatabaseType("BINARY"));
        fieldTypeMapping.put(Character[].class, new FieldDefinition.DatabaseType("MEMO"));
        fieldTypeMapping.put(byte[].class, new FieldDefinition.DatabaseType("BINARY"));
        fieldTypeMapping.put(char[].class, new FieldDefinition.DatabaseType("MEMO"));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldDefinition.DatabaseType("BINARY"));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldDefinition.DatabaseType("MEMO"));

        fieldTypeMapping.put(java.sql.Date.class, new FieldDefinition.DatabaseType("DATE", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldDefinition.DatabaseType("CHAR", 15));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldDefinition.DatabaseType("CHAR", 25));

        return fieldTypeMapping;
    }

    /**
     * INTERNAL
     * We support more primitive than JDBC does so we must do conversion before printing or binding.
     */
    @Override
    public Object convertToDatabaseType(Object value) {
        Object databaseValue = super.convertToDatabaseType(value);
        if ((databaseValue instanceof java.sql.Time) || (databaseValue instanceof java.sql.Timestamp)) {
            databaseValue = databaseValue.toString();
        }
        return databaseValue;
    }

    /**
     * INTERNAL:
     * DBase does not support Time/Timestamp so we must map to strings.
     */
    @Override
    public void setParameterValueInDatabaseCall(Object parameter,
            PreparedStatement statement, int index, AbstractSession session) throws SQLException {
        Object databaseValue = super.convertToDatabaseType(parameter);
        if ((databaseValue instanceof java.sql.Time) || (databaseValue instanceof java.sql.Timestamp)) {
            databaseValue = databaseValue.toString();
        }
        super.setParameterValueInDatabaseCall(databaseValue, statement, index, session);
    }

    /**
     * INTERNAL:
     * DBase does not support Time/Timestamp so we must map to strings.
     */
    @Override
    public void setParameterValueInDatabaseCall(Object parameter,
            CallableStatement statement, String name, AbstractSession session) throws SQLException {
        Object databaseValue = super.convertToDatabaseType(parameter);
        if ((databaseValue instanceof java.sql.Time) || (databaseValue instanceof java.sql.Timestamp)) {
            databaseValue = databaseValue.toString();
        }
        super.setParameterValueInDatabaseCall(databaseValue, statement, name, session);
    }

    /**
     * INTERNAL:
     * returns the maximum number of characters that can be used in a field
     * name on this platform.
     */
    @Override
    public int getMaxFieldNameSize() {
        return 10;
    }

    @Override
    public String getSelectForUpdateString() {
        return " FOR UPDATE OF *";
    }

    @Override
    public boolean isDBase() {
        return true;
    }

    /**
     * Builds a table of minimum numeric values keyed on java class. This is used for type testing but
     * might also be useful to end users attempting to sanitize values.
     * <p><b>NOTE</b>: BigInteger {@literal &} BigDecimal minimums are dependent upon their precision {@literal &} Scale
     */
    @Override
    public Map<Class<? extends Number>, ? super Number> maximumNumericValues() {
        Map<Class<? extends Number>, ? super Number> values = new HashMap<>();

        values.put(Integer.class, Integer.MAX_VALUE);
        values.put(Long.class, Long.valueOf("922337203685478000"));
        values.put(Double.class, Double.valueOf("99999999.999999999"));
        values.put(Short.class, Short.MIN_VALUE);
        values.put(Byte.class, Byte.MIN_VALUE);
        values.put(Float.class, Float.valueOf("99999999.999999999"));
        values.put(BigInteger.class, new BigInteger("922337203685478000"));
        values.put(BigDecimal.class, new BigDecimal("999999.999999999"));
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
        values.put(Long.class, Long.valueOf("-922337203685478000"));
        values.put(Double.class, Double.valueOf("-99999999.999999999"));
        values.put(Short.class, Short.MIN_VALUE);
        values.put(Byte.class, Byte.MIN_VALUE);
        values.put(Float.class, Float.valueOf("-99999999.999999999"));
        values.put(BigInteger.class, new BigInteger("-922337203685478000"));
        values.put(BigDecimal.class, new BigDecimal("-999999.999999999"));
        return values;
    }

    /**
     *    Append the receiver's field 'NOT NULL' constraint clause to a writer.
     */
    @Override
    public void printFieldNotNullClause(Writer writer) {
        // Do nothing
    }

    /**
     * JDBC defines and outer join syntax, many drivers do not support this. So we normally avoid it.
     */
    @Override
    public boolean shouldUseJDBCOuterJoinSyntax() {
        return false;
    }

    @Override
    public boolean supportsForeignKeyConstraints() {
        return false;
    }

    @Override
    public boolean supportsPrimaryKeyConstraint() {
        return false;
    }
}
