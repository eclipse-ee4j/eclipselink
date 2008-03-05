/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

public class SelectSimpleReverseConcatTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        Employee emp = (Employee)getSomeEmployees().firstElement();

        String partOne;
        String partTwo;
        String ejbqlString;

        partOne = emp.getFirstName().substring(0, 2);
        partTwo = emp.getFirstName().substring(2);

        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "CONCAT(\"";
        ejbqlString = ejbqlString + partOne;
        ejbqlString = ejbqlString + "\", \"";
        ejbqlString = ejbqlString + partTwo;
        ejbqlString = ejbqlString + "\")";
        ejbqlString = ejbqlString + " = emp.firstName";

        setEjbqlString(ejbqlString);
        setOriginalOject(emp);

        super.setup();
    }
}