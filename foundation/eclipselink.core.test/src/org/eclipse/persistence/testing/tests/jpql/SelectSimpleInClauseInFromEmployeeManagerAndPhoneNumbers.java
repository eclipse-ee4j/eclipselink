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

import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

class SelectSimpleInClauseInFromEmployeeManagerAndPhoneNumbers extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        setReferenceClass(Project.class);

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);

        ExpressionBuilder employeeBuilder = new ExpressionBuilder(Employee.class);
        Expression whereClause = employeeBuilder.get("manager").anyOf("phoneNumbers").get("areaCode").equal("613");

        raq.setSelectionCriteria(whereClause);
        // Make sure we don't use distinct in the query as we don't have it in the EJBQL
        raq.dontUseDistinct();

        Vector employees;
        employees = (Vector)getSession().executeQuery(raq);

        String ejbqlString = "SELECT OBJECT(employee) FROM Employee employee, IN (employee.manager.phoneNumbers) phone " + "WHERE phone.areaCode = \"613\"";

        setEjbqlString(ejbqlString);
        setOriginalOject(employees);

        super.setup();
    }
}
