/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.queries.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class QBESpecialOperators extends TestCase {

    public Employee employee;
    public QueryByExamplePolicy policy;

    public QBESpecialOperators() {
        this.setDescription("To test the Query By Example Policy by using specail operators for comparison.");
    }

    public void setup() {

        //This method tests the specail operations:
        employee = new Employee();
        policy = new QueryByExamplePolicy();
        employee.setFirstName("J__l");
        employee.setLastName("M%");
        employee.setSalary(60000);

        policy.addSpecialOperation(Integer.class, "lessThan");
        policy.addSpecialOperation(String.class, "like");

    }

    public void test() {

        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(Employee.class);
        query.setExampleObject(employee);
        query.setQueryByExamplePolicy(policy);
        Employee emp = new Employee();
        emp = (Employee)getSession().executeQuery(query);
        if (!(emp.getFirstName().charAt(0) == 'J') && (emp.getFirstName().charAt(3) == 'l') &&
            (emp.getLastName().charAt(0) == 'M')) {
            throw (new TestErrorException("Error in using specail operators."));
        }
        if (!(emp.getSalary() < 60000)) {
            throw (new TestErrorException("Error is using spcial operators."));
        }
    }
}
