/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
    public void moreRowsDetected(SessionEvent event) {
    }

    /**
     * outputParametersDetected method comment.
     */
    public void outputParametersDetected(SessionEvent event) {
    }

    /**
     * postAcquireClientSession method comment.
     */
    public void postAcquireClientSession(SessionEvent event) {
        postAcquireClientSession = true;
    }

    /**
     * postAcquireUnitOfWork method comment.
     */
    public void postAcquireUnitOfWork(SessionEvent event) {
    }

    /**
     * postBeginTransaction method comment.
     */
    public void postBeginTransaction(SessionEvent event) {
    }

    public void preCalculateUnitOfWorkChangeSet(SessionEvent event) {
        preCalculateUnitOfWork = true;
    }

    public void postCalculateUnitOfWorkChangeSet(SessionEvent event) {
        if (event.getProperty("UnitOfWorkChangeSet") != null) {
            postCalculateUnitOfWork = true;
        }
    }

    /**
     * postCommitTransaction method comment.
     */
    public void postCommitTransaction(SessionEvent event) {
    }

    /**
     * postCommitUnitOfWork method comment.
     */
    public void postCommitUnitOfWork(SessionEvent event) {
        postCommitUnitOfWork = true;
    }

    /**
     * postReleaseClientSession method comment.
     */
    public void postConnect(SessionEvent event) {
    }

    /**
     * postExecuteQuery method comment.
     */
    public void postExecuteQuery(SessionEvent event) {
    }

    /**
     * postReleaseClientSession method comment.
     */
    public void postReleaseClientSession(SessionEvent event) {
    }

    /**
     * postReleaseUnitOfWork method comment.
     */
    public void postReleaseUnitOfWork(SessionEvent event) {
    }

    /**
     * postResumeUnitOfWork method comment.
     */
    public void postResumeUnitOfWork(SessionEvent event) {
    }

    /**
     * postRollbackTransaction method comment.
     */
    public void postRollbackTransaction(SessionEvent event) {
    }

    /**
     * preBeginTransaction method comment.
     */
    public void preBeginTransaction(SessionEvent event) {
        preBeginTransaction = true;
    }

    /**
     * preCommitTransaction method comment.
     */
    public void preCommitTransaction(SessionEvent event) {
    }

    /**
     * preCommitUnitOfWork method comment.
     */
    public void preCommitUnitOfWork(SessionEvent event) {
    }

    /**
     * preExecuteQuery method comment.
     */
    public void preExecuteQuery(SessionEvent event) {
    }

    /**
     * prepareUnitOfWork method comment.
     */
    public void prepareUnitOfWork(SessionEvent event) {
    }

    /**
     * preReleaseClientSession method comment.
     */
    public void preReleaseClientSession(SessionEvent event) {
    }

    /**
     * preReleaseUnitOfWork method comment.
     */
    public void preReleaseUnitOfWork(SessionEvent event) {
    }

    /**
     * preRollbackTransaction method comment.
     */
    public void preRollbackTransaction(SessionEvent event) {
    }
}
