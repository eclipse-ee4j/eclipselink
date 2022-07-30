/*
 * Copyright (c) 2008, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     12/04/2008 - 2.0 Darani Yallapragada
//       - 248780: Initial contribution for JPA 2.0
//     06/02/2010 - 2.1 Michael O'Brien
//       - 248780: Refactor Cache Implementation surrounding evict()
//         http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/cache_api#Refactor_20100322
//         Fix evict() to handle non-Entity classes
//         Refactor to get IdentityMapAccessor state through EMF reference
//         Refactor dependencies to use Interfaces instead of Impl subclasses
//         Handle no CMPPolicy case for getId()
//         Handle no associated descriptor for Class parameter
//         MappedSuperclasses passed to evict() cause implementing subclasses to be evicted
//         Throw an IAE for Interfaces and Embeddable classes passed to evict()
package org.eclipse.persistence.testing.tests.jpa.advanced;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.jpa.JpaCache;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Department;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;

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

    @Override
    public void setUp() {
        super.setUp();
        clearCache();
    }

    /**
     * Note: These tests are setup with ID generation - but it is not used.
     * We set the Id manually so we can test cache operations like eviction properly.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CacheImplJUnitTest");

        suite.addTest(new CacheImplJUnitTest("testSetup"));
        suite.addTest(new CacheImplJUnitTest("testContains"));
        suite.addTest(new CacheImplJUnitTest("testEvictClassObject"));
        suite.addTest(new CacheImplJUnitTest("testEvictClass"));
        suite.addTest(new CacheImplJUnitTest("testEvictAll"));
        suite.addTest(new CacheImplJUnitTest("testEvictContains"));
        suite.addTest(new CacheImplJUnitTest("testCacheAPI"));
        // 20100322: 248780: CacheImpl refactor for non-Entity classes

//        suite.addTest(new CacheImplJUnitTest("testGetId_fromUnmanagedMappedSuperclass_handles_null_descriptor"));
        suite.addTest(new CacheImplJUnitTest("testGetId_fromUnsupportedJavaLangInteger_throwsIAE_on_null_descriptor"));
        // Run these tests last as they modify the state of the ClassDescriptor permanently to verify variant corner use cases
//        suite.addTest(new CacheImplJUnitTest("testGetId_fromNativeMappedSuperclass_handles_null_cmp3policy_weaving_on"));
        // 315714: comment out 3 of the 10 new tests requiring setup() table creation - until the metamodel one is added to setup()
        // Tests fail to create tables when run outside of ant or before the metamodel model is created
        /*
        suite.addTest(new CacheImplJUnitTest("testGetId_fromNativeMappedSuperclass_handles_null_cmp3policy_and_null_pk_with_weaving_on"));
        suite.addTest(new CacheImplJUnitTest("testGetId_fromNativeMappedSuperclass_handles_null_cmp3policy_and_null_pk_with_weaving_off"));
        */
        // test null descriptor on closed entityManager
        return suite;
    }

    @Override
    public String getPersistenceUnitName(){
        return "default1";
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
        EntityManager em1 = createEntityManager();
        beginTransaction(em1);
        Employee e1 = new Employee();
        e1.setFirstName("ellie1");
        e1.setId(101);
        em1.persist(e1);
        commitTransaction(em1);
        closeEntityManager(em1);
        boolean result = getEntityManagerFactory().getCache().contains(Employee.class, 101);
        assertTrue("Employee not found in cache", result);
    }

    /**
     * Test cache API.
     */
    public void testCacheAPI() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee employee = new Employee();
        employee.setFirstName("testCacheAPI");
        em.persist(employee);
        commitTransaction(em);
        closeEntityManager(em);
        JpaCache cache = (JpaCache)getEntityManagerFactory().getCache();
        assertTrue("Employee not valid in cache", cache.isValid(employee));
        assertTrue("Employee not valid in cache", cache.isValid(Employee.class, employee.getId()));
        cache.timeToLive(employee);
        assertTrue("Employee not found in cache", cache.getObject(Employee.class, employee.getId()) != null);
        assertTrue("Employee not found in cache", cache.contains(employee));
        cache.evict(employee);
        cache.putObject(employee);
        cache.print();
        cache.removeObject(employee);
        cache.removeObject(Employee.class, employee.getId());
        cache.clear();
        cache.clear(Employee.class);
        cache.clearQueryCache();
        cache.clearQueryCache("findAllEmployeesByIdAndFirstName");
        assertTrue("Employee id not correct", employee.getId().equals(cache.getId(employee)));
        cache.print();
        cache.print(Employee.class);
        cache.printLocks();
        cache.validate();
    }

    /**
     * Test of evict method, of class CacheImpl.
     */
    public void testEvictClassObject() {
        String beforeCache;
        String afterCache;
        EntityManager em2 = createEntityManager();
        beginTransaction(em2);
        Employee e2 = new Employee();
        e2.setFirstName("ellie");
        e2.setId(121);
        em2.persist(e2);
        commitTransaction(em2);
        closeEntityManager(em2);
        EntityManager em3 = createEntityManager();
        EntityManager em4=createEntityManager();
        try {
            Employee emp1 = (Employee) getDatabaseSession().getIdentityMapAccessor().getFromIdentityMap(e2);
            emp1.setFirstName("foo");
            beforeCache = em3.find(Employee.class, 121).getFirstName();
            getEntityManagerFactory().getCache().evict(Employee.class, 121);
            Employee e3 = em4.find(Employee.class, 121);
            afterCache = e3.getFirstName();
            assertNotSame("Assertion Error", beforeCache, afterCache);
        } finally {
            closeEntityManager(em3);
            closeEntityManager(em4);
        }
    }

    /**
     * Test of evict method, of class CacheImpl.
     */
    public void testEvictClass() {
        EntityManager em5 = createEntityManager();
        beginTransaction(em5);
        Employee e4 = new Employee();
        e4.setFirstName("ellie");
        e4.setId(131);
        em5.persist(e4);
        commitTransaction(em5);
        closeEntityManager(em5);
        EntityManager em6 = createEntityManager();
        EntityManager em7 = createEntityManager();
        try {
            Employee emp2 = (Employee) getDatabaseSession().getIdentityMapAccessor().getFromIdentityMap(e4);
            emp2.setFirstName("food");
            String expected = em6.find(Employee.class, 131).getFirstName();
            getEntityManagerFactory().getCache().evict(Employee.class);
            Employee e5 = em7.find(Employee.class, 131);
            String actual = e5.getFirstName();
            assertNotSame("Assertion Error", expected, actual);
        } finally {
            closeEntityManager(em6);
            closeEntityManager(em7);
        }
    }

    /**
     * Test of evictAll method, of class CacheImpl.
     */
    public void testEvictAll() {
        EntityManager em8 = createEntityManager();
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
        EntityManager em9 = createEntityManager();
        try {
            Employee emp3 = (Employee) getDatabaseSession().getIdentityMapAccessor().getFromIdentityMap(e6);
            Department dept1 = (Department) getDatabaseSession().getIdentityMapAccessor().getFromIdentityMap(d1);
            emp3.setFirstName("foo");
            dept1.setName("science");
            getEntityManagerFactory().getCache().evictAll();
            Employee e4 = em9.find(Employee.class, 141);
            String actualEmp = e4.getFirstName();
            Department d2 = em9.find(Department.class, 3);
            String actualDept = d2.getName();
            assertEquals("Assertion Error", expectedEmp, actualEmp);
            assertEquals("Assertion Error", expectedDept, actualDept);
        } finally {
            closeEntityManager(em9);
        }

    }

    public void testEvictContains() {
        EntityManager em =  createEntityManager();
        beginTransaction(em);
        Employee emp = new Employee();
        emp.setFirstName("evictContains");
        em.persist(emp);
        commitTransaction(em);

        try {
            assertTrue(em.getEntityManagerFactory().getCache().contains(Employee.class, emp.getId()));

            em.clear();
            Employee findEmp = em.find(Employee.class, emp.getId());
            assertNotNull(findEmp);

            em.getEntityManagerFactory().getCache().evict(Employee.class, emp.getId());
            assertFalse(em.getEntityManagerFactory().getCache().contains(Employee.class, emp.getId()));
        } finally {
            closeEntityManager(em);
        }
    }


    public void testGetId_fromUnsupportedJavaLangInteger_throwsIAE_on_null_descriptor() {
        if (!isJPA10()) {
            boolean _exceptionThrown = false;
            EntityManager em1 = createEntityManager();
            JpaCache anEclipseLinkCache = (JpaCache)getEntityManagerFactory().getCache();
            try {
                anEclipseLinkCache.getId(1);
            } catch (IllegalArgumentException iae) {
                _exceptionThrown = true;
            } finally {
                closeEntityManager(em1);
                assertTrue("IllegalArgumentException should have been thrown on a getId() call on an Integer which obviously has no descriptor.", _exceptionThrown);
            }
        }
    }

}
