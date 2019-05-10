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
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Tests Simple OR clause
 */
class SimpleOrFollowedByAndTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        Employee emp1;
        Employee emp2;
        Employee emp3;
        emp1 = (Employee)getSomeEmployees().firstElement();
        emp2 = (Employee)getSomeEmployees().elementAt(1);
        emp3 = (Employee)getSomeEmployees().elementAt(2);

        Vector employeesUsed = new Vector();
        employeesUsed.add(emp1);

        //employeesUsed.add(emp2);
        //employeesUsed.add(emp3);
        String ejbqlString = "SELECT OBJECT(e) FROM Employee e WHERE e.id = " + emp1.id + " OR e.id = " + emp2.id + " AND e.id = " + emp3.id;
        setEjbqlString(ejbqlString);
        setOriginalOject(employeesUsed);

        super.setup();
    }
}

//testEJBQL("FROM EmployeeBean employee WHERE employee.id = 456 OR employee.id = 756 AND employee.id = 956", false);
