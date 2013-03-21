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
 *     10/29/2010-2.2 Michael O'Brien 
 *       - 325167: Make reserved # bind parameter char generic to enable native SQL pass through
 *     05/24/2011-2.3 Guy Pelletier 
 *       - 345962: Join fetch query when using tenant discriminator column fails.
 *     07/13/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     08/24/2012-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.internal.databaseaccess;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;
import org.eclipse.persistence.logging.SessionLog;

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
    protected List parameters;

    // The parameter types determine if the parameter is a modify, translation or literal type.
    protected List<Integer> parameterTypes;
    public static final Integer LITERAL = Integer.valueOf(1);
    public static final Integer MODIFY = Integer.valueOf(2);
    public static final Integer TRANSLATION = Integer.valueOf(3);
    public static final Integer CUSTOM_MODIFY = Integer.valueOf(4);
    public static final Integer OUT = Integer.valueOf(5);
    public static final Integer INOUT = Integer.valueOf(6);
    public static final Integer IN = Integer.valueOf(7);
    public static final Integer OUT_CURSOR = Integer.valueOf(8);
    public static final Integer INLINE = Integer.valueOf(9);

    // Store if the call has been prepared.
    protected boolean isPrepared;

    /** Allow connection unwrapping to be configured. */
    protected boolean isNativeConnectionRequired;
    
    //Eclipselink Bug 217745 indicates whether or not the token(#,?) needs to be processed if they are in the quotes.
    protected boolean shouldProcessTokenInQuotes; 

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
    }

    /**
     * The parameters are the values in order of occurrence in the SQL statement.
     * This is lazy initialized to conserve space on calls that have no parameters.
     */
    public List getParameters() {
        if (parameters == null) {
            parameters = new ArrayList();
        }
        return parameters;
    }

    /**
     * The parameter types determine if the parameter is a modify, translation or literal type.
     */
    public List<Integer> getParameterTypes() {
        if (parameterTypes == null) {
            parameterTypes = new ArrayList<Integer>();
        }
        return parameterTypes;
    }

    /**
     * The parameters are the values in order of occurrence in the SQL statement.
     */
    public void setParameters(List parameters) {
        this.parameters = parameters;
    }

    /**
     * The parameter types determine if the parameter is a modify, translation or literal type.
     */
    public void setParameterTypes(List<Integer> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    /**
     * The parameters are the values in order of occurrence in call.
     * This is lazy initialized to conserve space on calls that have no parameters.
     */
    public boolean hasParameters() {
        return (parameters != null) && (!getParameters().isEmpty());
    }

    /**
     * The return type is one of, NoReturn, ReturnOneRow or ReturnManyRows.
     */
    public boolean areManyRowsReturned() {
        return this.returnType == RETURN_MANY_ROWS;
    }

    public boolean isOutputParameterType(Integer parameterType) {
        return (parameterType == OUT) || (parameterType == INOUT) || (parameterType == OUT_CURSOR);
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
        if(this.shouldProcessTokenInQuotes){
            if (getQueryString().indexOf(this.query.getParameterDelimiter()) == -1) {
                if (this.getQuery().shouldBindAllParameters() && getQueryString().indexOf("?") == -1){
                    return;
                }
                translatePureSQLCustomQuery();
                return;
            }
        }else{
            if (!hasArgumentMark(getQueryString(), this.query.getParameterDelimiterChar())) {
                if (this.getQuery().shouldBindAllParameters() && !hasArgumentMark(getQueryString(),'?')){
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
        getParameters().add(literal);
        getParameterTypes().add(LITERAL);
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
        getParameters().add(modifyField);
        getParameterTypes().add(TRANSLATION);
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
        getParameters().add(modifyField);
        getParameterTypes().add(MODIFY);
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
        getParameters().add(field);
        getParameterTypes().add(IN);
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
        Object[] inOut = { inoutField, inoutField };
        getParameters().add(inOut);
        getParameterTypes().add(INOUT);
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
        getParameters().add(outField);
        getParameterTypes().add(OUT);
    }

    /**
     * Add the parameter.
     * If using binding bind the parameter otherwise let the platform print it.
     * The platform may also decide to bind the value.
     */
    public void appendParameter(Writer writer, Object parameter, AbstractSession session) {
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
            List parameterFields = getParameters();
            List<Integer> parameterTypes = getParameterTypes();
            setParameters(new ArrayList(parameterFields.size()));
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
                    Integer parameterType = parameterTypes.get(parameterIndex);
                    Object parameter = parameterFields.get(parameterIndex);
                    if (parameterType == MODIFY) {
                        DatabaseField field = (DatabaseField)parameter;
                        Object value = modifyRow.get(field);
                        appendParameter(writer, value, session);
                    } else if (parameterType == CUSTOM_MODIFY) {
                        DatabaseField field = (DatabaseField)parameter;
                        Object value = modifyRow.get(field);
                        if (value != null) {
                            value = session.getDatasourcePlatform().getCustomModifyValueForCall(this, value, field, false);
                            //Bug#5200826 needs use unwrapped connection.
                            if ((value instanceof BindCallCustomParameter) && ((BindCallCustomParameter)value).shouldUseUnwrappedConnection()){
                                this.isNativeConnectionRequired=true;
                            }
                        }
                        appendParameter(writer, value, session);
                    } else if (parameterType == TRANSLATION) {
                        Object value = null;
                        // Parameter expressions are used for nesting and correct mapping conversion of the value.
                        if (parameter instanceof ParameterExpression) {
                            value = ((ParameterExpression)parameter).getValue(translationRow, getQuery(), session);
                        } else {
                            DatabaseField field = (DatabaseField)parameter;
                            value = translationRow.get(field);
                            // Must check for the modify row as well for custom SQL compatibility as only one # is required.
                            if ((value == null) && (modifyRow != null)) {
                                value = modifyRow.get(field);
                            }
                        }
                        appendParameter(writer, value, session);
                    } else if (parameterType == LITERAL) {
                        if (parameter instanceof DatabaseField) {
                            parameter = null;
                        }
                        appendParameter(writer, parameter, session);
                    } else if (parameterType == IN) {
                        Object value = getValueForInParameter(parameter, translationRow, modifyRow, session, false);
                        appendParameter(writer, value, session);
                    } else if (parameterType == INOUT) {
                        Object value = getValueForInOutParameter(parameter, translationRow, modifyRow, session);
                        appendParameter(writer, value, session);
                    } else if (parameterType == INLINE) {
                        writer.write((String)parameter);
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
     */
    private boolean hasArgumentMark(String string, char mark){
        int quoteIndex = -1;
        int lastEndQuoteIndex = -1;
        
        do{
            int markIndex=string.indexOf(mark,lastEndQuoteIndex+1);
            if(markIndex==-1){
                return false; //no mark at all.
            }
            quoteIndex = string.lastIndexOf('\'',markIndex);
            if(quoteIndex==-1){//no quote before the mark
                return true;
            }else{//has quote before the mark
                boolean hasPairedQuoteBeforeMark = false;
                while(quoteIndex!=-1 && quoteIndex >= lastEndQuoteIndex){
                    if((quoteIndex=string.lastIndexOf('\'',quoteIndex-1))!=-1){
                        hasPairedQuoteBeforeMark = !hasPairedQuoteBeforeMark;
                    }
                }
                if(hasPairedQuoteBeforeMark){//if there is paired quotes before the mark.
                    return true; 
                }else{//might have quotes around the mark, need further check.
                    lastEndQuoteIndex = string.indexOf('\'',markIndex+1);
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
            List<Integer> newParameterTypes = new ArrayList<Integer>(parameterTypes.size());
            for (Integer type: parameterTypes){
                if (LITERAL.equals(type)) {
                    newParameterTypes.add(LITERAL);
                } else if (MODIFY.equals(type)) {
                    newParameterTypes.add(MODIFY);
                } else if (TRANSLATION.equals(type)) {
                    newParameterTypes.add(TRANSLATION);
                } else if (CUSTOM_MODIFY.equals(type)) {
                    newParameterTypes.add(CUSTOM_MODIFY);
                } else if (OUT.equals(type)) {
                    newParameterTypes.add(OUT);
                } else if (INOUT.equals(type)) {
                    newParameterTypes.add(INOUT);
                } else if (IN.equals(type)) {
                    newParameterTypes.add(IN);
                } else if (OUT_CURSOR.equals(type)) {
                    newParameterTypes.add(OUT_CURSOR);
                } 
            }
            parameterTypes = newParameterTypes;
        }
    }
}
