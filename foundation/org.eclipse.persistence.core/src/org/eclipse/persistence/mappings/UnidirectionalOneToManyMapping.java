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
 *     ailitchev - Uni-directional OneToMany
 *     07/19/2011-2.2.1 Guy Pelletier 
 *       - 338812: ManyToMany mapping in aggregate object violate integrity constraint on deletion
 ******************************************************************************/  
package org.eclipse.persistence.mappings;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.internal.descriptors.CascadeLockingPolicy;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.CollectionChangeRecord;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.DeleteObjectQuery;
import org.eclipse.persistence.queries.ObjectLevelModifyQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * <p><b>Purpose</b>: UnidirectionalOneToManyMapping doesn't have 1:1 back reference mapping.
 *
 * @author Andrei Ilitchev
 * @since Eclipselink 1.1
 */
public class UnidirectionalOneToManyMapping extends OneToManyMapping {
    /**
     * Indicates whether target's optimistic locking value should be incremented on
     * target being added to / removed from a source. 
     **/
    protected boolean shouldIncrementTargetLockValueOnAddOrRemoveTarget;

    /**
     * Indicates whether target's optimistic locking value should be incremented on
     * the source deletion. 
     * Note that if the flag is set to true then the indirection will be triggered on
     * source delete - in order to verify all targets' versions.
     **/
    protected boolean shouldIncrementTargetLockValueOnDeleteSource;

    /**
     * PUBLIC:
     * Default constructor.
     */
    public UnidirectionalOneToManyMapping() {
        super();
        this.shouldIncrementTargetLockValueOnAddOrRemoveTarget = true;
        this.shouldIncrementTargetLockValueOnDeleteSource = true;
    }
    

    /**
     * INTERNAL:
     * Build a row containing the keys for use in the query that updates the row for the
     * target object during an insert or update
     */
    protected AbstractRecord buildKeyRowForTargetUpdate(ObjectLevelModifyQuery query){
       AbstractRecord keyRow = new DatabaseRecord();
       // Extract primary key and value from the source.
       int size = sourceKeyFields.size();
       for (int index = 0; index < size; index++) {
           DatabaseField sourceKey = sourceKeyFields.get(index);
           DatabaseField targetForeignKey = targetForeignKeyFields.get(index);
           Object sourceKeyValue = query.getTranslationRow().get(sourceKey);
           keyRow.put(targetForeignKey, sourceKeyValue);
       }
       return keyRow;
   }
    
    /**
     * INTERNAL:
     * This method is used to create a change record from comparing two collections
     * @return org.eclipse.persistence.internal.sessions.ChangeRecord
     */
    @Override
    public ChangeRecord compareForChange(Object clone, Object backUp, ObjectChangeSet owner, AbstractSession uow) {
        ChangeRecord record = super.compareForChange(clone, backUp, owner, uow);
        if(record != null && getReferenceDescriptor().getOptimisticLockingPolicy() != null) {
            postCalculateChanges(record, (UnitOfWorkImpl)uow);
        }
        return record;
    }

    /**
     * INTERNAL:
     * Extract the source primary key value from the target row.
     * Used for batch reading, most following same order and fields as in the mapping.
     */
    protected Vector extractSourceKeyFromRow(AbstractRecord row, AbstractSession session) {
        int size = sourceKeyFields.size();
        Vector key = new Vector(size);
        ConversionManager conversionManager = session.getDatasourcePlatform().getConversionManager();

        for (int index = 0; index < size; index++) {
            DatabaseField targetField = targetForeignKeyFields.get(index);
            DatabaseField sourceField = sourceKeyFields.get(index);
            Object value = row.get(targetField);

            // Must ensure the classification gets a cache hit.
            try {
                value = conversionManager.convertObject(value, sourceField.getType());
            } catch (ConversionException e) {
                throw ConversionException.couldNotBeConverted(this, getDescriptor(), e);
            }

            key.addElement(value);
        }

        return key;
    }

    /**
     * INTERNAL:
     */
    public boolean isOwned(){
        return true;
    }

    /**
     * INTERNAL:
     */
    public boolean isUnidirectionalOneToManyMapping() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Initialize the mapping.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        if (getReferenceDescriptor().getOptimisticLockingPolicy() != null) {
            if (this.shouldIncrementTargetLockValueOnAddOrRemoveTarget) {
                this.descriptor.addMappingsPostCalculateChanges(this);
            }
            if (this.shouldIncrementTargetLockValueOnDeleteSource && !this.isPrivateOwned) {
                this.descriptor.addMappingsPostCalculateChangesOnDeleted(this);
            }
        }
    }
    
    /**
     * Initialize the type of the target foreign key, as it will be null as it is not mapped in the target.
     */
    public void postInitialize(AbstractSession session) {
        super.postInitialize(session);
        Iterator<DatabaseField> targetForeignKeys = getTargetForeignKeyFields().iterator();
        Iterator<DatabaseField> sourceKeys = getSourceKeyFields().iterator();
        while (targetForeignKeys.hasNext()) {
            DatabaseField targetForeignKey = targetForeignKeys.next();
            DatabaseField sourcePrimaryKey = sourceKeys.next();
            if (targetForeignKey.getType() == null) {
                DatabaseMapping mapping = getDescriptor().getObjectBuilder().getMappingForField(sourcePrimaryKey);
                // If we have a mapping, set the type, otherwise at this point 
                // there is not much more we can do. This case will likely hit
                // when we have a UnidirectionalOneToManyMapping on an aggregate
                // outside of JPA. Within JPA, in most cases, the metadata 
                // processing should set the type on the targetForeignKey for us. 
                // Bug 278263 has been entered to revisit this code.
                if (mapping != null) {
                    targetForeignKey.setType(mapping.getFieldClassification(sourcePrimaryKey));
                }
            }
        }
    }

    /**
     * INTERNAL:
     */
    protected AbstractRecord createModifyRowForAddTargetQuery() {
        AbstractRecord modifyRow = super.createModifyRowForAddTargetQuery();
        int size = targetForeignKeyFields.size();
        for (int index = 0; index < size; index++) {
            DatabaseField targetForeignKey = targetForeignKeyFields.get(index);
            modifyRow.put(targetForeignKey, null);
        }
        return modifyRow;
    }

    /**
     * INTERNAL:
     * Delete the reference objects.
     */
    public void preDelete(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (shouldObjectModifyCascadeToParts(query)) {
            super.preDelete(query);
        } else {
            updateTargetRowPreDeleteSource(query);
        }

    }
    
    /**
     * Prepare a cascade locking policy.
     */
    public void prepareCascadeLockingPolicy() {
        CascadeLockingPolicy policy = new CascadeLockingPolicy(getDescriptor(), getReferenceDescriptor());
        policy.setQueryKeyFields(getSourceKeysToTargetForeignKeys());
        policy.setShouldHandleUnmappedFields(true);
        getReferenceDescriptor().addCascadeLockingPolicy(policy);
    }

    /**
     * INTERNAL:
     * Overridden by mappings that require additional processing of the change record after the record has been calculated.
     */
    @Override
    public void postCalculateChanges(org.eclipse.persistence.sessions.changesets.ChangeRecord changeRecord, UnitOfWorkImpl uow) {
        // targets are added to and/or removed to/from the source.
        CollectionChangeRecord collectionChangeRecord = (CollectionChangeRecord)changeRecord;
        Iterator it = collectionChangeRecord.getAddObjectList().values().iterator();
        while(it.hasNext()) {
            ObjectChangeSet change = (ObjectChangeSet)it.next();
            if(!change.hasChanges()) {
                change.setShouldModifyVersionField(Boolean.TRUE);
                ((org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet)change.getUOWChangeSet()).addObjectChangeSet(change, uow, false);
            }
        }
        // in the mapping is privately owned then the target will be deleted - no need to modify target version.
        it = collectionChangeRecord.getRemoveObjectList().values().iterator();
        while(it.hasNext()) {
            ObjectChangeSet change = (ObjectChangeSet)it.next(); 
            if (!isPrivateOwned()){
                if(!change.hasChanges()) {
                    change.setShouldModifyVersionField(Boolean.TRUE);
                    ((org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet)change.getUOWChangeSet()).addObjectChangeSet(change, uow, false);
                }
            }else{
                containerPolicy.postCalculateChanges(change, referenceDescriptor, this, uow);
            }
        }
    }

    /**
     * INTERNAL:
     * Overridden by mappings that require objects to be deleted contribute to change set creation.
     */
    @Override
    public void postCalculateChangesOnDeleted(Object deletedObject, UnitOfWorkChangeSet uowChangeSet, UnitOfWorkImpl uow) {
        // the source is deleted:
        // trigger the indirection - we have to get optimistic lock exception
        // in case another thread has updated one of the targets:
        // triggered indirection caches the target with the old version,
        // then the version update waits until the other thread (which is locking the version field) commits,
        // then the version update is executed and it throws optimistic lock exception.
        Object col = getRealCollectionAttributeValueFromObject(deletedObject, uow);
        if (col != null) {
            Object iterator = this.containerPolicy.iteratorFor(col);
            while (this.containerPolicy.hasNext(iterator)) {
                Object target = this.containerPolicy.next(iterator, uow);
                ObjectChangeSet change = this.referenceDescriptor.getObjectBuilder().createObjectChangeSet(target, uowChangeSet, uow);
                if (!change.hasChanges()) {
                    change.setShouldModifyVersionField(Boolean.TRUE);
                    ((org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet)change.getUOWChangeSet()).addObjectChangeSet(change, uow, false);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Add additional fields
     */
    @Override
    protected void postPrepareNestedBatchQuery(ReadQuery batchQuery, ObjectLevelReadQuery query) {
        super.postPrepareNestedBatchQuery(batchQuery, query);
        ReadAllQuery mappingBatchQuery = (ReadAllQuery)batchQuery;
        int size = this.targetForeignKeyFields.size();
        for (int i=0; i < size; i++) {
            mappingBatchQuery.addAdditionalField(this.targetForeignKeyFields.get(i));
        }        
    }

    /**
     * INTERNAL:
     * The translation row may require additional fields than the primary key if the mapping in not on the primary key.
     */
    @Override
    protected void prepareTranslationRow(AbstractRecord translationRow, Object object, ClassDescriptor descriptor, AbstractSession session) {
        // Make sure that each source key field is in the translation row.
        int size = sourceKeyFields.size();
        for(int i=0; i < size; i++) {
            DatabaseField sourceKey = sourceKeyFields.get(i);
            if (!translationRow.containsKey(sourceKey)) {
                Object value = descriptor.getObjectBuilder().extractValueFromObjectForField(object, sourceKey, session);
                translationRow.put(sourceKey, value);
            }
        }
    }


    /**
     * INTERNAL:
     * Overridden by mappings that require additional processing of the change record after the record has been calculated.
     */
    @Override
    public void recordPrivateOwnedRemovals(Object object, UnitOfWorkImpl uow) {
        //need private owned check for this mapping as this method is called for any mapping
        // that also registers a postCalculateChanges() method.  Most mappings only register the 
        // postCalculateChanges if they are privately owned.  This Mapping is a special case an
        // always registers a postCalculateChanges mapping when the target has OPT locking.
        if (isPrivateOwned){
            super.recordPrivateOwnedRemovals(object, uow);
        }
    }
    
    /**
     * INTERNAL:
     * UnidirectionalOneToManyMapping performs some events after INSERT/UPDATE to maintain the keys
     * @return
     */
    @Override
    public boolean requiresDataModificationEvents(){
        return true;
    }

    /**
     * PUBLIC:
     * Set value that indicates whether target's optimistic locking value should be incremented on
     * target being added to / removed from a source (default value is true).
     **/
    public void setShouldIncrementTargetLockValueOnAddOrRemoveTarget(boolean shouldIncrementTargetLockValueOnAddOrRemoveTarget) {
        this.shouldIncrementTargetLockValueOnAddOrRemoveTarget = shouldIncrementTargetLockValueOnAddOrRemoveTarget;
    }

    /**
     * PUBLIC:
     * Set value that indicates whether target's optimistic locking value should be incremented on
     * the source deletion (default value is true).
     **/
    public void setShouldIncrementTargetLockValueOnDeleteSource(boolean shouldIncrementTargetLockValueOnDeleteSource) {
        this.shouldIncrementTargetLockValueOnDeleteSource = shouldIncrementTargetLockValueOnDeleteSource;
    }

    /**
     * PUBLIC:
     * Indicates whether target's optimistic locking value should be incremented on
     * target being added to / removed from a source (default value is true).
     **/
    public boolean shouldIncrementTargetLockValueOnAddOrRemoveTarget() {
        return shouldIncrementTargetLockValueOnAddOrRemoveTarget;
    }

    /**
     * PUBLIC:
     * Indicates whether target's optimistic locking value should be incremented on
     * the source deletion (default value is true). 
     **/
    public boolean shouldIncrementTargetLockValueOnDeleteSource() {
        return shouldIncrementTargetLockValueOnDeleteSource;
    }
    
    /**
     * INTERNAL
     * Target foreign key of the removed object should be modified (set to null).
     */
    protected boolean shouldRemoveTargetQueryModifyTargetForeignKey() {
        return true;
    }    
}
