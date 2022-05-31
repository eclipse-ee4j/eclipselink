/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019, 2022 IBM Corporation. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     10/29/2010-2.2 Michael O'Brien
//       - 325167: Make reserved # bind parameter char generic to enable native SQL pass through
//     05/24/2011-2.3 Guy Pelletier
//       - 345962: Join fetch query when using tenant discriminator column fails.
//     07/13/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     08/24/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     11/10/2014-2.6 Dmitry Kornilov
//       - 450818: Column names with hash mark => "java.sql.SQLException: Invalid column index"
package org.eclipse.persistence.internal.databaseaccess;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.queries.DatabaseQueryMechanism;
import org.eclipse.persistence.internal.queries.DatasourceCallQueryMechanism;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.DatabaseQuery;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * INTERNAL:
 * <b>Purpose<b>: Used as an abstraction of a datasource invocation.
 *
 * @author James Sutherland
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public abstract class DatasourceCall implements Call {
    // Back reference to query, unfortunately required for events.
    protected transient DatabaseQuery query;

    // The parameters (values) are ordered as they appear in the call.
    protected List<Object> parameters;

    // The parameter types determine if the parameter is a modify, translation or literal type.

    protected List<ParameterType> parameterTypes;

    // The parameter binding determines if the specific parameter should be bound.
    protected List<Boolean> parameterBindings;

    /**
     *  The call may specify that all of its parameters should/shouldn't be bound.
     *  <p>
     *  Typically, this is set to false in the event that the DatabasePlatform marks the call
     *  as containing illegal binding behavior during JPQL parsing. 
     *  <p>
     *  Defaults to null to indicate no preference and allows database platforms to determine
     */
    protected Boolean usesBinding;

    public enum ParameterType {
        LITERAL(1), MODIFY(2), TRANSLATION(3), CUSTOM_MODIFY(4), OUT(5), 
        INOUT(6), IN(7), OUT_CURSOR(8), INLINE(9);

        public int val; 

        ParameterType(int val) {
            this.val = val;
        }

        public static ParameterType valueOf(int value) {
            for(ParameterType v : values())
                if(v.val == value) return v;
            throw new IllegalArgumentException("Value (" + value + ") does not match a ParameterType");
        }
    }

    // Store if the call has been prepared.
    protected boolean isPrepared;

    /** Allow connection unwrapping to be configured. */
    protected boolean isNativeConnectionRequired;

    //Eclipselink Bug 217745 indicates whether or not the token(#,?) needs to be processed if they are in the quotes.
    protected boolean shouldProcessTokenInQuotes;

    /**
     * Keep a list of the output cursors.
     */
    protected List<DatabaseField> outputCursors;

    // Type of call.
    protected int returnType;
    protected static final int NO_RETURN = 1;
    protected static final int RETURN_ONE_ROW = 2;
    protected static final int RETURN_MANY_ROWS = 3;
    protected static final int RETURN_CURSOR = 4;
    protected static final int EXECUTE_UPDATE = 5;

    public DatasourceCall() {
        this.isPrepared = false;
        this.shouldProcessTokenInQuotes = true;
        this.usesBinding = null;
    }

    /**
     * The parameters are the values in order of occurrence in the SQL statement.
     * This is lazy initialized to conserve space on calls that have no parameters.
     */
    public List getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<Object>();
        }
        return parameters;
    }

    /**
     * The parameter types determine if the parameter is a modify, translation or literal type.
     */
    public List<ParameterType> getParameterTypes() {
        if (parameterTypes == null) {
            parameterTypes = new ArrayList<ParameterType>();
        }
        return parameterTypes;
    }

    /**
     * The parameter binding determines if the specific parameter should be bound.
     */
    public List<Boolean> getParameterBindings() {
        if (parameterBindings == null) {
            parameterBindings = new ArrayList<Boolean>();
        }
        return parameterBindings;
    }

    /**
     * The parameters are the values in order of occurrence in the SQL statement.
     */
    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    /**
     * The parameter types determine if the parameter is a modify, translation or literal type.
     */
    public void setParameterTypes(List<ParameterType> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    /**
     * The parameter binding determines if the specific parameter should be bound.
     */
    public void setParameterBindings(List<Boolean> parameterBindings) {
        this.parameterBindings = parameterBindings;
    }

    /**
     * The parameters are the values in order of occurrence in call.
     * This is lazy initialized to conserve space on calls that have no parameters.
     */
    public boolean hasParameters() {
        return (parameters != null) && (!getParameters().isEmpty());
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
     * Return true if there are output cursors on this call.
     */
    public boolean hasOutputCursors() {
        return outputCursors != null && ! outputCursors.isEmpty();
    }

    /**
     * The return type is one of, NoReturn, ReturnOneRow or ReturnManyRows.
     */
    public boolean areManyRowsReturned() {
        return this.returnType == RETURN_MANY_ROWS;
    }

    public static boolean isOutputParameterType(ParameterType parameterType) {
        return (parameterType == ParameterType.OUT) || (parameterType == ParameterType.INOUT) || (parameterType == ParameterType.OUT_CURSOR);
    }

    /**
     * Bound calls can have the SQL pre generated.
     */
    public boolean isPrepared() {
        return isPrepared;
    }

    /**
     * Bound calls can have the SQL pre generated.
     */
    public void setIsPrepared(boolean isPrepared) {
        this.isPrepared = isPrepared;
    }

    /**
     * Set that this call should or shouldn't bind all parameters
     */
    public void setUsesBinding(boolean usesBinding) {
        this.usesBinding = Boolean.valueOf(usesBinding);
    }

    /**
     * Convenience method
     * @see {@link #usesBinding(DatabasePlatform databasePlatform)}
     */
    public boolean usesBinding(AbstractSession session) {
        return usesBinding(session.getPlatform());
    }

    /**
     * Determines if this call should bind all parameters. 
     * <p>
     * Defaults behavior to the databasePlatform if this call does not have a preference; if 
     * {@link org.eclipse.persistence.internal.databaseaccess.DatasourceCall#usesBinding} is not set
     * <p>
     * @see org.eclipse.persistence.internal.databaseaccess.DatabasePlatform#shouldBindAllParameters()
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
    public Boolean usesBinding() {
        return this.usesBinding;
    }

    /**
     * INTERNAL
     * Indicates whether usesBinding has been set.
     */
    public boolean isUsesBindingSet() {
        return this.usesBinding != null;
    }

    /**
     * Return the appropriate mechanism,
     * with the call added as necessary.
     */
    public DatabaseQueryMechanism buildNewQueryMechanism(DatabaseQuery query) {
        return new DatasourceCallQueryMechanism(query, this);
    }

    /**
     * Return the appropriate mechanism,
     * with the call added as necessary.
     */
    public DatabaseQueryMechanism buildQueryMechanism(DatabaseQuery query, DatabaseQueryMechanism mechanism) {
        if (mechanism.isCallQueryMechanism() && (mechanism instanceof DatasourceCallQueryMechanism)) {
            // Must also add the call singleton...
            DatasourceCallQueryMechanism callMechanism = ((DatasourceCallQueryMechanism)mechanism);
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

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException exception) {
            ;//Do nothing
        }

        return null;
    }

    /**
     * Return the SQL string for logging purposes.
     */
    public abstract String getLogString(Accessor accessor);

    /**
     * Back reference to query, unfortunately required for events.
     */
    public DatabaseQuery getQuery() {
        return query;
    }

    /**
     * The return type is one of, NoReturn, ReturnOneRow or ReturnManyRows.
     */
    public int getReturnType() {
        return returnType;
    }

    /**
     * The return type is one of, NoReturn, ReturnOneRow or ReturnManyRows.
     */
    public boolean isCursorReturned() {
        return this.returnType == RETURN_CURSOR;
    }

    /**
     * Returns true if this call returns from a statement.execute call.
     */
    public boolean isExecuteUpdate() {
        return this.returnType == EXECUTE_UPDATE;
    }

    /**
     * Return whether all the results of the call have been returned.
     */
    public boolean isFinished() {
        return !isCursorReturned() && !isExecuteUpdate();
    }

    /**
     * The return type is one of, NoReturn, ReturnOneRow or ReturnManyRows.
     */
    public boolean isNothingReturned() {
        return this.returnType == NO_RETURN;
    }

    /**
     * The return type is one of, NoReturn, ReturnOneRow or ReturnManyRows.
     */
    public boolean isOneRowReturned() {
        return this.returnType == RETURN_ONE_ROW;
    }

    public boolean isSQLCall() {
        return false;
    }

    public boolean isStoredPLSQLFunctionCall() {
        return false;
    }

    public boolean isStoredPLSQLProcedureCall() {
        return false;
    }

    public boolean isStoredFunctionCall() {
        return false;
    }

    public boolean isStoredProcedureCall() {
        return false;
    }

    public boolean isJPQLCall() {
        return false;
    }

    public boolean isEISInteraction() {
        return false;
    }

    public boolean isQueryStringCall() {
        return false;
    }

    /**
     * Allow pre-printing of the query/SQL string for fully bound calls, to save from reprinting.
     */
    public void prepare(AbstractSession session) {
        setIsPrepared(true);
    }

    /**
     * Cursor return is used for cursored streams.
     */
    public void returnCursor() {
        setReturnType(RETURN_CURSOR);
    }

    /**
     * Indicates that this call will return a boolean value from an execute()
     * call.
     */
    public void setExecuteUpdate() {
        setReturnType(EXECUTE_UPDATE);
    }

    /**
     * Return if the call's return type has been set.
     */
    public boolean isReturnSet() {
        return this.returnType != 0;
    }

    /**
     * Many rows are returned for read-all queries.
     */
    public void returnManyRows() {
        setReturnType(RETURN_MANY_ROWS);
    }

    /**
     * No return is used for modify calls like insert / update / delete.
     */
    public void returnNothing() {
        setReturnType(NO_RETURN);
    }

    /**
     * One row is returned for read-object queries.
     */
    public void returnOneRow() {
        setReturnType(RETURN_ONE_ROW);
    }

    /**
     * Back reference to query, unfortunately required for events.
     */
    public void setQuery(DatabaseQuery query) {
        this.query = query;
    }

    /**
     * The return type is one of, NoReturn, ReturnOneRow or ReturnManyRows.
     */
    public void setReturnType(int returnType) {
        this.returnType = returnType;
    }

    /**
     * Allow the call to translate from the translation for predefined calls.
     */
    public void translate(AbstractRecord translationRow, AbstractRecord modifyRow, AbstractSession session) {
        //do nothing by default.
    }

    /**
     * Return the query string of the call.
     * This must be overwritten by subclasses that support query language translation (SQLCall, XQueryCall).
     */
    public String getQueryString() {
        return "";
    }

    /**
     * Set the query string of the call.
     * This must be overwritten by subclasses that support query language translation (SQLCall, XQueryCall).
     */
    public void setQueryString(String queryString) {
        // Nothing by default.
    }

    /**
     * INTERNAL:
     * Parse the query string for # markers for custom query based on a query language.
     * This is used by SQLCall and XQuery call, but can be reused by other query languages.
     */
    public void translateCustomQuery() {
        if (this.shouldProcessTokenInQuotes) {
            if (getQueryString().indexOf(this.query.getParameterDelimiter()) == -1) {
                if (this.getQuery().shouldBindAllParameters() && getQueryString().indexOf("?") == -1) {
                    return;
                }
                translatePureSQLCustomQuery();
                return;
            }
        } else {
            if (!hasArgumentMark(getQueryString(), this.query.getParameterDelimiterChar(), '\'')
                    || !hasArgumentMark(getQueryString(), this.query.getParameterDelimiterChar(), '\"')
                    || !hasArgumentMark(getQueryString(), this.query.getParameterDelimiterChar(), '`')) {
                if (this.getQuery().shouldBindAllParameters() && !hasArgumentMark(getQueryString(),'?', '\'')) {
                    return;
                }
                translatePureSQLCustomQuery();
                return;
            }
        }

        int lastIndex = 0;
        String queryString = getQueryString();
        Writer writer = new CharArrayWriter(queryString.length() + 50);
        try {
            // ** This method is heavily optimized do not touch anything unless you "know" what your doing.
            while (lastIndex != -1) {
                int poundIndex = queryString.indexOf(this.query.getParameterDelimiterChar(), lastIndex);
                String token;
                if (poundIndex == -1) {
                    token = queryString.substring(lastIndex, queryString.length());
                    lastIndex = -1;
                } else {
                    if(this.shouldProcessTokenInQuotes){//Always process token no matter whether the quotes around it or not.
                        token = queryString.substring(lastIndex, poundIndex);
                    }else{
                        boolean hasPairedQuoteBeforePound = true;
                        int quotePairIndex=poundIndex;

                        do{
                            quotePairIndex=queryString.lastIndexOf('\'',quotePairIndex-1);
                            if(quotePairIndex!=-1 && quotePairIndex > lastIndex){
                                hasPairedQuoteBeforePound = !hasPairedQuoteBeforePound;
                            } else {
                               break;
                            }
                        }while(true);

                        int endQuoteIndex = -1;
                        if(!hasPairedQuoteBeforePound){//There is begin quote, so search end quote.
                            endQuoteIndex = queryString.indexOf('\'', poundIndex+1);
                        }
                        if(endQuoteIndex!=-1){//There is quote around pound.
                            token = queryString.substring(lastIndex, endQuoteIndex+1);
                            poundIndex=-1;
                            lastIndex = endQuoteIndex + 1;
                        } else { //No quote around pound,
                            token = queryString.substring(lastIndex, poundIndex);
                            lastIndex = poundIndex + 1;
                        }
                    }
                }
                writer.write(token);
                if (poundIndex != -1) {
                    int wordEndIndex = poundIndex + 1;
                    while ((wordEndIndex < queryString.length()) && (whitespace().indexOf(queryString.charAt(wordEndIndex)) == -1)) {
                        wordEndIndex = wordEndIndex + 1;
                    }

                    // Check for ## which means field from modify row.
                    if (queryString.charAt(poundIndex + 1) == this.query.getParameterDelimiterChar()) {
                        // Check for ### which means OUT parameter type.
                        if (queryString.charAt(poundIndex + 2) == this.query.getParameterDelimiterChar()) {
                            // Check for #### which means INOUT parameter type.
                            if (queryString.charAt(poundIndex + 3) == this.query.getParameterDelimiterChar()) {
                                String fieldName = queryString.substring(poundIndex + 4, wordEndIndex);
                                DatabaseField field = createField(fieldName);
                                appendInOut(writer, field);
                            } else {
                                String fieldName = queryString.substring(poundIndex + 3, wordEndIndex);
                                DatabaseField field = createField(fieldName);
                                appendOut(writer, field);
                            }
                        } else {
                            String fieldName = queryString.substring(poundIndex + 2, wordEndIndex);
                            DatabaseField field = createField(fieldName);
                            appendModify(writer, field);
                        }
                    } else {
                        String fieldName = queryString.substring(poundIndex + 1, wordEndIndex);
                        DatabaseField field = createField(fieldName);
                        appendIn(writer, field);
                    }
                    lastIndex = wordEndIndex;
                }
            }
            setQueryString(writer.toString());
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }

    /**
     * INTERNAL:
     * Parse the query string for ? markers for custom query based on a query language.
     * This is used by SQLCall and XQuery call, but can be reused by other query languages.
     */
    public void translatePureSQLCustomQuery() {
        int lastIndex = 0;
        String queryString = getQueryString();
        int parameterIndex = 1; // this is the parameter index
        Writer writer = new CharArrayWriter(queryString.length() + 50);
        try {
            // ** This method is heavily optimized do not touch anything unless you "know" what your doing.
            while (lastIndex != -1) {
                int markIndex = queryString.indexOf('?', lastIndex);
                String token;
                if (markIndex == -1) { // did not find question mark then we are done looking
                    token = queryString.substring(lastIndex, queryString.length()); //write rest of sql
                    lastIndex = -1;
                } else {
                    if(this.shouldProcessTokenInQuotes){
                        token = queryString.substring(lastIndex, markIndex);
                        lastIndex = markIndex + 1;
                    }else{
                        boolean hasPairedQuoteBeforeMark = true;
                        int quotePairIndex=markIndex;
                        do{
                            quotePairIndex=queryString.lastIndexOf('\'',quotePairIndex-1);
                            if(quotePairIndex!=-1 && quotePairIndex > lastIndex){
                                hasPairedQuoteBeforeMark = !hasPairedQuoteBeforeMark;
                            } else {
                               break;
                            }
                        }while(true);

                        int endQuoteIndex = -1;
                        if(!hasPairedQuoteBeforeMark){//There is begin quote, so search end quote.
                            endQuoteIndex = queryString.indexOf('\'', markIndex+1);
                        }
                        if(endQuoteIndex!=-1){//There is quote around mark.
                            token = queryString.substring(lastIndex, endQuoteIndex+1);
                            markIndex=-1;
                            lastIndex = endQuoteIndex + 1;
                        }else{
                            //if no quote around the mark, write the rest of sql.
                            token = queryString.substring(lastIndex, markIndex);
                            lastIndex = markIndex + 1;
                        }
                    }
                }
                writer.write(token);
                if (markIndex != -1) {  // found the question mark now find the named token
                    int wordEndIndex = markIndex + 1;
                    while ((wordEndIndex < queryString.length()) && (whitespace().indexOf(queryString.charAt(wordEndIndex)) == -1)) {
                        wordEndIndex = wordEndIndex + 1;
                    }
                    if (wordEndIndex > markIndex + 1){ //found a 'name' for this token (may be positional)
                        String fieldName = queryString.substring(markIndex + 1, wordEndIndex);
                        DatabaseField field = createField(fieldName);
                        appendIn(writer, field);
                        lastIndex = wordEndIndex;
                    }else{
                        DatabaseField field = createField(String.valueOf(parameterIndex));
                        parameterIndex++;
                        appendIn(writer, field);
                    }
                }
            }
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
        setQueryString(writer.toString());
    }

    /**
     * INTERNAL:
     * Create a new Database Field
     * This method can be overridden by subclasses to return other field types
     */
    protected DatabaseField createField(String fieldName) {
        return new DatabaseField(fieldName);
    }

    /**
     * INTERNAL:
     * All values are printed as ? to allow for parameter binding or translation during the execute of the call.
     */
    public void appendLiteral(Writer writer, Object literal) {
        try {
            writer.write(argumentMarker());
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
        appendLiteral(literal);
    }

    /**
     * INTERNAL:
     * All values are printed as ? to allow for parameter binding or translation during the execute of the call.
     */
    public void appendTranslation(Writer writer, DatabaseField modifyField) {
        try {
            writer.write(argumentMarker());
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
        appendTranslation(modifyField);
    }

    /**
     * INTERNAL:
     * All values are printed as ? to allow for parameter binding or translation during the execute of the call.
     */
    public void appendModify(Writer writer, DatabaseField modifyField) {
        try {
            writer.write(argumentMarker());
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
        appendModify(modifyField);
    }

    /**
     * INTERNAL:
     * All values are printed as ? to allow for parameter binding or translation during the execute of the call.
     */
    public void appendIn(Writer writer, DatabaseField field) {
        try {
            writer.write(argumentMarker());
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
        appendIn(field);
    }

    /**
     * INTERNAL:
     * All values are printed as ? to allow for parameter binding or translation during the execute of the call.
     */
    public void appendInOut(Writer writer, DatabaseField inoutField) {
        try {
            writer.write(argumentMarker());
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
        appendInOut(inoutField);
    }

    /**
     * INTERNAL:
     * All values are printed as ? to allow for parameter binding or translation during the execute of the call.
     */
    public void appendOut(Writer writer, DatabaseField outField) {
        try {
            writer.write(argumentMarker());
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
        appendOut(outField);
    }

    /**
     * INTERNAL:
     */
    public void appendLiteral(Object literal) {
        getParameters().add(literal);
        getParameterTypes().add(ParameterType.LITERAL);
        getParameterBindings().add(true);
    }

    /**
     * INTERNAL:
     */
    public void appendLiteral(Object literal, Boolean shouldBind) {
        getParameters().add(literal);
        getParameterTypes().add(ParameterType.LITERAL);
        getParameterBindings().add(shouldBind);
    }

    /**
     * INTERNAL:
     */
    public void appendTranslation(DatabaseField modifyField) {
        getParameters().add(modifyField);
        getParameterTypes().add(ParameterType.TRANSLATION);
        getParameterBindings().add(true);
    }

    /**
     * INTERNAL:
     */
    public void appendTranslation(DatabaseField modifyField, Boolean shouldBind) {
        getParameters().add(modifyField);
        getParameterTypes().add(ParameterType.TRANSLATION);
        getParameterBindings().add(shouldBind);
    }

    /**
     * INTERNAL:
     */
    public void appendModify(DatabaseField modifyField) {
        getParameters().add(modifyField);
        getParameterTypes().add(ParameterType.MODIFY);
        getParameterBindings().add(true);
    }

    /**
     * INTERNAL:
     */
    public void appendModify(DatabaseField modifyField, Boolean shouldBind) {
        getParameters().add(modifyField);
        getParameterTypes().add(ParameterType.MODIFY);
        getParameterBindings().add(shouldBind);
    }

    /**
     * INTERNAL:
     */
    public void appendIn(Object inObject) {
        getParameters().add(inObject);
        getParameterTypes().add(ParameterType.IN);
        getParameterBindings().add(true);
    }

    /**
     * INTERNAL:
     */
    public void appendIn(Object inObject, Boolean shouldBind) {
        getParameters().add(inObject);
        getParameterTypes().add(ParameterType.IN);
        getParameterBindings().add(shouldBind);
    }

    /**
     * INTERNAL:
     */
    public void appendInOut(DatabaseField inoutField) {
        Object[] inOut = { inoutField, inoutField };
        getParameters().add(inOut);
        getParameterTypes().add(ParameterType.INOUT);
        getParameterBindings().add(true);
    }

    /**
     * INTERNAL:
     */
    public void appendInOut(DatabaseField inoutField, Boolean shouldBind) {
        Object[] inOut = { inoutField, inoutField };
        getParameters().add(inOut);
        getParameterTypes().add(ParameterType.INOUT);
        getParameterBindings().add(shouldBind);
    }

    /**
     * INTERNAL:
     */
    public void appendInOut(Object inValueOrField, DatabaseField outField) {
        Object[] inOut = { inValueOrField, outField };
        getParameters().add(inOut);
        getParameterTypes().add(ParameterType.INOUT);
        getParameterBindings().add(true);
    }

    /**
     * INTERNAL:
     */
    public void appendInOut(Object inValueOrField, DatabaseField outField, Boolean shouldBind) {
        Object[] inOut = { inValueOrField, outField };
        getParameters().add(inOut);
        getParameterTypes().add(ParameterType.INOUT);
        getParameterBindings().add(shouldBind);
    }

    /**
     * INTERNAL:
     */
    public void appendOut(DatabaseField outField) {
        getParameters().add(outField);
        getParameterTypes().add(ParameterType.OUT);
        getParameterBindings().add(true);
    }

    /**
     * INTERNAL:
     */
    public void appendOut(DatabaseField outField, Boolean shouldBind) {
        getParameters().add(outField);
        getParameterTypes().add(ParameterType.OUT);
        getParameterBindings().add(shouldBind);
    }

    /**
     * INTERNAL:
     */
    public void appendOutCursor(DatabaseField outField) {
        getParameters().add(outField);
        getParameterTypes().add(ParameterType.OUT_CURSOR);
        getParameterBindings().add(true);
        getOutputCursors().add(outField);
    }

    /**
     * Add the parameter.
     * If using binding bind the parameter otherwise let the platform print it.
     * The platform may also decide to bind the value.
     */
    public void appendOutCursor(DatabaseField outField, Boolean shouldBind) {
        getParameters().add(outField);
        getParameterTypes().add(ParameterType.OUT_CURSOR);
        getParameterBindings().add(shouldBind);
        getOutputCursors().add(outField);
    }

    /**
     * Add the parameter using the DatasourcePlatform.
     */
    public void appendParameter(Writer writer, Object parameter, boolean shouldBind, AbstractSession session) {
        session.getDatasourcePlatform().appendParameter(this, writer, parameter);
    }

    /**
     * INTERNAL:
     * Return the character to use for the argument marker.
     * ? is used in SQL, however other query languages such as XQuery need to use other markers.
     */
    protected char argumentMarker() {
        return '?';
    }

    /**
     * INTERNAL:
     * Return the characters that represent non-arguments names.
     */
    protected String whitespace() {
        return ",); \n\t:";
    }

    /**
     * INTERNAL:
     * Allow the call to translate from the translation for predefined calls.
     */
    public void translateQueryString(AbstractRecord translationRow, AbstractRecord modifyRow, AbstractSession session) {
      //has a '?'
        if ((this.parameters == null) || getParameters().isEmpty()) {
            //has no parameters
            return;
        }
        if (getQueryString().indexOf(argumentMarker()) == -1) {
            return;
        }

        int lastIndex = 0;
        int parameterIndex = 0;
        String queryString = getQueryString();
        Writer writer = new CharArrayWriter(queryString.length() + 50);
        try {
            // PERF: This method is heavily optimized do not touch anything unless you know "very well" what your doing.
            // Must translate field parameters and may get new bound parameters for large data.
            List<Object> parameterFields = getParameters();
            List<ParameterType> parameterTypes = getParameterTypes();
            setParameters(new ArrayList<Object>(parameterFields.size()));
            while (lastIndex != -1) {
                int tokenIndex = queryString.indexOf(argumentMarker(), lastIndex);
                String token;
                if (tokenIndex == -1) {
                    token = queryString.substring(lastIndex, queryString.length());
                    lastIndex = -1;
                } else {
                    if (this.shouldProcessTokenInQuotes) {
                        token = queryString.substring(lastIndex, tokenIndex);
                    } else {
                        boolean hasPairedQuoteBeforeMark = true;
                        int quotePairIndex = tokenIndex;
                        do {
                            quotePairIndex = queryString.lastIndexOf('\'', quotePairIndex - 1);
                            if (quotePairIndex != -1 && quotePairIndex > lastIndex){
                                hasPairedQuoteBeforeMark = !hasPairedQuoteBeforeMark;
                            } else {
                                break;
                            }
                        } while (true);

                        int endQuoteIndex = -1;
                        if (!hasPairedQuoteBeforeMark) { // there is a begin quote, so search for end quote.
                            endQuoteIndex = queryString.indexOf('\'', tokenIndex + 1);
                        }
                        if (endQuoteIndex != -1) { // there is a quote around the mark.
                            token = queryString.substring(lastIndex, endQuoteIndex + 1);
                            tokenIndex = -1;
                            lastIndex = endQuoteIndex + 1;
                        } else {
                            // if no quote around the mark, write the rest of sql.
                            token = queryString.substring(lastIndex, tokenIndex);
                            lastIndex = tokenIndex + 1;
                        }
                    }
                }
                writer.write(token);
                if (tokenIndex != -1) {
                    // Process next parameter.

                    DatabaseField field = null;
                    Object value = null;
                    ParameterType parameterType = parameterTypes.get(parameterIndex);
                    Object parameter = parameterFields.get(parameterIndex);

                    switch(parameterType) {
                        case MODIFY:
                            field = (DatabaseField)parameter;
                            value = modifyRow.get(field);
                            appendParameter(writer, value, false, session);
                            break;
                        case CUSTOM_MODIFY:
                            field = (DatabaseField)parameter;
                            value = modifyRow.get(field);
                            if (value != null) {
                                value = session.getDatasourcePlatform().getCustomModifyValueForCall(this, value, field, false);
                                //Bug#5200826 needs use unwrapped connection.
                                if ((value instanceof BindCallCustomParameter) && ((BindCallCustomParameter)value).shouldUseUnwrappedConnection()){
                                    this.isNativeConnectionRequired=true;
                                }
                            }
                            appendParameter(writer, value, false, session);
                            break;
                        case TRANSLATION:
                            value = null;
                            // Parameter expressions are used for nesting and correct mapping conversion of the value.
                            if (parameter instanceof ParameterExpression) {
                                value = ((ParameterExpression)parameter).getValue(translationRow, getQuery(), session);
                            } else {
                                field = (DatabaseField)parameter;
                                value = translationRow.get(field);
                                // Must check for the modify row as well for custom SQL compatibility as only one # is required.
                                if ((value == null) && (modifyRow != null)) {
                                    value = modifyRow.get(field);
                                }
                            }
                            appendParameter(writer, value, false, session);
                            break;
                        case LITERAL:
                            if (parameter instanceof DatabaseField) {
                                parameter = null;
                            }
                            appendParameter(writer, parameter, false, session);
                            break;
                        case IN:
                            value = getValueForInParameter(parameter, translationRow, modifyRow, session, false);
                            appendParameter(writer, value, false, session);
                            break;
                        case INOUT:
                            value = getValueForInOutParameter(parameter, translationRow, modifyRow, session);
                            appendParameter(writer, value, false, session);
                            break;
                        case OUT:
                        case OUT_CURSOR:
                            if (parameter instanceof DatabaseField) {
                                parameter = null;
                            }
                            appendParameter(writer, parameter, false, session);
                            break;
                    }
                    lastIndex = tokenIndex + 1;
                    parameterIndex++;
                }
            }

            setQueryString(writer.toString());

        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }

    /**
     * INTERNAL:
     * Allow the call to translate from the translation for predefined calls.
     */
    public void translateQueryStringAndBindParameters(AbstractRecord translationRow, AbstractRecord modifyRow, AbstractSession session) {
        List<Object> parameters = getParameters();

        // This call has no parameters
        if ((parameters == null) || parameters.isEmpty()) {
            return;
        }

        String marker = "" + argumentMarker();
        StringBuilder queryString = new StringBuilder(getQueryString());
        // The string has no argument markers
        if (queryString.indexOf(marker) == -1) {
            return;
        }

        int lastIndex = 0;
        int tokenIndex = -1;

        boolean hasParameterizedIN = false;
        int size = parameters.size();
        List<Object> translatedParametersValues = new ArrayList<Object>(size);

        Writer writer = new CharArrayWriter(queryString.length() + 50);
        try {
            // PERF: This method is heavily optimized do not touch anything unless you know "very well" what your doing.
            // Must translate field parameters and may get new bound parameters for large data.
            List<Object> parameterFields = getParameters();
            List<ParameterType> parameterTypes = getParameterTypes();
            List<Boolean> canBindParameters = getParameterBindings();

            // clear the parameters list 
            setParameters(new ArrayList<Object>(parameterFields.size()));

            for (int parameterIndex = 0; parameterIndex < size; parameterIndex++) {
                tokenIndex = queryString.indexOf(marker, tokenIndex + 1);
                if (!this.shouldProcessTokenInQuotes) {

                    // Look for a parameter marker NOT inside quotes
                    do {
                        boolean hasPairedQuoteBeforeMark = true;
                        int quotePairIndex = tokenIndex;

                        // First, check if current mark is inside quotes
                        do {
                            quotePairIndex = queryString.lastIndexOf(String.valueOf('\''), quotePairIndex - 1);
                            if (quotePairIndex != -1 && quotePairIndex > lastIndex) {
                                hasPairedQuoteBeforeMark = !hasPairedQuoteBeforeMark;
                            } else {
                                break;
                            }
                        } while (true);

                        int endQuoteIndex = -1;
                        if (!hasPairedQuoteBeforeMark) { 
                            // All the quotes in front of current mark are not paired, so we should be inside quotes
                            endQuoteIndex = queryString.indexOf(String.valueOf('\''), tokenIndex + 1);
                        }

                        if (endQuoteIndex != -1) { 
                            // there is a quote around the mark, so find the next mark and try again
                            tokenIndex = queryString.indexOf(marker, tokenIndex + 1);
                        } else {
                            // we aren't inside quotes, so we're done
                            break;
                        }
                    } while (true);
                }

                DatabaseField field = null;
                Object translatedValue = null;
                Object parameterValue = parameterFields.get(parameterIndex);
                ParameterType parameterType = parameterTypes.get(parameterIndex);
                Boolean canBind = canBindParameters.get(parameterIndex);

                switch(parameterType) {
                    case MODIFY: 
                        field = (DatabaseField) parameterValue;
                        translatedValue = modifyRow.get(field);

                        // If the value is null, the field is passed as the value so the type can be obtained from the field.
                        if (translatedValue == null) {
                            // The field from the modify row is used, as the calls field may not have the type,
                            // but if the field is missing the calls field may also have the type.
                            translatedValue = modifyRow.getField(field);
                            if (translatedValue == null) {
                                translatedValue = field;
                            }
                        }

                        // If the parameter doesn't allow binding, we have to append this translated 
                        // parameter value into the query string
                        if(Boolean.FALSE.equals(canBind)) {
                            String token = queryString.substring(lastIndex, tokenIndex);
                            writer.write(token);
                            lastIndex = tokenIndex + 1;
                            appendParameter(writer, translatedValue, false, session);
                        } else {
                            translatedParametersValues.add(translatedValue);
                        }

                        break;
                    case CUSTOM_MODIFY: 
                        field = (DatabaseField) parameterValue;
                        translatedValue = modifyRow.get(field);
                        translatedValue = session.getPlatform().getCustomModifyValueForCall(this, translatedValue, field, true);
                        //Bug#8200836 needs use unwrapped connection
                        if ((translatedValue != null) && (translatedValue instanceof BindCallCustomParameter) &&  (((BindCallCustomParameter)translatedValue).shouldUseUnwrappedConnection())){
                            this.isNativeConnectionRequired=true;
                        }

                        // If the value is null, the field is passed as the value so the type can be obtained from the field.
                        if (translatedValue == null) {
                            // The field from the modify row is used, as the calls field may not have the type,
                            // but if the field is missing the calls field may also have the type.
                            translatedValue = modifyRow.getField(field);
                            if (translatedValue == null) {
                                translatedValue = field;
                            }
                        }

                        // If the parameter doesn't allow binding, we have to append this translated 
                        // parameter value into the query string
                        if(Boolean.FALSE.equals(canBind)) {
                            String token = queryString.substring(lastIndex, tokenIndex);
                            writer.write(token);
                            lastIndex = tokenIndex + 1;
                            appendParameter(writer, translatedValue, false, session);
                        } else {
                            translatedParametersValues.add(translatedValue);
                        }

                        break;
                    case TRANSLATION: 
                        if (parameterValue instanceof ParameterExpression) {
                            field = ((ParameterExpression) parameterValue).getField();
                            translatedValue = ((ParameterExpression) parameterValue).getValue(translationRow, query, session);
                        } else {
                            field = (DatabaseField)parameterValue;
                            translatedValue = translationRow.get(field);
                            if (translatedValue == null) {// Backward compatibility double check.
                                translatedValue = modifyRow.get(field);
                            }
                        }

                        if (translatedValue instanceof Collection && !Boolean.FALSE.equals(canBind)) {
                            // Must re-translate IN parameters.
                            hasParameterizedIN = true;
                        }

                        // If the value is null, the field is passed as the value so the type can be obtained from the field.
                        if ((translatedValue == null) && (field != null)) {
                            if (!this.query.hasNullableArguments() || !this.query.getNullableArguments().contains(field)) {
                                translatedValue = translationRow.getField(field);
                                // The field from the row is used, as the calls field may not have the type,
                                // but if the field is missing the calls field may also have the type.
                                if (translatedValue == null) {
                                    translatedValue = field;
                                }

                                // If the parameter doesn't allow binding, we have to append this translated 
                                // parameter value into the query string
                                if(Boolean.FALSE.equals(canBind)) {
                                    String token = queryString.substring(lastIndex, tokenIndex);
                                    writer.write(token);
                                    lastIndex = tokenIndex + 1;
                                    appendParameter(writer, translatedValue, false, session);
                                } else {
                                    translatedParametersValues.add(translatedValue);
                                }
                            }
                        } else {
                            // If the parameter doesn't allow binding, we have to append this translated 
                            // parameter value into the query string
                            if(Boolean.FALSE.equals(canBind)) {
                                String token = queryString.substring(lastIndex, tokenIndex);
                                writer.write(token);
                                lastIndex = tokenIndex + 1;
                                appendParameter(writer, translatedValue, false, session);
                            } else {
                                translatedParametersValues.add(translatedValue);
                            }
                        }
                        break;
                    case LITERAL: 
                        translatedValue = parameterValue;

                        // If the parameter doesn't allow binding, we have to append this translated 
                        // parameter value into the query string
                        if(Boolean.FALSE.equals(canBind)) {
                            String token = queryString.substring(lastIndex, tokenIndex);
                            writer.write(token);
                            lastIndex = tokenIndex + 1;

                            if (parameterValue instanceof DatabaseField) {
                                translatedValue = null;
                            }
                            appendParameter(writer, translatedValue, false, session);
                        } else {
                            translatedParametersValues.add(translatedValue);
                        }
                        break;
                    case IN: 
                        translatedValue = getValueForInParameter(parameterValue, translationRow, modifyRow, session, true);
                        // Returning this means the parameter was optional and should not be included.
                        if (translatedValue != this) {
                            // If the parameter doesn't allow binding, we have to append this translated 
                            // parameter value into the query string
                            if(Boolean.FALSE.equals(canBind)) {
                                String token = queryString.substring(lastIndex, tokenIndex);
                                writer.write(token);
                                lastIndex = tokenIndex + 1;
                                appendParameter(writer, translatedValue, false, session);
                            } else {
                                translatedParametersValues.add(translatedValue);
                            }
                        }
                        break;
                    case INOUT: 
                        translatedValue = getValueForInOutParameter(parameterValue, translationRow, modifyRow, session);

                        // If the parameter doesn't allow binding, we have to append this translated 
                        // parameter value into the query string
                        if(Boolean.FALSE.equals(canBind)) {
                            String token = queryString.substring(lastIndex, tokenIndex);
                            writer.write(token);
                            lastIndex = tokenIndex + 1;
                            appendParameter(writer, translatedValue, false, session);
                        } else {
                            translatedParametersValues.add(translatedValue);
                        }
                        break;
                    case OUT: 
                    case OUT_CURSOR: 
                        if (parameterValue != null && parameterValue instanceof OutputParameterForCallableStatement) {
                            ((OutputParameterForCallableStatement) parameterValue).getOutputField().setIndex(parameterIndex);
                        }

                        // If the parameter doesn't allow binding, we have to append this translated 
                        // parameter value into the query string
                        if(Boolean.FALSE.equals(canBind)) {
                            String token = queryString.substring(lastIndex, tokenIndex);
                            writer.write(token);
                            lastIndex = tokenIndex + 1;
                            appendParameter(writer, translatedValue, false, session);
                        } else {
                            translatedParametersValues.add(translatedValue);
                        }
                        break;
                }
            }

            if(writer.toString().length() > 0) {
                String token = queryString.substring(lastIndex);
                writer.write(token);
                setQueryString(writer.toString());
            }
            if(translatedParametersValues.size() > 0) {
                setParameters(translatedParametersValues);
            }

            // If an IN parameter was found must translate SQL.
            if (hasParameterizedIN) {
                translateQueryStringForParameterizedIN(translationRow, modifyRow, session);
            }
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
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

                            int limit = ((DatasourcePlatform)session.getDatasourcePlatform()).getINClauseLimit();
                            //The database platform has a limit for the IN clause so we need to reformat the clause
                            if(limit > 0) {
                                boolean not = token.endsWith(" NOT IN ");
                                String subToken = token.substring(0, token.length() - (not ? " NOT IN " : " IN ").length());
                                int spaceIndex = subToken.lastIndexOf(' ');
                                int braceIndex = subToken.lastIndexOf('(');
                                String fieldName = subToken.substring((spaceIndex > braceIndex ? spaceIndex : braceIndex) + 1);
                                String inToken = not ? ") AND " + fieldName + " NOT IN (" : ") OR " + fieldName + " IN (";

                                for (int index = 0; index < size; index++) {
                                    writer.write("?");
                                    if ((index + 1) < size) {
                                        if (index > 0 && (index + 1) % limit == 0) {
                                            writer.write(inToken);
                                        } else  {
                                            writer.write(",");
                                        }
                                    }
                                }
                            } else {
                                for (int index = 0; index < size; index++) {
                                    writer.write("?");
                                    if ((index + 1) < size) {
                                        writer.write(",");
                                    }
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
     * INTERNAL:
     * Returns value for IN parameter. Called by translate and translateSQLString methods.
     * In case shouldBind==true tries to return a DatabaseField with type instead of null,
     * returns null only in case no DatabaseField with type was found (case sensitive).
     */
    protected Object getValueForInParameter(Object parameter, AbstractRecord translationRow, AbstractRecord modifyRow, AbstractSession session, boolean shouldBind) {
        Object value = parameter;
        DatabaseField field = null;
        boolean isNull = false;

        // Parameter expressions are used for nesting and correct mapping conversion of the value.
        if (parameter instanceof ParameterExpression) {
            value = ((ParameterExpression)parameter).getValue(translationRow, getQuery(), session);
            field = ((ParameterExpression)parameter).getField();
        } else if (parameter instanceof DatabaseField) {
            field = (DatabaseField)parameter;
            value = translationRow.get(field);
            // Must check for the modify row as well for custom SQL compatibility as only one # is required.
            if (modifyRow != null) {
                if (value == null) {
                    value = modifyRow.get(field);
                }
                if (value != null) {
                    DatabaseField modifyField = modifyRow.getField(field);
                    if (modifyField != null) {
                        if (session.getDatasourcePlatform().shouldUseCustomModifyForCall(modifyField)) {
                            value = session.getDatasourcePlatform().getCustomModifyValueForCall(this, value, modifyField, shouldBind);
                        }
                    }
                }
            }
            if (value == null && shouldBind) {
                isNull = true;
                if ((field.getType() != null) ||(field.getSqlType()!= DatabaseField.NULL_SQL_TYPE)){
                    value = field;
                } else if (modifyRow != null) {
                    DatabaseField modifyField = modifyRow.getField(field);
                    if ((modifyField != null) && (modifyField.getType() != null)) {
                        value = modifyField;
                    }
                }
                if (value == null) {
                    DatabaseField translationField = translationRow.getField(field);
                    if (translationField == null){
                        session.log(SessionLog.WARNING, SessionLog.SQL, "named_argument_not_found_in_query_parameters", new Object[]{field});
                    }
                    if ((translationField != null) && (translationField.getType() != null)) {
                        value = translationField;
                    }
                }
            } else {
                if (parameter instanceof ObjectRelationalDatabaseField){
                    value = new InParameterForCallableStatement(value, (DatabaseField)parameter);
                }
            }
        }
        if ((value == null || isNull) && this.query.hasNullableArguments() && this.query.getNullableArguments().contains(field)) {
            return this;
        }
        return value;
    }

    /**
     * INTERNAL:
     * Returns value for INOUT parameter. Called by translate and translateSQLString methods.
     */
    protected Object getValueForInOutParameter(Object parameter, AbstractRecord translationRow, AbstractRecord modifyRow, AbstractSession session) {
        // parameter ts an array of two Objects: inParameter and outParameter
        Object inParameter = ((Object[])parameter)[0];
        Object inValue = getValueForInParameter(inParameter, translationRow, modifyRow, session, true);
        Object outParameter = ((Object[])parameter)[1];
        return createInOutParameter(inValue, outParameter, session);
    }

    /**
     * INTERNAL:
     * Returns INOUT parameter. Called by getValueForInOutParameter method.
     * Descendants may override this method.
     */
    protected Object createInOutParameter(Object inValue, Object outParameter, AbstractSession session) {
        Object[] inOut = { inValue, outParameter };
        return inOut;
    }

    /**
     * Return true if the specific mark is existing and not quoted around.
     *
     * @param string    string to search
     * @param mark      mark to find
     * @param quote     quote char (usually ' or ")
     */
    private boolean hasArgumentMark(String string, char mark, char quote){
        int quoteIndex = -1;
        int lastEndQuoteIndex = -1;

        do{
            int markIndex=string.indexOf(mark,lastEndQuoteIndex+1);
            if(markIndex==-1){
                return false; //no mark at all.
            }
            quoteIndex = string.lastIndexOf(quote, markIndex);
            if(quoteIndex==-1){//no quote before the mark
                return true;
            }else{//has quote before the mark
                boolean hasPairedQuoteBeforeMark = false;
                while(quoteIndex!=-1 && quoteIndex >= lastEndQuoteIndex){
                    if((quoteIndex=string.lastIndexOf(quote, quoteIndex-1))!=-1){
                        hasPairedQuoteBeforeMark = !hasPairedQuoteBeforeMark;
                    }
                }
                if(hasPairedQuoteBeforeMark){//if there is paired quotes before the mark.
                    return true;
                }else{//might have quotes around the mark, need further check.
                    lastEndQuoteIndex = string.indexOf(quote, markIndex+1);
                    if(lastEndQuoteIndex==-1){
                        return true;//no end quote around the mark.
                    }
                }
            }
            //Upon to here, the current mark is positioning between quotes
            //we need search for the next mark.
        }while(true);
    }

    /**
     * Set if the call requires usage of a native (unwrapped) JDBC connection.
     * This may be required for some Oracle JDBC support when a wrapping DataSource is used.
     */
    public void setIsNativeConnectionRequired(boolean isNativeConnectionRequired) {
        this.isNativeConnectionRequired = isNativeConnectionRequired;
    }

    /**
     * Return if the call requires usage of a native (unwrapped) JDBC connection.
     * This may be required for some Oracle JDBC support when a wrapping DataSource is used.
     */
    public boolean isNativeConnectionRequired() {
        return isNativeConnectionRequired;
    }

    /**
     * INTERNAL:
     * This method is used to correct parameterTypes which are compared to static values using == equality, which changes
     * during serialization/deserialization.
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (parameterTypes !=null) {
            List<ParameterType> newParameterTypes = new ArrayList<ParameterType>(parameterTypes.size());
            for (ParameterType type: parameterTypes){
                if (ParameterType.LITERAL.equals(type)) {
                    newParameterTypes.add(ParameterType.LITERAL);
                } else if (ParameterType.MODIFY.equals(type)) {
                    newParameterTypes.add(ParameterType.MODIFY);
                } else if (ParameterType.TRANSLATION.equals(type)) {
                    newParameterTypes.add(ParameterType.TRANSLATION);
                } else if (ParameterType.CUSTOM_MODIFY.equals(type)) {
                    newParameterTypes.add(ParameterType.CUSTOM_MODIFY);
                } else if (ParameterType.OUT.equals(type)) {
                    newParameterTypes.add(ParameterType.OUT);
                } else if (ParameterType.INOUT.equals(type)) {
                    newParameterTypes.add(ParameterType.INOUT);
                } else if (ParameterType.IN.equals(type)) {
                    newParameterTypes.add(ParameterType.IN);
                } else if (ParameterType.OUT_CURSOR.equals(type)) {
                    newParameterTypes.add(ParameterType.OUT_CURSOR);
                }
            }
            parameterTypes = newParameterTypes;
        }
    }
}
