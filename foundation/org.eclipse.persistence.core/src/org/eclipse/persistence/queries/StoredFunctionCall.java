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
 ******************************************************************************/  
package org.eclipse.persistence.queries;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;

import static org.eclipse.persistence.internal.helper.Helper.getShortClassName;

/**
 * <b>Purpose</b>: Used to define a platform independent function call.
 * Note that not all platforms support stored functions.
 * This supports output parameters.
 * Functions can also be called through custom SQL.
 */
public class StoredFunctionCall extends StoredProcedureCall {
    
    public StoredFunctionCall() {
        super();
        addUnamedOutputArgument("");
    }
    
    public StoredFunctionCall(int sqlType, String sqlTypeName, Class javaType) {
        super();
        addUnamedOutputArgument("", sqlType, sqlTypeName, javaType);
    }
    
    public StoredFunctionCall(int sqlType, String sqlTypeName, Class javaType, DatabaseField nestedType) {
        super();
        addUnamedOutputArgument("", sqlType, sqlTypeName, javaType, nestedType);
    }
    
    public StoredFunctionCall(int sqlType, String sqlTypeName, String javaTypeClassName) {
        this(sqlType, sqlTypeName, (Class)null);
        ObjectRelationalDatabaseField ordf = (ObjectRelationalDatabaseField)parameters.get(0);
        ordf.setTypeName(javaTypeClassName);
    }

    public StoredFunctionCall(int sqlType, String sqlTypeName, String javaTypeClassName, DatabaseField nestedType) {
        this(sqlType, sqlTypeName, javaTypeClassName);
        ObjectRelationalDatabaseField ordf = (ObjectRelationalDatabaseField)parameters.get(0);
        ordf.setNestedTypeField(nestedType);
    }
    

    /**
     * INTERNAL:
     * Return call header for the call string.
     */
    public String getCallHeader(DatabasePlatform platform) {
        return platform.getFunctionCallHeader();
    }

    /**
     * INTERNAL:
     * Return the first index of parameter to be placed inside brackets
     * in the call string.
     */
    public int getFirstParameterIndexForCallString() {
        return 1;
    }

    /**
     * INTERNAL:
     */
    public boolean isStoredFunctionCall() {
        return true;
    }

    /**
     * INTERNAL:
     */
    public void prepareInternal(AbstractSession session) {
        if (session.getPlatform().supportsStoredFunctions()) {
            super.prepareInternal(session);
        } else {
            throw ValidationException.platformDoesNotSupportStoredFunctions(getShortClassName(session.getPlatform()));
        }
    }

    /**
     * PUBLIC:
     * Define the field name to be substitute for the function return.
     */
    public void setResult(String name) {
        DatabaseField returnField = (DatabaseField)getParameters().get(0);
        returnField.setName(name);
    }

    /**
     * PUBLIC:
     * Define the field name to be substitute for the function return.
     * The type is the type of Java class desired back from the function, this is dependent on the type returned from the function.
     */
    public void setResult(String name, Class type) {
        DatabaseField returnField = (DatabaseField)getParameters().get(0);
        returnField.setName(name);
        returnField.setType(type);
    }

    /**
     * PUBLIC:
     * Define the field name to be substitute for the function return.
     * The type is the type of Java class desired back from the function, this is dependent on the type returned from the function.
     */
    public void setResult(String name, int type, String typeName) {
        ObjectRelationalDatabaseField field = new ObjectRelationalDatabaseField(name);
        field.setSqlType(type);
        field.setSqlTypeName(typeName);
        getParameters().set(0, field);
    }

    /**
     * PUBLIC:
     * Define the ObjectRelationalDatabaseField to be substituted for the function return.
     * The type is the JDBC type code, this is dependent on the type required by the procedure.
     * The typeName is the JDBC type name, this may be required for ARRAY or STRUCT types.
     * The javaType is the mapped Class that has an ObjectRelationalDataTypeDescriptor for the ARRAY
     * or STRUCT type typeName
     */
    public void setResult(int type, String typeName, Class javaType) {
        ObjectRelationalDatabaseField field = new ObjectRelationalDatabaseField("");
        field.setSqlType(type);
        field.setSqlTypeName(typeName);
        field.setType(javaType);
        getParameters().set(0, field);
    }    

    /**
     * PUBLIC:
     * Define the ObjectRelationalDatabaseField to be substituted for the function return.  This
     * will typically be called for ARRAY types.
     * The type is the JDBC type code, this is dependent on the type required by the procedure.
     * The typeName is the JDBC type name, this may be required for ARRAY types.
     * The javaType is the mapped Class that has an ObjectRelationalDataTypeDescriptor for the ARRAY
     * type typeName
     * The nestedType is a database field representing the type the ARRAY holds onto.
     */
    public void setResult(int type, String typeName, Class javaType, DatabaseField nestedType) {
        ObjectRelationalDatabaseField field = new ObjectRelationalDatabaseField("");
        field.setSqlType(type);
        field.setSqlTypeName(typeName);
        field.setType(javaType);
        field.setNestedTypeField(nestedType);
        getParameters().set(0, field);
    }
    
    /**
     * PUBLIC:
     * Define the field name to be substitute for the function return.
     * The type is the type of Java class desired back from the function, this is dependent on the type returned from the function.
     */
    public void setResult(String name, int type) {
        DatabaseField returnField = (DatabaseField)getParameters().get(0);
        returnField.setName(name);
        returnField.setSqlType(type);
    }
}
