/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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

import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class ComplexParameterTest extends org.eclipse.persistence.testing.tests.jpql.JPQLParameterTestCase {
    public void setup() {
        // Get the baseline employees for the verify
        Employee emp = (Employee)getSomeEmployees().firstElement();

        String firstName = "firstName";
        String lastName = "lastName";

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get(firstName).equal(builder.getParameter(firstName));
        whereClause = whereClause.and(builder.get(lastName).equal(builder.getParameter(lastName)));

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);
        raq.addArgument(firstName);
        raq.addArgument(lastName);

        Vector parameters = new Vector();
        parameters.add(emp.getFirstName());
        parameters.add(emp.getLastName());

        Vector employees = (Vector)getSession().executeQuery(raq, parameters);

        emp = (Employee)employees.firstElement();

        // Set up the EJBQL using the retrieved employees
        String ejbqlString = "SELECT OBJECT(e) FROM Employee e WHERE ";
        ejbqlString = ejbqlString + "e.firstName = ?1 ";
        ejbqlString = ejbqlString + " AND ";
        ejbqlString = ejbqlString + "e.lastName = ?2";

        setEjbqlString(ejbqlString);
        setOriginalOject(employees);

        setArguments(parameters);

        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        myArgumentNames.add("2");
        setArgumentNames(myArgumentNames);

        // Finish the setup
        super.setup();
    }
}
