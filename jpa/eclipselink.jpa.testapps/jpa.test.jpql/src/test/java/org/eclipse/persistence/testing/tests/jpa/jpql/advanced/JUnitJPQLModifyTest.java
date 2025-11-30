/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.EmploymentPeriod;
import org.eclipse.persistence.testing.models.jpa.advanced.FormerEmployment;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitDomainObjectComparer;

import java.time.LocalDate;
import java.util.Calendar;

/**
 * <p>
 * <b>Purpose</b>: Test JPQL UPDATE and DELETE queries.
 * <p>
 * <b>Description</b>: This class creates a test suite and adds tests to the
 * suite. The database gets initialized prior to each test method.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for JPQL UPDATE and DELETE queries.
 * </ul>
 * @see org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator
 * @see JUnitDomainObjectComparer
 */

public class JUnitJPQLModifyTest extends JUnitTestCase {

    static JUnitDomainObjectComparer comparer; //the global comparer object used in all tests

    public JUnitJPQLModifyTest()
    {
        super();
    }

    public JUnitJPQLModifyTest(String name)
    {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "advanced";
    }

    //This method is run at the start of EVERY test case method
    @Override
    public void setUp()
    {
        //get session to start setup
        DatabaseSession session = getPersistenceUnitServerSession();

        new AdvancedTableCreator().replaceTables(session);

        //create a new EmployeePopulator
        EmployeePopulator employeePopulator = new EmployeePopulator(supportsStoredProcedures());

        //Populate the tables
        employeePopulator.buildExamples();

        //Persist the examples in the database
        employeePopulator.persistExample(session);
    }

    //This method is run at the end of EVERY test case method
    @Override
    public void tearDown()
    {
        clearCache();
    }

    //This suite contains all tests contained in this class
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLModifyTest");
        suite.addTest(new JUnitJPQLModifyTest("testSetup"));
        suite.addTest(new JUnitJPQLModifyTest("simpleUpdate"));
        suite.addTest(new JUnitJPQLModifyTest("updateWithSubquery"));
        suite.addTest(new JUnitJPQLModifyTest("updateEmbedded"));
        suite.addTest(new JUnitJPQLModifyTest("updateEmbeddedObjectWithValue"));
        suite.addTest(new JUnitJPQLModifyTest("updateEmbeddedObjectNestedWithValue"));
        suite.addTest(new JUnitJPQLModifyTest("updateEmbeddedObjectWithNull"));
        suite.addTest(new JUnitJPQLModifyTest("updateEmbeddedFieldTest"));
        suite.addTest(new JUnitJPQLModifyTest("updateUnqualifiedAttributeInSet"));
        suite.addTest(new JUnitJPQLModifyTest("updateUnqualifiedAttributeInWhere"));
        suite.addTest(new JUnitJPQLModifyTest("updateUnqualifiedAttributeInWhereWithInputParameter"));
        suite.addTest(new JUnitJPQLModifyTest("simpleDelete"));
        suite.addTest(new JUnitJPQLModifyTest("simpleUpdateWithInputParameters"));

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

    }

    public void simpleUpdate()
    {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test simpleUpdate skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        int nrOfEmps = executeJPQLReturningInt(
            em, "SELECT COUNT(e) FROM Employee e");

        // test query
        String update = "UPDATE Employee e SET e.firstName = 'CHANGED'";
        beginTransaction(em);
        try {
            Query q = em.createQuery(update);
            int updated = q.executeUpdate();
            assertEquals("simpleUpdate: wrong number of updated instances",
                         nrOfEmps, updated);
            commitTransaction(em);

            // check database changes
            int nr = executeJPQLReturningInt(
                em, "SELECT COUNT(e) FROM Employee e WHERE e.firstName = 'CHANGED'");
            assertEquals("simpleUpdate: unexpected number of changed values in the database",
                         nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }

    public void updateWithSubquery()
    {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test updateWithSubquery skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        int nrOfEmps = executeJPQLReturningInt(
            em, "SELECT COUNT(e) FROM Employee e WHERE e.managedEmployees IS NOT EMPTY");

        // test query
        String update = "UPDATE Employee e SET e.firstName = 'CHANGED'" +
                        " WHERE (SELECT COUNT(m) FROM e.managedEmployees m) > 0";
        beginTransaction(em);
        try {
            Query q = em.createQuery(update);
            int updated = q.executeUpdate();
            assertEquals("updateWithSubquery: wrong number of updated instances",
                         nrOfEmps, updated);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }

    public void updateEmbedded()
    {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test updateEmbedded skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();

        int nrOfEmps = executeJPQLReturningInt(
            em, "SELECT COUNT(e) FROM Employee e");

        // test query
        String update = "UPDATE Employee e SET e.period.startDate = NULL";
        beginTransaction(em);
        try {
            Query q = em.createQuery(update);
            int updated = q.executeUpdate();
            assertEquals("updateEmbedded: wrong number of updated instances",
                         nrOfEmps, updated);
            commitTransaction(em);

            // check database changes
            int nr = executeJPQLReturningInt(
                em, "SELECT COUNT(e) FROM Employee e WHERE e.period.startDate IS NULL");
            assertEquals("updateEmbedded: unexpected number of changed values in the database",
                         nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }

    public void updateEmbeddedObjectWithValue()
    {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test updateEmbedded skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();

        long nrOfEmps = executeJPQLReturningInt(
                em, "SELECT COUNT(e) FROM Employee e");

        // test query
        EmploymentPeriod employmentPeriod = new EmploymentPeriod(java.sql.Date.valueOf(LocalDate.of(2020, 1, 1)), java.sql.Date.valueOf(LocalDate.of(2025, 12, 31)));
        String update = "UPDATE Employee e SET e.period = ?1";
        beginTransaction(em);
        try {
            Query updateQuery = em.createQuery(update);
            updateQuery.setParameter(1, employmentPeriod);
            int updated = updateQuery.executeUpdate();
            assertEquals("updateEmbedded: wrong number of updated instances",
                    nrOfEmps, updated);
            commitTransaction(em);

            // check database changes
            Query selectQuery = em.createQuery("SELECT COUNT(e) FROM Employee e WHERE e.period.startDate = ?1", Long.class);
            selectQuery.setParameter(1, employmentPeriod.getStartDate());
            long nr = (long) selectQuery.getSingleResult();
            assertEquals("updateEmbedded: unexpected number of changed values in the database",
                    nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }

    public void updateEmbeddedObjectNestedWithValue()
    {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test updateEmbedded skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();

        long nrOfEmps = executeJPQLReturningInt(
                em, "SELECT COUNT(e) FROM Employee e");

        // test query
        EmploymentPeriod employmentPeriod = new EmploymentPeriod(java.sql.Date.valueOf(LocalDate.of(2020, 1, 1)), java.sql.Date.valueOf(LocalDate.of(2025, 12, 31)));
        FormerEmployment formerEmployment = new FormerEmployment("Former company 2", employmentPeriod);
        String update = "UPDATE Employee e SET e.formerEmployment = ?1";
        beginTransaction(em);
        try {
            Query updateQuery = em.createQuery(update);
            updateQuery.setParameter(1, formerEmployment);
            int updated = updateQuery.executeUpdate();
            assertEquals("updateNestedEmbedded: wrong number of updated instances",
                    nrOfEmps, updated);
            commitTransaction(em);

            // check database changes
            Query selectQuery = em.createQuery("SELECT COUNT(e) FROM Employee e WHERE e.formerEmployment.formerCompany = ?1", Long.class);
            selectQuery.setParameter(1, formerEmployment.getFormerCompany());
            long nr = (long) selectQuery.getSingleResult();
            assertEquals("updateNestedEmbedded: unexpected number of changed values in the database",
                    nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }

    public void updateEmbeddedObjectWithNull()
    {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test updateEmbedded skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();

        int nrOfEmps = executeJPQLReturningInt(
                em, "SELECT COUNT(e) FROM Employee e");

        // test query
        String update = "UPDATE Employee e SET e.period = ?1";
        beginTransaction(em);
        try {
            Query q = em.createQuery(update);
            q.setParameter(1, null);
            int updated = q.executeUpdate();
            assertEquals("updateEmbedded: wrong number of updated instances",
                    nrOfEmps, updated);
            commitTransaction(em);

            // check database changes
            int nr = executeJPQLReturningInt(
                    em, "SELECT COUNT(e) FROM Employee e WHERE e.period.startDate IS NULL");
            assertEquals("updateEmbedded: unexpected number of changed values in the database",
                    nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }

    public void updateEmbeddedFieldTest()
    {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test updateEmbeddedFieldTest skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }

        EntityManager em = createEntityManager();

        int nrOfEmps = executeJPQLReturningInt(
            em, "SELECT COUNT(e) FROM Employee e");

        // test query
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(1905, 11, 31, 0, 0, 0);
        java.sql.Date startDate = new java.sql.Date(startCalendar.getTime().getTime());
        try {
            beginTransaction(em);

            em.createQuery("UPDATE Employee e SET e.period.startDate= :startDate")
            .setParameter("startDate", startDate)
            .executeUpdate();

            commitTransaction(em);
            // check database changes

            Query q = em.createQuery("SELECT COUNT(e) FROM Employee e WHERE e.period.startDate=:startDate")
            .setParameter("startDate", startDate);
            Object result = q.getSingleResult();
            int nr = ((Number)result).intValue();
            assertEquals("updateEmbedded: unexpected number of changed values in the database", nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }

    }

    public void updateUnqualifiedAttributeInSet()
    {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test updateUnqualifiedAttributeInSet skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        int nrOfEmps = executeJPQLReturningInt(
            em, "SELECT COUNT(e) FROM Employee e");

        // test query
        String update = "UPDATE Employee SET firstName = 'CHANGED'";
        beginTransaction(em);
        try {
            Query q = em.createQuery(update);
            int updated = q.executeUpdate();
            assertEquals("updateUnqualifiedAttributeInSet: wrong number of updated instances",
                         nrOfEmps, updated);
            commitTransaction(em);

            // check database changes
            int nr = executeJPQLReturningInt(
                em, "SELECT COUNT(e) FROM Employee e WHERE e.firstName = 'CHANGED'");
            assertEquals("updateUnqualifiedAttributeInSet: unexpected number of changed values in the database",
                         nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }

        // test query
        update = "UPDATE Employee SET period.startDate = NULL";
        beginTransaction(em);
        try {
            Query q = em.createQuery(update);
            int updated = q.executeUpdate();
            assertEquals("simpleUpdate: wrong number of updated instances",
                         nrOfEmps, updated);
            commitTransaction(em);

            // check database changes
            int nr = executeJPQLReturningInt(
                em, "SELECT COUNT(e) FROM Employee e WHERE e.period.startDate IS NULL");
            assertEquals("simpleUpdate: unexpected number of changed values in the database",
                         nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }

    public void updateUnqualifiedAttributeInWhere()
    {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test updateUnqualifiedAttributeInWhere skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        int nrOfEmps = executeJPQLReturningInt(
            em, "SELECT COUNT(e) FROM Employee e WHERE e.firstName = 'Bob'");

        // test query
        String update =
            "UPDATE Employee SET firstName = 'CHANGED' WHERE firstName = 'Bob'";
        beginTransaction(em);
        try {
            Query q = em.createQuery(update);
            int updated = q.executeUpdate();
            assertEquals("updateUnqualifiedAttributeInWhere: wrong number of updated instances",
                         nrOfEmps, updated);
            commitTransaction(em);

            // check database changes
            int nr = executeJPQLReturningInt(
                em, "SELECT COUNT(e) FROM Employee e WHERE e.firstName = 'CHANGED'");
            assertEquals("simpleUnqualifiedUpdate: unexpected number of changed values in the database",
                         nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }

        nrOfEmps = executeJPQLReturningInt(
            em, "SELECT COUNT(e) FROM Employee e WHERE e.managedEmployees IS NOT EMPTY");

        // test query
        update = "UPDATE Employee SET firstName = 'MODIFIED' " +
                 "WHERE (SELECT COUNT(m) FROM managedEmployees m) > 0";
        beginTransaction(em);
        try {
            Query q = em.createQuery(update);
            int updated = q.executeUpdate();
            assertEquals("simpleUpdate: wrong number of updated instances",
                         nrOfEmps, updated);
            commitTransaction(em);

            // check database changes
            int nr = executeJPQLReturningInt(
                em, "SELECT COUNT(e) FROM Employee e WHERE e.firstName = 'MODIFIED'");
            assertEquals("simpleUpdate: unexpected number of changed values in the database",
                         nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }

    // Bug 13972866: Test for a NPE found in Hermes in regards to unqualified path
    // expression in the where clause mixed with input parameters
    public void updateUnqualifiedAttributeInWhereWithInputParameter() {

        EntityManager em = createEntityManager();

        try {
           Query query = em.createQuery("update Employee set salary = :salary where version = :version");
           query.setParameter("salary",  1);
           query.setParameter("version", 2);
        }
        finally {
           if (isTransactionActive(em)){
               rollbackTransaction(em);
           }
       }
    }

    public void simpleDelete()
    {
        EntityManager em = createEntityManager();
        String jpql = "SELECT COUNT(p) FROM PhoneNumber p WHERE p.areaCode = '613'";
        int nrOfEmps = executeJPQLReturningInt(em, jpql);

        // test query
        String delete = "DELETE FROM PhoneNumber p WHERE p.areaCode = '613'";
        beginTransaction(em);
        try {
            Query q = em.createQuery(delete);
            int deleted = q.executeUpdate();
            assertEquals("simpleDelete: wrong number of deleted instances",
                         nrOfEmps, deleted);
            commitTransaction(em);

            // check database changes
            int nr = executeJPQLReturningInt(em, jpql);
            assertEquals("simpleDelete: unexpected number of instances in the database",
                         0, nr);
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

    public void simpleUpdateWithInputParameters() {
       // Bug 381302 In Symfoware, a base table name to be updated cannot be identical to table name in from clause in query or subquery specification
       if (getDatabaseSession().getPlatform().isSymfoware()) {
          warning("INTERSECT not supported on Symfoware.");
          return;
       }
        EntityManager em = createEntityManager();
       beginTransaction(em);
       try {
           String jpql = "Update Employee a SET a.payScale = :acctStatus WHERE LOCATE(:acctName, a.lastName)> 0";
          Query query = em.createQuery(jpql);
          query.setParameter("acctStatus", Employee.SalaryRate.EXECUTIVE);
          query.setParameter("acctName",   "Jones");
          int updated = query.executeUpdate();
          assertEquals("simpleUpdateWithInputParameters: did not update correclty", 2, updated);
          commitTransaction(em);
       }
       finally {
           if (isTransactionActive(em)){
               rollbackTransaction(em);
           }
       }
    }
}
