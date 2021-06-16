/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpql;

import java.util.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Tests Simple OR clause
 */
class SelectSimpleDoubleOrTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        Employee emp1;
        Employee emp2;
        Employee emp3;
        emp1 = (Employee)getSomeEmployees().firstElement();
        emp2 = (Employee)getSomeEmployees().elementAt(1);
        emp3 = (Employee)getSomeEmployees().elementAt(2);

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
