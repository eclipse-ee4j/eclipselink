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
 *     Mike Keith
 * 
 * Patterned after:
 *   org.eclipse.persistence.platform.database.DB2MainframePlatform      
 ******************************************************************************/
package org.eclipse.persistence.platform.database;

import java.io.*;
import java.util.*;

import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.platform.database.HSQLPlatform;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.NativeSequence;

public class H2Platform extends HSQLPlatform {
    private static final long serialVersionUID = -2935483687958482934L;

    public H2Platform() {
        super();
        setPingSQL("SELECT 1");
        setSupportsAutoCommit(true);
    }

    @Override
    public final boolean isHSQL() {
        return false;
    }

    /**
     * Print the pagination SQL using H2 syntax " LIMIT <max> OFFSET <first>".
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
        call.setIgnoreFirstRowMaxResultsSettings(true);
    }
    
    
    /**
     * INTERNAL:
     * Use the JDBC maxResults and firstResultIndex setting to compute a value to use when
     * limiting the results of a query in SQL.  These limits tend to be used in two ways.
     * 
     * 1. MaxRows is the index of the last row to be returned (like JDBC maxResults)
     * 2. MaxRows is the number of rows to be returned
     * 
     * H2 uses case #2 and therefore the maxResults has to be altered based on the firstResultIndex.
     */
    @Override
    public int computeMaxRowsForSQL(int firstResultIndex, int maxResults){
        return maxResults - ((firstResultIndex >= 0) ? firstResultIndex : 0);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypeMapping = super.buildFieldTypes();
        fieldTypeMapping.put(java.sql.Date.class, new FieldTypeDefinition("DATE", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldTypeDefinition("TIME", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldTypeDefinition("TIMESTAMP", false));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldTypeDefinition("BLOB", false));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldTypeDefinition("CLOB", false));
        fieldTypeMapping.put(Float.class, new FieldTypeDefinition("DOUBLE", false));
        fieldTypeMapping.put(Double.class, new FieldTypeDefinition("DOUBLE", false));
        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("BOOLEAN", false));
        return fieldTypeMapping;
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
    protected Sequence createPlatformDefaultSequence() {
        return new NativeSequence();
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
            writer.append(" IDENTITY");
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
                                                     Collection pkFields,
                                                     Collection assignedFields) throws IOException 
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
        writer.write(((DatabaseField)pkFields.iterator().next()).getNameDelimited(this));
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
        return new ValueReadQuery("SELECT CURRENT_TIMESTAMP()");
    }

    @Override
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        addOperator(ExpressionOperator.simpleMath(ExpressionOperator.Concat, "||"));
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.Ceil, "CEILING"));
        addOperator(ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Nvl, "IFNULL"));
        addOperator(toNumberOperator());
        addOperator(monthsBetweenOperator());
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

    /**
     * INTERNAL
     * H2 has some issue with un-typed parameters, this allows a workaround to these issues using literals.
     */
    public boolean shouldBindLiterals() {
        return false;
    }

    public boolean isH2() {
        return true;
    }
}