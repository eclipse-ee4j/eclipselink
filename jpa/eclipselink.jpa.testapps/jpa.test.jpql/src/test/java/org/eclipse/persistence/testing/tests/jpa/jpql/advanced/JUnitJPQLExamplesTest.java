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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.jpql.advanced;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.EmploymentPeriod;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitDomainObjectComparer;
import org.junit.Assert;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class JUnitJPQLExamplesTest extends JUnitTestCase {
    static JUnitDomainObjectComparer comparer;

    public JUnitJPQLExamplesTest() {
        super();
    }

    public JUnitJPQLExamplesTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "advanced";
    }
    /**
     * This method is run at the end of EVERY test case method.
     */
    @Override
    public void tearDown() {
        clearCache();
    }

    /**
     * This suite contains all tests contained in this class.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLExamplesTest");
        suite.addTest(new JUnitJPQLExamplesTest("testSetup"));
        suite.addTest(new JUnitJPQLExamplesTest("findEmployeesInOntario"));
        suite.addTest(new JUnitJPQLExamplesTest("findAllProvinceWithEmployees"));
        suite.addTest(new JUnitJPQLExamplesTest("findAllEmployeesWithPhoneNumbers"));
        suite.addTest(new JUnitJPQLExamplesTest("findAllEmployeesWithOutPhoneNumbers"));
        suite.addTest(new JUnitJPQLExamplesTest("findAllEmployeesWithCellPhones"));
        suite.addTest(new JUnitJPQLExamplesTest("findEmployeeWithWorkPhone2258812"));
        suite.addTest(new JUnitJPQLExamplesTest("parameterTest"));
        suite.addTest(new JUnitJPQLExamplesTest("testOuterJoin"));
        suite.addTest(new JUnitJPQLExamplesTest("testExistsExpression"));
        suite.addTest(new JUnitJPQLExamplesTest("testAllExpressions"));
        suite.addTest(new JUnitJPQLExamplesTest("testGroupByHavingExpression"));
        suite.addTest(new JUnitJPQLExamplesTest("testGroupByHavingCount"));
        suite.addTest(new JUnitJPQLExamplesTest("testSumExpression"));
        suite.addTest(new JUnitJPQLExamplesTest("testOrderByExpression"));
        suite.addTest(new JUnitJPQLExamplesTest("testOrderByExpressionWithSelect"));

        suite.addTest(new JUnitJPQLExamplesTest("testCountExpression"));    //bug 5166658

        //Bug5097278
        suite.addTest(new JUnitJPQLExamplesTest("updateAllTest"));
        //Bug4924639
        suite.addTest(new JUnitJPQLExamplesTest("aggregateParameterTest"));
        // Bug 5090182
        suite.addTest(new JUnitJPQLExamplesTest("testEJBQLQueryString"));
        suite.addTest(new JUnitJPQLExamplesTest("updateEmbeddedFieldTest"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        clearCache();
        //get session to start setup
        DatabaseSession session = getPersistenceUnitServerSession();

        //create a new EmployeePopulator
        EmployeePopulator employeePopulator = new EmployeePopulator(supportsStoredProcedures());

        new AdvancedTableCreator().replaceTables(session);

        //initialize the global comparer object
        comparer = new JUnitDomainObjectComparer();

        //set the session for the comparer to use
        comparer.setSession((AbstractSession)session.getActiveSession());

        //Populate the advanced model
        employeePopulator.buildExamples();

        //Persist the advanced model examples in the database
        employeePopulator.persistExample(session);
    }

    public void findEmployeesInOntario() {
        EntityManager em = createEntityManager();
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("address").get("province").equal("ONT");

        @SuppressWarnings({"unchecked"})
        List<Employee> expectedResult = getPersistenceUnitServerSession().readAllObjects(Employee.class, whereClause);

        String ejbqlString = "SELECT e FROM Employee e WHERE e.address.province='ONT'";
        List<?> result = em.createQuery(ejbqlString).getResultList();
        //9 employees returned
        Assert.assertEquals("Find Employees in Ontario test failed: data validation error", result.size(), 9);
        Assert.assertTrue("Find Employees in Ontario test failed", comparer.compareObjects(expectedResult, result));

    }

    public void findAllProvinceWithEmployees() {
        boolean testPass = false;
        EntityManager em = createEntityManager();
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("address").get("province");

        ReportQuery rq = new ReportQuery();
        rq.returnWithoutReportQueryResult();
        rq.setReferenceClass(Employee.class);
        rq.addItem("province", whereClause);
        rq.useDistinct();

        List<?> expectedResult = (List<?>)getPersistenceUnitServerSession().executeQuery(rq);

        String ejbqlString = "SELECT DISTINCT e.address.province FROM Employee e";
        List<?> result = em.createQuery(ejbqlString).getResultList();

        if (expectedResult.equals(result))
            testPass = true;
        //5 provinces returned
        Assert.assertEquals("Find Province with employees test failed: data validation error", result.size(), 5);
        Assert.assertTrue("Find Province with employees test failed", testPass);
    }

    public void findAllEmployeesWithPhoneNumbers() {
        EntityManager em = createEntityManager();
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.isEmpty("phoneNumbers").not();

        ReadAllQuery raq = new ReadAllQuery(Employee.class);
        raq.setSelectionCriteria(whereClause);
        raq.useDistinct();

        List<?> expectedResult = (List<?>)getPersistenceUnitServerSession().executeQuery(raq);

        String ejbqlString = "SELECT DISTINCT e FROM Employee e, IN (e.phoneNumbers) l";
        Query query = em.createQuery(ejbqlString);
        if (usesSOP() && getPersistenceUnitServerSession().getPlatform().isOracle()) {
            // distinct is incompatible with blob in selection clause on Oracle
            query.setHint(QueryHints.SERIALIZED_OBJECT, "false");
        }
        List<?> firstResult = query.getResultList();

        String alternateEjbqlString = "SELECT e FROM Employee e WHERE e.phoneNumbers IS NOT EMPTY";
        List<?> secondResult = em.createQuery(alternateEjbqlString).getResultList();
        //14 employees returned
        Assert.assertEquals("Ejbql statements returned different results: data validation error", firstResult.size(), 14);
        Assert.assertTrue("Equivalent Ejbql statements returned different results", comparer.compareObjects(secondResult, firstResult));
        Assert.assertTrue("Find all employees with phone numbers test failed", comparer.compareObjects(expectedResult, firstResult));
    }

    public void findAllEmployeesWithOutPhoneNumbers() {
        EntityManager em = createEntityManager();
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.isEmpty("phoneNumbers");

        ReadAllQuery raq = new ReadAllQuery(Employee.class);
        raq.setSelectionCriteria(whereClause);
        raq.useDistinct();

        List<?> expectedResult = (List<?>)getPersistenceUnitServerSession().executeQuery(raq);

        String ejbqlString = "SELECT DISTINCT e FROM Employee e WHERE e.phoneNumbers IS EMPTY";
        List<?> result = em.createQuery(ejbqlString).getResultList();
        //1 employee w/o phone number returned
        Assert.assertEquals("Find all employees WITHOUT phone numbers test failed: data validation error", result.size(), 1);
        Assert.assertTrue("Find all employees WITHOUT phone numbers test failed", comparer.compareObjects(expectedResult, result));
    }

    public void findAllEmployeesWithCellPhones() {
        EntityManager em = createEntityManager();
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.anyOf("phoneNumbers").get("type").equal("Cellular");

        ReadAllQuery raq = new ReadAllQuery(Employee.class);
        raq.setSelectionCriteria(whereClause);
        if (usesSOP() && getPersistenceUnitServerSession().getPlatform().isOracle()) {
            // distinct is incompatible with blob in selection clause on Oracle
        } else {
            raq.useDistinct();
        }

        List<?> expectedResult = (List<?>)getPersistenceUnitServerSession().executeQuery(raq);

        String ejbqlString;
        if (usesSOP() && getPersistenceUnitServerSession().getPlatform().isOracle()) {
            // distinct is incompatible with blob in selection clause on Oracle
            ejbqlString = "SELECT e FROM Employee e JOIN e.phoneNumbers p " + "WHERE p.type = 'Cellular'";
        } else {
            ejbqlString = "SELECT DISTINCT e FROM Employee e JOIN e.phoneNumbers p " + "WHERE p.type = 'Cellular'";
        }
        List<?> firstResult = em.createQuery(ejbqlString).getResultList();
        String alternateEjbqlString;
        if (usesSOP() && getPersistenceUnitServerSession().getPlatform().isOracle()) {
            // distinct is incompatible with blob in selection clause on Oracle
            alternateEjbqlString = "SELECT e FROM Employee e INNER JOIN e.phoneNumbers p " + "WHERE p.type = 'Cellular'";
        } else {
            alternateEjbqlString = "SELECT DISTINCT e FROM Employee e INNER JOIN e.phoneNumbers p " + "WHERE p.type = 'Cellular'";
        }
        List<?> secondResult = em.createQuery(alternateEjbqlString).getResultList();
        //4 employees returned
        Assert.assertEquals("Find all employees with cellular phone numbers test failed: data validation error", firstResult.size(), 4);
        Assert.assertTrue("Find all employees with cellular phone numbers test failed: two equivalent ejb queries return different results", comparer.compareObjects(secondResult, firstResult));
        Assert.assertTrue("Find all employees with cellular phone numbers test failed", comparer.compareObjects(expectedResult, secondResult));
    }

    public void findEmployeeWithWorkPhone2258812() {
        EntityManager em = createEntityManager();
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause1 = builder.anyOf("phoneNumbers").get("type").equal("Work");
        Expression whereClause2 = builder.anyOf("phoneNumbers").get("number").equal("2258812");

        ReadAllQuery raq = new ReadAllQuery(Employee.class);
        raq.setSelectionCriteria(whereClause1.and(whereClause2));
        if (usesSOP() && getPersistenceUnitServerSession().getPlatform().isOracle()) {
            // distinct is incompatible with blob in selection clause on Oracle
        } else {
            raq.useDistinct();
        }

        List<?> expectedResult = (List<?>)getPersistenceUnitServerSession().executeQuery(raq);

        String ejbqlString;
        if (usesSOP() && getPersistenceUnitServerSession().getPlatform().isOracle()) {
            // distinct is incompatible with blob in selection clause on Oracle
            ejbqlString = "SELECT e FROM Employee e JOIN e.phoneNumbers p " + "WHERE p.type = 'Work' AND p.number = '2258812' ";
        } else {
            ejbqlString = "SELECT DISTINCT e FROM Employee e JOIN e.phoneNumbers p " + "WHERE p.type = 'Work' AND p.number = '2258812' ";
        }
        List<?> result = em.createQuery(ejbqlString).getResultList();
        //8 employees
        Assert.assertEquals("Find employee with 2258812 number test failed: data validation error", result.size(), 8);
        Assert.assertTrue("Find employee with 2258812 number test failed", comparer.compareObjects(expectedResult, result));
    }

    public void parameterTest() {
        EntityManager em = createEntityManager();
        @SuppressWarnings({"unchecked"})
        List<Employee> employeeList = getPersistenceUnitServerSession().readAllObjects(Employee.class);
        Employee expectedEmployee = employeeList.get(0);
        int i = 1;
        while (expectedEmployee.getPhoneNumbers().size() == 0) {
            expectedEmployee = employeeList.get(i);
            i++;
        }
        String phoneNumber = (expectedEmployee.getPhoneNumbers().iterator().next()).getNumber();
        String ejbqlString;
        String alternateEjbqlString;
        if (usesSOP() && getPersistenceUnitServerSession().getPlatform().isOracle()) {
            // distinct is incompatible with blob in selection clause on Oracle
            ejbqlString = "SELECT e FROM Employee e, IN(e.phoneNumbers) p WHERE p.number = ?1";
            alternateEjbqlString = "SELECT e FROM Employee e, IN(e.phoneNumbers) p WHERE p.number = :number";
        } else {
            ejbqlString = "SELECT DISTINCT e FROM Employee e, IN(e.phoneNumbers) p WHERE p.number = ?1";
            alternateEjbqlString = "SELECT DISTINCT e FROM Employee e, IN(e.phoneNumbers) p WHERE p.number = :number";
        }

        List<?> firstResult = em.createQuery(ejbqlString).setParameter(1, phoneNumber).getResultList();
        List<?> secondResult = em.createQuery(alternateEjbqlString).setParameter("number", phoneNumber).getResultList();
        //random test cant duplicate
        Assert.assertTrue("Parameter test failed: two equivalent ejb queries return different results", comparer.compareObjects(secondResult, firstResult));
        Assert.assertTrue("Parameter test failed", comparer.compareObjects(expectedEmployee, firstResult));
    }

    public void testOuterJoin() {
        EntityManager em = createEntityManager();
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.anyOfAllowingNone("phoneNumbers").get("type").equal("Cellular");
        ReadAllQuery raq = new ReadAllQuery(Employee.class);
        raq.setSelectionCriteria(whereClause);
        List<?> expectedResult = (List<?>)getPersistenceUnitServerSession().executeQuery(raq);

        String ejbqlString = "SELECT e FROM Employee e LEFT JOIN e.phoneNumbers p " + "WHERE p.type = 'Cellular'";
        List<?> firstResult = em.createQuery(ejbqlString).getResultList();
        String alternateEjbqlString = "SELECT e FROM Employee e LEFT OUTER JOIN e.phoneNumbers p " + "WHERE p.type = 'Cellular'";
        List<?> secondResult = em.createQuery(alternateEjbqlString).getResultList();
        //return 4 employees with cell phones
        Assert.assertEquals("Get SalesPerson for Orders test failed: data validation error", firstResult.size(), 4);
        Assert.assertTrue("Get Outer Join test failed: two equivalent ejb queries return different results", comparer.compareObjects(secondResult, firstResult));
        Assert.assertTrue("Get Outer Join test failed", comparer.compareObjects(expectedResult, firstResult));
    }

    public void testExistsExpression() {
        EntityManager em = createEntityManager();
        boolean testPass = false;
        ExpressionBuilder employeeBuilder = new ExpressionBuilder(Employee.class);
        ExpressionBuilder managerBuilder = new ExpressionBuilder(Employee.class);
        ReportQuery mainQuery = new ReportQuery();
        ReportQuery subQuery = new ReportQuery();

        subQuery.setReferenceClass(Employee.class);
        Expression managerExpression = employeeBuilder.get("manager").get("id").equal(managerBuilder.get("id"));
        subQuery.addAttribute("one", new ConstantExpression(1, subQuery.getExpressionBuilder()));
        subQuery.setSelectionCriteria(managerExpression);
        Expression employeeExpression = employeeBuilder.exists(subQuery);

        mainQuery.setReferenceClass(Employee.class);
        mainQuery.setSelectionCriteria(employeeExpression);
        mainQuery.addAttribute("id");
        mainQuery.returnWithoutReportQueryResult();
        List<?> expectedResult = (List<?>)getPersistenceUnitServerSession().executeQuery(mainQuery);

        String ejbqlString = "SELECT DISTINCT emp.id FROM Employee emp WHERE EXISTS ( SELECT managedEmp.id FROM Employee managedEmp WHERE managedEmp = emp.manager)";
        List<?> result = em.createQuery(ejbqlString).getResultList();

        if (result.containsAll(expectedResult) && expectedResult.containsAll(result))
            testPass = true;
        //8 employees with managers
        Assert.assertEquals("Exists Expression test failed: data validation error", result.size(), 8);
        Assert.assertTrue("Exists Expression test failed", testPass);
    }

    public void testAllExpressions() {
        EntityManager em = createEntityManager();
        boolean testPass = false;
        ExpressionBuilder mainQueryBuilder = new ExpressionBuilder(Employee.class);
        ExpressionBuilder subQueryBuilder = new ExpressionBuilder(Employee.class);
        ReportQuery mainQuery = new ReportQuery();
        mainQuery.setReferenceClass(Employee.class);
        ReportQuery subQuery = new ReportQuery();
        subQuery.setReferenceClass(Employee.class);

        Expression subQueryExpression = subQueryBuilder.get("address").get("city").equal(mainQueryBuilder.get("address").get("city")).and(subQueryBuilder.get("salary").lessThan(1000));
        subQuery.setSelectionCriteria(subQueryExpression);
        subQuery.addAttribute("salary");
        Expression mainQueryExpression = mainQueryBuilder.get("salary").greaterThan(mainQueryBuilder.all(subQuery));
        mainQuery.addAttribute("id");
        mainQuery.setSelectionCriteria(mainQueryExpression);
        mainQuery.returnWithoutReportQueryResult();
        List<?> expectedResult = (List<?>)getPersistenceUnitServerSession().executeQuery(mainQuery);

        String ejbqlString = "SELECT emp.id FROM Employee emp WHERE emp.salary > ALL ( SELECT e.salary FROM Employee e WHERE e.address.city = emp.address.city AND e.salary < 1000)";
        List<?> result = em.createQuery(ejbqlString).getResultList();

        if (result.containsAll(expectedResult) && expectedResult.containsAll(result))
            testPass = true;

        if (result.size() != 12) {
            // H2 ALL does not work correctly if the result is empty.
            if (getPersistenceUnitServerSession().getPlatform().isH2()) {
                warning("ALL fails on H2 as H2 has an SQL bug in ALL of none");
            } else {
                fail("All Expression test failed: data validation error: " + result.size() + " != " + 12);
            }
        }
        Assert.assertTrue("All Expression test failed", testPass);
    }

    public void testGroupByHavingExpression() {
        EntityManager em = createEntityManager();
        boolean testPass = true;

        ReadAllQuery raq = new ReadAllQuery(Employee.class, new ExpressionBuilder());
        Expression whereClause1 = raq.getExpressionBuilder().get("firstName").equal("Bob");
        Expression whereClause2 = raq.getExpressionBuilder().get("lastName").equal("Smith");
        Expression whereClause3 = raq.getExpressionBuilder().get("firstName").equal("John");
        Expression whereClause4 = raq.getExpressionBuilder().get("lastName").equal("Way");

        raq.setSelectionCriteria((whereClause1.and(whereClause2)).or(whereClause3.and(whereClause4)));
        @SuppressWarnings({"unchecked"})
        List<Employee> employees = (List<Employee>)getPersistenceUnitServerSession().executeQuery(raq);
        int firstManagerId = employees.get(0).getId();
        int secondManagerId = employees.get(1).getId();
        int expectedEmployeesManaged = employees.get(0).getManagedEmployees().size() + employees.get(1).getManagedEmployees().size();
        Vector<Integer> managerVector = new Vector<>();
        managerVector.add(firstManagerId);
        managerVector.add(secondManagerId);
        ReportQuery query = new ReportQuery(Employee.class, new ExpressionBuilder());
        query.returnWithoutReportQueryResult();
        query.addGrouping(query.getExpressionBuilder().get("manager").get("id"));
        query.setHavingExpression(query.getExpressionBuilder().get("manager").get("id").in(managerVector));
        query.addAttribute("managerId", query.getExpressionBuilder().get("manager").get("id"));
        query.addAverage("salary", Double.class);
        query.addCount("id", Long.class);

        @SuppressWarnings({"unchecked"})
        List<Object[]> expectedResult = (List<Object[]>)getPersistenceUnitServerSession().executeQuery(query);

        String ejbqlString = "SELECT e.manager.id, avg(e.salary), count(e) FROM Employee e" + " GROUP BY e.manager.id HAVING e.manager.id IN (" + firstManagerId + "," + secondManagerId + ")";

        List<Object[]> result = em.createQuery(ejbqlString, Object[].class).getResultList();
        int employeesManaged = 0;
        Iterator<Object[]> expectedResultIterator = expectedResult.iterator();
        Iterator<Object[]> resultIterator = result.iterator();
        if (expectedResult.size() == result.size()) {
            while (resultIterator.hasNext()) {
                Object[] objectArray = expectedResultIterator.next();
                Object[] otherObjectArray = resultIterator.next();
                testPass = testPass && objectArray[0].equals(otherObjectArray[0]);
                testPass = testPass && objectArray[1].equals(otherObjectArray[1]);
                testPass = testPass && objectArray[2].equals(otherObjectArray[2]);
                employeesManaged = ((Long)objectArray[2]).intValue() + employeesManaged;
            }
        } else {
            testPass = false;
        }

        Assert.assertEquals("GroupBy Having expression test failed: data validation error", employeesManaged, expectedEmployeesManaged);
        Assert.assertTrue("GroupBy Having expression test failed", testPass);

    }

    public void testGroupByHavingCount() {
        EntityManager em = createEntityManager();
        boolean testPass = true;
        ReportQuery query = new ReportQuery(Employee.class, new ExpressionBuilder());
        query.returnWithoutReportQueryResult();
        query.addGrouping(query.getExpressionBuilder().get("address").get("province"));
        query.addAttribute("province", query.getExpressionBuilder().get("address").get("province"));
        query.addCount("provinces", query.getExpressionBuilder().get("address").get("province"), Long.class);
        query.setHavingExpression(query.getExpressionBuilder().get("address").get("province").count().greaterThan(3));
        @SuppressWarnings({"unchecked"})
        List<Object[]> expectedResult = (List<Object[]>)getPersistenceUnitServerSession().executeQuery(query);

        String ejbqlString = "SELECT e.address.province, COUNT(e) FROM Employee e GROUP BY e.address.province HAVING COUNT(e.address.province) > 3";
        List<Object[]> result = em.createQuery(ejbqlString, Object[].class).getResultList();

        Iterator<Object[]> expectedResultIterator = expectedResult.iterator();
        Iterator<Object[]> resultIterator = result.iterator();
        if (expectedResult.size() == result.size()) {
            while (resultIterator.hasNext()) {
                Object[] objectArray = expectedResultIterator.next();
                Object[] otherObjectArray = resultIterator.next();
                testPass = testPass && objectArray[0].equals(otherObjectArray[0]);
                testPass = testPass && objectArray[1].equals(otherObjectArray[1]);
            }
        } else {
            testPass = false;
        }
        Assert.assertEquals("GroupBy Having count expression test failed: data validation error", result.size(), 1);
        Assert.assertTrue("GroupBy Having Count expression test failed", testPass);
    }

    public void testSumExpression() {
        EntityManager em = createEntityManager();

        ReportQuery query = new ReportQuery(Employee.class, new ExpressionBuilder());
        Expression whereClause = query.getExpressionBuilder().get("address").get("province").equal("QUE");
        query.addSum("areaCodeSums", query.getExpressionBuilder().anyOf("phoneNumbers").get("id"), Long.class);
        query.setSelectionCriteria(whereClause);
        query.returnWithoutReportQueryResult();
        @SuppressWarnings({"unchecked"})
        Long expectedResult = ((List<Long>)getPersistenceUnitServerSession().executeQuery(query)).get(0);
        String ejbqlString = "SELECT SUM(p.id) FROM Employee e JOIN e.phoneNumbers p JOIN e.address a" + " WHERE a.province = 'QUE' ";
        Long result = em.createQuery(ejbqlString, Long.class).getSingleResult();
        Assert.assertEquals("Average expression test failed", expectedResult, result);
    }

    //bug 5166658

    public void testCountExpression() {
        EntityManager em = createEntityManager();

        ReportQuery query = new ReportQuery(Employee.class, new ExpressionBuilder());
        Expression whereClause1 = query.getExpressionBuilder().get("address").get("province").equal("QUE");
        Expression whereClause2 = query.getExpressionBuilder().get("address").get("city").equal("Montreal");
        query.setSelectionCriteria(whereClause1.and(whereClause2));
        query.addCount("areaCodeCount", query.getExpressionBuilder().anyOf("phoneNumbers").get("areaCode"), Long.class);
        query.returnSingleResult();
        Long expectedResult = (Long)((ReportQueryResult)getPersistenceUnitServerSession().executeQuery(query)).get("areaCodeCount");

        String ejbqlString = "SELECT COUNT(p.areaCode) FROM Employee e JOIN e.phoneNumbers p JOIN e.address a " + " WHERE a.province='QUE' AND a.city='Montreal'";
        Long result = (Long)em.createQuery(ejbqlString).getSingleResult();

        String alternateEjbqlString = "SELECT COUNT(p) FROM Employee e JOIN e.phoneNumbers p JOIN e.address a " + " WHERE a.province='QUE' AND a.city='Montreal' AND p.areaCode IS NOT NULL";
        Long alternateResult = (Long)em.createQuery(alternateEjbqlString).getSingleResult();

        Assert.assertTrue("Count expression test failed: data validation error, ReportQuery returned 0", expectedResult.intValue() > 0);
        Assert.assertTrue("Count expression test failed: data validation error, first JPQL returned 0", result.intValue() > 0);
        Assert.assertTrue("Count expression test failed: data validation error, second JPQL returned 0", alternateResult.intValue() > 0);
        Assert.assertEquals("Count expression test failed: two equivalent ejb queries return different results", alternateResult, result);
        Assert.assertEquals("Count expression test failed", expectedResult, result);
    }

    public void testOrderByExpression() {
        EntityManager em = createEntityManager();

        ReadAllQuery raq = new ReadAllQuery(PhoneNumber.class, new ExpressionBuilder());
        Expression whereClause = raq.getExpressionBuilder().get("owner").get("address").get("province").equal("ONT");
        raq.setSelectionCriteria(whereClause);
        raq.addOrdering(raq.getExpressionBuilder().get("areaCode"));
        raq.addOrdering(raq.getExpressionBuilder().get("type"));
        List<?> expectedResult = (List<?>)getPersistenceUnitServerSession().executeQuery(raq);

        String ejbqlString = "SELECT p FROM Employee e JOIN e.phoneNumbers p JOIN e.address a WHERE a.province = 'ONT' " + "ORDER BY p.areaCode, p.type";
        List<?> result = em.createQuery(ejbqlString).getResultList();
        Assert.assertEquals("OrderBy expression test failed: data validation error", result.size(), 13);
        Assert.assertTrue("OrderBy expression test failed", comparer.compareObjects(expectedResult, result));
    }

    public void testOrderByExpressionWithSelect() {
        EntityManager em = createEntityManager();
        boolean testPass = true;
        ReportQuery query = new ReportQuery(PhoneNumber.class, new ExpressionBuilder());
        Expression whereClause = query.getExpressionBuilder().get("owner").get("address").get("province").equal("ONT");
        query.setSelectionCriteria(whereClause);
        query.addOrdering(query.getExpressionBuilder().get("areaCode"));
        query.addOrdering(query.getExpressionBuilder().get("type"));
        query.addAttribute("areaCode", query.getExpressionBuilder().get("areaCode"));
        query.addAttribute("type", query.getExpressionBuilder().get("type"));
        //query.useDistinct(); //removed as distinct no longer used on joins in JPQL
        query.returnWithoutReportQueryResult();
        @SuppressWarnings({"unchecked"})
        List<Object[]> expectedResult = (List<Object[]>)getPersistenceUnitServerSession().executeQuery(query);

        String ejbqlString = "SELECT p.areaCode, p.type FROM Employee e JOIN e.phoneNumbers p JOIN e.address a WHERE a.province = 'ONT' " + "ORDER BY p.areaCode, p.type";
        List<Object[]> result = em.createQuery(ejbqlString, Object[].class).getResultList();
        Iterator<Object[]> expectedResultIterator = expectedResult.iterator();
        Iterator<Object[]> resultIterator = result.iterator();
        if (expectedResult.size() == result.size()) {
            while (resultIterator.hasNext()) {
                Object[] objectArray = expectedResultIterator.next();
                Object[] otherObjectArray = resultIterator.next();
                testPass = testPass && objectArray[0].equals(otherObjectArray[0]);
                testPass = testPass && objectArray[1].equals(otherObjectArray[1]);
            }
        } else {
            testPass = false;
        }

        Assert.assertEquals("OrderBy expression test failed: data validation error", result.size(), 13);
        Assert.assertTrue("OrderBy with Select expression test failed", testPass);
    }

    // Bug 5090182

    public void testEJBQLQueryString() {
        List<Object[]> emps = createEntityManager().createQuery("SELECT e, a FROM Employee e, Address a WHERE e.address = a", Object[].class).getResultList();
        assertFalse("No employees were read, debug and look at the SQL that was generated. ", emps.isEmpty());
    }

    //Bug5097278 Test case for updating the manager of ALL employees that have a certain address

    public void updateAllTest() {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test updateAllTest skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();

        String empName = "Saunders";
        String manName = "Smitty";

        String ejbqlString = "SELECT DISTINCT e FROM Employee e WHERE e.lastName = '" + empName + "'";
        Employee employee = (Employee)em.createQuery(ejbqlString).getSingleResult();
        Address addr = em.find(Address.class, employee.getAddress().getID());

        String ejbqlString2 = "SELECT DISTINCT e FROM Employee e WHERE e.lastName = '" + manName + "'";
        Employee manager = (Employee)em.createQuery(ejbqlString2).getSingleResult();


        beginTransaction(em);

        em.createQuery("UPDATE Employee e SET e.manager = :manager " + "WHERE e.address = :addr ").setParameter("manager", manager).setParameter("addr", addr).executeUpdate();

        commitTransaction(em);

        String ejbqlString3 = "SELECT DISTINCT e.manager FROM Employee e WHERE e.lastName = '" + empName + "'";
        String result = ((Employee)em.createQuery(ejbqlString3).getSingleResult()).getLastName();

        Assert.assertEquals("UpdateAll test failed", result, manName);

    }

    public void updateEmbeddedFieldTest() {
        if ((getPersistenceUnitServerSession()).getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test updateEmbeddedFieldTest skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }

        EntityManager em = createEntityManager();
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(1905, 11, 31, 0, 0, 0);
        java.sql.Date startDate = new java.sql.Date(startCalendar.getTime().getTime());

        beginTransaction(em);
        em.createQuery("UPDATE Employee e SET e.period.startDate= :startDate").setParameter("startDate", startDate).executeUpdate();
        commitTransaction(em);
    }

    //Bug5040609 Test case for aggregates as parameters in EJBQL

    public void aggregateParameterTest() {
        EntityManager em = createEntityManager();

        ExpressionBuilder builder = new ExpressionBuilder();
        ReportQuery query = new ReportQuery(Employee.class, builder);
        query.returnWithoutReportQueryResult();
        query.addItem("employee", builder);

        EmploymentPeriod period = new EmploymentPeriod();
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        startCalendar.set(1901, 11, 31, 0, 0, 0);
        endCalendar.set(1995, 0, 12, 0, 0, 0);
        period.setStartDate(new java.sql.Date(startCalendar.getTime().getTime()));
        period.setEndDate(new java.sql.Date(endCalendar.getTime().getTime()));
        Expression exp = builder.get("period").equal(builder.getParameter("period"));
        query.setSelectionCriteria(exp);
        query.addArgument("period", EmploymentPeriod.class);

        Vector<EmploymentPeriod> args = new Vector<>();
        args.add(period);

        List<?> expectedResult = (Vector<?>)getPersistenceUnitServerSession().executeQuery(query, args);

        List<?> result = em.createQuery("SELECT e FROM Employee e WHERE e.period = :period ").setParameter("period", period).getResultList();

        Assert.assertTrue("aggregateParameterTest failed", comparer.compareObjects(expectedResult, result));
    }

}
