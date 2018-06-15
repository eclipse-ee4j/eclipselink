/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.testing.tests.jpa.jpql;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;

import javax.persistence.Query;
import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.Department;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.Jigsaw;
import org.eclipse.persistence.testing.models.jpa.advanced.JigsawPiece;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.relationshipmaintenance.Dept;
/**
 * <p>
 * <b>Purpose</b>: Test EJBQL parameter functionality.
 * <p>
 * <b>Description</b>: This class creates a test suite, initializes the database
 * and adds tests to the suite.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for EJBQL parameter functionality
 * </ul>
 * @see org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator
 * @see JUnitDomainObjectComparer
 */

public class JUnitJPQLParameterTestSuite extends JUnitTestCase {

    static JUnitDomainObjectComparer comparer; //the global comparer object used in all tests

    public JUnitJPQLParameterTestSuite()
    {
        super();
    }

    public JUnitJPQLParameterTestSuite(String name)
    {
        super(name);
    }

    //This method is run at the start of EVERY test case method
    public void setUp()
    {

    }

    //This method is run at the end of EVERY test case method
    public void tearDown()
    {
        clearCache();
    }

    //This suite contains all tests contained in this class
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLParameterTestSuite");
        suite.addTest(new JUnitJPQLParameterTestSuite("testSetup"));
        suite.addTest(new JUnitJPQLParameterTestSuite("multipleParameterTest"));
        suite.addTest(new JUnitJPQLParameterTestSuite("updateEnumParameter"));
        suite.addTest(new JUnitJPQLParameterTestSuite("testQueryParametersCheckCacheTest"));
        suite.addTest(new JUnitJPQLParameterTestSuite("testQueryParametersDontCheckCacheTest"));
        suite.addTest(new JUnitJPQLParameterTestSuite("testQueryParametersReversedCheckCacheTest"));
        suite.addTest(new JUnitJPQLParameterTestSuite("testQueryParametersReversedDontCheckCacheTest"));
        if (! JUnitTestCase.isJPA10()) {
            suite.addTest(new JUnitJPQLParameterTestSuite("emptyParametersForNonParameterizedNamedQueryTest"));
        }
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        clearCache();
        //get session to start setup
        DatabaseSession session = JUnitTestCase.getServerSession();

        //create a new EmployeePopulator
        EmployeePopulator employeePopulator = new EmployeePopulator();

        new AdvancedTableCreator().replaceTables(session);

        //initialize the global comparer object
        comparer = new JUnitDomainObjectComparer();

        //set the session for the comparer to use
        comparer.setSession((AbstractSession)session.getActiveSession());

        //Populate the tables
        employeePopulator.buildExamples();

        //Persist the examples in the database
        employeePopulator.persistExample(session);
    }

    //Test case for selecting employee from the database using parameters
    public void multipleParameterTest()
    {
        EntityManager em = createEntityManager();

        Employee employee = (Employee) (getServerSession().readAllObjects(Employee.class).firstElement());
        Vector expectedResult = new Vector();
        expectedResult.add(employee);

        Query query = em.createQuery("SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName = ?1 AND emp.id = ?3");
        query.setParameter(1, employee.getFirstName());
        query.setParameter(3, employee.getId());
        List result = query.getResultList();

        assertTrue("Multiple Parameter Test Case Failed", comparer.compareObjects(result, expectedResult));
    }

    // Test for GF#1123 - UPDATE with JPQL does not handle enums correctly.
    public void updateEnumParameter() {
        if ((JUnitTestCase.getServerSession()).getPlatform().isSymfoware()) {
            getServerSession().logMessage("Test updateEnumParameter skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();

        int nrOfEmps = executeJPQLReturningInt(
            em, "SELECT COUNT(e) FROM Employee e WHERE e.period.endDate IS NULL");

        // test query
        String update = "UPDATE Employee e SET e.status = :status, e.payScale = :payScale WHERE e.period.endDate IS NULL";
        beginTransaction(em);
        try {
            Query q = em.createQuery(update);
            q.setParameter("status", Employee.EmployeeStatus.FULL_TIME);
            q.setParameter("payScale", Employee.SalaryRate.SENIOR);
            int updated = q.executeUpdate();
            assertEquals("wrong number of updated instances", nrOfEmps, updated);

            // check database changes
            Query q2 = em.createQuery(
                "SELECT COUNT(e) FROM Employee e WHERE e.period.endDate IS NULL AND e.status = :status AND e.payScale = :payScale");
            q2.setParameter("status", Employee.EmployeeStatus.FULL_TIME);
            q2.setParameter("payScale", Employee.SalaryRate.SENIOR);
            int nr = ((Number)q2.getSingleResult()).intValue();
            assertEquals("unexpected number of changed values in the database", nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }

    /** Helper method executing a JPQL query retuning an int value. */
    private int executeJPQLReturningInt(EntityManager em, String jpql)
    {
        Query q = em.createQuery(jpql);
        Object result = q.getSingleResult();
        return ((Number)result).intValue();
    }

    // Bug 344492
    public void emptyParametersForNonParameterizedNamedQueryTest() {
        EntityManager em = createEntityManager();
        assertNotNull(em);
        Query query = em.createNamedQuery("findAllEmployeesOrderById");
        assertNotNull(query);
        Set parameters = query.getParameters();
        assertNotNull(parameters);
        assertEquals("Parameters size should be 0", 0, parameters.size());
    }

    public void testQueryParametersCheckCacheTest() {
        testQueryParametersWithQueryAndCheckCache("SELECT p FROM JigsawPiece p, Jigsaw j WHERE j.id=:jigsawId AND p.id=:jigsawPieceId", true);
    }

    public void testQueryParametersDontCheckCacheTest() {
        testQueryParametersWithQueryAndCheckCache("SELECT p FROM JigsawPiece p, Jigsaw j WHERE j.id=:jigsawId AND p.id=:jigsawPieceId", false);
    }

    public void testQueryParametersReversedCheckCacheTest() {
        testQueryParametersWithQueryAndCheckCache("SELECT p FROM JigsawPiece p, Jigsaw j WHERE p.id=:jigsawPieceId AND j.id=:jigsawId", true);
    }

    public void testQueryParametersReversedDontCheckCacheTest() {
        testQueryParametersWithQueryAndCheckCache("SELECT p FROM JigsawPiece p, Jigsaw j WHERE p.id=:jigsawPieceId AND j.id=:jigsawId", false);
    }

    // EL bug 430042 - pk query parameter binding incorrect
    private void testQueryParametersWithQueryAndCheckCache(String jpqlQueryString, boolean checkCacheOnly) {
        EntityManager em = createEntityManager();
        Jigsaw jigsawTestData = null;
        JigsawPiece jigsawPieceTestData = null;

        try {
            beginTransaction(em);

            jigsawTestData = new Jigsaw();
            jigsawPieceTestData = new JigsawPiece();
            jigsawTestData.addPiece(jigsawPieceTestData);
            em.persist(jigsawTestData);

            commitTransaction(em);
            closeEntityManager(em);

            em = createEntityManager();

            Query query = em.createQuery(jpqlQueryString);
            query.setParameter("jigsawId", jigsawTestData.getId());
            query.setParameter("jigsawPieceId", jigsawPieceTestData.getId());

            if (checkCacheOnly) {
                query.setHint(QueryHints.CACHE_USAGE, CacheUsage.CheckCacheOnly);
            }

            JigsawPiece resultPiece = (JigsawPiece) query.getSingleResult();
            assertNotNull("Query Entity should not be null", resultPiece);
            Jigsaw resultJigsaw = resultPiece.getJigsaw();
            assertNotNull("Queried Entity's parent reference should not be null", resultJigsaw);
            assertEquals("Queried Entity should have the same id", jigsawPieceTestData.getId(), resultPiece.getId());
            assertEquals("Queried Entity should reference the same parent Entity", jigsawTestData.getId(), resultJigsaw.getId());
        } finally {
            if (jigsawTestData != null) {
                beginTransaction(em);
                Jigsaw jigsawToRemove = em.find(Jigsaw.class, jigsawTestData.getId());
                em.remove(jigsawToRemove);
                for (JigsawPiece piece : jigsawToRemove.getPieces()) {
                    em.remove(piece);
                }
                commitTransaction(em);
            }
            closeEntityManager(em);
        }
    }

}
