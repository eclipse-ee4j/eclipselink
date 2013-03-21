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
 *     arnaud nauwynck, tware - Bug 274975 - ensure custom sql calls are only translated once
 ******************************************************************************/  
package org.eclipse.persistence.queries;

import java.util.List;
import java.io.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;

/**
 * <b>Purpose</b>: Used as an abstraction of an SQL call.
 * A call is an SQL string with parameters.
 */
public class SQLCall extends DatabaseCall implements QueryStringCall {
    protected boolean hasCustomSQLArguments;
    transient protected boolean isTranslatedCustomQuery;

    /**
     * PUBLIC:
     * Create a new SQL call.
     */
    public SQLCall() {
        super();
        this.hasCustomSQLArguments = false;
        this.isTranslatedCustomQuery = false;
    }

    /**
     * PUBLIC:
     * Create a new SQL call.
	 * Warning: Allowing an unverified SQL string to be passed into this 
	 * method makes your application vulnerable to SQL injection attacks. 
     */
    public SQLCall(String sqlString) {
        this();
        setSQLString(sqlString);
    }

    /**
     * INTERNAL:
     * Set the data passed through setCustomSQLArgumentType and useCustomSQLCursorOutputAsResultSet methods.
     */
    protected void afterTranslateCustomQuery(List updatedParameters, List<Integer> updatedParameterTypes) {
        int size = getParameters().size();
        for (int i = 0; i < size; i++) {
            Integer parameterType = this.parameterTypes.get(i);
            Object parameter = this.parameters.get(i);
            if ((parameterType == MODIFY) || (parameterType == OUT) || (parameterType == OUT_CURSOR) || ((parameterType == IN) && parameter instanceof DatabaseField)) {
                DatabaseField field = afterTranslateCustomQueryUpdateParameter((DatabaseField)parameter, i, parameterType, updatedParameters, updatedParameterTypes);
                if (field!=null){
                    this.parameters.set(i, field);
                }
            } else if (parameterType == INOUT) {
                DatabaseField outField = afterTranslateCustomQueryUpdateParameter((DatabaseField)((Object[])parameter)[1], i, parameterType, updatedParameters, updatedParameterTypes);
                if (outField != null) {
                    if (((Object[])parameter)[0] instanceof DatabaseField){
                        if ( ((Object[])parameter)[0] != ((Object[])parameter)[1] ) {
                            DatabaseField inField = outField.clone();
                            inField.setName( ((DatabaseField)((Object[])parameter)[0]).getName());
                            ((Object[])parameter)[0] = inField;
                        } else {
                            ((Object[])parameter)[0] = outField;
                        }
                    }
                    ((Object[])parameter)[1] = outField;
                }
            } else if ((parameterType == IN) && (parameter instanceof DatabaseField)){
                DatabaseField field = afterTranslateCustomQueryUpdateParameter((DatabaseField)parameter, i, parameterType, updatedParameters, updatedParameterTypes);
                if (field != null) {
                    this.parameters.set(i, field);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Set the data passed through setCustomSQLArgumentType and useCustomSQLCursorOutputAsResultSet methods.
     * This will return the null if the user did not add the field/type using the setCustomSQLArgumentType method
     */
    protected DatabaseField afterTranslateCustomQueryUpdateParameter(DatabaseField field, int index, Integer parameterType, List updatedParameters, List<Integer> updatedParameterTypes) {
        int size = updatedParameters.size();
        for (int j = 0; j < size; j++) {
            DatabaseField updateField = (DatabaseField)updatedParameters.get(j);
            if (field.equals(updateField)) {
                Integer updateParameterType = updatedParameterTypes.get(j);
                if (updateParameterType == null) {
                    return updateField;
                } else if (updateParameterType == OUT_CURSOR) {
                    if (parameterType == OUT) {
                        this.parameterTypes.set(index, OUT_CURSOR);
                        return updateField;
                    } else {
                        throw ValidationException.cannotSetCursorForParameterTypeOtherThanOut(field.getName(), toString());
                    }
                }
                break;
            }
        }
        return null;
    }

    /**
     * INTERNAL:
     * Used to avoid misinterpreting the # in custom SQL.
     */
    public boolean hasCustomSQLArguments() {
        return hasCustomSQLArguments;
    }

    @Override
    public boolean isSQLCall() {
        return true;
    }

    @Override
    public boolean isQueryStringCall() {
        return true;
    }

    /**
     * INTERNAL:
     * Called by prepare method only.
     */
    @Override
    protected void prepareInternal(AbstractSession session) {
        if (hasCustomSQLArguments()) {
            // hold results of setCustomSQLArgumentType and useCustomSQLCursorOutputAsResultSet methods
            List updatedParameters = null;
            List updatedParameterTypes = null;
            if (getParameters().size() > 0) {
                updatedParameters = getParameters();
                setParameters(org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance());
                updatedParameterTypes = getParameterTypes();
                setParameterTypes(org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance());
            }

            translateCustomQuery();

            if (updatedParameters != null) {
                afterTranslateCustomQuery(updatedParameters, updatedParameterTypes);
            }
        }

        super.prepareInternal(session);
    }

    /**
     * INTERNAL:
     * Used to avoid misinterpreting the # in custom SQL.
     */
    public void setHasCustomSQLArguments(boolean hasCustomSQLArguments) {
        this.hasCustomSQLArguments = hasCustomSQLArguments;
    }

    /**
     * PUBLIC:
     * This method should only be used with custom SQL:
     * it sets a type to IN, OUT or INOUT parameter (prefixed with #, ### or #### in custom SQL string).
     */
    public void setCustomSQLArgumentType(String customParameterName, Class type) {
        DatabaseField field = new DatabaseField(customParameterName);
        field.setType(type);
        getParameters().add(field);
        getParameterTypes().add(null);
    }
    
    /**
     * PUBLIC:
     * This method should only be used with custom SQL:
     * it sets a type to IN, OUT or INOUT parameter (prefixed with #, ### or #### in custom SQL string).
     * The argumentFieldName is the field or argument name used in the SQL.
     * The type is the JDBC type code for the parameter.
     */
    public void setCustomSQLArgumentType(String argumentFieldName, int type) {
        DatabaseField field = new DatabaseField(argumentFieldName);
        field.setSqlType(type);
        getParameters().add(field);
        getParameterTypes().add(null);
    }
    
    /**
     * PUBLIC:
     * This method should only be used with custom SQL:
     * it sets a type to IN, OUT or INOUT parameter (prefixed with #, ### or #### in custom SQL string).
     * The argumentFieldName is the field or argument name used in the SQL.
     * The type is the JDBC type code for the parameter.
     * The typeName is the JDBC type name, this may be required for ARRAY or STRUCT types.
     */
    public void setCustomSQLArgumentType(String argumentFieldName, int type, String typeName) {
        ObjectRelationalDatabaseField field = new ObjectRelationalDatabaseField(argumentFieldName);
        field.setSqlType(type);
        field.setSqlTypeName(typeName);
        getParameters().add(field);
        getParameterTypes().add(null);
    }
    
    /**
     * PUBLIC:
     * This method should only be used with custom SQL:
     * it sets a type to IN, OUT or INOUT parameter (prefixed with #, ### or #### in custom SQL string).
     * TThe argumentFieldName is the field or argument name used in the SQL.
     * The type is the JDBC type code for the parameter.
     * The typeName is the JDBC type name, this may be required for ARRAY or STRUCT types.
     * The javaType is the java class to return instead of the ARRAY and STRUCT types if a conversion is possible.
     */
    public void setCustomSQLArgumentType(String argumentFieldName, int type, String typeName, Class javaType) {
        ObjectRelationalDatabaseField field = new ObjectRelationalDatabaseField(argumentFieldName);
        field.setSqlType(type);
        field.setSqlTypeName(typeName);
        field.setType(javaType);
        getParameters().add(field);
        getParameterTypes().add(null);
    }
    
    /**
     * PUBLIC:
     * This method should only be used with custom SQL:
     * it sets a type to IN, OUT or INOUT parameter (prefixed with #, ### or #### in custom SQL string).
     * TThe argumentFieldName is the field or argument name used in the SQL.
     * The type is the JDBC type code for the parameter.
     * The typeName is the JDBC type name, this may be required for ARRAY or STRUCT types.
     * The nestedType is a DatabaseField with type information set to match the VARRAYs object types
     */
    public void setCustomSQLArgumentType(String argumentFieldName, int type, String typeName, DatabaseField nestedType) {
        ObjectRelationalDatabaseField field = new ObjectRelationalDatabaseField(argumentFieldName);
        field.setSqlType(type);
        field.setSqlTypeName(typeName);
        field.setNestedTypeField(nestedType);
        getParameters().add(field);
        getParameterTypes().add(null);
    }
    
    /**
     * PUBLIC:
     * This method should only be used with custom SQL:
     * it sets a type to IN, OUT or INOUT parameter (prefixed with #, ### or #### in custom SQL string).
     * TThe argumentFieldName is the field or argument name used in the SQL.
     * The type is the JDBC type code for the parameter.
     * The typeName is the JDBC type name, this may be required for ARRAY or STRUCT types.
     * The javaType is the java class to return instead of the ARRAY and STRUCT types if a conversion is possible.
     * The nestedType is a DatabaseField with type information set to match the VARRAYs object types
     */
    public void setCustomSQLArgumentType(String argumentFieldName, int type, String typeName, Class javaType, DatabaseField nestedType) {
        ObjectRelationalDatabaseField field = new ObjectRelationalDatabaseField(argumentFieldName);
        field.setSqlType(type);
        field.setSqlTypeName(typeName);
        field.setType(javaType);
        field.setNestedTypeField(nestedType);
        getParameters().add(field);
        getParameterTypes().add(null);
    }

    /**
     * Set the SQL string.
     * Warning: Allowing an unverified SQL string to be passed into this 
	 * method makes your application vulnerable to SQL injection attacks. 
     */
    public void setSQLString(String sqlString) {
        setSQLStringInternal(sqlString);
    }

    /**
     * INTERNAL:
     * Keep track of the fact that this call has been translated.  This information is used to
     * ensure the translation code for a custom SQLCall is only run once
     * In the case of inheritance we will try to call the translation code once to get the
     * list of types and again for each subclass
     */
    @Override
    public void translateCustomQuery() {
        super.translateCustomQuery();
        isTranslatedCustomQuery = true;
    }
    
    /**
     * INTERNAL:
     * Only translate the call if it was not previously translated
     * 
     * This code ensures the translation code for a custom SQLCall is only run once
     * In the case of inheritance we will try to call the translation code once to get the
     * list of types and again for each subclass
     */
    @Override
    public void translatePureSQLCustomQuery() {
        if (isTranslatedCustomQuery) {
            return;
        }
        super.translatePureSQLCustomQuery();
        isTranslatedCustomQuery = true;
    }
    
    /**
     * INTERNAL:
     * All values are printed as ? to allow for parameter binding or translation during the execute of the call.
     */
    public void appendTranslationParameter(Writer writer, ParameterExpression expression, DatabasePlatform platform, AbstractRecord record) throws IOException {
        try {
            platform.writeParameterMarker(writer, expression, record, this);
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
        getParameters().add(expression);
        getParameterTypes().add(TRANSLATION);
    }

    /**
     * PUBLIC:
     * This method should only be used with custom SQL:
     * Used for Oracle result sets through procedures.
     * It defines OUT parameter (prefixed with ### in custom SQL string)
     * as a cursor output.
     */
    public void useCustomSQLCursorOutputAsResultSet(String customParameterName) {
        DatabaseField field = new DatabaseField(customParameterName);
        getParameters().add(field);
        getParameterTypes().add(OUT_CURSOR);
        setIsCursorOutputProcedure(true);
    }
}
