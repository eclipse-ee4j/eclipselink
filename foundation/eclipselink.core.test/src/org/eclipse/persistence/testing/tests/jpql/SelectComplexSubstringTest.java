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

public class SelectComplexSubstringTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    protected final static int MIN_FIRSTNAME_LENGTH = 2;

    public void setup() {
        // Bug 223005: Verify that we have at least 1 employee with the required field length otherwise an EclipseLinkException will be thrown
        Employee emp = getEmployeeWithRequiredNameLength(MIN_FIRSTNAME_LENGTH, getName());

        String firstNamePart;
        String lastNamePart;
        String ejbqlString;

        firstNamePart = emp.getFirstName().substring(0, 2);
        lastNamePart = emp.getLastName().substring(0, 1);
        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "(SUBSTRING(emp.firstName, 1, 2) = ";//changed from 0, 2 to 1, 2(ZYP)
        ejbqlString = ejbqlString + "\"" + firstNamePart + "\")";
        ejbqlString = ejbqlString + " AND ";
        ejbqlString = ejbqlString + "(SUBSTRING(emp.lastName, 1, 1) = ";//changed from 0, 1 to 1, 1(ZYP)
        ejbqlString = ejbqlString + "\"" + lastNamePart + "\")";

        setEjbqlString(ejbqlString);
        setOriginalOject(emp);

        super.setup();
    }
}
