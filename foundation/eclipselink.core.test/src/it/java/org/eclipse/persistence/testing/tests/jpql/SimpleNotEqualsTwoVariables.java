/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Vector;

/**
 * Tests Simple OR clause
 */
class SimpleNotEqualsTwoVariables extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    @Override
    public void setup() {
        Vector employees = getSomeEmployees();
        Employee firstEmployee = (Employee)getSomeEmployees().get(0);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("id").notEqual(builder.get("manager").get("id"));

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);

        setOriginalOject(getSession().executeQuery(raq));
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE emp.id <> emp.manager.id";
        setEjbqlString(ejbqlString);

        super.setup();
    }
}
