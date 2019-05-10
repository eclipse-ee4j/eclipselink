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

import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionMath;
import org.eclipse.persistence.testing.framework.*;

public class ModTest extends JPQLTestCase {
    private static ModTest getNewTestCaseNamed(String name, String ejbql, Class referenceClass) {
        ModTest test = new ModTest();

        test.setName(name);
        test.setEjbqlString(ejbql);
        test.setReferenceClass(referenceClass);

        return test;
    }

    public static ModTest getSimpleModTest() {
        String ejbql = "SELECT OBJECT(e) FROM Employee e WHERE MOD(e.salary, 2) > 0";
        ModTest test = getNewTestCaseNamed("Mod Test", ejbql, Employee.class);

        ExpressionBuilder employee = new ExpressionBuilder();
        Expression whereClause = ExpressionMath.mod(employee.get("salary"), 2).greaterThan(0);

        test.setOriginalObjectExpression(whereClause);

        return test;
    }

    public static ModTest getComplexModTest() {
        ModTest test = getSimpleModTest();

        test.setName("Complex " + test.getName());
        test.setEjbqlString(test.getEjbqlString() + " AND e.firstName <> \"XCV\"");

        return test;
    }

    public void setup() {
        if (getSession().getLogin().getPlatform().isSQLServer() || getSession().getLogin().getPlatform().isSybase()) {
            throw new TestWarningException("This test is not supported on SQL Server and Sybase. Because 'MOD' is not a recognized function name on SQL Server and Sybase.");
        }

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(getReferenceClass());
        raq.setSelectionCriteria(getOriginalObjectExpression());

        setOriginalOject(getSession().executeQuery(raq));

        super.setup();
    }
}
