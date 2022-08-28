/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Jun 29, 2009-1.0M6 Chris Delahunt
//       - TODO Bug#: Bug Description
//     07/05/2010-2.1.1 Michael O'Brien
//       - 321716: modelgen and jpa versions of duplicate code in both copies of
//       JUnitCriteriaSimpleTest must be kept in sync (to avoid only failing on WebSphere under Derby)
//       (ideally there should be only one copy of the code - the other suite should reference or subclass for changes)
//       see
//       org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTest.simpleModTest():1796
//       org.eclipse.persistence.testing.tests.jpa.criteria.metamodel.JUnitCriteriaSimpleTest.simpleModTest():1766
//       - 321902: this copied code should be renamed, merged or subclassed off the original
package org.eclipse.persistence.testing.tests.jpa.criteria.advanced;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;

import java.util.HashMap;
import java.util.List;

import static org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTestSuiteBase.Attributes.Address_city;
import static org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTestSuiteBase.Attributes.Address_postalCode;
import static org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTestSuiteBase.Attributes.Address_street;
import static org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTestSuiteBase.Attributes.Employee_address;
import static org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTestSuiteBase.Attributes.Employee_firstName;
import static org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTestSuiteBase.Attributes.Employee_hugeProject;
import static org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTestSuiteBase.Attributes.Employee_id;
import static org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTestSuiteBase.Attributes.Employee_lastName;
import static org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTestSuiteBase.Attributes.Employee_managedEmployees;
import static org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTestSuiteBase.Attributes.Employee_normalHours;
import static org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTestSuiteBase.Attributes.Employee_phoneNumbers;
import static org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTestSuiteBase.Attributes.Employee_projects;
import static org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTestSuiteBase.Attributes.Employee_salary;
import static org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTestSuiteBase.Attributes.Employee_status;
import static org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTestSuiteBase.Attributes.LargeProject_budget;
import static org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTestSuiteBase.Attributes.PhoneNumber_areaCode;
import static org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTestSuiteBase.Attributes.PhoneNumber_number;
import static org.eclipse.persistence.testing.tests.jpa.criteria.advanced.JUnitCriteriaSimpleTestSuiteBase.Attributes.PhoneNumber_owner;

/**
 * @author cdelahun Converted from JUnitJPQLSimpleTestSuite
 */
public class JUnitCriteriaSimpleTest extends JUnitCriteriaSimpleTestSuiteBase<String> {

    private final class Wrapper implements CriteriaQueryWrapper {
        @Override
        public <X,Y> Root<X> from(CriteriaQuery<Y> query, Class<X> entityClass) {
            return query.from(entityClass);
        }

        @Override
        public <X,Y> Fetch<X,Y> fetch(Root<X> root, Attributes attributeKey, JoinType joinType) {
            return root.fetch(attributes.get(attributeKey), JoinType.LEFT);
        }

        @Override
        public <X,Y> Path<Y> get(Path<X> path, Attributes attributeKey) {
            return path.get(attributes.get(attributeKey));
        }

        @Override
        public <X,Y> Join<X,Y> join(Root<X> root, Attributes attributeKey) {
            return root.join(attributes.get(attributeKey));
        }

        @Override
        public <X, Y> Join<X, Y> join(Root<X> root, Attributes attributeKey, JoinType joinType) {
            return root.join(attributes.get(attributeKey), joinType);
        }
    }

    @Override
    protected void setWrapper() {
        this.wrapper = new Wrapper();
    }

    @Override
    protected void populateAttributes() {
        attributes = new HashMap<>();
        attributes.put(Employee_id, "id");
        attributes.put(Employee_firstName, "firstName");
        attributes.put(Employee_lastName, "lastName");
        attributes.put(Employee_salary, "salary");
        attributes.put(Employee_normalHours, "normalHours");
        attributes.put(Employee_phoneNumbers, "phoneNumbers");
        attributes.put(Employee_managedEmployees, "managedEmployees");
        attributes.put(Employee_projects, "projects");
        attributes.put(Employee_address, "address");
        attributes.put(Employee_status, "status");
        attributes.put(Employee_hugeProject, "hugeProject");
        attributes.put(PhoneNumber_number, "number");
        attributes.put(PhoneNumber_areaCode, "areaCode");
        attributes.put(PhoneNumber_owner, "owner");
        attributes.put(Address_street, "street");
        attributes.put(Address_postalCode, "postalCode");
        attributes.put(Address_city, "city");
        attributes.put(LargeProject_budget, "budget");
    }

    public JUnitCriteriaSimpleTest() {
        super();
    }

    public JUnitCriteriaSimpleTest(String name) {
        super(name);
    }

    // This suite contains all tests contained in this class
    public static Test suite() {
        TestSuite suite = (TestSuite) JUnitCriteriaSimpleTestSuiteBase.suite(JUnitCriteriaSimpleTest.class);

        suite.setName("JUnitCriteriaSimpleTest");
        suite.addTest(new JUnitCriteriaSimpleTest("testParameterEqualsParameter"));
        suite.addTest(new JUnitCriteriaSimpleTest("testOneEqualsOne"));
        suite.addTest(new JUnitCriteriaSimpleTest("testTupleTupleValidation"));

        return suite;
    }

    /**
     * Tests 1=1 returns correct result.
     */
    @Override
    public void testParameterEqualsParameter() {
        DatabasePlatform databasePlatform = getPersistenceUnitServerSession().getPlatform();

        if (databasePlatform.isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test testParameterEqualsParameter skipped for this platform, " + "Symfoware doesn't allow dynamic parameters on both sides of the equals operator at the same time. (bug 304897)");
            return;
        }

        if (databasePlatform.isMaxDB()) {
            getPersistenceUnitServerSession().logMessage("Test testParameterEqualsParameter skipped for this platform, " + "MaxDB doesn't allow dynamic parameters on both sides of the equals operator at the same time. (bug 326962)");
            return;
        }

        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            CriteriaBuilder qb = em.getCriteriaBuilder();
            // "SELECT e FROM Employee e"
            Query query = em.createQuery(qb.createQuery(Employee.class));
            List emps = query.getResultList();

            assertNotNull(emps);
            int numRead = emps.size();

            // "SELECT e FROM Employee e WHERE :arg1=:arg2");
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            cq.where(qb.equal(qb.parameter(Integer.class, "arg1"), qb.parameter(Integer.class, "arg2")));
            query = em.createQuery(cq);

            query.setParameter("arg1", 1);
            query.setParameter("arg2", 1);
            emps = query.getResultList();

            assertNotNull(emps);
            assertEquals(numRead, emps.size());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * Tests 1=1 returns correct result.
     */
    public void testOneEqualsOne() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            CriteriaBuilder qb = em.getCriteriaBuilder();
            // "SELECT e FROM Employee e"
            Query query = em.createQuery(qb.createQuery(Employee.class));
            List emps = query.getResultList();

            assertNotNull(emps);
            int numRead = emps.size();

            // "SELECT e FROM Employee e WHERE 1=1");
            CriteriaQuery<Employee> cq = qb.createQuery(Employee.class);
            cq.where(qb.equal(qb.literal(1), 1));
            emps = em.createQuery(cq).getResultList();

            assertNotNull(emps);
            assertEquals(numRead, emps.size());

            ExpressionBuilder builder = new ExpressionBuilder();
            query = ((JpaEntityManager) em.getDelegate()).createQuery(builder.value(1).equal(builder.value(1)), Employee.class);
            emps = query.getResultList();

            assertNotNull(emps);
            assertEquals(numRead, emps.size());
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * bug 366182 : test Tuple.get(tuple) validation
     */
    public void testTupleTupleValidation() {
        EntityManager em = createEntityManager();

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteria = qb.createTupleQuery();

        Root<Employee> emp = criteria.from(Employee.class);
        Path<Object> unusedTupleElement = emp.get("normalHours"); // Receives IllegalArgumentException if metamodel is used
        Path<Object> lastName = emp.get("lastName");
        criteria.multiselect(lastName, emp.get("firstName"));

        TypedQuery<Tuple> query = em.createQuery(criteria);
        List<Tuple> list = query.getResultList();
        Tuple row = list.get(0);

        //verify it doesn't throw an exception in a valid case first:
        row.get(lastName);
        try {
            Object result = row.get(unusedTupleElement);
            fail("IllegalArgumentException expected using an invalid value. Result returned:"+result);
        } catch (Exception iae) {
            assertEquals(IllegalArgumentException.class, iae.getClass());
        }
        closeEntityManager(em);
    }

}
