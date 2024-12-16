/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.jpa.deadlock.diagnostic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.ConcurrencyUtil;
import org.eclipse.persistence.internal.helper.WriteLockManager;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.CacheDeadLockDetectionDetail;
import org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.CacheDeadLockDetectionMaster;
import org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.DeadLockDiagnosticTableCreator;

import java.util.Map;

public class CacheDeadLockManagersTest extends JUnitTestCase {

    public static final int RECORDS_NO = 10;

    public CacheDeadLockManagersTest() {
    }

    public CacheDeadLockManagersTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CacheDeadLockDetectionTest");
        suite.addTest(new CacheDeadLockManagersTest("testSetup"));
        suite.addTest(new CacheDeadLockManagersTest("testWriteLockManagerAcquireLocksForClone"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow
     * execution in the server.
     */
    public void testSetup() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("cachedeadlockdetection-pu", JUnitTestCaseHelper.getDatabaseProperties());
        EntityManager em = emf.createEntityManager();
        new DeadLockDiagnosticTableCreator().replaceTables(((JpaEntityManager)em).getServerSession());
        clearCache("cachedeadlockdetection-pu");
        try {
            em.getTransaction().begin();
            initData(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
            if (emf.isOpen()) {
                emf.close();
            }
        }
    }

    public void testWriteLockManagerAcquireLocksForClone() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("cachedeadlockdetection-pu", JUnitTestCaseHelper.getDatabaseProperties());
        EntityManager em = emf.createEntityManager();
        AbstractSession serverSession = ((JpaEntityManager)em).getServerSession();
        LogWrapper logWrapper = new LogWrapper();
        serverSession.setSessionLog(logWrapper);
        AbstractSessionLog.setLog(logWrapper);
        try {
            Long primaryKey = Long.valueOf(1L);
            Query query = em.createQuery("SELECT c FROM CacheDeadLockDetectionMaster c WHERE c.id = :id");
            query.setParameter("id", primaryKey);
            CacheDeadLockDetectionMaster result = (CacheDeadLockDetectionMaster) query.getSingleResult();
            ClassDescriptor descriptor = serverSession.getDescriptor(CacheDeadLockDetectionMaster.class);
            CacheKey cacheKey = serverSession.retrieveCacheKey(primaryKey, descriptor, null, (ObjectBuildingQuery) ((EJBQueryImpl)query).getDatabaseQuery());
            cacheKey.setInvalidationState(CacheKey.CACHE_KEY_INVALID);
            //Tricky part with some negative value to simulate deadlock detection.
            ConcurrencyUtil.SINGLETON.setMaxAllowedSleepTime(-1000000);
            ConcurrencyUtil.SINGLETON.setAllowConcurrencyExceptionToBeFiredUp(true);
            WriteLockManager writeLockManager = new WriteLockManager();
            Map map = writeLockManager.acquireLocksForClone(result, descriptor, cacheKey, serverSession);
        } catch (Exception e) {
            assertEquals(2, logWrapper.getMessageCount(WriteLockManager.class.getName() + ".acquireLocksForClone"));
        } finally {
            if (em != null) {
                if (em.isOpen()) {
                    em.close();
                }
            }
        }
    }

    private static void initData(EntityManager em) {
        for (int i = 1, j = 1; i <= RECORDS_NO; i++, j = j + 2) {
            CacheDeadLockDetectionMaster cacheDeadLockDetectionMaster = new CacheDeadLockDetectionMaster(i, "M" + i);
            CacheDeadLockDetectionDetail cacheDeadLockDetectionDetail1 = new CacheDeadLockDetectionDetail(j, "D" + j);
            CacheDeadLockDetectionDetail cacheDeadLockDetectionDetail2 = new CacheDeadLockDetectionDetail(j + 1, "D" + (j + 1));
            cacheDeadLockDetectionDetail1.setMaster(cacheDeadLockDetectionMaster);
            cacheDeadLockDetectionDetail2.setMaster(cacheDeadLockDetectionMaster);
            em.persist(cacheDeadLockDetectionMaster);
            em.persist(cacheDeadLockDetectionDetail1);
            em.persist(cacheDeadLockDetectionDetail2);
        }
    }
}
