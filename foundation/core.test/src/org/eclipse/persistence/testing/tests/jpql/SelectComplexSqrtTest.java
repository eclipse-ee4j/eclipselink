/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.testing.models.employee.domain.*;

public class SelectComplexSqrtTest extends SqrtTestCase {
    public void setup() {
        setTestEmployees(getExtraEmployees());
        Employee emp1 = (Employee)getTestEmployees().firstElement();
        Employee emp2 = (Employee)getTestEmployees().lastElement();

        String ejbqlString;
        double salarySquareRoot1 = Math.sqrt((new Double(emp1.getSalary()).doubleValue()));
        double salarySquareRoot2 = Math.sqrt((new Double(emp2.getSalary()).doubleValue()));

        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "(SQRT(emp.salary) = ";
        ejbqlString = ejbqlString + salarySquareRoot1 + ")";
        ejbqlString = ejbqlString + " OR ";
        ejbqlString = ejbqlString + "(SQRT(emp.salary) = ";
        ejbqlString = ejbqlString + salarySquareRoot2 + ")";

        setEjbqlString(ejbqlString);
        setOriginalOject(getTestEmployees());
        super.setup();
    }
}