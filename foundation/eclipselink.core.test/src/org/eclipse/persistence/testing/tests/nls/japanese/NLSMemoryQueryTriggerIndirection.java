/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.nls.japanese;

import java.util.Vector;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

public class NLSMemoryQueryTriggerIndirection extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    protected NLSEmployee employee;
    protected ReadAllQuery queryAll;
    protected ReadObjectQuery queryObject;
    protected java.util.Vector allEmployees;
    protected java.util.Vector inMemoryResult;

    /**
     * This method was created in VisualAge.
     */
    public NLSMemoryQueryTriggerIndirection() {
        super();
    }

    /**
     * This method was created in VisualAge.
     */
    public void reset() {
        //clear the cache.
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    /**
     * This method was created in VisualAge.
     */
    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        allEmployees = new Vector();
        queryAll = new ReadAllQuery();
        queryAll.setReferenceClass(NLSEmployee.class);
        queryAll.setSelectionCriteria(new ExpressionBuilder().get("address").get("city").greaterThan("\u3059\u30db\u30bb\u30c8\u30c4\u30aa\u30a2\u30b7"));//("Montreal"));
        allEmployees = (Vector)getSession().executeQuery(queryAll);

    }

    /**
     * This method was created in VisualAge.
     */
    public void test() {
        //all the employees with cities that come after Montreal should be
        //in the cache right now.
        this.queryAll.checkCacheOnly();//read from cache only
        this.queryAll.getInMemoryQueryIndirectionPolicy().triggerIndirection();

        this.inMemoryResult = (Vector)getSession().executeQuery(this.queryAll);

    }

    /**
     * This method was created in VisualAge.
     */
    public void verify() {
        if (this.inMemoryResult.size() != this.allEmployees.size()) {
            throw new TestErrorException("In Memory Query did not return all objects.  Auto-indirection triggering is not working");
        }
    }
}
