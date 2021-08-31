/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Vector;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.exceptions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class MemoryQueryThrowExceptionOnIndirection extends TestCase {
    protected Employee employee;
    protected ReadAllQuery queryAll;
    protected boolean exceptionThrown = false;
    protected java.util.Vector allEmployees;
    protected java.util.Vector inMemoryResult;

    public MemoryQueryThrowExceptionOnIndirection() {
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
        queryAll.setSelectionCriteria(new ExpressionBuilder().get("address").get("city").notEqual("Montreal"));
        allEmployees = (Vector)getSession().executeQuery(queryAll);
    }

    @Override
    public void test() {
        //all the employees with cities that come after Montreal should be
        //in the cache right now.
        this.queryAll.checkCacheOnly();//read from cache only
        //The next line is what we are testing
        this.queryAll.getInMemoryQueryIndirectionPolicy().throwIndirectionException();
        try {
            this.inMemoryResult = (Vector)getSession().executeQuery(this.queryAll);
        } catch (QueryException exception) {
            exceptionThrown = exception.getErrorCode() == QueryException.MUST_INSTANTIATE_VALUEHOLDERS;
        }
    }

    @Override
    public void verify() {
        if (!exceptionThrown) {
            throw new TestErrorException("In Memory Query did not return all objects because of indirection");
        }
    }
}
