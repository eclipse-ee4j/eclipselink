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

import java.math.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

/**
 * Tests Simple OR clause
 */
class SelectSimpleBetweenAndTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        BigDecimal emp1 = new BigDecimal(0);
        Employee emp2;
        emp2 = (Employee)getSomeEmployees().lastElement();

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("id").between(emp1, emp2.getId());
        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);
        setOriginalOject(getSession().executeQuery(raq));

        String ejbqlString = "SELECT OBJECT(e) FROM Employee e WHERE e.id BETWEEN " + emp1 + " AND " + emp2.id;
        setEjbqlString(ejbqlString);

        super.setup();
    }
}
