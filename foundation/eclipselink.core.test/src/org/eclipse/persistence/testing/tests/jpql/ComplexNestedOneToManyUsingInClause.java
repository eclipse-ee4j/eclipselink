/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;

public class ComplexNestedOneToManyUsingInClause extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        Employee emp = (Employee)getSomeEmployees().firstElement();

        //we want a random reference class to make the EJBQL set it properly to Employee
        setReferenceClass(Employee.class);

        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.anyOf("managedEmployees").anyOf("projects").get("name").equal("Enterprise");
        ReadAllQuery readQuery = new ReadAllQuery();
        readQuery.dontMaintainCache();
        readQuery.setReferenceClass(Employee.class);
        readQuery.setSelectionCriteria(whereClause);

        setOriginalOject(getSession().executeQuery(readQuery));
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        String ejbqlString;
        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp, " + "IN(emp.managedEmployees) mEmployees, IN(mEmployees.projects) projects " + "WHERE projects.name = 'Enterprise'";
        setEjbqlString(ejbqlString);
        super.setup();
    }
}
