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
package org.eclipse.persistence.mappings;

import java.util.*;

import org.eclipse.persistence.descriptors.CMPPolicy;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.indirection.*;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.sessions.ObjectCopyingPolicy;
import org.eclipse.persistence.sessions.Project;

/**
 * <p><b>Purpose</b>: Abstract class for 1:1, variable 1:1 and reference mappings
 */
public abstract class ObjectReferenceMapping extends ForeignReferenceMapping {

    /** Keeps track if any of the fields are foreign keys. */
    protected boolean isForeignKeyRelationship;

    /** Keeps track of which fields are foreign keys on a per field basis (can have mixed foreign key relationships). */
    protected Vector<DatabaseField> foreignKeyFields;

    protected ObjectReferenceMapping() {
        super();
    }

    /**
     * INTERNAL:
     * Used during building the backup shallow copy to copy the vector without re-registering the target objects.
     * For 1-1 or ref the reference is from the clone so it is already registered.
     */
    public Object buildBackupCloneForPartObject(Object attributeValue, Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        return attributeValue;
    }

    /**
     * INTERNAL:
     * Require for cloning, the part must be cloned.
     * Ignore the objects, use the attribute value.
     */
    public Object buildCloneForPartObject(Object attributeValue, Object original, Object clone, UnitOfWorkImpl unitOfWork, boolean isExisting) {
        if (attributeValue == null) {
            return null;
        }
        // Optimize registration to knowledge of existence.
        Object registeredObject = null;
        if (isExisting) {
            registeredObject = unitOfWork.registerExistingObject(attributeValue);
        } else {
            // Not known whether existing or not.
            registeredObject = unitOfWork.registerObject(attributeValue);
            // if the mapping is privately owned, keep track of the privately owned reference in the UnitOfWork
            if (isCandidateForPrivateOwnedRemoval() && unitOfWork.shouldDiscoverNewObjects() && registeredObject != null && unitOfWork.isObjectNew(registeredObject)) {
                unitOfWork.addPrivateOwnedObject(this, registeredObject);
            }
        }
        return registeredObject;
    }

    /**
     * INTERNAL:
     * Copy of the attribute of the object.
     * This is NOT used for unit of work but for templatizing an object.
     */
    public void buildCopy(Object copy, Object original, ObjectCopyingPolicy policy) {
        Object attributeValue = getRealAttributeValueFromObject(original, policy.getSession());
        if ((attributeValue != null) && (policy.shouldCascadeAllParts() || (policy.shouldCascadePrivateParts() && isPrivateOwned()))) {
            attributeValue = policy.getSession().copyObject(attributeValue, policy);
        } else if (attributeValue != null) {
            // Check for copy of part, i.e. back reference.
            Object copyValue = policy.getCopies().get(attributeValue);
            if (copyValue != null) {
                attributeValue = copyValue;
            }
        }
        setRealAttributeValueInObject(copy, attributeValue);
    }

    /**
     * INTERNAL:
     * In case Query By Example is used, this method generates an expression from a attribute value pair.  Since
     * this is a ObjectReference mapping, a recursive call is made to the buildExpressionFromExample method of
     * ObjectBuilder.
     */
    public Expression buildExpression(Object queryObject, QueryByExamplePolicy policy, Expression expressionBuilder, Map processedObjects, AbstractSession session) {
        String attributeName = this.getAttributeName();
        Object attributeValue = this.getRealAttributeValueFromObject(queryObject, session);

        if (!policy.shouldIncludeInQuery(queryObject.getClass(), attributeName, attributeValue)) {
            //the attribute name and value pair is not to be included in the query.
            return null;
        }

        if (attributeValue == null) {
            //even though it is null, it is to be always included in the query
            Expression expression = expressionBuilder.get(attributeName);
            return policy.completeExpressionForNull(expression);
        }

        ObjectBuilder objectBuilder = getReferenceDescriptor().getObjectBuilder();
        return objectBuilder.buildExpressionFromExample(attributeValue, policy, expressionBuilder.get(attributeName), processedObjects, session);
    }

    /**
     * INTERNAL:
     * Return an ObjectReferenceChangeRecord describing the change, or null if no change.
     * Used to compute changes for deferred change tracking.
     */
    public ChangeRecord compareForChange(Object clone, Object backUp, ObjectChangeSet owner, AbstractSession session) {
        Object cloneAttribute = null;
        Object backUpAttribute = null;

        cloneAttribute = getAttributeValueFromObject(clone);

        if (!owner.isNew()) {
            backUpAttribute = getAttributeValueFromObject(backUp);
            if ((backUpAttribute == null) && (cloneAttribute == null)) {
                return null;
            }
        }

        if ((cloneAttribute != null) && (!this.indirectionPolicy.objectIsInstantiated(cloneAttribute))) {
            //the clone's valueholder was never triggered so there will be no change
            return null;
        }
        Object cloneAttributeValue = null;
        Object backUpAttributeValue = null;

        if (cloneAttribute != null) {
            cloneAttributeValue = getRealAttributeValueFromAttribute(cloneAttribute, clone, session);
        }
        if (backUpAttribute != null) {
            backUpAttributeValue = getRealAttributeValueFromAttribute(backUpAttribute, backUp, session);
        }

        if ((cloneAttributeValue == backUpAttributeValue) && (!owner.isNew())) {// if it is new record the value
            return null;
        }

        ObjectReferenceChangeRecord record = internalBuildChangeRecord(cloneAttributeValue, owner, session);
        if (!owner.isNew()) {
            record.setOldValue(backUpAttributeValue);
        }
        return record;
    }

    /**
     * INTERNAL:
     * Directly build a change record based on the newValue without comparison
     */
    public ObjectReferenceChangeRecord internalBuildChangeRecord(Object newValue, ObjectChangeSet owner, AbstractSession session) {
        ObjectReferenceChangeRecord changeRecord = new ObjectReferenceChangeRecord(owner);
        changeRecord.setAttribute(getAttributeName());
        changeRecord.setMapping(this);
        setNewValueInChangeRecord(newValue, changeRecord, owner, session);
        return changeRecord;
    }

    /**
     * INTERNAL:
     * Set the newValue in the change record
     */
    public void setNewValueInChangeRecord(Object newValue, ObjectReferenceChangeRecord changeRecord, ObjectChangeSet owner, AbstractSession session) {
        if (newValue != null) {
            // Bug 2612571 - added more flexible manner of getting descriptor
            ObjectChangeSet newSet = getDescriptorForTarget(newValue, session).getObjectBuilder().createObjectChangeSet(newValue, (UnitOfWorkChangeSet)owner.getUOWChangeSet(), session);
            changeRecord.setNewValue(newSet);
        } else {
            changeRecord.setNewValue(null);
        }
    }

    /**
     * INTERNAL:
     * Compare the references of the two objects are the same, not the objects themselves.
     * Used for independent relationships.
     * This is used for testing and validation purposes.
     */
    protected boolean compareObjectsWithoutPrivateOwned(Object firstObject, Object secondObject, AbstractSession session) {
        Object firstReferencedObject = getRealAttributeValueFromObject(firstObject, session);
        Object secondReferencedObject = getRealAttributeValueFromObject(secondObject, session);

        if ((firstReferencedObject == null) && (secondReferencedObject == null)) {
            return true;
        }

        if ((firstReferencedObject == null) || (secondReferencedObject == null)) {
            return false;
        }

        Object firstKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(firstReferencedObject, session);
        Object secondKey = getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(secondReferencedObject, session);

        if (firstKey == null) {
            if (secondKey == null) {
                return true;
            }
            return false;
        }

        return firstKey.equals(secondKey);
    }

    /**
     * INTERNAL:
     * Compare the references of the two objects are the same, and the objects themselves are the same.
     * Used for private relationships.
     * This is used for testing and validation purposes.
     */
    protected boolean compareObjectsWithPrivateOwned(Object firstObject, Object secondObject, AbstractSession session) {
        Object firstPrivateObject = getRealAttributeValueFromObject(firstObject, session);
        Object secondPrivateObject = getRealAttributeValueFromObject(secondObject, session);

        return session.compareObjects(firstPrivateObject, secondPrivateObject);
    }

    /**
     * INTERNAL:
     * We are not using a remote valueholder
     * so we need to replace the reference object(s) with
     * the corresponding object(s) from the remote session.
     *
     * ObjectReferenceMappings need to unwrap and wrap the
     * reference object.
     */
    public void fixRealObjectReferences(Object object, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, RemoteSession session) {
        //bug 4147755 getRealAttribute... / setReal...
        Object attributeValue = getRealAttributeValueFromObject(object, session);
        attributeValue = getReferenceDescriptor().getObjectBuilder().unwrapObject(attributeValue, session);

        ObjectLevelReadQuery tempQuery = query;
        if (!tempQuery.shouldMaintainCache()) {
            if ((!tempQuery.shouldCascadeParts()) || (tempQuery.shouldCascadePrivateParts() && (!isPrivateOwned()))) {
                tempQuery = null;
            }
        }
        Object remoteAttributeValue = session.getObjectCorrespondingTo(attributeValue, objectDescriptors, processedObjects, tempQuery);
        remoteAttributeValue = getReferenceDescriptor().getObjectBuilder().wrapObject(remoteAttributeValue, session);
        setRealAttributeValueInObject(object, remoteAttributeValue);
    }

    /**
     * INTERNAL:
     * Return a descriptor for the target of this mapping
     * @see org.eclipse.persistence.mappings.VariableOneToOneMapping
     * Bug 2612571
     */
    public ClassDescriptor getDescriptorForTarget(Object object, AbstractSession session) {
        return session.getDescriptor(object);
    }
    
    /**
     * INTERNAL:
     * Object reference must unwrap the reference object if required.
     */
    public Object getRealAttributeValueFromAttribute(Object attributeValue, Object object, AbstractSession session) {
        Object value = super.getRealAttributeValueFromAttribute(attributeValue, object, session);
        value = getReferenceDescriptor().getObjectBuilder().unwrapObject(value, session);

        return value;
    }

    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     */
    public boolean isObjectReferenceMapping() {
        return true;
    }

    /**
     * INTERNAL:
     * Iterate on the attribute value.
     * The value holder has already been processed.
     */
    public void iterateOnRealAttributeValue(DescriptorIterator iterator, Object realAttributeValue) {
        // This may be wrapped as the caller in iterate on foreign reference does not unwrap as the type is generic.
        Object unwrappedAttributeValue = getReferenceDescriptor().getObjectBuilder().unwrapObject(realAttributeValue, iterator.getSession());
        iterator.iterateReferenceObjectForMapping(unwrappedAttributeValue, this);
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object. Which is the original from the parent UnitOfWork
     */
    public void mergeChangesIntoObject(Object target, ChangeRecord changeRecord, Object source, MergeManager mergeManager) {
        Object targetValueOfSource = null;

        // The target object must be completely merged before setting it otherwise
        // another thread can pick up the partial object.
        if (shouldMergeCascadeParts(mergeManager)) {
            ObjectChangeSet set = (ObjectChangeSet)((ObjectReferenceChangeRecord)changeRecord).getNewValue();
            if (set != null) {
                if (mergeManager.shouldMergeChangesIntoDistributedCache()) {
                    //Let's try and find it first.  We may have merged it allready. In which case merge
                    //changes will  stop the recursion
                    targetValueOfSource = set.getTargetVersionOfSourceObject(mergeManager.getSession(), false);
                    if ((targetValueOfSource == null) && (set.isNew() || set.isAggregate()) && set.containsChangesFromSynchronization()) {
                        if (!mergeManager.getObjectsAlreadyMerged().containsKey(set)) {
                            // if we haven't merged this object allready then build a new object
                            // otherwise leave it as null which will stop the recursion
                            // CR 2855
                            // CR 3424 Need to build the right instance based on class type instead of refernceDescriptor
                            Class objectClass = set.getClassType(mergeManager.getSession());
                            targetValueOfSource = mergeManager.getSession().getDescriptor(objectClass).getObjectBuilder().buildNewInstance();
                            //Store the changeset to prevent us from creating this new object again
                            mergeManager.getObjectsAlreadyMerged().put(set, targetValueOfSource);
                        } else {
                            //CR 4012
                            //we have all ready created the object, must be in a cyclic
                            //merge on a new object so get it out of the allreadymerged collection
                            targetValueOfSource = mergeManager.getObjectsAlreadyMerged().get(set);
                        }
                    } else {
                        // If We have not found it anywhere else load it from the database
                        targetValueOfSource = set.getTargetVersionOfSourceObject(mergeManager.getSession(), true);
                    }
                    if (set.containsChangesFromSynchronization()) {
                        mergeManager.mergeChanges(targetValueOfSource, set);
                    }
                    //bug:3604593 - ensure reference not changed source is invalidated if target object not found
                    if (targetValueOfSource == null) {
                      mergeManager.getSession().getIdentityMapAccessorInstance().invalidateObject(target);
                      return;
                    }
                } else {
                    mergeManager.mergeChanges(set.getUnitOfWorkClone(), set);
                }
            }
        }
        if ((targetValueOfSource == null) && (((ObjectReferenceChangeRecord)changeRecord).getNewValue() != null)) {
            targetValueOfSource = ((ObjectChangeSet)((ObjectReferenceChangeRecord)changeRecord).getNewValue()).getTargetVersionOfSourceObject(mergeManager.getSession());
        }

        // Register new object in nested units of work must not be registered into the parent,
        // so this records them in the merge to parent case.
        if (isPrivateOwned() && (source != null)) {
            mergeManager.registerRemovedNewObjectIfRequired(getRealAttributeValueFromObject(source, mergeManager.getSession()));
        }

        targetValueOfSource = getReferenceDescriptor().getObjectBuilder().wrapObject(targetValueOfSource, mergeManager.getSession());
        setRealAttributeValueInObject(target, targetValueOfSource);
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     */
    public void mergeIntoObject(Object target, boolean isTargetUnInitialized, Object source, MergeManager mergeManager) {
        if (isTargetUnInitialized) {
            // This will happen if the target object was removed from the cache before the commit was attempted,
            // or for new objects.
            if (mergeManager.shouldMergeWorkingCopyIntoOriginal()) {
                if (!isAttributeValueInstantiated(source)) {
                    setAttributeValueInObject(target, this.indirectionPolicy.getOriginalIndirectionObject(getAttributeValueFromObject(source), mergeManager.getSession()));
                    return;
                } else {
                    // Must clear the old value holder to cause it to be reset.
                    this.indirectionPolicy.reset(target);
                }
            }
        }
        if (!shouldMergeCascadeReference(mergeManager)) {
            // This is only going to happen on mergeClone, and we should not attempt to merge the reference
            return;
        }
        if (mergeManager.shouldRefreshRemoteObject() && usesIndirection()) {
            mergeRemoteValueHolder(target, source, mergeManager);
            return;
        }
        if (mergeManager.shouldMergeOriginalIntoWorkingCopy()) {
            if (!isAttributeValueInstantiated(target)) {
                // This will occur when the clone's value has not been instantiated yet and we do not need
                // the refresh that attribute
                return;
            }
        } else if (!isAttributeValueInstantiated(source)) {
            // I am merging from a clone into an original.  No need to do merge if the attribute was never
            // modified
            return;
        }

        Object valueOfSource = getRealAttributeValueFromObject(source, mergeManager.getSession());

        Object targetValueOfSource = null;

        // The target object must be completely merged before setting it otherwise
        // another thread can pick up the partial object.
        if (shouldMergeCascadeParts(mergeManager) && (valueOfSource != null)) {
            if ((mergeManager.getSession().isUnitOfWork()) && (((UnitOfWorkImpl)mergeManager.getSession()).getUnitOfWorkChangeSet() != null)) {
                // If it is a unit of work, we have to check if I have a change Set fot this object
                mergeManager.mergeChanges(mergeManager.getObjectToMerge(valueOfSource), (ObjectChangeSet)((UnitOfWorkChangeSet)((UnitOfWorkImpl)mergeManager.getSession()).getUnitOfWorkChangeSet()).getObjectChangeSetForClone(valueOfSource));
            } else {
                mergeManager.mergeChanges(mergeManager.getObjectToMerge(valueOfSource), null);
            }
        }

        if (valueOfSource != null) {
            // Need to do this after merge so that an object exists in the database
            targetValueOfSource = mergeManager.getTargetVersionOfSourceObject(valueOfSource);
        }
        
        // If merge into the unit of work, must only merge and raise the event is the value changed.
        if ((mergeManager.shouldMergeCloneIntoWorkingCopy() || mergeManager.shouldMergeCloneWithReferencesIntoWorkingCopy())
                && this.descriptor.getObjectChangePolicy().isObjectChangeTrackingPolicy()) {
            // Object level or attribute level so lets see if we need to raise the event?
            Object valueOfTarget = getRealAttributeValueFromObject(target, mergeManager.getSession());
            if (valueOfTarget != targetValueOfSource) { //equality comparison cause both are uow clones
                this.descriptor.getObjectChangePolicy().raiseInternalPropertyChangeEvent(target, getAttributeName(), valueOfTarget, targetValueOfSource);
            } else {
                // No change.
                return;
            }
        }
 
        targetValueOfSource = this.referenceDescriptor.getObjectBuilder().wrapObject(targetValueOfSource, mergeManager.getSession());
        setRealAttributeValueInObject(target, targetValueOfSource);
    }
    
    /**
     * INTERNAL:
     * Return all the fields populated by this mapping, these are foreign keys only.
     */
    @Override
    protected Vector<DatabaseField> collectFields() {
        return getForeignKeyFields();
    }

    /**
     * INTERNAL:
     * Returns the foreign key names associated with the mapping.
     * These are the fields that will be populated by the 1-1 mapping when writing.
     */
    public Vector<DatabaseField> getForeignKeyFields() {
        return foreignKeyFields;
    }

    /**
    * INTERNAL:
    * Set the foreign key fields associated with the mapping.
    * These are the fields that will be populated by the 1-1 mapping when writing.
    */
    protected void setForeignKeyFields(Vector<DatabaseField> foreignKeyFields) {
        this.foreignKeyFields = foreignKeyFields;
        if (!foreignKeyFields.isEmpty()) {
            setIsForeignKeyRelationship(true);
        }
    }

    /**
     * INTERNAL:
     * Return if the 1-1 mapping has a foreign key dependency to its target.
     * This is true if any of the foreign key fields are true foreign keys,
     * i.e. populated on write from the targets primary key.
     */
    public boolean isForeignKeyRelationship() {
        return isForeignKeyRelationship;
    }

    /**
     * INTERNAL:
     * Set if the 1-1 mapping has a foreign key dependency to its target.
     * This is true if any of the foreign key fields are true foreign keys,
     * i.e. populated on write from the targets primary key.
     */
    public void setIsForeignKeyRelationship(boolean isForeignKeyRelationship) {
        this.isForeignKeyRelationship = isForeignKeyRelationship;
    }

    /**
     * INTERNAL:
     * Insert privately owned parts
     */
    public void preInsert(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (isForeignKeyRelationship()) {
            insert(query);
        }
    }

    /**
     * INTERNAL:
     * Reads the private owned object.
     */
    protected Object readPrivateOwnedForObject(ObjectLevelModifyQuery modifyQuery) throws DatabaseException {
        if (modifyQuery.getSession().isUnitOfWork()) {
            if (modifyQuery.getObjectChangeSet() != null) {
                ObjectReferenceChangeRecord record = (ObjectReferenceChangeRecord) modifyQuery.getObjectChangeSet().getChangesForAttributeNamed(getAttributeName());
                if (record != null) {
                    return record.getOldValue();
                } 
            } else { // Old commit.
                return getRealAttributeValueFromObject(modifyQuery.getBackupClone(), modifyQuery.getSession());
            }
        }
        
        return null;
    }

    /**
     * INTERNAL:
     * Update privately owned parts
     */
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

        if (!isForeignKeyRelationship()) {
            return;
        }

        update(query);
    }

    /**
     * INTERNAL:
     * Overridden by mappings that require additional processing of the change record after the record has been calculated.
     */
    @Override
    public void postCalculateChanges(org.eclipse.persistence.sessions.changesets.ChangeRecord changeRecord, UnitOfWorkImpl uow) {
        // no need for private owned check.  This code is only registered for private owned mappings.
        // targets are added to and/or removed to/from the source.
        Object oldValue = ((ObjectReferenceChangeRecord)changeRecord).getOldValue();
        if (oldValue != null) {
            uow.addDeletedPrivateOwnedObjects(this, oldValue);
        }
    }

    /**
     * INTERNAL:
     * Overridden by mappings that require additional processing of the change record after the record has been calculated.
     */
    @Override
    public void recordPrivateOwnedRemovals(Object object, UnitOfWorkImpl uow) {
        Object target = getRealAttributeValueFromObject(object, uow);
        if (target != null){
            this.referenceDescriptor.getObjectBuilder().recordPrivateOwnedRemovals(target, uow, false);
        }
    }

    /**
     * INTERNAL:
     * Delete privately owned parts
     */
    public void postDelete(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        // Deletion takes place only if it has privately owned parts and mapping is not read only.
        if (!shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        Object object = query.getProperty(this);

        // The object is stored in the query by preDeleteForObjectUsing(...).
        if (isForeignKeyRelationship()) {
            if (object != null) {
                query.removeProperty(this);

                //if the query is being passed from an aggregate collection descriptor then 
                // The delete will have been cascaded at update time.  This will cause sub objects
                // to be ignored, and real only classes to throw exceptions.
                // If it is an aggregate Collection then delay deletes until they should be deleted
                //CR 2811	
                if (query.isCascadeOfAggregateDelete()) {
                    query.getSession().getCommitManager().addObjectToDelete(object);
                } else {
                    DeleteObjectQuery deleteQuery = new DeleteObjectQuery();
                    deleteQuery.setIsExecutionClone(true);
                    deleteQuery.setObject(object);
                    deleteQuery.setCascadePolicy(query.getCascadePolicy());
                    query.getSession().executeQuery(deleteQuery);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Insert privately owned parts
     */
    public void postInsert(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!isForeignKeyRelationship()) {
            insert(query);
        }
    }

    /**
     * INTERNAL:
     * Update privately owned parts
     */
    public void postUpdate(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!isAttributeValueInstantiated(query.getObject())) {
            return;
        }

        if (!isForeignKeyRelationship()) {
            update(query);
        }

        // If a private owned reference was changed the old value will be set on the query as a property.
        Object objectInDatabase = query.getProperty(this);
        if (objectInDatabase != null) {
            query.removeProperty(this);
        } else {
            return;
        }

        // If there is no change (old commit), it must be determined if the value changed.
        if (query.getObjectChangeSet() == null) {
            Object objectInMemory = getRealAttributeValueFromObject(query.getObject(), query.getSession());
    
            // delete the object in the database if it is no more a referenced object.						
            if (objectInDatabase != objectInMemory) {
    
                Object keyForObjectInDatabase = getPrimaryKeyForObject(objectInDatabase, query.getSession());
                Object keyForObjectInMemory = null;
                if (objectInMemory != null) {
                    keyForObjectInMemory = getPrimaryKeyForObject(objectInMemory, query.getSession());
                }
    
                if ((keyForObjectInMemory != null) && keyForObjectInDatabase.equals(keyForObjectInMemory)) {
                    return;
                }
            } else {
                return;
            }
        }            
            
        if (!query.shouldCascadeOnlyDependentParts()) {
            query.getSession().deleteObject(objectInDatabase);
        }
    }

    /**
     * INTERNAL:
     * Delete privately owned parts
     */
    public void preDelete(DeleteObjectQuery query) throws DatabaseException, OptimisticLockException {
        // Deletion takes place according the the cascading policy
        if (!shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        // Get the privately owned parts.
        Object objectInMemory = getRealAttributeValueFromObject(query.getObject(), query.getSession());
        Object objectFromDatabase = null;

        // Because the value in memory may have been changed we check the previous value or database value.
        objectFromDatabase = readPrivateOwnedForObject(query);

        // If the value was changed, both values must be deleted (uow will have inserted the new one).
        if ((objectFromDatabase != null) && (objectFromDatabase != objectInMemory)) {
            // Also check pk as may not be maintaining identity.	
            Object keyForObjectInMemory = null;
            Object keyForObjectInDatabase = getPrimaryKeyForObject(objectFromDatabase, query.getSession());

            if (objectInMemory != null) {
                keyForObjectInMemory = getPrimaryKeyForObject(objectInMemory, query.getSession());
            }
            if ((keyForObjectInMemory == null) || !keyForObjectInDatabase.equals(keyForObjectInMemory)) {
                if (objectFromDatabase != null) {
                    DeleteObjectQuery deleteQuery = new DeleteObjectQuery();
                    deleteQuery.setIsExecutionClone(true);
                    deleteQuery.setObject(objectFromDatabase);
                    deleteQuery.setCascadePolicy(query.getCascadePolicy());
                    query.getSession().executeQuery(deleteQuery);
                }
            }
        }

        if (!isForeignKeyRelationship()) {
            if (objectInMemory != null) {
                DeleteObjectQuery deleteQuery = new DeleteObjectQuery();
                deleteQuery.setIsExecutionClone(true);
                deleteQuery.setObject(objectInMemory);
                deleteQuery.setCascadePolicy(query.getCascadePolicy());
                query.getSession().executeQuery(deleteQuery);
            }
        } else {
            // The actual deletion of part takes place in postDeleteForObjectUsing(...).
            if (objectInMemory != null) {
                query.setProperty(this, objectInMemory);
            }
        }
    }

    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects){
        cascadePerformRemoveIfRequired(object, uow, visitedObjects, true);
    }
    
    /**
     * INTERNAL:
     * Cascade remove through mappings that require the cascade.
     * @param object is either the source object, or attribute value if getAttributeValueFromObject is true.
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects, boolean getAttributeValueFromObject) {
        if (!isCascadeRemove()) {
            return;
        }
        Object attributeValue = null;
        if (getAttributeValueFromObject) {
            attributeValue = getAttributeValueFromObject(object);
        } else {
            attributeValue = object;
        }
        if (attributeValue != null) {
            if (getAttributeValueFromObject) {
                attributeValue = this.indirectionPolicy.getRealAttributeValueFromObject(object, attributeValue);
            }
            if (attributeValue != null && (! visitedObjects.containsKey(attributeValue)) ){
                visitedObjects.put(attributeValue, attributeValue);
                uow.performRemove(attributeValue, visitedObjects);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Cascade removal of orphaned private owned objects from the UnitOfWorkChangeSet
     */
    public void cascadePerformRemovePrivateOwnedObjectFromChangeSetIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        // if the object is not instantiated, do not instantiate or cascade
        Object attributeValue = getAttributeValueFromObject(object);
        if (attributeValue != null && this.indirectionPolicy.objectIsInstantiated(attributeValue)) {
            Object realValue = getRealAttributeValueFromObject(object, uow);
            if (!visitedObjects.containsKey(realValue)){
                visitedObjects.put(realValue, realValue);
                // remove private owned object from UnitOfWork ChangeSet
                uow.performRemovePrivateOwnedObjectFromChangeSet(realValue, visitedObjects);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Cascade discover and persist new objects during commit.
     */
    public void cascadeDiscoverAndPersistUnregisteredNewObjects(Object object, Map newObjects, Map unregisteredExistingObjects, Map visitedObjects, UnitOfWorkImpl uow) {
        cascadeDiscoverAndPersistUnregisteredNewObjects(object, newObjects, unregisteredExistingObjects, visitedObjects, uow, true);
    }
    
    /**
     * INTERNAL:
     * Cascade discover and persist new objects during commit.
     */
    public void cascadeDiscoverAndPersistUnregisteredNewObjects(Object object, Map newObjects, Map unregisteredExistingObjects, Map visitedObjects, UnitOfWorkImpl uow, boolean getAttributeValueFromObject) {
        Object attributeValue = null;
        if (getAttributeValueFromObject){
            attributeValue = getAttributeValueFromObject(object);
        } else {
            attributeValue = object;
        }
        if (attributeValue != null && this.indirectionPolicy.objectIsInstantiated(attributeValue)) {
            if (getAttributeValueFromObject){
                attributeValue = this.indirectionPolicy.getRealAttributeValueFromObject(object, attributeValue);
            }
            // remove private owned object from uow list
            if (isCandidateForPrivateOwnedRemoval()) {
                uow.removePrivateOwnedObject(this, attributeValue);
            }
            uow.discoverAndPersistUnregisteredNewObjects(attributeValue, isCascadePersist(), newObjects, unregisteredExistingObjects, visitedObjects);
        }
    }
    
    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects){
        cascadeRegisterNewIfRequired(object, uow, visitedObjects, true);
    }
    
    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     * @param object is either the source object, or attribute value if getAttributeValueFromObject is true.
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects, boolean getAttributeValueFromObject) {
        if (!isCascadePersist()) {
            return;
        }
        Object attributeValue = null;
        if (getAttributeValueFromObject) {
            attributeValue = getAttributeValueFromObject(object);
        } else {
            attributeValue = object;
        }
        if ((attributeValue != null) && this.indirectionPolicy.objectIsInstantiated(attributeValue)){
            if (getAttributeValueFromObject){
                attributeValue = this.indirectionPolicy.getRealAttributeValueFromObject(object, attributeValue);
            }
            uow.registerNewObjectForPersist(attributeValue, visitedObjects);
            // add private owned object to uow list if mapping is a candidate and uow should discover new objects and the source object is new.
            if (isCandidateForPrivateOwnedRemoval() && uow.shouldDiscoverNewObjects() && attributeValue != null && uow.isObjectNew(object)) {
                uow.addPrivateOwnedObject(this, attributeValue);
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    protected Object getPrimaryKeyForObject(Object object, AbstractSession session) {
        return getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(object, session);
    }

    /**
     * INTERNAL:
     * The returns if the mapping has any constraint dependencies, such as foreign keys and join tables.
     */
    public boolean hasConstraintDependency() {
        return isForeignKeyRelationship();
    }

    /**
     * INTERNAL:
     * Builder the unit of work value holder.
     * @param buildDirectlyFromRow indicates that we are building the clone directly
     * from a row as opposed to building the original from the row, putting it in
     * the shared cache, and then cloning the original.
     */
    public UnitOfWorkValueHolder createUnitOfWorkValueHolder(ValueHolderInterface attributeValue, Object original, Object clone, AbstractRecord row, UnitOfWorkImpl unitOfWork, boolean buildDirectlyFromRow) {
        UnitOfWorkQueryValueHolder valueHolder = null;
        if ((row == null) && (isPrimaryKeyMapping())) {
            // The row must be built if a primary key mapping for remote case.
            AbstractRecord rowFromTargetObject = extractPrimaryKeyRowForSourceObject(original, unitOfWork);
            valueHolder = new UnitOfWorkQueryValueHolder(attributeValue, clone, this, rowFromTargetObject, unitOfWork);
        } else {
            valueHolder = new UnitOfWorkQueryValueHolder(attributeValue, clone, this, row, unitOfWork);
        }

        // In case of joined attributes it so happens that the attributeValue 
        // contains a registered clone, as valueFromRow was called with a
        // UnitOfWork.  So switch the values.
        // Note that this UOW valueholder starts off as instantiated but that
        // is fine, for the reality is that it is.
        if (buildDirectlyFromRow && attributeValue.isInstantiated()) {
            Object cloneAttributeValue = attributeValue.getValue();
            valueHolder.privilegedSetValue(cloneAttributeValue);
            valueHolder.setInstantiated();

            // PERF: Do not modify the original value-holder, it is never used.
        }
        return valueHolder;
    }

    /**
     * INTERNAL:
     * Extract the reference pk for rvh usage in remote model.
     */
    public AbstractRecord extractPrimaryKeyRowForSourceObject(Object domainObject, AbstractSession session) {
        AbstractRecord databaseRow = getDescriptor().getObjectBuilder().createRecord(session);
        writeFromObjectIntoRow(domainObject, databaseRow, session, WriteType.UNDEFINED);
        return databaseRow;
    }

    /**
     * INTERNAL:
     * Extract the reference pk for rvh usage in remote model.
     */
    public Object extractPrimaryKeysForReferenceObject(Object domainObject, AbstractSession session) {
        return this.indirectionPolicy.extractPrimaryKeyForReferenceObject(getAttributeValueFromObject(domainObject), session);
    }

    /**
     * INTERNAL:
     * Return the primary key for the reference object (i.e. the object
     * object referenced by domainObject and specified by mapping).
     * This key will be used by a RemoteValueHolder.
     */
    public Object extractPrimaryKeysForReferenceObjectFromRow(AbstractRecord row) {
        return null;
    }

    /**
     * INTERNAL:
     * Extract the reference pk for rvh usage in remote model.
     */
    public Object extractPrimaryKeysFromRealReferenceObject(Object object, AbstractSession session) {
        if (object == null) {
            return null;
        } else {
            Object implementation = getReferenceDescriptor().getObjectBuilder().unwrapObject(object, session);
            return getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(implementation, session);
        }
    }

    /**
     * INTERNAL:
     * Initialize the state of mapping.
     */
    public void preInitialize(AbstractSession session) throws DescriptorException {
        super.preInitialize(session);
		//Bug#4251902 Make Proxy Indirection writable and readable to deployment xml.  If ProxyIndirectionPolicy does not
		//have any targetInterfaces, build a new set.
        if ((this.indirectionPolicy instanceof ProxyIndirectionPolicy) && !((ProxyIndirectionPolicy)this.indirectionPolicy).hasTargetInterfaces()) {
            useProxyIndirection();
        }
    }

    /**
     * INTERNAL:
      * Insert privately owned parts
     */
    protected void insert(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        // Checks if privately owned parts should be inserted or not.
        if (!shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        // Get the privately owned parts
        Object object = getRealAttributeValueFromObject(query.getObject(), query.getSession());

        if (object == null) {
            return;
        }
        ObjectChangeSet changeSet = null;
        UnitOfWorkChangeSet uowChangeSet = null;
        // Get changeSet for referenced object.  Change record may not exist for new objects, so always lookup.
        if (query.getSession().isUnitOfWork() && (((UnitOfWorkImpl)query.getSession()).getUnitOfWorkChangeSet() != null)) {
            uowChangeSet = (UnitOfWorkChangeSet)((UnitOfWorkImpl)query.getSession()).getUnitOfWorkChangeSet();
            changeSet = (ObjectChangeSet)uowChangeSet.getObjectChangeSetForClone(object);
            // PERF: If the changeSet is null it must be existing, if it is not new, then cascading is not required.
            if (changeSet == null || !changeSet.isNew()) {
                return;
            }
        }

        WriteObjectQuery writeQuery = null;
        // If private owned, the dependent objects should also be new.
        // However a bug was logged was put in to allow dependent objects to be existing in a unit of work,
        // so this allows existing dependent objects in the unit of work.
        if (isPrivateOwned() && ((changeSet == null) || (changeSet.isNew()))) {
            // no identity check needed for private owned
            writeQuery = new InsertObjectQuery();
        } else {
            writeQuery = new WriteObjectQuery();
        }
        writeQuery.setIsExecutionClone(true);
        writeQuery.setObject(object);
        writeQuery.setObjectChangeSet(changeSet);
        writeQuery.setCascadePolicy(query.getCascadePolicy());
        query.getSession().executeQuery(writeQuery);
    }

    /**
     * INTERNAL:
     * Update the private owned part.
     */
    protected void update(WriteObjectQuery query) throws DatabaseException, OptimisticLockException {
        if (!shouldObjectModifyCascadeToParts(query)) {
            return;
        }

        // If objects are not instantiated that means they are not changed.
        if (!isAttributeValueInstantiated(query.getObject())) {
            return;
        }

        // Get the privately owned parts in the memory
        Object object = getRealAttributeValueFromObject(query.getObject(), query.getSession());
        if (object != null) {
            ObjectChangeSet changeSet = query.getObjectChangeSet();
            if (changeSet != null) {
                ObjectReferenceChangeRecord changeRecord = (ObjectReferenceChangeRecord)query.getObjectChangeSet().getChangesForAttributeNamed(getAttributeName());
                if (changeRecord != null) {
                    changeSet = (ObjectChangeSet)changeRecord.getNewValue();
                    // PERF: If it is not new, then cascading is not required.
                    if (!changeSet.isNew()) {
                        return;
                    }
                } else {
                    // no changeRecord no change to reference.
                    return;
                }
            } else {
                UnitOfWorkChangeSet uowChangeSet = null;
                // Get changeSet for referenced object.
                if (query.getSession().isUnitOfWork() && (((UnitOfWorkImpl)query.getSession()).getUnitOfWorkChangeSet() != null)) {
                    uowChangeSet = (UnitOfWorkChangeSet)((UnitOfWorkImpl)query.getSession()).getUnitOfWorkChangeSet();
                    changeSet = (ObjectChangeSet)uowChangeSet.getObjectChangeSetForClone(object);
                    // PERF: If the changeSet is null it must be existing, if it is not new, then cascading is not required.
                    if (changeSet == null || !changeSet.isNew()) {
                        return;
                    }
                }
            }
            // PERF: Only write dependent object if they are new.
            if ((!query.shouldCascadeOnlyDependentParts()) || (changeSet == null) || changeSet.isNew()) {
                WriteObjectQuery writeQuery = new WriteObjectQuery();
                writeQuery.setIsExecutionClone(true);
                writeQuery.setObject(object);
                writeQuery.setObjectChangeSet(changeSet);
                writeQuery.setCascadePolicy(query.getCascadePolicy());
                query.getSession().executeQuery(writeQuery);
            }
        }
    }

    /**
     * PUBLIC:
     * Set this mapping to use Proxy Indirection.
     *
     * Proxy Indirection uses the <CODE>Proxy</CODE> and <CODE>InvocationHandler</CODE> features
     * of JDK 1.3 to provide "transparent indirection" for 1:1 relationships.  In order to use Proxy
     * Indirection:<P>
     *
     * <UL>
     *        <LI>The target class must implement at least one public interface
     *        <LI>The attribute on the source class must be typed as that public interface
     *        <LI>get() and set() methods for the attribute must use the interface
     * </UL>
     *
     * With this policy, proxy objects are returned during object creation.  When a message other than
     * <CODE>toString</CODE> is called on the proxy the real object data is retrieved from the database.
     *
     * By default, use the target class' full list of interfaces for the proxy.
     *
     */
    public void useProxyIndirection() {
        Class[] targetInterfaces = getReferenceClass().getInterfaces();
        if (!getReferenceClass().isInterface() && getReferenceClass().getSuperclass() == null) {
            setIndirectionPolicy(new ProxyIndirectionPolicy(targetInterfaces));
        } else {
            HashSet targetInterfacesCol = new HashSet();        
            //Bug#4432781 Include all the interfaces and the super interfaces of the target class
            if (getReferenceClass().getSuperclass() != null) {
                buildTargetInterfaces(getReferenceClass(), targetInterfacesCol);
            }
			//Bug#4251902 Make Proxy Indirection writable and readable to deployment xml.  If
			//ReferenceClass is an interface, it needs to be included in the array.
            if (getReferenceClass().isInterface()) {
                targetInterfacesCol.add(getReferenceClass());
            }
            targetInterfaces = (Class[])targetInterfacesCol.toArray(targetInterfaces);
            setIndirectionPolicy(new ProxyIndirectionPolicy(targetInterfaces));
        }
    }

    /**
     * INTERNAL: This method will access the target relationship and create a
     * list of PKs of the target entities. This method is used in combination
     * with the CachedValueHolder to store references to PK's to be loaded from
     * a cache instead of a query.
     */
    public Object[] buildReferencesPKList(Object entity, Object attribute, AbstractSession session) {
        ClassDescriptor referenceDescriptor = getReferenceDescriptor();
        Object target = this.indirectionPolicy.getRealAttributeValueFromObject(entity, attribute);
        Object[] result = new Object[1];
        if (target != null){
            CMPPolicy policy = referenceDescriptor.getCMPPolicy();
            if (policy != null && policy.isCMP3Policy()) {
                result[0] = policy.createPrimaryKeyInstance(target, session);
            } else {
                result[0] = referenceDescriptor.getObjectBuilder().extractPrimaryKeyFromObject(target, session);
            }
        }
        return result;
    }

    /**
     * INTERNAL:
     * Build a list of all the interfaces and super interfaces for a given class.
     */
    public Collection buildTargetInterfaces(Class aClass, Collection targetInterfacesCol) {
        Class[] targetInterfaces = aClass.getInterfaces();
        for (int index = 0; index < targetInterfaces.length; index++) {
            targetInterfacesCol.add(targetInterfaces[index]);
        }
        if (aClass.getSuperclass() == null) {
            return targetInterfacesCol;
        } else {
            return buildTargetInterfaces(aClass.getSuperclass(), targetInterfacesCol);
        }
    }
    
    /**
     * PUBLIC:
     * Set this mapping to use Proxy Indirection.
     *
     * Proxy Indirection uses the <CODE>Proxy</CODE> and <CODE>InvocationHandler</CODE> features
     * of JDK 1.3 to provide "transparent indirection" for 1:1 relationships.  In order to use Proxy
     * Indirection:<P>
     *
     * <UL>
     *        <LI>The target class must implement at least one public interface
     *        <LI>The attribute on the source class must be typed as that public interface
     *        <LI>get() and set() methods for the attribute must use the interface
     * </UL>
     *
     * With this policy, proxy objects are returned during object creation.  When a message other than
     * <CODE>toString</CODE> is called on the proxy the real object data is retrieved from the database.
     *
     * @param    proxyInterfaces        The interfaces that the target class implements.  The attribute must be typed
     *                                as one of these interfaces.
     */
    public void useProxyIndirection(Class[] targetInterfaces) {
        setIndirectionPolicy(new ProxyIndirectionPolicy(targetInterfaces));
    }

    /**
     * PUBLIC:
     * Set this mapping to use Proxy Indirection.
     *
     * Proxy Indirection uses the <CODE>Proxy</CODE> and <CODE>InvocationHandler</CODE> features
     * of JDK 1.3 to provide "transparent indirection" for 1:1 relationships.  In order to use Proxy
     * Indirection:<P>
     *
     * <UL>
     *        <LI>The target class must implement at least one public interface
     *        <LI>The attribute on the source class must be typed as that public interface
     *        <LI>get() and set() methods for the attribute must use the interface
     * </UL>
     *
     * With this policy, proxy objects are returned during object creation.  When a message other than
     * <CODE>toString</CODE> is called on the proxy the real object data is retrieved from the database.
     *
     * @param    proxyInterface        The interface that the target class implements.  The attribute must be typed
     *                                as this interface.
     */
    public void useProxyIndirection(Class targetInterface) {
        Class[] targetInterfaces = new Class[] { targetInterface };
        setIndirectionPolicy(new ProxyIndirectionPolicy(targetInterfaces));
    }

    /**
     * INTERNAL: 
     * This method is used to load a relationship from a list of PKs.
     * This list may be available if the relationship has been cached.
     */
    public Object valueFromPKList(Object[] pks, AbstractSession session) {
        Object pk = null;
        if (pks[0] == null) return null;
        if (getReferenceDescriptor().hasCMPPolicy()) {
            pk = getReferenceDescriptor().getCMPPolicy().createPrimaryKeyFromId(pks[0], session);
        } else {
            pk = pks[0];
        }
        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(getReferenceClass());
        query.setSelectionId(pk);
        query.setIsExecutionClone(true);
        return session.executeQuery(query);
    }

    /**
     * INTERNAL: 
     * To verify if the specified object is deleted or not.
     */
    public boolean verifyDelete(Object object, AbstractSession session) throws DatabaseException {
        if (isPrivateOwned()) {
            Object attributeValue = getRealAttributeValueFromObject(object, session);

            if (attributeValue != null) {
                return session.verifyDelete(attributeValue);
            }
        }

        return true;
    }

    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     * But before that check if the reference object is instantiated or not.
     */
    public void writeFromObjectIntoRowForUpdate(WriteObjectQuery query, AbstractRecord databaseRow) {
        Object object = query.getObject();
        AbstractSession session = query.getSession();

        if (!isAttributeValueInstantiated(object)) {
            return;
        }

        if (session.isUnitOfWork()) {
            if (compareObjectsWithoutPrivateOwned(query.getBackupClone(), object, session)) {
                return;
            }
        }

        writeFromObjectIntoRow(object, databaseRow, session, WriteType.UPDATE);
    }

    /**
     * INTERNAL:
     * Get a value from the object and set that in the respective field of the row.
     */
    public void writeFromObjectIntoRowForWhereClause(ObjectLevelModifyQuery query, AbstractRecord databaseRow) {
        if (isReadOnly()) {
            return;
        }

        if (query.isDeleteObjectQuery()) {
            writeFromObjectIntoRow(query.getObject(), databaseRow, query.getSession(), WriteType.UNDEFINED);
        } else {
            // If the original was never instantiated the backup clone has a ValueHolder of null 
            // so for this case we must extract from the original object.
            if (isAttributeValueInstantiated(query.getObject())) {
                writeFromObjectIntoRow(query.getBackupClone(), databaseRow, query.getSession(), WriteType.UNDEFINED);
            } else {
                writeFromObjectIntoRow(query.getObject(), databaseRow, query.getSession(), WriteType.UNDEFINED);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Return if this mapping supports change tracking.
     */
    public boolean isChangeTrackingSupported(Project project) {
        return true;
    }
    
    /**
     * INTERNAL:
     * Either create a new change record or update the change record with the new value.
     * This is used by attribute change tracking.
     */
    public void updateChangeRecord(Object clone, Object newValue, Object oldValue, ObjectChangeSet objectChangeSet, UnitOfWorkImpl uow) {
        // Must ensure values are unwrapped.
        Object unwrappedNewValue = newValue;
        Object unwrappedOldValue = oldValue;
        if (newValue != null) {
            unwrappedNewValue = getReferenceDescriptor().getObjectBuilder().unwrapObject(newValue, uow);
        }
        if (oldValue != null) {
            unwrappedOldValue = getReferenceDescriptor().getObjectBuilder().unwrapObject(oldValue, uow);
        }
        ObjectReferenceChangeRecord changeRecord = (ObjectReferenceChangeRecord)objectChangeSet.getChangesForAttributeNamed(this.getAttributeName());
        if (changeRecord == null) {
            changeRecord = internalBuildChangeRecord(unwrappedNewValue, objectChangeSet, uow);
            changeRecord.setOldValue(unwrappedOldValue);
            objectChangeSet.addChange(changeRecord);
            
        } else {
            setNewValueInChangeRecord(unwrappedNewValue, changeRecord, objectChangeSet, uow);
        }
    }

    /**
     * INTERNAL:
     * Directly build a change record without comparison
     */
    public ChangeRecord buildChangeRecord(Object clone, ObjectChangeSet owner, AbstractSession session) {
        return internalBuildChangeRecord(getRealAttributeValueFromObject(clone, session), owner, session);
    }
}
