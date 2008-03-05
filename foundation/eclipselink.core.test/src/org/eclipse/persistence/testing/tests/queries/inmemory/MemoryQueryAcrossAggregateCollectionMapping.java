/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import java.util.Enumeration;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.aggregate.*;

public class MemoryQueryAcrossAggregateCollectionMapping extends TestCase {
    protected Agent agent;
    protected Customer customer;
    protected ReadAllQuery queryAll;
    protected ReadAllQuery queryObjects;
    protected ReadObjectQuery queryObject;
    protected java.util.Vector allCustomers;
    protected java.util.Vector customers;

    public void reset() {
        //clear the cache.
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        allCustomers = new Vector();
        customers = new Vector();
        queryAll = new ReadAllQuery();
        queryAll.setReferenceClass(Agent.class);
        queryAll.setSelectionCriteria(new ExpressionBuilder().anyOf("customers").get("name").greaterThan("Alan Greenspan"));
        getSession().removeQuery("getAllCustomers");
        getSession().addQuery("getAllCustomers", queryAll);
        allCustomers = (Vector)getSession().executeQuery("getAllCustomers");
        for (Enumeration enumtr = allCustomers.elements(); enumtr.hasMoreElements();) {
            Vector eachCustomer = ((Agent)enumtr.nextElement()).getCustomers();
            customers.addAll(eachCustomer);
            //trigger all the value holders of customers
        }
    }

    public void test() {
        //all the employees with project names greater than Amagedon should be
        //in the cache right now.
        queryObjects = new ReadAllQuery();
        queryObjects.setReferenceClass(Agent.class);
        queryObjects.checkCacheOnly();//read from cache only

        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp = bldr.anyOf("customers").get("name").equal("Vince Carter");
        queryObjects.setSelectionCriteria(exp);
        customers = (Vector)getSession().executeQuery(queryObjects);

    }
}