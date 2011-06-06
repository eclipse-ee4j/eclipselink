/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.advanced.concurrency;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import junit.framework.*;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import org.eclipse.persistence.testing.models.jpa.advanced.ConcurrencyA;
import org.eclipse.persistence.testing.models.jpa.advanced.ConcurrencyB;
import org.eclipse.persistence.testing.models.jpa.advanced.ConcurrencyC;
import org.eclipse.persistence.testing.models.jpa.advanced.Department;
import org.eclipse.persistence.testing.models.jpa.advanced.Equipment;

public class ConcurrencyTest extends JUnitTestCase {
    public ConcurrencyTest() {
        super();
    }

    public ConcurrencyTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("ConcurrencyTestSuite");

        suite.addTest(new ConcurrencyTest("testDeadLockOnReadLock"));
        suite.addTest(new ConcurrencyTest("testTransitionToDeferedFailure"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow
     * execution in the server.
     */
    public void testDeadLockOnReadLock() {
        if (isOnServer()) {
            return;
        }
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Department dept = new Department();
        em.persist(dept);
        Equipment equip = new Equipment();
        em.persist(equip);
        em.getTransaction().commit();
        em.close();
        try {
            Integer i = new Integer(5);
            Thread thread1 = new Thread(new Runner1(i, dept.getId(), equip.getId(), emf));
            thread1.setName("Runner1");
            Thread thread2 = new Thread(new Runner2(i, dept.getId(), equip.getId(), emf));
            thread2.setName("Runner2");
            thread2.start();
            thread1.start();
            try {
                Thread.currentThread().sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (i) {
                i.notifyAll();
            }
            try {
                thread2.join(30000);
                thread1.join(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (thread2.isAlive() || thread1.isAlive()) {
                thread2.interrupt();
                thread1.interrupt();
                fail("Dead-lock occurred");
            }
        } finally {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            em.remove(em.find(Department.class, dept.getId()));
            em.remove(em.find(Equipment.class, equip.getId()));
            em.getTransaction().commit();
        }
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow
     * execution in the server.
     */
    public void testTransitionToDeferedFailure() {
        if (isOnServer()) {
            return;
        }
        Integer toWaitOn = new Integer(4);
        Thread thread1 = null;
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
        ConcurrencyA a = new ConcurrencyA();
        ConcurrencyA a2 = new ConcurrencyA();
        ConcurrencyB b = new ConcurrencyB();
        ConcurrencyC c = new ConcurrencyC();
        em.persist(a);
        em.persist(a2);
        em.persist(c);
        em.persist(b);
        em.getTransaction().commit();
        em.close();
        try {
            thread1 = new Thread(new TransitionRunner1(toWaitOn, b, c, emf));
            thread1.start();

            em = emf.createEntityManager();
            a = em.find(ConcurrencyA.class, a.getId());
            a2 = em.find(ConcurrencyA.class, a2.getId());
            b = em.find(ConcurrencyB.class, b.getId());
            c = em.find(ConcurrencyC.class, c.getId());
            
            a2.setName(System.currentTimeMillis() + "_A");

            a.setConcurrencyB(b);
            a.setConcurrencyC(c);

            UnitOfWorkImpl uow = ((EntityManagerImpl) em).getActivePersistenceContext(null);
            try {
                Thread.currentThread().sleep(20000);
                synchronized (toWaitOn) {
                    toWaitOn.notifyAll();
                    toWaitOn.wait();
                }
            } catch (InterruptedException e) {
            }
            uow.issueSQLbeforeCompletion(true);
            uow.mergeClonesAfterCompletion();

            synchronized (toWaitOn) {
                toWaitOn.notifyAll();
            }
            try {
                thread1.join(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            if (thread1.isAlive()){
                thread1.interrupt();
            }
            em = emf.createEntityManager();
            em.getTransaction().begin();
            em.remove(em.find(ConcurrencyA.class, a.getId()));
            em.remove(em.find(ConcurrencyA.class, a2.getId()));
            em.remove(em.find(ConcurrencyC.class, c.getId()));
            em.remove(em.find(ConcurrencyB.class, b.getId()));
            em.getTransaction().commit();
        }
    }

}
