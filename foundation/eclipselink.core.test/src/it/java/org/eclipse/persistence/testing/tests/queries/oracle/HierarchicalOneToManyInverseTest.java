/*
 * Copyright (c) 2014, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.queries.oracle;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.models.mapping.Employee;

import java.util.Vector;

import static org.eclipse.persistence.queries.ReadAllQuery.Direction.CHILD_TO_PARENT;

public class HierarchicalOneToManyInverseTest extends HierarchicalQueryTest {

    @Override
    public Vector expectedResults() {
        Vector<Employee> v = new Vector<>();
        Employee norman = (Employee) getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").equal("Norman"));
        v.add(norman);
        Employee manager = norman.getManager();
        while (manager != null) {
            v.add(manager);
            manager = manager.getManager();
        }
        return v;
    }

    @Override
    public ReadAllQuery getQuery() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression startWith = builder.get("firstName").equal("Norman");
        Expression connectBy = builder.get("managedEmployees");
        // Default direction for collections is PARENT_TO_CHILD
        query.setHierarchicalQueryClause(startWith, connectBy, null, CHILD_TO_PARENT);
        return query;
    }

}
