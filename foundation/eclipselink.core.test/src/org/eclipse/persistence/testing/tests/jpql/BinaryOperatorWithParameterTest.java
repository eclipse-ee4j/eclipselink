/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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


// Java imports
import java.util.*;
// TopLink imports
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

// Testing imports
import org.eclipse.persistence.testing.framework.*;

// Domain imports
import org.eclipse.persistence.testing.models.employee.domain.*;

public class BinaryOperatorWithParameterTest extends JPQLParameterTestCase {
    private Vector expressionParameters;

    public static BinaryOperatorWithParameterTest getNumericParameterGreaterThanTest() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Greater than with parameter test");

        String parameterName = "id";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterName);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("id").greaterThan(builder.getParameter(parameterName));
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "emp.id > ?1 ";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        theTest.setArgumentNames(myArgumentNames);

        return theTest;
    }

    public static BinaryOperatorWithParameterTest getNumericParameterGreaterThanEqualTest() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Greater than equal to with parameter test");

        String parameterName = "id";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterName);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("id").greaterThanEqual(builder.getParameter(parameterName));
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "emp.id >= ?1 ";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        theTest.setArgumentNames(myArgumentNames);

        return theTest;
    }

    public static BinaryOperatorWithParameterTest getNumericParameterLessThanTest() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Less than with parameter test");

        String parameterName = "id";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterName);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("id").lessThan(builder.getParameter(parameterName));
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "emp.id < ?1 ";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        theTest.setArgumentNames(myArgumentNames);

        return theTest;
    }

    public static BinaryOperatorWithParameterTest getNumericParameterLessThanEqualTest() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Less than equal to with parameter test");

        String parameterName = "id";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterName);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("id").lessThanEqual(builder.getParameter(parameterName));
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "emp.id <= ?1 ";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        theTest.setArgumentNames(myArgumentNames);

        return theTest;
    }

    public static BinaryOperatorWithParameterTest getNumericParameterPlusTest() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Plus with parameter test");

        String parameterName = "amountToAdd";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterName);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.add(builder.get("salary"), (builder.getParameter(parameterName))).lessThanEqual(50000);
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.salary + ?1 <= 50000";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        theTest.setArgumentNames(myArgumentNames);

        theTest.setArguments(new Vector());
        theTest.getArguments().addElement(new Integer(1000));

        return theTest;
    }

    public static BinaryOperatorWithParameterTest getNumericParameterPlusTestWithBracketsBeforeComparison() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Plus with parameter test with brackets before comparison");

        String parameterName = "amountToAdd";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterName);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.add(builder.get("salary"), (builder.getParameter(parameterName))).lessThanEqual(50000);
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE (emp.salary + ?1) <= 50000";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        theTest.setArgumentNames(myArgumentNames);

        theTest.setArguments(new Vector());
        theTest.getArguments().addElement(new Integer(1000));

        return theTest;
    }

    public static BinaryOperatorWithParameterTest getNumericParameterPlusTestWithBracketsAfterComparison() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Plus with parameter test with brackets after comparison");

        String parameterName = "amountToAdd";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterName);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionBuilder.fromConstant(new Integer(50000), builder).greaterThan(ExpressionMath.add(builder.get("salary"), (builder.getParameter(parameterName))));
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE 50000 > (emp.salary + ?1)";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        theTest.setArgumentNames(myArgumentNames);

        theTest.setArguments(new Vector());
        theTest.getArguments().addElement(new Integer(1000));

        return theTest;
    }

    public static BinaryOperatorWithParameterTest getNumericParameterMinusTest() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Minus with parameter test");

        String parameterName = "amountToSubtract";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterName);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.subtract(builder.get("salary"), (builder.getParameter(parameterName))).lessThanEqual(50000);
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.salary - ?1 <= 50000";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        theTest.setArgumentNames(myArgumentNames);

        theTest.setArguments(new Vector());
        theTest.getArguments().addElement(new Integer(1000));

        return theTest;
    }

    public static BinaryOperatorWithParameterTest getNumericParameterMinusTestWithBracketsBeforeComparison() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Minus with parameter test with brackets before comparison");

        String parameterName = "amountToSubtract";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterName);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.subtract(builder.get("salary"), (builder.getParameter(parameterName))).lessThanEqual(50000);
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE (emp.salary - ?1) <= 50000";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        theTest.setArgumentNames(myArgumentNames);

        theTest.setArguments(new Vector());
        theTest.getArguments().addElement(new Integer(1000));

        return theTest;
    }

    public static BinaryOperatorWithParameterTest getNumericParameterMinusTestWithBracketsAfterComparison() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Minus with parameter test with brackets after comparison");

        String parameterName = "amountToSubtract";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterName);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionBuilder.fromConstant(new Integer(50000), builder).greaterThan(ExpressionMath.subtract(builder.get("salary"), (builder.getParameter(parameterName))));
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE 50000 > (emp.salary - ?1)";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        theTest.setArgumentNames(myArgumentNames);

        theTest.setArguments(new Vector());
        theTest.getArguments().addElement(new Integer(1000));

        return theTest;
    }

    public static BinaryOperatorWithParameterTest getNumericParameterMultiplyTest() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Multiply with parameter test");

        String parameterName = "amountToMultiply";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterName);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.multiply(builder.get("salary"), (builder.getParameter(parameterName))).lessThanEqual(100000);
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.salary * ?1 <= 100000";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        theTest.setArgumentNames(myArgumentNames);

        theTest.setArguments(new Vector());
        theTest.getArguments().addElement(new Integer(2));

        return theTest;
    }

    public static BinaryOperatorWithParameterTest getNumericParameterMultiplyTestWithBracketsBeforeComparison() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Multiply with parameter test with brackets before comparison");

        String parameterName = "amountToMultiply";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterName);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.multiply(builder.get("salary"), (builder.getParameter(parameterName))).lessThanEqual(100000);
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE (emp.salary * ?1) <= 100000";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        theTest.setArgumentNames(myArgumentNames);

        theTest.setArguments(new Vector());
        theTest.getArguments().addElement(new Integer(2));

        return theTest;
    }

    public static BinaryOperatorWithParameterTest getNumericParameterMultiplyTestWithBracketsAfterComparison() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Multiply with parameter test with brackets after comparison");

        String parameterName = "amountToMultiply";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterName);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionBuilder.fromConstant(new Integer(100000), builder).greaterThan(ExpressionMath.multiply(builder.get("salary"), (builder.getParameter(parameterName))));
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE 100000 > (emp.salary * ?1)";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        theTest.setArgumentNames(myArgumentNames);

        theTest.setArguments(new Vector());
        theTest.getArguments().addElement(new Integer(2));

        return theTest;
    }

    public static BinaryOperatorWithParameterTest getNumericParameterDivideTest() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Divide with parameter test");

        String parameterName = "amountToDivide";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterName);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.divide(builder.get("salary"), (builder.getParameter(parameterName))).lessThanEqual(20000);
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.salary / ?1 <= 20000";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        theTest.setArgumentNames(myArgumentNames);

        theTest.setArguments(new Vector());
        theTest.getArguments().addElement(new Integer(2));

        return theTest;
    }

    public static BinaryOperatorWithParameterTest getNumericParameterDivideTestWithBracketsBeforeComparison() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Divide with parameter test with brackets before comparison");

        String parameterName = "amountToDivide";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterName);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.divide(builder.get("salary"), (builder.getParameter(parameterName))).lessThanEqual(20000);
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE (emp.salary / ?1) <= 20000";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        theTest.setArgumentNames(myArgumentNames);

        theTest.setArguments(new Vector());
        theTest.getArguments().addElement(new Integer(2));

        return theTest;
    }

    public static BinaryOperatorWithParameterTest getNumericParameterDivideTestWithBracketsAfterComparison() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Divide with parameter test with brackets after comparison");

        String parameterName = "amountToDivide";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterName);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionBuilder.fromConstant(new Integer(20000), builder).greaterThan(ExpressionMath.divide(builder.get("salary"), (builder.getParameter(parameterName))));
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE 20000 > (emp.salary / ?1)";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        theTest.setArgumentNames(myArgumentNames);

        theTest.setArguments(new Vector());
        theTest.getArguments().addElement(new Integer(2));

        return theTest;
    }

    public static BinaryOperatorWithParameterTest getNumericTwoParameterMultipleOperators() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Multiple operators with two parameters");

        String parameterNameForDivide = "amountToDivide";
        String parameterNameForMultiply = "amountToMultiply";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterNameForDivide);
        theTest.getExpressionParameters().add(parameterNameForMultiply);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.subtract(ExpressionMath.add(builder.get("salary"), 10000), ExpressionMath.multiply(ExpressionMath.divide(ExpressionBuilder.fromConstant(new Integer(10000), builder), builder.getParameter(parameterNameForDivide)), builder.getParameter(parameterNameForMultiply))).greaterThanEqual(50000);
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.salary + 10000 - 10000 / ?1 * ?2 >= 50000";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        myArgumentNames.add("2");
        theTest.setArgumentNames(myArgumentNames);

        theTest.setArguments(new Vector());
        theTest.getArguments().addElement(new Integer(2));
        theTest.getArguments().addElement(new Integer(3));

        return theTest;
    }

    public static BinaryOperatorWithParameterTest getNumericTwoParameterMultipleOperatorsWithBracketsAroundPlusMinus() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Multiple operators with two parameters with brackets around plus/minus");

        String parameterNameForDivide = "amountToDivide";
        String parameterNameForMultiply = "amountToMultiply";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterNameForDivide);
        theTest.getExpressionParameters().add(parameterNameForMultiply);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.multiply(ExpressionMath.divide(ExpressionMath.subtract(ExpressionMath.add(builder.get("salary"), 10), 20), builder.getParameter(parameterNameForDivide)), builder.getParameter(parameterNameForMultiply)).greaterThanEqual(70000);
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE (emp.salary + 10 - 20) / ?1 * ?2 >= 70000";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        myArgumentNames.add("2");
        theTest.setArgumentNames(myArgumentNames);

        theTest.setArguments(new Vector());
        theTest.getArguments().addElement(new Integer(2));
        theTest.getArguments().addElement(new Integer(3));

        return theTest;
    }

    public static BinaryOperatorWithParameterTest getNumericTwoParameterMultipleOperatorsWithBracketsAroundMultiply() {
        BinaryOperatorWithParameterTest theTest = new BinaryOperatorWithParameterTest();
        theTest.setName("Multiple operators with two parameters with brackets around multiply");

        String parameterNameForDivide = "amountToDivide";
        String parameterNameForMultiply = "amountToMultiply";
        theTest.setExpressionParameters(new Vector());
        theTest.getExpressionParameters().add(parameterNameForDivide);
        theTest.getExpressionParameters().add(parameterNameForMultiply);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("salary").greaterThan(ExpressionMath.subtract(ExpressionMath.add(ExpressionBuilder.fromConstant(new Integer(50000), builder), 10000), ExpressionMath.divide(ExpressionBuilder.fromConstant(new Integer(10000), builder), ExpressionMath.multiply(builder.getParameter(parameterNameForMultiply), builder.getParameter(parameterNameForDivide)))));
        theTest.setOriginalObjectExpression(whereClause);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.salary > (50000 + 10000 - 10000 / (?1 * ?2))";

        theTest.setEjbqlString(ejbqlString);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        myArgumentNames.add("2");
        theTest.setArgumentNames(myArgumentNames);

        theTest.setArguments(new Vector());
        theTest.getArguments().addElement(new Integer(2));
        theTest.getArguments().addElement(new Integer(5));

        return theTest;
    }

    public static TestSuite getParameterBinaryOperatorTests() {
        TestSuite theSuite = new TestSuite();

        theSuite.setName("Parameter Binary Operator Test Suite");
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericParameterGreaterThanTest());
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericParameterGreaterThanEqualTest());
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericParameterLessThanTest());
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericParameterLessThanEqualTest());
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericParameterPlusTest());
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericParameterPlusTestWithBracketsBeforeComparison());
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericParameterPlusTestWithBracketsAfterComparison());
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericParameterMinusTest());
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericParameterMinusTestWithBracketsBeforeComparison());
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericParameterMinusTestWithBracketsAfterComparison());
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericParameterMultiplyTest());
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericParameterMultiplyTestWithBracketsBeforeComparison());
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericParameterMultiplyTestWithBracketsAfterComparison());
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericParameterDivideTest());
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericParameterDivideTestWithBracketsBeforeComparison());
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericParameterDivideTestWithBracketsAfterComparison());
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericTwoParameterMultipleOperators());
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericTwoParameterMultipleOperatorsWithBracketsAroundPlusMinus());
        theSuite.addTest(BinaryOperatorWithParameterTest.getNumericTwoParameterMultipleOperatorsWithBracketsAroundMultiply());
        return theSuite;
    }

    private void setArgumentsForTestUsing(Vector employees) {
        setArguments(new Vector());

        Enumeration names = getExpressionParameters().elements();
        Enumeration employeeEnum = employees.elements();
        while (names.hasMoreElements()) {
            Employee emp = (Employee)employeeEnum.nextElement();
            getArguments().add(emp.getId());
            names.nextElement();
        }
    }

    public void setup() {
        //JGL: If the arguments are already set by the test, don't 
        //set them again
        if (!hasArguments()) {
            setArgumentsForTestUsing(getSomeEmployees());
        }
        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(getOriginalObjectExpression());
        // Add all the arguments to the query
        Enumeration enumtr = getExpressionParameters().elements();
        while (enumtr.hasMoreElements()) {
            raq.addArgument((String)enumtr.nextElement());
        }

        // Save the retrieved employees for the verify
        setOriginalOject(getSession().executeQuery(raq, getArguments()));

        // Finish the setup
        super.setup();
    }

    private void setExpressionParameters(Vector theArgumentNames) {
        expressionParameters = theArgumentNames;
    }

    private Vector getExpressionParameters() {
        return expressionParameters;
    }
}
