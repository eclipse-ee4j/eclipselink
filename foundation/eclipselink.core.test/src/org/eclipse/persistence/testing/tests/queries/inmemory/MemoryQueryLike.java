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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import java.util.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 *  Test added for Odin
 *  Tests running a query which uses 'like' and checkCacheOnly
 *  This will only work in JDK 1.4
 */
public class MemoryQueryLike extends TestCase {
    Vector employees = null;

    public MemoryQueryLike() {
        super();
        employees = new Vector();
    }

    public void reset() {
        //clear the cache.
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        // read all the employees into the cache.
        getSession().readAllObjects(Employee.class);
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        query.checkCacheOnly();//read from cache only

        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp = bldr.get("firstName").like("Bo_");
        query.setSelectionCriteria(exp);
        employees = (Vector)getSession().executeQuery(query);

    }

    public void verify() {
        if (employees.size() != 1) {
            throw new TestErrorException("Expected 1 employees but retured " + employees.size() + " for in-memory 'like' query.");
        }
    }
}
