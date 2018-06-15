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

public class SqrtTestCase extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    Vector testEmployees;

    public Vector getExtraEmployees() {
        Employee e1 = new Employee();
        Employee e2 = new Employee();

        Vector employees = new Vector();
        employees.addElement(e1);
        employees.addElement(e2);

        e1.setFirstName("E1F");
        e1.setLastName("E1L");
        e1.setSalary(36);
        e1.setFemale();

        e2.setFirstName("E2F");
        e2.setLastName("E2L");
        e2.setSalary(49);
        e2.setFemale();

        getDatabaseSession().writeObject(e1);
        getDatabaseSession().writeObject(e2);

        return employees;
    }

    public Vector getTestEmployees() {
        return testEmployees;
    }

    public void reset() {
        getDatabaseSession().deleteAllObjects(getTestEmployees());
    }

    public void setTestEmployees(Vector theTestEmployees) {
        testEmployees = theTestEmployees;
    }
}
