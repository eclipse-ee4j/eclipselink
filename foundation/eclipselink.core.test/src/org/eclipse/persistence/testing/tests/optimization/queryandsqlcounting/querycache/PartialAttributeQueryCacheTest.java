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

/**
 * Ensure partial attribute queries work with query caching
 */
public class PartialAttributeQueryCacheTest extends NamedQueryQueryCacheTest {
    public PartialAttributeQueryCacheTest() {
        setDescription("Ensure queries using partial attributes can use query caching.");
    }

    @SuppressWarnings("deprecation")
    public ReadQuery getQueryForTest() {
        ReadAllQuery testQuery = (ReadAllQuery)super.getQueryForTest();
        testQuery.addPartialAttribute("firstName");
        testQuery.addPartialAttribute("lastName");
        testQuery.dontMaintainCache();
        testQuery.cacheQueryResults();
        return testQuery;
    }

    public void verify() {
        super.verify();
        Iterator employees = ((Vector)results).iterator();
        while (employees.hasNext()) {
            Employee emp = (Employee)employees.next();
            if ((emp.getFirstName() == null) || (emp.getLastName() == null)) {
                throw new TestErrorException("Returned query result was missing data for partial object " + "query with query caching turned on.");
            }
            if (emp.getSalary() > 0) {
                throw new TestErrorException("Additional results were returned in a partial object " + " query with caching turned on.");
            }
        }
    }
}
