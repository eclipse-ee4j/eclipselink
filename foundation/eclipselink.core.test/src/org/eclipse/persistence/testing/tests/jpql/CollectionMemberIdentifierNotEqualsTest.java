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

class CollectionMemberIdentifierNotEqualsTest extends JPQLParameterTestCase {
    public void setup() {
        Employee emp;
        Vector employees = getSomeEmployees();
        emp = (Employee)employees.firstElement();
        PhoneNumber phoneNumber = (PhoneNumber)emp.getPhoneNumbers().elementAt(0);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp, IN (emp.phoneNumbers) phone " + "WHERE phone <> ?1";

        setEjbqlString(ejbqlString);

        // The query selects all the employees that have at least one phone that differs from the specified one:
        // that means the Employee who owns the specified phone will not be returned only in case
        // the specified phone is the only phone he has.
        if(emp.getPhoneNumbers().size() == 1) {
            employees.removeElementAt(0);
        }
        setOriginalOject(employees);

        Vector parameters = new Vector();
        parameters.add(phoneNumber);
        setArguments(parameters);

        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        setArgumentNames(myArgumentNames);

        super.setup();
    }
}
