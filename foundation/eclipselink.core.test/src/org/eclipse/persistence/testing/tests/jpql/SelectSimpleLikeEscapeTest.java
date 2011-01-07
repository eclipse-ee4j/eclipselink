/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.testing.models.employee.domain.*;

public class SelectSimpleLikeEscapeTest extends JPQLTestCase {
	protected final static int MIN_FIRSTNAME_LENGTH = 3;
	
    public SelectSimpleLikeEscapeTest() {
    }

    public void setup() {
        // Bug 223005: Verify that we have at least 1 employee with the required field length otherwise an EclipseLinkException will be thrown
        Employee emp = getEmployeeWithRequiredNameLength(MIN_FIRSTNAME_LENGTH, getName());

        String partialFirstName = emp.getFirstName().substring(0, 3) + "%";
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp ";
        ejbqlString = ejbqlString + "WHERE emp.firstName LIKE '" + partialFirstName + "' ";
        ejbqlString = ejbqlString + "ESCAPE '/'";

        setEjbqlString(ejbqlString);
        setOriginalOject(emp);

        super.setup();
    }
}
