/*
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     09/21/2012-2.5 Chris Delahunt
//       - 367452: JPA 2.1 Specification support for joins with ON clause
//     09/26/2012-2.5 Chris Delahunt
//       - 350469: JPA 2.1 Criteria Query framework Bulk Update/Delete support
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaBuilder.Case;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa21.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa21.advanced.PhoneNumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class CriteriaQueryTest extends JUnitTestCase {
    protected boolean m_reset = false;

    public CriteriaQueryTest() {}

    public CriteriaQueryTest(String name) {
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
        suite.setName("CriteriaQueryTest");

        suite.addTest(new CriteriaQueryTest("testSetup"));

        suite.addTest(new CriteriaQueryTest("testOnClause"));
        suite.addTest(new CriteriaQueryTest("testOnClauseOverCollection"));
        suite.addTest(new CriteriaQueryTest("testOnClauseWithLeftJoin"));
        suite.addTest(new CriteriaQueryTest("testOnClauseCompareSQL"));
        suite.addTest(new CriteriaQueryTest("simpleCriteriaUpdateTest"));
        suite.addTest(new CriteriaQueryTest("testCriteriaUpdate"));
        suite.addTest(new CriteriaQueryTest("testComplexConditionCaseInCriteriaUpdate"));
        suite.addTest(new CriteriaQueryTest("testCriteriaUpdateEmbeddedField"));
        suite.addTest(new CriteriaQueryTest("testCriteriaUpdateCompareSQL"));
        suite.addTest(new CriteriaQueryTest("testReusingCriteriaUpdate"));
        suite.addTest(new CriteriaQueryTest("simpleCriteriaDeleteTest"));
        suite.addTest(new CriteriaQueryTest("testCriteriaDelete"));
        suite.addTest(new CriteriaQueryTest("testCriteriaDeleteCompareSQL"));
        suite.addTest(new CriteriaQueryTest("testEqualsClauseSingleExpressionEmptyExpressionsList"));
        suite.addTest(new CriteriaQueryTest("testEqualsClauseExpressionConjunctionNonEmptyExpressionsList"));
        suite.addTest(new CriteriaQueryTest("testInClauseSingleExpressionEmptyExpressionsList"));
        //Downcast/treat support - needs to be moved so it can use the JPA 2.0 models
        //suite.addTest(CriteriaQueryCastTest.suite());

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

    // Bug 367452
    // Test join on clause
    public void testOnClause() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e join e.address a on a.city = 'Ottawa'");
        List<?> baseResult = query.getResultList();

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        Join<Object, Object> address = root.join("address");
        address.on(qb.equal(address.get("city"), "Ottawa"));
        List<Employee> testResult = em.createQuery(cq).getResultList();

        clearCache();
        closeEntityManager(em);

        if (baseResult.size() != testResult.size()) {
            fail("Criteria query using ON clause did not match JPQL results; "
                    +baseResult.size()+" were expected, while criteria query returned "+testResult.size());
        }
    }

    public void testOnClauseOverCollection() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e join e.phoneNumbers p on p.areaCode = '613'");
        List<?> baseResult = query.getResultList();

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        Join<Object, Object> phoneNumber = root.join("phoneNumbers");
        phoneNumber.on(qb.equal(phoneNumber.get("areaCode"), "613"));
        List<Employee> testResult = em.createQuery(cq).getResultList();

        clearCache();
        closeEntityManager(em);

        if (baseResult.size() != testResult.size()) {
            fail("Criteria query using ON clause did not match JPQL results; "
                    +baseResult.size()+" were expected, while criteria query returned "+testResult.size());
        }
    }

    public void testOnClauseWithLeftJoin() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e left join e.address a on a.city = 'Ottawa' " +
                "where a.postalCode is not null");
        List<?> baseResult = query.getResultList();

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee>cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        Join<Object, Object> address = root.join("address", JoinType.LEFT);
        address.on(qb.equal(address.get("city"), "Ottawa"));
        cq.where(qb.isNotNull(address.get("postalCode")));
        List<Employee> testResult = em.createQuery(cq).getResultList();

        clearCache();
        closeEntityManager(em);

        if (baseResult.size() != testResult.size()) {
            fail("Criteria query using ON clause with a left join did not match JPQL results; "
                    +baseResult.size()+" were expected, while criteria query returned "+testResult.size());
        }
    }

    /**
     * This test verifies that the SQL generated for a criteria query with joins and an on clause matches
     * the SQL generated for the equivalent JPQL query, to ensure that excess table joins are not occurring that do
     * not affect the results.
     */
    public void testOnClauseCompareSQL() {
        EntityManager em = createEntityManager();
        JpaEntityManager jpaEM = JpaHelper.getEntityManager((EntityManager)em.getDelegate());
        EJBQueryImpl<?> query = (EJBQueryImpl<?>) jpaEM.createQuery("Select e from Employee e left join e.manager m left join m.address a on a.city = 'Ottawa' " +
                "where a.postalCode is not null");
        String baseSQL = query.getDatabaseQuery().getTranslatedSQLString(this.getDatabaseSession(),
                new org.eclipse.persistence.sessions.DatabaseRecord());

        CriteriaBuilder qb = jpaEM.getCriteriaBuilder();
        CriteriaQuery<Employee>cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        Join<Object, Object> address = root.join("manager", JoinType.LEFT).join("address", JoinType.LEFT);
        address.on(qb.equal(address.get("city"), "Ottawa"));
        cq.where(qb.isNotNull(address.get("postalCode")));
        EJBQueryImpl<?> testQuery = (EJBQueryImpl<?>) jpaEM.createQuery(cq);

        String testSQL = testQuery.getDatabaseQuery().getTranslatedSQLString(this.getDatabaseSession(),
                new org.eclipse.persistence.sessions.DatabaseRecord());

        closeEntityManager(em);

        if (!testSQL.equals(baseSQL)) {
            fail("Criteria query using ON clause did not match SQL used for a JPQL query; generated SQL was: \""
                    +testSQL + "\"  but we expected: \""+baseSQL+"\"");
        }
    }

    /////UPDATE Criteria tests:
    public void simpleCriteriaUpdateTest()
    {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test simpleUpdate skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        int nrOfEmps = ((Number)em.createQuery("SELECT COUNT(e) FROM Employee e ").getSingleResult()).intValue();

        // test query "UPDATE Employee e SET e.firstName = 'CHANGED'";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaUpdate<Employee>cq = qb.createCriteriaUpdate(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        cq.set(root.get("firstName"), "CHANGED");

        beginTransaction(em);
        try {
            Query q = em.createQuery(cq);
            int updated = q.executeUpdate();
            assertEquals("simpleCriteriaUpdateTest: wrong number of updated instances",
                    nrOfEmps, updated);
            //commitTransaction(em);

            // check database changes
            int nr = ((Number)em.createQuery("SELECT COUNT(e) FROM Employee e WHERE e.firstName = 'CHANGED'").getSingleResult()).intValue();
            assertEquals("simpleCriteriaUpdateTest: unexpected number of changed values in the database",
                    nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
    public void testCriteriaUpdate() {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test simpleUpdate skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        int nrOfEmps = ((Number)em.createQuery("SELECT COUNT(e) FROM Employee e where e.firstName is not null").getSingleResult()).intValue();

        // test query "UPDATE Employee e SET e.firstName = 'CHANGED'";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaUpdate<Employee>cq = qb.createCriteriaUpdate(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        cq.set(root.get("firstName"), "CHANGED");
        cq.where(qb.isNotNull(root.get("firstName")));

        beginTransaction(em);
        try {
            Query q = em.createQuery(cq);
            int updated = q.executeUpdate();
            assertEquals("simpleCriteriaUpdateTest: wrong number of updated instances",
                    nrOfEmps, updated);

            // check database changes
            int nr = ((Number)em.createQuery("SELECT COUNT(e) FROM Employee e WHERE e.firstName = 'CHANGED'").getSingleResult()).intValue();
            assertEquals("simpleCriteriaUpdateTest: unexpected number of changed values in the database",
                    nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    //test ejbqlString = "Update Employee e set e.lastName = case when e.firstName = 'Bob' then 'Jones' when e.firstName = 'Jill' then 'Jones' else '' end";
    @SuppressWarnings({"unchecked"})
    public void testComplexConditionCaseInCriteriaUpdate(){
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test complexConditionCaseInUpdateTest skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        List<Employee> results = null;

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaUpdate<Employee> cq = qb.createCriteriaUpdate(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        Case<String> caseExp = qb.selectCase();
        caseExp.when(qb.equal(root.<String>get("firstName"),  "Bob"), "Jones");
        caseExp.when(qb.equal(root.<String>get("firstName"),  "Jill"), "Jones");
        caseExp.otherwise("");
        cq.set(root.<String>get("lastName"), caseExp);

        beginTransaction(em);
        try {
            clearCache();

            em.createQuery(cq).executeUpdate();

            String verificationString = "select e from Employee e where e.lastName = 'Jones'";
            results = em.createQuery(verificationString).getResultList();
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
        assertEquals("complexConditionCaseInUpdateTest - wrong number of results", 2, results.size());
        for (Employee e : results) {
            assertEquals("complexConditionCaseInUpdateTest wrong last name for - " + e.getFirstName(), "Jones", e.getLastName());
        }

    }

    public void testCriteriaUpdateEmbeddedField() {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test updateEmbeddedFieldTest skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }

        EntityManager em = createEntityManager();
        int nrOfEmps = ((Number)em.createQuery("SELECT COUNT(e) FROM Employee e where e.firstName is not null").getSingleResult()).intValue();

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(1905, 11, 31, 0, 0, 0);
        java.sql.Date startDate = new java.sql.Date(startCalendar.getTime().getTime());

        //em.createQuery("UPDATE Employee e SET e.period.startDate= :startDate").setParameter("startDate", startDate).executeUpdate();
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaUpdate<Employee> cq = qb.createCriteriaUpdate(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        cq.set(root.get("period").get("startDate"), startDate);

        beginTransaction(em);
        try {
            clearCache();

            int updated = em.createQuery(cq).executeUpdate();
            assertEquals("testCriteriaUpdateEmbeddedField: wrong number of updated instances",
                    nrOfEmps, updated);

            // check database changes
            int nr = ((Number)em.createQuery("SELECT COUNT(e) FROM Employee e WHERE e.period.startDate = :startDate")
                    .setParameter("startDate", startDate).getSingleResult()).intValue();
            assertEquals("testCriteriaUpdateEmbeddedField: unexpected number of changed values in the database",
                    nrOfEmps, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testCriteriaUpdateCompareSQL() {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test simpleUpdate skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        JpaEntityManager jpaEM = JpaHelper.getEntityManager((EntityManager)em.getDelegate());
        EJBQueryImpl<?> query = (EJBQueryImpl<?>) jpaEM.createQuery("UPDATE Employee e SET e.firstName = 'CHANGED' where e.firstName is not null");
        String baseSQL = query.getDatabaseQuery().getTranslatedSQLString(this.getDatabaseSession(),
                new org.eclipse.persistence.sessions.DatabaseRecord());
        @SuppressWarnings({"unchecked"})
        List<String> baseSQLStrings = query.getDatabaseQuery().getTranslatedSQLStrings(this.getDatabaseSession(),
                    new org.eclipse.persistence.sessions.DatabaseRecord());

        // test query "UPDATE Employee e SET e.firstName = 'CHANGED'";
        CriteriaBuilder qb = jpaEM.getCriteriaBuilder();
        CriteriaUpdate<Employee>cq = qb.createCriteriaUpdate(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        cq.set(root.get("firstName"), "CHANGED");
        cq.where(qb.isNotNull(root.get("firstName")));
        EJBQueryImpl<?> testQuery = (EJBQueryImpl<?>)jpaEM.createQuery(cq);

        String testSQL = testQuery.getDatabaseQuery().getTranslatedSQLString(this.getDatabaseSession(),
                new org.eclipse.persistence.sessions.DatabaseRecord());
        @SuppressWarnings({"unchecked"})
        List<String> testSQLStrings = testQuery.getDatabaseQuery().getTranslatedSQLStrings(this.getDatabaseSession(),
                new org.eclipse.persistence.sessions.DatabaseRecord());

        closeEntityManager(em);
        if (testSQL != null) {
            assertEquals("UPDATE Criteria query did not match SQL used for a JPQL query; generated SQL was: \""
                        +testSQL + "\"  but we expected: \""+baseSQL+"\"", testSQL, baseSQL);
        } else {
            //check list of strings instead
            boolean pass = true;
            if (testSQLStrings == null || baseSQLStrings == null || testSQLStrings.size() != baseSQLStrings.size()) {
                pass = false;
            } else {
                List<String> clonedBaseStrings = new ArrayList<>(baseSQLStrings);
                for (String testSQLString: testSQLStrings) {
                    if (clonedBaseStrings.contains(testSQLString)) {
                        clonedBaseStrings.remove(testSQLString);
                    } else {
                        pass = false;
                        break;
                    }
                }
                if (!clonedBaseStrings.isEmpty()) {
                    pass = false;
                }
            }
            assertTrue("UPDATE Criteria query translated strings did not match JPQL query; criteria generated "
                    +testSQLStrings +" but JPQL generated "+baseSQLStrings, pass);
        }
    }

    //Bug 403518 - reusing CriteriaUpdate qb.createCriteriaUpdate in multiple queries causes duplicate query executions
    public void testReusingCriteriaUpdate() {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test simpleUpdate skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        int nrOfEmps = ((Number)em.createQuery("SELECT COUNT(e) FROM Employee e where e.firstName is not null").getSingleResult()).intValue();
        int nrOfEmpsWLastName = ((Number)em.createQuery("SELECT COUNT(e) FROM Employee e where e.lastName is not null").getSingleResult()).intValue();

        // test query "UPDATE Employee e SET e.firstName = 'CHANGED'";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaUpdate<Employee>cq = qb.createCriteriaUpdate(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        cq.set(root.get("firstName"), "CHANGED");
        cq.where(qb.isNotNull(root.get("firstName")));

        beginTransaction(em);
        try {
            Query q = em.createQuery(cq);
            int updated = q.executeUpdate();
            assertEquals("simpleCriteriaUpdateTest: wrong number of updated instances",
                    nrOfEmps, updated);

            // check database changes
            int nr = ((Number)em.createQuery("SELECT COUNT(e) FROM Employee e WHERE e.firstName = 'CHANGED'").getSingleResult()).intValue();
            assertEquals("simpleCriteriaUpdateTest: unexpected number of changed values in the database",
                    nrOfEmps, nr);
            cq.where(qb.isNotNull(root.get("lastName")));
            q = em.createQuery(cq);
            int updatedLastNames = q.executeUpdate();
            assertEquals("simpleCriteriaUpdateTest: wrong number of updated instances",
                    nrOfEmpsWLastName, updatedLastNames);

        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }


    /////DELETE Criteria tests:
    public void simpleCriteriaDeleteTest() {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test simpleDelete skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        int nrOfEmps = ((Number)em.createQuery("SELECT COUNT(phone) FROM PhoneNumber phone").getSingleResult()).intValue();

        // test query "Delete PhoneNumber phone";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaDelete<PhoneNumber>cq = qb.createCriteriaDelete(PhoneNumber.class);
        Root<PhoneNumber> root = cq.from(PhoneNumber.class);

        beginTransaction(em);
        try {
            Query q = em.createQuery(cq);
            int updated = q.executeUpdate();
            assertEquals("simpleCriteriaDeleteTest: wrong number of deleted instances"+updated,
                    nrOfEmps, updated);

            // check database changes
            int nr = ((Number)em.createQuery("SELECT COUNT(phone) FROM PhoneNumber phone").getSingleResult()).intValue();
            assertEquals("simpleCriteriaDeleteTest: found "+nr+" employees after delete all", 0, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testCriteriaDelete() {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test simpleDelete skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            int nrOfEmps = ((Number)em.createQuery("SELECT COUNT(phone) FROM PhoneNumber phone where phone.owner.firstName is not null")
                    .getSingleResult()).intValue();

            // test query "Delete Employee e where e.firstName is not null";
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaDelete<PhoneNumber> cq = qb.createCriteriaDelete(PhoneNumber.class);
            Root<PhoneNumber> root = cq.from(PhoneNumber.class);
            cq.where(qb.isNotNull(root.get("owner").get("firstName")));
            Query testQuery = em.createQuery(cq);

            int updated = testQuery.executeUpdate();
            assertEquals("testCriteriaDelete: wrong number of deleted instances"+updated,
                    nrOfEmps, updated);

            // check database changes
            int nr = ((Number)em.createQuery("SELECT COUNT(phone) FROM PhoneNumber phone where phone.owner.firstName is not null")
                    .getSingleResult()).intValue();
            assertEquals("testCriteriaDelete: found "+nr+" PhoneNumbers after delete all", 0, nr);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testCriteriaDeleteCompareSQL() {
        EntityManager em = createEntityManager();

        JpaEntityManager jpaEM = JpaHelper.getEntityManager((EntityManager)em.getDelegate());
        EJBQueryImpl<?> query = (EJBQueryImpl<?>)jpaEM.createQuery("DELETE FROM PhoneNumber phone where phone.owner.firstName is not null");
        String baseSQL = query.getDatabaseQuery().getTranslatedSQLString(this.getDatabaseSession(),
                new org.eclipse.persistence.sessions.DatabaseRecord());
        @SuppressWarnings({"unchecked"})
        List<String> baseSQLStrings = query.getDatabaseQuery().getTranslatedSQLStrings(this.getDatabaseSession(),
                new org.eclipse.persistence.sessions.DatabaseRecord());

        // test query "Delete Employee e where e.firstName is not null";
        CriteriaBuilder qb = jpaEM.getCriteriaBuilder();
        CriteriaDelete<PhoneNumber> cq = qb.createCriteriaDelete(PhoneNumber.class);
        Root<PhoneNumber> root = cq.from(PhoneNumber.class);
        cq.where(qb.isNotNull(root.get("owner").get("firstName")));
        @SuppressWarnings({"unchecked"})
        EJBQueryImpl<PhoneNumber> testQuery = (EJBQueryImpl<PhoneNumber>) jpaEM.createQuery(cq);

        String testSQL = testQuery.getDatabaseQuery().getTranslatedSQLString(this.getDatabaseSession(),
                new org.eclipse.persistence.sessions.DatabaseRecord());
        @SuppressWarnings({"unchecked"})
        List<String> testSQLStrings = testQuery.getDatabaseQuery().getTranslatedSQLStrings(this.getDatabaseSession(),
                new org.eclipse.persistence.sessions.DatabaseRecord());

        closeEntityManager(em);

        if (testSQL != null) {
            assertEquals("Delete Criteria query did not match SQL used for a JPQL query; generated SQL was: \""
                        +testSQL + "\"  but we expected: \""+baseSQL+"\"", testSQL, baseSQL);
        } else {
            //check list of strings instead
            boolean pass = true;
            if (testSQLStrings == null || baseSQLStrings == null || testSQLStrings.size() != baseSQLStrings.size()) {
                pass = false;
            } else {
                List<String> clonedBaseStrings = new ArrayList<>(baseSQLStrings);
                for (String testSQLString: testSQLStrings) {
                    if (clonedBaseStrings.contains(testSQLString)) {
                        clonedBaseStrings.remove(testSQLString);
                    } else {
                        pass = false;
                        break;
                    }
                }
                if (!clonedBaseStrings.isEmpty()) {
                    pass = false;
                }
            }
            assertTrue("Delete Criteria query translated strings did not match JPQL query; criteria generated "
                    +testSQLStrings +" but JPQL generated "+baseSQLStrings, pass);
        }
    }

    public void testEqualsClauseSingleExpressionEmptyExpressionsList(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Employee> query = criteriaBuilder.createQuery(Employee.class);
            Root<Employee> employee = query.from(Employee.class);
            EntityType<Employee> employeeModel = employee.getModel();
            Predicate predicate = criteriaBuilder.equal(employee.get(employeeModel.getSingularAttribute("firstName", String.class)), "Bob");

            List<Expression<Boolean>> expressions = predicate.getExpressions();
            assertTrue("An empty list should be returned", expressions.isEmpty());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testEqualsClauseExpressionConjunctionNonEmptyExpressionsList(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Employee> query = criteriaBuilder.createQuery(Employee.class);
            Root<Employee> employee = query.from(Employee.class);
            EntityType<Employee> employeeModel = employee.getModel();
            Predicate predicate1 = criteriaBuilder.equal(employee.get(employeeModel.getSingularAttribute("firstName", String.class)), "Bob");
            Predicate predicate2 = criteriaBuilder.equal(employee.get(employeeModel.getSingularAttribute("firstName", String.class)), "Bobby");
            Predicate predicate = criteriaBuilder.and(predicate1, predicate2);

            List<Expression<Boolean>> expressions = predicate.getExpressions();
            assertEquals("Two expressions should be returned", 2, expressions.size());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    public void testInClauseSingleExpressionEmptyExpressionsList(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaDelete<Employee> criteriaDelete = criteriaBuilder.createCriteriaDelete(Employee.class);
            Root<Employee> employee = criteriaDelete.from(Employee.class);
            CriteriaDelete<Employee> where = criteriaDelete.where(employee.get("firstName").in(Arrays.asList("Bob", "Bobby")));
            Predicate restriction = where.getRestriction();

            List<Expression<Boolean>> expressions = restriction.getExpressions();
            assertTrue("An empty list should be returned", expressions.isEmpty());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }
}
