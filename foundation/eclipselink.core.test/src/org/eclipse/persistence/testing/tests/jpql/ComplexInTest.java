/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpql;

import java.util.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

public class ComplexInTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        Employee emp1;
        Employee emp2;
        Employee emp3;
        emp1 = (Employee)getSomeEmployees().firstElement();
        emp2 = (Employee)getSomeEmployees().elementAt(1);
        emp3 = (Employee)getSomeEmployees().elementAt(2);

        Vector employees = new Vector();
        Vector idVector = new Vector();
        idVector.add(emp1.getId());
        idVector.add(emp2.getId());
        idVector.add(emp3.getId());

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        ExpressionBuilder eb = new ExpressionBuilder();
        Expression whereClause = eb.get("id").in(idVector);
        raq.setSelectionCriteria(whereClause);
        employees = (Vector)getSession().executeQuery(raq);

        String ejbqlString = "SELECT OBJECT(e) FROM Employee e WHERE e.id IN (";
        ejbqlString = ejbqlString + emp1.getId().toString() + ", ";
        ejbqlString = ejbqlString + emp2.getId().toString() + ", ";
        ejbqlString = ejbqlString + emp3.getId().toString();
        ejbqlString = ejbqlString + ")";

        setEjbqlString(ejbqlString);
        setOriginalOject(employees);

        super.setup();
    }
}
