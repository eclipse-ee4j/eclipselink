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

import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Vector;

/**
 * Tests Simple OR clause
 */
class SelectSimpleDoubleOrTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    @Override
    public void setup() {
        Employee emp1;
        Employee emp2;
        Employee emp3;
        emp1 = (Employee)getSomeEmployees().get(0);
        emp2 = (Employee)getSomeEmployees().get(1);
        emp3 = (Employee)getSomeEmployees().get(2);

        Vector employeesUsed = new Vector();
        employeesUsed.add(emp1);
        employeesUsed.add(emp2);
        employeesUsed.add(emp3);

        //String partialFirstName = emp.getFirstName().substring(0, 3) + "%";
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.id = " + emp1.id + " OR emp.id = " + emp2.id + " OR emp.id = " + emp3.id;

        //testEJBQL("FROM EmployeeBean employee WHERE employee.id = 456 OR employee.id = 756 ", false);
        setEjbqlString(ejbqlString);
        setOriginalOject(employeesUsed);

        super.setup();
    }
}

//    testEJBQL("FROM EmployeeBean employee WHERE employee.id = 456 OR employee.id = 756 OR employee.id = 956");
