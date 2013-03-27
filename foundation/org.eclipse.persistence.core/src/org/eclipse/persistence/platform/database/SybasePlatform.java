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

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.SessionProfiler;

/**
 *    <p><b>Purpose</b>: Provides Sybase ASE specific behavior.
 *    <p><b>Responsibilities</b>:<ul>
 *    <li> Native SQL for byte[], Date, Time, & Timestamp.
 *    <li> Native sequencing using @@IDENTITY.
 *    </ul>
 *
 * @since TOPLink/Java 1.0
 */
public class SybasePlatform extends org.eclipse.persistence.platform.database.DatabasePlatform {
    // An array could be used here with the type being the index, but upon looking at the source some types are
    // assigned negative values, making them unusable as indexes without guessing at modifying them.
    // this attribute is used for registering output params in stored procedure calls.  JConnect 5.5 requires
    // that the API that accepts a string is called so we have a collection of strings to use.
    protected Map typeStrings;

    public SybasePlatform(){
        super();
        this.pingSQL = "SELECT 1";
    }
    
    protected Map getTypeStrings() {
        if (typeStrings == null) {
            initializeTypeStrings();
        }
        return typeStrings;
    }

    protected synchronized void initializeTypeStrings() {
        if (typeStrings == null) {
            Map types = new HashMap(30);
            types.put(new Integer(Types.ARRAY), "ARRAY");
            types.put(new Integer(Types.BIGINT), "BIGINT");
            types.put(new Integer(Types.BINARY), "BINARY");
            types.put(new Integer(Types.BIT), "BIT");
            types.put(new Integer(Types.BLOB), "BLOB");
            types.put(new Integer(Types.CHAR), "CHAR");
            types.put(new Integer(Types.CLOB), "CLOB");
            types.put(new Integer(Types.DATE), "DATE");
            types.put(new Integer(Types.DECIMAL), "DECIMAL");
            types.put(new Integer(Types.DOUBLE), "DOUBLE");
            types.put(new Integer(Types.FLOAT), "FLOAT");
            types.put(new Integer(Types.INTEGER), "INTEGER");
            types.put(new Integer(Types.JAVA_OBJECT), "JAVA_OBJECT");
            types.put(new Integer(Types.LONGVARBINARY), "LONGVARBINARY");
            types.put(new Integer(Types.LONGVARCHAR), "LONGVARCHAR");
            types.put(new Integer(Types.NULL), "NULL");
            types.put(new Integer(Types.NUMERIC), "NUMERIC");
            types.put(new Integer(Types.OTHER), "OTHER");
            types.put(new Integer(Types.REAL), "REAL");
            types.put(new Integer(Types.REF), "REF");
            types.put(new Integer(Types.SMALLINT), "SMALLINT");
            types.put(new Integer(Types.STRUCT), "STRUCT");
            types.put(new Integer(Types.TIME), "TIME");
            types.put(new Integer(Types.TIMESTAMP), "TIMESTAMP");
            types.put(new Integer(Types.TINYINT), "TINYINT");
            types.put(new Integer(Types.VARBINARY), "VARBINARY");
            types.put(new Integer(Types.VARCHAR), "VARCHAR");
            this.typeStrings = types;
        }
    }

    /**
     * Sybase and SQL Anywhere do not support BLOB/CLOB but require LONGVARBINARY/LONGVARCHAR.
     */
    @Override
    public int getJDBCType(Class javaType) {
        if (javaType == ClassConstants.BLOB)  {
            return Types.LONGVARBINARY;
        } else if (javaType == ClassConstants.CLOB) {
            return Types.LONGVARCHAR;
        } else {
            return super.getJDBCType(javaType);
        }
    }
    
    /**
     * If using native SQL then print a byte[] as '0xFF...'
     */
    @Override
    protected void appendByteArray(byte[] bytes, Writer writer) throws IOException {
        if (usesNativeSQL() && (!usesByteArrayBinding())) {
            writer.write("0x");
            Helper.writeHexString(bytes, writer);
        } else {
            super.appendByteArray(bytes, writer);
        }
    }

    /**
     * Answer a platform correct string representation of a Date, suitable for SQL generation.
     * Native format: 'yyyy-mm-dd
     */
    @Override
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
     * Write a timestamp in Sybase specific format (yyyy-mm-dd-hh.mm.ss.fff).
     */
    protected void appendSybaseTimestamp(java.sql.Timestamp timestamp, Writer writer) throws IOException {
        writer.write("'");
        writer.write(Helper.printTimestampWithoutNanos(timestamp));
        writer.write(':');

        // Must truncate the nanos to three decimal places,
        // it is actually a complex algorithm...
        String nanoString = Integer.toString(timestamp.getNanos());
        int numberOfZeros = 0;
        for (int num = Math.min(9 - nanoString.length(), 3); num > 0; num--) {
            writer.write('0');
            numberOfZeros++;
        }
        if ((nanoString.length() + numberOfZeros) > 3) {
            nanoString = nanoString.substring(0, (3 - numberOfZeros));
        }
        writer.write(nanoString);
        writer.write("'");
    }

    /**
     * Answer a platform correct string representation of a Time, suitable for SQL generation.
     * The time is printed in the ODBC platform independent format {t'hh:mm:ss'}.
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
     * Answer a platform correct string representation of a Timestamp, suitable for SQL generation.
     * The date is printed in the ODBC platform independent format {d'YYYY-MM-DD'}.
     */
    @Override
    protected void appendTimestamp(java.sql.Timestamp timestamp, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            appendSybaseTimestamp(timestamp, writer);
        } else {
            super.appendTimestamp(timestamp, writer);
        }
    }

    /**
     * Answer a platform correct string representation of a Calendar, suitable for SQL generation.
     * The date is printed in the ODBC platform independent format {d'YYYY-MM-DD'}.
     */
    @Override
    protected void appendCalendar(Calendar calendar, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            appendSybaseCalendar(calendar, writer);
        } else {
            super.appendCalendar(calendar, writer);
        }
    }

    /**
     * Write a timestamp in Sybase specific format ( yyyy-mm-dd-hh.mm.ss.fff)
     */
    protected void appendSybaseCalendar(Calendar calendar, Writer writer) throws IOException {
        writer.write("'");
        writer.write(Helper.printCalendar(calendar));
        writer.write("'");
    }

    /**
     * INTERNAL:
     * Build operator.
     */
    public ExpressionOperator atan2Operator() {
        return ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Atan2, "ATN2");
    }

    @Override
    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypeMapping;

        fieldTypeMapping = new Hashtable();
        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("BIT default 0", false, false));

        fieldTypeMapping.put(Integer.class, new FieldTypeDefinition("INTEGER", false));
        fieldTypeMapping.put(Long.class, new FieldTypeDefinition("NUMERIC", 19));
        fieldTypeMapping.put(Float.class, new FieldTypeDefinition("FLOAT(16)", false));
        fieldTypeMapping.put(Double.class, new FieldTypeDefinition("FLOAT(32)", false));
        fieldTypeMapping.put(Short.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(Byte.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(java.math.BigInteger.class, new FieldTypeDefinition("NUMERIC", 38));
        fieldTypeMapping.put(java.math.BigDecimal.class, new FieldTypeDefinition("NUMERIC", 38).setLimits(38, -19, 19));
        fieldTypeMapping.put(Number.class, new FieldTypeDefinition("NUMERIC", 38).setLimits(38, -19, 19));

        fieldTypeMapping.put(String.class, new FieldTypeDefinition("VARCHAR", DEFAULT_VARCHAR_SIZE));
        fieldTypeMapping.put(Character.class, new FieldTypeDefinition("CHAR", 1));
        fieldTypeMapping.put(Byte[].class, new FieldTypeDefinition("IMAGE", false));
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("TEXT", false));
        fieldTypeMapping.put(byte[].class, new FieldTypeDefinition("IMAGE", false));
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("TEXT", false));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldTypeDefinition("IMAGE", false));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldTypeDefinition("TEXT", false));        
        
        fieldTypeMapping.put(java.sql.Date.class, new FieldTypeDefinition("DATETIME", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldTypeDefinition("DATETIME", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldTypeDefinition("DATETIME", false));

        return fieldTypeMapping;
    }

    /**
     * INTERNAL:
     * Build the identity query for native sequencing.
     */
    @Override
    public ValueReadQuery buildSelectQueryForIdentity() {
        ValueReadQuery selectQuery = new ValueReadQuery();
        StringWriter writer = new StringWriter();
        writer.write("SELECT @@IDENTITY");
        selectQuery.setSQLString(writer.toString());
        return selectQuery;
    }

    /**
     * because each platform has different requirements for accessing stored procedures and
     * the way that we can combine resultsets and output params the stored procedure call
     * is being executed on the platform.  This entire process needs some serious refactoring to eliminate
     * the chance of bugs.
     */
    @Override
    public Object executeStoredProcedure(DatabaseCall dbCall, PreparedStatement statement, DatabaseAccessor accessor, AbstractSession session) throws SQLException {
        if (useJDBCStoredProcedureSyntax()){
            return super.executeStoredProcedure(dbCall, statement, accessor, session);
        }
        Object result = null;
        ResultSet resultSet = null;
        if (!dbCall.getReturnsResultSet()) {
            accessor.executeDirectNoSelect(statement, dbCall, session);
            result = accessor.buildOutputRow((CallableStatement)statement, dbCall, session);

            //ReadAllQuery may be returning just output params, or they may be executing a DataReadQuery, which also
            //assumes a vector
            if (dbCall.areManyRowsReturned()) {
                Vector tempResult = new Vector();
                (tempResult).add(result);
                result = tempResult;
            }
        } else {
            // start the process of processing the result set and the output params.  this is specific to Sybase JConnect 5.5
            // as we must process the result set before the output params.
            session.startOperationProfile(SessionProfiler.StatementExecute, dbCall.getQuery(), SessionProfiler.ALL);
            try {
                resultSet = statement.executeQuery();
            } finally {
                session.endOperationProfile(SessionProfiler.StatementExecute, dbCall.getQuery(), SessionProfiler.ALL);
            }
            dbCall.matchFieldOrder(resultSet, accessor, session);

            // cursored result set and output params not supported because of database limitation
            if (dbCall.isCursorReturned()) {
                dbCall.setStatement(statement);
                dbCall.setResult(resultSet);
                return dbCall;
            }
            result = accessor.processResultSet(resultSet, dbCall, statement, session);

            if (dbCall.shouldBuildOutputRow()) {
                AbstractRecord outputRow = accessor.buildOutputRow((CallableStatement)statement, dbCall, session);
                dbCall.getQuery().setProperty("output", outputRow);
                session.getEventManager().outputParametersDetected(outputRow, dbCall);
            }
            return result;
            // end special sybase behavior.
        }
        return result;
    }

    /**
     * The sybase syntax for obtaining pessimistic lock is "SELECT ADDRESS_ID, ... FROM ADDRESS WITH (HOLDLOCK) WHERE (ADDRESS_ID = ?)"
     * Please note that above only obtains shared lock. Apparently there is no way to obtain exclusive lock on Sybase
     * using only a select statement
     */
    @Override
    public boolean shouldPrintLockingClauseAfterWhereClause() {
        return false;
    }

    @Override
    public String getSelectForUpdateString() {
        return " HOLDLOCK";
    }

    /**
     * Used for batch writing and sp defs.
     */
    @Override
    public String getBatchDelimiterString() {
        return "";
    }

    /* This method is used to print the required output parameter token for the
     * specific platform.  Used when stored procedures are created.
     */
    @Override
    public String getCreationInOutputProcedureToken() {
        return getInOutputProcedureToken();
    }

    /*
     * this method was added because SQLServer requires the output paramater token
     * to be set on creation but not on execution.
     */
    @Override
    public String getCreationOutputProcedureToken() {
        return getOutputProcedureToken();
    }

    /* This method is used to print the output parameter token when stored
     * procedures are called
     */
    @Override
    public String getInOutputProcedureToken() {
        return "OUT";
    }

    /**
     * INTERNAL:
     * Returns the type name corresponding to the jdbc type
     */
    @Override
    public String getJdbcTypeName(int jdbcType) {
        return (String)getTypeStrings().get(new Integer(jdbcType));
    }

    /**
     * INTERNAL:
     * returns the maximum number of characters that can be used in a field
     * name on this platform.
     */
    @Override
    public int getMaxFieldNameSize() {
        return 22;
    }

    /**
     * Return the catalog information through using the native SQL catalog selects.
     * This is required because many JDBC driver do not support meta-data.
     * Willcards can be passed as arguments.
     */
    public Vector getNativeTableInfo(String table, String creator, AbstractSession session) {
        // need to filter only tables / views
        String query = "SELECT * FROM sysobjects WHERE table_type <> 'SYSTEM_TABLE'";
        if (table != null) {
            if (table.indexOf('%') != -1) {
                query = query + " AND table_name LIKE " + table;
            } else {
                query = query + " AND table_name = " + table;
            }
        }
        if (creator != null) {
            if (creator.indexOf('%') != -1) {
                query = query + " AND table_owner LIKE " + creator;
            } else {
                query = query + " AND table_owner = " + creator;
            }
        }
        return session.executeSelectingCall(new org.eclipse.persistence.queries.SQLCall(query));
    }

    /* This method is used to print the output parameter token when stored
     * procedures are called
     */
    @Override
    public String getOutputProcedureToken() {
        return useJDBCStoredProcedureSyntax() ? "" : "OUTPUT";
    }

    /**
     * Used for sp defs.
     */
    @Override
    public String getProcedureArgumentString() {
        return "@";
    }

    /**
     * Used for sp calls.
     */
    @Override
    public String getProcedureCallHeader() {
        return useJDBCStoredProcedureSyntax() ? "{Call " : "EXECUTE ";
    }
    public String getProcedureCallTail() {
        return useJDBCStoredProcedureSyntax() ? "}" : "";
    }
    /**
     * Return true if this platform is to use the JDBC supported syntax for executing stored procedures.
     * If the driver is known to be the DataDirec driver, and the value is not set, then set to true and return.
     */
    public boolean useJDBCStoredProcedureSyntax() {
    
        if (useJDBCStoredProcedureSyntax == null) {
            useJDBCStoredProcedureSyntax = this.driverName != null && this.driverName.equals("Sybase");
        }
        return useJDBCStoredProcedureSyntax;
    }

    @Override
    public String getStoredProcedureParameterPrefix() {
        return "@";
    }

    /**
     * INTERNAL:
     * This method returns the delimiter between stored procedures in multiple stored procedure
     * calls.
     */
    @Override
    public String getStoredProcedureTerminationToken() {
        return " go";
    }

    /**
     * PUBLIC:
     * This method returns the query to select the timestamp
     * from the server for Sybase.
     */
    @Override
    public ValueReadQuery getTimestampQuery() {
        if (timestampQuery == null) {
            timestampQuery = new ValueReadQuery();
            timestampQuery.setSQLString("SELECT GETDATE()");
            timestampQuery.setAllowNativeSQLQuery(true);
        }
        return timestampQuery;
    }

    /**
     * Initialize any platform-specific operators
     */
    @Override
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        addOperator(operatorOuterJoin());
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.Today, "GETDATE"));
        // GETDATE returns both date and time. It is not the perfect match for 
        // ExpressionOperator.currentDate and ExpressionOperator.currentTime
        // However, there is no known function on sql server that returns just 
        // the date or just the time.
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.CurrentDate, "GETDATE"));
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.CurrentTime, "GETDATE"));
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.Length, "CHAR_LENGTH"));
        addOperator(ExpressionOperator.sybaseLocateOperator());
        addOperator(ExpressionOperator.sybaseLocate2Operator());
        addOperator(ExpressionOperator.simpleThreeArgumentFunction(ExpressionOperator.Substring, "SUBSTRING"));
        addOperator(singleArgumentSubstringOperator());
        addOperator(ExpressionOperator.addDate());
        addOperator(ExpressionOperator.dateName());
        addOperator(ExpressionOperator.datePart());
        addOperator(ExpressionOperator.dateDifference());
        addOperator(ExpressionOperator.difference());
        addOperator(ExpressionOperator.charIndex());
        addOperator(ExpressionOperator.charLength());
        addOperator(ExpressionOperator.reverse());
        addOperator(ExpressionOperator.replicate());
        addOperator(ExpressionOperator.right());
        addOperator(ExpressionOperator.cot());
        addOperator(ExpressionOperator.sybaseAtan2Operator());
        addOperator(ExpressionOperator.sybaseAddMonthsOperator());
        addOperator(ExpressionOperator.sybaseInStringOperator());
        // bug 3061144
        addOperator(ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Nvl, "ISNULL"));
        // CR### TO_NUMBER, TO_CHAR, TO_DATE is CONVERT(type, ?)
        addOperator(ExpressionOperator.sybaseToNumberOperator());
        addOperator(ExpressionOperator.sybaseToDateToStringOperator());
        addOperator(ExpressionOperator.sybaseToDateOperator());
        addOperator(ExpressionOperator.sybaseToCharOperator());
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.Ceil, "CEILING"));
        addOperator(modOperator());
        addOperator(trimOperator());
        addOperator(trim2Operator());
        // Sybase does not define REPLACE only STR_REPLACE
        addOperator(ExpressionOperator.simpleThreeArgumentFunction(ExpressionOperator.Replace, "STR_REPLACE"));
        addOperator(extractOperator());
    }

    /**
     * INTERNAL:
     * Derby does not support EXTRACT, but does have DATEPART.
     */
    public static ExpressionOperator extractOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.Extract);
        exOperator.setName("EXTRACT");
        Vector v = NonSynchronizedVector.newInstance(5);
        v.add("DATEPART(");
        v.add(",");
        v.add(")");
        exOperator.printsAs(v);
        int[] indices = new int[2];
        indices[0] = 1;
        indices[1] = 0;
        exOperator.setArgumentIndices(indices);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }
    
    /**
     * INTERNAL:
     * Use RTRIM(LTRIM(?)) function for trim.
     */
    public static ExpressionOperator trimOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.Trim);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        v.add("RTRIM(LTRIM(");
        v.add("))");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }
    
    /**
     * INTERNAL:
     * Build Trim operator.
     */
    public static ExpressionOperator trim2Operator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.Trim2);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(5);
        v.add("STR_REPLACE(");
        v.add(", ");
        v.add(", NULL)");
        exOperator.printsAs(v);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    @Override
    public boolean isSybase() {
        return true;
    }

    /**
     * Builds a table of maximum numeric values keyed on java class. This is used for type testing but
     * might also be useful to end users attempting to sanitize values.
     * <p><b>NOTE</b>: BigInteger & BigDecimal maximums are dependent upon their precision & Scale
     */
    @Override
    public Hashtable maximumNumericValues() {
        Hashtable values = new Hashtable();

        values.put(Integer.class, Integer.valueOf(Integer.MAX_VALUE));
        values.put(Long.class, Long.valueOf(Long.MAX_VALUE));
        values.put(Double.class, Double.valueOf(Float.MAX_VALUE));
        values.put(Short.class, Short.valueOf(Short.MAX_VALUE));
        values.put(Byte.class, Byte.valueOf(Byte.MAX_VALUE));
        values.put(Float.class, Float.valueOf(Float.MAX_VALUE));
        values.put(java.math.BigInteger.class, new java.math.BigInteger("99999999999999999999999999999999999999"));
        values.put(java.math.BigDecimal.class, new java.math.BigDecimal("9999999999999999999.9999999999999999999"));
        return values;
    }

    /**
     * Builds a table of minimum numeric values keyed on java class. This is used for type testing but
     * might also be useful to end users attempting to sanitize values.
     * <p><b>NOTE</b>: BigInteger & BigDecimal minimums are dependent upon their precision & Scale
     */
    @Override
    public Hashtable minimumNumericValues() {
        Hashtable values = new Hashtable();

        values.put(Integer.class, Integer.valueOf(Integer.MIN_VALUE));
        values.put(Long.class, Long.valueOf(Long.MIN_VALUE));
        values.put(Double.class, Double.valueOf(1.4012984643247149E-44));// The double values are weird. They lose precision at E-45
        values.put(Short.class, Short.valueOf(Short.MIN_VALUE));
        values.put(Byte.class, Byte.valueOf(Byte.MIN_VALUE));
        values.put(Float.class, Float.valueOf(Float.MIN_VALUE));
        values.put(java.math.BigInteger.class, new java.math.BigInteger("-99999999999999999999999999999999999999"));
        values.put(java.math.BigDecimal.class, new java.math.BigDecimal("-9999999999999999999.9999999999999999999"));
        return values;
    }

    /**
     * Override the default MOD operator.
     */
    public ExpressionOperator modOperator() {
        ExpressionOperator result = new ExpressionOperator();
        result.setSelector(ExpressionOperator.Mod);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
        v.addElement(" % ");
        result.printsAs(v);
        result.bePostfix();
        result.setNodeClass(org.eclipse.persistence.internal.expressions.FunctionExpression.class);
        return result;
    }

    /*
     *  Create the outer join operator for this platform
     */
    protected ExpressionOperator operatorOuterJoin() {
        ExpressionOperator result = new ExpressionOperator();
        result.setSelector(ExpressionOperator.EqualOuterJoin);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
        v.addElement(" =* ");
        result.printsAs(v);
        result.bePostfix();
        result.setNodeClass(RelationExpression.class);
        return result;
    }

    /** Append the receiver's field 'identity' constraint clause to a writer.*/
    @Override
    public void printFieldIdentityClause(Writer writer) throws ValidationException {
        try {
            writer.write(" IDENTITY");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /** Append the receiver's field 'NULL' constraint clause to a writer.*/
    @Override
    public void printFieldNullClause(Writer writer) throws ValidationException {
        try {
            writer.write(" NULL");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /**
     * This method is used to register output parameter on Callable Statements for Stored Procedures
     * as each database seems to have a different method.
     */
    @Override
    public void registerOutputParameter(CallableStatement statement, int index, int jdbcType) throws SQLException {
        statement.registerOutParameter(index, jdbcType, (String)getTypeStrings().get(new Integer(jdbcType)));
    }

    /**
     * USed for sp calls.
     */
    @Override
    public boolean requiresProcedureCallBrackets() {
        return useJDBCStoredProcedureSyntax();
    }

    /**
     * Used for sp calls.  Sybase must print output after output params.
     */
    @Override
    public boolean requiresProcedureCallOuputToken() {
        return true;
    }

    /**
     * INTERNAL:
     * Indicates whether the version of CallableStatement.registerOutputParameter method
     * that takes type name should be used.
     */
    @Override
    public boolean requiresTypeNameToRegisterOutputParameter() {
        return true;
    }

    /* This is required in the construction of the stored procedures with
     * output parameters
     */
    @Override
    public boolean shouldPrintInOutputTokenBeforeType() {
        return false;
    }


    /* This is required in the construction of the stored procedures with
     * output parameters
     */
    @Override
    public boolean shouldPrintOutputTokenBeforeType() {
        return false;
    }

    /**
     * JDBC defines and outer join syntax, many drivers do not support this. So we normally avoid it.
     */
    @Override
    public boolean shouldUseJDBCOuterJoinSyntax() {
        return false;
    }

    /**
     * Override the default SubstringSingleArg operator.
     */
    public ExpressionOperator singleArgumentSubstringOperator() {
        ExpressionOperator result = new ExpressionOperator();
        result.setSelector(ExpressionOperator.SubstringSingleArg);
        result.setType(ExpressionOperator.FunctionOperator);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
        v.addElement("SUBSTRING(");
        v.addElement(",");
        v.addElement(", CHAR_LENGTH(");
        v.addElement("))");
        result.printsAs(v);
        int[] indices = new int[3];
        indices[0] = 0;
        indices[1] = 1;
        indices[2] = 0;

        result.setArgumentIndices(indices);
        result.setNodeClass(ClassConstants.FunctionExpression_Class);
        result.bePrefix();
        return result;
    }
    
    /**
     * INTERNAL:
     * Indicates whether the platform supports identity.
     * Sybase does through IDENTITY field types.
     * This method is to be used *ONLY* by sequencing classes
     */
    @Override
    public boolean supportsIdentity() {
        return true;
    }

    /**
     * Sybase (as of Sybase ASE 15, does not support delete on cascade).
     * Sybase ASA (SQL Anywhere does, so must use different platform).
     */
    @Override
    public boolean supportsDeleteOnCascade() {
        return false;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean supportsGlobalTempTables() {
        return true;
    }
     
    /**
     * INTERNAL:
     */
    @Override
    protected String getCreateTempTableSqlPrefix() {
        return "CREATE TABLE ";
    }          

    /**
     * INTERNAL:
     */
    @Override
    public DatabaseTable getTempTableForTable(DatabaseTable table) {
        return new DatabaseTable("#" + table.getName(), table.getTableQualifier(), table.shouldUseDelimiters(), getStartDelimiter(), getEndDelimiter());
    }          

    /**
     * INTERNAL:
     */
    @Override
    public void writeUpdateOriginalFromTempTableSql(Writer writer, DatabaseTable table,
                                                    Collection pkFields,
                                                    Collection assignedFields) throws IOException 
    {
        writer.write("UPDATE ");
        String tableName = table.getQualifiedNameDelimited(this);
        writer.write(tableName);
        String tempTableName = getTempTableForTable(table).getQualifiedNameDelimited(this);
        writeAutoAssignmentSetClause(writer, null, tempTableName, assignedFields, this);
        writer.write(" FROM ");
        writer.write(tableName);
        writer.write(", ");
        writer.write(tempTableName);
        writeAutoJoinWhereClause(writer, tableName, tempTableName, pkFields, this);
    }          
}
