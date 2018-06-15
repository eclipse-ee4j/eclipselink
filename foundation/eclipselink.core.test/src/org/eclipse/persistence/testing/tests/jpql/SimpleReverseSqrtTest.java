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

public class SimpleReverseSqrtTest extends org.eclipse.persistence.testing.tests.jpql.SqrtTestCase {
    public void setup() {
        setTestEmployees(getExtraEmployees());
        Employee emp = (Employee)getTestEmployees().firstElement();

        double salarySquareRoot = Math.sqrt((new Double(emp.getSalary()).doubleValue()));
        String ejbqlString;

        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + salarySquareRoot;
        ejbqlString = ejbqlString + " = SQRT(emp.salary)";
        setEjbqlString(ejbqlString);
        setOriginalOject(emp);
        super.setup();
    }
}
