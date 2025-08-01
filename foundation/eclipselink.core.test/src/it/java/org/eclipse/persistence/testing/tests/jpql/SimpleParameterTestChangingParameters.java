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
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Vector;

public class SimpleParameterTestChangingParameters extends JPQLParameterTestCase {
    Employee first;
    Employee second;
    Vector firstParameters;
    Vector secondParameters;

    @Override
    public void setup() {
        // Get the baseline employees for the verify
        Vector employees = getSomeEmployees();
        first = (Employee)employees.get(0);
        second = (Employee)employees.get(1);

        String parameterName = "firstName";
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("firstName").equal(builder.getParameter(parameterName));

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);
        raq.addArgument(parameterName);

        firstParameters = new Vector();
        firstParameters.add(first.getFirstName());
        secondParameters = new Vector();
        secondParameters.add(second.getFirstName());

        Vector firstEmployees = (Vector)getSession().executeQuery(raq, firstParameters);
        Vector secondEmployees = (Vector)getSession().executeQuery(raq, secondParameters);
        Vector allEmployees = new Vector();
        allEmployees.addAll(firstEmployees);
        allEmployees.addAll(secondEmployees);

        // Set up the EJBQL using the retrieved employees
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "emp.firstName = ?1 ";

        setEjbqlString(ejbqlString);
        setOriginalOject(allEmployees);

        //        setArguments(parameters);
        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");

        //setArgumentsNames(myArgumentNames);
        // Finish the setup
        super.setup();
    }

    @Override
    public void test() {
        try {
            Vector firstSetOfResults;
            Vector secondSetOfResults;

            //populateQuery();
            getQuery().addArgument("1");
            getSession().logMessage("Running EJBQL -> " + getEjbqlString());
            firstSetOfResults = (Vector)getSession().executeQuery(getQuery(), firstParameters);
            secondSetOfResults = (Vector)getSession().executeQuery(getQuery(), secondParameters);

            Vector allResults = new Vector();
            allResults.addAll(firstSetOfResults);
            allResults.addAll(secondSetOfResults);

            setReturnedObjects(allResults);
        } catch (Exception e) {
            throw new TestErrorException(e.getMessage());
        }
    }
}
