/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.tests.jpa.advanced.concurrency;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.testing.models.jpa.advanced.ConcurrencyB;
import org.eclipse.persistence.testing.models.jpa.advanced.ConcurrencyC;

public class TransitionRunner1 implements Runnable {
    protected ConcurrencyB concB;
    protected ConcurrencyC concC;
    protected EntityManagerFactory emf;
    protected Object toWaitOn;

    public TransitionRunner1(Object toWaitOn, ConcurrencyB concB, ConcurrencyC concC, EntityManagerFactory emf) {
        this.concB = concB;
        this.concC = concC;
        this.emf = emf;
        this.toWaitOn = toWaitOn;
    }

    public void run() {
        EntityManager em = emf.createEntityManager();
        ConcurrencyB b = em.find(ConcurrencyB.class, concB.getId());
        ConcurrencyC c = em.find(ConcurrencyC.class, concC.getId());
        c.setName(System.currentTimeMillis() + "_C");
        b.setName(System.currentTimeMillis() + "_B");
        UnitOfWorkImpl uow = ((EntityManagerImpl) em).getActivePersistenceContext(null);
        try {
            synchronized (toWaitOn) {
                toWaitOn.wait(120000);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        uow.issueSQLbeforeCompletion(true);
        try {
            synchronized (toWaitOn) {
                toWaitOn.notifyAll();
                toWaitOn.wait(6000);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        uow.release();
    }

}
