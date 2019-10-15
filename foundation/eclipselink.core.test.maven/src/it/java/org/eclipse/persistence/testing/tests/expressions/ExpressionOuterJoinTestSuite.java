/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.expressions;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;

public class ExpressionOuterJoinTestSuite extends TestSuite {
    protected PopulationManager manager;

    public ExpressionOuterJoinTestSuite() {
        setDescription("This suite tests expressions.");
    }

    private void addOuterJoinJoiningComplexTest() {
        ExpressionBuilder emp = new ExpressionBuilder();

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Project.class, 15);
        test.setName("OuterJoinJoiningComplexTest");
        test.setDescription("Test joining with outer joins");
        ReadAllQuery query = new ReadAllQuery(Project.class);
        query.addJoinedAttribute(emp.getAllowingNull("teamLeader"));
        test.setQuery(query);

        addTest(test);
    }

    private void addOuterJoinAcrossInheritanceTest() {
        ExpressionBuilder emp = new ExpressionBuilder();

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(org.eclipse.persistence.testing.models.inheritance.Person.class, 1);
        test.setName("OuterJoinAcrossInheritanceTest");
        test.setDescription("Test joining with outer joins across inheritance");
        ReadAllQuery query = new ReadAllQuery(org.eclipse.persistence.testing.models.inheritance.Person.class);

        //This test used to make no sense...
        //query.setSelectionCriteria(emp.getAllowingNull("representitive").get("name").equalOuterJoin("Richard"));
        query.addOrdering(emp.getAllowingNull("representitive").get("name"));
        test.setQuery(query);

        addTest(test);
    }

    private void addOuterJoinJoiningTest() {
        ExpressionBuilder emp = new ExpressionBuilder();

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 12);
        test.setName("OuterJoinJoiningTest");
        test.setDescription("Test joining with outer joins");
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.addJoinedAttribute(emp.getAllowingNull("address"));
        test.setQuery(query);

        addTest(test);
    }

    private void addOuterJoinJoiningTest2() {
        ExpressionBuilder emp = new ExpressionBuilder();

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest2(org.eclipse.persistence.testing.models.insurance.PolicyHolder.class, 4);
        test.setName("OuterJoinJoiningTest2");
        test.setDescription("Test joining with outer joins");
        ReadAllQuery query = new ReadAllQuery(org.eclipse.persistence.testing.models.insurance.PolicyHolder.class);
        query.addJoinedAttribute(emp.getAllowingNull("address"));
        test.setQuery(query);

        addTest(test);
    }

    private void addOuterJoinManyToManyTest() {
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression expression = emp.get("firstName").like("%").or(emp.anyOfAllowingNone("projects").get("description").like("%"));

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 12);
        test.setName("OuterJoinManytoManyTest");
        test.setDescription("Tests manytomany relationships with outer joins");
        test.setExpression(expression);

        addTest(test);
    }

    private void addOuterJoinOrAnyWhereClauseTest() {
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression expression = emp.get("firstName").like("Sarah%").or(emp.anyOfAllowingNone("phoneNumbers").get("areaCode").equal("613"));

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 10);
        test.setName("OuterJoinOrAnyWhereClauseTest");
        test.setDescription("Test expression anyof with outer joins");
        test.setExpression(expression);

        addTest(test);
    }

    private void addOuterJoinOrderByComplexTest() {
        ExpressionBuilder emp = new ExpressionBuilder();

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 12);
        test.setName("OuterJoinOrderByComplexTest");
        test.setDescription("Test order by with outer joins");
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.addOrdering(emp.getAllowingNull("manager").get("firstName"));
        test.setQuery(query);

        addTest(test);
    }

    private void addOuterJoinOrderByTest() {
        ExpressionBuilder emp = new ExpressionBuilder();

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 12);
        test.setName("OuterJoinOrderByTest");
        test.setDescription("Test order by with outer joins");
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.addOrdering(emp.getAllowingNull("address").get("city"));
        test.setQuery(query);

        addTest(test);
    }

    private void addOuterJoinOrWhereClauseTest1() {
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression expression = emp.get("firstName").like("Bob%").or(emp.getAllowingNull("address").get("city").like("Ot%"));

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 2);
        test.setName("OuterJoinOrWhereClauseTest1");
        test.setDescription("Test expression with outer joins");
        test.setExpression(expression);

        addTest(test);
    }

    private void addOuterJoinOrWhereClauseTest2() {
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression expression = emp.get("firstName").like("Sarah%").or(emp.getAllowingNull("manager").get("firstName").like("Sarah%"));

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 3);
        test.setName("OuterJoinOrWhereClauseTest2");
        test.setDescription("Test expression with outer joins");
        test.setExpression(expression);

        addTest(test);
    }

    private void addOuterJoinOrWhereClauseTest3() {
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression expression = emp.get("firstName").like("Sarah%").and(emp.get("lastName").like("Smit%")).or(emp.getAllowingNull("manager").get("firstName").like("Sarah%").and(emp.getAllowingNull("manager").get("lastName").like("Smit%")));

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 2);
        test.setName("OuterJoinOrWhereClauseTest3");
        test.setDescription("Test expression with outer joins");
        test.setExpression(expression);

        addTest(test);
    }

    private void addOuterJoinOrWhereClauseTest4() {
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression expression = emp.get("firstName").like("Bob%").or(emp.getAllowingNull("address").get("city").like("Ot%").and(emp.getAllowingNull("address").get("city").like("%wa")));

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 2);
        test.setName("OuterJoinOrWhereClauseTest4");
        test.setDescription("Test expression with outer joins");
        test.setExpression(expression);

        addTest(test);
    }

    private void addOuterJoinSimpleTest() {
        // This one does not really make sense, however its simple and tests that the syntax works.
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression expression = emp.getAllowingNull("address").get("city").equal("Ottawa");

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 1);
        test.setName("OuterJoinSimpleTest");
        test.setDescription("Test expression with outer joins");
        test.setExpression(expression);

        addTest(test);
    }

    private void addOuterJoinGetOnClauseTest() {
        // This one does not really make sense, however its simple and tests that the syntax works.
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression address = emp.getAllowingNull("address");
        emp.join(address, address.get("city").notEqual("Ottawa"));
        Expression expression = address.get("city").equal("Ottawa");

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 0);
        test.setName("OuterJoinGetOnClauseTest");
        test.setDescription("Test expression with outer joins and on clause");
        test.setExpression(expression);

        addTest(test);
    }

    private void addOuterJoinAnyOfOnClauseTest() {
        // This one does not really make sense, however its simple and tests that the syntax works.
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression phone = emp.anyOfAllowingNone("phoneNumbers");
        emp.join(phone, phone.get("areaCode").notEqual("613"));
        Expression expression = phone.get("areaCode").equal("613");

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 0);
        test.setName("OuterJoinAnyOfOnClauseTest");
        test.setDescription("Test expression with outer joins and on clause");
        test.setExpression(expression);

        addTest(test);
    }

    private void addJoinAnyOfOnClauseTest() {
        // This one does not really make sense, however its simple and tests that the syntax works.
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression phone = emp.anyOf("phoneNumbers");
        emp.join(phone, phone.get("areaCode").notEqual("613"));
        Expression expression = phone.get("areaCode").equal("613");

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 0);
        test.setName("JoinAnyOfOnClauseTest");
        test.setDescription("Test expression with outer joins and on clause");
        test.setExpression(expression);

        addTest(test);
    }

    private void addJoinGetOnClauseTest() {
        // This one does not really make sense, however its simple and tests that the syntax works.
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression address = emp.get("address");
        emp.join(address, address.get("city").notEqual("Ottawa"));
        Expression expression = address.get("city").equal("Ottawa");

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 0);
        test.setName("JoinGetOnClauseTest");
        test.setDescription("Test expression with outer joins and on clause");
        test.setExpression(expression);

        addTest(test);
    }

    private void addJoinOnClauseTest() {
        ExpressionBuilder emp = new ExpressionBuilder(Employee.class);
        ExpressionBuilder project = new ExpressionBuilder(Project.class);
        emp.join(project, project.get("teamLeader").equal(emp));

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 3);
        test.setName("JoinOnClauseTest");
        test.setDescription("Test expression with parrallel joins and on clause");
        test.setExpression(null);
        test.getQuery(true).setExpressionBuilder(emp);
        test.getQuery(true).addNonFetchJoin(project);

        addTest(test);
    }

    private void addOuterJoinOnClauseTest() {
        ExpressionBuilder emp = new ExpressionBuilder(Employee.class);
        ExpressionBuilder project = new ExpressionBuilder(Project.class);
        emp.leftJoin(project, project.get("teamLeader").equal(emp));

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 12);
        test.setName("OuterJoinOnClauseTest");
        test.setDescription("Test expression with parrallel joins and on clause");
        test.setExpression(null);
        test.getQuery(true).setExpressionBuilder(emp);
        test.getQuery(true).addNonFetchJoin(project);

        addTest(test);
    }

    private void addOuterJoinDirectCollectionTest() {
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression expression = emp.get("firstName").equal("Nancy").or(emp.anyOfAllowingNone("responsibilitiesList").equal("Write lots of Java code."));

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 2);
        test.setName("OuterJoinDirectCollectionTest");
        test.setDescription("Tests direct collection relationships with outer joins");
        test.setExpression(expression);

        addTest(test);
    }

    private void addOuterJoinIsNullTest() {
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression expression =
        emp.get("firstName").equal("Bob").or(emp.getAllowingNull("address").isNull()).or
            (emp.getAllowingNull("address").get("city").equal("Ottawa"));

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 2);
        test.setName("OuterJoinIsNullTest");
        test.setDescription("Test using isNull with outer joins");
        test.setExpression(expression);

        addTest(test);

    }

    private void addOuterJoinParallelExpressionTest() {
        ExpressionBuilder emp = new ExpressionBuilder(Employee.class);
        ExpressionBuilder addr = new ExpressionBuilder(Address.class);
        Expression expression =
        emp.get("firstName").equal("Bob").or(emp.getAllowingNull("address").get("city").equal("Ottawa")).or
        (emp.getAllowingNull("address").equal(addr).and(addr.get("city").equal("Ottawa")));

        ReadAllExpressionTest test = new ReadAllOuterJoinExpressionTest(Employee.class, 22);
        test.setName("OuterJoinParallelExpressionTest");
        test.setDescription("Test using isNull with outer joins");
        test.setExpression(expression);

        addTest(test);

    }

    public void addTests() {
        setManager(PopulationManager.getDefaultManager());
        addOuterJoinSimpleTest();
        addOuterJoinOrWhereClauseTest1();
        addOuterJoinOrWhereClauseTest2();
        addOuterJoinOrWhereClauseTest3();
        addOuterJoinOrWhereClauseTest4();
        addOuterJoinOrderByTest();
        addOuterJoinOrderByComplexTest();
        addOuterJoinJoiningTest();
        addOuterJoinManyToManyTest();
        addOuterJoinJoiningTest2();
        addOuterJoinJoiningComplexTest();
        addOuterJoinOrAnyWhereClauseTest();
        addOuterJoinAcrossInheritanceTest();
        addOuterJoinDirectCollectionTest();
        addOuterJoinParallelExpressionTest();
        addOuterJoinIsNullTest();
        addOuterJoinGetOnClauseTest();
        addJoinGetOnClauseTest();
        addOuterJoinAnyOfOnClauseTest();
        addJoinAnyOfOnClauseTest();
        addOuterJoinOnClauseTest();
        addJoinOnClauseTest();
    }

    protected PopulationManager getManager() {
        return manager;
    }

    protected void setManager(PopulationManager theManager) {
        manager = theManager;
    }
}
