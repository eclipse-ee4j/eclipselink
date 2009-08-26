/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.RelationExpression;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.queries.ValueReadQuery;

public class TimesTenPlatform extends DatabasePlatform {

    //supportsForeignKeyConstraints is settable because TimesTen does not support circular referencing/self referencing
    private boolean supportsForeignKeyConstraints;
    
    public TimesTenPlatform() {
        supportsForeignKeyConstraints = true;
        printOuterJoinInWhereClause = true;
    }

    /**
     *    If using native SQL then print a byte[] literally as a hex string otherwise use ODBC format
     *    as provided in DatabasePlatform.
     */
    protected void appendByteArray(byte[] bytes, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            writer.write("Ox");
            Helper.writeHexString(bytes, writer);
        } else {
            super.appendByteArray(bytes, writer);
        }
    }

    /**
     * Appends an MySQL specific date if usesNativeSQL is true otherwise use the ODBC format.
     * Native FORMAT: 'YYYY-MM-DD'
     */
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
     * Appends an MySQL specific time if usesNativeSQL is true otherwise use the ODBC format.
     * Native FORMAT: 'HH:MM:SS'.
     */
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
     * Appends an MySQL specific Timestamp, if usesNativeSQL is true otherwise use the ODBC format.
     * Native Format: 'YYYY-MM-DD HH:MM:SS' 
     */
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
     * Appends an MySQL specific Timestamp, if usesNativeSQL is true otherwise use the ODBC format.
     * Native Format: 'YYYY-MM-DD HH:MM:SS'
     */
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

        fieldTypeMapping.put(String.class, new FieldTypeDefinition("VARCHAR", 255));
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
     *  Produce a DataReadQuery which updates(!) the sequence number in the db
     *  and returns it. 
     *  @param qualifiedSeqName known by TimesTen to be a defined sequence
     */
    public ValueReadQuery buildSelectQueryForSequenceObject(String qualifiedSeqName, Integer size) {
        return new ValueReadQuery("SELECT " + qualifiedSeqName + ".NEXTVAL FROM DUAL");
    }

    /**
     * INTERNAL:
     * Used for view creation.
     */
    public String getCreateViewString() {
        return "CREATE MATERIALIZED VIEW ";
    }
    
    /**
     * INTERNAL:
     * Used for pessimistic locking.
     */
    public String getSelectForUpdateString() {
        return " FOR UPDATE";
    }
    
    /**
     * PUBLIC:
     * This method returns the query to select the timestamp
     * from the server for TimesTen.
     */
    public ValueReadQuery getTimestampQuery() {
        if (timestampQuery == null) {
            timestampQuery = new ValueReadQuery();
            timestampQuery.setSQLString("SELECT SYSDATE FROM DUAL");
        }
        return timestampQuery;
    }

    /**
     * Initialize any platform-specific operators
     */
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        addOperator(ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Concat, "CONCAT"));
        addOperator(operatorOuterJoin());
        addOperator(ExpressionOperator.ifNull());
    }

    /**
     * Answers whether platform is TimesTen
     */
    public boolean isTimesTen() {
        return true;
    }

    /**
     *  Create the outer join operator for this platform
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
     *  INTERNAL:
     *  Indicates whether the platform supports sequence objects.
     *  This method is to be used *ONLY* by sequencing classes
     */
    public boolean supportsSequenceObjects() {
        return true;
    }

    public boolean supportsForeignKeyConstraints() {
        return supportsForeignKeyConstraints;
    }

    public void setSupportsForeignKeyConstraints(boolean supportsForeignKeyConstraints) {
        this.supportsForeignKeyConstraints = supportsForeignKeyConstraints;
    }

    /**
     * INTERNAL:
     * Override this method if the platform supports sequence objects.
     * Returns sql used to create sequence object in the database.
     */
    public Writer buildSequenceObjectCreationWriter(Writer writer, String fullSeqName, int increment, int start) throws IOException {
        writer.write("CREATE SEQUENCE ");
        writer.write(fullSeqName);
        if (increment != 1) {
            writer.write(" INCREMENT BY " + increment);
        }
        writer.write(" START WITH " + start);
        return writer;
    }

    /**
     * INTERNAL:
     * Override this method if the platform supports sequence objects.
     * Returns sql used to delete sequence object from the database.
     */
    public Writer buildSequenceObjectDeletionWriter(Writer writer, String fullSeqName) throws IOException {
        writer.write("DROP SEQUENCE ");
        writer.write(fullSeqName);
        return writer;
    }

    /**
     * INTERNAL:
     * TimesTen and DB2 require cast around parameter markers if both operands of certain
     * operators are parameter markers
     * This method generates CAST for parameter markers whose type is correctly
     * identified by the query compiler
     */
    public void writeParameterMarker(Writer writer, ParameterExpression parameter) throws IOException {
        String parameterMarker = "?";
        Object type = parameter.getType();
        if(type != null) {
            FieldTypeDefinition fieldType;
            fieldType = getFieldTypeDefinition((Class)type);
            if (fieldType != null){
                parameterMarker = "CAST (? AS " + fieldType.getName() + " )";
            }
        }
        writer.write(parameterMarker);
    }

    /**
     * INTERNAL
     * Allows platform to choose whether to bind literals in DatabaseCalls or not.
     */
    public boolean shouldBindLiterals() {
        return false;
    }
}
