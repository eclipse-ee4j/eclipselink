/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import org.eclipse.persistence.exceptions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class MemoryQueryReturnNotConformedOnIndirection extends TestCase {
    protected Employee employee;
    protected ReadAllQuery queryAll;
    protected ReadObjectQuery queryObject;
    protected boolean exceptionThrown = false;
    protected java.util.Vector allEmployees;
    protected java.util.Vector inMemoryResult;

    public MemoryQueryReturnNotConformedOnIndirection() {
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
        queryAll.setSelectionCriteria(new ExpressionBuilder().get("address").get("city").notEqual("Montreal"));
        allEmployees = (Vector)getSession().executeQuery(queryAll);

        queryObject = new ReadObjectQuery(Employee.class);
        queryObject.setSelectionCriteria(new ExpressionBuilder().get("address").get("city").notEqual("Montreal"));
        Employee employee = (Employee)getSession().executeQuery(queryObject);
        if (employee != null) {
            employee.getAddress();
        } else {
            throw new TestErrorException("Test failed to execute correctly");
        }
    }

    public void test() {
        //all the employees with cities that come after Montreal should be
        //in the cache right now.
        this.queryAll.checkCacheOnly();//read from cache only
        // the next line is what we are testing in this test case
        this.queryAll.getInMemoryQueryIndirectionPolicy().ignoreIndirectionExceptionReturnNotConformed();
        try {
            this.inMemoryResult = (Vector)getSession().executeQuery(this.queryAll);
        } catch (QueryException exception) {
            exceptionThrown = exception.getErrorCode() == QueryException.MUST_INSTANTIATE_VALUEHOLDERS;
        }
    }

    public void verify() {
        if (exceptionThrown) {
            throw new TestErrorException("In Memory Query did not return appropraite results because of indirection");
        }
        if (this.inMemoryResult.size() != 1) {
            throw new TestErrorException("In Memory Query returned incorrect number of results. This is not expected to happen because of database changes");
        }
    }
}
