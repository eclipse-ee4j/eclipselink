/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/24/2015-2.6.0 Rick Curtis
 *       - 460740: Fix pessimistic locking with setFirst/Max results on DB2
 *     03/13/2015-2.6.0 Will Dazey
 *       - 458301: Added tests for force increment on scalar results
 *     03/18/2015-2.6.0 Joe Grassel
 *       - 462498: Missing isolation level expression in SQL for Derby platform
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.locking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.locking.model.LockingDog;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestPessimisticLocking {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = { LockingDog.class, }, properties = { 
            @Property(name = "eclipselink.cache.shared.default", value = "false") })
    private EntityManagerFactory emf;

    static ExecutorService executor = null;

    @BeforeClass
    public static void beforeClass() {
        executor = Executors.newFixedThreadPool(5);
    }

    @AfterClass
    public static void afterClass() {
        executor.shutdownNow();
    }

    List<LockingDog> dogs;

    @Before
    public void before() {
        EntityManager em = emf.createEntityManager();
        dogs = new ArrayList<LockingDog>();
        try {
            em.getTransaction().begin();
            for (int i = 0; i < 10; i++) {
                LockingDog ld = new LockingDog();
                dogs.add(ld);
                em.persist(ld);
            }
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    @Test
    public void testPessimisticFind() throws Exception {
        EntityManager em = emf.createEntityManager();
        final EntityManager em2 = emf.createEntityManager();
        final CountDownLatch cdl = new CountDownLatch(2);
        try {
            em.getTransaction().begin();
            em2.getTransaction().begin();

            LockingDog locked = em.find(LockingDog.class, dogs.get(0).getId(), LockModeType.PESSIMISTIC_READ);
            Assert.assertNotNull(locked);
            Callable<LockingDog> blocked = new Callable<LockingDog>() {
                @Override
                public LockingDog call() {
                    cdl.countDown();
                    return em2.find(LockingDog.class, dogs.get(0).getId(), LockModeType.PESSIMISTIC_READ);
                }
            };
            Future<LockingDog> future = executor.submit(blocked);
            cdl.countDown();
            Thread.sleep(3000);
            // Make sure worker is blocked
            Assert.assertFalse(future.isDone());
            // Rolling back of tran should allow worker to complete
            em.getTransaction().rollback();
            Assert.assertEquals(locked.getId(), future.get().getId());

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em2.getTransaction().isActive()) {
                em2.getTransaction().rollback();
            }
        }
    }

    @Test
    public void testFirstResultPessimisticRead() throws Exception {
        EntityManager em = emf.createEntityManager();
        final EntityManager em2 = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em2.getTransaction().begin();
            em.createNamedQuery("find.lockingdogs", LockingDog.class).setLockMode(LockModeType.PESSIMISTIC_READ).setMaxResults(1).setFirstResult(4).getResultList();
            // This worker should block as he is trying to lock already locked
            // rows
            Future<Boolean> result = executor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    em2.createNamedQuery("find.lockingdogs", LockingDog.class).setLockMode(LockModeType.PESSIMISTIC_READ).setMaxResults(1).setFirstResult(5).getResultList();
                    return true;
                }
            });
            // Make sure the worker isn't done
            Assert.assertFalse(result.isDone());
            em.getTransaction().rollback();
            // Make sure that the worker obtained the locks and completed
            Assert.assertTrue(result.get());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em2.getTransaction().isActive()) {
                em2.getTransaction().rollback();
            }
            em.close();
            em2.close();
        }
    }

    @Test
    public void testMaxResultPessimisticRead() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            List<LockingDog> dogs = em.createNamedQuery("find.lockingdogs", LockingDog.class).
                    setLockMode(LockModeType.PESSIMISTIC_READ).setMaxResults(5).getResultList();
            Assert.assertEquals(5, dogs.size());

            dogs = em.createNamedQuery("find.lockingdogs", LockingDog.class).
                    setFirstResult(1).setMaxResults(5).getResultList();
            Assert.assertEquals(5, dogs.size());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    @Test
    public void testFirstResultMaxResultPessimisticRead() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            List<LockingDog> dogs = em.createNamedQuery("find.lockingdogs", LockingDog.class).
                    setLockMode(LockModeType.PESSIMISTIC_READ).setFirstResult(5).setMaxResults(5).getResultList();
            Assert.assertEquals(5, dogs.size());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    /**
     * This test verifies that aggregate queries will not fail with locking
     * set.
     * 
     * @see 
     *      org.eclipse.persistence.testing.tests.jpa.jpql.AdvancedQueryTestSuite
     *      .testQueryPESSIMISTIC_FORCE_INCREMENTLock()
     */
    @Test
    public void testAggregateResultPessimisticForceIncrement() {
        if (((EntityManagerFactoryImpl) emf).getServerSession().getDatasourcePlatform().isDerby()) {
            return;
        }
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("find.lockingdogs.avg").setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT).getSingleResult();
        } finally {
            em.getTransaction().rollback();
            em.close();
        }
    }

    /**
     * This test verifies that queries that return non-Entity results will not
     * fail up with locking set.
     * 
     * @see 
     *      org.eclipse.persistence.testing.tests.jpa.jpql.AdvancedQueryTestSuite
     *      .testQueryPESSIMISTIC_FORCE_INCREMENTLock()
     */
    @Test
    public void testObjectQueryPessimisticForceIncrement() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("find.lockingdogs.id").setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT).getResultList();
        } finally {
            em.getTransaction().rollback();
            em.close();
        }
    }
}
