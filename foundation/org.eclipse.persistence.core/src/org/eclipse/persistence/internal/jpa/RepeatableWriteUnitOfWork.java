/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
        Gordon Yorke - VM managed entity detachment
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa;

import java.util.*;

import org.eclipse.persistence.config.FlushClearCache;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.sessions.IdentityMapAccessor;
import org.eclipse.persistence.sessions.factories.ReferenceMode;


public class RepeatableWriteUnitOfWork extends UnitOfWorkImpl {
    
    /** Used to store the final UnitOfWorkChangeSet for merge into the shared cache */
    protected UnitOfWorkChangeSet cumulativeUOWChangeSet;
    /** Used to store objects already deleted from the db and unregistered */
    protected Map unregisteredDeletedObjectsCloneToBackupAndOriginal;
    
    /** Used to determine if UnitOfWork should commit and rollback transactions 
     * This is used when an EntityTransaction is controlling the transaction
     */
    protected boolean shouldTerminateTransaction;
    
    /** The FlashClearCache mode to be used (see oracle.toplink.config.FlushClearCache).
     * Initialized by setUnitOfWorkChangeSet method in case it's null;
     * commitAndResume sets this attribute back to null.
     * Relevant only in case call to flush method followed by call to clear method.
     */
    protected transient String flushClearCache;
    
    /** Contains classes that should be invalidated in the shared cache on commit.
     * Used only in case fushClearCache == FlushClearCache.DropInvalidate:
     * clear method copies contents of updatedObjectsClasses to this set,
     * adding classes of deleted objects, too;
     * on commit the classes contained here are invalidated in the shared cache
     * and the set is cleared.
     * Relevant only in case call to flush method followed by call to clear method.
     * Works together with flushClearCache.
     */
    protected transient Set<Class> classesToBeInvalidated;
    
    public RepeatableWriteUnitOfWork(org.eclipse.persistence.internal.sessions.AbstractSession parentSession, ReferenceMode referenceMode){
        super(parentSession, referenceMode);
        this.shouldTerminateTransaction = true;
        this.shouldNewObjectsBeCached = true;
    }
    
    /**
     * INTERNAL:
     * This method will clear all registered objects from this UnitOfWork.
     * If parameter value is 'true' then the cache(s) are cleared, too.
     */
    public void clear(boolean shouldClearCache) {
        super.clear(shouldClearCache);
        if(cumulativeUOWChangeSet != null) {
            if(flushClearCache == null) {
                flushClearCache = PropertiesHandler.getSessionPropertyValueLogDebug(PersistenceUnitProperties.FLUSH_CLEAR_CACHE, this);
                if(flushClearCache == null) {
                    flushClearCache = FlushClearCache.DEFAULT;
                }
            }
            if(flushClearCache == FlushClearCache.Drop) {
                cumulativeUOWChangeSet = null;
                unregisteredDeletedObjectsCloneToBackupAndOriginal = null;
            } else if(flushClearCache == FlushClearCache.DropInvalidate) {
                // classes of the updated objects should be invalidated in the shared cache on commit.
                Set updatedObjectsClasses = cumulativeUOWChangeSet.findUpdatedObjectsClasses();
                if(updatedObjectsClasses != null) {
                    if(classesToBeInvalidated == null) {
                        classesToBeInvalidated = updatedObjectsClasses;
                    } else {
                        classesToBeInvalidated.addAll(updatedObjectsClasses);
                    }
                }
                // unregisteredDeletedObjectsCloneToBackupAndOriginal != null because cumulativeUOWChangeSet != null
                if(!unregisteredDeletedObjectsCloneToBackupAndOriginal.isEmpty()) {
                    if(classesToBeInvalidated == null) {
                        classesToBeInvalidated = new HashSet<Class>();
                    }
                    Iterator enumDeleted = unregisteredDeletedObjectsCloneToBackupAndOriginal.keySet().iterator();
                    // classes of the deleted objects should be invalidated in the shared cache
                    while(enumDeleted.hasNext()) {
                        classesToBeInvalidated.add(enumDeleted.next().getClass());
                    }
                }
                cumulativeUOWChangeSet = null;
                unregisteredDeletedObjectsCloneToBackupAndOriginal = null;
            }
        }
    }
    
    /**
     * INTERNAL:
     * Call this method if the uow will no longer used for committing transactions:
     * all the changes sets will be dereferenced, and (optionally) the cache cleared.
     * If the uow is not released, but rather kept around for ValueHolders, then identity maps shouldn't be cleared:
     * the parameter value should be 'false'. The lifecycle set to Birth so that uow ValueHolder still could be used.
     * Alternatively, if called from release method then everything should go and therefore parameter value should be 'true'.
     * In this case lifecycle won't change - uow.release (optionally) calls this method when it (uow) is already dead.
     * The reason for calling this method from release is to free maximum memory right away:
     * the uow might still be referenced by objects using UOWValueHolders (though they shouldn't be around
     * they still might).
     */
    public void clearForClose(boolean shouldClearCache){
        this.cumulativeUOWChangeSet = null;
        this.unregisteredDeletedObjectsCloneToBackupAndOriginal = null;
        super.clearForClose(shouldClearCache);
    }
    
    /**
     * INTERNAL:
     * Indicates whether clearForClose method should be called by release method.
     */
    public boolean shouldClearForCloseOnRelease() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Commit the changes to any objects to the parent.
     */
    public void commitRootUnitOfWork() throws DatabaseException, OptimisticLockException {
        commitToDatabaseWithChangeSet(false);
        // unit of work has been committed so it's ok to set the cumulative into the UOW for merge
        if(this.cumulativeUOWChangeSet != null) {
            this.cumulativeUOWChangeSet.mergeUnitOfWorkChangeSet((UnitOfWorkChangeSet)this.getUnitOfWorkChangeSet(), this, true);
            setUnitOfWorkChangeSet(this.cumulativeUOWChangeSet);
        }

        commitTransactionAfterWriteChanges(); // this method will commit the transaction
                                              // and set the transaction flags appropriately

        // Merge after commit	
        mergeChangesIntoParent();
    }

    /**
     * INTERNAL:
     * Traverse the object to find references to objects not registered in this unit of work.
     * Any unregistered new objects found will be persisted or an error will be thrown depending on the mapping's cascade persist.
     * References to deleted objects will also currently cause them to be undeleted.
     */
    public void discoverUnregisteredNewObjects(Map clones, Map newObjects, Map unregisteredExistingObjects, Map visitedObjects) {
        for (Iterator clonesEnum = clones.keySet().iterator(); clonesEnum.hasNext(); ) {        
            discoverAndPersistUnregisteredNewObjects(clonesEnum.next(), false, newObjects, unregisteredExistingObjects, visitedObjects);
        }
    }
    
    /**
     * INTERNAL:
     * Has writeChanges() been attempted on this UnitOfWork?  It may have
     * either succeeded or failed but either way the UnitOfWork is in a highly
     * restricted state.
     */
    public boolean isAfterWriteChangesButBeforeCommit() {
        //don't check for writechanges failure.
        return (getLifecycle() == CommitTransactionPending);
    }

    /**
     * INTERNAL:
     * Return if the object has been deleted in this unit of work.
     */
    public boolean isObjectDeleted(Object object) {
        if(super.isObjectDeleted(object)) {
            return true;
        } else {
            if(unregisteredDeletedObjectsCloneToBackupAndOriginal != null) {
                if(unregisteredDeletedObjectsCloneToBackupAndOriginal.containsKey(object)) {
                    return true;
                }
            }
            if (hasObjectsDeletedDuringCommit()) {
                return getObjectsDeletedDuringCommit().containsKey(object);
            } else {
                return false;
            }
        }
    }

    /**
     * INTERNAL:
     * For synchronized units of work, dump SQL to database
     */
    public void issueSQLbeforeCompletion() {

        super.issueSQLbeforeCompletion(false);

        if (this.cumulativeUOWChangeSet != null && this.getUnitOfWorkChangeSet() != null){
            // unit of work has been committed so it's ok to set the cumulative into the UOW for merge
            this.cumulativeUOWChangeSet.mergeUnitOfWorkChangeSet((UnitOfWorkChangeSet)this.getUnitOfWorkChangeSet(), this, true);
            setUnitOfWorkChangeSet(this.cumulativeUOWChangeSet);
        }

        commitTransactionAfterWriteChanges(); // this method will commit the transaction
                                              // and set the transaction flags appropriately
    }
    
    /**
     * INTERNAL: Merge the changes to all objects to the parent.
     */
    protected void mergeChangesIntoParent() {
        if(classesToBeInvalidated != null) {
            // get identityMap of the parent ServerSession
            IdentityMapAccessor accessor = this.getParentIdentityMapSession(null, false, true).getIdentityMapAccessor();
            Iterator<Class> it = classesToBeInvalidated.iterator();
            while(it.hasNext()) {
               accessor.invalidateClass(it.next(), false);
            }
            classesToBeInvalidated = null;
        }
        flushClearCache = null;
        super.mergeChangesIntoParent();
    }
    
    /**
     * INTERNAL:
     * Merge the attributes of the clone into the unit of work copy.
     */
    public Object mergeCloneWithReferences(Object rmiClone, MergeManager manager) {
        Object mergedObject = super.mergeCloneWithReferences(rmiClone, manager);
        
        //iterate over new objects, assign sequences and put in the identitymap
        Iterator iterator = manager.getMergedNewObjects().values().iterator();
        while (iterator.hasNext()) {
            Object newObjectClone = iterator.next();
            ClassDescriptor descriptor = getDescriptor(newObjectClone);
            assignSequenceNumber(newObjectClone, descriptor);
            registerNewObjectInIdentityMap(newObjectClone, null, descriptor);
        }
        
        return mergedObject;
    }

    /**
     * INTERNAL:
     * This method is used internally to update the tracked objects if required
     */
    public void updateChangeTrackersIfRequired(Object objectToWrite, ObjectChangeSet changeSetToWrite, UnitOfWorkImpl uow, ClassDescriptor descriptor) {
        descriptor.getObjectChangePolicy().updateWithChanges(objectToWrite, changeSetToWrite, uow, descriptor);
    }

    /**
     * INTERNAL:
     * This method will cause the all of the tracked objects at this level to have
     * their changes written to the database.  It will then decrement the depth
     * level.
     */
    public void writeChanges() {
            if (unregisteredDeletedObjectsCloneToBackupAndOriginal == null) {
                unregisteredDeletedObjectsCloneToBackupAndOriginal = new IdentityHashMap(2);
            }
            if (getUnitOfWorkChangeSet() == null) {
                setUnitOfWorkChangeSet(new UnitOfWorkChangeSet());
            }
            // This also assigns sequence numbers and discover unregistered new objects.
            calculateChanges(getCloneMapping(), (UnitOfWorkChangeSet)getUnitOfWorkChangeSet(), true);
            // Write those changes to the database.
            UnitOfWorkChangeSet changeSet = (UnitOfWorkChangeSet)getUnitOfWorkChangeSet();
            if (!changeSet.hasChanges() && !changeSet.hasForcedChanges() && ! this.hasDeletedObjects() && ! this.hasModifyAllQueries()){
            	return;
            }
            try {
                commitToDatabaseWithPreBuiltChangeSet(changeSet, false);
                writesCompleted();
            } catch (RuntimeException exception) {
                clearFlushClearCache();
                setLifecycle(WriteChangesFailed);
                throw exception;
            }

            Iterator enumtr = getNewObjectsCloneToOriginal().keySet().iterator();
            while (enumtr.hasNext()) {
                Object clone = enumtr.next();
                Object original = getNewObjectsCloneToOriginal().get(clone);
                if (original != null) {
                    // No longer new to this unit of work, so need to store original.
                    getCloneToOriginals().put(clone, original);
                }
            }
            getNewObjectsCloneToOriginal().clear();
            getNewObjectsOriginalToClone().clear();
            getUnregisteredExistingObjects().clear();
            getUnregisteredNewObjects().clear();
            
            // bug 4730595: fix puts deleted objects in the UnitOfWorkChangeSet as they are removed.
            getDeletedObjects().clear();
            // Unregister all deleted objects,
            // keep them along with their original and backup values in unregisteredDeletedObjectsCloneToBackupAndOriginal.
            Iterator enumDeleted = getObjectsDeletedDuringCommit().keySet().iterator();
            while (enumDeleted.hasNext()) {
                Object deletedObject = enumDeleted.next();
                Object[] backupAndOriginal = {getCloneMapping().get(deletedObject), getCloneToOriginals().get(deletedObject)};
                unregisteredDeletedObjectsCloneToBackupAndOriginal.put(deletedObject, backupAndOriginal);
                unregisterObject(deletedObject);
            }
            getObjectsDeletedDuringCommit().clear();

            if (this.cumulativeUOWChangeSet == null) {
                this.cumulativeUOWChangeSet = (UnitOfWorkChangeSet)getUnitOfWorkChangeSet();
            } else {
                // Merge those changes back into the backup clones and the final uowChangeSet.
                this.cumulativeUOWChangeSet.mergeUnitOfWorkChangeSet((UnitOfWorkChangeSet)getUnitOfWorkChangeSet(), this, true);
            }
            // Clean up, new objects are now existing.
            setUnitOfWorkChangeSet(new UnitOfWorkChangeSet());
            this.changeTrackedHardList = null;
        }

    /**
     * INTERNAL:
     * Called only by registerNewObjectForPersist method,
     * and only if newObject is not already registered.
     * If newObject is found in
     * unregisteredDeletedObjectsCloneToBackupAndOriginal then it's re-registered,
     * otherwise the superclass method called.
     */
    protected void registerNotRegisteredNewObjectForPersist(Object newObject, ClassDescriptor descriptor) {
        if(unregisteredDeletedObjectsCloneToBackupAndOriginal != null) {
            Object[] backupAndOriginal = (Object[])unregisteredDeletedObjectsCloneToBackupAndOriginal.remove(newObject);
            if(backupAndOriginal != null) {
                // backup
                getCloneMapping().put(newObject, backupAndOriginal[0]);
                // original
                registerNewObjectClone(newObject, backupAndOriginal[1], descriptor);

                // Check if the new objects should be cached.
                registerNewObjectInIdentityMap(newObject, newObject, descriptor);
                
                return;
            }
        }
        super.registerNotRegisteredNewObjectForPersist(newObject, descriptor);
    }

    /**
     * INTERNAL:
     * This is internal to the uow, transactions should not be used explictly in a uow.
     * The uow shares its parents transactions.
     */
    public void rollbackTransaction() throws DatabaseException {
        if (this.shouldTerminateTransaction || getParent().getTransactionMutex().isNested()){
            super.rollbackTransaction();
        }else{
            //rollback called which means txn failed.
            //but rollback was stopped by entitytransaction which means the
            //transaction will want to call release later.  Make sure release
            //will rollback transaction.
            setWasTransactionBegunPrematurely(true);
        }
    }

    /**
     * INTERNAL
     * Synchronize the clones and update their backup copies.
     * Called after commit and commit and resume.
     */
    public void synchronizeAndResume() {
        this.cumulativeUOWChangeSet = null;
        this.unregisteredDeletedObjectsCloneToBackupAndOriginal = null;
        super.synchronizeAndResume();
    }
    
    /**
     * INTERNAL:
     * Return if the object was deleted previously (in a flush).
     */
    public boolean wasDeleted(Object original) {
        return getUnregisteredDeletedCloneForOriginal(original) != null;
    }
    
    /**
     * INTERNAL:
     * To avoid putting the original object into the shared cache, and
     * therefore, impede the 'detaching' of the original after commit, a clone 
     * of the original should be registered not the actual original object. 
     * This is a JPA override to traditional Eclipselink behavior.
     */
    @Override
    protected Object cloneAndRegisterNewObject(Object original) {
        ClassDescriptor descriptor = getDescriptor(original);
        //Nested unit of work is not supported for attribute change tracking
        if (isNestedUnitOfWork() && (descriptor.getObjectChangePolicy() instanceof AttributeChangeTrackingPolicy)) {
            throw ValidationException.nestedUOWNotSupportedForAttributeTracking();
        }
        ObjectBuilder builder = descriptor.getObjectBuilder();

        // bug 2612602 create the working copy object.
        Object clone = builder.instantiateWorkingCopyClone(original, this);
        
        // This is the only difference from my superclass. I am building a new
        // original to put in the shared cache.
        Object newOriginal = builder.buildNewInstance();
            
        // Must put in the detached original to clone to resolve circular refs.
        getNewObjectsOriginalToClone().put(original, clone);
        
        // Must put in clone mapping.
        getCloneMapping().put(clone, clone);

        builder.populateAttributesForClone(original, clone, this);
        // Must reregister in both new objects.
        registerNewObjectClone(clone, newOriginal, descriptor);

        //Build backup clone for DeferredChangeDetectionPolicy or ObjectChangeTrackingPolicy,
        //but not for AttributeChangeTrackingPolicy
        Object backupClone = descriptor.getObjectChangePolicy().buildBackupClone(clone, builder, this);
        getCloneMapping().put(clone, backupClone);// The backup clone must be updated.

        return clone;
    }
    
    /**
     * INTERNAL:
     * Called only by UnitOfWorkIdentityMapAccessor.getAndCloneCacheKeyFromParent method.
     * Return unregisteredDeletedClone corresponding to the passed original, or null
     */
    public Object getUnregisteredDeletedCloneForOriginal(Object original) {
        if (unregisteredDeletedObjectsCloneToBackupAndOriginal != null) {
            Iterator keys = unregisteredDeletedObjectsCloneToBackupAndOriginal.keySet().iterator();
            Iterator values = unregisteredDeletedObjectsCloneToBackupAndOriginal.values().iterator();
            while(keys.hasNext()) {
                Object deletedObjectClone = keys.next();
                Object[] backupAndOriginal = (Object[])values.next();
                Object currentOriginal = backupAndOriginal[1];
                if (original == currentOriginal) {
                    return deletedObjectClone;
                }
            }
        }
        return null;
    }
    
  /**
   * INTERNAL:
   * Wraps the org.eclipse.persistence.exceptions.OptimisticLockException in a  
   * javax.persistence.OptimisticLockException. This conforms to the EJB3 specs
   * @param commitTransaction 
   */
    protected void commitToDatabase(boolean commitTransaction) {
        try {
            super.commitToDatabase(commitTransaction);
        } catch (org.eclipse.persistence.exceptions.OptimisticLockException ole) {
            throw new javax.persistence.OptimisticLockException(ole);
        }
    }

    /**
     * INTERNAL:
     * This is internal to the uow, transactions should not be used explicitly in a uow.
     * The uow shares its parents transactions.
     */
    public void commitTransaction() throws DatabaseException {
        if (this.shouldTerminateTransaction || getParent().getTransactionMutex().isNested()){
            super.commitTransaction();
        }
    }

    public void setShouldTerminateTransaction(boolean shouldTerminateTransaction) {
        this.shouldTerminateTransaction = shouldTerminateTransaction;
    }
    
    /**
     * INTERNAL:
     * Clears flushClearCache attribute and the related collections.
     */
    public void clearFlushClearCache() {
        flushClearCache = null;
        classesToBeInvalidated = null;
    }
}
