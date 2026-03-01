/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.transactions;

import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * Test for Bug 2834266
 * Ensure no transaction is started when there are no changes in a UnitOfWork
 * Use pre and post transaction events.
 */
public class EmptyTransactionTest extends AutoVerifyTestCase {
    protected boolean transactionOccurred = false;

    // The following is an anonymous class which is used for event listening
    // it simply calls the commitOccurred() method.
    private SessionEventAdapter eventAdapter = new SessionEventAdapter() {
        @Override
        public void preBeginTransaction(SessionEvent event) {
            transactionOccurred(event);
        }
        @Override
        public void postBeginTransaction(SessionEvent event) {
            transactionOccurred(event);
        }
    };

    /**
     * Set the transaction occurred flag.
     */
    public void transactionOccurred(SessionEvent event) {
        transactionOccurred = true;
    }

    @Override
    public void setup() {
        getSession().getEventManager().addListener(eventAdapter);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    @Override
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.commit();
    }

    @Override
    public void verify() {
        if (transactionOccurred) {
            throw new TestErrorException("A transaction was started in the UnitOfWork even though" + " there were no changes.");
        }
    }

    @Override
    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getSession().getEventManager().removeListener(eventAdapter);
    }
}
