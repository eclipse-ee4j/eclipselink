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

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.aggregate.Customer;
import org.eclipse.persistence.testing.tests.aggregate.AgentBuilderHelper;

import java.util.List;
import java.util.Vector;

public class MemoryQueryAcrossAggregateCollectionMapping extends TestCase {
    protected Class<?> cls;
    // Either Agent or Builder
    protected Object object;
    protected Customer customer;
    protected ReadAllQuery queryAll;
    protected ReadAllQuery queryObjects;
    protected ReadObjectQuery queryObject;
    protected java.util.List allCustomers;
    protected java.util.List customers;

    // Must be Agent or Builder
    public MemoryQueryAcrossAggregateCollectionMapping(Class<?> cls) {
        super();
        this.cls = cls;
        setName(getName() + AgentBuilderHelper.getNameInBrackets(cls));
    }

    @Override
    public void reset() {
        //clear the cache.
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    public void setup() {
        customers = new Vector();
        queryAll = new ReadAllQuery();
        queryAll.setReferenceClass(cls);
        queryAll.setSelectionCriteria(new ExpressionBuilder().anyOf("customers").get("name").greaterThan("Alan Greenspan"));
        getSession().removeQuery("getAllCustomers");
        getSession().addQuery("getAllCustomers", queryAll);
        allCustomers = (Vector)getSession().executeQuery("getAllCustomers");
        for (Object allCustomer : allCustomers) {
            List eachCustomer = AgentBuilderHelper.getCustomers(allCustomer);
            customers.addAll(eachCustomer);
            //trigger all the value holders of customers
        }
    }

    @Override
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
