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
package org.eclipse.persistence.testing.tests.jpql;


// TopLink imports
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;

// Testing imports
import org.eclipse.persistence.testing.framework.*;

// Domain imports
import org.eclipse.persistence.testing.models.employee.domain.*;

public class BinaryOperatorTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public static BinaryOperatorTest getSimpleGreaterThanTest() {
        BinaryOperatorTest theTest = new BinaryOperatorTest();

        theTest.setName("Simple Greater Than test");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM Employee emp WHERE emp.id > 12");
        theTest.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("id").greaterThan(12);
        theTest.setOriginalObjectExpression(whereClause);

        return theTest;
    }

    public static BinaryOperatorTest getSimpleGreaterThanEqualTest() {
        BinaryOperatorTest theTest = new BinaryOperatorTest();

        theTest.setName("Simple Greater Than Equal test");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM Employee emp WHERE emp.id >= 12");
        theTest.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("id").greaterThanEqual(12);
        theTest.setOriginalObjectExpression(whereClause);

        return theTest;
    }

    public static BinaryOperatorTest getSimpleLessThanEqualTest() {
        BinaryOperatorTest theTest = new BinaryOperatorTest();

        theTest.setName("Simple Less Than Equal test");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM Employee emp WHERE emp.id <= 1000000");
        theTest.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("id").lessThanEqual(1000000);
        theTest.setOriginalObjectExpression(whereClause);

        return theTest;
    }

    public static BinaryOperatorTest getSimpleLessThanTest() {
        BinaryOperatorTest theTest = new BinaryOperatorTest();

        theTest.setName("Simple Less Than test");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM Employee emp WHERE emp.id < 1000000");
        theTest.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("id").lessThan(1000000);
        theTest.setOriginalObjectExpression(whereClause);

        return theTest;
    }

    public static BinaryOperatorTest getSimplePlusTest() {
        BinaryOperatorTest theTest = new BinaryOperatorTest();

        theTest.setName("Simple Plus test");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM Employee emp WHERE emp.salary + 1000 <= 50000");
        theTest.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.add(builder.get("salary"), new Integer(1000)).lessThanEqual(50000);
        theTest.setOriginalObjectExpression(whereClause);

        return theTest;
    }

    public static BinaryOperatorTest getSimplePlusTestWithBracketsBeforeComparison() {
        BinaryOperatorTest theTest = new BinaryOperatorTest();

        theTest.setName("Simple Plus test with brackets before comparison");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM Employee emp WHERE (emp.salary + 1000) <= 50000");
        theTest.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.add(builder.get("salary"), new Integer(1000)).lessThanEqual(50000);
        theTest.setOriginalObjectExpression(whereClause);

        return theTest;
    }

    public static BinaryOperatorTest getSimplePlusTestWithBracketsAfterComparison() {
        BinaryOperatorTest theTest = new BinaryOperatorTest();

        theTest.setName("Simple Plus test with brackets after comparison");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM Employee emp WHERE 50000 > (emp.salary + 1000)");
        theTest.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionBuilder.fromConstant(new Integer(50000), builder).greaterThan(ExpressionMath.add(builder.get("salary"), new Integer(1000)));
        theTest.setOriginalObjectExpression(whereClause);

        return theTest;
    }

    public static BinaryOperatorTest getSimpleMinusTest() {
        BinaryOperatorTest theTest = new BinaryOperatorTest();

        theTest.setName("Simple Minus test");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM Employee emp WHERE emp.salary - 1000 <= 50000");
        theTest.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.subtract(builder.get("salary"), new Integer(1000)).lessThanEqual(50000);
        theTest.setOriginalObjectExpression(whereClause);

        return theTest;
    }

    public static BinaryOperatorTest getSimpleMinusTestWithBracketsBeforeComparison() {
        BinaryOperatorTest theTest = new BinaryOperatorTest();

        theTest.setName("Simple Minus test with Brackets Before Comparison");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM Employee emp WHERE (emp.salary - 1000) <= 50000");
        theTest.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.subtract(builder.get("salary"), new Integer(1000)).lessThanEqual(50000);
        theTest.setOriginalObjectExpression(whereClause);

        return theTest;
    }

    public static BinaryOperatorTest getSimpleMinusTestWithBracketsAfterComparison() {
        BinaryOperatorTest theTest = new BinaryOperatorTest();

        theTest.setName("Simple Minus test with Brackets After Comparison");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM Employee emp WHERE 50000 > (emp.salary - 1000)");
        theTest.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionBuilder.fromConstant(new Integer(50000), builder).greaterThan(ExpressionMath.subtract(builder.get("salary"), new Integer(1000)));
        theTest.setOriginalObjectExpression(whereClause);

        return theTest;
    }

    public static BinaryOperatorTest getSimpleMultiplyTest() {
        BinaryOperatorTest theTest = new BinaryOperatorTest();

        theTest.setName("Simple Multiply test");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM Employee emp WHERE emp.salary * 2 <= 100000");
        theTest.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.multiply(builder.get("salary"), new Integer(2)).lessThanEqual(100000);
        theTest.setOriginalObjectExpression(whereClause);

        return theTest;
    }

    public static BinaryOperatorTest getSimpleMultiplyTestWithBracketsBeforeComparison() {
        BinaryOperatorTest theTest = new BinaryOperatorTest();

        theTest.setName("Simple Multiply test with brackets before comparison");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM Employee emp WHERE (emp.salary * 2) <= 100000");
        theTest.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.multiply(builder.get("salary"), new Integer(2)).lessThanEqual(100000);
        theTest.setOriginalObjectExpression(whereClause);

        return theTest;
    }

    public static BinaryOperatorTest getSimpleMultiplyTestWithBracketsAfterComparison() {
        BinaryOperatorTest theTest = new BinaryOperatorTest();

        theTest.setName("Simple Multiply test with brackets after comparison");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM Employee emp WHERE 100000 > (emp.salary * 2)");
        theTest.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionBuilder.fromConstant(new Integer(100000), builder).greaterThan(ExpressionMath.multiply(builder.get("salary"), new Integer(2)));
        theTest.setOriginalObjectExpression(whereClause);

        return theTest;
    }

    public static BinaryOperatorTest getSimpleDivideTest() {
        BinaryOperatorTest theTest = new BinaryOperatorTest();

        theTest.setName("Simple Divide test");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM Employee emp WHERE emp.salary / 2 <= 20000");
        theTest.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.divide(builder.get("salary"), new Integer(2)).lessThanEqual(20000);
        theTest.setOriginalObjectExpression(whereClause);

        return theTest;
    }

    public static BinaryOperatorTest getSimpleDivideTestWithBracketsBeforeComparison() {
        BinaryOperatorTest theTest = new BinaryOperatorTest();

        theTest.setName("Simple Divide test with brackets before comparison");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM Employee emp WHERE (emp.salary / 2) <= 20000");
        theTest.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.divide(builder.get("salary"), new Integer(2)).lessThanEqual(20000);
        theTest.setOriginalObjectExpression(whereClause);

        return theTest;
    }

    public static BinaryOperatorTest getSimpleDivideTestWithBracketsAfterComparison() {
        BinaryOperatorTest theTest = new BinaryOperatorTest();

        theTest.setName("Simple Divide test with brackets after comparison");
        theTest.setEjbqlString("SELECT OBJECT(emp) FROM Employee emp WHERE 20000 > (emp.salary / 2)");
        theTest.setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionBuilder.fromConstant(new Integer(20000), builder).greaterThan(ExpressionMath.divide(builder.get("salary"), new Integer(2)));
        theTest.setOriginalObjectExpression(whereClause);

        return theTest;
    }

    public static TestSuite getSimpleBinaryOperatorTests() {
        TestSuite theSuite = new TestSuite();

        theSuite.setName("Simple Binary Operator Test Suite");
        theSuite.addTest(BinaryOperatorTest.getSimpleGreaterThanTest());
        theSuite.addTest(BinaryOperatorTest.getSimpleGreaterThanEqualTest());
        theSuite.addTest(BinaryOperatorTest.getSimpleLessThanEqualTest());
        theSuite.addTest(BinaryOperatorTest.getSimpleLessThanTest());
        theSuite.addTest(BinaryOperatorTest.getSimplePlusTest());
        theSuite.addTest(BinaryOperatorTest.getSimplePlusTestWithBracketsBeforeComparison());
        theSuite.addTest(BinaryOperatorTest.getSimplePlusTestWithBracketsAfterComparison());
        theSuite.addTest(BinaryOperatorTest.getSimpleMinusTest());
        theSuite.addTest(BinaryOperatorTest.getSimpleMinusTestWithBracketsBeforeComparison());
        theSuite.addTest(BinaryOperatorTest.getSimpleMinusTestWithBracketsAfterComparison());
        theSuite.addTest(BinaryOperatorTest.getSimpleMultiplyTest());
        theSuite.addTest(BinaryOperatorTest.getSimpleMultiplyTestWithBracketsBeforeComparison());
        theSuite.addTest(BinaryOperatorTest.getSimpleMultiplyTestWithBracketsAfterComparison());
        theSuite.addTest(BinaryOperatorTest.getSimpleDivideTest());
        theSuite.addTest(BinaryOperatorTest.getSimpleDivideTestWithBracketsBeforeComparison());
        theSuite.addTest(BinaryOperatorTest.getSimpleDivideTestWithBracketsAfterComparison());

        return theSuite;
    }

    public void setup() {
        ReadAllQuery raq = new ReadAllQuery();
        raq.setSelectionCriteria(getOriginalObjectExpression());
        raq.setReferenceClass(getReferenceClass());

        setOriginalOject(getSession().executeQuery(raq));

        super.setup();
    }
}
