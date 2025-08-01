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
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Vector;

public class SimpleParameterTest extends JPQLParameterTestCase {
    @Override
    public void setup() {
        // Get the baseline employees for the verify
        Employee emp = (Employee)getSomeEmployees().get(0);

        String parameterName = "firstName";
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("firstName").equal(builder.getParameter(parameterName));

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);
        raq.addArgument(parameterName);

        Vector parameters = new Vector();
        parameters.add(emp.getFirstName());

        Vector employees = (Vector)getSession().executeQuery(raq, parameters);

        // Set up the EJBQL using the retrieved employees
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "emp.firstName = ?1 ";

        setEjbqlString(ejbqlString);
        setOriginalOject(employees);

        setArguments(parameters);

        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        setArgumentNames(myArgumentNames);

        // Finish the setup
        super.setup();
    }
}
