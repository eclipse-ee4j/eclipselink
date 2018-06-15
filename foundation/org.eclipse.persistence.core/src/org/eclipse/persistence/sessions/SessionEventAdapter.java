/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.sessions;


/**
 * <p><b>Purpose</b>: To provide a trivial implemetation of SessionEventListener.
 * You may subclass this class rather than implement the entire SessonEventListener
 * interface in cases where only a small subset of the interface methods are needed.
 *
 * @see SessionEventManager#addListener(SessionEventListener)
 * @see SessionEventListener
 * @see SessionEvent
 */
public abstract class SessionEventAdapter implements SessionEventListener {

    /**
     * PUBLIC:
     * This event is raised on the session if a descriptor is missing for a class being persisted.
     * This can be used to lazy register the descriptor or set of descriptors.
     */
    @Override
    public void missingDescriptor(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised on the session after read object query detected more than a single row back from the database.
     * The "result" of the event will be the call.  Some applications may want to interpret this as an error or warning condition.
     */
    @Override
    public void moreRowsDetected(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised on the session after update or delete SQL has been sent to the database
     * but a row count of zero was returned.
     */
    @Override
    public void noRowsModified(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised on the session after a stored procedure call has been executed that had output parameters.
     * If the proc was used to override an insert/update/delete operation then EclipseLink will not be expecting any return value.
     * This event mechanism allows for a listener to be registered before the proc is call to process the output values.
     * The event "result" will contain a Record of the output values, and property "call" will be the StoredProcedureCall.
     */
    @Override
    public void outputParametersDetected(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised on the client session after creation/acquiring.
     */
    @Override
    public void postAcquireClientSession(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised on when using the server/client sessions.
     * This event is raised after a connection is acquired from a connection pool.
     */
    @Override
    public void postAcquireConnection(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised when a ClientSession, with Isolated data, acquires
     * an exclusive connection.
     */
    @Override
    public void postAcquireExclusiveConnection(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised on the unit of work after creation/acquiring.
     * This will be raised on nest units of work.
     */
    @Override
    public void postAcquireUnitOfWork(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised after a database transaction is started.
     * It is not raised for nested transactions.
     */
    @Override
    public void postBeginTransaction(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised after the commit has begun on the UnitOfWork but before
     * the changes are calculated.
     */
    @Override
    public void preCalculateUnitOfWorkChangeSet(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised after the commit has begun on the UnitOfWork and
     * after the changes are calculated.  The UnitOfWorkChangeSet, at this point,
     * will contain changeSets without the version fields updated and without
     * IdentityField type primary keys.  These will be updated after the insert, or
     * update, of the object
     */
    @Override
    public void postCalculateUnitOfWorkChangeSet(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised after a database transaction is commited.
     * It is not raised for nested transactions.
     */
    @Override
    public void postCommitTransaction(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised on the unit of work after commit.
     * This will be raised on nest units of work.
     */
    @Override
    public void postCommitUnitOfWork(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This even will be raised after a UnitOfWorkChangeSet has been merged
     * When that changeSet has been received from a distributed session
     */
    @Override
    public void postDistributedMergeUnitOfWorkChangeSet(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This even will be raised after a UnitOfWorkChangeSet has been merged
     */
    @Override
    public void postMergeUnitOfWorkChangeSet(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised after the session connects to the database.
     * In a server session this event is raised on every new connection established.
     */
    @Override
    public void postConnect(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised after the execution of every query against the session.
     * The event contains the query and query result.
     */
    @Override
    public void postExecuteQuery(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised on the client session after releasing.
     */
    @Override
    public void postReleaseClientSession(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised on the unit of work after release.
     * This will be raised on nest units of work.
     */
    @Override
    public void postReleaseUnitOfWork(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised on the unit of work after resuming.
     * This occurs after pre/postCommit.
     */
    @Override
    public void postResumeUnitOfWork(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised after a database transaction is rolledback.
     * It is not raised for nested transactions.
     */
    @Override
    public void postRollbackTransaction(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised before a database transaction is started.
     * It is not raised for nested transactions.
     */
    @Override
    public void preBeginTransaction(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised before a database transaction is committed.
     * It is not raised for nested transactions.
     */
    @Override
    public void preCommitTransaction(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised on the unit of work before commit.
     * This will be raised on nest units of work.
     */
    @Override
    public void preCommitUnitOfWork(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised before the execution of every query against the session.
     * The event contains the query to be executed.
     */
    @Override
    public void preExecuteQuery(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised on the unit of work after the SQL has been flushed, but the commit transaction has not been executed.
     * It is similar to the JTS prepare phase.
     */
    @Override
    public void prepareUnitOfWork(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised on the client session before releasing.
     */
    @Override
    public void preReleaseClientSession(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised on when using the server/client sessions.
     * This event is raised before a connection is released into a connection pool.
     */
    @Override
    public void preReleaseConnection(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is fired just before a Client Session, with isolated data,
     * releases its Exclusive Connection
     */
    @Override
    public void preReleaseExclusiveConnection(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised on the unit of work before release.
     * This will be raised on nest units of work.
     */
    @Override
    public void preReleaseUnitOfWork(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This even will be raised before a UnitOfWorkChangeSet has been merged
     * When that changeSet has been received from a distributed session
     */
    @Override
    public void preDistributedMergeUnitOfWorkChangeSet(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This even will be raised before a UnitOfWorkChangeSet has been merged
     */
    @Override
    public void preMergeUnitOfWorkChangeSet(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This event is raised before a database transaction is rolledback.
     * It is not raised for nested transactions.
     */
    @Override
    public void preRollbackTransaction(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This Event is raised before the session logs in.
     */
    @Override
    public void preLogin(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This Event is raised after the session logs in.
     */
    @Override
    public void postLogin(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This Event is raised before the session logs out.
     */
    @Override
    public void preLogout(SessionEvent event) {
    }

    /**
     * PUBLIC:
     * This Event is raised after the session logs out.
     */
    @Override
    public void postLogout(SessionEvent event) {
    }
}
