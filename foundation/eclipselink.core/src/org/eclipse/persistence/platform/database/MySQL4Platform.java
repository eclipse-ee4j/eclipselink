/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.platform.database;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.queries.ValueReadQuery;

/**
 * <p><b>Purpose</b>: Provides MySQL specific behavior.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Native SQL for Date, Time, & Timestamp.
 * <li> Native sequencing.
 * <li> Mapping of class types to database types for the schema framework.
 * <li> Pessimistic locking.
 *  <li> Platform specific operators.
 * </ul>
 *
 * @since OracleAS TopLink 10<i>g</i> (10.1.3)
 */
public class MySQL4Platform extends DatabasePlatform {

    public MySQL4Platform(){
        super();
        this.pingSQL = "SELECT 1";
    }
    
    /**
     * Appends an MySQL specific date if usesNativeSQL is true otherwise use the ODBC format.
     * Native FORMAT: 'YYYY-MM-DD'
     */
    protected void appendDate(java.sql.Date date, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            writer.write("'");
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
            writer.write("'");
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
            writer.write("'");
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
            writer.write("'");
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
        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("TINYINT(1) default 0", false));

        fieldTypeMapping.put(Integer.class, new FieldTypeDefinition("INTEGER", false));
        fieldTypeMapping.put(Long.class, new FieldTypeDefinition("BIGINT", false));
        fieldTypeMapping.put(Float.class, new FieldTypeDefinition("FLOAT", false));
        fieldTypeMapping.put(Double.class, new FieldTypeDefinition("DOUBLE", false));
        fieldTypeMapping.put(Short.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(Byte.class, new FieldTypeDefinition("TINYINT", false));
        fieldTypeMapping.put(java.math.BigInteger.class, new FieldTypeDefinition("BIGINT", false));
        fieldTypeMapping.put(java.math.BigDecimal.class, new FieldTypeDefinition("DECIMAL",38));
        fieldTypeMapping.put(Number.class, new FieldTypeDefinition("DECIMAL",38));

        fieldTypeMapping.put(String.class, new FieldTypeDefinition("VARCHAR", 255));
        fieldTypeMapping.put(Character.class, new FieldTypeDefinition("CHAR", 1));

        fieldTypeMapping.put(Byte[].class, new FieldTypeDefinition("BLOB", 64000));
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("TEXT", 64000));
        fieldTypeMapping.put(byte[].class, new FieldTypeDefinition("BLOB", 64000));
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("TEXT", 64000));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldTypeDefinition("BLOB", 64000));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldTypeDefinition("TEXT", 64000));
        
        fieldTypeMapping.put(java.sql.Date.class, new FieldTypeDefinition("DATE", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldTypeDefinition("TIME", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldTypeDefinition("DATETIME", false));

        return fieldTypeMapping;
    }

    /**
     * INTERNAL:
     * Build the identity query for native sequencing.
     */
    public ValueReadQuery buildSelectQueryForIdentity() {
        ValueReadQuery selectQuery = new ValueReadQuery();
        StringWriter writer = new StringWriter();
        writer.write("SELECT LAST_INSERT_ID()");
        selectQuery.setSQLString(writer.toString());
        return selectQuery;
    }

    /**
     * INTERNAL:
     * Used for constraint deletion.
     */
    public String getConstraintDeletionString() {
        return " DROP FOREIGN KEY ";
    }
    
    /**
     * INTERNAL:
     * Used for pessimistic locking.
     */
    public String getSelectForUpdateString() {
        return " FOR UPDATE";
    }
    
    /**
     * INTERNAL:
     * This method returns the query to select the timestamp
     * from the server for MySQL.
     */
    public ValueReadQuery getTimestampQuery() {
        if (timestampQuery == null) {
            timestampQuery = new ValueReadQuery();
            timestampQuery.setSQLString("SELECT NOW()");
        }
        return timestampQuery;
    }

    /**
     * Answers whether platform is MySQL.
     */
    public boolean isMySQL() {
        return true;
    }

    /**
     * Initialize any platform-specific operators.
     */
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        addOperator(logOperator());
        addOperator(ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Atan2, "ATAN2"));
        addOperator(ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Concat, "CONCAT"));
        addOperator(toNumberOperator());
        addOperator(toCharOperator());
        addOperator(toDateOperator());
        addOperator(dateToStringOperator());
        addOperator(ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Nvl, "IFNULL"));
        addOperator(ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Trunc, "TRUNCATE"));
        addOperator(leftTrim2());
        addOperator(rightTrim2());
    }

    /**
     * INTERNAL:
     * Create the 10 based log operator for this platform.
     */
    protected ExpressionOperator logOperator() {
        ExpressionOperator result = new ExpressionOperator();
        result.setSelector(ExpressionOperator.Log);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        v.addElement("LOG(");
        v.addElement(", 10)");
        result.printsAs(v);
        result.bePrefix();
        result.setNodeClass(FunctionExpression.class);
        return result;

    }

    /**
     * INTERNAL:
     * Build MySQL equivalent to TO_NUMBER.
     */
    protected ExpressionOperator toNumberOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.ToNumber);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        v.addElement("CONVERT(");
        v.addElement(", SIGNED)");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * INTERNAL:
     * Build MySQL equivalent to TO_DATE.
     */
    protected ExpressionOperator toDateOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.ToDate);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        v.addElement("CONVERT(");
        v.addElement(", DATETIME)");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * INTERNAL:
     * Build MySQL equivalent to TO_CHAR.
     */
    protected ExpressionOperator toCharOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.ToChar);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        v.addElement("CONVERT(");
        v.addElement(", CHAR)");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * INTERNAL:
     * Build MySQL equivalent to TO_CHAR.
     */
    protected ExpressionOperator dateToStringOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.DateToString);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        v.addElement("CONVERT(");
        v.addElement(", CHAR)");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }
    
    /**
     * INTERNAL:
     * Build MySQL equivalent to LTRIM(string_exp, character).
     * MySQL: TRIM(LEADING character FROM string_exp)
     */
    protected ExpressionOperator leftTrim2() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.LeftTrim2);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(5);
        v.addElement("TRIM(LEADING ");
        v.addElement(" FROM ");
        v.addElement(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = {1, 0};
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * INTERNAL:
     * Build MySQL equivalent to RTRIM(string_exp, character).
     * MySQL: TRIM(TRAILING character FROM string_exp)
     */
    protected ExpressionOperator rightTrim2() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.RightTrim2);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(5);
        v.addElement("TRIM(TRAILING ");
        v.addElement(" FROM ");
        v.addElement(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = {1, 0};
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * INTERNAL:
     * Create the current date operator for this platform.
     */
    protected ExpressionOperator currentDateOperator() {
        return ExpressionOperator.simpleFunctionNoParentheses(ExpressionOperator.CurrentDate, "CURRENT_DATE");
    }

    /**
     * INTERNAL:
     * Append the receiver's field 'identity' constraint clause to a writer.
     */
    public void printFieldIdentityClause(Writer writer) throws ValidationException {
        try {
            writer.write(" AUTO_INCREMENT");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * INTERNAL:
     * JDBC defines an outer join syntax which many drivers do not support. So we normally avoid it.
     */
    public boolean shouldUseJDBCOuterJoinSyntax() {
        return false;
    }

    /**
     * INTERNAL:
     * Indicates whether the platform supports identity.
     * MySQL supports native sequencing through AUTO_INCREMENT field types.
     */
    public boolean supportsIdentity() {
        return true;
    }

    /**
     * INTERNAL:
     * MySQL supports temp tables for update-all, delete-all queries.
     */
    public boolean supportsGlobalTempTables() {
        return true;
    }

    /**
     * INTERNAL:
     * MySQL temp table syntax, used for update-all, delete-all queries.
     */
    protected String getCreateTempTableSqlPrefix() {
        return "CREATE TEMPORARY TABLE IF NOT EXISTS ";
    }

    /**
     * INTERNAL:
     */
     protected String getCreateTempTableSqlBodyForTable(DatabaseTable table) {
         return " LIKE " + table.getQualifiedName();
     }
     
    /**
     * INTERNAL:
     * MySQL supports temp tables for update-all, delete-all queries.
     */
    public boolean shouldAlwaysUseTempStorageForModifyAll() {
        return true;
    }
    
    /**
     * INTERNAL:
     * MySQL stored procedure calls do not require the argument name be printed in the call string
     * e.g. call MyStoredProc(?) instead of call MyStoredProc(myvariable = ?)
     * @return
     */
    public boolean shouldPrintStoredProcedureArgumentNameInCall(){
	    return false;
    }
   
    /**
     * INTERNAL:
     * MySQL uses ' to allow identifier to have spaces.
     */
    public String getIdentifierQuoteCharacter() {
        return "`";
    }
    
    /**
     * INTERNAL:
     * MySQL uses the INOUT keyword for this.
     */
    public String getInOutputProcedureToken() {
        return "INOUT";
    }
    
    /**
     * MySQL does not use the AS token.
     */
    public String getProcedureAsString() {
        return "";
    }
    
    /**
     * INTERNAL:
     * MySQL requires the direction at the start of the argument.
     */
    public boolean shouldPrintOutputTokenAtStart() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Used for stored procedure calls.
     */
    public String getProcedureCallHeader() {
        return "CALL ";
    }
    
    /**
     * INTERNAL:
     * MySQL requires BEGIN.
     */
    public String getProcedureBeginString() {
        return "BEGIN ";
    }

    /**
     * INTERNAL:
     * MySQL requires END.
     */
    public String getProcedureEndString() {
        return "END";
    }
    
    /**
     * INTERNAL:
     * Writes MySQL specific SQL for accessing temp tables for update-all queries.
     */
    public void writeUpdateOriginalFromTempTableSql(Writer writer, DatabaseTable table,
                                                    Collection pkFields,
                                                    Collection assignedFields) throws IOException 
    {
        writer.write("UPDATE ");
        String tableName = table.getQualifiedName();
        writer.write(tableName);
        writer.write(", ");
        String tempTableName = getTempTableForTable(table).getQualifiedName();
        writer.write(tempTableName);
        writeAutoAssignmentSetClause(writer, tableName, tempTableName, assignedFields);
        writeAutoJoinWhereClause(writer, tableName, tempTableName, pkFields);
    }

    /**
     * INTERNAL:
     * Writes MySQL specific SQL for accessing temp tables for delete-all queries.
     */
    public void writeDeleteFromTargetTableUsingTempTableSql(Writer writer, DatabaseTable table, DatabaseTable targetTable,
                                                        Collection pkFields, 
                                                        Collection targetPkFields) throws IOException 
    {
        writer.write("DELETE FROM ");
        String targetTableName = targetTable.getQualifiedName();
        writer.write(targetTableName);
        writer.write(" USING ");
        writer.write(targetTableName);
        writer.write(", ");
        String tempTableName = getTempTableForTable(table).getQualifiedName();
        writer.write(tempTableName);
        writeJoinWhereClause(writer, targetTableName, tempTableName, targetPkFields, pkFields);
    }
}