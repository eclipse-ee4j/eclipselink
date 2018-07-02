/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.sessionbroker;

import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class BatchWritingWithSessionBrokerTest extends AutoVerifyTestCase {
    protected boolean doesUseBatchWriting;
    protected static int NUM_INSERTS = 5;

    /**
     * BatchWritingWithSessionBrokerTest constructor comment.
     */
    public BatchWritingWithSessionBrokerTest() {

        setDescription("Tests batch writing in conjunction with a unit of work, a session broker and a client/server session.");
    }

    public void reset() {
        ((SessionBroker)getSession()).getSessionForName("broker1").getLogin().dontUseBatchWriting();
        //((SessionBroker)getSession()).getSessionForName("broker2").getLogin().dontUseBatchWriting();
        getAbstractSession().rollbackTransaction();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
        ((SessionBroker)getSession()).getSessionForName("broker1").getLogin().useBatchWriting();
        //((SessionBroker)getSession()).getSessionForName("broker2").getLogin().useBatchWriting();
    }

    public void test() {

        UnitOfWork uow = ((SessionBroker)getSession()).acquireUnitOfWork();
        for (int i = 0; i < NUM_INSERTS; i++) {
            uow.registerObject(new Employee());
        }

        uow.commit();
    }

    public void verify() {
        int size = getSession().readAllObjects(Employee.class).size();
        if (size != 17) {
            throw new TestErrorException("Batch inserted objects not found, expects 17 objects found:" + size);
        }
    }
}
