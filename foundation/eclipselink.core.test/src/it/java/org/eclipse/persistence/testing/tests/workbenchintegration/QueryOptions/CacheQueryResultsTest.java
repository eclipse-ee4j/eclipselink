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
