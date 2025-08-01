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
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;

import java.util.Vector;

public class SimpleSelectPhoneNumberNumberWithEmployeeWithExplicitJoin extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    @Override
    public void setup() {
        Employee emp = (Employee)getSomeEmployees().get(0);

        PhoneNumber phone = (PhoneNumber)emp.getPhoneNumbers().get(0);
        String areaCode = phone.getAreaCode();
        String firstName = emp.getFirstName();

        setReferenceClass(Employee.class);

        ExpressionBuilder employeeBuilder = new ExpressionBuilder(Employee.class);
        Expression phones = employeeBuilder.anyOf("phoneNumbers");
        Expression whereClause = phones.get("areaCode").equal(areaCode).and(phones.get("owner").get("id").equal(employeeBuilder.get("id")).and(employeeBuilder.get("firstName").equal(firstName)));

        ReportQuery rq = new ReportQuery();
        rq.addAttribute("number", new ExpressionBuilder().anyOf("phoneNumbers").get("number"));
        rq.setSelectionCriteria(whereClause);
        rq.setReferenceClass(Employee.class);

        setOriginalOject(getAttributeFromAll("number", (Vector)getSession().executeQuery(rq)));
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        String ejbqlString;
        ejbqlString = "SELECT phone.number FROM Employee employee, IN (employee.phoneNumbers) phone " + "WHERE phone.areaCode = \"" + areaCode + "\" AND (phone.owner.id = employee.id AND employee.firstName = \"" + firstName + "\")";

        useReportQuery();
        setEjbqlString(ejbqlString);
        super.setup();
    }
}
