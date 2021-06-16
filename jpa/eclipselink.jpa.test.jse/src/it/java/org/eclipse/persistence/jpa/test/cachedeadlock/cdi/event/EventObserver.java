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
package org.eclipse.persistence.jpa.test.cachedeadlock.cdi.event;

import org.eclipse.persistence.jpa.test.cachedeadlock.MainThread;
import org.eclipse.persistence.jpa.test.cachedeadlock.model.CacheDeadLockDetectionMaster;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import jakarta.enterprise.event.ObservesAsync;

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
