/*
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022 IBM Corporation. All rights reserved.
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
//     James Sutherland - initial impl
//     09/14/2011-2.3.1 Guy Pelletier
//       - 357533: Allow DDL queries to execute even when Multitenant entities are part of the PU
package org.eclipse.persistence.platform.database;

import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirebirdPlatform extends DatabasePlatform {

    public FirebirdPlatform() {
        super();
        setPingSQL("SELECT 1 FROM RDB$DATABASE");
    }

    @Override
    protected Map<Class<?>, FieldDefinition.DatabaseType> buildDatabaseTypes() {
        Map<Class<?>, FieldDefinition.DatabaseType> fieldTypeMapping = super.buildDatabaseTypes();
        fieldTypeMapping.put(Boolean.class, new FieldDefinition.DatabaseType("SMALLINT", false));

        fieldTypeMapping.put(Integer.class, new FieldDefinition.DatabaseType("INTEGER", false));
        fieldTypeMapping.put(Long.class, new FieldDefinition.DatabaseType("NUMERIC", 18, 0, 18, -18, 18));
        fieldTypeMapping.put(Float.class, new FieldDefinition.DatabaseType("FLOAT", false));
        fieldTypeMapping.put(Double.class, new FieldDefinition.DatabaseType("DOUBLE PRECISION", false));
        fieldTypeMapping.put(Short.class, new FieldDefinition.DatabaseType("SMALLINT", false));
        fieldTypeMapping.put(Byte.class, new FieldDefinition.DatabaseType("SMALLINT", false));
        fieldTypeMapping.put(BigInteger.class, new FieldDefinition.DatabaseType("NUMERIC", 18, 0, 18, -18, 18));
        fieldTypeMapping.put(BigDecimal.class, new FieldDefinition.DatabaseType("NUMERIC", 18, 0, 18, -18, 18));
        fieldTypeMapping.put(Number.class, new FieldDefinition.DatabaseType("NUMERIC", 38, 0, 18, -18, 18));

        fieldTypeMapping.put(String.class, new FieldDefinition.DatabaseType("VARCHAR", DEFAULT_VARCHAR_SIZE));
        fieldTypeMapping.put(Character.class, new FieldDefinition.DatabaseType("VARCHAR", 1));

        fieldTypeMapping.put(Byte[].class, TYPE_BLOB);
        fieldTypeMapping.put(Character[].class, new FieldDefinition.DatabaseType("VARCHAR", 32000));
        fieldTypeMapping.put(byte[].class, TYPE_BLOB);
        fieldTypeMapping.put(char[].class, new FieldDefinition.DatabaseType("VARCHAR", 32000));
        fieldTypeMapping.put(java.sql.Blob.class, TYPE_BLOB);
        fieldTypeMapping.put(java.sql.Clob.class, new FieldDefinition.DatabaseType("VARCHAR", 32000));

        fieldTypeMapping.put(java.sql.Date.class, new FieldDefinition.DatabaseType("DATE", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldDefinition.DatabaseType("TIMESTAMP", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldDefinition.DatabaseType("TIME", false));
        fieldTypeMapping.put(java.util.Calendar.class, new FieldDefinition.DatabaseType("TIMESTAMP", false));
        fieldTypeMapping.put(java.util.Date.class, new FieldDefinition.DatabaseType("TIMESTAMP", false));

        return fieldTypeMapping;
    }

    /**
     * INTERNAL: returns the maximum number of characters that can be used in a
     * foreign key name on this platform.
     */
    @Override
    public int getMaxForeignKeyNameSize() {
        return 24;
    }

    /**
     * INTERNAL:
     * returns the maximum number of characters that can be used in a unique key
     * name on this platform.
     */
    @Override
    public int getMaxUniqueKeyNameSize() {
        return 24;
    }

    @Override
    public ValueReadQuery getTimestampQuery() {
        if (this.timestampQuery == null) {
            this.timestampQuery = new ValueReadQuery("SELECT CURRENT_TIMESTAMP FROM RDB$DATABASE");
            this.timestampQuery.setAllowNativeSQLQuery(true);
        }
        return this.timestampQuery;
    }

    @Override
    public boolean isAlterSequenceObjectSupported() {
        return false;
    }

    /**
     * INTERNAL:
     * Returns sql used to create sequence object in the database.
     */
    @Override
    public Writer buildSequenceObjectCreationWriter(Writer writer, String fullSeqName, int increment, int start) throws IOException {
        writer.write("CREATE GENERATOR ");
        writer.write(fullSeqName);
        return writer;
    }

    /**
     * INTERNAL:
     * Returns sql used to delete sequence object from the database.
     */
    @Override
    public Writer buildSequenceObjectDeletionWriter(Writer writer, String fullSeqName) throws IOException {
        writer.write("DROP GENERATOR ");
        writer.write(fullSeqName);
        return writer;
    }

    @Override
    public ValueReadQuery buildSelectQueryForSequenceObject(String seqName, Integer size) {
        StringBuilder builder = new StringBuilder(26 + seqName.length());
        builder.append("SELECT GEN_ID(");
        builder.append(seqName);
        builder.append(", ");
        builder.append(size);
        builder.append(") FROM RDB$DATABASE");
        return new ValueReadQuery(builder.toString());
    }

    @Override
    public boolean supportsSequenceObjects() {
        return true;
    }

    @Override
    public boolean supportsForeignKeyConstraints() {
        return true;
    }

    @Override
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        addOperator(ExpressionOperator.simpleMath(ExpressionOperator.Concat, "||"));
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.Ceil, "CEILING"));
        addOperator(ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Nvl, "NULLIF"));
        addOperator(toNumberOperator());
        addOperator(monthsBetweenOperator());
        addOperator(greatest());
        addOperator(rightTrim2());
        addOperator(leftTrim());
        addOperator(rightTrim());
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.Length, "CHAR_LENGTH"));
        addOperator(substring());
        addOperator(substring2());
    }

    /**
     * INTERNAL:
     * Build FB equivalent to SUBSTR(x, y)
     * FB: SUBSTRING(x FROM y)
     */
    protected ExpressionOperator substring() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.SubstringSingleArg);
        List<String> v = new ArrayList<>(5);
        v.add("SUBSTRING(");
        v.add(" FROM ");
        v.add(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = {0, 1};
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * INTERNAL:
     * Build FB equivalent to SUBSTR(x, y, z)
     * FB: SUBSTRING(x FROM y FOR z)
     */
    protected ExpressionOperator substring2() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.Substring);
        List<String> v = new ArrayList<>(5);
        v.add("SUBSTRING(");
        v.add(" FROM ");
        v.add(" FOR ");
        v.add(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = {0, 1, 2};
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * INTERNAL:
     * Build FB equivalent to GREATEST(x, y)
     * FB: CASE WHEN x {@literal >=} y THEN x ELSE y END
     */
    protected ExpressionOperator greatest() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.Greatest);
        List<String> v = new ArrayList<>(5);
        v.add("(CASE WHEN ");
        v.add(" >= ");
        v.add(" THEN ");
        v.add(" ELSE ");
        v.add(" END)");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = {0, 1, 0, 1};
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * INTERNAL:
     * Build FB equivalent to LTRIM(string_exp).
     * FB: TRIM(LEADING FROM string_exp)
     */
    protected ExpressionOperator leftTrim() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.LeftTrim);
        List<String> v = new ArrayList<>(5);
        v.add("TRIM(LEADING FROM ");
        v.add(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = {0};
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * INTERNAL:
     * Build FB equivalent to LTRIM(string_exp).
     * FB: TRIM(LEADING FROM string_exp)
     */
    protected ExpressionOperator rightTrim() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.RightTrim);
        List<String> v = new ArrayList<>(5);
        v.add("TRIM(TRAILING FROM ");
        v.add(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = {0};
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * INTERNAL:
     * Build FB equivalent to RTRIM(string_exp, character).
     * FB: TRIM(TRAILING character FROM string_exp)
     */
    protected ExpressionOperator rightTrim2() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.RightTrim2);
        List<String> v = new ArrayList<>(5);
        v.add("TRIM(TRAILING ");
        v.add(" FROM ");
        v.add(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = {1, 0};
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
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
        v.add("CAST(");
        v.add(" AS NUMERIC)");
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

    @Override
    public boolean isFirebird() {
        return true;
    }

    /**
     * Print the pagination SQL using FB syntax " ROWS {@literal <max> TO <first>}".
     */
    @Override
    public void printSQLSelectStatement(DatabaseCall call, ExpressionSQLPrinter printer, SQLSelectStatement statement) {
        int max = 0;
        int firstRow = 0;
        if (statement.getQuery() != null) {
            max = statement.getQuery().getMaxRows();
            firstRow = statement.getQuery().getFirstResult();
        }
        // Both must be set, otherwise if one is set next, the old cached SQL would be used.
        if (!shouldUseRownumFiltering() || (max <= 0) || (firstRow <= 0)) {
            super.printSQLSelectStatement(call, printer, statement);
            return;
        }
        statement.setUseUniqueFieldAliases(true);
        call.setFields(statement.printSQL(printer));
        printer.printString(" ROWS (");
        printer.printParameter(DatabaseCall.FIRSTRESULT_FIELD);
        printer.printString(" + 1) TO ");
        printer.printParameter(DatabaseCall.MAXROW_FIELD);
        call.setIgnoreFirstRowSetting(true);
        call.setIgnoreMaxResultsSetting(true);
    }

    /**
     * INTERNAL
     * Firebird has some issues with using parameters on certain functions and relations.
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

    /**
     * WITH LOCK is required on FB to hold the lock.
     */
    @Override
    public String getSelectForUpdateString() {
        return " WITH LOCK";
    }
}
