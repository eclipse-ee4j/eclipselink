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

import java.util.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

public class ComplexLikeTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        Vector employees = new Vector();
        Employee emp;
        emp = (Employee)getSomeEmployees().firstElement();

        String firstName = emp.getFirstName();
        String partialFirstName = emp.getFirstName().substring(0, 1);
        partialFirstName = partialFirstName + "_";
        partialFirstName = partialFirstName + firstName.substring(2, Math.min(4, firstName.length()));
        partialFirstName = partialFirstName + "%";

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        ExpressionBuilder eb = new ExpressionBuilder();
        Expression whereClause = eb.get("firstName").like(partialFirstName);
        raq.setSelectionCriteria(whereClause);
        employees = (Vector)getSession().executeQuery(raq);

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName LIKE \"" + partialFirstName + "\"";

        setEjbqlString(ejbqlString);
        setOriginalOject(employees);

        super.setup();
    }
}