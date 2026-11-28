/*
 * Copyright (c) 2024, 2025 Oracle and/or its affiliates. All rights reserved.
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
import jakarta.persistence.RollbackException;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.MergeManagerOperationMode;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ConcurrencyException;
import org.eclipse.persistence.internal.helper.ConcurrencyManager;
import org.eclipse.persistence.internal.helper.ConcurrencyUtil;
import org.eclipse.persistence.internal.helper.WriteLockManager;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.IdentityMapAccessor;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.CacheDeadLockDetectionDetail;
import org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.CacheDeadLockDetectionMaster;
import org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.DeadLockDiagnosticTableCreator;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

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
        suite.addTest(new CacheDeadLockManagersTest("testAbstractSessionCacheKeyFromTargetSessionForMerge"));
        suite.addTest(new CacheDeadLockManagersTest("testAbstractSessionCacheKeyFromTargetSessionForMergeWithLockedCacheKey"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow
     * execution in the server.
     */
    public void testSetup() {
        final String PU_NAME = "cachedeadlockdetection-pu";

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU_NAME, JUnitTestCaseHelper.getDatabaseProperties());
        EntityManager em = emf.createEntityManager();
        new DeadLockDiagnosticTableCreator().replaceTables(((JpaEntityManager)em).getServerSession());
        clearCache(PU_NAME);
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
        final String PU_NAME = "cachedeadlockdetection-pu";

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU_NAME, JUnitTestCaseHelper.getDatabaseProperties());
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
            //WriteLockManager.acquireLocksForClone acquire read lock, not write lock -> not any "...acquire writing.." message
            assertEquals(0, logWrapper.getMessageCount("waitingOnAcquireWritingCacheKey: true  waiting to acquire writing: --- CacheKey  (org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.CacheDeadLockDetectionMaster):  (primaryKey: 1)"));
            assertEquals(1, logWrapper.getMessageCount("Waiting to acquire (read lock): --- CacheKey  (org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.CacheDeadLockDetectionMaster):  (primaryKey: 1)"));
        } finally {
            if (em != null) {
                if (em.isOpen()) {
                    em.close();
                }
            }
        }
    }

    public void testAbstractSessionCacheKeyFromTargetSessionForMerge() {
        final String PU_NAME = "cachedeadlockdetection-loopwait-pu";
        final long MASTER_ID = 1000L;
        final long DETAIL_ID_1 = 1111L;
        final long DETAIL_ID_2 = 1112L;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU_NAME, JUnitTestCaseHelper.getDatabaseProperties());
        EntityManager em = emf.createEntityManager();
        assertEquals(MergeManagerOperationMode.WAITLOOP, ConcurrencyUtil.SINGLETON.getConcurrencyManagerAllowGetCacheKeyForMergeMode());
        clearCache(PU_NAME);
        try {
            em.getTransaction().begin();
            CacheDeadLockDetectionMaster cacheDeadLockDetectionMaster = new CacheDeadLockDetectionMaster(MASTER_ID, "M1000");
            CacheDeadLockDetectionDetail cacheDeadLockDetectionDetail1 = new CacheDeadLockDetectionDetail(DETAIL_ID_1, "D1111");
            cacheDeadLockDetectionDetail1.setMaster(cacheDeadLockDetectionMaster);
            em.persist(cacheDeadLockDetectionMaster);
            em.persist(cacheDeadLockDetectionDetail1);
            em.getTransaction().commit();

            IdentityMapAccessor identityMapAccessor = (IdentityMapAccessor) ((JpaEntityManager)em).getServerSession().getIdentityMapAccessor();
            CacheKey cacheKey = identityMapAccessor.getCacheKeyForObject(cacheDeadLockDetectionMaster);
            Semaphore semaphore = new Semaphore(1);
            semaphore.acquire();
            Object backupObject = cacheKey.getObject();
            //Lock existing cache key by another thread
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        cacheKey.acquire(true);
                        cacheKey.setObject(null);
                        semaphore.acquire();
                        cacheKey.setObject(backupObject);
                        cacheKey.release();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            thread.start();

            em.getTransaction().begin();
            CacheDeadLockDetectionDetail cacheDeadLockDetectionDetail2 = new CacheDeadLockDetectionDetail(DETAIL_ID_2, "D1112");
            cacheDeadLockDetectionDetail2.setMaster(cacheDeadLockDetectionMaster);
            em.persist(cacheDeadLockDetectionDetail2);
            //Release semaphore which block second thread and unlock cacheKey to allow process next piece of code without any issue.
            semaphore.release();
            em.getTransaction().commit();
            CacheDeadLockDetectionDetail findResult = em.find(CacheDeadLockDetectionDetail.class, DETAIL_ID_2);
            assertEquals(DETAIL_ID_2, findResult.getId());
            assertEquals("D1112", findResult.getName());
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

    public void testAbstractSessionCacheKeyFromTargetSessionForMergeWithLockedCacheKey() throws Exception {
        final String PU_NAME = "cachedeadlockdetection-loopwait-pu";
        final long MASTER_ID = 2000L;
        final long DETAIL_ID_1 = 2111L;
        final long DETAIL_ID_2 = 2222L;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU_NAME, JUnitTestCaseHelper.getDatabaseProperties());
        EntityManager em = emf.createEntityManager();
        AbstractSession serverSession = ((JpaEntityManager)em).getServerSession();
        LogWrapper logWrapper = new LogWrapper();
        serverSession.setSessionLog(logWrapper);
        AbstractSessionLog.setLog(logWrapper);
        assertEquals(MergeManagerOperationMode.WAITLOOP, ConcurrencyUtil.SINGLETON.getConcurrencyManagerAllowGetCacheKeyForMergeMode());
        clearCache(PU_NAME);
        try {
            em.getTransaction().begin();
            CacheDeadLockDetectionMaster cacheDeadLockDetectionMaster = new CacheDeadLockDetectionMaster(MASTER_ID, "M2000");
            CacheDeadLockDetectionDetail cacheDeadLockDetectionDetail1 = new CacheDeadLockDetectionDetail(DETAIL_ID_1, "D2111");
            cacheDeadLockDetectionDetail1.setMaster(cacheDeadLockDetectionMaster);
            em.persist(cacheDeadLockDetectionMaster);
            em.persist(cacheDeadLockDetectionDetail1);
            em.getTransaction().commit();

            IdentityMapAccessor identityMapAccessor = (IdentityMapAccessor) ((JpaEntityManager)em).getServerSession().getIdentityMapAccessor();
            CacheKey cacheKey = identityMapAccessor.getCacheKeyForObject(cacheDeadLockDetectionMaster);
            Semaphore semaphore = new Semaphore(0);
            CountDownLatch cacheKeyLocked = new CountDownLatch(1);
            Object backupObject = cacheKey.getObject();
            FutureTask<Void> lockOwner = new FutureTask<>(() -> {
                cacheKey.acquire(true);
                try {
                    cacheKey.setObject(null);
                    cacheKeyLocked.countDown();
                    semaphore.acquire();
                } finally {
                    cacheKey.setObject(backupObject);
                    cacheKey.release();
                }
                return null;
            });
            Thread thread = new Thread(lockOwner);
            thread.setDaemon(true);
            thread.start();
            try {
                assertTrue("The other thread did not acquire the cache key", cacheKeyLocked.await(5, TimeUnit.SECONDS));
                em.getTransaction().begin();
                CacheDeadLockDetectionDetail cacheDeadLockDetectionDetail2 = new CacheDeadLockDetectionDetail(DETAIL_ID_2, "D2222");
                cacheDeadLockDetectionDetail2.setMaster(cacheDeadLockDetectionMaster);
                em.persist(cacheDeadLockDetectionDetail2);
                // Allow diagnostic logging again after the preceding test's deadlock report.
                Thread.sleep(1000);
                try {
                    em.getTransaction().commit();
                    fail("The merge must fail while the other thread keeps the cache key locked");
                } catch (RollbackException e) {
                    assertTrue(e.getCause() instanceof ConcurrencyException);
                    assertEquals(ConcurrencyException.WAIT_WAS_INTERRUPTED, ((ConcurrencyException) e.getCause()).getErrorCode());
                }
                assertNull(ConcurrencyManager.getDeferredLockManager(Thread.currentThread()));
                assertFalse(AbstractSession.getThreadsToWaitMergeManagerWaitingDeferredCacheKeysSnapshot().containsKey(Thread.currentThread()));
                assertTrue(logWrapper.getMessageCount("Page 08 start") >= 1);
                assertTrue(logWrapper.getMessageCount("competing thread: " + thread) >= 1);
            } finally {
                semaphore.release();
                thread.join(TimeUnit.SECONDS.toMillis(5));
                assertFalse("The cache key owner did not finish", thread.isAlive());
                lockOwner.get();
            }
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
