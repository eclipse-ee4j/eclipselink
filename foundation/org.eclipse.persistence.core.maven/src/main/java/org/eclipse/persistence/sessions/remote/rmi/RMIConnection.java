/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.sessions.remote.rmi;

import java.rmi.RemoteException;
import java.rmi.server.ObjID;
import java.util.Enumeration;
import java.util.IdentityHashMap;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.CommunicationException;
import org.eclipse.persistence.internal.sessions.remote.RemoteCommand;
import org.eclipse.persistence.internal.sessions.remote.RemoteConnection;
import org.eclipse.persistence.internal.sessions.remote.RemoteCursoredStream;
import org.eclipse.persistence.internal.sessions.remote.RemoteScrollableCursor;
import org.eclipse.persistence.internal.sessions.remote.RemoteUnitOfWork;
import org.eclipse.persistence.internal.sessions.remote.RemoteValueHolder;
import org.eclipse.persistence.internal.sessions.remote.Transporter;
import org.eclipse.persistence.queries.CursoredStreamPolicy;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.ScrollableCursorPolicy;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.remote.DistributedSession;
import org.eclipse.persistence.sessions.remote.RemoteSession;

/**
 * This class exists on on the client side which talks to remote session controller through
 * RMI connection.
 */
public class RMIConnection extends RemoteConnection {
    RMIRemoteSessionController remoteSessionController;

    /**
     * PUBLIC:
     * The connection must be create from the server-side session controllers stub.
     * The session in then created from the connection through createRemoteSession().
     * @see #createRemoteSession()
     */
    public RMIConnection(RMIRemoteSessionController controller) {
        this.remoteSessionController = controller;
    }

    /**
     * ADVANCED:
     * This method will send the command to the remote session for processing
     * @param command Contains a command that will be executed on the remote session
     * @see org.eclipse.persistence.internal.sessions.remote.RemoteCommand
     */
    @Override
    public void processCommand(RemoteCommand command) {
        try {
            Transporter transporter = new Transporter();
            transporter.setObject(command);
            transporter = getRemoteSessionController().processCommand(transporter);
            if (!transporter.wasOperationSuccessful()) {
                throw transporter.getException();
            }
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
    }

    /**
     * INTERNAL:
     * Begin a transaction on the database.
     */
    @Override
    public void beginTransaction() {
        try {
            Transporter transporter = getRemoteSessionController().beginTransaction();
            if (!transporter.wasOperationSuccessful()) {
                throw transporter.getException();
            }
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
    }

    /**
     * INTERNAL:
     * Begin an early unit of work transaction.
     */
    @Override
    public void beginEarlyTransaction() {
        try {
            Transporter transporter = getRemoteSessionController().beginEarlyTransaction();
            if (!transporter.wasOperationSuccessful()) {
                throw transporter.getException();
            }
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
    }

    /**
     * INTERNAL:
     * Commit root unit of work from the client side to the server side.
     */
    @Override
    public RemoteUnitOfWork commitRootUnitOfWork(RemoteUnitOfWork theRemoteUnitOfWork) {
        try {
            Transporter transporter = new Transporter();
            transporter.setObject(theRemoteUnitOfWork);
            transporter.prepare(this.session);
            transporter = getRemoteSessionController().commitRootUnitOfWork(transporter);
            transporter.expand(this.session);
            if (!transporter.wasOperationSuccessful()) {
                throw transporter.getException();
            } else {
                return (RemoteUnitOfWork)transporter.getObject();
            }
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
    }

    /**
     * INTERNAL:
     * Commit a transaction on the database.
     */
    @Override
    public void commitTransaction() {
        try {
            Transporter transporter = getRemoteSessionController().commitTransaction();
            if (!transporter.wasOperationSuccessful()) {
                throw transporter.getException();
            }
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
    }

    /**
     * PUBLIC:
     * Returns a remote session.
     */
    @Override
    public org.eclipse.persistence.sessions.Session createRemoteSession() {
        return new RemoteSession(this);
    }

    /**
     * Used for closing cursored streams across RMI.
     */
    @Override
    public void cursoredStreamClose(ObjID remoteCursoredStreamOid) {
        Transporter transporter = null;
        try {
            transporter = getRemoteSessionController().cursoredStreamClose(new Transporter(remoteCursoredStreamOid));
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
    }

    /**
     * Retrieve next page size of objects from the remote cursored stream
     */
    @Override
    public Vector cursoredStreamNextPage(RemoteCursoredStream remoteCursoredStream, ReadQuery query, DistributedSession session, int pageSize) {
        Transporter transporter = null;
        try {
            transporter = getRemoteSessionController().cursoredStreamNextPage(new Transporter(remoteCursoredStream.getID()), pageSize);
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }

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
    @Override
    public int cursoredStreamSize(ObjID remoteCursoredStreamID) {
        Transporter transporter = null;
        try {
            transporter = getRemoteSessionController().cursoredStreamSize(new Transporter(remoteCursoredStreamID));
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
        return ((Integer)transporter.getObject()).intValue();

    }

    /**
     * INTERNAL:
     * Returns remote cursor stream
     */
    @Override
    public RemoteCursoredStream cursorSelectObjects(CursoredStreamPolicy policy, DistributedSession session) {
        try {
            Transporter transporter = getRemoteSessionController().cursorSelectObjects(new Transporter(policy));
            if (!transporter.wasOperationSuccessful()) {
                throw transporter.getException();
            }

            RemoteCursoredStream remoteCursoredStream = (RemoteCursoredStream)transporter.getObject();
            remoteCursoredStream.setSession(session);
            remoteCursoredStream.setPolicy(policy);

            if (policy.getQuery().isReadAllQuery() && (!policy.getQuery().isReportQuery())) {// could be DataReadQuery
                fixObjectReferences(transporter, (ObjectLevelReadQuery)policy.getQuery(), session);
            }
            return remoteCursoredStream;
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
    }

    /**
     * INTERNAL:
     * Returns remote cursor stream
     */
    @Override
    public RemoteScrollableCursor cursorSelectObjects(ScrollableCursorPolicy policy, DistributedSession session) {
        try {
            Transporter transporter = getRemoteSessionController().cursorSelectObjects(new Transporter(policy));
            if (!transporter.wasOperationSuccessful()) {
                throw transporter.getException();
            }

            RemoteScrollableCursor remoteScrollableCursor = (RemoteScrollableCursor)transporter.getObject();
            remoteScrollableCursor.setSession(session);
            remoteScrollableCursor.setPolicy(policy);

            return remoteScrollableCursor;
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
    }

    /**
     * INTERNAL
     * Return the read-only classes
     */
    @Override
    public Vector getDefaultReadOnlyClasses() {
        try {
            Transporter transporter = getRemoteSessionController().getDefaultReadOnlyClasses();
            if (!transporter.wasOperationSuccessful()) {
                throw transporter.getException();
            } else {
                return (Vector)transporter.getObject();
            }
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
    }

    /**
     * INTERNAL:
     * Return the table descriptor specified for the class.
     */
    @Override
    public ClassDescriptor getDescriptor(Class domainClass) {
        try {
            Transporter transporter = getRemoteSessionController().getDescriptor(new Transporter(domainClass));
            if (!transporter.wasOperationSuccessful()) {
                throw transporter.getException();
            } else {
                return (ClassDescriptor)transporter.getObject();
            }
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
    }

    /**
     * INTERNAL:
     * Return the table descriptor specified for the alias.
     */
    @Override
    public ClassDescriptor getDescriptorForAlias(String alias) {
        try {
            Transporter transporter = getRemoteSessionController().getDescriptorForAlias(new Transporter(alias));
            if (!transporter.wasOperationSuccessful()) {
                throw transporter.getException();
            } else {
                return (ClassDescriptor)transporter.getObject();
            }
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
    }

    /**
     * INTERNAL:
     * Return the table descriptor specified for the class.
     */
    @Override
    public Login getLogin() {
        try {
            Transporter transporter = getRemoteSessionController().getLogin();
            if (!transporter.wasOperationSuccessful()) {
                throw transporter.getException();
            } else {
                return (Login)transporter.getObject();
            }
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
    }

    /**
     * INTERNAL:
     * Return the remote session controller
     */
    public RMIRemoteSessionController getRemoteSessionController() {
        return remoteSessionController;
    }

    /**
     * INTERNAL:
     * Perform remote function call
     */
    @Override
    public Object getSequenceNumberNamed(Object remoteFunctionCall) {
        try {
            Transporter transporter = getRemoteSessionController().getSequenceNumberNamed(new Transporter(remoteFunctionCall));
            Object returnValue = transporter.getObject();

            if (!transporter.wasOperationSuccessful()) {
                throw transporter.getException();
            }

            return returnValue;
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
    }

    /**
     * INTERNAL:
     * Reset the cache on the server-side session.
     */
    @Override
    public void initializeIdentityMapsOnServerSession() {
        try {
            Transporter transporter = getRemoteSessionController().initializeIdentityMapsOnServerSession();
            if (!transporter.wasOperationSuccessful()) {
                throw transporter.getException();
            }
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
    }

    /**
     * INTERNAL:
     * Instantiate remote value holder on the server
     */
    @Override
    public Transporter instantiateRemoteValueHolderOnServer(RemoteValueHolder remoteValueHolder) {
        try {
            Transporter transporter = new Transporter();
            transporter.setObject(remoteValueHolder);
            transporter.prepare(this.session);
            transporter = getRemoteSessionController().instantiateRemoteValueHolderOnServer(transporter);
            if (!transporter.wasOperationSuccessful()) {
                throw transporter.getException();
            }
            transporter.expand(this.session);
            return transporter;
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
    }

    /**
     * INTERNAL:
     * Execute the query on the server.
     */
    @Override
    public Transporter remoteExecute(DatabaseQuery query) {
        try {
            Transporter transporter = new Transporter();
            transporter.setObject(query);
            transporter.prepare(this.session);
            transporter = getRemoteSessionController().executeQuery(transporter);
            transporter.expand(this.session);
            if (!transporter.wasOperationSuccessful()) {
                throw transporter.getException();
            }
            return transporter;
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
    }

    /**
     * INTERNAL:
     * Execute query remotely.
     */
    @Override
    public Transporter remoteExecuteNamedQuery(String name, Class javaClass, Vector arguments) {
        try {
            Transporter transporter = getRemoteSessionController().executeNamedQuery(new Transporter(name), new Transporter(javaClass), new Transporter(arguments));
            if (!transporter.wasOperationSuccessful()) {
                throw transporter.getException();
            }
            return transporter;
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
    }

    /**
     * INTERNAL:
     * Rollback a transaction on the database.
     */
    @Override
    public void rollbackTransaction() {
        try {
            Transporter transporter = getRemoteSessionController().rollbackTransaction();
            if (!transporter.wasOperationSuccessful()) {
                throw transporter.getException();
            }
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
    }

    /**
     * Moves the cursor to the given row number in the result set
     */
    @Override
    public boolean scrollableCursorAbsolute(ObjID remoteScrollableCursorOid, int rows) {
        Transporter transporter = null;
        try {
            transporter = getRemoteSessionController().scrollableCursorAbsolute(new Transporter(remoteScrollableCursorOid), rows);
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }

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
    @Override
    public void scrollableCursorAfterLast(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        try {
            transporter = getRemoteSessionController().scrollableCursorAfterLast(new Transporter(remoteScrollableCursorOid));
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
    }

    /**
     * Moves the cursor to the front of the result set, just before the first row
     */
    @Override
    public void scrollableCursorBeforeFirst(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        try {
            transporter = getRemoteSessionController().scrollableCursorBeforeFirst(new Transporter(remoteScrollableCursorOid));
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
    }

    /**
     * Used for closing scrollable cursor across RMI.
     */
    @Override
    public void scrollableCursorClose(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        try {
            transporter = getRemoteSessionController().scrollableCursorClose(new Transporter(remoteScrollableCursorOid));
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
    }

    /**
     * Retrieves the current row index number
     */
    @Override
    public int scrollableCursorCurrentIndex(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        try {
            transporter = getRemoteSessionController().scrollableCursorAfterLast(new Transporter(remoteScrollableCursorOid));
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
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
    @Override
    public boolean scrollableCursorFirst(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        try {
            transporter = getRemoteSessionController().scrollableCursorFirst(new Transporter(remoteScrollableCursorOid));
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }

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
    @Override
    public boolean scrollableCursorIsAfterLast(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        try {
            transporter = getRemoteSessionController().scrollableCursorIsAfterLast(new Transporter(remoteScrollableCursorOid));
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
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
    @Override
    public boolean scrollableCursorIsBeforeFirst(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        try {
            transporter = getRemoteSessionController().scrollableCursorIsBeforeFirst(new Transporter(remoteScrollableCursorOid));
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }

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
    @Override
    public boolean scrollableCursorIsFirst(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        try {
            transporter = getRemoteSessionController().scrollableCursorIsFirst(new Transporter(remoteScrollableCursorOid));
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }

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
    @Override
    public boolean scrollableCursorIsLast(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        try {
            transporter = getRemoteSessionController().scrollableCursorIsLast(new Transporter(remoteScrollableCursorOid));
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }

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
    @Override
    public boolean scrollableCursorLast(ObjID remoteScrollableCursorOid) {
        Transporter transporter = null;
        try {
            transporter = getRemoteSessionController().scrollableCursorLast(new Transporter(remoteScrollableCursorOid));
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }

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
    @Override
    public Object scrollableCursorNextObject(ObjID remoteScrollableCursorOid, ReadQuery query, DistributedSession session) {
        Transporter transporter = null;
        try {
            transporter = getRemoteSessionController().scrollableCursorNextObject(new Transporter(remoteScrollableCursorOid));
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }

        if (transporter == null) {
            return null;
        }

        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }

        Object object = transporter.getObject();
        if (object == null) {
            // For bug 2797683 do not close when read the end!
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
    @Override
    public Object scrollableCursorPreviousObject(ObjID remoteScrollableCursorOid, ReadQuery query, DistributedSession session) {
        Transporter transporter = null;
        try {
            transporter = getRemoteSessionController().scrollableCursorPreviousObject(new Transporter(remoteScrollableCursorOid));
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }

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
    @Override
    public boolean scrollableCursorRelative(ObjID remoteScrollableCursorOid, int rows) {
        Transporter transporter = null;
        try {
            transporter = getRemoteSessionController().scrollableCursorRelative(new Transporter(remoteScrollableCursorOid), rows);
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }

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
    @Override
    public int scrollableCursorSize(ObjID cursorId) {
        Transporter transporter = null;
        try {
            transporter = getRemoteSessionController().scrollableCursorSize(new Transporter(cursorId));
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
        if (!transporter.wasOperationSuccessful()) {
            throw transporter.getException();
        }
        return ((Integer)transporter.getObject()).intValue();

    }

    /**
     * INTERNAL:
     * Set remote session controller
     */
    public void setRemoteSessionController(RMIRemoteSessionController remoteSessionController) {
        this.remoteSessionController = remoteSessionController;
    }
}
