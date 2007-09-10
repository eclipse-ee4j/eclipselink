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

public class SelectComplexReverseSubstringTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        Employee emp = (Employee)getSomeEmployees().firstElement();

        String firstNamePart;
        String lastNamePart;
        String ejbqlString;

        firstNamePart = emp.getFirstName().substring(0, 2);
        lastNamePart = emp.getLastName().substring(0, 1);
        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "\"" + firstNamePart + "\"";
        ejbqlString = ejbqlString + " = SUBSTRING(emp.firstName, 1, 2)";//changed from 0, 2 to 1, 2(ZYP)
        ejbqlString = ejbqlString + " AND ";
        ejbqlString = ejbqlString + "\"" + lastNamePart + "\"";
        ejbqlString = ejbqlString + " = SUBSTRING(emp.lastName, 1, 1)";//changed from 0, 1 to 1, 1(ZYP)

        setEjbqlString(ejbqlString);
        setOriginalOject(emp);

        super.setup();
    }
}