/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.queries.oracle;

import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.models.mapping.Employee;

import static org.eclipse.persistence.queries.ReadAllQuery.Direction.CHILD_TO_PARENT;

public class HierarchicalOneToManyInverseTest extends HierarchicalQueryTest {

    public Vector expectedResults() {
        Vector<Employee> v = new Vector<>();
        Employee norman = (Employee) getSession().readObject(Employee.class, new ExpressionBuilder().get("firstName").equal("Norman"));
        v.addElement(norman);
        Employee manager = norman.getManager();
        while (manager != null) {
            v.addElement(manager);
            manager = manager.getManager();
        }
        return v;
    }

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
