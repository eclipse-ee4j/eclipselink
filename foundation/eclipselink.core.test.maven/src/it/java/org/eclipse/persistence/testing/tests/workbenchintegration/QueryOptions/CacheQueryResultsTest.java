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
package org.eclipse.persistence.testing.tests.workbenchintegration.QueryOptions;

import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class CacheQueryResultsTest extends AutoVerifyTestCase {
    protected Employee employeeFromDatabase;
    protected String firstName;
    protected ReadObjectQuery query;
    protected Employee employee;

    public CacheQueryResultsTest() {
        setDescription("Test cache query results option");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() {
        query =
                (ReadObjectQuery)getSession().getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class).getQueryManager().getQuery("cacheQueryResultsQuery");
        employeeFromDatabase = (Employee)getSession().executeQuery(query);
    }

    public void test() {
        employeeFromDatabase.setFirstName("Yvon");
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        employee = (Employee)getSession().executeQuery(query);
    }

    protected void verify() {
        if (!(employee.getFirstName().equals(employeeFromDatabase.getFirstName()))) {
            throw new TestErrorException("The cache query results test has failed");
        }
    }
}
