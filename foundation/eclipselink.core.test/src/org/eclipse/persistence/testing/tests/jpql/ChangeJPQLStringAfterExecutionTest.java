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


// Java imports
import java.util.*;

// Domain imports
import org.eclipse.persistence.testing.models.employee.domain.*;

//Bug 3175011: Verify that if we change the EJBQLString after execution, it will be
//re-parsed. Verify this by matching the expected results with the second execution
public class ChangeJPQLStringAfterExecutionTest extends JPQLTestCase {
    String firstEJBQLString = null;
    String secondEJBQLString = null;

    public void setup() {
        Vector employees = getSomeEmployees();

        Employee emp = (Employee)employees.firstElement();
        setOriginalOject(emp);

        //Define the firstEJBQLString, which will NOT match the results expected, and the
        //secondEJBQLString which will match.
        firstEJBQLString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName = ";
        firstEJBQLString = firstEJBQLString + "\"WRONG\"";
        secondEJBQLString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName = ";
        secondEJBQLString = secondEJBQLString + "\"" + emp.getFirstName() + "\"";

        //start with the firstEJBQLString
        setEjbqlString(firstEJBQLString);
        super.setup();
    }

    public void test() throws Exception {
        super.test();

        //change the EJBQL to the second (the one the matches the results), and re-run
        getQuery().setEJBQLString(secondEJBQLString);

        super.test();
    }
}
