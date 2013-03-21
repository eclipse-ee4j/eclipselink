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
 *     09/14/2011-2.3.1 Guy Pelletier 
 *       - 357533: Allow DDL queries to execute even when Multitenant entities are part of the PU
 ******************************************************************************/  
package org.eclipse.persistence.platform.database;

import java.io.*;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.RelationExpression;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.queries.ValueReadQuery;

/**
 * Database platform for the TimesTen database product.
 */
public class TimesTenPlatform extends DatabasePlatform {

    // supportsForeignKeyConstraints is configurable because TimesTen does not support circular references/self references.
    private boolean supportsForeignKeyConstraints;
    
    public TimesTenPlatform() {
        this.supportsForeignKeyConstraints = true;
    }

    /**
     * If using native SQL then print a byte[] literally as a hex string otherwise use ODBC format
     * as provided in DatabasePlatform.
     */
    @Override
    protected void appendByteArray(byte[] bytes, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            writer.write("Ox");
            Helper.writeHexString(bytes, writer);
        } else {
            super.appendByteArray(bytes, writer);
        }
    }

    /**
     * Appends an TimesTen specific date if usesNativeSQL is true otherwise use the ODBC format.
     * Native FORMAT: 'YYYY-MM-DD'
     */
    @Override
    protected void appendDate(java.sql.Date date, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            writer.write("DATE '");
            writer.write(Helper.printDate(date));
            writer.write("'");
        } else {
            super.appendDate(date, writer);
        }
    }

    /**
     * Appends an TimesTen specific time if usesNativeSQL is true otherwise use the ODBC format.
     * Native FORMAT: 'HH:MM:SS'.
     */
    @Override
    protected void appendTime(java.sql.Time time, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            writer.write("TIME '");
            writer.write(Helper.printTime(time));
            writer.write("'");
        } else {
            super.appendTime(time, writer);
        }
    }

    /**
     * Appends an TimesTen specific Timestamp, if usesNativeSQL is true otherwise use the ODBC format.
     * Native Format: 'YYYY-MM-DD HH:MM:SS' 
     */
    @Override
    protected void appendTimestamp(java.sql.Timestamp timestamp, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            writer.write("TIMESTAMP '");
            writer.write(Helper.printTimestampWithoutNanos(timestamp));
            writer.write("'");
        } else {
            super.appendTimestamp(timestamp, writer);
        }
    }

    /**
     * Appends an TimesTen specific Timestamp, if usesNativeSQL is true otherwise use the ODBC format.
     * Native Format: 'YYYY-MM-DD HH:MM:SS'
     */
    @Override
    protected void appendCalendar(Calendar calendar, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            writer.write("TIMESTAMP '");
            writer.write(Helper.printCalendarWithoutNanos(calendar));
            writer.write("'");
        } else {
            super.appendCalendar(calendar, writer);
        }
    }

    /**
     * Return the mapping of class types to database types for the schema framework.
     */
    @Override
    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypeMapping;

        fieldTypeMapping = new Hashtable();
        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("TINYINT", false));

        fieldTypeMapping.put(Integer.class, new FieldTypeDefinition("INTEGER", false));
        fieldTypeMapping.put(Long.class, new FieldTypeDefinition("BIGINT", false));
        fieldTypeMapping.put(Float.class, new FieldTypeDefinition("FLOAT", false));
        fieldTypeMapping.put(Double.class, new FieldTypeDefinition("DOUBLE", false));
        fieldTypeMapping.put(Short.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(Byte.class, new FieldTypeDefinition("TINYINT", false));
        fieldTypeMapping.put(java.math.BigInteger.class, new FieldTypeDefinition("BIGINT", false));
        fieldTypeMapping.put(java.math.BigDecimal.class, new FieldTypeDefinition("DECIMAL(38)", false));
        fieldTypeMapping.put(Number.class, new FieldTypeDefinition("DECIMAL(38)", false));

        fieldTypeMapping.put(String.class, new FieldTypeDefinition("VARCHAR", DEFAULT_VARCHAR_SIZE));
        fieldTypeMapping.put(Character.class, new FieldTypeDefinition("CHAR", 1));

        fieldTypeMapping.put(Byte[].class, new FieldTypeDefinition("VARBINARY", 64000));
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("VARCHAR", 64000));
        fieldTypeMapping.put(byte[].class, new FieldTypeDefinition("VARBINARY", 64000));
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("VARCHAR", 64000));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldTypeDefinition("VARBINARY", 64000));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldTypeDefinition("VARCHAR", 64000));        
        
        fieldTypeMapping.put(java.sql.Date.class, new FieldTypeDefinition("DATE", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldTypeDefinition("TIME", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldTypeDefinition("TIMESTAMP", false));

        return fieldTypeMapping;
    }

    /**
     * INTERNAL:
     * Produce a DataReadQuery which updates(!) the sequence number in the db
     * and returns it. 
     * @param qualifiedSeqName known by TimesTen to be a defined sequence
     */
    @Override
    public ValueReadQuery buildSelectQueryForSequenceObject(String qualifiedSeqName, Integer size) {
        return new ValueReadQuery("SELECT " + qualifiedSeqName + ".NEXTVAL FROM DUAL");
    }

    /**
     * INTERNAL:
     * Used for view creation.
     */
    @Override
    public String getCreateViewString() {
        return "CREATE MATERIALIZED VIEW ";
    }
    
    /**
     * INTERNAL:
     * Used for pessimistic locking.
     */
    @Override
    public String getSelectForUpdateString() {
        return " FOR UPDATE";
    }
    
    /**
     * PUBLIC:
     * TimesTen uses the Oracle syntax for getting the current timestamp.
     */
    @Override
    public ValueReadQuery getTimestampQuery() {
        if (this.timestampQuery == null) {
            this.timestampQuery = new ValueReadQuery();
            this.timestampQuery.setSQLString("SELECT SYSDATE FROM DUAL");
            this.timestampQuery.setAllowNativeSQLQuery(true);
        }
        return this.timestampQuery;
    }

    /**
     * Initialize any platform-specific operators
     */
    @Override
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        addOperator(ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Concat, "CONCAT"));
        addOperator(operatorOuterJoin());
        addOperator(ExpressionOperator.ifNull());
    }

    @Override
    public boolean isTimesTen() {
        return true;
    }

    /**
     * TimesTen uses the Oracle where clause style outer join.
     */
    protected ExpressionOperator operatorOuterJoin() {
        ExpressionOperator result = new ExpressionOperator();
        result.setSelector(ExpressionOperator.EqualOuterJoin);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        v.addElement(" (+) = ");
        result.printsAs(v);
        result.bePostfix();
        result.setNodeClass(RelationExpression.class);
        return result;
    }


    /**
     * Some database require outer joins to be given in the where clause, others require it in the from clause.
     */
    public boolean shouldPrintOuterJoinInWhereClause() {
        if(this.printOuterJoinInWhereClause == null) {
            this.printOuterJoinInWhereClause = Boolean.TRUE;
        }
        return this.printOuterJoinInWhereClause;
    }
    
    /**
     *  INTERNAL:
     *  Indicates whether the platform supports sequence objects.
     */
    @Override
    public boolean supportsSequenceObjects() {
        return true;
    }

    @Override
    public boolean supportsForeignKeyConstraints() {
        return supportsForeignKeyConstraints;
    }

    public void setSupportsForeignKeyConstraints(boolean supportsForeignKeyConstraints) {
        this.supportsForeignKeyConstraints = supportsForeignKeyConstraints;
    }

    /**
     * INTERNAL:
     * TimesTen and requires cast around parameter markers if both operands of certain
     * operators are parameter markers
     * This method generates CAST for parameter markers whose type is correctly
     * identified by the query compiler.
     * This is not used by default, only if isCastRequired is set to true,
     * by default dynamic SQL is used to avoid the issue in only the required cases.
     */
    @Override
    public void writeParameterMarker(Writer writer, ParameterExpression parameter, AbstractRecord record, DatabaseCall call) throws IOException {
        String parameterMarker = "?";
        Object type = parameter.getType();
        if (this.isCastRequired && (type != null)) {
            FieldTypeDefinition fieldType;
            fieldType = getFieldTypeDefinition((Class)type);
            if (fieldType != null){
                parameterMarker = "CAST (? AS " + fieldType.getName() + " )";
            }
        }
        writer.write(parameterMarker);
    }
}
