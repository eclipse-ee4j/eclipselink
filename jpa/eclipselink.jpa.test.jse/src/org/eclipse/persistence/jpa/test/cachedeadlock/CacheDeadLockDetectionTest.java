/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.test.cachedeadlock;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import javax.persistence.*;

import org.eclipse.persistence.internal.helper.ConcurrencyUtil;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.jpa.test.cachedeadlock.model.CacheDeadLockDetectionDetail;
import org.eclipse.persistence.jpa.test.cachedeadlock.model.CacheDeadLockDetectionMaster;
import org.eclipse.persistence.jpa.test.cachedeadlock.cdi.event.EventProducer;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CacheDeadLockDetectionTest {

    public static final int RECORDS_NO = 10;
    public static final int NO_OF_THREADS = 100;

    public static EntityManagerFactory emf = Persistence.createEntityManagerFactory("cachedeadlockdetection-pu");
    public static EntityManagerFactory emfSemaphore = Persistence.createEntityManagerFactory("cachedeadlocksemaphore-pu");

    SeContainer container;

    EventProducer eventProducer;

    @Test
    public void bugTest() {
        EntityManager em = emf.createEntityManager();
        verifyPersistenceProperties();
        setup();
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
        }
        threadExecution(em);
        try {
            Thread.currentThread().sleep(7000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("##########################Test with semaphores - begin###########################");
        EntityManager emSemaphore = emfSemaphore.createEntityManager();
        verifySemaphoreProperties();
        threadExecution(emSemaphore);
        System.out.println("##########################Test with semaphores - end###########################");
    }

    private void threadExecution(EntityManager em) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(NO_OF_THREADS);
        for (int i = 1; i <= NO_OF_THREADS; i++) {
            Thread thread = new Thread(new MainThread(container, emf, em));
            thread.setName("MainThread: " + i);
            executor.execute(thread);
        }
        executor.shutdown();
        // Wait for everything to finish.
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Before
    public void initContainer() {
        container = SeContainerInitializer.newInstance().initialize();
        eventProducer = container.select(EventProducer.class).get();
    }

    private static void initData(EntityManager em) {
        for (int i = 1, j = 1; i <= RECORDS_NO; i++, j = j +2) {
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


    public void setup() {
        EntityManager em = emf.createEntityManager();
        try {
            DatabaseSession session = ((EntityManagerImpl) em).getDatabaseSession();
            try {
                session.executeNonSelectingSQL("DROP TABLE cachedeadlock_detail");
            } catch (Exception ignore) {
            }
            try {
                session.executeNonSelectingSQL("DROP TABLE cachedeadlock_master");
            } catch (Exception ignore) {
            }
            try {
                session.executeNonSelectingSQL("CREATE TABLE cachedeadlock_master (id integer PRIMARY KEY, name varchar(200))");
                session.executeNonSelectingSQL("CREATE TABLE cachedeadlock_detail (id integer PRIMARY KEY, id_fk integer , name varchar(200))");
                session.executeNonSelectingSQL("ALTER TABLE cachedeadlock_detail ADD CONSTRAINT fk_cachedeadlock_detail FOREIGN KEY ( id_fk ) REFERENCES cachedeadlock_master (ID)");
            } catch (Exception ignore) {
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    private void verifyPersistenceProperties() {
        assertEquals(1L, ConcurrencyUtil.SINGLETON.getAcquireWaitTime());
        assertEquals(2L, ConcurrencyUtil.SINGLETON.getMaxAllowedSleepTime());
        assertEquals(800L, ConcurrencyUtil.SINGLETON.getMaxAllowedFrequencyToProduceTinyDumpLogMessage());
        assertEquals(1000L, ConcurrencyUtil.SINGLETON.getMaxAllowedFrequencyToProduceMassiveDumpLogMessage());
        assertEquals(5L, ConcurrencyUtil.SINGLETON.getBuildObjectCompleteWaitTime());
        assertTrue(ConcurrencyUtil.SINGLETON.isAllowTakingStackTraceDuringReadLockAcquisition());
        assertTrue(ConcurrencyUtil.SINGLETON.isAllowConcurrencyExceptionToBeFiredUp());
        assertTrue(ConcurrencyUtil.SINGLETON.isAllowInterruptedExceptionFired());
    }

    private void verifySemaphoreProperties() {
        assertEquals(1L, ConcurrencyUtil.SINGLETON.getAcquireWaitTime());
        assertEquals(2L, ConcurrencyUtil.SINGLETON.getMaxAllowedSleepTime());
        assertEquals(1000L, ConcurrencyUtil.SINGLETON.getMaxAllowedFrequencyToProduceTinyDumpLogMessage());
        assertEquals(2000L, ConcurrencyUtil.SINGLETON.getMaxAllowedFrequencyToProduceMassiveDumpLogMessage());
        assertTrue(ConcurrencyUtil.SINGLETON.isAllowTakingStackTraceDuringReadLockAcquisition());
        assertTrue(ConcurrencyUtil.SINGLETON.isAllowConcurrencyExceptionToBeFiredUp());
        assertTrue(ConcurrencyUtil.SINGLETON.isAllowInterruptedExceptionFired());
        assertTrue(ConcurrencyUtil.SINGLETON.isUseSemaphoreInObjectBuilder());
        assertEquals(5L, ConcurrencyUtil.SINGLETON.getNoOfThreadsAllowedToObjectBuildInParallel());
        assertTrue(ConcurrencyUtil.SINGLETON.isUseSemaphoreToLimitConcurrencyOnWriteLockManagerAcquireRequiredLocks());
        assertEquals(6L, ConcurrencyUtil.SINGLETON.getNoOfThreadsAllowedToDoWriteLockManagerAcquireRequiredLocksInParallel());
        assertEquals(7L, ConcurrencyUtil.SINGLETON.getConcurrencySemaphoreMaxTimePermit());
        assertEquals(8L, ConcurrencyUtil.SINGLETON.getConcurrencySemaphoreLogTimeout());
    }
}