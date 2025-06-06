/*
 * Copyright (c) 2024, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.jpql.advanced;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumberPK;
import org.eclipse.persistence.testing.models.jpa.advanced.Vegetable;
import org.eclipse.persistence.testing.models.jpa.advanced.VegetablePK;
import org.eclipse.persistence.testing.models.jpa.advanced.VegetableRecord;
import org.eclipse.persistence.testing.models.jpa.advanced.VegetablePKRecord;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitDomainObjectComparer;

/**
 * <p>
 * <b>Purpose</b>: Test additional JPQL functions.
 * <p>
 * <b>Description</b>: This class creates a test suite, initializes the database
 * and adds tests to the suite.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for additional JPQL functions
 * </ul>
 * @see EmployeePopulator
 * @see JUnitDomainObjectComparer
 */
public class JUnitJPQLFunctionsTest extends JUnitTestCase {

    static JUnitDomainObjectComparer comparer; //the global comparer object used in all tests

    //create a new EmployeePopulator
    EmployeePopulator employeePopulator = new EmployeePopulator(supportsStoredProcedures());

    private final String VEGETABLE_NAME = "Carrot";
    private final String VEGETABLE_COLOR = "Orange";
    private final VegetablePK VEGETABLE_ID = new VegetablePK(VEGETABLE_NAME, VEGETABLE_COLOR);
    private final VegetablePKRecord VEGETABLE_RECORD_ID = new VegetablePKRecord(VEGETABLE_NAME, VEGETABLE_COLOR);
    private final double VEGETABLE_COST = 99999.99;

    public JUnitJPQLFunctionsTest() {
        super();
    }

    public JUnitJPQLFunctionsTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "advanced";
    }

    //This method is run at the end of EVERY test case method

   @Override
    public void tearDown() {
        clearCache();
    }

    //This suite contains all tests contained in this class

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLFunctionsTest");
        suite.addTest(new JUnitJPQLFunctionsTest("testSetup"));

        suite.addTest(new JUnitJPQLFunctionsTest("queryID01Test"));
        suite.addTest(new JUnitJPQLFunctionsTest("queryID02Test"));
        suite.addTest(new JUnitJPQLFunctionsTest("queryID03WHERETest"));
        suite.addTest(new JUnitJPQLFunctionsTest("queryID04Test"));
        suite.addTest(new JUnitJPQLFunctionsTest("queryID05CompositePKTest"));
        suite.addTest(new JUnitJPQLFunctionsTest("queryID06CompositePKTest"));
        suite.addTest(new JUnitJPQLFunctionsTest("queryID07CompositePKTestWithIdClass"));
        suite.addTest(new JUnitJPQLFunctionsTest("queryID08CompositePKTestWithIdClass"));
        suite.addTest(new JUnitJPQLFunctionsTest("queryID09CompositePKTestWithIdClass"));
        suite.addTest(new JUnitJPQLFunctionsTest("queryID10CompositePKTestWithIdClassRecord"));
        suite.addTest(new JUnitJPQLFunctionsTest("queryVERSION1Test"));
        suite.addTest(new JUnitJPQLFunctionsTest("queryVERSION2Test"));
        suite.addTest(new JUnitJPQLFunctionsTest("queryVERSION3Test"));
        suite.addTest(new JUnitJPQLFunctionsTest("queryVERSION4Test"));
        suite.addTest(new JUnitJPQLFunctionsTest("queryVERSION5Test"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        clearCache();
        //get session to start setup
        DatabaseSession session = getPersistenceUnitServerSession();

        new AdvancedTableCreator().replaceTables(session);

        //initialize the global comparer object
        comparer = new JUnitDomainObjectComparer();

        //set the session for the comparer to use
        comparer.setSession((AbstractSession)session.getActiveSession());

        //Populate the tables
        employeePopulator.buildExamples();

        //Persist the examples in the database
        employeePopulator.persistExample(session);

        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            Vegetable vegetable = new Vegetable();
            vegetable.setId(VEGETABLE_ID);
            vegetable.setCost(VEGETABLE_COST);
            em.persist(vegetable);
            VegetableRecord vegetableRecord = new VegetableRecord();
            vegetableRecord.setName(VEGETABLE_RECORD_ID.name());
            vegetableRecord.setColor(VEGETABLE_RECORD_ID.color());
            vegetableRecord.setCost(VEGETABLE_COST);
            em.persist(vegetableRecord);
            commitTransaction(em);

        } catch (Exception e) {
            fail("An exception was caught: [" + e.getMessage() + "]");
        } finally {
            if (em.getTransaction().isActive()) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void queryID01Test(){
        final Employee EMPLOYEE_EXPECTED = employeePopulator.employeeExample1();

        EntityManager em = createEntityManager();
        Query query = em.createQuery("SELECT ID(e) FROM Employee e WHERE e.id = :idParam");
        query.setParameter("idParam", EMPLOYEE_EXPECTED.getId());
        Integer result  = (Integer)query.getSingleResult();
        assertNotNull(result);
        assertEquals(EMPLOYEE_EXPECTED.getId(), result);
    }

    public void queryID02Test(){
        final Employee EMPLOYEE_EXPECTED = employeePopulator.employeeExample1();

        EntityManager em = createEntityManager();
        Query query = em.createQuery("SELECT e.id, e.lastName, ID(e) FROM Employee e WHERE e.id = :idParam");
        query.setParameter("idParam", EMPLOYEE_EXPECTED.getId());
        Object[] result  = (Object[])query.getSingleResult();
        assertNotNull(result);
        assertEquals(EMPLOYEE_EXPECTED.getId(), result[0]);
        assertEquals(EMPLOYEE_EXPECTED.getLastName(), result[1]);
        assertEquals(EMPLOYEE_EXPECTED.getId(), result[2]);
    }

    public void queryID03WHERETest(){
        final Employee EMPLOYEE_EXPECTED = employeePopulator.employeeExample1();

        EntityManager em = createEntityManager();
        Query query = em.createQuery("SELECT ID(e) FROM Employee e WHERE ID(e) = :idParam");
        query.setParameter("idParam", EMPLOYEE_EXPECTED.getId());
        Integer result  = (Integer)query.getSingleResult();
        assertNotNull(result);
        assertEquals(EMPLOYEE_EXPECTED.getId(), result);
    }

    public void queryID04Test(){
        final Employee EMPLOYEE_EXPECTED = employeePopulator.employeeExample1();

        EntityManager em = createEntityManager();
        Query query = em.createQuery("SELECT e.id, e.lastName, ID(e) FROM Employee e WHERE ID(e) = :idParam");
        query.setParameter("idParam", EMPLOYEE_EXPECTED.getId());
        Object[] result  = (Object[])query.getSingleResult();
        assertNotNull(result);
        assertEquals(EMPLOYEE_EXPECTED.getId(), result[0]);
        assertEquals(EMPLOYEE_EXPECTED.getLastName(), result[1]);
        assertEquals(EMPLOYEE_EXPECTED.getId(), result[2]);
    }

    public void queryID05CompositePKTest(){
        EntityManager em = createEntityManager();
        Query query = em.createQuery("SELECT ID(v) FROM Vegetable v WHERE v.id = :idParam");
        query.setParameter("idParam", VEGETABLE_ID);
        VegetablePK result  = (VegetablePK) query.getSingleResult();
        assertNotNull(result);
        assertEquals(VEGETABLE_ID, result);
    }

    public void queryID06CompositePKTest(){
        EntityManager em = createEntityManager();
        Query query = em.createQuery("SELECT v.id, v.cost, ID(v) FROM Vegetable v WHERE v.id = :idParam");
        query.setParameter("idParam", VEGETABLE_ID);
        Object[] result  = (Object[])query.getSingleResult();
        assertNotNull(result);
        assertEquals(VEGETABLE_ID, result[0]);
        assertEquals(VEGETABLE_COST, result[1]);
        assertEquals(VEGETABLE_ID, result[2]);
    }

    public void queryID07CompositePKTestWithIdClass(){
        final PhoneNumber PHONE_EXPECTED = employeePopulator.employeeExample1().getPhoneNumbers().stream().findFirst().get();

        EntityManager em = createEntityManager();
        Query query = em.createQuery("SELECT ID(p) FROM PhoneNumber p WHERE p.id = :idParam AND p.type = :typeParam");
        query.setParameter("idParam", PHONE_EXPECTED.getOwner().getId());
        query.setParameter("typeParam", PHONE_EXPECTED.getType());
        Object[] result  = (Object[])query.getSingleResult();
        assertNotNull(result);
        //result array order is important too
        assertEquals(PHONE_EXPECTED.getOwner().getId(), result[0]);
        assertEquals(PHONE_EXPECTED.getType(), result[1]);
    }

    public void queryID08CompositePKTestWithIdClass(){
        final PhoneNumber PHONE_EXPECTED = employeePopulator.employeeExample1().getPhoneNumbers().stream().findFirst().get();

        EntityManager em = createEntityManager();
        Query query = em.createQuery("SELECT ID(this) FROM PhoneNumber WHERE this.id = :idParam AND this.type = :typeParam");
        query.setParameter("idParam", PHONE_EXPECTED.getOwner().getId());
        query.setParameter("typeParam", PHONE_EXPECTED.getType());
        Object[] result  = (Object[])query.getSingleResult();
        assertNotNull(result);
        //result array order is important too
        assertEquals(PHONE_EXPECTED.getOwner().getId(), result[0]);
        assertEquals(PHONE_EXPECTED.getType(), result[1]);
    }

    public void queryID09CompositePKTestWithIdClass(){
        final PhoneNumber PHONE_EXPECTED = employeePopulator.employeeExample1().getPhoneNumbers().stream().findFirst().get();
        EntityManager em = createEntityManager();
        TypedQuery<PhoneNumberPK> query = em.createQuery(
                "SELECT ID(this) FROM PhoneNumber WHERE this.id = :idParam AND this.type = :typeParam AND this.areaCode = :areaCode",
                PhoneNumberPK.class);
        query.setParameter("idParam", PHONE_EXPECTED.getOwner().getId());
        query.setParameter("typeParam", PHONE_EXPECTED.getType());
        query.setParameter("areaCode", PHONE_EXPECTED.getAreaCode());
        PhoneNumberPK result = query.getSingleResult();
        assertNotNull(result);
        assertEquals(PHONE_EXPECTED.getOwner().getId(), result.getId());
        assertEquals(PHONE_EXPECTED.getType(), result.getType());
    }

    public void queryID10CompositePKTestWithIdClassRecord(){
        EntityManager em = createEntityManager();
        TypedQuery<VegetablePKRecord> query = em.createQuery(
                "SELECT ID(this) FROM VegetableRecord WHERE this.name = :nameParam AND this.color = :colorParam", VegetablePKRecord.class);
        query.setParameter("nameParam", VEGETABLE_NAME);
        query.setParameter("colorParam", VEGETABLE_COLOR);
        VegetablePKRecord result = query.getSingleResult();
        assertNotNull(result);
        assertEquals(VEGETABLE_RECORD_ID, result);
    }

    public void queryVERSION1Test(){
        final Employee EMPLOYEE_EXPECTED = employeePopulator.employeeExample1();

        EntityManager em = createEntityManager();
        Query query = em.createQuery("SELECT VERSION(e) FROM Employee e WHERE ID(e) = :idParam");
        query.setParameter("idParam", EMPLOYEE_EXPECTED.getId());
        Integer result  = (Integer)query.getSingleResult();
        assertNotNull(result);
        assertEquals(EMPLOYEE_EXPECTED.getVersion(), result);
    }

    public void queryVERSION2Test(){
        final Employee EMPLOYEE_EXPECTED = employeePopulator.employeeExample1();

        EntityManager em = createEntityManager();
        Query query = em.createQuery("SELECT e.id, e.lastName, VERSION(e) FROM Employee e WHERE ID(e) = :idParam");
        query.setParameter("idParam", EMPLOYEE_EXPECTED.getId());
        Object[] result  = (Object[])query.getSingleResult();
        assertNotNull(result);
        assertEquals(EMPLOYEE_EXPECTED.getId(), result[0]);
        assertEquals(EMPLOYEE_EXPECTED.getLastName(), result[1]);
        assertEquals(EMPLOYEE_EXPECTED.getVersion(), result[2]);
    }

    public void queryVERSION3Test(){
        final Employee EMPLOYEE_EXPECTED = employeePopulator.employeeExample1();

        EntityManager em = createEntityManager();
        Query query = em.createQuery("SELECT e.id FROM Employee e WHERE VERSION(e) = :versionParam AND ID(e) = :idParam");
        query.setParameter("idParam", EMPLOYEE_EXPECTED.getId());
        query.setParameter("versionParam", EMPLOYEE_EXPECTED.getVersion());
        Integer result  = (Integer)query.getSingleResult();
        assertNotNull(result);
        assertEquals(EMPLOYEE_EXPECTED.getId(), result);
    }

    public void queryVERSION4Test(){
        final Employee EMPLOYEE_EXPECTED = employeePopulator.employeeExample1();

        EntityManager em = createEntityManager();
        Query query = em.createQuery("SELECT VERSION(e) FROM Employee e WHERE VERSION(e) = :versionParam AND ID(e) = :idParam");
        query.setParameter("idParam", EMPLOYEE_EXPECTED.getId());
        query.setParameter("versionParam", EMPLOYEE_EXPECTED.getVersion());
        Integer result  = (Integer)query.getSingleResult();
        assertNotNull(result);
        assertEquals(EMPLOYEE_EXPECTED.getVersion(), result);
    }

    public void queryVERSION5Test(){
        final Employee EMPLOYEE_EXPECTED = employeePopulator.employeeExample1();

        EntityManager em = createEntityManager();
        Query query = em.createQuery("SELECT e.id, e.lastName, VERSION(e) FROM Employee e WHERE VERSION(e) = :versionParam AND ID(e) = :idParam");
        query.setParameter("idParam", EMPLOYEE_EXPECTED.getId());
        query.setParameter("versionParam", EMPLOYEE_EXPECTED.getVersion());
        Object[] result  = (Object[])query.getSingleResult();
        assertNotNull(result);
        assertEquals(EMPLOYEE_EXPECTED.getId(), result[0]);
        assertEquals(EMPLOYEE_EXPECTED.getLastName(), result[1]);
        assertEquals(EMPLOYEE_EXPECTED.getVersion(), result[2]);
    }
}
