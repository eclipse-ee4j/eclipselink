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
package org.eclipse.persistence.internal.databaseaccess;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Array;
import java.sql.Struct;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.descriptors.ClassDescriptor;

import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose</b>: To provide a base type for customary parameters' types
 * used for binding by DatabaseCall:
 * descendants of DatabasePlatform may create instances of descendants of this class
 * in customModifyInDatabaseCall method.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * </ul>
 */
public class BindCallCustomParameter implements Serializable {
    /**
     * Return if unwrapped connection should be used. 
     */
    public boolean shouldUseUnwrappedConnection() {
        return false;
    }
      
    /**
    * INTERNAL:
    * Binds the custom parameter (obj) into  the passed PreparedStatement
    * for the passed DatabaseCall.
    *
    * Called only by DatabasePlatform.setParameterValueInDatabaseCall method
    */
    public BindCallCustomParameter(Object obj) {
        this.obj = obj;
    }

    protected BindCallCustomParameter() {
    }

    public void set(DatabasePlatform platform, PreparedStatement statement, int index, AbstractSession session) throws SQLException {
        platform.setParameterValueInDatabaseCall(obj, statement, index, session);
    }

    public String toString() {
        if (obj != null) {
            return obj.toString();
        } else {
            return "null";
        }
    }

    protected Object obj;
    
    /**
     * INTERNAL:
     * Converts the parameter object from a collection or java object into a JDBC type such as an
     * Array or Struct object, based on the type information contained in the dbField object.  
     * If the parameter is null, the dbField will be returned to represent the null value.  
     * A Struct object
     * will be returned if: 
     *  1) the dbField is an ObjectRelationalDatabaseField with its sqltype set to Types.STRUCT
     *  2) parameter is not an instanceof java.sql.Struct
     *  3) There is an ObjectRelationalDataTypeDescriptor defined for the parameter object's class
     *  
     * An Array object will be returned if:
     *  1) the dbField is an ObjectRelationalDatabaseField and sqltype is set to Types.ARRAY
     *  2) parameter is an instanceof collection and is not an instanceof java.sql.Array
     *  
     * This method will be used recursively on the dbField's nestedField and objects in the collection
     * to build the Array object, allowing for nested structures to be produced ie arrays of arrays
     */
    public Object convert(Object parameter, DatabaseField dbField, AbstractSession session, java.sql.Connection connection) throws SQLException{
        if (parameter == null){
            return dbField;
        }
        if (dbField == null || (!dbField.isObjectRelationalDatabaseField())){
            return parameter;
        }
        //handle STRUCT conversions
        ObjectRelationalDatabaseField ordField = ((ObjectRelationalDatabaseField)dbField);
        if (dbField.getSqlType()==Types.STRUCT){
            if (!(parameter instanceof Struct)){
                ClassDescriptor descriptor=session.getDescriptor(parameter);
                if ((descriptor!=null) && (descriptor.isObjectRelationalDataTypeDescriptor())){
                    //this is used to convert non-null objects passed through stored procedures and custom SQL to structs 
                    ObjectRelationalDataTypeDescriptor ord=(ObjectRelationalDataTypeDescriptor)descriptor;
                    AbstractRecord nestedRow = ord.getObjectBuilder().buildRow(parameter, session, WriteType.UNDEFINED);
                    return ord.buildStructureFromRow(nestedRow, session, connection);
                }
            }
            return parameter;
        }
        // Handle java.sql.Array conversions from Collection or Java Arrays.
        if (parameter instanceof Object[]) {
        	parameter = Arrays.asList((Object[])parameter);
        }
        if ((dbField.getSqlType()!=Types.ARRAY)||(parameter instanceof Array) || !(parameter instanceof Collection) ){
            return parameter;
        }
        
        Collection container = (Collection)parameter;
        
        DatabaseField nestedType = ordField.getNestedTypeField();

        Object[] fields = new Object[container.size()];
        int i = 0;
        for (Iterator iter = container.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (element != null) {
                element = convert(element, nestedType, session, connection);
            }
            fields[i++] = element;
        }

        return session.getPlatform().createArray(((ObjectRelationalDatabaseField)dbField).getSqlTypeName(), fields, session, connection);
    }
    
}
