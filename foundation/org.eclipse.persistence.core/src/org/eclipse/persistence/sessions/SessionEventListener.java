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
package org.eclipse.persistence.sessions;

import org.eclipse.persistence.core.sessions.CoreSessionEventListener;

/**
 * <p><b>Purpose</b>: Used to support session events.
 * To register for events notification an event listener must be registered with the session.
 *
 * @see SessionEventManager#addListener(SessionEventListener)
 * @see Session#getEventManager()
 * @see SessionEvent
 */
public interface SessionEventListener extends CoreSessionEventListener {

    /**
     * PUBLIC:
     * This event is raised on the session if a descriptor is missing for a class being persisted.
     * This can be used to lazy register the descriptor or set of descriptors.
     */
    void missingDescriptor(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised on the session after read object query detected more than a single row back from the database.
     * The "result" of the event will be the call.  Some applications may want to interpret this as an error or warning condition.
     */
    void moreRowsDetected(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised on the session after update or delete SQL has been sent to the database
     * but a row count of zero was returned.
     */
    void noRowsModified(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised on the session after a stored procedure call has been executed that had output parameters.
     * If the proc was used to override an insert/update/delete operation then EclipseLink will not be expecting any return value.
     * This event mechanism allows for a listener to be registered before the proc is call to process the output values.
     * The event "result" will contain a Record of the output values, and property "call" will be the StoredProcedureCall.
     */
    void outputParametersDetected(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised on the client session after creation/acquiring.
     */
    void postAcquireClientSession(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised on when using the server/client sessions.
     * This event is raised after a connection is acquired from a connection pool.
     */
    void postAcquireConnection(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised when a ClientSession, with Isolated data, acquires
     * an exclusive connection.  The event will contain the ClientSession that
     * is being acquired.  Users can set properties within the ConnectionPolicy
     * of that ClientSession for access within this event.
     */
    void postAcquireExclusiveConnection(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised on the unit of work after creation/acquiring.
     * This will be raised on nest units of work.
     */
    void postAcquireUnitOfWork(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised after a database transaction is started.
     * It is not raised for nested transactions.
     */
    void postBeginTransaction(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised after the commit has begun on the UnitOfWork but before
     * the changes are calculated.
     */
    void preCalculateUnitOfWorkChangeSet(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised after the commit has begun on the UnitOfWork and
     * after the changes are calculated.  The UnitOfWorkChangeSet, at this point,
     * will contain changeSets without the version fields updated and without
     * IdentityField type primary keys.  These will be updated after the insert, or
     * update, of the object
     */
    void postCalculateUnitOfWorkChangeSet(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised after a database transaction is commited.
     * It is not raised for nested transactions.
     */
    void postCommitTransaction(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised on the unit of work after commit.
     * This will be raised on nest units of work.
     */
    void postCommitUnitOfWork(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised after the session connects to the database.
     * In a server session this event is raised on every new connection established.
     */
    void postConnect(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised after the execution of every query against the session.
     * The event contains the query and query result.
     */
    void postExecuteQuery(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised on the client session after releasing.
     */
    void postReleaseClientSession(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised on the unit of work after release.
     * This will be raised on nest units of work.
     */
    void postReleaseUnitOfWork(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised on the unit of work after resuming.
     * This occurs after pre/postCommit.
     */
    void postResumeUnitOfWork(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised after a database transaction is rolledback.
     * It is not raised for nested transactions.
     */
    void postRollbackTransaction(SessionEvent event);

    /**
     * PUBLIC:
     * This even will be raised after a UnitOfWorkChangeSet has been merged
     * When that changeSet has been received from a distributed session
     */
    void postDistributedMergeUnitOfWorkChangeSet(SessionEvent event);

    /**
     * PUBLIC:
     * This even will be raised after a UnitOfWorkChangeSet has been merged
     */
    void postMergeUnitOfWorkChangeSet(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised before a database transaction is started.
     * It is not raised for nested transactions.
     */
    void preBeginTransaction(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised before a database transaction is commited.
     * It is not raised for nested transactions.
     */
    void preCommitTransaction(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised on the unit of work before commit.
     * This will be raised on nest units of work.
     */
    void preCommitUnitOfWork(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised before the execution of every query against the session.
     * The event contains the query to be executed.
     */
    void preExecuteQuery(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised on the unit of work after the SQL has been flushed, but the commit transaction has not been executed.
     * It is similar to the JTS prepare phase.
     */
    void prepareUnitOfWork(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised on the client session before releasing.
     */
    void preReleaseClientSession(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised on when using the server/client sessions.
     * This event is raised before a connection is released into a connection pool.
     */
    void preReleaseConnection(SessionEvent event);

    /**
     * PUBLIC:
     * This event is fired just before a Client Session, with isolated data,
     * releases its Exclusive Connection
     */
    void preReleaseExclusiveConnection(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised on the unit of work before release.
     * This will be raised on nest units of work.
     */
    void preReleaseUnitOfWork(SessionEvent event);

    /**
     * PUBLIC:
     * This event is raised before a database transaction is rolledback.
     * It is not raised for nested transactions.
     */
    void preRollbackTransaction(SessionEvent event);

    /**
     * PUBLIC:
     * This even will be raised before a UnitOfWorkChangeSet has been merged
     * When that changeSet has been received from a distributed session
     */
    void preDistributedMergeUnitOfWorkChangeSet(SessionEvent event);

    /**
     * PUBLIC:
     * This even will be raised before a UnitOfWorkChangeSet has been merged
     */
    void preMergeUnitOfWorkChangeSet(SessionEvent event);

    /**
     * PUBLIC:
     * This Event is raised before the session logs in.
     */
    void preLogin(SessionEvent event);

    /**
     * PUBLIC:
     * This Event is raised after the session logs in.
     */
    void postLogin(SessionEvent event);

    /**
     * PUBLIC:
     * This Event is raised before the session logs out.
     */
    void preLogout(SessionEvent event);

    /**
     * PUBLIC:
     * This Event is raised after the session logs out.
     */
    void postLogout(SessionEvent event);
}
