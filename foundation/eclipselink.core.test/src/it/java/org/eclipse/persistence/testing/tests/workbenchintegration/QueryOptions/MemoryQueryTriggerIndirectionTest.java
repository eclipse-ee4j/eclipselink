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

import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


public class MemoryQueryTriggerIndirectionTest extends AutoVerifyTestCase {
    protected ReadAllQuery queryAll;
    protected java.util.Vector allEmployees;
    protected java.util.Vector inMemoryResult;

    public MemoryQueryTriggerIndirectionTest() {
        setDescription("Test memory query trigger indirection option");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        allEmployees =
                (Vector)getSession().executeQuery("memoryQueryTriggerIndirectionQuery", org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        queryAll =
                (ReadAllQuery)getSession().getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class).getQueryManager().getQuery("memoryQueryTriggerIndirectionQuery");
    }

    public void test() {
        ReadAllQuery queryAllCopy = (ReadAllQuery)queryAll.clone();
        queryAllCopy.checkCacheOnly(); //read from cache only
        queryAllCopy.setSelectionCriteria(new ExpressionBuilder().get("address").get("city").notEqual("Montreal"));
        inMemoryResult = (Vector)getSession().executeQuery(queryAllCopy);
    }

    public void verify() {
        if (inMemoryResult.size() != (allEmployees.size() - 1)) {
            throw new TestErrorException("In Memory Query did not return all objects.  Auto-indirection triggering is not working");
        }
    }
}
