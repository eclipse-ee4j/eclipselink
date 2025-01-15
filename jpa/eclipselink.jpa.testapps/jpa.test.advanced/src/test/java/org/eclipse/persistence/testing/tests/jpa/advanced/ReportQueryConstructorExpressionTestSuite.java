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


package org.eclipse.persistence.testing.tests.jpa.advanced;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ConstructorReportItem;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.holders.DataHolder;

import java.util.Iterator;
import java.util.Vector;

public class ReportQueryConstructorExpressionTestSuite extends JUnitTestCase {

    protected boolean m_reset = false;    // reset gets called twice on error

    public ReportQueryConstructorExpressionTestSuite() {
    }

    public ReportQueryConstructorExpressionTestSuite(String name) {
        super(name);
    }

    @Override
    public void setUp () {
        m_reset = true;
        super.setUp();
        clearCache();
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
        suite.setName("ReportQueryConstructorExpressionTestSuite");

        suite.addTest(new ReportQueryConstructorExpressionTestSuite("testSetup"));
        suite.addTest(new ReportQueryConstructorExpressionTestSuite("testSimpleConstructorExpression"));
        suite.addTest(new ReportQueryConstructorExpressionTestSuite("testSimpleConstructorExpressionWithNamedQuery"));
        suite.addTest(new ReportQueryConstructorExpressionTestSuite("testMultipleTypeConstructorExpression"));
        suite.addTest(new ReportQueryConstructorExpressionTestSuite("testNonExistantConstructorConstructorExpression"));
        suite.addTest(new ReportQueryConstructorExpressionTestSuite("testPrimitiveConstructorExpression"));
        suite.addTest(new ReportQueryConstructorExpressionTestSuite("testConstructorEJBQLWithInheritance"));
        suite.addTest(new ReportQueryConstructorExpressionTestSuite("testConstructorExpressionWithOtherAttributes"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());
        //create a new EmployeePopulator
        EmployeePopulator employeePopulator = new EmployeePopulator(supportsStoredProcedures());

        //Populate the tables
        employeePopulator.buildExamples();

        //Persist the examples in the database
        employeePopulator.persistExample(JUnitTestCase.getServerSession());

        clearCache();
    }

    public void testSimpleConstructorExpression(){
        ExpressionBuilder employees = new ExpressionBuilder();
        ReportQuery query = new ReportQuery(Employee.class, employees);
        query.addAttribute("firstName");
        query.addAttribute("lastName");
        EntityManager em = createEntityManager();

        @SuppressWarnings({"unchecked"})
        Vector<ReportQueryResult> reportResults = (Vector<ReportQueryResult>) getServerSession().executeQuery(query);

        employees = new ExpressionBuilder();
        query = new ReportQuery(Employee.class, employees);

        Class<?>[] argTypes = new Class<?>[]{String.class, String.class};
        query.beginAddingConstructorArguments(Employee.class, argTypes);
        query.addAttribute("firstName");
        query.addAttribute("lastName");
        query.endAddingToConstructorItem();
        @SuppressWarnings({"unchecked"})
        Vector<ReportQueryResult> results = (Vector<ReportQueryResult>) getServerSession().executeQuery(query);
        Iterator<ReportQueryResult> i = results.iterator();
        Iterator<ReportQueryResult> report = reportResults.iterator();
        while (i.hasNext()){
            Employee emp = (Employee) i.next().get(Employee.class.getName());
            ReportQueryResult result = report.next();
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
        EntityManager em = createEntityManager();

        @SuppressWarnings({"unchecked"})
        Vector<ReportQueryResult> reportResults = (Vector<ReportQueryResult>) getServerSession().executeQuery(query);

        @SuppressWarnings({"unchecked"})
        Vector<Employee> results = (Vector<Employee>) em.createNamedQuery("constuctEmployees").getResultList();
        Iterator<Employee> i = results.iterator();
        Iterator<ReportQueryResult> report = reportResults.iterator();
        while (i.hasNext()){
            Employee emp = i.next();
            ReportQueryResult result = report.next();
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

        EntityManager em = createEntityManager();

        @SuppressWarnings({"unchecked"})
        Vector<ReportQueryResult> reportResults = (Vector<ReportQueryResult>) getServerSession().executeQuery(query);
        query = new ReportQuery(Employee.class, employees);

        Class<?>[] argTypes = new Class<?>[]{String.class, java.sql.Date.class, Integer.class};
        query.beginAddingConstructorArguments(DataHolder.class, argTypes);
        query.addAttribute("firstName");
        query.addItem("endDate", employees.get("period").get("endDate"));
        query.addAttribute("id");
        query.endAddingToConstructorItem();
        @SuppressWarnings({"unchecked"})
        Vector<ReportQueryResult> results = (Vector<ReportQueryResult>) getServerSession().executeQuery(query);
        Iterator<ReportQueryResult> i = results.iterator();
        Iterator<ReportQueryResult> report = reportResults.iterator();
        while (i.hasNext()){
            DataHolder holder = (DataHolder) i.next().get(DataHolder.class.getName());
            ReportQueryResult result = report.next();
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
        EntityManager em = createEntityManager();
        QueryException exception = null;
        try{
            getServerSession().executeQuery(query);
        } catch (QueryException ex){
            exception = ex;
        }
        assertNotNull("Exception not throw. ", exception);

    }

    public void testPrimitiveConstructorExpression(){
        ExpressionBuilder employees = new ExpressionBuilder();
        ReportQuery query = new ReportQuery(Employee.class, employees);
        query.addAttribute("salary");
        EntityManager em = createEntityManager();

        @SuppressWarnings({"unchecked"})
        Vector<ReportQueryResult> reportResults = (Vector<ReportQueryResult>) getServerSession().executeQuery(query);

        query = new ReportQuery(Employee.class, employees);
        Class<?>[] argTypes = new Class<?>[]{int.class};
        query.beginAddingConstructorArguments(DataHolder.class, argTypes);
        query.addAttribute("salary");
        query.endAddingToConstructorItem();
        @SuppressWarnings({"unchecked"})
        Vector<ReportQueryResult> results = (Vector<ReportQueryResult>) getServerSession().executeQuery(query);
        Iterator<ReportQueryResult> i = results.iterator();
        Iterator<ReportQueryResult> report = reportResults.iterator();
        while (i.hasNext()){
            DataHolder holder = (DataHolder) i.next().get(DataHolder.class.getName());
            ReportQueryResult result = report.next();
            assertEquals("Incorrect salary ", (int) (Integer) result.get("salary"), holder.getPrimitiveInt());

        }
        assertFalse("Different result sizes", report.hasNext());
    }

    public void testConstructorEJBQLWithInheritance() {
        Exception exception = null;
        try {
            createEntityManager().createNamedQuery("constructLProject").getResultList();
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
        EntityManager em = createEntityManager();

        @SuppressWarnings({"unchecked"})
        Vector<ReportQueryResult> reportResults = (Vector<ReportQueryResult>) getServerSession().executeQuery(query);

        ConstructorReportItem citem = new ConstructorReportItem("Employee");
        citem.setResultType(Employee.class);

        citem.addAttribute(employees.get("firstName"));
        citem.addAttribute(employees.get("lastName"));

        query.addConstructorReportItem(citem);

        @SuppressWarnings({"unchecked"})
        Vector<ReportQueryResult> results = (Vector<ReportQueryResult>)getServerSession().executeQuery(query);
        Iterator<ReportQueryResult> i = results.iterator();
        Iterator<ReportQueryResult> report = reportResults.iterator();
        while (i.hasNext()){
            ReportQueryResult result1 = i.next();
            Employee emp = (Employee) result1.get("Employee");
            //Employee emp = (Employee)i.next();
            ReportQueryResult result2 = report.next();
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

