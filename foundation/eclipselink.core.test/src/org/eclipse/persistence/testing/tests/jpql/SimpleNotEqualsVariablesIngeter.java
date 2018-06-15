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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpql;

import java.util.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

class SimpleNotEqualsVariablesIngeter extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        Employee emp1;
        Vector employeesUsed = new Vector();

        employeesUsed = getSomeEmployees();
        emp1 = (Employee)getSomeEmployees().firstElement();
        employeesUsed.remove(emp1);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.id <> " + emp1.id;
        setEjbqlString(ejbqlString);
        setOriginalOject(employeesUsed);
        //setOriginalOject(emp1);
        super.setup();
    }
}
