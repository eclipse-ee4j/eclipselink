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
 *     05/24/2011-2.3 Guy Pelletier 
 *       - 345962: Join fetch query when using tenant discriminator column fails.
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     07/13/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     08/24/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     09/27/2012-2.5 Guy Pelletier
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.internal.databaseaccess;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;

/**
 * INTERNAL:
 * <b>Purpose<b>: Used as an abstraction of a database invocation.
 * A call is an SQL string or procedure call with parameters.
 */
public abstract class DatabaseCall extends DatasourceCall {
    /** 
     * JPA 2.1 NamedStoredProcedureQuery execute API implementation. 
     */
    protected boolean executeReturnValue;
    
    /**
     * Following fields are used to bind MaxResults and FirstRow settings into 
     * the query instead of using the values stored in the call.
     */
    public static DatabaseField MAXROW_FIELD = new DatabaseField("EclipseLink-MaxResults");
    public static DatabaseField FIRSTRESULT_FIELD = new DatabaseField("EclipseLink-FirstRow");

    /**
     * Indicates if the FirstRow value in this call object is to be ignored. If
     * true, it should mean it has been built into the SQL statement directly
     * ex: using Oracle Rownum support
     */
    protected boolean ignoreFirstRowSetting;

    /**
     * Indicates if the MaxResults value in this call object is to be ignored.
     * If true, it should mean it has been built into the SQL statement directly
     * ex: using Oracle Rownum support
     */
    protected boolean ignoreMaxResultsSetting;

    // The result and statement are cached for cursor selects.
    transient protected Statement statement;
    transient protected ResultSet result;

    // The call may specify that its parameters should be bound.
    protected Boolean usesBinding;

    // Bound calls can use prepared statement caching.
    protected Boolean shouldCacheStatement;

    // The returned fields.
    transient protected Vector fields;
    // PERF: fields array
    transient protected DatabaseField[] fieldsArray;

    // Field matching is required for custom SQL when the fields order is not known.
    protected boolean isFieldMatchingRequired;

    // optimistic locking determination is required for batch writing
    protected boolean hasOptimisticLock;
    protected boolean isResultSetScrollable;

    // JDK 1.2 supports initial fetch size for the result set.
    protected int resultSetFetchSize;

    // JDK 1.2 supports various types of results set
    protected int resultSetType;

    // JDK 1.2 supports various types of concurrency on results set
    protected int resultSetConcurrency;

    //query timeout limit in seconds
    protected int queryTimeout;

    //max rows returned in the result set by the call
    protected int maxRows;

    //firstResult set into the result set by the call
    protected int firstResult;

    //contain field - value pairs for LOB fields used to the
    //streaming operation during the writing (to the table)
    private transient AbstractRecord contexts;
    
    /** Allow for a single cursored output parameter. */
    protected boolean isCursorOutputProcedure;
    
    /** Allow for multiple cursored output parameter. */
    protected boolean isMultipleCursorOutputProcedure;

    // This parameter is here to determine if we should expect a ResultSet back from the call
    // We need to know this information in order to call the correct JDBC API
    protected Boolean returnsResultSet;

    // Whether the call has to build output row
    protected boolean shouldBuildOutputRow;

    // Callable statement is required if there is an output parameter
    protected boolean isCallableStatementRequired;
    
    /** Support multiple result sets. */
    protected boolean hasMultipleResultSets;
    
    /**
     * Support returning multiple results sets instead of just one list, i.e.
     * support multiple results set mappings.
     */
    protected boolean returnMultipleResultSetCollections;

    /** The SQL string to execute. */
    protected String sqlString;
    
    /** Indicates whether the call has allocated connection. May be set if the call has not finished */
    protected boolean hasAllocatedConnection;

    /**
     * Define if this query is compatible with batch writing.
     * Some queries, such as DDL are not compatible.
     */
    protected boolean isBatchExecutionSupported;
    
    /**
     * Keep a list of the output cursors.
     */
    protected List<DatabaseField> outputCursors;
    
    public DatabaseCall() {
        super.shouldProcessTokenInQuotes = false;
        this.usesBinding = null;
        this.shouldCacheStatement = null;
        this.isFieldMatchingRequired = false;
        this.queryTimeout = 0;
        this.maxRows = 0;
        this.resultSetFetchSize = 0;
        this.isCursorOutputProcedure = false;
        this.shouldBuildOutputRow = false;
        this.returnsResultSet = null;
        this.isBatchExecutionSupported = true;
    }
    
    /**
     * Return if the call returns multiple result sets.
     */
    public boolean hasMultipleResultSets() {
        return hasMultipleResultSets;
    }
    
    /**
     * Set if the call returns multiple result sets.
     */
    public void setHasMultipleResultSets(boolean hasMultipleResultSets) {
        this.hasMultipleResultSets = hasMultipleResultSets;
    }

    /**
     * INTERNAL:
     */
    public void appendIn(Object inObject) {
        getParameters().add(inObject);
        getParameterTypes().add(IN);
    }

    /**
     * INTERNAL:
     */
    public void appendInOut(DatabaseField inoutField) {
        Object[] inOut = { inoutField, inoutField };
        getParameters().add(inOut);
        getParameterTypes().add(INOUT);
    }

    /**
     * INTERNAL:
     */
    public void appendInOut(Object inValueOrField, DatabaseField outField) {
        Object[] inOut = { inValueOrField, outField };
        getParameters().add(inOut);
        getParameterTypes().add(INOUT);
    }

    /**
     * INTERNAL:
     */
    public void appendOut(DatabaseField outField) {
        getParameters().add(outField);
        getParameterTypes().add(OUT);
    }

    /**
     * INTERNAL:
     */
    public void appendOutCursor(DatabaseField outField) {
        getParameters().add(outField);
        getParameterTypes().add(OUT_CURSOR);
        getOutputCursors().add(outField);
    }

    /**
     * Add the parameter.
     * If using binding bind the parameter otherwise let the platform print it.
     * The platform may also decide to bind the value.
     */
    public void appendParameter(Writer writer, Object parameter, AbstractSession session) {
        if (Boolean.TRUE.equals(usesBinding)) {
            bindParameter(writer, parameter);
        } else {
            session.getPlatform().appendParameter(this, writer, parameter);
        }
    }

    /**
     * Bind the parameter. Binding is determined by the call and second the platform.
     */
    public void bindParameter(Writer writer, Object parameter) {
        if (parameter instanceof Collection) {
            throw QueryException.inCannotBeParameterized(getQuery());
        }

        try {
            writer.write("?");
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
        getParameters().add(parameter);
    }

    /**
     * Return the appropriate mechanism,
     * with the call added as necessary.
     */
    public DatabaseQueryMechanism buildNewQueryMechanism(DatabaseQuery query) {
        return new CallQueryMechanism(query, this);
    }

    /**
     * INTERNAL:
     * Return Record containing output fields and values.
     * Called only if shouldBuildOutputRow method returns true.
     */
    public AbstractRecord buildOutputRow(CallableStatement statement, DatabaseAccessor accessor, AbstractSession session) throws SQLException {
        AbstractRecord row = new DatabaseRecord();
        int size = this.parameters.size();
        for (int index = 0; index < size; index++) {
            Object parameter = this.parameters.get(index);
            if (parameter instanceof OutputParameterForCallableStatement) {
                OutputParameterForCallableStatement outParameter = (OutputParameterForCallableStatement)parameter;
                if (!outParameter.isCursor() || !isCursorOutputProcedure()) {
                    Object value = statement.getObject(index + 1);
                    DatabaseField field = outParameter.getOutputField();
                    if (value instanceof Struct){
                        ClassDescriptor descriptor = session.getDescriptor(field.getType());
                        if ((value != null) && (descriptor != null) && descriptor.isObjectRelationalDataTypeDescriptor()) {
                            AbstractRecord nestedRow = ((ObjectRelationalDataTypeDescriptor)descriptor).buildRowFromStructure((Struct)value);
                            ReadObjectQuery query = new ReadObjectQuery();
                            query.setSession(session);
                            value = descriptor.getObjectBuilder().buildNewInstance();
                            descriptor.getObjectBuilder().buildAttributesIntoObject(value, null, nestedRow, query, null, null, false, this.getQuery().getSession());
                        }
                    } else if ((value instanceof Array) && (field.isObjectRelationalDatabaseField())) {
                        value = ObjectRelationalDataTypeDescriptor.buildContainerFromArray((Array)value, (ObjectRelationalDatabaseField)field, session);
                    } else if (value instanceof ResultSet) {
                        // Support multiple out cursors, put list of records in row.
                        ResultSet resultSet = (ResultSet)value;
                        setFields(null);
                        matchFieldOrder(resultSet, accessor, session);
                        value = accessor.processResultSet(resultSet, this, statement, session);                        
                    }
                    row.put(field, value);
                }
            }
        }

        return row;
    }

    /**
     * Return the appropriate mechanism,
     * with the call added as necessary.
     */
    public DatabaseQueryMechanism buildQueryMechanism(DatabaseQuery query, DatabaseQueryMechanism mechanism) {
        if (mechanism.isCallQueryMechanism() && (mechanism instanceof CallQueryMechanism)) {
            // Must also add the call singleton...
            CallQueryMechanism callMechanism = ((CallQueryMechanism)mechanism);
            if (!callMechanism.hasMultipleCalls()) {
                callMechanism.addCall(callMechanism.getCall());
                callMechanism.setCall(null);
            }
            callMechanism.addCall(this);
            return mechanism;
        } else {
            return buildNewQueryMechanism(query);
        }
    }

    /**
     * INTERNAL:
     * Returns INOUT parameter. The first parameter is value to pass in, the second DatabaseField for out.
     */
    protected Object createInOutParameter(Object inValue, Object outParameter, AbstractSession session) {
        if (outParameter instanceof OutputParameterForCallableStatement) {
            return new InOutputParameterForCallableStatement(inValue, (OutputParameterForCallableStatement)outParameter);
        }
        if (outParameter instanceof DatabaseField) {
            return new InOutputParameterForCallableStatement(inValue, (DatabaseField)outParameter, session);
        }

        //should never happen
        return null;
    }

    /**
     * INTERNAL:
     * Return the SQL string for the call.
     */
    public String getCallString() {
        return getSQLString();
    }

    /**
     * The fields expected by the calls result set.
     * null means that the fields are unknown and should be built from the result set.
     */
    public Vector getFields() {
        return fields;
    }

    /**
     * INTERNAL:
     * The array of fields returned by the call.
     */
    public DatabaseField[] getFieldsArray() {
        return fieldsArray;
    }

    /**
     * INTERNAL:
     * Unfortunately can't avoid referencing query and descriptor:
     * the call should be performed after the translateCustomSQL (in SQLCall)
     * in the middle of prepare method (no parameter available earlier).
     *
     */
    protected DatabaseField getFieldWithTypeFromDescriptor(DatabaseField outField) {
        if (getQuery().getDescriptor() != null) {
            return getQuery().getDescriptor().getTypedField(outField);
        } else {
            return null;
        }
    }

    /**
     * INTERNAL:
     * Return 1-based index of out cursor parameter, or -1.
     */
    public int getCursorOutIndex() {
        int size = getParameters().size();
        for (int i = 0; i < size; i++) {
            Object parameter = this.parameters.get(i);
            if (parameter instanceof OutputParameterForCallableStatement) {
                if (((OutputParameterForCallableStatement)parameter).isCursor()) {
                    return i + 1;
                }
            }
        }
        return -1;
    }

    /**
     * After an execute call the return value can be retrieved here. 
     */
    public boolean getExecuteReturnValue() {
        return executeReturnValue;
    }
    
    /**
     * get first result
     */
    public int getFirstResult() {
        return this.firstResult;
    }

    /**
     * Return the SQL string for logging purposes.
     */
    public String getLogString(Accessor accessor) {
        if (hasParameters()) {
            StringWriter writer = new StringWriter();
            writer.write(getSQLString());
            writer.write(Helper.cr());
            if (hasParameters()) {
                AbstractSession session = null;
                if (getQuery() != null) {
                    session = getQuery().getSession();
                }
                appendLogParameters(getParameters(), accessor, writer, session);
            }
            return writer.toString();
        } else {
            return getSQLString();
        }
    }
    
    /**
     * Print the parameters to the write for logging purposes.
     */
    public static void appendLogParameters(Collection parameters, Accessor accessor, StringWriter writer, AbstractSession session) {
        writer.write("\tbind => [");
        
        if (session == null || (session != null && session.shouldDisplayData())) {
            for (Iterator paramsEnum = parameters.iterator(); paramsEnum.hasNext();) {
                Object parameter = paramsEnum.next();
                if (parameter instanceof DatabaseField) {
                    writer.write("null");
                } else {
                    if (session != null) {
                        parameter = session.getPlatform().convertToDatabaseType(parameter);
                    }
                    writer.write(String.valueOf(parameter));
                }
                if (paramsEnum.hasNext()) {
                    writer.write(", ");
                } else {
                    writer.write("]");
                }
            }
        } else {
            String parameterString = parameters.size() == 1 ? " parameter" : " parameters";
            writer.write(parameters.size() + parameterString + " bound]");
        }
    }
    
    /**
     * get max rows returned from the call
     */
    public int getMaxRows() {
        return this.maxRows;
    }

    /**
     * INTERNAL
     * Returns the fields to be used in output row.
     */
    public Vector getOutputRowFields() {
        Vector fields = new Vector();
        int size = getParameters().size();
        for (int i = 0; i < size; i++) {
            Integer parameterType = this.parameterTypes.get(i);
            Object parameter = this.parameters.get(i);
            if (parameterType == OUT) {
                fields.add(parameter);
            } else if (parameterType == INOUT) {
                fields.add(((Object[])parameter)[1]);
            }
        }
        return fields;
    }
    
    /**
     * INTERNAL:
     * Return the output cursors for this stored procedure call.
     */
    public List<DatabaseField> getOutputCursors() {
        if (outputCursors == null) {
            outputCursors = new ArrayList<DatabaseField>();
        }
        
        return outputCursors;
    }

    /**
     * INTERNAL:
     * Return the query string (SQL) of the call.
     */
    public String getQueryString() {
        return getSQLString();
    }

    /**
     * Get timeout limit from the call
     */
    public int getQueryTimeout() {
        return this.queryTimeout;
    }

    /**
     * The result set is stored for the return value of cursor selects.
     */
    public ResultSet getResult() {
        return result;
    }

    /**
     * ADVANCED:
     * This method returns a value that represents if the customer has set whether or not EclipseLink should expect
     * the stored procedure to returning a JDBC ResultSet.  The result of the method corresponds
     * to false, true.
     */
    public boolean getReturnsResultSet() {
        if (returnsResultSet == null) {
            return !shouldBuildOutputRow();
        } else {
            return returnsResultSet.booleanValue();
        }
    }

    public int getResultSetConcurrency() {
        return resultSetConcurrency;
    }

    public int getResultSetFetchSize() {
        return resultSetFetchSize;
    }

    public int getResultSetType() {
        return resultSetType;
    }

    /**
     * Return the SQL string that will be executed.
     */
    public String getSQLString() {
        return sqlString;
    }

    /**
     * The statement is stored for the return value of cursor selects.
     */
    public Statement getStatement() {
        return statement;
    }

    /**
     * This check is needed only when doing batch writing.
     */
    public boolean hasOptimisticLock() {
        return hasOptimisticLock;
    }

    /**
     * Return true if there are output cursors on this call.
     */
    public boolean hasOutputCursors() {
        return outputCursors != null && ! outputCursors.isEmpty();
    }
    
    /**
     * Callable statement is required if there is an output parameter.
     */
    protected boolean isCallableStatementRequired() {
        return isCallableStatementRequired;
    }

    /**
     * Return if the call is dynamic SQL call.
     * This means the call has no parameters, is not using binding,
     * is not a stored procedure (CallableStatement), or cursor.
     * This means that a Statement, not a PreparedStatement will be used for the call.
     */
    protected boolean isDynamicCall(AbstractSession session) {
        return DatabaseAccessor.shouldUseDynamicStatements && (!usesBinding(session)) && (!isResultSetScrollable()) && (!hasParameters());
    }

    /**
     * Used for Oracle result sets through procedures.
     */
    public boolean isCursorOutputProcedure() {
        return isCursorOutputProcedure;
    }

    /**
     * The return type is one of, NoReturn, ReturnOneRow or ReturnManyRows.
     */
    public boolean isCursorReturned() {
        return this.returnType == RETURN_CURSOR;
    }

    /**
     * Return if field matching is required.
     * Field matching is required for custom SQL statements where the result set field order is not known.
     */
    public boolean isFieldMatchingRequired() {
        return isFieldMatchingRequired;
    }

    /**
     * Return whether all the results of the call have been returned.
     */
    public boolean isFinished() {
        return !isCursorReturned() && !isExecuteUpdate();
    }

    /**
     * Used for Oracle result sets through procedures.
     */
    public boolean isMultipleCursorOutputProcedure() {
        return this.isMultipleCursorOutputProcedure; 
    }
    
    /**
     * Return true for procedures with any output (or in/out) parameters and no cursors
     */
    public boolean isNonCursorOutputProcedure() {
        return !isCursorOutputProcedure() && shouldBuildOutputRow();
    }

    public boolean isResultSetScrollable() {
        return isResultSetScrollable;
    }

    /**
     * Allow for the field order to be matched if required.
     * This is required for custom SQL.
     */
    public void matchFieldOrder(ResultSet resultSet, DatabaseAccessor accessor, AbstractSession session) {
        if ((getFields() != null) && (!isFieldMatchingRequired())) {
            return;
        }
        setFields(accessor.buildSortedFields(getFields(), resultSet, session));
    }

    /**
     * INTERNAL:
     * Allow pre-printing of the SQL string for fully bound calls, to save from reprinting.
     * Should be called before translation.
     */
    @Override
    public void prepare(AbstractSession session) {
        if (this.isPrepared) {
            return;
        }
        prepareInternal(session);
        this.isPrepared = true;
    }

    /**
     * INTERNAL:
     * Called by prepare method only. May be overridden.
     */
    protected void prepareInternal(AbstractSession session) {
        prepareInternalParameters(session);
    }
    
    /**
     * INTERNAL:
     * Called by prepareInternal method only. May be overridden.
     */
    protected void prepareInternalParameters(AbstractSession session) {
        if (isCursorOutputProcedure()) {
            // 1. If there are no OUT_CURSOR parameters - change the first OUT to OUT_CURSOR;
            // 2. If there are multiple OUT_CURSOR parameters - throw Validation exception
            int nFirstOutParameterIndex = -1;
            boolean hasFoundOutCursor = false;
            int size = this.parameters.size();
            for (int index = 0; index < size; index++) {
                Integer parameterType = this.parameterTypes.get(index);
                if (parameterType == DatasourceCall.OUT_CURSOR) {
                    if (hasFoundOutCursor) {
                        // one cursor has been already found
                        throw ValidationException.multipleCursorsNotSupported(toString());
                    } else {
                        hasFoundOutCursor = true;
                    }
                } else if (parameterType == DatasourceCall.OUT) {
                    if (nFirstOutParameterIndex == -1) {
                        nFirstOutParameterIndex = index;
                    }
                } else if (parameterType == null) {
                    // setCustomSQLArgumentType method was called when custom SQL is not used
                    throw ValidationException.wrongUsageOfSetCustomArgumentTypeMethod(toString());
                }
            }
            if (!hasFoundOutCursor && (nFirstOutParameterIndex >= 0)) {
                this.parameterTypes.set(nFirstOutParameterIndex, DatasourceCall.OUT_CURSOR);
            }
        }

        int size = getParameters().size();
        for (int i = 0; i < size; i++) {
            Object parameter = this.parameters.get(i);
            Integer parameterType = this.parameterTypes.get(i);
            if (parameterType == MODIFY) {
                // in case the field's type is not set, the parameter type is set to CUSTOM_MODIFY.
                DatabaseField field = (DatabaseField)parameter;
                if ((field.getType() == null) || session.getPlatform().shouldUseCustomModifyForCall(field)) {
                    this.parameterTypes.set(i, CUSTOM_MODIFY);
                }
            } else if (parameterType == INOUT) {
                // In case there is a type in outField, outParameter is created.
                // During translate call, either outParameter or outField is used for
                // creating inOut parameter.
                setShouldBuildOutputRow(true);
                setIsCallableStatementRequired(true);
                DatabaseField outField = (DatabaseField)((Object[])parameter)[1];
                if (outField.getType() == null) {
                    DatabaseField typeOutField = getFieldWithTypeFromDescriptor(outField);
                    if (typeOutField != null) {
                        outField = typeOutField.clone();
                    }
                }
                if (outField.getType() != null) {
                    // outParameter contains all the info for registerOutputParameter call.
                    OutputParameterForCallableStatement outParameter = new OutputParameterForCallableStatement(outField, session);
                    ((Object[])parameter)[1] = outParameter;
                }
            } else if ((parameterType == OUT) || (parameterType == OUT_CURSOR)) {
                boolean isCursor = parameterType == OUT_CURSOR;
                if (!isCursor) {
                    setShouldBuildOutputRow(true);
                }
                setIsCallableStatementRequired(true);
                DatabaseField outField = (DatabaseField)parameter;
                if (outField.getType() == null) {
                    DatabaseField typeOutField = getFieldWithTypeFromDescriptor(outField);
                    if (typeOutField != null) {
                        outField = typeOutField.clone();
                    }
                }

                // outParameter contains all the info for registerOutputParameter call.
                OutputParameterForCallableStatement outParameter = new OutputParameterForCallableStatement(outField, session, isCursor);
                this.parameters.set(i, outParameter);
                // nothing to do during translate method
                this.parameterTypes.set(i, LITERAL);
            }
        }
        if (this.returnsResultSet == null) {
            setReturnsResultSet(!isCallableStatementRequired());
        }
        // if there is nothing returned and we are not using optimistic locking then batch
        //if it is a StoredProcedure with in/out or out parameters then do not batch
        //logic may be weird but we must not batch if we are not using JDBC batchwriting and we have parameters
        // we may want to refactor this some day
        this.isBatchExecutionSupported = (isNothingReturned()
                && (!hasOptimisticLock() || session.getPlatform().canBatchWriteWithOptimisticLocking(this)) 
                && (!shouldBuildOutputRow())
                && (session.getPlatform().usesJDBCBatchWriting() || (!hasParameters()))
                && (!isLOBLocatorNeeded()))
                && (getQuery().isModifyQuery() && ((ModifyQuery)getQuery()).isBatchExecutionSupported());
    }

    /**
     * INTERNAL:
     * Prepare the JDBC statement, this may be parameterize or a call statement.
     * If caching statements this must check for the pre-prepared statement and re-bind to it.
     */
    public Statement prepareStatement(DatabaseAccessor accessor, AbstractRecord translationRow, AbstractSession session) throws SQLException {
        //#Bug5200836 pass shouldUnwrapConnection flag to indicate whether or not using unwrapped connection.
        Statement statement = accessor.prepareStatement(this, session);

        // Setup the max rows returned and query timeout limit.
        if (this.queryTimeout > 0) { 
            statement.setQueryTimeout(this.queryTimeout); 
        } 
        if (!this.ignoreMaxResultsSetting && this.maxRows > 0) { 
            statement.setMaxRows(this.maxRows); 
        }
        if (this.resultSetFetchSize > 0) { 
            statement.setFetchSize(this.resultSetFetchSize);
        } 

        if (this.parameters == null) {
            return statement;
        }
        List parameters = getParameters();
        int size = parameters.size();
        for (int index = 0; index < size; index++) {
            session.getPlatform().setParameterValueInDatabaseCall(parameters.get(index), (PreparedStatement)statement, index+1, session);
        }

        return statement;
    }

    /**
     * Return true if the multiple results 'lists' should be returned.
     */
    public boolean returnMultipleResultSetCollections() {
        return returnMultipleResultSetCollections;
    }
    
    /**
     * The fields expected by the calls result set.
     */
    public void setFields(Vector fields) {
        this.fields = fields;
        if (fields != null) {
            int size = fields.size();
            this.fieldsArray = new DatabaseField[size];
            for (int index = 0; index < size; index++) {
                this.fieldsArray[index] = (DatabaseField)fields.get(index);
            }
        } else {
            this.fieldsArray = null;
        }
    }

    /**
     * The firstResult set on the result set
     */
    public void setFirstResult(int firstResult) {
        this.firstResult = firstResult;
    }

    /**
     * This check is needed only when doing batch writing and we hit on optimistic locking.
     */
    public void setHasOptimisticLock(boolean hasOptimisticLock) {
        this.hasOptimisticLock = hasOptimisticLock;
    }

    /**
     * INTERNAL:
     * Sets the ignoreFirstRowSetting flag. If true, FirstResult option are
     * assumed built into the SQL string and ignored if set in the call, and
     * instead are added as query arguments.
     * Default is false.
     */
    public void setIgnoreFirstRowSetting(boolean ignoreFirstRowSetting){
        this.ignoreFirstRowSetting = ignoreFirstRowSetting;
    }

    /**
     * INTERNAL:
     * Sets the ignoreMaxResultsSetting flag. If true, MaxRows option are
     * assumed built into the SQL string and ignored if set in the call, and
     * instead are added as query arguments.
     * Default is false.
     */
    public void setIgnoreMaxResultsSetting(boolean ignoreMaxResultsSetting){
        this.ignoreMaxResultsSetting = ignoreMaxResultsSetting;
    }

    /**
     * Callable statement is required if there is an output parameter.
     */
    protected void setIsCallableStatementRequired(boolean isCallableStatementRequired) {
        this.isCallableStatementRequired = isCallableStatementRequired;
    }

    /**
     * Used for Oracle result sets through procedures.
     */
    public void setIsCursorOutputProcedure(boolean isCursorOutputProcedure) {
        this.isCursorOutputProcedure = isCursorOutputProcedure;
    }
    
    /**
     * Field matching is required for custom SQL statements where the result set field order is not known.
     */
    public void setIsFieldMatchingRequired(boolean isFieldMatchingRequired) {
        this.isFieldMatchingRequired = isFieldMatchingRequired;
    }

    /**
     * Used for Oracle result sets through procedures.
     */
    public void setIsMultipleCursorOutputProcedure(boolean isMultipleCursorOutputProcedure) {
        this.isMultipleCursorOutputProcedure = isMultipleCursorOutputProcedure; 
    }
    
    public void setIsResultSetScrollable(boolean isResultSetScrollable) {
        this.isResultSetScrollable = isResultSetScrollable;
    }

    /**
     * set query max returned row size to the JDBC Statement
     */
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    /**
     * INTERNAL:
     * Set the query string (SQL) of the call.
     */
    public void setQueryString(String queryString) {
        setSQLStringInternal(queryString);
    }

    /**
     * set query timeout limit to the JDBC Statement
     */
    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    /**
     * The result set is stored for the return value of cursor selects.
     */
    public void setResult(ResultSet result) {
        this.result = result;
    }

    public void setResultSetConcurrency(int resultSetConcurrency) {
        this.resultSetConcurrency = resultSetConcurrency;
    }

    /**
     * INTERNAL:
     * Set the SQL string.
     */
    protected void setSQLStringInternal(String sqlString) {
        this.sqlString = sqlString;
    }

    public void setResultSetFetchSize(int resultSetFetchSize) {
        this.resultSetFetchSize = resultSetFetchSize;
    }

    public void setResultSetType(int resultSetType) {
        this.resultSetType = resultSetType;
    }

    /**
     * PUBLIC:
     * Use this method to tell EclipseLink that the stored procedure will be returning a JDBC ResultSet
     */
    public void setReturnsResultSet(boolean returnsResultSet) {
        this.returnsResultSet = Boolean.valueOf(returnsResultSet);
    }

    /**
     * Set if the call returns multiple result sets.
     */
    public void setReturnMultipleResultSetCollections(boolean returnMultipleResultSetCollections) {
        this.returnMultipleResultSetCollections = returnMultipleResultSetCollections;
    }
    
    /**
     * INTERNAL:
     * Set whether the call has to build output row
     */
    protected void setShouldBuildOutputRow(boolean shouldBuildOutputRow) {
        this.shouldBuildOutputRow = shouldBuildOutputRow;
    }

    /**
     * Bound calls can use prepared statement caching.
     */
    public void setShouldCacheStatement(boolean shouldCacheStatement) {
        this.shouldCacheStatement = Boolean.valueOf(shouldCacheStatement);
    }

    /**
     * The statement is stored for the return value of cursor selects.
     */
    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    /**
     * The call may specify that its parameters should be bound.
     */
    public void setUsesBinding(boolean usesBinding) {
        this.usesBinding = Boolean.valueOf(usesBinding);
    }

    /**
     * Set whether the call has to build output row
     */
    public boolean shouldBuildOutputRow() {
        return this.shouldBuildOutputRow;
    }

    /**
     * Bound calls can use prepared statement caching.
     */
    public boolean shouldCacheStatement(AbstractSession session) {
        return shouldCacheStatement(session.getPlatform());
    }

    /**
     * Bound calls can use prepared statement caching.
     */
    public boolean shouldCacheStatement(DatabasePlatform databasePlatform) {
        //CR4272  If result set is scrollable, do not cache statement since scrollable cursor can not be used for cached statement
        if (isResultSetScrollable()) {
            return false;
        }
        if (this.shouldCacheStatement == null) {
            return databasePlatform.shouldCacheAllStatements();
        } else {
            return this.shouldCacheStatement.booleanValue();
        }
    }

    /**
     * INTERNAL:
     * Returns the ignoreFirstRowSetting flag. If true, FirstResult option is
     * assumed built into the SQL string and ignored if set in the call.
     */
    public boolean shouldIgnoreFirstRowSetting(){
        return this.ignoreFirstRowSetting;
    }

    /**
     * INTERNAL:
     * Returns the ignoreMaxResultsSetting flag. If true, MaxRows option is
     * assumed built into the SQL string and ignored if set in the call.
     */
    public boolean shouldIgnoreMaxResultsSetting(){
        return this.ignoreMaxResultsSetting;
    }

    /**
     * INTERNAL:
     * Print the SQL string.
     */
    public String toString() {
        String str = Helper.getShortClassName(getClass());
        if (getSQLString() == null) {
            return str;
        } else {
            return str + "(" + getSQLString() + ")";
        }
    }

    /**
     * INTERNAL:
     * Allow the call to translate from the translation for predefined calls.
     */
    @Override
    public void translate(AbstractRecord translationRow, AbstractRecord modifyRow, AbstractSession session) {
        if (!isPrepared()) {
            throw ValidationException.cannotTranslateUnpreparedCall(toString());
        }
        if (usesBinding(session) && (this.parameters != null)) {
            boolean hasParameterizedIN = false;
            List parameters = getParameters();
            List<Integer> parameterTypes = getParameterTypes();
            int size = parameters.size();
            List parametersValues = new ArrayList(size);
            for (int index = 0; index < size; index++) {
                Object parameter = parameters.get(index);
                Object parameterType = parameterTypes.get(index);
                if (parameterType == MODIFY) {
                    DatabaseField field = (DatabaseField)parameter;
                    Object value = modifyRow.get(field);
                    // If the value is null, the field is passed as the value so the type can be obtained from the field.
                    if (value == null) {
                        // The field from the modify row is used, as the calls field may not have the type,
                        // but if the field is missing the calls field may also have the type.
                        value = modifyRow.getField(field);
                        if (value == null) {
                            value = field;
                        }
                    }
                    parametersValues.add(value);
                } else if (parameterType == CUSTOM_MODIFY) {
                    DatabaseField field = (DatabaseField)parameter;
                    Object value = modifyRow.get(field);
                    value = session.getPlatform().getCustomModifyValueForCall(this, value, field, true);
                    //Bug#8200836 needs use unwrapped connection
                    if ((value!=null) && (value instanceof BindCallCustomParameter) &&  (((BindCallCustomParameter)value).shouldUseUnwrappedConnection())){
                        this.isNativeConnectionRequired=true;
                    }

                    // If the value is null, the field is passed as the value so the type can be obtained from the field.
                    if (value == null) {
                        // The field from the modify row is used, as the calls field may not have the type,
                        // but if the field is missing the calls field may also have the type.
                        value = modifyRow.getField(field);
                        if (value == null) {
                            value = field;
                        }
                    }
                    parametersValues.add(value);
                } else if (parameterType == TRANSLATION) {
                    Object value = null;
                    DatabaseField field = null;
                    if (parameter instanceof ParameterExpression) {
                        field = ((ParameterExpression)parameter).getField();
                        value = ((ParameterExpression)parameter).getValue(translationRow, query, session);
                    } else {
                        field = (DatabaseField)parameter;
                        value = translationRow.get(field);
                        if (value == null) {// Backward compatibility double check.
                            value = modifyRow.get(field);
                        }
                    }
                    if (value instanceof Collection) {
                        // Must re-translate IN parameters.
                        hasParameterizedIN = true;
                    }
                    // If the value is null, the field is passed as the value so the type can be obtained from the field.
                    if ((value == null) && (field != null)) {
                        if (!this.query.hasNullableArguments() || !this.query.getNullableArguments().contains(field)) {
                            value = translationRow.getField(field);
                            // The field from the row is used, as the calls field may not have the type,
                            // but if the field is missing the calls field may also have the type.
                            if (value == null) {
                                value = field;
                            }
                            parametersValues.add(value);
                        }
                    } else {
                        parametersValues.add(value);
                    }
                } else if (parameterType == LITERAL) {
                    parametersValues.add(parameter);
                } else if (parameterType == IN) {
                    Object value = getValueForInParameter(parameter, translationRow, modifyRow, session, true);
                    // Returning this means the parameter was optional and should not be included.
                    if (value != this) {
                        parametersValues.add(value);
                    }
                } else if (parameterType == INOUT) {
                    Object value = getValueForInOutParameter(parameter, translationRow, modifyRow, session);
                    parametersValues.add(value);
                }
            }
            setParameters(parametersValues);
            // If an IN parameter was found must translate SQL.
            if (hasParameterizedIN) {
                translateQueryStringForParameterizedIN(translationRow, modifyRow, session);
            }
            return;
        }

        translateQueryString(translationRow, modifyRow, session);
    }

    /**
     * INTERNAL:
     * Translate only IN() parameter values (List parameters).
     */
    public void translateQueryStringForParameterizedIN(AbstractRecord translationRow, AbstractRecord modifyRow, AbstractSession session) {
        int lastIndex = 0;
        int parameterIndex = 0;
        String queryString = getQueryString();
        Writer writer = new CharArrayWriter(queryString.length() + 50);
        try {
            // PERF: This method is heavily optimized do not touch anything unless you know "very well" what your doing.
            List parameters = getParameters();            
            List parametersValues = new ArrayList(parameters.size());
            while (lastIndex != -1) {
                int tokenIndex = queryString.indexOf(argumentMarker(), lastIndex);
                String token;
                if (tokenIndex == -1) {
                    token = queryString.substring(lastIndex, queryString.length());
                    lastIndex = -1;
                } else {
                    token = queryString.substring(lastIndex, tokenIndex);
                }
                writer.write(token);
                if (tokenIndex != -1) {
                    // Process next parameter.
                    Object parameter = parameters.get(parameterIndex);
                    // Parameter expressions are used for nesting and correct mapping conversion of the value.
                    if (parameter instanceof Collection) {
                        Collection values = (Collection)parameter;
                        writer.write("(");
                        if ((values.size() > 0) && (values.iterator().next() instanceof List)) {
                            // Support nested lists.
                            int size = values.size();
                            Iterator valuesIterator = values.iterator();
                            for (int index = 0; index < size; index++) {
                                List nestedValues = (List)valuesIterator.next();
                                parametersValues.addAll(nestedValues);
                                int nestedSize = nestedValues.size();
                                writer.write("(");
                                for (int nestedIndex = 0; nestedIndex < nestedSize; nestedIndex++) {
                                    writer.write("?");
                                    if ((nestedIndex + 1) < nestedSize) {
                                        writer.write(",");
                                    }
                                }
                                writer.write(")");
                                if ((index + 1) < size) {
                                    writer.write(",");
                                }
                            }
                        } else {
                            parametersValues.addAll(values);
                            int size = values.size();
                            for (int index = 0; index < size; index++) {
                                writer.write("?");
                                if ((index + 1) < size) {
                                    writer.write(",");
                                }
                            }
                        }
                        writer.write(")");
                    } else {
                        parametersValues.add(parameter);
                        writer.write("?");
                    }
                    lastIndex = tokenIndex + 1;
                    parameterIndex++;
                }
            }
            setParameters(parametersValues);
            setQueryString(writer.toString());

        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }
    
    /**
     * The call may specify that its parameters should be bound.
     */
    public boolean usesBinding(AbstractSession session) {
        return usesBinding(session.getPlatform());
    }

    /**
     * The call may specify that its parameters should be bound.
     */
    public boolean usesBinding(DatabasePlatform databasePlatform) {
        if (this.usesBinding == null) {
            return databasePlatform.shouldBindAllParameters();
        } else {
            return this.usesBinding.booleanValue();
        }
    }

    /**
     * INTERNAL
     * Indicates whether usesBinding has been set.
     */
    public boolean isUsesBindingSet() {
        return this.usesBinding != null;
    }

    /**
     * INTERNAL:
     * Return if the locator is required for the LOB (BLOB and CLOB) writing.
     */
    public boolean isLOBLocatorNeeded() {
        return contexts != null;
    }

    /**
     * INTERNAL:
     * Add a field - value pair for LOB field into the context.
     */
    public void addContext(DatabaseField field, Object value) {
        if (this.contexts == null) {
            this.contexts = new DatabaseRecord(2);
        }
        this.contexts.add(field, value);
        this.isBatchExecutionSupported = false;
    }

    /**
     * INTERNAL:
     * Return the contexts (for LOB)
     */
    public AbstractRecord getContexts() {
        return contexts;
    }

    /**
     * INTERNAL:
     * Set the contexts (for LOB)
     */
    public void setContexts(AbstractRecord contexts) {
        this.contexts = contexts;
    }

    /**
     * An execute return value will be set here after the call.
     */
    public void setExecuteReturnValue(boolean value) {
        executeReturnValue = value;
    }
    
    /**
     * PUBLIC:
     * Used for Oracle result sets through procedures.
     * The first OUT parameter is set as a cursor output.
     */
    public void useUnnamedCursorOutputAsResultSet() {
        setIsCursorOutputProcedure(true);
    }

    /**
     * INTERNAL:
     * Return if this query is compatible with batch writing.
     * Some queries, such as DDL are not compatible.
     */
    public boolean isBatchExecutionSupported() {
        return isBatchExecutionSupported;
    }

    /**
     * INTERNAL:
     * Set if this query is compatible with batch writing.
     * Some queries, such as DDL are not compatible.
     */
    public void setBatchExecutionSupported(boolean isBatchExecutionSupported) {
        this.isBatchExecutionSupported = isBatchExecutionSupported;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasAllocatedConnection() {
        return this.hasAllocatedConnection;
    }

    /**
     * INTERNAL:
     */
    public void setHasAllocatedConnection(boolean hasAllocatedConnection) {
        this.hasAllocatedConnection = hasAllocatedConnection;
    }
}
