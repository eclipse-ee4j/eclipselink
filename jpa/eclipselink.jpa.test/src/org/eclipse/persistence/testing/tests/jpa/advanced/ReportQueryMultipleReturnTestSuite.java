/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.testing.models.jpa.advanced.LargeProject;

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
        clearCache();
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
        List result = (List)getServerSession().executeQuery(reportQuery);
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
        List result = (List)getServerSession().executeQuery(reportQuery);
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
        List result = (List)getServerSession().executeQuery(reportQuery);
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
        List result = (List)getServerSession().executeQuery(reportQuery);
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

    public void testReturnRootObject(){
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.returnWithoutReportQueryResult();
        reportQuery.setReferenceClass(LargeProject.class);
        reportQuery.addAttribute("project", reportQuery.getExpressionBuilder());
        List result = (List)getServerSession().executeQuery(reportQuery);
        Object resultItem = result.get(0);
        assertTrue("Failed to return Project as expression root correctly, Not A Project", LargeProject.class.isAssignableFrom(resultItem.getClass()));
    }

    public static Test suite() {
        return new TestSuite(ReportQueryMultipleReturnTestSuite.class);
    }

}
