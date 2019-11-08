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
package org.eclipse.persistence.testing.tests.jpql;

import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class SimpleLikeTestWithParameter extends JPQLParameterTestCase {
    protected final static int MIN_FIRSTNAME_LENGTH = 3;

    public void setup() {
        Vector employees = getSomeEmployees();
        // Bug 223005: Verify that we have at least 1 employee with the required field length otherwise an EclipseLinkException will be thrown
        Employee emp = getEmployeeWithRequiredNameLength(employees, MIN_FIRSTNAME_LENGTH, getName());

        String partialFirstName = "%" + emp.getFirstName().substring(0, 3) + "%";

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);

        Vector parameters = new Vector();
        parameters.add(partialFirstName);

        ExpressionBuilder eb = new ExpressionBuilder();
        Expression whereClause = eb.get("firstName").like(partialFirstName);
        raq.setSelectionCriteria(whereClause);
        employees = (Vector)getSession().executeQuery(raq);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName LIKE ?1";

        setEjbqlString(ejbqlString);
        setOriginalOject(employees);
        setArguments(parameters);

        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        setArgumentNames(myArgumentNames);

        super.setup();
    }
}
