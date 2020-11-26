/*******************************************************************************
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.test.cachedeadlock.cdi.event;

import org.eclipse.persistence.jpa.test.cachedeadlock.MainThread;
import org.eclipse.persistence.jpa.test.cachedeadlock.model.CacheDeadLockDetectionMaster;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import javax.enterprise.event.ObservesAsync;

public class EventObserver {

    public void onAsyncEvent(@ObservesAsync CacheDeadLockDetectionMaster cacheDeadLockDetectionMaster) {
        EntityManager em = MainThread.emf.createEntityManager();
        cacheDeadLockDetectionMaster = getMasterEntityByQuery(em, cacheDeadLockDetectionMaster.getId());
        cacheDeadLockDetectionMaster.setName(Thread.currentThread().toString());
        em.getTransaction().begin();
        em.merge(cacheDeadLockDetectionMaster);
        em.getTransaction().commit();
    }

    private CacheDeadLockDetectionMaster getMasterEntityByQuery(EntityManager em, long id) {
        Query query = em.createQuery("SELECT e FROM CacheDeadLockDetectionMaster AS e WHERE e.id = :id", CacheDeadLockDetectionMaster.class);
        query.setParameter("id", id);
        CacheDeadLockDetectionMaster cacheDeadLockDetectionMaster = (CacheDeadLockDetectionMaster)query.getSingleResult();
        return cacheDeadLockDetectionMaster;
    }

}
