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
 *     Markus Karg - bug fix for log operator
 *     09/09/2011-2.3.1 Guy Pelletier 
 *       - 356197: Add new VPD type to MultitenantType
 *     09/14/2011-2.3.1 Guy Pelletier 
 *       - 357533: Allow DDL queries to execute even when Multitenant entities are part of the PU
 *     02/04/2013-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 ******************************************************************************/  
package org.eclipse.persistence.platform.database;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.RelationExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.queries.ValueReadQuery;

/**
 * <p><b>Purpose</b>: Provides Oracle specific behavior.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Native SQL for byte[], Date, Time, & Timestamp.
 * <li> Native sequencing named sequences.
 * <li> Native SQL/ROWNUM support for MaxRows and FirstResult filtering.
 * </ul>
 *
 * @since TOPLink/Java 1.0
 */
public class OraclePlatform extends org.eclipse.persistence.platform.database.DatabasePlatform {
    protected static DataModifyQuery vpdSetIdentifierQuery;
    protected static DataModifyQuery vpdClearIdentifierQuery;
    
    /** 
     * Advanced attribute indicating whether identity is supported,
     * see comment to setSupportsIdentity method.
     */ 
    protected boolean supportsIdentity;

    public OraclePlatform(){
    	super();
    	this.pingSQL = "SELECT 1 FROM DUAL";
    }
    
    /*
     * Used for stored procedure definitions.
     */
    @Override
    public boolean allowsSizeInProcedureArguments() {
        return false;
    }

    /**
     * INTERNAL:
     * If using native SQL then print a byte[] literally as a hex string otherwise use ODBC format
     * as provided in DatabasePlatform.
     */
    @Override
    protected void appendByteArray(byte[] bytes, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            writer.write('\'');
            Helper.writeHexString(bytes, writer);
            writer.write('\'');
        } else {
            super.appendByteArray(bytes, writer);
        }
    }

    /**
     * INTERNAL:
     * Appends an Oracle specific date if usesNativeSQL is true otherwise use the ODBC format.
     * Native FORMAT: to_date('1997-11-06','yyyy-mm-dd')
     */
    @Override
    protected void appendDate(java.sql.Date date, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            writer.write("to_date('");
            writer.write(Helper.printDate(date));
            writer.write("','yyyy-mm-dd')");
        } else {
            super.appendDate(date, writer);
        }
    }

    /**
     * INTERNAL:
     * Appends an Oracle specific time if usesNativeSQL is true otherwise use the ODBC format.
     * Native FORMAT: to_date(#####, 'sssss').
     */
    @Override
    protected void appendTime(java.sql.Time time, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            writer.write("to_date('");
            writer.write(Helper.printTime(time));
            writer.write("','hh24:mi:ss')");
        } else {
            super.appendTime(time, writer);
        }
    }

    /**
     * INTERNAL:
     * Appends an Oracle specific Timestamp, if usesNativeSQL is true otherwise use the ODBC format.
     * Native Format: to_date ('1997-11-06 10:35:45.0' , 'yyyy-mm-dd hh:mm:ss.n')
     */
    @Override
    protected void appendTimestamp(java.sql.Timestamp timestamp, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            writer.write("to_date('");
            writer.write(Helper.printTimestampWithoutNanos(timestamp));
            writer.write("','yyyy-mm-dd hh24:mi:ss')");
        } else {
            super.appendTimestamp(timestamp, writer);
        }
    }

    /**
     * INTERNAL:
     * Appends an Oracle specific Timestamp, if usesNativeSQL is true otherwise use the ODBC format.
     * Native Format: to_date ('1997-11-06 10:35:45.0' , 'yyyy-mm-dd hh:mm:ss.n')
     */
    @Override
    protected void appendCalendar(Calendar calendar, Writer writer) throws IOException {
        if (usesNativeSQL()) {
            writer.write("to_date('");
            writer.write(Helper.printCalendarWithoutNanos(calendar));
            writer.write("','yyyy-mm-dd hh24:mi:ss')");
        } else {
            super.appendCalendar(calendar, writer);
        }
    }

    /**
     * INTERNAL:
     * Build operator.
     */
    public ExpressionOperator atan2Operator() {
        return ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Atan2, "ATAN2");
    }

    /**
     * INTERNAL:
     */
    @Override
    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypeMapping;

        fieldTypeMapping = new Hashtable();
        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("NUMBER(1) default 0", false));

        fieldTypeMapping.put(Integer.class, new FieldTypeDefinition("NUMBER", 10));
        fieldTypeMapping.put(Long.class, new FieldTypeDefinition("NUMBER", 19));
        fieldTypeMapping.put(Float.class, new FieldTypeDefinition("NUMBER", 19, 4));
        fieldTypeMapping.put(Double.class, new FieldTypeDefinition("NUMBER", 19, 4));
        fieldTypeMapping.put(Short.class, new FieldTypeDefinition("NUMBER", 5));
        fieldTypeMapping.put(Byte.class, new FieldTypeDefinition("NUMBER", 3));
        fieldTypeMapping.put(java.math.BigInteger.class, new FieldTypeDefinition("NUMBER", 38));
        fieldTypeMapping.put(java.math.BigDecimal.class, new FieldTypeDefinition("NUMBER", 38).setLimits(38, -38, 38));
        fieldTypeMapping.put(Number.class, new FieldTypeDefinition("NUMBER", 38).setLimits(38, -38, 38));

        fieldTypeMapping.put(String.class, new FieldTypeDefinition("VARCHAR2", DEFAULT_VARCHAR_SIZE));
        fieldTypeMapping.put(Character.class, new FieldTypeDefinition("CHAR", 1));

        fieldTypeMapping.put(Byte[].class, new FieldTypeDefinition("BLOB", false));
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("CLOB", false));
        fieldTypeMapping.put(byte[].class, new FieldTypeDefinition("BLOB", false));
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("CLOB", false)); 
        fieldTypeMapping.put(java.sql.Blob.class, new FieldTypeDefinition("BLOB", false));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldTypeDefinition("CLOB", false));         

        fieldTypeMapping.put(java.sql.Date.class, new FieldTypeDefinition("DATE", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldTypeDefinition("TIMESTAMP", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldTypeDefinition("TIMESTAMP", false));
        //bug 5871089 the default generator requires definitions based on all java types
        fieldTypeMapping.put(java.util.Calendar.class, new FieldTypeDefinition("TIMESTAMP"));
        fieldTypeMapping.put(java.util.Date.class, new FieldTypeDefinition("TIMESTAMP"));

        return fieldTypeMapping;
    }

    /**
     * INTERNAL:
     * Returns null unless the platform supports call with returning
     */
    @Override
    public DatabaseCall buildCallWithReturning(SQLCall sqlCall, Vector returnFields) {
        SQLCall call = new SQLCall();
        call.setParameters(sqlCall.getParameters());
        call.setParameterTypes(sqlCall.getParameterTypes());

        Writer writer = new CharArrayWriter(200);
        try {
            writer.write("BEGIN ");
            writer.write(sqlCall.getSQLString());
            writer.write(" RETURNING ");

            for (int i = 0; i < returnFields.size(); i++) {
                DatabaseField field = (DatabaseField)returnFields.elementAt(i);
                writer.write(field.getNameDelimited(this));
                if ((i + 1) < returnFields.size()) {
                    writer.write(", ");
                }
            }

            writer.write(" INTO ");

            for (int i = 0; i < returnFields.size(); i++) {
                DatabaseField field = (DatabaseField)returnFields.elementAt(i);
                call.appendOut(writer, field);
                if ((i + 1) < returnFields.size()) {
                    writer.write(", ");
                }
            }

            writer.write("; END;");

            call.setQueryString(writer.toString());

        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }

        return call;
    }

    /**
     * INTERNAL:
     * Indicates whether the platform can build call with returning.
     * In case this method returns true, buildCallWithReturning method
     * may be called.
     */
    @Override
    public boolean canBuildCallWithReturning() {
        return true;
    }

    /**
     * INTERNAL:
     * Clears both implicit and explicit caches of OracleConnection on Oracle9Platform, noop here.
     */
    public void clearOracleConnectionCache(Connection conn) {    
    }
    
    /**
     * INTERNAL:
     * Used for stored function calls.
     */
    @Override
    public String getAssignmentString() {
        return ":= ";
    }

    /**
     * INTERNAL:
     * DECLARE stanza header for Anonymous PL/SQL block
     */
    public String getDeclareBeginString() {
        return "DECLARE ";
    }
    
    /**
     * Used for batch writing and sp defs.
     */
    @Override
    public String getBatchBeginString() {
        return "BEGIN ";
    }

    /**
     * Used for batch writing and sp defs.
     */
    @Override
    public String getBatchEndString() {
        return "END;";
    }

    /**
     * Used for batch writing for row count return.
     */
    @Override
    public String getBatchRowCountDeclareString() {
        return "DECLARE EL_COUNTER NUMBER := 0; ";
    }
    
    /**
     * Oracle does not return the row count from PLSQL anon blocks,
     * so an output parameter is required for this.
     */
    @Override
    public boolean isRowCountOutputParameterRequired() {
        return true;
    }

    /**
     * Used for batch writing for row count return.
     */
    @Override
    public String getBatchRowCountReturnString() {
        return "? := EL_COUNTER; ";
    }
    
    /**
     * Return the drop schema definition. Subclasses should override as needed.
     */
    @Override
    public String getDropDatabaseSchemaString(String schema) {
        return "DROP SCHEMA " + schema + " RESTRICT";
    }
    
    /**
     * Used for batch writing for row count return.
     */
    @Override
    public String getBatchRowCountAssignString() {
        return "EL_COUNTER := EL_COUNTER + SQL%ROWCOUNT; ";
    }
    
    /**
     * INTERNAL:
     * returns the maximum number of characters that can be used in a field
     * name on this platform.
     */
    @Override
    public int getMaxFieldNameSize() {
        return 30;
    }

    /**
     * Return the catalog information through using the native SQL catalog selects.
     * This is required because many JDBC driver do not support meta-data.
     * Willcards can be passed as arguments.
     */
    public Vector getNativeTableInfo(String table, String creator, AbstractSession session) {
        String query = "SELECT * FROM ALL_TABLES WHERE OWNER NOT IN ('SYS', 'SYSTEM')";
        if (table != null) {
            if (table.indexOf('%') != -1) {
                query = query + " AND TABLE_NAME LIKE " + table;
            } else {
                query = query + " AND TABLE_NAME = " + table;
            }
        }
        if (creator != null) {
            if (creator.indexOf('%') != -1) {
                query = query + " AND OWNER LIKE " + creator;
            } else {
                query = query + " AND OWNER = " + creator;
            }
        }
        return session.executeSelectingCall(new SQLCall(query));
    }
    
    /**
     * Used for sp calls.
     */
    @Override
    public String getProcedureArgumentSetter() {
        return "=>";
    }

    /**
     * Used for sp calls.
     */
    @Override
    public String getProcedureCallHeader() {
        return useJDBCStoredProcedureSyntax() ? "{CALL " : "BEGIN ";  
    }

    /**
     * Used for sp calls.
     */
    @Override
    public String getProcedureCallTail() {
        return useJDBCStoredProcedureSyntax() ? "}" : "; END;";
    }
    
    /**
     * Allows DROP TABLE to cascade dropping of any dependent constraints if the database supports this option.
     */
    @Override
    public String getDropCascadeString() {
        return " CASCADE CONSTRAINTS";
    }

    @Override
    public String getSelectForUpdateString() {
        return " FOR UPDATE";
    }
    
    @Override
    public String getSelectForUpdateWaitString(Integer waitTimeout) {
        return " FOR UPDATE WAIT " + waitTimeout.intValue();
    }

    @Override
    public String getStoredProcedureParameterPrefix() {
        return "P_";
    }

    @Override
    public String getStoredProcedureTerminationToken() {
        return "";
    }

    /**
     * PUBLIC:
     * The query to select the current system change number
     * from Oracle.
     * In order to execute this query a database administrator may need
     * to grant execute permission on pl/sql package DBMS_FLASHBACK.
     */
    public ValueReadQuery getSystemChangeNumberQuery() {
        ValueReadQuery sCNQuery = new ValueReadQuery();
        sCNQuery.setSQLString("SELECT DBMS_FLASHBACK.GET_SYSTEM_CHANGE_NUMBER FROM DUAL");
        return sCNQuery;
    }

    /**
     * PUBLIC:
     * This method returns the query to select the timestamp
     * from the server for Oracle.
     */
    @Override
    public ValueReadQuery getTimestampQuery() {
        if (timestampQuery == null) {
            timestampQuery = new ValueReadQuery();
            timestampQuery.setSQLString("SELECT SYSDATE FROM DUAL");
            timestampQuery.setAllowNativeSQLQuery(true);
        }
        return timestampQuery;
    }
    
    /**
     * INTERNAL:
     * Return an Oracle defined VPD clear identifier query.
     */
    @Override
    public DatabaseQuery getVPDClearIdentifierQuery(String vpdIdentifier) {
        if (vpdClearIdentifierQuery == null) {
            vpdClearIdentifierQuery = new DataModifyQuery("CALL DBMS_SESSION.CLEAR_IDENTIFIER()");
        }
    
        return vpdClearIdentifierQuery;
    }
    
    /**
     * INTERNAL:
     * Return an Oracle defined VPD identifier function. Used for DDL generation.
     */
    @Override
    public String getVPDCreationFunctionString(String tableName, String tenantFieldName) {
        String functionName = tableName + "_ident_func";
        return "CREATE OR REPLACE FUNCTION " + functionName + " (p_schema in VARCHAR2 default NULL, p_object in VARCHAR2 default NULL) RETURN VARCHAR2 AS BEGIN return '" + tenantFieldName + " = sys_context(''userenv'', ''client_identifier'')'; END;";
    }
    
    /**
     * INTERNAL:
     * Return an Oracle defined VPD identifier policy. Used for DDL generation.
     */
    @Override
    public String getVPDCreationPolicyString(String tableName, AbstractSession session) {
        String functionName = tableName + "_ident_func";
        String schemaName = session.getDatasourceLogin().getUserName();
        String policyName = tableName + "_todo_list_policy";
        return "\nCALL DBMS_RLS.ADD_POLICY ('" + schemaName + "', '" + tableName + "', '" + policyName + "', '" + schemaName + "', '" + functionName +"', 'select, update, delete')\n";
    }
    
    /**
     * INTERNAL:
     * Return an Oracle defined VPD identifier policy deletion. Used for DDL generation.
     */
    @Override
    public String getVPDDeletionString(String tableName, AbstractSession session) {
        String schemaName = session.getDatasourceLogin().getUserName();
        String policyName = tableName + "_todo_list_policy";
        return "\nCALL DBMS_RLS.DROP_POLICY ('" + schemaName + "', '" + tableName + "', '" + policyName + "')";
    }
    
    /**
     * INTERNAL:
     * Return an Oracle defined VPD set identifier query.
     */
    @Override
    public DatabaseQuery getVPDSetIdentifierQuery(String vpdIdentifier) {
        if (vpdSetIdentifierQuery == null) {
            vpdSetIdentifierQuery = new DataModifyQuery("CALL DBMS_SESSION.SET_IDENTIFIER(#" + vpdIdentifier + ")");
        }
        
        return vpdSetIdentifierQuery;
    }
    
    /**
     * INTERNAL:
     * Get a timestamp value from a result set.
     * Overrides the default behavior to specifically return a timestamp.  Added
     * to overcome an issue with the oracle 9.0.1.4 JDBC driver.
     */
    @Override
    public Object getObjectFromResultSet(ResultSet resultSet, int columnNumber, int type, AbstractSession session) throws java.sql.SQLException {
        //Bug#3381652 10G Drivers return sql.Date instead of timestamp on DATE field
        if ((type == Types.TIMESTAMP) || (type == Types.DATE)) {
            return resultSet.getTimestamp(columnNumber);
        } else {
            return super.getObjectFromResultSet(resultSet, columnNumber, type, session);
        }
    }

    /**
     * Initialize any platform-specific operators
     */
    @Override
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        addOperator(operatorOuterJoin());
        addOperator(logOperator());
        addOperator(ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Concat, "CONCAT"));
        addOperator(todayOperator());
        addOperator(currentDateOperator());
        addOperator(currentTimeOperator());
        addOperator(ExpressionOperator.truncateDate());
        addOperator(ExpressionOperator.newTime());
        addOperator(ExpressionOperator.ifNull());
        addOperator(atan2Operator());
        addOperator(ExpressionOperator.oracleDateName());
        addOperator(operatorLocate());
        addOperator(operatorLocate2());
        addOperator(regexpOperator());
        addOperator(exceptOperator());
    }
    
    /**
     * INTERNAL:
     * Create the EXCEPT operator, MINUS in Oracle.
     */
    public static ExpressionOperator exceptOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.Except);
        exOperator.printsAs("MINUS ");
        exOperator.bePostfix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * INTERNAL:
     * Create the REGEXP_LIKE operator.
     */
    public static ExpressionOperator regexpOperator() {
        ExpressionOperator result = new ExpressionOperator();
        result.setSelector(ExpressionOperator.Regexp);
        result.setType(ExpressionOperator.FunctionOperator);
        Vector v = NonSynchronizedVector.newInstance(3);
        v.add("REGEXP_LIKE(");
        v.add(", ");
        v.add(")");
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
     * INTERNAL:
     * Used by derived platforms (Oracle8Platform and higher)
     * to indicate whether app. server should unwrap connection
     * to use lob locator.
     */
    public boolean isNativeConnectionRequiredForLobLocator() {
        return false;
    }

    @Override
    public boolean isOracle() {
        return true;
    }

    /**
     *  Create the log operator for this platform
     */
    protected ExpressionOperator logOperator() {
        ExpressionOperator result = new ExpressionOperator();
        result.setSelector(ExpressionOperator.Log);
        Vector v = NonSynchronizedVector.newInstance(2);
        v.addElement("LOG(10,");
        v.addElement(")");
        result.printsAs(v);
        result.bePrefix();
        result.setNodeClass(FunctionExpression.class);
        return result;

    }

    /**
     *    Builds a table of maximum numeric values keyed on java class. This is used for type testing but
     * might also be useful to end users attempting to sanitize values.
     * <p><b>NOTE</b>: BigInteger & BigDecimal maximums are dependent upon their precision & Scale
     */
    @Override
    public Hashtable maximumNumericValues() {
        Hashtable values = new Hashtable();

        values.put(Integer.class, Integer.valueOf(Integer.MAX_VALUE));
        values.put(Long.class, Long.valueOf(Long.MAX_VALUE));
        values.put(Double.class, Double.valueOf(9.9999E125));
        values.put(Short.class, Short.valueOf(Short.MAX_VALUE));
        values.put(Byte.class, Byte.valueOf(Byte.MAX_VALUE));
        values.put(Float.class, Float.valueOf(Float.MAX_VALUE));
        values.put(java.math.BigInteger.class, new java.math.BigInteger("0"));
        values.put(java.math.BigDecimal.class, new java.math.BigDecimal(new java.math.BigInteger("0"), 38));
        return values;
    }

    /**
     *    Builds a table of minimum numeric values keyed on java class. This is used for type testing but
     * might also be useful to end users attempting to sanitize values.
     * <p><b>NOTE</b>: BigInteger & BigDecimal minimums are dependent upon their precision & Scale
     */
    @Override
    public Hashtable minimumNumericValues() {
        Hashtable values = new Hashtable();

        values.put(Integer.class, Integer.valueOf(Integer.MIN_VALUE));
        values.put(Long.class, Long.valueOf(Long.MIN_VALUE));
        values.put(Double.class, Double.valueOf(-1E-129));
        values.put(Short.class, Short.valueOf(Short.MIN_VALUE));
        values.put(Byte.class, Byte.valueOf(Byte.MIN_VALUE));
        values.put(Float.class, Float.valueOf(Float.MIN_VALUE));
        values.put(java.math.BigInteger.class, new java.math.BigInteger("0"));
        values.put(java.math.BigDecimal.class, new java.math.BigDecimal(new java.math.BigInteger("0"), 38));
        return values;
    }

    /**
     * INTERNAL:
     * Produce a DataReadQuery which updates(!) the sequence number in the db
     * and returns it. Currently implemented on Oracle only.
     * @param qualifiedSeqName known by Oracle to be a defined sequence
     */
    @Override
    public ValueReadQuery buildSelectQueryForSequenceObject(String qualifiedSeqName, Integer size) {
        return new ValueReadQuery("SELECT " + qualifiedSeqName + ".NEXTVAL FROM DUAL");
    }

    /**
     * INTERNAL:
     * Though Oracle doesn't support identity it could be imitated,
     * see comment to setSupportsIdentity method.
     * @param qualifiedSeqName known by Oracle to be a defined sequence
     */
    @Override
    public ValueReadQuery buildSelectQueryForIdentity(String qualifiedSeqName, Integer size) {
        return new ValueReadQuery("SELECT " + qualifiedSeqName + ".CURRVAL FROM DUAL");
    }

    /**
     * Create the outer join operator for this platform
     */
    protected ExpressionOperator operatorOuterJoin() {
        ExpressionOperator result = new ExpressionOperator();
        result.setSelector(ExpressionOperator.EqualOuterJoin);
        Vector v = NonSynchronizedVector.newInstance(2);
        v.addElement(" (+) = ");
        result.printsAs(v);
        result.bePostfix();
        result.setNodeClass(RelationExpression.class);
        return result;

    }

    /**
     * INTERNAL:
     * Override the default locate operator
     */
    protected ExpressionOperator operatorLocate() {
        ExpressionOperator result = new ExpressionOperator();
        result.setSelector(ExpressionOperator.Locate);
        Vector v = NonSynchronizedVector.newInstance(2);
        v.addElement("INSTR(");
        v.addElement(", ");
        v.addElement(")");
        result.printsAs(v);
        result.bePrefix();
        result.setNodeClass(RelationExpression.class);
        return result;
    }

    /**
     * INTERNAL:
     * Override the default locate operator
     */
    protected ExpressionOperator operatorLocate2() {
        ExpressionOperator result = new ExpressionOperator();
        result.setSelector(ExpressionOperator.Locate2);
        Vector v = NonSynchronizedVector.newInstance(2);
        v.addElement("INSTR(");
        v.addElement(", ");
        v.addElement(", ");
        v.addElement(")");
        result.printsAs(v);
        result.bePrefix();
        result.setNodeClass(RelationExpression.class);
        return result;
    }

    /**
     * INTERNAL:
     * Append the receiver's field 'NULL' constraint clause to a writer.
     */
    @Override
    public void printFieldNullClause(Writer writer) throws ValidationException {
        try {
            writer.write(" NULL");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    /** 
     * Return the current date and time from the server.
     */
    public String serverTimestampString() {
        return "SYSDATE";
    }

    /**
     * INTERNAL:
     * Should the variable name of a stored procedure call be printed as part of the procedure call
     * e.g. EXECUTE PROCEDURE MyStoredProc(myvariable = ?)
     */
    @Override
    public boolean shouldPrintStoredProcedureArgumentNameInCall() {
        return ! useJDBCStoredProcedureSyntax();
    }
    
    /**
     * JDBC defines and outer join syntax, many drivers do not support this. So we normally avoid it.
     */
    @Override
    public boolean shouldUseJDBCOuterJoinSyntax() {
        return false;
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
     * Indicates whether the platform supports sequence objects.
     * This method is to be used *ONLY* by sequencing classes
     */
    @Override
    public boolean supportsSequenceObjects() {
        return true;
    }

    /**
     * INTERNAL:
     * Indicates whether the platform supports identity.
     * This method is to be used *ONLY* by sequencing classes
     */
    @Override
    public boolean supportsIdentity() {
        return supportsIdentity;
    }

    /**
     * ADVANCED:
     * Oracle db doesn't support identity.
     * However it's possible to get identity-like behavior
     * using sequence in an insert trigger - that's the only 
     * case when supportsIdentity should be set to true:
     * in this case all the sequences that have shouldAcquireValueAfterInsert
     * set to true will keep this setting (it would've been reversed in case
     * identity is not supported).
     * Note that with supportsIdentity==true attempt to create tables that have
     * identity fields will fail - Oracle doesn't support identity.
     * Therefore if there's table creation reqiured it should be done
     * with supportsIdentity==false, then set the flag to true and reset sequencing
     * (or logout and login the session).
     */
    public void setSupportsIdentity(boolean supportsIdentity) {
        this.supportsIdentity = supportsIdentity;
    }

    /**
     * INTERNAL:
     * Return if database stored functions are supported.
     */
    @Override
    public boolean supportsStoredFunctions() {
        return true;
    }
    
    /**
     * Oracle db supports VPD.
     */
    @Override
    public boolean supportsVPD() {
        return true;
    }

    /**
     * Returns true if the database supports SQL syntax not to wait on a SELECT..FOR UPADTE
     * (i.e. In Oracle adding NOWAIT to the end will accomplish this)
     */
    public boolean supportsSelectForUpdateNoWait() {
        return true;
    }

    /**
     * Create the sysdate operator for this platform
     */
    protected ExpressionOperator todayOperator() {
        return ExpressionOperator.simpleFunctionNoParentheses(ExpressionOperator.Today, "SYSDATE");
    }

    protected ExpressionOperator currentDateOperator() {
        return ExpressionOperator.simpleFunctionNoParentheses(ExpressionOperator.CurrentDate, "TO_DATE(CURRENT_DATE)");
    }

    protected ExpressionOperator currentTimeOperator() {
        return ExpressionOperator.simpleFunctionNoParentheses(ExpressionOperator.CurrentTime, "SYSDATE");
    }

    /**
     * INTERNAL:
     * Indicates whether this Oracle platform can unwrap Oracle connection.
     */
    public boolean canUnwrapOracleConnection() {
        return false;
    }

    /**
     * INTERNAL:
     * If can unwrap returns unwrapped Oracle connection, otherwise original connection.
     */
    public Connection unwrapOracleConnection(Connection connection) {
        return connection;
    }
    
    /**
     * Return true if JDBC syntax should be used for stored procedure calls.
     */
    public boolean useJDBCStoredProcedureSyntax() {
        if (useJDBCStoredProcedureSyntax == null) {
            useJDBCStoredProcedureSyntax = this.driverName != null && this.driverName.equals("Oracle");
        }
        
        return useJDBCStoredProcedureSyntax;
    }
    
    //Oracle Rownum support
    protected String SELECT = "SELECT * FROM (SELECT ";
    protected String HINT = "/*+ FIRST_ROWS */ ";
    protected String FROM = "a.*, ROWNUM rnum  FROM (";
    protected String END_FROM = ") a ";
    protected String MAX_ROW = "WHERE ROWNUM <= ";
    protected String MIN_ROW = ") WHERE rnum > ";
    
    /**
     * INTERNAL:
     * Print the SQL representation of the statement on a stream, storing the fields
     * in the DatabaseCall.  This implementation works MaxRows and FirstResult into the SQL using
     * Oracle's ROWNUM to filter values if shouldUseRownumFiltering is true.  
     */
    @Override
    public void printSQLSelectStatement(DatabaseCall call, ExpressionSQLPrinter printer, SQLSelectStatement statement){
        int max = 0;
        int firstRow = 0;

        if (statement.getQuery()!=null){
            max = statement.getQuery().getMaxRows();
            firstRow = statement.getQuery().getFirstResult();
        }
        
        if ( !(this.shouldUseRownumFiltering()) || ( !(max>0) && !(firstRow>0) ) ){
            super.printSQLSelectStatement(call, printer,statement);
            return;
        } else if ( max > 0 ){
            statement.setUseUniqueFieldAliases(true);
            printer.printString(SELECT);
            printer.printString(HINT);
            printer.printString(FROM);
            
            call.setFields(statement.printSQL(printer));
            printer.printString(END_FROM);
            printer.printString(MAX_ROW);
            printer.printParameter(DatabaseCall.MAXROW_FIELD);
            printer.printString(MIN_ROW);
            printer.printParameter(DatabaseCall.FIRSTRESULT_FIELD);
        } else {// firstRow>0
            statement.setUseUniqueFieldAliases(true);
            printer.printString(SELECT);
            printer.printString(FROM);
            
            call.setFields(statement.printSQL(printer));
            printer.printString(END_FROM);
            printer.printString(MIN_ROW);
            printer.printParameter(DatabaseCall.FIRSTRESULT_FIELD);
        }
        call.setIgnoreFirstRowSetting(true);
        call.setIgnoreMaxResultsSetting(true);
    }

    /**
     * INTERNAL:
     * Override this method if the platform supports sequence objects
     * and it's possible to alter sequence object's increment in the database.
     */
    @Override
    public boolean isAlterSequenceObjectSupported() {
        return true;
    }

    /**
     * INTERNAL:
     * Indicates whether SELECT DISTINCT ... FOR UPDATE is allowed by the platform (Oracle doesn't allow this).
     */
    @Override
    public boolean isForUpdateCompatibleWithDistinct() {
        return false;
    }
    
    /**
     * Return true if the given exception occurred as a result of a lock
     * time out exception (WAIT clause).
     */
    @Override
    public boolean isLockTimeoutException(DatabaseException e) {
        return (e.getInternalException() instanceof java.sql.SQLException && ((java.sql.SQLException) e.getInternalException()).getErrorCode() == 30006);
    }
    
    /**
     * INTERNAL:
     * A call to this method will perform a platform based check on the connection and exception
     * error code to determine if the connection is still valid or if a communication error has occurred.
     * If a communication error has occurred then the query may be retried.
     * If this platform is unable to determine if the error was communication based it will return
     * false forcing the error to be thrown to the user.
     */
    @Override
    public boolean wasFailureCommunicationBased(SQLException exception, Connection connection, AbstractSession sessionForProfile){
        if (exception != null){
            if (exception.getErrorCode() == 17410){
                return true;
            }
            if (exception.getErrorCode() == 17002){
                return true;
            }
            if (exception.getErrorCode() == 2399){
                return true;
            }
            if (exception.getErrorCode() == 2396){
                return true;
            }
        }
        return super.wasFailureCommunicationBased(exception, connection, sessionForProfile);
    }

}
