/*
 * Copyright (c) 2012, 2026 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019, 2024 IBM Corporation. All rights reserved.
 * Copyright (c) 2012, 2024 SAP. All rights reserved.
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
// SAP AG - Initial implementation, enhancement bug 380226
//
// This code is being developed under INCUBATION and is not currently included
// in the automated EclipseLink build. The API in this code may change, or
// may never be included in the product. Please provide feedback through mailing
// lists or the bug database.
package org.eclipse.persistence.platform.database;

import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

import java.io.IOException;
import java.io.Serial;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <b>Database Platform for SAP HANA</b> <br>
 *
 * <p>
 * <b>Feature Testing</b><br>
 * ----------------------
 * <ul>
 * <li>DDL Generation - Succeeds
 * <li>Outer Join - Succeeds
 * <li>Subquery - Succeeds
 * <li>Stored Procedure Calls - Not supported
 * <li>Stored Procedure Generation - Not supported
 * <li>Native Sequences/Identifier fields - Succeeds
 * <li>JPA Bulk Update/Delete - Succeeds with Limitations
 * <li>Batch Reading - Succeeds
 * <li>Batch Writing - Succeeds
 * <li>Pessimistic Locking - Succeeds with Limitations
 * <li>First Result/Limit - Succeeds with Limitations
 * <li>Expression Framework - Succeeds with Limitations
 * <li>Delimiters - Succeeds
 * <li>Auto Detection - Succeeds
 * </ul>
 * <br>
 * <p>
 * <b>Limitations</b><br>
 * ----------------
 * <ul>
 * <li>Reserved SQL keywords cannot be used as table, column or sequence names. Use a different
 * name, or enclose the name in double quotes. For example: @Column(name="\"LANGUAGE\"")
 * <li>Pessimistic locking adds 'FOR UPDATE' to the SELECT statement, and cannot be used with
 * queries that use DISTINCT.
 * <li>Pessimistic locking cannot be used with queries that select from multiple tables.
 * <li>The LockNoWait option of Pessimistic Locking cannot be used; it is ignored when specified
 * (i.e. only 'FOR UPDATE' is added to the SELECT statement).
 * <li>Bulk update and delete operations that require multiple tables to be accessed cannot be used
 * (e.g. bulk operation on an entity that is part of an inheritance hierarchy, UpdateAll and
 * DeleteAll queries).
 * {@literal <li>'= NULL' and '<> NULL'} cannot be used for null comparisons in the WHERE clause.
 * Use 'IS (NOT) NULL' instead.
 * <li>Scrollable cursors are not supported.
 * <li>Query timeouts are not supported.
 * </ul>
 *
 * @author Reiner Singer (SAP AG), Sabine Heider (SAP AG)
 */
public final class HANAPlatform extends DatabasePlatform {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final int MAX_VARTYPE_LENGTH = 2000;

    public HANAPlatform() {
        super();
        this.pingSQL = "SELECT 1 FROM DUMMY";
    }

    @Override
    public boolean isHANA() {
        return true;
    }

    @Override
    public boolean usesStringBinding() {
        return false;
    }

    @Override
    public boolean requiresUniqueConstraintCreationOnTableCreate() {
        return true;
    }

    @Override
    public boolean isForUpdateCompatibleWithDistinct() {
        return false;
    }

    @Override
    public boolean supportsIndividualTableLocking() {
        return false;
    }

    @Override
    protected Map<Class<?>, FieldDefinition.DatabaseType> buildDatabaseTypes() {
        final Map<Class<?>, FieldDefinition.DatabaseType> fieldTypeMapping = new HashMap<>();
        fieldTypeMapping.put(Boolean.class, new FieldDefinition.DatabaseType("SMALLINT", false)); // TODO
                                                                                         // boolean
        fieldTypeMapping.put(Number.class, new FieldDefinition.DatabaseType("DOUBLE", false));
        fieldTypeMapping.put(Short.class, new FieldDefinition.DatabaseType("SMALLINT", false));
        fieldTypeMapping.put(Integer.class, new FieldDefinition.DatabaseType("INTEGER", false));
        fieldTypeMapping.put(Long.class, new FieldDefinition.DatabaseType("BIGINT", false));
        fieldTypeMapping.put(Float.class, new FieldDefinition.DatabaseType("FLOAT", false));
        fieldTypeMapping.put(Double.class, new FieldDefinition.DatabaseType("DOUBLE", false));

        fieldTypeMapping.put(BigInteger.class, new FieldDefinition.DatabaseType("DECIMAL", 34));
        fieldTypeMapping.put(BigDecimal.class,
                new FieldDefinition.DatabaseType("DECIMAL", 34, 0, 34, -34, 34));

        fieldTypeMapping.put(Character.class, new FieldDefinition.DatabaseType("NCHAR", 1));
        fieldTypeMapping.put(Character[].class, new FieldDefinition.DatabaseType("NVARCHAR", 255));
        fieldTypeMapping.put(char[].class, new FieldDefinition.DatabaseType("NVARCHAR", 255));
        fieldTypeMapping.put(String.class, new FieldDefinition.DatabaseType("NVARCHAR", 255));

        fieldTypeMapping.put(Byte.class, new FieldDefinition.DatabaseType("SMALLINT", false));
        fieldTypeMapping.put(Byte[].class, new FieldDefinition.DatabaseType("VARBINARY", 255));
        fieldTypeMapping.put(byte[].class, new FieldDefinition.DatabaseType("VARBINARY", 255));

        fieldTypeMapping.put(Blob.class, new FieldDefinition.DatabaseType("BLOB", false));
        fieldTypeMapping.put(Clob.class, new FieldDefinition.DatabaseType("NCLOB", false));

        fieldTypeMapping.put(Date.class, new FieldDefinition.DatabaseType("DATE", false));
        fieldTypeMapping.put(Time.class, new FieldDefinition.DatabaseType("TIME", false));
        fieldTypeMapping.put(Timestamp.class, new FieldDefinition.DatabaseType("TIMESTAMP", false));
        return fieldTypeMapping;
    }

    /**
     * EclipseLink does not support length dependent type mapping.
     * Map varchar types with length > MAX_VARCHAR_UNICODE_LENGTH to CLOB (i.e clob); shorter types to NVARCHAR (n)
     * See also bugs 317597, 202348
     */
    @Override
    public void printFieldTypeSize(Writer writer, FieldDefinition field,
            FieldDefinition.DatabaseType fieldType) throws IOException {
        String typeName = fieldType.name();
        if ("NVARCHAR".equals(typeName)) {
            if (field.getSize() > MAX_VARTYPE_LENGTH) {
                fieldType = new FieldDefinition.DatabaseType("NCLOB", false);
            }
        } else if ("VARBINARY".equals(typeName)) {
            if (field.getSize() > MAX_VARTYPE_LENGTH || field.getSize() == 0) {
                fieldType = new FieldDefinition.DatabaseType("BLOB", false);
            }
        }

        super.printFieldTypeSize(writer, field, fieldType);
        if (fieldType.suffix() != null) {
            writer.append(" " + fieldType.suffix());
        }
    }

    @Override
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        this.addOperator(HANAPlatform.createConcatExpressionOperator());
        this.addOperator(HANAPlatform.createNullifOperator());
        this.addOperator(HANAPlatform.createTodayExpressionOperator());
        this.addOperator(HANAPlatform.createCurrentDateExpressionOperator());
        this.addOperator(HANAPlatform.createCurrentTimeExpressionOperator());
        this.addOperator(HANAPlatform.createLogOperator());
        this.addOperator(HANAPlatform.createLocateOperator());
        this.addOperator(HANAPlatform.createLocate2Operator());
        this.addOperator(HANAPlatform.createVarianceOperator());
        this.addOperator(HANAPlatform.createNullValueOperator());
    }

    private static ExpressionOperator createConcatExpressionOperator() {
        return ExpressionOperator.simpleLogicalNoParens(ExpressionOperator.Concat, "||");
    }

    /**
     * Creates the expression operator representing the JPQL function current_timestamp as defined
     * by 4.6.17.2.3 of the JPA 2.0 specification
     *
     * @return the expression operator representing the JPQL function current_timestamp as defined
     *         by 4.6.17.2.3 of the JPA 2.0 specification
     */
    private static ExpressionOperator createTodayExpressionOperator() {
        return ExpressionOperator.simpleLogicalNoParens(ExpressionOperator.Today,
                "CURRENT_TIMESTAMP");
    }

    /**
     * Creates the expression operator representing the JPQL function current_date as defined by
     * 4.6.17.2.3 of the JPA 2.0 specification
     *
     * @return the expression operator representing the JPQL function current_date as defined by
     *         4.6.17.2.3 of the JPA 2.0 specification
     */
    private static ExpressionOperator createCurrentDateExpressionOperator() {
        return ExpressionOperator.simpleLogicalNoParens(ExpressionOperator.CurrentDate,
                "CURRENT_DATE");
    }

    /**
     * Creates the expression operator representing the JPQL function current_timestamp as defined
     * by 4.6.17.2.3 of the JPA 2.0 specification
     *
     * @return the expression operator representing the JPQL function current_timestamp as defined
     *         by 4.6.17.2.3 of the JPA 2.0 specification
     */
    private static ExpressionOperator createCurrentTimeExpressionOperator() {
        return ExpressionOperator.simpleLogicalNoParens(ExpressionOperator.CurrentTime,
                "CURRENT_TIME");
    }

    /**
     * Creates the expression operator representing the JPQL function variance
     *
     * @return the expression operator representing the JPQL function variance
     */
    private static ExpressionOperator createVarianceOperator() {
        return ExpressionOperator.simpleAggregate(ExpressionOperator.Variance, "VAR", "variance");
    }

    /**
     * Create the log operator for this platform
     */
    private static ExpressionOperator createLogOperator() {
        ExpressionOperator result = new ExpressionOperator();
        result.setSelector(ExpressionOperator.Log);
        List<String> v = new ArrayList<>(2);
        v.add("LOG(10,");
        v.add(")");
        result.printsAs(v);
        result.bePrefix();
        result.setNodeClass(FunctionExpression.class);
        return result;
    }

    /**
     * INTERNAL: Build locate operator i.e. LOCATE("ob", t0.F_NAME)
     */
    public static ExpressionOperator createLocateOperator() {
        ExpressionOperator expOperator = ExpressionOperator.simpleTwoArgumentFunction(
                ExpressionOperator.Locate, "INSTR");
        int[] argumentIndices = new int[2];
        argumentIndices[0] = 0;
        argumentIndices[1] = 1;
        expOperator.setArgumentIndices(argumentIndices);
        expOperator.setIsBindingSupported(false);
        return expOperator;
    }

    /**
     * INTERNAL: Build locate operator with 3 params i.e. LOCATE("coffee", t0.DESCRIP, 4). Last
     * parameter is a start at.
     */
    public static ExpressionOperator createLocate2Operator() {
        ExpressionOperator expOperator = ExpressionOperator.simpleThreeArgumentFunction(
                ExpressionOperator.Locate2, "INSTR");
        int[] argumentIndices = new int[3];
        argumentIndices[0] = 0;
        argumentIndices[1] = 1;
        argumentIndices[2] = 2;
        expOperator.setArgumentIndices(argumentIndices);
        expOperator.setIsBindingSupported(false);
        return expOperator;
    }

    private static ExpressionOperator createNullifOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.NullIf);
        List<String> v = new ArrayList<>(4);
        v.add(" (CASE WHEN ");
        v.add(" = ");
        v.add(" THEN NULL ELSE ");
        v.add(" END) ");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = { 0, 1, 0 };
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    private static ExpressionOperator createNullValueOperator() {
        ExpressionOperator exOperator = ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Nvl, "IFNULL");
        exOperator.setIsBindingSupported(false);
        return exOperator;
    }

    @Override
    public void printSQLSelectStatement(DatabaseCall call, ExpressionSQLPrinter printer,
            SQLSelectStatement statement) {
        int max = 0;
        int firstRow = 0;
        ReadQuery query = statement.getQuery();
        if (query != null) {
            max = query.getMaxRows();
            firstRow = query.getFirstResult();
        }

        if (max <= 0 && firstRow <= 0) {
            // neither max nor firstRow is set
            super.printSQLSelectStatement(call, printer, statement);
            return;
        }
        if (max <= 0) {
            // if max row is not set use MAX_VALUE instead
            // this is done, because NewDB does not allow
            // OFFSET without LIMIT, and scrollable cursor is not supported
            // in order to support firstRows without MaxRows also MaxRow has to be set
            // this limits also the size of the result set in this case to Integer.MAX_VALUE rows
            query.setMaxRows(Integer.MAX_VALUE);
        }
        statement.setUseUniqueFieldAliases(true);
        call.setFields(statement.printSQL(printer));
        printer.printString(" LIMIT ");
        printer.printParameter(DatabaseCall.MAXROW_FIELD);
        printer.printString(" OFFSET ");
        printer.printParameter(DatabaseCall.FIRSTRESULT_FIELD);
        call.setIgnoreFirstRowSetting(true);
        call.setIgnoreMaxResultsSetting(true);
    }

    @Override
    public int computeMaxRowsForSQL(int firstResultIndex, int maxResults) {
        return maxResults - ((firstResultIndex >= 0) ? firstResultIndex : 0);
    }

    @Override
    public boolean shouldOptimizeDataConversion() {
        return true; // TODO is this needed? (seems to default to true)
    }

    @Override
    public boolean supportsNativeSequenceNumbers() {
        return true;
    }

    @Override
    public ValueReadQuery buildSelectQueryForSequenceObject(final String sequenceName, final Integer size) {
        return new ValueReadQuery("SELECT " + sequenceName + ".NEXTVAL FROM DUMMY");
    }

    @Override
    public boolean supportsGlobalTempTables() {
        return false;
    }

    @Override
    protected String getCreateTempTableSqlPrefix() {
        return "CREATE LOCAL TEMPORARY TABLE ";
    }

    @Override
    public DatabaseTable getTempTableForTable(DatabaseTable table) {
        return new DatabaseTable("#" + table.getName(), table.getTableQualifier(), table.shouldUseDelimiters(), getStartDelimiter(), getEndDelimiter());
    }

    @Override
    protected boolean shouldTempTableSpecifyPrimaryKeys() {
        return false;
    }

    @Override
    public int getMaxFieldNameSize() {
        return 120;
    }

    @Override
    public boolean supportsLocalTempTables() {
        return true;
    }

    @Override
    public boolean shouldAlwaysUseTempStorageForModifyAll() {
        return false;
    }

    @Override
    public boolean shouldBindLiterals() {
        return false;
    }

    @Override
    public boolean shouldPrintOuterJoinInWhereClause() {
        return false;
    }

    @Override
    public boolean shouldUseJDBCOuterJoinSyntax() {
        return false;
    }

    @Override
    public boolean supportsSequenceObjects() {
        return true;
    }

    @Override
    public boolean canBatchWriteWithOptimisticLocking(DatabaseCall call) {
        return true;
    }

    @Override
    public int executeBatch(Statement statement, boolean isStatementPrepared) throws SQLException {
        int[] updateResult = statement.executeBatch();
        setExecuteBatchRowCounts(updateResult);
        if (isStatementPrepared) {
            int updateCount = 0;
            for (int count : updateResult) {
                if (count == Statement.SUCCESS_NO_INFO) {
                    count = 1;
                }
                updateCount += count;
            }
            return updateCount;
        } else {
            return updateResult.length;
        }
    }

    @Override
    public boolean supportsForeignKeyConstraints() {
        return true;
    }

    /**
     * Used for stored procedure creation: Prefix for INPUT parameters. Not required on most
     * platforms.
     */
    @Override
    public String getInputProcedureToken() {
        return "IN";
    }

    /**
     * This method is used to print the output parameter token when stored procedures are called
     */
    @Override
    public String getOutputProcedureToken() {
        return "OUT";
    }

    /**
     * Used for sp calls.
     */
    @Override
    public String getProcedureCallTail() {
        return "";
    }

    /**
     * INTERNAL: Should the variable name of a stored procedure call be printed as part of the
     * procedure call e.g. EXECUTE PROCEDURE MyStoredProc(myvariable = ?)
     */
    @Override
    public boolean shouldPrintStoredProcedureArgumentNameInCall() {
        return false;
    }

    @Override
    public boolean supportsStoredFunctions() {
        return false;
    }

    @Override
    protected void appendDate(Date date, Writer writer) throws IOException {
        writer.write("TO_DATE('");
        writer.write(Helper.printDate(date));
        writer.write("')");
    }

    @Override
    protected void appendTime(Time time, Writer writer) throws IOException {
        writer.write("TO_TIME('");
        writer.write(Helper.printTime(time));
        writer.write("')");
    }

    @Override
    protected void appendTimestamp(Timestamp timestamp, Writer writer) throws IOException {
        writer.write("TO_TIMESTAMP('");
        writer.write(Helper.printTimestamp(timestamp));
        writer.write("')");
    }

    @Override
    protected void appendCalendar(Calendar calendar, Writer writer) throws IOException {
        writer.write("TO_TIMESTAMP('");
        writer.write(Helper.printCalendar(calendar));
        writer.write("')");
    }

    @Override
    public void writeAddColumnClause(Writer writer, AbstractSession session, TableDefinition table, FieldDefinition field) throws IOException {
        writer.write("ADD (");
        field.appendDBString(writer, session, table);
        writer.write(")");
    }

    @Override
    public String getProcedureCallHeader() {
        return "CALL ";
    }
}
