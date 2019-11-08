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

import java.rmi.*;
import org.eclipse.persistence.internal.sessions.remote.*;

/**
 * Defines the public methods remote connection can invoke on the remote session controller.
 */
public interface RMIRemoteSessionController extends Remote {

    /**
     * INTERNAL:
     * This method is intended to be used by by sessions that wish to execute a command on a
     * remote session
     * @param remoteTransporter The Transporter carrying the command to be executed on the remote session
     */
    Transporter processCommand(Transporter remoteTransporter) throws RemoteException;

    Transporter beginTransaction() throws RemoteException;

    Transporter beginEarlyTransaction() throws RemoteException;

    /**
     * To commit remote unit of work on the server side.
     */
    Transporter commitRootUnitOfWork(Transporter remoteUnitOfWork) throws RemoteException;

    /**
     * Commit a transaction on the database.
     */
    Transporter commitTransaction() throws RemoteException;

    /**
     * Used for closing cursored streams across RMI.
     */
    Transporter cursoredStreamClose(Transporter remoetCursoredStreamID) throws RemoteException;

    /**
     * Retrieve next page size of objects from the remote cursored stream
     */
    Transporter cursoredStreamNextPage(Transporter remoteCursoredStream, int pageSize) throws RemoteException;

    /**
     * INTERNAL:
     * Return the cursored stream size
     */
    Transporter cursoredStreamSize(Transporter cursoredStream) throws RemoteException;

    /**
     * To get remote cursor stub in a transporter
     */
    Transporter cursorSelectObjects(Transporter policy) throws RemoteException;

    /**
     * A remote query after serialization is executed locally.
     */
    Transporter executeNamedQuery(Transporter name, Transporter theClass, Transporter arguments) throws RemoteException;

    /**
     * To execute remote query on the server side.
     */
    Transporter executeQuery(Transporter query) throws RemoteException;

    /**
     * To get the default read-only classes from the server side.
     **/
    Transporter getDefaultReadOnlyClasses() throws RemoteException;

    /**
     * To get descriptor from the server side
     */
    Transporter getDescriptor(Transporter domainClass) throws RemoteException;

    /**
     * To get descriptor from the server side
     */
    Transporter getDescriptorForAlias(Transporter alias) throws RemoteException;

    /**
     * To get login from the server side
     */
    Transporter getLogin() throws RemoteException;

    /**
     * INTERNAL:
     * Get the value returned by remote function call
     */
    Transporter getSequenceNumberNamed(Transporter remoteFunctionCall) throws RemoteException;

    Transporter initializeIdentityMapsOnServerSession() throws RemoteException;

    /**
     * To instantiate remote value holder on the server side.
     */
    Transporter instantiateRemoteValueHolderOnServer(Transporter remoteValueHolder) throws RemoteException;

    /**
     * Rollback a transaction on the database.
     */
    Transporter rollbackTransaction() throws RemoteException;

    /**
     * Moves the cursor to the given row number in the result set
     */
    Transporter scrollableCursorAbsolute(Transporter remoteScrollableCursorOid, int rows) throws RemoteException;

    /**
     * Moves the cursor to the end of the result set, just after the last row.
     */
    Transporter scrollableCursorAfterLast(Transporter remoteScrollableCursorOid) throws RemoteException;

    /**
     * Moves the cursor to the front of the result set, just before the first row
     */
    Transporter scrollableCursorBeforeFirst(Transporter remoteScrollableCursor) throws RemoteException;

    /**
     * Used for closing scrollable cursor across RMI.
     */
    Transporter scrollableCursorClose(Transporter remoteScrollableCursorOid) throws RemoteException;

    /**
     * Retrieves the current row index number
     */
    Transporter scrollableCursorCurrentIndex(Transporter remoteScrollableCursor) throws RemoteException;

    /**
     * Moves the cursor to the first row in the result set
     */
    Transporter scrollableCursorFirst(Transporter remoteScrollableCursor) throws RemoteException;

    /**
     * Indicates whether the cursor is after the last row in the result set.
     */
    Transporter scrollableCursorIsAfterLast(Transporter remoteScrollableCursor) throws RemoteException;

    /**
     * Indicates whether the cursor is before the first row in the result set.
     */
    Transporter scrollableCursorIsBeforeFirst(Transporter remoteScrollableCursor) throws RemoteException;

    /**
     * Indicates whether the cursor is on the first row of the result set.
     */
    Transporter scrollableCursorIsFirst(Transporter remoteScrollableCursor) throws RemoteException;

    /**
     * Indicates whether the cursor is on the last row of the result set.
     */
    Transporter scrollableCursorIsLast(Transporter remoteScrollableCursor) throws RemoteException;

    /**
     * Moves the cursor to the last row in the result set
     */
    Transporter scrollableCursorLast(Transporter remoteScrollableCursor) throws RemoteException;

    /**
     * INTERNAL:
     * Retrieve next object from the scrollable cursor
     */
    Transporter scrollableCursorNextObject(Transporter scrollableCursorOid) throws RemoteException;

    /**
     * INTERNAL:
     * Retrieve previous object from the scrollable cursor
     */
    Transporter scrollableCursorPreviousObject(Transporter scrollableCursorOid) throws RemoteException;

    /**
     * Moves the cursor to the given row number in the result set
     */
    Transporter scrollableCursorRelative(Transporter remoteScrollableCursor, int rows) throws RemoteException;

    /**
     * INTERNAL:
     * Return the cursor size
     */
    Transporter scrollableCursorSize(Transporter cursoredStream) throws RemoteException;
}
