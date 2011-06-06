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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import java.util.List;
import java.util.Vector;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.aggregate.*;
import org.eclipse.persistence.testing.tests.aggregate.AgentBuilderHelper;

public class MemoryQueryAcrossAggregateCollectionMapping extends TestCase {
    protected Class cls;
    // Either Agent or Builder
    protected Object object;
    protected Customer customer;
    protected ReadAllQuery queryAll;
    protected ReadAllQuery queryObjects;
    protected ReadObjectQuery queryObject;
    protected java.util.List allCustomers;
    protected java.util.List customers;

    // Must be Agent or Builder
    public MemoryQueryAcrossAggregateCollectionMapping(Class cls) {
        super();
        this.cls = cls;
        setName(getName() + AgentBuilderHelper.getNameInBrackets(cls));
    }
    
    public void reset() {
        //clear the cache.
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        customers = new Vector();
        queryAll = new ReadAllQuery();
        queryAll.setReferenceClass(cls);
        queryAll.setSelectionCriteria(new ExpressionBuilder().anyOf("customers").get("name").greaterThan("Alan Greenspan"));
        getSession().removeQuery("getAllCustomers");
        getSession().addQuery("getAllCustomers", queryAll);
        allCustomers = (Vector)getSession().executeQuery("getAllCustomers");
        for(int i=0; i < allCustomers.size(); i++) {
            List eachCustomer = AgentBuilderHelper.getCustomers(allCustomers.get(i));
            customers.addAll(eachCustomer);
            //trigger all the value holders of customers
        }
    }

    public void test() {
        //all the employees with project names greater than Amagedon should be
        //in the cache right now.
        queryObjects = new ReadAllQuery();
        queryObjects.setReferenceClass(cls);
        queryObjects.checkCacheOnly();//read from cache only

        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp = bldr.anyOf("customers").get("name").equal("Vince Carter");
        queryObjects.setSelectionCriteria(exp);
        customers = (List)getSession().executeQuery(queryObjects);

    }
}
