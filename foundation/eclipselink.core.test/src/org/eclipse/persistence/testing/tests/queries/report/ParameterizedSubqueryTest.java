/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.queries.report;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.persistence.exceptions.EclipseLinkException;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.Project;

/**
 * Test using a Parameterized Subquery in a Report Query
 * EL Bug 415010
 *
 * @author dminsky
 */
public class ParameterizedSubqueryTest extends TestCase {

    protected EclipseLinkException exception;
    protected List<ReportQueryResult> results;
    protected BigDecimal employeeId;
    protected String projectName;
    protected ReportQuery queryToExecute;

    public ParameterizedSubqueryTest() {
        super();
        setDescription("Test using a Parameterized Subquery in a Report Query");
    }

    public void setup() {
        if (getSession().isRemoteSession()) {
            throwWarning("Report queries with objects are not supported on remote session.");
        }

        // Retrieve values (employee id, project name) for the main query, let's use John Way
        ExpressionBuilder newBuilder = new ExpressionBuilder();
        Expression newExpression = newBuilder.get("firstName").equal("John").and(newBuilder.get("lastName").equal("Way"));
        Employee emp = (Employee) getSession().readObject(Employee.class, newExpression);
        Project aProject = (Project) emp.getProjects().get(0);
        employeeId = emp.getId();
        projectName = aProject.getName();

        // ReportQuery - is the employee with this id on the project with this name?
        ExpressionBuilder builder = new ExpressionBuilder();
        ReportQuery reportQuery = new ReportQuery(Employee.class, builder);
        reportQuery.addArgument("employeeId");
        reportQuery.addArgument("projectName");

        Expression employeeExpression = builder.get("id").equal(builder.getParameter("employeeId"));

        // inner query
        ExpressionBuilder builder2 = new ExpressionBuilder();
        ReportQuery query2 = new ReportQuery(Employee.class, builder2);

        Expression projects = builder.anyOf("projects").get("name").equal(builder2.getParameter("projectName"));

        query2.addAttribute("EmployeeIdentifier", builder2.get("id"));
        query2.setSelectionCriteria(projects);
        // end of inner query

        Map<Expression, Object> caseParameters = new HashMap<Expression, Object>();
        caseParameters.put(builder.getParameter("employeeId").in(query2), "true");
        Expression caseExpression = builder.caseStatement(caseParameters, "false");

        reportQuery.setSelectionCriteria(employeeExpression);
        reportQuery.addAttribute("EmployeeOnProject", caseExpression);
        queryToExecute = reportQuery;
    }

    public void test() {
        try {
            List arguments = new ArrayList<Object>();
            arguments.add(employeeId);
            arguments.add(projectName);

            results = (List<ReportQueryResult>)getSession().executeQuery(queryToExecute, arguments);
        } catch (EclipseLinkException ex) {
            this.exception = ex;
        }
    }

    public void verify() {
        if (exception != null) {
            throw new TestErrorException("An exception occurred executing a ReportQuery with a parameterized subquery", exception);
        }
        if (results == null || results.isEmpty()) {
            throw new TestErrorException("Unexpected error - no ReportQuery results returned");
        }
        ReportQueryResult reportResult = results.get(0);
        if (!"true".equals(reportResult.get("EmployeeOnProject"))) {
            throw new TestErrorException("Unexpected error - incorrect results returned: " + reportResult);
        }
    }

}
