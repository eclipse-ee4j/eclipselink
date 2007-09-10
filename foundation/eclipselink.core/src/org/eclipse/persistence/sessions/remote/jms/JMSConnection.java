/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions.remote.jms;

import java.util.*;
import javax.jms.*;
import java.rmi.server.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.remote.*;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.remote.*;

/**
 * This class exists on on the client side which talks to remote session controller through
 * RMI connection.
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.sessions.coordination.jms.JMSTopicTransportManager}
 */
public class JMSConnection extends RemoteConnection {
    protected TopicSession session;
    protected TopicPublisher publisher;
    protected String topicName;

    /**
     * PUBLIC:
     * The connection must be create from the server-side session controllers stub.
     * The session in then created from the connection through createRemoteSession().
     * @see #createRemoteSession();
     */
    public JMSConnection(TopicSession session, TopicPublisher publisher) {
        this.session = session;
        this.publisher = publisher;
    }

    /**
     * ADVANCED:
     * This method will send the command to the remote session for processing
     * @param command RemoteCOmmand Contains a command that will be executed on the remote session
     * @see org.eclipse.persistence.internal.RemoteCommand
     */
    public void processCommand(RemoteCommand command) {
        try {
            ObjectMessage message = session.createObjectMessage();
            message.setObject(command);
            this.publisher.publish(message);
        } catch (JMSException exception) {
            throw new org.eclipse.persistence.exceptions.CommunicationException(exception.toString());
        }
    }

    /**
     * ADVANCED:
     * This method is intended to be used by newly connecting nodes to notify the
     * other nodes in a distributed system to send changes to this calling server
     * @param remoteTransporter Transporter This transporter contains the RemoteDispatcher of the calling
     * server.
     * @deprecated Since 4.0
     */
    public void addRemoteControllerForSynchronization(Object remoteDispatcher) throws Exception {
    }

    /**
     * INTERNAL:
     * Begin a transaction on the database.
     */
    public void beginTransaction() {
    }

    /**
     * INTERNAL:
     * Commit root unit of work from the client side to the server side.
     */
    public RemoteUnitOfWork commitRootUnitOfWork(RemoteUnitOfWork theRemoteUnitOfWork) {
        return null;
    }

    /**
     * INTERNAL:
     * Commit a transaction on the database.
     */
    public void commitTransaction() {
    }

    /**
     * PUBLIC:
     * Returns a remote session.
     */
    public org.eclipse.persistence.sessions.Session createRemoteSession() {
        return null;
    }

    /**
     * Used for closing cursored streams across RMI.
     */
    public void cursoredStreamClose(ObjID remoteCursoredStreamOid) {
    }

    /**
     * Retrieve next page size of objects from the remote cursored stream
     */
    public Vector cursoredStreamNextPage(RemoteCursoredStream remoteCursoredStream, ReadQuery query, RemoteSession session, int pageSize) {
        return null;
    }

    /**
     * Return the cursored stream size
     */
    public int cursoredStreamSize(ObjID remoteCursoredStreamID) {
        return 0;

    }

    /**
     * INTERNAL:
     * Returns remote cursor stream
     */
    public RemoteCursoredStream cursorSelectObjects(CursoredStreamPolicy policy, DistributedSession session) {
        return null;
    }

    /**
     * INTERNAL:
     * Returns remote cursor stream
     */
    public RemoteScrollableCursor cursorSelectObjects(ScrollableCursorPolicy policy, DistributedSession session) {
        return null;
    }

    /**
     * INTERNAL:
     * An object has been serialized from the server to the remote client.
     * Replace the transient attributes of the remote value holders with client-side objects.
     * Being used for the cursored stream only
     */
    public void fixObjectReferences(Transporter remoteCursoredStream, ObjectLevelReadQuery query, RemoteSession session) {
    }

    /**
     * INTERNAL
     * Return the read-only classes
     */
    public Vector getDefaultReadOnlyClasses() {
        return null;
    }

    /**
     * INTERNAL:
     * Return the table descriptor specified for the class.
     */
    public ClassDescriptor getDescriptor(Class domainClass) {
        return null;
    }

    /**
     * INTERNAL:
     * Return the table descriptor specified for the class.
     */
    public Login getLogin() {
        return null;
    }

    /**
     * INTERNAL:
     * Perform remote function call
     */
    public Object getSequenceNumberNamed(Object remoteFunctionCall) {
        return null;
    }

    /**
     * INTERNAL:
     * Reset the cache on the server-side session.
     */
    public void initializeIdentityMapsOnServerSession() {
    }

    /**
     * INTERNAL:
     * Instantiate remote value holder on the server
     */
    public Transporter instantiateRemoteValueHolderOnServer(RemoteValueHolder remoteValueHolder) {
        return null;
    }

    /**
     * INTERNAL:
     * Execute the query on the server.
     */
    public Transporter remoteExecute(DatabaseQuery query) {
        return null;
    }

    /**
     * INTERNAL:
     * Execute query remotely.
     */
    public Transporter remoteExecuteNamedQuery(String name, Class javaClass, Vector arguments) {
        return null;
    }

    /**
     * INTERNAL:
     * Rollback a transaction on the database.
     */
    public void rollbackTransaction() {
    }

    /**
     * Moves the cursor to the given row number in the result set
     */
    public boolean scrollableCursorAbsolute(ObjID remoteScrollableCursorOid, int rows) {
        return false;
    }

    /**
     * Moves the cursor to the end of the result set, just after the last row.
     */
    public void scrollableCursorAfterLast(ObjID remoteScrollableCursorOid) {
    }

    /**
     * Moves the cursor to the front of the result set, just before the first row
     */
    public void scrollableCursorBeforeFirst(ObjID remoteScrollableCursorOid) {
    }

    /**
     * Used for closing scrollable cursor across RMI.
     */
    public void scrollableCursorClose(ObjID remoteScrollableCursorOid) {
    }

    /**
     * Retrieves the current row index number
     */
    public int scrollableCursorCurrentIndex(ObjID remoteScrollableCursorOid) {
        return 0;
    }

    /**
     * Moves the cursor to the first row in the result set
     */
    public boolean scrollableCursorFirst(ObjID remoteScrollableCursorOid) {
        return false;
    }

    /**
     * Indicates whether the cursor is after the last row in the result set.
     */
    public boolean scrollableCursorIsAfterLast(ObjID remoteScrollableCursorOid) {
        return false;
    }

    /**
     * Indicates whether the cursor is before the first row in the result set.
     */
    public boolean scrollableCursorIsBeforeFirst(ObjID remoteScrollableCursorOid) {
        return false;
    }

    /**
     * Indicates whether the cursor is on the first row of the result set.
     */
    public boolean scrollableCursorIsFirst(ObjID remoteScrollableCursorOid) {
        return false;
    }

    /**
     * Indicates whether the cursor is on the last row of the result set.
     */
    public boolean scrollableCursorIsLast(ObjID remoteScrollableCursorOid) {
        return false;
    }

    /**
     * Moves the cursor to the last row in the result set
     */
    public boolean scrollableCursorLast(ObjID remoteScrollableCursorOid) {
        return false;
    }

    /**
     * Retrieve next object from the remote scrollable cursor
     */
    public Object scrollableCursorNextObject(ObjID remoteScrollableCursorOid, ReadQuery query, RemoteSession session) {
        return null;
    }

    /**
     * Retrieve previous object from the remote scrollable cursor
     */
    public Object scrollableCursorPreviousObject(ObjID remoteScrollableCursorOid, ReadQuery query, RemoteSession session) {
        return null;
    }

    /**
     * Moves the cursor to the given row number in the result set
     */
    public boolean scrollableCursorRelative(ObjID remoteScrollableCursorOid, int rows) {
        return false;
    }

    /**
     * Return the scrollable cursor size
     */
    public int scrollableCursorSize(ObjID cursorId) {
        return 0;
    }
}