/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2021 IBM Corporation. All rights reserved.
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
    @Override
    public void missingDescriptor(SessionEvent event) { }

    @Override
    public void moreRowsDetected(SessionEvent event) { }

    @Override
    public void noRowsModified(SessionEvent event) { }

    @Override
    public void outputParametersDetected(SessionEvent event) { }

    @Override
    public void postAcquireClientSession(SessionEvent event) { }

    @Override
    public void postAcquireConnection(SessionEvent event) { }

    @Override
    public void postAcquireExclusiveConnection(SessionEvent event) { }

    @Override
    public void postAcquireUnitOfWork(SessionEvent event) { }

    @Override
    public void postBeginTransaction(SessionEvent event) { }

    @Override
    public void preCalculateUnitOfWorkChangeSet(SessionEvent event) { }

    @Override
    public void postCalculateUnitOfWorkChangeSet(SessionEvent event) { }


    public void postCommitTransaction(SessionEvent event) { }

    @Override
    public void postCommitUnitOfWork(SessionEvent event) { }

    @Override
    public void postDistributedMergeUnitOfWorkChangeSet(SessionEvent event) { }

    @Override
    public void postMergeUnitOfWorkChangeSet(SessionEvent event) { }

    @Override
    public void postConnect(SessionEvent event) { }

    @Override
    public void postExecuteCall(SessionEvent event) { }

    @Override
    public void postExecuteQuery(SessionEvent event) { }

    @Override
    public void postReleaseClientSession(SessionEvent event) { }


    @Override
    public void postReleaseUnitOfWork(SessionEvent event) { }

    @Override
    public void postResumeUnitOfWork(SessionEvent event) { }

    @Override
    public void postRollbackTransaction(SessionEvent event) { }

    @Override
    public void preBeginTransaction(SessionEvent event) { }

    @Override
    public void preCommitTransaction(SessionEvent event) { }

    @Override
    public void preCommitUnitOfWork(SessionEvent event) { }

    @Override
    public void preExecuteCall(SessionEvent event) { }

    @Override
    public void preExecuteQuery(SessionEvent event) { }

    @Override
    public void prepareUnitOfWork(SessionEvent event) { }

    @Override
    public void preReleaseClientSession(SessionEvent event) { }

    @Override
    public void preReleaseConnection(SessionEvent event) { }

    @Override
    public void preReleaseExclusiveConnection(SessionEvent event) { }

    @Override
    public void preReleaseUnitOfWork(SessionEvent event) { }

    @Override
    public void preDistributedMergeUnitOfWorkChangeSet(SessionEvent event) { }

    @Override
    public void preMergeUnitOfWorkChangeSet(SessionEvent event) { }

    @Override
    public void preRollbackTransaction(SessionEvent event) { }

    @Override
    public void preLogin(SessionEvent event) { }

    @Override
    public void postLogin(SessionEvent event) { }

    @Override
    public void preLogout(SessionEvent event) { }

    @Override
    public void postLogout(SessionEvent event) { }
}
