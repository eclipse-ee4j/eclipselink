/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.queries;

// javase imports
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Iterator;
import java.util.Vector;

// EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.InOutputParameterForCallableStatement;
import org.eclipse.persistence.internal.databaseaccess.OutputParameterForCallableStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;
import org.eclipse.persistence.platform.database.oracle.OraclePLSQLType;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * <b>Purpose</b>: Used to define a platform independent procedure call.
 * This supports output parameters.
 * Procedures can also be called through custom SQL.
 */
public class StoredProcedureCall extends DatabaseCall {
    protected String procedureName;
    protected Vector<String> procedureArgumentNames;

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
    public void addNamedArgument(String procedureParameterName, String argumentFieldName, int type, String typeName) {
        getProcedureArgumentNames().add(procedureParameterName);
        ObjectRelationalDatabaseField field = new ObjectRelationalDatabaseField(argumentFieldName);
        field.setSqlType(type);
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
     * Define the output argument to the stored procedure and its field and argument name.
     * The presence of the single parameter procedureParameterAndArgumentFieldName indicates that
     * the name of the procedure argument and the TopLink argument fieldName are the same.
     * The databaseType parameter classifies the parameter (JDBCType vs. OraclePLSQLType, simple
     * vs. complex)
     */
    public void addNamedArgument(String procedureParameterAndArgumentFieldName, DatabaseType databaseType) {
        addNamedArgument(procedureParameterAndArgumentFieldName,
            procedureParameterAndArgumentFieldName, databaseType);
    }

    /**
     * PUBLIC:
     * Define the output argument to the stored procedure and its field and argument name.
     * The procedureParameterName is the name of the procedure argument.
     * The argumentFieldName is the argument name to be passed to the TopLink query.
     * The databaseType parameter classifies the parameter (JDBCType vs. OraclePLSQLType, simple
     * vs. complex)
     */
    public void addNamedArgument(String procedureParameterName, String argumentFieldName, DatabaseType databaseType) {
        getProcedureArgumentNames().add(procedureParameterName);
        DatabaseField field = new DatabaseField(argumentFieldName);
        field.setDatabaseType(databaseType);
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
        addNamedInOutputArgument(procedureParameterName, argumentFieldName, argumentFieldName,
            (Class)null);
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
        inField.setType(javaType);//needed for out, less neccessary for in.  maybe use containerPolicy instead?
        inField.setNestedTypeField(nestedType);
        if (inArgumentFieldName.equals(outArgumentFieldName)) {
            appendInOut(inField);
        } else {
            ObjectRelationalDatabaseField outField = new ObjectRelationalDatabaseField(outArgumentFieldName);
            outField.setSqlType(type);
            outField.setSqlTypeName(typeName);
            outField.setType(javaType);//needed for out, less neccessary for in.  maybe use containerPolicy instead?
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
     * Define the inoutput argument to the stored procedure.
     * The presence of the single parameter procedureParameterAndArgumentFieldName indicates that
     * the name of the procedure argument and the TopLink argument fieldName are the same.
     * The databaseType parameter classifies the parameter (JDBCType vs. OraclePLSQLType, simple
     * vs. complex)
     */
    public void addNamedInOutputArgument(String procedureParameterAndArgumentFieldName,
        DatabaseType databaseType) {
        addNamedInOutputArgument(procedureParameterAndArgumentFieldName,
            procedureParameterAndArgumentFieldName, procedureParameterAndArgumentFieldName,
            databaseType);
    }

    /**
     * PUBLIC:
     * Define the inoutput argument to the stored procedure.
     * The procedureParameterName is the name of the procedure argument.
     * The argumentFieldName is the argument name to be passed to the TopLink query.
     * The databaseType parameter classifies the parameter (JDBCType vs. OraclePLSQLType, simple
     * vs. complex)
     */
    public void addNamedInOutputArgument(String procedureParameterName, String argumentFieldName,
        DatabaseType databaseType) {
        addNamedInOutputArgument(procedureParameterName, argumentFieldName, argumentFieldName,
            databaseType);
    }

    /**
     * PUBLIC:
     * Define the inout parameter for a stored procedure.
     * The procedureParameterName is the name of the procedure parameter.
     * The inArgumentFieldName is the argument name to be passed to the TopLink query.
     * The outArgumentFieldName is a separate argument name for the inout parameter
     * that allows one to retrieve the out value from the TopLink query using a different name.
     * The databaseType parameter classifies the parameter (JDBCType vs. OraclePLSQLType, simple
     * vs. complex)
     */
    public void addNamedInOutputArgument(String procedureParameterName, String inArgumentFieldName,
        String outArgumentFieldName, DatabaseType databaseType) {
        
        getProcedureArgumentNames().add(procedureParameterName);
        DatabaseField inField = new DatabaseField(inArgumentFieldName);
        inField.setDatabaseType(databaseType);
        if (inArgumentFieldName.equals(outArgumentFieldName)) {
            appendInOut(inField);
        }
        else {
            DatabaseField outField = new DatabaseField(outArgumentFieldName);
            outField.setDatabaseType(databaseType);
            appendInOut(inField, outField);
        }
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
        addNamedOutputArgument(procedureParameterName, argumentFieldName, jdbcType, typeName, javaType, null);
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
     * Define an out parameter for a stored procedure.
     * The presence of the single parameter procedureParameterAndArgumentFieldName indicates that
     * the name of the procedure argument and the TopLink argument fieldName are the same.
     * The databaseType parameter classifies the parameter (JDBCType vs. OraclePLSQLType, simple
     * vs. complex)
     */
    public void addNamedOutputArgument(String procedureParameterAndArgumentFieldName, 
        DatabaseType databaseType) {
        addNamedOutputArgument(procedureParameterAndArgumentFieldName,
            procedureParameterAndArgumentFieldName, databaseType);
    }

    /**
     * PUBLIC:
     * Define an out parameter for a stored procedure.
     * The procedureParameterName is the name of the procedure argument.
     * The argumentFieldName is that name by which one can retrieve the out value from the TopLink
     * query.
     * The databaseType parameter classifies the parameter (JDBCType vs. OraclePLSQLType, simple
     * vs. complex)
     */
    public void addNamedOutputArgument(String procedureParameterName, String argumentFieldName,
        DatabaseType databaseType) {
        getProcedureArgumentNames().add(procedureParameterName);
        DatabaseField field = new DatabaseField(argumentFieldName);
        field.setDatabaseType(databaseType);
        appendOut(field);
    }
    
    // Note: there are no addUnamedArgument(... DatabaseType databaseType) methods -
    // custom DatabaseType's require a parameter name


    /**
     * PUBLIC:
     * @return true any of the procedure's parameter are an OraclePLSQLType
     */
    public boolean hasNonJDBCTypes() {
        
        boolean hasNonJDBCTypes = false;
        int size = getParameters().size();
        for (int i = 0; i < size; i++) {
            Object parameter = getParameters().get(i);
            DatabaseField fieldParameter = null;
            if (parameter instanceof DatabaseField) {
                fieldParameter = (DatabaseField)parameter;
            }
            else if (parameter instanceof OutputParameterForCallableStatement) {
                fieldParameter = ((OutputParameterForCallableStatement)parameter).getOutputField();
            }
            else if (parameter instanceof Object []) {
                Object[] outParameters = (Object[])parameter;
                Object outParameter = outParameters[0];
                if (outParameter instanceof DatabaseField) {
                    fieldParameter = (DatabaseField)outParameter;
                }
            }
            if (fieldParameter != null && fieldParameter.getDatabaseType() != null &&
                (fieldParameter.getDatabaseType() instanceof OraclePLSQLType)) {
                hasNonJDBCTypes = true;
                break;
            }
        }
        return hasNonJDBCTypes;
    }

    /**
     * PUBLIC:
     * Define the field/argument name to be substitute for the index argument.
     * This method is used if the procedure is not named and the order is explict, names must be added in the correct order.
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
     * This method is used if the procedure is not named and the order is explict, arguments must be added in the correct order.
     * The argumentValue is the value of the argument to be used to pass to the procedure.
     */
    public void addUnamedArgumentValue(Object argumentValue) {
        getProcedureArgumentNames().add(null);
        appendIn(argumentValue);
    }
    
    /**
     * PUBLIC:
     * Define the argument to the stored procedure for the index argument.
     * This method is used if the procedure is not named and the order is explict, arguments must be added in the correct order.
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
     * This method is used if the procedure is not named and the order is explict, arguments must be added in the correct order.
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
     * This method is used if the procedure is not named and the order is explict, arguments must be added in the correct order.
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
     * This method is used if the procedure is not named and the order is explict, arguments must be added in the correct order.
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
     * This method is used if the procedure is not named and the order is explict, arguments must be added in the correct order.
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
     * This method is used if the procedure is not named and the order is explict, arguments must be added in the correct order.
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
     * This method is used if the procedure is not named and the order is explict, arguments must be added in the correct order.
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
     * This method is used if the procedure is not named and the order is explict, arguments must be added in the correct order.
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
     * This method is used if the procedure is not named and the order is explict, arguments must be added in the correct order.
     * The argumentFieldName is the field name of the argument to be used to pass to the procedure
     * and to be used is the result of the output row.
     */
    public void addUnamedInOutputArgument(String argumentFieldName) {
        addUnamedInOutputArgument(argumentFieldName, argumentFieldName, null);
    }
    
    /**
     * PUBLIC:
     * Define the inoutput argument to the stored procedure for the index argument and the field/argument name to be substitute for it on the way in and out.
     * This method is used if the procedure is not named and the order is explict, arguments must be added in the correct order.
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
     * This method is used if the procedure is not named and the order is explict, arguments must be added in the correct order.
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
     * This method is used if the procedure is not named and the order is explict, arguments must be added in the correct order.
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
     * This method is used if the procedure is not named and the order is explict, names must be added in the correct order.
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
     * This method is used if the procedure is not named and the order is explict, names must be added in the correct order.
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
     * This method is used if the procedure is not named and the order is explict, names must be added in the correct order.
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
     * This method is used if the procedure is not named and the order is explict, names must be added in the correct order.
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
     * This method is used if the procedure is not named and the order is explict, names must be added in the correct order.
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
     * This method is used if the procedure is not named and the order is explict, names must be added in the correct order.
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
     * INTERNAL: (override implementation in DatabaseCall)
     * If a StoredProcedureCall has <b>any<b> non-JDBC types, the target procedure was invoked via
     * an Anonymous PL/SQL block; if <b>any<b> of its arguments are inout - regardless of whether or
     * not they are non-JDBC types - then the index calculation to retrieve the 'out' portion of that
     * parameter's value is different than the 'normal' buildOutputRow. Anonymous PL/SQL blocks do
     * not support inout arguments; therefore, during the invocation an an extra 'out' binding
     * at position N+1 was made; as a side-effect, this causes the indices of any subsequent out or
     * inout parameter to be shuffled down.
     */
    @Override
    public AbstractRecord buildOutputRow(CallableStatement statement) throws SQLException {
        
        if (!hasNonJDBCTypes()) {
            return super.buildOutputRow(statement);
        }
        
        AbstractRecord row = new DatabaseRecord();
        for (int i = 0, index = 1, l = parameters.size(); i < l; i++, index++) {
            Object parameter = parameters.elementAt(i);
            if (parameter instanceof OutputParameterForCallableStatement) {
                OutputParameterForCallableStatement outParameter = 
                    (OutputParameterForCallableStatement)parameter;
                if (outParameter instanceof InOutputParameterForCallableStatement) {
                    ++index;
                }
                // TODO - borrowed verbatim from implementation in DatabaseCall; this should be
                // refactored
                if (!outParameter.isCursor()) {
                    Object value = statement.getObject(index);
                    DatabaseField field = outParameter.getOutputField();
                    if (value instanceof Struct){
                        ClassDescriptor descriptor = this.getQuery().getSession().getDescriptor(field.getType());
                        if ((value!=null) && (descriptor!=null) && (descriptor.isObjectRelationalDataTypeDescriptor())){
                            AbstractRecord nestedRow = ((ObjectRelationalDataTypeDescriptor)descriptor).buildRowFromStructure((Struct)value);
                            ReadObjectQuery query = new ReadObjectQuery();
                            query.setSession(this.getQuery().getSession());
                            value = descriptor.getObjectBuilder().buildObject(query, nestedRow);
                        }
                    } else if ((value instanceof Array)&&( field.isObjectRelationalDatabaseField() )){
                        value = ObjectRelationalDataTypeDescriptor.buildContainerFromArray((Array)value, (ObjectRelationalDatabaseField)field, this.getQuery().getSession());
                    }
                    row.put(field, value);
                }
            }
        }

        return row;
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
     * This is lazy initialized to conserv space on calls that have no parameters.
     */
    public Vector getProcedureArgumentNames() {
        if (procedureArgumentNames == null) {
            procedureArgumentNames = new NonSynchronizedVector();
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
     * This is lazy initialized to conserv space on calls that have no parameters.
     */
    public void setProcedureArgumentNames(Vector<String> procedureArgumentNames) {
        this.procedureArgumentNames = procedureArgumentNames;
    }

    /**
     * PUBLIC: (REQUIRED)
     * Set the name of the store procedure on the database.
     */
    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    /**
     * PUBLIC:
     * helper method that sets the length of the parameter named 'parameterName'
     * Used by the code that builds Anonymous PL/SQL blocks's temporary '_TARGET'
     * variables
     */
    public void setParameterLength(String parameterName, int length) {
        if (parameterName != null) {
            int idx = 0;
            for (Iterator i = getProcedureArgumentNames().iterator(); i.hasNext();) {
                String paramName = (String)i.next();
                if (parameterName.equalsIgnoreCase(paramName)) {
                    setParameterLength(idx, length);
                    break;
                }
                idx++;
            }
        }
    }

    /**
     * PUBLIC:
     * helper method that sets the length of the parameter at index 'parameterIndex'
     * Used by the code that builds Anonymous PL/SQL blocks's temporary '_TARGET'
     * variables
     */
    public void setParameterLength(int parameterIndex, int length) {
        DatabaseField field = (DatabaseField)getParameters().get(parameterIndex);
        if (field != null) {
            field.setLength(length);
        }
    }

    /**
     * PUBLIC:
     * helper method that sets the scale of the parameter named 'parameterName'
     * Used by the code that builds Anonymous PL/SQL blocks's temporary '_TARGET'
     * variables
     */
    public void setParameterScale(String parameterName, int scale) {
        if (parameterName != null) {
            int idx = 0;
            for (Iterator i = getProcedureArgumentNames().iterator(); i.hasNext();) {
                String paramName = (String)i.next();
                if (parameterName.equalsIgnoreCase(paramName)) {
                    setParameterScale(idx, scale);
                    break;
                }
                idx++;
            }
        }
    }

    /**
     * PUBLIC:
     * helper method that sets the scale of the parameter at index 'parameterIndex'
     * Used by the code that builds Anonymous PL/SQL blocks's temporary '_TARGET'
     * variables
     */
    public void setParameterScale(int parameterIndex, int scale) {
        DatabaseField field = (DatabaseField)getParameters().get(parameterIndex);
        if (field != null) {
            field.setScale(scale);
        }
    }

    /**
     * PUBLIC:
     * helper method that sets the precision of the parameter named 'parameterName'
     * Used by the code that builds Anonymous PL/SQL blocks's temporary '_TARGET'
     * variables
     */
    public void setParameterPrecision(String parameterName, int precision) {
        if (parameterName != null) {
            int idx = 0;
            for (Iterator i = getProcedureArgumentNames().iterator(); i.hasNext();) {
                String paramName = (String)i.next();
                if (parameterName.equalsIgnoreCase(paramName)) {
                    setParameterPrecision(idx, precision);
                    break;
                }
                idx++;
            }
        }
    }

    /**
     * PUBLIC:
     * helper method that sets the precision of the parameter at index 'parameterIndex'
     * Used by the code that builds Anonymous PL/SQL blocks's temporary '_TARGET'
     * variables
     */
    public void setParameterPrecision(int parameterIndex, int precision) {
        DatabaseField field = (DatabaseField)getParameters().get(parameterIndex);
        if (field != null) {
            field.setPrecision(precision);
        }
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
