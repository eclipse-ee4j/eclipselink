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
 *     05/28/2008-1.0M8 Andrei Ilitchev 
 *        - 224964: Provide support for Proxy Authentication through JPA.
 *        The class was amended to allow it to instantiate ValueHolders after release method has been called
 *        (internalExecuteQuery method no longer throws exception if the uow is dead).
 *        Note that release method clears change sets but keeps the cache.
 *     02/11/2009-1.1 Michael O'Brien 
 *        - 259993: 1) Defer a full clear(true) call from entityManager.clear() to release() 
 *          only if uow lifecycle is 1,2 or 4 (*Pending) and perform a clear of the cache only in this case.
 *          2) During mergeClonesAfterCompletion() If the the acquire and release threads are different 
 *          switch back to the stored acquire thread stored on the mergeManager.
 *     17/04/2009-1.1 Michael O'Brien
 *         - 272022: For rollback scenarios - If the current thread and the active thread
 *            on the mutex do not match for read locks (not yet transitioned to deferred locks) - switch them
 *     07/16/2009-2.0 Guy Pelletier 
 *       - 277039: JPA 2.0 Cache Usage Settings
 *     07/15/2011-2.2.1 Guy Pelletier 
 *       - 349424: persists during an preCalculateUnitOfWorkChangeSet event are lost
 *     14/05/2012-2.4 Guy Pelletier   
 *       - 376603: Provide for table per tenant support for multitenant applications
 *     08/11/2012-2.5 Guy Pelletier  
 *       - 393867: Named queries do not work when using EM level Table Per Tenant Multitenancy.
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions;

import java.util.*;
import java.io.*;

import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.annotations.CacheKeyType;
import org.eclipse.persistence.config.ReferenceMode;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.descriptors.DescriptorIterator.CascadeCondition;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.indirection.DatabaseValueHolder;
import org.eclipse.persistence.internal.indirection.UnitOfWorkQueryValueHolder;
import org.eclipse.persistence.internal.indirection.UnitOfWorkTransformerValueHolder;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.sessions.coordination.MergeChangeSetCommand;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.internal.localization.LoggingLocalization;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangePolicy;

/**
 * Implementation of org.eclipse.persistence.sessions.UnitOfWork
 * The public interface should be used.
 * @see org.eclipse.persistence.sessions.UnitOfWork
 * <p>
 * <b>Purpose</b>: To allow object level transactions.
 * <p>
 * <b>Description</b>: The unit of work is a session that implements all of the normal
 * protocol of an EclipseLink session. It can be spawned from any other session including another unit of work.
 * Objects can be brought into the unit of work through reading them or through registering them.
 * The unit of work will operate on its own object space, that is the objects within the unit of work
 * will be clones of the original objects.  When the unit of work is committed, all changes to any objects
 * registered within the unit of work will be committed to the database.  A minimal commit/update will
 * be performed and any foreign keys/circular reference/referential integrity will be resolved.
 * If the commit to the database is successful the changed objects will be merged back into the unit of work
 * parent session.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Allow parallel transactions against a session's objects.
 * <li> Allow nested transactions.
 * <li> Not require the application to write objects that is changes, automatically determine what has changed.
 * <li> Perform a minimal commit/update of all changes that occurred.
 * <li> Resolve foreign keys for newly created objects and maintain referential integrity.
 * <li> Allow for the object transaction to use its own object space.
 * </ul>
 * 
 */
public class UnitOfWorkImpl extends AbstractSession implements org.eclipse.persistence.sessions.UnitOfWork {

    /** Fix made for weak caches to avoid garbage collection of the originals. **/
    /** As well as used as lookup in merge algorithm for aggregates and others **/
    protected transient Map<Object, Object> cloneToOriginals;
    protected transient AbstractSession parent;

    /** Map of all the clones.  The key contains the clone of the object. */
    protected Map<Object, Object> cloneMapping;
    protected Map<Object, Object> newObjectsCloneToOriginal;
    protected Map<Object, Object> newObjectsOriginalToClone;
    /**
     * Stores a map from the clone to the original merged object, as a different instance is used as the original for merges.
     */
    protected Map<Object, Object> newObjectsCloneToMergeOriginal;
    protected Map<Object, Object> deletedObjects;

    /** This member variable contains a copy of all of the clones for this particular UOW */
    protected Map<Object, Object> allClones;
    protected Map<Object, Object> objectsDeletedDuringCommit;
    protected Map<Object, Object> removedObjects;
    protected Map<Object, Object> unregisteredNewObjects;
    protected Map<Object, Object> unregisteredNewObjectsInParent;
    protected Map<Object, Object> unregisteredExistingObjects;

    // bug # 3228185
    // this collection is used to store the new objects from the parent.
    // They will not be treated as new in the nested unit of work, so we must
    //store them somewhere specifically to lookup later.
    protected Map<Object, Object> newObjectsInParentOriginalToClone;
    
    /** Cache references of private owned objects for the removal of private owned orphans */
    protected Map<DatabaseMapping, Set> privateOwnedObjects;

    /** used to store a list of the new objects in the parent */

    //cr 2783 
    protected Map<Object, Object> newObjectsInParent;
    protected Map<Object, Object> newAggregates;

    /** This method is used to store the current changeSet for this UnitOfWork. */
    protected UnitOfWorkChangeSet unitOfWorkChangeSet;

    /** This is only used for EJB entity beans to manage beans accessed in a transaction context. */
    protected UnitOfWorkImpl containerUnitOfWork;
    protected Map<Object, Object> containerBeans;

    /** use to track pessimistic locked objects  */
    protected Map<Object, Object> pessimisticLockedObjects;

    /** Used to store the list of locks that this UnitOfWork has acquired for this merge */
    protected transient MergeManager lastUsedMergeManager;

    /**
     * When in transaction batch read objects must use query local
     * to the unit of work.
     */
    protected Map<ReadQuery, ReadQuery> batchQueries;

    /** Read-only class can be used for reference data to avoid cloning when not required. */
    protected Set<Class> readOnlyClasses;

    /** Flag indicating that the transaction for this UOW was already begun. */
    protected boolean wasTransactionBegunPrematurely;

    /** Allow for double merges of new objects by putting them into the cache. */
    protected boolean shouldNewObjectsBeCached;

    /** Flag indicating that deletes should be performed before other updates. */
    protected boolean shouldPerformDeletesFirst;

    /** Flag indicating how to deal with exceptions on conforming queries. **/
    protected int shouldThrowConformExceptions;

    /** The amount of validation can be configured. */
    protected int validationLevel;
    static public final int None = 0;
    static public final int Partial = 1;
    static public final int Full = 2;

    /**
     * With the new synchronized unit of work, need a lifecycle state variable to
     * track birth, committed, pending_merge and death.
     */
    protected int lifecycle;
    public static final int Birth = 0;
    public static final int CommitPending = 1;

    // After a call to writeChanges() but before commit.
    public static final int CommitTransactionPending = 2;

    // After an unsuccessful call to writeChanges().  No recovery at all.
    public static final int WriteChangesFailed = 3;
    public static final int MergePending = 4;
    public static final int Death = 5;
    public static final int AfterExternalTransactionRolledBack = 6;

    /** Used for Conforming Queries */
    public static final int DO_NOT_THROW_CONFORM_EXCEPTIONS = 0;
    public static final int THROW_ALL_CONFORM_EXCEPTIONS = 1;

    //CR 3677 removed option to only throw valueHolderExceptions as this governed by
    //the InMemoryQueryIndirectionPolicy
    public static final String LOCK_QUERIES_PROPERTY = "LockQueriesProperties";

    /** Used for merging dependent values without use of WL SessionAccessor */
    protected static boolean SmartMerge = false;

    /** Kept reference of read lock objects*/
    protected Map<Object, Object> optimisticReadLockObjects;

    /** Used for read lock to determine update the version field with the same value or increment value */
    public static final String ReadLockOnly = "no update";
    public static final String ReadLockUpdateVersion = "update version";

    /** lazy initialization done in storeModifyAllQuery.  For UpdateAllQuery, only clones of all UpdateAllQuery's (deferred and non-deferred) are stored here for validation only.*/
    protected List<ModifyAllQuery> modifyAllQueries;

    /**
     * Contains deferred ModifyAllQuery's that have translation row for execution only.
     * At commit their clones will be added to modifyAllQueries for validation afterwards.
     * Array of the query (ModifyAllQuery) and translationRow (AbstractRecord).
     */
    //Bug4607551 
    protected List<Object[]> deferredModifyAllQueries;

    /**
     * Used during the cloning process to track the recursive depth in.  This will
     * be used to determine at which point the process can begin to wait on locks
     * without being concerned about creating deadlock situations.
     */
    protected int cloneDepth;

    /**
     * PERF: Stores the JTA transaction to optimize activeUnitOfWork lookup.
     */
    protected Object transaction;
    
    /**
     * True if UnitOfWork should be resumed on completion of transaction.
     * Used when UnitOfWork is Synchronized with external transaction control
     */
    protected boolean resumeOnTransactionCompletion;
    
    /**
     * PERF: Allows discover new objects to be skipped if app always calls persist.
     */
    protected boolean shouldDiscoverNewObjects;
    
    /**
     * True if either DataModifyQuery or ModifyAllQuery was executed.
     * Gets reset on commit, effects DoesExistQuery behavior and reading.
     */
    protected boolean wasNonObjectLevelModifyQueryExecuted;

    /**
     * True if the value holder for the joined attribute should be triggered.
     * Required by ejb30 fetch join.
     */
    protected boolean shouldCascadeCloneToJoinedRelationship;

    /** PERF: Cache isNestedUnitOfWork check. */
    protected boolean isNestedUnitOfWork;
    
    /** Determine if does-exist should be performed on persist. */
    protected boolean shouldValidateExistence;
    
    /** Allow updates to be ordered by id to avoid possible deadlocks. */
    protected boolean shouldOrderUpdates;

    /** This stored the reference mode for this UOW.  If the reference mode is
     * weak then this unit of work will retain only weak references to non new, 
     * non-deleted objects allowing for garbage collection.  If ObjectChangeTracking
     * is used then any objects with changes will not be garbage collected.
     */
    protected ReferenceMode referenceMode;
    
    // This is list is used during change tracking to keep hard references
    // to changed objects that may otherwise have been garbage collected.
    protected Set<Object> changeTrackedHardList;

    /** Used to store objects already deleted from the db and unregistered */
    protected Map<Object, Object> unregisteredDeletedObjectsCloneToBackupAndOriginal;
    
    /** This attribute records when the preDelete stage of Commit has completed */
    protected boolean preDeleteComplete = false;
    
    /** Stores all of the private owned objects that have been removed and may need to cascade deletion */
    protected Map<DatabaseMapping, List<Object>> deletedPrivateOwnedObjects;

    /** temporarily holds a reference to a merge manager that is calling this UnitOfWork during merge **/
    protected transient MergeManager mergeManagerForActiveMerge = null;

    /** Set of objects that were deleted by database cascade delete constraints. */
    protected Set<Object> cascadeDeleteObjects;

    /**
     * Used to store deleted objects that have reference to other deleted objects.
     * This is need to delete cycles of objects in the correct order.
     */
    protected Map<Object, Set<Object>> deletionDependencies;

    /**
     * INTERNAL:
     */
    public UnitOfWorkImpl() {        
    }
    
    /**
     * INTERNAL:
     * Create and return a new unit of work with the session as its parent.
     */
    public UnitOfWorkImpl(AbstractSession parent, ReferenceMode referenceMode) {
        super();
        this.isLoggingOff = parent.isLoggingOff;
        this.referenceMode = referenceMode;
        this.shouldDiscoverNewObjects = true;
        this.name = parent.name;
        this.parent = parent;
        this.project = parent.project;
        this.profiler = parent.profiler;
        this.isInProfile = parent.isInProfile;
        this.sessionLog = parent.sessionLog;
        if (parent.hasEventManager()) {
            this.eventManager = parent.getEventManager().clone(this);
        }
        this.exceptionHandler = parent.exceptionHandler;
        this.pessimisticLockTimeoutDefault = parent.pessimisticLockTimeoutDefault;
        this.queryTimeoutDefault = parent.queryTimeoutDefault;
        this.isConcurrent = parent.isConcurrent();
        // Initialize the readOnlyClasses variable.
        this.setReadOnlyClasses(parent.copyReadOnlyClasses());
        this.validationLevel = Partial;

        // for 3.0.x this conforming queries will not throw exceptions unless explicitly asked to
        this.shouldThrowConformExceptions = DO_NOT_THROW_CONFORM_EXCEPTIONS;

        // initialize lifecycle state variable
        this.lifecycle = Birth;
        // PERF: Cache the write-lock check to avoid cost of checking in every register/clone.
        this.isNestedUnitOfWork = parent.isUnitOfWork();
        
        if (this.eventManager != null) {
            this.eventManager.postAcquireUnitOfWork();
        }
        this.descriptors = parent.getDescriptors();
        incrementProfile(SessionProfiler.UowCreated);
        // PERF: Cache the write-lock check to avoid cost of checking in every register/clone.
        this.shouldCheckWriteLock = getParent().getDatasourceLogin().shouldSynchronizedReadOnWrite() || getParent().getDatasourceLogin().shouldSynchronizeWrites();
        
        // Order updates by id
        this.shouldOrderUpdates = true;
        
        // Copy down the table per tenant information.
        this.tablePerTenantDescriptors = parent.tablePerTenantDescriptors;
        this.tablePerTenantQueries = parent.tablePerTenantQueries;
    }

    /**
     * INTERNAL:
     * Acquires a special historical session for reading objects as of a past time.
     */
    public org.eclipse.persistence.sessions.Session acquireHistoricalSession(org.eclipse.persistence.history.AsOfClause clause) throws ValidationException {
        throw ValidationException.cannotAcquireHistoricalSession();
    }

    /**
       * PUBLIC:
       * Return a nested unit of work for this unit of work.
       * A nested unit of work can be used to isolate a subset of work on the unit of work,
       * such as a dialog being open from an editor.  The nested unit of work will only
       * commit change to its objects to its parent unit of work, not the database.
       * Only the parent unit of work will commit to the database.
       *
       * @see UnitOfWorkImpl
       */
    public UnitOfWorkImpl acquireUnitOfWork() {
        UnitOfWorkImpl uow = super.acquireUnitOfWork();
        uow.discoverAllUnregisteredNewObjectsInParent();

        return uow;
    }

    /**
     * INTERNAL:
     * Records a private owned object that has been de-referenced and will need to processed
     * for related private owned objects.
     */
    public void addDeletedPrivateOwnedObjects(DatabaseMapping mapping, Object object)
    {
        if(deletedPrivateOwnedObjects == null){
            deletedPrivateOwnedObjects = new IdentityHashMap();
        }
        List<Object> list = deletedPrivateOwnedObjects.get(mapping);
        if(list == null){
            list = new ArrayList<Object>();
            deletedPrivateOwnedObjects.put(mapping, list);
        }
        list.add(object);
    }

    /**
     * INTERNAL:
     * Register a new aggregate object with the unit of work.
     */
    public void addNewAggregate(Object originalObject) {
        getNewAggregates().put(originalObject, originalObject);
    }

    /**
     * INTERNAL:
     * Add object deleted during root commit of unit of work.
     */
    public void addObjectDeletedDuringCommit(Object object, ClassDescriptor descriptor) {
        // The object's key is keyed on the object, this avoids having to compute the key later on.
        getObjectsDeletedDuringCommit().put(object, keyFromObject(object, descriptor));
        //bug 4730595: changed to add deleted objects to the changesets.
        ((UnitOfWorkChangeSet)getUnitOfWorkChangeSet()).addDeletedObject(object, this);
    }

    /**
     * PUBLIC:
     * Adds the given Java class to the receiver's set of read-only classes.
     * Cannot be called after objects have been registered in the unit of work.
     */
    public void addReadOnlyClass(Class theClass) throws ValidationException {
        if (!canChangeReadOnlySet()) {
            throw ValidationException.cannotModifyReadOnlyClassesSetAfterUsingUnitOfWork();
        }

        getReadOnlyClasses().add(theClass);

        ClassDescriptor descriptor = getDescriptor(theClass);

        // Also mark all subclasses as read-only.
        if (descriptor.hasInheritance()) {
            for (ClassDescriptor childDescriptor : descriptor.getInheritancePolicy().getChildDescriptors()) {
                addReadOnlyClass(childDescriptor.getJavaClass());
            }
        }
    }

    /**
     * PUBLIC:
     * Adds the classes in the given Vector to the existing set of read-only classes.
     * Cannot be called after objects have been registered in the unit of work.
     */
    public void addReadOnlyClasses(Collection classes) {
        for (Iterator iterator = classes.iterator(); iterator.hasNext();) {
            Class theClass = (Class)iterator.next();
            addReadOnlyClass(theClass);
        }
    }

    /**
     * INTERNAL:
     * Register that an object was removed in a nested unit of work.
     */
    public void addRemovedObject(Object orignal) {
        getRemovedObjects().put(orignal, orignal);// Use as set.
    }

    /**
     * ADVANCED:
     * Assign sequence number to the object.
     * This allows for an object's id to be assigned before commit.
     * It can be used if the application requires to use the object id before the object exists on the database.
     * Normally all ids are assigned during the commit automatically.
     */
    public void assignSequenceNumber(Object object) throws DatabaseException {
        ClassDescriptor descriptor = getDescriptor(object);
        Object implementation = descriptor.getObjectBuilder().unwrapObject(object, this);
        assignSequenceNumber(implementation, descriptor);
    }
    
    /**
     * INTERNAL:
     * Assign sequence number to the object.
     */
    public Object assignSequenceNumber(Object object, ClassDescriptor descriptor) throws DatabaseException {
        Object value = null;
        
        // This is done outside of a transaction to ensure optimal concurrency and deadlock avoidance in the sequence table.
        if (descriptor.usesSequenceNumbers() && !descriptor.getSequence().shouldAcquireValueAfterInsert()) {
            startOperationProfile(SessionProfiler.AssignSequence);
            ObjectBuilder builder = descriptor.getObjectBuilder();
            try {
                value = builder.assignSequenceNumber(object, this);
            } catch (RuntimeException exception) {
                handleException(exception);
            } finally {
                endOperationProfile(SessionProfiler.AssignSequence);
            }
        }    
        
        return value;
    }
    
    /**
     * ADVANCED:
     * Assign sequence numbers to all new objects registered in this unit of work,
     * or any new objects reference by any objects registered.
     * This allows for an object's id to be assigned before commit.
     * It can be used if the application requires to use the object id before the object exists on the database.
     * Normally all ids are assigned during the commit automatically.
     */
    public void assignSequenceNumbers() throws DatabaseException {
        // This should be done outside of a transaction to ensure optimal concurrency and deadlock avoidance in the sequence table.
        // discoverAllUnregisteredNewObjects() should be called no matter whether sequencing used
        // or not, because collectAndPrepareObjectsForCommit() method (which calls assignSequenceNumbers())
        // needs it.
        // It would be logical to remove discoverAllUnregisteredNewObjects() from  assignSequenceNumbers()
        // and make collectAndPrepareObjectsForCommit() to call discoverAllUnregisteredNewObjects()
        // first and assignSequenceNumbers() next,
        // but assignSequenceNumbers() is a public method which could be called by user - and
        // in this case discoverAllUnregisteredNewObjects() is needed again (though 
        // if sequencing is not used the call will make no sense - but no harm, too).
        discoverAllUnregisteredNewObjects();
        if (hasUnregisteredNewObjects()) {
            assignSequenceNumbers(getUnregisteredNewObjects());
        }
        if (hasNewObjects()) {
            assignSequenceNumbers(getNewObjectsCloneToOriginal());
        }
    }
    
    /**
     * INTERNAL:
     * Assign sequence numbers to all of the objects.
     * This allows for an object's id to be assigned before commit.
     * It can be used if the application requires to use the object id before the object exists on the database.
     * Normally all ids are assigned during the commit automatically.
     */
    protected void assignSequenceNumbers(Map objects) throws DatabaseException {
        if (objects.isEmpty()) {
            return;
        }
        
        Sequencing sequencing = getSequencing();
        if (sequencing == null) {
            return;
        }
        int whenShouldAcquireValueForAll = sequencing.whenShouldAcquireValueForAll();
        if (whenShouldAcquireValueForAll == Sequencing.AFTER_INSERT) {
            return;
        }
        boolean shouldAcquireValueBeforeInsertForAll = whenShouldAcquireValueForAll == Sequencing.BEFORE_INSERT;
        startOperationProfile(SessionProfiler.AssignSequence);
        Iterator newObjects = objects.keySet().iterator();
        while (newObjects.hasNext()) {
            Object object = newObjects.next();
            ClassDescriptor descriptor = getDescriptor(object);
            if (descriptor.usesSequenceNumbers()
                    && (shouldAcquireValueBeforeInsertForAll || !descriptor.getSequence().shouldAcquireValueAfterInsert())) {
                descriptor.getObjectBuilder().assignSequenceNumber(object, this);
            }
        }
        endOperationProfile(SessionProfiler.AssignSequence);
    }

    /**
     * PUBLIC:
     * Tell the unit of work to begin a transaction now.
     * By default the unit of work will begin a transaction at commit time.
     * The default is the recommended approach, however sometimes it is
     * necessary to start the transaction before commit time.  When the
     * unit of work commits, this transaction will be committed.
     *
     * @see #commit()
     * @see #release()
     */
    public void beginEarlyTransaction() throws DatabaseException {
        beginTransaction();
        setWasTransactionBegunPrematurely(true);
    }

    /**
     * INTERNAL:
     * This is internal to the uow, transactions should not be used explicitly in a uow.
     * The uow shares its parents transactions.
     */
    public void beginTransaction() throws DatabaseException {
        this.parent.beginTransaction();
    }

    /**
     * INTERNAL:
     * Unregistered new objects have no original so we must create one for commit and resume and
     * to put into the parent.  We can NEVER let the same copy of an object exist in multiple units of work.
     */
    public Object buildOriginal(Object workingClone) {
        ClassDescriptor descriptor = getDescriptor(workingClone);
        ObjectBuilder builder = descriptor.getObjectBuilder();
        Object original = builder.instantiateClone(workingClone, this);

        // If no original exists can mean any of the following:
        // -A RemoteUnitOfWork and cloneToOriginals is transient.
        // -A clone read while in transaction, and built directly from
        // the database row with no intermediary original.
        // -An unregistered new object
        if (checkIfAlreadyRegistered(workingClone, descriptor) != null) {
            getCloneToOriginals().put(workingClone, original);
            return original;
        } else {
            // Assume it is an unregisteredNewObject, but this is worrisome, as
            // it may be an unregistered existing object, not in the parent cache?
            Object backup = builder.instantiateClone(workingClone, this);

            // Original is fine for backup as state is the same.
            getCloneMapping().put(workingClone, backup);

            // Must register new instance / clone as the original.
            getNewObjectsCloneToOriginal().put(workingClone, original);
            getNewObjectsOriginalToClone().put(original, workingClone);

            // no need to register in identity map as the DatabaseQueryMechanism will have
            //placed the object in the identity map on insert.  bug 3431586
        }
        return original;
    }

    /**
     * INTERNAL:
     * <p> This calculates changes in two passes, first on registered objects,
     * second it discovers unregistered new objects on only those objects that changed, and calculates their changes.
     * This also assigns sequence numbers to new objects.
     */
    public UnitOfWorkChangeSet calculateChanges(Map registeredObjects, UnitOfWorkChangeSet changeSet, boolean assignSequences, boolean shouldCloneMap) {
        // Fire the event first which may add to the registered objects. If we
        // need to clone the registered objects, it should be done after this 
        // call.
        if (this.eventManager != null) {
            this.eventManager.preCalculateUnitOfWorkChangeSet();
        }
        
        Map allObjects = (shouldCloneMap) ? cloneMap(registeredObjects) : registeredObjects;
        
        if (assignSequences && hasNewObjects()) {
            // First assign sequence numbers to new objects.
            assignSequenceNumbers(this.newObjectsCloneToOriginal);
        }
        
        // Second calculate changes for all registered objects.
        Iterator objects = allObjects.keySet().iterator();
        Map changedObjects = new IdentityHashMap();
        Map visitedNodes = new IdentityHashMap();
        while (objects.hasNext()) {
            Object object = objects.next();

            // Block of code removed because it will never be touched see bug # 2903565
            
            ClassDescriptor descriptor = getDescriptor(object);

            // Update any derived id's.
            updateDerivedIds(object, descriptor);
            
            // Block of code removed for code coverage, as it would never have been touched. bug # 2903600
            
            boolean isNew = isCloneNewObject(object);
            // Use the object change policy to determine if we should run a comparison for this object - TGW.
            if (isNew || descriptor.getObjectChangePolicy().shouldCompareExistingObjectForChange(object, this, descriptor)) {
                ObjectChangeSet changes = null;
                if (isNew) {
                    changes = descriptor.getObjectChangePolicy().calculateChangesForNewObject(object, changeSet, this, descriptor, true);
                } else {
                    changes = descriptor.getObjectChangePolicy().calculateChangesForExistingObject(object, changeSet, this, descriptor, true);
                }
                if (changes != null) {
                    changeSet.addObjectChangeSet(changes, this, true);
                    changedObjects.put(object, object);
                    if (changes.hasChanges() && !changes.hasForcedChangesFromCascadeLocking()) {
                        if (descriptor.hasCascadeLockingPolicies()) {
                            for (CascadeLockingPolicy policy : descriptor.getCascadeLockingPolicies()) {
                                policy.lockNotifyParent(object, changeSet, this);
                            }
                        } else if (descriptor.usesOptimisticLocking() && descriptor.getOptimisticLockingPolicy().isCascaded()) {
                            changes.setHasForcedChangesFromCascadeLocking(true);
                        }
                    }
                } else {
                    // Mark as visited so do not need to traverse.
                    visitedNodes.put(object, object);
                }
            } else {
                // Mark as visited so do not need to traverse.
                visitedNodes.put(object, object);
            }
        }
        if (hasDeletedObjects() && !isNestedUnitOfWork()) {
            for (Object deletedObject : ((IdentityHashMap)((IdentityHashMap)this.deletedObjects).clone()).keySet()) {
                getDescriptor(deletedObject).getObjectBuilder().recordPrivateOwnedRemovals(deletedObject, this, true);
            }
        }
        if ((this.deletedPrivateOwnedObjects != null) && !this.isNestedUnitOfWork) {
            for (Map.Entry<DatabaseMapping, List<Object>> entry : this.deletedPrivateOwnedObjects.entrySet()) {
                DatabaseMapping databasemapping = entry.getKey();
                for (Object deletedObject : entry.getValue()) {
                    databasemapping.getReferenceDescriptor().getObjectBuilder().recordPrivateOwnedRemovals(deletedObject, this, false);
                }
            }
            this.deletedPrivateOwnedObjects.clear();
        }
        
        if (this.project.hasMappingsPostCalculateChangesOnDeleted()) {
            if (hasDeletedObjects()) {
                for (Iterator deletedObjects = getDeletedObjects().keySet().iterator(); deletedObjects.hasNext();) {
                    Object deletedObject = deletedObjects.next();
                    ClassDescriptor descriptor = getDescriptor(deletedObject);
                    if(descriptor.hasMappingsPostCalculateChangesOnDeleted()) {
                        int size = descriptor.getMappingsPostCalculateChangesOnDeleted().size();
                        for(int i=0; i < size; i++) {
                            DatabaseMapping mapping = descriptor.getMappingsPostCalculateChangesOnDeleted().get(i); 
                            mapping.postCalculateChangesOnDeleted(deletedObject, changeSet, this);
                        }
                    }
                }
            }
        }
        
        if (this.shouldDiscoverNewObjects && !changedObjects.isEmpty()) {
            // Third discover any new objects from the new or changed objects.
            Map newObjects = new IdentityHashMap();
            // Bug 294259 -  Do not replace the existingObjects list
            // Iterate over the changed objects only.
            discoverUnregisteredNewObjects(changedObjects, newObjects, getUnregisteredExistingObjects(), visitedNodes);
            
            setUnregisteredNewObjects(newObjects);
            if (assignSequences) {
                assignSequenceNumbers(newObjects);
            }
            for (Iterator newObjectsEnum = newObjects.values().iterator(); newObjectsEnum.hasNext(); ) {
                Object object = newObjectsEnum.next();
                ClassDescriptor descriptor = getDescriptor(object);
                ObjectChangeSet changes = descriptor.getObjectChangePolicy().calculateChangesForNewObject(object, changeSet, this, descriptor, true);
                // Since it is new, it will always have a change set.
                changeSet.addObjectChangeSet(changes, this, true);
            }
        }
        
        // Remove any orphaned privately owned objects from the UnitOfWork and ChangeSets,
        // these are the objects remaining in the UnitOfWork privateOwnedObjects map
        if (hasPrivateOwnedObjects()) {
            Map visitedObjects = new IdentityHashMap();
            for (Set privateOwnedObjects : getPrivateOwnedObjects().values()) {
                for (Object objectToRemove : privateOwnedObjects) {
                    performRemovePrivateOwnedObjectFromChangeSet(objectToRemove, visitedObjects);
                }
            }
            this.privateOwnedObjects.clear();
        }
        if (this.eventManager != null) {
            this.eventManager.postCalculateUnitOfWorkChangeSet(changeSet);
        }
        return changeSet;
    }

    /**
     * INTERNAL:
     * Checks whether the receiver has been used. i.e. objects have been registered.
     *
     * @return true or false depending on whether the read-only set can be changed or not.
     */
    protected boolean canChangeReadOnlySet() {
        return !hasCloneMapping() && !hasDeletedObjects();
    }

    /**
     * INTERNAL:
     * Return if the object is an existing object (but has not been registered),
     * or a new object (that has not be persisted).
     */
    public boolean checkForUnregisteredExistingObject(Object object) {

        ClassDescriptor descriptor = getDescriptor(object.getClass());
        Object primaryKey = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(object, this, true);
        if (primaryKey == null) {
            return false;
        }
        DoesExistQuery existQuery = descriptor.getQueryManager().getDoesExistQuery();

        existQuery = (DoesExistQuery) existQuery.clone();
        existQuery.setObject(object);
        existQuery.setPrimaryKey(primaryKey);
        existQuery.setDescriptor(descriptor);
        existQuery.setIsExecutionClone(true);

        return ((Boolean) executeQuery(existQuery)).booleanValue();
    }

    /**
     * INTERNAL: Register the object and return the clone if it is existing
     * otherwise return null if it is new. The unit of work determines existence
     * during registration, not during the commit.
     */
    public Object checkExistence(Object object) {
        ClassDescriptor descriptor = getDescriptor(object.getClass());
        Object primaryKey = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(object, this, true);
        // PERF: null primary key cannot exist.
        if (primaryKey == null) {
            return null;
        }
        DoesExistQuery existQuery = descriptor.getQueryManager().getDoesExistQuery();

        // PERF: Avoid cost of query execution as normally can determine from checkEarlyReturn.
        Boolean exists = (Boolean)existQuery.checkEarlyReturn(object, primaryKey, this, null);
        if (exists == null) {
            // Need to execute database query.
            existQuery = (DoesExistQuery)existQuery.clone();
            existQuery.setObject(object);
            existQuery.setPrimaryKey(primaryKey);
            existQuery.setDescriptor(descriptor);
            existQuery.setIsExecutionClone(true);
            exists = ((Boolean)executeQuery(existQuery)).booleanValue();
        }
        if (exists) {
            //we know if it exists or not, now find or register it
            Object objectFromCache = getIdentityMapAccessorInstance().getFromIdentityMap(primaryKey, object.getClass(), descriptor);
    
            if (objectFromCache != null) {
                // Ensure that the registered object is the one from the parent cache.
                if (shouldPerformFullValidation()) {
                    if ((objectFromCache != object) && (this.parent.getIdentityMapAccessorInstance().getFromIdentityMap(primaryKey, object.getClass(), descriptor) != object)) {
                        throw ValidationException.wrongObjectRegistered(object, objectFromCache);
                    }
                }
    
                // Has already been cloned.
                if (!this.isObjectDeleted(objectFromCache))
                    return objectFromCache;
            }
            // This is a case where the object is not in the session cache,
            // so a new cache-key is used as there is no original to use for locking.
            // It read time must be set to avoid it being invalidated.
            CacheKey cacheKey = new CacheKey(primaryKey);
            cacheKey.setReadTime(System.currentTimeMillis());
            cacheKey.setIsolated(true); // if the cache does not have a version then this must be built from the supplied version
            return cloneAndRegisterObject(object, cacheKey, descriptor);
        } else {
            return null;
        }
    }

    /**
     * INTERNAL:
     * Return the value of the object if it already is registered, otherwise null.
     */
    public Object checkIfAlreadyRegistered(Object object, ClassDescriptor descriptor) {
        // Don't register read-only classes
        if (isClassReadOnly(object.getClass(), descriptor)) {
            return null;
        }

        // Check if the working copy is again being registered in which case we return the same working copy
        Object registeredObject = getCloneMapping().get(object);
        if (registeredObject != null) {
            return object;
        }

        // Check if object exists in my new objects if it is in the new objects cache then it means domain object is being 
        // re-registered and we should return the same working clone. This check holds only for the new registered objects 
        // PERF: Avoid initialization of new objects if none.
        if (hasNewObjects()) {
            registeredObject = getNewObjectsOriginalToClone().get(object);
            if (registeredObject != null) {
                return registeredObject;
            }
        }
        if (this.isNestedUnitOfWork) {
            // bug # 3228185
            //may be a new object from a parent Unit Of Work, let's check our new object in parent list to see
            //if it has already been registered locally
            if (hasNewObjectsInParentOriginalToClone()) {
                registeredObject = getNewObjectsInParentOriginalToClone().get(object);
            }
            if (registeredObject != null) {
                return registeredObject;
            }
        }

        return null;
    }

    /**
     * INTERNAL:
     * Check if the object is invalid and *should* be refreshed.
     * This is used to ensure that no invalid objects are cloned.
     */
    @Override
    public boolean isConsideredInvalid(Object object, CacheKey cacheKey, ClassDescriptor descriptor) {
        if (! isNestedUnitOfWork){
            return getParent().isConsideredInvalid(object, cacheKey, descriptor);
        }
        return false;
    }

    /**
     * ADVANCED:
     * Register the new object with the unit of work.
     * This will register the new object with cloning.
     * Normally the registerObject method should be used for all registration of new and existing objects.
     * This version of the register method can only be used for new objects.
     * This method should only be used if a new object is desired to be registered without an existence Check.
     *
     * @see #registerObject(Object)
     */
    protected Object cloneAndRegisterNewObject(Object original, boolean isShallowClone) {
        ClassDescriptor descriptor = getDescriptor(original);
        //Nested unit of work is not supported for attribute change tracking
        if (this.isNestedUnitOfWork && (descriptor.getObjectChangePolicy() instanceof AttributeChangeTrackingPolicy)) {
            throw ValidationException.nestedUOWNotSupportedForAttributeTracking();
        }
        ObjectBuilder builder = descriptor.getObjectBuilder();

        // bug 2612602 create the working copy object.
        Object clone = builder.instantiateWorkingCopyClone(original, this);

        // Must put in the original to clone to resolve circular refs.
        getNewObjectsOriginalToClone().put(original, clone);
        getNewObjectsCloneToOriginal().put(clone, original);        
        // Must put in clone mapping.
        getCloneMapping().put(clone, clone);

        if (isShallowClone) {
            builder.copyInto(original, clone, true);
        } else {
            builder.populateAttributesForClone(original, null, clone, null, this);
        }
        // Must reregister in both new objects.
        registerNewObjectClone(clone, original, descriptor);

        //Build backup clone for DeferredChangeDetectionPolicy or ObjectChangeTrackingPolicy,
        //but not for AttributeChangeTrackingPolicy
        Object backupClone = descriptor.getObjectChangePolicy().buildBackupClone(clone, builder, this);
        getCloneMapping().put(clone, backupClone);// The backup clone must be updated.
        executeDeferredEvents();

        return clone;
    }

    /**
     * INTERNAL:
     * Clone and register the object.
     * The cache key must the cache key from the session cache, as it will be used for locking.
     * The unit of work cache key is passed to the normal cloneAndRegisterObject method.
     */
    public Object cloneAndRegisterObject(Object original, CacheKey parentCacheKey, ClassDescriptor descriptor) {
        CacheKey unitOfWorkCacheKey = null;
        if (parentCacheKey.getKey() == null) {
            // The primary key may be null for nested units of work with new parent objects.
            unitOfWorkCacheKey = new CacheKey(null);
            unitOfWorkCacheKey.setIsolated(true);
            unitOfWorkCacheKey.acquire();
        } else {
            unitOfWorkCacheKey = getIdentityMapAccessorInstance().acquireLock(parentCacheKey.getKey(), original.getClass(), descriptor, false);
        }
        try {
            return cloneAndRegisterObject(original, parentCacheKey, unitOfWorkCacheKey, descriptor);
        } finally {
            unitOfWorkCacheKey.release();
        }
    }
    
    /**
     * INTERNAL:
     * Clone and register the object.
     * The cache key must the cache key from the session cache,
     * as it will be used for locking.
     */
    public Object cloneAndRegisterObject(Object original, CacheKey parentCacheKey, CacheKey unitOfWorkCacheKey, ClassDescriptor descriptor) {
        ClassDescriptor concreteDescriptor = descriptor;
        // Ensure correct subclass descriptor.
        if (original.getClass() != descriptor.getJavaClass()) {
            concreteDescriptor = getDescriptor(original);
        }
        // Nested unit of work is not supported for attribute change tracking.
        if (this.isNestedUnitOfWork && (concreteDescriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy())) {
            throw ValidationException.nestedUOWNotSupportedForAttributeTracking();
        }

        ObjectBuilder builder = concreteDescriptor.getObjectBuilder();
        Object workingClone = null;
        
        // The cache/objects being registered must first be locked to ensure
        // that a merge or refresh does not occur on the object while being cloned to
        // avoid cloning a partially merged/refreshed object.
        // If a cache isolation level is used, then lock the entire cache.
        // otherwise lock the object and it related objects (not using indirection) as a unit.
        // If just a simple object (all indirection) a simple read-lock can be used.
        // PERF: Cache if check to write is required.
        boolean identityMapLocked = this.parent.shouldCheckWriteLock && this.parent.getIdentityMapAccessorInstance().acquireWriteLock();
        boolean rootOfCloneRecursion = false;
        if (identityMapLocked) {
            checkAndRefreshInvalidObject(original, parentCacheKey, descriptor);
        } else {
            // Check if we have locked all required objects already.
            if (this.objectsLockedForClone == null) {
                // PERF: If a simple object just acquire a simple read-lock.
                if (concreteDescriptor.shouldAcquireCascadedLocks()) {
                    this.objectsLockedForClone = this.parent.getIdentityMapAccessorInstance().getWriteLockManager().acquireLocksForClone(original, concreteDescriptor, parentCacheKey, this.parent);
                } else {
                    checkAndRefreshInvalidObject(original, parentCacheKey, descriptor);
                    parentCacheKey.acquireReadLock();
                }
                rootOfCloneRecursion = true;
            }
        }
        try {
            // bug:6167576   Must acquire the lock before cloning.
            workingClone = builder.instantiateWorkingCopyClone(original, this);
            // PERF: Cache the primary key if implements PersistenceEntity.
            if (workingClone instanceof PersistenceEntity) {
                ((PersistenceEntity)workingClone)._persistence_setId(parentCacheKey.getKey());
            }

            // This must be registered before it is built to avoid really obscure cycles.
            getCloneMapping().put(workingClone, workingClone);

            // bug # 3228185 & Bug4736360
            // if this is a nested unit of work and the object is new in the parent
            //  and we must store it in the newobject list for lookup later
            if (this.isNestedUnitOfWork && isCloneNewObjectFromParent(original)) {
                getNewObjectsInParentOriginalToClone().put(original, workingClone);
            }

            //store this for look up later
            getCloneToOriginals().put(workingClone, original);
            // just clone it.
            populateAndRegisterObject(original, workingClone, unitOfWorkCacheKey, parentCacheKey, concreteDescriptor);

            //also clone the fetch group reference if applied
            if (concreteDescriptor.hasFetchGroupManager()) {
                concreteDescriptor.getFetchGroupManager().copyFetchGroupInto(original, workingClone, this);
            }
        } finally {
            // If the entire cache was locked, release the cache lock,
            // otherwise either release the cache-key for a simple lock,
            // otherwise release the entire set of locks for related objects if this was the root.
            if (identityMapLocked) {
                this.parent.getIdentityMapAccessorInstance().releaseWriteLock();
            } else {
                if (rootOfCloneRecursion) {
                    if (this.objectsLockedForClone == null) {
                        parentCacheKey.releaseReadLock();
                    } else {                        
                        for (Iterator iterator = this.objectsLockedForClone.values().iterator(); iterator.hasNext();) {
                            ((CacheKey)iterator.next()).releaseReadLock();
                        }
                        this.objectsLockedForClone = null;
                    }
                    executeDeferredEvents();
                }
            }
        }
        concreteDescriptor.getObjectBuilder().instantiateEagerMappings(workingClone, this);
        return workingClone;
    }
    
    /**
     * INTERNAL:
     * Prepare for merge in nested uow.
     */
    public Map collectAndPrepareObjectsForNestedMerge() {
        discoverAllUnregisteredNewObjectsInParent();
        return new IdentityHashMap(this.getCloneMapping());
    }

    /**
     * PUBLIC:
     * Commit the unit of work to its parent.
     * For a nested unit of work this will merge any changes to its objects
     * with its parents.
     * For a first level unit of work it will commit all changes to its objects
     * to the database as a single transaction.  If successful the changes to its
     * objects will be merged to its parent's objects.  If the commit fails the database
     * transaction will be rolledback, and the unit of work will be released.
     * If the commit is successful the unit of work is released, and a new unit of work
     * must be acquired if further changes are desired.
     *
     * @see #commitAndResumeOnFailure()
     * @see #commitAndResume()
     * @see #release()
     */
    public void commit() throws DatabaseException, OptimisticLockException {
        //CR#2189 throwing exception if UOW try to commit again(XC)
        if (!isActive()) {
            throw ValidationException.cannotCommitUOWAgain();
        }
        if (isAfterWriteChangesFailed()) {
            throw ValidationException.unitOfWorkAfterWriteChangesFailed("commit");
        }

        if (!this.isNestedUnitOfWork) {
            if (isSynchronized()) {
                // If we started the JTS transaction then we have to commit it as well.
                if (this.parent.wasJTSTransactionInternallyStarted()) {
                    commitInternallyStartedExternalTransaction();
                }

                // Do not commit until the JTS wants to.
                return;
            }
        }
        log(SessionLog.FINER, SessionLog.TRANSACTION, "begin_unit_of_work_commit");
        if (this.lifecycle == CommitTransactionPending) {
            commitAfterWriteChanges();
            return;
        }
        if (this.eventManager != null) {
            this.eventManager.preCommitUnitOfWork();
        }
        setLifecycle(CommitPending);
        if (this.isNestedUnitOfWork) {
            commitNestedUnitOfWork();
        } else {
            commitRootUnitOfWork();
        }
        if (this.eventManager != null) {
            this.eventManager.postCommitUnitOfWork();
        }
        log(SessionLog.FINER, SessionLog.TRANSACTION, "end_unit_of_work_commit");
        release();
    }

    /**
     * PUBLIC:
     * Commit the unit of work to its parent.
     * For a nested unit of work this will merge any changes to its objects
     * with its parents.
     * For a first level unit of work it will commit all changes to its objects
     * to the database as a single transaction.  If successful the changes to its
     * objects will be merged to its parent's objects.  If the commit fails the database
     * transaction will be rolledback, and the unit of work will be released.
     * The normal commit releases the unit of work, forcing a new one to be acquired if further changes are desired.
     * The resuming feature allows for the same unit of work (and working copies) to be continued to be used.
     *
     * @see #commitAndResumeOnFailure()
     * @see #commit()
     * @see #release()
     */
    public void commitAndResume() throws DatabaseException, OptimisticLockException {
        //CR#2189 throwing exception if UOW try to commit again(XC)
        if (!isActive()) {
            throw ValidationException.cannotCommitUOWAgain();
        }

        if (isAfterWriteChangesFailed()) {
            throw ValidationException.unitOfWorkAfterWriteChangesFailed("commit");
        }

        if (!this.isNestedUnitOfWork) {
            if (isSynchronized()) {
                // JTA synchronized units of work, cannot be resumed as there is no
                // JTA transaction to register with after the commit,
                // technically this could be supported if the uow started the transaction,
                // but currently the after completion releases the uow and client session so not really possible.
                throw ValidationException.cannotCommitAndResumeSynchronizedUOW(this);
            }
        }
        if (this.lifecycle == CommitTransactionPending) {
            commitAndResumeAfterWriteChanges();
            return;
        }
        log(SessionLog.FINER, SessionLog.TRANSACTION, "begin_unit_of_work_commit");// bjv - correct spelling
        if (this.eventManager != null) {
            this.eventManager.preCommitUnitOfWork();
        }
        setLifecycle(CommitPending);
        if (this.parent.isUnitOfWork()) {
            commitNestedUnitOfWork();
        } else {
            commitRootUnitOfWork();
        }
        if (this.eventManager != null) {
            this.eventManager.postCommitUnitOfWork();
        }
        log(SessionLog.FINER, SessionLog.TRANSACTION, "end_unit_of_work_commit");
        log(SessionLog.FINER, SessionLog.TRANSACTION, "resume_unit_of_work");
        synchronizeAndResume();
        if (this.eventManager != null) {
            this.eventManager.postResumeUnitOfWork();
        }
    }

    /**
     * INTERNAL:
     * This method is used by the MappingWorkbench for their read-only file feature
     * this method must not be exposed to or used by customers until it has been revised
     * and the feature revisited to support OptimisticLocking and Serialization
     */
    public void commitAndResumeWithPreBuiltChangeSet(UnitOfWorkChangeSet uowChangeSet) throws DatabaseException, OptimisticLockException {
        if (!this.isNestedUnitOfWork) {
            if (isSynchronized()) {
                // If we started the JTS transaction then we have to commit it as well.
                if (this.parent.wasJTSTransactionInternallyStarted()) {
                    commitInternallyStartedExternalTransaction();
                }

                // Do not commit until the JTS wants to.
                return;
            }
        }
        log(SessionLog.FINER, SessionLog.TRANSACTION, "begin_unit_of_work_commit");// bjv - correct spelling
        if (this.eventManager != null) {
            this.eventManager.preCommitUnitOfWork();
        }
        setLifecycle(CommitPending);
        if (this.parent.isUnitOfWork()) {
            commitNestedUnitOfWork();
        } else {
            commitRootUnitOfWorkWithPreBuiltChangeSet(uowChangeSet);
        }
        if (this.eventManager != null) {
            this.eventManager.postCommitUnitOfWork();
        }
        log(SessionLog.FINER, SessionLog.TRANSACTION, "end_unit_of_work_commit");
        log(SessionLog.FINER, SessionLog.TRANSACTION, "resume_unit_of_work");

        synchronizeAndResume();
        if (this.eventManager != null) {
            this.eventManager.postResumeUnitOfWork();
        }
    }

    /**
     * PUBLIC:
     * Commit the unit of work to its parent.
     * For a nested unit of work this will merge any changes to its objects
     * with its parents.
     * For a first level unit of work it will commit all changes to its objects
     * to the database as a single transaction.  If successful the changes to its
     * objects will be merged to its parent's objects.  If the commit fails the database
     * transaction will be rolledback, but the unit of work will remain active.
     * It can then be retried or released.
     * The normal commit failure releases the unit of work, forcing a new one to be acquired if further changes are desired.
     * The resuming feature allows for the same unit of work (and working copies) to be continued to be used if an error occurs.
     * The UnitOfWork will also remain active if the commit is successful.
     *
     * @see #commit()
     * @see #release()
     */
    public void commitAndResumeOnFailure() throws DatabaseException, OptimisticLockException {
        // First clone the identity map, on failure replace the clone back as the cache.
        IdentityMapManager failureManager = (IdentityMapManager)getIdentityMapAccessorInstance().getIdentityMapManager().clone();
        try {
            // Call commitAndResume.
            // Oct 13, 2000 - JED PRS #13551
            // This method will always resume now. Calling commitAndResume will sync the cache 
            // if successful. This method will take care of resuming if a failure occurs
            commitAndResume();
        } catch (RuntimeException exception) {
            //reset unitOfWorkChangeSet.  Needed for ObjectChangeTrackingPolicy and DeferredChangeDetectionPolicy
            setUnitOfWorkChangeSet(null);
            getIdentityMapAccessorInstance().setIdentityMapManager(failureManager);
            log(SessionLog.FINER, SessionLog.TRANSACTION, "resuming_unit_of_work_from_failure");
            throw exception;
        }
    }

    /**
     * INTERNAL:
     * Commits a UnitOfWork where the commit process has already been
     * initiated by all call to writeChanges().
     * <p>
     * a.k.a finalizeCommit()
     */
    protected void commitAfterWriteChanges() {
        commitTransactionAfterWriteChanges();
        mergeClonesAfterCompletion();
        setDead();
        release();
    }

    /**
     * INTERNAL:
     * Commits and resumes a UnitOfWork where the commit process has already been
     * initiated by all call to writeChanges().
     * <p>
     * a.k.a finalizeCommit()
     */
    protected void commitAndResumeAfterWriteChanges() {
        commitTransactionAfterWriteChanges();
        mergeClonesAfterCompletion();
        log(SessionLog.FINER, SessionLog.TRANSACTION, "resume_unit_of_work");
        synchronizeAndResume();
        if (this.eventManager != null) {
            this.eventManager.postResumeUnitOfWork();
        }
    }

    /**
     * PROTECTED:
     * Used in commit and commit-like methods to commit
     * internally started external transaction
     */
    protected boolean commitInternallyStartedExternalTransaction() {
        boolean committed = false;
        if (!this.parent.isInTransaction() || (wasTransactionBegunPrematurely() && (this.parent.getTransactionMutex().getDepth() == 1))) {
            committed = this.parent.commitExternalTransaction();
        }
        return committed;
    }

    /**
     * INTERNAL:
     * Commit the changes to any objects to the parent.
     */
    protected void commitNestedUnitOfWork() {
        this.parent.getIdentityMapAccessorInstance().acquireWriteLock();//Ensure concurrency

        try {
            // Iterate over each clone and let the object build merge to clones into the originals.
            // The change set may already exist if using change tracking.
            if (getUnitOfWorkChangeSet() == null) {
                setUnitOfWorkChangeSet(new UnitOfWorkChangeSet(this));
            }
            unitOfWorkChangeSet = calculateChanges(collectAndPrepareObjectsForNestedMerge(), (UnitOfWorkChangeSet)getUnitOfWorkChangeSet(), false, false);
            this.allClones = null;
            mergeChangesIntoParent();

            if (hasDeletedObjects()) {
                for (Iterator deletedObjects = getDeletedObjects().keySet().iterator();
                         deletedObjects.hasNext();) {
                    Object deletedObject = deletedObjects.next();
                    Object originalObject = getOriginalVersionOfObject(deletedObject);

                    // bug # 3132979 if the deleted object is new in the parent
                    //then  unregister in the parent.
                    //else add it to the deleted object list to be removed from the parent's parent
                    //this prevents erroneous insert and delete sql
                    if ((originalObject != null) && ((UnitOfWorkImpl)this.parent).getNewObjectsCloneToOriginal().containsKey(originalObject)) {
                        ((UnitOfWorkImpl)this.parent).unregisterObject(originalObject);
                    } else {
                        ((UnitOfWorkImpl)this.parent).getDeletedObjects().put(originalObject, getId(originalObject));
                    }
                }
            }
            if (hasRemovedObjects()) {
                for (Iterator removedObjects = getRemovedObjects().values().iterator();
                         removedObjects.hasNext();) {
                    ((UnitOfWorkImpl)this.parent).getCloneMapping().remove(removedObjects.next());
                }
            }
        } finally {
            this.parent.getIdentityMapAccessorInstance().releaseWriteLock();
        }
    }

    /**
     * INTERNAL:
     * Commit the changes to any objects to the parent.
     */
    public void commitRootUnitOfWork() throws DatabaseException, OptimisticLockException {
        commitToDatabaseWithChangeSet(true);
        // Merge after commit	
        mergeChangesIntoParent();
        this.changeTrackedHardList = null;

    }

    /**
     * INTERNAL:
     * This method is used by the MappingWorkbench read-only files feature
     * It will commit a pre-built unitofwork change set to the database
     */
    public void commitRootUnitOfWorkWithPreBuiltChangeSet(UnitOfWorkChangeSet uowChangeSet) throws DatabaseException, OptimisticLockException {
        //new code no need to check old commit
        commitToDatabaseWithPreBuiltChangeSet(uowChangeSet, true, true);

        // Merge after commit	
        mergeChangesIntoParent();
    }

    /**
     * INTERNAL:
     * CommitChanges To The Database from a calculated changeSet
     * @param commitTransaction false if called by writeChanges as intent is
     * not to finalize the transaction.
     */
    protected void commitToDatabase(boolean commitTransaction) {
        try {
            //CR4202 - ported from 3.6.4
            if (wasTransactionBegunPrematurely()) {
                // beginTransaction() has been already called
                setWasTransactionBegunPrematurely(false);
            } else {
                beginTransaction();
            }

            if (commitTransaction) {
                setWasNonObjectLevelModifyQueryExecuted(false);
            }
            this.preDeleteComplete = false;
            
            List deletedObjects = null;// PERF: Avoid deletion if nothing to delete.
            if (hasDeletedObjects()) {
                deletedObjects = new ArrayList(this.deletedObjects.size());
                for (Object objectToDelete : this.deletedObjects.keySet()) {
                    ClassDescriptor descriptor = getDescriptor(objectToDelete);
                    if (descriptor.hasPreDeleteMappings()) {
                        for (DatabaseMapping mapping : descriptor.getPreDeleteMappings()) {
                            DeleteObjectQuery deleteQuery = descriptor.getQueryManager().getDeleteQuery();
                            if (deleteQuery == null) {
                                deleteQuery = new DeleteObjectQuery();
                                deleteQuery.setDescriptor(descriptor);
                            } else {
                                // Ensure original query has been prepared.
                                deleteQuery.checkPrepare(this, deleteQuery.getTranslationRow());
                                deleteQuery = (DeleteObjectQuery)deleteQuery.clone();
                            }
                            deleteQuery.setIsExecutionClone(true);
                            deleteQuery.setTranslationRow(new DatabaseRecord());
                            deleteQuery.setObject(objectToDelete);
                            deleteQuery.setSession(this);
                            mapping.earlyPreDelete(deleteQuery, objectToDelete);
                        }
                    }
                    deletedObjects.add(objectToDelete);
                }
                this.preDeleteComplete = true;
            }

            if (this.shouldPerformDeletesFirst) {
                if (deletedObjects != null) {
                    // This must go to the commit manager because uow overrides to do normal deletion.
                    getCommitManager().deleteAllObjects(deletedObjects);

                    // Clear change sets of the deleted object to avoid redundant updates.
                    for (Iterator objects = getObjectsDeletedDuringCommit().keySet().iterator();
                             objects.hasNext();) {
                        org.eclipse.persistence.internal.sessions.ObjectChangeSet objectChangeSet = (org.eclipse.persistence.internal.sessions.ObjectChangeSet)this.unitOfWorkChangeSet.getObjectChangeSetForClone(objects.next());
                        if (objectChangeSet != null) {
                            objectChangeSet.clear(true);
                        }
                    }
                }

                // Let the commit manager figure out how to write the objects
                super.writeAllObjectsWithChangeSet(this.unitOfWorkChangeSet);
                // Issue all the SQL for the ModifyAllQuery's, don't touch the cache though
                issueModifyAllQueryList();
            } else {
                // Let the commit manager figure out how to write the objects
                super.writeAllObjectsWithChangeSet(this.unitOfWorkChangeSet);
                if (deletedObjects != null) {
                    // This must go to the commit manager because uow overrides to do normal deletion.
                    getCommitManager().deleteAllObjects(deletedObjects);
                }

                // Issue all the SQL for the ModifyAllQuery's, don't touch the cache though
                issueModifyAllQueryList();
            }

            // Issue prepare event.
            if (this.eventManager != null) {
                this.eventManager.prepareUnitOfWork();
            }

            // writeChanges() does everything but this step.
            // do not lock objects unless we are at the commit stage
            if (commitTransaction) {
                try {
                    acquireWriteLocks();
                    commitTransaction();
                } catch (RuntimeException throwable) {
                    releaseWriteLocks();
                    throw throwable;
                } catch (Error throwable) {
                    releaseWriteLocks();
                    throw throwable;
                }
            } else {
                setWasTransactionBegunPrematurely(true);

                //must let the UnitOfWork know that the transaction was begun
                //before the commit process.
            }
        } catch (RuntimeException exception) {
            // The number of SQL statements been prepared need be stored into UOW 
            // before any exception being thrown.
            copyStatementsCountIntoProperties();
            try {
                rollbackTransaction(commitTransaction);
            } catch (RuntimeException ignore) {
                // Ignore
            }
            if (hasExceptionHandler()) {
                getExceptionHandler().handleException(exception);
            } else {
                throw exception;
            }
        }
    }

    /**
     * INTERNAL:
     * Commit the changes to any objects to the parent.
     * @param commitTransaction false if called by writeChanges as intent is
     * not to finalize the transaction.
     */
    protected void commitToDatabaseWithChangeSet(boolean commitTransaction) throws DatabaseException, OptimisticLockException {
        
        try {
            incrementProfile(SessionProfiler.UowCommits);
            startOperationProfile(SessionProfiler.UowCommit);
            // PERF: If this is an empty unit of work, do nothing (but still may need to commit SQL changes).
            boolean hasChanges = (this.unitOfWorkChangeSet != null) || hasCloneMapping() || hasDeletedObjects() || hasModifyAllQueries() || hasDeferredModifyAllQueries();
            if (hasChanges) {                
                try{
                    // The sequence numbers are assigned outside of the commit transaction.
                    // This improves concurrency, avoids deadlock and in the case of three-tier will
                    // not leave invalid cached sequences on rollback.
                    // Iterate over each clone and let the object build merge to clones into the originals.
                    // The change set may already exist if using change tracking.
                    if (this.unitOfWorkChangeSet == null) {
                        this.unitOfWorkChangeSet = new UnitOfWorkChangeSet(this);
                    }
                    // PERF: clone is faster than new.
                    calculateChanges(getCloneMapping(), this.unitOfWorkChangeSet, true, true);
        
                } catch (RuntimeException exception){
                    // The number of SQL statements been prepared need be stored into UOW 
                    // before any exception being thrown.
                    copyStatementsCountIntoProperties();
                    throw exception;
                }
                hasChanges = hasModifications();
            }

            // Bug 2834266 only commit to the database if changes were made, avoid begin/commit of transaction
            if (hasChanges) {
                // Also must first set the commit manager active.
                getCommitManager().setIsActive(true);
                commitToDatabase(commitTransaction);
            } else {
                try {
                    // CR#... need to commit the transaction if begun early.
                    if (wasTransactionBegunPrematurely()) {
                        if (commitTransaction) {
                            // Must be set to false for release to know not to rollback.
                            setWasTransactionBegunPrematurely(false);
                            setWasNonObjectLevelModifyQueryExecuted(false);
                            try {
                                commitTransaction();
                            } catch (RuntimeException commitFailed) {
                                try {
                                    rollbackTransaction();
                                } catch (RuntimeException ignore) {
                                    // Ignore
                                }
                                throw commitFailed;
                            } catch (Error error) {
                                try {
                                    rollbackTransaction();
                                } catch (RuntimeException ignore) {
                                    // Ignore
                                }
                                throw error;
                            }
                        }
                    }
                } catch (RuntimeException exception) {
                    // The number of SQL statements been prepared need be stored into UOW 
                    // before any exception being thrown.
                    copyStatementsCountIntoProperties();
                    throw exception;
                }
            }
        } catch (RuntimeException exception) {
            handleException(exception);
        } finally {
            endOperationProfile(SessionProfiler.UowCommit);            
        }
    }

    /**
     * INTERNAL:
     * Commit pre-built changeSet to the database changeSet to the database.
     */
    protected void commitToDatabaseWithPreBuiltChangeSet(UnitOfWorkChangeSet uowChangeSet, boolean commitTransaction, boolean isChangeSetFromOutsideUOW) throws DatabaseException, OptimisticLockException {
        try {
            uowChangeSet.setIsChangeSetFromOutsideUOW(isChangeSetFromOutsideUOW);
            // The sequence numbers are assigned outside of the commit transaction.
            // This improves concurrency, avoids deadlock and in the case of three-tier will
            // not leave invalid cached sequences on rollback.
            // Also must first set the commit manager active.
            getCommitManager().setIsActive(true);
            // Iterate over each clone and let the object build merge to clones into the originals.
            setUnitOfWorkChangeSet(uowChangeSet);
            commitToDatabase(commitTransaction);
            uowChangeSet.setIsChangeSetFromOutsideUOW(false);
        } catch (RuntimeException exception) {
            handleException(exception);
        }
    }

    /**
     * INTERNAL:
     * This is internal to the uow, transactions should not be used explicitly in a uow.
     * The uow shares its parents transactions.
     */
    public void commitTransaction() throws DatabaseException {
        this.parent.commitTransaction();
    }

    /**
     * INTERNAL:
     * After writeChanges() everything has been done except for committing
     * the transaction.  This allows that execution path to 'catch up'.
     */
    public void commitTransactionAfterWriteChanges() {
        setWasNonObjectLevelModifyQueryExecuted(false);
        if (hasModifications() || wasTransactionBegunPrematurely()) {
             try{
                //gf934: ensuring release doesn't cause an extra rollback call if acquireRequiredLocks throws an exception
                setWasTransactionBegunPrematurely(false);
                acquireWriteLocks();
                commitTransaction();
            } catch (RuntimeException exception) {
                releaseWriteLocks();
                try {
                    rollbackTransaction(false);
                } catch (RuntimeException ignore) {
                    // Ignore
                }
                setLifecycle(WriteChangesFailed);
                handleException(exception);
            } catch (Error throwable) {
                releaseWriteLocks();
                try {
                    rollbackTransaction();
                } catch (RuntimeException ignore) {
                    // Ignore
                }
                throw throwable;
            }
        }
    }
        
    /**
     * INTERNAL:
     * Acquire the unit of work cache write locks, if required.
     */
    protected void acquireWriteLocks() {
        // If everything is isolated, can bypass merge entirely.
        if (this.project.hasNonIsolatedUOWClasses() || (this.modifyAllQueries != null)) {
            // if we should be acquiring locks before commit let's do that here 
            if (getDatasourceLogin().shouldSynchronizeObjectLevelReadWriteDatabase() && (this.unitOfWorkChangeSet != null)) {
                writesCompleted();//flush Batch Statements
                this.lastUsedMergeManager = new MergeManager(this);
                //If we are merging into the shared cache acquire all required locks before merging.
                this.parent.getIdentityMapAccessorInstance().getWriteLockManager().acquireRequiredLocks(this.lastUsedMergeManager, this.unitOfWorkChangeSet);
            }
        }
    }

    /**
     * INTERNAL:
     * Release the unit of work cache write locks, if acquired.
     */
    public void releaseWriteLocks() {
        if (this.lastUsedMergeManager != null) {
            // 272022: If the current thread and the active thread on the mutex do not match - switch them
            verifyMutexThreadIntegrityBeforeRelease();
            // exception occurred during the commit.
            this.parent.getIdentityMapAccessorInstance().getWriteLockManager().releaseAllAcquiredLocks(this.lastUsedMergeManager);
            this.lastUsedMergeManager = null;
        }
    }
        
    /**
     * INTERNAL:
     * Copy the read only classes from the unit of work.
     */
    // Added Nov 8, 2000 JED for Patch 2.5.1.8, Ref: Prs 24502
    public Vector copyReadOnlyClasses() {
        return new Vector(getReadOnlyClasses());
    }

    /**
     * PUBLIC:
     * Merge the attributes of the clone into the unit of work copy.
     * This can be used for objects that are returned from the client through
     * RMI serialization or other serialization mechanisms, because the RMI object will
     * be a clone this will merge its attributes correctly to preserve object identity
     * within the unit of work and record its changes.
     * Everything connected to this object (i.e. the entire object tree where rmiClone
     * is the root) is also merged.
     *
     * @return the registered version for the clone being merged.
     * @see #mergeClone(Object)
     * @see #shallowMergeClone(Object)
     */
    public Object deepMergeClone(Object rmiClone) {
        return mergeClone(rmiClone, MergeManager.CASCADE_ALL_PARTS, false);
    }

    /**
     * PUBLIC:
     * Revert the object's attributes from the parent.
     * This reverts everything the object references.
     *
     * @return the object reverted.
     * @see #revertObject(Object)
     * @see #shallowRevertObject(Object)
     */
    public Object deepRevertObject(Object clone) {
        return revertObject(clone, MergeManager.CASCADE_ALL_PARTS);
    }

    /**
     * ADVANCED:
     * Unregister the object with the unit of work.
     * This can be used to delete an object that was just created and is not yet persistent.
     * Delete object can also be used, but will result in inserting the object and then deleting it.
     * The method should be used carefully because it will delete all the reachable parts.
     */
    public void deepUnregisterObject(Object clone) {
        unregisterObject(clone, DescriptorIterator.CascadeAllParts);
    }

    /**
     * INTERNAL:
     * Search for any objects in the parent that have not been registered.
     * These are required so that the nested unit of work does not add them to the parent
     * clone mapping on commit, causing possible incorrect insertions if they are dereferenced.
     */
    protected void discoverAllUnregisteredNewObjects() {
        // 2612538 - the default size of Map (32) is appropriate
        Map visitedNodes = new IdentityHashMap();
        Map newObjects = new IdentityHashMap();
        
         // Bug 294259 -  Do not replace the existingObjects list
        // Iterate over the clones.
        discoverUnregisteredNewObjects(new IdentityHashMap(getCloneMapping()), newObjects, getUnregisteredExistingObjects(), visitedNodes);
        setUnregisteredNewObjects(newObjects);

    }

    /**
     * INTERNAL:
     * Search for any objects in the parent that have not been registered.
     * These are required so that the nested unit of work does not add them to the parent
     * clone mapping on commit, causing possible incorrect insertions if they are dereferenced.
     */
    protected void discoverAllUnregisteredNewObjectsInParent() {
        // Iterate over the clones.
        if (this.isNestedUnitOfWork) {
            // 2612538 - the default size of Map (32) is appropriate
            Map visitedNodes = new IdentityHashMap();
            Map newObjects = new IdentityHashMap();
            UnitOfWorkImpl parent = (UnitOfWorkImpl)this.parent;
            parent.discoverUnregisteredNewObjects(((UnitOfWorkImpl)this.parent).getCloneMapping(), newObjects, new IdentityHashMap(), visitedNodes);
            setUnregisteredNewObjectsInParent(newObjects);
        }
    }

    /**
     * INTERNAL:
     * Traverse the object to find references to objects not registered in this unit of work.
     */
    public void discoverUnregisteredNewObjects(Map clones, final Map knownNewObjects, final Map unregisteredExistingObjects, Map visitedObjects) {
        // This define an inner class for process the iteration operation, don't be scared, its just an inner class.
        DescriptorIterator iterator = new DescriptorIterator() {
            public void iterate(Object object) {
                // If the object is read-only then do not continue the traversal.
                if (isClassReadOnly(object.getClass(), this.getCurrentDescriptor())) {
                    this.setShouldBreak(true);
                    return;
                }

                /* CR3440: Steven Vo
                 * Include the case that object is original then do nothing.
                 */
                if (isSmartMerge() && isOriginalNewObject(object)) {
                    return;
                } else if (!isObjectRegistered(object)) {// Don't need to check for aggregates, as iterator does not iterate on them by default.
                    if (shouldPerformNoValidation()) {
                        if (checkForUnregisteredExistingObject(object)) {
                            // If no validation is performed and the object exists we need
                            // To keep a record of this object to ignore it, also I need to
                            // Stop iterating over it.
                            unregisteredExistingObjects.put(object, object);
                            this.setShouldBreak(true);
                            return;
                        }
                    } else {
                        // This will validate that the object is not from the parent session, moved from calculate to optimize JPA.
                        getBackupClone(object, getCurrentDescriptor());
                    }
                    // This means it is a unregistered new object
                    knownNewObjects.put(object, object);
                }
            }
            
            public void iterateReferenceObjectForMapping(Object referenceObject, DatabaseMapping mapping) {
                super.iterateReferenceObjectForMapping(referenceObject, mapping);
                if (mapping.isCandidateForPrivateOwnedRemoval()) {
                    removePrivateOwnedObject(mapping, referenceObject);
                }
            }
        };
        // Bug 294259 -  Do not replace the existingObjects list
        
        iterator.setVisitedObjects(visitedObjects);
        iterator.setResult(knownNewObjects);
        iterator.setSession(this);
        // When using wrapper policy in EJB the iteration should stop on beans,
        // this is because EJB forces beans to be registered anyway and clone identity can be violated
        // and the violated clones references to session objects should not be traversed.
        iterator.setShouldIterateOverWrappedObjects(false);
        
        for (Iterator clonesEnum = clones.keySet().iterator(); clonesEnum.hasNext(); ) {        
            iterator.startIterationOn(clonesEnum.next());
        }
    }

    /**
     * ADVANCED:
     * The unit of work performs validations such as,
     * ensuring multiple copies of the same object don't exist in the same unit of work,
     * ensuring deleted objects are not referred after commit,
     * ensures that objects from the parent cache are not referred in the unit of work cache.
     * The level of validation can be increased or decreased for debugging purposes or under
     * advanced situation where the application requires/desires to violate clone identity in the unit of work.
     * It is strongly suggested that clone identity not be violate in the unit of work.
     */
    public void dontPerformValidation() {
        setValidationLevel(None);
    }
    
    /**
     * INTERNAL:
     * Override From session.  Get the accessor based on the query, and execute call,
     * this is here for session broker.
     */
    public Object executeCall(Call call, AbstractRecord translationRow, DatabaseQuery query) throws DatabaseException {
        Collection<Accessor> accessors = query.getSession().getAccessors(call, translationRow, query);
        query.setAccessors(accessors);
        try {
            return basicExecuteCall(call, translationRow, query);
        } finally {
            if (call.isFinished()) {
                query.setAccessor(null);
            }
        }
    }
    
    /**
     * ADVANCED:
     * Set optimistic read lock on the object.  This feature is override by normal optimistic lock.
     * when the object is changed in UnitOfWork. The cloneFromUOW must be the clone of from this
     * UnitOfWork and it must implements version locking or timestamp locking.
     * The SQL would look like the followings.
     *
     * If shouldModifyVersionField is true,
     * "UPDATE EMPLOYEE SET VERSION = 2 WHERE EMP_ID = 9 AND VERSION = 1"
     *
     * If shouldModifyVersionField is false,
     * "UPDATE EMPLOYEE SET VERSION = 1 WHERE EMP_ID = 9 AND VERSION = 1"
     */
    public void forceUpdateToVersionField(Object lockObject, boolean shouldModifyVersionField) {
        ClassDescriptor descriptor = getDescriptor(lockObject);
        if (descriptor == null) {
            throw DescriptorException.missingDescriptor(lockObject.getClass().toString());
        }
        getOptimisticReadLockObjects().put(descriptor.getObjectBuilder().unwrapObject(lockObject, this), Boolean.valueOf(shouldModifyVersionField));
    }

    /**
     * INTERNAL:
     * The uow does not store a local accessor but shares its parents.
     */
    @Override
    public Accessor getAccessor() {
        return this.parent.getAccessor();
    }

    /**
     * INTERNAL:
     * The uow does not store a local accessor but shares its parents.
     */
    @Override
    public Collection<Accessor> getAccessors() {
        return this.parent.getAccessors();
    }

    /**
     * INTERNAL:
     * The commit manager is used to resolve referential integrity on commits of multiple objects.
     * The commit manage is lazy init from parent.
     */
    public CommitManager getCommitManager() {
        // PERF: lazy init, not always required for release/commit with no changes.
        if (this.commitManager == null) {
            this.commitManager = new CommitManager(this);
            // Initialize the commit manager
            this.commitManager.setCommitOrder(this.parent.getCommitManager().getCommitOrder());
        }
        return this.commitManager;
    }
    
    /**
     * INTERNAL:
     * Return the connections to use for the query execution.
     */
    public Collection<Accessor> getAccessors(Call call, AbstractRecord translationRow, DatabaseQuery query) {
        return this.parent.getAccessors(call, translationRow, query);
    }

    /**
     * PUBLIC:
     * Return the active unit of work for the current active external (JTS) transaction.
     * This should only be used with JTS and will return null if no external transaction exists.
     */
    public org.eclipse.persistence.sessions.UnitOfWork getActiveUnitOfWork() {

        /* Steven Vo:  CR# 2517
           This fixed the problem of returning null when this method is called on a UOW.
           UOW does not copy the parent session's external transaction controller
           when it is acquired but session does  */
        return this.parent.getActiveUnitOfWork();
    }

    /**
     * INTERNAL:
     * Return any new objects matching the expression.
     * Used for in-memory querying.
     */
    public Vector getAllFromNewObjects(Expression selectionCriteria, Class theClass, AbstractRecord translationRow, int valueHolderPolicy) {
        // PERF: Avoid initialization of new objects if none.
        if (!hasNewObjects()) {
            return new Vector(1);
        }
        
        // bug 327900 - If don't read subclasses is set on the descriptor heed it.
        ClassDescriptor descriptor = getDescriptor(theClass);
        boolean readSubclassesOrNoInheritance = (!descriptor.hasInheritance() || descriptor.getInheritancePolicy().shouldReadSubclasses());

        Vector objects = new Vector();
        for (Iterator newObjectsEnum = getNewObjectsCloneToOriginal().keySet().iterator();
                 newObjectsEnum.hasNext();) {
            Object object = newObjectsEnum.next();
            // bug 327900
            if ((object.getClass() == theClass) || (readSubclassesOrNoInheritance && (theClass.isInstance(object)))) {
                if (selectionCriteria == null) {
                    objects.addElement(object);
                } else if (selectionCriteria.doesConform(object, this, translationRow, valueHolderPolicy)) {
                    objects.addElement(object);
                }
            }
        }
        return objects;
    }

    /**
     * INTERNAL:
     * Return the backup clone for the working clone.
     */
    public Object getBackupClone(Object clone) throws QueryException {
        return getBackupClone(clone, null);
    }
    
    /**
     * INTERNAL:
     * Return the backup clone for the working clone.
     */
    public Object getBackupClone(Object clone, ClassDescriptor descriptor) throws QueryException {
        Object backupClone = getCloneMapping().get(clone);
        if (backupClone != null) {
            return backupClone;
        }

        /* CR3440: Steven Vo
         * Smart merge if necessary in isObjectRegistered()
         */
        if (isObjectRegistered(clone)) {
            return getCloneMapping().get(clone);

        } else {
            if(descriptor == null) {
                descriptor = getDescriptor(clone);
            }
            Object primaryKey = keyFromObject(clone, descriptor);

            // This happens if clone was from the parent identity map.		
            if (this.getParentIdentityMapSession(descriptor, false, true).getIdentityMapAccessorInstance().containsObjectInIdentityMap(primaryKey, clone.getClass(), descriptor)) {
                //cr 3796
                if ((getUnregisteredNewObjects().get(clone) != null) && isMergePending()) {
                    //Another thread has read the new object before it has had a chance to
                    //merge this object.
                    // It also means it is an unregistered new object, so create a new backup clone for it.
                    return descriptor.getObjectBuilder().buildNewInstance();
                }
                if (hasObjectsDeletedDuringCommit() && getObjectsDeletedDuringCommit().containsKey(clone)) {
                    throw QueryException.backupCloneIsDeleted(clone);
                }
                throw QueryException.backupCloneIsOriginalFromParent(clone);
            }
            // Also check that the object is not the original to a registered new object
            // (the original should not be referenced if not smart merge, this is an error.	
            else if (hasNewObjects() && getNewObjectsOriginalToClone().containsKey(clone)) {

                /* CR3440: Steven Vo
                 * Check case that clone is original
                 */
                if (isSmartMerge()) {
                    backupClone = getCloneMapping().get(getNewObjectsOriginalToClone().get(clone));

                } else {
                    throw QueryException.backupCloneIsOriginalFromSelf(clone);
                }
            } else {
                // This means it is an unregistered new object, so create a new backup clone for it.
                backupClone = descriptor.getObjectBuilder().buildNewInstance();
            }
        }

        return backupClone;
    }

    /**
     * INTERNAL:
     * Return the backup clone for the working clone.
     */
    public Object getBackupCloneForCommit(Object clone, ClassDescriptor descriptor) {
        Object backupClone = getBackupClone(clone, descriptor);

        /* CR3440: Steven Vo
         * Build new instance only if it was not handled by getBackupClone()
         */
        if (isCloneNewObject(clone)) {
            if(descriptor != null) {
                return descriptor.getObjectBuilder().buildNewInstance();
            } else {
                // Can this ever happen?
                return getDescriptor(clone).getObjectBuilder().buildNewInstance();
            }
        }

        return backupClone;
    }

    /**
     * INTERNAL:
     * Return the backup clone for the working clone.
     */
    public Object getBackupCloneForCommit(Object clone) {
        Object backupClone = getBackupClone(clone);

        /* CR3440: Steven Vo
         * Build new instance only if it was not handled by getBackupClone()
         */
        if (isCloneNewObject(clone)) {
            return getDescriptor(clone).getObjectBuilder().buildNewInstance();
        }

        return backupClone;
    }


    /**
     * ADVANCED:
     * This method Will Calculate the changes for the UnitOfWork.  Without assigning sequence numbers
     * This is a computationally intensive operation and should be avoided unless necessary.
     * A valid changeSet, with sequencenumbers can be collected from the UnitOfWork after the commit
     * is complete by calling unitOfWork.getUnitOfWorkChangeSet()
     */
    public org.eclipse.persistence.sessions.changesets.UnitOfWorkChangeSet getCurrentChanges() {
        Map allObjects = collectAndPrepareObjectsForNestedMerge();
        return calculateChanges(allObjects, new UnitOfWorkChangeSet(this), false, false);
    }

    /**
     * INTERNAL:
     * Returns the appropriate IdentityMap session for this descriptor.  Sessions can be 
     * chained and each session can have its own Cache/IdentityMap.  Entities can be stored
     * at different levels based on Cache Isolation.  This method will return the correct Session
     * for a particular Entity class based on the Isolation Level and the attributes provided.
     * <p>
     * @param canReturnSelf true when method calls itself.  If the path
     * starting at <code>this</code> is acceptable.  Sometimes true if want to
     * move to the first valid session, i.e. executing on ClientSession when really
     * should be on ServerSession.
     * @param terminalOnly return the last session in the chain where the Enitity is stored.
     * @return Session with the required IdentityMap
     */
    public AbstractSession getParentIdentityMapSession(ClassDescriptor descriptor, boolean canReturnSelf, boolean terminalOnly) {
        if (canReturnSelf && !terminalOnly) {
            return this;
        } else {
            return this.parent.getParentIdentityMapSession(descriptor, true, terminalOnly);
        }
    }

    /**
     * INTERNAL:
     * Gets the session which this query will be executed on.
     * Generally will be called immediately before the call is translated,
     * which is immediately before session.executeCall.
     * <p>
     * Since the execution session also knows the correct datasource platform
     * to execute on, it is often used in the mappings where the platform is
     * needed for type conversion, or where calls are translated.
     * <p>
     * Is also the session with the accessor.  Will return a ClientSession if
     * it is in transaction and has a write connection.
     * @return a session with a live accessor
     * @param query may store session name or reference class for brokers case
     */
    public AbstractSession getExecutionSession(DatabaseQuery query) {
        // This optimization is only for when executing with a ClientSession in
        // transaction.  In that case log with the UnitOfWork instead of the
        // ClientSession.
        // Note that if actually executing on ServerSession or a registered
        // session of a broker, must execute on that session directly.
    
        //bug 5201121 Always use the parent or execution session from the parent
        // should never use the unit of work as it does not control the
        //accessors and with a session broker it will not have the correct
        //login info
        return this.parent.getExecutionSession(query);
    }

    /**
     * INTERNAL:
     * Return the clone mapping.
     * The clone mapping contains clone of all registered objects,
     * this is required to store the original state of the objects when registered
     * so that only what is changed will be committed to the database and the parent,
     * (this is required to support parallel unit of work).
     */
    public Map getCloneMapping() {
        // PERF: lazy-init (3286089)
        if (cloneMapping == null) {
            // 2612538 - the default size of Map (32) is appropriate
            cloneMapping = createMap();
        }
        return cloneMapping;
    }

    /**
     * INTERNAL:
     * Return if the unit of work has any clones.
     */
    public boolean hasCloneMapping() {
        return ((cloneMapping != null) && !cloneMapping.isEmpty());
    }

    /**
     * INTERNAL:
     * Map used to avoid garbage collection in weak caches.
     * Also, map used as lookup when originals used for merge when original in
     * identitymap can not be found.  As in a CacheIdentityMap
     */
    public Map getCloneToOriginals() {
        if (cloneToOriginals == null) {// Must lazy initialize for remote.
            // 2612538 - the default size of Map (32) is appropriate
            cloneToOriginals = createMap();
        }
        return cloneToOriginals;
    }

    protected boolean hasCloneToOriginals() {
        return ((cloneToOriginals != null) && !cloneToOriginals.isEmpty());
    }

    /**
     * INTERNAL:
     * This is only used for EJB entity beans to manage beans accessed in a transaction context.
     */
    public Map getContainerBeans() {
        if (containerBeans == null) {
            containerBeans = new IdentityHashMap();
        }
        return containerBeans;
    }

    /**
     * INTERNAL:
     * Return if any container beans exist.
     * PERF: Used to avoid lazy initialization of getContainerBeans().
     */
    public boolean hasContainerBeans() {
        return ((containerBeans != null) && !containerBeans.isEmpty());
    }

    /**
     * INTERNAL:
     * Return any objects have been deleted through database cascade delete constraints.
     */
    public Set<Object> getCascadeDeleteObjects() {
        if (this.cascadeDeleteObjects == null) {
            this.cascadeDeleteObjects = new IdentityHashSet();
        }
        return this.cascadeDeleteObjects;
    }

    /**
     * INTERNAL:
     * Set any objects have been deleted through database cascade delete constraints.
     */
    protected void setCascadeDeleteObjects(Set<Object> cascadeDeleteObjects) {
        this.cascadeDeleteObjects = cascadeDeleteObjects;
    }

    /**
     * INTERNAL:
     * Return if any objects have been deleted through database cascade delete constraints.
     */
    public boolean hasCascadeDeleteObjects() {
        return ((this.cascadeDeleteObjects != null) && !this.cascadeDeleteObjects.isEmpty());
    }
    
    /**
     * INTERNAL:
     * Return if there are any unregistered new objects.
     * PERF: Used to avoid initialization of new objects map unless required.
     */
    public boolean hasUnregisteredNewObjects() {
        return ((this.unregisteredNewObjects != null) && !this.unregisteredNewObjects.isEmpty());
    }
    
    /**
     * INTERNAL:
     * Return if there are any registered new objects.
     * This is used for both newObjectsOriginalToClone and newObjectsCloneToOriginal as they are always in synch.
     * PERF: Used to avoid initialization of new objects map unless required.
     */
    public boolean hasNewObjects() {
        return ((newObjectsCloneToOriginal != null) && !newObjectsCloneToOriginal.isEmpty());
    }

    /**
     * INTERNAL:
     * This is only used for EJB entity beans to manage beans accessed in a transaction context.
     */
    public UnitOfWorkImpl getContainerUnitOfWork() {
        if (containerUnitOfWork == null) {
            containerUnitOfWork = this.parent.acquireNonSynchronizedUnitOfWork(ReferenceMode.WEAK);
        }
        return containerUnitOfWork;
    }

    /**
     * INTERNAL: Returns the set of read-only classes that gets assigned to each newly created UnitOfWork.
     *
     * @see org.eclipse.persistence.sessions.Project#setDefaultReadOnlyClasses(Vector)
     */
    public Vector getDefaultReadOnlyClasses() {
        return this.parent.getDefaultReadOnlyClasses();
    }

    /**
     * INTERNAL:
     * The deleted objects stores any objects removed during the unit of work.
     * On commit they will all be removed from the database.
     */
    public Map getDeletedObjects() {
        if (deletedObjects == null) {
            // 2612538 - the default size of Map (32) is appropriate
            deletedObjects = new IdentityHashMap();
        }
        return deletedObjects;
    }

    /**
     * INTERNAL:
     * The deleted objects stores any objects removed during the unit of work.
     * On commit they will all be removed from the database.
     */
    public boolean hasDeletedObjects() {
        return ((deletedObjects != null) && !deletedObjects.isEmpty());
    }

    /**
     * INTERNAL:
     * The life cycle tracks if the unit of work is active and is used for JTS.
     */
    public int getLifecycle() {
        return lifecycle;
    }

    /**
     * A reference to the last used merge manager.  This is used to track locked
     * objects.
     */
    public MergeManager getMergeManager() {
        return this.lastUsedMergeManager;
    }

    /**
     * INTERNAL:
     * The map stores any new aggregates that have been cloned.
     */
    public Map getNewAggregates() {
        if (this.newAggregates == null) {
            // 2612538 - the default size of Map (32) is appropriate
            this.newAggregates = new IdentityHashMap();
        }
        return newAggregates;
    }

    /**
     * INTERNAL:
     * The new objects stores any objects newly created during the unit of work.
     * On commit they will all be inserted into the database.
     */
    public Map getNewObjectsCloneToOriginal() {
        if (newObjectsCloneToOriginal == null) {
            // 2612538 - the default size of Map (32) is appropriate
            newObjectsCloneToOriginal = new IdentityHashMap();
        }
        return newObjectsCloneToOriginal;
    }

    /**
     * INTERNAL:
     * The stores a map from new object clones to the original object used from merge.
     */
    public Map getNewObjectsCloneToMergeOriginal() {
        if (newObjectsCloneToMergeOriginal == null) {
            newObjectsCloneToMergeOriginal = new IdentityHashMap();
        }
        return newObjectsCloneToMergeOriginal;
    }
    
    /**
     * INTERNAL:
     * The returns the list that will hold the new objects from the Parent UnitOfWork
     */
    public Map getNewObjectsInParentOriginalToClone() {
        // PERF: lazy-init (3286089)
        if (newObjectsInParentOriginalToClone == null) {
            // 2612538 - the default size of Map (32) is appropriate
            newObjectsInParentOriginalToClone = new IdentityHashMap();
        }
        return newObjectsInParentOriginalToClone;
    }

    protected boolean hasNewObjectsInParentOriginalToClone() {
        return ((newObjectsInParentOriginalToClone != null) && !newObjectsInParentOriginalToClone.isEmpty());
    }
    
    /**
     * INTERNAL:
     * Return the privateOwnedRelationships attribute.
     */
    private Map<DatabaseMapping, Set> getPrivateOwnedObjects() {
        if (privateOwnedObjects == null) {
            privateOwnedObjects = new IdentityHashMap<DatabaseMapping, Set>();
        }
        return privateOwnedObjects;
    }

    /**
     * INTERNAL:
     * Return true if privateOwnedObjects is not null and not empty, false otherwise.
     */
    public boolean hasPrivateOwnedObjects() {
        return privateOwnedObjects != null && !privateOwnedObjects.isEmpty();
    }
    
    /**
     * INTERNAL:
     * Return if there are any optimistic read locks.
     */
    public boolean hasOptimisticReadLockObjects() {
        return ((optimisticReadLockObjects != null) && !optimisticReadLockObjects.isEmpty());
    }

    /**
     * INTERNAL:
     * The new objects stores any objects newly created during the unit of work.
     * On commit they will all be inserted into the database.
     */
    public Map getNewObjectsOriginalToClone() {
        if (newObjectsOriginalToClone == null) {
            // 2612538 - the default size of Map (32) is appropriate
            newObjectsOriginalToClone = new IdentityHashMap();
        }
        return newObjectsOriginalToClone;
    }

    /**
     * INTERNAL:
     * Return the Sequencing object used by the session.
     */
    public Sequencing getSequencing() {
        return this.parent.getSequencing();
    }

    /**
     * INTERNAL:
     * Marked internal as this is not customer API but helper methods for
     * accessing the server platform from within EclipseLink's other sessions types
     * (i.e. not DatabaseSession)
     */
    public ServerPlatform getServerPlatform() {
        return this.parent.getServerPlatform();
    }

    /**
     * INTERNAL:
     * Returns the type of session, its class.
     * <p>
     * Override to hide from the user when they are using an internal subclass
     * of a known class.
     * <p>
     * A user does not need to know that their UnitOfWork is a
     * non-deferred UnitOfWork, or that their ClientSession is an
     * IsolatedClientSession.
     */
    public String getSessionTypeString() {
        return "UnitOfWork";
    }

    /**
     * INTERNAL:
     * Called after external transaction rolled back.
     */
    public void afterExternalTransactionRollback() {
        // In case jts transaction was internally started but rolled back
        // directly by TransactionManager this flag may still be true during afterCompletion
        this.parent.setWasJTSTransactionInternallyStarted(false);
        //bug#4699614 -- added a new life cycle status so we know if the external transaction was rolledback and we don't try to rollback again later            
        setLifecycle(AfterExternalTransactionRolledBack);

        if ((getMergeManager() != null) && (getMergeManager().getAcquiredLocks() != null) && (!getMergeManager().getAcquiredLocks().isEmpty())) {
            // 272022: If the current thread and the active thread on the mutex do not match - switch them            
            verifyMutexThreadIntegrityBeforeRelease();
            //may have unreleased cache locks because of a rollback...
            this.parent.getIdentityMapAccessorInstance().getWriteLockManager().releaseAllAcquiredLocks(getMergeManager());
            this.setMergeManager(null);
        }
    }

    /**
     * INTERNAL:
     * Called in the end of beforeCompletion of external transaction synchronization listener.
     * Close the managed sql connection corresponding to the external transaction.
     */
    public void releaseJTSConnection() {
        this.parent.releaseJTSConnection();
    }

    /**
     * INTERNAL:
     * Return any new object matching the expression.
     * Used for in-memory querying.
     */
    public Object getObjectFromNewObjects(Class theClass, Object selectionKey) {
        // PERF: Avoid initialization of new objects if none.
        if (!hasNewObjects()) {
            return null;
        }
        
        // bug 327900 - If don't read subclasses is set on the descriptor heed it.
        ClassDescriptor descriptor = getDescriptor(theClass);
        boolean readSubclassesOrNoInheritance = (!descriptor.hasInheritance() || descriptor.getInheritancePolicy().shouldReadSubclasses());
        
        ObjectBuilder objectBuilder = descriptor.getObjectBuilder();
        for (Iterator newObjectsEnum = getNewObjectsCloneToOriginal().keySet().iterator();
                 newObjectsEnum.hasNext();) {
            Object object = newObjectsEnum.next();
            // bug 327900
            if ((object.getClass() == theClass) || (readSubclassesOrNoInheritance && (theClass.isInstance(object)))) {
                Object primaryKey = objectBuilder.extractPrimaryKeyFromObject(object, this, true);
                if ((primaryKey != null) && primaryKey.equals(selectionKey)) {
                    return object;
                }
            }
        }
        return null;
    }

    /**
     * INTERNAL:
     * Return any new object matching the expression.
     * Used for in-memory querying.
     */
    public Object getObjectFromNewObjects(Expression selectionCriteria, Class theClass, AbstractRecord translationRow, int valueHolderPolicy) {
        // PERF: Avoid initialization of new objects if none.
        if (!hasNewObjects()) {
            return null;
        }
        
        // bug 327900 - If don't read subclasses is set on the descriptor heed it.
        ClassDescriptor descriptor = getDescriptor(theClass);
        boolean readSubclassesOrNoInheritance = (!descriptor.hasInheritance() || descriptor.getInheritancePolicy().shouldReadSubclasses());
        
        for (Object object : getNewObjectsCloneToOriginal().keySet()) {
            // bug 327900
            if ((object.getClass() == theClass) || (readSubclassesOrNoInheritance && (theClass.isInstance(object)))) {
                if (selectionCriteria == null) {
                    return object;
                }
                if (selectionCriteria.doesConform(object, this, translationRow, valueHolderPolicy)) {
                    return object;
                }
            }
        }
        return null;
    }

    /**
     * INTERNAL:
     * Returns all the objects which are deleted during root commit of unit of work.
     */
    public Map getObjectsDeletedDuringCommit() {
        // PERF: lazy-init (3286089)
        if (objectsDeletedDuringCommit == null) {
            // 2612538 - the default size of Map (32) is appropriate
            objectsDeletedDuringCommit = new IdentityHashMap();
        }
        return objectsDeletedDuringCommit;
    }

    protected boolean hasObjectsDeletedDuringCommit() {
        return ((objectsDeletedDuringCommit != null) && !objectsDeletedDuringCommit.isEmpty());
    }

    /**
     * INTERNAL:
     * Return optimistic read lock objects
     */
    public Map getOptimisticReadLockObjects() {
        if (this.optimisticReadLockObjects == null) {
            this.optimisticReadLockObjects = new HashMap(2);
        }
        return this.optimisticReadLockObjects;
    }

    /**
     * INTERNAL:
     * Return the original version of the new object (working clone).
     */
    public Object getOriginalVersionOfNewObject(Object workingClone) {
        // PERF: Avoid initialization of new objects if none.
        if (!hasNewObjects()) {
            return null;
        }
        return getNewObjectsCloneToOriginal().get(workingClone);
    }

    /**
     * ADVANCED:
     * Return the original version of the object(clone) from the parent's identity map.
     */
    public Object getOriginalVersionOfObject(Object workingClone) {
        // Can be null when called from the mappings.
        if (workingClone == null) {
            return null;
        }
        Object original = null;
        ClassDescriptor descriptor = getDescriptor(workingClone);
        ObjectBuilder builder = descriptor.getObjectBuilder();
        Object implementation = builder.unwrapObject(workingClone, this);
        CacheKey cacheKey = getParentIdentityMapSession(descriptor, false, false).getCacheKeyFromTargetSessionForMerge(implementation, builder, descriptor, lastUsedMergeManager);
        if (cacheKey != null){
            original = cacheKey.getObject();
        }
        if (original == null) {
            // Check if it is a registered new object.
            original = getOriginalVersionOfNewObject(implementation);
        }

        if (original == null) {
            // For bug 3013948 looking in the cloneToOriginals mapping will not help
            // if the object was never registered.
            if (isClassReadOnly(implementation.getClass(), descriptor)) {
                return implementation;
            }

            // The object could have been removed from the cache even though it was in the unit of work.
            // fix for 2.5.1.3 PWK (1360)
            if (hasCloneToOriginals()) {
                original = getCloneToOriginals().get(workingClone);
            }
        }

        if (original == null) {
            // This means that it must be an unregistered new object, so register a new clone as its original.
            original = buildOriginal(implementation);
        }

        return original;
    }
    
    /**
     * INTERNAL:
     * Return the original version of the object(clone) from the parent's identity map.
     * PERF: Use the change set to avoid cache lookups.
     */
    public Object getOriginalVersionOfObjectOrNull(Object workingClone, ObjectChangeSet changeSet, ClassDescriptor descriptor, AbstractSession targetSession) {
        // Can be null when called from the mappings.
        if (workingClone == null) {
            return null;
        }

        ObjectBuilder builder = descriptor.getObjectBuilder();
        Object implementation = builder.unwrapObject(workingClone, this);
        Object original = getOriginalVersionOfNewObject(implementation);

        if (original == null) {
            // For bug 3013948 looking in the cloneToOriginals mapping will not help
            // if the object was never registered.
            if (isClassReadOnly(implementation.getClass(), descriptor)) {
                return implementation;
            }

            // The object could have been removed from the cache even though it was in the unit of work.
            // fix for 2.5.1.3 PWK (1360)
            if (hasCloneToOriginals()) {
                original = getCloneToOriginals().get(workingClone);
            }
        }
        return original;
    }
    
    /**
     * INTERNAL:
     * Return the original version of the object(clone) from the parent's identity map.
     */
    public Object getOriginalVersionOfObjectOrNull(Object workingClone, ClassDescriptor descriptor) {
        // Can be null when called from the mappings.
        if (workingClone == null) {
            return null;
        }
        ObjectBuilder builder = descriptor.getObjectBuilder();
        Object implementation = builder.unwrapObject(workingClone, this);

        Object primaryKey = builder.extractPrimaryKeyFromObject(implementation, this);
        // there's no need to elaborately avoid the readlock like the other getOriginalVersionOfObjectOrNull
        // method as this one is not used during the commit cycle
        Object original = this.parent.getIdentityMapAccessorInstance().getFromIdentityMap(primaryKey, implementation.getClass(), descriptor);

        if (original == null) {
            // Check if it is a registered new object.
            original = getOriginalVersionOfNewObject(implementation);
        }

        if (original == null) {
            // For bug 3013948 looking in the cloneToOriginals mapping will not help
            // if the object was never registered.
            if (isClassReadOnly(implementation.getClass(), descriptor)) {
                return implementation;
            }

            // The object could have been removed from the cache even though it was in the unit of work.
            // fix for 2.5.1.3 PWK (1360)
            if (hasCloneToOriginals()) {
                original = getCloneToOriginals().get(workingClone);
            }
        }
        return original;
    }

    /**
     * PUBLIC:
     * Return the parent.
     * This is a unit of work if nested, otherwise a database session or client session.
     */
    public AbstractSession getParent() {
        return parent;
    }

    /**
     * INTERNAL:
     * Search for and return the user defined property from this UOW, if it not found then search for the property
     * from parent.
     */
    public Object getProperty(String name){
        Object propertyValue = super.getProperties().get(name);
        if (propertyValue == null) {
           propertyValue = this.parent.getProperty(name);
        }
        return propertyValue;
    }
    
    /**
     * INTERNAL:
     * Return the platform for a particular class.
     */
    public Platform getPlatform(Class domainClass) {
        return this.parent.getPlatform(domainClass);
    }
    
    /**
     * INTERNAL:
     * Return whether to throw exceptions on conforming queries
     */
    public int getShouldThrowConformExceptions() {
        return shouldThrowConformExceptions;
    }

    /**
     * PUBLIC:
     * Return the query from the session pre-defined queries with the given name.
     * This allows for common queries to be pre-defined, reused and executed by name.
     */
    public DatabaseQuery getQuery(String name, Vector arguments) {
        DatabaseQuery query = super.getQuery(name, arguments);
        if (query == null) {
            query = this.parent.getQuery(name, arguments);
        }

        return query;
    }

    /**
     * PUBLIC:
     * Return the query from the session pre-defined queries with the given name.
     * This allows for common queries to be pre-defined, reused and executed by name.
     */
    public DatabaseQuery getQuery(String name) {
        DatabaseQuery query = super.getQuery(name);
        if (query == null) {
            query = this.parent.getQuery(name);
        }

        return query;
    }

    /**
     * ADVANCED:
     * Returns the set of read-only classes in this UnitOfWork.
     */
    public Set getReadOnlyClasses() {
        if (this.readOnlyClasses == null) {
            this.readOnlyClasses = new HashSet();
        }
        return this.readOnlyClasses;
    }

    /**
     * INTERNAL:
     * The removed objects stores any newly registered objects removed during the nested unit of work.
     * On commit they will all be removed from the parent unit of work.
     */
    protected Map getRemovedObjects() {
        // PERF: lazy-init (3286089)
        if (removedObjects == null) {
            // 2612538 - the default size of Map (32) is appropriate
            removedObjects = new IdentityHashMap();
        }
        return removedObjects;
    }

    protected boolean hasRemovedObjects() {
        return ((removedObjects != null) && !removedObjects.isEmpty());
    }

    protected boolean hasModifyAllQueries() {
        return ((modifyAllQueries != null) && !modifyAllQueries.isEmpty());
    }

    protected boolean hasDeferredModifyAllQueries() {
        return ((deferredModifyAllQueries != null) && !deferredModifyAllQueries.isEmpty());
    }

    /**
     * INTERNAL:
     * Find out what the lifecycle state of this UoW is in.
     */
    public int getState() {
        return lifecycle;
    }
    
    /**
     * INTERNAL:
     * PERF: Return the associated external transaction.
     * Used to optimize activeUnitOfWork lookup.
     */
    public Object getTransaction() {
        return transaction;
    }
    
    /**
     * INTERNAL:
     * PERF: Set the associated external transaction.
     * Used to optimize activeUnitOfWork lookup.
     */
    public void setTransaction(Object transaction) {
        this.transaction = transaction;
    }

    /**
     * ADVANCED:
     * Returns the currentChangeSet from the UnitOfWork.
     * This is only valid after the UnitOfWOrk has committed successfully
     */
    public org.eclipse.persistence.sessions.changesets.UnitOfWorkChangeSet getUnitOfWorkChangeSet() {
        return unitOfWorkChangeSet;
    }

    /**
     * INTERNAL:
     * Used to lazy Initialize the unregistered existing Objects collection.
     * @return Map
     */
    public Map getUnregisteredExistingObjects() {
        if (this.unregisteredExistingObjects == null) {
            // 2612538 - the default size of Map (32) is appropriate
            this.unregisteredExistingObjects = new IdentityHashMap();
        }
        return unregisteredExistingObjects;
    }

    /**
     * INTERNAL:
     * This is used to store unregistered objects discovered in the parent so that the child
     * unit of work knows not to register them on commit.
     */
    protected Map getUnregisteredNewObjects() {
        if (unregisteredNewObjects == null) {
            // 2612538 - the default size of Map (32) is appropriate
            unregisteredNewObjects = new IdentityHashMap();
        }
        return unregisteredNewObjects;
    }

    /**
     * INTERNAL:
     * This is used to store unregistered objects discovered in the parent so that the child
     * unit of work knows not to register them on commit.
     */
    protected Map getUnregisteredNewObjectsInParent() {
        if (unregisteredNewObjectsInParent == null) {
            // 2612538 - the default size of Map (32) is appropriate
            unregisteredNewObjectsInParent = new IdentityHashMap();
        }
        return unregisteredNewObjectsInParent;
    }

    /**
     * ADVANCED:
     * The unit of work performs validations such as,
     * ensuring multiple copies of the same object don't exist in the same unit of work,
     * ensuring deleted objects are not referred after commit,
     * ensures that objects from the parent cache are not referred in the unit of work cache.
     * The level of validation can be increased or decreased for debugging purposes or under
     * advanced situation where the application requires/desires to violate clone identity in the unit of work.
     * It is strongly suggested that clone identity not be violate in the unit of work.
     */
    public int getValidationLevel() {
        return validationLevel;
    }

    /**
     * ADVANCED:
     * The Unit of work is capable of preprocessing to determine if any on the clone have been changed.
     * This is computationally expensive and should be avoided on large object graphs.
     */
    public boolean hasChanges() {
        if (hasNewObjects()) {
            return true;
        }
        if (hasDeletedObjects()) {
            return true;
        }
        Map allObjects = new IdentityHashMap(getCloneMapping());
        UnitOfWorkChangeSet changeSet = calculateChanges(allObjects, new UnitOfWorkChangeSet(this), false, false);
        return changeSet.hasChanges();
    }

    /**
     * INTERNAL:
     * Does this unit of work have any changes or anything that requires a write
     * to the database and a transaction to be started.
     * Should be called after changes are calculated internally by commit.
     * <p>
     * Note if a transaction was begun prematurely it still needs to be committed.
     */
    protected boolean hasModifications() {
        if (((this.unitOfWorkChangeSet != null) && (this.unitOfWorkChangeSet.hasChanges() || ((org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet)getUnitOfWorkChangeSet()).hasForcedChanges()))
                || hasDeletedObjects() || hasModifyAllQueries() || hasDeferredModifyAllQueries()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * INTERNAL:
     * Set up the IdentityMapManager.  This method allows subclasses of Session to override
     * the default IdentityMapManager functionality.
     */
    public void initializeIdentityMapAccessor() {
        this.identityMapAccessor = new UnitOfWorkIdentityMapAccessor(this, new IdentityMapManager(this));
    }

    /**
     * INTERNAL:
     * Return the results from executing the database query.
     * the arguments should be a database row with raw data values.
     */
    public Object internalExecuteQuery(DatabaseQuery query, AbstractRecord databaseRow) throws DatabaseException, QueryException {
        Object result = query.executeInUnitOfWork(this, databaseRow);
        executeDeferredEvents();
        return result;
    }

    /**
     * INTERNAL:
     * Register the object with the unit of work.
     * This does not perform wrapping or unwrapping.
     * This is used for internal registration in the merge manager.
     */
    public Object internalRegisterObject(Object object, ClassDescriptor descriptor, boolean isShallowClone) {
        if (object == null) {
            return null;
        }
        if (descriptor.isDescriptorTypeAggregate()) {
            throw ValidationException.cannotRegisterAggregateObjectInUnitOfWork(object.getClass());
        }
        Object registeredObject = checkIfAlreadyRegistered(object, descriptor);
        if (registeredObject == null) {
            // Nested units of work are special because the parent can be used to determine if the object exists
            // in most case and new object may be in the cache in the parent.
            if (this.isNestedUnitOfWork) {
                UnitOfWorkImpl parentUnitOfWork = (UnitOfWorkImpl)this.parent;

                // If it is not registered in the parent we must go through the existence check.
                if (parentUnitOfWork.isObjectRegistered(object) || isUnregisteredNewObjectInParent(object)) {
                    Object primaryKey = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(object, this);
                    if (this.isCloneNewObjectFromParent(object) || isUnregisteredNewObjectInParent(object)) {
                        // Since it is a new object a new cache-key can be used for both parent and child as not put into the cache.
                        registeredObject = cloneAndRegisterObject(object, new CacheKey(primaryKey), new CacheKey(primaryKey), descriptor);
                    } else {
                        registeredObject = getIdentityMapAccessorInstance().getFromIdentityMap(primaryKey, descriptor.getJavaClass(), descriptor);
                    }
                    return registeredObject;
                }
            }
            registeredObject = checkExistence(object);
            if (registeredObject == null) {
                // This means that the object is not in the parent im, so was created under this unit of work.
                // This means that it must be new.
                registeredObject = cloneAndRegisterNewObject(object, isShallowClone);
                if (mergeManagerForActiveMerge != null) {
                    mergeManagerForActiveMerge.getMergedNewObjects().put(registeredObject, registeredObject);
                }
            }
        }

        return registeredObject;
    }

    /**
     * PUBLIC:
     * Return if the unit of work is active. (i.e. has not been released).
     */
    public boolean isActive() {
        return this.lifecycle != Death;
    }

    /**
     * INTERNAL:
     * Checks to see if the specified class or descriptor is read-only or not in this UnitOfWork.
     * @return boolean, true if the class is read-only, false otherwise.
     */
    public boolean isClassReadOnly(Class theClass, ClassDescriptor descriptor) {
        if ((descriptor != null) && (descriptor.shouldBeReadOnly())) {
            return true;
        }
        if ((theClass != null) && (this.readOnlyClasses != null) && this.readOnlyClasses.contains(theClass)) {
            return true;
        }
        return false;
    }

    /**
     * INTERNAL:
     * Check if the object is already registered in a parent Unit Of Work
     */
    public boolean isCloneNewObjectFromParent(Object clone) {
        if (this.parent.isUnitOfWork()) {
            if (((UnitOfWorkImpl)this.parent).isCloneNewObject(clone)) {
                return true;
            } else {
                if (((UnitOfWorkImpl)this.parent).isObjectRegistered(clone)) {
                    clone = ((UnitOfWorkImpl)this.parent).getCloneToOriginals().get(clone);
                }
                return ((UnitOfWorkImpl)this.parent).isCloneNewObjectFromParent(clone);
            }
        } else {
            return false;
        }
    }

    /**
     * INTERNAL:
     * Check if the object is already registered.
     */
    public boolean isCloneNewObject(Object clone) {
        return (this.newObjectsCloneToOriginal != null) && this.newObjectsCloneToOriginal.containsKey(clone);
    }

    /**
     * INTERNAL:
     * Return if the unit of work is waiting to be committed or in the process of being committed.
     */
    public boolean isCommitPending() {
        return this.lifecycle == CommitPending;
    }

    /**
     * INTERNAL:
     * Return if the unit of work is dead.
     */
    public boolean isDead() {
        return this.lifecycle == Death;
    }

    /**
     * PUBLIC:
     * Return whether the session currently has a database transaction in progress.
     */
    public boolean isInTransaction() {
        return this.parent.isInTransaction();
    }

    /**
     * INTERNAL:
     * Return if the unit of work is waiting to be merged or in the process of being merged.
     */
    public boolean isMergePending() {
        return this.lifecycle == MergePending;
    }

    /**
     * INTERNAL:
     * Has writeChanges() been attempted on this UnitOfWork?  It may have
     * either succeeded or failed but either way the UnitOfWork is in a highly
     * restricted state.
     */
    public boolean isAfterWriteChangesButBeforeCommit() {
        return ((this.lifecycle == CommitTransactionPending) || (this.lifecycle == WriteChangesFailed));
    }

    /**
     * INTERNAL:
     * Once writeChanges has failed all a user can do really is rollback.
     */
    protected boolean isAfterWriteChangesFailed() {
        return this.lifecycle == WriteChangesFailed;
    }

    /**
     * PUBLIC:
     * Return whether this session is a nested unit of work or not.
     */
    public boolean isNestedUnitOfWork() {
        return isNestedUnitOfWork;
    }

    /**
     * INTERNAL:
     * This method determines if the specified clone is new in the parent UnitOfWork
     */
    public boolean isNewObjectInParent(Object clone) {
        Object original = null;
        if (hasCloneToOriginals()) {
            original = getCloneToOriginals().get(clone);
        }
        if (original != null) {
            //bug 3115160 as a side this method was fixed to perform the correct lookup on the collection
            return ((UnitOfWorkImpl)this.parent).getNewObjectsCloneToOriginal().containsKey(original);
        }
        return false;
    }

    /**
     * INTERNAL:
     * Return if the object has been deleted in this unit of work.
     */
    public boolean isObjectDeleted(Object object) {
        boolean isDeleted = (this.deletedObjects != null) && this.deletedObjects.containsKey(object);

        if (this.parent.isUnitOfWork()) {
            return isDeleted || ((UnitOfWorkImpl)this.parent).isObjectDeleted(object);
        } else {
            return isDeleted;
        }
    }

    /**
     * INTERNAL:
     * This method is used to determine if the clone is a new Object in the UnitOfWork
     */
    public boolean isObjectNew(Object clone) {
        //CR3678 - ported from 4.0
        return (isCloneNewObject(clone) || (!isObjectRegistered(clone) && !isClassReadOnly(clone.getClass()) && !isUnregisteredExistingObject(clone)));
    }

    /**
     * INTERNAL:
     * Return if the object is a known unregistered existing object.
     */
    public boolean isUnregisteredExistingObject(Object object) {
        return (this.unregisteredExistingObjects != null) && this.unregisteredExistingObjects.containsKey(object);
    }

    /**
     * ADVANCED:
     * Return whether the clone object is already registered.
     */
    public boolean isObjectRegistered(Object clone) {
        if (getCloneMapping().containsKey(clone)) {
            return true;
        }

        // We do smart merge here 
        if (isSmartMerge()){
            ClassDescriptor descriptor = getDescriptor(clone);
            if (this.parent.getIdentityMapAccessorInstance().containsObjectInIdentityMap(keyFromObject(clone, descriptor), clone.getClass(), descriptor) ) {
                mergeCloneWithReferences(clone);

                // don't put clone in  clone mapping since it would result in duplicate clone
                return true;
            }
        }
        return false;
    }

    /**
     * INTERNAL:
     * Return whether the original object is new.
     * It was either registered as new or discovered as a new aggregate
     * within another new object.
     */
    public boolean isOriginalNewObject(Object original) {
        return ((this.newObjectsOriginalToClone != null) && this.newObjectsOriginalToClone.containsKey(original)) 
                    || ((this.newAggregates != null) && this.newAggregates.containsKey(original));
    }

/**
     * INTERNAL:
     * Return the status of smart merge
     */
    public static boolean isSmartMerge() {
        return SmartMerge;
    }

    /**
     * INTERNAL:
     * For synchronized units of work, dump SQL to database.
     * For cases where writes occur before the end of the transaction don't commit
     */
    public void issueSQLbeforeCompletion() {
        issueSQLbeforeCompletion(true);
    }
    /**
     * INTERNAL:
     * For synchronized units of work, dump SQL to database.
     * For cases where writes occur before the end of the transaction don't commit
     */
    public void issueSQLbeforeCompletion(boolean commitTransaction) {
        if (this.lifecycle == CommitTransactionPending) {
            commitTransactionAfterWriteChanges();
            return;
        }
        log(SessionLog.FINER, SessionLog.TRANSACTION, "begin_unit_of_work_commit");
        mergeBmpAndWsEntities();
        // CR#... call event and log.
        if (this.eventManager != null) {
            this.eventManager.preCommitUnitOfWork();
        }
        this.lifecycle = CommitPending;
        commitToDatabaseWithChangeSet(commitTransaction);
    }

    /**
     * INTERNAL:
     * Will notify all the deferred ModifyAllQuery's (excluding UpdateAllQuery's) and deferred UpdateAllQuery's to execute.
     */
    protected void issueModifyAllQueryList() {
        if (this.deferredModifyAllQueries != null) {
            int size = this.deferredModifyAllQueries.size();
            for (int index = 0; index < size; index++) {
                Object[] queries = this.deferredModifyAllQueries.get(index);
                ModifyAllQuery query = (ModifyAllQuery)queries[0];
                AbstractRecord translationRow = (AbstractRecord)queries[1];
                this.parent.executeQuery(query, translationRow);
            }
        }
    }

    /**
     * PUBLIC:
     * Return if this session is a unit of work.
     */
    public boolean isUnitOfWork() {
        return true;
    }

    /**
     * INTERNAL:
     * Return if the object was existing but not registered in the parent of the nested unit of work.
     */
    public boolean isUnregisteredNewObjectInParent(Object originalObject) {
        return getUnregisteredNewObjectsInParent().containsKey(originalObject);
    }

    /**
     * INTERNAL:
     * BMP and Websphere CMP entities have to be merged if they are registered in the unit of work.
     * Check to see if there are any such entities and do the merge if required.
     */
    protected void mergeBmpAndWsEntities() {
        // Check for container registered beans that need to be merged.
        // This is required for EJB entity beans.
        // PERF: First check if there are any.
        if (hasContainerBeans()) {
            Iterator containerBeansEnum = getContainerBeans().keySet().iterator();
            while (containerBeansEnum.hasNext()) {
                mergeCloneWithReferences(containerBeansEnum.next());
            }
        }
    }

    /**
     * INTERNAL: Merge the changes to all objects to the parent.
     */
    protected void mergeChangesIntoParent() {
        UnitOfWorkChangeSet uowChangeSet = (UnitOfWorkChangeSet)getUnitOfWorkChangeSet();
        if (uowChangeSet == null) {
            // No changes.
            return;
        }

        // 3286123 - if no work to be done, skip this part of uow.commit()
        if (!hasModifications()) {
            return;
        }

        boolean isNestedUnitOfWork = this.isNestedUnitOfWork;
        
        // If everything is isolated, can bypass merge entirely.
        if (!isNestedUnitOfWork && (!this.project.hasNonIsolatedUOWClasses() && (this.modifyAllQueries == null))) {
            return;
        }
        
        setPendingMerge();
        startOperationProfile(SessionProfiler.Merge);
        // Ensure concurrency if cache isolation requires.
        this.parent.getIdentityMapAccessorInstance().acquireWriteLock();
        MergeManager manager = getMergeManager();
        if (manager == null) {
            // no MergeManager created for locks during commit
            manager = new MergeManager(this);
        }
        try {
            if (!isNestedUnitOfWork) {
                preMergeChanges();
            }

            // Must clone the clone mapping because entries can be added to it during the merging,
            // and that can lead to concurrency problems.if (this.eventManager != null) {
            if (this.parent.hasEventManager()) {
                this.parent.getEventManager().preMergeUnitOfWorkChangeSet(uowChangeSet);
            }
            if (!isNestedUnitOfWork && getDatasourceLogin().shouldSynchronizeObjectLevelReadWrite()) {
                // Note shouldSynchronizeObjectLevelReadWrite is not the default, shouldSynchronizeObjectLevelReadWriteDatabase
                // is the default, and locks are normally acquire before the commit transaction.
                setMergeManager(manager);
                //If we are merging into the shared cache acquire all required locks before merging.
                this.parent.getIdentityMapAccessorInstance().getWriteLockManager().acquireRequiredLocks(getMergeManager(), (UnitOfWorkChangeSet)getUnitOfWorkChangeSet());
            }
            Set<Class> classesChanged = new HashSet<Class>();
            if (! shouldStoreBypassCache()) {
                for (Map<ObjectChangeSet, ObjectChangeSet> objectChangesList : ((UnitOfWorkChangeSet)getUnitOfWorkChangeSet()).getObjectChanges().values()) {
                    // May be no changes for that class type.
                    for (ObjectChangeSet changeSetToWrite : objectChangesList.values()) {
                        if (changeSetToWrite.hasChanges()) {
                            Object objectToWrite = changeSetToWrite.getUnitOfWorkClone();
                            ClassDescriptor descriptor = changeSetToWrite.getDescriptor();
                            // PERF: Do not merge into the session cache if set to unit of work isolated.
                            if ((!isNestedUnitOfWork) && descriptor.getCachePolicy().shouldIsolateObjectsInUnitOfWork() ) {
                                break;
                            }
                            manager.mergeChanges(objectToWrite, changeSetToWrite, this.getParentIdentityMapSession(descriptor, false, false));
                            classesChanged.add(objectToWrite.getClass());
                        }
                    }
                }
            }

            // Notify the queries to merge into the shared cache
            if (this.modifyAllQueries != null) {
                int size = this.modifyAllQueries.size();
                for (int index = 0; index < size; index++) {
                    ModifyAllQuery query = this.modifyAllQueries.get(index);
                    query.setSession(this.parent);// ensure the query knows which cache to update
                    query.mergeChangesIntoSharedCache();
                }
            }

            if (isNestedUnitOfWork) {
                for (Map<ObjectChangeSet, ObjectChangeSet> objectChangesList : ((UnitOfWorkChangeSet)getUnitOfWorkChangeSet()).getNewObjectChangeSets().values()) {
                    for (ObjectChangeSet changeSetToWrite : objectChangesList.values()) {
                        if (changeSetToWrite.hasChanges()) {
                            Object objectToWrite = changeSetToWrite.getUnitOfWorkClone();
                            manager.mergeChanges(objectToWrite, changeSetToWrite, this.parent);
                        }
                    }
                }
            }
            if (!isNestedUnitOfWork) {
                //If we are merging into the shared cache release all of the locks that we acquired.
                // We will not check If the current thread and the active thread on the mutex do not match
                this.parent.getIdentityMapAccessorInstance().getWriteLockManager().releaseAllAcquiredLocks(manager);
                setMergeManager(null);

                postMergeChanges(classesChanged);

                for (Class changedClass : classesChanged) {
                    this.parent.getIdentityMapAccessorInstance().invalidateQueryCache(changedClass);
                }
                // If change propagation enabled through RemoteCommandManager then go for it
                if (this.parent.shouldPropagateChanges() && (this.parent.getCommandManager() != null)) {
                    if (hasDeletedObjects()) {
                        uowChangeSet.addDeletedObjects(getDeletedObjects(), this);
                    }
                    if (hasObjectsDeletedDuringCommit()) {
                        uowChangeSet.addDeletedObjects(getObjectsDeletedDuringCommit(), this);
                    }
                    boolean hasData = false;
                    if (uowChangeSet.hasChanges()) {
                        MergeChangeSetCommand command = new MergeChangeSetCommand();
                        command.setChangeSet(uowChangeSet);
                        try {
                            hasData = command.convertChangeSetToByteArray(this);
                        } catch (java.io.IOException exception) {
                            throw CommunicationException.unableToPropagateChanges("", exception);
                        }
                        if (hasData) {
                            this.parent.getCommandManager().propagateCommand(command);
                        }
                    }
                }
            }
        } finally {
            if (!this.isNestedUnitOfWork && !manager.getAcquiredLocks().isEmpty()) {
                // if the locks have not already been released (!acquiredLocks.empty)
                // then there must have been an error, release all of the locks.
                try{
                    // 272022: If the current thread and the active thread on the mutex do not match - switch them
                    verifyMutexThreadIntegrityBeforeRelease();
                    this.parent.getIdentityMapAccessorInstance().getWriteLockManager().releaseAllAcquiredLocks(manager);
                }catch(Exception ex){
                    //something has gone wrong twice so lets make sure the original exception is raised
                }
                setMergeManager(null);
            }
            this.parent.getIdentityMapAccessorInstance().releaseWriteLock();
            this.parent.getEventManager().postMergeUnitOfWorkChangeSet(uowChangeSet);
            endOperationProfile(SessionProfiler.Merge);
        }
    }

    /**
     * PUBLIC:
     * Merge the attributes of the clone into the unit of work copy.
     * This can be used for objects that are returned from the client through
     * RMI serialization (or another serialization mechanism), because the RMI object
     * will be a clone this will merge its attributes correctly to preserve object
     * identity within the unit of work and record its changes.
     *
     * The object and its private owned parts are merged.
     *
     * @return the registered version for the clone being merged.
     * @see #shallowMergeClone(Object)
     * @see #deepMergeClone(Object)
     */
    public Object mergeClone(Object rmiClone) {
        return mergeClone(rmiClone, MergeManager.CASCADE_PRIVATE_PARTS, false);
    }

    /**
     * INTERNAL:
     * Merge the attributes of the clone into the unit of work copy.
     */
    public Object mergeClone(Object rmiClone, int cascadeDepth, boolean forRefresh) {
        if (rmiClone == null) {
            return null;
        }

        //CR#2272
        logDebugMessage(rmiClone, "merge_clone");

        startOperationProfile(SessionProfiler.Merge);
        ObjectBuilder builder = getDescriptor(rmiClone).getObjectBuilder();
        Object implementation = builder.unwrapObject(rmiClone, this);

        MergeManager manager = new MergeManager(this);
        manager.mergeCloneIntoWorkingCopy();
        manager.setCascadePolicy(cascadeDepth);
        manager.setForRefresh(forRefresh);

        Object merged = null;
        try {
            merged = manager.mergeChanges(implementation, null, this);
        } catch (RuntimeException exception) {
            merged = handleException(exception);
        }
        endOperationProfile(SessionProfiler.Merge);

        return merged;
    }

    /**
     * INTERNAL:
     * for synchronized units of work, merge changes into parent
     */
    public void mergeClonesAfterCompletion() {
        // 259993: If the current thread and the active thread on the mutex do not match - switch them
        verifyMutexThreadIntegrityBeforeRelease();
        mergeChangesIntoParent();
        // CR#... call event and log.
        if (this.eventManager != null) {
            this.eventManager.postCommitUnitOfWork();
        }
        log(SessionLog.FINER, SessionLog.TRANSACTION, "end_unit_of_work_commit");
    }

    /**
     * PUBLIC:
     * Merge the attributes of the clone into the unit of work copy.
     * This can be used for objects that are returned from the client through
     * RMI serialization (or another serialization mechanism), because the RMI object
     * will be a clone this will merge its attributes correctly to preserve object
     * identity within the unit of work and record its changes.
     *
     * The object and its private owned parts are merged. This will include references from
     * dependent objects to independent objects.
     *
     * @return the registered version for the clone being merged.
     * @see #shallowMergeClone(Object)
     * @see #deepMergeClone(Object)
     */
    public Object mergeCloneWithReferences(Object rmiClone) {
        return this.mergeCloneWithReferences(rmiClone, MergeManager.CASCADE_PRIVATE_PARTS);
    }

    /**
     * PUBLIC:
     * Merge the attributes of the clone into the unit of work copy.
     * This can be used for objects that are returned from the client through
     * RMI serialization (or another serialization mechanism), because the RMI object
     * will be a clone this will merge its attributes correctly to preserve object
     * identity within the unit of work and record its changes.
     *
     * The object and its private owned parts are merged. This will include references from
     * dependent objects to independent objects.
     *
     * @return the registered version for the clone being merged.
     * @see #shallowMergeClone(Object)
     * @see #deepMergeClone(Object)
     */
    public Object mergeCloneWithReferences(Object rmiClone, int cascadePolicy) {
        return mergeCloneWithReferences(rmiClone, cascadePolicy, false);
    }
    
    /**
     * INTERNAL:
     * Merge the attributes of the clone into the unit of work copy.
     * This can be used for objects that are returned from the client through
     * RMI serialization (or another serialization mechanism), because the RMI object
     * will be a clone this will merge its attributes correctly to preserve object
     * identity within the unit of work and record its changes.
     *
     * The object and its private owned parts are merged. This will include references from
     * dependent objects to independent objects.
     *
     * @return the registered version for the clone being merged.
     * @see #shallowMergeClone(Object)
     * @see #deepMergeClone(Object)
     */
    public Object mergeCloneWithReferences(Object rmiClone, int cascadePolicy, boolean forceCascade) {
        Object returnValue = null;
        try{
            MergeManager manager = new MergeManager(this);
            manager.mergeCloneWithReferencesIntoWorkingCopy();
            manager.setCascadePolicy(cascadePolicy);
            manager.setForceCascade(forceCascade);
            mergeManagerForActiveMerge = manager;
            returnValue= mergeCloneWithReferences(rmiClone, manager);
        } finally {
            mergeManagerForActiveMerge = null;
        }
        return returnValue;
    }
    
    /**
     * INTERNAL:
     * Merge the attributes of the clone into the unit of work copy.
     * This can be used for objects that are returned from the client through
     * RMI serialization (or another serialization mechanism), because the RMI object
     * will be a clone this will merge its attributes correctly to preserve object
     * identity within the unit of work and record its changes.
     *
     * The object and its private owned parts are merged. This will include references from
     * dependent objects to independent objects.
     *
     * @return the registered version for the clone being merged.
     * @see #shallowMergeClone(Object)
     * @see #deepMergeClone(Object)
     */
    public Object mergeCloneWithReferences(Object rmiClone, MergeManager manager) {
        if (rmiClone == null) {
            return null;
        }
        ClassDescriptor descriptor = getDescriptor(rmiClone);
        if ((descriptor == null) || descriptor.isDescriptorTypeAggregate()) {
            if (manager.getCascadePolicy() == MergeManager.CASCADE_BY_MAPPING){
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("not_an_entity", new Object[]{rmiClone}));
            }
            return rmiClone;
        }

        //CR#2272
        logDebugMessage(rmiClone, "merge_clone_with_references");

        ObjectBuilder builder = descriptor.getObjectBuilder();
        Object implementation = builder.unwrapObject(rmiClone, this);
        
        Object mergedObject = manager.mergeChanges(implementation, null, this);
        if (isSmartMerge()) {
            return builder.wrapObject(mergedObject, this);
        } else {
            return mergedObject;
        }
    }

    /**
     * PUBLIC:
     * Return a new instance of the class registered in this unit of work.
     * This can be used to ensure that new objects are registered correctly.
     */
    public Object newInstance(Class theClass) {
        //CR#2272
        logDebugMessage(theClass, "new_instance");

        ClassDescriptor descriptor = getDescriptor(theClass);
        Object newObject = descriptor.getObjectBuilder().buildNewInstance();
        return registerObject(newObject);
    }

    /**
     * INTERNAL:
     * This method will perform a delete operation on the provided objects pre-determining
     * the objects that will be deleted by a commit of the UnitOfWork including privately
     * owned objects.  It does not execute a query for the deletion of these objects as the
     * normal deleteobject operation does.  Mainly implemented to provide EJB 3.0 deleteObject
     * support.
     */
    public void performRemove(Object toBeDeleted, Map visitedObjects) {
        if (toBeDeleted == null) {
            return;
        }
        ClassDescriptor descriptor = getDescriptor(toBeDeleted);
        if ((descriptor == null) || descriptor.isDescriptorTypeAggregate()) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("not_an_entity", new Object[] { toBeDeleted }));
        }
        logDebugMessage(toBeDeleted, "deleting_object");

        //bug 4568370+4599010; fix EntityManager.remove() to handle new objects
        if (getDeletedObjects().containsKey(toBeDeleted)){
          return;
        }
        visitedObjects.put(toBeDeleted,toBeDeleted);
        Object registeredObject = checkIfAlreadyRegistered(toBeDeleted, descriptor);
        if (registeredObject == null) {
            Object primaryKey = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(toBeDeleted, this);
            DoesExistQuery existQuery = descriptor.getQueryManager().getDoesExistQuery();
            existQuery = (DoesExistQuery)existQuery.clone();
            existQuery.setObject(toBeDeleted);
            existQuery.setPrimaryKey(primaryKey);
            existQuery.setDescriptor(descriptor);
            existQuery.setIsExecutionClone(true);

            existQuery.setCheckCacheFirst(true);
            if (((Boolean)executeQuery(existQuery)).booleanValue()){
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("cannot_remove_detatched_entity", new Object[]{toBeDeleted}));
            }//else, it is a new or previously deleted object that should be ignored (and delete should cascade)
        } else {
            //fire events only if this is a managed object
            if (descriptor.getEventManager().hasAnyEventListeners()) {
                org.eclipse.persistence.descriptors.DescriptorEvent event = new org.eclipse.persistence.descriptors.DescriptorEvent(toBeDeleted);
                event.setEventCode(DescriptorEventManager.PreRemoveEvent);
                event.setSession(this);
                descriptor.getEventManager().executeEvent(event);
            }
            if (hasNewObjects() && getNewObjectsCloneToOriginal().containsKey(registeredObject)){
                unregisterObject(registeredObject, DescriptorIterator.NoCascading);
            } else {
                getDeletedObjects().put(toBeDeleted, toBeDeleted);
            }
        }
        descriptor.getObjectBuilder().cascadePerformRemove(toBeDeleted, this, visitedObjects);
    }

    /**
     * INTERNAL:
     * Cascade remove the private owned object from the owned UnitOfWorkChangeSet
     */
    public void performRemovePrivateOwnedObjectFromChangeSet(Object toBeRemoved, Map visitedObjects) {
        if (toBeRemoved == null) {
            return;
        }
        visitedObjects.put(toBeRemoved, toBeRemoved);
        ClassDescriptor descriptor = getDescriptor(toBeRemoved);

        // remove object from ChangeSet 
        UnitOfWorkChangeSet uowChanges = (UnitOfWorkChangeSet)getUnitOfWorkChangeSet();

        if (uowChanges != null) {
            ObjectChangeSet ocs = (ObjectChangeSet)uowChanges.getObjectChangeSetForClone(toBeRemoved);
            if (ocs != null) {
                // remove object change set and object change set from new list
                uowChanges.removeObjectChangeSet(ocs);
                uowChanges.removeObjectChangeSetFromNewList(ocs, this);
            }
        }
        
        // unregister object and cascade the removal
        unregisterObject(toBeRemoved, DescriptorIterator.NoCascading);

        descriptor.getObjectBuilder().cascadePerformRemovePrivateOwnedObjectFromChangeSet(toBeRemoved, this, visitedObjects);
    }

    /**
     * ADVANCED:
     * The unit of work performs validations such as,
     * ensuring multiple copies of the same object don't exist in the same unit of work,
     * ensuring deleted objects are not referred after commit,
     * ensures that objects from the parent cache are not referred in the unit of work cache.
     * The level of validation can be increased or decreased for debugging purposes or under
     * advanced situation where the application requires/desires to violate clone identity in the unit of work.
     * It is strongly suggested that clone identity not be violate in the unit of work.
     */
    public void performFullValidation() {
        setValidationLevel(Full);

    }

    /**
     * ADVANCED:
     * The unit of work performs validations such as,
     * ensuring multiple copies of the same object don't exist in the same unit of work,
     * ensuring deleted objects are not referred after commit,
     * ensures that objects from the parent cache are not referred in the unit of work cache.
     * The level of validation can be increased or decreased for debugging purposes or under
     * advanced situation where the application requires/desires to violate clone identity in the unit of work.
     * It is strongly suggested that clone identity not be violate in the unit of work.
     */
    public void performPartialValidation() {
        setValidationLevel(Partial);
    }

    /**
     * INTERNAL:
     * This method is called from clone and register.  It includes the processing
     * required to clone an object, including populating attributes, putting in
     * UOW identitymap and building a backupclone
     */
    protected void populateAndRegisterObject(Object original, Object workingClone, CacheKey unitOfWorkCacheKey, CacheKey parentCacheKey, ClassDescriptor descriptor) {
        // This must be registered before it is built to avoid cycles.
        unitOfWorkCacheKey.setObject(workingClone);
        unitOfWorkCacheKey.setReadTime(parentCacheKey.getReadTime());
        unitOfWorkCacheKey.setWriteLockValue(parentCacheKey.getWriteLockValue());

        //Set ChangeListener for ObjectChangeTrackingPolicy and AttributeChangeTrackingPolicy,
        //but not DeferredChangeDetectionPolicy.  Build backup clone for DeferredChangeDetectionPolicy 
        //or ObjectChangeTrackingPolicy, but not for AttributeChangeTrackingPolicy.  
        // - Set listener before populating attributes so aggregates can find the parent's listener
        ObjectChangePolicy changePolicy = descriptor.getObjectChangePolicy();
        changePolicy.setChangeListener(workingClone, this, descriptor);
        changePolicy.dissableEventProcessing(workingClone);

        ObjectBuilder builder = descriptor.getObjectBuilder();
        builder.populateAttributesForClone(original, parentCacheKey, workingClone, null, this);
        Object backupClone = changePolicy.buildBackupClone(workingClone, builder, this);
        // PERF: Avoid put if no backup clone.
        if (workingClone != backupClone) {
            getCloneMapping().put(workingClone, backupClone);
        }
        changePolicy.enableEventProcessing(workingClone);
    }

    /**
     * INTERNAL:
     * Remove objects from parent's identity map.
     */
    protected void postMergeChanges(Set classesChanged) {
        //bug 4730595: objects removed during flush are not removed from the cache during commit
        if (this.unitOfWorkChangeSet.hasDeletedObjects()) {
            Map deletedObjects = this.unitOfWorkChangeSet.getDeletedObjects();
            for (Iterator removedObjects = deletedObjects.keySet().iterator(); removedObjects.hasNext(); ) {
                ObjectChangeSet removedObjectChangeSet = (ObjectChangeSet) removedObjects.next();
                Object primaryKey = removedObjectChangeSet.getId();
                ClassDescriptor descriptor = removedObjectChangeSet.getDescriptor();
                // PERF: Do not remove if uow is isolated.
                if (!descriptor.getCachePolicy().shouldIsolateObjectsInUnitOfWork()) {
                    this.parent.getIdentityMapAccessorInstance().removeFromIdentityMap(primaryKey, descriptor.getJavaClass(), descriptor, removedObjectChangeSet.getUnitOfWorkClone());
                    classesChanged.add(descriptor.getJavaClass());
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Remove objects deleted during commit from clone and new object cache so that these are not merged
     */
    protected void preMergeChanges() {
        if (hasObjectsDeletedDuringCommit()) {
            for (Iterator removedObjects = getObjectsDeletedDuringCommit().keySet().iterator();
                     removedObjects.hasNext();) {
                Object removedObject = removedObjects.next();
                getCloneMapping().remove(removedObject);
                // PERF: Avoid initialization of new objects if none.
                if (hasNewObjects()) {
                    Object referenceObjectToRemove = getNewObjectsCloneToOriginal().get(removedObject);
                    if (referenceObjectToRemove != null) {
                        getNewObjectsCloneToOriginal().remove(removedObject);
                        getNewObjectsOriginalToClone().remove(referenceObjectToRemove);
                    }
                }
            }
        }
    }

    /**
     * PUBLIC:
     * Print the objects in the unit of work.
     * The output of this method will be logged to this unit of work's SessionLog at SEVERE level.
     */
    public void printRegisteredObjects() {
        if (shouldLog(SessionLog.SEVERE, SessionLog.CACHE)) {
            basicPrintRegisteredObjects();
        }
    }

    /**
     * INTERNAL:
     * This method is used to process delete queries that pass through the unitOfWork
     * It is extracted out of the internalExecuteQuery method to reduce duplication
     */
    public Object processDeleteObjectQuery(DeleteObjectQuery deleteQuery) {
        // We must ensure that we delete the clone not the original, (this can happen in the mappings update)
        if (deleteQuery.getObject() == null) {// Must validate.
            throw QueryException.objectToModifyNotSpecified(deleteQuery);
        }

        ClassDescriptor descriptor = deleteQuery.getDescriptor();
        if(descriptor == null) {
            descriptor = getDescriptor(deleteQuery.getObject());
        }
        ObjectBuilder builder = descriptor.getObjectBuilder();
        Object implementation = builder.unwrapObject(deleteQuery.getObject(), this);

        if (isClassReadOnly(implementation.getClass(), descriptor)) {
            throw QueryException.cannotDeleteReadOnlyObject(implementation);
        }

        if (isCloneNewObject(implementation)) {
            unregisterObject(implementation);
            return implementation;
        }
        Object primaryKey = builder.extractPrimaryKeyFromObject(implementation, this);
        Object clone = getIdentityMapAccessorInstance().getFromIdentityMap(primaryKey, implementation.getClass(), descriptor);
        if (clone == null) {
            clone = implementation;
        }

        // Register will wrap so must unwrap again.
        clone = builder.unwrapObject(clone, this);

        deleteQuery.setObject(clone);
        if (!getCommitManager().isActive()) {
            getDeletedObjects().put(clone, primaryKey);
            return clone;
        } else {
            // If the object has already been deleted i.e. private-owned + deleted then don't do it twice.
            if (hasObjectsDeletedDuringCommit()) {
                if (getObjectsDeletedDuringCommit().containsKey(clone)) {
                    return clone;
                }
            }
        }
        return null;
    }

    /**
     * INTERNAL:
     * Print the objects in the unit of work.
     */
    protected void basicPrintRegisteredObjects() {
        String cr = Helper.cr();
        StringWriter writer = new StringWriter();
        writer.write(LoggingLocalization.buildMessage("unitofwork_identity_hashcode", new Object[] { cr, String.valueOf(System.identityHashCode(this)) }));
        if (hasDeletedObjects()) {
            writer.write(cr + LoggingLocalization.buildMessage("deleted_objects"));
            for (Iterator enumtr = getDeletedObjects().keySet().iterator(); enumtr.hasNext();) {
                Object object = enumtr.next();
                writer.write(LoggingLocalization.buildMessage("key_identity_hash_code_object", new Object[] { cr, getDescriptor(object).getObjectBuilder().extractPrimaryKeyFromObject(object, this), "\t", String.valueOf(System.identityHashCode(object)), object }));
            }
        }
        writer.write(cr + LoggingLocalization.buildMessage("all_registered_clones"));
        for (Iterator enumtr = getCloneMapping().keySet().iterator(); enumtr.hasNext();) {
            Object object = enumtr.next();
            writer.write(LoggingLocalization.buildMessage("key_identity_hash_code_object", new Object[] { cr, getDescriptor(object).getObjectBuilder().extractPrimaryKeyFromObject(object, this), "\t", String.valueOf(System.identityHashCode(object)), object }));
        }
        if (hasNewObjectsInParentOriginalToClone()) {
            writer.write(cr + LoggingLocalization.buildMessage("new_objects"));
            for (Iterator enumtr = getNewObjectsCloneToOriginal().keySet().iterator();
                     enumtr.hasNext();) {
                Object object = enumtr.next();
                writer.write(LoggingLocalization.buildMessage("key_identity_hash_code_object", new Object[] { cr, getDescriptor(object).getObjectBuilder().extractPrimaryKeyFromObject(object, this), "\t", String.valueOf(System.identityHashCode(object)), object }));
            }
        }
        log(SessionLog.SEVERE, SessionLog.TRANSACTION, writer.toString(), null, null, false);
    }

    /**
     * PUBLIC:
     * Register the objects with the unit of work.
     * All newly created root domain objects must be registered to be inserted on commit.
     * Also any existing objects that will be edited and were not read from this unit of work
     * must also be registered.
     * Once registered any changes to the objects will be committed to the database on commit.
     *
     * @return is the clones of the original objects, the return value must be used for editing.
     * Editing the original is not allowed in the unit of work.
     */
    public Vector registerAllObjects(Collection domainObjects) {
        Vector clones = new Vector(domainObjects.size());
        for (Iterator objectsEnum = domainObjects.iterator(); objectsEnum.hasNext();) {
            clones.addElement(registerObject(objectsEnum.next()));
        }
        return clones;
    }

    /**
     * PUBLIC:
     * Register the objects with the unit of work.
     * All newly created root domain objects must be registered to be inserted on commit.
     * Also any existing objects that will be edited and were not read from this unit of work
     * must also be registered.
     * Once registered any changes to the objects will be committed to the database on commit.
     *
     * @return is the clones of the original objects, the return value must be used for editing.
     * Editing the original is not allowed in the unit of work.
     */
    public Vector registerAllObjects(Vector domainObjects) throws DatabaseException, OptimisticLockException {
        Vector clones = new Vector(domainObjects.size());
        for (Enumeration objectsEnum = domainObjects.elements(); objectsEnum.hasMoreElements();) {
            clones.addElement(registerObject(objectsEnum.nextElement()));
        }
        return clones;
    }

    /**
     * ADVANCED:
     * Register the existing object with the unit of work.
     * This is a advanced API that can be used if the application can guarantee the object exists on the database.
     * When registerObject is called the unit of work determines existence through the descriptor's doesExist setting.
     *
     * @return The clone of the original object, the return value must be used for editing.
     * Editing the original is not allowed in the unit of work.
     */
    public Object registerExistingObject(Object existingObject) {
        return this.registerExistingObject(existingObject, false);
    }

    /**
     * ADVANCED:
     * Register the existing object with the unit of work.
     * This is a advanced API that can be used if the application can guarantee the object exists on the database.
     * When registerObject is called the unit of work determines existence through the descriptor's doesExist setting.
     *
     * @return The clone of the original object, the return value must be used for editing.
     * Editing the original is not allowed in the unit of work.
     */
    public Object registerExistingObject(Object existingObject, boolean isFromSharedCache) {
        if (existingObject == null) {
            return null;
        }
        ClassDescriptor descriptor = getDescriptor(existingObject);
        if (descriptor == null) {
            throw DescriptorException.missingDescriptor(existingObject.getClass().toString());
        }
        if (this.isClassReadOnly(descriptor.getJavaClass(), descriptor)) {
            return existingObject;
        }

        ObjectBuilder builder = descriptor.getObjectBuilder();
        Object implementation = builder.unwrapObject(existingObject, this);
        Object registeredObject = this.registerExistingObject(implementation, descriptor, null, isFromSharedCache);

        // Bug # 3212057 - workaround JVM bug (MWN)
        if (implementation != existingObject) {
            return builder.wrapObject(registeredObject, this);
        } else {
            return registeredObject;
        }
    }

    /**
     * INTERNAL:
     * Register the existing object with the unit of work.
     * This is a advanced API that can be used if the application can guarantee the object exists on the database.
     * When registerObject is called the unit of work determines existence through the descriptor's doesExist setting.
     *
     * @return The clone of the original object, the return value must be used for editing.
     * Editing the original is not allowed in the unit of work.
     */
    public Object registerExistingObject(Object objectToRegister, ClassDescriptor descriptor, Object queryPrimaryKey, boolean isFromSharedCache) {
        if (this.isClassReadOnly(descriptor.getJavaClass(), descriptor)) {
            return objectToRegister;
        }
        if (isAfterWriteChangesButBeforeCommit()) {
            throw ValidationException.illegalOperationForUnitOfWorkLifecycle(this.lifecycle, "registerExistingObject");
        }
        if (descriptor.isDescriptorTypeAggregate()) {
            throw ValidationException.cannotRegisterAggregateObjectInUnitOfWork(objectToRegister.getClass());
        }
        //CR#2272
        logDebugMessage(objectToRegister, "register_existing");
        Object registeredObject;
        try {
            startOperationProfile(SessionProfiler.Register);
            registeredObject = checkIfAlreadyRegistered(objectToRegister, descriptor);
            if (registeredObject == null) {
                // Check if object is existing, if it is it must be cloned into the unit of work
                // otherwise it is a new object
                Object primaryKey = queryPrimaryKey;
                if (primaryKey == null){
                    primaryKey = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(objectToRegister, this, true);
                }
                if (descriptor.shouldLockForClone() || !isFromSharedCache){
                // The primary key may be null for a new object in a nested unit of work (is existing in nested, new in parent).
                    if (primaryKey != null) {
                        // Always check the cache first.
                        registeredObject = getIdentityMapAccessorInstance().getFromIdentityMap(primaryKey, objectToRegister, objectToRegister.getClass(), true, descriptor);
                    }
                } else {
                    // perform a check of the UOW identitymap.  This would be done by getFromIdentityMap
                    // but that method also calls back up to the shared cache in case it is not found locally.
                    // and we wish to avoid checking the shared cache twice.
                    CacheKey localCacheKey = getIdentityMapAccessorInstance().getCacheKeyForObject(primaryKey, objectToRegister.getClass(), descriptor, false);
                    if (localCacheKey != null){
                        registeredObject = localCacheKey.getObject();
                    }
                }
                if (registeredObject == null) {
                    // This is a case where the object is not in the session cache,
                    // so a new cache-key is used as there is no original to use for locking.
                    // It read time must be set to avoid it being invalidated.
                    CacheKey cacheKey = new CacheKey(primaryKey);
                    cacheKey.setReadTime(System.currentTimeMillis());
                    cacheKey.setIsolated(true); // if the cache does not have a version then this must be built from the supplied version
                    registeredObject = cloneAndRegisterObject(objectToRegister, cacheKey, descriptor);
                }
            }
            //bug3659327
            //fetch group manager control fetch group support
            if (descriptor.hasFetchGroupManager()) {
                //if the object is already registered in uow, but it's partially fetched (fetch group case)	
                if (descriptor.getFetchGroupManager().shouldWriteInto(objectToRegister, registeredObject)) {
                    //there might be cases when reverting/refreshing clone is needed.
                    descriptor.getFetchGroupManager().writePartialIntoClones(objectToRegister, registeredObject, this.getBackupClone(registeredObject, descriptor), this);
                }
            }
        } finally {
            endOperationProfile(SessionProfiler.Register);
        }
        return registeredObject;
    }

    /**
     * INTERNAL:
     * Register the new container bean with the unit of work.
     * Normally the registerObject method should be used for all registration of new and existing objects.
     * This version of the register method can only be used for new container beans.
     *
     * @see #registerObject(Object)
     */
    public Object registerNewContainerBean(Object newObject) {
        if (newObject == null) {
            return null;
        }

        //CR#2272
        logDebugMessage(newObject, "register_new");

        startOperationProfile(SessionProfiler.Register);
        setShouldNewObjectsBeCached(true);
        ClassDescriptor descriptor = getDescriptor(newObject);
        if (descriptor == null) {
            throw DescriptorException.missingDescriptor(newObject.getClass().toString());
        }

        ObjectBuilder builder = descriptor.getObjectBuilder();

        //Pine Beta.  Removed Checking the containerBean collection.  It is not required as these are new objects.
        // Was removed to prevent issue where weblogic would re-use beans from the pool in a single transaction
        // Ensure that the registered object is the one from the parent cache.
        if (shouldPerformFullValidation()) {
            Object primaryKey = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(newObject, this);
            Object objectFromCache = this.parent.getIdentityMapAccessorInstance().getFromIdentityMap(primaryKey, descriptor.getJavaClass(), descriptor);
            if (objectFromCache != null) {
                throw ValidationException.wrongObjectRegistered(newObject, objectFromCache);
            }
        }

        Object original = builder.buildNewInstance();
        builder.copyInto(newObject, original);

        Object clone = registerObject(original);
        getContainerBeans().put(newObject, clone);

        endOperationProfile(SessionProfiler.Register);

        return newObject;
    }

    /**
     * INTERNAL:
     * Register the new Bean with the unit of work.
     * This will register the new Bean with cloning.
     * Normally the registerObject method should be used for all registration of new and existing objects.
     * This version of the register method can only be used for new objects.
     */
    public Object registerNewContainerBeanForCMP(Object newObject) {
        if (newObject == null) {
            return null;
        }

        //CR#2272
        logDebugMessage(newObject, "register_new_bean");

        startOperationProfile(SessionProfiler.Register);

        Object clone = cloneAndRegisterNewObject(newObject, false);

        endOperationProfile(SessionProfiler.Register);

        return clone;
    }

    /**
     * ADVANCED:
     * Register the new object with the unit of work.
     * This will register the new object without cloning.
     * Normally the registerObject method should be used for all registration of new and existing objects.
     * This version of the register method can only be used for new objects.
     * This method should only be used if a new object is desired to be registered without cloning.
     *
     * @see #registerObject(Object)
     */
    public Object registerNewObject(Object newObject) {
        if (newObject == null) {
            return null;
        }
        ClassDescriptor descriptor = getDescriptor(newObject);
        if (descriptor == null) {
            throw DescriptorException.missingDescriptor(newObject.getClass().toString());
        }

        ObjectBuilder builder = descriptor.getObjectBuilder();
        Object implementation = builder.unwrapObject(newObject, this);

        this.registerNewObject(implementation, descriptor);

        if (implementation == newObject) {
            return newObject;
        } else {
            return builder.wrapObject(implementation, this);
        }
    }

    /**
     * INTERNAL:
     * Updated to allow passing in of the object's descriptor
     *
     * Register the new object with the unit of work.
     * This will register the new object without cloning.
     * Normally the registerObject method should be used for all registration of new and existing objects.
     * This version of the register method can only be used for new objects.
     * This method should only be used if a new object is desired to be registered without cloning.
     *
     * @see #registerObject(Object)
     */
    protected Object registerNewObject(Object implementation, ClassDescriptor descriptor) {
        if (isAfterWriteChangesButBeforeCommit()) {
            throw ValidationException.illegalOperationForUnitOfWorkLifecycle(this.lifecycle, "registerNewObject");
        }
        if (descriptor.isDescriptorTypeAggregate()) {
            throw ValidationException.cannotRegisterAggregateObjectInUnitOfWork(implementation.getClass());
        }
        try {
            //CR#2272
            logDebugMessage(implementation, "register_new");

            startOperationProfile(SessionProfiler.Register);
            Object registeredObject = checkIfAlreadyRegistered(implementation, descriptor);
            if (registeredObject == null) {
                // Ensure that the registered object is the one from the parent cache.
                if (shouldPerformFullValidation()) {
                    Object primaryKey = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(implementation, this);
                    Object objectFromCache = this.parent.getIdentityMapAccessorInstance().getFromIdentityMap(primaryKey, implementation.getClass(), descriptor);
                    if (objectFromCache != null) {
                        throw ValidationException.wrongObjectRegistered(implementation, objectFromCache);
                    }
                }
                ObjectBuilder builder = descriptor.getObjectBuilder();
                // New objects should not have an original until merge.
                Object original = null;

                Object backupClone = implementation;
                if (!descriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy()) {
                    backupClone = builder.buildNewInstance();
                }
                getCloneMapping().put(implementation, backupClone);

                // Check if the new objects should be cached.
                registerNewObjectClone(implementation, original, descriptor); //this method calls registerNewObjectInIdentityMap
            }
        } finally {
            endOperationProfile(SessionProfiler.Register);
        }

        //as this is register new return the object passed in.
        return implementation;
    }
    
    /**
     * INTERNAL:
     * Discover any new objects referenced from registered objects and persist them.
     * This is similar to persist, except that it traverses (all changed or new) objects
     * during the commit to find any unregistered new objects and persists them.
     * Only objects referenced by cascade persist mappings will be persisted,
     * an error will be thrown from non-cascade persist mappings to new objects (detached existing object are ok...in thoery).
     * This is specific to EJB 3.0 support.
     * @param newObjects any new objects found must be added to this collection.
     * @param cascadePersist determines if this call is cascading from a cascadePersist mapping or not.
     */
    public void discoverAndPersistUnregisteredNewObjects(Object object, boolean cascadePersist, Map newObjects, Map unregisteredExistingObjects, Map visitedObjects, Set cascadeErrors) {
        if (object == null) {
            return;
        }
        
        if (cascadePersist && isObjectDeleted(object)) {
            // It is deleted but reference by a cascade persist mapping, spec seems to state it should be undeleted, but seems wrong.
            // TODO: Reconsider this.
            undeleteObject(object);
        }
        
        // EL Bug 343925 - Add all unregistered objects which are not marked as cascade persist 
        // to cascadeErrors so that all the registered objects and their mappings are iterated 
        // to discover if the object is marked with CascadeType.PERSIST using a different mapping
        // of a different registered object. Throw IllegalStateException only after iterating through 
        // all the registered objects and their mappings is completed, and if cascadeErrors 
        // collection contains any unregistered object.
        if (visitedObjects.containsKey(object) && !cascadeErrors.contains(object)) {
            return;
        }
        visitedObjects.put(object, object);
        
        // If this object is deleted, avoid any discovery and return.
        if (isObjectDeleted(object)) {
            return;
        }
        
        ClassDescriptor descriptor = getDescriptor(object);        
        // If the object is read-only or deleted then do not continue the traversal.
        if (isClassReadOnly(object.getClass(), descriptor)) {
            return;
        }
        if (!isObjectRegistered(object)) {
            if (cascadePersist) {
                // It is new and reference by a cascade persist mapping, persist it.
                // This will also throw an exception if it is an unregistered existing object (which the spec seems to state).
                registerNotRegisteredNewObjectForPersist(object, descriptor);
                newObjects.put(object, object);
                if (cascadeErrors.contains(object)) {
                    cascadeErrors.remove(object);
                }
            } else if (checkForUnregisteredExistingObject(object)) {
                // Always ignore unregistered existing objects in JPA (when not cascade persist).
                // If the object exists we need to keep a record of this object to ignore it,
                // also need to stop iterating over it.
                // Spec seems to say this is undefined.
                unregisteredExistingObjects.put(object, object);
                return;
            } else {
                // It is new but not referenced by a cascade persist mapping, throw an error.
                cascadeErrors.add(object);
                return;
            }
        }
        descriptor.getObjectBuilder().cascadeDiscoverAndPersistUnregisteredNewObjects(object, newObjects, unregisteredExistingObjects, visitedObjects, this, cascadeErrors);        
    }
    
    /**
     * INTERNAL:
     * Register the new object with the unit of work.
     * This will register the new object without cloning.
     * Checks based on existence will be completed and the create will be cascaded based on the
     * object's mappings cascade requirements.  This is specific to EJB 3.0 support.
     * @see #registerObject(Object)
     */
    public void registerNewObjectForPersist(Object newObject, Map visitedObjects) {
        if (newObject == null) {
            return;
        }
        if(visitedObjects.containsKey(newObject)) {
            return;
        }
        visitedObjects.put(newObject, newObject);
        ClassDescriptor descriptor = getDescriptor(newObject);
        if ((descriptor == null) || descriptor.isDescriptorTypeAggregate()) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("not_an_entity", new Object[] { newObject }));
        }

        startOperationProfile(SessionProfiler.Register);
        try {
            Object registeredObject = checkIfAlreadyRegistered(newObject, descriptor);
            if (registeredObject == null) {
                registerNotRegisteredNewObjectForPersist(newObject, descriptor);
            } else if (this.isObjectDeleted(newObject)) {
                //if object is deleted and a create is issued on the that object
                // then the object must be transitioned back to existing and not deleted
                this.undeleteObject(newObject);
            }
            descriptor.getObjectBuilder().cascadeRegisterNewForCreate(newObject, this, visitedObjects);
            // After any cascade persists and assigning any sequence numbers,
            // update any derived id attributes on the new object.
            updateDerivedIds(newObject, descriptor);
        } finally {
            endOperationProfile(SessionProfiler.Register);
        }
    }

    /**
     * INTERNAL:
     * Return if the object was deleted previously (in a flush).
     */
    public boolean wasDeleted(Object original) {
        // Implemented by subclass
        return false;
    }
    
    /**
     * INTERNAL:
     * Called only by registerNewObjectForPersist method,
     * and only if newObject is not already registered.
     * Could be overridden in subclasses.
     */
    protected void registerNotRegisteredNewObjectForPersist(Object newObject, ClassDescriptor descriptor) {
        // Ensure that the registered object is not detached.
        // Only check existence if validating, as only results in an earlier error.
        if (shouldValidateExistence()) {
            DoesExistQuery existQuery = descriptor.getQueryManager().getDoesExistQuery();
            existQuery = (DoesExistQuery)existQuery.clone();
            existQuery.setObject(newObject);
            existQuery.setDescriptor(descriptor);
            existQuery.setIsExecutionClone(true);
            if (((Boolean)executeQuery(existQuery)).booleanValue()) {
                throw ValidationException.cannotPersistExistingObject(newObject, this);
            }
        }
        
        logDebugMessage(newObject, "register_new_for_persist");
        
        ObjectBuilder builder = descriptor.getObjectBuilder();
        // New objects should not have an original until merge.
        Object original = null;

        Object backupClone = newObject;
        if (!descriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy()) {
            backupClone = builder.buildNewInstance();
        }
        getCloneMapping().put(newObject, backupClone);
        assignSequenceNumber(newObject, descriptor);

        // Check if the new objects should be cached.
        registerNewObjectClone(newObject, original, descriptor); //this method calls registerNewObjectInIdentityMap
    }
    
    /**
     * INTERNAL:
     * Register the working copy of a new object and its original.
     * The user must edit the working copy and the original is used to merge into the parent.
     * This mapping is kept both ways because lookup is required in both directions.
     */
    protected void registerNewObjectClone(Object clone, Object original, ClassDescriptor descriptor) {
        // Check if the new objects should be cached.
        registerNewObjectInIdentityMap(clone, original, descriptor);

        getNewObjectsCloneToOriginal().put(clone, original);
        if (original != null) {
            getNewObjectsOriginalToClone().put(original, clone);
        }
        
        // run prePersist callbacks if any
        if (descriptor.getEventManager().hasAnyEventListeners()) {
            DescriptorEvent event = new DescriptorEvent(clone);
            event.setEventCode(DescriptorEventManager.PrePersistEvent);
            event.setSession(this);
            descriptor.getEventManager().executeEvent(event);
        }  
    }
    
    /**
     * INTERNAL:
     * Add the new object to the cache if set to.
     * This is useful for using mergeclone on new objects.
     */
    protected void registerNewObjectInIdentityMap(Object clone, Object original, ClassDescriptor descriptor) {
        if (shouldNewObjectsBeCached()) {
            // Put new objects in the cache if it has a valid primary key, this allows for double new object merges,
            // and cache hits on pk queries.
            // PERF: Only need to extract key using object builder, it will now return null if the key is not valid.
            Object key = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(clone, this, true);
            if (key != null) {
                getIdentityMapAccessorInstance().putInIdentityMap(clone, key, null, 0, descriptor);
            }
        }
    }

    /**
     * PUBLIC:
     * Register the object with the unit of work.
     * All newly created root domain objects must be registered to be inserted on commit.
     * Also any existing objects that will be edited and were not read from this unit of work
     * must also be registered.
     * Once registered any changes to the objects will be committed to the database on commit.
     *
     * @return the clone of the original object, the return value must be used for editing,
     *
     * ** Editing the original is not allowed in the unit of work. **
     */
    public Object registerObject(Object object) {
        if (object == null) {
            return null;
        }
        ClassDescriptor descriptor = getDescriptor(object);
        if (descriptor == null) {
            throw DescriptorException.missingDescriptor(object.getClass().toString());
        }
        if (this.isClassReadOnly(descriptor.getJavaClass(), descriptor)) {
            return object;
        }
        ObjectBuilder builder = descriptor.getObjectBuilder();
        Object implementation = builder.unwrapObject(object, this);
        boolean wasWrapped = implementation != object;
        Object registeredObject = this.registerObject(implementation, descriptor);
        if (wasWrapped) {
            return builder.wrapObject(registeredObject, this);
        } else {
            return registeredObject;
        }
    }

    /**
     * INTERNAL:
     * Allows for calling method to provide the descriptor information for this
     * object.  Prevents double lookup of descriptor.
     *
     *
     * Register the object with the unit of work.
     * All newly created root domain objects must be registered to be inserted on commit.
     * Also any existing objects that will be edited and were not read from this unit of work
     * must also be registered.
     * Once registered any changes to the objects will be committed to the database on commit.
     *
     * calling this method will also sort the objects into different different groups
     * depending on if the object being registered is a bean or a regular Java
     * object and if its updates are deferred, non-deferred or if all modifications
     * are deferred.
     *
     * @return the clone of the original object, the return value must be used for editing,
     */
    protected Object registerObject(Object object, ClassDescriptor descriptor) {
        if (this.isClassReadOnly(descriptor.getJavaClass(), descriptor)) {
            return object;
        }
        if (isAfterWriteChangesButBeforeCommit()) {
            throw ValidationException.illegalOperationForUnitOfWorkLifecycle(this.lifecycle, "registerObject");
        }

        //CR#2272
        logDebugMessage(object, "register");

        Object registeredObject;
        try {
            startOperationProfile(SessionProfiler.Register);

            registeredObject = internalRegisterObject(object, descriptor, false);

        } finally {
            endOperationProfile(SessionProfiler.Register);
        }
        return registeredObject;
    }

    /**
     * INTERNAL:
     * Register a new object from a nested unit of work into its parent.
     */
    public void registerOriginalNewObjectFromNestedUnitOfWork(Object originalObject, Object backupClone, Object newInstance, ClassDescriptor descriptor) {
        getCloneMapping().put(originalObject, backupClone);
        registerNewObjectClone(originalObject, newInstance, descriptor);
    }

    /**
     * INTERNAL:
     * Register this UnitOfWork against an external transaction controller
     */
    public void registerWithTransactionIfRequired() {
        if (this.parent.hasExternalTransactionController() && ! isSynchronized()) {
            //TODO: Throw an exception in case the parent is already synchronized:
            // DatabaseSession or ClientSession may have only one synchronized uow at a time.
            boolean hasAlreadyStarted = this.parent.wasJTSTransactionInternallyStarted();
            this.parent.getExternalTransactionController().registerSynchronizationListener(this, this.parent);

            // CR#2998 - registerSynchronizationListener may toggle the wasJTSTransactionInternallyStarted 
            // flag. As a result, we must compare the states and if the state is changed, then we must set the
            // setWasTransactionBegunPrematurely flag to ensure that we handle the transaction depth count 
            // appropriately
            if (!hasAlreadyStarted && this.parent.wasJTSTransactionInternallyStarted()) {
                // registerSynchronizationListener caused beginTransaction() called
                // and an external transaction internally started.
                this.setWasTransactionBegunPrematurely(true);
            }
        }
    }

    /**
     * PUBLIC:
     * Release the unit of work. This terminates this unit of work.
     * Because the unit of work operates on its own object space (clones) no work is required.
     * The unit of work should no longer be used or referenced by the application beyond this point
     * so that it can be garbage collected.
     *
     * @see #commit()
     */
    public void release() {
        if (isDead()) {
            return;
        }
        log(SessionLog.FINER, SessionLog.TRANSACTION, "release_unit_of_work");
        if (this.eventManager != null) {
            this.eventManager.preReleaseUnitOfWork();
        }

        RuntimeException exception = null;
        // If already succeeded at a writeChanges(), then transaction still open.
        // As already issued sql must at least mark the external transaction for rollback only.        
        if (this.lifecycle == CommitTransactionPending) {
            if (hasModifications() || wasTransactionBegunPrematurely()) {
                try {
                    rollbackTransaction(false);
                } catch (RuntimeException ex) {
                    exception = ex;
                }
                setWasTransactionBegunPrematurely(false);
            }
        } else if (wasTransactionBegunPrematurely() && (!this.isNestedUnitOfWork)) {
            rollbackTransaction();
            setWasTransactionBegunPrematurely(false);
        }
        releaseWriteLocks();
        setDead();
        if (shouldClearForCloseOnRelease()) {
            //uow still could be used for instantiating of ValueHolders after it's released.
            clearForClose(false);
        }
        // To be safe clean up as much state as possible.
        this.batchQueries = null;
        this.parent.releaseUnitOfWork(this);
        if (this.eventManager != null) {
            this.eventManager.postReleaseUnitOfWork();
        }
        incrementProfile(SessionProfiler.UowReleased);
        if (exception != null) {
            throw exception;
        }
    }

    /**
     * PUBLIC:
     * Empties the set of read-only classes.
     * It is illegal to call this method on nested UnitOfWork objects. A nested UnitOfWork
     * cannot have a subset of its parent's set of read-only classes.
     * Also removes classes which are read only because their descriptors are readonly
     */
    public void removeAllReadOnlyClasses() throws ValidationException {
        if (this.isNestedUnitOfWork) {
            throw ValidationException.cannotRemoveFromReadOnlyClassesInNestedUnitOfWork();
        }
        getReadOnlyClasses().clear();
    }

    /**
     * ADVANCED:
     * Remove optimistic read lock from the object
     * See forceUpdateToVersionField(Object)
     */
    public void removeForceUpdateToVersionField(Object lockObject) {
        getOptimisticReadLockObjects().remove(lockObject);
    }
    
    /**
     * INTERNAL:
     * Remove a privately owned object from the privateOwnedObjects Map.
     * The UnitOfWork needs to keep track of privately owned objects in order to
     * detect and remove private owned objects which are de-referenced.
     * When an object (which is referenced) is removed from the privateOwnedObjects Map, 
     * it is no longer considered for removal from ChangeSets and the UnitOfWork identitymap.
     */
    public void removePrivateOwnedObject(DatabaseMapping mapping, Object privateOwnedObject) {
        if (this.privateOwnedObjects != null) {
            Set objectsForMapping = this.privateOwnedObjects.get(mapping);
            if (objectsForMapping != null){
                objectsForMapping.remove(privateOwnedObject);
                if (objectsForMapping.isEmpty()) {
                    this.privateOwnedObjects.remove(mapping);
                }
            }
        }
    }

    /**
     * PUBLIC:
     * Removes a Class from the receiver's set of read-only classes.
     * It is illegal to try to send this method to a nested UnitOfWork.
     */
    public void removeReadOnlyClass(Class theClass) throws ValidationException {
        if (!canChangeReadOnlySet()) {
            throw ValidationException.cannotModifyReadOnlyClassesSetAfterUsingUnitOfWork();
        }
        if (this.isNestedUnitOfWork) {
            throw ValidationException.cannotRemoveFromReadOnlyClassesInNestedUnitOfWork();
        }
        getReadOnlyClasses().remove(theClass);

    }

    /**
     * PUBLIC:
     * Revert all changes made to any registered object.
     * Clear all deleted and new objects.
     * Revert should not be confused with release which it the normal compliment to commit.
     * Revert is more similar to commit and resume, however reverts all changes and resumes.
     * If you do not require to resume the unit of work release should be used instead.
     *
     * @see #commitAndResume()
     * @see #release()
     */
    public void revertAndResume() {
        if (isAfterWriteChangesButBeforeCommit()) {
            throw ValidationException.illegalOperationForUnitOfWorkLifecycle(this.lifecycle, "revertAndResume");
        }
        log(SessionLog.FINER, SessionLog.TRANSACTION, "revert_unit_of_work");

        MergeManager manager = new MergeManager(this);
        manager.mergeOriginalIntoWorkingCopy();
        manager.setForRefresh(true);
        manager.cascadeAllParts();
        for (Iterator cloneEnum = new IdentityHashMap(getCloneMapping()).keySet().iterator(); cloneEnum.hasNext();) {
            Object clone = cloneEnum.next();

            // Revert each clone.
            manager.mergeChanges(clone, null, this);
            ClassDescriptor descriptor = getDescriptor(clone);

            //revert the tracking policy
            descriptor.getObjectChangePolicy().revertChanges(clone, descriptor, this, getCloneMapping(), true);
        }

        // PERF: Avoid initialization of new objects if none.
        if (hasNewObjects()) {
            for (Iterator cloneEnum = getNewObjectsCloneToOriginal().keySet().iterator();
                     cloneEnum.hasNext();) {
                Object clone = cloneEnum.next();

                // De-register the object.
                getCloneMapping().remove(clone);
            }
            if (getUnitOfWorkChangeSet() != null) {
                ((UnitOfWorkChangeSet)getUnitOfWorkChangeSet()).getNewObjectChangeSets().clear();
            }
        }

        // Clear new and deleted objects.
        setNewObjectsCloneToOriginal(null);
        setNewObjectsOriginalToClone(null);
        // Reset the all clones collection
        this.allClones = null;
        // 2612538 - the default size of Map (32) is appropriate
        setObjectsDeletedDuringCommit(new IdentityHashMap());
        setDeletedObjects(new IdentityHashMap());
        setRemovedObjects(new IdentityHashMap());
        setUnregisteredNewObjects(new IdentityHashMap());
        if (this.isNestedUnitOfWork) {
            discoverAllUnregisteredNewObjectsInParent();
        }
        log(SessionLog.FINER, SessionLog.TRANSACTION, "resume_unit_of_work");
    }

    /**
     * PUBLIC:
     * Revert the object's attributes from the parent.
     * This also reverts the object privately-owned parts.
     *
     * @return the object reverted.
     * @see #shallowRevertObject(Object)
     * @see #deepRevertObject(Object)
     */
    public Object revertObject(Object clone) {
        return revertObject(clone, MergeManager.CASCADE_PRIVATE_PARTS);
    }

    /**
     * INTERNAL:
     * Revert the object's attributes from the parent.
     * This uses merging to merge the object changes.
     */
    public Object revertObject(Object clone, int cascadeDepth) {
        if (clone == null) {
            return null;
        }

        //CR#2272
        logDebugMessage(clone, "revert");

        ClassDescriptor descriptor = getDescriptor(clone);
        ObjectBuilder builder = descriptor.getObjectBuilder();
        Object implementation = builder.unwrapObject(clone, this);

        MergeManager manager = new MergeManager(this);
        manager.mergeOriginalIntoWorkingCopy();
        manager.setForRefresh(true);
        manager.setCascadePolicy(cascadeDepth);
        try {
            manager.mergeChanges(implementation, null, this);
        } catch (RuntimeException exception) {
            return handleException(exception);
        }
        if (cascadeDepth != MergeManager.NO_CASCADE) {
            builder.instantiateEagerMappings(clone, this);
        }
        return clone;
    }

    /**
     * INTERNAL:
     * This is internal to the uow, transactions should not be used explicitly in a uow.
     * The uow shares its parents transactions.
     */
    public void rollbackTransaction() throws DatabaseException {
        incrementProfile(SessionProfiler.UowRollbacks);
        this.parent.rollbackTransaction();
    }

    /**
     * INTERNAL:
     * rollbackTransaction() with a twist for external transactions.
     * <p>
     * writeChanges() is called outside the JTA beforeCompletion(), so the
     * accompanying exception won't propagate up and cause a rollback by itself.
     * <p>
     * Instead must mark the transaction for rollback only here.
     * <p>
     * If internally started external transaction or no external transaction
     * can still rollback normally.
     * @param intendedToCommitTransaction whether we were inside a commit or just trying to
     * write out changes early.
     */
    protected void rollbackTransaction(boolean intendedToCommitTransaction) throws DatabaseException {
        if (!intendedToCommitTransaction && this.parent.hasExternalTransactionController() && !this.parent.wasJTSTransactionInternallyStarted()) {            
            this.parent.getExternalTransactionController().markTransactionForRollback();
        }
        rollbackTransaction();
    }

    /**
     * INTERNAL:
     * Scans the UnitOfWork identity map for conforming instances.
     * <p>
     * Later this method can be made recursive to check all parent units of
     * work also.
     * @param selectionCriteria must be cloned and specially prepared for conforming
     * @return Map to facilitate merging with conforming instances
     * returned from a query on the database.
     */
    public Map<Object, Object> scanForConformingInstances(Expression selectionCriteria, Class referenceClass, AbstractRecord arguments, ObjectLevelReadQuery query) {
        // for bug 3568141 use the painstaking shouldTriggerIndirection if set
        int policy = query.getInMemoryQueryIndirectionPolicyState();
        if (policy != InMemoryQueryIndirectionPolicy.SHOULD_TRIGGER_INDIRECTION) {
            policy = InMemoryQueryIndirectionPolicy.SHOULD_IGNORE_EXCEPTION_RETURN_NOT_CONFORMED;
        }
        Map<Object, Object> indexedInterimResult = new IdentityHashMap<Object, Object>();
        try {
            List fromCache = null;
            if (selectionCriteria != null) {
                // assume objects that have the compared relationship 
                // untriggered do not conform as they have not been changed.
                // bug 2637555
                fromCache = getIdentityMapAccessor().getAllFromIdentityMap(selectionCriteria, referenceClass, arguments, policy);
                for (Object object : fromCache) {
                    if (!isObjectDeleted(object)) {
                        indexedInterimResult.put(object, object);
                    }
                }
            }

            // Add any new objects that conform to the query.
            List newObjects = null;
            newObjects = getAllFromNewObjects(selectionCriteria, referenceClass, arguments, policy);
            for (Object object : newObjects) {
                if (!isObjectDeleted(object)) {
                    indexedInterimResult.put(object, object);
                }
            }
        } catch (QueryException exception) {
            if (getShouldThrowConformExceptions() == THROW_ALL_CONFORM_EXCEPTIONS) {
                throw exception;
            }
        }
        return indexedInterimResult;
    }

    /**
     * INTERNAL:
     * Used to set the collections of all objects in the UnitOfWork.
     * @param newUnregisteredExistingObjects Map
     */
    protected void setAllClonesCollection(Map objects) {
        this.allClones = objects;
    }

    /**
     * INTERNAL:
     * Set the clone mapping.
     * The clone mapping contains clone of all registered objects,
     * this is required to store the original state of the objects when registered

     * so that only what is changed will be committed to the database and the parent,
     * (this is required to support parallel unit of work).
     */
    protected void setCloneMapping(Map cloneMapping) {
        this.cloneMapping = cloneMapping;
    }

    /**
     * INTERNAL:
     * This is only used for EJB entity beans to manage beans accessed in a transaction context.
     */
    protected void setContainerBeans(Map containerBeans) {
        this.containerBeans = containerBeans;
    }

    /**
     * INTERNAL:
     * This is only used for EJB entity beans to manage beans accessed in a transaction context.
     */
    protected void setContainerUnitOfWork(UnitOfWorkImpl containerUnitOfWork) {
        this.containerUnitOfWork = containerUnitOfWork;
    }

    /**
     * INTERNAL:
     * set UoW lifecycle state variable to DEATH
     */
    public void setDead() {
        setLifecycle(Death);
    }

    /**
     * INTERNAL:
     * The deleted objects stores any objects removed during the unit of work.
     * On commit they will all be removed from the database.
     */
    protected void setDeletedObjects(Map deletedObjects) {
        this.deletedObjects = deletedObjects;
    }

    /**
     * INTERNAL:
     * The life cycle tracks if the unit of work is active and is used for JTS.
     */
    protected void setLifecycle(int lifecycle) {
        this.lifecycle = lifecycle;
    }

    /**
     * INTERNAL:
     * A reference to the last used merge manager.  This is used to track locked
     * objects.
     */
    public void setMergeManager(MergeManager mergeManager) {
        this.lastUsedMergeManager = mergeManager;
    }

    /**
     * INTERNAL:
     * The new objects stores any objects newly created during the unit of work.
     * On commit they will all be inserted into the database.
     */
    protected void setNewObjectsCloneToOriginal(Map newObjects) {
        this.newObjectsCloneToOriginal = newObjects;
    }

    /**
     * INTERNAL:
     * The new objects stores any objects newly created during the unit of work.
     * On commit they will all be inserted into the database.
     */
    protected void setNewObjectsOriginalToClone(Map newObjects) {
        this.newObjectsOriginalToClone = newObjects;
    }

    /**
     * INTERNAL:
     * Set the objects that have been deleted.
     */
    public void setObjectsDeletedDuringCommit(Map deletedObjects) {
        objectsDeletedDuringCommit = deletedObjects;
    }

    /**
     * INTERNAL:
     * Set the parent.
     * This is a unit of work if nested, otherwise a database session or client session.
     */
    public void setParent(AbstractSession parent) {
        this.parent = parent;
    }

    /**
     * INTERNAL:
     * set UoW lifecycle state variable to PENDING_MERGE
     */
    public void setPendingMerge() {
        setLifecycle(MergePending);
    }

    /**
     * @param preDeleteComplete the preDeleteComplete to set
     */
    public void setPreDeleteComplete(boolean preDeleteComplete) {
        this.preDeleteComplete = preDeleteComplete;
    }

    /**
     * INTERNAL:
     * Gives a new set of read-only classes to the receiver.
     * This set of classes given are checked that subclasses of a read-only class are also
     * in the read-only set provided.
     */
    public void setReadOnlyClasses(List<Class> classes) {
        if (classes.isEmpty()) {
            this.readOnlyClasses = null;
            return;
        }
        int size = classes.size();
        this.readOnlyClasses = new HashSet<Class>(size);
        for (int index = 0; index < size; index++) {
            this.readOnlyClasses.add(classes.get(index));
        }
    }

    /**
     * INTERNAL:
     * The removed objects stores any newly registered objects removed during the nested unit of work.
     * On commit they will all be removed from the parent unit of work.
     */
    protected void setRemovedObjects(Map removedObjects) {
        this.removedObjects = removedObjects;
    }

    /**
     * INTERNAL:
     * Set if this UnitofWork should be resumed after the end of the transaction
     * Used when UnitOfWork is synchronized with external transaction control
     */
    public void setResumeUnitOfWorkOnTransactionCompletion(boolean resumeUnitOfWork) {
        this.resumeOnTransactionCompletion = resumeUnitOfWork;
    }

    /**
     * INTERNAL:
     * Set if this UnitofWork should discover new objects on commit.
     */
    public boolean shouldDiscoverNewObjects() {
        return this.shouldDiscoverNewObjects;
    }
    
    /**
     * INTERNAL:
     * Set if this UnitofWork should discover new objects on commit.
     */
    public void setShouldDiscoverNewObjects(boolean shouldDiscoverNewObjects) {
        this.shouldDiscoverNewObjects = shouldDiscoverNewObjects;
    }

    /**
     * INTERNAL:
     * True if the value holder for the joined attribute should be triggered.
     * Required by ejb30 fetch join.
     */
    public void setShouldCascadeCloneToJoinedRelationship(boolean shouldCascadeCloneToJoinedRelationship) {
        this.shouldCascadeCloneToJoinedRelationship = shouldCascadeCloneToJoinedRelationship;
    }

    /**
     * INTERNAL:
     * Calculate whether we should read directly from the database to the UOW.
     * This may be necessary in subclasses of UnitOfWork that have special behavior
     * @see RepeatableWriteUnitOfWork
     */
    public boolean shouldForceReadFromDB(ObjectBuildingQuery query, Object primaryKey){
        return false;
    }

    /**
     * ADVANCED:
     * By default new objects are not cached until the exist on the database.
     * Occasionally if mergeClone is used on new objects and is required to allow multiple merges
     * on the same new object, then if the new objects are not cached, each mergeClone will be
     * interpretted as a different new object.
     * By setting new objects to be cached mergeClone can be performed multiple times before commit.
     * New objects cannot be cached unless they have a valid assigned primary key before being registered.
     * New object with non-null invalid primary keys such as 0 or '' can cause problems and should not be used with this option.
     */
    public void setShouldNewObjectsBeCached(boolean shouldNewObjectsBeCached) {
        this.shouldNewObjectsBeCached = shouldNewObjectsBeCached;
    }

    /**
     * ADVANCED:
     * By default deletes are performed last in a unit of work.
     * Sometimes you may want to have the deletes performed before other actions.
     */
    public void setShouldPerformDeletesFirst(boolean shouldPerformDeletesFirst) {
        this.shouldPerformDeletesFirst = shouldPerformDeletesFirst;
    }

    /**
     * ADVANCED:
     * Conforming queries can be set to provide different levels of detail about the
     * exceptions they encounter
     * There are two levels:
     *    DO_NOT_THROW_CONFORM_EXCEPTIONS = 0;
     *    THROW_ALL_CONFORM_EXCEPTIONS = 1;
     */
    public void setShouldThrowConformExceptions(int shouldThrowExceptions) {
        this.shouldThrowConformExceptions = shouldThrowExceptions;
    }

    /**
     * INTERNAL:
     * Set smart merge flag.  This feature is used in WL to merge dependent values without SessionAccessor
     */
    public static void setSmartMerge(boolean option) {
        SmartMerge = option;
    }

    /**
     * INTERNAL:
     * Set isSynchronized flag to indicate that this session is a synchronized unit of work.
     */
    public void setSynchronized(boolean synched) {
        super.setSynchronized(synched);
        this.parent.setSynchronized(synched);
    }

    /**
     * INTERNAL:
     * Sets the current UnitOfWork change set to be the one passed in.
     */
    public void setUnitOfWorkChangeSet(UnitOfWorkChangeSet unitOfWorkChangeSet) {
        this.unitOfWorkChangeSet = unitOfWorkChangeSet;
    }

    /**
     * INTERNAL:
     * Used to set the unregistered existing objects vector used when validation has been turned off.
     * @param newUnregisteredExistingObjects Map
     */
    protected void setUnregisteredExistingObjects(Map newUnregisteredExistingObjects) {
        unregisteredExistingObjects = newUnregisteredExistingObjects;
    }

    /**
     * INTERNAL:
     */
    protected void setUnregisteredNewObjects(Map newObjects) {
        unregisteredNewObjects = newObjects;
    }

    /**
     * INTERNAL:
     */
    protected void setUnregisteredNewObjectsInParent(Map newObjects) {
        unregisteredNewObjectsInParent = newObjects;
    }

    /**
     * ADVANCED:
     * The unit of work performs validations such as,
     * ensuring multiple copies of the same object don't exist in the same unit of work,
     * ensuring deleted objects are not referred after commit,
     * ensures that objects from the parent cache are not referred in the unit of work cache.
     * The level of validation can be increased or decreased for debugging purposes or under
     * advanced situation where the application requires/desires to violate clone identity in the unit of work.
     * It is strongly suggested that clone identity not be violate in the unit of work.
     */
    public void setValidationLevel(int validationLevel) {
        this.validationLevel = validationLevel;
    }

    /**
     * INTERNAL:
     * Set a flag in the root UOW to indicate that a pess. locking or non-selecting SQL query was executed
     * and forced a transaction to be started.
     */
    public void setWasTransactionBegunPrematurely(boolean wasTransactionBegunPrematurely) {
        if (this.isNestedUnitOfWork) {
            ((UnitOfWorkImpl)this.parent).setWasTransactionBegunPrematurely(wasTransactionBegunPrematurely);
        }
        this.wasTransactionBegunPrematurely = wasTransactionBegunPrematurely;
    }

    /**
     * PUBLIC:
     * Merge the attributes of the clone into the unit of work copy.
     * This can be used for objects that are returned from the client through
     * RMI serialization (or other serialization mechanisms), because the RMI object will
     * be a clone this will merge its attributes correctly to preserve object identity
     * within the unit of work and record its changes.
     *
     * Only direct attributes are merged.
     *
     * @return the registered version for the clone being merged.
     * @see #mergeClone(Object)
     * @see #deepMergeClone(Object)
     */
    public Object shallowMergeClone(Object rmiClone) {
        return mergeClone(rmiClone, MergeManager.NO_CASCADE, false);
    }

    /**
     * PUBLIC:
     * Revert the object's attributes from the parent.
     * This only reverts the object's direct attributes.
     *
     * @return the object reverted.
     * @see #revertObject(Object)
     * @see #deepRevertObject(Object)
     */
    public Object shallowRevertObject(Object clone) {
        return revertObject(clone, MergeManager.NO_CASCADE);
    }

    /**
     * ADVANCED:
     * Unregister the object with the unit of work.
     * This can be used to delete an object that was just created and is not yet persistent.
     * Delete object can also be used, but will result in inserting the object and then deleting it.
     * The method will only unregister the clone, none of its parts.
     */
    public void shallowUnregisterObject(Object clone) {
        unregisterObject(clone, DescriptorIterator.NoCascading);
    }

    /**
     * INTERNAL:
     * True if the value holder for the joined attribute should be triggered.
     * Required by ejb30 fetch join.
     */
    public boolean shouldCascadeCloneToJoinedRelationship() {
        return shouldCascadeCloneToJoinedRelationship;
    }

    /**
     * ADVANCED:
     * By default new objects are not cached until the exist on the database.
     * Occasionally if mergeClone is used on new objects and is required to allow multiple merges
     * on the same new object, then if the new objects are not cached, each mergeClone will be
     * interpretted as a different new object.
     * By setting new objects to be cached mergeClone can be performed multiple times before commit.
     * New objects cannot be cached unless they have a valid assigned primary key before being registered.
     * New object with non-null invalid primary keys such as 0 or '' can cause problems and should not be used with this option.
     */
    public boolean shouldNewObjectsBeCached() {
        return shouldNewObjectsBeCached;
    }

    /**
     * Return the default to determine if does-exist should be performed on persist.
     */
    public boolean shouldValidateExistence() {
        return shouldValidateExistence;
    }

    /**
     * Set the default to determine if does-exist should be performed on persist.
     */
    public void setShouldValidateExistence(boolean shouldValidateExistence) {
        this.shouldValidateExistence = shouldValidateExistence;
    }
    
    /**
     * ADVANCED:
     * By default all objects are inserted and updated in the database before
     * any object is deleted. If this flag is set to true, deletes will be
     * performed before inserts and updates
     */
    public boolean shouldPerformDeletesFirst() {
        return shouldPerformDeletesFirst;
    }

    /**
     * ADVANCED:
     * The unit of work performs validations such as,
     * ensuring multiple copies of the same object don't exist in the same unit of work,
     * ensuring deleted objects are not referred after commit,
     * ensures that objects from the parent cache are not refered in the unit of work cache.
     * The level of validation can be increased or decreased for debugging purposes or under
     * advanced situation where the application requires/desires to violate clone identity in the unit of work.
     * It is strongly suggested that clone identity not be violate in the unit of work.
     */
    public boolean shouldPerformFullValidation() {
        return getValidationLevel() == Full;
    }

    /**
     * ADVANCED:
     * The unit of work performs validations such as,
     * ensuring multiple copies of the same object don't exist in the same unit of work,
     * ensuring deleted objects are not referred after commit,
     * ensures that objects from the parent cache are not refered in the unit of work cache.
     * The level of validation can be increased or decreased for debugging purposes or under
     * advanced situation where the application requires/desires to violate clone identity in the unit of work.
     * It is strongly suggested that clone identity not be violated in the unit of work.
     */
    public boolean shouldPerformNoValidation() {
        return getValidationLevel() == None;
    }

    /**
     * ADVANCED:
     * The unit of work performs validations such as,
     * ensuring multiple copies of the same object don't exist in the same unit of work,
     * ensuring deleted objects are not refered after commit,
     * ensures that objects from the parent cache are not refered in the unit of work cache.
     * The level of validation can be increased or decreased for debugging purposes or under
     * advanced situation where the application requires/desires to violate clone identity in the unit of work.
     * It is strongly suggested that clone identity not be violate in the unit of work.
     */
    public boolean shouldPerformPartialValidation() {
        return getValidationLevel() == Partial;
    }

    /**
     * INTERNAL:
     * Returns true if this UnitofWork should be resumed after the end of the transaction
     * Used when UnitOfWork is synchronized with external transaction control
     */
    public boolean shouldResumeUnitOfWorkOnTransactionCompletion(){
        return this.resumeOnTransactionCompletion;
    }
    
    /**
     * INTERNAL:
     * This is a JPA setting that is off by default in regular EclipseLink. It's
     * used to avoid updating the shared cache when the cacheStoreMode property
     * is set to BYPASS.
     * @see RepeatableWriteUnitOfWork.
     */
    public boolean shouldStoreBypassCache() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Store the ModifyAllQuery's from the UoW in the list. They are always
     * deferred to commit time
     */
    public void storeModifyAllQuery(DatabaseQuery query) {
        if (this.modifyAllQueries == null) {
            this.modifyAllQueries = new ArrayList<ModifyAllQuery>();
        }

        this.modifyAllQueries.add((ModifyAllQuery)query);
    }

    /**
     * INTERNAL:
     * Store the deferred UpdateAllQuery's from the UoW in the list for execution.
     */
    public void storeDeferredModifyAllQuery(DatabaseQuery query, AbstractRecord translationRow) {
        if (deferredModifyAllQueries == null) {
            deferredModifyAllQueries = new ArrayList();
        }
        deferredModifyAllQueries.add(new Object[]{query, translationRow});
    }

    /**
     * INTERNAL
     * Synchronize the clones and update their backup copies.
     * Called after commit and commit and resume.
     */
    public void synchronizeAndResume() {
        // For pessimistic locking all locks were released by commit.
        this.pessimisticLockedObjects = null;
        if (hasProperties()) {
            getProperties().remove(LOCK_QUERIES_PROPERTY);
        }

        resumeUnitOfWork();

        // The collections of clones may change in the new UnitOfWork
        this.allClones = null;
        this.removedObjects = null;
        //Reset lifecycle
        this.lifecycle = Birth;
        this.isSynchronized = false;
        this.unregisteredNewObjectsInParent = null;
        if (this.isNestedUnitOfWork) {
            discoverAllUnregisteredNewObjectsInParent();
        }
    }


    /**
     * INTERNAL:
     * Resume the unit of work state after a flush, or resume operation.
     * This will occur on commitAndResume, JPA commit and JPA flush.
     */
    public void resumeUnitOfWork() {
        // Resume new objects.
        if (hasNewObjects() && !this.isNestedUnitOfWork) {
            Iterator newEntries = this.newObjectsCloneToOriginal.entrySet().iterator();
            Map cloneToOriginals = getCloneToOriginals();
            while (newEntries.hasNext()) {
                Map.Entry entry = (Map.Entry)newEntries.next();
                Object clone = entry.getKey();
                Object original = entry.getValue();
                if (original != null) {
                    // No longer new to this unit of work, so need to store original.
                    cloneToOriginals.put(clone, original);
                }
            }
            this.newObjectsCloneToOriginal = null;
            this.newObjectsOriginalToClone = null;
        }
        this.unregisteredExistingObjects = null;
        this.unregisteredNewObjects = null;

        Map cloneMapping = getCloneMapping();
        // Clear all changes, reset backup clones.
        // PERF: only clear objects that changed.
        // The change sets include new objects as well.
        if (this.unitOfWorkChangeSet != null) {
            for (Map<ObjectChangeSet, ObjectChangeSet> objectChanges : this.unitOfWorkChangeSet.getObjectChanges().values()) {
                ClassDescriptor descriptor = null;
                for (ObjectChangeSet changeSet : objectChanges.values()) {
                    descriptor = changeSet.getDescriptor();
                    // Build backup clone for DeferredChangeDetectionPolicy or ObjectChangeTrackingPolicy,
                    // but not for AttributeChangeTrackingPolicy.
                    descriptor.getObjectChangePolicy().revertChanges(changeSet.getUnitOfWorkClone(), descriptor, this, cloneMapping, false);
                }
            }
        }
        
        // Resume deleted objects.
        // bug 4730595: fix puts deleted objects in the UnitOfWorkChangeSet as they are removed.
        this.deletedObjects = null;
        // Unregister all deleted objects,
        // keep them along with their original and backup values in unregisteredDeletedObjectsCloneToBackupAndOriginal.
        if (hasObjectsDeletedDuringCommit()) {
            if (this.unregisteredDeletedObjectsCloneToBackupAndOriginal == null) {
                this.unregisteredDeletedObjectsCloneToBackupAndOriginal = new IdentityHashMap(this.objectsDeletedDuringCommit.size());
            }
            Iterator iterator = this.objectsDeletedDuringCommit.keySet().iterator();
            Map cloneToOriginals = getCloneToOriginals();
            while (iterator.hasNext()) {
                Object deletedObject = iterator.next();
                Object[] backupAndOriginal = {cloneMapping.get(deletedObject), cloneToOriginals.get(deletedObject)};
                this.unregisteredDeletedObjectsCloneToBackupAndOriginal.put(deletedObject, backupAndOriginal);
                // If object exists in IM remove it from the IM and also from clone mapping.
                getIdentityMapAccessorInstance().removeFromIdentityMap(deletedObject);
                cloneMapping.remove(deletedObject);
            }
        }
        this.objectsDeletedDuringCommit = null;

        // Clean up, new objects are now existing.
        this.unitOfWorkChangeSet = null;
        this.changeTrackedHardList = null;
    }
    
    /**
     * INTERNAL:
     * This method is used to transition an object from the deleted objects list
     * to be simply be register.
     */
    protected void undeleteObject(Object object) {
        getDeletedObjects().remove(object);
        if (this.parent.isUnitOfWork()) {
            ((UnitOfWorkImpl)this.parent).undeleteObject(object);
        }
    }
    /**
     * PUBLIC:
     * Unregister the object with the unit of work.
     * This can be used to delete an object that was just created and is not yet persistent.
     * Delete object can also be used, but will result in inserting the object and then deleting it.
     * The method will only unregister the object and its privately owned parts
     */
    public void unregisterObject(Object clone) {
        unregisterObject(clone, DescriptorIterator.CascadePrivateParts);
    }

    /**
     * INTERNAL:
     * Unregister the object with the unit of work.
     * This can be used to delete an object that was just created and is not yet persistent.
     * Delete object can also be used, but will result in inserting the object and then deleting it.
     */
    public void unregisterObject(Object clone, int cascadeDepth) {
        unregisterObject(clone, cascadeDepth, false);
    }
    /**
     * INTERNAL:
     * Unregister the object with the unit of work.
     * This can be used to delete an object that was just created and is not yet persistent.
     * Delete object can also be used, but will result in inserting the object and then deleting it.
     */
    public void unregisterObject(Object clone, int cascadeDepth, boolean forDetach) {
        // Allow register to be called with null and just return true
        if (clone == null) {
            return;
        }
        //CR#2272
        logDebugMessage(clone, "unregister");
        Object implementation = getDescriptor(clone).getObjectBuilder().unwrapObject(clone, this);

        // This define an inner class for process the itteration operation, don't be scared, its just an inner class.
        DescriptorIterator iterator = new DescriptorIterator() {
            public void iterate(Object object) {
                if (isClassReadOnly(object.getClass(), getCurrentDescriptor())) {
                    setShouldBreak(true);
                    return;
                }
                
                // Check if object exists in the IM.
                Object primaryKey = getCurrentDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(object, UnitOfWorkImpl.this, true);
                if (primaryKey != null) {
                    // If object exists in IM remove it from the IM and also from clone mapping.
                    getIdentityMapAccessorInstance().removeFromIdentityMap(primaryKey, object.getClass(), getCurrentDescriptor(), object);
                }
                getCloneMapping().remove(object);
                
                //remove from deleted objects.
                if (hasDeletedObjects()) {
                    getDeletedObjects().remove(object);
                }

                // Remove object from the new object cache				
                // PERF: Avoid initialization of new objects if none.
                if (hasNewObjects()) {
                    Object original = getNewObjectsCloneToOriginal().remove(object);
                    if (original != null) {
                        getNewObjectsOriginalToClone().remove(original);
                    }
                    // Also need to remove the original merged object.
                    if (UnitOfWorkImpl.this.newObjectsCloneToMergeOriginal != null) {
                        original = UnitOfWorkImpl.this.newObjectsCloneToMergeOriginal.remove(object);
                        if (original != null) {
                            getNewObjectsOriginalToClone().remove(original);
                        }
                    }
                }
            }
        };

        iterator.setSession(this);
        iterator.setCascadeDepth(cascadeDepth);
        if (forDetach){
            CascadeCondition detached = iterator.new CascadeCondition(){
                public boolean shouldNotCascade(DatabaseMapping mapping){
                    return ! (mapping.isForeignReferenceMapping() && ((ForeignReferenceMapping)mapping).isCascadeDetach());
                }
            };
            iterator.setCascadeCondition(detached);
            iterator.setShouldIterateOverUninstantiatedIndirectionObjects(false);
        }
        iterator.setShouldIterateOnFetchGroupAttributesOnly(true);
        iterator.startIterationOn(implementation);
    }

    /**
     * INTERNAL:
     * This method is used internally to update the tracked objects if required
     */
    public void updateChangeTrackersIfRequired(Object objectToWrite, ObjectChangeSet changeSetToWrite, UnitOfWorkImpl uow, ClassDescriptor descriptor) {
        //this is a no op in this unitOfWork Class see subclasses for implementation.
    }

    /**
     * INTERNAL:
     * On persist and flush operations we must update any derived id fields.
     */
    protected Object updateDerivedIds(Object clone, ClassDescriptor descriptor) {
        Object key = null;
        
        if (descriptor.hasDerivedId()) {
            for (DatabaseMapping derivesIdMapping : descriptor.getDerivesIdMappinps()) {
                DatabaseMapping derivedIdMapping = derivesIdMapping.getDerivedIdMapping();
                
                // If there is no derived id mapping, then there is no update required. Case #1a-#6a 
                // from the JPA spec.
                if (derivedIdMapping != null) {
                    ClassDescriptor parentDescriptor = derivesIdMapping.getReferenceDescriptor();
                    Object parentClone = derivesIdMapping.getRealAttributeValueFromObject(clone, this);
                    
                    // If the parent clone is null, we don't have any work to do, continue to the next 
                    // mapping. Some mappings may be part of a composite primary key that allows for a 
                    // null setting or the mapping may just not be set.
                    if (parentClone != null) {
                        // Recurse up the chain to figure out the key. The first dependent will figure 
                        // it out and pass it to its sub-dependents (keeping it the same)
                        if (parentDescriptor.hasDerivedId()) {
                            key = updateDerivedIds(parentClone, parentDescriptor);
                        } else {
                            key = parentDescriptor.getCMPPolicy().createPrimaryKeyInstance(parentClone, this);
                        }
                        
                        if (derivesIdMapping.hasMapsIdValue()) {
                            // Case #1b, #2b and #3b from the JPA spec. The derived id is within our 
                            // embedded id. We need to deal with that object and its mapping within the clone.
                            Object aggregateClone = derivedIdMapping.getRealAttributeValueFromObject(clone, this);
                            
                            // If the aggregate clone is null, create one and set it on the clone.
                            if (aggregateClone == null) {
                                aggregateClone = derivedIdMapping.getReferenceDescriptor().getObjectBuilder().buildNewInstance();
                                derivedIdMapping.setRealAttributeValueInObject(clone, aggregateClone);
                            }
                            
                            // Now get the actual derived id mapping from the aggregate and populate it on the aggregate clone.
                            DatabaseMapping aggregateMapping = derivedIdMapping.getReferenceDescriptor().getObjectBuilder().getMappingForAttributeName(derivesIdMapping.getMapsIdValue());
                            aggregateMapping.setRealAttributeValueInObject(aggregateClone, key);
                            
                            // The key should be the aggregate clone when we are done.
                            key = aggregateClone;
                        } else {
                            // Case #4b, #5b, #6b from the JPA spec. Our id mapping is the derived id. 
                            // We will deal with the clone provided.
                            derivedIdMapping.setRealAttributeValueInObject(clone, key);
                        }
                    }
                }
            }
        }
        
        // Return the key once we have had an opportunity to update all the
        // parts of it.
        return key;
    }
    
    /**
     * ADVANCED:
     * This can be used to help debugging an object-space corruption.
     * An object-space corruption is when your application has incorrectly related a clone to an original object.
     * This method will validate that all registered objects are in a correct state and throw
     * an error if not,  it will contain the full stack of object references in the error message.
     * If you call this method after each register or change you perform it will pin-point where the error was made.
     */
    public void validateObjectSpace() {
        log(SessionLog.FINER, SessionLog.TRANSACTION, "validate_object_space");
        // This define an inner class for process the iteration operation, don't be scared, its just an inner class.
        DescriptorIterator iterator = new DescriptorIterator() {
            public void iterate(Object object) {
                try {
                    if (isClassReadOnly(object.getClass(), getCurrentDescriptor())) {
                        setShouldBreak(true);
                        return;
                    } else {
                        getBackupClone(object, getCurrentDescriptor());
                    }
                } catch (EclipseLinkException exception) {
                    log(SessionLog.FINEST, SessionLog.TRANSACTION, "stack_of_visited_objects_that_refer_to_the_corrupt_object", getVisitedStack());
                    log(SessionLog.FINER, SessionLog.TRANSACTION, "corrupt_object_referenced_through_mapping", getCurrentMapping());
                    throw exception;
                }
            }
        };

        iterator.setSession(this);
        for (Iterator clonesEnum = getCloneMapping().keySet().iterator(); clonesEnum.hasNext();) {
            iterator.startIterationOn(clonesEnum.next());
        }
    }

    /**
     * INTERNAL:
     * Indicates if a transaction was begun by a pessimistic locking or non-selecting query.
     * Traverse to the root UOW to get value.
     */

    // * 2.5.1.8 Nov 17, 2000 JED
    // * Prs 25751 Changed to make this method public
    public boolean wasTransactionBegunPrematurely() {
        if (this.isNestedUnitOfWork) {
            return ((UnitOfWorkImpl)this.parent).wasTransactionBegunPrematurely();
        }
        return wasTransactionBegunPrematurely;
    }
    
    /**
     * INTERNAL:
     * A query execution failed due to an invalid query.
     * Re-connect and retry the query.
     */
    @Override
    public Object retryQuery(DatabaseQuery query, AbstractRecord row, DatabaseException databaseException, int retryCount, AbstractSession executionSession) {
        return getParent().retryQuery(query, row, databaseException, retryCount, executionSession);
    }

    /**
     * ADVANCED: Writes all changes now before commit().
     * The commit process will begin and all changes will be written out to the datastore, but the datastore transaction will not
     * be committed, nor will changes be merged into the global cache.
     * <p>
     * A subsequent commit (on UnitOfWork or global transaction) will be required to finalize the commit process.
     * <p>
     * As the commit process has begun any attempt to register objects, or execute object-level queries will
     * generate an exception.  Report queries, non-caching queries, and data read/modify queries are allowed.
     * <p>
     * On exception any global transaction will be rolled back or marked rollback only.  No recovery of this UnitOfWork will be possible.
     * <p>
     * Can only be called once.  It can not be used to write out changes in an incremental fashion.
     * <p>
     * Use to partially commit a transaction outside of a JTA transaction's callbacks.  Allows you to get back any exception directly.
     * <p>
     * Use to commit a UnitOfWork in two stages.
     */
    public void writeChanges() {
        if (!isActive()) {
            throw ValidationException.inActiveUnitOfWork("writeChanges");
        }
        if (isAfterWriteChangesButBeforeCommit()) {
            throw ValidationException.cannotWriteChangesTwice();
        }
        if (this.isNestedUnitOfWork) {
            throw ValidationException.writeChangesOnNestedUnitOfWork();
        }
        log(SessionLog.FINER, SessionLog.TRANSACTION, "begin_unit_of_work_flush");
        mergeBmpAndWsEntities();
        if (this.eventManager != null) {
            this.eventManager.preCommitUnitOfWork();
        }
        setLifecycle(CommitPending);
        try {
            commitToDatabaseWithChangeSet(false);
            //bug:5526260 - flush batch mechanisms
            writesCompleted();
        } catch (RuntimeException exception) {
            setLifecycle(WriteChangesFailed);
            throw exception;
        }
        setLifecycle(CommitTransactionPending);
        log(SessionLog.FINER, SessionLog.TRANSACTION, "end_unit_of_work_flush");
    }

    /**
     * INTERNAL:
     * This method notifies the accessor that a particular sets of writes has
     * completed.  This notification can be used for such thing as flushing the
     * batch mechanism
     */
    public void writesCompleted() {
        this.parent.writesCompleted();
    }

    /**
     * log the message and debug info if option is set. (reduce the duplicate codes)
     */
    private void logDebugMessage(Object object, String debugMessage) {
        log(SessionLog.FINEST, SessionLog.TRANSACTION, debugMessage, object);
    }

    /**
     * INTERNAL:
     * When in transaction batch read objects must use query local
     * to the unit of work.
     */
    public Map<ReadQuery, ReadQuery> getBatchQueries() {
        if (batchQueries == null) {
            // 2612538 - the default size of Map (32) is appropriate
            batchQueries = createMap();
        }
        return batchQueries;
    }

    /**
     * INTERNAL:
     * When in transaction batch read objects must use query local
     * to the unit of work.
     */
    public void setBatchQueries(Map<ReadQuery, ReadQuery> batchQueries) {
        this.batchQueries = batchQueries;
    }

    /**
     * INTERNAL:
     */
    public Map getPessimisticLockedObjects() {
        if (pessimisticLockedObjects == null) {
            // 2612538 - the default size of Map (32) is appropriate
            pessimisticLockedObjects = new IdentityHashMap();
        }
        return pessimisticLockedObjects;
    }
    
    public void addToChangeTrackedHardList(Object obj){
    	if (this.referenceMode != ReferenceMode.HARD){
    		this.getChangeTrackedHardList().add(obj);
    	}
    }
    
    /**
     * INTERNAL:
     */
    public void addPessimisticLockedClone(Object clone) {
        log(SessionLog.FINEST, SessionLog.TRANSACTION, "tracking_pl_object", clone, Integer.valueOf(this.hashCode()));
        getPessimisticLockedObjects().put(clone, clone);
    }

    /**
     * INTERNAL:
     * Add a privately owned object to the privateOwnedObjectsMap.
     * The UnitOfWork needs to keep track of privately owned objects in order to
     * detect and remove private owned objects which are de-referenced.
     */
    public void addPrivateOwnedObject(DatabaseMapping mapping, Object privateOwnedObject) {
        // only allow mapped, non-null objects to be added
        if (privateOwnedObject != null && getDescriptor(privateOwnedObject) != null) {
            Map<DatabaseMapping, Set> privateOwnedObjects = getPrivateOwnedObjects();
            Set objectsForMapping = privateOwnedObjects.get(mapping);
            if (objectsForMapping == null) {
                objectsForMapping = new IdentityHashSet();
                privateOwnedObjects.put(mapping, objectsForMapping);
            }
            objectsForMapping.add(privateOwnedObject);
        }
    }

    /**
     * INTERNAL:
     * Return if the clone has been pessimistic locked in this unit of work.
     */
    public boolean isPessimisticLocked(Object clone) {
        return (this.pessimisticLockedObjects != null )&& this.pessimisticLockedObjects.containsKey(clone);
    }

    /**
     * @return the preDeleteComplete
     */
    public boolean isPreDeleteComplete() {
        return preDeleteComplete;
    }

    /**
     * INTERNAL:
     * True if either DataModifyQuery or ModifyAllQuery was executed.
     * In absense of transaction the query execution starts one, therefore
     * the flag may only be true in transaction, it's reset on commit or rollback.
     */
    public void setWasNonObjectLevelModifyQueryExecuted(boolean wasNonObjectLevelModifyQueryExecuted) {
        this.wasNonObjectLevelModifyQueryExecuted = wasNonObjectLevelModifyQueryExecuted;
    }
    
    /**
     * INTERNAL:
     * True if either DataModifyQuery or ModifyAllQuery was executed.
     */
    public boolean wasNonObjectLevelModifyQueryExecuted() {
        return wasNonObjectLevelModifyQueryExecuted;
    }
    
    /**
      * INTERNAL:
      * Indicates whether readObject should return the object read from the db
      * in case there is no object in uow cache (as opposed to fetching the object from 
      * parent's cache). Note that wasNonObjectLevelModifyQueryExecuted()==true implies inTransaction()==true.
      */
    public boolean shouldReadFromDB() {
        return wasNonObjectLevelModifyQueryExecuted();
    }

    /**
     * INTERNAL:
     * Release the read connection to the read connection pool.
     */
    public void releaseReadConnection(Accessor connection) {
        //bug 4668234 -- used to only release connections on server sessions but should always release
        this.parent.releaseReadConnection(connection);
    }

    /**
     * INTERNAL:
     * This method will clear all registered objects from this UnitOfWork.
     * If parameter value is 'true' then the cache(s) are cleared, too.
     */
    public void clear(boolean shouldClearCache) {
        this.cloneToOriginals = null;
        this.cloneMapping = null;
        this.newObjectsCloneToOriginal = null;
        this.newObjectsOriginalToClone = null;
        this.deletedObjects = null;
        this.allClones = null;
        this.objectsDeletedDuringCommit = null;
        this.removedObjects = null;
        this.unregisteredNewObjects = null;
        this.unregisteredExistingObjects = null;
        this.newAggregates = null;
        this.unitOfWorkChangeSet = null;
        this.pessimisticLockedObjects = null;
        this.optimisticReadLockObjects = null;
        this.batchQueries = null;
        this.privateOwnedObjects = null;
        if(shouldClearCache) {
            clearIdentityMapCache();
        }
    }
    
    /**
     * INTERNAL:
     * Clear the identityMaps
     */
    private void clearIdentityMapCache() {
        getIdentityMapAccessor().initializeIdentityMaps();
        if (this.parent instanceof IsolatedClientSession) {
            this.parent.getIdentityMapAccessor().initializeIdentityMaps();
        }
    }
        
    /**
     * INTERNAL:
     * Call this method if the uow will no longer be used for committing transactions:
     * all the change sets will be dereferenced, and (optionally) the cache cleared.
     * If the uow is not released, but rather kept around for ValueHolders, then identity maps shouldn't be cleared:
     * the parameter value should be 'false'. The lifecycle set to Birth so that uow ValueHolder still could be used.
     * Alternatively, if called from release method then everything should go and therefore parameter value should be 'true'.
     * In this case lifecycle won't change - uow.release (optionally) calls this method when it (uow) is already dead.
     * The reason for calling this method from release is to free maximum memory right away:
     * the uow might still be referenced by objects using UOWValueHolders (though they shouldn't be around
     * they still might).
     * We defer a clear() call to release() if the uow lifecycle is 1,2 or 4 (*Pending).
     */
    public void clearForClose(boolean shouldClearCache) {
        clear(shouldClearCache);
        if (isActive()) {
            //Reset lifecycle
            this.lifecycle = Birth;
            this.isSynchronized = false;
        }
    }
    
    /**
     * INTERNAL:
     * Indicates whether clearForClose method should be called by release method.
     */
    public boolean shouldClearForCloseOnRelease() {
        return false;
    }    
    
    /**
     * INTERNAL:
     * Copy statements counts into UOW properties.
     */
    private void copyStatementsCountIntoProperties(){
        Accessor accessor = null;
        try {
            accessor = getAccessor();
        } catch(DatabaseException exception){
            //ignore for bug 290703
        }
        if(accessor!=null && accessor instanceof DatasourceAccessor){
            getProperties().put(DatasourceAccessor.READ_STATEMENTS_COUNT_PROPERTY,Integer.valueOf(((DatasourceAccessor)accessor).getReadStatementsCount()));
            getProperties().put(DatasourceAccessor.WRITE_STATEMENTS_COUNT_PROPERTY,Integer.valueOf(((DatasourceAccessor)accessor).getWriteStatementsCount()));
            getProperties().put(DatasourceAccessor.STOREDPROCEDURE_STATEMENTS_COUNT_PROPERTY,Integer.valueOf(((DatasourceAccessor)accessor).getStoredProcedureStatementsCount()));
        }
    }
  
    /**
     * This method is used internally to create a map to hold the persistenceContexts.  A weak map is returned if ReferenceMode is weak. 
     */
    protected Map createMap(){
        if (this.referenceMode != null && this.referenceMode != ReferenceMode.HARD) return new IdentityWeakHashMap();
        return new IdentityHashMap();
    }
    /**
     * This method is used internally to create a map to hold the persistenceContexts.  A weak map is returned if ReferenceMode is weak.  
     * 
     *  @param size
     */ 
    protected Map createMap(int size){
        if (this.referenceMode != null && this.referenceMode != ReferenceMode.HARD) return new IdentityWeakHashMap(size);
        return new IdentityHashMap(size);
    }
    /**
     * This method is used internally to clone a map that holds the persistenceContexts.  A weak map is returned if ReferenceMode is weak.  
     * 
     */

    protected Map cloneMap(Map map){
        // bug 270413.  This method is needed to avoid the class cast exception when the reference mode is weak.
    	if (this.referenceMode != null && this.referenceMode != ReferenceMode.HARD) return (IdentityWeakHashMap)((IdentityWeakHashMap)map).clone();
        return (IdentityHashMap)((IdentityHashMap)map).clone();
    }

    
    public ReferenceMode getReferenceMode() {
        return referenceMode;
    }

    /**
     * INTERNAL:
     * Return the list of object with changes.
     * This is used in weak reference mode to avoid garbage collection of changed objects.
     */
    public Set<Object> getChangeTrackedHardList() {
        if (this.changeTrackedHardList == null) {
            this.changeTrackedHardList = new IdentityHashSet();
        }
        return this.changeTrackedHardList;
    }
    
    /**
     * Get an instance, whose state may be lazily fetched.
     * If the requested instance does not exist in the database, null is returned, or the object will fail when accessed.
     * The instance will be lazy when it does not exist in the cache, and supports fetch groups.
     * @param primaryKey - The primary key of the object, either as a List, singleton, IdClass or an instance of the object.
     */
    public Object getReference(Class theClass, Object id) {
        ClassDescriptor descriptor = getDescriptor(theClass);
        if (descriptor == null || descriptor.isDescriptorTypeAggregate()) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("unknown_bean_class", new Object[] { theClass }));
        }
        Object reference;
        if (id == null) { //gf721 - check for null PK
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_pk"));
        }
        Object primaryKey;
        if (id instanceof List) {
            if (descriptor.getCachePolicy().getCacheKeyType() == CacheKeyType.ID_VALUE) {
                if (((List)id).isEmpty()) {
                    primaryKey = null;
                } else {
                    primaryKey = ((List)id).get(0);
                }
            } else {
                primaryKey = new CacheId(((List)id).toArray());
            }
        } else if (id instanceof CacheId) {
            primaryKey = id;
        } else {
            if (descriptor.getCMPPolicy() != null) {
                if (descriptor.getCMPPolicy().getPKClass() != null && !descriptor.getCMPPolicy().getPKClass().isAssignableFrom(id.getClass())) {
                    throw new IllegalArgumentException(ExceptionLocalization.buildMessage("invalid_pk_class", new Object[] { descriptor.getCMPPolicy().getPKClass(), id.getClass() }));
                }
                primaryKey = descriptor.getCMPPolicy().createPrimaryKeyFromId(id, this);
            } else {
                if (!id.getClass().equals(theClass)) {
                    primaryKey = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(id, this);
                } else {
                    primaryKey = id;
                }
            }
        }
        // If the class supports fetch groups then return a un-fetched instance.
        if (descriptor.hasFetchGroupManager()) {
            reference = getIdentityMapAccessor().getFromIdentityMap(primaryKey, theClass);
            if (reference == null) {
                if ((id instanceof List) || (id instanceof CacheId) || (descriptor.getCMPPolicy() == null)) {
                    AbstractRecord row = descriptor.getObjectBuilder().buildRowFromPrimaryKeyValues(primaryKey, this);
                    reference = descriptor.getObjectBuilder().buildNewInstance();
                    descriptor.getObjectBuilder().buildPrimaryKeyAttributesIntoObject(reference, row, new ReadObjectQuery(), this);
                } else {
                    reference = descriptor.getCMPPolicy().createBeanUsingKey(id, this);
                }
                descriptor.getFetchGroupManager().getIdEntityFetchGroup().setOnEntity(reference, this);
                reference = registerExistingObject(reference);
            }
        } else {
            ReadObjectQuery query = new ReadObjectQuery(descriptor.getJavaClass());
            query.setSelectionId(primaryKey);
            query.conformResultsInUnitOfWork();
            query.setIsExecutionClone(true);
            reference = executeQuery(query);
        }
        return reference;
    }

    /**
     * INTERNAL:
     * 272022: Avoid releasing locks on the wrong server thread.
     * If the current thread and the active thread on the mutex do not match - switch them
     * Before we release acquired locks (do the same as we do for mergeClonesBeforeCompletion())
     * Check that the current thread is the active thread on all lock managers by 
     * checking the cached lockThread on the mergeManager.
     * If we find that these 2 threads are different - then all threads in the acquired locks list are different.
     * Switch the activeThread on the mutex to this current thread for each lock.
     * @return true if threads were switched
     */
    public boolean verifyMutexThreadIntegrityBeforeRelease() {        
        if (this.lastUsedMergeManager != null) { // mergeManager may be null in a com.ibm.tx.jta.RegisteredSyncs.coreDistributeAfter() afterCompletion() callback
            Thread currentThread = Thread.currentThread();
            Thread lockThread = this.lastUsedMergeManager.getLockThread();
            if (currentThread != lockThread) {
                if (ConcurrencyManager.getDeferredLockManager(lockThread) != null){
                    // check for transitioned old deferred lock manager and switch to the new thread.
                    ConcurrencyManager.deferredLockManagers.put(
                        currentThread, ConcurrencyManager.deferredLockManagers.remove(lockThread));
                }
                ArrayList<CacheKey> locks = this.getMergeManager().getAcquiredLocks();        
                if (null != locks) {                
                    Iterator<CacheKey> locksIterator = locks.iterator();
                    log(SessionLog.FINER, AbstractSessionLog.CACHE, "active_thread_is_different_from_current_thread", 
                            lockThread, getMergeManager(), currentThread);
                    while (locksIterator.hasNext()) {
                        ConcurrencyManager lockMutex = locksIterator.next();
                        if (null != lockMutex) {
                            Thread activeThread = lockMutex.getActiveThread();
                            // check for different acquire and release threads
                            if (currentThread != activeThread) {
                                // Switch activeThread to currentThread - we will release the lock later
                                lockMutex.setActiveThread(currentThread);
                            }
                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * ADVANCED:
     * Return if updates should be ordered by primary key to avoid possible database deadlocks.
     */
    public boolean shouldOrderUpdates() {
        return shouldOrderUpdates;
    }

    /**
     * ADVANCED:
     * Set updates should be ordered by primary key to avoid possible database deadlocks.
     */
    public void setShouldOrderUpdates(boolean shouldOrderUpdates) {
        this.shouldOrderUpdates = shouldOrderUpdates;
    }

    @Override
    public DatabaseValueHolder createCloneQueryValueHolder(ValueHolderInterface attributeValue, Object clone, AbstractRecord row, ForeignReferenceMapping mapping) {
        return new UnitOfWorkQueryValueHolder(attributeValue, clone, mapping, row, this);
    }

    @Override
    public DatabaseValueHolder createCloneTransformationValueHolder(ValueHolderInterface attributeValue, Object original, Object clone, AbstractTransformationMapping mapping) {
        return new UnitOfWorkTransformerValueHolder(attributeValue, original, clone, mapping, this);
    }

    /**
     * INTERNAL:
     * Return deleted objects that have reference to other deleted objects.
     * This is need to delete cycles of objects in the correct order.
     */
    public Map<Object, Set<Object>> getDeletionDependencies() {
        if (this.deletionDependencies == null) {
            this.deletionDependencies = new HashMap<Object, Set<Object>>();
        }
        return this.deletionDependencies;
    }

    /**
     * INTERNAL:
     * Record deleted objects that have reference to other deleted objects.
     * This is need to delete cycles of objects in the correct order.
     */
    public void addDeletionDependency(Object target, Object source) {
        if (this.deletionDependencies == null) {
            this.deletionDependencies = new HashMap<Object, Set<Object>>();
        }
        Set<Object> dependencies = this.deletionDependencies.get(target);
        if (dependencies == null) {
            dependencies = new HashSet<Object>();
            this.deletionDependencies.put(target, dependencies);
        }
        dependencies.add(source);
    }

    /**
     * INTERNAL:
     * Return references to other deleted objects for this deleted object.
     * This is need to delete cycles of objects in the correct order.
     */
    public Set<Object> getDeletionDependencies(Object deletedObject) {
        if (this.deletionDependencies == null) {
            return null;
        }
        return this.deletionDependencies.get(deletedObject);
    }

}
