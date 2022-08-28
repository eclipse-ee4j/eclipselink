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
package org.eclipse.persistence.testing.tests.jpa.jpql.advanced;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.EmploymentPeriod;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitDomainObjectComparer;
import org.junit.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * <p>
 * <b>Purpose</b>: Test complex aggregate EJBQL functionality.
 * <p>
 * <b>Description</b>: This class creates a test suite, initializes the database
 * and adds tests to the suite.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for complex aggregate EJBQL functionality
 * </ul>
 * @see org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator
 * @see JUnitDomainObjectComparer
 */

//This test suite demonstrates the bug 4616218, waiting for bug fix
public class JUnitJPQLComplexAggregateTest extends JUnitTestCase
{
    static JUnitDomainObjectComparer comparer;        //the global comparer object used in all tests

    public JUnitJPQLComplexAggregateTest()
    {
        super();
    }

    public JUnitJPQLComplexAggregateTest(String name)
    {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "advanced";
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
        suite.setName("JUnitJPQLComplexAggregateTest");
        suite.addTest(new JUnitJPQLComplexAggregateTest("testSetup"));

        suite.addTest(new JUnitJPQLComplexAggregateTest("complexSelectAggregateTest"));

        suite.addTest(new JUnitJPQLComplexAggregateTest("complexAVGTest"));
        suite.addTest(new JUnitJPQLComplexAggregateTest("complexAVGOrderTest"));
        suite.addTest(new JUnitJPQLComplexAggregateTest("complexCountDistinctWithGroupByAndHavingTest"));
        suite.addTest(new JUnitJPQLComplexAggregateTest("complexCountDistinctWithGroupByTest"));
        suite.addTest(new JUnitJPQLComplexAggregateTest("complexCountDistinctWithGroupByTest2"));
        suite.addTest(new JUnitJPQLComplexAggregateTest("complexHavingWithAggregate"));
        suite.addTest(new JUnitJPQLComplexAggregateTest("complexCountTest"));
        suite.addTest(new JUnitJPQLComplexAggregateTest("complexCountWithGroupByTest"));
        suite.addTest(new JUnitJPQLComplexAggregateTest("complexDistinctCountTest"));
        suite.addTest(new JUnitJPQLComplexAggregateTest("complexMaxTest"));
        suite.addTest(new JUnitJPQLComplexAggregateTest("complexMinTest"));
        suite.addTest(new JUnitJPQLComplexAggregateTest("complexSumTest"));
        suite.addTest(new JUnitJPQLComplexAggregateTest("complexCountDistinctOnBaseQueryClass"));
        suite.addTest(new JUnitJPQLComplexAggregateTest("complexCountOnJoinedVariableCompositePK"));
        suite.addTest(new JUnitJPQLComplexAggregateTest("testMultipleCoalesce"));

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
//        new CompositePKTableCreator().replaceTables(session);

        //initialize the global comparer object
        comparer = new JUnitDomainObjectComparer();

        //set the session for the comparer to use
        comparer.setSession((AbstractSession)session.getActiveSession());

        //Populate the tables
        employeePopulator.buildExamples();

        //Persist the examples in the database
        employeePopulator.persistExample(session);

    }

    public void complexAVGTest()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        ExpressionBuilder expbldr = new ExpressionBuilder();

        ReportQuery rq = new ReportQuery(Employee.class, expbldr);

        Expression exp = expbldr.get("lastName").equal("Smith");

        rq.setReferenceClass(Employee.class);
        rq.setSelectionCriteria(exp);
        rq.returnSingleAttribute();
        rq.dontRetrievePrimaryKeys();
        rq.useDistinct();
        rq.addAverage("salary", Double.class);

        String ejbqlString = "SELECT AVG(DISTINCT emp.salary) FROM Employee emp WHERE emp.lastName = \"Smith\"";

        @SuppressWarnings({"unchecked"})
        Vector<Double> expectedResultVector = (Vector<Double>) getPersistenceUnitServerSession().executeQuery(rq);
        Double expectedResult = expectedResultVector.get(0);

        clearCache();

        Double result = em.createQuery(ejbqlString, Double.class).getSingleResult();

        Assert.assertEquals("Complex AVG test failed", expectedResult, result);
        rollbackTransaction(em);
        closeEntityManager(em);
    }

    public void complexAVGOrderTest()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        ExpressionBuilder expbldr = new ExpressionBuilder();

        ReportQuery rq = new ReportQuery(Employee.class, expbldr);

        Expression exp = expbldr.get("lastName").equal("Smith");

        rq.setReferenceClass(Employee.class);
        rq.setSelectionCriteria(exp);
        rq.dontRetrievePrimaryKeys();
        if ((getPersistenceUnitServerSession().getPlatform().isSymfoware() || getPersistenceUnitServerSession().getPlatform().isDerby())) {
            warning("Distinct on aggregate function not supported by " + getPersistenceUnitServerSession().getPlatform());
        } else {
            // the following line uncovers a bug where 'AVG(t1.SALARY)' is
            // listed in the generated select list twice. As it is also in the
            // ORDER BY clause, Symfoware complains that it does not know which
            // in the SELECT list it is referring to.
            rq.useDistinct();
        }

        Expression avgSal = expbldr.get("salary").average();
        rq.addAttribute("salary", avgSal);
        rq.addOrdering(avgSal);

        Expression gender = expbldr.get("gender");
        rq.addAttribute("gender", gender);
        rq.addGrouping(gender);



        String ejbqlString = "SELECT emp.gender, AVG(DISTINCT emp.salary) sal FROM Employee emp WHERE emp.lastName = \"Smith\" group by emp.gender order by sal";

        if ((getPersistenceUnitServerSession().getPlatform().isSymfoware() || getPersistenceUnitServerSession().getPlatform().isDerby())) {
            ejbqlString = "SELECT emp.gender, AVG(emp.salary) sal FROM Employee emp WHERE emp.lastName = \"Smith\" group by emp.gender order by sal";
        }
        Vector<?> expectedResultVector = (Vector<?>) getPersistenceUnitServerSession().executeQuery(rq);

        clearCache();

        List<?> result =  em.createQuery(ejbqlString).getResultList();

        Assert.assertTrue("complexAVGOrderTest test failed", comparer.compareObjects(result, expectedResultVector));
        rollbackTransaction(em);
        closeEntityManager(em);
    }

    /*
     * test for gf675, using count, group by and having fails.  This test is specific for a a use case
     * with Count and group by
     */
    public void complexCountDistinctWithGroupByAndHavingTest()
    {
        String havingFilterString = "Toronto";
        EntityManager em = createEntityManager();
        beginTransaction(em);
        //Need to set the class in the expressionbuilder, as the Count(Distinct) will cause the
        // query to change and be built around the Employee class instead of the Address class.
        ExpressionBuilder expbldr = new ExpressionBuilder(Address.class);

        ReportQuery rq = new ReportQuery(Address.class, expbldr);
        Expression exp = expbldr.anyOf("employees");

        Expression exp2 = expbldr.get("city");
        rq.addAttribute("city", exp2);
        rq.addCount("COUNT",exp.distinct(),Long.class );
        rq.addGrouping(exp2);
        rq.setHavingExpression(exp2.equal(havingFilterString));
        Vector<?> expectedResult = (Vector<?>) getPersistenceUnitServerSession().executeQuery(rq);
        String ejbqlString3 = "SELECT a.city, COUNT( DISTINCT e ) FROM Address a JOIN a.employees e GROUP BY a.city HAVING a.city =?1";
        Query q = em.createQuery(ejbqlString3);
        q.setParameter(1,havingFilterString);
        List<?> result = q.getResultList();

        Assert.assertTrue("Complex COUNT test failed", comparer.compareObjects(result, expectedResult));
        rollbackTransaction(em);
        closeEntityManager(em);
    }


    /*
     * test for gf675, using count, group by and having fails.  This test is specific for a a use case
     * where DISTINCT is used with Count and group by
     */
    public void complexCountDistinctWithGroupByTest()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        //need to set the class in the expressionbuilder, as the Count(Distinct) will cause the
        // query to change and be built around the Employee class instead of the Address class.
        ExpressionBuilder expbldr = new ExpressionBuilder(Address.class);

        ReportQuery rq = new ReportQuery(Address.class, expbldr);
        Expression exp = expbldr.anyOf("employees");

        Expression exp2 = expbldr.get("city");
        rq.addAttribute("city", exp2);
        rq.addCount("COUNT",exp.distinct(),Long.class );
        rq.addGrouping(exp2);
        Vector<?> expectedResult = (Vector<?>) getPersistenceUnitServerSession().executeQuery(rq);
        String ejbqlString3 = "SELECT a.city, COUNT( DISTINCT e ) FROM Address a JOIN a.employees e GROUP BY a.city";
        Query q = em.createQuery(ejbqlString3);
        List<?> result = q.getResultList();

        Assert.assertTrue("Complex COUNT(Distinct) with Group By test failed", comparer.compareObjects(result, expectedResult));
        rollbackTransaction(em);
        closeEntityManager(em);
    }

    /*
     * test for gf675, using count, group by and having fails.  This test is specific for a a use case
     * where DISTINCT is used with Count and group by
     */
    public void complexCountDistinctWithGroupByTest2()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        //need to set the class in the expressionbuilder, as the Count(Distinct) will cause the
        // query to change and be built around the Employee class instead of the Address class.
        ExpressionBuilder expbldr = new ExpressionBuilder(Address.class);

        ReportQuery rq = new ReportQuery(Address.class, expbldr);
        Expression exp = expbldr.anyOf("employees");

        Expression exp2 = expbldr.get("city");
        rq.addAttribute("city", exp2);
        rq.addCount("COUNT1",exp, Long.class);
        rq.addCount("COUNT2",exp.get("lastName").distinct(),Long.class );
        rq.addGrouping(exp2);
        Vector<?> expectedResult = (Vector<?>) getPersistenceUnitServerSession().executeQuery(rq);
        String ejbqlString3 = "SELECT a.city, COUNT( e ), COUNT( DISTINCT e.lastName ) FROM Address a JOIN a.employees e GROUP BY a.city";
        Query q = em.createQuery(ejbqlString3);
        List<?> result = q.getResultList();

        Assert.assertTrue("Complex COUNT(Distinct) with Group By test failed", comparer.compareObjects(result, expectedResult));
        rollbackTransaction(em);
        closeEntityManager(em);
    }

    /**
     * Test for partial fix of GF 932.
     */
    public void complexHavingWithAggregate()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Test using the project id in COUNT, GROUP BY and HAVING
        ExpressionBuilder employeeBuilder = new ExpressionBuilder(Employee.class);
        ReportQuery rq = new ReportQuery(Employee.class, employeeBuilder);
        Expression projects = employeeBuilder.anyOf("projects");
        Expression pid = projects.get("id");
        Expression count = pid.count();
        rq.addAttribute("id", pid);
        rq.addAttribute("COUNT", count, Long.class);
        rq.addGrouping(pid);
        rq.setHavingExpression(count.greaterThan(1));
        rq.setShouldReturnWithoutReportQueryResult(true);
        //Vector expectedResult = (Vector) em.getActiveSession().executeQuery(rq);
        Vector<?> expectedResult = (Vector<?>) getPersistenceUnitServerSession().executeQuery(rq);
        String jpql =
            "SELECT p.id, COUNT(p.id) FROM Employee e JOIN e.projects p " +
            "GROUP BY p.id HAVING COUNT(p.id)>1";
        List<?> result = em.createQuery(jpql).getResultList();

        Assert.assertTrue("Complex HAVING with aggregate function failed",
                          comparer.compareObjects(result, expectedResult));

        // Test using the project itself in COUNT, GROUP BY and HAVING
        employeeBuilder = new ExpressionBuilder(Employee.class);
        rq = new ReportQuery(Employee.class, employeeBuilder);
        projects = employeeBuilder.anyOf("projects");
        count = projects.count();
        rq.addAttribute("projects", projects);
        rq.addAttribute("COUNT", count, Long.class);
        rq.addGrouping(projects);
        rq.setHavingExpression(count.greaterThan(1));
        rq.setShouldReturnWithoutReportQueryResult(true);
        expectedResult = (Vector<?>) getPersistenceUnitServerSession().executeQuery(rq);

        jpql =
            "SELECT p, COUNT(p) FROM Employee e JOIN e.projects p " +
            "GROUP BY p HAVING COUNT(p)>1";
        result = em.createQuery(jpql).getResultList();

        Assert.assertTrue("Complex HAVING with aggregate function failed",
                          comparer.compareObjects(result, expectedResult));
        rollbackTransaction(em);
        closeEntityManager(em);
    }

    public void complexCountTest()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            ExpressionBuilder expbldr = new ExpressionBuilder();

            ReportQuery rq = new ReportQuery(Employee.class, expbldr);

            Expression exp = expbldr.get("lastName").equal("Smith");

            rq.setReferenceClass(Employee.class);
            rq.setSelectionCriteria(exp);
            rq.returnSingleAttribute();
            rq.dontRetrievePrimaryKeys();
            rq.addCount("COUNT", expbldr, Long.class);
            @SuppressWarnings({"unchecked"})
            Vector<Long> expectedResultVector = (Vector<Long>) getPersistenceUnitServerSession().executeQuery(rq);
            Long expectedResult = expectedResultVector.get(0);

            String ejbqlString = "SELECT COUNT(emp) FROM Employee emp WHERE emp.lastName = \"Smith\"";
            Long result = em.createQuery(ejbqlString, Long.class).getSingleResult();

            Assert.assertEquals("Complex COUNT test failed", expectedResult, result);
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    /*
     * test for gf675, using count, group by and having fails.  This test is specific for a a use case
     * with Count and group by
     */
    public void complexCountWithGroupByTest()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        //Need to set the class in the expressionbuilder, as the Count(Distinct) will cause the
        // query to change and be built around the Employee class instead of the Address class.
        ExpressionBuilder expbldr = new ExpressionBuilder(Address.class);

        ReportQuery rq = new ReportQuery(Address.class, expbldr);
        Expression exp = expbldr.anyOf("employees");

        Expression exp2 = expbldr.get("city");
        rq.addAttribute("city", exp2);
        rq.addCount("COUNT",exp.distinct(),Long.class );
        rq.addGrouping(exp2);
        Vector<?> expectedResult = (Vector<?>) getPersistenceUnitServerSession().executeQuery(rq);
        String ejbqlString3 = "SELECT a.city, COUNT( DISTINCT e ) FROM Address a JOIN a.employees e GROUP BY a.city";
        Query q = em.createQuery(ejbqlString3);
        List<?> result = q.getResultList();

        Assert.assertTrue("Complex COUNT with Group By test failed", comparer.compareObjects(result, expectedResult));
        rollbackTransaction(em);
        closeEntityManager(em);
    }

    public void complexDistinctCountTest()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        ExpressionBuilder expbldr = new ExpressionBuilder();

        ReportQuery rq = new ReportQuery(Employee.class, expbldr);

        Expression exp = expbldr.get("lastName").equal("Smith");

        rq.setReferenceClass(Employee.class);
        rq.setSelectionCriteria(exp);
        rq.useDistinct();
        rq.returnSingleAttribute();
        rq.dontRetrievePrimaryKeys();
        rq.addCount("COUNT", expbldr.get("lastName").distinct(), Long.class);
        @SuppressWarnings({"unchecked"})
        Vector<Long> expectedResultVector = (Vector<Long>) getPersistenceUnitServerSession().executeQuery(rq);
        Long expectedResult = expectedResultVector.get(0);

        String ejbqlString = "SELECT COUNT(DISTINCT emp.lastName) FROM Employee emp WHERE emp.lastName = \"Smith\"";
        Long result = em.createQuery(ejbqlString, Long.class).getSingleResult();

        Assert.assertEquals("Complex DISTINCT COUNT test failed", expectedResult, result);
        rollbackTransaction(em);
        closeEntityManager(em);
    }

    public void complexMaxTest()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        ExpressionBuilder expbldr = new ExpressionBuilder();

        ReportQuery rq = new ReportQuery(Employee.class, expbldr);
        rq.setReferenceClass(Employee.class);
        rq.returnSingleAttribute();
        rq.dontRetrievePrimaryKeys();
        rq.addAttribute("salary", expbldr.get("salary").distinct().maximum(), Integer.class);
        @SuppressWarnings({"unchecked"})
        Vector<Number> expectedResultVector = (Vector<Number>) getPersistenceUnitServerSession().executeQuery(rq);
        Number expectedResult = expectedResultVector.get(0);

        String ejbqlString = "SELECT MAX(DISTINCT emp.salary) FROM Employee emp";
        Number result = em.createQuery(ejbqlString, Number.class).getSingleResult();

        Assert.assertEquals("Type returned was not expected", Integer.class, result.getClass());
        Assert.assertEquals("Complex MAX test failed", expectedResult, result);
        rollbackTransaction(em);
        closeEntityManager(em);
    }

    public void complexMinTest()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        ExpressionBuilder expbldr = new ExpressionBuilder();

        ReportQuery rq = new ReportQuery(Employee.class, expbldr);
        rq.setReferenceClass(Employee.class);
        rq.returnSingleAttribute();
        rq.dontRetrievePrimaryKeys();
        rq.addAttribute("salary", expbldr.get("salary").distinct().minimum(), Integer.class);
        @SuppressWarnings({"unchecked"})
        Vector<Number> expectedResultVector = (Vector<Number>) getPersistenceUnitServerSession().executeQuery(rq);
        Number expectedResult = expectedResultVector.get(0);

        String ejbqlString = "SELECT MIN(DISTINCT emp.salary) FROM Employee emp";
        Number result = em.createQuery(ejbqlString, Number.class).getSingleResult();

        Assert.assertEquals("Type returned was not expected", Integer.class, result.getClass());
        Assert.assertEquals("Complex MIN test failed", expectedResult, result);
        rollbackTransaction(em);
        closeEntityManager(em);
    }

    public void complexSumTest()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        ExpressionBuilder expbldr = new ExpressionBuilder();

        ReportQuery rq = new ReportQuery(Employee.class, expbldr);
        rq.setReferenceClass(Employee.class);
        rq.returnSingleAttribute();
        rq.dontRetrievePrimaryKeys();
        rq.addAttribute("salary", expbldr.get("salary").distinct().sum(), Long.class);
        @SuppressWarnings({"unchecked"})
        Vector<Long> expectedResultVector = (Vector<Long>) getPersistenceUnitServerSession().executeQuery(rq);
        Long expectedResult = expectedResultVector.get(0);

        String ejbqlString = "SELECT SUM(DISTINCT emp.salary) FROM Employee emp";
        Long result = em.createQuery(ejbqlString, Long.class).getSingleResult();

        Assert.assertEquals("Complex SUMtest failed", expectedResult, result);
        rollbackTransaction(em);
        closeEntityManager(em);
    }

    /**
     * Test case glassfish issue 2725:
     */
    public void complexCountDistinctOnBaseQueryClass()
    {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Long expectedResult = (long) getPersistenceUnitServerSession().readAllObjects(Employee.class).size();

        String jpql = "SELECT COUNT(DISTINCT e) FROM Employee e";
        Query q = em.createQuery(jpql);
        Long result = (Long) q.getSingleResult();

        Assert.assertEquals("Complex COUNT DISTINCT on base query class ", expectedResult, result);

        rollbackTransaction(em);
        closeEntityManager(em);
    }

    /**
     * Test case glassfish issue 2497:
     */
    public void complexCountOnJoinedVariableCompositePK()
    {
        EntityManager em = createEntityManager();

        // Need to create the expected result manually, because using the
        // TopLink query API would run into the same issue 2497.
        List<Long> expectedResult = Arrays.asList(2L, 5L, 3L);
        Collections.sort(expectedResult);

        String jpql = "SELECT COUNT(p) FROM Employee e LEFT JOIN e.phoneNumbers p WHERE e.lastName LIKE 'S%' GROUP BY e.lastName";
        TypedQuery<Long> q = em.createQuery(jpql, Long.class);
        List<Long> result = q.getResultList();
        Collections.sort(result);

        Assert.assertEquals("Complex COUNT on outer joined variable composite PK", expectedResult, result);

        // COUNT DISTINCT with inner join
        jpql = "SELECT COUNT(DISTINCT p) FROM Employee e JOIN e.phoneNumbers p WHERE e.lastName LIKE 'S%' GROUP BY e.lastName";
        q = em.createQuery(jpql, Long.class);
        result = q.getResultList();
        Collections.sort(result);

        Assert.assertEquals("Complex DISTINCT COUNT on inner joined variable composite PK", expectedResult, result);
    }

    public void complexSelectAggregateTest(){
        EntityManager em = createEntityManager();

        Expression exp = (new ExpressionBuilder()).get("firstName").equal("Bob");
        Employee employee = (Employee)getPersistenceUnitServerSession().readObject(Employee.class, exp);
        EmploymentPeriod expectedResult = employee.getPeriod();

        String jpql = "SELECT e.period from Employee e where e.firstName = 'Bob'";
        Query q = em.createQuery(jpql);
        EmploymentPeriod result = (EmploymentPeriod)q.getSingleResult();

        Assert.assertEquals("complexSelectAggregateTest failed - start dates don't match",
                            expectedResult.getStartDate(), result.getStartDate());
        Assert.assertEquals("complexSelectAggregateTest failed - end dates don't match",
                expectedResult.getEndDate(), result.getEndDate());
    }

    public void testMultipleCoalesce() {
        EntityManager em = createEntityManager();
        TypedQuery<Object[]> query = em.createQuery("SELECT SUM(COALESCE(e.roomNumber, 20)), SUM(COALESCE(e.salary, 10000)) FROM Employee e", Object[].class);
        List<Object[]> result = query.getResultList();
        Assert.assertNotNull("testMultipleCoalesce Test Failed - Unable to fetch employee data", result);
        Assert.assertFalse("testMultipleCoalesce Test Failed - Unable to fetch employee data", result.isEmpty());
        Object[] aggregateResult = result.get(0);
        Assert.assertNotEquals("testMultipleCoalesce Test Failed ", aggregateResult[0], aggregateResult[1]);
        closeEntityManager(em);
    }
}
