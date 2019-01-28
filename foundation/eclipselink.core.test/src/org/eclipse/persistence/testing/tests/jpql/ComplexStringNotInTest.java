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

        String ejbqlString = "SELECT OBJECT(e) FROM Employee e WHERE e.firstName NOT IN (";
        ejbqlString = ejbqlString + "\"" + emp1.getFirstName() + "\"" + ", ";
        ejbqlString = ejbqlString + "\"" + emp2.getFirstName() + "\"" + ", ";
        ejbqlString = ejbqlString + "\"" + emp3.getFirstName() + "\"";
        ejbqlString = ejbqlString + ")";

        setEjbqlString(ejbqlString);

        super.setup();

    }
}
