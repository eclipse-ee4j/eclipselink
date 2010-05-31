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
package org.eclipse.persistence.sessions.remote;

import java.util.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.remote.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.sessions.*;

/**
 * <b>Purpose</b>: Super class to all remote client session's.
 */
public abstract class DistributedSession extends AbstractSession {
    protected transient RemoteConnection remoteConnection;

    /**
     * INTERNAL:
     * Create a blank session, used for proxy session.
     */
    protected DistributedSession(int nothing) {
    }

    /**
     * PUBLIC:
     * Creates a DistributedSession.
     * @param remoteConnection remote session requires a remote connection. This must be accessed remotely from the client through RMI or CORBA.
     */
    public DistributedSession(RemoteConnection remoteConnection) {
        this.remoteConnection = remoteConnection;
        this.project = new org.eclipse.persistence.sessions.Project();
    }

    /**
     * PUBLIC:
     * Return a unit of work for this session.
     * The unit of work is an object level transaction that allows
     * a group of changes to be applied as a unit.
     *
     * @see UnitOfWorkImpl
     */
    public abstract UnitOfWorkImpl acquireUnitOfWork();

    /**
     * PUBLIC:
     * Start a transaction on the server.
     * A unit of work should normally be used instead of transactions for the remote session.
     */
    public void beginTransaction() {
        // Acquire the mutex so session knows it is in a transaction.
        getTransactionMutex().acquire();
        getRemoteConnection().beginTransaction();
    }

    /**
     * PUBLIC:
     * Commit a transaction on the server.
     * A unit of work should normally be used instead of transactions for the remote session.
     */
    public void commitTransaction() {
        getRemoteConnection().commitTransaction();
        getTransactionMutex().release();
    }

    /**
     * INTERNAL:
     * Return remote cursor stream.
     */
    public RemoteCursoredStream cursorSelectObjects(CursoredStreamPolicy policy) {
        return getRemoteConnection().cursorSelectObjects(policy, this);
    }

    /**
     * INTERNAL:
     * Return remote scrollable cursor
     */
    public RemoteScrollableCursor cursorSelectObjects(ScrollableCursorPolicy policy) {
        return getRemoteConnection().cursorSelectObjects(policy, this);
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
        Transporter transporter = getRemoteConnection().remoteExecuteNamedQuery(queryName, domainClass, argumentValues);
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
     * Execute the database query.
     */
    public abstract Object executeQuery(DatabaseQuery query);

    /**
     * INTERNAL:
     * Execute the database query.
     */
    public Object executeQuery(DatabaseQuery query, AbstractRecord row) {
        query.setTranslationRow(row);
        return executeQuery(query);
    }

    /**
     * INTERNAL:
     * CR#2751
     * Returns the set of read-only classes for the receiver.  These class come from the
     * Remote connection
     * @return A Vector containing the Java Classes that are currently read-only.
     */
    public Vector getDefaultReadOnlyClasses() {
        // how should we cache the readOnly classes on the client side?
        // We should check the cache here.
        Vector readOnlyClasses = getRemoteConnection().getDefaultReadOnlyClasses();
        return readOnlyClasses;
    }

    /**
     * INTERNAL:
     * Return the table descriptor specified for the class.
     */
    public ClassDescriptor getDescriptor(Class domainClass) {
        ClassDescriptor descriptor = getDescriptors().get(domainClass);

        // If the descriptor is null then this means that descriptor must now be read from the server.
        if (descriptor == null) {
            descriptor = getRemoteConnection().getDescriptor(domainClass);
            if (descriptor == null) {
                return null;
            }
            getDescriptors().put(domainClass, descriptor);
            descriptor.remoteInitialization(this);
        }

        return descriptor;
    }

    /**
     * INTERNAL:
     * Return the descriptor.
     */
    public ClassDescriptor getDescriptorCorrespondingTo(ClassDescriptor descriptor) {
        return getDescriptors().get(descriptor.getJavaClass());
    }

    /**
     * INTERNAL:
     * Return the corresponding objects from the remote session for the objects read from the server.
     */
    public abstract Object getObjectCorrespondingTo(Object serverSideDomainObject, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query);

    /**
     * INTERNAL:
     * Return the corresponding objects from the remote session for the objects read from the server.
     */
    public abstract Object getObjectsCorrespondingToAll(Object serverSideDomainObjects, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, ContainerPolicy containerPolicy);

    /**
     * INTERNAL:
     * Return the remote connection.
     */
    public RemoteConnection getRemoteConnection() {
        return remoteConnection;
    }

    /**
     * INTERNAL:
     * Checks if the descriptor exists or not.
     */
    public boolean hasCorrespondingDescriptor(ClassDescriptor descriptor) {
        return getDescriptors().containsKey(descriptor.getJavaClass());
    }

    /**
     * INTERNAL:
     * Set up the IdentityMapManager.  Overrides the default IdentityMapManager
     */
    public void initializeIdentityMapAccessor() {
        this.identityMapAccessor = new DistributedSessionIdentityMapAccessor(this, new IdentityMapManager(this));
    }

    /**
     * INTERNAL:
     * This will instantiate value holder on the server.
     */
    public abstract Object instantiateRemoteValueHolderOnServer(RemoteValueHolder remoteValueHolder);

    /**
     * PUBLIC:
     * Return if this session is connected to the server.
     */
    public boolean isConnected() {
        if (getRemoteConnection() == null) {
            return false;
        }

        return true;
    }
        
    /**
     * INTERNAL:
     * Return if this session is a distributed session.
     */
    public boolean isDistributedSession() {
        return true;
    }

    /**
     * INTERNAL:
     * Return if this session is a remote session.
     */
    public boolean isRemoteSession() {
        return false;
    }

    /**
     * INTERNAL:
     * You cannot add descriptors to a remote session. This is a internal method used by TopLink
     */
    public void privilegedAddDescriptor(ClassDescriptor descriptor) {
        getDescriptors().put(descriptor.getJavaClass(), descriptor);
    }

    /**
     * PUBLIC:
     * Rollback a transaction on the server.
     * A unit of work should normally be used instead of transactions for the remote session.
     */
    public void rollbackTransaction() {
        getRemoteConnection().rollbackTransaction();
        getTransactionMutex().release();
    }

    /**
     * INTERNAL:
     * Set the remote connection.
     */
    protected void setRemoteConnection(RemoteConnection remoteConnection) {
        this.remoteConnection = remoteConnection;
    }

    /**
     * PUBLIC:
     * Avoid printing the accessor and platform.
     */
    public String toString() {
        return Helper.getShortClassName(getClass()) + "()";
    }

    /**
     * PUBLIC:
     * Logout the session, close the remote connection and release the hold resources
     */
    public void release() {
        //CR3854: logging out of DistributedSession is not releasing remote resources.
        //The remote connection remove() call should  release the resource, like the stateful
        //session been, which the connection holds on.
        remoteConnection.release();
    }
}
