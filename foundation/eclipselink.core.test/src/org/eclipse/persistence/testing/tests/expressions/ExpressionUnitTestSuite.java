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
package org.eclipse.persistence.testing.tests.expressions;

import java.util.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.platform.database.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test suite is designed to cover all the API in
 * org.eclipse.persistence.expressions.Expression.
 * This is in addition to what is covered in ExpressionTestSuite.
 * Since 9.0.4
 * @author Stephen McRitchie
 */
public class ExpressionUnitTestSuite extends ExpressionTestSuite {
    protected PopulationManager manager;

    public ExpressionUnitTestSuite() {
        setDescription("This suite covers the public/advanced API of Expression.");
    }

    protected void _addAllOfTest() {
        ExpressionBuilder employee = new ExpressionBuilder();
        ExpressionBuilder phone = new ExpressionBuilder();
        Expression expression = employee.allOf("phoneNumbers", phone.get("areaCode").equal("613"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 3);
        test.setExpression(expression);
        test.setSupportedInMemory(false);
        test.setName("AllOfTest");
        test.setDescription("Test allOf expression");
        addTest(test);
    }

    protected void _addAppendSQLTest() {
        Expression expression = (new ExpressionBuilder()).prefixSQL("(t0.F_NAME = 'Bob')");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setSupportedInMemory(false);
        test.setName("AppendSQLTest");
        test.setDescription("Test appendSQL expression");
        addTest(test);
    }

    /*protected void _addBetween$booleanTest()
    {
        Expression expression = (new ExpressionBuilder()).get("salary").greaterThan(50000).between(true,true);
    
        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 7);
        test.setExpression(expression);
        test.setName("BetweenBooleanTest");
        test.setDescription("Test BETWEEN (boolean) expression");
        addTest(test);
    }*/
    protected void _addBetween$byteTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").between((byte)0, (byte)120);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 3);
        test.setExpression(expression);
        test.setName("Between$byteTest");
        test.setDescription("Test BETWEEN (byte) expression");
        addTest(test);
    }

    protected void _addBetween$charTest() {
        Expression expression = (new ExpressionBuilder()).getField("GENDER").between('M', 'N');

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 7);
        test.setExpression(expression);
        test.setName("Between$charTest");
        test.setDescription("Test BETWEEN (char) expression");
        addTest(test);
    }

    protected void _addBetween$doubleTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").between(3000.0, 6000.0);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 2);
        test.setExpression(expression);
        test.setName("Between$doubleTest");
        test.setDescription("Test BETWEEN (double) expression");
        addTest(test);
    }

    protected void _addBetween$floatTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").between(3000.0F, 6000.0F);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 2);
        test.setExpression(expression);
        test.setName("Between$floatTest");
        test.setDescription("Test BETWEEN (float) expression");
        addTest(test);
    }

    protected void _addBetween$longTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").between(75000L, 87000L);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(expression);
        test.setName("Between$longTest");
        test.setDescription("Test BETWEEN (long) expression");
        addTest(test);
    }

    protected void _addBetween$shortTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").between((short)5000, (short)32000);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("Between$shortTest");
        test.setDescription("Test BETWEEN (short) expression");
        addTest(test);
    }

    protected void _addContainsAllKeyWords$nullTest() {
        Expression expression = (new ExpressionBuilder()).anyOf("responsibilitiesList").containsAllKeyWords("");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 6);
        test.setExpression(expression);
        test.setName("ContainsAllKeyWords$nullTest");
        test.setDescription("Test containsAllKeyWords with wildcard % expression");
        addTest(test);
    }

    protected void _addContainsAllKeyWordsTest() {
        Expression expression = (new ExpressionBuilder()).anyOf("responsibilitiesList").containsAllKeyWords("java write");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("ContainsAllKeyWordsTest");
        test.setDescription("Test containsAllKeyWords expression");
        addTest(test);
    }

    protected void _addContainsAnyKeyWords$nullTest() {
        Expression expression = (new ExpressionBuilder()).anyOf("responsibilitiesList").containsAnyKeyWords("");
        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 6);
        test.setExpression(expression);
        test.setName("ContainsAnyKeyWords$nullTest");
        test.setDescription("Test containsAnyKeyWords with wildcard % expression");
        addTest(test);
    }

    protected void _addContainsAnyKeyWordsTest() {
        Expression expression = (new ExpressionBuilder()).anyOf("responsibilitiesList").containsAnyKeyWords("java write");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 3);
        test.setExpression(expression);
        test.setName("ContainsAnyKeyWordsTest");
        test.setDescription("Test containsAnyKeyWords expression");
        addTest(test);
    }

    protected void _addContainsSubstringIgnoringCaseTest() {
        Expression expression = (new ExpressionBuilder()).anyOf("responsibilitiesList").containsSubstringIgnoringCase("tHe CoFfEe");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("ContainsSubstringIgnoringCaseTest");
        test.setDescription("Test containsSubstringIgnoringCase expression");
        addTest(test);
    }

    protected void _addContainsSubstringTest() {
        Expression expression = (new ExpressionBuilder()).anyOf("responsibilitiesList").containsSubstring("the coffee");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("ContainsSubstringTest");
        test.setDescription("Test containsSubstring expression");
        addTest(test);
    }

    protected void _addCurrentDateTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("period").get("startDate").lessThan(builder.currentDate());

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setExpression(expression);
        test.setName("CurrentDateTest");
        test.setDescription("Test currentDate expression");
        test.addUnsupportedPlatform(DB2Platform.class);
        test.addUnsupportedPlatform(TimesTenPlatform.class);
        addTest(test);
    }

    protected void _addDifferenceTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("firstName").difference("Bib").equal(4);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("DifferenceTest");
        test.setDescription("Test Difference expression");
        test.addSupportedPlatform(SybasePlatform.class);
        test.addSupportedPlatform(SQLAnywherePlatform.class);
        addTest(test);
    }

    /*
     * The following are equal(primitive) tests...
     */
    protected void _addEqual$booleanTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").equal(false);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("Equal$booleanTest");
        test.setDescription("Test EQUAL (boolean) expression");
        addTest(test);
    }

    protected void _addEqual$byteTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").equal((byte)100).not();

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 5);
        test.setExpression(expression);
        test.setName("Equal$byteTest");
        test.setDescription("Test Equal (byte) expression");
        addTest(test);
    }

    protected void _addEqual$charTest() {
        Expression expression = (new ExpressionBuilder()).getField("GENDER").equal('M');

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 7);
        test.setExpression(expression);
        test.setName("Equal$charTest");
        test.setDescription("Test EQUAL (char) expression");
        addTest(test);
    }

    protected void _addEqual$doubleTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").equal(4000.98);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 1);
        test.setExpression(expression);
        test.setName("Equal$doubleTest");
        test.setDescription("Test Equal (double) expression");
        addTest(test);
    }

    /**
     * This tests the equals(float) API.
     * floats are (base 2) approximations, and cannot represent all numbers,
     * so this test was changed to use 5000 to avoid float conversion issues.
     */
    protected void _addEqual$floatTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression budget = builder.get("budget");
        Expression expression = ExpressionMath.max(budget, 0).equal(5000F);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 1);
        test.setExpression(expression);
        test.setName("Equal$floatTest");
        test.setDescription("Test Equal (float) expression");
        test.addUnsupportedPlatform(DB2Platform.class);
        test.addUnsupportedPlatform(SybasePlatform.class);
        test.addUnsupportedPlatform(SQLAnywherePlatform.class);
        test.addUnsupportedPlatform(SQLServerPlatform.class);
        test.addUnsupportedPlatform(org.eclipse.persistence.platform.database.TimesTenPlatform.class);
        addTest(test);
    }

    protected void _addEqual$longTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").equal(75000L);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("Equal$longTest");
        test.setDescription("Test Equal (long) expression");
        addTest(test);
    }

    protected void _addEqual$shortTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").equal((short)31000);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("Equal$shortTest");
        test.setDescription("Test Equal (short) expression");
        addTest(test);
    }

    protected void _addGetFunction$int$VectorTest() {
        // The selector should be unique and the selectors for pre-defined operators are > 0.
        int applyRaiseSelector = 0 - "applyRaise".hashCode();
        Vector v = new Vector();
        v.addElement("(");
        v.addElement(" * (100 + ");
        v.addElement(") / 100)");
        ExpressionOperator applyRaiseOperator = new ExpressionOperator(applyRaiseSelector, v);
        applyRaiseOperator.bePrefix();
        ExpressionOperator.addOperator(applyRaiseOperator);

        // The following query will select all employees who will still have a salary under
        // $50,000 after a 15% raise.
        Vector arguments = new Vector();
        arguments.addElement(new Integer(15));
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("salary").getFunction(applyRaiseSelector, arguments).lessThan(50000);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 3);
        test.setExpression(expression);
        test.setSupportedInMemory(false);
        test.setName("GetFunction$int$Vector$Test");
        test.setDescription("Test GetFunction(operator,arguments) expression");
        addTest(test);
    }

    /*
     * The following are greaterThan(primitive) tests...
     */
    protected void _addGetFunctionWithArgumentsTest() {
        Vector arguments = new Vector();
        arguments.addElement(new String("Smith"));
        Expression expression = (new ExpressionBuilder()).get("firstName").getFunctionWithArguments("Concat", arguments).equal("BobSmith");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.addSupportedPlatform(OraclePlatform.class);
        test.addSupportedPlatform(MySQLPlatform.class);
        test.setName("GetFunctionWithArgumentsTest");
        test.setDescription("Test GetFunctionWithArguments expression");
        addTest(test);
    }

    /*
     * The following are greaterThan(primitive) tests...
     */
    protected void _addGreaterThan$booleanTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").greaterThan(true);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setExpression(expression);
        test.setName("GreaterThan$booleanTest");
        test.setDescription("Test GreaterThan (boolean) expression");
        addTest(test);
    }

    protected void _addGreaterThan$byteTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").greaterThan((byte)100);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 4);
        test.setExpression(expression);
        test.setName("GreaterThan$byteTest");
        test.setDescription("Test GreaterThan (byte) expression");
        addTest(test);
    }

    protected void _addGreaterThan$charTest() {
        Expression expression = (new ExpressionBuilder()).getField("GENDER").greaterThan('F');

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 7);
        test.setExpression(expression);
        test.setName("GreaterThan$charTest");
        test.setDescription("Test GreaterThan (char) expression");
        addTest(test);
    }

    protected void _addGreaterThan$doubleTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").greaterThan(4000.98);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 1);
        test.setExpression(expression);
        test.setName("GreaterThan$doubleTest");
        test.setDescription("Test GreaterThan (double) expression");
        addTest(test);
    }

    protected void _addGreaterThan$floatTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").greaterThan(4000F);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 2);
        test.setExpression(expression);
        test.setName("GreaterThan$floatTest");
        test.setDescription("Test GreaterThan (float) expression");
        addTest(test);
    }

    protected void _addGreaterThan$longTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").greaterThan(75000L);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 3);
        test.setExpression(expression);
        test.setName("GreaterThan$longTest");
        test.setDescription("Test GreaterThan (long) expression");
        addTest(test);
    }

    protected void _addGreaterThan$shortTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").greaterThan((short)32000);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 11);
        test.setExpression(expression);
        test.setName("GreaterThan$shortTest");
        test.setDescription("Test GreaterThan (short) expression");
        addTest(test);
    }

    /*
     * The following are greaterThanEqual(primitive) tests...
     */
    protected void _addGreaterThanEqual$booleanTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").greaterThanEqual(true);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setExpression(expression);
        test.setName("GreaterThanEqual$booleanTest");
        test.setDescription("Test GreaterThanEqual (boolean) expression");
        addTest(test);
    }

    protected void _addGreaterThanEqual$byteTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").greaterThanEqual((byte)100);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 4);
        test.setExpression(expression);
        test.setName("GreaterThanEqual$byteTest");
        test.setDescription("Test GreaterThanEqual (byte) expression");
        addTest(test);
    }

    protected void _addGreaterThanEqual$charTest() {
        Expression expression = (new ExpressionBuilder()).getField("GENDER").greaterThanEqual('M');

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 7);
        test.setExpression(expression);
        test.setName("GreaterThanEqual$charTest");
        test.setDescription("Test GreaterThanEqual (char) expression");
        addTest(test);
    }

    protected void _addGreaterThanEqual$doubleTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").greaterThanEqual(4000.98);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 2);
        test.setExpression(expression);
        test.setName("GreaterThanEqual$doubleTest");
        test.setDescription("Test GreaterThanEqual (double) expression");
        addTest(test);
    }

    protected void _addGreaterThanEqual$floatTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").greaterThanEqual(4000.98F);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 2);
        test.setExpression(expression);
        test.setName("GreaterThanEqual$floatTest");
        test.setDescription("Test GreaterThanEqual (float) expression");
        addTest(test);
    }

    protected void _addGreaterThanEqual$longTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").greaterThanEqual(75000L);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 4);
        test.setExpression(expression);
        test.setName("GreaterThanEqual$longTest");
        test.setDescription("Test GreaterThanEqual (long) expression");
        addTest(test);
    }

    protected void _addGreaterThanEqual$shortTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").greaterThanEqual((short)31000);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setExpression(expression);
        test.setName("GreaterThanEqual$shortTest");
        test.setDescription("Test GreaterThanEqual (short) expression");
        addTest(test);
    }

    protected void _addHexToRawTest() {
        Expression expression = (new ExpressionBuilder()).get("firstName").hexToRaw().equal("Bob");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("HexToRawTest");
        test.setDescription("Test HexToRaw expression");
        addTest(test);
    }

    /*
     * The following are in(primitive[]) tests...
     */
    protected void _addIn$booleanTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").in(new boolean[] { true });

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("In$booleanTest");
        test.setDescription("Test In (boolean) expression");
        addTest(test);
    }

    protected void _addIn$byteTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").in(new byte[] { (byte)100 }).not();

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 5);
        test.setExpression(expression);
        test.setName("In$byteTest");
        test.setDescription("Test In (byte) expression");
        addTest(test);
    }

    protected void _addIn$charTest() {
        Expression expression = (new ExpressionBuilder()).getField("GENDER").in(new char[] { 'A', 'N', 'M' });

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 7);
        test.setExpression(expression);
        test.setName("In$charTest");
        test.setDescription("Test In (char) expression");
        addTest(test);
    }

    protected void _addIn$doubleTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").in(new double[] { 4000.98 });

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 1);
        test.setExpression(expression);
        test.setName("In$doubleTest");
        test.setDescription("Test In (double) expression");
        addTest(test);
    }

    /**
     * Test changed to choose float value that can be represented as a float without conversion issues.
     * This method also tests ExpressionMath.subtract(Expression, int)
     */
    protected void _addIn$floatTest() {
        Expression expression = 
            ExpressionMath.subtract((new ExpressionBuilder()).get("budget"), 1000).in(new float[] { 4000F });

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 1);
        test.setExpression(expression);
        test.setName("In$floatTest");
        test.setDescription("Test In (float) expression");
        addTest(test);
    }

    protected void _addIn$intTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").in(new int[] { 2, 5654443, 75000 });

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("In$intTest");
        test.setDescription("Test In (int) expression");
        addTest(test);
    }

    protected void _addIn$longTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").in(new long[] { 2L, 5654443L, 75000L });

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("In$longTest");
        test.setDescription("Test In (long) expression");
        addTest(test);
    }

    protected void _addIn$shortTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").in(new short[] { (short)-31000, (short)31000 });

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("In$shortTest");
        test.setDescription("Test In (short) expression");
        addTest(test);
    }

    protected void _addIs___ExpressionTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("salary").greaterThan(0).not();

        if (!exp.isCompoundExpression() && !exp.isDataExpression() && !exp.isLiteralExpression() && !exp.isLogicalExpression()) {
            exp = exp.not();
        }

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setExpression(exp);
        test.setName("Is___ExpressionTest");
        test.setDescription("Test is{Compound,Data,Literal,Logical}Expression");
        addTest(test);
    }

    private void _addIsEmptyTest() {
        ExpressionBuilder builder = new ExpressionBuilder();

        Expression expression = builder.isEmpty("projects");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 6);
        test.setExpression(expression);
        test.testBatchAttributesOnEmployee();
        test.setName("IsEmptyWithBatchAttributeTest");
        test.setDescription("Test batch reading attributes from query with isEmpty.");
        test.addUnsupportedPlatform(org.eclipse.persistence.platform.database.TimesTenPlatform.class);
        addTest(test);
    }

    protected void _addLengthTest() {
        Expression expression = (new ExpressionBuilder()).get("firstName").length().equal(3);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("LengthTest");
        test.setDescription("Test Length expression");
        addTest(test);
    }

    /*
     * The following are lessThan(primitive) tests...
     */
    protected void _addLessThan$booleanTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").lessThan(true);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("LessThan$booleanTest");
        test.setDescription("Test LessThan (boolean) expression");
        addTest(test);
    }

    protected void _addLessThan$byteTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").lessThan((byte)100);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 1);
        test.setExpression(expression);
        test.setName("LessThan$byteTest");
        test.setDescription("Test LessThan (byte) expression");
        addTest(test);
    }

    protected void _addLessThan$charTest() {
        Expression expression = (new ExpressionBuilder()).getField("GENDER").lessThan('M');

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 5);
        test.setExpression(expression);
        test.setName("LessThan$charTest");
        test.setDescription("Test LessThan (char) expression");
        addTest(test);
    }

    protected void _addLessThan$doubleTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").lessThan(4000.98);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 3);
        test.setExpression(expression);
        test.setName("LessThan$doubleTest");
        test.setDescription("Test LessThan (double) expression");
        addTest(test);
    }

    protected void _addLessThan$floatTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").lessThan(4000.98F);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 3);
        test.setExpression(expression);
        test.setName("LessThan$floatTest");
        test.setDescription("Test LessThan (float) expression");
        addTest(test);
    }

    protected void _addLessThan$longTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").lessThan(75000L);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 8);
        test.setExpression(expression);
        test.setName("LessThan$longTest");
        test.setDescription("Test LessThan (long) expression");
        addTest(test);
    }

    protected void _addLessThan$shortTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").lessThan((short)32000);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("LessThan$shortTest");
        test.setDescription("Test LessThan (short) expression");
        addTest(test);
    }

    /*
     * The following are lessThanEqual(primitive) tests...
     */
    protected void _addLessThanEqual$booleanTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").lessThanEqual(true);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("LessThanEqual$booleanTest");
        test.setDescription("Test LessThanEqual (boolean) expression");
        addTest(test);
    }

    protected void _addLessThanEqual$byteTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").lessThanEqual((byte)100);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 1);
        test.setExpression(expression);
        test.setName("LessThanEqual$byteTest");
        test.setDescription("Test LessThanEqual (byte) expression");
        addTest(test);
    }

    protected void _addLessThanEqual$charTest() {
        Expression expression = (new ExpressionBuilder()).getField("GENDER").lessThanEqual('F');

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 5);
        test.setExpression(expression);
        test.setName("LessThanEqual$charTest");
        test.setDescription("Test LessThanEqual (char) expression");
        addTest(test);
    }

    protected void _addLessThanEqual$doubleTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").lessThanEqual(4000.98);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 4);
        test.setExpression(expression);
        test.setName("LessThanEqual$doubleTest");
        test.setDescription("Test LessThanEqual (double) expression");
        addTest(test);
    }

    protected void _addLessThanEqual$floatTest() {
        Expression expression = ExpressionMath.add((new ExpressionBuilder()).get("budget"), 1000).lessThanEqual(6000F);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 5);
        test.setExpression(expression);
        test.setName("LessThanEqual$floatTest");
        test.setDescription("Test LessThanEqual (float) expression");
        addTest(test);
    }

    protected void _addLessThanEqual$longTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").lessThanEqual(75000L);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 9);
        test.setExpression(expression);
        test.setName("LessThanEqual$longTest");
        test.setDescription("Test LessThanEqual (long) expression");
        addTest(test);
    }

    protected void _addLessThanEqual$shortTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").lessThanEqual((short)31000);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("LessThanEqual$shortTest");
        test.setDescription("Test LessThanEqual (short) expression");
        addTest(test);
    }

    protected void _addLocate$StringTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("firstName").locate("ob").equal(2);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("Locate$StringTest");
        test.setDescription("Test locate(String) expression");
        addTest(test);
    }

    protected void _addLocate$String$intTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("responsibilities").locate("coffee", 4).greaterThan(0);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("Locate$String$intTest");
        test.setDescription("Test locate(String, int) expression");
        addTest(test);
    }

    protected void _addMonthsBetweenTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("period").get("startDate").monthsBetween(builder.currentDate()).lessThan(0);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setExpression(expression);
        test.setName("MonthsBetweenTest");
        test.setDescription("Test monthsBetween expression");
        test.addSupportedPlatform(OraclePlatform.class);
        addTest(test);
    }

    protected void _addNextDayTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("period").get("startDate").lessThan(builder.get("period").get("startDate").nextDay("Sunday"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setExpression(expression);
        test.setName("NextDayTest");
        test.setDescription("Test nextDay expression");
        test.addSupportedPlatform(OraclePlatform.class);
        addTest(test);
    }

    protected void _addNoneOfTest() {
        ExpressionBuilder employee = new ExpressionBuilder();
        ExpressionBuilder phone = new ExpressionBuilder();
        Expression expression = employee.noneOf("phoneNumbers", phone.get("areaCode").equal("613"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(expression);
        test.setSupportedInMemory(false);
        test.setName("NoneOfTest");
        test.setDescription("Test noneOf expression");
        addTest(test);
    }

    /*
     * The following tests notBetween.
     */
    protected void _addNotBetween$byteTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").notBetween((byte)0, (byte)120);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 2);
        test.setExpression(expression);
        test.setName("NotBetween$byteTest");
        test.setDescription("Test NotBetween (byte) expression");
        addTest(test);
    }

    protected void _addNotBetween$charTest() {
        Expression expression = (new ExpressionBuilder()).getField("GENDER").notBetween('M', 'N');

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 5);
        test.setExpression(expression);
        test.setName("NotBetween$charTest");
        test.setDescription("Test NotBetween (char) expression");
        addTest(test);
    }

    protected void _addNotBetween$doubleTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").notBetween(3000.0, 6000.0);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 3);
        test.setExpression(expression);
        test.setName("NotBetween$doubleTest");
        test.setDescription("Test NotBetween (double) expression");
        addTest(test);
    }

    protected void _addNotBetween$ExpressionTest() {
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression expression = emp.get("salary").notBetween(emp.anyOf("managedEmployees").get("salary"), emp.get("manager").get("salary"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 3);
        test.setExpression(expression);
        test.setSupportedInMemory(false);
        test.setName("NotBetween$ExpressionTest");
        test.setDescription("Test NotBetween (expression) expression");
        addTest(test);
    }

    protected void _addNotBetween$floatTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").notBetween(3000.0F, 6000.0F);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 3);
        test.setExpression(expression);
        test.setName("NotBetween$floatTest");
        test.setDescription("Test NotBetween (float) expression");
        addTest(test);
    }

    protected void _addNotBetween$intTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").notBetween(75000, 87000);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 10);
        test.setExpression(expression);
        test.setName("NotBetween$intTest");
        test.setDescription("Test NotBetween (int) expression");
        addTest(test);
    }

    protected void _addNotBetween$longTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").notBetween(75000L, 87000L);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 10);
        test.setExpression(expression);
        test.setName("NotBetween$longTest");
        test.setDescription("Test NotBetween (long) expression");
        addTest(test);
    }

    protected void _addNotBetween$ObjectTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("salary").notBetween(builder.get("manager").get("salary"), new Integer(500000));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 3);
        test.setExpression(expression);
        test.setName("NotBetween$ObjectTest");
        test.setDescription("Test NotBetween (Object) expression");
        addTest(test);
    }

    protected void _addNotBetween$shortTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").notBetween((short)5000, (short)32000);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 11);
        test.setExpression(expression);
        test.setName("NotBetween$shortTest");
        test.setDescription("Test NotBetween (short) expression");
        addTest(test);
    }

    private void _addNotEmptyTest() {
        ExpressionBuilder builder = new ExpressionBuilder();

        Expression expression = builder.notEmpty("projects");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 6);
        test.setExpression(expression);
        test.testBatchAttributesOnEmployee();
        test.setName("NotEmptyWithBatchAttributeTest");
        test.setDescription("Test batch reading attributes from query with notEmpty.");
        test.addUnsupportedPlatform(org.eclipse.persistence.platform.database.TimesTenPlatform.class);
        addTest(test);
    }

    /*
     * The following are notEqual(primitive) tests...
     */
    protected void _addNotEqual$booleanTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").notEqual(true);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setExpression(expression);
        test.setName("NotEqual$booleanTest");
        test.setDescription("Test NotEqual (boolean) expression");
        addTest(test);
    }

    protected void _addNotEqual$byteTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").notEqual((byte)100);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 5);
        test.setExpression(expression);
        test.setName("NotEqual$byteTest");
        test.setDescription("Test NotEqual (byte) expression");
        addTest(test);
    }

    protected void _addNotEqual$charTest() {
        Expression expression = (new ExpressionBuilder()).getField("GENDER").notEqual('M');

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 5);
        test.setExpression(expression);
        test.setName("NotEqual$charTest");
        test.setDescription("Test NotEqual (char) expression");
        addTest(test);
    }

    protected void _addNotEqual$doubleTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").notEqual(4000.98);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 4);
        test.setExpression(expression);
        test.setName("NotEqual$doubleTest");
        test.setDescription("Test NotEqual (double) expression");
        addTest(test);
    }

    /**
     * This method also tests ExpressionMath.divide(Expression, int).
     */
    protected void _addNotEqual$floatTest() {
        Expression expression = ExpressionMath.divide((new ExpressionBuilder()).get("budget"), 1).notEqual(5000F);

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 4);
        test.setExpression(expression);
        test.setName("NotEqual$floatTest");
        test.setDescription("Test NotEqual (float) expression");
        addTest(test);
    }

    protected void _addNotEqual$longTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").notEqual(75000L);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 11);
        test.setExpression(expression);
        test.setName("NotEqual$longTest");
        test.setDescription("Test NotEqual (long) expression");
        addTest(test);
    }

    protected void _addNotEqual$shortTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").notEqual((short)31000);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 11);
        test.setExpression(expression);
        test.setName("NotEqual$shortTest");
        test.setDescription("Test NotEqual (short) expression");
        addTest(test);
    }

    /*
     * The following are notIn(primitive[]) tests...
     */
    protected void _addNotIn$booleanTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").notIn(new boolean[] { false, true });

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setExpression(expression);
        test.setName("NotIn$booleanTest");
        test.setDescription("Test NotIn (boolean) expression");
        addTest(test);
    }

    protected void _addNotIn$byteTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").notIn(new byte[] { (byte)100 });

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 5);
        test.setExpression(expression);
        test.setName("NotIn$byteTest");
        test.setDescription("Test NotIn (byte) expression");
        addTest(test);
    }

    protected void _addNotIn$charTest() {
        Expression expression = (new ExpressionBuilder()).getField("GENDER").notIn(new char[] { 'A', 'N', 'M' });

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 5);
        test.setExpression(expression);
        test.setName("NotIn$charTest");
        test.setDescription("Test NotIn (char) expression");
        addTest(test);
    }

    protected void _addNotIn$doubleTest() {
        Expression expression = (new ExpressionBuilder()).get("budget").notIn(new double[] { 4000.98 });

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 4);
        test.setExpression(expression);
        test.setName("NotIn$doubleTest");
        test.setDescription("Test NotIn (double) expression");
        addTest(test);
    }

    /**
     * This method also tests ExpressionMath.subtract(Expression, int)
     */
    protected void _addNotIn$floatTest() {
        Expression expression = 
            ExpressionMath.subtract((new ExpressionBuilder()).get("budget"), 1000).notIn(new float[] { 4000F });

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 4);
        test.setExpression(expression);
        test.setName("NotIn$floatTest");
        test.setDescription("Test NotIn (float) expression");
        addTest(test);
    }

    protected void _addNotIn$intTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").notIn(new int[] { 2, 5654443, 75000 });

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 11);
        test.setExpression(expression);
        test.setName("In$intTest");
        test.setDescription("Test NotIn (int) expression");
        addTest(test);
    }

    protected void _addNotIn$longTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").notIn(new long[] { 2L, 5654443L, 75000L });

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 11);
        test.setExpression(expression);
        test.setName("NotIn$longTest");
        test.setDescription("Test NotIn (long) expression");
        addTest(test);
    }

    protected void _addNotIn$shortTest() {
        Expression expression = (new ExpressionBuilder()).get("salary").notIn(new short[] { (short)-31000, (short)31000 });

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 11);
        test.setExpression(expression);
        test.setName("In$shortTest");
        test.setDescription("Test In (short) expression");
        addTest(test);
    }

    protected void _addOr$DifferentBuildersTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ExpressionBuilder builder2 = new ExpressionBuilder();
        Expression expression1 = builder.get("firstName").equal("Bob");
        Expression expression2 = builder2.get("firstName").equal("Betty");
        Expression expression = expression1.or(expression2);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(expression);
        test.setName("Or$DifferentBuildersTest");
        test.setDescription("Test Or(DifferentBuilders) expression");
        addTest(test);
    }

    /**
     * Tests 'appending' sql to an existing selection criteria.
     * For bug 2872161.
     */
    protected void _addPostfixSQLTest() {
        Expression expression = (new ExpressionBuilder()).get("lastName").equal("Way");
        expression = expression.postfixSQL(" AND (t0.F_NAME = 'Sarah')");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("PostfixSQLTest");
        test.setDescription("Test postfixSQL expression");
        test.addSupportedPlatform(OraclePlatform.class);
        test.addSupportedPlatform(MySQLPlatform.class);
        addTest(test);
    }

    /**
     * Tests 'appending' sql to an existing selection criteria.
     * The custom sql appears before the sql for the expression it is
     * applied to.
     * For bug 2872161.
     */
    protected void _addPrefixSQLTest() {
        Expression expression = (new ExpressionBuilder()).get("lastName").equal("Way");
        expression = expression.prefixSQL("(t0.F_NAME = 'Sarah') AND ");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("PrefixSQLTest");
        test.setDescription("Test prefixSQL expression");
        test.addSupportedPlatform(OraclePlatform.class);
        test.addSupportedPlatform(MySQLPlatform.class);
        addTest(test);
    }

    /**
     * For bug 2916893.
     */
    protected void _addRightTrim$StringTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("firstName").rightTrim("b").equal("Bo");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("RightTrim$StringTest");
        test.setDescription("Test RightTrim(String) expression");
        test.addUnsupportedPlatform(DB2Platform.class);
        test.addUnsupportedPlatform(SybasePlatform.class);
        test.addUnsupportedPlatform(SQLAnywherePlatform.class);
        test.addUnsupportedPlatform(SQLServerPlatform.class);
        test.addUnsupportedPlatform(MySQLPlatform.class);
        test.addUnsupportedPlatform(TimesTenPlatform.class);
        addTest(test);
    }

    protected void _addRightTrimTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("firstName").equal(builder.value("Bob    ").rightTrim());

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setSupportedInMemory(false);
        test.setName("RightTrimTest");
        test.setDescription("Test RightTrim expression");
        addTest(test);
    }

    protected void _addRoundDateTest() {
        Calendar calendar = new GregorianCalendar(1902, 0, 1);
        Object date = calendar.getTime();

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("period").get("startDate").roundDate("YEAR").equal(builder.value(date).roundDate("YEAR"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 6);
        test.setExpression(expression);
        test.setName("RoundDateTest");
        test.setDescription("Test RoundDate expression");
        addTest(test);
    }

    private void _addSizeTest() {
        ExpressionBuilder builder = new ExpressionBuilder();

        Expression expression = builder.size("projects").greaterThan(2);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 4);
        test.setExpression(expression);
        test.testBatchAttributesOnEmployee();
        test.setName("SizeWithBatchAttributeTest");
        test.setDescription("Test batch reading attributes from query with size expression.");
        test.addUnsupportedPlatform(org.eclipse.persistence.platform.database.TimesTenPlatform.class);
        addTest(test);
    }

    protected void _addStandardDeviationTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ExpressionBuilder subBuilder = new ExpressionBuilder();
        Expression subExpression = subBuilder.get("manager").equal(builder);

        ReportQuery subQuery = new ReportQuery(Employee.class, subExpression);
        subQuery.addStandardDeviation("salary");

        Expression expression = builder.subQuery(subQuery).greaterThan(19000);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.addUnsupportedPlatform(SybasePlatform.class);
        test.addUnsupportedPlatform(SQLAnywherePlatform.class);
        test.addUnsupportedPlatform(TimesTenPlatform.class);
        test.addUnsupportedPlatform(DerbyPlatform.class);
        test.setExpression(expression);
        test.setName("StandardDeviationTest");
        test.setDescription("Test StandardDeviation expression");
        addTest(test);
    }

    protected void _addToCharacterTest() {
        Expression expression = (new ExpressionBuilder()).get("firstName").toCharacter().equal("Bob");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("ToCharacterTest");
        test.setDescription("Test ToCharacter expression");
        addTest(test);
    }

    protected void _addToUpperCasedWordsTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("responsibilitiesList").toUppercaseCasedWords().containsSubstring("The Coffee");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("ToUpperCasedWordsTest");
        test.setDescription("Test toUpperCasedWords expression");
        test.addSupportedPlatform(OraclePlatform.class);
        test.addSupportedPlatform(PostgreSQLPlatform.class);
        addTest(test);
    }

    /*
     * The following are value(primitive) tests...
     */
    protected void _addValue$booleanTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("salary").equal(builder.value(false));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("Value$booleanTest");
        test.setDescription("Test Value (boolean) expression");
        addTest(test);
    }

    protected void _addValue$byteTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("budget").equal(builder.value((byte)100)).not();

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 5);
        test.setExpression(expression);
        test.setName("Value$byteTest");
        test.setDescription("Test Value (byte) expression");
        addTest(test);
    }

    protected void _addValue$charTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.getField("GENDER").equal(builder.value('M'));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 7);
        test.setExpression(expression);
        test.setName("Value$charTest");
        test.setDescription("Test Value (char) expression");
        addTest(test);
    }

    protected void _addValue$doubleTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("budget").equal(builder.value(4000.98));

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 1);
        test.setExpression(expression);
        test.setName("Value$doubleTest");
        test.setDescription("Test Value (double) expression");
        addTest(test);
    }

    /**
     * This method also tests ExpressionMath.add(Expression, int);
     */
    protected void _addValue$floatTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = ExpressionMath.add(builder.get("budget"), 0).equal(builder.value(5000F));

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 1);
        test.setExpression(expression);
        test.setName("Value$floatTest");
        test.setDescription("Test Value (float) expression");
        addTest(test);
    }

    protected void _addValue$longTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("salary").equal(builder.value(75000L));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("Value$longTest");
        test.setDescription("Test Value (long) expression");
        addTest(test);
    }

    protected void _addValue$shortTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("salary").equal(builder.value((short)31000));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("Value$shortTest");
        test.setDescription("Test Value (short) expression");
        addTest(test);
    }

    protected void _addVarianceTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ExpressionBuilder subBuilder = new ExpressionBuilder();
        Expression subExpression = subBuilder.get("manager").equal(builder);

        ReportQuery subQuery = new ReportQuery(Employee.class, subExpression);
        subQuery.addVariance("salary");

        Expression expression = builder.subQuery(subQuery).greaterThan(1000000);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.addUnsupportedPlatform(SybasePlatform.class);
        test.addUnsupportedPlatform(SQLAnywherePlatform.class);
        test.addUnsupportedPlatform(SQLServerPlatform.class);
        test.addUnsupportedPlatform(DerbyPlatform.class);
        test.addUnsupportedPlatform(TimesTenPlatform.class);
        test.setExpression(expression);
        test.setName("VarianceTest");
        test.setDescription("Test Variance expression");
        addTest(test);
    }

    public void _testAliasForTableTest() {
        // Uses ConstantExpression to test methods defined in abstract Expression.
        Expression exp = (new ExpressionBuilder()).value(0);
        if (exp.aliasForTable(null) != null) {
            throw new TestErrorException("Expression.aliasForTable(...) should return null.");
        }
    }

    public void _testCreate$Expression$Object$ExpressionOperatorTest() {
        // Uses ConstantExpression to test methods defined in abstract Expression.
        Expression exp = (new ExpressionBuilder()).value(0);
        if (exp.create(exp, exp, null) != exp) {
            throw new TestErrorException("Expression.create(Expression, Object, ExpressionOperator) should return itself.");
        }
    }

    public void _testCreate$Expression$Vector$ExpressionOperatorTest() {
        // Uses ConstantExpression to test methods defined in abstract Expression.
        Expression exp = (new ExpressionBuilder()).value(0);
        if (exp.create(exp, new Vector(1), null) != exp) {
            throw new TestErrorException("Expression.create(Expression, Vector, ExpressionOperator) should return itself.");
        }
    }

    public void _testDoesConformTest() {
        try {
            Expression exp = (new ExpressionBuilder()).value(0);
            exp.doesConform(null, null, null, 0);
            throw new TestErrorException("Cannot conform expression exception not thrown.");
        } catch (QueryException e) {
            ;
        }
    }

    public void _testGetField$DatabaseFieldTest() {
        try {
            (new ExpressionBuilder()).value(0).getField(new DatabaseField("Foo"));
            throw new TestErrorException("Expression.getField(DatabaseField) should throw query exception.");
        } catch (QueryException e) {
            ;
        }
    }

    public void _testGetField$StringTest() {
        try {
            (new ExpressionBuilder()).value(0).getField(new String("Foo"));
            throw new TestErrorException("Expression.getField(String) should throw query exception.");
        } catch (QueryException e) {
            ;
        }
    }

    public void _testGetFieldsTest() {
        // Uses ConstantExpression to test methods defined in abstract Expression.
        Expression exp = (new ExpressionBuilder()).value(0);
        if ((exp.getFields() == null) || (exp.getFields().size() != 0)) {
            throw new TestErrorException("Expression.getFields() should return empty vector");
        }
    }

    public void _testGetNameTest() {
        // Uses ConstantExpression to test methods defined in abstract Expression.
        Expression exp = (new ExpressionBuilder()).value(0);
        if (exp.getName() != "") {
            throw new TestErrorException("Expression.getName(\"\") should return empty string.");
        }
    }

    public void _testGetOperatorTest() {
        // Uses ConstantExpression to test methods defined in abstract Expression.
        Expression exp = (new ExpressionBuilder()).value(0);
        if (exp.getOperator() != null) {
            throw new TestErrorException("Expression.getOperator() should return null.");
        }
    }

    public void _testGetTable$DatabaseTableTest() {
        try {
            (new ExpressionBuilder()).value(0).getTable(new DatabaseTable("Foo"));
            throw new TestErrorException("Expression.getTable(DatabaseTable) should throw query exception.");
        } catch (QueryException e) {
            ;
        }
    }

    public void _testGetTable$StringTest() {
        try {
            (new ExpressionBuilder()).value(0).getTable(new String("Foo"));
            throw new TestErrorException("Expression.getTable(String) should throw query exception.");
        } catch (QueryException e) {
            ;
        }
    }

    public void _testTwist$null$ExpressionTest() {
        // Uses ConstantExpression to test methods defined in abstract Expression.
        Expression exp = (new ExpressionBuilder()).value(0);
        if (exp.twist(null, exp) != null) {
            throw new TestErrorException("Expression.twist(exp, newBase) should return null if exp null.");
        }
    }

    public void _testValueFromObjectTest() {
        try {
            Expression exp = (new ExpressionBuilder()).get("salary").greaterThan(0);
            exp.valueFromObject(null, null, null, 0);
            throw new TestErrorException("Cannot conform expression exception not thrown.");
        } catch (QueryException e) {
            ;
        }
    }

    public void addBaseExpressionTests() {
        super.addTests();
    }

    public void addTests() {
        setManager(PopulationManager.getDefaultManager());
        _addAllOfTest();
        _addAppendSQLTest();
        _addBetween$byteTest();
        _addBetween$charTest();
        _addBetween$doubleTest();
        _addBetween$floatTest();
        _addBetween$longTest();
        _addBetween$shortTest();
        _addContainsAllKeyWords$nullTest();
        _addContainsAllKeyWordsTest();
        _addContainsAnyKeyWords$nullTest();
        _addContainsAnyKeyWordsTest();
        _addContainsSubstringTest();
        _addContainsSubstringIgnoringCaseTest();
        _addCurrentDateTest();
        _addDifferenceTest();
        _addEqual$booleanTest();
        _addEqual$byteTest();
        _addEqual$charTest();
        _addEqual$doubleTest();
        _addEqual$floatTest();
        _addEqual$longTest();
        _addEqual$shortTest();
        _addGetFunction$int$VectorTest();
        _addGetFunctionWithArgumentsTest();
        _addGreaterThan$booleanTest();
        _addGreaterThan$byteTest();
        _addGreaterThan$charTest();
        _addGreaterThan$doubleTest();
        _addGreaterThan$floatTest();
        _addGreaterThan$longTest();
        _addGreaterThan$shortTest();
        _addGreaterThanEqual$booleanTest();
        _addGreaterThanEqual$byteTest();
        _addGreaterThanEqual$charTest();
        _addGreaterThanEqual$doubleTest();
        _addGreaterThanEqual$floatTest();
        _addGreaterThanEqual$longTest();
        _addGreaterThanEqual$shortTest();
        // broken _addHexToRawTest();
        _addIn$booleanTest();
        _addIn$byteTest();
        _addIn$charTest();
        _addIn$doubleTest();
        _addIn$floatTest();
        _addIn$intTest();
        _addIn$longTest();
        _addIn$shortTest();
        _addIs___ExpressionTest();
        _addIsEmptyTest();
        _addLengthTest();
        _addLessThan$booleanTest();
        _addLessThan$byteTest();
        _addLessThan$charTest();
        _addLessThan$doubleTest();
        _addLessThan$floatTest();
        _addLessThan$longTest();
        _addLessThan$shortTest();
        _addLessThanEqual$booleanTest();
        _addLessThanEqual$byteTest();
        _addLessThanEqual$charTest();
        _addLessThanEqual$doubleTest();
        _addLessThanEqual$floatTest();
        _addLessThanEqual$longTest();
        _addLessThanEqual$shortTest();
        // broken _addLocate$StringTest();
        // broken _addLocate$String$intTest();
        _addMonthsBetweenTest();
        _addNextDayTest();
        _addNoneOfTest();
        _addNotBetween$byteTest();
        _addNotBetween$charTest();
        _addNotBetween$doubleTest();
        _addNotBetween$ExpressionTest();
        _addNotBetween$floatTest();
        _addNotBetween$intTest();
        _addNotBetween$longTest();
        _addNotBetween$ObjectTest();
        _addNotBetween$shortTest();
        _addNotEmptyTest();
        _addNotEqual$booleanTest();
        _addNotEqual$byteTest();
        _addNotEqual$charTest();
        _addNotEqual$doubleTest();
        _addNotEqual$floatTest();
        _addNotEqual$longTest();
        _addNotEqual$shortTest();
        _addNotIn$booleanTest();
        _addNotIn$byteTest();
        _addNotIn$charTest();
        _addNotIn$doubleTest();
        _addNotIn$floatTest();
        _addNotIn$intTest();
        _addNotIn$longTest();
        _addNotIn$shortTest();
        _addOr$DifferentBuildersTest();
        _addRightTrim$StringTest();
        _addRightTrimTest();
        // broken _addRoundDateTest();
        _addPostfixSQLTest();
        _addPrefixSQLTest();
        _addSizeTest();
        _addStandardDeviationTest();
        // broken _addToCharacterTest();
        _addToUpperCasedWordsTest();
        _addValue$booleanTest();
        _addValue$byteTest();
        _addValue$charTest();
        _addValue$doubleTest();
        _addValue$floatTest();
        _addValue$longTest();
        _addValue$shortTest();
        _addVarianceTest();

        addTest(new UnitTestCase("AliasForTableTest"));
        addTest(new UnitTestCase("Create$Expression$Object$ExpressionOperatorTest"));
        addTest(new UnitTestCase("Create$Expression$Vector$ExpressionOperatorTest"));
        addTest(new UnitTestCase("DoesConformTest"));
        addTest(new UnitTestCase("GetField$DatabaseFieldTest"));
        addTest(new UnitTestCase("GetField$StringTest"));
        addTest(new UnitTestCase("GetFieldsTest"));
        addTest(new UnitTestCase("GetNameTest"));
        addTest(new UnitTestCase("GetOperatorTest"));
        addTest(new UnitTestCase("GetTable$DatabaseTableTest"));
        addTest(new UnitTestCase("GetTable$StringTest"));
        addTest(new UnitTestCase("Twist$null$ExpressionTest"));
        // Should be declared abstract: addTest(new UnitTestCase("TwisedForBaseAndContextTest"));
        addTest(new UnitTestCase("ValueFromObjectTest"));
    }

    protected PopulationManager getManager() {
        return manager;
    }

    protected void setManager(PopulationManager theManager) {
        manager = theManager;
    }
}
