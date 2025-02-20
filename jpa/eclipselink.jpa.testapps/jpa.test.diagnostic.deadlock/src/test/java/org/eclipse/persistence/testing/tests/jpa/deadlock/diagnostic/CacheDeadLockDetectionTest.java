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

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.junit.Assert;

import org.eclipse.persistence.config.MergeManagerOperationMode;
import org.eclipse.persistence.internal.helper.ConcurrencyUtil;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.logging.AbstractSessionLog;

import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.CacheDeadLockDetectionDetail;
import org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.CacheDeadLockDetectionMaster;
import org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.DeadLockDiagnosticTableCreator;
import org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.MainThread;
import org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic.event.EventProducer;

public class CacheDeadLockDetectionTest extends JUnitTestCase {

    public static final int RECORDS_NO = 10;
    public static final int NO_OF_THREADS = 100;

    SeContainer container;

    EventProducer eventProducer;

    public CacheDeadLockDetectionTest() {
    }

    public CacheDeadLockDetectionTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CacheDeadLockDetectionTest");
        suite.addTest(new CacheDeadLockDetectionTest("testSetup"));
        suite.addTest(new CacheDeadLockDetectionTest("testVerifyPersistenceAndBasicLogOutput"));
        suite.addTest(new CacheDeadLockDetectionTest("testVerifySemaphorePersistenceProperties"));
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

    public void testVerifyPersistenceAndBasicLogOutput() {
        initContainer();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("cachedeadlockdetection-pu", JUnitTestCaseHelper.getDatabaseProperties());
        EntityManager em = emf.createEntityManager();
        AbstractSession serverSession = ((JpaEntityManager)em).getServerSession();
        LogWrapper logWrapper = new LogWrapper();
        serverSession.setSessionLog(logWrapper);
        AbstractSessionLog.setLog(logWrapper);
        verifyPersistenceProperties();
        threadExecution(em, emf);
        try {
            Thread.sleep(7000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
            closeContainer();
        }
        //Check if at least one log message is generated
        assertTrue(logWrapper.getMessageCount("Stuck thread problem: unique tiny message number") > 0);
        assertTrue(logWrapper.getMessageCount("Start full concurrency manager state (massive) dump No") > 0);
    }

    public void testVerifySemaphorePersistenceProperties() {
        EntityManagerFactory emfSemaphore = Persistence.createEntityManagerFactory("cachedeadlocksemaphore-pu", JUnitTestCaseHelper.getDatabaseProperties());
        EntityManager emSemaphore = emfSemaphore.createEntityManager();
        verifySemaphoreProperties();
    }

    private void initContainer() {
        container = SeContainerInitializer.newInstance().initialize();
        eventProducer = container.select(EventProducer.class).get();
    }

    private void closeContainer() {
        if (container != null && container.isRunning()) {
            container.close();
        }
    }

    private void threadExecution(EntityManager em, EntityManagerFactory emf) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(NO_OF_THREADS);
        for (int i = 1; i <= NO_OF_THREADS; i++) {
            Thread thread = new Thread(new MainThread(container, emf, em));
            thread.setName("MainThread: " + i);
            executor.execute(thread);
        }
        executor.shutdown();
        // Wait for everything to finish.
        try {
            if (!executor.awaitTermination(10000, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            e.printStackTrace();
            Assert.fail();
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

    private void verifyPersistenceProperties() {
        Assert.assertEquals(1L, ConcurrencyUtil.SINGLETON.getAcquireWaitTime());
        Assert.assertEquals(2L, ConcurrencyUtil.SINGLETON.getMaxAllowedSleepTime());
        Assert.assertEquals(800L, ConcurrencyUtil.SINGLETON.getMaxAllowedFrequencyToProduceTinyDumpLogMessage());
        Assert.assertEquals(1000L, ConcurrencyUtil.SINGLETON.getMaxAllowedFrequencyToProduceMassiveDumpLogMessage());
        Assert.assertEquals(5L, ConcurrencyUtil.SINGLETON.getBuildObjectCompleteWaitTime());
        //MergeManagerOperationMode.ORIGIN is default value not explicitly specified in persistence.xml
        Assert.assertEquals(MergeManagerOperationMode.ORIGIN, ConcurrencyUtil.SINGLETON.getConcurrencyManagerAllowGetCacheKeyForMergeMode());
        Assert.assertTrue(ConcurrencyUtil.SINGLETON.isAllowTakingStackTraceDuringReadLockAcquisition());
        Assert.assertTrue(ConcurrencyUtil.SINGLETON.isAllowConcurrencyExceptionToBeFiredUp());
        Assert.assertTrue(ConcurrencyUtil.SINGLETON.isAllowInterruptedExceptionFired());
    }

    private void verifySemaphoreProperties() {
        Assert.assertEquals(1L, ConcurrencyUtil.SINGLETON.getAcquireWaitTime());
        Assert.assertEquals(2L, ConcurrencyUtil.SINGLETON.getMaxAllowedSleepTime());
        Assert.assertEquals(1000L, ConcurrencyUtil.SINGLETON.getMaxAllowedFrequencyToProduceTinyDumpLogMessage());
        Assert.assertEquals(2000L, ConcurrencyUtil.SINGLETON.getMaxAllowedFrequencyToProduceMassiveDumpLogMessage());
        Assert.assertTrue(ConcurrencyUtil.SINGLETON.isAllowTakingStackTraceDuringReadLockAcquisition());
        Assert.assertTrue(ConcurrencyUtil.SINGLETON.isAllowConcurrencyExceptionToBeFiredUp());
        Assert.assertTrue(ConcurrencyUtil.SINGLETON.isAllowInterruptedExceptionFired());
        Assert.assertTrue(ConcurrencyUtil.SINGLETON.isUseSemaphoreInObjectBuilder());
        Assert.assertEquals(5L, ConcurrencyUtil.SINGLETON.getNoOfThreadsAllowedToObjectBuildInParallel());
        Assert.assertTrue(ConcurrencyUtil.SINGLETON.isUseSemaphoreToLimitConcurrencyOnWriteLockManagerAcquireRequiredLocks());
        Assert.assertEquals(6L, ConcurrencyUtil.SINGLETON.getNoOfThreadsAllowedToDoWriteLockManagerAcquireRequiredLocksInParallel());
        Assert.assertEquals(7L, ConcurrencyUtil.SINGLETON.getConcurrencySemaphoreMaxTimePermit());
        Assert.assertEquals(8L, ConcurrencyUtil.SINGLETON.getConcurrencySemaphoreLogTimeout());
    }
}
