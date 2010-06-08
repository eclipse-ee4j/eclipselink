/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

public class SelectComplexLikeTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
	protected final static int MIN_FIRSTNAME_LENGTH = 4;
	
	public void setup() {
        // Bug 223005: Verify that we have at least 1 employee with the required field length otherwise an EclipseLinkException will be thrown
        Employee emp = getEmployeeWithRequiredNameLength(MIN_FIRSTNAME_LENGTH, getName());
        String firstName = emp.getFirstName();
    	StringBuffer partialFirstName = new StringBuffer();
    	partialFirstName.append(firstName.substring(0, 1));
    	partialFirstName.append("_");
    	partialFirstName.append(firstName.substring(2, 4));
    	partialFirstName.append( "%");
    	String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName LIKE \"" + partialFirstName.toString() + "\"";
    	setEjbqlString(ejbqlString);
    	setOriginalOject(emp);
    	super.setup();
	}
}
