/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     09/26/2012-2.5 Chris Delahunt
//       - 350469: JPA 2.1 Criteria Query framework Bulk Update/Delete support
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaBuilder.Case;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.metamodel.EntityTypeImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa21.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa21.advanced.Address;
import org.eclipse.persistence.testing.models.jpa21.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa21.advanced.EmploymentPeriod;
import org.eclipse.persistence.testing.models.jpa21.advanced.PhoneNumber;

//see CriteriaQueryTestSuite
public class CriteriaQueryMetamodelTestSuite extends JUnitTestCase {

    protected boolean m_reset = false;

    public CriteriaQueryMetamodelTestSuite() {}

    public CriteriaQueryMetamodelTestSuite(String name) {
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
        suite.setName("CriteriaQueryMetamodelTestSuite");

        suite.addTest(new CriteriaQueryMetamodelTestSuite("testSetup"));

        suite.addTest(new CriteriaQueryMetamodelTestSuite("testMetamodelOnClause"));
        suite.addTest(new CriteriaQueryMetamodelTestSuite("testMetamodelOnClauseOverCollection"));
        suite.addTest(new CriteriaQueryMetamodelTestSuite("testMetamodelOnClauseWithLeftJoin"));
        suite.addTest(new CriteriaQueryMetamodelTestSuite("simpleMetamodelCriteriaUpdateTest"));
        suite.addTest(new CriteriaQueryMetamodelTestSuite("testMetamodelCriteriaUpdate"));
        suite.addTest(new CriteriaQueryMetamodelTestSuite("testMetamodelComplexConditionCaseInCriteriaUpdate"));
        suite.addTest(new CriteriaQueryMetamodelTestSuite("testMetamodelCriteriaUpdateEmbeddedField"));
        suite.addTest(new CriteriaQueryMetamodelTestSuite("simpleMetamodelCriteriaDeleteTest"));
        suite.addTest(new CriteriaQueryMetamodelTestSuite("testMetamodelCriteriaDelete"));

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
    public void testMetamodelOnClause() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e join e.address a on a.city = 'Ottawa'");
        List baseResult = query.getResultList();

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);

        Metamodel metamodel = em.getMetamodel();
        EntityType<Employee> entityEmp_ = metamodel.entity(Employee.class);
        EntityType<Address> entityAddr_ = metamodel.entity(Address.class);

        Root<Employee> root = cq.from(entityEmp_);
        Join address = root.join(entityEmp_.getSingularAttribute("address"));
        address.on(qb.equal(address.get(entityAddr_.getSingularAttribute("city")), "Ottawa"));
        List testResult = em.createQuery(cq).getResultList();

        clearCache();
        closeEntityManager(em);

        if (baseResult.size() != testResult.size()) {
            fail("Criteria query using ON clause did not match JPQL results; "
                    +baseResult.size()+" were expected, while criteria query returned "+testResult.size());
        }
    }

    public void testMetamodelOnClauseOverCollection() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e join e.phoneNumbers p on p.areaCode = '613'");
        List baseResult = query.getResultList();

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
        Metamodel metamodel = em.getMetamodel();
        EntityType<Employee> entityEmp_ = metamodel.entity(Employee.class);
        EntityType<PhoneNumber> entityPhone_ = metamodel.entity(PhoneNumber.class);

        Root<Employee> root = cq.from(entityEmp_);
        Join phoneNumber = root.join(entityEmp_.getCollection("phoneNumbers"));
        phoneNumber.on(qb.equal(phoneNumber.get(entityPhone_.getSingularAttribute("areaCode")), "613"));
        List testResult = em.createQuery(cq).getResultList();

        clearCache();
        closeEntityManager(em);

        if (baseResult.size() != testResult.size()) {
            fail("Criteria query using ON clause did not match JPQL results; "
                    +baseResult.size()+" were expected, while criteria query returned "+testResult.size());
        }
    }

    public void testMetamodelOnClauseWithLeftJoin() {
        EntityManager em = createEntityManager();
        Query query = em.createQuery("Select e from Employee e left join e.address a on a.city = 'Ottawa' " +
                "where a.postalCode is not null");
        List baseResult = query.getResultList();

        Metamodel metamodel = em.getMetamodel();
        EntityType<Employee> entityEmp_ = metamodel.entity(Employee.class);
        EntityType<Address> entityAddr_ = metamodel.entity(Address.class);

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Employee>cq = qb.createQuery(Employee.class);
        Root<Employee> root = cq.from(entityEmp_);
        Join address = root.join(entityEmp_.getSingularAttribute("address"), JoinType.LEFT);
        address.on(qb.equal(address.get(entityAddr_.getSingularAttribute("city")), "Ottawa"));
        cq.where(qb.isNotNull(address.get(entityAddr_.getSingularAttribute("postalCode"))));
        List testResult = em.createQuery(cq).getResultList();

        clearCache();
        closeEntityManager(em);

        if (baseResult.size() != testResult.size()) {
            fail("Criteria query using ON clause with a left join did not match JPQL results; "
                    +baseResult.size()+" were expected, while criteria query returned "+testResult.size());
        }
    }

    /////UPDATE Criteria tests:
    public void simpleMetamodelCriteriaUpdateTest()
    {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test simpleUpdate skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        int nrOfEmps = ((Number)em.createQuery("SELECT COUNT(e) FROM Employee e ").getSingleResult()).intValue();

        Metamodel metamodel = em.getMetamodel();
        EntityType<Employee> entityEmp_ = metamodel.entity(Employee.class);

        // test query "UPDATE Employee e SET e.firstName = 'CHANGED'";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaUpdate<Employee>cq = qb.createCriteriaUpdate(Employee.class);
        Root<Employee> root = cq.from(entityEmp_);
        cq.set(root.get(entityEmp_.getSingularAttribute("firstName", String.class)), "CHANGED");

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
    public void testMetamodelCriteriaUpdate() {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test simpleUpdate skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        int nrOfEmps = ((Number)em.createQuery("SELECT COUNT(e) FROM Employee e where e.firstName is not null").getSingleResult()).intValue();

        Metamodel metamodel = em.getMetamodel();
        EntityType<Employee> entityEmp_ = metamodel.entity(Employee.class);

        // test query "UPDATE Employee e SET e.firstName = 'CHANGED' where e.firstName is not null";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaUpdate<Employee>cq = qb.createCriteriaUpdate(Employee.class);
        Root<Employee> root = cq.from(entityEmp_);
        cq.set(root.get(entityEmp_.getSingularAttribute("firstName", String.class)), "CHANGED");
        cq.where(qb.isNotNull(root.get(entityEmp_.getSingularAttribute("firstName"))));

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
    public void testMetamodelComplexConditionCaseInCriteriaUpdate(){
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test complexConditionCaseInUpdateTest skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        List<Employee> results = null;

        Metamodel metamodel = em.getMetamodel();
        EntityType<Employee> entityEmp_ = metamodel.entity(Employee.class);

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaUpdate<Employee> cq = qb.createCriteriaUpdate(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
        Case caseExp = qb.selectCase();
        caseExp.when(qb.equal(root.get(entityEmp_.getSingularAttribute("firstName",String.class)),  "Bob"), "Jones");
        caseExp.when(qb.equal(root.get(entityEmp_.getSingularAttribute("firstName",String.class)),  "Jill"), "Jones");
        caseExp.otherwise("");
        cq.set(root.get(entityEmp_.getSingularAttribute("lastName")), caseExp);

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
        assertTrue("complexConditionCaseInUpdateTest - wrong number of results", results.size() == 2);
        for (Employee e : results) {
            assertTrue("complexConditionCaseInUpdateTest wrong last name for - " + e.getFirstName(), e.getLastName().equals("Jones"));
        }

    }

    public void testMetamodelCriteriaUpdateEmbeddedField() {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test updateEmbeddedFieldTest skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }

        EntityManager em = createEntityManager();
        int nrOfEmps = ((Number)em.createQuery("SELECT COUNT(e) FROM Employee e where e.firstName is not null").getSingleResult()).intValue();

        Metamodel metamodel = em.getMetamodel();
        EntityType<Employee> entityEmp_ = metamodel.entity(Employee.class);
        javax.persistence.metamodel.EmbeddableType<EmploymentPeriod> embedEmpPeriod_ =
                metamodel.embeddable(EmploymentPeriod.class);

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(1905, 11, 31, 0, 0, 0);
        java.sql.Date startDate = new java.sql.Date(startCalendar.getTime().getTime());

        //em.createQuery("UPDATE Employee e SET e.period.startDate= :startDate").setParameter("startDate", startDate).executeUpdate();
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaUpdate<Employee> cq = qb.createCriteriaUpdate(Employee.class);
        Root<Employee> root = cq.from(entityEmp_);
        cq.set(root.get(entityEmp_.getSingularAttribute("period", EmploymentPeriod.class))
                .get(embedEmpPeriod_.getSingularAttribute("startDate", java.sql.Date.class)), startDate);

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

    /////DELETE Criteria tests:
    public void simpleMetamodelCriteriaDeleteTest() {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test simpleDelete skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        int nrOfEmps = ((Number)em.createQuery("SELECT COUNT(phone) FROM PhoneNumber phone").getSingleResult()).intValue();

        Metamodel metamodel = em.getMetamodel();
        EntityType<PhoneNumber> entityPhone_ = metamodel.entity(PhoneNumber.class);

        // test query "Delete PhoneNumber phone";
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaDelete<PhoneNumber>cq = qb.createCriteriaDelete(PhoneNumber.class);
        Root<PhoneNumber> root = cq.from(entityPhone_);

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

    public void testMetamodelCriteriaDelete() {
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

            Metamodel metamodel = em.getMetamodel();
            EntityType<PhoneNumber> entityPhone_ = metamodel.entity(PhoneNumber.class);
            EntityType<Employee> entityEmp_ = metamodel.entity(Employee.class);

            // test query "Delete Employee e where e.firstName is not null";
            CriteriaBuilder qb = em.getCriteriaBuilder();
            CriteriaDelete<PhoneNumber> cq = qb.createCriteriaDelete(PhoneNumber.class);
            Root<PhoneNumber> root = cq.from(entityPhone_);
            cq.where(qb.isNotNull(root.get(entityPhone_.getSingularAttribute("owner", Employee.class))
                    .get(entityEmp_.getSingularAttribute("firstName"))));
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

    @Override
    public String getPersistenceUnitName() {
       return "MulitPU-1";
    }
}
