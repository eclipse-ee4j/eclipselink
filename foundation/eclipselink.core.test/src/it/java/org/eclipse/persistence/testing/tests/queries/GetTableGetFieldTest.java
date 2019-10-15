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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import java.util.*;

/**
 * Test using Expression getTable("x").getField("y") when getField
 * is not a fully-qualified field (tablename.fieldname)
 *
 * Addresses CR3791
 */
public class GetTableGetFieldTest extends TestCase {
    protected List employees;// john way

    public GetTableGetFieldTest() {
        super();
        setDescription("Test using Expression getTable(\"x\").getField(\"y\")");
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder builder = query.getExpressionBuilder();

        // Employees whose address is in Ottawa and their last name is Way (more important)
        Expression subExp = builder.get("address").get("city").equal("Ottawa").and(builder.getTable("EMPLOYEE").getField("L_NAME").equal("Way"));
        query.setSelectionCriteria(subExp);
        query.useDistinct();

        List emps = null;
        try {
            emps = (List)getSession().executeQuery(query);
        } catch (NullPointerException e) {
            e.printStackTrace();
            emps = null;// not needed, but explicit
        }
        setEmployees(emps);
    }

    public void verify() {
        if ((getEmployees() == null) || getEmployees().isEmpty()) {
            throw new TestErrorException("Failure. Employees read in is null / empty");
        } else {
            if (getEmployees().size() != 1) {
                throw new TestWarningException("Employee data on database is not consistent");
            }
            Employee employee = (Employee)getEmployees().get(0);
            if (!employee.getFirstName().equals("John") && !employee.getLastName().equals("Way")) {
                throw new TestWarningException("Expected employee John Way was not selected");
            }
        }
    }

    public void reset() {
        setEmployees(null);
    }

    public List getEmployees() {
        return employees;
    }

    public void setEmployees(List employees) {
        this.employees = employees;
    }
}
