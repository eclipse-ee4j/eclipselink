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
package org.eclipse.persistence.mappings.structures;

import java.sql.Ref;
import java.sql.Struct;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.internal.expressions.ObjectExpression;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.ObjectReferenceChangeRecord;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.queries.DeleteObjectQuery;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.QueryByExamplePolicy;
import org.eclipse.persistence.queries.WriteObjectQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * <p><b>Purpose:</b>
 * In an object-relational data model, structures reference each other through "Refs"; not through foreign keys as
 * in the relational data model. TopLink supports using the Ref to reference the target object.
 */
public class ReferenceMapping extends ObjectReferenceMapping {

    /** A ref is always stored in a single field. */
    protected DatabaseField field;

    public ReferenceMapping() {
        super();
        this.setWeight(WEIGHT_AGGREGATE);
    }

    /**
     * INTERNAL:
     * In case Query By Example is used, this method builds and returns an expression that
     * corresponds to a single attribute and it's value.
     */
    @Override
    public Expression buildExpression(Object queryObject, QueryByExamplePolicy policy, Expression expressionBuilder, Map processedObjects, AbstractSession session) {
        if (policy.shouldValidateExample()){
            throw QueryException.unsupportedMappingQueryByExample(queryObject.getClass().getName(), this);
        }
        return null;
    }

    /**
     * Returns all the aggregate fields.
     */
    @Override
    protected Vector collectFields() {
        Vector fields = new Vector(1);
        fields.addElement(getField());
        return fields;
    }

    /**
     * INTERNAL:
     * Returns the field which this mapping represents.
     */
    @Override
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
        return null;
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
        setReferenceDescriptor(session.getDescriptor(getReferenceClass()));

        if (referenceDescriptor == null) {
            throw DescriptorException.descriptorIsMissing(getReferenceClass().getName(), this);
        }

        // For bug 2730536 convert the field to be an ObjectRelationalDatabaseField.
        ObjectRelationalDatabaseField field = (ObjectRelationalDatabaseField)getField();
        field.setSqlType(java.sql.Types.REF);
        if (referenceDescriptor instanceof ObjectRelationalDataTypeDescriptor) {
            field.setSqlTypeName(((ObjectRelationalDataTypeDescriptor)referenceDescriptor).getStructureName());
        }

        setField(getDescriptor().buildField(getField()));
        setFields(collectFields());

        // Ref mapping requires native connection in WLS as the Ref is wrapped.
        getDescriptor().setIsNativeConnectionRequired(true);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isReferenceMapping() {
        return true;
    }

    /**
     * INTERNAL:
     * Insert privately owned parts
     */
    @Override
    public void preInsert(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        // Checks if privately owned parts should be inserted or not.
        if (!shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        // Get the privately owned parts
        Object object = getRealAttributeValueFromObject(query.getObject(), query.getSession());

        if (object == null) {
            return;
        }

        if (isPrivateOwned()) {
            // No need to set changeSet as insert is a straight copy anyway
            InsertObjectQuery insertQuery = new InsertObjectQuery();
            insertQuery.setIsExecutionClone(true);
            insertQuery.setObject(object);
            insertQuery.setCascadePolicy(query.getCascadePolicy());
            query.getSession().executeQuery(insertQuery);
        } else {
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

    /**
     * INTERNAL:
     * Update privately owned parts
     */
    @Override
    public void preUpdate(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!isAttributeValueInstantiated(query.getObject())) {
            return;
        }

        if (isPrivateOwned()) {
            Object objectInDatabase = readPrivateOwnedForObject(query);
            if (objectInDatabase != null) {
                query.setProperty(this, objectInDatabase);
            }
        }

        if (!shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        // Get the privately owned parts in the memory
        Object object = getRealAttributeValueFromObject(query.getObject(), query.getSession());
        if (object != null) {
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

    /**
     * INTERNAL:
     * Insert privately owned parts
     */
    @Override
    public void postInsert(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        return;
    }

    /**
     * INTERNAL:
     * Delete privately owned parts
     */
    @Override
    public void postDelete(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        return;
    }

    /**
     * INTERNAL:
     * Update privately owned parts
     */
    @Override
    public void postUpdate(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        return;
    }

    /**
     * INTERNAL:
     * Delete privately owned parts
     */
    @Override
    public void preDelete(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        return;
    }

    /**
     * Set the field in the mapping.
     */
    protected void setField(DatabaseField field) {
        this.field = field;
    }

    /**
     * PUBLIC:
     * Set the field name in the mapping.
     */
    public void setFieldName(String fieldName) {
        setField(new ObjectRelationalDatabaseField(fieldName));
    }

    /**
     * PUBLIC:
     * This is a reference class whose instances this mapping will store in the domain objects.
     */
    @Override
    public void setReferenceClass(Class referenceClass) {
        this.referenceClass = referenceClass;
    }

    /**
     * INTERNAL:
     * Return the value of the field from the row or a value holder on the query to obtain the object.
     * Check for batch + aggregation reading.
     */
    @Override
    public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery query, CacheKey cacheKey, AbstractSession executionSession, boolean isTargetProtected, Boolean[] wasCacheUsed) throws DatabaseException {
        if (this.descriptor.getCachePolicy().isProtectedIsolation()) {
            if (this.isCacheable && isTargetProtected && cacheKey != null) {
                //cachekey will be null when isolating to uow
                //used cached collection
                Object result = null;
                Object cached = cacheKey.getObject();
                if (cached != null) {
                    if (wasCacheUsed != null){
                        wasCacheUsed[0] = Boolean.TRUE;
                    }
                    return this.getAttributeValueFromObject(cached);
                }
                return result;
            } else if (!this.isCacheable && !isTargetProtected && cacheKey != null) {
                return this.indirectionPolicy.buildIndirectObject(new ValueHolder(null));
            }
        }
        AbstractRecord targetRow = null;
        if (row.hasSopObject()) {
            Object sopAttributeValue = getAttributeValueFromObject(row.getSopObject());
            if (sopAttributeValue == null) {
                return this.indirectionPolicy.nullValueFromRow();
            }
            // As part of SOP object the indirection should be already triggered
            Object sopRealAttributeValue = getIndirectionPolicy().getRealAttributeValueFromObject(row.getSopObject(), sopAttributeValue);
            if (sopRealAttributeValue == null) {
                return sopAttributeValue;
            }
            targetRow = new DatabaseRecord(0);
            targetRow.setSopObject(sopRealAttributeValue);
            // As part of SOP object the indirection should be already triggered and should be no references outside of sopObject (and its privately owned (possibly nested privately owned) objects)
            return getReferenceDescriptor().getObjectBuilder().buildObject(query, targetRow, null);
        }
        Ref ref = (Ref)row.get(getField());
        if (ref == null) {
            return null;
        }
        Struct struct;
        try {
            executionSession.getAccessor().incrementCallCount(executionSession);
            java.sql.Connection connection = executionSession.getAccessor().getConnection();
            struct = (Struct)executionSession.getPlatform().getRefValue(ref,executionSession,connection);
            targetRow = ((ObjectRelationalDataTypeDescriptor)getReferenceDescriptor()).buildRowFromStructure(struct);
        } catch (java.sql.SQLException exception) {
            throw DatabaseException.sqlException(exception, executionSession, false);
        } finally {
            executionSession.getAccessor().decrementCallCount();
        }

        return getReferenceDescriptor().getObjectBuilder().buildObject(query, targetRow, joinManager);
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
        writeFromObjectIntoRowInternal(object, record, session, false);
    }

    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     */
    public void writeFromObjectIntoRowInternal(Object object, AbstractRecord record, AbstractSession session, boolean shouldIgnoreNull) {
        Object referenceObject = getRealAttributeValueFromObject(object, session);

        if (referenceObject == null) {
            if (!shouldIgnoreNull) {
                // Fix for 2730536, must put something in modify row, even if it is null.
                record.put(getField(), null);
            }
            return;
        }

        Ref ref = ((ObjectRelationalDataTypeDescriptor)getReferenceDescriptor()).getRef(referenceObject, session);

        record.put(getField(), ref);
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

        ObjectChangeSet changeSet = (ObjectChangeSet)((ObjectReferenceChangeRecord)changeRecord).getNewValue();
        Object referenceObject = changeSet.getUnitOfWorkClone();

        if (referenceObject == null) {
            return;
        }

        Ref ref = ((ObjectRelationalDataTypeDescriptor)getReferenceDescriptor()).getRef(referenceObject, session);

        record.put(getField(), ref);
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
            writeFromObjectIntoRowInternal(object, record, session, false);
        }
    }

    /**
     * INTERNAL:
     * This row is built for update after shallow insert which happens in case of bidirectional inserts.
     * It contains the foreign keys with non null values that were set to null for shallow insert.
     */
    @Override
    public void writeFromObjectIntoRowForUpdateAfterShallowInsert(Object object, AbstractRecord record, AbstractSession session, DatabaseTable table) {
        if (this.isReadOnly) {
            return;
        }

        if (!getField().getTable().equals(table) || !getField().isNullable()) {
            return;
        }

        writeFromObjectIntoRowInternal(object, record, session, true);
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
     * Write fields needed for insert into the template for with null values.
     */
    @Override
    public void writeInsertFieldsIntoRow(AbstractRecord record, AbstractSession session) {
        if (isReadOnly()) {
            return;
        }

        record.put(getField(), null);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isRelationalMapping() {
        return true;
    }
}
