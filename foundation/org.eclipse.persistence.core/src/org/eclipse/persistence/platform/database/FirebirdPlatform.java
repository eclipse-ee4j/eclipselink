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
 *     James Sutherland - initial impl
 *     09/14/2011-2.3.1 Guy Pelletier 
 *       - 357533: Allow DDL queries to execute even when Multitenant entities are part of the PU
 ******************************************************************************/
package org.eclipse.persistence.platform.database;

import java.io.*;
import java.util.*;

import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.expressions.ExpressionOperator;

public class FirebirdPlatform extends DatabasePlatform {

    public FirebirdPlatform() {
        super();
        setPingSQL("SELECT 1 FROM RDB$DATABASE");
    }
    
    @Override
    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypeMapping = super.buildFieldTypes();
        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("SMALLINT", false));

        fieldTypeMapping.put(Integer.class, new FieldTypeDefinition("INTEGER", false));
        fieldTypeMapping.put(Long.class, new FieldTypeDefinition("NUMERIC", 18).setLimits(18, -18, 18));
        fieldTypeMapping.put(Float.class, new FieldTypeDefinition("FLOAT", false));
        fieldTypeMapping.put(Double.class, new FieldTypeDefinition("DOUBLE PRECISION", false));
        fieldTypeMapping.put(Short.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(Byte.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(java.math.BigInteger.class, new FieldTypeDefinition("NUMERIC", 18).setLimits(18, -18, 18));
        fieldTypeMapping.put(java.math.BigDecimal.class, new FieldTypeDefinition("NUMERIC", 18).setLimits(18, -18, 18));
        fieldTypeMapping.put(Number.class, new FieldTypeDefinition("NUMERIC", 38).setLimits(18, -18, 18));
        
        fieldTypeMapping.put(String.class, new FieldTypeDefinition("VARCHAR", DEFAULT_VARCHAR_SIZE));
        fieldTypeMapping.put(Character.class, new FieldTypeDefinition("VARCHAR", 1));
        
        fieldTypeMapping.put(Byte[].class, new FieldTypeDefinition("BLOB"));
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("VARCHAR", 32000));
        fieldTypeMapping.put(byte[].class, new FieldTypeDefinition("BLOB"));
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("VARCHAR", 32000));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldTypeDefinition("BLOB"));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldTypeDefinition("VARCHAR", 32000));
        
        fieldTypeMapping.put(java.sql.Date.class, new FieldTypeDefinition("DATE", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldTypeDefinition("TIMESTAMP", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldTypeDefinition("TIME", false));
        fieldTypeMapping.put(java.util.Calendar.class, new FieldTypeDefinition("TIMESTAMP", false));
        fieldTypeMapping.put(java.util.Date.class, new FieldTypeDefinition("TIMESTAMP", false));
        
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
    public Writer buildSequenceObjectCreationWriter(Writer writer, String fullSeqName, int increment, int start) throws IOException {
        writer.write("CREATE GENERATOR ");
        writer.write(fullSeqName);
        return writer;
    }
 
    /**
     * INTERNAL:
     * Returns sql used to delete sequence object from the database.
     */
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
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(5);
        v.addElement("SUBSTRING(");
        v.addElement(" FROM ");
        v.addElement(")");
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
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(5);
        v.addElement("SUBSTRING(");
        v.addElement(" FROM ");
        v.addElement(" FOR ");
        v.addElement(")");
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
     * FB: CASE WHEN x >= y THEN x ELSE y END
     */
    protected ExpressionOperator greatest() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.Greatest);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(5);
        v.addElement("(CASE WHEN ");
        v.addElement(" >= ");
        v.addElement(" THEN ");
        v.addElement(" ELSE ");
        v.addElement(" END)");
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
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(5);
        v.addElement("TRIM(LEADING FROM ");
        v.addElement(")");
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
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(5);
        v.addElement("TRIM(TRAILING FROM ");
        v.addElement(")");
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
     * Use CONVERT function for toNumber.
     */
    public static ExpressionOperator toNumberOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.ToNumber);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        v.addElement("CAST(");
        v.addElement(" AS NUMERIC)");
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
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        v.addElement("(MONTH(");
        v.addElement(") - MONTH(");
        v.addElement("))");
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
     * Print the pagination SQL using FB syntax " ROWS <max> TO <first>".
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


    /**
     * WITH LOCK is required on FB to hold the lock.
     */
    public String getSelectForUpdateString() {
        return " WITH LOCK";
    }
}