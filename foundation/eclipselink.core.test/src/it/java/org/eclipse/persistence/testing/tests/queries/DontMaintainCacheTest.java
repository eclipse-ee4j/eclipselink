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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

public class DontMaintainCacheTest extends TestCase {
    protected Employee employeeFromDatabase;
    protected String firstName;

    public DontMaintainCacheTest() {
        super();
    }

    @Override
    public void reset() {
        // Because the name of the employee was changed, clear the cache.
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    @Override
    protected void setup() {
        employeeFromDatabase = (Employee)getSession().readObject(Employee.class);
    }

    @Override
    public void test() {
        firstName = employeeFromDatabase.getFirstName();
        employeeFromDatabase.setFirstName("Yvon");

    }

    @Override
    protected void verify() {
        Employee employee;
        ReadObjectQuery query = new ReadObjectQuery();

        query.setSelectionObject(employeeFromDatabase);
        query.dontMaintainCache();

        employee = (Employee)getSession().executeQuery(query);

        if (!employee.getFirstName().equals(firstName)) {
            throw new TestErrorException("The dontMaintainCache test failed");
        }
    }
}
