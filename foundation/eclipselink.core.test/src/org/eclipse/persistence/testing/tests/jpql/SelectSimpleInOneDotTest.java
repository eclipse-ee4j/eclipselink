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

import org.eclipse.persistence.testing.models.employee.domain.*;

class SelectSimpleInOneDotTest extends JPQLTestCase {
    public void setup() {
        Employee emp;
        emp = (Employee)getSomeEmployees().firstElement();
        PhoneNumber empPhoneNumbers = (PhoneNumber)emp.getPhoneNumbers().elementAt(0);

        String ejbqlString = "SelecT OBJECT(emp) from Employee emp, in (emp.phoneNumbers) phone " + "Where phone.areaCode = \"" + empPhoneNumbers.getAreaCode() + "\"";

        setEjbqlString(ejbqlString);
        setOriginalOject(emp);

        super.setup();
    }
}
