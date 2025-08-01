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

public class SimpleReverseSubstringTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    protected final static int MIN_FIRSTNAME_LENGTH = 2;

    @Override
    public void setup() {
        // Bug 223005: Verify that we have at least 1 employee with the required field length otherwise an EclipseLinkException will be thrown
        Employee emp = getEmployeeWithRequiredNameLength(MIN_FIRSTNAME_LENGTH, getName());

        String firstNamePart;
        String ejbqlString;

           firstNamePart = emp.getFirstName().substring(0, 2);
        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "\"" + firstNamePart + "\"";
        ejbqlString = ejbqlString + " = SUBSTRING(emp.firstName, 1, 2)";

        setEjbqlString(ejbqlString);
        setOriginalOject(emp);

        super.setup();
    }
}
