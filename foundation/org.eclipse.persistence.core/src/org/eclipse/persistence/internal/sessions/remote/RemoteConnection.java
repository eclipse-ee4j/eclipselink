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
package org.eclipse.persistence.internal.sessions.remote;

import java.util.*;
import java.rmi.server.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.remote.*;

/**
 * Defines set of abstract methods which one must overwrite for any kind to implement a communication mechanism.
 */
public abstract class RemoteConnection implements java.io.Serializable {

    /**
     * This attribute is used to provide a globally unique identifier for this connection.
     * It should be the same value as the JNDI context or the RMIRegistry context.
     */
    protected String serviceName;
    
    /** Back reference to owning session. */
    protected DistributedSession session;

    /**
     * INTERNAL:
     * This method is intended to be used by newly connecting nodes to notify the
     * other nodes in a distributed system to send changes to this calling server
     * @param remoteTransporter Transporter This transporter contains the RemoteDispatcher of the calling
     * server.
     */
    public abstract void processCommand(RemoteCommand remoteCommand);

    /**
     * Allow the connection to initialize an setting in the session.
     */
    public void initialize(DistributedSession session) {
        this.session = session;
    }

    public DistributedSession getSession() {
        return session;
    }

    public void setSession(DistributedSession session) {
        this.session = session;
    }
    
    public boolean isConnected() {
        return true;
    }
    
    /**
     * Begin a transaction on the database.
     */
    public abstract void beginTransaction();
    
    /**
     * Begin an early unit of work transaction.
     */
    public abstract void beginEarlyTransaction();

    /**
     * Commit remote unit of work
     */
    public abstract RemoteUnitOfWork commitRootUnitOfWork(RemoteUnitOfWork remoteUnitOfWork);

    /**
     * Commit a transaction on the database.
     */
    public abstract void commitTransaction();

    /**
     * Returns remote client session.
     */
    public abstract org.eclipse.persistence.sessions.Session createRemoteSession();

    /**
     * Used for closing cursored streams across RMI.
     */
    public abstract void cursoredStreamClose(ObjID id);

    /**
     * Retrieve next page size of objects from the remote cursored stream
     */
    public abstract Vector cursoredStreamNextPage(RemoteCursoredStream remoteCursoredStream, ReadQuery query, DistributedSession session, int pageSize);

    /**
     * Return the cursored stream size
     */
    public abstract int cursoredStreamSize(ObjID remoteCursoredStreamID);

    /**
     * Get remote cursor stream.
     */
    public abstract RemoteCursoredStream cursorSelectObjects(CursoredStreamPolicy policy, DistributedSession session);

    /**
     * Get remote cursor stream.
     */
    public abstract RemoteScrollableCursor cursorSelectObjects(ScrollableCursorPolicy policy, DistributedSession session);

    /**
     * INTERNAL:
     * Get the read-only classes
     */
    public abstract Vector getDefaultReadOnlyClasses();

    /**
     * Get descriptor
     */
    public abstract ClassDescriptor getDescriptor(Class domainClass);

    /**
     * Get descriptor
     */
    public abstract ClassDescriptor getDescriptorForAlias(String alias);

    /**
     * Return the login informaiton from the server.
     */
    public abstract Login getLogin();

    /**
     * INTERNAL:
     * Perform remote function call
     */
    public abstract Object getSequenceNumberNamed(Object remoteFunctionCall);

    /**
     * ADVANCED:
     * This method is used to get the globally unique identifier for this connection.
     * This identifier should be the same as the JNDI context the service was stored under.
     * @return java.lang.String the name
     */
    public java.lang.String getServiceName() {
        if (serviceName == null) {
            serviceName = "";
        }
        return serviceName;
    }

    public abstract void initializeIdentityMapsOnServerSession();

    /**
     * Instantiated remote value holder.
     */
    public abstract Transporter instantiateRemoteValueHolderOnServer(RemoteValueHolder remoteValueHolder);

    /**
     * Execute query remotely.
     */
    public abstract Transporter remoteExecute(DatabaseQuery query);

    /**
     * Execute query remotely.
     */
    public abstract Transporter remoteExecuteNamedQuery(String name, Class javaClass, Vector arguments);

    /**
     * Rollback a transaction on the database.
     */
    public abstract void rollbackTransaction();

    /**
     * Moves the cursor to the given row number in the result set
     */
    public abstract boolean scrollableCursorAbsolute(ObjID remoteScrollableCursorOid, int rows);

    /**
     * Moves the cursor to the end of the result set, just after the last row.
     */
    public abstract void scrollableCursorAfterLast(ObjID remoteScrollableCursorOid);

    /**
     * Moves the cursor to the front of the result set, just before the first row
     */
    public abstract void scrollableCursorBeforeFirst(ObjID remoteScrollableCursorOid);

    /**
     * Used for closing scrolable cursor across RMI.
     */
    public abstract void scrollableCursorClose(ObjID remoteScrollableCursorOid);

    /**
     * Retrieves the current row index number
     */
    public abstract int scrollableCursorCurrentIndex(ObjID remoteScrollableCursorOid);

    /**
     * Moves the cursor to the first row in the result set
     */
    public abstract boolean scrollableCursorFirst(ObjID remoteScrollableCursorOid);

    /**
     * Indicates whether the cursor is after the last row in the result set.
     */
    public abstract boolean scrollableCursorIsAfterLast(ObjID remoteScrollableCursorOid);

    /**
     * Indicates whether the cursor is before the first row in the result set.
     */
    public abstract boolean scrollableCursorIsBeforeFirst(ObjID remoteScrollableCursorOid);

    /**
     * Indicates whether the cursor is on the first row of the result set.
     */
    public abstract boolean scrollableCursorIsFirst(ObjID remoteScrollableCursorOid);

    /**
     * Indicates whether the cursor is on the last row of the result set.
     */
    public abstract boolean scrollableCursorIsLast(ObjID remoteScrollableCursorOid);

    /**
     * Moves the cursor to the last row in the result set
     */
    public abstract boolean scrollableCursorLast(ObjID remoteScrollableCursorOid);

    /**
     * Retrieve next object from the remote scrollable cursor
     */
    public abstract Object scrollableCursorNextObject(ObjID remoteScrollableCursorOid, ReadQuery query, DistributedSession session);

    /**
     * Retrieve previous object from the remote scrollable cursor
     */
    public abstract Object scrollableCursorPreviousObject(ObjID remoteScrollableCursorOid, ReadQuery query, DistributedSession session);

    /**
     * Moves the cursor a relative number of rows, either positive or negative.
     * Attempting to move beyond the first/last row in the result set positions the cursor before/after the
     * the first/last row
     */
    public abstract boolean scrollableCursorRelative(ObjID remoteScrollableCursorOid, int rows);

    /**
     * Return the scrollable cursor size
     */
    public abstract int scrollableCursorSize(ObjID cursorId);

    /**
     * ADVANCED:
     * This method is used to set the globally unique identifier for this connection.
     * This identifier should be the same as the JNDI context the service was stored under.
     * @param newServiceName java.lang.String
     */
    public void setServiceName(java.lang.String newServiceName) {
        serviceName = newServiceName;
    }

    /**
     * PUBLIC:
     * Release the connection resource.
     */
    public void release() {
        //no-op
    }

    /**
     * INTERNAL:
     * An object has been serialized from the server to the remote client.
     * Replace the transient attributes of the remote value holders with client-side objects.
     * Being used for the cursored stream only
     */
    public void fixObjectReferences(Transporter remoteCursoredStream, ObjectLevelReadQuery query, DistributedSession session) {
        RemoteCursoredStream stream = (RemoteCursoredStream)remoteCursoredStream.getObject();
        List remoteObjectCollection = stream.getObjectCollection();
        if (query.isReadAllQuery() && (!query.isReportQuery())) {// could be DataReadQuery
            Vector clientObjectCollection = new Vector(remoteObjectCollection.size());

            // find next power-of-2 size
            Map recursiveSet = new IdentityHashMap(remoteObjectCollection.size() + 1);
            for (Object serverSideDomainObject : remoteObjectCollection) {
                clientObjectCollection.addElement(session.getObjectCorrespondingTo(serverSideDomainObject, remoteCursoredStream.getObjectDescriptors(), recursiveSet, query));
            }
            stream.setObjectCollection(clientObjectCollection);
        }
    }
}
