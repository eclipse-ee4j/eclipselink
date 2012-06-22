/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import java.util.*;

public class SimpleSelectPhoneNumberNumberWithEmployeeWithFirstNameFirst extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        Employee emp = (Employee)getSomeEmployees().firstElement();

        PhoneNumber phone = (PhoneNumber)emp.getPhoneNumbers().firstElement();
        String areaCode = phone.getAreaCode();
        String firstName = emp.getFirstName();

        setReferenceClass(Employee.class);

        ExpressionBuilder employeeBuilder = new ExpressionBuilder();
        Expression phones = employeeBuilder.anyOf("phoneNumbers");
        Expression whereClause = phones.get("owner").get("firstName").equal(firstName).and(phones.get("areaCode").equal(areaCode));

        ReportQuery rq = new ReportQuery();
        rq.setSelectionCriteria(whereClause);
        rq.addAttribute("number", phones.get("number"));
        rq.setReferenceClass(Employee.class);

        setOriginalOject(getAttributeFromAll("number", (Vector)getSession().executeQuery(rq)));
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        String ejbqlString;
        ejbqlString = "SELECT phone.number FROM Employee employee, IN(employee.phoneNumbers) phone " + "WHERE phone.owner.firstName = \"" + firstName + "\" AND phone.areaCode = \"" + areaCode + "\"";

        useReportQuery();
        setEjbqlString(ejbqlString);
        super.setup();
    }
}
