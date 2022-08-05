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
package org.eclipse.persistence.testing.tests.jpa.jpql.relationships;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.relationships.Customer;
import org.eclipse.persistence.testing.models.jpa.relationships.CustomerDetails;
import org.eclipse.persistence.testing.models.jpa.relationships.Order;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsExamples;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsTableManager;
import org.eclipse.persistence.testing.models.jpa.relationships.SalesPerson;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitDomainObjectComparer;
import org.junit.Assert;

import java.util.List;

public class JUnitJPQLExamplesTest extends JUnitTestCase {

    private static JUnitDomainObjectComparer comparer;

    public JUnitJPQLExamplesTest() {
        super();
    }

    public JUnitJPQLExamplesTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "relationships";
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
        suite.addTest(new JUnitJPQLExamplesTest("findAllOrders"));
        suite.addTest(new JUnitJPQLExamplesTest("findOrdersWithDifferentBilledCustomer"));
        suite.addTest(new JUnitJPQLExamplesTest("getOrderLargerThan"));
        suite.addTest(new JUnitJPQLExamplesTest("getSalesPersonForOrders"));
        suite.addTest(new JUnitJPQLExamplesTest("getOrderForCustomer"));
        suite.addTest(new JUnitJPQLExamplesTest("testCountInSubQuery"));
        suite.addTest(new JUnitJPQLExamplesTest("testConstructorQuery"));
        suite.addTest(new JUnitJPQLExamplesTest("testAvgExpression"));
        suite.addTest(new JUnitJPQLExamplesTest("testCountInSubQuery"));
        suite.addTest(new JUnitJPQLExamplesTest("testDeleteExpression"));
        suite.addTest(new JUnitJPQLExamplesTest("testComplexDeleteExpression"));

        suite.addTest(new JUnitJPQLExamplesTest("testUpdateExpression"));   //bug 5159164, 5159198
        //Bug5040609
        suite.addTest(new JUnitJPQLExamplesTest("namedQueryCloneTest"));

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

        new RelationshipsTableManager().replaceTables(session);

        //populate the relationships model and persist as well
        RelationshipsExamples relationshipExamples = new RelationshipsExamples();
        relationshipExamples.buildExamples(session);
    }

    public void findAllOrders() {
        EntityManager em = createEntityManager();
        List<?> expectedResult = getPersistenceUnitServerSession().readAllObjects(Order.class);

        String ejbqlString = "SELECT o FROM OrderBean o";
        List<?> result = em.createQuery(ejbqlString).getResultList();
        // 4 orders returned
        Assert.assertEquals("Find all orders test failed: data validation error", result.size(), 4);
        Assert.assertTrue("Find all orders test failed", comparer.compareObjects(expectedResult, result));
    }

    public void findOrdersWithDifferentBilledCustomer() {
        EntityManager em = createEntityManager();
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("customer").equal(builder.get("billedCustomer")).not();

        ReadAllQuery raq = new ReadAllQuery(Order.class);
        raq.setSelectionCriteria(whereClause);

        List<?> expectedResult = (List<?>)getPersistenceUnitServerSession().executeQuery(raq);

        String ejbqlString = "SELECT o FROM OrderBean o WHERE o.customer <> o.billedCustomer";
        List<?> firstResult = em.createQuery(ejbqlString).getResultList();

        String alternateEjbqlString = "SELECT o FROM OrderBean o WHERE NOT o.customer.customerId = o.billedCustomer.customerId";
        List<?> secondResult = em.createQuery(alternateEjbqlString).getResultList();
        //2 orders returned
        Assert.assertTrue("Find orders with different billed customers test failed: two equivalent ejb queries return different results", comparer.compareObjects(secondResult, firstResult));
        Assert.assertTrue("Find orders with different billed customers test failed", comparer.compareObjects(expectedResult, firstResult));
    }

    public void getOrderLargerThan() {
        EntityManager em = createEntityManager();

        ExpressionBuilder builder1 = new ExpressionBuilder(Order.class);
        ExpressionBuilder builder2 = new ExpressionBuilder(Order.class);
        Expression o1Quantity = builder1.get("quantity");
        Expression o2Quantity = builder2.get("quantity");
        Expression quantityComparison = o1Quantity.greaterThan(o2Quantity);
        Expression o2CustomerName = builder2.get("customer").get("name");
        Expression nameComparison = o2CustomerName.equal("Jane Smith");
        Expression whereClause = quantityComparison.and(nameComparison);

        ReadAllQuery raq = new ReadAllQuery();
        raq.setSelectionCriteria(whereClause);
        raq.setReferenceClass(Order.class);
        raq.useDistinct();
        List<?> expectedResult = (List<?>)getPersistenceUnitServerSession().executeQuery(raq);

        String ejbqlString = "SELECT DISTINCT o1 FROM OrderBean o1, OrderBean o2 WHERE o1.quantity > o2.quantity AND" + " o2.customer.name = 'Jane Smith' ";
        List<?> result = em.createQuery(ejbqlString).getResultList();
        //only 1 order
        Assert.assertEquals("Get order larger than test failed: data validation error", result.size(), 1);
        Assert.assertTrue("Get order larger than test failed", comparer.compareObjects(expectedResult, result));
    }

    public void getOrderForCustomer() {
        EntityManager em = createEntityManager();
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("name").equal("Jane Smith");

        ReadAllQuery raq = new ReadAllQuery(Customer.class);
        raq.setSelectionCriteria(whereClause);

        Customer expectedCustomer = (Customer)(((List<?>)getPersistenceUnitServerSession().executeQuery(raq)).get(0));
        SalesPerson salesPerson = expectedCustomer.getOrders().iterator().next().getSalesPerson();

        String ejbqlString = "SELECT DISTINCT c FROM Customer c JOIN c.orders o JOIN o.salesPerson s WHERE s.id = " + salesPerson.getId();
        List<?> firstResult = em.createQuery(ejbqlString).getResultList();
        String alternateEjbqlString = "SELECT DISTINCT c FROM Customer c, IN(c.orders) o WHERE o.salesPerson.id = " + salesPerson.getId();
        List<?> secondResuslt = em.createQuery(alternateEjbqlString).getResultList();

        //only 1 order for this customer
        Assert.assertEquals("Get order for customer test failed: data validation error", firstResult.size(), 1);
        Assert.assertTrue("Get order for customer test failed: two equivalent ejb queries return different results", comparer.compareObjects(secondResuslt, firstResult));
        Assert.assertTrue("Get order for customer test failed", comparer.compareObjects(expectedCustomer, firstResult));
    }

    public void getSalesPersonForOrders() {
        EntityManager em = createEntityManager();

        List<?> expectedResult = getPersistenceUnitServerSession().readAllObjects(SalesPerson.class);

        String ejbqlString = "SELECT DISTINCT o.salesPerson FROM Customer AS c, IN(c.orders) o";
        List<?> result = em.createQuery(ejbqlString).getResultList();
        //2 sales person
        Assert.assertEquals("Get SalesPerson for Orders test failed: data validation error", result.size(), 2);
        Assert.assertTrue("Get SalesPerson for Orders test failed", comparer.compareObjects(expectedResult, result));
    }

    public void testCountInSubQuery() {
        EntityManager em = createEntityManager();
        boolean testPass = false;
        ReportQuery mainQuery = new ReportQuery(Customer.class, new ExpressionBuilder());
        ReportQuery subQuery = new ReportQuery(Order.class, new ExpressionBuilder());
        subQuery.setSelectionCriteria(subQuery.getExpressionBuilder().get("customer").get("customerId").equal(mainQuery.getExpressionBuilder().get("customerId")));
        subQuery.addCount("orderId");
        mainQuery.setSelectionCriteria(mainQuery.getExpressionBuilder().subQuery(subQuery).greaterThan(0));
        mainQuery.addAttribute("customerId");
        mainQuery.returnWithoutReportQueryResult();
        List<?> expectedResult = (List<?>)getPersistenceUnitServerSession().executeQuery(mainQuery);
        String ejbqlString = "SELECT c.customerId FROM Customer c WHERE (SELECT COUNT(o) FROM c.orders o) > 0";
        List<?> result = em.createQuery(ejbqlString).getResultList();
        if (result.containsAll(expectedResult) && expectedResult.containsAll(result))
            testPass = true;

        Assert.assertEquals("Count subquery test failed: data validation error", result.size(), 2);
        Assert.assertTrue("Count subquery test failed", testPass);
    }

    public void testConstructorQuery() {
        EntityManager em = createEntityManager();
        String ejbqlString = "SELECT NEW org.eclipse.persistence.testing.models.jpa.relationships.CustomerDetails(c.customerId, o.quantity) FROM Customer " + "c JOIN c.orders o WHERE o.quantity > 100";

        List<?> custDetails = em.createQuery(ejbqlString).getResultList();
        Assert.assertTrue("Constructor query test failed: not an instance of CustomerDetail", custDetails.get(0) instanceof CustomerDetails);
        Assert.assertEquals("Constructor query test failed, expecting only 1 customer with order > 100", custDetails.size(), 1);
    }

    public void testAvgExpression() {
        EntityManager em = createEntityManager();

        ReportQuery query = new ReportQuery(Order.class, new ExpressionBuilder());
        query.addAverage("average quantity", query.getExpressionBuilder().get("quantity"), Double.class);
        query.returnSingleResult();
        Double expectedResult = (Double)((ReportQueryResult)getPersistenceUnitServerSession().executeQuery(query)).get("average quantity");
        String ejbqlString = "SELECT AVG(o.quantity) FROM OrderBean o";
        Double result = (Double)em.createQuery(ejbqlString).getSingleResult();
        Assert.assertEquals("Average expression test failed", expectedResult, result);
    }

    public void testDeleteExpression() {
        if (isOnServer()) {
            // Not work on server.
            return;
        }
        if (getPersistenceUnitServerSession().getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test testDeleteExpression skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }

        JpaEntityManager em = (org.eclipse.persistence.jpa.JpaEntityManager)createEntityManager();
        try {
            beginTransaction(em);
            String orderString = "DELETE FROM OrderBean o WHERE o.customer.name ='Karen McDonald' ";
            em.createQuery(orderString).executeUpdate();
            orderString = "DELETE FROM OrderBean o WHERE o.billedCustomer.name ='Karen McDonald' ";
            em.createQuery(orderString).executeUpdate();
            String ejbqlString = "DELETE FROM Customer c WHERE c.name='Karen McDonald' ";
            int result = em.createQuery(ejbqlString).executeUpdate();
            Assert.assertEquals("Delete Expression test failed: customer to delete not found", 1, result);
            em.flush();

            ReadAllQuery raq = new ReadAllQuery(Customer.class, new ExpressionBuilder());
            Expression whereClause = raq.getExpressionBuilder().get("name").equal("Karen McDonald");
            raq.setSelectionCriteria(whereClause);
            List<?> customerFound = (List<?>)em.getActiveSession().executeQuery(raq);
            Assert.assertEquals("Delete Expression test failed", 0, customerFound.size());
        } finally {
            rollbackTransaction(em);
        }
    }

    public void testComplexDeleteExpression() {
        if (isOnServer()) {
            // Not work on server.
            return;
        }
        if (getPersistenceUnitServerSession().getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test testComplexDeleteExpression skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }

        JpaEntityManager em = (org.eclipse.persistence.jpa.JpaEntityManager)createEntityManager();
        try {
            beginTransaction(em);
            String orderString = "DELETE FROM OrderBean o WHERE o.customer.name ='Karen McDonald' ";
            em.createQuery(orderString).executeUpdate();
            orderString = "DELETE FROM OrderBean o WHERE o.billedCustomer.name ='Karen McDonald' ";
            em.createQuery(orderString).executeUpdate();
            String ejbqlString = "DELETE FROM Customer c WHERE c.name='Karen McDonald' AND c.orders IS EMPTY";
            int result = em.createQuery(ejbqlString).executeUpdate();
            Assert.assertEquals("Complex Delete Expression test failed: customer to delete not found", 1, result);
            em.flush();

            ReadAllQuery raq = new ReadAllQuery(Customer.class, new ExpressionBuilder());
            Expression whereClause1 = raq.getExpressionBuilder().get("name").equal("Karen McDonald");
            Expression whereClause2 = raq.getExpressionBuilder().isEmpty("orders");
            raq.setSelectionCriteria(whereClause1.and(whereClause2));
            List<?> customerFound = (List<?>)em.getActiveSession().executeQuery(raq);
            Assert.assertEquals("Complex Delete Expression test failed", 0, customerFound.size());
        } finally {
            rollbackTransaction(em);
        }
    }

    //bug 5159164, 5159198

    public void testUpdateExpression() {
        if (getPersistenceUnitServerSession().getPlatform().isSymfoware()) {
            getPersistenceUnitServerSession().logMessage("Test testUpdateExpression skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
            return;
        }
        EntityManager em = createEntityManager();
        Number result = null;
        beginTransaction(em);
        try {
            String ejbqlString = "UPDATE Customer c SET c.name = 'Test Case' WHERE c.name = 'Jane Smith' " + "AND 0 < (SELECT COUNT(o) FROM Customer cust JOIN cust.orders o)";
            result = em.createQuery(ejbqlString).executeUpdate();
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }

        Assert.assertEquals("Update expression test failed", 1, result);
    }

    //Bug5040609  Test if EJBQuery makes a clone of the original DatabaseQuery from the session

    public void namedQueryCloneTest() {
        EntityManager em = createEntityManager();

        List<?> result1 = em.createNamedQuery("findAllCustomers").getResultList();

        List<?> result2 = em.createNamedQuery("findAllCustomers").setMaxResults(1).getResultList();

        List<?> result3 = em.createNamedQuery("findAllCustomers").getResultList();

        Assert.assertEquals("Named query clone test failed: the first result should be 4", result1.size(), 4);
        Assert.assertEquals("Named query clone test failed: the second result should be 1", result2.size(), 1);
        Assert.assertEquals("Named query clone test failed: the third result should be 4", result3.size(), 4);
    }
}
