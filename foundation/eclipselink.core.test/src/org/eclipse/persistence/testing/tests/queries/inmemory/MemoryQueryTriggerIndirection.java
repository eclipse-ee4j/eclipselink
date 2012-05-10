/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries.inmemory;

import java.util.Vector;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class MemoryQueryTriggerIndirection extends TestCase {
    protected Employee employee;
    protected ReadAllQuery queryAll;
    protected ReadObjectQuery queryObject;
    protected java.util.Vector allEmployees;
    protected java.util.Vector inMemoryResult;

    public MemoryQueryTriggerIndirection() {
        super();
    }

    public void reset() {
        //clear the cache.
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        allEmployees = new Vector();
        queryAll = new ReadAllQuery();
        queryAll.setReferenceClass(Employee.class);
        queryAll.setSelectionCriteria(new ExpressionBuilder().get("address").get("city").greaterThan("Montreal"));
        allEmployees = (Vector)getSession().executeQuery(queryAll);

    }

    public void test() {
        //all the employees with cities that come after Montreal should be
        //in the cache right now.
        this.queryAll.checkCacheOnly();//read from cache only
        this.queryAll.getInMemoryQueryIndirectionPolicy().triggerIndirection();

        this.inMemoryResult = (Vector)getSession().executeQuery(this.queryAll);

    }

    public void verify() {
        if (this.inMemoryResult.size() != this.allEmployees.size()) {
            throw new TestErrorException("In Memory Query did not return all objects.  Auto-indirection triggering is not working");
        }
    }
}
