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

public class SelectComplexAbsTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        Vector employees = getSomeEmployees();

        Employee emp1 = (Employee)employees.firstElement();
        Employee emp2 = (Employee)employees.lastElement();

        String ejbqlString;

        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "(ABS(emp.salary) = ";
        ejbqlString = ejbqlString + emp1.getSalary() + ")";
        ejbqlString = ejbqlString + " OR (ABS(emp.salary) = ";
        ejbqlString = ejbqlString + emp2.getSalary() + ")";
        setEjbqlString(ejbqlString);
        Vector employeesUsed = new Vector();
        employeesUsed.add(emp1);
        employeesUsed.add(emp2);
        setOriginalOject(employeesUsed);
        super.setup();
    }
}
