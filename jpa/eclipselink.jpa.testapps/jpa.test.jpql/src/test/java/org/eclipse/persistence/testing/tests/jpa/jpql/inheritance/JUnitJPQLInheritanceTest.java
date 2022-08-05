/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.jpql.inheritance;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inheritance.AAA;
import org.eclipse.persistence.testing.models.jpa.inheritance.Engineer;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritancePopulator;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritanceTableCreator;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitDomainObjectComparer;
import org.junit.Assert;

import java.util.List;

/**
 * <p>
 * <b>Purpose</b>: Test inheritance EJBQL functionality.
 * <p>
 * <b>Description</b>: This class creates a test suite, initializes the database
 * and adds tests to the suite.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for inheritance EJBQL functionality
 * </ul>
 * @see JUnitDomainObjectComparer
 */
public class JUnitJPQLInheritanceTest extends JUnitTestCase {
    static JUnitDomainObjectComparer comparer;        //the global comparer object used in all tests

    public JUnitJPQLInheritanceTest() {
        super();
    }

    public JUnitJPQLInheritanceTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "inheritance";
    }

    //This method is run at the end of EVERY test case method
    @Override
    public void tearDown() {
        clearCache();
    }

    //This suite contains all tests contained in this class
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLInheritanceTest");
        suite.addTest(new JUnitJPQLInheritanceTest("testSetup"));
        suite.addTest(new JUnitJPQLInheritanceTest("testJoinSubClass"));
        suite.addTest(new JUnitJPQLInheritanceTest("testJoinFetchSubClass"));
        suite.addTest(new JUnitJPQLInheritanceTest("testJoinedInheritance"));
        suite.addTest(new JUnitJPQLInheritanceTest("testJoinedInheritanceWithLeftOuterJoin1"));
        suite.addTest(new JUnitJPQLInheritanceTest("testJoinedInheritanceWithLeftOuterJoin2"));
        suite.addTest(new JUnitJPQLInheritanceTest("testJoinedInheritanceWithLeftOuterJoin3"));
        suite.addTest(new JUnitJPQLInheritanceTest("testComputer"));
        suite.addTest(new JUnitJPQLInheritanceTest("testAllPeople"));
        suite.addTest(new JUnitJPQLInheritanceTest("testConverter"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        clearCache();
        //get session to start setup
        DatabaseSession session = getPersistenceUnitServerSession();

        //initialize the global comparer object
        comparer = new JUnitDomainObjectComparer();

        //set the session for the comparer to use
        comparer.setSession((AbstractSession)session.getActiveSession());

        new InheritanceTableCreator().replaceTables(session);

        //Populate the tables
        InheritancePopulator inheritancePopulator = new InheritancePopulator();
        inheritancePopulator.buildExamples();

        //Persist the examples in the database
        inheritancePopulator.persistExample(session);
    }

    public void testJoinSubClass() {
        EntityManager em = createEntityManager();

        Engineer emp = (Engineer)em.createQuery("SELECT e from Engineer e JOIN e.bestFriend b WHERE e.title is not null").getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(Engineer.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(emp.getId()));
        tlQuery.addJoinedAttribute(tlQuery.getExpressionBuilder().get("bestFriend"));

        Engineer tlEmp = (Engineer)getPersistenceUnitServerSession().executeQuery(tlQuery);
        Assert.assertTrue("Join Subclass Inheritance Test Failed", comparer.compareObjects(emp, tlEmp));
    }

    public void testJoinFetchSubClass() {
        EntityManager em = createEntityManager();

        Engineer emp = (Engineer)em.createQuery("SELECT e from Engineer e JOIN FETCH e.bestFriend").getResultList().get(0);
        clearCache();
        ReadObjectQuery tlQuery = new ReadObjectQuery(Engineer.class);
        tlQuery.setSelectionCriteria(tlQuery.getExpressionBuilder().get("id").equal(emp.getId()));
        tlQuery.addJoinedAttribute(tlQuery.getExpressionBuilder().get("bestFriend"));

        Engineer tlEmp = (Engineer)getPersistenceUnitServerSession().executeQuery(tlQuery);
        Assert.assertTrue("Join Subclass Inheritance Test Failed", comparer.compareObjects(emp, tlEmp));
    }

    /**
     * Checks, that the selection criteria for joined inheritance is well-formed,
     * i.e. all tables are joined.
     * See issue 860.
     */
    public void testJoinedInheritance() {
        EntityManager em = createEntityManager();

        String ejbqlString = "SELECT OBJECT(b) FROM BBB b WHERE b.foo = ?1";
        // query throws exception, if result not unique!
        em.createQuery(ejbqlString).setParameter(1, "bar").getSingleResult();
    }

    public void testJoinedInheritanceWithLeftOuterJoin1() {
        EntityManager em = createEntityManager();
        String ejbqlString = "SELECT t0.maxSpeed, t0.color, t0.description, t0.fuelCapacity, t0.fuelType, t0.id, t0.passengerCapacity, t1.name, t1.id FROM SportsCar t0 LEFT OUTER JOIN t0.owner t1";
        try {
            em.createQuery(ejbqlString).getResultList();
        } catch (Exception e) {
            fail("Error occurred on a left outer join sql expression on a joined inheritance test: " + e.getCause());
        }
    }

    public void testJoinedInheritanceWithLeftOuterJoin2() {
        EntityManager em = createEntityManager();
        String ejbqlString = "SELECT t0.color, t0.description, t0.fuelCapacity, t0.fuelType, t0.id, t0.passengerCapacity, t1.name, t1.id FROM FueledVehicle t0 LEFT OUTER JOIN t0.owner t1";
        try {
            em.createQuery(ejbqlString).getResultList();
        } catch (Exception e) {
            fail("Error occurred on a left outer join sql expression on a joined inheritance test: " + e.getCause());
        }
    }

    public void testJoinedInheritanceWithLeftOuterJoin3() {
        EntityManager em = createEntityManager();
        String ejbqlString = "SELECT t0.color, t0.description, t0.fuelCapacity, t0.fuelType, t0.id, t0.passengerCapacity, t1.name, t1.id FROM Bus t0 LEFT OUTER JOIN t0.busDriver t1";
        try {
            em.createQuery(ejbqlString).getResultList();
        } catch (Exception e) {
            fail("Error occurred on a left outer join sql expression on a joined inheritance test: " + e.getCause());
        }
    }

    public void testComputer() {
        EntityManager em = createEntityManager();
        String ejbqlString = "SELECT c FROM Computer c";
        List<?> result = em.createQuery(ejbqlString).getResultList();
        if (result.size() != 4) {
            fail("Expected 4 computers got: " + result);
        }
    }

    public void testAllPeople() {
        EntityManager em = createEntityManager();
        String ejbqlString = "SELECT p FROM Person p order by p.id";
        List<?> result = em.createQuery(ejbqlString).getResultList();
        if (result.size() != 8) {
            fail("Expected 8 people got: " + result);
        }
    }

    public void testConverter(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            AAA aaa = new AAA();
            em.persist(aaa);
            aaa = new AAA();
            em.persist(aaa);
            em.flush();
            String ejbqlString = "SELECT MAX(aaa.id) FROM AAA aaa";
            Object result = em.createQuery(ejbqlString).getSingleResult();
            Assert.assertEquals("Converter not applied", String.class, result.getClass());
        } finally{
            rollbackTransaction(em);
        }

    }
}
