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
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class SelectComplexParameterTest extends org.eclipse.persistence.testing.tests.jpql.JPQLParameterTestCase {
    public void setup() {
        // Get the baseline employees for the verify
        Employee emp = (Employee)getSomeEmployees().firstElement();

        String firstName = "firstName";
        String lastName = "lastName";

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get(firstName).equal(builder.getParameter(firstName));
        whereClause = whereClause.and(builder.get(lastName).equal(builder.getParameter(lastName)));

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);
        raq.addArgument(firstName);
        raq.addArgument(lastName);

        Vector parameters = new Vector();
        parameters.add(emp.getFirstName());
        parameters.add(emp.getLastName());

        Vector employees = (Vector)getSession().executeQuery(raq, parameters);

        emp = (Employee)employees.firstElement();

        // Set up the EJBQL using the retrieved employees
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "emp.firstName = ?1 ";
        ejbqlString = ejbqlString + " AND ";
        ejbqlString = ejbqlString + "emp.lastName = ?2";

        setEjbqlString(ejbqlString);
        setOriginalOject(employees);

        setArguments(parameters);

        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        myArgumentNames.add("2");
        setArgumentNames(myArgumentNames);

        // Finish the setup
        super.setup();
    }
}
