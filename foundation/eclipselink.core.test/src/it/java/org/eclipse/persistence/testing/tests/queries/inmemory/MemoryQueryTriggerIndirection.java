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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Vector;

public class MemoryQueryTriggerIndirection extends TestCase {
    protected Employee employee;
    protected ReadAllQuery queryAll;
    protected ReadObjectQuery queryObject;
    protected java.util.Vector allEmployees;
    protected java.util.Vector inMemoryResult;

    public MemoryQueryTriggerIndirection() {
        super();
    }

    @Override
    public void reset() {
        //clear the cache.
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        allEmployees = new Vector();
        queryAll = new ReadAllQuery();
        queryAll.setReferenceClass(Employee.class);
        queryAll.setSelectionCriteria(new ExpressionBuilder().get("address").get("city").greaterThan("Montreal"));
        allEmployees = (Vector)getSession().executeQuery(queryAll);

    }

    @Override
    public void test() {
        //all the employees with cities that come after Montreal should be
        //in the cache right now.
        this.queryAll.checkCacheOnly();//read from cache only
        this.queryAll.getInMemoryQueryIndirectionPolicy().triggerIndirection();

        this.inMemoryResult = (Vector)getSession().executeQuery(this.queryAll);

    }

    @Override
    public void verify() {
        if (this.inMemoryResult.size() != this.allEmployees.size()) {
            throw new TestErrorException("In Memory Query did not return all objects.  Auto-indirection triggering is not working");
        }
    }
}
