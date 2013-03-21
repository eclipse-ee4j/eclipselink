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
package org.eclipse.persistence.sessions.remote;

import java.util.*;

import org.eclipse.persistence.config.ReferenceMode;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.history.AsOfClause;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.internal.sessions.remote.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.internal.sequencing.SequencingFactory;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <b>Purpose</b>: Provide transparent remote three-tiered replacation support.
 * The remote session allows for complex three-tiered applications to be easily built.
 * It gives the remote client the fully functionality of the TopLink api including,</p>
 * <p><ul>
 * <li>Client side caching and object-identity maintainence.
 * <li>Complex query support
 * <li>Unit of work support
 * <li>Indirection support through remote value holders.
 * </ul></p>
 *
 * This session is a primary interface which resides on the client side. Users would interact
 * with session just the same way as if it was a normal session.
 */
public class RemoteSession extends DistributedSession {
    protected Sequencing sequencing;
    protected boolean shouldEnableDistributedIndirectionGarbageCollection = false;

    public RemoteSession() {
        super(0);
    }
    
    /**
     * PUBLIC:
     * Creates a RemoteSession.
     * @param remoteConnection remote session requires a remote connection. This must be accessed remotely from the client through RMI or CORBA.
     */
    public RemoteSession(RemoteConnection remoteConnection) {
        super(remoteConnection);
        initializeSequencing();
    }

    /**
     * ADVANCED:
     * Allow the server-side value holders to be cleaned-up when the client-side value holder finalize.
     */
    public void setShouldEnableDistributedIndirectionGarbageCollection(boolean shouldEnableDistributedIndirectionGarbageCollection) {
        this.shouldEnableDistributedIndirectionGarbageCollection = shouldEnableDistributedIndirectionGarbageCollection;
    }

    /**
     * ADVANCED:
     * Allow the server-side value holders to be cleaned-up when the client-side value holder finalize.
     */
    public boolean shouldEnableDistributedIndirectionGarbageCollection() {
        return shouldEnableDistributedIndirectionGarbageCollection;
    }

    /**
     * INTERNAL:
     * Acquires a special historical session for reading objects as of a past time.
     */
    @Override
    public Session acquireHistoricalSession(AsOfClause clause) throws ValidationException {
        throw ValidationException.cannotAcquireHistoricalSession();
    }

    /**
     * PUBLIC:
     * Return a unit of work for this session.
     * The unit of work is an object level transaction that allows
     * a group of changes to be applied as a unit.
     *
     * @see UnitOfWorkImpl
     */
    @Override
    public UnitOfWorkImpl acquireUnitOfWork() {
        return acquireUnitOfWork(null);
    }

    /**
     * PUBLIC:
     * Return a unit of work for this session.
     * The unit of work is an object level transaction that allows
     * a group of changes to be applied as a unit.
     *
     * @see UnitOfWorkImpl
     * @param referenceMode The reference type the UOW should use internally when
     * referencing Working clones.  Setting this to WEAK means the UOW will use 
     * weak references to reference clones and if the application no longer
     * references the clone the clone may be garbage collected.  If the clone
     * has uncommitted changes then those changes will be lost.
     */
    @Override
    public UnitOfWorkImpl acquireUnitOfWork(ReferenceMode referenceMode) {
        log(SessionLog.FINER, SessionLog.TRANSACTION, "acquire_unit_of_work");
        setNumberOfActiveUnitsOfWork(getNumberOfActiveUnitsOfWork() + 1);
        return new RemoteUnitOfWork(this, referenceMode);
    }

    /**
     * PUBLIC:
     * Return a repeatable write unit of work for this session.
     * A repeatable write unit of work allows multiple writeChanges (flushes).
     *
     * @see RepeatableWriteUnitOfWork
     */
    @Override
    public RepeatableWriteUnitOfWork acquireRepeatableWriteUnitOfWork(ReferenceMode referenceMode) {
        return new RemoteUnitOfWork(this, referenceMode);
    }

    /**
     * PUBLIC:
     * Execute the database query.
     */
    @Override
    public Object executeQuery(DatabaseQuery query) {
        return query.remoteExecute(this);
    }


    /**
     * PUBLIC:
     * Return the login.
     * This must retrieve the login information from the server this first time called.
     * This is useful to be able to do things differently depending on the database platform.
     */
    @Override
    public Login getDatasourceLogin() {
        Login login = super.getDatasourceLogin();
        if ((login == null) && (getRemoteConnection() != null)) {
            startOperationProfile(SessionProfiler.RemoteMetadata, null, SessionProfiler.ALL);
            login = getRemoteConnection().getLogin();
            endOperationProfile(SessionProfiler.RemoteMetadata, null, SessionProfiler.ALL);
            setDatasourceLogin(login);
        }

        return login;
    }

    /**
     * INTERNAL:
     * Return the corresponding objects from the remote session for the objects read from the server.
     */
    @Override
    public Object getObjectCorrespondingTo(Object serverSideDomainObject, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query) {
        if (serverSideDomainObject == null) {
            return null;
        }

        ClassDescriptor descriptor = getDescriptor(serverSideDomainObject);

        // CR... fix to descriptor iterator exposed the bug that we were putting aggregate-collections in the cache.
        if (descriptor.isAggregateCollectionDescriptor() || ((query != null) && (!query.shouldMaintainCache()))) {
            if ((query != null) && (!query.hasPartialAttributeExpressions())) {
                descriptor.getObjectBuilder().fixObjectReferences(serverSideDomainObject, objectDescriptors, processedObjects, query, this);
            }
            return serverSideDomainObject;
        }

        // Extract the object primary key and check if it exist on the remote session or not. If we find an object
        // with this primary key then that's the corresponding object. Other wise its a new object for the remote 
        // session which needs to be registered in the remote sessions identity map and this is also a corresponding 
        // object.
        ObjectDescriptor objectDescriptor = (ObjectDescriptor)objectDescriptors.get(serverSideDomainObject);
        if (objectDescriptor == null){
            //the object must have been added concurrently before serialize generate a new ObjectDescriptor on this side
            objectDescriptor = new ObjectDescriptor();
            objectDescriptor.setKey(descriptor.getObjectBuilder().extractPrimaryKeyFromObject(serverSideDomainObject, this));
            objectDescriptor.setObject(serverSideDomainObject);
            OptimisticLockingPolicy policy = descriptor.getOptimisticLockingPolicy();
            if (policy == null){
                objectDescriptor.setWriteLockValue(null);
            }else{
                objectDescriptor.setWriteLockValue(policy.getBaseValue());
            }
            objectDescriptors.put(serverSideDomainObject, objectDescriptor);
        }
        Object primaryKey = objectDescriptor.getKey();
        Object clientSideDomainObject = getIdentityMapAccessorInstance().getFromIdentityMap(primaryKey, serverSideDomainObject.getClass(), descriptor);

        // If object is already processed the return back, this check must be done after the cliet-side object is found.
        if (processedObjects.containsKey(serverSideDomainObject)) {
            if (clientSideDomainObject == null) {
                return serverSideDomainObject;
            } else {
                return clientSideDomainObject;
            }
        }

        processedObjects.put(serverSideDomainObject, serverSideDomainObject);

        if (clientSideDomainObject == null) {
            getIdentityMapAccessorInstance().putInIdentityMap(serverSideDomainObject, primaryKey, objectDescriptor.getWriteLockValue(), objectDescriptor.getReadTime(), descriptor);
            descriptor.getObjectBuilder().fixObjectReferences(serverSideDomainObject, objectDescriptors, processedObjects, query, this);
            clientSideDomainObject = serverSideDomainObject;
        } else {
            // if the query is null, that means we refreshed a newly-created client object at some point
            // and we should refresh the identity map and cascade private parts
            if ((query == null) || (query.shouldRefreshRemoteIdentityMapResult()) || getDescriptor(clientSideDomainObject).shouldAlwaysRefreshCacheOnRemote()) {
                MergeManager mergeManager = new MergeManager(this);
                mergeManager.refreshRemoteObject();
                mergeManager.setObjectDescriptors(objectDescriptors);
                if (query == null) {
                    mergeManager.cascadePrivateParts();
                } else {
                    mergeManager.setCascadePolicy(query.getCascadePolicy());
                }
                clientSideDomainObject = mergeManager.mergeChanges(serverSideDomainObject, null, this);
            }
        }

        return clientSideDomainObject;
    }

    /**
     * INTERNAL:
     * Return the corresponding objects from the remote session for the objects read from the server.
     */
    @Override
    public Object getObjectsCorrespondingToAll(Object serverSideDomainObjects, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, ContainerPolicy containerPolicy) {
        Object clientSideDomainObjects = containerPolicy.containerInstance(containerPolicy.sizeFor(serverSideDomainObjects));

        for (Object iter = containerPolicy.iteratorFor(serverSideDomainObjects);
                 containerPolicy.hasNext(iter);) {
            Object serverSideDomainObject = containerPolicy.next(iter, this);
            containerPolicy.addInto(getObjectCorrespondingTo(serverSideDomainObject, objectDescriptors, processedObjects, query), clientSideDomainObjects, this);
        }

        return clientSideDomainObjects;
    }

    /**
     * INTERNAL:
     * This will instantiate value holder on the server.
     */
    @Override
    public Object instantiateRemoteValueHolderOnServer(RemoteValueHolder remoteValueHolder) {
        startOperationProfile(SessionProfiler.RemoteLazy, null, SessionProfiler.ALL);
        Transporter transporter = getRemoteConnection().instantiateRemoteValueHolderOnServer(remoteValueHolder);
        endOperationProfile(SessionProfiler.RemoteLazy, null, SessionProfiler.ALL);
        return remoteValueHolder.getMapping().getObjectCorrespondingTo(transporter.getObject(), this, transporter.getObjectDescriptors(), new IdentityHashMap(), remoteValueHolder.getQuery());
    }

    /**
     * INTERNAL:
     * Return if this session is remote.
     */
    @Override
    public boolean isRemoteSession() {
        return true;
    }

    /**
     * INTERNAL:
     * Return the Sequencing object used by the session.
     * Sequences may be provided locally, or remotely.
     */
    @Override
    public Sequencing getSequencing() {
        if (this.isMetadataRemote) {
            return this.sequencing;
        } else {
            return super.getSequencing();
        }
    }

    /**
     * ADVANCED:
     * Creates sequencing object for the session.
     * Sequences may be provided locally, or remotely.
     */
    @Override
    public void initializeSequencing() {
        if (this.isMetadataRemote) {        
            this.sequencing = SequencingFactory.createSequencing(this);
        } else {
            super.initializeSequencing();
        }
    }
}
