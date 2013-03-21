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
package org.eclipse.persistence.sessions.remote.corba.sun;

import java.util.*;
import java.rmi.server.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.remote.*;
import org.eclipse.persistence.sessions.remote.*;

/**
 * This class exists on on the client side which talks to remote session controller through
 * RMI connection.
 */
public class CORBAConnection extends RemoteConnection {
    CORBARemoteSessionController remoteSessionController;

    /**
     * PUBLIC:
     * The connection must be create from the server-side session controllers stub.
     * The session in then created from the connection through createRemoteSession().
     * @see #createRemoteSession();
     */
    public CORBAConnection(CORBARemoteSessionController controller) {
        this.remoteSessionController = controller;
    }

    /**
     * ADVANCED:
     * This method will send the command to the remote session for processing
     * @param command RemoteCOmmand Contains a command that will be executed on the remote session
     * @see org.eclipse.persistence.internal.RemoteCommand
     */
    public void processCommand(RemoteCommand command) {
        Transporter transporter = new Transporter();
        transporter.setObject(command);
        transporter = getRemoteSessionController().processCommand(transporter);
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
    }

    /**
     * INTERNAL:
     * Begin a transaction on the database.
     */
    public void beginTransaction() {
        Transporter transporter = getRemoteSessionController().beginTransaction();
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
    }

    /**
     * INTERNAL:
     * Begin a transaction on the database.
     */
    public void beginEarlyTransaction() {
        Transporter transporter = getRemoteSessionController().beginEarlyTransaction();
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
    }

    /**
     * INTERNAL:
     * Commit root unit of work from the client side to the server side.
     */
    public RemoteUnitOfWork commitRootUnitOfWork(RemoteUnitOfWork theRemoteUnitOfWork) {
        Transporter transporter = getRemoteSessionController().commitRootUnitOfWork(new Transporter(theRemoteUnitOfWork));
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        } else {
            return (RemoteUnitOfWork)transporter.getObject();
        }
    }

    /**
     * INTERNAL:
     * Commit a transaction on the database.
     */
    public void commitTransaction() {
        Transporter transporter = getRemoteSessionController().commitTransaction();
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
    }

    /**
     * PUBLIC:
     * Returns a remote session.
     */
    public org.eclipse.persistence.sessions.Session createRemoteSession() {
        return new RemoteSession(this);
    }

    /**
     * Used for closing cursored streams across RMI.
     */
    public void cursoredStreamClose(ObjID remoteCursoredStreamOid) {
        Transporter transporter = null;
        transporter = getRemoteSessionController().cursoredStreamClose(new Transporter(remoteCursoredStreamOid));

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
    }

    /**
     * Retrieve next page size of objects from the remote cursored stream
     */
    public Vector cursoredStreamNextPage(RemoteCursoredStream remoteCursoredStream, ReadQuery query, DistributedSession session, int pageSize) {
        Transporter transporter = null;
        transporter = getRemoteSessionController().cursoredStreamNextPage(new Transporter(remoteCursoredStream.getID()), pageSize);
        if (transporter == null) {
            return null;
        }

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }

        Vector serverNextPageObjects = (Vector)transporter.getObject();
        if (serverNextPageObjects == null) {
            cursoredStreamClose(remoteCursoredStream.getID());
            return null;
        }
        Vector clientNextPageObjects = serverNextPageObjects;
        if (query.isReadAllQuery() && (!query.isReportQuery())) {// could be DataReadQuery
            clientNextPageObjects = new Vector(serverNextPageObjects.size());
            for (Enumeration objEnum = serverNextPageObjects.elements(); objEnum.hasMoreElements();) {
                // 2612538 - the default size of Map (32) is appropriate
                Object clientObject = session.getObjectCorrespondingTo(objEnum.nextElement(), transporter.getObjectDescriptors(), new IdentityHashMap(), (ObjectLevelReadQuery)query);
                clientNextPageObjects.addElement(clientObject);
            }
        }

        return clientNextPageObjects;

    }

    /**
     * Return the cursored stream size
     */
    public int cursoredStreamSize(ObjID remoteCursoredStreamID) {
        Transporter transporter = null;
        transporter = getRemoteSessionController().cursoredStreamSize(new Transporter(remoteCursoredStreamID));

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
        return ((Integer)transporter.getObject()).intValue();

    }

    /**
     * INTERNAL:
     * Returns remote cursor stream
     */
    public RemoteCursoredStream cursorSelectObjects(CursoredStreamPolicy policy, DistributedSession session) {
        Transporter transporter = getRemoteSessionController().cursorSelectObjects(new Transporter(policy));
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }

        RemoteCursoredStream remoteCursoredStream = (RemoteCursoredStream)transporter.getObject();
        remoteCursoredStream.setSession(session);
        remoteCursoredStream.setPolicy(policy);

        if (policy.getQuery().isReadAllQuery() && (!policy.getQuery().isReportQuery())) {// could be DataReadQuery
            fixObjectReferences(transporter, (ObjectLevelReadQuery)policy.getQuery(), (DistributedSession)session);
        }
        return remoteCursoredStream;

    }

    /**
     * INTERNAL:
     * Returns remote cursor stream
     */
    public RemoteScrollableCursor cursorSelectObjects(ScrollableCursorPolicy policy, DistributedSession session) {
        Transporter transporter = getRemoteSessionController().cursorSelectObjects(new Transporter(policy));
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }

        RemoteScrollableCursor remoteScrollableCursor = (RemoteScrollableCursor)transporter.getObject();
        remoteScrollableCursor.setSession(session);
        remoteScrollableCursor.setPolicy(policy);

        return remoteScrollableCursor;

    }

    /**
     * INTERNAL:
     * Return the table descriptor specified for the class.
     */
    public ClassDescriptor getDescriptor(Class domainClass) {
        Transporter transporter = getRemoteSessionController().getDescriptor(new Transporter(domainClass));
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        } else {
            return (ClassDescriptor)transporter.getObject();
        }
    }

    /**
     * INTERNAL:
     * Return the table descriptor specified for the alias.
     */
    public ClassDescriptor getDescriptorForAlias(String alias) {
        Transporter transporter = getRemoteSessionController().getDescriptorForAlias(new Transporter(alias));
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        } else {
            return (ClassDescriptor)transporter.getObject();
        }
    }

    /**
     * INTERNAL:
     * Return the table descriptor specified for the alias.
     */
    public ClassDescriptor getDescriptorForAlias(Class domainClass) {
        Transporter transporter = getRemoteSessionController().getDescriptorForAlias(new Transporter(domainClass));
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        } else {
            return (ClassDescriptor)transporter.getObject();
        }
    }

    /**
     * INTERNAL
     * Return the read-only classes
     */
    public Vector getDefaultReadOnlyClasses() {
        Transporter transporter = getRemoteSessionController().getDefaultReadOnlyClasses();
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        } else {
            return (Vector)transporter.getObject();
        }
    }

    /**
     * INTERNAL:
     * Return the table descriptor specified for the class.
     */
    public Login getLogin() {
        Transporter transporter = getRemoteSessionController().getLogin();
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        } else {
            return (Login)transporter.getObject();
        }
    }

    /**
     * INTERNAL:
     * Return the remote session controller
     */
    public CORBARemoteSessionController getRemoteSessionController() {
        return remoteSessionController;
    }

    /**
     * INTERNAL:
     * Perform remote function call
     */
    public Object getSequenceNumberNamed(Object remoteFunctionCall) {
        Transporter transporter = getRemoteSessionController().getSequenceNumberNamed(new Transporter(remoteFunctionCall));
        Object returnValue = transporter.getObject();

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }

        return returnValue;

    }

    /**
     * INTERNAL:
     * Reset the cache on the server-side session.
     */
    public void initializeIdentityMapsOnServerSession() {
        Transporter transporter = getRemoteSessionController().initializeIdentityMapsOnServerSession();
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
    }

    /**
     * INTERNAL:
     * Instantiate remote value holder on the server
     */
    public Transporter instantiateRemoteValueHolderOnServer(RemoteValueHolder remoteValueHolder) {
        Transporter transporter = getRemoteSessionController().instantiateRemoteValueHolderOnServer(new Transporter(remoteValueHolder));
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
        return transporter;

    }

    /**
     * INTERNAL:
     * Execute the query on the server.
     */
    public Transporter remoteExecute(DatabaseQuery query) {
        Transporter transporter = getRemoteSessionController().executeQuery(new Transporter(query));
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
        return transporter;

    }

    /**
     * INTERNAL:
     * Execute query remotely.
     */
    public Transporter remoteExecuteNamedQuery(String name, Class javaClass, Vector arguments) {
        Transporter transporter = getRemoteSessionController().executeNamedQuery(new Transporter(name), new Transporter(javaClass), new Transporter(arguments));
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
        return transporter;

    }

    /**
     * INTERNAL:
     * Rollback a transaction on the database.
     */
    public void rollbackTransaction() {
        Transporter transporter = getRemoteSessionController().rollbackTransaction();
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
    }

    /**
     * Moves the cursor to the given row number in the result set
     */
    public boolean scrollableCursorAbsolute(ObjID remoteScrollableCursorOid, int rows) {
        Transporter transporter = null;
        transporter = getRemoteSessionController().scrollableCursorAbsolute(new Transporter(remoteScrollableCursorOid), rows);

        if (transporter == null) {
            return false;
        }

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
        return ((Boolean)transporter.getObject()).booleanValue();
    }

    /**
     * Moves the cursor to the end of the result set, just after the last row.
     */
    public void scrollableCursorAfterLast(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        transporter = getRemoteSessionController().scrollableCursorAfterLast(new Transporter(remoteScrollableCursorOid));

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
    }

    /**
     * Moves the cursor to the front of the result set, just before the first row
     */
    public void scrollableCursorBeforeFirst(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        transporter = getRemoteSessionController().scrollableCursorBeforeFirst(new Transporter(remoteScrollableCursorOid));
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
    }

    /**
     * Used for closing scrollable cursor across RMI.
     */
    public void scrollableCursorClose(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        transporter = getRemoteSessionController().scrollableCursorClose(new Transporter(remoteScrollableCursorOid));

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
    }

    /**
     * Retrieves the current row index number
     */
    public int scrollableCursorCurrentIndex(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        transporter = getRemoteSessionController().scrollableCursorAfterLast(new Transporter(remoteScrollableCursorOid));

        if (transporter == null) {
            return -1;
        }

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
        return ((Integer)transporter.getObject()).intValue();
    }

    /**
     * Moves the cursor to the first row in the result set
     */
    public boolean scrollableCursorFirst(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        transporter = getRemoteSessionController().scrollableCursorFirst(new Transporter(remoteScrollableCursorOid));

        if (transporter == null) {
            return false;
        }

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
        return ((Boolean)transporter.getObject()).booleanValue();
    }

    /**
     * Indicates whether the cursor is after the last row in the result set.
     */
    public boolean scrollableCursorIsAfterLast(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        transporter = getRemoteSessionController().scrollableCursorIsAfterLast(new Transporter(remoteScrollableCursorOid));

        if (transporter == null) {
            return false;
        }

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
        return ((Boolean)transporter.getObject()).booleanValue();
    }

    /**
     * Indicates whether the cursor is before the first row in the result set.
     */
    public boolean scrollableCursorIsBeforeFirst(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        transporter = getRemoteSessionController().scrollableCursorIsBeforeFirst(new Transporter(remoteScrollableCursorOid));

        if (transporter == null) {
            return false;
        }

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }

        return ((Boolean)transporter.getObject()).booleanValue();
    }

    /**
     * Indicates whether the cursor is on the first row of the result set.
     */
    public boolean scrollableCursorIsFirst(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        transporter = getRemoteSessionController().scrollableCursorIsFirst(new Transporter(remoteScrollableCursorOid));

        if (transporter == null) {
            return false;
        }

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
        return ((Boolean)transporter.getObject()).booleanValue();
    }

    /**
     * Indicates whether the cursor is on the last row of the result set.
     */
    public boolean scrollableCursorIsLast(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        transporter = getRemoteSessionController().scrollableCursorIsLast(new Transporter(remoteScrollableCursorOid));

        if (transporter == null) {
            return false;
        }

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
        return ((Boolean)transporter.getObject()).booleanValue();
    }

    /**
     * Moves the cursor to the last row in the result set
     */
    public boolean scrollableCursorLast(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        transporter = getRemoteSessionController().scrollableCursorLast(new Transporter(remoteScrollableCursorOid));

        if (transporter == null) {
            return false;
        }

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
        return ((Boolean)transporter.getObject()).booleanValue();
    }

    /**
     * Retrieve next object from the remote scrollable cursor
     */
    public Object scrollableCursorNextObject(ObjID remoteScrollableCursorOid, ReadQuery query, DistributedSession session) {
        Transporter transporter = null;
        transporter = getRemoteSessionController().scrollableCursorNextObject(new Transporter(remoteScrollableCursorOid));

        if (transporter == null) {
            return null;
        }

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }

        Object object = transporter.getObject();
        if (object == null) {
            // For bug 2797683 do not close if at end of stream.
            return null;
        }

        if (query.isReadAllQuery() && (!query.isReportQuery())) {// could be DataReadQuery
            object = session.getObjectCorrespondingTo(object, transporter.getObjectDescriptors(), new IdentityHashMap(), (ObjectLevelReadQuery)query);
        }
        return object;
    }

    /**
     * Retrieve previous object from the remote scrollable cursor
     */
    public Object scrollableCursorPreviousObject(ObjID remoteScrollableCursorOid, ReadQuery query, DistributedSession session) {
        Transporter transporter = null;
        transporter = getRemoteSessionController().scrollableCursorPreviousObject(new Transporter(remoteScrollableCursorOid));

        if (transporter == null) {
            return null;
        }

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }

        Object object = transporter.getObject();
        if (object == null) {
            // For bug 2797683 do not close if at end of stream.
            return null;
        }

        if (query.isReadAllQuery() && (!query.isReportQuery())) {// could be DataReadQuery
            object = session.getObjectCorrespondingTo(object, transporter.getObjectDescriptors(), new IdentityHashMap(), (ObjectLevelReadQuery)query);
        }
        return object;
    }

    /**
     * Moves the cursor to the given row number in the result set
     */
    public boolean scrollableCursorRelative(ObjID remoteScrollableCursorOid, int rows) {
        Transporter transporter = null;
        transporter = getRemoteSessionController().scrollableCursorRelative(new Transporter(remoteScrollableCursorOid), rows);

        if (transporter == null) {
            return false;
        }

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
        return ((Boolean)transporter.getObject()).booleanValue();
    }

    /**
     * Return the scrollable cursor size
     */
    public int scrollableCursorSize(ObjID cursorId) {
        Transporter transporter = null;

        transporter = getRemoteSessionController().scrollableCursorSize(new Transporter(cursorId));

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
        return ((Integer)transporter.getObject()).intValue();

    }

    /**
     * INTERNAL:
     * Set remote session controller
     */
    public void setRemoteSessionController(CORBARemoteSessionController remoteSessionController) {
        this.remoteSessionController = remoteSessionController;
    }
}
