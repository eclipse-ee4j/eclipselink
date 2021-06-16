/*
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.jpa.test.cachedeadlock;

import org.eclipse.persistence.jpa.test.cachedeadlock.cdi.event.EventProducer;
import org.eclipse.persistence.jpa.test.cachedeadlock.model.CacheDeadLockDetectionMaster;
import org.eclipse.persistence.jpa.test.cachedeadlock.model.CacheDeadLockDetectionDetail;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.inject.Inject;
import jakarta.persistence.*;

public class MainThread implements Runnable {

    public static final long ID = 1L;

    public static EntityManagerFactory emf = null;
    public static EntityManager em = null;

    TypedQuery<CacheDeadLockDetectionMaster> queryMaster;
    TypedQuery<CacheDeadLockDetectionDetail> queryDetail;

    SeContainer container;

    @Inject
    EventProducer eventProducer;

    public MainThread() {}

    public MainThread(SeContainer container, EntityManagerFactory emf, EntityManager em) {
        this.container = container;
        this.emf = emf;
        this.em = em;
        eventProducer = container.select(EventProducer.class).get();
        queryMaster = em.createNamedQuery("MasterEntity.findById", CacheDeadLockDetectionMaster.class);
        queryMaster.setParameter("id", ID);
        queryDetail = em.createNamedQuery("DetailEntity.findById", CacheDeadLockDetectionDetail.class);
        queryDetail.setParameter("id", ID);
    }

    public void run() {
        CacheDeadLockDetectionMaster resultsMaster = queryMaster.getSingleResult();
        CacheDeadLockDetectionDetail resultsDetail = queryDetail.getSingleResult();
        eventProducer.fireAsyncEvent(getMasterEntityByQuery(em));
    }

    private CacheDeadLockDetectionMaster getMasterEntityByQuery(EntityManager em) {
        Query query = em.createQuery("SELECT e FROM CacheDeadLockDetectionMaster AS e WHERE e.id = :id", CacheDeadLockDetectionMaster.class);
        query.setParameter("id", ID);
        CacheDeadLockDetectionMaster cacheDeadLockDetectionMaster = (CacheDeadLockDetectionMaster)query.getSingleResult();
        return cacheDeadLockDetectionMaster;
    }
}
