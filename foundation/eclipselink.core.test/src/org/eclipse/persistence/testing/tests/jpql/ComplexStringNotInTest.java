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
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

public class ComplexStringNotInTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        Employee emp1;
        Employee emp2;
        Employee emp3;
        emp1 = (Employee)getSomeEmployees().firstElement();
        emp2 = (Employee)getSomeEmployees().elementAt(1);
        emp3 = (Employee)getSomeEmployees().elementAt(2);

        ExpressionBuilder builder = new ExpressionBuilder();

        Vector nameVector = new Vector();
        nameVector.add(emp1.getFirstName());
        nameVector.add(emp2.getFirstName());
        nameVector.add(emp3.getFirstName());

        Expression whereClause = builder.get("firstName").notIn(nameVector);
        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        setOriginalOject(getSession().executeQuery(raq));
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.firstName NOT IN (";
        ejbqlString = ejbqlString + "\"" + emp1.getFirstName() + "\"" + ", ";
        ejbqlString = ejbqlString + "\"" + emp2.getFirstName() + "\"" + ", ";
        ejbqlString = ejbqlString + "\"" + emp3.getFirstName() + "\"";
        ejbqlString = ejbqlString + ")";

        setEjbqlString(ejbqlString);

        super.setup();

    }
}
