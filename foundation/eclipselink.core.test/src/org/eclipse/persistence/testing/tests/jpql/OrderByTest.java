/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpql;

import java.util.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class OrderByTest extends JPQLTestCase {
    Collection orderingExpressions = null;

    public static OrderByTest getSimpleOrderByTestAscDirection() {
        OrderByTest test = new OrderByTest();
        test.setName("Simple Order By Test Asc Direction");

        ExpressionBuilder employee = new ExpressionBuilder();
        Expression whereClause = employee.get("firstName").notEqual("XXX");

        test.setOriginalObjectExpression(whereClause);

        test.addOrderingExpression(employee.get("lastName").ascending());

        String ejbqlString;

        ejbqlString = "SELECT OBJECT(e) FROM Employee e WHERE e.firstName <> ";
        ejbqlString = ejbqlString + "\"XXX\" ";
        ejbqlString = ejbqlString + "ORDER BY e.lastName ASC";

        test.setEjbqlString(ejbqlString);

        return test;
    }

    public static OrderByTest getSimpleOrderByTestDescDirection() {
        OrderByTest test = new OrderByTest();
        test.setName("Simple Order By Test Desc Direction");

        ExpressionBuilder employee = new ExpressionBuilder();
        Expression whereClause = employee.get("firstName").notEqual("XXX");

        test.setOriginalObjectExpression(whereClause);

        test.addOrderingExpression(employee.get("lastName").descending());

        String ejbqlString;

        ejbqlString = "SELECT OBJECT(e) FROM Employee e WHERE e.firstName <> ";
        ejbqlString = ejbqlString + "\"XXX\" ";
        ejbqlString = ejbqlString + "ORDER BY e.lastName DESC";

        test.setEjbqlString(ejbqlString);

        return test;
    }

    // The default should be ascending when not specified explicitly.
    public static OrderByTest getSimpleOrderByTestNoDirection() {
        OrderByTest test = new OrderByTest();
        test.setName("Simple Order By Test No Direction");

        ExpressionBuilder employee = new ExpressionBuilder();
        Expression whereClause = employee.get("firstName").notEqual("XXX");

        test.setOriginalObjectExpression(whereClause);

        test.addOrderingExpression(employee.get("lastName"));

        String ejbqlString;

        ejbqlString = "SELECT OBJECT(e) FROM Employee e WHERE e.firstName <> ";
        ejbqlString = ejbqlString + "\"XXX\" ";
        ejbqlString = ejbqlString + "ORDER BY e.lastName";

        test.setEjbqlString(ejbqlString);

        return test;
    }

    public static OrderByTest getComplexOrderByNoDirection() {
        OrderByTest test = new OrderByTest();
        test.setName("Complex Order By No Direction");

        ExpressionBuilder employee = new ExpressionBuilder();
        Expression whereClause = employee.get("firstName").notEqual("XXX");

        test.setOriginalObjectExpression(whereClause);

        test.addOrderingExpression(employee.get("lastName"));
        test.addOrderingExpression(employee.get("firstName"));

        String ejbqlString;

        ejbqlString = "SELECT OBJECT(e) FROM Employee e WHERE e.firstName <> ";
        ejbqlString = ejbqlString + "\"XXX\" ";
        ejbqlString = ejbqlString + "ORDER BY e.lastName, e.firstName";

        test.setEjbqlString(ejbqlString);

        return test;
    }

    public static OrderByTest getComplexOrderByAscDirection() {
        OrderByTest test = new OrderByTest();
        test.setName("Complex Order By ASC Direction");

        ExpressionBuilder employee = new ExpressionBuilder();
        Expression whereClause = employee.get("firstName").notEqual("XXX");

        test.setOriginalObjectExpression(whereClause);

        test.addOrderingExpression(employee.get("lastName").ascending());
        test.addOrderingExpression(employee.get("firstName").ascending());

        String ejbqlString;

        ejbqlString = "SELECT OBJECT(e) FROM Employee e WHERE e.firstName <> ";
        ejbqlString = ejbqlString + "\"XXX\" ";
        ejbqlString = ejbqlString + "ORDER BY e.lastName ASC, e.firstName ASC";

        test.setEjbqlString(ejbqlString);

        return test;
    }

    public static OrderByTest getComplexOrderByDescDirection() {
        OrderByTest test = new OrderByTest();
        test.setName("Complex Order By Desc Direction");

        ExpressionBuilder employee = new ExpressionBuilder();
        Expression whereClause = employee.get("firstName").notEqual("XXX");

        test.setOriginalObjectExpression(whereClause);

        test.addOrderingExpression(employee.get("lastName").descending());
        test.addOrderingExpression(employee.get("firstName").descending());

        String ejbqlString;

        ejbqlString = "SELECT OBJECT(e) FROM Employee e WHERE e.firstName <> ";
        ejbqlString = ejbqlString + "\"XXX\" ";
        ejbqlString = ejbqlString + "ORDER BY e.lastName DESC, e.firstName DESC";

        test.setEjbqlString(ejbqlString);

        return test;
    }

    public static OrderByTest getComplexOrderByMixedDirection() {
        OrderByTest test = new OrderByTest();
        test.setName("Complex Order By Mixed Direction");

        ExpressionBuilder employee = new ExpressionBuilder();
        Expression whereClause = employee.get("firstName").notEqual("XXX");

        test.setOriginalObjectExpression(whereClause);

        test.addOrderingExpression(employee.get("lastName").ascending());
        test.addOrderingExpression(employee.get("firstName").descending());

        String ejbqlString;

        ejbqlString = "SELECT OBJECT(e) FROM Employee e WHERE e.firstName <> ";
        ejbqlString = ejbqlString + "\"XXX\" ";
        ejbqlString = ejbqlString + "ORDER BY e.lastName ASC, e.firstName DESC";

        test.setEjbqlString(ejbqlString);

        return test;
    }

    public static TestSuite getOrderByTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Order By Test Suite");

        suite.addTest(getSimpleOrderByTestAscDirection());
        suite.addTest(getSimpleOrderByTestDescDirection());
        suite.addTest(getSimpleOrderByTestNoDirection());
        suite.addTest(getComplexOrderByNoDirection());
        suite.addTest(getComplexOrderByAscDirection());
        suite.addTest(getComplexOrderByDescDirection());
        suite.addTest(getComplexOrderByMixedDirection());

        return suite;
    }

    private void addOrderingExpression(Expression newExpression) {
        getOrderingExpressions().add(newExpression);
    }

    public void setup() {
        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(getOriginalObjectExpression());

        Iterator iter = getOrderingExpressions().iterator();
        while (iter.hasNext()) {
            raq.addOrdering((Expression)iter.next());
        }

        setOriginalOject(getSession().executeQuery(raq));
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        super.setup();
    }

    private Collection getOrderingExpressions() {
        if (orderingExpressions == null) {
            setOrderingExpression(new Vector());
        }
        return orderingExpressions;
    }

    private void setOrderingExpression(Collection newExpressions) {
        orderingExpressions = newExpressions;
    }
}
