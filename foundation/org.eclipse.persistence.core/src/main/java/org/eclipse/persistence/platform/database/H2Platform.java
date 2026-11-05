/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
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
//     Mike Keith
//
// Patterned after:
//   org.eclipse.persistence.platform.database.DB2MainframePlatform
//
//     09/14/2011-2.3.1 Guy Pelletier
//       - 357533: Allow DDL queries to execute even when Multitenant entities are part of the PU
package org.eclipse.persistence.platform.database;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.expressions.ExpressionJavaPrinter;
import org.eclipse.persistence.internal.expressions.ExtractOperator;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

import java.io.IOException;
import java.io.Serial;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class H2Platform extends DatabasePlatform {
    @Serial
    private static final long serialVersionUID = -2935483687958482934L;

    public H2Platform() {
        super();
        setPingSQL("SELECT 1");
        setSupportsReturnGeneratedKeys(true);
    }

    /**
     * Print the pagination SQL using H2 syntax " LIMIT {@literal <max> OFFSET <first>}".
     */
    @Override
    public void printSQLSelectStatement(DatabaseCall call, ExpressionSQLPrinter printer, SQLSelectStatement statement) {
        int max = 0;
        if (statement.getQuery() != null) {
            max = statement.getQuery().getMaxRows();
        }
        if (max <= 0  || !(this.shouldUseRownumFiltering())) {
            super.printSQLSelectStatement(call, printer, statement);
            return;
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

    /**
     * INTERNAL:
     * Use the JDBC maxResults and firstResultIndex setting to compute a value to use when
     * limiting the results of a query in SQL.  These limits tend to be used in two ways.
     * <p>
     * 1. MaxRows is the index of the last row to be returned (like JDBC maxResults)
     * 2. MaxRows is the number of rows to be returned
     * <p>
     * H2 uses case #2 and therefore the maxResults has to be altered based on the firstResultIndex.
     */
    @Override
    public int computeMaxRowsForSQL(int firstResultIndex, int maxResults){
        return maxResults - ((firstResultIndex >= 0) ? firstResultIndex : 0);
    }

    @Override
    protected Map<Class<?>, FieldDefinition.DatabaseType> buildDatabaseTypes() {
        Map<Class<?>, FieldDefinition.DatabaseType> fieldTypeMapping = super.buildDatabaseTypes();
        fieldTypeMapping.put(Boolean.class, new FieldDefinition.DatabaseType("BOOLEAN", false));

        fieldTypeMapping.put(Integer.class, new FieldDefinition.DatabaseType("INTEGER", false));
        fieldTypeMapping.put(Long.class, new FieldDefinition.DatabaseType("BIGINT", false));
        fieldTypeMapping.put(Float.class, new FieldDefinition.DatabaseType("DOUBLE", false));
        fieldTypeMapping.put(Double.class, new FieldDefinition.DatabaseType("DOUBLE", false));
        fieldTypeMapping.put(Short.class, new FieldDefinition.DatabaseType("SMALLINT", false));
        fieldTypeMapping.put(Byte.class, new FieldDefinition.DatabaseType("SMALLINT", false));
        fieldTypeMapping.put(BigInteger.class, TYPE_NUMERIC.ofSize(38));
        fieldTypeMapping.put(BigDecimal.class, new FieldDefinition.DatabaseType("NUMERIC", 38, 0, 38, -19, 19));
        fieldTypeMapping.put(Number.class, new FieldDefinition.DatabaseType("NUMERIC", 38, 0, 38, -19, 19));
        fieldTypeMapping.put(Byte[].class, new FieldDefinition.DatabaseType("LONGVARBINARY", false));
        fieldTypeMapping.put(Character[].class, new FieldDefinition.DatabaseType("LONGVARCHAR", false));
        fieldTypeMapping.put(byte[].class, new FieldDefinition.DatabaseType("LONGVARBINARY", false));
        fieldTypeMapping.put(char[].class, new FieldDefinition.DatabaseType("LONGVARCHAR", false));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldDefinition.DatabaseType("BLOB", false));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldDefinition.DatabaseType("CLOB", false));

        fieldTypeMapping.put(java.sql.Date.class, new FieldDefinition.DatabaseType("DATE", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldDefinition.DatabaseType("TIMESTAMP", true));
        fieldTypeMapping.put(java.sql.Time.class, new FieldDefinition.DatabaseType("TIME", true));
        fieldTypeMapping.put(java.util.Calendar.class, new FieldDefinition.DatabaseType("TIMESTAMP", true));
        fieldTypeMapping.put(java.util.Date.class, new FieldDefinition.DatabaseType("TIMESTAMP", true));
        fieldTypeMapping.put(java.util.UUID.class, new FieldDefinition.DatabaseType("BINARY", 16));

        return fieldTypeMapping;
    }

    @Override
    public boolean supportsFractionalTime() {
        return true;
    }

    @Override
    public void printFieldTypeSize(Writer writer, FieldDefinition field,
                                   FieldDefinition.DatabaseType databaseType) throws IOException {
        // Metadata distinguishes an explicit zero from an unspecified precision.
        // Unspecified timestamps use H2's maximum precision; TIME retains its default zero.
        DatabaseField databaseField = field.getDatabaseField();
        Class<?> type = field.getType();
        if (databaseField == null && (type == java.sql.Time.class || type == java.sql.Timestamp.class
                || type == java.util.Date.class || type == java.util.Calendar.class)) {
            // Legacy manual definitions ignored sizes before metadata precision was supported.
            writer.write(databaseType.name());
            return;
        }
        if (databaseField != null && ("TIME".equals(databaseType.name()) || "TIMESTAMP".equals(databaseType.name()))) {
            int precision = databaseField.getSecondPrecision();
            if (precision == 0 || (precision < 0 && "TIMESTAMP".equals(databaseType.name()) && field.getSize() == 0)) {
                writer.write(databaseType.name());
                writer.write(precision == 0 ? "(0)" : "(9)");
                return;
            }
        }
        super.printFieldTypeSize(writer, field, databaseType);
    }

    @Override
    public boolean isAlterSequenceObjectSupported() {
        return true;
    }

    /**
     * INTERNAL
     * H2 has some issues with using parameters on certain functions and relations.
     * This allows statements to disable binding only in these cases.
     */
    @Override
    public boolean isDynamicSQLRequiredForFunctions() {
        return true;
    }

    @Override
    public boolean allowBindingForSelectClause() {
        return false;
    }

    @Override
    public ValueReadQuery buildSelectQueryForSequenceObject(String seqName, Integer size) {
        return new ValueReadQuery(new StringBuilder(20 + seqName.length()).append("CALL NEXT VALUE FOR ").append(seqName).toString());
    }

    @Override
    public boolean supportsIdentity() {
        return true;
    }

    @Override
    public boolean supportsSequenceObjects() {
        return true;
    }

    @Override
    public ValueReadQuery buildSelectQueryForIdentity() {
        return new ValueReadQuery("CALL IDENTITY()");
    }

    @Override
    public void printFieldIdentityClause(Writer writer) throws ValidationException {
        try {
            writer.append(" GENERATED BY DEFAULT AS IDENTITY");
        } catch (IOException e) {
            throw ValidationException.logIOError(e);
        }
    }

    @Override
    public boolean supportsForeignKeyConstraints() {
        return true;
    }

    @Override
    public boolean supportsGlobalTempTables() {
        return true;
    }

    @Override
    protected String getCreateTempTableSqlPrefix() {
        return "CREATE TEMPORARY TABLE IF NOT EXISTS ";
    }

    /**
     * H2 does not allow using () in the update if only one field.
     */
    @Override
    public void writeUpdateOriginalFromTempTableSql(Writer writer, DatabaseTable table,
                                                    Collection<DatabaseField> pkFields,
                                                    Collection<DatabaseField> assignedFields) throws IOException
    {
        writer.write("UPDATE ");
        String tableName = table.getQualifiedNameDelimited(this);
        writer.write(tableName);
        writer.write(" SET ");
        if (assignedFields.size() > 1) {
            writer.write("(");
        }
        writeFieldsList(writer, assignedFields, this);
        if (assignedFields.size() > 1) {
            writer.write(")");
        }
        writer.write(" = (SELECT ");
        writeFieldsList(writer, assignedFields, this);
        writer.write(" FROM ");
        String tempTableName = getTempTableForTable(table).getQualifiedNameDelimited(this);
        writer.write(tempTableName);
        writeAutoJoinWhereClause(writer, null, tableName, pkFields, this);
        writer.write(") WHERE EXISTS(SELECT ");
        writer.write(pkFields.iterator().next().getNameDelimited(this));
        writer.write(" FROM ");
        writer.write(tempTableName);
        writeAutoJoinWhereClause(writer, null, tableName, pkFields, this);
        writer.write(")");
    }

    @Override
    public boolean supportsStoredFunctions() {
        return true;
    }

    @Override
    public ValueReadQuery getTimestampQuery() {
        if (timestampQuery == null) {
            timestampQuery = new ValueReadQuery();
            timestampQuery.setSQLString("SELECT CURRENT_TIMESTAMP()");
            timestampQuery.setAllowNativeSQLQuery(true);
        }
        return timestampQuery;
    }

    @Override
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        addOperator(ExpressionOperator.simpleMath(ExpressionOperator.Concat, "||"));
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.Ceil, "CEILING"));
        addOperator(ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Nvl, "IFNULL"));
        addOperator(toNumberOperator());
        addOperator(monthsBetweenOperator());
        addOperator(h2ExtractOperator());
    }

    /**
     * INTERNAL:
     * Use CONVERT function for toNumber.
     */
    public static ExpressionOperator toNumberOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.ToNumber);
        List<String> v = new ArrayList<>(2);
        v.add("CONVERT(");
        v.add(",DECIMAL)");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * INTERNAL:
     * Use MONTH function for MONTH_BETWEEN.
     */
    public static ExpressionOperator monthsBetweenOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.MonthsBetween);
        List<String> v = new ArrayList<>(2);
        v.add("(MONTH(");
        v.add(") - MONTH(");
        v.add("))");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Help to generate SQL like WHERE (REDUCED = TRUE) instead of
     * WHERE (REDUCED = 1), which is compatible with H2's strict type system
     * @param bool
     * @param writer
     * @throws IOException
     */
    @Override
    protected void appendBoolean(Boolean bool, Writer writer) throws IOException {
        if (bool) {
            writer.write("TRUE");
        } else {
            writer.write("FALSE");
        }
    }

    /** DISTINCT result rows cannot carry a FOR UPDATE locking clause. */
    @Override
    public boolean isForUpdateCompatibleWithDistinct() {
        return false;
    }

    @Override
    public boolean isH2() {
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

    @Override
    public String buildProcedureCallString(StoredProcedureCall call, AbstractSession session, AbstractRecord row) {
        String sql = super.buildProcedureCallString(call, session, row);
        if (call.isStoredFunctionCall()) {
            return "{? = CALL " + sql.substring(getFunctionCallHeader().length()) + "}";
        }
        return sql;
    }

    /**
     * INTERNAL
     * Set the parameter in the JDBC statement at the given index.
     * Bind {@link LocalTime} as a JDBC 4.2 TIME value, preserving nanoseconds.
     * The base-class TIMESTAMP binding has a date component that prevents TIME
     * comparisons from matching; conversion through java.sql.Time loses fractions.
     */
    @Override
    public void setParameterValueInDatabaseCall(Object parameter,
                                                PreparedStatement statement, int index, AbstractSession session)
            throws SQLException {
        if (parameter instanceof LocalTime) {
            statement.setObject(index, parameter);
            return;
        }
        super.setParameterValueInDatabaseCall(parameter, statement, index, session);
    }

    /**
     * INTERNAL
     * Set the parameter in the JDBC callable statement with the given name.
     * Same LocalTime fix as the indexed variant above.
     */
    @Override
    public void setParameterValueInDatabaseCall(Object parameter,
                                                CallableStatement statement, String name, AbstractSession session)
            throws SQLException {
        if (parameter instanceof LocalTime) {
            statement.setObject(name, parameter);
            return;
        }
        super.setParameterValueInDatabaseCall(parameter, statement, name, session);
    }

    @Override
    protected void printTemporalTypeContext(Expression value, Class<?> type, ExpressionSQLPrinter printer) {
        if (type == LocalTime.class) {
            printExpressionCast(value, "TIME(9)", printer);
        } else if (type == java.time.LocalDateTime.class) {
            printExpressionCast(value, "TIMESTAMP(9)", printer);
        } else {
            super.printTemporalTypeContext(value, type, printer);
        }
    }

    // H2 does not accept EXTRACT(DATE FROM ...) or EXTRACT(TIME FROM ...) because
    // DATE and TIME are not valid single-field datetime specifiers in H2's EXTRACT().
    // We emulate them with CAST(col AS DATE) and CAST(col AS TIME(9)) instead.
    // Explicit TIME precision preserves the fractional seconds of the input value.
    private static final class H2ExtractOperator extends ExtractOperator {

        // DATE emulation: CAST(:first AS DATE)
        private static final String[] DATE_STRINGS = new String[] {"CAST(", " AS DATE)"};
        // TIME emulation: CAST(:first AS TIME(9))
        private static final String[] TIME_STRINGS = new String[] {"CAST(", " AS TIME(9))"};

        private H2ExtractOperator() {
            super();
        }

        @Override
        protected void printWeekSQL(final Expression first, Expression second, final ExpressionSQLPrinter printer) {
            printer.printString("EXTRACT(ISO_WEEK FROM ");
            first.printSQL(printer);
            printer.printString(")");
        }

        @Override
        protected void printWeekJava(final Expression first, Expression second, final ExpressionJavaPrinter printer) {
            printer.printString("EXTRACT(ISO_WEEK FROM ");
            first.printJava(printer);
            printer.printString(")");
        }

        @Override
        protected void printSecondSQL(final Expression first, Expression second, final ExpressionSQLPrinter printer) {
            printer.printString("(EXTRACT(SECOND FROM ");
            first.printSQL(printer);
            printer.printString(") + EXTRACT(NANOSECOND FROM ");
            first.printSQL(printer);
            printer.printString(") / 1000000000.0)");
        }

        @Override
        protected void printSecondJava(final Expression first, Expression second, final ExpressionJavaPrinter printer) {
            printer.printString("(EXTRACT(SECOND FROM ");
            first.printJava(printer);
            printer.printString(") + EXTRACT(NANOSECOND FROM ");
            first.printJava(printer);
            printer.printString(") / 1000000000.0)");
        }

        @Override
        protected void printDateSQL(final Expression first, Expression second, final ExpressionSQLPrinter printer) {
            printer.printString(DATE_STRINGS[0]);
            first.printSQL(printer);
            printer.printString(DATE_STRINGS[1]);
        }

        @Override
        protected void printDateJava(final Expression first, Expression second, final ExpressionJavaPrinter printer) {
            printer.printString(DATE_STRINGS[0]);
            first.printJava(printer);
            printer.printString(DATE_STRINGS[1]);
        }

        @Override
        protected void printTimeSQL(final Expression first, Expression second, final ExpressionSQLPrinter printer) {
            printer.printString(TIME_STRINGS[0]);
            first.printSQL(printer);
            printer.printString(TIME_STRINGS[1]);
        }

        @Override
        protected void printTimeJava(final Expression first, Expression second, final ExpressionJavaPrinter printer) {
            printer.printString(TIME_STRINGS[0]);
            first.printJava(printer);
            printer.printString(TIME_STRINGS[1]);
        }
    }

    private static ExpressionOperator h2ExtractOperator() {
        return new H2ExtractOperator();
    }
}
