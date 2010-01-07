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


package org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Employee;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Project;

public class ReportQueryMultipleReturnTestSuite extends JUnitTestCase {
    protected boolean m_reset = false;    // reset gets called twice on error

        
    public ReportQueryMultipleReturnTestSuite() {
    }
    
    public ReportQueryMultipleReturnTestSuite(String name) {
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
    
    public void testSimpleReturnDirectToField(){
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.returnWithoutReportQueryResult();
        reportQuery.setReferenceClass(Employee.class);
        ExpressionBuilder empbuilder = new ExpressionBuilder();
        reportQuery.addAttribute("salary",empbuilder.get("salary"));
        reportQuery.setSelectionCriteria(empbuilder.get("salary").greaterThan(1));
        List result = (List)getServerSession("fieldaccess").executeQuery(reportQuery);
        Object resultItem = result.get(0);
        assertTrue("Failed to return Employees correctly, Not A Number", Number.class.isAssignableFrom(resultItem.getClass()));
        assertTrue("Failed to return Employees correctly, Not Correct Result", ((Number)resultItem).intValue() > 1);
    }
    
    public void testSimpleReturnObject(){
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.returnWithoutReportQueryResult();
        reportQuery.setReferenceClass(Employee.class);
        ExpressionBuilder empbuilder = new ExpressionBuilder();
        reportQuery.addAttribute("manager",empbuilder.get("manager"));
        reportQuery.setSelectionCriteria(empbuilder.get("salary").greaterThan(1));
        List result = (List)getServerSession("fieldaccess").executeQuery(reportQuery);
        Object resultItem = result.get(0);
        assertTrue("Failed to return Employees correctly, Not An Employee", Employee.class.isAssignableFrom(resultItem.getClass()));
    }
    
    public void testReturnObjectAndDirectToField(){
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.returnWithoutReportQueryResult();
        reportQuery.setReferenceClass(Employee.class);
        ExpressionBuilder empbuilder = new ExpressionBuilder();
        reportQuery.addAttribute("salary",empbuilder.get("salary"));
        reportQuery.addAttribute("manager",empbuilder.get("manager"));
        reportQuery.setSelectionCriteria(empbuilder.get("salary").greaterThan(1));
        List result = (List)getServerSession("fieldaccess").executeQuery(reportQuery);
        Object innerResult = result.get(0);
        assertTrue("Failed to return Employees correctly, Not an Object Array", Object[].class.isAssignableFrom(innerResult.getClass()));
        Object resultItem = ((Object[])innerResult)[0];
        assertTrue("Failed to return Employees correctly, Not A Number", Number.class.isAssignableFrom(resultItem.getClass()));
        assertTrue("Failed to return Employees correctly, Not Correct Result", ((Number)resultItem).intValue() > 1);
        resultItem = ((Object[])innerResult)[1];
        assertTrue("Failed to return Employees correctly, Not An Employee", Employee.class.isAssignableFrom(resultItem.getClass()));
    }
    
    public void testReturnUnrelatedObjectAndDirectToField(){
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.returnWithoutReportQueryResult();
        reportQuery.setReferenceClass(Employee.class);
        ExpressionBuilder empbuilder = new ExpressionBuilder();
        ExpressionBuilder addBuilder = new ExpressionBuilder(Address.class);
        reportQuery.addAttribute("salary",empbuilder.get("salary"));
        reportQuery.addAttribute("manager",empbuilder.get("manager"));
        reportQuery.addAttribute("adress.city",addBuilder.get("city"));
        reportQuery.setSelectionCriteria(empbuilder.get("salary").greaterThan(1));
        List result = (List)getServerSession("fieldaccess").executeQuery(reportQuery);
        Object innerResult = result.get(0);
        assertTrue("Failed to return Employees correctly, Not an Object Array", Object[].class.isAssignableFrom(innerResult.getClass()));
        Object resultItem = ((Object[])innerResult)[0];
        assertTrue("Failed to return Employees correctly, Not A Number", Number.class.isAssignableFrom(resultItem.getClass()));
        assertTrue("Failed to return Employees correctly, Not Correct Result", ((Number)resultItem).intValue() > 1);
        resultItem = ((Object[])innerResult)[1];
        assertTrue("Failed to return Employees correctly, Not An Employee", Employee.class.isAssignableFrom(resultItem.getClass()));
        resultItem = ((Object[])innerResult)[2];
        assertTrue("Failed to return Employees correctly, Not a City", String.class.isAssignableFrom(resultItem.getClass()));
    }
    
    public void testInheritanceMultiTableException(){
        try {
            ReportQuery reportQuery = new ReportQuery();
            reportQuery.returnWithoutReportQueryResult();
            reportQuery.setReferenceClass(Project.class);
            ExpressionBuilder empbuilder = new ExpressionBuilder();
            reportQuery.addAttribute("project",empbuilder);
            List result = (List)getServerSession("fieldaccess").executeQuery(reportQuery);
            result.size();
        } catch (QueryException ex){
           return; 
        }
        fail("Failed to throw exception, ReportItems must not have multi-table inheritance.");
    }
    
    public void testReturnRootObject(){
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.returnWithoutReportQueryResult();
        reportQuery.setReferenceClass(LargeProject.class);
        ExpressionBuilder empbuilder = new ExpressionBuilder();
        reportQuery.addAttribute("project",empbuilder);
        List result = (List)getServerSession("fieldaccess").executeQuery(reportQuery);
        Object resultItem = result.get(0);
        assertTrue("Failed to return Project as expression root correctly, Not A Project", LargeProject.class.isAssignableFrom(resultItem.getClass()));
    }

    public static Test suite() {
        return new TestSuite(ReportQueryMultipleReturnTestSuite.class);
    }    

}
