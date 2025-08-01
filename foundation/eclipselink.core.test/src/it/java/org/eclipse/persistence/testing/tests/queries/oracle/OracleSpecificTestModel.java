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
package org.eclipse.persistence.testing.tests.queries.oracle;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.models.inheritance.InheritanceSystem;
import org.eclipse.persistence.testing.models.mapping.Employee;
import org.eclipse.persistence.testing.models.mapping.MappingSystem;
import org.eclipse.persistence.testing.models.mapping.Phone;
import org.eclipse.persistence.testing.tests.proxyindirection.ProxyIndirectionSystem;

public class OracleSpecificTestModel extends TestModel {
    public OracleSpecificTestModel() {
        setDescription("This model tests the use of Oracle Hints and Hierarchical Queries on Oracle databases");

    }

    @Override
    public void addRequiredSystems() {
        if ((getSession().getPlatform().isOracle())) {
            addRequiredSystem(new MappingSystem());
            addRequiredSystem(new ProxyIndirectionSystem());
            addRequiredSystem(new InheritanceSystem());
        }
    }

    @Override
    public void addTests() {
        if ((getSession().getPlatform().isOracle())) {
            addTest(getOracleHintsTestSuite());
            addTest(getHierarchicalQueriesTestSuite());
        }
    }

    public static TestSuite getOracleHintsTestSuite() {
        TestSuite testSuite = new TestSuite();
        testSuite.setName("OracleHintsTestSuite");
        testSuite.setDescription("Tests the Oracle Hints feature");
        ReadAllQuery raq = new ReadAllQuery(Employee.class);

        TestCase tc1 = new BasicReadTest(raq);
        tc1.setName("ReadAllTest");
        tc1.setDescription("Tests hints on a ReadAllQuery");
        testSuite.addTest(tc1);

        ReadObjectQuery roq = new ReadObjectQuery(Employee.class);
        Employee emp = new Employee();
        emp.firstName = "Bill";
        roq.setExampleObject(emp);
        TestCase tc2 = new BasicReadTest(roq);
        tc2.setName("QBE Test");
        tc2.setDescription("Tests hints using a Query By Example");
        testSuite.addTest(tc2);

        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder raqb = new ExpressionBuilder(Employee.class);
        ExpressionBuilder rqb = new ExpressionBuilder();
        ReportQuery rq = new ReportQuery(Phone.class, rqb);
        BasicReadTest test = new BasicReadTest(query);
        Expression exp = rqb.get("areaCode").equal(raqb.get("lastName"));
        rq.setSelectionCriteria(exp);
        rq.addAttribute("areaCode");
        rq.setHintString(BasicReadTest.INNER_HINT);
        Expression expression = raqb.get("lastName").in(rq);
        query.setSelectionCriteria(expression);
        test.setName("HintsInSubselectsTest");
        test.setDescription("Test hints with subselects");
        testSuite.addTest(test);

        ExpressionBuilder eb = new ExpressionBuilder();
        ReadAllQuery query2 = new ReadAllQuery(Employee.class);
        Expression expr1 = eb.get("firstName").like("%av%");
        Expression expr2 = eb.get("cubicle").get("location").equal("2nd floor, Section P, Close to the Middle");
        Expression expr3 = expr1.and(expr2);
        query2.setSelectionCriteria(expr3);
        BasicReadTest test2 = new BasicReadTest(query2);
        test2.setName("Complex Query Test");
        test2.setDescription("Test hints in a complex query");
        testSuite.addTest(test2);

        testSuite.addTest(new UpdateTestCase());
        testSuite.addTest(new DeleteTestCase());
        testSuite.addTest(new ChangedHintStringTest());
        return testSuite;
    }

    public static TestSuite getHierarchicalQueriesTestSuite() {
        TestSuite testSuite = new TestSuite();
        testSuite.setName("HierarchicalQueriesTestSuite");

        testSuite.addTest(new ReadWholeHierarchyTest());
        testSuite.addTest(new HierarchicalQueryWithWhere());
        testSuite.addTest(new OrderSiblingsTest());
        testSuite.addTest(new JoinInWhereClauseTest());
        testSuite.addTest(new HierarchicalOneToOneTest());
        testSuite.addTest(new HierarchicalOneToOneInverseTest());
        testSuite.addTest(new HierarchicalOneToManyTest());
        testSuite.addTest(new HierarchicalOneToManyInverseTest());
        testSuite.addTest(new IndirectionTest());
        testSuite.addTest(new HierarchicalQueryWithInheritenceTest());

        return testSuite;
    }
}
