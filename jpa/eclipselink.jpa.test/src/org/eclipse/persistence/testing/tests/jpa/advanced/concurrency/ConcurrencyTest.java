/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

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
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testDeadLockOnReadLock() {
        EntityManagerFactory emf = getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Department dept = new Department();
        em.persist(dept);
        Equipment equip = new Equipment();
        em.persist(equip);
        em.getTransaction().commit();
        em.close();
try{
        Integer i = new Integer(5);
        Thread thread1 = new Thread(new Runner1(i, dept.getId(), equip.getId(),emf));
        thread1.setName("Runner1");
        Thread thread2 = new Thread(new Runner2(i, dept.getId(), equip.getId(),emf));
        thread2.setName("Runner2");
        thread2.start();
        thread1.start();
        try {
            Thread.currentThread().sleep(8000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        synchronized (i) {
            i.notifyAll();
        }
        try {
            thread2.join(30000);
            thread1.join(30000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (thread2.isAlive() || thread1.isAlive()){
            thread2.interrupt();
            thread1.interrupt();
            fail("Dead-lock occurred");
       }
    }finally{
        em = emf.createEntityManager();
        em.getTransaction().begin();
        em.remove(em.find(Department.class, dept.getId()));
        em.remove(em.find(Equipment.class, equip.getId()));
        em.getTransaction().commit();
    }
    }
}
