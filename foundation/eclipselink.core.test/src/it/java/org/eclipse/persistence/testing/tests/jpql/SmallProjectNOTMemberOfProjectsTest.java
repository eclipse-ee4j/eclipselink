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


// Java imports

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;

import java.util.Vector;

public class SmallProjectNOTMemberOfProjectsTest extends JPQLTestCase {
    @Override
    public void setup() {
        String ejbqlString = null;

        //query for those employees with Project named "Enterprise" (which should be
        //a SmallProject)
        ReadObjectQuery smallProjectQuery = new ReadObjectQuery();
        smallProjectQuery.setReferenceClass(SmallProject.class);
        smallProjectQuery.setSelectionCriteria(new ExpressionBuilder().get("name").equal("Enterprise"));
        SmallProject smallProject = (SmallProject)getSession().executeQuery(smallProjectQuery);

        ReadAllQuery query = new ReadAllQuery();
        query.addArgument("smallProject");
        Expression selectionCriteria = new ExpressionBuilder().noneOf("projects", new ExpressionBuilder().equal(new ExpressionBuilder().getParameter("smallProject")));

        query.setSelectionCriteria(selectionCriteria);
        query.setReferenceClass(Employee.class);

        Vector arguments = new Vector();
        arguments.add(smallProject);
        Vector employeesWithoutProjectEnterprise = (Vector)getSession().executeQuery(query, arguments);
        setOriginalOject(employeesWithoutProjectEnterprise);

        //setup the EJBQL to do the same
        ejbqlString = "SELECT OBJECT(employee) FROM Employee employee WHERE ";
        ejbqlString = ejbqlString + "?1 NOT MEMBER OF employee.projects";
        setEjbqlString(ejbqlString);
        setArguments(arguments);
        super.setup();
    }
}
