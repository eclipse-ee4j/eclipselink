/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Vector;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class SelectSimpleMemberOfWithParameterTest extends JPQLParameterTestCase {
    public SelectSimpleMemberOfWithParameterTest() {
    }

    public void setup() {
        Employee employee = (Employee)getSession().readObject(Employee.class);

        PhoneNumber phone = new PhoneNumber();
        phone.setAreaCode("613");
        phone.setNumber("1234567");
        phone.setOwner(employee);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp " + "WHERE ?1 MEMBER OF emp.phoneNumbers";

        setEjbqlString(ejbqlString);

        Vector parameters = new Vector();
        parameters.add(phone);

        setArguments(parameters);

        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        setArgumentNames(myArgumentNames);

        super.setup();
    }

    public void verify() {
    }
}
