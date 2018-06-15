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

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class SimpleReverseLengthTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        if ((getSession().getLogin().getPlatform().isSQLServer())) {
            throw new TestWarningException("This test is not supported on SQL Server. Because 'LENGTH' is not a recognized function name on SQL Server.");
        }

        Employee emp = (Employee)getSomeEmployees().firstElement();

        String ejbqlString;
        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + emp.getFirstName().length();
        ejbqlString = ejbqlString + " = LENGTH(emp.firstName)";

        setEjbqlString(ejbqlString);
        setOriginalOject(emp);
        super.setup();
    }
}
