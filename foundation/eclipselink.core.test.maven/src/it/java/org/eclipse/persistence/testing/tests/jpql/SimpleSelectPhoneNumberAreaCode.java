/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import java.util.*;

public class SimpleSelectPhoneNumberAreaCode extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        Employee emp = (Employee)getSomeEmployees().firstElement();

        setReferenceClass(Employee.class);

        ExpressionBuilder employeeBuilder = new ExpressionBuilder();
        Expression phones = employeeBuilder.anyOf("phoneNumbers");
        Expression whereClause = phones.get("areaCode").equal("613");

        ReportQuery rq = new ReportQuery();
        rq.setSelectionCriteria(whereClause);
        rq.addAttribute("areaCode", new ExpressionBuilder().anyOf("phoneNumbers").get("areaCode"));
        //rq.dontMaintainCache();
        rq.setReferenceClass(Employee.class);
        //rq.dontUseDistinct();
        setOriginalOject(getAttributeFromAll("areaCode", (Vector)getSession().executeQuery(rq)));
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        String ejbqlString;
        ejbqlString = "SELECT phone.areaCode FROM Employee employee, IN (employee.phoneNumbers) phone " + "WHERE phone.areaCode = \"613\"";

        setEjbqlString(ejbqlString);

        useReportQuery();
        super.setup();
    }
}
