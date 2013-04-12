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
 *     Phillip Ross - LIMIT/OFFSET syntax support
 *     09/14/2011-2.3.1 Guy Pelletier 
 *       - 357533: Allow DDL queries to execute even when Multitenant entities are part of the PU
 ******************************************************************************/
package org.eclipse.persistence.platform.database;

import java.io.*;
import java.sql.Types;
import java.util.*;

import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.RelationExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

/**
 * <p>
 * <b>Purpose</b>: Provides Postgres specific behavior.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li>Native SQL for Date, Time, & Timestamp.
 * <li>Native sequencing.
 * <li>Mapping of class types to database types for the schema framework.
 * <li>Pessimistic locking.
 * <li>Platform specific operators.
 * <li>LIMIT/OFFSET query syntax for select statements.
 * </ul>
 * 
 * @since OracleAS TopLink 10<i>g</i> (10.1.3)
 */
public class PostgreSQLPlatform extends DatabasePlatform {

    private static final String LIMIT = " LIMIT ";
    private static final String OFFSET = " OFFSET ";

    public PostgreSQLPlatform() {
        super();
        this.cursorCode = 1111; //jdbc.Types.OTHER - PostGreSQL expects this for refCursor types
        this.pingSQL = "SELECT 1";
    }
    
    /**
     * Return the JDBC type for the Java type. For some reason PostgreSQL does
     * not seem to like the JDBC Blob type (PostgreSQL 8.2).
     */
    @Override
    public int getJDBCType(Class javaType) {
        if (javaType == ClassConstants.BLOB) {
            return Types.LONGVARBINARY;
        }
        return super.getJDBCType(javaType);
    }

    /**
     * Appends a Boolean value. Refer to :
     * http://www.postgresql.org/docs/8.0/static/datatype-boolean.html In
     * PostgreSQL the following are the values that are value for a boolean
     * field Valid literal values for the "true" state are: TRUE, 't', 'true',
     * 'y', 'yes', '1' Valid literal values for the false" state are : FALSE,
     * 'f', 'false', 'n', 'no', '0'
     * 
     * To be consistent with the other data platforms we are using the values
     * '1' and '0' for true and false states of a boolean field.
     */
    @Override
    protected void appendBoolean(Boolean bool, Writer writer) throws IOException {
        if (bool.booleanValue()) {
            writer.write("\'1\'");
        } else {
            writer.write("\'0\'");
        }
    }

    /**
     * INTERNAL: Use the JDBC maxResults and firstResultIndex setting to compute
     * a value to use when limiting the results of a query in SQL. These limits
     * tend to be used in two ways.
     * 
     * 1. MaxRows is the index of the last row to be returned (like JDBC
     * maxResults) 2. MaxRows is the number of rows to be returned
     * 
     * PostGreSQL uses case #2 and therefore the maxResults has to be altered
     * based on the firstResultIndex
     * 
     * @param readQuery
     * @param firstResultIndex
     * @param maxResults
     * 
     * @see org.eclipse.persistence.platform.database.MySQLPlatform
     */
    @Override
    public int computeMaxRowsForSQL(int firstResultIndex, int maxResults) {
        return maxResults - ((firstResultIndex >= 0) ? firstResultIndex : 0);
    }

    /**
     * INTERNAL: Initialize any platform-specific operators
     */
    @Override
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        addOperator(ExpressionOperator.simpleLogicalNoParens(ExpressionOperator.Concat, "||"));
        addOperator(ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Nvl, "COALESCE"));
        addOperator(operatorLocate());
        addOperator(operatorLocate2());
        addOperator(toNumberOperator());
        addOperator(regexpOperator());
    }

    /**
     * INTERNAL:
     * Create the ~ operator.
     * REGEXP allows for comparison through regular expression.
     */
    public static ExpressionOperator regexpOperator() {
        ExpressionOperator result = new ExpressionOperator();
        result.setSelector(ExpressionOperator.Regexp);
        result.setType(ExpressionOperator.FunctionOperator);
        Vector v = NonSynchronizedVector.newInstance(3);
        v.add("");
        v.add(" ~ ");
        v.add("");
        result.printsAs(v);
        result.bePrefix();
        result.setNodeClass(ClassConstants.FunctionExpression_Class);
        v = NonSynchronizedVector.newInstance(2);
        v.add(".regexp(");
        v.add(")");
        result.printsJavaAs(v);
        return result;
    }

    /**
     * INTERNAL: Postgres to_number has two arguments, as fix format argument.
     */
    protected ExpressionOperator toNumberOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.ToNumber);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        v.addElement("TO_NUMBER(");
        v.addElement(", '999999999.9999')");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * INTERNAL: This method returns the query to select the timestamp from the
     * server for Postgres.
     */
    @Override
    public ValueReadQuery getTimestampQuery() {
        if (this.timestampQuery == null) {
            this.timestampQuery = new ValueReadQuery();
            this.timestampQuery.setSQLString("SELECT NOW()");
            this.timestampQuery.setAllowNativeSQLQuery(true);
        }
        return this.timestampQuery;

    }

    /**
     * This method is used to print the output parameter token when stored
     * procedures are called
     */
    @Override
    public String getInOutputProcedureToken() {
        return "OUT";
    }

    /**
     * This is required in the construction of the stored procedures with output
     * parameters
     */
    @Override
    public boolean shouldPrintOutputTokenAtStart() {
        // TODO: Check with the reviewer where this is used
        return true;
    }

    /**
     * Calling a stored procedure query on PostgreSQL with no output parameters
     * always returns true from an execute call regardless if a result set is 
     * returned or not. This flag will help avoid throwing a JPA mandated 
     * exception on an executeUpdate call (which calls jdbc execute and checks
     * the return value to ensure no results sets are returned (true)) 
     * 
     * @see PostgreSQLPlatform
     */
    @Override
    public boolean isJDBCExecuteCompliant() {
        return false;
    }
    
    /**
     * INTERNAL: Answers whether platform is Postgres.
     */
    @Override
    public boolean isPostgreSQL() {
        return true;
    }

    /**
     * INTERNAL:
     */
    @Override
    protected String getCreateTempTableSqlSuffix() {
        return " ON COMMIT DROP";
    }

    /**
     * INTERNAL: Indicates whether locking OF clause should print alias for
     * field. Example: on Oracle platform (method returns false): SELECT
     * ADDRESS_ID, ... FROM ADDRESS T1 WHERE (T1.ADDRESS_ID = ?) FOR UPDATE OF
     * T1.ADDRESS_ID on Postgres platform (method returns true): SELECT
     * ADDRESS_ID, ... FROM ADDRESS T1 WHERE (T1.ADDRESS_ID = ?) FOR UPDATE OF
     * T1
     */
    @Override
    public boolean shouldPrintAliasForUpdate() {
        return true;
    }

    /**
     * INTERNAL: Indicates whether the platform supports identity.
     */
    @Override
    public boolean supportsIdentity() {
        return true;
    }

    /**
     * INTERNAL: Returns query used to read back the value generated by
     * Identity. This method is called when identity NativeSequence is
     * connected, the returned query used until the sequence is disconnected. If
     * the platform supportsIdentity then (at least) one of
     * buildSelectQueryForIdentity methods should return non-null query.
     */
    @Override
    public ValueReadQuery buildSelectQueryForIdentity() {
        ValueReadQuery selectQuery = new ValueReadQuery();
        selectQuery.setSQLString("select lastval()");
        return selectQuery;
    }

    /**
     * INTERNAL: Indicates whether the platform supports sequence objects.
     */
    @Override
    public boolean supportsSequenceObjects() {
        return true;
    }

    /**
     * INTERNAL: Returns query used to read value generated by sequence object
     * (like Oracle sequence). This method is called when sequence object
     * NativeSequence is connected, the returned query used until the sequence
     * is disconnected. If the platform supportsSequenceObjects then (at least)
     * one of buildSelectQueryForSequenceObject methods should return non-null
     * query.
     */
    @Override
    public ValueReadQuery buildSelectQueryForSequenceObject(String qualifiedSeqName, Integer size) {
        return new ValueReadQuery("select nextval(\'" + qualifiedSeqName + "\')");
    }

    /**
     * INTERNAL: Append the receiver's field 'identity' constraint clause to a
     * writer.
     */
    @Override
    public void printFieldIdentityClause(Writer writer) throws ValidationException {
        try {
            writer.write(" SERIAL");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    @Override
    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypeMapping = new Hashtable();

        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("BOOLEAN", false));

        fieldTypeMapping.put(Integer.class, new FieldTypeDefinition("INTEGER", false));
        fieldTypeMapping.put(Long.class, new FieldTypeDefinition("BIGINT", false));
        fieldTypeMapping.put(Float.class, new FieldTypeDefinition("FLOAT", false));
        fieldTypeMapping.put(Double.class, new FieldTypeDefinition("FLOAT", false));
        fieldTypeMapping.put(Short.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(Byte.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(java.math.BigInteger.class, new FieldTypeDefinition("BIGINT", false));
        fieldTypeMapping.put(java.math.BigDecimal.class, new FieldTypeDefinition("DECIMAL", 38));
        fieldTypeMapping.put(Number.class, new FieldTypeDefinition("DECIMAL", 38));

        fieldTypeMapping.put(String.class, new FieldTypeDefinition("VARCHAR", DEFAULT_VARCHAR_SIZE));
        fieldTypeMapping.put(Character.class, new FieldTypeDefinition("CHAR", 1));

        fieldTypeMapping.put(Byte[].class, new FieldTypeDefinition("BYTEA", false));
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("TEXT", false));
        fieldTypeMapping.put(byte[].class, new FieldTypeDefinition("BYTEA", false));
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("TEXT", false));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldTypeDefinition("BYTEA"));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldTypeDefinition("TEXT", false));

        fieldTypeMapping.put(java.sql.Date.class, new FieldTypeDefinition("DATE", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldTypeDefinition("TIME", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldTypeDefinition("TIMESTAMP", false));

        return fieldTypeMapping;
    }

    /**
     * INTERNAL: Override the default locate operator.
     */
    protected ExpressionOperator operatorLocate() {
        ExpressionOperator result = new ExpressionOperator();
        result.setSelector(ExpressionOperator.Locate);
        Vector v = new Vector(3);
        v.addElement("STRPOS(");
        v.addElement(", ");
        v.addElement(")");
        result.printsAs(v);
        result.bePrefix();
        result.setNodeClass(RelationExpression.class);
        return result;
    }

    /**
     * INTERNAL: Override the default locate operator.
     */
    protected ExpressionOperator operatorLocate2() {
        ExpressionOperator operator = new ExpressionOperator();
        operator.setSelector(ExpressionOperator.Locate2);
        Vector v = NonSynchronizedVector.newInstance(2);
        v.add("COALESCE(NULLIF(STRPOS(SUBSTRING(");
        v.add(" FROM ");
        v.add("), ");
        v.add("), 0) - 1 + ");
        v.add(", 0)");        
        operator.printsAs(v);
        operator.bePrefix();
        int[] argumentIndices = new int[4];
        argumentIndices[0] = 0;
        argumentIndices[1] = 2;
        argumentIndices[2] = 1;
        argumentIndices[3] = 2;
        operator.setArgumentIndices(argumentIndices);
        operator.setNodeClass(RelationExpression.class);
        return operator;
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean supportsLocalTempTables() {
        return true;
    }

    /**
     * INTERNAL:
     */
    @Override
    protected String getCreateTempTableSqlPrefix() {
        return "CREATE LOCAL TEMPORARY TABLE ";
    }

    /**
     * INTERNAL: returns the maximum number of characters that can be used in a
     * field name on this platform.
     */
    @Override
    public int getMaxFieldNameSize() {
        // The system uses no more than NAMEDATALEN-1 characters of an
        // identifier; longer names can be written in commands,
        // but they will be truncated. By default, NAMEDATALEN is 64 so the
        // maximum identifier length is 63 (but at the time PostgreSQL
        // is built, NAMEDATALEN can be changed in src/include/postgres_ext.h).
        // http://www.postgresql.org/docs/7.3/interactive/sql-syntax.html#SQL-SYNTAX-IDENTIFIERS
        return 63;
    }

    // http://www.postgresql.org/docs/8.1/interactive/plpgsql-declarations.html
    /**
     * INTERNAL: Used for sp calls.
     */
    @Override
    public String getProcedureBeginString() {
        return "AS $$  BEGIN ";
    }

    /**
     * INTERNAL: Used for sp calls.
     */
    @Override
    public String getProcedureEndString() {
        return "; END ; $$ LANGUAGE plpgsql;";
    }

     /**
     * INTERNAL: Used for sp calls.  PostGreSQL uses a different method for executing StoredProcedures than other platforms.
     */
    @Override
    public String buildProcedureCallString(StoredProcedureCall call, AbstractSession session, AbstractRecord row) {
        StringWriter tailWriter = new StringWriter();
        StringWriter writer = new StringWriter();
        boolean outParameterFound = false;;

        tailWriter.write(call.getProcedureName());
        tailWriter.write("(");

        int indexFirst = call.getFirstParameterIndexForCallString();
        int size = call.getParameters().size();
        String nextBindString = "?";

        for (int index = indexFirst; index < size; index++) {
             String name = call.getProcedureArgumentNames().get(index);
             Object parameter = call.getParameters().get(index);
             Integer parameterType = call.getParameterTypes().get(index);
             // If the argument is optional and null, ignore it.
             if (!call.hasOptionalArguments() || !call.getOptionalArguments().contains(parameter) || (row.get(parameter) != null)) {
                  if (!call.isOutputParameterType(parameterType)) {
                       tailWriter.write(nextBindString);
                       nextBindString = ", ?";
                  } else {
                       if (outParameterFound) {
                            //multiple outs found
                            throw ValidationException.multipleOutParamsNotSupported(Helper.getShortClassName(this), call.getProcedureName());
                       }
                       outParameterFound = true; //PostGreSQL uses a very different header to execute when there are out params
                  }
             }
        }
        tailWriter.write(")");

        if (outParameterFound) {
             writer.write("{?= CALL ");
             tailWriter.write("}");
        } else {
             writer.write("SELECT * FROM ");
        }
        writer.write(tailWriter.toString());

        return writer.toString();
    }
    /**
     * INTERNAL Used for stored function calls.
     */
    @Override
    public String getAssignmentString() {
        return ":= ";
    }
    
    /**
     * Allows DROP TABLE to cascade dropping of any dependent constraints if the database supports this option.
     */
    @Override
    public String getDropCascadeString() {
        return " CASCADE";
    }

    @Override
    public void printFieldTypeSize(Writer writer, FieldDefinition field, FieldTypeDefinition fieldType, boolean shouldPrintFieldIdentityClause) throws IOException {
        if (!shouldPrintFieldIdentityClause) {
            super.printFieldTypeSize(writer, field, fieldType, shouldPrintFieldIdentityClause);
        }
    }

    @Override
    public void printFieldUnique(Writer writer, boolean shouldPrintFieldIdentityClause) throws IOException {
        if (!shouldPrintFieldIdentityClause) {
            super.printFieldUnique(writer, shouldPrintFieldIdentityClause);
        }
    }

    /**
     * JDBC defines and outer join syntax, many drivers do not support this. So
     * we normally avoid it.
     */
    @Override
    public boolean shouldUseJDBCOuterJoinSyntax() {
        return false;
    }

    /**
     * INTERNAL: Override this method if the platform supports sequence objects
     * and it's possible to alter sequence object's increment in the database.
     */
    @Override
    public boolean isAlterSequenceObjectSupported() {
        return true;
    }

    /**
     * Print the pagination SQL using Postgres syntax
     * " LIMIT <max> OFFSET <first>".
     */
    @Override
    public void printSQLSelectStatement(DatabaseCall call, ExpressionSQLPrinter printer, SQLSelectStatement statement) {
        int max = 0;
        if (statement.getQuery() != null) {
            max = statement.getQuery().getMaxRows();
        }
        if (max <= 0 || !(this.shouldUseRownumFiltering())) {
            super.printSQLSelectStatement(call, printer, statement);
            return;
        }
        statement.setUseUniqueFieldAliases(true);
        call.setFields(statement.printSQL(printer));
        printer.printString(LIMIT);
        printer.printParameter(DatabaseCall.MAXROW_FIELD);
        printer.printString(OFFSET);
        printer.printParameter(DatabaseCall.FIRSTRESULT_FIELD);
        call.setIgnoreFirstRowSetting(true);
        call.setIgnoreMaxResultsSetting(true);
    }

    /**
     * INTERNAL: May need to override this method if the platform supports
     * temporary tables and the generated sql doesn't work. Write an sql string
     * for updating the original table from the temporary table. Precondition:
     * supportsTempTables() == true. Precondition: pkFields and assignFields
     * don't intersect.
     * 
     * @parameter Writer writer for writing the sql
     * @parameter DatabaseTable table is original table for which temp table is
     *            created.
     * @parameter Collection pkFields - primary key fields for the original
     *            table.
     * @parameter Collection assignedFields - fields to be assigned a new value.
     */
    @Override
    public void writeUpdateOriginalFromTempTableSql(Writer writer, DatabaseTable table, Collection pkFields, Collection assignedFields) throws IOException {
        writer.write("UPDATE ");
        String tableName = table.getQualifiedNameDelimited(this);
        writer.write(tableName);
        writer.write(" SET ");

        String tempTableName = getTempTableForTable(table).getQualifiedNameDelimited(this);
        boolean isFirst = true;
        Iterator itFields = assignedFields.iterator();
        while (itFields.hasNext()) {
            if (isFirst) {
                isFirst = false;
            } else {
                writer.write(", ");
            }
            DatabaseField field = (DatabaseField) itFields.next();
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
        writer.write(((DatabaseField) pkFields.iterator().next()).getNameDelimited(this));
        writer.write(" FROM ");
        writer.write(tempTableName);
        writeAutoJoinWhereClause(writer, null, tableName, pkFields, this);
        writer.write(")");
    }

    /**
     * INTERNAL:
     * Postgres has a returning clause.
     */
    public boolean canBuildCallWithReturning() {
        return true;
    }

    /**
     * INTERNAL:
     * Uses the returning clause on Postgres.
     */
    public DatabaseCall buildCallWithReturning(SQLCall sqlCall, Vector returnFields) {
        SQLCall call = new SQLCall();
        call.setParameters(sqlCall.getParameters());
        call.setParameterTypes(sqlCall.getParameterTypes());
        call.returnOneRow();
        Writer writer = new CharArrayWriter(sqlCall.getSQLString().length() + 32);
        try {
            writer.write(sqlCall.getSQLString());
            writer.write(" RETURNING ");
            for (int i = 0; i < returnFields.size(); i++) {
                DatabaseField field = (DatabaseField)returnFields.elementAt(i);
                writer.write(field.getNameDelimited(this));
                if ((i + 1) < returnFields.size()) {
                    writer.write(", ");
                }
            }
            call.setQueryString(writer.toString());
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
        return call;
    }

}
