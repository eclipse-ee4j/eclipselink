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
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.logging.SessionLog;

/**
 * Counter part of the unit of work which exists on the client side.
 */
public class RemoteUnitOfWork extends UnitOfWorkImpl {
    protected Vector newObjectsCache;
    protected Vector unregisteredNewObjectsCache;
    protected boolean isOnClient;
    protected transient RemoteSessionController parentSessionController;

    public RemoteUnitOfWork(RemoteUnitOfWork parent) {
        this(parent, null);
    }

    public RemoteUnitOfWork(RemoteSession parent) {
        this(parent, null);
        this.isOnClient = true;
    }
    public RemoteUnitOfWork(RemoteUnitOfWork parent, ReferenceMode referenceMode) {
        super(parent, referenceMode);
        this.isOnClient = true;
    }

    public RemoteUnitOfWork(RemoteSession parent, ReferenceMode referenceMode) {
        super(parent, referenceMode);
        this.isOnClient = true;
    }

    /**
     * The nested unit of work must also be remote.
     */
    public UnitOfWorkImpl acquireUnitOfWork() {
        return acquireUnitOfWork(null);
    }

    /**
     * The nested unit of work must also be remote.
     */
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
    protected Vector collectNewObjects() {
        Set keys = getNewObjectsCloneToOriginal().keySet();
        
        Vector vector = new Vector(keys.size());
        Iterator enumeration = keys.iterator();

        while (enumeration.hasNext()) {
            vector.addElement(enumeration.next());
        }
        return vector;
    }

    /**
     * This is done to maintain correspondence between local unregistered new objects and returned unregistered new
     * objects from serialization. Object correspondence is maintained by comparing primary keys but for unregistered
     * new objects it is possible that primary key value is null as it is still not inserted. The returned unregistered
     * new objects from serialization will have primary key value which will be inserted into corresponding local new
     * objects.
     */
    protected Vector collectUnregisteredNewObjects() {
        discoverAllUnregisteredNewObjects();
        return Helper.buildVectorFromMapElements(getUnregisteredNewObjects());
    }

    /**
     * The remote unit of work returned after its commit on the server is merged with remote unit of work
     * on the remote side.
     */
    protected void commitIntoRemoteUnitOfWork() {
        // Must merge the transaction flag.
        ((UnitOfWorkImpl)getParent()).setWasTransactionBegunPrematurely(wasTransactionBegunPrematurely());

        MergeManager manager = new MergeManager(this);
        manager.mergeWorkingCopyIntoRemote();

        // Must clone the clone mapping because entries can be added to it during the merging,
        // and that can lead to concurrency problems.
        Iterator clones = new IdentityHashMap(getCloneMapping()).keySet().iterator();

        // Iterate over each clone and let the object build merge to clones into the originals.
        while (clones.hasNext()) {
            manager.mergeChanges(clones.next(), null);
        }
    }

    /**
     * Starts committing the remote unit of work.
     * This must serialize the unit of work across to the server,
     * commit the unit of work on the server,
     * serialize it back and merge any server-side changes (such as sequence numbers) it into itself,
     * then merge into the parent remote session.
     */
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

        // New objects cache is created to maintain the correspondence when they are returned back as differnt copy
        setNewObjectsCache(collectNewObjects());
        // Unregistered new objects cache is created to maintain the correspondence when they are returned back as differnt copy
        setUnregisteredNewObjectsCache(collectUnregisteredNewObjects());

        // Commit on the server
        RemoteUnitOfWork remoteUnitOfWork;
        try {
            remoteUnitOfWork = ((RemoteSession)getParent()).getRemoteConnection().commitRootUnitOfWork(this);
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
            setUnitOfWorkChangeSet(new UnitOfWorkChangeSet(this));
            uowChangeSet = (UnitOfWorkChangeSet)getUnitOfWorkChangeSet();
            calculateChanges(new IdentityHashMap(this.cloneMapping), (UnitOfWorkChangeSet)getUnitOfWorkChangeSet(), false);
            this.allClones = null;
        }
        for (Map newList : uowChangeSet.getNewObjectChangeSets().values()) {
            Iterator newChangeSets = new IdentityHashMap(newList).keySet().iterator();
            while (newChangeSets.hasNext()) {
                uowChangeSet.putNewObjectInChangesList((ObjectChangeSet)newChangeSets.next(), this);
            }
        }
        
        //add the deleted objects
        for (Iterator iterator = getObjectsDeletedDuringCommit().keySet().iterator(); iterator.hasNext(); ){
            ((UnitOfWorkChangeSet)getUnitOfWorkChangeSet()).addDeletedObject(iterator.next(), this);
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
    public Object executeQuery(String queryName, Class domainClass, Vector argumentValues) throws DatabaseException {
        RemoteSession remoteSession = null;
        if (getParent().isRemoteSession()) {
            remoteSession = (RemoteSession)getParent();
        } else {//must be remote unit of work
            RemoteUnitOfWork uow = (RemoteUnitOfWork)getParent();
            while (uow.getParent().isRemoteUnitOfWork()) {
                uow = (RemoteUnitOfWork)uow.getParent();
            }
            remoteSession = (RemoteSession)uow.getParent();
        }

        Transporter transporter = remoteSession.getRemoteConnection().remoteExecuteNamedQuery(queryName, domainClass, argumentValues);
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
    public Object executeQuery(String queryName, Vector argumentValues) throws DatabaseException {
        if (containsQuery(queryName)) {
            return super.executeQuery(queryName, argumentValues);
        }
        return executeQuery(queryName, null, argumentValues);
    }

    /**
     * Return the table descriptor specified for the class.
     */
    public ClassDescriptor getDescriptor(Class domainClass) {
        return getParent().getDescriptor(domainClass);
    }

    /**
     * Returns a new object cache
     */
    public Vector getNewObjectsCache() {
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
    public DatabasePlatform getPlatform() {
        return getParent().getPlatform();
    }

    /**
     * INTERNAL:
     * Return the database platform currently connected to.
     * The platform is used for database specific behavior.
     */
    public Platform getDatasourcePlatform() {
        return getParent().getDatasourcePlatform();
    }

    /**
     * Returns an unregistered new object cache
     */
    public Vector getUnregisteredNewObjectsCache() {
        return unregisteredNewObjectsCache;
    }

    /**
     * INTERNAL:
     * Return the results from exeucting the database query.
     * the arguments should be a database row with raw data values.
     */
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
                return query.executeInUnitOfWork(this, Record);
            }

            // Starting a transaction early when on a remote UnitOfWork starts
            // a transaction on the server side client session, and all queries
            // when they arrive they will go down the write connection.

            /* Fix to allow executing non-selecting SQL in a UnitOfWork. - RB */
            if ((!getCommitManager().isActive()) && query.isDataModifyQuery()) {
                if (!wasTransactionBegunPrematurely()) {
                    beginEarlyTransaction();
                }
            }
            Object result = getParent().executeQuery(query, Record);

            if (objectLevelRead) {
                result = ((ObjectLevelReadQuery)query).registerResultInUnitOfWork(result, this, Record, false);
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
    public boolean isRemoteUnitOfWork() {
        return true;
    }

    /**
     * The returned remote unit of work from the server is prepared to merge with local remote unit of work.
     */
    protected void prepareForMergeIntoRemoteUnitOfWork() {
        Map originalToClone = new IdentityHashMap();
        Map cloneToOriginal = new IdentityHashMap();

        // For new and unregistered objects the clone from the parent remote unit of work is picked and store as original
        // in the remote unit of work. This is done so that changes are merged into the clone of the parent.
        Enumeration returnedNewObjects = getNewObjectsCache().elements();

        // For new and unregistered objects the clone from the parent remote unit of work is picked and store as original
        // in the remote unit of work. This is done so that changes are merged into the clone of the parent.
        Enumeration newObjects = ((RemoteUnitOfWork)getParent()).getNewObjectsCache().elements();

        for (; returnedNewObjects.hasMoreElements();) {
            Object cloneFromParent = newObjects.nextElement();
            Object cloneFromSelf = returnedNewObjects.nextElement();
            if (cloneFromSelf != null) {
                originalToClone.put(cloneFromParent, cloneFromSelf);
                cloneToOriginal.put(cloneFromSelf, cloneFromParent);
            }
        }

        Enumeration returnedUnregisteredNewObjects = getUnregisteredNewObjectsCache().elements();
        Enumeration unregisteredNewObjects = ((RemoteUnitOfWork)getParent()).getUnregisteredNewObjectsCache().elements();

        for (; returnedUnregisteredNewObjects.hasMoreElements();) {
            Object cloneFromParent = ((RemoteUnitOfWork)getParent()).getUnregisteredNewObjects().get(unregisteredNewObjects.nextElement());
            Object cloneFromSelf = getUnregisteredNewObjects().get(returnedUnregisteredNewObjects.nextElement());
            originalToClone.put(cloneFromParent, cloneFromSelf);
            cloneToOriginal.put(cloneFromSelf, cloneFromParent);
        }

        setNewObjectsOriginalToClone(originalToClone);
        setNewObjectsCloneToOriginal(cloneToOriginal);

        // Get the corresponding deleted objects from the original remote unit of work,
        // and set them in the remote unit of work, this is the parent.
        Map objectsDeletedDuringCommit = new IdentityHashMap();
        for (Iterator deletedObjects = getObjectsDeletedDuringCommit().keySet().iterator();
                 deletedObjects.hasNext();) {
            Object deletedObject = deletedObjects.next();
            Object primaryKey = getId(deletedObject);
            Object cloneFromParent = getParent().getIdentityMapAccessor().getFromIdentityMap(primaryKey, deletedObject.getClass());

            // The original may be a new object, or read on the server.
            if (cloneFromParent == null) {
                cloneFromParent = cloneToOriginal.get(deletedObject);
                // This means read on the server, so not on client, so use the same one.
                if (cloneFromParent == null) {
                    cloneFromParent = deletedObject;
                }
            }

            objectsDeletedDuringCommit.put(cloneFromParent, getId(cloneFromParent));
            ((UnitOfWorkImpl)getParent()).getIdentityMapAccessor().removeFromIdentityMap(cloneFromParent);
        }
        ((UnitOfWorkImpl)getParent()).setObjectsDeletedDuringCommit(objectsDeletedDuringCommit);
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
        setProfiler(getProfiler());
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

        if (getParent().hasExternalTransactionController()) {
            getParent().getExternalTransactionController().registerSynchronizationListener(this, getParent());
        }
    }

    protected void setIsOnClient(boolean isOnClient) {
        this.isOnClient = isOnClient;
    }

    /**
     * Set a new object cache
     */
    protected void setNewObjectsCache(Vector newObjectsCache) {
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
    protected void setUnregisteredNewObjectsCache(Vector unregisteredNewObjectsCache) {
        this.unregisteredNewObjectsCache = unregisteredNewObjectsCache;
    }

    /**
     * Avoid the toString printing the accessor and platform.
     */
    public String toString() {
        return Helper.getShortClassName(getClass()) + "()";
    }

    /**
     * TESTING:
     * This is used by testing code to ensure that a deletion was successful.
     */
    public boolean verifyDelete(Object domainObject) {
        return getParent().verifyDelete(domainObject);
    }
}
