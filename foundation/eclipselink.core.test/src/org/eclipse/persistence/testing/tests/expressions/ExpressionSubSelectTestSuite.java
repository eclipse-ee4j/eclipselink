/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.expressions;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.platform.database.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.testing.models.inheritance.Vehicle;

public class ExpressionSubSelectTestSuite extends TestSuite {
    protected PopulationManager manager;

    public ExpressionSubSelectTestSuite() {
        setDescription("This suite tests expressions.");
    }

    /**
     * For bug 3105559 Object comparisons do not work with aggregate objects (subtitle:
     * because aggregate objects don't have a primary key).
     */
    private void addAggregateObjectJoiningTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ExpressionBuilder parallelBuilder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.get("period").equal(parallelBuilder.get("period")).and(builder.notEqual(parallelBuilder));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 9);
        test.setExpression(expression);
        test.getQuery(true).useDistinct();
        test.setName("AggregateObjectJoiningTest");
        test.setDescription("Test .period.equal(parallelBuilder.period).  a.k.a. employees with same employment  period as someone else.");
        addTest(test);
    }

    /**
     * For bug 3105559 Object comparisons do not work with aggregate objects (subtitle:
     * because aggregate objects don't have a primary key).
     */
    private void addAggregateObjectComparisonTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ExpressionBuilder parallelBuilder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.notEqual(parallelBuilder).and(builder.get("period").get("startDate").equal(parallelBuilder.get("period").get("startDate")).and(builder.get("period").get("endDate").equal(parallelBuilder.get("period").get("endDate"))));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 9);
        test.setExpression(expression);
        test.getQuery(true).useDistinct();
        test.setName("AggregateObjectComparisonTest");
        test.setDescription("Test .period.startDate.equal(parallelBuilder.period.startDate).and(...)  a.k.a. employees with same employment  period as someone else.");
        addTest(test);
    }

    private void addCorrelatedSubSelectTest() {
        ExpressionBuilder manager = new ExpressionBuilder(Employee.class);
        Expression employee = manager.anyOf("managedEmployees");

        Expression outerExpression = employee.anyOf("projects").get("name").equal("Problem Reporter");

        ReportQuery subQuery = new ReportQuery(Project.class, new ExpressionBuilder());
        subQuery.addCount();

        ExpressionBuilder swirlyDirlProject = subQuery.getExpressionBuilder();
        Expression correlateExpression = swirlyDirlProject.equal(employee.anyOf("projects"));
        Expression innerExpression = swirlyDirlProject.get("name").in(new Object[] { "Swirly Dirly", "Swirly Dirl" });

        subQuery.setSelectionCriteria(correlateExpression.and(innerExpression));

        Expression expression = outerExpression.and(manager.subQuery(subQuery).greaterThan(0));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("Correlated SubSelect Test");
        test.setDescription("Tests correlated subselects, using attributes selected in enclosing query to refine the subquery.  In this test find managers with an employee working on the problem reporter project.  Refine the query with a subselect to check if this employee also works on the Swirly Dirly project.");
        test.addUnsupportedPlatform(TimesTenPlatform.class);
        addTest(test);
    }

    /**
     * this test added for bug 3252935.  When fixed the workaround should be
     * removed from this test (i.e. query.usedDistinct()).
     */
    private void addSameManagerTest()
    {
            ExpressionBuilder emp = new ExpressionBuilder();
            ExpressionBuilder coworker = new ExpressionBuilder(Employee.class);

            Expression exp = emp.get("manager").equal(coworker.get("manager")).and(emp.notEqual(coworker));

            ReadAllQuery query = new ReadAllQuery(Employee.class, exp);
            query.useDistinct();

            ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 5);
            test.setExpression(exp);
            test.setQuery(query);
            test.setName("SameManagerTest");
            test.setDescription("Tests whether a parallel select will use distinct.");
            addTest(test);
    }
    
    /**
     * Test that outer expressions are normalized inside sub-selects correctly.
     */
    private void addParallelCorrelatedSubSelectsTest() {
        ExpressionBuilder manager = new ExpressionBuilder(Employee.class);
        Expression employee = manager.anyOf("managedEmployees");
        Expression outerAddress = employee.get("address");

        Expression expression = null;
        String[] cities = new String[] { "Ottawa", "Vancouver" };
        for (int i = 0; i < cities.length; i++) {
            ReportQuery subQuery = new ReportQuery(Address.class, new ExpressionBuilder(Address.class));

            ExpressionBuilder address = subQuery.getExpressionBuilder();
            subQuery.addAttribute("fish", address.value(1));

            Expression correlateExpression = address.equal(outerAddress);
            Expression innerExpression = address.get("city").equal(cities[i]);
            subQuery.setSelectionCriteria(correlateExpression.and(innerExpression));

            expression = manager.exists(subQuery).or(expression);
        }
        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(expression);
        test.getQuery(true).addNonFetchJoin(employee);
        test.setName("Parallel Correlated SubSelect Test");
        test.setDescription("Finds managers with an employee in Ottawa or an employee in Vancouver.  For bug 3381830.");
        addTest(test);
    }

    private void addCorrelatedSubSelectExpressionsReorderedTest() {
        ExpressionBuilder manager = new ExpressionBuilder(Employee.class);
        Expression employee = manager.anyOf("managedEmployees");

        Expression outerExpression = employee.anyOf("projects").get("name").equal("Problem Reporter");

        ReportQuery subQuery = new ReportQuery(Project.class, new ExpressionBuilder());
        subQuery.addCount();

        ExpressionBuilder swirlyDirlProject = subQuery.getExpressionBuilder();
        Expression correlateExpression = swirlyDirlProject.equal(employee.anyOf("projects"));
        Expression innerExpression = swirlyDirlProject.get("name").in(new Object[] { "Swirly Dirly", "Swirly Dirl" });

        subQuery.setSelectionCriteria(correlateExpression.and(innerExpression));

        // This is the only change from CorrelatedSubSelectExpressionsTest...
        Expression expression = manager.subQuery(subQuery).greaterThan(0).and(outerExpression);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("Correlated SubSelect Expressions Reordered Test");
        test.setDescription("Tests correlated subselects, with the expressions to be correlated normalized by the subselect first.");
        test.addUnsupportedPlatform(TimesTenPlatform.class);
        addTest(test);
    }

    private void addObjectEqualOrObjectEqualTest() {
        ExpressionBuilder employee = new ExpressionBuilder(Employee.class);
        Expression employeeProject = employee.anyOf("projects");

        ExpressionBuilder project = new ExpressionBuilder(Project.class);
        ReportQuery subQuery = new ReportQuery(Project.class, project);
        subQuery.addAttribute("id");
        Expression condition1 = project.equal(employeeProject).and(project.get("name").equal("Problem Reporter"));
        Expression condition2 = project.equal(employeeProject).and(project.get("name").equal("Sales Reporter"));

        subQuery.setSelectionCriteria(condition1.or(condition2));
        Expression expression = employee.exists(subQuery);
        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 4);
        test.setExpression(expression);
        test.setName("Object Equal Or Object Equal Test");
        test.setDescription("Finds employees working on one of two projects.  Trying to break CR2456 with bizarre expression structure.");
        addTest(test);
    }

    /*
     * The following test is from the Oracle 9i online SQL reference.
     * It selects all employees who make more than the average for their region (or
     * department, etc.).
     */
    private void addOracleExampleCorrelatedSubSelectTest() {
        ExpressionBuilder wellpaidEmployee = new ExpressionBuilder(Employee.class);
        ExpressionBuilder averageEmployee = new ExpressionBuilder(Employee.class);

        ReportQuery subQuery = new ReportQuery(Employee.class, averageEmployee);
        subQuery.addAverage("salary");
        subQuery.setSelectionCriteria(averageEmployee.get("address").get("province").equal(wellpaidEmployee.get("address").get("province")));

        Expression expression = wellpaidEmployee.subQuery(subQuery).lessThan(wellpaidEmployee.get("salary"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(expression);
        test.setName("Oracle Example Correlated SubSelect Test");
        test.setDescription("Tests correlated subselects, finds all employees who make more on average than other employees in their city.");
        test.addUnsupportedPlatform(TimesTenPlatform.class);
        addTest(test);
    }

    private void addParralelSelectCityTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ExpressionBuilder otherBuilder = new ExpressionBuilder(Address.class);

        Expression expression = builder.get("address").get("city").equal(otherBuilder.get("city")).and(otherBuilder.get("country").equalsIgnoreCase("canada"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setExpression(expression);
        test.setName("ParralelSelectCityTest");
        test.setDescription("Test using two object builds in one expression");
        addTest(test);
    }

    private void addParralelSelectSameCityTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ExpressionBuilder otherBuilder = new ExpressionBuilder(Employee.class);

        Expression expression = builder.get("address").get("city").equal(otherBuilder.get("address").get("city")).and(builder.notEqual(otherBuilder));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("ParralelSelectSameCityTest");
        test.setDescription("Test using two object builds in one expression");
        addTest(test);
    }

    private void addParralelSelectObjectComparisonTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ExpressionBuilder otherBuilder = new ExpressionBuilder(LargeProject.class);
        Expression expression = builder.anyOf("managedEmployees").equal(otherBuilder.get("teamLeader"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);

        test.setExpression(expression);
        test.setName("ParralelSelectObjectComparisonTest");
        test.setDescription("Test using two object builders in one expression and comparing with an object comparison");
        addTest(test);
    }

    /**
     * @bug  2639318 PARALLEL EXPRESSION INSIDE SUBSELECT OMITS SECOND BUILDER
     */
    private void addParralelSelectSameNameInsideSubSelectTest() {
        ExpressionBuilder innerBuilder = new ExpressionBuilder(Employee.class);
        ExpressionBuilder otherBuilder = new ExpressionBuilder(Employee.class);
        ExpressionBuilder outerBuilder = new ExpressionBuilder(Employee.class);

        Expression parallelExpression = innerBuilder.get("lastName").equal(otherBuilder.get("lastName")).and(innerBuilder.notEqual(otherBuilder));

        // The subselect is quite trivial: this just moves the original parallel
        // select test into a subselect.
        Expression correlatingExpression = innerBuilder.equal(outerBuilder);
        Expression innerExpression = correlatingExpression.and(parallelExpression);

        ReportQuery subQuery = new ReportQuery(Employee.class, innerBuilder);
        subQuery.setSelectionCriteria(innerExpression);
        subQuery.addAttribute("id");
        Expression expression = outerBuilder.exists(subQuery);
        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 6);
        test.setExpression(expression);
        test.setName("ParralelSelectSameNameInsideSubSelectTest");
        test.setDescription("Tests using two object builders in one subselect expression, for 2639318");
        addTest(test);
    }

    private void addParralelSelectSameNameTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ExpressionBuilder otherBuilder = new ExpressionBuilder(Employee.class);

        Expression expression = builder.get("lastName").equal(otherBuilder.get("lastName")).and(builder.notEqual(otherBuilder));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 6);
        test.setExpression(expression);
        test.setName("ParralelSelectSameNameTest");
        test.setDescription("Test using two object builds in one expression");
        addTest(test);
    }

	/**
	 * Tests a common usage of parallel selects: to avoid defining a mapping between
	 * tables.
	 * @bug 2637484 INVALID QUERY KEY EXCEPTION THROWN USING BATCH READS AND PARALLEL EXPRESSIONS
	 */
	private void addParallelSelectWithBatchAttributeTest() {
		ExpressionBuilder builder = new ExpressionBuilder();
		ExpressionBuilder otherBuilder = new ExpressionBuilder(Address.class);

		Expression expression = builder.getField("ADDR_ID").equal(otherBuilder.getField("ADDRESS_ID"));
		expression = expression.and(otherBuilder.get("province").equal("ONT"));

		ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 6);
		test.setExpression(expression);
		test.testBatchAttributesOnEmployee();
		test.setName("ParallelSelectWithBatchAttributeTest");
		test.setDescription("Test batch reading attributes from query with parallel selects.  For 2637484");
		addTest(test);
	}

	/**
     *  @bug 2612185 Support ReportItems,OrderBy Expressions from Parallel Builders.
     *  Find all managers of employees who have a spouse at work and a family
     *  income in excess of 100,000.
     */
    private void addManagersOfWealthyMarriedAtWorkEmployeesTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ExpressionBuilder innerBuilder = new ExpressionBuilder(Employee.class);
        ExpressionBuilder innerSpouses = new ExpressionBuilder(Employee.class);

        Expression innerExpression = innerBuilder.get("manager").equal(builder);
        innerExpression = innerExpression.and(innerBuilder.get("lastName").equal(innerSpouses.get("lastName")));
        innerExpression = innerExpression.and(innerBuilder.get("gender").notEqual(innerSpouses.get("gender")));

        ReportQuery subquery = new ReportQuery(Employee.class, innerBuilder);
        subquery.addAverage("family income", ExpressionMath.add(innerBuilder.get("salary"), innerSpouses.get("salary")));
        subquery.setSelectionCriteria(innerExpression);

        Expression expression = builder.subQuery(subquery).equal(140000);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(expression);
        test.testBatchAttributesOnEmployee();
        test.setName("ManagersOfWealthyMarriedAtWorkEmployeesTest");
        test.setDescription("Test executing query where subselect is a ReportQuery with ReportItems from multiple builders.  Tests batch reading too.  For 2612185.");
        test.addUnsupportedPlatform(TimesTenPlatform.class);
        addTest(test);
    }

    /**
     *  @bug 2611850 Support notIn(subquery).
     */
    private void addNotInTest() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder emp = new ExpressionBuilder(Employee.class);

        ExpressionBuilder phone = new ExpressionBuilder();
        ReportQuery subquery = new ReportQuery(PhoneNumber.class, phone);
        Expression subexp = phone.get("areaCode").equal("613");
        subquery.setSelectionCriteria(subexp);
        subquery.addAttribute("id");

        Expression expression = emp.get("id").notIn(subquery);
        query.setSelectionCriteria(expression);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(expression);
        test.setName("NotInTest");
        test.setDescription("Finds all employees who are not within the 613 area code.");
        addTest(test);
    }

	/**
	 * @bug  2718460 QUERY EXCEPTION THROWN USING BATCH READS AND OBJECT COMPARISONS
	 */
	private void addObjectComparisonWithBatchAttributeTest() {
		ExpressionBuilder builder = new ExpressionBuilder();
		ExpressionBuilder otherBuilder = new ExpressionBuilder(Employee.class);

		Expression expression = builder.get("lastName").equal(otherBuilder.get("lastName")).and(builder.notEqual(otherBuilder));

		ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 6);
		test.setExpression(expression);
		test.testBatchAttributesOnEmployee();
		test.setName("ObjectComparisonWithBatchAttributeTest");
		test.setDescription("Test batch reading attributes from query with object comparisons.  Bug 2718460.");
		addTest(test);
	}

	/**
	 *  @bug 2612140 CR2973- BATCHATTRIBUTE QUERIES WILL FAIL WHEN THE INITIAL QUERY HAS A SUBQUERY
	 */
	private void addExistsWithBatchAttributeTest() {
		ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
		ExpressionBuilder innerBuilder = new ExpressionBuilder(Address.class);

		Expression innerExpression = innerBuilder.equal(builder.get("address"));
		innerExpression = innerExpression.and(innerBuilder.get("province").equal("ONT"));

		ReportQuery subquery = new ReportQuery(Address.class, innerBuilder);
		subquery.addAttribute("id");
		subquery.setSelectionCriteria(innerExpression);

		Expression expression = builder.exists(subquery);

		ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 6);
		test.setExpression(expression);
		test.testBatchAttributesOnEmployee();
		test.setName("ExistsWithBatchAttributeTest");
		test.setDescription("Test batch reading attributes from query with exists (correlatedsub selects).  For 2612140.");
		addTest(test);
	}

	/**
	 *  @bug 2612567 CR4298- NULLPOINTEREXCEPTION WHEN USING SUBQUERY AND BATCH READING IN 4.6
	 */
	private void addSubSelectInWithBatchAttributeTest() {
		ExpressionBuilder innerBuilder = new ExpressionBuilder();
		ExpressionBuilder outerBuilder = new ExpressionBuilder();

		ReportQuery subQuery = new ReportQuery(Employee.class, innerBuilder);
		subQuery.addAttribute("lastName");
		subQuery.setSelectionCriteria(innerBuilder.get("firstName").like("B%"));

		Expression expression = outerBuilder.get("lastName").in(subQuery);

		ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 4);
		test.setExpression(expression);
		test.testBatchAttributesOnEmployee();
		test.setName("SubSelectInWithBatchAttributeTest");
		test.setDescription("Regression test batch reading attributes from query with IN sub select, for 2612567.");
		addTest(test);
	}

    private void addSubSelectCountTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ExpressionBuilder innerBuilder = new ExpressionBuilder();
        ReportQuery subQuery = new ReportQuery(Employee.class, innerBuilder);

        subQuery.addCount("Count", innerBuilder.anyOf("projects").distinct());
        subQuery.setSelectionCriteria(innerBuilder.equal(builder.anyOf("managedEmployees")));

        Expression expression = builder.subQuery(subQuery).greaterThanEqual(4);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(expression);
        test.setName("SubSelectCountTest");
        test.setDescription("Test subselects with count on an object attribute.  All managers whose employees work on 4 or more projects.");
        test.addUnsupportedPlatform(TimesTenPlatform.class);
        addTest(test);
    }

    private void addSubSelectCustomSQLTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ReportQuery subQuery = new ReportQuery(Employee.class, new ExpressionBuilder());
        subQuery.addMinimum("salary");
        subQuery.setSQLString("Select Min(s.SALARY) from EMPLOYEE e, SALARY s where e.EMP_ID = s.EMP_ID");
        Expression expression = builder.get("salary").equal(subQuery);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("SubSelectCustomSQLTest");
        test.setDescription("Test subselects with max salary through custom SQL");
        addTest(test);
    }

    private void addSubSelectEmployeeWithBusyManagerTest() {
        ExpressionBuilder employee = new ExpressionBuilder(Employee.class);
        Expression manager = employee.get("manager");
        ExpressionBuilder otherEmployee = new ExpressionBuilder(Employee.class);

        ReportQuery subQuery = new ReportQuery(Employee.class, otherEmployee);
        subQuery.addAttribute("id");

        subQuery.setSelectionCriteria(otherEmployee.get("manager").equal(manager).and(otherEmployee.equal(employee).not()));

        Expression expression = employee.exists(subQuery);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 5);
        test.setExpression(expression);
        test.setName("SubSelect Employee With Busy Manager Test");
        test.setDescription("Tests obj equals obj with 1:1 join optimization.  Finds employees whos managers manage at least one other.");
        addTest(test);
    }

    private void addSubSelectMaxManagerSalaryTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ReportQuery subQuery = new ReportQuery(Employee.class, new ExpressionBuilder());
        subQuery.addMaximum("salary");
        Expression expression = builder.get("manager").get("salary").equal(subQuery);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("SubSelectMaxManagerSalaryTest");
        test.setDescription("Test subselects with max and equals");
        addTest(test);
    }

    private void addSubSelectMaxPostalCodeTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ReportQuery subQuery = new ReportQuery(Address.class, new ExpressionBuilder());
        subQuery.addMaximum("postalCode");
        Expression expression = builder.get("postalCode").equal(subQuery);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Address.class, 1);
        test.setExpression(expression);
        test.setName("SubSelectMaxPostalCodeTest");
        test.setDescription("Test subselects with max and equals");
        addTest(test);
    }

    private void addSubSelectMaxSalaryInOttawaTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ReportQuery subQuery = new ReportQuery(Employee.class, new ExpressionBuilder());
        subQuery.addMaximum("salary");
        subQuery.setSelectionCriteria(subQuery.getExpressionBuilder().get("address").get("city").equal("Ottawa"));
        Expression expression = builder.get("salary").equal(subQuery);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("SubSelectMaxSalaryInOttawaTest");
        test.setDescription("Test subselects with max and equals with values");
        addTest(test);
    }

    private void addSubSelectMaxSalaryTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ReportQuery subQuery = new ReportQuery(Employee.class, new ExpressionBuilder());
        subQuery.addMaximum("salary");
        Expression expression = builder.get("salary").equal(subQuery);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("SubSelectMaxSalaryTest");
        test.setDescription("Test subselects with max and equals");
        addTest(test);
    }

    private void addSubSelectNestedSalaryTest() {
        ExpressionBuilder builder = new ExpressionBuilder();

        ReportQuery subQuery1 = new ReportQuery(Address.class, new ExpressionBuilder());
        subQuery1.addAttribute("city");
        subQuery1.setSelectionCriteria(subQuery1.getExpressionBuilder().get("country").likeIgnoreCase("canada"));

        ReportQuery subQuery2 = new ReportQuery(Employee.class, new ExpressionBuilder());
        subQuery2.addMaximum("salary");
        subQuery2.setSelectionCriteria(subQuery2.getExpressionBuilder().get("address").get("city").in(subQuery1));

        Expression expression = builder.get("salary").greaterThan(subQuery2).and((builder.get("address").get("country").likeIgnoreCase("canada")).not());

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("SubSelectNestedSalaryTest");
        test.setDescription("Test nested subselects with max salary and in cities");
        addTest(test);
    }

    private void addSubSelectNoProjectsTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ReportQuery subQuery = new ReportQuery(Project.class, new ExpressionBuilder());
        subQuery.addAttribute("id");
        subQuery.setSelectionCriteria(subQuery.getExpressionBuilder().equal(builder.anyOf("projects")));
        Expression expression = builder.notExists(subQuery);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 6);
        test.setExpression(expression);
        test.setName("SubSelectNoProjectsTest");
        test.setDescription("Test subselects with employees with no projects");
        addTest(test);
    }

    /**
     * @bug 2627019 NON-UNIQUE TABLE ALIASES ASSIGNED FOR NESTED SUBSELECTS
     * With an expression containing two nested parallel subselects, the nested
     * subquery will absolutely have to have unique aliases assigned to it.
     */
    private void addSubSelectCorrelatedNestedTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ExpressionBuilder subQueryBuilder = new ExpressionBuilder();

        ReportQuery nestedSubQuery = new ReportQuery(Employee.class, new ExpressionBuilder());
        nestedSubQuery.addCount();
        nestedSubQuery.setSelectionCriteria(nestedSubQuery.getExpressionBuilder().equal(subQueryBuilder.anyOf("managedEmployees")));

        ReportQuery subQuery = new ReportQuery(Employee.class, subQueryBuilder);
        subQuery.addCount();
        subQuery.setSelectionCriteria(subQueryBuilder.equal(builder.anyOf("managedEmployees")).and(subQueryBuilder.subQuery(nestedSubQuery).greaterThan(0)));
        Expression expression = builder.subQuery(subQuery).greaterThan(0);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 3);
        test.setExpression(expression);
        test.setName("SubSelectCorrelatedNestedTest");
        test.setDescription("Finds all managers of managers.  Tests nested correlated subselects for bug 2627019");
        test.addUnsupportedPlatform(TimesTenPlatform.class);
        addTest(test);
    }

    private void addSubSelectTwoManagedEmployeesAnyOfTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ReportQuery subQuery = new ReportQuery(Employee.class, new ExpressionBuilder());
        subQuery.addCount();
        subQuery.setSelectionCriteria(subQuery.getExpressionBuilder().equal(builder.anyOf("managedEmployees")));
        Expression expression = builder.subQuery(subQuery).greaterThan(2);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("SubSelectTwoManagedEmployeesAnyOfTest");
        test.setDescription("Test subselects with employees with more than 2 managed employees");
        test.addUnsupportedPlatform(TimesTenPlatform.class);
        addTest(test);
    }

    private void addSubSelectTwoManagedEmployeesTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ReportQuery subQuery = new ReportQuery(Employee.class, new ExpressionBuilder());
        subQuery.addCount();
        subQuery.setSelectionCriteria(subQuery.getExpressionBuilder().get("manager").equal(builder));
        Expression expression = builder.subQuery(subQuery).greaterThan(2);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("SubSelectTwoManagedEmployeesTest");
        test.setDescription("Test subselects with employees with more than 2 managed employees");
        test.addUnsupportedPlatform(AccessPlatform.class);
        test.addUnsupportedPlatform(TimesTenPlatform.class);
        addTest(test);
    }

    private void addSubSelectTwoProjectsTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ReportQuery subQuery = new ReportQuery(Project.class, new ExpressionBuilder());
        subQuery.addCount();
        subQuery.setSelectionCriteria(subQuery.getExpressionBuilder().equal(builder.anyOf("projects")));
        Expression expression = builder.subQuery(subQuery).lessThan(2).not();

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 4);
        test.setExpression(expression);
        test.setName("SubSelectTwoProjectsTest");
        test.setDescription("Test subselects with employees with 2 projects");
        test.addUnsupportedPlatform(TimesTenPlatform.class);
        addTest(test);
    }

    private void addSubSelectUnderpaidManagersTest() {
        ExpressionBuilder manager = new ExpressionBuilder(Employee.class);
        ExpressionBuilder employee = new ExpressionBuilder(Employee.class);

        ReportQuery subQuery = new ReportQuery(Employee.class, employee);
        subQuery.addAttribute("id");
        Expression managedCriteria = employee.get("manager").equal(manager);
        Expression overPaidCriteria = employee.get("salary").greaterThan(manager.get("salary"));

        subQuery.setSelectionCriteria(managedCriteria.and(overPaidCriteria));

        Expression underpaidManager = manager.exists(subQuery);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 3);
        test.setExpression(underpaidManager);
        test.setName("SubSelect Underpaid Managers Test");
        test.setDescription("Tests correlated subselects, finds all managers who make less than an employee they manage.");
        addTest(test);
    }

    private void addVehicleViewTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = ((builder.get("owner").get("name").equal("ABC")).and(builder.get("owner").get("name").equal("ABC"))).and((builder.get("passengerCapacity").equal(-1)).not());

        ExpressionBuilder sub = new ExpressionBuilder(Vehicle.class);
        ReportQuery subQuery = new ReportQuery(Vehicle.class, sub);
        subQuery.addAttribute("passengerCapacity");
        Expression subExpression = ((sub.get("owner").get("name").equal("ABC")).and(sub.get("owner").get("name").equal("ABC"))).and((sub.get("passengerCapacity").equal(-1)).not()).and(builder.equal(sub));
        subQuery.setSelectionCriteria(subExpression);

        expression = expression.and(builder.exists(subQuery));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Vehicle.class, 5);
        ReadAllQuery query = new ReadAllQuery(Vehicle.class, expression);
        query.addOrdering(builder.get("owner").get("name"));
        query.addJoinedAttribute("owner");
        query.useDistinct();
        test.setQuery(query);
        test.setName("VehicleViewOrderByJoinTest");
        test.setDescription("Test inheritance view with ordering and joining.");
        test.addSupportedPlatform(OraclePlatform.class);
        addTest(test);
    }

    public void addTests() {
        setManager(PopulationManager.getDefaultManager());

        addAggregateObjectJoiningTest();
        addAggregateObjectComparisonTest();
        addCorrelatedSubSelectTest();
        addCorrelatedSubSelectExpressionsReorderedTest();
        addParallelCorrelatedSubSelectsTest();
        addExistsWithBatchAttributeTest();
        addManagersOfWealthyMarriedAtWorkEmployeesTest();
        addNotInTest();
        addObjectComparisonWithBatchAttributeTest();
        addObjectEqualOrObjectEqualTest();
        addOracleExampleCorrelatedSubSelectTest();
        addParralelSelectCityTest();
        addParralelSelectSameCityTest();
        addParralelSelectSameNameInsideSubSelectTest();
        addParralelSelectSameNameTest();
        addParallelSelectWithBatchAttributeTest();
        addParralelSelectObjectComparisonTest();
        addSubSelectCorrelatedNestedTest();
        addSubSelectCountTest();
        addSubSelectEmployeeTest();
        addSubSelectEmployeeWithBusyManagerTest();
        addSubSelectInWithBatchAttributeTest();
        addSubSelectMaxSalaryTest();
        addSubSelectMaxPostalCodeTest();
        addSubSelectMaxSalaryInOttawaTest();
        addSubSelectMaxManagerSalaryTest();
        addSubSelectNoProjectsTest();
        addSubSelectCustomSQLTest();
        addSubSelectTwoManagedEmployeesTest();
        addSubSelectTwoManagedEmployeesAnyOfTest();
        addSubSelectNestedSalaryTest();
        addSubSelectTwoProjectsTest();
        addSubSelectUnderpaidManagersTest();
        addVehicleViewTest();
        addUpperCaseTest();
        addVehicleViewTest1();
        addSameManagerTest();
        addSubSelectSelectClauseTest();
        addSubSelectSelectClauseTest2();
        addSubSelectFromClauseTest();
        addSubSelectFromClauseTest2();
        addSubSelectObjectEqualsTest();
    }

    private void addSubSelectEmployeeTest() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder raqb = new ExpressionBuilder(Employee.class);

        ExpressionBuilder rqb = new ExpressionBuilder();
        ReportQuery rq = new ReportQuery(PhoneNumber.class, rqb);
        Expression exp = rqb.get("id").equal(raqb.get("id"));
        rq.setSelectionCriteria(exp);
        rq.addAttribute("id");

        Expression expression = raqb.get("id").in(rq);
        query.setSelectionCriteria(expression);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setExpression(expression);
        test.setName("SubSelectEmployeeTest");
        test.setDescription("Test subselects with employees and PhoneNumbers");
        addTest(test);
    }

    /**
     * This test was removed, not sure why?  Added back.
     */
    private void addUpperCaseTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0002");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("address").get("city").toUpperCase().equal("OTTAWA");

        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
        test.setName("UpperCaseExpressionTest");
        test.setDescription("Test UPPER expression");
        addTest(test);
    }

    /**
     * This test was removed?  Added back.
     */
    private void addVehicleViewTest1() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = ((builder.get("owner").get("name").equal("ABC")).and(builder.get("owner").get("name").equal("ABC"))).and((builder.get("passengerCapacity").equal(-1)).not());

        ReadAllExpressionTest test = new ReadAllExpressionTest(Vehicle.class, 5);
        test.setExpression(expression);
        test.setName("VehicleViewTest1");
        test.setDescription("Test expression against view, or multiple table subclass read.");
        addTest(test);
    }

    private void addSubSelectObjectEqualsTest() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        ReportQuery subQuery = new ReportQuery(Project.class, new ExpressionBuilder(Project.class));
        subQuery.addCount("id");
        subQuery.setSelectionCriteria(subQuery.getExpressionBuilder().equal(builder.anyOf("projects")));
        
        ReportQuery query = new ReportQuery(Employee.class, builder);
        query.addAttribute("id");
        query.addAttribute("firstName");
        query.setSelectionCriteria(builder.exists(subQuery));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        // Derby seems to ignore the count when it is 0, needs an outer join for some reason.
        test.addUnsupportedPlatform(DerbyPlatform.class);
        test.setQuery(query);
        test.setName("SubSelectObjectEqualsTest");
        test.setDescription("Test subselects that uses an object eqauls");
        addTest(test);
    }
    
    private void addSubSelectSelectClauseTest() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        ReportQuery subQuery = new ReportQuery(Project.class, new ExpressionBuilder(Project.class));
        subQuery.addCount("id");
        subQuery.setSelectionCriteria(subQuery.getExpressionBuilder().equal(builder.anyOf("projects")));
        
        ReportQuery query = new ReportQuery(Employee.class, builder);
        query.addAttribute("id");
        query.addAttribute("firstName");
        query.addItem("count", builder.subQuery(subQuery));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setQuery(query);
        test.setName("SubSelectSelectClauseTest");
        test.setDescription("Test subselects in the select clause");
        addTest(test);
    }

    private void addSubSelectSelectClauseTest2() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        ReportQuery subQuery = new ReportQuery(Project.class, new ExpressionBuilder(Project.class));
        subQuery.addCount("id");
        subQuery.setSelectionCriteria(subQuery.getExpressionBuilder().get("id").equal(builder.anyOf("projects").get("id")));
        
        ReportQuery query = new ReportQuery(Employee.class, builder);
        query.addAttribute("id");
        query.addAttribute("firstName");
        query.addItem("count", builder.subQuery(subQuery));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setQuery(query);
        test.setName("SubSelectSelectClauseTest2");
        test.setDescription("Test subselects in the select clause");
        addTest(test);
    }

    private void addSubSelectFromClauseTest() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        ReportQuery subQuery = new ReportQuery(LargeProject.class, new ExpressionBuilder(LargeProject.class));
        subQuery.addAttribute("id");
        
        ReportQuery query = new ReportQuery(Employee.class, builder);
        query.addAttribute("id");
        query.addAttribute("firstName");
        Expression alias = builder.getAlias(builder.subQuery(subQuery));
        query.addNonFetchJoin(alias);
        query.setSelectionCriteria(builder.get("id").equal(alias.get("id")).and(builder.get("id").notEqual(alias.get("id"))));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setQuery(query);
        test.setName("SubSelectFromClauseTest");
        test.setDescription("Test subselects in the from clause");
        addTest(test);
    }

    private void addSubSelectFromClauseTest2() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        ReportQuery subQuery = new ReportQuery(LargeProject.class, new ExpressionBuilder(LargeProject.class));
        subQuery.addItem("id", subQuery.getExpressionBuilder().get("id").average());
        subQuery.addItem("id2", subQuery.getExpressionBuilder().get("id").maximum());
        
        ReportQuery query = new ReportQuery(Employee.class, builder);
        query.addItem("e", builder);
        Expression alias = builder.getAlias(builder.subQuery(subQuery));
        query.addNonFetchJoin(alias);
        query.setSelectionCriteria(builder.get("id").equal(alias.get("id")).and(builder.get("id").notEqual(alias.get("id2"))));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setQuery(query);
        test.setName("SubSelectFromClauseTest2");
        test.setDescription("Test subselects in the from clause");
        addTest(test);
    }

    protected PopulationManager getManager() {
        return manager;
    }

    protected void setManager(PopulationManager theManager) {
        manager = theManager;
    }
}
