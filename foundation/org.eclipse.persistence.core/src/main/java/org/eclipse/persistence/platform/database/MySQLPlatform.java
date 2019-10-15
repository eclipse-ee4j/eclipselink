/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2018 IBM Corporation. All rights reserved.
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
//     Zoltan NAGY & tware - added implementation of updateMaxRowsForQuery
//     09/14/2011-2.3.1 Guy Pelletier
//       - 357533: Allow DDL queries to execute even when Multitenant entities are part of the PU
//     02/04/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     04/30/2014-2.6 Lukas Jungmann
//       - 380101: Invalid MySQL SQL syntax in query with LIMIT and FOR UPDATE
//     07/23/2014-2.6 Lukas Jungmann
//       - 440278: Support fractional seconds in time values
//     02/19/2015 - Rick Curtis
//       - 458877 : Add national character support
package org.eclipse.persistence.platform.database;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * <p><b>Purpose</b>: Provides MySQL specific behavior.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Native SQL for Date, Time, {@literal &} Timestamp.
 * <li> Native sequencing.
 * <li> Mapping of class types to database types for the schema framework.
 * <li> Pessimistic locking.
 *  <li> Platform specific operators.
 * </ul>
 *
 * @since OracleAS TopLink 10<i>g</i> (10.1.3)
 */
public class MySQLPlatform extends DatabasePlatform {


    private static final String LIMIT = " LIMIT ";

    /** Support fractional seconds in time values since MySQL v. 5.6.4. */
    private boolean isFractionalTimeSupported;
    private boolean isConnectionDataInitialized;

    public MySQLPlatform(){
        super();
        this.pingSQL = "SELECT 1";
        this.startDelimiter = "`";
        this.endDelimiter = "`";
    }

    @Override
    public void initializeConnectionData(Connection connection) throws SQLException {
        if (this.isConnectionDataInitialized) {
            return;
        }
        DatabaseMetaData dmd = connection.getMetaData();
        String databaseVersion = dmd.getDatabaseProductVersion();
        this.isFractionalTimeSupported = Helper.compareVersions(databaseVersion, "5.6.4") >= 0;
        // Driver 5.1 supports NVARCHAR
        this.driverSupportsNationalCharacterVarying = Helper.compareVersions(dmd.getDriverVersion(), "5.1.0") >= 0;
        this.isConnectionDataInitialized = true;
    }

    public boolean isFractionalTimeSupported() {
        return isFractionalTimeSupported;
    }

    /**
     * Appends an MySQL specific date if usesNativeSQL is true otherwise use the ODBC format.
     * Native FORMAT: 'YYYY-MM-DD'
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
     * Appends an MySQL specific time if usesNativeSQL is true otherwise use the ODBC format.
     * Native FORMAT: 'HH:MM:SS'.
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
     * Appends an MySQL specific Timestamp, if usesNativeSQL is true otherwise use the ODBC format.
     * Native Format: 'YYYY-MM-DD HH:MM:SS'
     */
    @Override
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
    @Override
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
    @Override
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

        if(getUseNationalCharacterVaryingTypeForString()){
            fieldTypeMapping.put(String.class, new FieldTypeDefinition("NVARCHAR", DEFAULT_VARCHAR_SIZE));
        } else {
            fieldTypeMapping.put(String.class, new FieldTypeDefinition("VARCHAR", DEFAULT_VARCHAR_SIZE));
        }
        fieldTypeMapping.put(Character.class, new FieldTypeDefinition("CHAR", 1));

        fieldTypeMapping.put(Byte[].class, new FieldTypeDefinition("LONGBLOB", false));
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("LONGTEXT", false));
        fieldTypeMapping.put(byte[].class, new FieldTypeDefinition("LONGBLOB", false));
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("LONGTEXT", false));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldTypeDefinition("LONGBLOB", false));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldTypeDefinition("LONGTEXT", false));

        fieldTypeMapping.put(java.sql.Date.class, new FieldTypeDefinition("DATE", false));
        FieldTypeDefinition fd = new FieldTypeDefinition("TIME");
        if (!isFractionalTimeSupported) {
            fd.setIsSizeAllowed(false);
        }
        fieldTypeMapping.put(java.sql.Time.class, fd);
        fd = new FieldTypeDefinition("DATETIME");
        if (!isFractionalTimeSupported) {
            fd.setIsSizeAllowed(false);
        }
        fieldTypeMapping.put(java.sql.Timestamp.class, fd);

        fieldTypeMapping.put(java.time.LocalDate.class, new FieldTypeDefinition("DATE"));

        fd = new FieldTypeDefinition("DATETIME");
        if (!isFractionalTimeSupported) {
            fd.setIsSizeAllowed(false);
        } else {
            fd.setDefaultSize(3);
            fd.setIsSizeRequired(true);
        }
        fieldTypeMapping.put(java.time.LocalDateTime.class,fd); //no timezone info

        fd = new FieldTypeDefinition("TIME");
        if (!isFractionalTimeSupported) {
            fd.setIsSizeAllowed(false);
        } else {
            fd.setDefaultSize(3);
            fd.setIsSizeRequired(true);
        }
        fieldTypeMapping.put(java.time.LocalTime.class, fd);

        fd = new FieldTypeDefinition("DATETIME");
        if (!isFractionalTimeSupported) {
            fd.setIsSizeAllowed(false);
        } else {
            fd.setDefaultSize(3);
            fd.setIsSizeRequired(true);
        }
        fieldTypeMapping.put(java.time.OffsetDateTime.class, fd); //no timezone info

        fd = new FieldTypeDefinition("TIME");
        if (!isFractionalTimeSupported) {
            fd.setIsSizeAllowed(false);
        } else {
            fd.setDefaultSize(3);
            fd.setIsSizeRequired(true);
        }
        fieldTypeMapping.put(java.time.OffsetTime.class, fd);
        return fieldTypeMapping;
    }

    @Override
    public int getJDBCType(Class javaType) {
        if (javaType == ClassConstants.TIME_ODATETIME) {
            return Types.TIMESTAMP;
        } else if (javaType == ClassConstants.TIME_OTIME) {
            return Types.TIME;
        }
        return super.getJDBCType(javaType);
    }

    /**
     * INTERNAL:
     * Build the identity query for native sequencing.
     */
    @Override
    public ValueReadQuery buildSelectQueryForIdentity() {
        ValueReadQuery selectQuery = new ValueReadQuery();
        StringWriter writer = new StringWriter();
        writer.write("SELECT LAST_INSERT_ID()");
        selectQuery.setSQLString(writer.toString());
        return selectQuery;
    }

    /**
     * Return the stored procedure syntax for this platform.
     */
    @Override
    public String buildProcedureCallString(StoredProcedureCall call, AbstractSession session, AbstractRecord row) {
        return "{ " + super.buildProcedureCallString(call, session, row);
    }

    /**
     * INTERNAL:
     * Use the JDBC maxResults and firstResultIndex setting to compute a value to use when
     * limiting the results of a query in SQL.  These limits tend to be used in two ways.
     *
     * 1. MaxRows is the index of the last row to be returned (like JDBC maxResults)
     * 2. MaxRows is the number of rows to be returned
     *
     * MySQL uses case #2 and therefore the maxResults has to be altered based on the firstResultIndex
     *
     * @param firstResultIndex
     * @param maxResults
     *
     * @see org.eclipse.persistence.platform.database.MySQLPlatform
     */
    @Override
    public int computeMaxRowsForSQL(int firstResultIndex, int maxResults){
        return maxResults - ((firstResultIndex >= 0) ? firstResultIndex : 0);
    }

    /**
     * INTERNAL:
     * Supports Batch Writing with Optimistic Locking.
     */
    @Override
    public boolean canBatchWriteWithOptimisticLocking(DatabaseCall call){
        return true;
    }

    /**
     * INTERNAL:
     * Used for constraint deletion.
     */
    @Override
    public String getConstraintDeletionString() {
        return " DROP FOREIGN KEY ";
    }

    /**
     * INTERNAL:
     * Used for unique constraint deletion.
     */
    @Override
    public String getUniqueConstraintDeletionString() {
        return " DROP KEY ";
    }

    /**
     * Used for stored function calls.
     */
    @Override
    public String getFunctionCallHeader() {
        return "? " + getAssignmentString() + getProcedureCallHeader();
        // different order -  CALL clause ^^^ comes AFTER assignment operator
    }

    /**
     * Used for sp calls.
     */
    @Override
    public String getProcedureCallTail() {
        return " }"; // case-sensitive
    }

    /**
     * INTERNAL:
     * Used for pessimistic locking.
     */
    @Override
    public String getSelectForUpdateString() {
        return " FOR UPDATE";
    }

    @Override
    public boolean isForUpdateCompatibleWithDistinct() {
        return false;
    }

    /**
     * INTERNAL:
     * This method returns the query to select the timestamp
     * from the server for MySQL.
     */
    @Override
    public ValueReadQuery getTimestampQuery() {
        if (timestampQuery == null) {
            timestampQuery = new ValueReadQuery();
            timestampQuery.setSQLString("SELECT NOW()");
            timestampQuery.setAllowNativeSQLQuery(true);
        }
        return timestampQuery;
    }

    /**
     * Answers whether platform is MySQL.
     */
    @Override
    public boolean isMySQL() {
        return true;
    }

    /**
     * Initialize any platform-specific operators.
     */
    @Override
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
        v.addElement("LOG(10, ");
        v.addElement(")");
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
    @Override
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
    @Override
    public boolean shouldUseJDBCOuterJoinSyntax() {
        return false;
    }

    /**
     * INTERNAL:
     * Indicates whether the platform supports identity.
     * MySQL supports native sequencing through AUTO_INCREMENT field types.
     */
    @Override
    public boolean supportsIdentity() {
        return true;
    }

    /**
     * INTERNAL:
     * Indicates whether the platform supports the count distinct function with multiple fields.
     */
    @Override
    public boolean supportsCountDistinctWithMultipleFields() {
        return true;
    }

    /**
     * INTERNAL:
     * Return if this database requires the table name when dropping an index.
     */
    @Override
    public boolean requiresTableInIndexDropDDL() {
        return true;
    }

    /**
     * INTERNAL:
     * MySQL supports temp tables for update-all, delete-all queries.
     */
    @Override
    public boolean supportsGlobalTempTables() {
        return true;
    }

    /**
     * INTERNAL:
     * Indicates whether locking clause could be selectively applied only to some tables in a ReadQuery.
     * Example: the following locks the rows in SALARY table, doesn't lock the rows in EMPLOYEE table:
     *   on Oracle platform (method returns true):
     *     SELECT t0.EMP_ID..., t1.SALARY FROM EMPLOYEE t0, SALARY t1 WHERE ... FOR UPDATE t1.SALARY
     *   on SQLServer platform (method returns true):
     *     SELECT t0.EMP_ID..., t1.SALARY FROM EMPLOYEE t0, SALARY t1 WITH (UPDLOCK) WHERE ...
     */
    @Override
    public boolean supportsIndividualTableLocking() {
        return false;
    }

    @Override
    public boolean supportsStoredFunctions() {
        return true;
    }

    /**
     * Some db allow VARCHAR db field to be used in arithmetic operations automatically converting them to numeric:
     * UPDATE OL_PHONE SET PHONE_ORDER_VARCHAR = (PHONE_ORDER_VARCHAR + 1) WHERE ...
     * SELECT ... WHERE  ... t0.MANAGED_ORDER_VARCHAR BETWEEN 1 AND 4 ...
     */
    @Override
    public boolean supportsAutoConversionToNumericForArithmeticOperations() {
        return true;
    }

    /**
     * INTERNAL:
     * MySQL temp table syntax, used for update-all, delete-all queries.
     */
    @Override
    protected String getCreateTempTableSqlPrefix() {
        return "CREATE TEMPORARY TABLE IF NOT EXISTS ";
    }

    /**
     * Return the drop schema definition. Subclasses should override as needed.
     */
    @Override
    public String getDropDatabaseSchemaString(String schema) {
        return "DROP SCHEMA " + schema + " IF EXISTS";
    }

    /**
     * INTERNAL:
     * MySQL supports temp tables for update-all, delete-all queries.
     */
    @Override
    public boolean shouldAlwaysUseTempStorageForModifyAll() {
        return true;
    }

    /**
     * INTERNAL:
     * MySQL stored procedure calls do not require the argument name be printed in the call string
     * e.g. call MyStoredProc(?) instead of call MyStoredProc(myvariable = ?)
     */
    @Override
    public boolean shouldPrintStoredProcedureArgumentNameInCall(){
        return false;
    }

    /**
     * INTERNAL:
     * MySQL FOR UPDATE clause has to be the last
     */
    @Override
    public boolean shouldPrintForUpdateClause(){
        return false;
    }

    /**
     * INTERNAL:
     * MySQL uses the INOUT keyword for this.
     */
    @Override
    public String getInOutputProcedureToken() {
        return "INOUT";
    }

    /**
     * MySQL does not use the AS token.
     */
    @Override
    public String getProcedureAsString() {
        return "";
    }

    /**
     * INTERNAL:
     * MySQL requires the direction at the start of the argument.
     */
    @Override
    public boolean shouldPrintOutputTokenAtStart() {
        return true;
    }

    /**
     * INTERNAL:
     * Used for stored procedure calls.
     */
    @Override
    public String getProcedureCallHeader() {
        return "CALL ";
    }

    /**
     * INTERNAL:
     * MySQL requires BEGIN.
     */
    @Override
    public String getProcedureBeginString() {
        return "BEGIN ";
    }

    /**
     * INTERNAL:
     * MySQL requires END.
     */
    @Override
    public String getProcedureEndString() {
        return "END";
    }

    /**
     * INTERNAL:
     * Writes MySQL specific SQL for accessing temp tables for update-all queries.
     */
    @Override
    public void writeUpdateOriginalFromTempTableSql(Writer writer, DatabaseTable table,
                                                    Collection pkFields,
                                                    Collection assignedFields) throws IOException
    {
        writer.write("UPDATE ");
        String tableName = table.getQualifiedNameDelimited(this);
        writer.write(tableName);
        writer.write(", ");
        String tempTableName = getTempTableForTable(table).getQualifiedNameDelimited(this);
        writer.write(tempTableName);
        writeAutoAssignmentSetClause(writer, tableName, tempTableName, assignedFields, this);
        writeAutoJoinWhereClause(writer, tableName, tempTableName, pkFields, this);
    }

    /**
     * INTERNAL:
     * Writes MySQL specific SQL for accessing temp tables for delete-all queries.
     */
    @Override
    public void writeDeleteFromTargetTableUsingTempTableSql(Writer writer, DatabaseTable table, DatabaseTable targetTable,
            Collection pkFields,
            Collection targetPkFields, DatasourcePlatform platform) throws IOException
    {
        writer.write("DELETE FROM ");
        String targetTableName = targetTable.getQualifiedNameDelimited(this);
        writer.write(targetTableName);
        writer.write(" USING ");
        writer.write(targetTableName);
        writer.write(", ");
        String tempTableName = getTempTableForTable(table).getQualifiedNameDelimited(this);
        writer.write(tempTableName);
        writeJoinWhereClause(writer, targetTableName, tempTableName, targetPkFields, pkFields, this);
    }

    @Override
    public void printSQLSelectStatement(DatabaseCall call, ExpressionSQLPrinter printer, SQLSelectStatement statement) {
        int max = 0;
        if (statement.getQuery() != null) {
            max = statement.getQuery().getMaxRows();
        }

        if (max <= 0 || !(this.shouldUseRownumFiltering())) {
            super.printSQLSelectStatement(call, printer, statement);
            statement.appendForUpdateClause(printer);
            return;
        }
        statement.setUseUniqueFieldAliases(true);
        call.setFields(statement.printSQL(printer));
        printer.printString(LIMIT);
        printer.printParameter(DatabaseCall.FIRSTRESULT_FIELD);
        printer.printString(", ");
        printer.printParameter(DatabaseCall.MAXROW_FIELD);
        statement.appendForUpdateClause(printer);
        call.setIgnoreFirstRowSetting(true);
        call.setIgnoreMaxResultsSetting(true);
    }

    /**
     * Used for stored procedure creation: MySQL platforms need brackets around arguments declaration even if no arguments exist.
     */
    @Override
    public boolean requiresProcedureBrackets() {
        return true;
    }

    /**
     * INTERNAL:
     * Prints return keyword for StoredFunctionDefinition:
     *    CREATE FUNCTION StoredFunction_In (P_IN BIGINT)
     *      RETURN  BIGINT
     * The method was introduced because MySQL requires "RETURNS" instead:
     *    CREATE FUNCTION StoredFunction_In (P_IN BIGINT)
     *      RETURNS  BIGINT
     */
    @Override
    public void printStoredFunctionReturnKeyWord(Writer writer) throws IOException {
        writer.write("\n\t RETURNS ");
    }

    // Value of shouldCheckResultTableExistsQuery must be true.
    /**
     * INTERNAL:
     * Returns query to check whether given table exists.
     * Query execution returns a row when table exists or empty result otherwise.
     * @param table database table meta-data
     * @return query to check whether given table exists
     */
    @Override
    protected DataReadQuery getTableExistsQuery(final TableDefinition table) {
        final DataReadQuery query = new DataReadQuery("SHOW TABLES LIKE '" + table.getFullName() + "'");
        query.setMaxRows(1);
        return query;
    }

    /**
     * INTERNAL:
     * Executes and evaluates query to check whether given table exists.
     * Returned value depends on returned result set being empty or not.
     * @param session current database session
     * @param table database table meta-data
     * @param suppressLogging whether to suppress logging during query execution
     * @return value of {@code true} if given table exists or {@code false} otherwise
     */
    @Override
    public boolean checkTableExists(final DatabaseSessionImpl session,
            final TableDefinition table, final boolean suppressLogging) {
        try {
            session.setLoggingOff(suppressLogging);
            final Vector result = (Vector)session.executeQuery(getTableExistsQuery(table));
            return !result.isEmpty();
        } catch (Exception notFound) {
            return false;
        }
    }

}
