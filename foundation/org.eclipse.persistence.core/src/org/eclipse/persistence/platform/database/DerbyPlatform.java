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
 ******************************************************************************/
package org.eclipse.persistence.platform.database;

import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.queries.ValueReadQuery;

import java.util.Vector;
import java.io.Writer;
import java.io.IOException;
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

    /**
     * INTERNAL:
     * TODO: Need to find out how can byte arrays be inlined in Derby
     */
    protected void appendByteArray(byte[] bytes, Writer writer) throws IOException {
            super.appendByteArray(bytes, writer);
    }

    /**
     * INTERNAL:
     * This method returns the query to select the timestamp from the server
     * for Derby.
     */
    public ValueReadQuery getTimestampQuery() {
        if (timestampQuery == null) {
            timestampQuery = new ValueReadQuery();
            timestampQuery.setSQLString("VALUES CURRENT_TIMESTAMP");
        }
        return timestampQuery;

    }

    //TODO: check with reviewer. This method should be made private in DB2platform
    public Vector getNativeTableInfo(String table, String creator, AbstractSession session) {
        throw new RuntimeException("Should never reach here");
    }

    /**
     * Used for stored procedure defs.
     */
    public String getProcedureEndString() {
        return getBatchEndString();
    }

    /**
     * Used for stored procedure defs.
     */
    public String getProcedureBeginString() {
        return getBatchBeginString();
    }

    /**
     * This method is used to print the output parameter token when stored
     * procedures are called
     */
    public String getInOutputProcedureToken() {
        return "INOUT";
    }

    /**
     * This is required in the construction of the stored procedures with
     * output parameters
     */
    public boolean shouldPrintOutputTokenAtStart() {
        //TODO: Check with the reviewer where this is used
        return false;
    }

    /**
     * INTERNAL:
     * Answers whether platform is Derby
     */
    public boolean isDerby() {
        return true;
    }

    public boolean isDB2() {
        //This class inhertits from DB2. But it is not DB2
        return false;
    }

    public String getSelectForUpdateString() {
        return " FOR UPDATE WITH RS";
    }


    /**
     * Allow for the platform to ignore exceptions.
     */
    public boolean shouldIgnoreException(SQLException exception) {
        // Nothing is ignored.
        return false;
    }


    /**
     * INTERNAL:
     */
    protected String getCreateTempTableSqlSuffix() {
        return " ON COMMIT DELETE ROWS NOT LOGGED";
    }

    /**
     * INTERNAL:
     * Build the identity query for native sequencing.
     */
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
    protected boolean shouldTempTableSpecifyPrimaryKeys() {
        return false;
    }
    
    /**
     * INTERNAL:
     */
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
    public void printFieldIdentityClause(Writer writer) throws ValidationException {
        try {
            writer.write(" GENERATED BY DEFAULT AS IDENTITY");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }
    
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
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        // Derby does not support DECIMAL, but does have a DOUBLE function.
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.ToNumber, "DOUBLE"));
    }
    
    /**
     * INTERNAL:
     * Derby does not support the DB2 syntax, so perform the default.
     */
    @Override
    public void printSQLSelectStatement(DatabaseCall call, ExpressionSQLPrinter printer, SQLSelectStatement statement) {
        call.setFields(statement.printSQL(printer));
    }

    /**
     * INTERNAL: Derby does not support sequence objects as DB2 does.
     */
    @Override
    public boolean supportsSequenceObjects() {
        return false;
    }
}
