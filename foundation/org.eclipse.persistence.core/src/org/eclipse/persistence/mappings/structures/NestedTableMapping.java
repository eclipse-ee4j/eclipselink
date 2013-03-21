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
package org.eclipse.persistence.mappings.structures;

import java.util.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.expressions.ObjectExpression;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.queries.*;

/**
 * <p><b>Purpose:</b>
 * Nested tables are similar to <code>VARRAYs</code> except internally they store their information in a separate table
 * from their parent structure's table. The advantage of nested tables is that they support querying and
 * joining much better than varrays that are inlined into the parent table. A nested table is typically
 * used to represent a one-to-many or many-to-many relationship of references to another independent
 * structure. TopLink supports storing a nested table of values into a single field.
 *
 * <p>NOTE: Only Oracle8i supports nested tables type.
 *
 * @since TOPLink/Java 2.5
 */
public class NestedTableMapping extends CollectionMapping {
    protected DatabaseMapping nestedMapping;

    /** A ref is always stored in a single field. */
    protected DatabaseField field;

    /** Arrays require a structure name, this is the ADT defined for the VARRAY. */
    protected String structureName;

    /**
     * PUBLIC:
     * Default constructor.
     */
    public NestedTableMapping() {
        super();
    }

    /**
     * INTERNAL:
     * The mapping clones itself to create deep copy
     */
    public Object clone() {
        NestedTableMapping clone = (NestedTableMapping)super.clone();
        return clone;
    }

    /**
     * Returns all the aggregate fields.
     */
    protected Vector collectFields() {
        Vector fields = new Vector(1);
        fields.addElement(getField());
        return fields;
    }

    /**
     * INTERNAL:
     * Returns the field which this mapping represents.
     */
    public DatabaseField getField() {
        return field;
    }

    /**
     * PUBLIC:
     * Return the name of the field this mapping represents.
     */
    public String getFieldName() {
        return getField().getName();
    }

    /**
     * INTERNAL:
     * Join criteria is created to read target records (nested table) from the table.
     */
    @Override
    public Expression getJoinCriteria(ObjectExpression context, Expression base) {
        return context.ref().equal(base.value());
    }

    /**
     * PUBLIC:
     * Return the structure name of the nestedTable.
     * This is the name of the user defined data type as defined on the database.
     */
    public String getStructureName() {
        return structureName;
    }

    /**
     * INTERNAL:
     * The returns if the mapping has any constraint dependencies, such as foreign keys and join tables.
     */
    @Override
    public boolean hasConstraintDependency() {
        return true;
    }

    /**
     * INTERNAL:
     * Initialize the mapping.
     */
    @Override
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        if (getField() == null) {
            throw DescriptorException.fieldNameNotSetInMapping(this);
        }

        // For bug 2730536 convert the field to be an ObjectRelationalDatabaseField.
        ObjectRelationalDatabaseField field = (ObjectRelationalDatabaseField)getField();
        field.setSqlType(java.sql.Types.ARRAY);
        field.setSqlTypeName(getStructureName());

        setField(getDescriptor().buildField(getField()));
    }

    /**
     * INTERNAL:
     * Selection criteria is created to read target records (nested table) from the table.
     */
    protected void initializeSelectionCriteria(AbstractSession session) {
        Expression exp1;
        Expression exp2;
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression queryKey = builder.getManualQueryKey(getAttributeName(), getDescriptor());

        exp1 = builder.ref().equal(queryKey.get(getAttributeName()).value());
        exp2 = getDescriptor().getObjectBuilder().getPrimaryKeyExpression().rebuildOn(queryKey);

        setSelectionCriteria(exp1.and(exp2));
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isNestedTableMapping() {
        return true;
    }

    /**
     * INTERNAL:
     * Post Initialize the mapping.
     */
    @Override
    public void postInitialize(AbstractSession session) throws DescriptorException {
        initializeSelectionCriteria(session);
    }

    /**
     * INTERNAL:
     * Delete privately owned parts
     */
    @Override
    public void preDelete(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        Object objects = getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());
        ContainerPolicy containerPolicy = getContainerPolicy();

        Object objectsIterator = containerPolicy.iteratorFor(objects);

        // delete parts one by one
        while (containerPolicy.hasNext(objectsIterator)) {
            DeleteObjectQuery deleteQuery = new DeleteObjectQuery();
            deleteQuery.setIsExecutionClone(true);
            deleteQuery.setObject(containerPolicy.next(objectsIterator, query.getSession()));
            deleteQuery.setCascadePolicy(query.getCascadePolicy());
            query.getSession().executeQuery(deleteQuery);
        }
        if (!query.getSession().isUnitOfWork()) {
            // This deletes any objects on the database, as the collection in memory may has been changed.
            // This is not required for unit of work, as the update would have already deleted these objects,
            // and the backup copy will include the same objects causing double deletes.
            verifyDeleteForUpdate(query);
        }
    }

    /**
     * INTERNAL:
     * Insert privately owned parts
     */
    @Override
    public void preInsert(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        Object objects = getRealCollectionAttributeValueFromObject(query.getObject(), query.getSession());

        // insert each object one by one
        ContainerPolicy cp = getContainerPolicy();
        for (Object iter = cp.iteratorFor(objects); cp.hasNext(iter);) {
            Object object = cp.next(iter, query.getSession());
            if (isPrivateOwned()) {
                InsertObjectQuery insertQuery = new InsertObjectQuery();
                insertQuery.setIsExecutionClone(true);
                insertQuery.setObject(object);
                insertQuery.setCascadePolicy(query.getCascadePolicy());
                query.getSession().executeQuery(insertQuery);
            } else {
                // Will happen in unit of work or cascaded query.					
                // This is done only for persistence by reachablility and it not require if the targets are in the queue anyway
                // Avoid cycles by checking commit manager, this is allowed because there is no dependency.
                if (!query.getSession().getCommitManager().isCommitInPreModify(object)) {
                    ObjectChangeSet changeSet = null;
                    UnitOfWorkChangeSet uowChangeSet = null;
                    if (query.getSession().isUnitOfWork() && (((UnitOfWorkImpl)query.getSession()).getUnitOfWorkChangeSet() != null)) {
                        uowChangeSet = (UnitOfWorkChangeSet)((UnitOfWorkImpl)query.getSession()).getUnitOfWorkChangeSet();
                        changeSet = (ObjectChangeSet)uowChangeSet.getObjectChangeSetForClone(object);
                    }
                    WriteObjectQuery writeQuery = new WriteObjectQuery();
                    writeQuery.setIsExecutionClone(true);
                    writeQuery.setObject(object);
                    writeQuery.setObjectChangeSet(changeSet);
                    writeQuery.setCascadePolicy(query.getCascadePolicy());
                    query.getSession().executeQuery(writeQuery);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Update the privately owned parts
     */
    @Override
    public void preUpdate(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {        
        if (!shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        // If objects are not instantiated that means they are not changed.
        if (!isAttributeValueInstantiatedOrChanged(query.getObject())) {
            return;
        }

        if (query.getObjectChangeSet() != null) {
            // UnitOfWork
            writeChanges(query.getObjectChangeSet(), query);
        } else {
            // OLD COMMIT
            compareObjectsAndWrite(query);
        }
    }

    /**
     * Set the field in the mapping.
     */
    protected void setField(DatabaseField theField) {
        field = theField;
    }

    /**
     * PUBLIC:
     * Set the field name in the mapping.
     */
    public void setFieldName(String FieldName) {
        setField(new ObjectRelationalDatabaseField(FieldName));
    }

    /**
     * PUBLIC:
     * Set the name of the structure.
     * This is the name of the user defined nested table data type as defined on the database.
     */
    public void setStructureName(String structureName) {
        this.structureName = structureName;
    }

    /**
     * INTERNAL:
     * Verifying deletes make sure that all the records privately owned by this mapping are
     * actually removed. If such records are found then those are all read and removed one
     * by one taking their privately owned parts into account.
     */
    protected void verifyDeleteForUpdate(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        Object objects = readPrivateOwnedForObject(query);

        // Delete all objects one by one.
        ContainerPolicy cp = getContainerPolicy();
        for (Object iter = cp.iteratorFor(objects); cp.hasNext(iter);) {
            query.getSession().deleteObject(cp.next(iter, query.getSession()));
        }
    }

    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord record, AbstractSession session, WriteType writeType) {
        if (isReadOnly()) {
            return;
        }

        Object values = getRealCollectionAttributeValueFromObject(object, session);
        ContainerPolicy cp = getContainerPolicy();
        Object[] fields = new Object[cp.sizeFor(values)];
        Object valuesIterator = cp.iteratorFor(values);
        for (int index = 0; index < cp.sizeFor(values); index++) {
            Object value = cp.next(valuesIterator, session);
            fields[index] = ((ObjectRelationalDataTypeDescriptor)getReferenceDescriptor()).getRef(value, session);
        }

        java.sql.Array array;
        try {
            ((DatabaseAccessor)session.getAccessor()).incrementCallCount(session);
            java.sql.Connection connection = ((DatabaseAccessor)session.getAccessor()).getConnection();
            array = session.getPlatform().createArray(getStructureName(), fields, session,connection);
        } catch (java.sql.SQLException exception) {
            throw DatabaseException.sqlException(exception, session.getAccessor(), session, false);
        } finally {
            ((DatabaseAccessor)session.getAccessor()).decrementCallCount();
        }

        record.put(getField(), array);
    }

    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     */
    @Override
    public void writeFromObjectIntoRowWithChangeRecord(ChangeRecord changeRecord, AbstractRecord record, AbstractSession session, WriteType writeType) {
        if (isReadOnly()) {
            return;
        }

        Object object = ((ObjectChangeSet)changeRecord.getOwner()).getUnitOfWorkClone();
        Object values = getRealAttributeValueFromObject(object, session);
        ContainerPolicy containterPolicy = getContainerPolicy();

        if (values == null) {
            values = containterPolicy.containerInstance(1);
        }

        Object[] fields = new Object[containterPolicy.sizeFor(values)];
        Object valuesIterator = containterPolicy.iteratorFor(values);
        for (int index = 0; index < containterPolicy.sizeFor(values); index++) {
            Object value = containterPolicy.next(valuesIterator, session);
            fields[index] = ((ObjectRelationalDataTypeDescriptor)getReferenceDescriptor()).getRef(value, session);
        }

        java.sql.Array array;
        try {
            ((DatabaseAccessor)session.getAccessor()).incrementCallCount(session);
            java.sql.Connection connection = ((DatabaseAccessor)session.getAccessor()).getConnection();
            array = session.getPlatform().createArray(getStructureName(), fields, session, connection);
        } catch (java.sql.SQLException exception) {
            throw DatabaseException.sqlException(exception, session.getAccessor(), session, false);
        } finally {
            ((DatabaseAccessor)session.getAccessor()).decrementCallCount();
        }

        record.put(getField(), array);
    }

    /**
     * INTERNAL:
     * This row is built for shallow insert which happens in case of bidirectional inserts.
     * The foreign keys must be set to null to avoid constraints.
     */
    @Override
    public void writeFromObjectIntoRowForShallowInsert(Object object, AbstractRecord record, AbstractSession session) {
        if (isReadOnly()) {
            return;
        }

        if (getField().isNullable()) {
            record.put(getField(), null);
        } else {
            writeFromObjectIntoRow(object, record, session, WriteType.INSERT);
        }
    }

    /**
     * INTERNAL:
     * This row is built for update after shallow insert which happens in case of bidirectional inserts.
     * It contains the foreign keys with non null values that were set to null for shallow insert.
     */
    @Override
    public void writeFromObjectIntoRowForUpdateAfterShallowInsert(Object object, AbstractRecord record, AbstractSession session, DatabaseTable table) {
        if (!getField().getTable().equals(table) || !getField().isNullable()) {
            return;
        }
        
        writeFromObjectIntoRow(object, record, session, WriteType.UPDATE);
    }

    /**
     * INTERNAL:
     * This row is built for shallow insert which happens in case of bidirectional inserts.
     * The foreign keys must be set to null to avoid constraints.
     */
    @Override
    public void writeFromObjectIntoRowForShallowInsertWithChangeRecord(ChangeRecord changeRecord, AbstractRecord record, AbstractSession session) {
        if (isReadOnly()) {
            return;
        }

        record.put(getField(), null);
    }

    /**
     * INTERNAL:
     * Write the entire structure into the row as a special type that prints as the constructor.
     * If any part of the structure has changed the whole thing is written.
     */
    @Override
    public void writeFromObjectIntoRowForUpdate(WriteObjectQuery writeQuery, AbstractRecord record) throws DescriptorException {
        if (!isAttributeValueInstantiatedOrChanged(writeQuery.getObject())) {
            return;
        }

        if (writeQuery.getSession().isUnitOfWork()) {
            if (compareObjects(writeQuery.getObject(), writeQuery.getBackupClone(), writeQuery.getSession())) {
                return;// Nothing has changed, no work required
            }
        }

        writeFromObjectIntoRow(writeQuery.getObject(), record, writeQuery.getSession(), WriteType.UPDATE);
    }

    /**
     * INTERNAL:
     * Write fields needed for insert into the template for with null values.
     */
    @Override
    public void writeInsertFieldsIntoRow(AbstractRecord record, AbstractSession session) {
        if (isReadOnly()) {
            return;
        }

        record.put(getField(), null);
    }
}
