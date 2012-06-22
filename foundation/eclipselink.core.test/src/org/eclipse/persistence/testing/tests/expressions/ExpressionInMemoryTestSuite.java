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

import java.util.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * <b>Purpose:</b> Tests Complex queries executed In-Memory.
 * <p>
 * <b>Responsibilities:</b> Execute the ExpressionTestSuite In-Memory.  Only
 * expressions that run on all platforms are supported In-Memory.
 * <p>
 * TopLink has the limited ability to execute some queries in memory, and
 * to decide if a particular domain object conforms to the selection criteria.
 *
 * @author Stephen McRitchie
 * @since 9.0.4
 */
public class ExpressionInMemoryTestSuite extends ExpressionUnitTestSuite {
    public ExpressionInMemoryTestSuite() {
        setDescription("This suite tests expressions executed in memory.");
    }

    private void _addAdvancedInMemoryExpressionMathTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("salary").greaterThanEqual(1000);

        //expression = expression.and((ExpressionMath.ln(builder.get("salary"))).lessThan(10)); 
        //expression = expression.and((ExpressionMath.mod(builder.get("salary"), new Integer(107)).lessThan(10)));
        expression = expression.and((ExpressionMath.floor(builder.get("salary"))).lessThan(45000));
        expression = expression.and((ExpressionMath.ceil(builder.get("salary"))).lessThan(10000));
        expression = expression.and(ExpressionMath.round(builder.get("salary"), 2).equal(40000));
        expression = expression.and(ExpressionMath.min(builder.get("salary"), new Integer(30000)).greaterThan(30000));
        expression = expression.and(ExpressionMath.max(builder.get("salary"), new Integer(30000)).lessThan(50000));
        //expression = expression.and((ExpressionMath.sinh(ExpressionMath.divide(builder.get("salary"),new Integer(10000000))).lessThanEqual(100)));	
        //expression = expression.and((ExpressionMath.cosh(ExpressionMath.divide(builder.get("salary"),new Integer(10000000))).lessThanEqual(100)));	
        //expression = expression.and((ExpressionMath.tanh(ExpressionMath.divide(builder.get("salary"),new Integer(10000000))).lessThanEqual(1)));
        expression = expression.and((ExpressionMath.acos(ExpressionMath.power(builder.get("salary"), 0)).lessThanEqual(100)));
        expression = expression.and((ExpressionMath.asin(ExpressionMath.power(builder.get("salary"), 0)).lessThanEqual(100)));
        expression = expression.and((ExpressionMath.atan(ExpressionMath.power(builder.get("salary"), 0)).lessThanEqual(100)));
        //expression = expression.and((ExpressionMath.atan2(ExpressionMath.power(builder.get("salary"), 0), 2).lessThanEqual(100)));
        expression = expression.and(ExpressionMath.power(builder.get("salary"), 1).equal(40000));
        //expression = expression.and((ExpressionMath.trunc(builder.get("salary"), 2).equal(50000)));	
        //expression = expression.and((ExpressionMath.chr(builder.get("salary"))).equal('b'));
        expression = expression.and(ExpressionMath.round(builder.get("salary"), 2).equal(40000));
        //expression = expression.and((ExpressionMath.sign(builder.get("salary"))).greaterThan(0));
        expression = expression.and((ExpressionMath.exp(ExpressionMath.min(builder.get("salary"), 5))).lessThan(1000000));
        // Test sqrt.
        expression = expression.and(ExpressionMath.power(builder.get("salary"), new Double(0.5)).greaterThan(0));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("AdvancedInMemoryExpressionMathTest");
        test.setDescription("Test advanced expression math package In-Memory");
        addTest(test);
    }

    protected void _addBetween$DateTest() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(1900, 1, 1);
        java.sql.Date start = new java.sql.Date(calendar.getTime().getTime());
        calendar.set(1902, 1, 1);
        java.sql.Date end = new java.sql.Date(calendar.getTime().getTime());
        Expression expression = (new ExpressionBuilder()).get("period").get("startDate").between(start, end);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 6);
        test.setExpression(expression);
        test.setName("Between$DateTest");
        test.setDescription("Test BETWEEN (Date) expression InMemory.");
        addTest(test);
    }

    protected void _addBetween$StringTest() {
        Expression expression = (new ExpressionBuilder()).get("firstName").between("Babi", "Buzz");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(expression);
        test.setName("Between$StringTest");
        test.setDescription("Test BETWEEN (String) expression inMemory");
        addTest(test);
    }

    /*
     * The following are testing doesRelationConform (Equal) in memory.
     */
    protected void _addEqual$nullTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("manager").equal(builder.value(null));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 4);
        test.setExpression(expression);
        test.setName("Equal$nullTest");
        test.setDescription("Test Equal(null) expression inMemory");
        addTest(test);
    }

    protected void _addEqual$DateTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0003");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("period").get("startDate").equal(employee.getPeriod().getStartDate());

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 6);
        test.setExpression(expression);
        test.setName("Equal$DateTest");
        test.setDescription("Test Equal(Date) expression inMemory");
        addTest(test);
    }

    protected void _addEqual$StringTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("firstName").equal("Bob");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("Equal$StringTest");
        test.setDescription("Test Equal(String) expression inMemory");
        addTest(test);
    }

    /*
     * The following are testing doesRelationConform (greaterThan) in memory.
     */
    protected void _addGreaterThan$nullTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("salary").greaterThan(builder.value(null));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("GreaterThan$nullTest");
        test.setDescription("Test GreaterThan(null) expression inMemory");
        addTest(test);
    }

    protected void _addGreaterThan$DateTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0003");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("period").get("startDate").greaterThan(employee.getPeriod().getStartDate());

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 5);
        test.setExpression(expression);
        test.setName("GreaterThan$DateTest");
        test.setDescription("Test GreaterThan(Date) expression inMemory");
        addTest(test);
    }

    protected void _addGreaterThan$StringTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("firstName").greaterThan("Bob");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 10);
        test.setExpression(expression);
        test.setName("GreaterThan$StringTest");
        test.setDescription("Test GreaterThan(String) expression inMemory");
        addTest(test);
    }

    /*
     * The following are testing doesRelationConform (greaterThanEqual) in memory.
     */
    protected void _addGreaterThanEqual$nullTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("salary").greaterThanEqual(builder.value(null));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("GreaterThanEqual$nullTest");
        test.setDescription("Test GreaterThanEqual(null) expression inMemory");
        addTest(test);
    }

    protected void _addGreaterThanEqual$DateTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0003");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("period").get("startDate").greaterThanEqual(employee.getPeriod().getStartDate());

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 11);
        test.setExpression(expression);
        test.setName("GreaterThanEqual$DateTest");
        test.setDescription("Test GreaterThanEqual(Date) expression inMemory");
        addTest(test);
    }

    protected void _addGreaterThanEqual$StringTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("firstName").greaterThanEqual("Bob");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 11);
        test.setExpression(expression);
        test.setName("GreaterThanEqual$StringTest");
        test.setDescription("Test GreaterThanEqual(String) expression inMemory");
        addTest(test);
    }

    /*
     * The following are testing doesRelationConform (lessThan) in memory.
     */
    protected void _addLessThan$nullTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("salary").lessThan(builder.value(null));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("LessThan$nullTest");
        test.setDescription("Test LessThan(null) expression inMemory");
        addTest(test);
    }

    /*
     * The following are testing doesRelationConform (Equal) in memory.
     */
    protected void _addIsNullAccrossAnyOfTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("projects").get("teamLeader").equal(null);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 4);
        test.setExpression(expression);
        test.setName("IsNullAccrossAnyOfTest");
        test.setDescription("Test anyOf().get().isNull expression inMemory");
        addTest(test);
    }

    /*
     * The following are testing doesRelationConform (Equal) in memory.
     */
    protected void _addIsNullAccrossAnyOfWorkaroundTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("projects").get("teamLeader").equal(builder.value(null));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 6);
        test.setExpression(expression);
        test.setName("IsNullAccrossAnyOfWorkaroundTest");
        test.setDescription("Test anyOf().get().isNull expression inMemory");
        addTest(test);
    }

    /*
     * The following are testing doesRelationConform (Equal) in memory.
     */
    protected void _addIsNullAccrossAnyOfWorkaround2Test() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("projects").get("teamLeader").equal(builder.getParameter("TL"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 6);
        test.setExpression(expression);
        ReadAllQuery query = test.getQuery(true);
        query.addArgument("TL");
        test.getArguments().add(null);
        test.setName("IsNullAccrossAnyOfWorkaround2Test");
        test.setDescription("Test anyOf().get().isNull expression inMemory");
        addTest(test);
    }

    protected void _addLessThan$DateTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0003");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("period").get("startDate").lessThan(employee.getPeriod().getStartDate());

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("LessThan$DateTest");
        test.setDescription("Test LessThan(Date) expression inMemory");
        addTest(test);
    }

    protected void _addLessThan$StringTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("firstName").lessThan("Bob");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("LessThan$StringTest");
        test.setDescription("Test LessThan(String) expression inMemory");
        addTest(test);
    }

    /*
     * The following are testing doesRelationConform (lessThanEqual) in memory.
     */
    protected void _addLessThanEqual$nullTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("salary").lessThanEqual(builder.value(null));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("LessThanEqual$nullTest");
        test.setDescription("Test LessThanEqual(null) expression inMemory");
        addTest(test);
    }

    protected void _addLessThanEqual$DateTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0003");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("period").get("startDate").lessThanEqual(employee.getPeriod().getStartDate());

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 7);
        test.setExpression(expression);
        test.setName("LessThanEqual$DateTest");
        test.setDescription("Test LessThanEqual(Date) expression inMemory");
        addTest(test);
    }

    protected void _addLessThanEqual$StringTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("firstName").lessThanEqual("Bob");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(expression);
        test.setName("LessThanEqual$StringTest");
        test.setDescription("Test LessThanEqual(String) expression inMemory");
        addTest(test);
    }

    /*
     * The following are testing doesRelationConform (NotEqual) in memory.
     */
    protected void _addNotEqual$nullTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("manager").notEqual(builder.value(null));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 8);
        test.setExpression(expression);
        test.setName("NotEqual$nullTest");
        test.setDescription("Test NotEqual(null) expression inMemory");
        addTest(test);
    }

    protected void _addNotEqual$DateTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0003");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("period").get("startDate").notEqual(employee.getPeriod().getStartDate());

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 6);
        test.setExpression(expression);
        test.setName("NotEqual$DateTest");
        test.setDescription("Test NotEqual(Date) expression inMemory");
        addTest(test);
    }

    protected void _addNotEqual$StringTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("firstName").notEqual("Bob");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 11);
        test.setExpression(expression);
        test.setName("NotEqual$StringTest");
        test.setDescription("Test NotEqual(String) expression inMemory");
        addTest(test);
    }

    protected void _addToNumberTest() {
        Expression expression = (new ExpressionBuilder()).anyOf("phoneNumbers").get("areaCode").toNumber().equal(613);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 10);
        test.setExpression(expression);
        test.setName("ToNumberTest");
        test.setDescription("Test ToNumber expression InMemory");
        addTest(test);
    }
    
    protected void _addLikeDoubleWildcardTest(){
        Expression expression = (new ExpressionBuilder()).get("firstName").like("B__");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("LikeDoubleWildCardTest");
        test.setDescription("Test like expression with two consecutive wildcards");
        addTest(test);
    }

    public void addTests() {
        // By adding tests from the parent, In-Memory expression support
        // can be kept current with future changes.
        addBaseExpressionTests();
        super.addTests();

        _addAdvancedInMemoryExpressionMathTest();
        _addBetween$DateTest();
        _addBetween$StringTest();
        _addEqual$nullTest();
        _addEqual$DateTest();
        _addEqual$longTest();
        _addEqual$StringTest();
        _addGreaterThan$nullTest();
        _addGreaterThan$DateTest();
        _addGreaterThan$longTest();
        _addGreaterThan$StringTest();
        _addGreaterThanEqual$nullTest();
        _addGreaterThanEqual$DateTest();
        _addGreaterThanEqual$longTest();
        _addGreaterThanEqual$StringTest();
        _addIsNullAccrossAnyOfTest();
        _addIsNullAccrossAnyOfWorkaroundTest();
        _addIsNullAccrossAnyOfWorkaround2Test();
        _addLengthTest();
        _addLessThan$nullTest();
        _addLessThan$DateTest();
        _addLessThan$longTest();
        _addLessThan$StringTest();
        _addLessThanEqual$nullTest();
        _addLessThanEqual$DateTest();
        _addLessThanEqual$longTest();
        _addLessThanEqual$StringTest();
        _addNotBetween$longTest();
        _addNotEqual$nullTest();
        _addNotEqual$DateTest();
        _addNotEqual$longTest();
        _addNotEqual$StringTest();

        _addToNumberTest();
        _addLikeDoubleWildcardTest();

        Vector inMemoryTests = new Vector(getTests().size());
        for (Iterator iter = getTests().iterator(); iter.hasNext();) {
            TestEntity baseTest = (TestEntity)iter.next();
            if (baseTest instanceof ReadAllExpressionTest) {
                ReadAllExpressionTest test = (ReadAllExpressionTest)baseTest;
                if (test.isPlatformSpecific()) {
                    continue;
                }
                if (!shouldTestPassInMemory(test)) {
                    continue;
                }
                ReadAllQuery query = test.getQuery(true);
                query.checkCacheOnly();
                InMemoryQueryIndirectionPolicy policy = new InMemoryQueryIndirectionPolicy();
                policy.triggerIndirection();
                query.setInMemoryQueryIndirectionPolicy(policy);
                inMemoryTests.addElement(baseTest);
            }
        }
        inMemoryTests.trimToSize();
        setTests(inMemoryTests);
    }

    /**
     * Filter out all Expression tests that trigger some fatal error
     * when executed In-Memory.
     */
    public boolean shouldTestPassInMemory(ReadAllExpressionTest test) {
        String name = test.getName();
        if (// Exclude batching tests
            (name.indexOf("MultiPlatformTest") > -1) ||// MultiPlatformTestx use functions that are not supported in memory yet
            (name.indexOf("Batch") > -1) ||// Exclude float tests because of Java floating point conversion issues.
            (name.indexOf("$float") > -1) ||// Exclude char tests because they use field values, with incorrect types.
            (name.indexOf("$char") > -1) ||// Excluded due to bug 3246889, inner/outerjoin symantics need to be supported.
            (name.equals("JoinsShrinkResultSetSizeTest")) || (name.equals("NotSelfManagedEmployeeTest")) || (name.equals("NotEqualSelfManagedEmployeeTest")) || (name.equals("NotBetween$ObjectTest")) || (name.equals("IsNullAccrossAnyOfTest")) || (name.equals("VehicleViewJoinOnlyTest"))) {
            return false;
        }
        return true;
    }
}
