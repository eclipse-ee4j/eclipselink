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
package org.eclipse.persistence.testing.tests.aggregate;

import java.util.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.ReadObjectTest;

public class BatchReadingWithAggregateCollectionMapping extends ReadObjectTest {

    Class cls;
    // Must be Agent or Builder
    public BatchReadingWithAggregateCollectionMapping(Class cls) {
        super();
        this.cls = cls;
        setName(getName() + AgentBuilderHelper.getNameInBrackets(cls));
    }

    // Must be Agent or Builder
    public BatchReadingWithAggregateCollectionMapping(Object originalObject) {
        super(originalObject);
    }

    public void reset() {

    }

    public void setup() {
    }

    public void test() {
        if(cls == null) {
            cls = originalObject.getClass();
        }
        ReadAllQuery query = new ReadAllQuery(cls);
        query.addBatchReadAttribute("houses");
        query.setSelectionCriteria(new ExpressionBuilder().get("lastName").equal("Jordan"));
        List objects = (List)getSession().executeQuery(query);
        Object object = objects.get(0);
        List houses = AgentBuilderHelper.getHouses(object);
    }

    public void verify() {
    }
}

