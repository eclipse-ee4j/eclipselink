/*
 * Copyright (c) 2012, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     14/12/2012 -2.5 Gordon Yorke
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa21.advanced.Employee;

import java.util.List;


public class EntityManagerFactoryTest extends JUnitTestCase {
    protected boolean m_reset = false;

    public EntityManagerFactoryTest() {}

    public EntityManagerFactoryTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "advanced2x";
    }

    @Override
    public void setUp () {
        m_reset = true;
        super.setUp();
        clearCache();
    }

    @Override
    public void tearDown () {
        if (m_reset) {
            m_reset = false;
        }

        super.tearDown();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("EntityManagerFactoryTest");

        suite.addTest(new EntityManagerFactoryTest("testSetup"));

        // These tests call stored procedures that return a result set.
        suite.addTest(new EntityManagerFactoryTest("testAddNamedQuery"));
        suite.addTest(new EntityManagerFactoryTest("testGetPersistenceUnitUtilOnCloseEMF"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(getPersistenceUnitServerSession());
        EmployeePopulator employeePopulator = new EmployeePopulator();
        employeePopulator.buildExamples();
        employeePopulator.persistExample(getPersistenceUnitServerSession());
        clearCache();
    }

    @SuppressWarnings({"unchecked"})
    public void testAddNamedQuery(){
        EntityManager em = createEntityManager();
        EntityManagerFactory factory = em.getEntityManagerFactory();
        List<Object[]> names = em.createQuery("Select e.firstName, count(e.firstName) as c from Employee e group by e.firstName order by c").getResultList();
        String name = (String) names.get(names.size()-1)[0];
        Query query = em.createQuery("Select e from Employee e where e.firstName = :p1 order by e.id");
        query.setParameter("p1", name);
        assertEquals("Unable to retrieve parameter value from query", query.getParameterValue("p1"), name);
        List<Employee> firstResult = query.getResultList();
        factory.addNamedQuery("Select_Employee_by_first_name", query);
        closeEntityManager(em);
        em = createEntityManager();

        Query namedQuery = em.createNamedQuery("Select_Employee_by_first_name");
        assertFalse("Named query retains parameter values from original query", namedQuery.isBound(namedQuery.getParameter("p1")));
        namedQuery.setParameter("p1", name);
        List<Employee> secondResult = namedQuery.getResultList();
        for (int i = firstResult.size()-1; i> -1; --i){
            assertEquals("Results do not match", firstResult.get(i).getId(), secondResult.get(i).getId());
        }
        names = em.createQuery("Select e.lastName, count(e.lastName) as c from Employee e group by e.lastName order by c").getResultList();
        name = (String) names.get(names.size()-1)[0];
        query = em.createQuery("Select e from Employee e where e.lastName = :p1 order by e.id");
        query.setParameter("p1", name);
        firstResult = query.getResultList();
        factory.addNamedQuery("Select_Employee_by_first_name", query);
        closeEntityManager(em);
        em = createEntityManager();

        namedQuery = em.createNamedQuery("Select_Employee_by_first_name");
        assertFalse("Named query retains parameter values from original query", namedQuery.isBound(namedQuery.getParameter("p1")));
        namedQuery.setParameter("p1", name);
        secondResult = namedQuery.getResultList();
        for (int i = firstResult.size()-1; i> -1; --i){
            assertEquals("Results do not match", firstResult.get(i).getId(), secondResult.get(i).getId());
        }
        query = em.createQuery("Select e from Employee e where e.lastName = :p1 order by e.id");
        query.setMaxResults(1);
        query.setFirstResult(1);
        query.setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        factory.addNamedQuery("Select_Employee_by_first_name", query);
        closeEntityManager(em);
        em = createEntityManager();

        try {
            beginTransaction(em);
            namedQuery = em.createNamedQuery("Select_Employee_by_first_name");
            assertEquals("LockMode not retained", namedQuery.getLockMode(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            assertEquals("MaxResults not retained", 1, namedQuery.getMaxResults());
            assertEquals("FirstResult not retained", 1, namedQuery.getFirstResult());
            namedQuery.setParameter("p1", names.get(names.size() - 1)[0]);
            assertEquals("MaxResults not applied", 1, namedQuery.getResultList().size());
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }

        em = createEntityManager();
        query = em.createNativeQuery("SELECT EMP_ID FROM CMP3_EMPLOYEE");
        query.setMaxResults(1);
        factory.addNamedQuery("Select_Employee_NATIVE", query);
        closeEntityManager(em);
        em = createEntityManager();

        namedQuery = em.createNamedQuery("Select_Employee_NATIVE");
        assertEquals("MaxResults not retained", 1, namedQuery.getMaxResults());
        query = em.createNativeQuery("SELECT EMP_ID FROM CMP3_EMPLOYEE", Employee.class);
        query.setMaxResults(1);
        factory.addNamedQuery("Select_Employee_NATIVE", query);
        closeEntityManager(em);
        em = createEntityManager();

        namedQuery = em.createNamedQuery("Select_Employee_NATIVE");
        assertEquals("MaxResults not retained", 1, namedQuery.getMaxResults());
    }

    public void testGetPersistenceUnitUtilOnCloseEMF(){
        if (!isOnServer()) {
            EntityManagerFactory emf = getEntityManagerFactory();
            try {
                closeEntityManagerFactory();
                emf.getPersistenceUnitUtil();
                fail("IllegalStateException not thrown when calling getPersistenceUnitUtil on a closed EMF.");
            } catch (IllegalStateException e) {
            } finally {
                //re-init the factory
                getEntityManagerFactory();
            }

        }
    }
}
