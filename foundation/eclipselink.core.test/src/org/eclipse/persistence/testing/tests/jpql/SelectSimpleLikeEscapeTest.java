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

public class SelectSimpleLikeEscapeTest extends JPQLTestCase {
    public SelectSimpleLikeEscapeTest() {
    }

    public void setup() {
        Employee emp;
        emp = (Employee)getSomeEmployees().firstElement();

        String partialFirstName = emp.getFirstName().substring(0, 3) + "%";
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp ";
        ejbqlString = ejbqlString + "WHERE emp.firstName LIKE '" + partialFirstName + "' ";
        ejbqlString = ejbqlString + "ESCAPE '/'";

        setEjbqlString(ejbqlString);
        setOriginalOject(emp);

        super.setup();
    }
}