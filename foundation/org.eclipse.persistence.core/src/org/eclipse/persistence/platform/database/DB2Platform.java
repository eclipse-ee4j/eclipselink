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
import java.sql.*;
import java.util.*;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.queries.*;

/**
 * <p>
 * <b>Purpose</b>: Provides DB2 specific behavior.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li>Support for schema creation.
 * <li>Native SQL for byte[], Date, Time, & Timestamp.
 * <li>Support for table qualified names.
 * <li>Support for stored procedures.
 * <li>Support for temp tables.
 * <li>Support for casting.
 * <li>Support for database functions.
 * <li>Support for identity sequencing.
 * <li>Support for SEQUENCE sequencing.
 * </ul>
 * 
 * @since TOPLink/Java 1.0
 */
public class DB2Platform extends org.eclipse.persistence.platform.database.DatabasePlatform {

    public DB2Platform() {
        super();
        this.pingSQL = "VALUES(1)";
    }

    /**
     * INTERNAL:
     * Append a byte[] in native DB@ format BLOB(hexString) if usesNativeSQL(),
     * otherwise use ODBC format from DatabasePLatform.
     */
    @Override
    protected void appendByteArray(byte[] bytes, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            writer.write("BLOB(x'");
            Helper.writeHexString(bytes, writer);
            writer.write("')");
        } else {
            super.appendByteArray(bytes, writer);
        }
    }

    /**
     * INTERNAL:
     * Appends the Date in native format if usesNativeSQL() otherwise use ODBC
     * format from DatabasePlatform. Native format: 'mm/dd/yyyy'
     */
    @Override
    protected void appendDate(java.sql.Date date, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            appendDB2Date(date, writer);
        } else {
            super.appendDate(date, writer);
        }
    }

    /**
     * INTERNAL:
     * Write a timestamp in DB2 specific format (mm/dd/yyyy).
     */
    protected void appendDB2Date(java.sql.Date date, Writer writer) throws IOException {
        writer.write("'");
        // PERF: Avoid deprecated get methods, that are now very inefficient and
        // used from toString.
        Calendar calendar = Helper.allocateCalendar();
        calendar.setTime(date);

        if ((calendar.get(Calendar.MONTH) + 1) < 10) {
            writer.write('0');
        }
        writer.write(Integer.toString(calendar.get(Calendar.MONTH) + 1));
        writer.write('/');
        if (calendar.get(Calendar.DATE) < 10) {
            writer.write('0');
        }
        writer.write(Integer.toString(calendar.get(Calendar.DATE)));
        writer.write('/');
        writer.write(Integer.toString(calendar.get(Calendar.YEAR)));
        writer.write("'");

        Helper.releaseCalendar(calendar);
    }

    /**
     * INTERNAL:
     * Write a timestamp in DB2 specific format (yyyy-mm-dd-hh.mm.ss.ffffff).
     */
    protected void appendDB2Timestamp(java.sql.Timestamp timestamp, Writer writer) throws IOException {
        // PERF: Avoid deprecated get methods, that are now very inefficient and
        // used from toString.
        Calendar calendar = Helper.allocateCalendar();
        calendar.setTime(timestamp);

        writer.write(Helper.printDate(calendar));
        writer.write('-');
        if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
            writer.write('0');
        }
        writer.write(Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)));
        writer.write('.');
        if (calendar.get(Calendar.MINUTE) < 10) {
            writer.write('0');
        }
        writer.write(Integer.toString(calendar.get(Calendar.MINUTE)));
        writer.write('.');
        if (calendar.get(Calendar.SECOND) < 10) {
            writer.write('0');
        }
        writer.write(Integer.toString(calendar.get(Calendar.SECOND)));
        writer.write('.');

        Helper.releaseCalendar(calendar);

        // Must truncate the nanos to six decimal places,
        // it is actually a complex algorithm...
        String nanoString = Integer.toString(timestamp.getNanos());
        int numberOfZeros = 0;
        for (int num = Math.min(9 - nanoString.length(), 6); num > 0; num--) {
            writer.write('0');
            numberOfZeros++;
        }
        if ((nanoString.length() + numberOfZeros) > 6) {
            nanoString = nanoString.substring(0, (6 - numberOfZeros));
        }
        writer.write(nanoString);
    }

    /**
     * Write a timestamp in DB2 specific format (yyyy-mm-dd-hh.mm.ss.ffffff).
     */
    protected void appendDB2Calendar(Calendar calendar, Writer writer) throws IOException {
        int hour;
        int minute;
        int second;
        if (!Helper.getDefaultTimeZone().equals(calendar.getTimeZone())) {
            // Must convert the calendar to the local timezone if different, as
            // dates have no timezone (always local).
            Calendar localCalendar = Helper.allocateCalendar();
            localCalendar.setTimeInMillis(calendar.getTimeInMillis());
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            second = calendar.get(Calendar.SECOND);
            Helper.releaseCalendar(localCalendar);
        } else {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            second = calendar.get(Calendar.SECOND);
        }
        writer.write(Helper.printDate(calendar));
        writer.write('-');
        if (hour < 10) {
            writer.write('0');
        }
        writer.write(Integer.toString(hour));
        writer.write('.');
        if (minute < 10) {
            writer.write('0');
        }
        writer.write(Integer.toString(minute));
        writer.write('.');
        if (second < 10) {
            writer.write('0');
        }
        writer.write(Integer.toString(second));
        writer.write('.');

        // Must truncate the nanos to six decimal places,
        // it is actually a complex algorithm...
        String millisString = Integer.toString(calendar.get(Calendar.MILLISECOND));
        int numberOfZeros = 0;
        for (int num = Math.min(3 - millisString.length(), 3); num > 0; num--) {
            writer.write('0');
            numberOfZeros++;
        }
        if ((millisString.length() + numberOfZeros) > 3) {
            millisString = millisString.substring(0, (3 - numberOfZeros));
        }
        writer.write(millisString);
    }

    /**
     * INTERNAL:
     * Append the Time in Native format if usesNativeSQL() otherwise use ODBC
     * format from DAtabasePlatform. Native Format: 'hh:mm:ss'
     */
    @Override
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
     * INTERNAL:
     * Append the Timestamp in native format if usesNativeSQL() is true
     * otherwise use ODBC format from DatabasePlatform. Native format:
     * 'YYYY-MM-DD-hh.mm.ss.SSSSSS'
     */
    @Override
    protected void appendTimestamp(java.sql.Timestamp timestamp, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            writer.write("'");
            appendDB2Timestamp(timestamp, writer);
            writer.write("'");
        } else {
            super.appendTimestamp(timestamp, writer);
        }
    }

    /**
     * INTERNAL:
     * Append the Timestamp in native format if usesNativeSQL() is true
     * otherwise use ODBC format from DatabasePlatform. Native format:
     * 'YYYY-MM-DD-hh.mm.ss.SSSSSS'
     */
    @Override
    protected void appendCalendar(Calendar calendar, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            writer.write("'");
            appendDB2Calendar(calendar, writer);
            writer.write("'");
        } else {
            super.appendCalendar(calendar, writer);
        }
    }

    @Override
    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypeMapping = new Hashtable();

        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("SMALLINT DEFAULT 0", false));

        fieldTypeMapping.put(Integer.class, new FieldTypeDefinition("INTEGER", false));
        fieldTypeMapping.put(Long.class, new FieldTypeDefinition("BIGINT", false));
        fieldTypeMapping.put(Float.class, new FieldTypeDefinition("FLOAT", false));
        fieldTypeMapping.put(Double.class, new FieldTypeDefinition("FLOAT", false));
        fieldTypeMapping.put(Short.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(Byte.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(java.math.BigInteger.class, new FieldTypeDefinition("BIGINT", false));
        fieldTypeMapping.put(java.math.BigDecimal.class, new FieldTypeDefinition("DECIMAL", 15));
        fieldTypeMapping.put(Number.class, new FieldTypeDefinition("DECIMAL", 15));

        fieldTypeMapping.put(String.class, new FieldTypeDefinition("VARCHAR", DEFAULT_VARCHAR_SIZE));
        fieldTypeMapping.put(Character.class, new FieldTypeDefinition("CHAR", 1));
        fieldTypeMapping.put(Byte[].class, new FieldTypeDefinition("BLOB", 64000));
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("CLOB", 64000));
        fieldTypeMapping.put(byte[].class, new FieldTypeDefinition("BLOB", 64000));
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("CLOB", 64000));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldTypeDefinition("BLOB", 64000));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldTypeDefinition("CLOB", 64000));

        fieldTypeMapping.put(java.sql.Date.class, new FieldTypeDefinition("DATE", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldTypeDefinition("TIME", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldTypeDefinition("TIMESTAMP", false));

        return fieldTypeMapping;
    }

    /**
     * INTERNAL: returns the maximum number of characters that can be used in a
     * field name on this platform.
     */
    @Override
    public int getMaxFieldNameSize() {
        return 128;
    }

    /**
     * INTERNAL: returns the maximum number of characters that can be used in a
     * foreign key name on this platform.
     */
    @Override
    public int getMaxForeignKeyNameSize() {
        return 18;
    }
    
    /**
     * INTERNAL:
     * returns the maximum number of characters that can be used in a unique key
     * name on this platform.
     */
    @Override
    public int getMaxUniqueKeyNameSize() {
        return 18;
    }    

    /**
     * INTERNAL:
     * Return the catalog information through using the native SQL catalog
     * selects. This is required because many JDBC driver do not support
     * meta-data. Wildcards can be passed as arguments.
     * This is currently not used.
     */
    public Vector getNativeTableInfo(String table, String creator, AbstractSession session) {
        String query = "SELECT * FROM SYSIBM.SYSTABLES WHERE TBCREATOR NOT IN ('SYS', 'SYSTEM')";
        if (table != null) {
            if (table.indexOf('%') != -1) {
                query = query + " AND TBNAME LIKE " + table;
            } else {
                query = query + " AND TBNAME = " + table;
            }
        }
        if (creator != null) {
            if (creator.indexOf('%') != -1) {
                query = query + " AND TBCREATOR LIKE " + creator;
            } else {
                query = query + " AND TBCREATOR = " + creator;
            }
        }
        return session.executeSelectingCall(new org.eclipse.persistence.queries.SQLCall(query));
    }

    /**
     * INTERNAL:
     * Used for sp calls.
     */
    @Override
    public String getProcedureCallHeader() {
        return "CALL ";
    }

    /**
     * INTERNAL:
     * Used for pessimistic locking in DB2.
     * Without the "WITH RS" the lock is not held.
     */
    // public String getSelectForUpdateString() { return " FOR UPDATE"; }
    @Override
    public String getSelectForUpdateString() {
        return " FOR READ ONLY WITH RS USE AND KEEP UPDATE LOCKS";
        //return " FOR READ ONLY WITH RR";
        //return " FOR READ ONLY WITH RS";
        //return " FOR UPDATE WITH RS";
    }

    /**
     * INTERNAL:
     * Used for stored procedure defs.
     */
    @Override
    public String getProcedureEndString() {
        return "END";
    }

    /**
     * Used for stored procedure defs.
     */
    @Override
    public String getProcedureBeginString() {
        return "BEGIN";
    }

    /**
     * INTERNAL:
     * Used for stored procedure defs.
     */
    @Override
    public String getProcedureAsString() {
        return "";
    }

    /**
     * INTERNAL:
     * This is required in the construction of the stored procedures with output
     * parameters.
     */
    @Override
    public boolean shouldPrintOutputTokenAtStart() {
        return true;
    }

    /**
     * INTERNAL:
     * This method returns the query to select the timestamp from the server for
     * DB2.
     */
    @Override
    public ValueReadQuery getTimestampQuery() {
        if (timestampQuery == null) {
            timestampQuery = new ValueReadQuery();
            timestampQuery.setSQLString("SELECT DISTINCT CURRENT TIMESTAMP FROM SYSIBM.SYSTABLES");
            timestampQuery.setAllowNativeSQLQuery(true);
        }
        return timestampQuery;
    }

    /**
     * INTERNAL:
     * Initialize any platform-specific operators
     */
    @Override
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();

        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.ToUpperCase, "UCASE"));
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.ToLowerCase, "LCASE"));
        addOperator(concatOperator());
        addOperator(ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Instring, "Locate"));
        // CR#2811076 some missing DB2 functions added.
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.ToNumber, "DECIMAL"));
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.ToChar, "CHAR"));
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.DateToString, "CHAR"));
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.ToDate, "DATE"));
        addOperator(ltrim2Operator());
        addOperator(rtrim2Operator());
    }

    @Override
    public boolean isDB2() {
        return true;
    }

    /**
     * INTERNAL:
     * Builds a table of maximum numeric values keyed on java class. This is
     * used for type testing but might also be useful to end users attempting to
     * sanitize values.
     * <p>
     * <b>NOTE</b>: BigInteger & BigDecimal maximums are dependent upon their
     * precision & Scale
     */
    @Override
    public Hashtable maximumNumericValues() {
        Hashtable values = new Hashtable();

        values.put(Integer.class, Integer.valueOf(Integer.MAX_VALUE));
        values.put(Long.class, Long.valueOf(Integer.MAX_VALUE));
        values.put(Float.class, Float.valueOf(123456789));
        values.put(Double.class, Double.valueOf(Float.MAX_VALUE));
        values.put(Short.class, Short.valueOf(Short.MAX_VALUE));
        values.put(Byte.class, Byte.valueOf(Byte.MAX_VALUE));
        values.put(java.math.BigInteger.class, new java.math.BigInteger("999999999999999"));
        values.put(java.math.BigDecimal.class, new java.math.BigDecimal("0.999999999999999"));
        return values;
    }

    /**
     * INTERNAL:
     * Builds a table of minimum numeric values keyed on java class. This is
     * used for type testing but might also be useful to end users attempting to
     * sanitize values.
     * <p>
     * <b>NOTE</b>: BigInteger & BigDecimal minimums are dependent upon their
     * precision & Scale
     */
    @Override
    public Hashtable minimumNumericValues() {
        Hashtable values = new Hashtable();

        values.put(Integer.class, Integer.valueOf(Integer.MIN_VALUE));
        values.put(Long.class, Long.valueOf(Integer.MIN_VALUE));
        values.put(Float.class, Float.valueOf(-123456789));
        values.put(Double.class, Double.valueOf(Float.MIN_VALUE));
        values.put(Short.class, Short.valueOf(Short.MIN_VALUE));
        values.put(Byte.class, Byte.valueOf(Byte.MIN_VALUE));
        values.put(java.math.BigInteger.class, new java.math.BigInteger("-999999999999999"));
        values.put(java.math.BigDecimal.class, new java.math.BigDecimal("-0.999999999999999"));
        return values;
    }

    /**
     * INTERNAL:
     * Allow for the platform to ignore exceptions. This is required for DB2
     * which throws no-data modified as an exception.
     */
    @Override
    public boolean shouldIgnoreException(SQLException exception) {
        if (exception.getMessage().equals("No data found") || exception.getMessage().equals("No row was found for FETCH, UPDATE or DELETE; or the result of a query is an empty table")
                || (exception.getErrorCode() == 100)) {
            return true;
        }
        return super.shouldIgnoreException(exception);
    }

    /**
     * INTERNAL:
     * JDBC defines and outer join syntax, many drivers do not support this. So
     * we normally avoid it.
     */
    @Override
    public boolean shouldUseJDBCOuterJoinSyntax() {
        return false;
    }

    /**
     * INTERNAL:
     * The Concat operator is of the form .... VARCHAR ( <operand1> ||
     * <operand2> )
     */
    private ExpressionOperator concatOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.Concat);
        Vector v = new Vector(5);
        v.add("VARCHAR(");
        v.add(" || ");
        v.add(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * INTERNAL:
     * The 2 arg LTRIM operator is of the form .... TRIM (LEADING, <operand2> FROM <operand1> )
     */
    private ExpressionOperator ltrim2Operator() {
        ExpressionOperator operator = new ExpressionOperator();
        operator.setType(ExpressionOperator.FunctionOperator);
        operator.setSelector(ExpressionOperator.LeftTrim2);
        Vector v = new Vector(5);
        v.add("TRIM(LEADING ");
        v.add(" FROM ");
        v.add(")");
        operator.printsAs(v);
        operator.bePrefix();
        int[] argumentIndices = new int[2];
        argumentIndices[0] = 1;
        argumentIndices[1] = 0;
        operator.setArgumentIndices(argumentIndices);
        operator.setNodeClass(ClassConstants.FunctionExpression_Class);
        operator.setIsBindingSupported(false);
        return operator;
    }

    /**
     * INTERNAL:
     * The 2 arg RTRIM operator is of the form .... TRIM (TRAILING, <operand2> FROM <operand1> )
     */
    private ExpressionOperator rtrim2Operator() {
        ExpressionOperator operator = new ExpressionOperator();
        operator.setType(ExpressionOperator.FunctionOperator);
        operator.setSelector(ExpressionOperator.RightTrim2);
        Vector v = new Vector(5);
        v.add("TRIM(TRAILING ");
        v.add(" FROM ");
        v.add(")");
        operator.printsAs(v);
        operator.bePrefix();
        int[] argumentIndices = new int[2];
        argumentIndices[0] = 1;
        argumentIndices[1] = 0;
        operator.setArgumentIndices(argumentIndices);
        operator.setNodeClass(ClassConstants.FunctionExpression_Class);
        operator.setIsBindingSupported(false);
        return operator;
    }

    /**
     * INTERNAL: Build the identity query for native sequencing.
     */
    @Override
    public ValueReadQuery buildSelectQueryForIdentity() {
        ValueReadQuery selectQuery = new ValueReadQuery();
        StringWriter writer = new StringWriter();
        writer.write("SELECT IDENTITY_VAL_LOCAL() FROM SYSIBM.SYSDUMMY1");

        selectQuery.setSQLString(writer.toString());
        return selectQuery;
    }

    /**
     * INTERNAL: Append the receiver's field 'identity' constraint clause to a
     * writer.
     * Used by table creation with sequencing.
     */
    @Override
    public void printFieldIdentityClause(Writer writer) throws ValidationException {
        try {
            writer.write(" GENERATED ALWAYS AS IDENTITY");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * INTERNAL: Indicates whether the platform supports identity. DB2 does
     * through AS IDENTITY field types.
     * This is used by sequencing.
     */
    @Override
    public boolean supportsIdentity() {
        return true;
    }

    /**
     * INTERNAL: DB2 supports temp tables.
     * This is used by UpdateAllQuerys.
     */
    @Override
    public boolean supportsGlobalTempTables() {
        return true;
    }

    /**
     * INTERNAL: DB2 temp table syntax.
     * This is used by UpdateAllQuerys.
     */
    @Override
    protected String getCreateTempTableSqlPrefix() {
        return "DECLARE GLOBAL TEMPORARY TABLE ";
    }

    /**
     * INTERNAL: DB2 temp table syntax.
     * This is used by UpdateAllQuerys.
     */
    @Override
    public DatabaseTable getTempTableForTable(DatabaseTable table) {
        DatabaseTable tempTable = super.getTempTableForTable(table);
        tempTable.setTableQualifier("session");
        return tempTable;
    }

    /**
     * INTERNAL: DB2 temp table syntax.
     * This is used by UpdateAllQuerys.
     */
    @Override
    protected String getCreateTempTableSqlSuffix() {
        return " ON COMMIT DELETE ROWS NOT LOGGED";
    }

    /**
     * INTERNAL: DB2 allows LIKE to be used to create temp tables, which avoids having to know the types.
     * This is used by UpdateAllQuerys.
     */
    @Override
    protected String getCreateTempTableSqlBodyForTable(DatabaseTable table) {
        return " LIKE " + table.getQualifiedNameDelimited(this);
    }

    /**
     * INTERNAL: DB2 has issues with binding with temp table queries.
     * This is used by UpdateAllQuerys.
     */
    @Override
    public boolean dontBindUpdateAllQueryUsingTempTables() {
        return true;
    }

    /**
     * INTERNAL: DB2 does not allow NULL in select clause.
     * This is used by UpdateAllQuerys.
     */
    @Override
    public boolean isNullAllowedInSelectClause() {
        return false;
    }
    
    /**
     * INTERNAL
     * DB2 has some issues with using parameters on certain functions and relations.
     * This allows statements to disable binding only in these cases.
     * If users set casting on, then casting is used instead of dynamic SQL.
     */
    @Override
    public boolean isDynamicSQLRequiredForFunctions() {
        return !isCastRequired();
    }

    /**
     * INTERNAL:
     * DB2 requires casting on certain operations, such as the CONCAT function,
     * and parameterized queries of the form, ":param = :param". This method
     * will write CAST operation to parameters if the type is known.
     * This is not used by default, only if isCastRequired is set to true,
     * by default dynamic SQL is used to avoid the issue in only the required cases.
     */
    @Override
    public void writeParameterMarker(Writer writer, ParameterExpression parameter, AbstractRecord record, DatabaseCall call) throws IOException {
        String paramaterMarker = "?";
        Object type = parameter.getType();
        // Update-all query requires casting of null parameter values in select into.
        if ((type != null) && (this.isCastRequired || ((call.getQuery() != null) && call.getQuery().isUpdateAllQuery()))) {
            BasicTypeHelperImpl typeHelper = BasicTypeHelperImpl.getInstance();
            String castType = null;
            if (typeHelper.isBooleanType(type) || typeHelper.isByteType(type) || typeHelper.isShortType(type)) {
                castType = "SMALLINT";
            } else if (typeHelper.isIntType(type)) {
                castType = "INTEGER";
            } else if (typeHelper.isLongType(type)) {
                castType = "BIGINT";
            } else if (typeHelper.isFloatType(type)) {
                castType = "REAL";
            } else if (typeHelper.isDoubleType(type)) {
                castType = "DOUBLE";
            } else if (typeHelper.isStringType(type)) {
                castType = "VARCHAR(" + getCastSizeForVarcharParameter() + ")";
            }

            if (castType != null) {
                paramaterMarker = "CAST (? AS " + castType + " )";
            }
        }
        writer.write(paramaterMarker);
    }

    /**
     * INTERNAL:
     * DB2 does not seem to allow FOR UPDATE on queries with multiple tables.
     * This is only used by testing to exclude these tests.
     */
    @Override
    public boolean supportsLockingQueriesWithMultipleTables() {
        return false;
    }

    /**
     * INTERNAL: DB2 added SEQUENCE support as of (I believe) v8.
     */
    @Override
    public ValueReadQuery buildSelectQueryForSequenceObject(String seqName, Integer size) {
        return new ValueReadQuery("VALUES(NEXT VALUE FOR " + getQualifiedName(seqName) + ")");
    }

    /**
     * INTERNAL: DB2 added SEQUENCE support as of (I believe) v8.
     */
    @Override
    public boolean supportsSequenceObjects() {
        return true;
    }

    /**
     * INTERNAL: DB2 added SEQUENCE support as of (I believe) v8.
     */
    @Override
    public boolean isAlterSequenceObjectSupported() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Print the SQL representation of the statement on a stream, storing the fields
     * in the DatabaseCall.  This implementation works MaxRows and FirstResult into the SQL using
     * DB2's ROWNUMBER() OVER() to filter values if shouldUseRownumFiltering is true.  
     */
    @Override
    public void printSQLSelectStatement(DatabaseCall call, ExpressionSQLPrinter printer, SQLSelectStatement statement){
        int max = 0;
        int firstRow = 0;

        if (statement.getQuery()!=null){
            max = statement.getQuery().getMaxRows();
            firstRow = statement.getQuery().getFirstResult();
        }
        
        if ( !(this.shouldUseRownumFiltering()) || ( !(max>0) && !(firstRow>0) ) ){
            super.printSQLSelectStatement(call, printer, statement);
            return;
        } else if ( max > 0 ){
            statement.setUseUniqueFieldAliases(true);
            printer.printString("SELECT * FROM (SELECT * FROM (SELECT ");
            printer.printString("EL_TEMP.*, ROWNUMBER() OVER() AS EL_ROWNM FROM (");            
            call.setFields(statement.printSQL(printer));
            printer.printString(") AS EL_TEMP) AS EL_TEMP2 WHERE EL_ROWNM <= ");
            printer.printParameter(DatabaseCall.MAXROW_FIELD);
            printer.printString(") AS EL_TEMP3 WHERE EL_ROWNM > ");
            printer.printParameter(DatabaseCall.FIRSTRESULT_FIELD);
        } else {// firstRow>0
            statement.setUseUniqueFieldAliases(true);
            printer.printString("SELECT * FROM (SELECT EL_TEMP.*, ROWNUMBER() OVER() AS EL_ROWNM FROM (");            
            call.setFields(statement.printSQL(printer));
            printer.printString(") AS EL_TEMP) AS EL_TEMP2 WHERE EL_ROWNM > ");
            printer.printParameter(DatabaseCall.FIRSTRESULT_FIELD);
        }
        call.setIgnoreFirstRowSetting(true);
        call.setIgnoreMaxResultsSetting(true);
    }
    
}
