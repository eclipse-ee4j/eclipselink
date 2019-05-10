/*
 * Copyright (c) 2012, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa21.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa21.advanced.EmployeePopulator;


public class EntityManagerFactoryTestSuite extends JUnitTestCase {
    protected boolean m_reset = false;

    public EntityManagerFactoryTestSuite() {}

    public EntityManagerFactoryTestSuite(String name) {
        super(name);
        setPuName("MulitPU-1");
    }

    public void setUp () {
        m_reset = true;
        super.setUp();
        clearCache();
    }

    public void tearDown () {
        if (m_reset) {
            m_reset = false;
        }

        super.tearDown();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("EntityManagerFactoryTestSuite");

        suite.addTest(new EntityManagerFactoryTestSuite("testSetup"));

        // These tests call stored procedures that return a result set.
        suite.addTest(new EntityManagerFactoryTestSuite("testAddNamedQuery"));
        suite.addTest(new EntityManagerFactoryTestSuite("testGetPersistenceUnitUtilOnCloseEMF"));

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

    public void testAddNamedQuery(){
        EntityManager em = createEntityManager();
        EntityManagerFactory factory = em.getEntityManagerFactory();
        List<Object[]> names = em.createQuery("Select e.firstName, count(e.firstName) as c from Employee e group by e.firstName order by c").getResultList();
        String name = (String) names.get(names.size()-1)[0];
        Query query = em.createQuery("Select e from Employee e where e.firstName = :p1 order by e.id");
        query.setParameter("p1", name);
        assertEquals("Unable to retrieve parameter value from query", name, query.getParameterValue("p1"));
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
            assertEquals("LockMode not retained", LockModeType.OPTIMISTIC_FORCE_INCREMENT, namedQuery.getLockMode());
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
        EntityManagerFactory emf = getEntityManagerFactory();
        emf.close();
        try{
            emf.getPersistenceUnitUtil();
            fail("IllegalStateException not thrown when calling getPersistenceUnitUtil on a closed EMF.");
        } catch (IllegalStateException e){}
    }

    @Override
    public String getPersistenceUnitName() {
       return "MulitPU-1";
    }

}
