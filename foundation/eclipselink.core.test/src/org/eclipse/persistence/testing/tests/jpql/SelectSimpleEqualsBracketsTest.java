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

public class SelectSimpleEqualsBracketsTest extends JPQLTestCase {
    public void setup() {
        Vector employees = getSomeEmployees();

        String ejbqlString = null;
        Employee emp = (Employee)employees.firstElement();
        setOriginalOject(emp);

        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "( emp.firstName = ";
        ejbqlString = ejbqlString + "\"" + emp.getFirstName() + "\")";

        setEjbqlString(ejbqlString);

        super.setup();
    }
}
