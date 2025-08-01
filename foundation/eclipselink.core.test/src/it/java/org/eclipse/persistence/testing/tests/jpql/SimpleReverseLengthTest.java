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

import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

public class SimpleReverseLengthTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    @Override
    public void setup() {
        if ((getSession().getLogin().getPlatform().isSQLServer())) {
            throw new TestWarningException("This test is not supported on SQL Server. Because 'LENGTH' is not a recognized function name on SQL Server.");
        }

        Employee emp = (Employee)getSomeEmployees().get(0);

        String ejbqlString;
        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + emp.getFirstName().length();
        ejbqlString = ejbqlString + " = LENGTH(emp.firstName)";

        setEjbqlString(ejbqlString);
        setOriginalOject(emp);
        super.setup();
    }
}
