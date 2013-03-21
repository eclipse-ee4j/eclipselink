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
 *     07/15/2011-2.2.1 Guy Pelletier 
 *       - 349424: persists during an preCalculateUnitOfWorkChangeSet event are lost
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.remote;

import java.util.*;

import org.eclipse.persistence.config.ReferenceMode;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.logging.SessionLog;

/**
 * Counter part of the unit of work which exists on the client side.
 */
public class RemoteUnitOfWork extends RepeatableWriteUnitOfWork {
    protected List newObjectsCache;
    protected List unregisteredNewObjectsCache;
    protected boolean isOnClient;
    protected transient RemoteSessionController parentSessionController;
    protected boolean isFlush;

    public RemoteUnitOfWork() {
    }
    
    public RemoteUnitOfWork(RemoteUnitOfWork parent) {
        this(parent, null);
    }

    public RemoteUnitOfWork(DistributedSession parent) {
        this(parent, null);
    }
    public RemoteUnitOfWork(RemoteUnitOfWork parent, ReferenceMode referenceMode) {
        super(parent, referenceMode);
        this.isOnClient = true;
        this.discoverUnregisteredNewObjectsWithoutPersist = true;
    }

    public RemoteUnitOfWork(DistributedSession parent, ReferenceMode referenceMode) {
        super(parent, referenceMode);
        this.isOnClient = true;
        this.discoverUnregisteredNewObjectsWithoutPersist = true;
    }

    public boolean isFlush() {
        return isFlush;
    }

    public void setIsFlush(boolean isFlush) {
        this.isFlush = isFlush;
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
        // Acquire the mutex so session knows it is in a transaction.
        getParent().getTransactionMutex().acquire();
        startOperationProfile(SessionProfiler.Remote, null, SessionProfiler.ALL);
        // This needs a special call for remote, to ensure subsequent queries isolate their data to a unit of work on the server.
        ((DistributedSession)getParent()).getRemoteConnection().beginEarlyTransaction();
        endOperationProfile(SessionProfiler.Remote, null, SessionProfiler.ALL);
        
        setWasTransactionBegunPrematurely(true);
    }
    
    /**
     * The nested unit of work must also be remote.
     */
    @Override
    public UnitOfWorkImpl acquireUnitOfWork() {
        return acquireUnitOfWork(null);
    }

    /**
     * The nested unit of work must also be remote.
     */
    @Override
    public UnitOfWorkImpl acquireUnitOfWork(ReferenceMode referenceMode) {
        log(SessionLog.FINER, SessionLog.TRANSACTION, "acquire_unit_of_work");
        setNumberOfActiveUnitsOfWork(getNumberOfActiveUnitsOfWork() + 1);
        RemoteUnitOfWork ruow = new RemoteUnitOfWork(this, referenceMode);
        ruow.discoverAllUnregisteredNewObjectsInParent();
        return ruow;
    }

    /**
     * This is done to maintain correspondence between local new objects and returned new objects from serialization.
     * Object correspondence is maintained by comparing primary keys but for new objects it is possible that primary
     * key value is null as it is still not inserted. The returned new objects from serialization will have primary
     * key value which will be inserted into corresponding local new objects.
     */
    protected List collectNewObjects() {
        if ((this.newObjectsCloneToOriginal == null) || this.newObjectsCloneToOriginal.isEmpty()) {
            return null;
        }      
        List newObjects = new ArrayList(this.newObjectsCloneToOriginal.size());
        for (Object newObject : this.newObjectsCloneToOriginal.keySet()) {
            newObjects.add(newObject);
        }
        return newObjects;
    }

    /**
     * This is done to maintain correspondence between local unregistered new objects and returned unregistered new
     * objects from serialization. Object correspondence is maintained by comparing primary keys but for unregistered
     * new objects it is possible that primary key value is null as it is still not inserted. The returned unregistered
     * new objects from serialization will have primary key value which will be inserted into corresponding local new
     * objects.
     */
    protected List collectUnregisteredNewObjects() {
        discoverAllUnregisteredNewObjects();
        return new ArrayList(getUnregisteredNewObjects().values());
    }

    /**
     * The remote unit of work returned after its commit on the server is merged with remote unit of work
     * on the remote side.
     */
    protected void commitIntoRemoteUnitOfWork() {
        UnitOfWorkImpl parent = ((UnitOfWorkImpl)getParent());
        // Must merge the transaction flag.
        parent.setWasTransactionBegunPrematurely(wasTransactionBegunPrematurely());
        
        MergeManager manager = new MergeManager(this);
        manager.mergeWorkingCopyIntoRemote();

        // Must clone the clone mapping because entries can be added to it during the merging,
        // and that can lead to concurrency problems.
        Iterator cloneIterator = new IdentityHashMap(getCloneMapping()).keySet().iterator();

        Map clones = new IdentityHashMap(this.cloneMapping.size());
        // Iterate over each clone and let the object build merge to clones into the originals.
        while (cloneIterator.hasNext()) {
            Object remoteClone = cloneIterator.next();
            manager.mergeChanges(remoteClone, null, this);
            Object clone = manager.getTargetVersionOfSourceObject(remoteClone, parent.getDescriptor(remoteClone), parent);
            clones.put(remoteClone, clone);
        }
        
        // Reset the remote change set to be the local one, need to reset all clones to local copy,
        // and reset transient variables.
        parent.setUnitOfWorkChangeSet(this.unitOfWorkChangeSet);
        fixRemoteChangeSet(this.unitOfWorkChangeSet, clones, parent);
        ((RemoteUnitOfWork)parent).setCumulativeUOWChangeSet(this.cumulativeUOWChangeSet);
        fixRemoteChangeSet(this.cumulativeUOWChangeSet, clones, parent);
        
        // Set the deleted objects into the parent.
        if (this.objectsDeletedDuringCommit != null) {
            Map newDeletedObjects = new IdentityHashMap();
            for (Object deletedObject : this.objectsDeletedDuringCommit.keySet()) {
                Object primaryKey = getId(deletedObject);
                Object clone = clones.get(deletedObject);
                if (clone == null) {
                    clone = parent.getIdentityMapAccessor().getFromIdentityMap(primaryKey, deletedObject.getClass());
                    if (clone == null) {
                        clone = deletedObject;
                    }
                }
                newDeletedObjects.put(clone, primaryKey);
                parent.getIdentityMapAccessor().removeFromIdentityMap(primaryKey, clone.getClass());
            }
            parent.setObjectsDeletedDuringCommit(newDeletedObjects);
        }
    }

    /**
     * Simulate a flush, current just begins a transaction and commits.
     */
    @Override
    public void writeChanges() {
        if (!isOnClient()) {
            super.writeChanges();
            return;
        }
        // Check for a nested flush and return early if we are in one
        if (this.isWithinFlush()) {
            log(SessionLog.WARNING, SessionLog.TRANSACTION, "nested_entity_manager_flush_not_executed_pre_query_changes_may_be_pending", getClass().getSimpleName());
            return;
        }
        log(SessionLog.FINER, SessionLog.TRANSACTION, "begin_unit_of_work_flush");
        
        // PERF: If this is an empty unit of work, do nothing (but still may need to commit SQL changes).
        boolean hasChanges = (this.unitOfWorkChangeSet != null) || hasCloneMapping() || hasDeletedObjects() || hasModifyAllQueries() || hasDeferredModifyAllQueries();
        if (hasChanges) {
            // The change set may already exist if using change tracking.
            if (this.unitOfWorkChangeSet == null) {
                this.unitOfWorkChangeSet = new UnitOfWorkChangeSet(this);
            }
            calculateChanges(getCloneMapping(), this.unitOfWorkChangeSet, true, true);
            hasChanges = hasModifications();
        }
        if (!hasChanges) {
            log(SessionLog.FINER, SessionLog.TRANSACTION, "end_unit_of_work_flush");
            return;
        }
        
        if (!wasTransactionBegunPrematurely()) {
            beginEarlyTransaction();
        }

        // New objects cache is created to maintain the correspondence when they are returned back as different copy
        setNewObjectsCache(collectNewObjects());
        // Unregistered new objects cache is created to maintain the correspondence when they are returned back as different copy
        setUnregisteredNewObjectsCache(collectUnregisteredNewObjects());

        // Commit on the server
        RemoteUnitOfWork remoteUnitOfWork;
        try {
            setIsFlush(true);
            startOperationProfile(SessionProfiler.Remote, null, SessionProfiler.ALL);
            remoteUnitOfWork = ((DistributedSession)getParent()).getRemoteConnection().commitRootUnitOfWork(this);
            endOperationProfile(SessionProfiler.Remote, null, SessionProfiler.ALL);
        } finally {
            setIsFlush(false);
        }

        // Make the returned remote unit of work a nested unit of work and merge it with the local remote unit of work
        remoteUnitOfWork.setParent(this);
        remoteUnitOfWork.setProject(getProject());
        remoteUnitOfWork.prepareForMergeIntoRemoteUnitOfWork();
        remoteUnitOfWork.commitIntoRemoteUnitOfWork();

        log(SessionLog.FINER, SessionLog.TRANSACTION, "end_unit_of_work_flush");

        resumeUnitOfWork();
        log(SessionLog.FINER, SessionLog.TRANSACTION, "resume_unit_of_work");
    }

    /**
     * Starts committing the remote unit of work.
     * This must serialize the unit of work across to the server,
     * commit the unit of work on the server,
     * serialize it back and merge any server-side changes (such as sequence numbers) it into itself,
     * then merge into the parent remote session.
     */
    @Override
    public void commitRootUnitOfWork() {
        if (!isOnClient()) {
            if (isSynchronized()) {
                // If we started the JTS transaction then we have to commit it as well.
                if (getParent().wasJTSTransactionInternallyStarted()) {
                    commitInternallyStartedExternalTransaction();
                }
                // Do not commit until the JTS wants to.
                return;
            }
            if (this.eventManager != null) {
                this.eventManager.preCommitUnitOfWork();
            }
            super.commitRootUnitOfWork(); // On the server the normal commit is done.
            if (this.eventManager != null) {
                this.eventManager.postCommitUnitOfWork();
            }
            return;
        }

        // PERF: If this is an empty unit of work, do nothing (but still may need to commit SQL changes).
        boolean hasChanges = (this.unitOfWorkChangeSet != null) || hasCloneMapping() || hasDeletedObjects() || hasModifyAllQueries() || hasDeferredModifyAllQueries();
        if (hasChanges) {
            // The change set may already exist if using change tracking.
            if (this.unitOfWorkChangeSet == null) {
                this.unitOfWorkChangeSet = new UnitOfWorkChangeSet(this);
            }
            calculateChanges(getCloneMapping(), this.unitOfWorkChangeSet, true, true);
            hasChanges = hasModifications();
        }
        if (!hasChanges && (this.cumulativeUOWChangeSet == null) && (this.classesToBeInvalidated == null)) {
            // If no changes, avoid the remote commit, just return.
            // CR#... need to commit the transaction if begun early.
            if (wasTransactionBegunPrematurely()) {
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
            return;
        }
        
        // New objects cache is created to maintain the correspondence when they are returned back as different copy
        setNewObjectsCache(collectNewObjects());
        // Unregistered new objects cache is created to maintain the correspondence when they are returned back as different copy
        setUnregisteredNewObjectsCache(collectUnregisteredNewObjects());

        // Commit on the server
        RemoteUnitOfWork remoteUnitOfWork;
        try {
            startOperationProfile(SessionProfiler.Remote, null, SessionProfiler.ALL);
            remoteUnitOfWork = ((DistributedSession)getParent()).getRemoteConnection().commitRootUnitOfWork(this);
            endOperationProfile(SessionProfiler.Remote, null, SessionProfiler.ALL);
        } catch (RuntimeException exception) {
            // Must ensure remote session transaction mutex is correct.
            if (wasTransactionBegunPrematurely()) {
                getParent().getTransactionMutex().release();
            }
            // If an exception occurred, the unit of work will have rolledback the early transaction
            // so must record this incase the unit of work re-commits.
            setWasTransactionBegunPrematurely(false);
            throw exception;
        }

        // Must ensure remote session transaction mutex is correct.
        if (wasTransactionBegunPrematurely()) {
            setWasTransactionBegunPrematurely(false);
            setWasNonObjectLevelModifyQueryExecuted(false);
            getParent().getTransactionMutex().release();
        }
        // Make the returned remote unit of work a nested unit of work and merge it with the local remote unit of work
        remoteUnitOfWork.setParent(this);
        remoteUnitOfWork.setProject(getProject());
        remoteUnitOfWork.prepareForMergeIntoRemoteUnitOfWork();
        remoteUnitOfWork.commitIntoRemoteUnitOfWork();
        // Now commit this unit of work to the parent remote session
        commitRootUnitOfWorkOnClient();
    }
    
    /**
     * INTERNAL:
     * Changes are calculated on the client, so avoid recalculating them on the server.
     */
    @Override
    public UnitOfWorkChangeSet calculateChanges(Map registeredObjects, UnitOfWorkChangeSet changeSet, boolean assignSequences, boolean shouldCloneMap) {
        if (!this.isOnClient) {
            // The changes are calculated on the client, so don't do them again on the server.
            return changeSet;
        }
        return super.calculateChanges(registeredObjects, changeSet, assignSequences, shouldCloneMap);
    }

    /**
     * INTERNAL:
     * Resume is not required on the server.
     */
    @Override
    public void resumeUnitOfWork() {
        if (!this.isOnClient) {
            // Avoid the resume on the server, only the client should resume.
            return;
        }
        super.resumeUnitOfWork();        
    }
    
    /**
     * Merges remote unit of work to parent remote session.
     */
    protected void commitRootUnitOfWorkOnClient() {
        collectAndPrepareObjectsForNestedMerge();

        //calculate the change set here as we have special behavior for remote
        // in that the new changesets must be updated within the UOWChangeSet as the
        // primary keys have already been assigned.  This was modified for updating
        // new object change set behavior.
        UnitOfWorkChangeSet uowChangeSet = (UnitOfWorkChangeSet)getUnitOfWorkChangeSet();
        if (uowChangeSet == null) {
            //may be using the old commit process usesOldCommit()
            uowChangeSet = new UnitOfWorkChangeSet(this);
            setUnitOfWorkChangeSet(uowChangeSet);
            calculateChanges(getCloneMapping(), uowChangeSet, false, true);
            this.allClones = null;
        }
        for (Map newList : uowChangeSet.getNewObjectChangeSets().values()) {
            Iterator newChangeSets = new IdentityHashMap(newList).keySet().iterator();
            while (newChangeSets.hasNext()) {
                uowChangeSet.putNewObjectInChangesList((ObjectChangeSet)newChangeSets.next(), this);
            }
        }
        
        //add the deleted objects
        if (this.objectsDeletedDuringCommit != null) {
            for (Object deletedObject : this.objectsDeletedDuringCommit.keySet()) {
                uowChangeSet.addDeletedObject(deletedObject, this);
            }
        }

        mergeChangesIntoParent();
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     * The named query can be defined on the remote session or the server-side session.
     *
     * @see #addQuery(String, DatabaseQuery)
     */
    @Override
    public Object executeQuery(String queryName) throws DatabaseException {
        return executeQuery(queryName, new Vector(1));
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     * The class is the descriptor in which the query was pre-defined.
     * The query is executed on the server-side session.
     *
     * @see DescriptorQueryManager#addQuery(String, DatabaseQuery)
     */
    @Override
    public Object executeQuery(String queryName, Class domainClass) throws DatabaseException {
        return executeQuery(queryName, domainClass, new Vector(1));
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     * The class is the descriptor in which the query was pre-defined.
     *
     * @see DescriptorQueryManager#addQuery(String, DatabaseQuery)
     */
    @Override
    public Object executeQuery(String queryName, Class domainClass, Vector argumentValues) throws DatabaseException {
        DistributedSession remoteSession = null;
        if (getParent().isRemoteSession()) {
            remoteSession = (DistributedSession)getParent();
        } else {//must be remote unit of work
            RemoteUnitOfWork uow = (RemoteUnitOfWork)getParent();
            while (uow.getParent().isRemoteUnitOfWork()) {
                uow = (RemoteUnitOfWork)uow.getParent();
            }
            remoteSession = (DistributedSession)uow.getParent();
        }

        startOperationProfile(SessionProfiler.Remote, null, SessionProfiler.ALL);
        Transporter transporter = remoteSession.getRemoteConnection().remoteExecuteNamedQuery(queryName, domainClass, argumentValues);
        endOperationProfile(SessionProfiler.Remote, null, SessionProfiler.ALL);
        transporter.getQuery().setSession(this);
        return transporter.getQuery().extractRemoteResult(transporter);
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     *
     * @see #addQuery(String, DatabaseQuery)
     */
    @Override
    public Object executeQuery(String queryName, Vector argumentValues) throws DatabaseException {
        if (containsQuery(queryName)) {
            return super.executeQuery(queryName, argumentValues);
        }
        return executeQuery(queryName, null, argumentValues);
    }

    /**
     * Return the table descriptor specified for the class.
     */
    @Override
    public ClassDescriptor getDescriptor(Class domainClass) {
        return getParent().getDescriptor(domainClass);
    }

    /**
     * Return the table descriptor specified for the class.
     */
    @Override
    public ClassDescriptor getDescriptorForAlias(String alias) {
        return getParent().getDescriptorForAlias(alias);
    }

    /**
     * Returns a new object cache
     */
    public List getNewObjectsCache() {
        return newObjectsCache;
    }

    /**
     * INTERNAL:
     * Method returns the parent RemoteSessionController for this Remote UnitOfWork
     * Used to retrieve Valueholders that were used on the client
     */
    public RemoteSessionController getParentSessionController() {
        return this.parentSessionController;
    }

    /**
     * INTERNAL:
     * Return the database platform currently connected to.
     * The platform is used for database specific behavior.
     */
    @Override
    public DatabasePlatform getPlatform() {
        return getParent().getPlatform();
    }

    /**
     * INTERNAL:
     * Return the database platform currently connected to.
     * The platform is used for database specific behavior.
     */
    @Override
    public Platform getDatasourcePlatform() {
        return getParent().getDatasourcePlatform();
    }

    /**
     * Returns an unregistered new object cache
     */
    public List getUnregisteredNewObjectsCache() {
        return unregisteredNewObjectsCache;
    }

    /**
     * INTERNAL:
     * Return the results from exeucting the database query.
     * the arguments should be a database row with raw data values.
     */
    @Override
    public Object internalExecuteQuery(DatabaseQuery query, AbstractRecord Record) throws DatabaseException, QueryException {
        if (isOnClient()) {
            //assert !getCommitManager().isActive();
            // This will either throw an exception or do a logic only operation
            // (i.e. mark for later deletion if a deleteObjet query).
            boolean objectLevelRead = (query.isObjectLevelReadQuery() && !query.isReportQuery() && query.shouldMaintainCache());
            if (objectLevelRead) {
                ObjectLevelReadQuery readQuery = (ObjectLevelReadQuery)query;
                if (isAfterWriteChangesButBeforeCommit()) {
                    throw ValidationException.illegalOperationForUnitOfWorkLifecycle(getLifecycle(), "executeQuery(ObjectLevelReadQuery)");
                }
                Object result = readQuery.checkEarlyReturn(this, Record);

                if (result != null) {
                    if (result == InvalidObject.instance) {
                        return null;
                    }
                    return result;
                }

                // Must use the uow connection in these cases.
                // can be certain that commit manager not active as on client.
                if (readQuery.isLockQuery(this) && !wasTransactionBegunPrematurely()) {
                    beginEarlyTransaction();
                }
            } else if (query.isObjectLevelModifyQuery()) {
                // Delete object queries must be processed locally.
                return query.executeInUnitOfWork(this, Record);
            }

            // Starting a transaction early when on a remote UnitOfWork starts
            // a transaction on the server side client session, and all queries
            // when they arrive they will go down the write connection.

            /* Fix to allow executing non-selecting SQL in a UnitOfWork. - RB */
            if ((!getCommitManager().isActive()) && query.isModifyQuery()) {
                if (!wasTransactionBegunPrematurely()) {
                    beginEarlyTransaction();
                }
            }
            Object result = getParent().executeQuery(query, Record);

            if (objectLevelRead) {
                result = ((ObjectLevelReadQuery)query).registerResultInUnitOfWork(result, this, Record, false);
            }
            if (query.isModifyAllQuery()) {
                storeModifyAllQuery(query);
            }
            return result;
        }
        return query.executeInUnitOfWork(this, Record);
    }

    protected boolean isOnClient() {
        return isOnClient;
    }

    /**
     * Return if this session is a unit of work.
     */
    @Override
    public boolean isRemoteUnitOfWork() {
        return true;
    }

    /**
     * The returned remote unit of work from the server is prepared to merge with local remote unit of work.
     */
    protected void prepareForMergeIntoRemoteUnitOfWork() {
        if (this.newObjectsCache == null) {
            return;
        }
        int size = this.newObjectsCache.size();
        if (size == 0) {
            return;
        }
        Map originalToClone = new IdentityHashMap(size);
        Map cloneToOriginal = new IdentityHashMap(size);

        // For new and unregistered objects the clone from the parent remote unit of work is picked and store as original
        // in the remote unit of work. This is done so that changes are merged into the clone of the parent.
        List remoteNewObjects = ((RemoteUnitOfWork)this.parent).getNewObjectsCache();
        for (int index = 0; index < size; index++) {
            Object cloneFromParent = remoteNewObjects.get(index);
            Object cloneFromSelf = this.newObjectsCache.get(index);
            if (cloneFromSelf != null) {
                originalToClone.put(cloneFromParent, cloneFromSelf);
                cloneToOriginal.put(cloneFromSelf, cloneFromParent);
            }
        }

        List remoteUnregisteredObjects = ((RemoteUnitOfWork)this.parent).getUnregisteredNewObjectsCache();
        size = remoteUnregisteredObjects.size();
        for (int index = 0; index < size; index++) {
            Object cloneFromParent = ((RemoteUnitOfWork)getParent()).getUnregisteredNewObjects().get(remoteUnregisteredObjects.get(index));
            Object cloneFromSelf = getUnregisteredNewObjects().get(this.unregisteredNewObjectsCache.get(index));
            originalToClone.put(cloneFromParent, cloneFromSelf);
            cloneToOriginal.put(cloneFromSelf, cloneFromParent);
        }

        this.newObjectsOriginalToClone = originalToClone;
        this.newObjectsCloneToOriginal = cloneToOriginal;
    }

    /**
     * INTERNAL:
     * Re-initialize for the server-side session.
     * This is done when the uow is passed back to the server for committing.
     */
    public void reinitializeForSession(AbstractSession session, RemoteSessionController parentSessionController) {
        // If a server, acquire a client to commit into as client store connection for commit.
        if (session.isServerSession()) {
            session = ((org.eclipse.persistence.sessions.server.ServerSession)session).acquireClientSession();
        }
        setIsOnClient(false);
        setParentSessionController(parentSessionController);
        setParent(session);
        setProject(session.getProject());
        setProfiler(session.getProfiler());
        if (session.hasEventManager()) {
            setEventManager(session.getEventManager().clone(this));
        }
        //	setShouldLogMessages(session.shouldLogMessages());
        setSessionLog(session.getSessionLog());
        setLog(session.getLog());
        // These are transient so must be reset.
        setCommitManager(new CommitManager(this));
        setTransactionMutex(new ConcurrencyManager());
        getCommitManager().setCommitOrder(session.getCommitManager().getCommitOrder());

        if (session.hasExternalTransactionController()) {
            session.getExternalTransactionController().registerSynchronizationListener(this, session);
        }

        if (this.unitOfWorkChangeSet != null) {
            fixRemoteChangeSet(this.unitOfWorkChangeSet, null, this);
        }
        if (this.cumulativeUOWChangeSet != null) {
            fixRemoteChangeSet(this.cumulativeUOWChangeSet, null, this);
        }
    }

    /**
     * INTERNAL:
     * Fix the transient fields in the serialized change set.
     */
    protected void fixRemoteChangeSet(UnitOfWorkChangeSet uowChangeSet, Map cloneMap, AbstractSession session) {
        if (uowChangeSet == null) {
            return;
        }
        uowChangeSet.setSession(session);
        for (Map.Entry<Class, Map<ObjectChangeSet, ObjectChangeSet>> entry : uowChangeSet.getObjectChanges().entrySet()) {
            ClassDescriptor descriptor = getDescriptor(entry.getKey());
            for (ObjectChangeSet changeSet : entry.getValue().values()) {
                changeSet.setDescriptor(descriptor);
                changeSet.setClassType(entry.getKey());
            }
        }
        for (Map.Entry<Class, Map<ObjectChangeSet, ObjectChangeSet>> entry : uowChangeSet.getNewObjectChangeSets().entrySet()) {
            ClassDescriptor descriptor = getDescriptor(entry.getKey());
            for (ObjectChangeSet changeSet : entry.getValue().values()) {
                changeSet.setDescriptor(descriptor);
                changeSet.setClassType(entry.getKey());
            }
        }
        if (cloneMap == null) {
            for (Map.Entry<Object, ObjectChangeSet> entry : uowChangeSet.getCloneToObjectChangeSet().entrySet()) {
                Object clone = entry.getKey();
                ObjectChangeSet changeSet = entry.getValue();
                changeSet.postSerialize(clone, uowChangeSet, session);
            }            
        } else {
            // Also need to reset the remote objects with their local clones.
            int size = uowChangeSet.getCloneToObjectChangeSet().size();
            Map<Object, ObjectChangeSet> newCloneToObjectChangeSet = new IdentityHashMap<Object, ObjectChangeSet>(size);
            Map<ObjectChangeSet, Object> newObjectChangeSetToUOWClone = new IdentityHashMap<ObjectChangeSet, Object>(size);
            for (Map.Entry<Object, ObjectChangeSet> entry : uowChangeSet.getCloneToObjectChangeSet().entrySet()) {
                Object clone = cloneMap.get(entry.getKey());
                // Deleted objects no longer exist, so use remote copy instead.
                if (clone == null) {
                    clone = entry.getKey();
                }
                ObjectChangeSet changeSet = entry.getValue();
                changeSet.postSerialize(clone, uowChangeSet, session);
                newCloneToObjectChangeSet.put(clone, changeSet);
                newObjectChangeSetToUOWClone.put(changeSet, clone);
            }
            uowChangeSet.setCloneToObjectChangeSet(newCloneToObjectChangeSet);
            uowChangeSet.setObjectChangeSetToUOWClone(newObjectChangeSetToUOWClone);
        }
    }

    protected void setIsOnClient(boolean isOnClient) {
        this.isOnClient = isOnClient;
    }

    /**
     * Set a new object cache
     */
    protected void setNewObjectsCache(List newObjectsCache) {
        this.newObjectsCache = newObjectsCache;
    }

    /**
     * INTERNAL:
     * Sets the parent RemoteSessionController for this Remote UnitOfWork
     * Used to retrieve Valueholders that were used on the client
     */
    public void setParentSessionController(RemoteSessionController parentSessionController) {
        this.parentSessionController = parentSessionController;
    }

    /**
     * Set unregistered new object cache
     */
    protected void setUnregisteredNewObjectsCache(List unregisteredNewObjectsCache) {
        this.unregisteredNewObjectsCache = unregisteredNewObjectsCache;
    }

    /**
     * Avoid the toString printing the accessor and platform.
     */
    @Override
    public String toString() {
        return Helper.getShortClassName(getClass()) + "()";
    }

    /**
     * TESTING:
     * This is used by testing code to ensure that a deletion was successful.
     */
    @Override
    public boolean verifyDelete(Object domainObject) {
        return getParent().verifyDelete(domainObject);
    }
}
