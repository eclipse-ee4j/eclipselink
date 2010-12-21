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

import java.util.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

class AbstractSchemaIdentifierNotEqualsTest extends JPQLParameterTestCase {
    public void setup() {
        Employee emp;
        Vector employees = getSomeEmployees();
        emp = (Employee)employees.firstElement();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp <> ?1";

        setEjbqlString(ejbqlString);

        employees.removeElementAt(0);
        setOriginalOject(employees);

        Vector parameters = new Vector();
        parameters.add(emp);
        setArguments(parameters);

        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        setArgumentNames(myArgumentNames);

        super.setup();
    }
}
