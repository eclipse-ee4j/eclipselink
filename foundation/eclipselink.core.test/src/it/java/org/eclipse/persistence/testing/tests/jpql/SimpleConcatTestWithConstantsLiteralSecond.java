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

//This tests CONCAT with the second parameter being a constant String
public class SimpleConcatTestWithConstantsLiteralSecond extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    @Override
    public void setup() {
        Employee emp = (Employee)getSomeEmployees().get(0);

        String partOne;
        String ejbqlString;

        partOne = emp.getFirstName();

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("firstName").concat("Smith").like(partOne + "Smith");

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        Vector employees = (Vector)getSession().executeQuery(raq);

        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "CONCAT(emp.firstName,\"Smith\") LIKE ";
        ejbqlString = ejbqlString + "\"" + partOne + "Smith\"";

        setEjbqlString(ejbqlString);
        setOriginalOject(employees);
        super.setup();
    }
}
