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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 *    <p><b>Purpose</b>: Provides DBase specific behavior.
 *    <p><b>Responsibilities</b>:<ul>
 *    <li> Writing Time & Timestamp as strings since they are not supported.
 *    </ul>
 *
 * @since TOPLink/Java 1.0
 */
public class DBasePlatform extends org.eclipse.persistence.platform.database.DatabasePlatform {
    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypeMapping;

        fieldTypeMapping = new Hashtable();
        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("NUMBER", 1));

        fieldTypeMapping.put(Integer.class, new FieldTypeDefinition("NUMBER", 11));
        fieldTypeMapping.put(Long.class, new FieldTypeDefinition("NUMBER", 19));
        fieldTypeMapping.put(Float.class, new FieldTypeDefinition("NUMBER", 12, 5).setLimits(19, 0, 19));
        fieldTypeMapping.put(Double.class, new FieldTypeDefinition("NUMBER", 10, 5).setLimits(19, 0, 19));
        fieldTypeMapping.put(Short.class, new FieldTypeDefinition("NUMBER", 6));
        fieldTypeMapping.put(Byte.class, new FieldTypeDefinition("NUMBER", 4));
        fieldTypeMapping.put(java.math.BigInteger.class, new FieldTypeDefinition("NUMBER", 19));
        fieldTypeMapping.put(java.math.BigDecimal.class, new FieldTypeDefinition("NUMBER", 19).setLimits(19, 0, 9));
        fieldTypeMapping.put(Number.class, new FieldTypeDefinition("NUMBER", 19).setLimits(19, 0, 9));

        fieldTypeMapping.put(String.class, new FieldTypeDefinition("CHAR", DEFAULT_VARCHAR_SIZE));
        fieldTypeMapping.put(Character.class, new FieldTypeDefinition("CHAR", 1));

        fieldTypeMapping.put(Byte[].class, new FieldTypeDefinition("BINARY"));
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("MEMO"));
        fieldTypeMapping.put(byte[].class, new FieldTypeDefinition("BINARY"));
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("MEMO"));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldTypeDefinition("BINARY"));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldTypeDefinition("MEMO"));        

        fieldTypeMapping.put(java.sql.Date.class, new FieldTypeDefinition("DATE", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldTypeDefinition("CHAR", 15));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldTypeDefinition("CHAR", 25));

        return fieldTypeMapping;
    }
    
    /**
     * INTERNAL
     * We support more primitive than JDBC does so we must do conversion before printing or binding.
     */
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
     * returns the maximum number of characters that can be used in a field
     * name on this platform.
     */
    public int getMaxFieldNameSize() {
        return 10;
    }

    public String getSelectForUpdateString() {
        return " FOR UPDATE OF *";
    }

    public boolean isDBase() {
        return true;
    }

    /**
     *    Builds a table of minimum numeric values keyed on java class. This is used for type testing but
     * might also be useful to end users attempting to sanitize values.
     * <p><b>NOTE</b>: BigInteger & BigDecimal minimums are dependent upon their precision & Scale
     */
    public Hashtable maximumNumericValues() {
        Hashtable values = new Hashtable();

        values.put(Integer.class, Integer.valueOf(Integer.MAX_VALUE));
        values.put(Long.class, Long.valueOf("922337203685478000"));
        values.put(Double.class, Double.valueOf("99999999.999999999"));
        values.put(Short.class, Short.valueOf(Short.MIN_VALUE));
        values.put(Byte.class, Byte.valueOf(Byte.MIN_VALUE));
        values.put(Float.class, Float.valueOf("99999999.999999999"));
        values.put(java.math.BigInteger.class, new java.math.BigInteger("922337203685478000"));
        values.put(java.math.BigDecimal.class, new java.math.BigDecimal("999999.999999999"));
        return values;
    }

    /**
     *    Builds a table of minimum numeric values keyed on java class. This is used for type testing but
     * might also be useful to end users attempting to sanitize values.
     * <p><b>NOTE</b>: BigInteger & BigDecimal minimums are dependent upon their precision & Scale
     */
    public Hashtable minimumNumericValues() {
        Hashtable values = new Hashtable();

        values.put(Integer.class, Integer.valueOf(Integer.MIN_VALUE));
        values.put(Long.class, Long.valueOf("-922337203685478000"));
        values.put(Double.class, Double.valueOf("-99999999.999999999"));
        values.put(Short.class, Short.valueOf(Short.MIN_VALUE));
        values.put(Byte.class, Byte.valueOf(Byte.MIN_VALUE));
        values.put(Float.class, Float.valueOf("-99999999.999999999"));
        values.put(java.math.BigInteger.class, new java.math.BigInteger("-922337203685478000"));
        values.put(java.math.BigDecimal.class, new java.math.BigDecimal("-999999.999999999"));
        return values;
    }

    /**
     *    Append the receiver's field 'NOT NULL' constraint clause to a writer.
     */
    public void printFieldNotNullClause(Writer writer) {
        // Do nothing
    }

    /**
     * JDBC defines and outer join syntax, many drivers do not support this. So we normally avoid it.
     */
    public boolean shouldUseJDBCOuterJoinSyntax() {
        return false;
    }

    public boolean supportsForeignKeyConstraints() {
        return false;
    }

    public boolean supportsPrimaryKeyConstraint() {
        return false;
    }
}
