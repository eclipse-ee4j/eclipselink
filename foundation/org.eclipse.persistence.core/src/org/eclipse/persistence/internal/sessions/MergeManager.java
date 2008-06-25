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
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions;

import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.linkedlist.LinkedNode;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.internal.sessions.remote.*;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.SessionProfiler;

/**
 * <p><b>Purpose</b>:
 * Used to manage the merge of two objects in a unit of work.
 *
 * @author James Sutherland
 * @since TOPLink/Java 1.1
 */
public class MergeManager {

    /** The unit of work merging for. */
    protected AbstractSession session;

    /** Used only while refreshing objects on remote session */
    protected Map objectDescriptors;

    /** Used to unravel recursion. */
    protected Map objectsAlreadyMerged;
    
    /** Used to keep track of merged new objects. */
    protected IdentityHashMap mergedNewObjects;

    /** Used to store the list of locks that this merge manager has acquired for this merge */
    protected ArrayList acquiredLocks;

    /** If this variable is not null then the mergemanager is waiting on a particular cacheKey */
    protected CacheKey writeLockQueued;

    /** Stores the node that holds this mergemanager within the WriteLocksManager queue */
    protected LinkedNode queueNode;

    /** Policy that determines merge type (i.e. merge is used for several usages). */
    protected int mergePolicy;
    protected static final int WORKING_COPY_INTO_ORIGINAL = 1;
    protected static final int ORIGINAL_INTO_WORKING_COPY = 2;
    protected static final int CLONE_INTO_WORKING_COPY = 3;
    protected static final int WORKING_COPY_INTO_REMOTE = 4;
    protected static final int REFRESH_REMOTE_OBJECT = 5;
    protected static final int CHANGES_INTO_DISTRIBUTED_CACHE = 6;
    protected static final int CLONE_WITH_REFS_INTO_WORKING_COPY = 7;

    /** Policy that determines how the merge will cascade to its object's parts. */
    protected int cascadePolicy;
    public static final int NO_CASCADE = 1;
    public static final int CASCADE_PRIVATE_PARTS = 2;
    public static final int CASCADE_ALL_PARTS = 3;
    public static final int CASCADE_BY_MAPPING = 4;
    
    /** Backdoor to disable merge locks. */
    public static boolean LOCK_ON_MERGE = true;
    
    /** Stored so that all objects merged by a merge manager can have the same readTime. */
    protected long systemTime = 0;
    
    /** Force cascade merge even if a clone is already registered */
    // GF#1139 Cascade doesn't work when merging managed entity
    protected boolean forceCascade;

    public MergeManager(AbstractSession session) {
        this.session = session;
        this.mergedNewObjects = new IdentityHashMap();
        this.objectsAlreadyMerged = new IdentityHashMap();
        this.cascadePolicy = CASCADE_ALL_PARTS;
        this.mergePolicy = WORKING_COPY_INTO_ORIGINAL;
        this.acquiredLocks = new ArrayList();
    }

    /**
     * Build and return an identity set for the specified container.
     */
    protected Map buildIdentitySet(Object container, ContainerPolicy containerPolicy, boolean keyByTarget) {
        // find next power-of-2 size
        Map result = new IdentityHashMap(containerPolicy.sizeFor(container) + 1);
        for (Object iter = containerPolicy.iteratorFor(container); containerPolicy.hasNext(iter);) {
            Object element = containerPolicy.next(iter, getSession());
            if (keyByTarget) {
                result.put(getTargetVersionOfSourceObject(element), element);
            } else {
                result.put(element, element);
            }
        }
        return result;
    }

    /**
     * Cascade all parts, this is the default for the merge.
     */
    public void cascadeAllParts() {
        setCascadePolicy(CASCADE_ALL_PARTS);
    }

    /**
     * Cascade private parts, this can be used to merge clone when using RMI.
     */
    public void cascadePrivateParts() {
        setCascadePolicy(CASCADE_PRIVATE_PARTS);
    }

    /**
     * Merge only direct parts, this can be used to merge clone when using RMI.
     */
    public void dontCascadeParts() {
        setCascadePolicy(NO_CASCADE);
    }

    public ArrayList getAcquiredLocks() {
        return this.acquiredLocks;
    }

    public int getCascadePolicy() {
        return cascadePolicy;
    }

    protected int getMergePolicy() {
        return mergePolicy;
    }

    public Map getObjectDescriptors() {
        if (this.objectDescriptors == null) {
            this.objectDescriptors = new IdentityHashMap();
        }
        return this.objectDescriptors;
    }

    public Map getObjectsAlreadyMerged() {
        return objectsAlreadyMerged;
    }

    public Object getObjectToMerge(Object sourceValue) {
        if (shouldMergeOriginalIntoWorkingCopy()) {
            return getTargetVersionOfSourceObject(sourceValue);
        }

        return sourceValue;
    }

    /**
     * INTENRAL:
     * Used to get the node that this merge manager is stored in, within the WriteLocksManager write lockers queue
     */
    public LinkedNode getQueueNode() {
        return this.queueNode;
    }

    public AbstractSession getSession() {
        return session;
    }

    /**
     * Get the stored value of the current time.  This method lazily initializes
     * so that read times for the same merge manager can all be set to the same read time
     */
    public long getSystemTime() {
        if (systemTime == 0) {
            systemTime = System.currentTimeMillis();
        }
        return systemTime;
    }

    /**
     * Return the coresponding value that should be assigned to the target object for the source object.
     * This value must be local to the targets object space.
     */
    public Object getTargetVersionOfSourceObject(Object source) {
        if (shouldMergeWorkingCopyIntoOriginal() || shouldMergeWorkingCopyIntoRemote()) {
            // Target is in uow parent, or original instance for new object.
            return ((UnitOfWorkImpl)getSession()).getOriginalVersionOfObject(source);
        } else if (shouldMergeCloneIntoWorkingCopy() || shouldMergeOriginalIntoWorkingCopy() || shouldMergeCloneWithReferencesIntoWorkingCopy()) {
            // Target is clone from uow.
            //make sure we use the register for merge
            //bug 3584343
            return registerObjectForMergeCloneIntoWorkingCopy(source);
        } else if (shouldRefreshRemoteObject()) {
            // Target is in session's cache.
            ClassDescriptor descriptor = getSession().getDescriptor(source);
            Vector primaryKey = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(source, getSession());
            return getSession().getIdentityMapAccessorInstance().getFromIdentityMap(primaryKey, source.getClass(), descriptor);
        }

        throw ValidationException.invalidMergePolicy();
    }

    /**
     * INTENRAL:
     * Used to get the object that the merge manager is waiting on, in order to acquire locks
     */
    public CacheKey getWriteLockQueued() {
        return this.writeLockQueued;
    }
    
    /**
     * Recursively merge changes in the object dependent on the merge policy.
     * The hastable is used to resolv recursion.
     */
    public Object mergeChanges(Object object, ObjectChangeSet objectChangeSet) throws ValidationException {
        if (object == null) {
            return object;
        }

        // Do not merged read-only objects in a unit of work.
        if (getSession().isClassReadOnly(object.getClass())) {
            return object;
        }

        // Means that object is either already merged or in the process of being merged.	
        if (getObjectsAlreadyMerged().containsKey(object)) {
            return object;
        }

        // Put the object to be merged in the set.
        getObjectsAlreadyMerged().put(object, object);

        Object mergedObject;
        if (shouldMergeWorkingCopyIntoOriginal()) {
            mergedObject = mergeChangesOfWorkingCopyIntoOriginal(object, objectChangeSet);
        } else if (shouldMergeChangesIntoDistributedCache()) {
            mergedObject = mergeChangesIntoDistributedCache(object, objectChangeSet);
        } else if (shouldMergeCloneIntoWorkingCopy() || shouldMergeCloneWithReferencesIntoWorkingCopy()) {
            mergedObject = mergeChangesOfCloneIntoWorkingCopy(object);
        } else if (shouldMergeOriginalIntoWorkingCopy()) {
            mergedObject = mergeChangesOfOriginalIntoWorkingCopy(object);
        } else if (shouldMergeWorkingCopyIntoRemote()) {
            mergedObject = mergeChangesOfWorkingCopyIntoRemote(object);
        } else if (shouldRefreshRemoteObject()) {
            mergedObject = mergeChangesForRefreshingRemoteObject(object);
        } else {
            throw ValidationException.invalidMergePolicy();
        }

        return mergedObject;
    }

    /**
     * Recursively merge the RMI clone from the server
     * into the client unit of work working copy.
     * This will only be called if the working copy exists.
     */
    protected Object mergeChangesForRefreshingRemoteObject(Object serverSideDomainObject) {
        ClassDescriptor descriptor = getSession().getDescriptor(serverSideDomainObject);
        Vector primaryKey = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(serverSideDomainObject, getSession());
        Object clientSideDomainObject = getSession().getIdentityMapAccessorInstance().getFromIdentityMap(primaryKey, serverSideDomainObject.getClass(), descriptor);
        if (clientSideDomainObject == null) {
            //the referenced object came back as null from the cache.
            ObjectDescriptor objectDescriptor = (ObjectDescriptor)getObjectDescriptors().get(serverSideDomainObject);
            if (objectDescriptor == null){
                //the object must have been added concurently before serialize generate a new ObjectDescriptor on this side
                objectDescriptor = new ObjectDescriptor();
                objectDescriptor.setKey(primaryKey);
                objectDescriptor.setObject(serverSideDomainObject);
                OptimisticLockingPolicy policy = descriptor.getOptimisticLockingPolicy();
                if (policy == null){
                    objectDescriptor.setWriteLockValue(null);
                }else{
                    objectDescriptor.setWriteLockValue(policy.getBaseValue());
                }
            }
            //query is used for the cascade policy only
            org.eclipse.persistence.queries.ObjectLevelReadQuery query = new org.eclipse.persistence.queries.ReadObjectQuery();
            query.setCascadePolicy(this.getCascadePolicy());
            getSession().getIdentityMapAccessorInstance().putInIdentityMap(serverSideDomainObject, primaryKey, objectDescriptor.getWriteLockValue(), objectDescriptor.getReadTime(), descriptor);
            descriptor.getObjectBuilder().fixObjectReferences(serverSideDomainObject, getObjectDescriptors(), getObjectsAlreadyMerged(), query, (RemoteSession)getSession());
            clientSideDomainObject = serverSideDomainObject;
        } else {
            // merge into the clientSideDomainObject from the serverSideDomainObject;
            // use clientSideDomainObject as the backup, as anything different should be merged
            descriptor.getObjectBuilder().mergeIntoObject(clientSideDomainObject, false, serverSideDomainObject, this);
            ObjectDescriptor objectDescriptor = (ObjectDescriptor)getObjectDescriptors().get(serverSideDomainObject);
            if (objectDescriptor == null){
                //the object must have been added concurently before serialize generate a new ObjectDescriptor on this side
                objectDescriptor = new ObjectDescriptor();
                objectDescriptor.setKey(primaryKey);
                objectDescriptor.setObject(serverSideDomainObject);
                OptimisticLockingPolicy policy = descriptor.getOptimisticLockingPolicy();
                if (policy == null){
                    objectDescriptor.setWriteLockValue(null);
                }else{
                    objectDescriptor.setWriteLockValue(policy.getBaseValue());
                }
            }
            CacheKey key = getSession().getIdentityMapAccessorInstance().getCacheKeyForObject(primaryKey, clientSideDomainObject.getClass(), descriptor);

            // Check for null because when there is NoIdentityMap, CacheKey will be null
            if (key != null) {
                key.setReadTime(objectDescriptor.getReadTime());
            }
            if (descriptor.usesOptimisticLocking()) {
                getSession().getIdentityMapAccessor().updateWriteLockValue(primaryKey, clientSideDomainObject.getClass(), objectDescriptor.getWriteLockValue());
            }
        }
        return clientSideDomainObject;
    }

    /**
     * INTERNAL:
     * Merge the changes to all objects to session's cache.
     */
    public void mergeChangesFromChangeSet(UnitOfWorkChangeSet uowChangeSet) {
        getSession().startOperationProfile(SessionProfiler.DistributedMerge);
        // Ensure concurrency if cache isolation requires.
        getSession().getIdentityMapAccessorInstance().acquireWriteLock();
        getSession().log(SessionLog.FINER, SessionLog.PROPAGATION, "received_updates_from_remote_server");
        getSession().getEventManager().preDistributedMergeUnitOfWorkChangeSet(uowChangeSet);

        try {
            // Iterate over each clone and let the object build merge to clones into the originals.
            getSession().getIdentityMapAccessorInstance().getWriteLockManager().acquireRequiredLocks(this, uowChangeSet);
            Iterator objectChangeEnum = uowChangeSet.getAllChangeSets().keySet().iterator();
            while (objectChangeEnum.hasNext()) {
                ObjectChangeSet objectChangeSet = (ObjectChangeSet)objectChangeEnum.next();
                Object object = objectChangeSet.getTargetVersionOfSourceObject(getSession(), false);

                // Don't read the object here.  If it is null then we won't merge it at this stage, unless it
                // is being referenced which will force the load later
                Object mergedObject = this.mergeChanges(object, objectChangeSet);

                // if mergedObject is null, it might be because objectChangeSet represents a new object and could not look it up
                // Check the descriptor setting for this change set to see if the new object should be added to the cache.
                if (mergedObject == null) {
                    if (objectChangeSet.isNew()) {
                        mergedObject = mergeNewObjectIntoCache(objectChangeSet);
                    }
                }
                if (mergedObject == null) {
                    getSession().incrementProfile(SessionProfiler.ChangeSetsNotProcessed);
                } else {
                    getSession().incrementProfile(SessionProfiler.ChangeSetsProcessed);
                }
            }
            Iterator deletedObjects = uowChangeSet.getDeletedObjects().values().iterator();
            while (deletedObjects.hasNext()) {
                ObjectChangeSet changeSet = (ObjectChangeSet)deletedObjects.next();
                changeSet.removeFromIdentityMap(getSession());
                getSession().incrementProfile(SessionProfiler.DeletedObject);
            }
        } catch (RuntimeException exception) {
            getSession().handleException(exception);
        } finally {
            getSession().getIdentityMapAccessorInstance().getWriteLockManager().releaseAllAcquiredLocks(this);
            getSession().getIdentityMapAccessorInstance().releaseWriteLock();
            getSession().getEventManager().postDistributedMergeUnitOfWorkChangeSet(uowChangeSet);
            getSession().endOperationProfile(SessionProfiler.DistributedMerge);
        }
    }

    /**
     * Merge the changes from the collection of a source object into the target using the backup as the diff.
     * Return true if any changes occurred.
     */
    public boolean mergeChangesInCollection(Object source, Object target, Object backup, DatabaseMapping mapping) {
        ContainerPolicy containerPolicy = mapping.getContainerPolicy();

        // The vectors must be converted to identity sets to avoid dependency on #equals().
        Map backupSet = buildIdentitySet(backup, containerPolicy, false);
        Map sourceSet = null;
        Map targetToSources = null;

        // We need either a source or target-to-source set, depending on the merge type.
        if (shouldMergeWorkingCopyIntoOriginal()) {
            sourceSet = buildIdentitySet(source, containerPolicy, false);
        } else {
            targetToSources = buildIdentitySet(source, containerPolicy, true);
        }

        boolean changeOccured = false;

        // If the backup is also the target, clone it; since it may change during the loop.
        if (backup == target) {
            backup = containerPolicy.cloneFor(backup);
        }

        // Handle removed elements.
        for (Object backupIter = containerPolicy.iteratorFor(backup);
                 containerPolicy.hasNext(backupIter);) {
            Object backupElement = containerPolicy.next(backupIter, getSession());

            // The check for delete depends on the type of merge.
            if (shouldMergeWorkingCopyIntoOriginal()) {// Source and backup are the same space.
                if (!sourceSet.containsKey(backupElement)) {
                    changeOccured = true;
                    containerPolicy.removeFrom((Object)null, getTargetVersionOfSourceObject(backupElement), target, getSession());

                    // Registered new object in nested units of work must not be registered into the parent,
                    // so this records them in the merge to parent case.
                    if (mapping.isPrivateOwned()) {
                        registerRemovedNewObjectIfRequired(backupElement);
                    }
                }
            } else {// Target and backup are same for all types of merge.
                if (!targetToSources.containsKey(backupElement)) {// If no source for target then was removed.
                    changeOccured = true;
                    containerPolicy.removeFrom((Object)null, backupElement, target, getSession());// Backup value is same as target value.
                }
            }
        }

        // Handle added elements.
        for (Object sourceIter = containerPolicy.iteratorFor(source);
                 containerPolicy.hasNext(sourceIter);) {
            Object sourceElement = containerPolicy.next(sourceIter, getSession());

            // The target object must be completely merged before adding to the collection
            // otherwise another thread could pick up the partial object.
            mapping.cascadeMerge(sourceElement, this);
            // The check for add depends on the type of merge.
            if (shouldMergeWorkingCopyIntoOriginal()) {// Source and backup are the same space.
                if (!backupSet.containsKey(sourceElement)) {
                    changeOccured = true;
                    containerPolicy.addInto(getTargetVersionOfSourceObject(sourceElement), target, getSession());
                } else {
                    containerPolicy.validateElementAndRehashIfRequired(sourceElement, target, getSession(), getTargetVersionOfSourceObject(sourceElement));
                }
            } else {// Target and backup are same for all types of merge.
                Object targetVersionOfSourceElement = getTargetVersionOfSourceObject(sourceElement);
                if (!backupSet.containsKey(targetVersionOfSourceElement)) {// Backup value is same as target value.
                    changeOccured = true;
                    containerPolicy.addInto(targetVersionOfSourceElement, target, getSession());
                }
            }
        }

        return changeOccured;
    }

    /**
     * Merge the changes specified within the changeSet into the cache.
     * The object passed in is the original object from the cache.
     */
    protected Object mergeChangesIntoDistributedCache(Object original, ObjectChangeSet changeSet) {
        AbstractSession session = getSession();

        // Determine if the object needs to be registered in the parent's clone mapping,
        // This is required for registered new objects in a nested unit of work.
        Class localClassType = changeSet.getClassType(session);
        ClassDescriptor descriptor = session.getDescriptor(localClassType);
        
        // Perform invalidation of a cached object (when set on the ChangeSet) to avoid refreshing or merging
        if (changeSet.getSynchronizationType() == ClassDescriptor.INVALIDATE_CHANGED_OBJECTS) {
            getSession().getIdentityMapAccessorInstance().invalidateObject(changeSet.getPrimaryKeys(), localClassType);
            return original;
		}
        
        if ((original != null) && descriptor.usesOptimisticLocking()) {
            if ((session.getCommandManager() != null) && (session.getCommandManager().getCommandConverter() != null)) {
                // Rebuild the version value from user format i.e the change set was converted to XML
                changeSet.rebuildWriteLockValueFromUserFormat(descriptor, session);
            }
            int difference = descriptor.getOptimisticLockingPolicy().getVersionDifference(changeSet.getWriteLockValue(), original, changeSet.getPrimaryKeys(), session);

            //cr 4143 if the difference is the same merge anyway.  This prevents the version not channg error
            if (difference < 0) {
                // The current version is newer than the one on the remote system
                getSession().log(SessionLog.FINEST, SessionLog.PROPAGATION, "change_from_remote_server_older_than_current_version", changeSet.getClassName(), changeSet.getPrimaryKeys());
                return original;
            } else if (difference > 1) {
                // If the current version is much older than the remote system, then refresh the object
                getSession().log(SessionLog.FINEST, SessionLog.PROPAGATION, "current_version_much_older_than_change_from_remote_server", changeSet.getClassName(), changeSet.getPrimaryKeys());
                session.refreshObject(original);
                return original;
            }
        }

        // Always merge into the original.
        getSession().log(SessionLog.FINEST, SessionLog.PROPAGATION, "Merging_from_remote_server", changeSet.getClassName(), changeSet.getPrimaryKeys());

        if (!(changeSet.getSynchronizationType() == ClassDescriptor.DO_NOT_SEND_CHANGES)) {
            descriptor.getObjectBuilder().mergeChangesIntoObject(original, changeSet, null, this);
            Vector primaryKey = changeSet.getPrimaryKeys();

            // Must ensure the get and put of the cache occur as a single operation.
            // Cache key hold a reference to a concurrency manager which is used for the lock/release operation
            CacheKey cacheKey = session.getIdentityMapAccessorInstance().acquireLock(primaryKey, original.getClass(), descriptor);
            try {
                if (descriptor.usesOptimisticLocking()) {
                    if (descriptor.getOptimisticLockingPolicy().isChildWriteLockValueGreater(session, primaryKey, original.getClass(), changeSet)) {
                        cacheKey.setWriteLockValue(changeSet.getWriteLockValue());
                    }
                }
                cacheKey.setObject(original);
                if (descriptor.getCacheInvalidationPolicy().shouldUpdateReadTimeOnUpdate()) {
                    cacheKey.setReadTime(getSystemTime());
                }
            } finally {
                cacheKey.updateAccess();
                cacheKey.release();
            }
        }

        return original;
    }

    /**
     * Recursively merge to rmi clone into the unit of work working copy.
     * The hastable is used to resolv recursion.
     */
    protected Object mergeChangesOfCloneIntoWorkingCopy(Object rmiClone) {
        Object registeredObject = registerObjectForMergeCloneIntoWorkingCopy(rmiClone);

        if (registeredObject == rmiClone && !shouldForceCascade()) {
            //need to find better better fix.  prevents merging into itself.
            return rmiClone;
        }

        ClassDescriptor descriptor = getSession().getDescriptor(rmiClone);
        try {
            ObjectBuilder builder = descriptor.getObjectBuilder();
            
            if (registeredObject != rmiClone && descriptor.usesVersionLocking() && ! mergedNewObjects.containsKey(registeredObject)) {
                VersionLockingPolicy policy = (VersionLockingPolicy) descriptor.getOptimisticLockingPolicy();
                if (policy.isStoredInObject()) {
                    Object currentValue = builder.extractValueFromObjectForField(registeredObject, policy.getWriteLockField(), session); 
                
                    if (policy.isNewerVersion(currentValue, rmiClone, session.keyFromObject(rmiClone), session)) {
                        throw OptimisticLockException.objectChangedSinceLastMerge(rmiClone);
                    }
                }
            }
            
            // Toggle change tracking during the merge.
            descriptor.getObjectChangePolicy().dissableEventProcessing(registeredObject);
            
            boolean cascadeOnly = false;
            if (registeredObject == rmiClone || mergedNewObjects.containsKey(registeredObject)) {    
                // GF#1139 Cascade merge operations to relationship mappings even if already registered
                cascadeOnly = true;
            }
            
            // Merge into the clone from the original and use the clone as 
            // backup as anything different should be merged.
            builder.mergeIntoObject(registeredObject, false, rmiClone, this, cascadeOnly);  
        } finally {
            descriptor.getObjectChangePolicy().enableEventProcessing(registeredObject);
        }

        return registeredObject;
    }

    /**
     * Recursively merge to original from its parent into the clone.
     * The hastable is used to resolv recursion.
     */
    protected Object mergeChangesOfOriginalIntoWorkingCopy(Object clone) {
        ClassDescriptor descriptor = getSession().getDescriptor(clone);

        // Find the original object, if it is not there then do nothing.
        Object original = ((UnitOfWorkImpl)getSession()).getOriginalVersionOfObjectOrNull(clone, descriptor);

        if (original == null) {
            return clone;
        }
        
        // Toggle change tracking during the merge.
        descriptor.getObjectChangePolicy().dissableEventProcessing(clone);
        try {
            // Merge into the clone from the original, use clone as backup as anything different should be merged.
            descriptor.getObjectBuilder().mergeIntoObject(clone, false, original, this);
        } finally {
            descriptor.getObjectChangePolicy().enableEventProcessing(clone);
        }
        //update the change policies with the refresh
        descriptor.getObjectChangePolicy().revertChanges(clone, descriptor, (UnitOfWorkImpl)this.getSession(), ((UnitOfWorkImpl)this.getSession()).getCloneMapping());
        Vector primaryKey = getSession().keyFromObject(clone);
        if (descriptor.usesOptimisticLocking()) {
            descriptor.getOptimisticLockingPolicy().mergeIntoParentCache((UnitOfWorkImpl)getSession(), primaryKey, clone);
        }

        CacheKey parentCacheKey = ((UnitOfWorkImpl)getSession()).getParent().getIdentityMapAccessorInstance().getCacheKeyForObject(primaryKey, clone.getClass(), descriptor);
        CacheKey uowCacheKey = getSession().getIdentityMapAccessorInstance().getCacheKeyForObject(primaryKey, clone.getClass(), descriptor);

        // Check for null because when there is NoIdentityMap, CacheKey will be null
        if ((parentCacheKey != null) && (uowCacheKey != null)) {
            uowCacheKey.setReadTime(parentCacheKey.getReadTime());
        }

        return clone;
    }

    /**
     * Recursively merge to clone into the orignal in its parent.
     * The hastable is used to resolv recursion.
     */
    protected Object mergeChangesOfWorkingCopyIntoOriginal(Object clone, ObjectChangeSet objectChangeSet) {
        UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl)getSession();
        AbstractSession parent = unitOfWork.getParent();
        
        // If the clone is deleted, avoid this merge and simply return the clone.
        if (unitOfWork.isObjectDeleted(clone)) {
            return clone;
        }
        
        // Determine if the object needs to be registered in the parent's clone mapping,
        // This is required for registered new objects in a nested unit of work.
        boolean requiresToRegisterInParent = false;
        if (unitOfWork.isNestedUnitOfWork()) {
            Object originalNewObject = unitOfWork.getOriginalVersionOfNewObject(clone);
            if ((originalNewObject != null) // Check that the object is new.
                     && (!((UnitOfWorkImpl)parent).isCloneNewObject(originalNewObject)) && (!unitOfWork.isUnregisteredNewObjectInParent(originalNewObject))) {
                requiresToRegisterInParent = true;
            }
        }

        ClassDescriptor descriptor = unitOfWork.getDescriptor(clone.getClass());
        ObjectBuilder objectBuilder = descriptor.getObjectBuilder();
        // This always finds an original different from the clone, even if it has to create one.
        // This must be done after special cases have been computed because it registers unregistered new objects.
        Object original = unitOfWork.getOriginalVersionOfObjectOrNull(clone, objectChangeSet, descriptor);

        // Always merge into the original.
        try {
            if (original == null) {
                // If original does not exist then we must merge the entire object.
                original = unitOfWork.buildOriginal(clone);
                if (objectChangeSet == null) {
                    objectBuilder.mergeIntoObject(original, true, clone, this);
                } else if (!objectChangeSet.isNew()) {
                    // Once the original is created we must put it in the cache and
                    // lock it to prevent a reading thread from creating it as well
                    // there will be no deadlock situation because no other threads
                    // will be able to reference this object.
                    original = parent.getIdentityMapAccessorInstance().getWriteLockManager().appendLock(objectChangeSet.getPrimaryKeys(), original, descriptor, this, parent);
                    objectBuilder.mergeIntoObject(original, true, clone, this);
                } else {
                    objectBuilder.mergeChangesIntoObject(original, objectChangeSet, clone, this);
                    // PERF: If PersistenceEntity is caching the primary key this must be cleared as the primary key may have changed in new objects.
                    objectBuilder.clearPrimaryKey(original);
                }
            } else if (objectChangeSet == null) {
                // If we have no change set then we must merge the entire object.
                objectBuilder.mergeIntoObject(original, false, clone, this);
            } else {
                // Not null and we have a valid changeSet then merge the changes.
                if (!objectChangeSet.isNew()) {
                    if (objectChangeSet.shouldInvalidateObject(original, parent)) {
                        parent.getIdentityMapAccessor().invalidateObject(original);
                        return clone;
                    }
                } else {                    
                    // PERF: If PersistenceEntity is caching the primary key this must be cleared as the primary key may have changed in new objects.
                    objectBuilder.clearPrimaryKey(original);
                }
                objectBuilder.mergeChangesIntoObject(original, objectChangeSet, clone, this);
            }
        } catch (QueryException exception) {
            // Ignore validation errors if unit of work validation is suppressed.
            // Also there is a very specific case under EJB wrappering where
            // a related object may have never been accessed in the unit of work context
            // but is still valid, so this error must be ignored.
            if (unitOfWork.shouldPerformNoValidation() || (descriptor.hasWrapperPolicy())) {
                if ((exception.getErrorCode() != QueryException.BACKUP_CLONE_DELETED) && (exception.getErrorCode() != QueryException.BACKUP_CLONE_IS_ORIGINAL_FROM_PARENT) && (exception.getErrorCode() != QueryException.BACKUP_CLONE_IS_ORIGINAL_FROM_SELF)) {
                    throw exception;
                }
                return clone;
            } else {
                throw exception;
            }
        }

        if (requiresToRegisterInParent) {
            // Can use a new instance as backup and original.
            Object backupClone = objectBuilder.buildNewInstance();
            Object newInstance = objectBuilder.buildNewInstance();
            ((UnitOfWorkImpl)parent).registerOriginalNewObjectFromNestedUnitOfWork(original, backupClone, newInstance, descriptor);
        }

        if (!unitOfWork.isNestedUnitOfWork()) {
            // PERF: Get the cached cache-key from the change-set.
            CacheKey cacheKey = null;
            if (objectChangeSet != null) {
                cacheKey = objectChangeSet.getActiveCacheKey();
            }
            boolean locked = false;
            // The cache key should never be null for the new commit, but may be for old commit, or depending on the cache isolation level may not be locked,
            // so needs to be re-acquired.
            if (cacheKey == null || (!cacheKey.isAcquired())) {
                // This is only true for the old merge.
                Vector primaryKey = objectBuilder.extractPrimaryKeyFromObject(original, unitOfWork);
                cacheKey = parent.getIdentityMapAccessorInstance().acquireLock(primaryKey, original.getClass(), descriptor);
                locked = true;
            }
            try {
                if (descriptor.usesOptimisticLocking() && descriptor.getOptimisticLockingPolicy().isStoredInCache()) {
                    cacheKey.setWriteLockValue(unitOfWork.getIdentityMapAccessor().getWriteLockValue(clone));
                }
                cacheKey.setObject(original);
                if (descriptor.getCacheInvalidationPolicy().shouldUpdateReadTimeOnUpdate() || ((objectChangeSet != null) && objectChangeSet.isNew())) {
                    cacheKey.setReadTime(getSystemTime());
                }
                cacheKey.updateAccess();
            } finally {
                if (locked) {
                    cacheKey.release();
                }
            }
        }
        return clone;
    }

    /**
     * Recursively merge changes in the object dependent on the merge policy.
     * This merges changes from a remote unit of work back from the server into
     * the original remote unit of work.  This is meant to merge server-side changes
     * such as sequence numbers, version numbers or events triggered changes.
     */
    public Object mergeChangesOfWorkingCopyIntoRemote(Object clone) throws ValidationException {
        UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl)getSession();

        // This will return the object from the parent unit of work (original unit of work).
        Object original = unitOfWork.getOriginalVersionOfObject(clone);

        // The original is used as the backup to merge everything different from it.
        // This makes this type of merge quite different than the normal unit of work merge.
        ClassDescriptor descriptor = unitOfWork.getDescriptor(clone);
        descriptor.getObjectBuilder().mergeIntoObject(original, false, clone, this);

        if (((RemoteUnitOfWork)unitOfWork.getParent()).getUnregisteredNewObjectsCache().contains(original)) {
            // Can use a new instance as backup and original.
            Object backupClone = descriptor.getObjectBuilder().buildNewInstance();
            Object newInstance = descriptor.getObjectBuilder().buildNewInstance();
            ((UnitOfWorkImpl)unitOfWork.getParent()).registerOriginalNewObjectFromNestedUnitOfWork(original, backupClone, newInstance, descriptor);
        }

        Vector primaryKey = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(clone, unitOfWork);

        // Must ensure the get and put of the cache occur as a single operation.
        // Cache key hold a reference to a concurrency manager which is used for the lock/release operation
        CacheKey cacheKey = unitOfWork.getParent().getIdentityMapAccessorInstance().acquireLock(primaryKey, original.getClass(), descriptor);
        try {
            if (descriptor.usesOptimisticLocking()) {
                cacheKey.setObject(original);
                cacheKey.setWriteLockValue(unitOfWork.getIdentityMapAccessor().getWriteLockValue(original));
            } else {
                // Always put in the parent im for root because it must now be persistent.
                cacheKey.setObject(original);
            }
        } finally {
            cacheKey.updateAccess();
            cacheKey.release();
        }

        return clone;
    }

    /**
     * This can be used by the user for merging clones from RMI into the unit of work.
     */
    public void mergeCloneIntoWorkingCopy() {
        setMergePolicy(CLONE_INTO_WORKING_COPY);
    }

    /**
     * This is used during the merge of dependent objects referencing independent objects, where you want
     * the independent objects merged as well.
     */
    public void mergeCloneWithReferencesIntoWorkingCopy() {
        setMergePolicy(CLONE_WITH_REFS_INTO_WORKING_COPY);
    }

    /**
     * This is used during cache synchronisation to merge the changes into the distributed cache.
     */
    public void mergeIntoDistributedCache() {
        setMergePolicy(CHANGES_INTO_DISTRIBUTED_CACHE);
    }

    /**
     * Merge a change set for a new object into the cache.  This method will create a
     * shell for the new object and then merge the changes from the change set into the object.
     * The newly merged object will then be added to the cache.
     */
    public Object mergeNewObjectIntoCache(ObjectChangeSet changeSet) {
        if (changeSet.isNew()) {
            Class objectClass = changeSet.getClassType(session);
            ClassDescriptor descriptor = getSession().getDescriptor(objectClass);
            // Try to find the object first we may have merged it all ready.
            Object object = changeSet.getTargetVersionOfSourceObject(getSession(), false);
            if (object == null) {
                if (!getObjectsAlreadyMerged().containsKey(changeSet)) {
                    // if we haven't merged this object allready then build a new object
                    // otherwise leave it as null which will stop the recursion
                    object = descriptor.getObjectBuilder().buildNewInstance();
                    //Store the changeset to prevent us from creating this new object again
                    getObjectsAlreadyMerged().put(changeSet, object);
                } else {
                    //we have all ready created the object, must be in a cyclic
                    //merge on a new object so get it out of the allreadymerged collection
                    object = getObjectsAlreadyMerged().get(changeSet);
                }
            } else {
                object = changeSet.getTargetVersionOfSourceObject(getSession(), true);
            }
            mergeChanges(object, changeSet);
            Object implementation = descriptor.getObjectBuilder().unwrapObject(object, getSession());

            return getSession().getIdentityMapAccessorInstance().putInIdentityMap(implementation, descriptor.getObjectBuilder().extractPrimaryKeyFromObject(implementation, getSession()), changeSet.getWriteLockValue(), getSystemTime(), descriptor);
        }
        return null;
    }

    /**
     * This is used to revert changes to objects, or during refreshes.
     */
    public void mergeOriginalIntoWorkingCopy() {
        setMergePolicy(ORIGINAL_INTO_WORKING_COPY);
    }

    /**
     * This is used during the unit of work commit to merge changes into the parent.
     */
    public void mergeWorkingCopyIntoOriginal() {
        setMergePolicy(WORKING_COPY_INTO_ORIGINAL);
    }

    /**
     * This is used during the unit of work commit to merge changes into the parent.
     */
    public void mergeWorkingCopyIntoRemote() {
        setMergePolicy(WORKING_COPY_INTO_REMOTE);
    }

    /**
     * INTERNAL:
     * This is used to refresh remote session object
     */
    public void refreshRemoteObject() {
        setMergePolicy(REFRESH_REMOTE_OBJECT);
    }

    /**
     * INTERNAL:
     * When merging from a clone when the cache cannot be guaranteed the object must be first read if it is existing
     * and not in the cache. Otherwise no changes will be detected as the original state is missing.
     */
    protected Object registerObjectForMergeCloneIntoWorkingCopy(Object clone) {
        UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl)getSession();
        ClassDescriptor descriptor = unitOfWork.getDescriptor(clone.getClass());
        Vector primaryKey = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(clone, unitOfWork);

        // Must use the java class as this may be a bean that we are merging and it may not have the same class as the
        // objects in the cache.  As of EJB 2.0.
        Object objectFromCache = unitOfWork.getIdentityMapAccessorInstance().getFromIdentityMap(primaryKey, descriptor.getJavaClass(), false, descriptor);
        if (objectFromCache == null) {
            // Ensure we return the working copy if this has already been registered.
            objectFromCache = unitOfWork.checkIfAlreadyRegistered(clone, descriptor);
        }
        if (objectFromCache != null) {
            // gf830 - merging a removed entity should throw exception.
            if (unitOfWork.isObjectDeleted(objectFromCache)) {
                if (shouldMergeCloneIntoWorkingCopy() || shouldMergeCloneWithReferencesIntoWorkingCopy()) {
                    throw new IllegalArgumentException(ExceptionLocalization.buildMessage("cannot_merge_removed_entity", new Object[] { clone }));
                }
            }
            return objectFromCache;
        }

        org.eclipse.persistence.queries.DoesExistQuery existQuery = descriptor.getQueryManager().getDoesExistQuery();
        // Optimize cache option to avoid executing the does exist query.
        if (existQuery.shouldCheckCacheForDoesExist()) {
            Object registeredObject = unitOfWork.internalRegisterObject(clone, descriptor);
            
            if (unitOfWork.getNewObjectsOriginalToClone().containsKey(clone)) {
                mergedNewObjects.put(registeredObject, registeredObject);
            }
            
            return registeredObject;
        }

        // Check early return to check if it is a new object, i.e. null primary key.
        Boolean doesExist = (Boolean)existQuery.checkEarlyReturn(clone, primaryKey, unitOfWork, null);
        if (doesExist == Boolean.FALSE) {
            Object registeredObject = unitOfWork.internalRegisterObject(clone, descriptor);
            mergedNewObjects.put(registeredObject, registeredObject);
            return registeredObject;
        }
    
        // Otherwise it is existing and not in the cache so it must be read.
        Object object = unitOfWork.readObject(clone);
        if (object == null) {
            //bug6180972: avoid internal register's existence check and be sure to put the new object in the mergedNewObjects collection
            object =  unitOfWork.cloneAndRegisterNewObject(clone);
            mergedNewObjects.put(object, object);
        }
        return object;
    }

    /**
     * Determine if the object is a registered new object, and that this is a nested unit of work
     * merge into the parent.  In this case private mappings will register the object as being removed.
     */
    public void registerRemovedNewObjectIfRequired(Object removedObject) {
        if (getSession().isUnitOfWork()) {
            UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl)getSession();

            if (shouldMergeWorkingCopyIntoOriginal() && unitOfWork.getParent().isUnitOfWork() && unitOfWork.isCloneNewObject(removedObject)) {
                Object originalVersionOfRemovedObject = unitOfWork.getOriginalVersionOfObject(removedObject);
                unitOfWork.addRemovedObject(originalVersionOfRemovedObject);
            }
        }
    }

    public void setCascadePolicy(int cascadePolicy) {
        this.cascadePolicy = cascadePolicy;
    }

    protected void setMergePolicy(int mergePolicy) {
        this.mergePolicy = mergePolicy;
    }

    public void setForceCascade(boolean forceCascade) {
        this.forceCascade = forceCascade;
    }

    public void setObjectDescriptors(Map objectDescriptors) {
        this.objectDescriptors = objectDescriptors;
    }

    protected void setObjectsAlreadyMerged(Map objectsAlreadyMerged) {
        this.objectsAlreadyMerged = objectsAlreadyMerged;
    }

    /**
     * INTENRAL:
     * Used to set the node that this merge manager is stored in, within the WriteLocksManager write lockers queue
     */
    public void setQueueNode(LinkedNode node) {
        this.queueNode = node;
    }

    protected void setSession(AbstractSession session) {
        this.session = session;
    }

    /**
     * INTENRAL:
     * Used to set the object that the merge manager is waiting on, in order to acquire locks
     * If this value is null then the merge manager is not waiting on any locks.
     */
    public void setWriteLockQueued(CacheKey writeLockQueued) {
        this.writeLockQueued = writeLockQueued;
    }

    /**
     * Flag used to determine that the mappings should be checked for
     * cascade requirements.
     */
    public boolean shouldCascadeByMapping() {
        return getCascadePolicy() == CASCADE_BY_MAPPING;
    }

    /**
     * Flag used to determine if all parts should be cascaded
     */
    public boolean shouldCascadeAllParts() {
        return getCascadePolicy() == CASCADE_ALL_PARTS;
    }

    /**
     * Flag used to determine if any parts should be cascaded
     */
    public boolean shouldCascadeParts() {
        return getCascadePolicy() != NO_CASCADE;
    }

    /**
     * Flag used to determine if any private parts should be cascaded
     */
    public boolean shouldCascadePrivateParts() {
        return (getCascadePolicy() == CASCADE_PRIVATE_PARTS) || (getCascadePolicy() == CASCADE_ALL_PARTS);
    }

    /**
     * Refreshes are based on the objects row, so all attributes of the object must be refreshed.
     * However merging from RMI, normally reference are made transient, so should not be merge unless
     * specified.
     */
    public boolean shouldCascadeReferences() {
        return !shouldMergeCloneIntoWorkingCopy();
    }

    /**
     * INTERNAL:
     * This happens when changes from an UnitOfWork is propagated to a distributed class.
     */
    public boolean shouldMergeChangesIntoDistributedCache() {
        return getMergePolicy() == CHANGES_INTO_DISTRIBUTED_CACHE;
    }

    /**
     * This can be used by the user for merging clones from RMI into the unit of work.
     */
    public boolean shouldMergeCloneIntoWorkingCopy() {
        return getMergePolicy() == CLONE_INTO_WORKING_COPY;
    }

    /**
     * This can be used by the user for merging remote EJB objects into the unit of work.
     */
    public boolean shouldMergeCloneWithReferencesIntoWorkingCopy() {
        return getMergePolicy() == CLONE_WITH_REFS_INTO_WORKING_COPY;
    }

    /**
     * This is used to revert changes to objects, or during refreshes.
     */
    public boolean shouldMergeOriginalIntoWorkingCopy() {
        return getMergePolicy() == ORIGINAL_INTO_WORKING_COPY;
    }

    /**
     * This is used during the unit of work commit to merge changes into the parent.
     */
    public boolean shouldMergeWorkingCopyIntoOriginal() {
        return getMergePolicy() == WORKING_COPY_INTO_ORIGINAL;
    }

    /**
     * INTERNAL:
     * This happens when serialized remote unit of work has to be merged with local remote unit of work.
     */
    public boolean shouldMergeWorkingCopyIntoRemote() {
        return getMergePolicy() == WORKING_COPY_INTO_REMOTE;
    }

    /**
     * INTERNAL:
     * This is used to refresh objects on the remote session
     */
    public boolean shouldRefreshRemoteObject() {
        return getMergePolicy() == REFRESH_REMOTE_OBJECT;
    }

    /**
     * This is used to cascade merge even if a clone is already registered. 
     */
    public boolean shouldForceCascade() {
        return forceCascade;
    }
    
    /**
     * INTERNAL:
     * Used to return a hashtable containing new objects found through the 
     * registerObjectForMergeCloneIntoWorkingCopy method.
     * @return Map
     */
    public IdentityHashMap getMergedNewObjects(){
        return mergedNewObjects;
    }
}