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

import java.util.*;
import java.math.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class SelectSimpleBetweenWithParametersTest extends JPQLParameterTestCase {
    public void setup() {
        Vector employees = getSomeEmployees();
        BigDecimal empid1 = new BigDecimal(0);
        Employee emp2 = (Employee)employees.lastElement();

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);

        ExpressionBuilder eb = new ExpressionBuilder();
        Expression whereClause = eb.get("id").between(empid1, emp2.getId());

        raq.setSelectionCriteria(whereClause);

        employees = (Vector)getSession().executeQuery(raq);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "emp.id BETWEEN ?1 AND ?2";

        setEjbqlString(ejbqlString);
        setOriginalOject(employees);

        Vector parameters = new Vector();
        parameters.add(empid1);
        parameters.add(emp2.getId());
        setArguments(parameters);

        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        myArgumentNames.add("2");
        setArgumentNames(myArgumentNames);

        super.setup();
    }
}
