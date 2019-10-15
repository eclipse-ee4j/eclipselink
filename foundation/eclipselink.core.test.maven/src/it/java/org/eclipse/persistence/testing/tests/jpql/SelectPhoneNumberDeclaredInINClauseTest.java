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

// Domain imports
import org.eclipse.persistence.testing.models.employee.domain.*;

//TopLink imports
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

//Tests: "Select Distinct Object(a) from CustomerBean c, IN(c.aliases) a WHERE a.alias
//IS NOT NULL ORDER BY a.alias, a.id"
//
// Variable being SELECTed is declared in an IN clause
public class SelectPhoneNumberDeclaredInINClauseTest extends JPQLTestCase {
    public void setup() {
        String ejbqlString = null;

        //query for those employees with SmallProjects using an expression
        ReadAllQuery query = new ReadAllQuery();
        ExpressionBuilder employeeBuilder = new ExpressionBuilder(Employee.class);
        Expression phoneAnyOf = employeeBuilder.anyOf("phoneNumbers");
        ExpressionBuilder phoneBuilder = new ExpressionBuilder(PhoneNumber.class);
        Expression selectionCriteria = phoneBuilder.equal(employeeBuilder.anyOf("phoneNumbers")).and(phoneAnyOf.get("number").notNull());
        query.setSelectionCriteria(selectionCriteria);
        query.setReferenceClass(PhoneNumber.class);
        query.addAscendingOrdering("number");
        query.addAscendingOrdering("areaCode");

        setOriginalOject(getSession().executeQuery(query));
        useReportQuery();
        setReferenceClass(Employee.class);

        //setup the EJBQL to do the same
        ejbqlString = "Select Distinct Object(p) from Employee emp, IN(emp.phoneNumbers) p WHERE ";
        ejbqlString = ejbqlString + "p.number IS NOT NULL ORDER BY p.number, p.areaCode";
        setEjbqlString(ejbqlString);
        super.setup();
    }
}
