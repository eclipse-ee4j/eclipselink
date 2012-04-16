/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.platform.database.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.collections.Restaurant;
import org.eclipse.persistence.testing.models.multipletable.LargeBusinessProject;
import org.eclipse.persistence.testing.models.ownership.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import java.util.*;

import org.eclipse.persistence.testing.models.inheritance.SalesRep;
import org.eclipse.persistence.testing.models.inheritance.Engineer;
import org.eclipse.persistence.testing.models.inheritance.Computer;
import org.eclipse.persistence.testing.models.inheritance.Vehicle;
import org.eclipse.persistence.testing.models.inheritance.NonFueledVehicle;

public class ExpressionTestSuite extends TestSuite {
    protected PopulationManager manager;

    public ExpressionTestSuite() {
        setDescription("This suite tests expressions.");
    }

    public ExpressionTestSuite(boolean isSRG) {
        super(isSRG);
        setDescription("This suite tests expressions.");
    }

    private void addAdvancedExpressionFunctionTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("salary").lessThan(100000);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(1993, 5, 1, 0, 0, 0);
        exp = exp.and(builder.get("period").get("startDate").dateName("year").equal("1993"));
        exp = exp.and(builder.get("period").get("startDate").addMonths(1).equal(new java.sql.Date(calendar.getTime().getTime())));
        exp = exp.and(builder.get("lastName").asciiValue().equal("84"));
        exp = exp.and(builder.get("period").get("endDate").dateToString().equal("1999-6-12 10:10:10.0"));
        exp = exp.and(builder.get("period").get("endDate").toChar().equal("1999-6-12 10:10:10.0"));
        exp = exp.and(builder.get("period").get("endDate").toChar("day").equal("monday"));

        calendar.set(1999, 1, 1, 0, 0, 0);
        exp = exp.and(builder.get("period").get("startDate").toDate().equal(new java.sql.Date(calendar.getTime().getTime())));
        exp = exp.and(builder.get("period").get("startDate").lastDay().equal(new java.sql.Date(calendar.getTime().getTime())));
        calendar.set(1993, 1, 1, 0, 0, 0);
        exp = exp.and(builder.get("period").get("startDate").truncateDate("year").equal(new java.sql.Date(calendar.getTime().getTime())));
        exp = exp.and(builder.get("period").get("endDate").newTime("EST", "CST").dateToString().equal("1999-6-12 9:10:10.0"));
        exp = exp.and(builder.get("firstName").leftPad(1, " ").equal(" Bob"));
        exp = exp.and(builder.get("firstName").replace("B", "C").equal("Cob"));
        exp = exp.and(builder.get("firstName").rightPad(1, " ").equal("Bob "));
        exp = exp.and(builder.get("salary").toNumber().lessThan(100000));
        exp = exp.and(builder.get("firstName").substring(1, 1).equal("B"));
        exp = exp.and(builder.get("firstName").translate("Bo", "bo").equal("bob"));
        exp = exp.and(builder.get("firstName").trim().equal("b"));

        Hashtable decodeTable = new Hashtable(3);
        decodeTable.put("Bob", "Bobby");
        decodeTable.put("Susan", "Susie");
        decodeTable.put("Eldrick", "Tiger");
        exp = exp.and(builder.get("firstName").ifNull("Bob").decode(decodeTable, "No-Nickname").equal("Bobby"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(exp);
        test.setName("AdvancedExpressionFunctionTest");
        test.setDescription("advanced expression function test");
        test.addSupportedPlatform(OraclePlatform.class);
        addTest(test);
    }

    private void addRegexpTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("firstName").regexp("^B.*");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(exp);
        test.setName("RegexpTest");
        test.setDescription("Regexp test");
        test.addSupportedPlatform(OraclePlatform.class);
        test.addSupportedPlatform(MySQLPlatform.class);
        test.addSupportedPlatform(PostgreSQLPlatform.class);
        addTest(test);
    }

    /**
     * TODO:
     * This tests seems to have been removed?  Should be added back, for DB2 platform.
     */
    private void addAdvancedDB2ExpressionFunctionTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("salary").lessThan(100000);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(1993, 5, 1, 0, 0, 0);
        exp = exp.and(builder.get("lastName").asciiValue().equal(84));
        exp = exp.and(builder.get("period").get("endDate").dateToString().equal("1999-6-12 10:10:10.0"));

        calendar.set(1999, 1, 1, 0, 0, 0);
        exp = exp.and(builder.get("period").get("startDate").toDate().equal(new java.sql.Date(calendar.getTime().getTime())));
        calendar.set(1993, 1, 1, 0, 0, 0);
        exp = exp.and(builder.get("firstName").replace("B", "C").equal("Cob"));
        exp = exp.and(builder.get("salary").toNumber().lessThan(100000));
        exp = exp.and(builder.get("firstName").substring(1, 1).equal("B"));
        exp = exp.and(builder.get("firstName").translate(new String("Bo"), new String("bo")).equal("bob"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(exp);
        test.setName("AdvancedDB2ExpressionFunctionTest");
        test.setDescription("advanced expression function test");
        test.addSupportedPlatform(DB2Platform.class);
        test.addUnsupportedPlatform(DerbyPlatform.class);
        addTest(test);
    }

    private void addAdvancedExpressionMathTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("salary").greaterThanEqual(1000);
        expression = expression.and((ExpressionMath.ln(builder.get("salary"))).lessThan(10));
        expression = expression.and((ExpressionMath.mod(builder.get("salary"), new Integer(107)).lessThan(10)));
        expression = expression.and((ExpressionMath.floor(builder.get("salary"))).lessThan(45000));
        expression = expression.and((ExpressionMath.ceil(builder.get("salary"))).lessThan(10000));
        expression = expression.and(ExpressionMath.round(builder.get("salary"), 2).equal(40000));
        expression = expression.and(ExpressionMath.min(builder.get("salary"), new Integer(30000)).greaterThan(30000));
        expression = expression.and(ExpressionMath.max(builder.get("salary"), new Integer(30000)).lessThan(50000));
        expression = expression.and((ExpressionMath.sinh(ExpressionMath.divide(builder.get("salary"), new Integer(10000000))).lessThanEqual(100)));
        expression = expression.and((ExpressionMath.cosh(ExpressionMath.divide(builder.get("salary"), new Integer(10000000))).lessThanEqual(100)));
        expression = expression.and((ExpressionMath.tanh(ExpressionMath.divide(builder.get("salary"), new Integer(10000000))).lessThanEqual(1)));
        expression = expression.and((ExpressionMath.acos(ExpressionMath.power(builder.get("salary"), 0)).lessThanEqual(100)));
        expression = expression.and((ExpressionMath.asin(ExpressionMath.power(builder.get("salary"), 0)).lessThanEqual(100)));
        expression = expression.and((ExpressionMath.atan(ExpressionMath.power(builder.get("salary"), 0)).lessThanEqual(100)));
        expression = expression.and((ExpressionMath.atan2(ExpressionMath.power(builder.get("salary"), 0), 2).lessThanEqual(100)));
        expression = expression.and(ExpressionMath.power(builder.get("salary"), 1).equal(40000));
        expression = expression.and((ExpressionMath.trunc(builder.get("salary"), 2).equal(50000)));
        expression = expression.and((ExpressionMath.chr(builder.get("salary"))).equal('b'));
        expression = expression.and(ExpressionMath.round(builder.get("salary"), 2).equal(40000));
        expression = expression.and((ExpressionMath.sign(builder.get("salary"))).greaterThan(0));
        expression = expression.and((ExpressionMath.exp(ExpressionMath.min(builder.get("salary"), 5))).lessThan(1000000));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("AdvancedExpressionMathTest");
        test.setDescription("Test advanced expression math package");
        test.addSupportedPlatform(OraclePlatform.class);
        addTest(test);
    }

    private void addAdvancedSybaseExpressionFunctionTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("period").get("startDate").datePart("year").equal(1993);
        exp = exp.and(builder.get("period").get("startDate").dateName("year").equal("1993"));
        exp = exp.and(builder.get("period").get("startDate").equal(builder.currentDate()));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(1994, 1, 1, 0, 0, 0);
        exp = exp.and(builder.get("period").get("startDate").dateDifference("year", new java.sql.Date(calendar.getTime().getTime())).equal(1));
        exp = exp.and(builder.get("period").get("startDate").addDate("year", 1).dateDifference("year", builder.value(new java.sql.Date(calendar.getTime().getTime()))).equal(2));
        exp = exp.and(builder.get("firstName").difference("Bob").greaterThan(1));
        exp = exp.and(builder.get("firstName").indexOf("o").greaterThan(0));
        exp = exp.and(builder.get("firstName").length().lessThan(6));
        exp = exp.and(builder.get("firstName").reverse().equal("boB"));
        exp = exp.and(builder.get("firstName").replicate(2).equal("BobBob"));
        exp = exp.and(builder.get("firstName").right(2).equal("ob"));
        exp = exp.and(builder.get("salary").toNumber().lessThan(100000));

        // bug 3061144
        exp = exp.and(builder.get("firstName").ifNull("Frank").equal("Bob"));
        exp = exp.and(builder.get("period").get("endDate").dateToString().equal("1999-6-12 10:10:10.0"));
        exp = exp.and(builder.get("period").get("startDate").toDate().equal(new java.sql.Date(calendar.getTime().getTime())));
        exp = exp.and(builder.get("period").get("endDate").toChar().equal("1999-6-12 10:10:10.0"));

        exp = exp.and(ExpressionMath.cot(builder.get("salary")).greaterThan(0));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(exp);
        test.setName("AdvancedSybaseExpressionFunctionTest");
        test.setDescription("advanced sybase expression function test");
        test.addSupportedPlatform(SybasePlatform.class);
        test.addSupportedPlatform(SQLAnywherePlatform.class);
        test.addSupportedPlatform(SQLServerPlatform.class);
        addTest(test);
    }

    private void addAggregateQueryTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0003");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("period").get("startDate").equal(employee.getPeriod().getStartDate());

        SearchTest test = new SearchTest();
        test.setExpression(expression);
        test.setErrorMessage("Failed to match an aggregate attribute (get('period').get('startDate').equals(dateObject)");
        test.setName("AggregateQueryTest");
        test.setDescription("Test for mateching of aggregate properties");
        addTest(test);
    }

    private void addAggregeateCollectionJoinTest(Class cls) {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("customers").anyOf("dependants").get("firstName").equal("Kyle");

        ReadAllExpressionTest test = new ReadAllExpressionTest(cls, 1);
        test.setExpression(expression);
        test.setName("Aggregate Colection Join Test " + Helper.getShortClassName(cls));
        test.setDescription("Test aggregate collection join with 2 anyOf clauses");
        addTest(test);
    }

    private void addAndNullTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0003");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp1 = builder.get("lastName").equal("Chanley");
        Expression exp2 = builder.get("firstName").equal("Charles");
        Expression expression = exp1.and(exp2).and(null);

        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
        test.setName("AndNullExpressionTest");
        test.setDescription("Test AND expression with NULL");
        addTest(test);
    }

    private void addAndTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0003");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp1 = builder.get("lastName").equal("Chanley");
        Expression exp2 = builder.get("firstName").equal("Charles");
        Expression expression = exp1.and(exp2);

        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
        test.setName("AndExpressionTest");
        test.setDescription("Test AND expression");
        addTest(test);
    }

    private void addBadFieldWithTableTest() {
        Expression expression = new ExpressionBuilder().getField("ADDRESS.STREET").equal("blee");
        BadExpressionTest test = new BadExpressionTest();
        test.setDescription("Test that fields with an invalid table are error handled correctly");
        test.setExceptionCode(QueryException.INVALID_TABLE_FOR_FIELD_IN_EXPRESSION);
        test.setExpression(expression);
        test.setName("BadFieldTableTest");

        addTest(test);
    }

    private void addBadQueryKeyTest() {
        Expression expression = new ExpressionBuilder().get("foofoo").equal("blee");
        BadExpressionTest test = new BadExpressionTest();
        test.setDescription("Test that wrong query keys are error handled correctly");
        test.setExceptionCode(QueryException.INVALID_QUERY_KEY_IN_EXPRESSION);
        test.setExpression(expression);
        test.setName("BadQueryKeyTest");

        addTest(test);
    }

    private void addBadQueryTableTest() {
        Expression expression = new ExpressionBuilder().getTable("foofoo").equal("blee");
        BadExpressionTest test = new BadExpressionTest();
        test.setDescription("Test that the comparison of tables is error handled correctly");
        test.setExceptionCode(QueryException.CANNOT_COMPARE_TABLES_IN_EXPRESSION);
        test.setExpression(expression);
        test.setName("BadQueryTableTest");

        addTest(test);
    }

    private void addBadToManyQueryKeyTest() {
        Expression expression = new ExpressionBuilder().get("managedEmployees").get("firstName").equal("blee");
        BadExpressionTest test = new BadExpressionTest();
        test.setDescription("Test that to-many query keys cannot be gotten with 'get', must use 'anyOf'");
        test.setExceptionCode(QueryException.INVALID_USE_OF_TO_MANY_QUERY_KEY_IN_EXPRESSION);
        test.setExpression(expression);
        test.setName("BadToManyQueryKeyTest");

        addTest(test);
    }

    private void addBadAnyOfTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("firstName").equal("blee").and(builder.anyOf("managedEmployees"));
        BadExpressionTest test = new BadExpressionTest();
        test.setDescription("Test and with an anyof.");
        test.setExceptionCode(QueryException.INVALID_EXPRESSION);
        test.setExpression(expression);
        test.setName("BadAnyOfTest");

        addTest(test);
    }

    private void addBetweenTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("salary").between(33000, 36000);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("BetweenExpressionTest");
        test.setDescription("Test BETWEEN expression");
        addTest(test);
    }

    private void addBetweenTest2() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("salary").between(builder.get("manager").get("salary"), new Integer(500000));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 5);
        test.setExpression(expression);
        test.setName("BetweenExpressionTest 2");
        test.setDescription("Test BETWEEN expression 2");
        addTest(test);
    }

    protected void addBuilderEqualParameterTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0002");

        ExpressionBuilder emp = new ExpressionBuilder();
        Expression expression = emp.equal(emp.getParameter("employee"));
        ReadAllQuery query = new ReadAllQuery(Employee.class, expression);
        query.addArgument("employee");

        //query.setShouldPrepare(false);
        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setQuery(query);
        test.getArguments().add(employee);
        test.setName("BuilderEqualParameter");
        test.setDescription("For bug 3003399 tests ExpressionBuilder.equal(ParameterExpression).");
        addTest(test);
    }

    private void addComplexBooleanTest() {
        ExpressionBuilder emp = new ExpressionBuilder();
        java.util.Calendar c = Calendar.getInstance();
        c.set(1992, 0, 1);

        java.sql.Date lowDate = new java.sql.Date(c.getTime().getTime());
        c.set(1994, 0, 1);

        java.sql.Date highDate = new java.sql.Date(c.getTime().getTime());

        Expression exp = emp.get("firstName").equal("Fred").and(emp.get("lastName").like("Jo%"));
        Expression subExp1 = emp.get("gender").equal("Female").and(emp.get("period").get("startDate").greaterThan(lowDate).and(emp.get("period").get("startDate").lessThan(highDate)));
        Expression subExp2 = emp.get("salary").greaterThan(400000).and(emp.get("firstName").notEqual("Fred"));
        exp = exp.or(subExp1.or(subExp2));

        //Thats either Fred Jones, or a woman with hired 92-94, or someone who's salary is >400K, except fred jones
        //expect fred, sarah-loo, nancy, betty
        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(exp);
        test.setName("Complex Boolean Test");
        test.setDescription("Test expression with complicated ands/ors");
        addTest(test);
    }

    private void addComputerViewCursoredStreamTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("memory").greaterThan(100);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Computer.class, 2);
        ReadAllQuery query = new ReadAllQuery(Computer.class, expression);
        query.useCursoredStream(1, 1);
        test.setExpression(expression);
        test.setQuery(query);
        test.setName("ComputerViewCursoredStreamTest");
        test.setDescription("Test query using cursors with inheritance, supported if using views.  For bug 2718118.");
        test.addSupportedPlatform(OraclePlatform.class);
        test.addSupportedPlatform(SybasePlatform.class);
        test.addSupportedPlatform(SQLAnywherePlatform.class);
        //Uncomment it when we support MySQL 5.  Please refer to the comments in InheritanceSystem
        //test.addSupportedPlatform(MySQLPlatform.class);
        addTest(test);
    }

    private void addComputerViewTest1() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("memory").greaterThan(100);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Computer.class, 2);
        test.setExpression(expression);
        test.setName("ComputerViewTest1");
        test.setDescription("Test expression against view, or multiple table subclass read.");
        addTest(test);
    }

    /**
     * For bug 3107049 test a potential infinite loop on constantExp.equal(constantExp).
     */
    private void addConstantEqualConstantTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.value(1).equal(1);
        expression = expression.and(builder.value(1).equal(builder.value(1)));
        expression = expression.and(builder.value(1).equal(builder.getParameter("1")));

        ReadAllQuery query = new ReadAllQuery(Employee.class, expression);
        query.addArgument("1", Integer.class);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setExpression(expression);
        test.setQuery(query);
        test.getArguments().add(new Integer(1));
        test.setName("ConstantEqualConstantTest");
        test.setDescription("Test meaningless selection criteria like 1 == 1.");

        addTest(test);
    }

    /* Test that when the user uses getFunction(FOO) we will print it as a function
     * FOO, even though we've never heard of it before.
     */
    private void addCustomDefaultExpressionTest() {
        ReadAllExpressionTest test = new DefaultingFunctionsExpressionTest(Employee.class, 1);
        test.setDescription("Test a database function we don't know about that is generated dynamically from the user expression");
        test.setName("Custom Default Expression Test");
        test.setSupportedInMemory(false);

        addTest(test);
    }

    private void addCustomQKJoinTest1() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("owner").get("name").equal("A1");

        ReadAllExpressionTest test = new ReadAllExpressionTest(ObjectB.class, 1);
        test.setExpression(expression);
        test.setSupportedInMemory(false);
        test.setName("CustomQKJoinExpressionTest1");
        test.setDescription("Test expression with user define query key joins.");
        addTest(test);
    }

    private void addCustomQKJoinTest2() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("root").get("name").equal("A1");

        ReadAllExpressionTest test = new ReadAllExpressionTest(ObjectC.class, 2);
        test.setExpression(expression);
        test.setSupportedInMemory(false);
        test.setName("CustomQKJoinExpressionTest2");
        test.setDescription("Test expression with user define query key joins.");
        addTest(test);
    }

    private void addCustomQKTest1() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("id").greaterThan(0);

        ReadAllExpressionTest test = new ReadAllExpressionTest(PhoneNumber.class, 26);
        test.setExpression(expression);
        test.setSupportedInMemory(false);
        test.setName("CustomQKExpressionTest1");
        test.setDescription("Test expression with user defined direct query key.");
        addTest(test);
    }

    private void addDirectCollectionJoinTest1() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("responsibilitiesList").like("%coffe%");
        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("DirectCollectionJoinTest1");
        test.setDescription("Test a join across a direct collection relation");
        addTest(test);
    }

    private void addEqualDoubleTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0001");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("salary").equal((double)35000);

        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
        test.setName("EqualDoubleExpressionTest");
        test.setDescription("Test = expression");
        addTest(test);
    }

    private void addEqualTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0003");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("lastName").equal("Chanley");

        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
        test.setName("EqualExpressionTest");
        test.setDescription("Test = expression");
        addTest(test);
    }

    /*
     * bug 5683148/2380: Reducing unnecessary joins on an equality check between the a statement
     *   and itself
     */
    private void addEqualUnneccessaryJoinTest() {
        Employee employee = (Employee)getManager().getObject(new Employee().getClass(), "0008");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("firstName").equal("Fred").or(builder.get("manager").notEqual(builder.get("manager")));

        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
        test.setName("EqualUnneccessaryJoinTest");
        test.setDescription("Test = expression does not create an extra unneccessary join");
        addTest(test);
    }

    private void addExpressionFunctionTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("salary").lessThan(100000);
        exp = exp.and(builder.get("firstName").toUpperCase().equal("BOB"));
        exp = exp.and(builder.get("firstName").toLowerCase().equal("bob"));
        exp = exp.and(builder.get("firstName").leftTrim().equal("Bob"));
        exp = exp.and(builder.get("firstName").rightTrim().equal("Bob"));
        exp = exp.and(builder.get("firstName").cast("CHAR(3)").equal("Bob"));
        exp = exp.and(builder.get("period").get("startDate").extract("YEAR").equal(1996));
        exp = exp.and(builder.get("firstName").concat(builder.get("lastName")).equal("BobSmith"));
        exp = exp.and(builder.get("firstName").substring(2).equal("ob"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(exp);
        test.setName("ExpressionFunctionTest");
        test.setSupportedInMemory(false);
        test.setDescription("expression function test");
        test.addUnsupportedPlatform(TimesTenPlatform.class);
        addTest(test);
    }

    private void addExpressionMathTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("salary").greaterThanEqual(1000);
        expression = expression.and((ExpressionMath.add(builder.get("salary"), new Integer(3000))).lessThan(90000));
        expression = expression.and((ExpressionMath.subtract(builder.get("salary"), new Integer(1000))).greaterThan(1000));
        expression = expression.and((ExpressionMath.multiply(builder.get("salary"), new Integer(3))).greaterThan(50000));
        expression = expression.and((ExpressionMath.divide(builder.get("salary"), new Integer(3))).lessThan(100000));
        expression = expression.and((ExpressionMath.abs(builder.get("salary"))).lessThan(100000));
        expression = expression.and((ExpressionMath.cos(builder.get("salary")).lessThanEqual(1)));
        expression = expression.and((ExpressionMath.sin(builder.get("salary")).lessThanEqual(1)));
        expression = expression.and((ExpressionMath.tan(builder.get("salary")).lessThanEqual(1)));
        expression = expression.and((ExpressionMath.log(builder.get("salary"))).greaterThan(4));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 6);
        test.setExpression(expression);
        test.setName("ExpressionMathTest");
        test.setDescription("Test expression math package");
        test.addUnsupportedPlatform(TimesTenPlatform.class);
        test.addUnsupportedPlatform(SymfowarePlatform.class);
        addTest(test);
    }

    private void addExpressionsDefaultingFieldTest() {
        Expression expression = new ExpressionBuilder().getField("L_NAME").equal("Way");
        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(expression);
        test.setDescription("Test that field references without a table default correctly");
        test.setName("FieldDefaultingTest");

        addTest(test);
    }

    private void addGetFunctionWithTwoArgumentsTest() {
        GetFunctionWithTwoArgumentsTest test = new GetFunctionWithTwoArgumentsTest(Employee.class, 1);
        test.setDescription("Tests a database function with two arguments");
        test.setName("GetFunction() With Two Arguments Test");
        test.setSupportedInMemory(false);

        addTest(test);
    }

    private void addGreaterThanEqualTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("salary").greaterThanEqual(99999.99);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(expression);
        test.setName("GreaterThanEqualExpressionTest");
        test.setDescription("Test >= expression");
        addTest(test);
    }

    private void addGreaterThanTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("lastName").greaterThan("N");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 7);
        test.setExpression(expression);
        test.setName("GreaterThanExpressionTest");
        test.setDescription("Test > expression");
        addTest(test);
    }

    private void addInConversionTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("gender").in(new String[] { "Male", "Female" });

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setExpression(expression);
        test.setName("InConversionTest");
        test.setDescription("Test that in converts correctly");
        addTest(test);
    }

    /*
     * added for bug 5842913 - tests JPAQL usage of 'in', testing that in takes
     *   a collection containing multiple parameter expressions.
     */
    private void addInMultipleExpressionParameterTest() {
        ExpressionBuilder ex = new ExpressionBuilder();
        Vector vec = new Vector();
        vec.add(ex.getParameter("salary1"));
        vec.add(ex.getParameter("salary2"));
        vec.add(ex.getParameter("salary3"));
        Expression exp = ex.get("salary").in(vec);

        ReadAllQuery rq = new ReadAllQuery(Employee.class);
        rq.setSelectionCriteria(exp);
        rq.addArgument("salary1");
        rq.addArgument("salary2");
        rq.addArgument("salary3");

        Vector vect = new Vector();
        vect.add(87000);
        vect.add(31000);
        vect.add(500001);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 3);
        test.setQuery(rq);
        test.setArguments(vect);
        test.setName("InMultipleExpressionParameterTest");
        test.setDescription("Test Expression IN with multiple parameters");
        addTest(test);
    }

    /*
     * added for bug 235340 - parameters in named query are not  transformed when IN is used
     */
    private void addInMultipleExpressionWithConvertionParameterTest() {

        ExpressionBuilder ex = new ExpressionBuilder();
        Vector vec = new Vector();
        vec.add(ex.getParameter("gender1"));
        vec.add(ex.getParameter("gender2"));
        Expression exp = ex.get("gender").in(vec);

        ReadAllQuery rq = new ReadAllQuery(Employee.class);
        rq.setSelectionCriteria(exp);
        rq.addArgument("gender1");
        rq.addArgument("gender2");

        Vector vect = new Vector();
        vect.add("Male");
        vect.add("Female");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setQuery(rq);
        test.setArguments(vect);
        test.setName("InMultipleExpressionWithConversionParameterTest");
        test.setDescription("Test Expression IN with multiple parameters that require convertion");
        addTest(test);
    }

    /*
     * added for bug 5842913 - tests backward compatibility of using 'in' with
     *   a parameter representing a collection
     */
    private void addInSingleVectorParameterTest() {
        ExpressionBuilder ex = new ExpressionBuilder();
        Vector vec = new Vector();
        vec.add(ex.getParameter("salary1"));
        Expression exp = ex.get("salary").in(ex.getParameter("list"));

        ReadAllQuery rq = new ReadAllQuery(Employee.class);
        rq.setSelectionCriteria(exp);
        rq.addArgument("list");

        Vector vect1 = new Vector();
        vect1.add(87000);
        vect1.add(31000);
        vect1.add(500001);

        Vector vect = new Vector();
        vect.add(vect1);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 3);
        test.setQuery(rq);
        test.setArguments(vect);
        test.setName("InSingleVectorParameterTest");
        test.setDescription("Test Expression IN with one list parameter");
        addTest(test);
    }

    private void addInTest() {
        Employee employee = (Employee)getManager().getObject(new Employee().getClass(), "0003");

        Vector names = new Vector();
        names.addElement("Jennifer");
        names.addElement("Chanley");
        names.addElement("Beavis");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("lastName").in(names);

        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
        test.setName("InExpressionTest");
        test.setDescription("Test IN expression");
        addTest(test);
    }

    private void addInCollectionTest() {
        Employee employee = (Employee)getManager().getObject(new Employee().getClass(), "0003");

        Set names = new HashSet();
        names.add("Jennifer");
        names.add("Chanley");
        names.add("Beavis");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("lastName").in(names);

        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
        test.setName("InCollectionExpressionTest");
        test.setDescription("Test IN expression");
        addTest(test);
    }

    private void addIsNotNullTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("firstName").notEqual(null);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setExpression(expression);
        test.setName("IsNotNullExpressionTest");
        test.setDescription("Test IS NOT NULL expression");
        addTest(test);
    }

    private void addIsNotNullWithJoinTest() {
        //This test case was added for cr2334.  Previously notNull
        //would build statements with a single where condition only.
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("projects").get("teamLeader").notEqual(null);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 3);
        test.setExpression(expression);
        test.setName("IsNotNullWithJoinExpressionTest");
        test.setDescription("Test IS NOT NULL expression with an additional join (for cr2334).");
        addTest(test);
    }

    private void addIsNullTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("firstName").equal(null);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("IsNullExpressionTest");
        test.setDescription("Test IS NULL expression");
        addTest(test);
    }

    private void addIsNullWithJoinTest() {
        //This test case was added for cr2334.  Previously isNull
        //would build statements with a single where condition only.
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("projects").get("teamLeader").equal(null);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 6);
        test.setExpression(expression);
        test.setName("IsNullWithJoinExpressionTest");
        test.setDescription("Test IS NULL expression with an additional join (for cr2334).");
        addTest(test);
    }

    private void addJoinsShrinkResultSetSizeTest() {
        Expression builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("managedEmployees").get("firstName").notEqual("Zanthara").not();

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("JoinsShrinkResultSetSizeTest");
        test.setDescription("Tests that joins shrink the size of a result set.  Exclusively added to break in memory querying.");
        addTest(test);
    }

    private void addJoinsShrinkResultSetSizeTest2() {
        Expression builder = new ExpressionBuilder();
        Expression expression = builder.get("manager").get("firstName").notEqual("Zanthara").not();

        addTruncCurrentDateTest();

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("JoinsShrinkResultSetSizeTest2");
        test.setDescription("Tests that joins shrink the size of a result set.  Exclusively added to break in memory querying.");
        addTest(test);
    }

    private void addLessThanEqualTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("salary").lessThanEqual(99999.99);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 10);
        test.setExpression(expression);
        test.setName("LessThanEqualExpressionTest");
        test.setDescription("Test <= expression");
        addTest(test);
    }

    private void addLessThanTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("firstName").lessThan("B");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("LessThanExpressionTest");
        test.setDescription("Test < expression");
        addTest(test);
    }

    private void addLikeEscapeTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0003");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("lastName").like("/%han", "/");

        ReadAllExpressionTest test = new ReadAllExpressionTest(employee.getClass(), 0);
        test.setExpression(expression);
        test.setName("LikeEscapeExpressionTest");
        test.setDescription("Test LIKE ESCAPE expression");
        test.addSupportedPlatform(OraclePlatform.class);
        test.addSupportedPlatform(SybasePlatform.class);
        test.addSupportedPlatform(SQLAnywherePlatform.class);
        test.addSupportedPlatform(SQLServerPlatform.class);
        test.addSupportedPlatform(DB2Platform.class);
        test.addSupportedPlatform(MySQLPlatform.class);
        addTest(test);
    }

    /**
     * This method was created in VisualAge.
     */
    public void addLikeIgnoreCaseTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0003");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("lastName").likeIgnoreCase("%haNLey");

        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
        test.setName("LikeIgnoreCaseExpressionTest");
        test.setDescription("Test LIKEIGNORECASE expression");
        addTest(test);
    }

    private void addLikeIgnoringCaseTest1() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0003");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp1 = builder.get("lastName").likeIgnoreCase("cHANley");
        Expression exp2 = builder.get("firstName").likeIgnoreCase("%arles");
        Expression expression = exp1.and(exp2);

        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
        test.setName("LikeIgnoreCaseTest");
        test.setDescription("Test likeIgnoreCase expression");
        addTest(test);
    }

    private void addLikeIgnoringCaseTest2() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0003");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp1 = builder.get("lastName").likeIgnoreCase("cHANlEy");
        Expression exp2 = builder.get("firstName").likeIgnoreCase("%harles");

        //test to see if object type mappings are handled properly
        Expression exp3 = builder.get("gender").likeIgnoreCase("m");

        Expression expression = exp1.and(exp2).and(exp3);

        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
        test.setName("LikeIgnoreCaseTest2");
        test.setDescription("Test likeIgnoreCase expression");
        addTest(test);
    }

    private void addLikeTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0003");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("lastName").like("%hanley");

        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
        test.setName("LikeExpressionTest");
        test.setDescription("Test LIKE expression");
        addTest(test);
    }

    private void addLowerCaseTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0002");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("address").get("city").toLowerCase().equal("ottawa");

        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
        test.setName("LowerCaseExpressionTest");
        test.setDescription("Test LOWER expression");
        addTest(test);
    }

    private void addManyToManyJoinTest1() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Object[] array = new Object[] { "Sales Reporter", "Sales Reporting" };
        Expression expression = builder.anyOf("projects").get("name").in(array);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 3);
        test.setExpression(expression);
        test.setName("ManyToManyJoinTest1");
        test.setDescription("Test a join across a many:many relation");
        addTest(test);
    }

    private void addManyToManyJoinTest2() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Object[] array = new Object[] { "Ottawa" };
        Expression expression = builder.anyOf("locations").get("city").in(array);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Restaurant.class, 2);
        test.setExpression(expression);
        test.setName("ManyToManyJoinTest2");
        test.setDescription("Test a join across a many:many relation");
        addTest(test);
    }

    private void addManyToManyJoinTest3() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Object[] array = new Object[] { "Ottawa", "Toronto" };
        Expression expression = builder.anyOf("locations").get("city").in(array);

        // test also of distinct objects
        ReadAllExpressionTest test = new ReadAllExpressionTest(Restaurant.class, 2);
        test.setExpression(expression);
        test.setName("ManyToManyJoinTest3");
        test.setDescription("Test a join across a many:many relation");
        addTest(test);
    }

    private void addManyToManyJoinTest4() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("employees").get("sex").equal("female");

        ReadAllExpressionTest test = new ReadAllExpressionTest(org.eclipse.persistence.testing.models.mapping.Shipment.class, 4);
        test.setExpression(expression);
        test.setName("ManyToManyJoinTest4");
        test.setDescription("Test a join across a many:many relation");
        addTest(test);
    }

    private void addManyToManyJoinTest5() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("teamLeader").anyOf("projects").get("name").like("S%");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Project.class, 2);
        test.setExpression(expression);
        test.setName("ManyToManyJoinTest5");
        test.setDescription("Test a join across a many:many relation againts an abstarct multiple table class");
        addTest(test);
    }

    private void addMismatchedQueryKeyTest() {
        Expression expression = new ExpressionBuilder().get("address").equal(2);
        BadExpressionTest test = new BadExpressionTest();
        test.setDescription("Test that mismatched expressions (e.g. address == 2) are error handled correctly");
        test.setExceptionCode(QueryException.INCORRECT_CLASS_FOR_OBJECT_COMPARISON);
        test.setExpression(expression);
        test.setName("MismatchedQueryKeyTest");

        addTest(test);
    }

    /**
     * Ensure certain operators work on multiple platforms should be tested on SQLServer and
     * Oracle in particular
     * Added for bug 3497618 and 3539971
     */
    private void addMultiPlatformTest() {
        ExpressionBuilder builder = new ExpressionBuilder();

        Hashtable caseTable = new Hashtable(3);
        caseTable.put("Bob", "Bobby");
        caseTable.put("Susan", "Susie");
        caseTable.put("Eldrick", "Tiger");

        Expression expression = builder.get("firstName").ifNull("Bob").caseStatement(caseTable, "No-Nickname").equal("Bobby");
        expression = expression.and((ExpressionMath.mod(builder.get("salary"), new Integer(10)).equal(0)));
        expression = expression.and((ExpressionMath.ceil(builder.get("salary")).equal(35000)));
        expression = expression.and(builder.get("firstName").length().equal(3));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("MultiPlatformTest");
        test.setDescription("test functions on multiple platforms");
        test.addUnsupportedPlatform(DB2Platform.class);
        test.addUnsupportedPlatform(TimesTenPlatform.class);
        addTest(test);
    }
    
    /**
     * Ensure certain operators work on multiple platforms should be tested on SQLServer and
     * Oracle in particular.  This tests "CASE field WHERE value THEN value1 ELSE value2" statements  
     * Added for JPA 2.0 support (bug 252491)
     */
    private void addMultiPlatformTest2() {
        ExpressionBuilder builder = new ExpressionBuilder();

        Hashtable caseTable = new Hashtable(3);
        caseTable.put("Bob", "Bobby");
        caseTable.put("Susan", "Susie");
        caseTable.put("Eldrick", "Tiger");

        Expression expression = builder.get("firstName").caseStatement(caseTable, "NoNickname").equal("Bobby");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("MultiPlatformTest2");
        test.setDescription("test simple Case function on multiple platforms");
        test.addUnsupportedPlatform(DerbyPlatform.class);
        addTest(test);
    }
    
    /**
     * Ensure certain operators work on multiple platforms.  This tests more complex 
     * "CASE WHERE expression THEN value1 ELSE value2" statements  
     * Added for JPA 2.0 support (bug 252491)
     */
    private void addMultiPlatformTest3() {
        ExpressionBuilder builder = new ExpressionBuilder();

        Hashtable caseTable = new Hashtable(3);
        caseTable.put(builder.get("firstName").equal("Bob"), "Bobby");
        caseTable.put(builder.get("firstName").equal("Susan"), "Susie");
        caseTable.put(builder.get("firstName").equal("Eldrick"), "Tiger");

        Expression expression = builder.caseConditionStatement(caseTable, "No-Nickname").equal("Bobby");
        expression = expression.and((ExpressionMath.mod(builder.get("salary"), new Integer(10)).equal(0)));
        expression = expression.and((ExpressionMath.ceil(builder.get("salary")).equal(35000)));
        expression = expression.and(builder.get("firstName").length().equal(3));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("MultiPlatformTest3");
        test.setDescription("test Case function with more complex expressions on multiple platforms");
        /*TODO: add in any unsupported platform checks*/
        addTest(test);
    }
    
    /**
     * Ensure certain operators work on multiple platforms.  This tests the NULLIF SQL statement 
     * Added for JPA 2.0 CASE support (bug 252491)
     */
    private void addMultiPlatformTest4() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("firstName").nullIf( "Bobby").equal("Bob");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("MultiPlatformTest4");
        test.setDescription("test Case and NullIf function on multiple platforms");
        /*TODO: add in any unsupported platform checks*/
        addTest(test);
    }
    
    /**
     * Ensure certain operators work on multiple platforms.  This tests the COALESCE SQL statement 
     * Added for JPA 2.0 CASE support (bug 252491)
     */
    private void addMultiPlatformTest5() {
        ExpressionBuilder builder = new ExpressionBuilder();

        Vector caseTable = new Vector(3);
        caseTable.add(builder.get("firstName"));
        caseTable.add(builder.get("lastName"));
        caseTable.add("NoName");

        Expression expression = builder.coalesce(caseTable).equal("Bob");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("MultiPlatformTest5");
        test.setDescription("test Coalesce function on multiple platforms");
        /*TODO: add in any unsupported platform checks*/
        addTest(test);
    }

    private void addMultipleAndsTest() {
        ExpressionBuilder builder = new ExpressionBuilder();

        //this is a bug, wrong generated SQL like... ADDRESS.ADDRESS_ID = EMPLOYEE.ADDR_ID...
        //however, it should be ... ADDRESS.ADDRESS_ID = 0...
        Expression expression = builder.get("address").equal(new org.eclipse.persistence.testing.models.employee.domain.Address()).and(builder.get("lastName").notEqual("foopoyp"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("MultipleAndsExpressionTest");
        test.setDescription("Test object equality with object will null primary key");
        addTest(test);
    }

    private void addMultipleAndsTest2() {
        //there is a bug, generated SQL looks like ... (ADDRESS.ADDRESS_ID = 123456)) AND (ADDRESS.ADDRESS_ID = EMPLOYEE.ADDR_ID)...
        //should be: ...EMPLOYEE.ADDR_ID = 123456...  no join needed!
        ExpressionBuilder builder = new ExpressionBuilder();
        Address a = new org.eclipse.persistence.testing.models.employee.domain.Address();
        a.setId(new java.math.BigDecimal(123456));

        Expression expression = builder.get("address").equal(a).and(builder.get("id").greaterThan(800));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setQuery(new ReadAllQuery(Employee.class, expression));
        test.getQuery().addAscendingOrdering("id");
        test.setExpression(expression);
        test.setName("MultipleAndsExpressionTest2");
        test.setDescription("Test multiple ands with object equality");
        addTest(test);
    }

    private void addMultipleAndsTest3() {
        //there is a bug, generated wrong SQL looks like ...  FROM ADDRESS t3, SALARY t2, EMPLOYEE t1 WHERE (((((t1.EMP_ID > '800') AND ) AND (t1.EMP_ID = t2.EMP_ID)) AND (t3.ADDRESS_ID = 123456))...
        ExpressionBuilder builder = new ExpressionBuilder();

        Address a = new org.eclipse.persistence.testing.models.employee.domain.Address();
        a.setId(new java.math.BigDecimal(123456));

        Expression expression = builder.get("id").greaterThan(800).and(builder.get("address").equal(a));
        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setQuery(new ReadAllQuery(Employee.class, expression));
        test.getQuery().addAscendingOrdering("id");
        test.setExpression(expression);
        test.setName("MultipleAndsExpressionTest3");
        test.setDescription("Test multiple ands with object equality");
        addTest(test);
    }

    private void addMultipleAndsTest4() {
        // This tests the case of a tree where the top-level AND should print, even though
        // both branches underneath it are partly suppressed.
        ExpressionBuilder builder = new ExpressionBuilder();
        Address address1 = new org.eclipse.persistence.testing.models.employee.domain.Address();
        address1.setId(new java.math.BigDecimal(999999876));

        Address address2 = new org.eclipse.persistence.testing.models.employee.domain.Address();
        address2.setId(new java.math.BigDecimal(999999877));

        Expression expression1 = builder.get("address").equal(address1).or(builder.get("lastName").equal("Smith"));
        Expression expression2 = builder.get("address").equal(address2).or(builder.get("firstName").equal("Bob"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression1.and(expression2));
        test.setName("MultipleAndsExpressionTest4");
        test.setDescription("Test multiple booleans with supression in each branch");
        addTest(test);
    }

    private void addMultipleAndsTest5() {
        ExpressionBuilder builder = new ExpressionBuilder();

        //this is a bug, Ill-formed expression in query, attempting to print an object reference into a
        //SQL statement for Query Key address{DatabaseTable(t1)=DatabaseTable(ADDRESS)}
        //however, it should be ... EMPLOYEE.ADDRESS_ID = 0 (null)...
        Expression expression = builder.get("address").equal(null).and(builder.get("lastName").notEqual("foopoyp"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("MultipleAndsExpressionTest5");
        test.setDescription("Test multiple ands expression");
        addTest(test);
    }

    private void addMultipleAndsTest6() {
        ExpressionBuilder builder = new ExpressionBuilder();

        //this is a bug, Ill-formed expression in query, attempting to print an object reference into a
        //SQL statement for Query Key address{DatabaseTable(t1)=DatabaseTable(ADDRESS)}
        //however, it should be ... EMPLOYEE.ADDRESS_ID = 0 (null)...
        Expression expression = builder.get("address").isNull().and(builder.get("lastName").notEqual("foopoyp"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("MultipleAndsExpressionTest6");
        test.setDescription("Test multiple ands expression");
        addTest(test);
    }

    private void addMultiplePrimaryKeyTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("employee").get("lastName").equal("Vadis");

        ReadAllExpressionTest test = new ReadAllExpressionTest(org.eclipse.persistence.testing.models.legacy.Computer.class, 1);
        test.setExpression(expression);
        test.setName("MultiplePrimaryKeyTest");
        test.setDescription("Test expression with multiple primary key");
        addTest(test);
    }

    private void addMultipleTableJoinTest1() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("car").get("fuelCapacity").equal(50);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Engineer.class, 1);
        test.setExpression(expression);
        test.setName("MultipleTableJoinExpressionTest1");
        test.setDescription("Test expression with joins");
        addTest(test);
    }

    private void addMultipleTableJoinTest2() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("car").get("description").equal("BMW");

        ReadAllExpressionTest test = new ReadAllExpressionTest(SalesRep.class, 1);
        test.setExpression(expression);
        test.setName("MultipleTableJoinExpressionTest2");
        test.setDescription("Test expression with joins");
        addTest(test);
    }

    private void addMultipleTableJoinTest3() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("car").get("passengerCapacity").equal(5);

        ReadAllExpressionTest test = new ReadAllExpressionTest(SalesRep.class, 1);
        test.setExpression(expression);
        test.setName("MultipleTableJoinExpressionTest3");
        test.setDescription("Test expression with joins");
        addTest(test);
    }

    private void addMultipleTableJoinTest4() {
        ExpressionBuilder employee = new ExpressionBuilder();
        Expression expression = employee.get("manager").get("address").get("street").toLowerCase().like("%mer%");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(expression.and(employee.get("firstName").notLike("NOTHING ON EARTH")));
        test.setName("MultipleTableJoinExpressionTest4");
        test.setDescription("Test expression with self-joins");
        addTest(test);
    }

    private void addMultipleTableJoinTest5() {
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression expression = emp.get("manager").get("lastName").like("Sm%");
        expression = expression.and(emp.get("lastName").like("%a%"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(expression);
        test.setName("MultipleTableJoinExpressionTest5");
        test.setDescription("Test expression with self joins, criteria on both tables and no other tables");
        addTest(test);
    }

    private void addMultipleTableJoinTest6() {
        ExpressionBuilder project = new ExpressionBuilder();
        Expression expression = project.get("teamLeader").get("address").get("street").like("12 Mer%");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Project.class, 1);
        test.setExpression(expression);
        test.setName("MultipleTableJoinExpressionTest6");
        test.setDescription("Test expression with joins and inheritance on queried class");
        addTest(test);
    }

    private void addMultipleTableJoinTest7() {
        // Not finished yet
        ExpressionBuilder project = new ExpressionBuilder();
        Expression expression = project.get("teamLeader").get("address").get("street").like("12 Mer%");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Project.class, 1);
        test.setExpression(expression);
        test.setName("MultipleTableJoinExpressionTest7");
        test.setDescription("Test expression with joins and inheritance on intermediate class");
        addTest(test);
    }

    private void addMultipleTableJoinTest8() {
        // Not finished yet
        ExpressionBuilder project = new ExpressionBuilder();
        Expression expression = project.get("budget").get("currency").like("C%");

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeBusinessProject.class, 2);
        test.setExpression(expression);
        test.setName("MultipleTableJoinExpressionTest8");
        test.setDescription("Test expression with joins and inheritance on intermediate class");
        addTest(test);
    }

    private void addMultipleTableJoinTest9() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("bestFriend").get("representitive").get("car").get("fuelCapacity").equal(50);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Engineer.class, 0);
        test.setExpression(expression);
        test.setName("MultipleTableJoinExpressionTest9");
        test.setDescription("Test expression with joins");
        addTest(test);
    }

    private void addNonFueledVehicleViewTest1() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = ((builder.get("owner").get("name").equal("ABC")).and(builder.get("owner").get("name").equal("ABC"))).and((builder.get("passengerCapacity").equal(-1)).not());

        ReadAllExpressionTest test = new ReadAllExpressionTest(NonFueledVehicle.class, 3);
        test.setExpression(expression);
        test.setName("NonFueledVehicleViewTest1");
        test.setDescription("Test expression against view, or multiple table subclass read.");
        addTest(test);
    }

    private void addNotInTest() {
        int[] salaries = { 35000, 6000, 50000 };
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("salary").notIn(salaries);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 10);
        test.setExpression(expression);
        test.setName("NotInExpressionTest");
        test.setDescription("Test NOT IN expression");
        addTest(test);
    }

    private void addNotLikeTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("lastName").notLike("W%");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 9);
        test.setExpression(expression);
        test.setName("NotLikeExpressionTest");
        test.setDescription("Test NOT LIKE expression");
        addTest(test);
    }

    private void addNotTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = (builder.get("salary").greaterThanEqual(99999.99).not().and(builder.get("firstName").equal("Bob"))).not();

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 11);
        test.setExpression(expression);
        test.setName("NotExpressionTest");
        test.setDescription("Test NOT expression");
        addTest(test);
    }

    private void addObjectComparisonAcrossJoin() {
        Address address = ((Employee)getManager().getObject(Employee.class, "0002")).getAddress();

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("manager").get("address").equal(address);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(expression);
        test.setName("ObjectComparisonAcrossJoin");
        test.setDescription("Test .manager.address.equal(address)");
        addTest(test);
    }

    /**
     * For bug 3105559 Object comparisons do not work with aggregate objects (subtitle:
     * because aggregate objects don't have a primary key).
     */
    private void addAggregateObjectObjectComparisonTest() {
        EmploymentPeriod period = ((Employee)getManager().getObject(Employee.class, "0002")).getPeriod();
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("period").equal(period);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setSupportedInMemory(false);
        test.setName("AggregateObjectObjectComparisonTest");
        test.setDescription("Test .period.equal(period)");
        addTest(test);
    }

    /**
     * For bug 3105559 Object comparisons do not work with aggregate objects (subtitle:
     * because aggregate objects don't have a primary key).
     */
    private void addAggregateObjectIsNullTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("period").isNull();

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("AggregateObjectIsNullTest");
        test.setDescription("Test .period.isNull()");
        addTest(test);
    }

    /**
     * For bug 3105559 Object comparisons do not work with aggregate objects (subtitle:
     * because aggregate objects don't have a primary key).
     */
    private void addAggregateObjectNullTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("period").notEqual(builder.value(null));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setExpression(expression);
        test.setName("AggregateObjectNullTest");
        test.setDescription("Test .period.notEqual(null)");
        addTest(test);
    }

    private void addOneToManyJoinObjectCompareTest() {
        Employee managed = (Employee)PopulationManager.getDefaultManager().getObject(Employee.class, "0001");
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("managedEmployees").equal(managed);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.setName("OneToManyJoinObjectCompareTest");
        test.setDescription("Test a join across a 1:many relation using object comparison");
        addTest(test);
    }

    private void addOneToManyJoinObjectCompareTest2() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("managedEmployees").equal(builder);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("OneToManyJoinObjectCompareTest2");
        test.setDescription("Test a join across a 1:many relation using object comparison to itself");
        addTest(test);
    }

    private void addOneToManyJoinTest1() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("managedEmployees").get("lastName").like("S%");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(expression);
        test.setName("OneToManyJoinTest1");
        test.setDescription("Test a join across a 1:many relation");
        addTest(test);
    }

    private void addOneToManyJoinTest2() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("managedEmployees").get("lastName").like("Sa%");
        Expression exp2 = builder.anyOf("managedEmployees").get("firstName").like("B%");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression.and(exp2));
        test.setName("OneToManyJoinTest2");
        test.setDescription("Test a join across a 1:many relation with 2 anyOf clauses");
        addTest(test);
    }

    /**
     * @bug 2720149 INVALID SQL WHEN USING BATCH READS AND MULTIPLE ANYOFS
     */
    private void addOneToManyJoin2WithBatchReadTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("managedEmployees").get("lastName").like("Sa%");
        Expression exp2 = builder.anyOf("managedEmployees").get("firstName").like("B%");

        expression = expression.and(exp2);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 1);
        test.setExpression(expression);
        test.testBatchAttributesOnEmployee();
        test.setName("OneToManyJoin2WithBatchReadTest");
        test.setDescription("Test a join across a 1:many relation with 2 anyOf clauses, and test again as part of a batch read.");
        addTest(test);
    }

    private void addOneToManyJoinTest3() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("managedEmployees").get("lastName").equal("Smith");
        Expression exp2 = builder.anyOf("managedEmployees").get("lastName").notEqual("Smith");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(expression.and(exp2));
        test.setName("OneToManyJoinTest3");
        test.setDescription("Test a join across a 1:many relation with 2 anyOf clauses");
        addTest(test);
    }

    private void addOneToManyJoinTest4() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.anyOf("managedEmployees").get("lastName").like("S%");
        Expression exp2 = builder.anyOf("managedEmployees").get("lastName").equal("Smith");

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 2);
        test.setExpression(expression.and(exp2));
        test.setName("OneToManyJoinTest4");
        test.setDescription("Test a join across a 1:many relation with 2 anyOf clauses");
        addTest(test);
    }

    private void addOneToManyJoinTest5() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("teamLeader").anyOf("phoneNumbers").get("areaCode").equal("905");
        Expression exp2 = builder.get("teamLeader").anyOf("phoneNumbers").get("areaCode").equal("613");

        ReadAllExpressionTest test = new ReadAllExpressionTest(LargeProject.class, 2);
        test.setExpression(expression.and(exp2));
        test.setName("OneToManyJoinTest5");
        test.setDescription("Test a join from 1;1 Mapping to 1:m relation (twice) with 2 anyOf clauses");
        addTest(test);
    }

    private void addOneToOneEqualTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0002");

        Expression expression = new ExpressionBuilder().get("address").equal(employee.getAddress());
        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
        test.setDescription("Test equal for 1-1 mappings (e.g. get('address').equal(myAddress)");
        test.setExpression(expression);
        test.setName("OneToOneEqualTest");

        addTest(test);
    }

    private void addOneToOneObjectTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("owner").equal(this.getManager().getObject(Employee.class, "0001"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(PhoneNumber.class, 1);
        test.setExpression(expression);
        test.setName("OneToOneExpressionUsingObjectAsValue1");
        test.setDescription("Test querying across a 1:1 mapping using an object as the value.");
        addTest(test);

        //
        builder = new ExpressionBuilder();
        expression = builder.get("manager").equal(this.getManager().getObject(Employee.class, "0001"));

        test = new ReadAllExpressionTest(Employee.class, 3);
        test.setExpression(expression);
        test.setName("OneToOneExpressionUsingObjectAsValue2");
        test.setDescription("Test querying across a 1:1 mapping using an object as the value.");
        addTest(test);
    }

    private void addOrNullTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0003");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp1 = builder.get("lastName").equal("Chanley");
        Expression exp2 = builder.get("firstName").equal("Charles");
        Expression expression = exp1.or(exp2).or(null);

        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
        test.setName("OrNullExpressionTest");
        test.setDescription("Test OR expression with NULL");
        addTest(test);
    }

    private void addOrTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0003");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp1 = builder.get("lastName").equal("Chanley");
        Expression exp2 = builder.get("firstName").equal("Charles");
        Expression expression = exp1.or(exp2);

        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
        test.setName("OrExpressionTest");
        test.setDescription("Test OR expression");
        addTest(test);
    }

    /**
     * For bug 3107049 test parameterExp.isNull()
     */
    private void addParameterIsNullTest() {
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression expression = emp.get("address").notNull().and(emp.getParameter("null").isNull());
        expression = expression.and(emp.getParameter("int").notNull());
        expression = expression.and(emp.getParameter("String").notNull());

        ReadAllQuery query = new ReadAllQuery(Employee.class, expression);
        query.addArgument("null");
        query.addArgument("int");
        query.addArgument("String");

        //query.setShouldPrepare(false);
        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setExpression(expression);
        test.setQuery(query);
        test.getArguments().add(null);
        test.getArguments().add(new Integer(1));
        test.getArguments().add(new String("String"));
        test.setName("ParameterIsNullTest");
        test.setDescription("For bug 3107049 tests parameterExp.isNull.");
        test.addUnsupportedPlatform(SybasePlatform.class);
        test.addSupportedPlatform(SQLAnywherePlatform.class);
        // ET. The test doesn't work with DB2 jcc driver(Bug 4563813)
        test.addUnsupportedPlatform(org.eclipse.persistence.platform.database.DB2Platform.class);
        test.addUnsupportedPlatform(org.eclipse.persistence.platform.database.TimesTenPlatform.class);
        addTest(test);
    }

    private void addValueEqualValueTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.value(123).equal(456);

        ReadAllExpressionTest test = new ReadAllExpressionTest(Address.class, 0);
        test.setExpression(expression);
        test.setName("ValueEqualValueTest");
        test.setDescription("Test query with no fields in where clause works.");

        addTest(test);
    }
    
    private void addSelectionObjectWithoutPrepareTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0002");
        ReadObjectQuery query = new ReadObjectQuery(employee);
        query.setShouldPrepare(false);

        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, null);
        test.setQuery(query);
        test.setName("SelectionObjectWithoutPrepareTest");
        test.setDescription("Test expression with selection object and shouldPrepare(false).");
        addTest(test);
    }

    private void addSelfManagedEmployeeTests() {
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.equal(builder.get("manager"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 0);
        test.setExpression(expression);
        test.setName("SelfManagedEmployeeTest");
        test.setDescription("Tests equal between two object expressions.  An optimization could mean this expression using a single table.");
        addTest(test);

        Expression notExpression = ((Expression)expression.clone()).not();
        test = new ReadAllExpressionTest(Employee.class, 8);
        test.setExpression(notExpression);
        test.setName("NotSelfManagedEmployeeTest");
        test.setDescription("Test NOT of equal between two object expressions.  An optimization could mean this expression using a single table.");
        addTest(test);

        builder = new ExpressionBuilder(Employee.class);

        Expression notEqualExpression = builder.notEqual(builder.get("manager"));
        test = new ReadAllExpressionTest(Employee.class, 8);
        test.setExpression(notEqualExpression);
        test.setName("NotEqualSelfManagedEmployeeTest");
        test.setDescription("Test notEqual between two object expressions.  An optimization could mean this expression using a single table.");
        addTest(test);
    }

    private void addSingleTableJoinTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0002");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("address").get("city").equal("Ottawa");

        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
        test.setName("SingleTableJoinExpressionTest");
        test.setDescription("Test expression with joins");
        addTest(test);
    }

    public void addTests() {
        addSRGTests();
        //Add new tests here, if any.
        addAdvancedSybaseExpressionFunctionTest();
        // ET. The test doesn't work with DB2 jcc driver(Bug 4563813)
        addAdvancedDB2ExpressionFunctionTest();
        addInCollectionTest();
        // Bug 247076 - LiteralExpression does not print SQL in statement 
        addTest(new LiteralExpressionTest());
        // Bug 284884 - Quoted '?' symbol in expression literal causes ArrayIndexOutOfBoundsException 
        addTest(new LiteralSQLExpressionWithQuestionMarkTest("'?'", true));
        addTest(new LiteralSQLExpressionWithQuestionMarkTest("'?'", false));
        addTest(new LiteralSQLExpressionWithQuestionMarkTest("'???'", true));
        addTest(new LiteralSQLExpressionWithQuestionMarkTest("'???'", false));
        addTest(new LiteralSQLExpressionWithQuestionMarkTest("'? ? ?'", true));
        addTest(new LiteralSQLExpressionWithQuestionMarkTest("'? ? ?'", false));
        addTest(new LiteralSQLExpressionWithQuestionMarkTest("' 123?123 '", true));
        addTest(new LiteralSQLExpressionWithQuestionMarkTest("' 123?123 '", false));
        addTest(new LiteralSQLExpressionWithQuestionMarkTest("' 123 ? 123 '", true));
        addTest(new LiteralSQLExpressionWithQuestionMarkTest("' 123 ? 123 '", false));
        
        addRegexpTest();
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public void addSRGTests() {
        setManager(PopulationManager.getDefaultManager());

        addAggregateObjectObjectComparisonTest();
        addAggregateObjectIsNullTest();
        addAggregateObjectNullTest();

        addMultipleAndsTest();
        addMultipleAndsTest2();
        addMultipleAndsTest3();
        addMultipleAndsTest4();
        addMultipleAndsTest5();
        addMultipleAndsTest6();
        addObjectComparisonAcrossJoin();

        addExpressionFunctionTest();
        addExpressionMathTest();
        addAdvancedExpressionFunctionTest();
        addAdvancedExpressionMathTest();

        addBuilderEqualParameterTest();
        addAndTest();
        addNotTest();
        addAndNullTest();
        addOrTest();
        addOrNullTest();
        addEqualTest();
        addEqualUnneccessaryJoinTest();
        addEqualDoubleTest();
        addGreaterThanEqualTest();
        addGreaterThanTest();
        addJoinsShrinkResultSetSizeTest();
        addJoinsShrinkResultSetSizeTest2();
        addLessThanEqualTest();
        addLessThanTest();
        addIsNullTest();
        addIsNullWithJoinTest();
        addIsNotNullTest();
        addIsNotNullWithJoinTest();
        addInTest();
        addInMultipleExpressionWithConvertionParameterTest();
        addInSingleVectorParameterTest();
        addInMultipleExpressionParameterTest();
        addNotInTest();
        addInConversionTest();
        addLikeTest();
        addLikeIgnoreCaseTest();
        addLikeIgnoringCaseTest1();
        addLikeIgnoringCaseTest2();
        addLikeEscapeTest();
        addNotLikeTest();
        addBetweenTest();
        addBetweenTest2();

        addSelectionObjectWithoutPrepareTest();
        addSelfManagedEmployeeTests();
        addSingleTableJoinTest();
        addMultiplePrimaryKeyTest();
        addMultipleTableJoinTest1();
        addMultipleTableJoinTest2();
        addMultipleTableJoinTest3();
        addMultipleTableJoinTest4();
        addMultipleTableJoinTest5();
        addMultipleTableJoinTest6();
        addMultipleTableJoinTest7();
        addMultipleTableJoinTest8();
        addMultipleTableJoinTest9();
        addCustomQKJoinTest1();
        addCustomQKJoinTest2();
        addCustomQKTest1();
        addComputerViewCursoredStreamTest();
        addComputerViewTest1();
        addVehicleViewCursoredStreamTest();
        addVehicleViewTest1();
        addVehicleViewOrderByJoinTest();
        addVehicleViewOrderByOnlyTest();
        addNonFueledVehicleViewTest1();
        addVehicleViewJoinOnlyTest();

        addBadQueryKeyTest();
        addMismatchedQueryKeyTest();
        addBadQueryTableTest();
        addBadFieldWithTableTest();
        addBadToManyQueryKeyTest();
        addBadFieldWithTableTest();
        addBadAnyOfTest();

        addExpressionsDefaultingFieldTest();

        addOneToOneEqualTest();
        addTransformationTest();
        addAggregateQueryTest();

        addLowerCaseTest();
        addUpperCaseTest();

        addCustomDefaultExpressionTest();

        addOneToManyJoinTest1();
        addOneToManyJoinTest2();
        addOneToManyJoin2WithBatchReadTest();
        addOneToManyJoinTest3();
        addOneToManyJoinTest4();
        addOneToManyJoinTest5();
        addManyToManyJoinTest1();
        addManyToManyJoinTest2();
        addManyToManyJoinTest3();
        addManyToManyJoinTest4();
        addManyToManyJoinTest5();
        addOneToManyJoinObjectCompareTest();
        addOneToManyJoinObjectCompareTest2();
        addDirectCollectionJoinTest1();

        addComplexBooleanTest();

        addOneToOneObjectTest();
        addGetFunctionWithTwoArgumentsTest();

        addAggregeateCollectionJoinTest(org.eclipse.persistence.testing.models.aggregate.Agent.class);
        addAggregeateCollectionJoinTest(org.eclipse.persistence.testing.models.aggregate.Builder.class);
        addTest(new InvalidQueryKeyFunctionExpressionTest(new ExpressionBuilder().get("appartments").isNull()));

        addConstantEqualConstantTest();
        addParameterIsNullTest();

        addValueEqualValueTest();
        addMultiPlatformTest();
        addMultiPlatformTest2();
        addMultiPlatformTest3();
        addMultiPlatformTest4();
        addMultiPlatformTest5();
        
        addInheritanceTypeTest1();
        addInheritanceTypeTest2();
        addInheritanceTypeTest3();
    }
    
    //bug:277509 Entity type expressions
    private void addInheritanceTypeTest1() {
        ExpressionBuilder builder = new ExpressionBuilder(Project.class);
        
        Expression expression = builder.type().equal(SmallProject.class);
       
        ReadAllExpressionTest test = new ReadAllExpressionTest(Project.class, 10);
        test.setExpression(expression);
        test.setName("InheritanceTypeTest1");
        test.setDescription("Test ClassForInheritance expression using an equals comparison.");
        addTest(test);
    }
    
    //bug:277509 Entity type expressions
    private void addInheritanceTypeTest2() {
        ExpressionBuilder builder = new ExpressionBuilder(Project.class);
        
        Expression expression = builder.type().equal(LargeProject.class);
       
        ReadAllExpressionTest test = new ReadAllExpressionTest(Project.class, 5);
        test.setExpression(expression);
        test.setName("InheritanceTypeTest2");
        test.setDescription("Test ClassForInheritance expression using an equals comparison.");
        addTest(test);
    }
    
    //bug:277509 Entity type expressions
    private void addInheritanceTypeTest3() {
        ExpressionBuilder builder = new ExpressionBuilder(Project.class);
        Vector classes = new Vector();
        classes.add(LargeProject.class);
        classes.add(SmallProject.class);
        Expression expression = builder.type().in(classes);
       
        ReadAllExpressionTest test = new ReadAllExpressionTest(Project.class, 15);
        test.setExpression(expression);
        test.setName("InheritanceTypeTest3");
        test.setDescription("Test ClassForInheritance expression using an In comparison.");
        addTest(test);
    }

    
    private void addTransformationTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("gender").equal("Male");

        SearchTest test = new SearchTest();
        test.setExpression(expression);
        test.setErrorMessage("Failed to read objects from database (due to transformation failure)");
        test.setName("TransformationTest");
        test.setDescription("Test for correct transformation within a query (e.g. 'M' => 'Male')");
        addTest(test);
    }

    private void addTruncCurrentDateTest() {
        //Bug#3879510  SYSDATE should be used instead of SYSTIMESTAMP for currentDate because TRUNC is not supported for SYSTIMESTAMP
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("period").get("startDate").lessThanEqual(builder.currentDate().truncateDate("dd"));

        ReadAllExpressionTest test = new ReadAllExpressionTest(Employee.class, 12);
        test.setSupportedInMemory(false);
        test.setExpression(expression);
        test.setName("TruncCurrentDateTest");
        test.setDescription("Test if trunc on current date (SYSDATE) works.");
        test.addSupportedPlatform(OraclePlatform.class);
        addTest(test);
    }

    private void addUpperCaseTest() {
        Employee employee = (Employee)getManager().getObject(new org.eclipse.persistence.testing.models.employee.domain.Employee().getClass(), "0002");

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = builder.get("address").get("city").toUpperCase().equal("OTTAWA");

        ReadObjectExpressionTest test = new ReadObjectExpressionTest(employee, expression);
        test.setName("UpperCaseExpressionTest");
        test.setDescription("Test UPPER expression");
        addTest(test);
    }

    private void addVehicleViewCursoredStreamTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = ((builder.get("owner").get("name").equal("ABC")).and(builder.get("owner").get("name").equal("ABC"))).and((builder.get("passengerCapacity").equal(-1)).not());

        ReadAllExpressionTest test = new ReadAllExpressionTest(Vehicle.class, 5);
        ReadAllQuery query = new ReadAllQuery(Vehicle.class, expression);
        query.useCursoredStream(1, 1);
        test.setQuery(query);
        test.setName("VehicleViewCursoredStreamTest");
        test.setDescription("Test cursors with inheritance, supported if using views.  For bug 2718118.");
        test.addSupportedPlatform(OraclePlatform.class);
        test.addSupportedPlatform(SybasePlatform.class);
        test.addSupportedPlatform(SQLAnywherePlatform.class);
        //Uncomment it when we support MySQL 5.  Please refer to the comments in InheritanceSystem
        //test.addSupportedPlatform(MySQLPlatform.class);
        addTest(test);
    }

    private void addVehicleViewJoinOnlyTest() {
        ReadAllExpressionTest test = new ReadAllExpressionTest(Vehicle.class, 15);
        test.getQuery(true).addJoinedAttribute("owner");
        test.setName("VehicleViewJoinOnlyTest");
        test.setDescription("Test inheritance view with joining only.");
        //CREATE VIEW statement was added in MySQL 5.0.1.  Remove the condition when we support MySQL 5
        //together with InheritanceSystem
        test.addUnsupportedPlatform(MySQLPlatform.class);
        //CREATE MATERIALIZED VIEW is supported in TimesTen, but seems to have trouble with outer join.
        //TT0805: Materialized view with no non-nullable selected column from inner table BUS has not been implemented
        test.addUnsupportedPlatform(org.eclipse.persistence.platform.database.TimesTenPlatform.class);
        //There are no sqlserverView() and db2View() methods defined in Computer and Vehicle classes
        test.addUnsupportedPlatform(SQLServerPlatform.class);
        test.addUnsupportedPlatform(DB2Platform.class);
        addTest(test);
    }

    private void addVehicleViewOrderByJoinTest() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = ((builder.get("owner").get("name").equal("ABC")).and(builder.get("owner").get("name").equal("ABC"))).and((builder.get("passengerCapacity").equal(-1)).not());

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

    private void addVehicleViewOrderByOnlyTest() {
        ReadAllExpressionTest test = new ReadAllExpressionTest(Vehicle.class, 19);
        test.getQuery(true).addDescendingOrdering("id");
        test.setName("VehicleViewOrderByOnlyTest");
        test.setDescription("Test inheritance view with ordering only.");
        addTest(test);
    }

    private void addVehicleViewTest1() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression = ((builder.get("owner").get("name").equal("ABC")).and(builder.get("owner").get("name").equal("ABC"))).and((builder.get("passengerCapacity").equal(-1)).not());

        ReadAllExpressionTest test = new ReadAllExpressionTest(Vehicle.class, 5);
        test.setExpression(expression);
        test.setName("VehicleViewTest1");
        test.setDescription("Test expression against view, or multiple table subclass read.");
        addTest(test);
    }

    protected PopulationManager getManager() {
        return manager;
    }

    protected void setManager(PopulationManager theManager) {
        manager = theManager;
    }
}
