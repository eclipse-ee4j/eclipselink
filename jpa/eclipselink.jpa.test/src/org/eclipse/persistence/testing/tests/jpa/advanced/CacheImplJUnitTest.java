/*******************************************************************************
* Copyright (c)  2008, Sun Microsystems, Inc. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     DaraniY  = 1.0 - Initialize contribution
******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.advanced;

import javax.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.models.jpa.advanced.*;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

/**
 * @author DaraniY
 */
public class CacheImplJUnitTest extends JUnitTestCase {


    public CacheImplJUnitTest() {
        super();
    }

    public CacheImplJUnitTest(String name) {
        super(name);
    }

    public static void setUpClass() throws Exception {
    }

    public static void tearDownClass() throws Exception {
    }

    public void setUp() {

        super.setUp();
        clearCache();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CacheImplJUnitTest");

        suite.addTest(new CacheImplJUnitTest("testSetup"));
        suite.addTest(new CacheImplJUnitTest("testContains"));
        suite.addTest(new CacheImplJUnitTest("testEvictClassObject"));
        suite.addTest(new CacheImplJUnitTest("testEvictClass"));
        suite.addTest(new CacheImplJUnitTest("testEvictAll"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }
    
    /**
     * Test of contains method, of class CacheImpl.
     */
    public void testContains() {
        EntityManager em1 = createEntityManager("default1");
        beginTransaction(em1);
        Employee e1 = new Employee();
        e1.setFirstName("ellie1");
        e1.setId(101);
        em1.persist(e1);
        commitTransaction(em1);
        closeEntityManager(em1);
        try {
            boolean result = getEntityManagerFactory("default1").getCache().contains(Employee.class, 101);
            assertTrue("Assertion Error",result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test of evict method, of class CacheImpl.
     */
    public void testEvictClassObject() {
        String beforeCache;
        String afterCache;
        EntityManager em2 = createEntityManager("default1");
        beginTransaction(em2);
        Employee e2 = new Employee();
        e2.setFirstName("ellie");
        e2.setId(121);
        em2.persist(e2);
        commitTransaction(em2);
        closeEntityManager(em2);
        EntityManager em3 = createEntityManager("default1");
        EntityManager em4=createEntityManager("default1");
        try {
            Employee emp1 = (Employee) ((EntityManagerFactoryImpl) getEntityManagerFactory("default1")).getServerSession().getIdentityMapAccessor().getFromIdentityMap(e2);
            emp1.setFirstName("foo");
            beforeCache = em3.find(Employee.class, 121).getFirstName();
            getEntityManagerFactory("default1").getCache().evict(Employee.class, 121);
            Employee e3 = em4.find(Employee.class, 121);
            afterCache = e3.getFirstName();
            assertNotSame("Assertion Error", beforeCache, afterCache);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeEntityManager(em3);
            closeEntityManager(em4);
        }
    }

    /**
     * Test of evict method, of class CacheImpl.
     */
    public void testEvictClass() {
        EntityManager em5 = createEntityManager("default1");
        beginTransaction(em5);
        Employee e4 = new Employee();
        e4.setFirstName("ellie");
        e4.setId(131);
        em5.persist(e4);
        commitTransaction(em5);
        closeEntityManager(em5);
        EntityManager em6 = createEntityManager("default1");
        EntityManager em7 = createEntityManager("default1");
        try {
            Employee emp2 = (Employee) ((EntityManagerFactoryImpl) getEntityManagerFactory("default1")).getServerSession().getIdentityMapAccessor().getFromIdentityMap(e4);
            emp2.setFirstName("food");
            String expected = em6.find(Employee.class, 131).getFirstName();
            getEntityManagerFactory("default1").getCache().evict(Employee.class);
            Employee e5 = em7.find(Employee.class, 131);
            String actual = e5.getFirstName();
            assertNotSame("Assertion Error", expected, actual);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeEntityManager(em6);
            closeEntityManager(em7);
        }
    }

    /**
     * Test of evictAll method, of class CacheImpl.
     */
    public void testEvictAll() {
        EntityManager em8 = createEntityManager("default1");
        beginTransaction(em8);
        Employee e6 = new Employee();
        e6.setFirstName("ellie");
        e6.setId(141);
        Department d1 = new Department();
        d1.setId(3);
        d1.setName("Computers");
        em8.persist(d1);
        em8.persist(e6);
        commitTransaction(em8);
        String expectedEmp = e6.getFirstName();
        String expectedDept = d1.getName();
        closeEntityManager(em8);
        EntityManager em9 = createEntityManager("default1");
        try {
            Employee emp3 = (Employee) ((EntityManagerFactoryImpl) getEntityManagerFactory("default1")).getServerSession().getIdentityMapAccessor().getFromIdentityMap(e6);
            Department dept1 = (Department) ((EntityManagerFactoryImpl) getEntityManagerFactory("default1")).getServerSession().getIdentityMapAccessor().getFromIdentityMap(d1);
            emp3.setFirstName("foo");
            dept1.setName("science");
            getEntityManagerFactory("default1").getCache().evictAll();
            Employee e4 = em9.find(Employee.class, 141);
            String actualEmp = e4.getFirstName();
            Department d2 = em9.find(Department.class, 3);
            String actualDept = d2.getName();
            assertEquals("Assertion Error", expectedEmp, actualEmp);
            assertEquals("Assertion Error", expectedDept, actualDept);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeEntityManager(em9);
        }

    }
}