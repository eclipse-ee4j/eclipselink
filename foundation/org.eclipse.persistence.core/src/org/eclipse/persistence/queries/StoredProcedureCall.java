/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

//javase imports
import java.util.Vector;

//EclipseLink imports
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;

/**
 * <b>Purpose</b>: Used to define a platform independent procedure call.
 * This supports output parameters.
 * Procedures can also be called through custom SQL.
 */
public class StoredProcedureCall extends DatabaseCall {
    protected String procedureName;
    protected Vector procedureArgumentNames;

    public StoredProcedureCall() {
        super();
    }

    /**
     * PUBLIC:
     * Define the argument to the stored procedure and the field/argument name to be substitute for it.
     * The procedureParameterAndArgumentFieldName is the name of the procedure argument expected,
     * and is the field or argument name to be used to pass to the procedure.
     * These names are assumed to be the same, if not this method can be called with two arguments.
     */
    public void addNamedArgument(String procedureParameterAndArgumentFieldName) {
        addNamedArgument(procedureParameterAndArgumentFieldName, procedureParameterAndArgumentFieldName);
    }

    /**
     * PUBLIC:
     * Define the argument to the stored procedure and the field/argument name to be substitute for it.
     * The procedureParameterName is the name of the procedure argument expected.
     * The argumentFieldName is the field or argument name to be used to pass to the procedure.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     */
    public void addNamedArgument(String procedureParameterName, String argumentFieldName) {
        getProcedureArgumentNames().add(procedureParameterName);
        appendIn(new DatabaseField(argumentFieldName));
    }

    /**
     * PUBLIC:
     * Define the argument to the stored procedure and the value to be substitute for it.
     * The procedureParameterName is the name of the procedure argument expected.
     * The argumentValue is the value of the argument to be used to pass to the procedure.
     */
    public void addNamedArgumentValue(String procedureParameterName, Object argumentValue) {
        getProcedureArgumentNames().add(procedureParameterName);
        appendIn(argumentValue);
    }

    /**
     * PUBLIC:
     * Define the input argument to the stored procedure and the field/argument name to be substitute for it.
     * The procedureParameterName is the name of the procedure argument expected.
     * The argumentFieldName is the field or argument name to be used to pass to the user.
     * The type is the type of Java class for the field, and is dependent on the type required by the procedure.  This is used
     * to set the type in case null is passed in.
     */
    public void addNamedArgument(String procedureParameterName, String argumentFieldName, Class type) {
        getProcedureArgumentNames().add(procedureParameterName);
        DatabaseField field = new DatabaseField(argumentFieldName);
        field.setType(type);
        appendIn(field);
    }
    
    /**
     * PUBLIC:
     * Define the input argument to the stored procedure and the field/argument name to be substitute for it.
     * The procedureParameterName is the name of the procedure argument expected.
     * The argumentFieldName is the field or argument name to be used to pass to the user.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the JDBC type code, this is dependent on the type required by the procedure.
     */
    public void addNamedArgument(String procedureParameterName, String argumentFieldName, int type) {
        getProcedureArgumentNames().add(procedureParameterName);
        DatabaseField field = new DatabaseField(argumentFieldName);
        field.setSqlType(type);
        appendIn(field);
    }
    
    /**
     * PUBLIC:
     * Define the output argument to the stored procedure and the field/argument name to be substitute for it.
     * The procedureParameterName is the name of the procedure argument expected.
     * The argumentFieldName is the field or argument name to be used to pass to the procedure.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the JDBC type code, this is dependent on the type required by the procedure.
     * The typeName is the JDBC type name, this may be required for ARRAY or STRUCT types.
     */
    public void addNamedArgument(String procedureParameterName, String argumentFieldName, int type,
        String typeName) {
        addNamedArgument(procedureParameterName, argumentFieldName, type, typeName, (Class)null);
    }
    
    /**
     * PUBLIC:
     * Define the output argument to the stored procedure and the field/argument name to be substitute for it.
     * The procedureParameterName is the name of the procedure argument expected.
     * The argumentFieldName is the field or argument name to be used to pass to the procedure.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the JDBC type code, this is dependent on the type required by the procedure.
     * The typeName is the JDBC type name, this may be required for ARRAY or STRUCT types.
     * The javaType is the mapped Class that has an ObjectRelationalDataTypeDescriptor for the ARRAY
     * or STRUCT type typeName
     */
    public void addNamedArgument(String procedureParameterName, String argumentFieldName, int type,
        String typeName, Class javaType) {
        getProcedureArgumentNames().add(procedureParameterName);
        ObjectRelationalDatabaseField field = new ObjectRelationalDatabaseField(argumentFieldName);
        field.setSqlType(type);
        field.setType(javaType);
        field.setSqlTypeName(typeName);
        appendIn(field);
    }
    
    /**
     * PUBLIC:
     * Define the output argument to the stored procedure and the field/argument name to be substitute for it.
     * The procedureParameterName is the name of the procedure argument expected.
     * The argumentFieldName is the field or argument name to be used to pass to the procedure.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the JDBC type code, this is dependent on the type required by the procedure.
     * The typeName is the JDBC type name, this may be required for ARRAY or STRUCT types.
     * The javaType is the name of the mapped Class that has an ObjectRelationalDataTypeDescriptor
     * for the ARRAY or STRUCT type typeName
     */
    public void addNamedArgument(String procedureParameterName, String argumentFieldName, int type,
        String typeName, String javaTypeName) {
        getProcedureArgumentNames().add(procedureParameterName);
        ObjectRelationalDatabaseField field = new ObjectRelationalDatabaseField(argumentFieldName);
        field.setSqlType(type);
        field.setTypeName(javaTypeName);
        field.setSqlTypeName(typeName);
        appendIn(field);
    }

    /**
     * PUBLIC:
     * Define the output argument to the stored procedure and the field/argument name to be substitute for it.
     * The procedureParameterName is the name of the procedure argument expected.
     * The argumentFieldName is the field or argument name to be used to pass to the procedure.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the JDBC type code, this is dependent on the type required by the procedure.
     * The typeName is the JDBC type name, as required for STRUCT and ARRAY types.
     * The nestedType is a DatabaseField with type information set to match the VARRAYs object types
     */
    public void addNamedArgument(String procedureParameterName, String argumentFieldName, int type, String typeName, DatabaseField nestedType) {
        getProcedureArgumentNames().add(procedureParameterName);
        ObjectRelationalDatabaseField field = new ObjectRelationalDatabaseField(argumentFieldName);
        field.setSqlType(type);
        field.setSqlTypeName(typeName);
        field.setNestedTypeField(nestedType);
        appendIn(field);
    }

    /**
     * PUBLIC:
     * Define the inoutput argument to the stored procedure and the field/argument name to be substitute for it on the way in and out.
     * The procedureParameterAndArgumentFieldName is the name of the procedure argument expected,
     * the field or argument name to be used to pass to the procedure and,
     * the field or argument name to be used is the result of the output row.
     */
    public void addNamedInOutputArgument(String procedureParameterAndArgumentFieldName) {
        getProcedureArgumentNames().add(procedureParameterAndArgumentFieldName);
        appendInOut(new DatabaseField(procedureParameterAndArgumentFieldName));
    }

    /**
     * PUBLIC:
     * Define the inoutput argument to the stored procedure and the field/argument name to be substitute for it on the way in and out.
     * The procedureParameterName is the name of the procedure argument expected.
     * The argumentFieldName is the field or argument name to be used to pass to the procedure and
     * is the result of the output row.
     */
    public void addNamedInOutputArgument(String procedureParameterName, String argumentFieldName) {
        addNamedInOutputArgument(procedureParameterName, argumentFieldName, argumentFieldName, null);
    }

    /**
     * PUBLIC:
     * Define the inoutput argument to the stored procedure and the field/argument name to be substitute for it on the way in and out.
     * The procedureParameterName is the name of the procedure argument expected.
     * The argumentFieldName is the field or argument name to be used to pass to the procedure and
     * is the result of the output row.
     * The type is the type of Java class desired back from the procedure, this is dependent on the type returned from the procedure.
     */
    public void addNamedInOutputArgument(String procedureParameterName, String argumentFieldName, Class type) {
        addNamedInOutputArgument(procedureParameterName, argumentFieldName, argumentFieldName, type);
    }

    /**
     * PUBLIC:
     * Define the inoutput argument to the stored procedure and the field/argument name to be substitute for it on the way in and out.
     * The procedureParameterName is the name of the procedure argument expected.
     * The inArgumentFieldName is the field or argument name to be used to pass to the procedure.
     * The outArgumentFieldName is the field or argument name to be used is the result of the output row.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type the Java class desired back from the procedure, if a struct is returned and the class has an ObjectRelationalDataTypeDescriptor defined .
     */
    public void addNamedInOutputArgument(String procedureParameterName, String inArgumentFieldName, String outArgumentFieldName, Class type) {
        getProcedureArgumentNames().add(procedureParameterName);
        DatabaseField inField = new DatabaseField(inArgumentFieldName);
        inField.setType(type);
        if (inArgumentFieldName.equals(outArgumentFieldName)) {
            appendInOut(inField);
        } else {
            DatabaseField outField = new DatabaseField(outArgumentFieldName);
            outField.setType(type);
            appendInOut(inField, outField);
        }
    }
    
    /**
     * PUBLIC:
     * Define the inoutput argument to the stored procedure and the field/argument name to be substitute for it on the way in and out.
     * The procedureParameterName is the name of the procedure argument expected.
     * The inArgumentFieldName is the field or argument name to be used to pass to the procedure.
     * The outArgumentFieldName is the field or argument name to be used is the result of the output row.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the JDBC type code, this dependent on the type returned from the procedure.
     */
    public void addNamedInOutputArgument(String procedureParameterName, String inArgumentFieldName, String outArgumentFieldName, int type) {
        getProcedureArgumentNames().add(procedureParameterName);
        DatabaseField inField = new DatabaseField(inArgumentFieldName);
        inField.setSqlType(type);
        if (inArgumentFieldName.equals(outArgumentFieldName)) {
            appendInOut(inField);
        } else {
            DatabaseField outField = new DatabaseField(outArgumentFieldName);
            outField.setSqlType(type);
            appendInOut(inField, outField);
        }
    }
        
    /**
     * PUBLIC:
     * Define the inoutput argument to the stored procedure and the field/argument name to be substitute for it on the way in and out.
     * The procedureParameterName is the name of the procedure argument expected.
     * The inArgumentFieldName is the field or argument name to be used to pass to the procedure.
     * The outArgumentFieldName is the field or argument name to be used is the result of the output row.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the JDBC type code, this dependent on the type returned from the procedure.
     * The typeName is the JDBC type name, this may be required for ARRAY or STRUCT types.
     */
    public void addNamedInOutputArgument(String procedureParameterName, String inArgumentFieldName, String outArgumentFieldName, int type, String typeName) {
        addNamedInOutputArgument(procedureParameterName, inArgumentFieldName, outArgumentFieldName, type, typeName, null, null);
    }
    
    /**
     * PUBLIC:
     * Define the inoutput argument to the stored procedure and the field/argument name to be substitute for it on the way in and out.
     * The procedureParameterName is the name of the procedure argument expected.
     * The inArgumentFieldName is the field or argument name to be used to pass to the procedure.
     * The outArgumentFieldName is the field or argument name to be used is the result of the output row.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the JDBC type code, this dependent on the type returned from the procedure.
     * The typeName is the JDBC type name, this may be required for STRUCT and ARRAY types.
     * The classType is the type of Java class desired back from the procedure, this is dependent on the type returned from the procedure.
     */
    public void addNamedInOutputArgument(String procedureParameterName, String inArgumentFieldName, String outArgumentFieldName, int type, String typeName, Class classType) {
        addNamedInOutputArgument(procedureParameterName, inArgumentFieldName, outArgumentFieldName, type, typeName, classType, null);
    }
    
    /**
     * PUBLIC:
     * Define the inoutput argument to the stored procedure and the field/argument name to be substitute for it on the way in and out.
     * The procedureParameterName is the name of the procedure argument expected.
     * The inArgumentFieldName is the field or argument name to be used to pass to the procedure.
     * The outArgumentFieldName is the field or argument name to be used is the result of the output row.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the JDBC type code, this dependent on the type returned from the procedure.
     * The typeName is the JDBC type name, this may be required for ARRAY types.
     * The javaType is the java class to return instead of the ARRAY and STRUCT types if a conversion is possible.
     * The nestedType is a DatabaseField with type information set to match the VARRAYs object types
     */
    public void addNamedInOutputArgument(String procedureParameterName, String inArgumentFieldName, String outArgumentFieldName, int type, String typeName, Class javaType, DatabaseField nestedType) {
        getProcedureArgumentNames().add(procedureParameterName);
        ObjectRelationalDatabaseField inField = new ObjectRelationalDatabaseField(inArgumentFieldName);
        inField.setSqlType(type);
        inField.setSqlTypeName(typeName);
        inField.setType(javaType);//needed for out, less necessary for in.  maybe use containerPolicy instead?
        inField.setNestedTypeField(nestedType);
        if (inArgumentFieldName.equals(outArgumentFieldName)) {
            appendInOut(inField);
        } else {
            ObjectRelationalDatabaseField outField = new ObjectRelationalDatabaseField(outArgumentFieldName);
            outField.setSqlType(type);
            outField.setSqlTypeName(typeName);
            outField.setType(javaType);//needed for out, less necessary for in.  maybe use containerPolicy instead?
            outField.setNestedTypeField(nestedType);
            appendInOut(inField, outField);
        }
    }

    /**
     * PUBLIC:
     * Define the inoutput argument to the stored procedure and the field/argument name to be substitute for it on the way in and out.
     * The procedureParameterName is the name of the procedure argument expected.
     * The inArgumentValue is the value of the argument to be used to pass to the procedure.
     * The outArgumentFieldName is the field or argument name to be used is the result of the output row.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the type of Java class desired back from the procedure, this is dependent on the type returned from the procedure.
     */
    public void addNamedInOutputArgumentValue(String procedureParameterName, Object inArgumentValue, String outArgumentFieldName, Class type) {
        getProcedureArgumentNames().add(procedureParameterName);
        DatabaseField outField = new DatabaseField(outArgumentFieldName);
        outField.setType(type);
        appendInOut(inArgumentValue, outField);
    }

    /**
     * PUBLIC:
     * Define the output argument to the stored procedure and the field/argument name to be substitute for it.
     * The procedureParameterAndArgumentFieldName is the name of the procedure argument expected,
     * and is the field or argument name to be used to pass to the procedure.
     * These names are assumed to be the same, if not this method can be called with two arguments.
     */
    public void addNamedOutputArgument(String procedureParameterAndArgumentFieldName) {
        addNamedOutputArgument(procedureParameterAndArgumentFieldName, procedureParameterAndArgumentFieldName);
    }

    /**
     * PUBLIC:
     * Define the output argument to the stored procedure and the field/argument name to be substitute for it.
     * The procedureParameterName is the name of the procedure argument expected.
     * The argumentFieldName is the field or argument name to be used to pass to the procedure.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     */
    public void addNamedOutputArgument(String procedureParameterName, String argumentFieldName) {
        getProcedureArgumentNames().add(procedureParameterName);
        appendOut(new DatabaseField(argumentFieldName));
    }

    /**
     * PUBLIC:
     * Define the output argument to the stored procedure and the field/argument name to be substitute for it.
     * The procedureParameterName is the name of the procedure argument expected.
     * The argumentFieldName is the field or argument name to be used to pass to the procedure.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the type of Java class desired back from the procedure, this is dependent on the type returned from the procedure.
     */
    public void addNamedOutputArgument(String procedureParameterName, String argumentFieldName, Class type) {
        getProcedureArgumentNames().add(procedureParameterName);
        DatabaseField field = new DatabaseField(argumentFieldName);
        field.setType(type);
        appendOut(field);
    }
    
    /**
     * PUBLIC:
     * Define the output argument to the stored procedure and the field/argument name to be substitute for it.
     * The procedureParameterName is the name of the procedure argument expected.
     * The argumentFieldName is the field or argument name to be used to pass to the procedure.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the JDBC type code, this is dependent on the type returned from the procedure.
     */
    public void addNamedOutputArgument(String procedureParameterName, String argumentFieldName, int type) {
        getProcedureArgumentNames().add(procedureParameterName);
        DatabaseField field = new DatabaseField(argumentFieldName);
        field.setSqlType(type);
        appendOut(field);
    }
    
    /**
     * PUBLIC:
     * Define the output argument to the stored procedure and the field/argument name to be substitute for it.
     * The procedureParameterName is the name of the procedure argument expected.
     * The argumentFieldName is the field or argument name to be used to pass to the procedure.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the JDBC type code, this is dependent on the type returned from the procedure.
     * The typeName is the JDBC type name, this may be required for ARRAY or STRUCT types.
     */
    public void addNamedOutputArgument(String procedureParameterName, String argumentFieldName, int type, String typeName) {
        getProcedureArgumentNames().add(procedureParameterName);
        ObjectRelationalDatabaseField field = new ObjectRelationalDatabaseField(argumentFieldName);
        field.setSqlType(type);
        field.setSqlTypeName(typeName);
        appendOut(field);
    }
    
    /**
     * PUBLIC:
     * Define the output argument to the stored procedure and the field/argument name to be substitute for it.
     * The procedureParameterName is the name of the procedure argument expected.
     * The argumentFieldName is the field or argument name to be used is the result of the output row.
     * The jdbcType is the JDBC type code, this dependent on the type returned from the procedure.
     * The typeName is the JDBC type name, this may be required for ARRAY and STRUCT types.
     * The javaType is the java class to return instead of the ARRAY and STRUCT types if a conversion is possible.
     */
    public void addNamedOutputArgument(String procedureParameterName, String argumentFieldName, int jdbcType, String typeName, Class javaType) {
    	getProcedureArgumentNames().add(procedureParameterName);
        ObjectRelationalDatabaseField field = new ObjectRelationalDatabaseField(argumentFieldName);
        field.setSqlType(jdbcType);
        field.setSqlTypeName(typeName);
        field.setType(javaType);
        appendOut(field);
    }
    
    /**
     * PUBLIC:
     * Define the output argument to the stored procedure and the field/argument name to be substitute for it.
     * The procedureParameterName is the name of the procedure argument expected.
     * The argumentFieldName is the field or argument name to be used is the result of the output row.
     * The jdbcType is the JDBC type code, this dependent on the type returned from the procedure.
     * The typeName is the JDBC type name, this may be required for ARRAY and STRUCT types.
     * The javaType is the java class to return instead of the ARRAY and STRUCT types if a conversion is possible.
     * The nestedType is a DatabaseField with type information set to match the VARRAYs object types
     */
    public void addNamedOutputArgument(String procedureParameterName, String argumentFieldName, int jdbcType, String typeName, Class javaType, DatabaseField nestedType) {
        getProcedureArgumentNames().add(procedureParameterName);
        ObjectRelationalDatabaseField field = new ObjectRelationalDatabaseField(argumentFieldName);
        field.setSqlType(jdbcType);
        field.setSqlTypeName(typeName);
        field.setType(javaType);
        field.setNestedTypeField(nestedType);
        appendOut(field);
    }

    /**
     * PUBLIC:
     * Define the field/argument name to be substitute for the index argument.
     * This method is used if the procedure is not named and the order is explicit, names must be added in the correct order.
     * The argumentFieldName is the field or argument name to be used to pass to the procedure.
     */
    public void addUnamedArgument(String argumentFieldName) {
        getProcedureArgumentNames().add(null);
        DatabaseField field = new DatabaseField(argumentFieldName);
        appendIn(field);
    }

    /**
     * PUBLIC:
     * Define the argument to the stored procedure for the index argument.
     * This method is used if the procedure is not named and the order is explicit, arguments must be added in the correct order.
     * The argumentValue is the value of the argument to be used to pass to the procedure.
     */
    public void addUnamedArgumentValue(Object argumentValue) {
        getProcedureArgumentNames().add(null);
        appendIn(argumentValue);
    }
    
    /**
     * PUBLIC:
     * Define the argument to the stored procedure for the index argument.
     * This method is used if the procedure is not named and the order is explicit, arguments must be added in the correct order.
     * The argumentFieldName is the field or argument name to be used to pass to the user.
     * The type is the type of Java class for the field, and is dependent on the type required by the procedure.  This is used
     * to set the type in case null is passed in.
     */
    public void addUnamedArgument(String argumentFieldName, Class type) {
        getProcedureArgumentNames().add(null);
        DatabaseField field = new DatabaseField(argumentFieldName);
        field.setType(type);
        appendIn(field);
    }
    
    /**
     * PUBLIC:
     * Define the argument to the stored procedure for the index argument.
     * This method is used if the procedure is not named and the order is explicit, arguments must be added in the correct order.
     * The argumentFieldName is the field or argument name to be used to pass to the user.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the JDBC type code, this is dependent on the type required by the procedure.
     */
    public void addUnamedArgument(String argumentFieldName, int type) {
        getProcedureArgumentNames().add(null);
        DatabaseField field = new DatabaseField(argumentFieldName);
        field.setSqlType(type);
        appendIn(field);
    }
    
    /**
     * PUBLIC:
     * Define the argument to the stored procedure for the index argument.
     * This method is used if the procedure is not named and the order is explicit, arguments must be added in the correct order.
     * The argumentFieldName is the field or argument name to be used to pass to the procedure.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the JDBC type code, this is dependent on the type required by the procedure.
     * The typeName is the JDBC type name, this may be required for ARRAY or STRUCT types.
     */
    public void addUnamedArgument(String argumentFieldName, int type, String typeName) {
        getProcedureArgumentNames().add(null);
        ObjectRelationalDatabaseField field = new ObjectRelationalDatabaseField(argumentFieldName);
        field.setSqlType(type);
        field.setSqlTypeName(typeName);
        appendIn(field);
    }

    /**
     * PUBLIC:
     * Define the argument to the stored procedure for the index argument.
     * This method is used if the procedure is not named and the order is explicit, arguments must be added in the correct order.
     * The argumentFieldName is the field or argument name to be used to pass to the procedure.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the JDBC type code, this is dependent on the type required by the procedure.
     * The typeName is the JDBC type name, as required for STRUCT and ARRAY types.
     * The nestedType is a DatabaseField with type information set to match the VARRAYs object types
     */
    public void addUnamedArgument(String argumentFieldName, int type, String typeName, DatabaseField nestedType) {
        getProcedureArgumentNames().add(null);
        ObjectRelationalDatabaseField field = new ObjectRelationalDatabaseField(argumentFieldName);
        field.setSqlType(type);
        field.setSqlTypeName(typeName);
        field.setNestedTypeField(nestedType);
        appendIn(field);
    }

    /**
     * PUBLIC:
     * Define the argument to the stored procedure for the index argument.
     * This method is used if the procedure is not named and the order is explicit, arguments must be added in the correct order.
     * The inArgumentFieldName is the field name of the argument to be used to pass to the procedure.
     * The outArgumentFieldName is the field or argument name to be used is the result of the output row.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the type of Java class desired back from the procedure, this is dependent on the type returned from the procedure.
     */
    public void addUnamedInOutputArgument(String inArgumentFieldName, String outArgumentFieldName, Class type) {
        getProcedureArgumentNames().add(null);
        DatabaseField inField = new DatabaseField(inArgumentFieldName);
        inField.setType(type);
        if (inArgumentFieldName.equals(outArgumentFieldName)) {
            appendInOut(inField);
        } else {
            DatabaseField outField = new DatabaseField(outArgumentFieldName);
            outField.setType(type);
            appendInOut(inField, outField);
        }
    }
    
    /**
     * PUBLIC:
     * Define the argument to the stored procedure for the index argument.
     * This method is used if the procedure is not named and the order is explicit, arguments must be added in the correct order.
     * The inArgumentFieldName is the field name of the argument to be used to pass to the procedure.
     * The outArgumentFieldName is the field or argument name to be used is the result of the output row.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the JDBC type code, this is dependent on the type returned from the procedure.
     */
    public void addUnamedInOutputArgument(String inArgumentFieldName, String outArgumentFieldName, int type) {
        getProcedureArgumentNames().add(null);
        DatabaseField inField = new DatabaseField(inArgumentFieldName);
        inField.setSqlType(type);
        if (inArgumentFieldName.equals(outArgumentFieldName)) {
            appendInOut(inField);
        } else {
            DatabaseField outField = new DatabaseField(outArgumentFieldName);
            outField.setSqlType(type);
            appendInOut(inField, outField);
        }
    }
    
    /**
     * PUBLIC:
     * Define the inoutput argument to the stored procedure for the index argument and the field/argument name to be substitute for it on the way in and out.
     * This method is used if the procedure is not named and the order is explicit, arguments must be added in the correct order.
     * The inArgumentFieldName is the field name of the argument to be used to pass to the procedure.
     * The outArgumentFieldName is the field or argument name to be used is the result of the output row.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the JDBC type code, this is dependent on the type returned from the procedure.
     * The typeName is the JDBC type name, this may be required for ARRAY or STRUCT types.
     */
    public void addUnamedInOutputArgument(String inArgumentFieldName, String outArgumentFieldName, int type, String typeName) {
        getProcedureArgumentNames().add(null);
        ObjectRelationalDatabaseField inField = new ObjectRelationalDatabaseField(inArgumentFieldName);
        inField.setSqlType(type);
        inField.setSqlTypeName(typeName);
        if (inArgumentFieldName.equals(outArgumentFieldName)) {
            appendInOut(inField);
        } else {
            ObjectRelationalDatabaseField outField = new ObjectRelationalDatabaseField(outArgumentFieldName);
            outField.setSqlType(type);
            outField.setSqlTypeName(typeName);
            appendInOut(inField, outField);
        }
    }

    /**
     * PUBLIC:
     * Define the inoutput argument to the stored procedure for the index argument and the field/argument name to be substitute for it on the way in and out.
     * This method is used if the procedure is not named and the order is explicit, arguments must be added in the correct order.
     * The argumentFieldName is the field name of the argument to be used to pass to the procedure
     * and to be used is the result of the output row.
     * The type is the type of Java class desired back from the procedure, this is dependent on the type returned from the procedure.
     */
    public void addUnamedInOutputArgument(String argumentFieldName, Class type) {
        addUnamedInOutputArgument(argumentFieldName, argumentFieldName, type);
    }

    /**
     * PUBLIC:
     * Define the inoutput argument to the stored procedure for the index argument and the field/argument name to be substitute for it on the way in and out.
     * This method is used if the procedure is not named and the order is explicit, arguments must be added in the correct order.
     * The argumentFieldName is the field name of the argument to be used to pass to the procedure
     * and to be used is the result of the output row.
     */
    public void addUnamedInOutputArgument(String argumentFieldName) {
        addUnamedInOutputArgument(argumentFieldName, argumentFieldName, null);
    }
    
    /**
     * PUBLIC:
     * Define the inoutput argument to the stored procedure for the index argument and the field/argument name to be substitute for it on the way in and out.
     * This method is used if the procedure is not named and the order is explicit, arguments must be added in the correct order.
     * The inArgumentFieldName is the field or argument name to be used to pass to the procedure.
     * The outArgumentFieldName is the field or argument name to be used is the result of the output row.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the JDBC type code, this dependent on the type returned from the procedure.
     * The typeName is the JDBC type name, this may be required for ARRAY types.
     * The collectionClass is the java class to return instead of the ARRAY type.
     */
    public void addUnamedInOutputArgument( String inArgumentFieldName, String outArgumentFieldName, int type, String typeName, Class collection ) {
        addNamedInOutputArgument( null, inArgumentFieldName, outArgumentFieldName, type, typeName, collection, null);
    }
    
    /**
     * PUBLIC:
     * Define the inoutput argument to the stored procedure for the index argument and the field/argument name to be substitute for it on the way in and out.
     * This method is used if the procedure is not named and the order is explicit, arguments must be added in the correct order.
     * The argumentFieldName is the field or argument name to be used is the result of the output row.
     * The jdbcType is the JDBC type code, this dependent on the type returned from the procedure.
     * The typeName is the JDBC type name, this may be required for ARRAY and STRUCT types.
     * The javaType is the java class to return instead of the ARRAY and STRUCT types if a conversion is possible.
     * The nestedType is a DatabaseField with type information set to match the VARRAYs object types
     */
    public void addUnamedInOutputArgument(String inArgumentFieldName, String outArgumentFieldName, int type, String typeName, Class collection, DatabaseField nestedType) {
        addNamedInOutputArgument(null, inArgumentFieldName,  outArgumentFieldName,  type,  typeName,  collection, nestedType);
    }

    /**
     * PUBLIC:
     * Define the inoutput argument to the stored procedure for the index argument and the field/argument name to be substitute for it on the way in and out.
     * This method is used if the procedure is not named and the order is explicit, arguments must be added in the correct order.
     * The inArgumentValue is the value of the argument to be used to pass to the procedure.
     * The outArgumentFieldName is the field or argument name to be used is the result of the output row.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     * The type is the type of Java class desired back from the procedure, this is dependent on the type returned from the procedure.
     */
    public void addUnamedInOutputArgumentValue(Object inArgumentValue, String outArgumentFieldName, Class type) {
        getProcedureArgumentNames().add(null);
        DatabaseField outField = new DatabaseField(outArgumentFieldName);
        outField.setType(type);
        appendInOut(inArgumentValue, outField);
    }

    /**
     * PUBLIC:
     * Define the field/argument name to be substitute for the index output argument.
     * This method is used if the procedure is not named and the order is explicit, names must be added in the correct order.
     * The argumentFieldName is the field or argument name to be used to pass to the procedure.
     * The type is the type of Java class desired back from the procedure, this is dependent on the type returned from the procedure.
     */
    public void addUnamedOutputArgument(String argumentFieldName) {
        getProcedureArgumentNames().add(null);
        appendOut(new DatabaseField(argumentFieldName));
    }

    /**
     * PUBLIC:
     * Define the field/argument name to be substitute for the index output argument.
     * This method is used if the procedure is not named and the order is explicit, names must be added in the correct order.
     * The argumentFieldName is the field or argument name to be used to pass to the procedure.
     * The type is the type of Java class desired back from the procedure, this is dependent on the type returned from the procedure.
     */
    public void addUnamedOutputArgument(String argumentFieldName, Class type) {
        getProcedureArgumentNames().add(null);
        DatabaseField field = new DatabaseField(argumentFieldName);
        field.setType(type);
        appendOut(field);
    }

    /**
     * PUBLIC:
     * Define the field/argument name to be substitute for the index output argument.
     * This method is used if the procedure is not named and the order is explicit, names must be added in the correct order.
     * The argumentFieldName is the field or argument name to be used to pass to the procedure.
     * The type is the JDBC type code, this is dependent on the type returned from the procedure.
     */
    public void addUnamedOutputArgument(String argumentFieldName, int type) {
        getProcedureArgumentNames().add(null);
        DatabaseField field = new DatabaseField(argumentFieldName);
        field.setSqlType(type);
        appendOut(field);
    }
    
    /**
     * PUBLIC:
     * Define the field/argument name to be substitute for the index output argument.
     * This method is used if the procedure is not named and the order is explicit, names must be added in the correct order.
     * The argumentFieldName is the field or argument name to be used to pass to the procedure.
     * The type is the JDBC type code, this is dependent on the type returned from the procedure.
     * The typeName is the JDBC type name, this may be required for ARRAY or STRUCT types.
     */
    public void addUnamedOutputArgument(String argumentFieldName, int type, String typeName) {
        getProcedureArgumentNames().add(null);
        ObjectRelationalDatabaseField field = new ObjectRelationalDatabaseField(argumentFieldName);
        field.setSqlType(type);
        field.setSqlTypeName(typeName);
        appendOut(field);
    }

    /**
     * PUBLIC:
     * Define the field/argument name to be substitute for the index output argument.
     * This method is used if the procedure is not named and the order is explicit, names must be added in the correct order.
     * The argumentFieldName is the field or argument name to be used is the result of the output row.
     * The jdbcType is the JDBC type code, this dependent on the type returned from the procedure.
     * The typeName is the JDBC type name, this may be required for ARRAY and STRUCT types.
     * The javaType is the java class to return instead of the ARRAY and STRUCT types if a conversion is possible.
     */
    public void addUnamedOutputArgument(String argumentFieldName, int jdbcType, String typeName, Class javaType) {
        addNamedOutputArgument(null, argumentFieldName, jdbcType, typeName, javaType, null);
    }
    
    /**
     * PUBLIC:
     * Define the field/argument name to be substitute for the index output argument.
     * This method is used if the procedure is not named and the order is explicit, names must be added in the correct order.
     * The argumentFieldName is the field or argument name to be used is the result of the output row.
     * The jdbcType is the JDBC type code, this dependent on the type returned from the procedure.
     * The typeName is the JDBC type name, this may be required for ARRAY and STRUCT types.
     * The javaType is the java class to return instead of the ARRAY and STRUCT types if a conversion is possible.
     * The nestedType is a DatabaseField with type information set to match the VARRAYs object types
     */
    public void addUnamedOutputArgument(String argumentFieldName, int jdbcType, String typeName, Class javaType, DatabaseField nestedType) {
        addNamedOutputArgument(null, argumentFieldName, jdbcType, typeName, javaType, nestedType);
    }
    
    /**
     * INTERNAL:
     * Return call header for the call string.
     */
    public String getCallHeader(DatabasePlatform platform) {
        return platform.getProcedureCallHeader();
    }

    /**
     * INTERNAL:
     * Return the first index of parameter to be placed inside brackets
     * in the call string
     */
    public int getFirstParameterIndexForCallString() {
        return 0;
    }

    /**
     * INTERNAL:
     * The if the names are provide the order is not required to match the call def.
     * This is lazy initialized to conserve space on calls that have no parameters.
     */
    public Vector getProcedureArgumentNames() {
        if (procedureArgumentNames == null) {
            procedureArgumentNames = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
        }
        return procedureArgumentNames;
    }

    /**
     * PUBLIC:
     * Return the name of the store procedure on the database.
     */
    public String getProcedureName() {
        return procedureName;
    }

    public boolean isStoredProcedureCall() {
        return true;
    }

    /**
     * INTERNAL:
     * Called by prepare method only.
     */
    protected void prepareInternal(AbstractSession session) {
        setSQLStringInternal(session.getPlatform().buildProcedureCallString(this, session));
        super.prepareInternal(session);
    }

    /**
     * INTERNAL:
     * The if the names are provide the order is not required to match the call def.
     * This is lazy initialized to conserve space on calls that have no parameters.
     */
    public void setProcedureArgumentNames(Vector procedureArgumentNames) {
        this.procedureArgumentNames = procedureArgumentNames;
    }

    /**
     * PUBLIC: (REQUIRED)
     * Set the name of the store procedure on the database.
     */
    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String toString() {
        return Helper.getShortClassName(getClass()) + "(" + getProcedureName() + ")";
    }

    /**
     * PUBLIC:
     * Used for Oracle result sets through procedures.
     * This can only be used if the arguments are not named but ordered.
     */
    public void useNamedCursorOutputAsResultSet(String argumentName) {
        setIsCursorOutputProcedure(true);
        getProcedureArgumentNames().add(argumentName);
        appendOutCursor(new DatabaseField(argumentName));
    }

    /**
     * PUBLIC:
     * Used for Oracle result sets through procedures.
     * This can only be used if the arguments are not named but ordered.
     */
    public void useUnnamedCursorOutputAsResultSet() {
        setIsCursorOutputProcedure(true);
    }
}
