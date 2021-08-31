/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.events;

import org.eclipse.persistence.sessions.*;

public class TestSessionListener extends SessionEventAdapter {
    public boolean postAcquireClientSession;
    public boolean postCommitUnitOfWork;
    public boolean preBeginTransaction;
    public boolean postCalculateUnitOfWork;
    public boolean preCalculateUnitOfWork;

    /**
     * TestSessionListener constructor comment.
     */
    public TestSessionListener() {
        super();
    }

    /**
     * moreRowsDetected method comment.
     */
    @Override
    public void moreRowsDetected(SessionEvent event) {
    }

    /**
     * outputParametersDetected method comment.
     */
    @Override
    public void outputParametersDetected(SessionEvent event) {
    }

    /**
     * postAcquireClientSession method comment.
     */
    @Override
    public void postAcquireClientSession(SessionEvent event) {
        postAcquireClientSession = true;
    }

    /**
     * postAcquireUnitOfWork method comment.
     */
    @Override
    public void postAcquireUnitOfWork(SessionEvent event) {
    }

    /**
     * postBeginTransaction method comment.
     */
    @Override
    public void postBeginTransaction(SessionEvent event) {
    }

    @Override
    public void preCalculateUnitOfWorkChangeSet(SessionEvent event) {
        preCalculateUnitOfWork = true;
    }

    @Override
    public void postCalculateUnitOfWorkChangeSet(SessionEvent event) {
        if (event.getProperty("UnitOfWorkChangeSet") != null) {
            postCalculateUnitOfWork = true;
        }
    }

    /**
     * postCommitTransaction method comment.
     */
    @Override
    public void postCommitTransaction(SessionEvent event) {
    }

    /**
     * postCommitUnitOfWork method comment.
     */
    @Override
    public void postCommitUnitOfWork(SessionEvent event) {
        postCommitUnitOfWork = true;
    }

    /**
     * postReleaseClientSession method comment.
     */
    @Override
    public void postConnect(SessionEvent event) {
    }

    /**
     * postExecuteQuery method comment.
     */
    @Override
    public void postExecuteQuery(SessionEvent event) {
    }

    /**
     * postReleaseClientSession method comment.
     */
    @Override
    public void postReleaseClientSession(SessionEvent event) {
    }

    /**
     * postReleaseUnitOfWork method comment.
     */
    @Override
    public void postReleaseUnitOfWork(SessionEvent event) {
    }

    /**
     * postResumeUnitOfWork method comment.
     */
    @Override
    public void postResumeUnitOfWork(SessionEvent event) {
    }

    /**
     * postRollbackTransaction method comment.
     */
    @Override
    public void postRollbackTransaction(SessionEvent event) {
    }

    /**
     * preBeginTransaction method comment.
     */
    @Override
    public void preBeginTransaction(SessionEvent event) {
        preBeginTransaction = true;
    }

    /**
     * preCommitTransaction method comment.
     */
    @Override
    public void preCommitTransaction(SessionEvent event) {
    }

    /**
     * preCommitUnitOfWork method comment.
     */
    @Override
    public void preCommitUnitOfWork(SessionEvent event) {
    }

    /**
     * preExecuteQuery method comment.
     */
    @Override
    public void preExecuteQuery(SessionEvent event) {
    }

    /**
     * prepareUnitOfWork method comment.
     */
    @Override
    public void prepareUnitOfWork(SessionEvent event) {
    }

    /**
     * preReleaseClientSession method comment.
     */
    @Override
    public void preReleaseClientSession(SessionEvent event) {
    }

    /**
     * preReleaseUnitOfWork method comment.
     */
    @Override
    public void preReleaseUnitOfWork(SessionEvent event) {
    }

    /**
     * preRollbackTransaction method comment.
     */
    @Override
    public void preRollbackTransaction(SessionEvent event) {
    }
}
