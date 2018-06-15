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
package org.eclipse.persistence.testing.tests.optimization.queryandsqlcounting.querycache;

import java.util.*;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;

public class ConformingQueryCacheTest extends UnitOfWorkQueryCacheTest {
    public ConformingQueryCacheTest() {
        setDescription("Ensure results can be conformed when a cached query is run in a UnitOfWork.");
    }

    public ReadQuery getQueryForTest() {
        ReadQuery query = super.getQueryForTest();
        ((ObjectLevelReadQuery)query).conformResultsInUnitOfWork();
        return query;
    }

    public void test() {
        super.test();
        Employee emp = (Employee)((Vector)results).firstElement();
        emp.setFirstName("Modified");
        Employee newEmp = new Employee();
        newEmp.setFirstName("Brooks");
        newEmp.setLastName("Hatlen");
        ((UnitOfWork)getSessionForQueryTest()).registerObject(newEmp);
        results = getSessionForQueryTest().executeQuery(NamedQueryQueryCacheTest.CACHING_QUERY_NAME);
    }

    public void verify() {
        super.verify();
        Iterator employees = ((Vector)results).iterator();
        while (employees.hasNext()) {
            Employee emp = (Employee)employees.next();
            if (!emp.getFirstName().startsWith("B")) {
                throw new TestErrorException("Employee returned from cached query results does not conform.");
            }
        }
    }
}
