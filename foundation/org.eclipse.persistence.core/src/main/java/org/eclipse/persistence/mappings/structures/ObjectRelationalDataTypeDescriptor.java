/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     14/05/2012-2.4 Guy Pelletier
//       - 376603: Provide for table per tenant support for multitenant applications
package org.eclipse.persistence.mappings.structures;

import java.sql.Array;
import java.sql.Ref;
import java.sql.Struct;
import java.sql.Types;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * <p><b>Purpose:</b>
 * Differentiates object-relational descriptors from normal relational descriptors.
 * The object-relational descriptor describes a type not a table, (although there
 * is normally a table associated with the type, unless it is aggregate).
 */
@SuppressWarnings("unchecked")
public class ObjectRelationalDataTypeDescriptor extends RelationalDescriptor {
    protected String structureName;
    protected Vector orderedFields;
    protected Vector allOrderedFields;

    public ObjectRelationalDataTypeDescriptor() {
        this.orderedFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
    }

    /**
     * INTERNAL:
     * Auto-Default orderedFields to fields
     */
    @Override
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        if (orderedFields==null || orderedFields.size()==0){
           orderedFields=getAllFields();
        }
        setAllOrderedFields();
    }


    /**
     * PUBLIC:
     * Order the fields in a specific
     * Add the field ordering, this will order the fields in the order this method is called.
     * @param fieldName the name of the field to add ordering on.
     */
    public void addFieldOrdering(String fieldName) {
        getOrderedFields().addElement(new DatabaseField(fieldName));
    }

    /**
     * INTERNAL:
     * Extract the direct values from the specified field value.
     * Return them in a vector.
     * The field value better be an Array.
     */
    @Override
    public Vector buildDirectValuesFromFieldValue(Object fieldValue) throws DatabaseException {

        if(fieldValue == null) {
            return null;
        }

        return Helper.vectorFromArray((Object[])fieldValue);
    }

    /**
     * INTERNAL:
     * Build the appropriate field value for the specified
     * set of direct values.
     * The database better be expecting an ARRAY.
     */
    @Override
    public Object buildFieldValueFromDirectValues(Vector directValues, String elementDataTypeName, AbstractSession session) throws DatabaseException {
        Object[] fields = Helper.arrayFromVector(directValues);
        try {
            session.getAccessor().incrementCallCount(session);
            java.sql.Connection connection = session.getAccessor().getConnection();
            return session.getPlatform().createArray(elementDataTypeName, fields, session,connection);
        } catch (java.sql.SQLException ex) {
            throw DatabaseException.sqlException(ex, session, false);
        } finally {
            session.getAccessor().decrementCallCount();
        }
    }

    /**
     * INTERNAL:
     * Build and return the field value from the specified nested database row.
     * The database better be expecting a Struct.
     */
    @Override
    public Object buildFieldValueFromNestedRow(AbstractRecord nestedRow, AbstractSession session) throws DatabaseException {
        java.sql.Connection connection = session.getAccessor().getConnection();
        return this.buildStructureFromRow(nestedRow, session, connection);
    }

    /**
     * INTERNAL:
     * Build and return the appropriate field value for the specified
     * set of nested rows.
     * The database better be expecting an ARRAY.
     * It looks like we can ignore inheritance here....
     */
    @Override
    public Object buildFieldValueFromNestedRows(Vector nestedRows, String structureName, AbstractSession session) throws DatabaseException {
        Object[] fields = new Object[nestedRows.size()];
        java.sql.Connection connection = session.getAccessor().getConnection();
        boolean reconnected = false;

        try {
            if (connection == null) {
                session.getAccessor().incrementCallCount(session);
                reconnected = true;
                connection = session.getAccessor().getConnection();
            }

            int i = 0;
            for (Enumeration stream = nestedRows.elements(); stream.hasMoreElements();) {
                AbstractRecord nestedRow = (AbstractRecord)stream.nextElement();
                fields[i++] = this.buildStructureFromRow(nestedRow, session, connection);
            }

            return session.getPlatform().createArray(structureName, fields, session,connection);
        } catch (java.sql.SQLException exception) {
            throw DatabaseException.sqlException(exception, session, false);
        } finally {
            if (reconnected) {
                session.getAccessor().decrementCallCount();
            }
        }
    }

     /**
      * INTERNAL:
      * Build and return the nested rows from the specified field value.
      * This method allows the field value to  be an ARRAY containing other structures
      * such as arrays or Struct, or direct values.
      */
     static public Object buildContainerFromArray(Array fieldValue, ObjectRelationalDatabaseField arrayField, AbstractSession session) throws DatabaseException {
        if (arrayField.getType()==null){
            return fieldValue;
        }
        Object[] objects = null;
        try {
            objects = (Object[])fieldValue.getArray();
        } catch (java.sql.SQLException ex) {
            throw DatabaseException.sqlException(ex, session, false);
        }
        if (objects == null) {
            return null;
        }

        boolean isNestedStructure = false;
        ObjectRelationalDataTypeDescriptor ord=null;
        DatabaseField nestedType = null;
        nestedType = arrayField.getNestedTypeField();
        if ((nestedType != null) && nestedType.getSqlType()==Types.STRUCT){
            ClassDescriptor descriptor = session.getDescriptor(nestedType.getType());
            if ((descriptor != null) && (descriptor.isObjectRelationalDataTypeDescriptor())) {
                //this is used to convert non-null objects passed through stored procedures and custom SQL to structs
                ord=(ObjectRelationalDataTypeDescriptor)descriptor;
            }
        } else if ((nestedType != null) && (nestedType instanceof ObjectRelationalDatabaseField) ){
            isNestedStructure = true;
        }
        //handle ARRAY conversions
        ReadObjectQuery query = new ReadObjectQuery();
        query.setSession(session);
        ContainerPolicy cp = ContainerPolicy.buildPolicyFor(arrayField.getType());
        Object container = cp.containerInstance(objects.length);
        for (int i = 0; i < objects.length; i++) {
            Object arrayValue = objects[i];
            if (arrayValue == null) {
                return null;
            }
            if (ord!=null){
                AbstractRecord nestedRow = ord.buildRowFromStructure( (Struct)arrayValue);
                ClassDescriptor descriptor = ord;
                if (descriptor.hasInheritance()) {
                    Class newElementClass = descriptor.getInheritancePolicy().classFromRow(nestedRow, session);
                    if (!descriptor.getJavaClass().equals(newElementClass)) {
                        descriptor = session.getDescriptor(newElementClass);
                        if (descriptor==null){
                            descriptor=ord;
                        }
                    }
                }
                arrayValue = descriptor.getObjectBuilder().buildNewInstance();
                descriptor.getObjectBuilder().buildAttributesIntoObject(arrayValue, null, nestedRow, query, null, null, false, session);
            } else if (isNestedStructure && (arrayValue instanceof Array)){
                arrayValue = buildContainerFromArray((Array)arrayValue, (ObjectRelationalDatabaseField)nestedType, session);
            }

            cp.addInto(arrayValue, container, session);
        }
        return container;
     }

    /**
     * INTERNAL:
     * Build and return the nested database row from the specified field value.
     * The field value better be an Struct.
     */
     @Override
    public AbstractRecord buildNestedRowFromFieldValue(Object fieldValue) throws DatabaseException {

        AbstractRecord row = new DatabaseRecord();
        Object[] attributes = (Object[])fieldValue;

        for (int index = 0; index < getAllOrderedFields().size(); index++) {
            DatabaseField field = (DatabaseField) getAllOrderedFields().get(index);
            row.put(field, attributes[index]);
        }

        return row;
    }

    /**
     * INTERNAL:
     * Creates allOrderedFields Vector, keeping allFields contents ordered.
     */
    private void setAllOrderedFields() {
        allOrderedFields = new Vector(orderedFields.size());
        Vector<DatabaseField> allFieldsCopy = new Vector(getAllFields());
        for (DatabaseField orderedField : (Vector<DatabaseField>) getOrderedFields()) {
            Iterator<DatabaseField> iterator = allFieldsCopy.iterator();
            while (iterator.hasNext()) {
                DatabaseField field = iterator.next();
                if (orderedField.getName().equalsIgnoreCase(field.getName())) {
                    allOrderedFields.add(field);
                    iterator.remove();
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Build and return the nested rows from the specified field value.
     * The field value better be an ARRAY.
     */
    @Override
    public Vector buildNestedRowsFromFieldValue(Object fieldValue, AbstractSession session) throws DatabaseException {

        if(fieldValue==null){
            return null;
        }

        Object[] structs = (Object[])fieldValue;

        Vector nestedRows = new Vector(structs.length);
        for (int i = 0; i < structs.length; i++) {
            Object[] struct = (Object[])structs[i];
            if (struct == null) {
                return null;
            }
            nestedRows.addElement(this.buildNestedRowFromFieldValue(struct));
        }
        return nestedRows;
    }

    /**
     * INTERNAL:
     * Build a row representation from the ADT structure field array.
     * TopLink will then build the object from the row.
     */
    public AbstractRecord buildRowFromStructure(Struct structure) throws DatabaseException {
        Object[] attributes;
        try {
            attributes = structure.getAttributes();
        } catch (java.sql.SQLException exception) {
            throw DatabaseException.sqlException(exception);
        }

        if(attributes!=null){
            for(int i=0;i<attributes.length;i++){
                if(attributes[i] instanceof Array ){
                    attributes[i]=ObjectRelationalDataTypeDescriptor.buildArrayObjectFromArray(attributes[i]);
                }else if(attributes[i] instanceof Struct){
                    attributes[i]=ObjectRelationalDataTypeDescriptor.buildArrayObjectFromStruct(attributes[i]);
                }
            }
        }

        return buildNestedRowFromFieldValue(attributes);
    }

    /**
     * INTERNAL:
     * Build a ADT structure from the row data.
     */
    public Struct buildStructureFromRow(AbstractRecord row, AbstractSession session, java.sql.Connection connection) throws DatabaseException {
        Struct structure;
        boolean reconnected = false;

        try {
            if (connection == null) {
                session.getAccessor().incrementCallCount(session);
                reconnected = true;
                connection = session.getAccessor().getConnection();
            }

            Object[] fields = new Object[getOrderedFields().size()];
            for (int index = 0; index < getOrderedFields().size(); index++) {
                DatabaseField field = (DatabaseField)getOrderedFields().elementAt(index);
                fields[index] = row.get(field);
            }

            structure = session.getPlatform().createStruct(getStructureName(), fields, session, connection);
        } catch (java.sql.SQLException exception) {
            throw DatabaseException.sqlException(exception, session, false);
        } finally {
            if (reconnected) {
                session.getAccessor().decrementCallCount();
            }
        }

        return structure;
    }

    /**
     * INTERNAL:
     * Build array of objects for Array data type.
     */
    public static Object buildArrayObjectFromArray(Object array) throws DatabaseException {
        Object[] objects = null;
        if(array==null){
            return array;
        }
        try {
            objects = (Object[])((Array)array).getArray();
        } catch (java.sql.SQLException ex) {
            throw DatabaseException.sqlException(ex);
        }
        if (objects == null ) {
            return null;
        } else {
            for (int i=0;i<objects.length;i++){
                if (objects[i] instanceof Array){
                    objects[i] = buildArrayObjectFromArray(objects[i]);
                }
                if (objects[i] instanceof Struct){
                    objects[i] = buildArrayObjectFromStruct(objects[i]);
                }
            }
        }
        return objects;
    }

    /**
     * INTERNAL:
     * Build array of objects for Struct data type.
     */
    public static Object buildArrayObjectFromStruct(Object structure) throws DatabaseException{
        Object[] attributes = null;
        if(structure==null){
            return structure;
        }
        try {
            attributes = ((Struct)structure).getAttributes();
        } catch (java.sql.SQLException exception) {
            throw DatabaseException.sqlException(exception);
        }
        if (attributes==null){
            return null;
        } else {
            for(int i=0;i<attributes.length;i++){
                if (attributes[i] instanceof Array){
                    attributes[i] = buildArrayObjectFromArray(attributes[i]);
                }
                if (attributes[i] instanceof Struct){
                    attributes[i] = buildArrayObjectFromStruct(attributes[i]);
                }
            }
        }
        return attributes;
    }

    /**
     * INTERNAL:
     * Aggregates use a dummy table as default.
     */
    @Override
    protected DatabaseTable extractDefaultTable() {
        if (isAggregateDescriptor()) {
            return new DatabaseTable();
        }

        return super.extractDefaultTable();
    }

    /**
     * INTERNAL:
     * Return the field order.
     */
    public Vector getOrderedFields() {
        return orderedFields;
    }

    /**
     * INTERNAL:
     * Return allFields contents ordered.
     */
    private Vector getAllOrderedFields() {
        return allOrderedFields;
    }

    /**
     * INTERNAL:
     * Get the ref for the object.
     * This is required for use by Refs, there might be a better way to do it when objID are supported.
     * (i.e. getting it from the object or identity map).
     */
    public Ref getRef(Object object, AbstractSession session) {
        SQLSelectStatement statement = new SQLSelectStatement();
        statement.addTable(getTables().firstElement());// Assumed only one for obj-rel descriptors.
        statement.getFields().addElement(new org.eclipse.persistence.expressions.ExpressionBuilder().ref());
        statement.setWhereClause(getObjectBuilder().buildPrimaryKeyExpressionFromObject(object, session));
        statement.setRequiresAliases(true);
        statement.normalize(session, this);

        ValueReadQuery valueQuery = new ValueReadQuery();
        valueQuery.setSQLStatement(statement);
        valueQuery.checkPrepare(session, new DatabaseRecord(), true);
        // Must return unwrapped Ref on WLS.
        valueQuery.getCall().setIsNativeConnectionRequired(true);

        Ref ref = (Ref)session.executeQuery(valueQuery);

        return ref;
    }

    /**
     * PUBLIC:
     * Return the name of the structure.
     * This is the name of the user defined data type as defined on the database.
     */
    public String getStructureName() {
        return structureName;
    }

    /**
     *  PUBLIC:
     *  Return if this is an ObjectRelationalDataTypeDescriptor.
     */
    @Override
    public boolean isObjectRelationalDataTypeDescriptor(){
        return true;
    }

    /**
     * INTERNAL:
     * Aggregates obj-rel are initialized normally as no cloning is required.
     */
    @Override
    public boolean requiresInitialization(AbstractSession session) {
        return true;
    }

    @Override
    protected void validateMappingType(DatabaseMapping mapping) {
        //do nothing
    }

    /**
     * INTERNAL:
     * Set the field order.
     */
    public void setOrderedFields(Vector orderedFields) {
        this.orderedFields = orderedFields;
    }

    /**
     * PUBLIC:
     * Set the name of the structure.
     * This is the name of the user defined data type as defined on the database.
     */
    public void setStructureName(String structureName) {
        this.structureName = structureName;
    }
}
