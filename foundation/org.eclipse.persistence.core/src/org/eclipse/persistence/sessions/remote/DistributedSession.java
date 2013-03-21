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
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.remote.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.sessions.*;

/**
 * <b>Purpose</b>: Super class to all remote client session's.
 */
public abstract class DistributedSession extends DatabaseSessionImpl {
    /** Connection to remote persistence service. */
    protected transient RemoteConnection remoteConnection;
    /** Cache if default classes have been read from server. */
    protected boolean hasDefaultReadOnlyClasses;
    /** Define if meta-data is initialized locally, or serialized from the server. */
    protected boolean isMetadataRemote = true;

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
        this.remoteConnection.initialize(this);
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
    @Override
    public abstract UnitOfWorkImpl acquireUnitOfWork();

    /**
     * PUBLIC:
     * Start a transaction on the server.
     * A unit of work should normally be used instead of transactions for the remote session.
     */
    @Override
    public void beginTransaction() {
        // Acquire the mutex so session knows it is in a transaction.
        getTransactionMutex().acquire();
        startOperationProfile(SessionProfiler.Remote, null, SessionProfiler.ALL);
        getRemoteConnection().beginTransaction();
        endOperationProfile(SessionProfiler.Remote, null, SessionProfiler.ALL);
    }

    /**
     * PUBLIC:
     * Commit a transaction on the server.
     * A unit of work should normally be used instead of transactions for the remote session.
     */
    @Override
    public void commitTransaction() {
        startOperationProfile(SessionProfiler.Remote, null, SessionProfiler.ALL);
        getRemoteConnection().commitTransaction();
        endOperationProfile(SessionProfiler.Remote, null, SessionProfiler.ALL);
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
        startOperationProfile(SessionProfiler.Remote, null, SessionProfiler.ALL);
        Transporter transporter = getRemoteConnection().remoteExecuteNamedQuery(queryName, domainClass, argumentValues);
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
     * Execute the database query.
     */
    @Override
    public abstract Object executeQuery(DatabaseQuery query);

    /**
     * INTERNAL:
     * Execute the database query.
     */
    @Override
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
    @Override
    public Vector getDefaultReadOnlyClasses() {
        if (this.isMetadataRemote && !this.hasDefaultReadOnlyClasses) {
            getProject().setDefaultReadOnlyClasses(getRemoteConnection().getDefaultReadOnlyClasses());
            this.hasDefaultReadOnlyClasses = true;
        }
        return super.getDefaultReadOnlyClasses();
    }

    /**
     * INTERNAL:
     * Return the table descriptor specified for the class.
     */
    @Override
    public ClassDescriptor getDescriptor(Class domainClass) {
        ClassDescriptor descriptor = getDescriptors().get(domainClass);

        // If the descriptor is null then this means that descriptor must now be read from the server.
        if (descriptor == null) {
            if (!this.isMetadataRemote) {
                return super.getDescriptor(domainClass);
            }
            startOperationProfile(SessionProfiler.RemoteMetadata, null, SessionProfiler.ALL);
            descriptor = getRemoteConnection().getDescriptor(domainClass);
            endOperationProfile(SessionProfiler.RemoteMetadata, null, SessionProfiler.ALL);
            if (descriptor == null) {
                return super.getDescriptor(domainClass);
            }
            getDescriptors().put(domainClass, descriptor);
            String alias = descriptor.getAlias();
            if (alias != null) {
                getProject().addAlias(alias, descriptor);
            }
            descriptor.remoteInitialization(this);
        }

        return descriptor;
    }

    /**
     * INTERNAL:
     * Return the table descriptor specified for the class.
     */
    @Override
    public ClassDescriptor getDescriptorForAlias(String alias) {
        ClassDescriptor descriptor = super.getDescriptorForAlias(alias);

        // If the descriptor is null then this means that descriptor must now be read from the server.
        if (descriptor == null) {
            if (!this.isMetadataRemote) {
                return null;
            }
            startOperationProfile(SessionProfiler.RemoteMetadata, null, SessionProfiler.ALL);
            descriptor = getRemoteConnection().getDescriptorForAlias(alias);
            endOperationProfile(SessionProfiler.RemoteMetadata, null, SessionProfiler.ALL);
            if (descriptor == null) {
                return null;
            }
            getDescriptors().put(descriptor.getJavaClass(), descriptor);            
            getProject().addAlias(alias, descriptor);
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
    @Override
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
    @Override
    public boolean isConnected() {
        if (getRemoteConnection() == null) {
            return false;
        }

        return getRemoteConnection().isConnected();
    }
        
    /**
     * INTERNAL:
     * Return if this session is a distributed session.
     */
    @Override
    public boolean isDistributedSession() {
        return true;
    }

    /**
     * INTERNAL:
     * Return if this session is a remote session.
     */
    @Override
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
    @Override
    public void rollbackTransaction() {
        startOperationProfile(SessionProfiler.Remote, null, SessionProfiler.ALL);
        getRemoteConnection().rollbackTransaction();
        endOperationProfile(SessionProfiler.Remote, null, SessionProfiler.ALL);
        getTransactionMutex().release();
    }

    /**
     * INTERNAL:
     * Set the remote connection.
     */
    public void setRemoteConnection(RemoteConnection remoteConnection) {
        this.remoteConnection = remoteConnection;
    }

    /**
     * PUBLIC:
     * Avoid printing the accessor and platform.
     */
    @Override
    public String toString() {
        return Helper.getShortClassName(getClass()) + "()";
    }

    /**
     * PUBLIC:
     * Logout the session, close the remote connection and release the hold resources
     */
    @Override
    public void logout() {
        //CR3854: logging out of DistributedSession is not releasing remote resources.
        //The remote connection remove() call should  release the resource, like the stateful
        //session been, which the connection holds on.
        this.remoteConnection.release();
    }

    /**
     * ADVANCED:
     * Return if the descriptors and meta-data should be serialized from the server,
     * or if they will be provided locally.
     */
    public boolean isMetadataRemote() {
        return isMetadataRemote;
    }

    /**
     * ADVANCED:
     * Set if the descriptors and meta-data should be serialized from the server,
     * or if they will be provided locally.
     */
    public void setIsMetadataRemote(boolean isMetadataRemote) {
        this.isMetadataRemote = isMetadataRemote;
    }
    
    /**
     * INTERNAL:
     * Connect not required.
     */
    @Override
    public void connect() throws DatabaseException {
        this.remoteConnection.initialize(this);
    }

    /**
     * INTERNAL:
     * Disconnect not required.
     */
    @Override
    public void disconnect() throws DatabaseException {
        getSequencingHome().onDisconnect();
    }

    /**
     * PUBLIC:
     * Connect to the database using the predefined login.
     * During connection, attempt to auto detect the required database platform.
     * This method can be used in systems where for ease of use developers have
     * EclipseLink autodetect the platform.
     * To be safe, however, the platform should be configured directly.
     * The login must have been assigned when or after creating the session.
     */
    @Override
    public void loginAndDetectDatasource() throws DatabaseException {
        preConnectDatasource();
        connect();
        Login login = this.remoteConnection.getLogin();
        setLogin(login);
        postConnectDatasource();
    }

    /**
     * PUBLIC:
     * Connect to the database using the predefined login.
     * Obtain the login from the server, as it may have configuration initialized from the database meta-data.
     */
    @Override
    public void login() throws DatabaseException {
        preConnectDatasource();
        connect();
        Login login = this.remoteConnection.getLogin();
        setLogin(login);
        postConnectDatasource();
    }
}
