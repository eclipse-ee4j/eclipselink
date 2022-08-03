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


package org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ConstructorReportItem;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class ReportQueryConstructorExpressionTest extends JUnitTestCase {

    protected boolean m_reset = false;    // reset gets called twice on error

    public ReportQueryConstructorExpressionTest() {
    }

    public ReportQueryConstructorExpressionTest(String name) {
        super(name);
    }

    @Override
    public void setUp () {
        m_reset = true;
        super.setUp();
        clearCache("fieldaccess");
    }

    @Override
    public void tearDown () {
        if (m_reset) {
            m_reset = false;
        }
        super.tearDown();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("ReportQueryConstructorExpressionTest (fieldaccess)");

        suite.addTest(new ReportQueryConstructorExpressionTest("testSetup"));
        suite.addTest(new ReportQueryConstructorExpressionTest("testSimpleConstructorExpression"));
        suite.addTest(new ReportQueryConstructorExpressionTest("testSimpleConstructorExpressionWithNamedQuery"));
        suite.addTest(new ReportQueryConstructorExpressionTest("testMultipleTypeConstructorExpression"));
        suite.addTest(new ReportQueryConstructorExpressionTest("testNonExistantConstructorConstructorExpression"));
        suite.addTest(new ReportQueryConstructorExpressionTest("testPrimitiveConstructorExpression"));
        suite.addTest(new ReportQueryConstructorExpressionTest("testConstructorEJBQLWithInheritance"));
        suite.addTest(new ReportQueryConstructorExpressionTest("testConstructorExpressionWithOtherAttributes"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession("fieldaccess"));
        //create a new EmployeePopulator
        EmployeePopulator employeePopulator = new EmployeePopulator();

        //Populate the tables
        employeePopulator.buildExamples();

        //Persist the examples in the database
        employeePopulator.persistExample(JUnitTestCase.getServerSession("fieldaccess"));

        clearCache("fieldaccess");
    }

    public void testSimpleConstructorExpression(){
        ExpressionBuilder employees = new ExpressionBuilder();
        ReportQuery query = new ReportQuery(Employee.class, employees);
        query.addAttribute("firstName");
        query.addAttribute("lastName");

        List reportResults = (List)getServerSession("fieldaccess").executeQuery(query);

        employees = new ExpressionBuilder();
        query = new ReportQuery(Employee.class, employees);

        Class<?>[] argTypes = new Class<?>[]{String.class, String.class};
        query.beginAddingConstructorArguments(Employee.class, argTypes);
        query.addAttribute("firstName");
        query.addAttribute("lastName");
        query.endAddingToConstructorItem();
        Vector results = (Vector)getServerSession("fieldaccess").executeQuery(query);
        Iterator i = results.iterator();
        Iterator report = reportResults.iterator();
        while (i.hasNext()){
            Employee emp = (Employee)((ReportQueryResult)i.next()).get(Employee.class.getName());
            ReportQueryResult result = (ReportQueryResult)report.next();
            if (emp.getFirstName() != null){
                assertNotNull("Null first name", result.get("firstName"));
                assertEquals("Wrong first name", emp.getFirstName(), result.get("firstName"));
            }
            if (emp.getLastName() != null){
                assertNotNull("Null last name", result.get("lastName"));
                assertEquals("Wrong last name", emp.getLastName(), result.get("lastName"));
            }

        }
        assertFalse("Different result sizes", report.hasNext());
    }

    public void testSimpleConstructorExpressionWithNamedQuery(){
        ExpressionBuilder employees = new ExpressionBuilder();
        ReportQuery query = new ReportQuery(Employee.class, employees);
        query.addAttribute("firstName");
        query.addAttribute("lastName");
        EntityManager em = createEntityManager("fieldaccess");

        Vector reportResults = (Vector)getServerSession("fieldaccess").executeQuery(query);

        Vector results = (Vector)em.createNamedQuery("constuctFieldAccessEmployees").getResultList();
        Iterator i = results.iterator();
        Iterator report = reportResults.iterator();
        while (i.hasNext()){
            Employee emp = (Employee)i.next();
            ReportQueryResult result = (ReportQueryResult)report.next();
            if (emp.getFirstName() != null){
                assertNotNull("Null first name", result.get("firstName"));
                assertEquals("Wrong first name", emp.getFirstName(), result.get("firstName"));
            }
            if (emp.getLastName() != null){
                assertNotNull("Null last name", result.get("lastName"));
                assertEquals("Wrong last name", emp.getLastName(), result.get("lastName"));
            }

        }
        assertFalse("Different result sizes", report.hasNext());
    }

    public void testMultipleTypeConstructorExpression(){
        ExpressionBuilder employees = new ExpressionBuilder();
        ReportQuery query = new ReportQuery(Employee.class, employees);
        query.addAttribute("firstName");
        query.addItem("endDate", employees.get("period").get("endDate"));
        query.addAttribute("id");

        List reportResults = (List)getServerSession("fieldaccess").executeQuery(query);
        query = new ReportQuery(Employee.class, employees);

        Class<?>[] argTypes = new Class<?>[]{String.class, java.sql.Date.class, Integer.class};
        query.beginAddingConstructorArguments(DataHolder.class, argTypes);
        query.addAttribute("firstName");
        query.addItem("endDate", employees.get("period").get("endDate"));
        query.addAttribute("id");
        query.endAddingToConstructorItem();
        Vector results = (Vector)getServerSession("fieldaccess").executeQuery(query);
        Iterator i = results.iterator();
        Iterator report = reportResults.iterator();
        while (i.hasNext()){
            DataHolder holder = (DataHolder)((ReportQueryResult)i.next()).get(DataHolder.class.getName());
            ReportQueryResult result = (ReportQueryResult)report.next();
            if (!(holder.getString() == null && result.get("firstName") == null)){
                assertEquals("Wrong first name", holder.getString(), result.get("firstName"));
            }
            if (!(holder.getDate() == null && result.get("endDate") == null)){
                assertEquals("Wrong date", holder.getDate(), result.get("endDate"));
            }
            if (!(holder.getInteger() == null && result.get("id") == null)){
                assertEquals("Wrong integer", holder.getInteger(), result.get("id"));
            }
        }
        assertFalse("Different result sizes", report.hasNext());
    }

    public void testNonExistantConstructorConstructorExpression(){
        ExpressionBuilder employees = new ExpressionBuilder();
        ReportQuery query = new ReportQuery(Employee.class, employees);

        Class<?>[] argTypes = new Class<?>[]{String.class, java.sql.Date.class, Integer.class};
        query.beginAddingConstructorArguments(Employee.class, argTypes);
        query.addAttribute("firstName");
        query.addItem("endDate", employees.get("period").get("endDate"));
        query.addAttribute("id");
        query.endAddingToConstructorItem();
        QueryException exception = null;
        try {
            getServerSession("fieldaccess").executeQuery(query);
        } catch (QueryException ex){
            exception = ex;
        }
        assertNotNull("Exception not throw. ", exception);
    }

    public void testPrimitiveConstructorExpression(){
        ExpressionBuilder employees = new ExpressionBuilder();
        ReportQuery query = new ReportQuery(Employee.class, employees);
        query.addAttribute("salary");

        List reportResults = (List)getServerSession("fieldaccess").executeQuery(query);

        query = new ReportQuery(Employee.class, employees);
        Class<?>[] argTypes = new Class<?>[]{int.class};
        query.beginAddingConstructorArguments(DataHolder.class, argTypes);
        query.addAttribute("salary");
        query.endAddingToConstructorItem();
        Vector results = (Vector)getServerSession("fieldaccess").executeQuery(query);
        Iterator i = results.iterator();
        Iterator report = reportResults.iterator();
        while (i.hasNext()){
            DataHolder holder = (DataHolder)((ReportQueryResult)i.next()).get(DataHolder.class.getName());
            ReportQueryResult result = (ReportQueryResult)report.next();
            assertEquals("Incorrect salary ", (int) (Integer) result.get("salary"), holder.getPrimitiveInt());

        }
        assertFalse("Different result sizes", report.hasNext());
    }

      public void testConstructorEJBQLWithInheritance() {
        Exception exception = null;
        try {
            createEntityManager("fieldaccess").createNamedQuery("constructFieldAccessLProject").getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        }

        assertNull("Exception was caught", exception);
    }

    public void testConstructorExpressionWithOtherAttributes(){
        ExpressionBuilder employees = new ExpressionBuilder();
        ReportQuery query = new ReportQuery(Employee.class, employees);
        query.addAttribute("firstName");
        query.addAttribute("lastName");

        List reportResults = (List)getServerSession("fieldaccess").executeQuery(query);

        ConstructorReportItem citem = new ConstructorReportItem("Employee");
        citem.setResultType(Employee.class);

        citem.addAttribute(employees.get("firstName"));
        citem.addAttribute(employees.get("lastName"));

        query.addConstructorReportItem(citem);

        Vector results = (Vector)getServerSession("fieldaccess").executeQuery(query);
        Iterator i = results.iterator();
        Iterator report = reportResults.iterator();
        while (i.hasNext()){
            ReportQueryResult result1 = (ReportQueryResult)i.next();
            Employee emp = (Employee) result1.get("Employee");
            //Employee emp = (Employee)i.next();
            ReportQueryResult result2 = (ReportQueryResult)report.next();
            if (emp.getFirstName() != null){
                assertNotNull("Null first name in constructor query", result1.get("firstName"));
                assertNotNull("Null first name", result2.get("firstName"));
                assertEquals("Wrong first name", emp.getFirstName(), result2.get("firstName"));
            }
            if (emp.getLastName() != null){
                assertNotNull("Null last name in constructor query", result1.get("lastName"));
                assertNotNull("Null last name", result2.get("lastName"));
                assertEquals("Wrong last name", emp.getLastName(), result2.get("lastName"));
            }

        }
        assertFalse("Different result sizes", report.hasNext());
    }

}

