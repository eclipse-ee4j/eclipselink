/*******************************************************************************
 * Copyright 2005 Sun Microsystems,  Inc. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     Sun Microsystems
 *     09/14/2011-2.3.1 Guy Pelletier 
 *       - 357533: Allow DDL queries to execute even when Multitenant entities are part of the PU
 ******************************************************************************/
package org.eclipse.persistence.platform.database;

import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.queries.ValueReadQuery;

import java.util.Vector;
import java.io.Writer;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.persistence.internal.helper.DatabaseField;

/**
 * <p><b>Purpose</b>: Provides Derby DBMS specific behavior.
 *
 * @since TOPLink Essentials 1.0
 */
public class DerbyPlatform extends DB2Platform {

    public static final int MAX_CLOB = 2147483647;  //The maximum clob/blob size is 2 gigs in Derby.
    public static final int MAX_BLOB = MAX_CLOB;

    /** Allow sequence support to be disabled for Derby < 10.6.1. */
    protected boolean isSequenceSupported;
    protected boolean isConnectionDataInitialized;
    
    /**
     * INTERNAL:
     * TODO: Need to find out how can byte arrays be inlined in Derby
     */
    @Override
    protected void appendByteArray(byte[] bytes, Writer writer) throws IOException {
            super.appendByteArray(bytes, writer);
    }

    /**
     * INTERNAL:
     * This method returns the query to select the timestamp from the server
     * for Derby.
     */
    @Override
    public ValueReadQuery getTimestampQuery() {
        if (timestampQuery == null) {
            timestampQuery = new ValueReadQuery();
            timestampQuery.setSQLString("VALUES CURRENT_TIMESTAMP");
            timestampQuery.setAllowNativeSQLQuery(true);
        }
        return timestampQuery;

    }
    
    /**
     * INTERNAL:
     * Not currently used.
     */
    @Override
    public Vector getNativeTableInfo(String table, String creator, AbstractSession session) {
        throw new RuntimeException("Not supported");
    }

    /**
     * Used for stored procedure defs.
     */
    @Override
    public String getProcedureEndString() {
        return getBatchEndString();
    }

    /**
     * Used for stored procedure defs.
     */
    @Override
    public String getProcedureBeginString() {
        return getBatchBeginString();
    }

    /**
     * This method is used to print the output parameter token when stored
     * procedures are called
     */
    @Override
    public String getInOutputProcedureToken() {
        return "INOUT";
    }

    /**
     * This is required in the construction of the stored procedures with
     * output parameters
     */
    @Override
    public boolean shouldPrintOutputTokenAtStart() {
        //TODO: Check with the reviewer where this is used
        return false;
    }

    /**
     * INTERNAL:
     * Answers whether platform is Derby
     */
    @Override
    public boolean isDerby() {
        return true;
    }

    @Override
    public boolean isDB2() {
        //This class inherits from DB2. But it is not DB2
        return false;
    }

    @Override
    public String getSelectForUpdateString() {
        return " FOR UPDATE WITH RS";
    }


    /**
     * Allow for the platform to ignore exceptions.
     */
    @Override
    public boolean shouldIgnoreException(SQLException exception) {
        // Nothing is ignored.
        return false;
    }


    /**
     * INTERNAL:
     */
    @Override
    protected String getCreateTempTableSqlSuffix() {
        return " ON COMMIT DELETE ROWS NOT LOGGED";
    }

    /**
     * INTERNAL:
     * Build the identity query for native sequencing.
     */
    @Override
    public ValueReadQuery buildSelectQueryForIdentity() {
        ValueReadQuery selectQuery = new ValueReadQuery();
        selectQuery.setSQLString("values IDENTITY_VAL_LOCAL()");
        return selectQuery;
    }

    /**
     * INTERNAL:
     * Indicates whether temporary table can specify primary keys (some platforms don't allow that).
     * Used by writeCreateTempTableSql method.
     */
    @Override
    protected boolean shouldTempTableSpecifyPrimaryKeys() {
        return false;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    protected String getCreateTempTableSqlBodyForTable(DatabaseTable table) {
        // returning null includes fields of the table in body
        // see javadoc of DatabasePlatform#getCreateTempTableSqlBodyForTable(DataBaseTable)
        // for details
        return null;
    }

    /**
     * INTERNAL:
     * May need to override this method if the platform supports temporary tables
     * and the generated sql doesn't work.
     * Write an sql string for updating the original table from the temporary table.
     * Precondition: supportsTempTables() == true.
     * Precondition: pkFields and assignFields don't intersect.
     * @parameter Writer writer for writing the sql
     * @parameter DatabaseTable table is original table for which temp table is created.
     * @parameter Collection pkFields - primary key fields for the original table.
     * @parameter Collection assignedFields - fields to be assigned a new value.
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
     * Append the receiver's field 'identity' constraint clause to a writer.
     */
    @Override
    public void printFieldIdentityClause(Writer writer) throws ValidationException {
        try {
            writer.write(" GENERATED BY DEFAULT AS IDENTITY");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
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
        fieldTypeMapping.put(Byte[].class, new FieldTypeDefinition("BLOB", MAX_BLOB));
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("CLOB", MAX_CLOB));
        fieldTypeMapping.put(byte[].class, new FieldTypeDefinition("BLOB", MAX_BLOB));
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("CLOB", MAX_CLOB));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldTypeDefinition("BLOB", MAX_BLOB));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldTypeDefinition("CLOB", MAX_CLOB));        
        
        fieldTypeMapping.put(java.sql.Date.class, new FieldTypeDefinition("DATE", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldTypeDefinition("TIME", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldTypeDefinition("TIMESTAMP", false));

        return fieldTypeMapping;
    }


    @Override
    protected void setNullFromDatabaseField(DatabaseField databaseField, PreparedStatement statement, int index) throws SQLException {
        int jdbcType = databaseField.getSqlType();
        if (jdbcType == DatabaseField.NULL_SQL_TYPE) {
            jdbcType = statement.getParameterMetaData().getParameterType(index);
        }
        
        statement.setNull(index, jdbcType);
    }

    /**
     * Initialize any platform-specific operators
     */
    @Override
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        // Derby does not support DECIMAL, but does have a DOUBLE function.
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.ToNumber, "DOUBLE"));
        addOperator(extractOperator());
    }

    /**
     * INTERNAL:
     * Derby does not support EXTRACT, but does have YEAR, MONTH, DAY, etc.
     */
    public static ExpressionOperator extractOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.Extract);
        exOperator.setName("EXTRACT");
        Vector v = NonSynchronizedVector.newInstance(5);
        v.add("");
        v.add("(");
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
     * Use the JDBC maxResults and firstResultIndex setting to compute a value to use when
     * limiting the results of a query in SQL.  These limits tend to be used in two ways.
     * 
     * 1. MaxRows is the index of the last row to be returned (like JDBC maxResults)
     * 2. MaxRows is the number of rows to be returned
     * 
     * Derby uses case #2 and therefore the maxResults has to be altered based on the firstResultIndex.
     */
    @Override
    public int computeMaxRowsForSQL(int firstResultIndex, int maxResults) {
        if (!isSequenceSupported) {
            return maxResults;
        }
        return maxResults - ((firstResultIndex >= 0) ? firstResultIndex : 0);
    }
    
    /**
     * INTERNAL:
     * Derby supports pagination through its "OFFSET n ROWS FETCH NEXT m ROWS" syntax.
     */
    @Override
    public void printSQLSelectStatement(DatabaseCall call, ExpressionSQLPrinter printer, SQLSelectStatement statement) {
        if (!this.isSequenceSupported) {
            call.setFields(statement.printSQL(printer));
            return;
        }
        int max = 0;
        int firstRow = 0;

        if (statement.getQuery()!=null) {
            max = statement.getQuery().getMaxRows();
            firstRow = statement.getQuery().getFirstResult();
        }
        
        if (!(this.shouldUseRownumFiltering()) || (!(max>0) && !(firstRow>0))) {
            call.setFields(statement.printSQL(printer));
            return;
        } else if (max > 0) {
            statement.setUseUniqueFieldAliases(true);
            call.setFields(statement.printSQL(printer));
            printer.printString(" OFFSET ");
            printer.printParameter(DatabaseCall.FIRSTRESULT_FIELD);
            printer.printString(" ROWS FETCH NEXT ");
            printer.printParameter(DatabaseCall.MAXROW_FIELD);
            printer.printString(" ROWS ONLY");
        } else {
            statement.setUseUniqueFieldAliases(true);
            call.setFields(statement.printSQL(printer));
            printer.printString(" OFFSET ");
            printer.printParameter(DatabaseCall.FIRSTRESULT_FIELD);
            printer.printString(" ROWS");
        }
        call.setIgnoreFirstRowSetting(true);
        call.setIgnoreMaxResultsSetting(true);
    }

    /**
     * INTERNAL: Derby supports sequence objects as of 10.6.1.
     */
    @Override
    public boolean supportsSequenceObjects() {
        return this.isSequenceSupported;
    }
    
    @Override
    public boolean isAlterSequenceObjectSupported() {
        return false;
    }

    /**
     * INTERNAL: Derby supports sequence objects as of 10.6.1.
     */
    @Override
    public Writer buildSequenceObjectDeletionWriter(Writer writer, String fullSeqName) throws IOException {
        writer.write("DROP SEQUENCE ");
        writer.write(fullSeqName);
        writer.write(" RESTRICT");
        return writer;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initializeConnectionData(Connection connection) throws SQLException {
        if (this.isConnectionDataInitialized) {
            return;
        }
        String databaseVersion = connection.getMetaData().getDatabaseProductVersion();
        if (Helper.compareVersions(databaseVersion, "10.6.1") < 0) {
            this.isSequenceSupported = false;
        } else {
            this.isSequenceSupported = true;            
        }
        this.isConnectionDataInitialized = true;
    }
}
