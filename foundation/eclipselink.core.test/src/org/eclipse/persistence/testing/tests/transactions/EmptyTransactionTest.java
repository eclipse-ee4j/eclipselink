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
package org.eclipse.persistence.testing.tests.transactions;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;

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
        public void preBeginTransaction(SessionEvent event) {
            transactionOccurred(event);
        }
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

    public void setup() {
        getSession().getEventManager().addListener(eventAdapter);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.commit();
    }

    public void verify() {
        if (transactionOccurred) {
            throw new TestErrorException("A transaction was started in the UnitOfWork even though" + " there were no changes.");
        }
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getSession().getEventManager().removeListener(eventAdapter);
    }
}
