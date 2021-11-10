/*******************************************************************************
 * Copyright (c) 2018, 2021 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/14/2018-2.7 Will Dazey
 *       - 500753: Synchronize initialization of InsertQuery
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.concurrency;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;

import org.eclipse.persistence.jpa.test.concurrency.model.User;
import org.eclipse.persistence.jpa.test.concurrency.model.UserTag;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestConcurrencyPersistence {

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { User.class, UserTag.class })
    private EntityManagerFactory emf;

    /**
     * Persisting multiple entities simultaneously on separate threads has the possibility of
     * causing a null descriptor to be set on the InsertQuery. 
     * This test spins up threads and runs them multiple times to replicate the race condition. 
     * This test should pass everytime it's run, but there exists the possibility that it 
     * passes when none of the threads actually test the collision.
     * 
     * @throws Exception
     */
    @Test
    public void testInsertConcurrency() throws Exception {

        Queue<Exception> errors = new ConcurrentLinkedQueue();
        int runs = 10;
        for(int run = 0; run < runs; run++) {
            int threadCount = 3;
            CountDownLatch startSignal = new CountDownLatch(1);
            CountDownLatch doneSignal = new CountDownLatch(threadCount);
            for (int i = 0; i < threadCount; i++) {
                User user = new User((1000*run) + i);
                user.getTags().add(new UserTag("k1", "v1"));
                new PersistWorker(startSignal, doneSignal, errors, user).start();
            }
            //start all threads running
            startSignal.countDown();
            try {
                //wait until all threads complete
                boolean fin = doneSignal.await(10, TimeUnit.SECONDS);
                Assert.assertTrue("Timeout occurred while running test", fin);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        Assert.assertTrue(errors.toString(), errors.isEmpty());
    }

    /**
     * Bug 463042: Executing the same query simultaneously on separate threads has the possibility of
     * causing an ArrayOutOfBoundsException to be thrown. This test spins up multiple threads, executes
     * the same query on each and validates that none of the threads failed.
     * 
     * @throws Exception
     */
    @Test
    public void testCaseExpressionOperatorConcurrency() throws Exception {
        final AtomicInteger count = new AtomicInteger();
        final AtomicInteger error = new AtomicInteger();

        final int threads = 100;
        final ExecutorService taskExecutor = Executors.newFixedThreadPool(threads);

        // Spawn 100 threads
        for (int i = 0; i < threads; i++) {
            taskExecutor.execute(new Runnable() {
                public void run() {
                    count.incrementAndGet();

                    final EntityManager em = emf.createEntityManager();
                    try {
                        // Executing the Query
                        em.createNamedQuery("CONCURR_CASE_QUERY", Integer.class).setParameter("id", 1).getSingleResult();
                    } catch (Exception e) {
                        error.incrementAndGet();
                        System.out.println(e.getMessage());
                    } finally {
                        if (em != null) {
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            em.close();
                        }
                    }
                }
            });
        }
        taskExecutor.shutdown();
        taskExecutor.awaitTermination(5, TimeUnit.SECONDS);

        Assert.assertEquals("Expected no failures, but " + error.intValue() + "/" + count.intValue() + " threads failed", 0, error.intValue());
    }

    /**
     * Thread class that persists passed Object. 
     * Aligns transaction commit with other threads on the 'startSignal' latch.
     * Signals the 'doneSignal' latch when complete
     * Logs any exceptions in the passed Queue. 
     */
    private final class PersistWorker extends Thread {
        private final Queue<Exception> errors;
        private final CountDownLatch startSignal;
        private final CountDownLatch doneSignal;
        private final Object obj;

        PersistWorker(CountDownLatch startSignal, CountDownLatch doneSignal, Queue<Exception> errors, Object obj) {
            this.errors = errors;
            this.startSignal = startSignal;
            this.doneSignal = doneSignal;
            this.obj = obj;
        }

        @Override
        public void run() {
            EntityManager em = emf.createEntityManager();
            try {

                try {
                    emf.getMetamodel().entity(obj.getClass());
                } catch (IllegalArgumentException ie) {
                    this.errors.add(ie);
                    return;
                }

                EntityTransaction et = em.getTransaction();
                et.begin();
                em.persist(this.obj);

                //block thread here until all others are ready
                this.startSignal.await();
                et.commit();
            } catch (RollbackException re) {
                this.errors.add(re);
            } catch (InterruptedException e) {
                // ignore
            } finally {
                em.close();
                //notify that this thread has completed
                this.doneSignal.countDown();
            }
        }
    }
}
