/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


package org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced;

import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.exceptions.QueryException;

import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.EmployeePopulator;
import org.eclipse.persistence.queries.ConstructorReportItem;

import junit.framework.TestSuite;
import junit.framework.Test;
import junit.extensions.TestSetup;

import java.util.Vector;
import java.util.Iterator;

public class ReportQueryConstructorExpressionTestSuite extends JUnitTestCase {

    protected boolean m_reset = false;    // reset gets called twice on error

    public ReportQueryConstructorExpressionTestSuite() {
    }
  
    public ReportQueryConstructorExpressionTestSuite(String name) {
        super(name);
    }
        
    public void setUp () {
        m_reset = true;
        super.setUp();
        clearCache("fieldaccess");
    }
    
    public void tearDown () {
        if (m_reset) {
            m_reset = false;
        }
        super.tearDown();
    }
    
    public static Test suite() {    
        TestSuite suite = new TestSuite(ReportQueryConstructorExpressionTestSuite.class);
        suite.setName("ReportQueryConstructorExpressionTestSuite (fieldaccess)");      

        return new TestSetup(suite) {
        
            protected void setUp(){
                           
                //get session to start setup
                DatabaseSession session = JUnitTestCase.getServerSession("fieldaccess");
                
                //create a new EmployeePopulator
                EmployeePopulator employeePopulator = new EmployeePopulator();
                
                new AdvancedTableCreator().replaceTables(session);
           
                //Populate the tables
                employeePopulator.buildExamples();
                
                //Persist the examples in the database
                employeePopulator.persistExample(session);       
            }

            protected void tearDown() {
                clearCache("fieldaccess");
            }
        };
    }
    
    public void testSimpleConstructorExpression(){
        ExpressionBuilder employees = new ExpressionBuilder();
        ReportQuery query = new ReportQuery(Employee.class, employees);
        query.addAttribute("firstName");
        query.addAttribute("lastName");
        org.eclipse.persistence.jpa.EntityManager em = (org.eclipse.persistence.jpa.EntityManager) createEntityManager("fieldaccess");          

        Vector reportResults = (Vector)em.getActiveSession().executeQuery(query);
        
        employees = new ExpressionBuilder();
        query = new ReportQuery(Employee.class, employees);
        
        Class[] argTypes = new Class[]{String.class, String.class};
        query.beginAddingConstructorArguments(Employee.class, argTypes);
        query.addAttribute("firstName");
        query.addAttribute("lastName");
        query.endAddingToConstructorItem();
        Vector results = (Vector)em.getActiveSession().executeQuery(query);
        Iterator i = results.iterator();
        Iterator report = reportResults.iterator();
        while (i.hasNext()){
            Employee emp = (Employee)((ReportQueryResult)i.next()).get(Employee.class.getName());
            ReportQueryResult result = (ReportQueryResult)report.next();
            if (emp.getFirstName() != null){
                assertTrue("Null first name", result.get("firstName") != null);
                assertTrue("Wrong first name", emp.getFirstName().equals(result.get("firstName")));
            }
            if (emp.getLastName() != null){
                assertTrue("Null last name", result.get("lastName") != null);
                assertTrue("Wrong last name", emp.getLastName().equals(result.get("lastName")));
            }
            
        }
        assertTrue("Different result sizes", !(report.hasNext()));
    }

    public void testSimpleConstructorExpressionWithNamedQuery(){
        ExpressionBuilder employees = new ExpressionBuilder();
        ReportQuery query = new ReportQuery(Employee.class, employees);
        query.addAttribute("firstName");
        query.addAttribute("lastName");
        org.eclipse.persistence.jpa.EntityManager em = (org.eclipse.persistence.jpa.EntityManager) createEntityManager("fieldaccess");          

        Vector reportResults = (Vector)em.getActiveSession().executeQuery(query);

        Vector results = (Vector)em.createNamedQuery("constuctFieldAccessEmployees").getResultList();
        Iterator i = results.iterator();
        Iterator report = reportResults.iterator();
        while (i.hasNext()){
            Employee emp = (Employee)i.next();
            ReportQueryResult result = (ReportQueryResult)report.next();
            if (emp.getFirstName() != null){
                assertTrue("Null first name", result.get("firstName") != null);
                assertTrue("Wrong first name", emp.getFirstName().equals(result.get("firstName")));
            }
            if (emp.getLastName() != null){
                assertTrue("Null last name", result.get("lastName") != null);
                assertTrue("Wrong last name", emp.getLastName().equals(result.get("lastName")));
            }
            
        }
        assertTrue("Different result sizes", !(report.hasNext()));
    }

    public void testMultipleTypeConstructorExpression(){
        ExpressionBuilder employees = new ExpressionBuilder();
        ReportQuery query = new ReportQuery(Employee.class, employees);
        query.addAttribute("firstName");
        query.addItem("endDate", employees.get("period").get("endDate"));
        query.addAttribute("id");

        org.eclipse.persistence.jpa.EntityManager em = (org.eclipse.persistence.jpa.EntityManager) createEntityManager("fieldaccess");          

        Vector reportResults = (Vector)em.getActiveSession().executeQuery(query);
        query = new ReportQuery(Employee.class, employees);

        Class[] argTypes = new Class[]{String.class, java.sql.Date.class, Integer.class};
        query.beginAddingConstructorArguments(DataHolder.class, argTypes);
        query.addAttribute("firstName");
        query.addItem("endDate", employees.get("period").get("endDate"));
        query.addAttribute("id");
        query.endAddingToConstructorItem();
        Vector results = (Vector)em.getActiveSession().executeQuery(query);
        Iterator i = results.iterator();
        Iterator report = reportResults.iterator();
        while (i.hasNext()){
            DataHolder holder = (DataHolder)((ReportQueryResult)i.next()).get(DataHolder.class.getName());
            ReportQueryResult result = (ReportQueryResult)report.next();
            if (!(holder.getString() == null && result.get("firstName") == null)){
                assertTrue("Wrong first name", holder.getString().equals(result.get("firstName")));
            }
            if (!(holder.getDate() == null && result.get("endDate") == null)){
                assertTrue("Wrong date", holder.getDate().equals(result.get("endDate")));
            }
            if (!(holder.getInteger() == null && result.get("id") == null)){
                assertTrue("Wrong integer", holder.getInteger().equals(result.get("id")));
            }
        }
        assertTrue("Different result sizes", !(report.hasNext()));
    }
    
    public void testNonExistantConstructorConstructorExpression(){
        ExpressionBuilder employees = new ExpressionBuilder();
        ReportQuery query = new ReportQuery(Employee.class, employees);
        
        Class[] argTypes = new Class[]{String.class, java.sql.Date.class, Integer.class};
        query.beginAddingConstructorArguments(Employee.class, argTypes);
        query.addAttribute("firstName");
        query.addItem("endDate", employees.get("period").get("endDate"));
        query.addAttribute("id");
        query.endAddingToConstructorItem();
        org.eclipse.persistence.jpa.EntityManager em = (org.eclipse.persistence.jpa.EntityManager) createEntityManager("fieldaccess");          
        QueryException exception = null;
        try{
            em.getActiveSession().executeQuery(query);
        } catch (QueryException ex){
            exception = ex;
        }
        assertTrue("Exception not throw. ", exception != null);
        
    }

    public void testPrimitiveConstructorExpression(){
        ExpressionBuilder employees = new ExpressionBuilder();
        ReportQuery query = new ReportQuery(Employee.class, employees);
        query.addAttribute("salary");
        org.eclipse.persistence.jpa.EntityManager em = (org.eclipse.persistence.jpa.EntityManager) createEntityManager("fieldaccess");          

        Vector reportResults = (Vector)em.getActiveSession().executeQuery(query);

        query = new ReportQuery(Employee.class, employees);
        Class[] argTypes = new Class[]{int.class};
        query.beginAddingConstructorArguments(DataHolder.class, argTypes);
        query.addAttribute("salary");
        query.endAddingToConstructorItem();
        Vector results = (Vector)em.getActiveSession().executeQuery(query);
        Iterator i = results.iterator();
        Iterator report = reportResults.iterator();
        while (i.hasNext()){
            DataHolder holder = (DataHolder)((ReportQueryResult)i.next()).get(DataHolder.class.getName());
            ReportQueryResult result = (ReportQueryResult)report.next();
            assertTrue("Incorrect salary ", ((Integer)result.get("salary")).intValue() == holder.getPrimitiveInt());
            
        }
        assertTrue("Different result sizes", !(report.hasNext()));
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
        org.eclipse.persistence.jpa.EntityManager em = (org.eclipse.persistence.jpa.EntityManager) createEntityManager("fieldaccess");          

        Vector reportResults = (Vector)em.getActiveSession().executeQuery(query);

        ConstructorReportItem citem = new ConstructorReportItem("Employee");
        citem.setResultType(Employee.class);
        
        citem.addAttribute(employees.get("firstName"));
        citem.addAttribute(employees.get("lastName"));
        
        query.addConstructorReportItem(citem);

        Vector results = (Vector)em.getActiveSession().executeQuery(query);
        Iterator i = results.iterator();
        Iterator report = reportResults.iterator();
        while (i.hasNext()){
            ReportQueryResult result1 = (ReportQueryResult)i.next();
            Employee emp = (Employee) result1.get("Employee");
            //Employee emp = (Employee)i.next();
            ReportQueryResult result2 = (ReportQueryResult)report.next();
            if (emp.getFirstName() != null){
                assertTrue("Null first name in constructor query", result1.get("firstName") != null);
                assertTrue("Null first name", result2.get("firstName") != null);
                assertTrue("Wrong first name", emp.getFirstName().equals(result2.get("firstName")));
            }
            if (emp.getLastName() != null){
                assertTrue("Null last name in constructor query", result1.get("lastName") != null);
                assertTrue("Null last name", result2.get("lastName") != null);
                assertTrue("Wrong last name", emp.getLastName().equals(result2.get("lastName")));
            }
            
        }
        assertTrue("Different result sizes", !(report.hasNext()));
    }

}

