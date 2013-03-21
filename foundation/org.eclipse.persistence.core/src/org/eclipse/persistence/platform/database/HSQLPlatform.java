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
 *     James Sutherland - Update to HSQL 1.8.1 and LRG
 *     11/04/2010-2.2 Michael O'Brien 
 *       - 319592: HSQL 2.0.0 requires VARCHAR length specified
 *     09/14/2011-2.3.1 Guy Pelletier 
 *       - 357533: Allow DDL queries to execute even when Multitenant entities are part of the PU
 ******************************************************************************/  
package org.eclipse.persistence.platform.database;

import java.io.StringWriter;
import java.io.Writer;
import java.io.IOException;
import java.util.*;

import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.queries.ValueReadQuery;

/**
 * <p><b>Purpose</b>: Provides HSQL specific behavior.
 * 
 * Support HSQL functionality as of 1.8.1.
 * <p>Includes:
 * <ul>
 * <li>DDL creation
 * <li>IDENTITY sequencing
 * <li>SEQUENCE objects
 * <li>Functions
 * <li>Pagination
 *
 * @since TOPLink/Java 4.5
 */
public class HSQLPlatform extends DatabasePlatform {
    public HSQLPlatform() {
        super();
        setPingSQL("CALL 1");
    }

    @Override
    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypeMapping;

        fieldTypeMapping = super.buildFieldTypes();
        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("BOOLEAN", false));

        fieldTypeMapping.put(Integer.class, new FieldTypeDefinition("INTEGER", false));
        fieldTypeMapping.put(Long.class, new FieldTypeDefinition("BIGINT", false));
        fieldTypeMapping.put(Float.class, new FieldTypeDefinition("REAL", false));
        fieldTypeMapping.put(Double.class, new FieldTypeDefinition("REAL", false));
        fieldTypeMapping.put(Short.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(Byte.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(java.math.BigInteger.class, new FieldTypeDefinition("BIGINT", false));
        fieldTypeMapping.put(java.math.BigDecimal.class, new FieldTypeDefinition("NUMERIC", 38).setLimits(38, -19, 19));
        fieldTypeMapping.put(Number.class, new FieldTypeDefinition("NUMERIC", 38).setLimits(38, -19, 19));
        fieldTypeMapping.put(Byte[].class, new FieldTypeDefinition("LONGVARBINARY", false));
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("LONGVARCHAR", false));
        fieldTypeMapping.put(byte[].class, new FieldTypeDefinition("LONGVARBINARY", false));
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("LONGVARCHAR", false));
        // 319592: HSQL 2.0.0 requires VARCHAR length specified
        fieldTypeMapping.put(String.class, new FieldTypeDefinition("VARCHAR",DEFAULT_VARCHAR_SIZE));
        
        fieldTypeMapping.put(java.sql.Blob.class, new FieldTypeDefinition("LONGVARBINARY", false));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldTypeDefinition("LONGVARCHAR", false));
        
        fieldTypeMapping.put(java.sql.Date.class, new FieldTypeDefinition("DATE", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldTypeDefinition("TIMESTAMP", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldTypeDefinition("TIME", false));
        fieldTypeMapping.put(java.util.Calendar.class, new FieldTypeDefinition("TIMESTAMP", false));
        fieldTypeMapping.put(java.util.Date.class, new FieldTypeDefinition("TIMESTAMP", false));

        return fieldTypeMapping;
    }

    @Override
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.Ceil, "CEILING"));
        addOperator(rightTrim2());
        addOperator(greatest());
        addOperator(ExpressionOperator.simpleAggregate(ExpressionOperator.StandardDeviation, "STDDEV_POP", "standardDeviation"));
        addOperator(ExpressionOperator.simpleAggregate(ExpressionOperator.Variance, "VAR_POP", "variance"));
        addOperator(toNumberOperator());
        addOperator(trimOperator());
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
        v.addElement("CONVERT(");
        v.addElement(",DECIMAL)");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }
    
    /**
     * INTERNAL:
     * Use TRIM(FROM ?) function for trim.
     */
    public static ExpressionOperator trimOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.Trim);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        v.addElement("TRIM(FROM ");
        v.addElement(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * INTERNAL:
     * Build HSQL equivalent to RTRIM(string_exp, character).
     * HSQL: TRIM(TRAILING character FROM string_exp)
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
     * Build HSQL equivalent to GREATEST(x, y)
     * HSQL: CASE WHEN x >= y THEN x ELSE y
     */
    protected ExpressionOperator greatest() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.Greatest);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(5);
        v.addElement("CASEWHEN(");
        v.addElement(" >= ");
        v.addElement(", ");
        v.addElement(", ");
        v.addElement(")");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        int[] indices = {0, 1, 0, 1};
        exOperator.setArgumentIndices(indices);
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }
    
    @Override
    public boolean isHSQL() {
        return true;
    }

    /**
     * HSQL (1.8.1) does not support the UNIQUE key word in a column, but does support unique constraints defined separately.
     * This allows the column setting to be generated as a constraint.
     */
    @Override
    public boolean supportsUniqueColumns() {
        return false;
    }

    @Override
    public boolean supportsIdentity() {
        return true;
    }

    @Override
    public ValueReadQuery buildSelectQueryForIdentity() {
        return new ValueReadQuery("CALL IDENTITY()");
    }

    @Override
    public void printFieldIdentityClause(Writer writer) throws ValidationException {
        try {
            writer.write(" GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1)");
        } catch (IOException ex) {
            throw ValidationException.fileError(ex);
        }
    }

    /**
     * INTERNAL
     * HSQL has some issues with using parameters on certain functions and relations.
     * This allows statements to disable binding only in these cases.
     */
    @Override
    public boolean isDynamicSQLRequiredForFunctions() {
        return true;
    }
    
    /**
     * JDBC escape syntax for outer joins is not supported (not required).
     */
    @Override
    public boolean shouldUseJDBCOuterJoinSyntax() {
        return false;
    }
    
    /**
     * Does not allow nesting outer joins, i.e. each join must be followed by the ON clause.
     */
    @Override
    public boolean supportsNestingOuterJoins() {
        return false;
    }

    @Override
    public boolean supportsSequenceObjects() {
        return true;
    }

    @Override
    public boolean isAlterSequenceObjectSupported() {
        return true;
    }

    @Override
    public ValueReadQuery buildSelectQueryForSequenceObject(String seqName, Integer size) {
        return new ValueReadQuery(new StringBuilder(20 + seqName.length()).append("CALL NEXT VALUE FOR ").append(seqName).toString());
    }
    
    @Override
    public boolean supportsGlobalTempTables() {
        return true;
    }

    @Override
    protected String getCreateTempTableSqlPrefix() {
        return "CREATE TEMPORARY TABLE ";
    }


    /**
     * INTERNAL:
     * HSQL does not allow multiple fields to be set as a list, so each field needs to be set one by one.
     */
     public void writeUpdateOriginalFromTempTableSql(Writer writer, DatabaseTable table,
                                                     Collection pkFields,
                                                     Collection assignedFields) throws IOException 
    {
        writer.write("UPDATE ");
        String tableName = table.getQualifiedNameDelimited(this);
        writer.write(tableName);
        writer.write(" SET ");
        
        String tempTableName = getTempTableForTable(table).getQualifiedNameDelimited(this);
        boolean isFirst = true;
        Iterator itFields = assignedFields.iterator();
        while(itFields.hasNext()) {
            if(isFirst) {
                isFirst = false;
            } else {
                writer.write(", ");
            }
            DatabaseField field = (DatabaseField)itFields.next();
            String fieldName = field.getNameDelimited(this);
            writer.write(fieldName);
            writer.write(" = (SELECT ");
            writer.write(fieldName);
            writer.write(" FROM ");
            writer.write(tempTableName);
            writeAutoJoinWhereClause(writer, null, tableName, pkFields, this);
            writer.write(")");
        }
        
        writer.write(" WHERE EXISTS(SELECT ");
        writer.write(((DatabaseField)pkFields.iterator().next()).getNameDelimited(this));
        writer.write(" FROM ");
        writer.write(tempTableName);
        writeAutoJoinWhereClause(writer, null, tableName, pkFields, this);
        writer.write(")");
    }

    /**
     * INTERNAL:
     * Use the JDBC maxResults and firstResultIndex setting to compute a value to use when
     * limiting the results of a query in SQL.  These limits tend to be used in two ways.
     * 
     * 1. MaxRows is the index of the last row to be returned (like JDBC maxResults)
     * 2. MaxRows is the number of rows to be returned
     * 
     * HSQL uses case #2 and therefore the maxResults has to be altered based on the firstResultIndex.
     */
    @Override
    public int computeMaxRowsForSQL(int firstResultIndex, int maxResults){
        return maxResults - ((firstResultIndex >= 0) ? firstResultIndex : 0);
    }

    /**
     * Print the pagination SQL using HSQL syntax "SELECT LIMIT <first> <max>".
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
        printer.printString("SELECT LIMIT ");
        printer.printParameter(DatabaseCall.FIRSTRESULT_FIELD);
        printer.printString(" ");
        printer.printParameter(DatabaseCall.MAXROW_FIELD);
        Writer writer = printer.getWriter();
        // Need to trim the SELECT from the SQL.
        printer.setWriter(new StringWriter());
        call.setFields(statement.printSQL(printer));
        String sql = printer.getWriter().toString();
        printer.setWriter(writer);
        printer.printString(sql.substring(6, sql.length()));
        call.setIgnoreFirstRowSetting(true);
        call.setIgnoreMaxResultsSetting(true);
    }

    @Override
    public ValueReadQuery getTimestampQuery() {
        if (this.timestampQuery == null) {
            this.timestampQuery = new ValueReadQuery();
            this.timestampQuery.setSQLString("CALL CURRENT_TIMESTAMP");
            this.timestampQuery.setAllowNativeSQLQuery(true);
        }
        return this.timestampQuery;
    }
    
    /**
     * INTERNAL:
     * HSQL requires START WITH first.
     */
    @Override
    public Writer buildSequenceObjectCreationWriter(Writer writer, String fullSeqName, int increment, int start) throws IOException {
        writer.write("CREATE SEQUENCE ");
        writer.write(fullSeqName);
        writer.write(" START WITH " + start);
        if (increment != 1) {
            writer.write(" INCREMENT BY " + increment);
        }
        return writer;
    }
}
