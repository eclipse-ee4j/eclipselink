/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.aggregate;

import java.util.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.ReadObjectTest;
import org.eclipse.persistence.testing.models.aggregate.*;

public class BatchReadingWithAggregateCollectionMapping extends ReadObjectTest {

    public BatchReadingWithAggregateCollectionMapping() {
        super();
    }

    public BatchReadingWithAggregateCollectionMapping(Object originalObject) {
        super(originalObject);
    }

    public void reset() {

    }

    public void setup() {
    }

    public void test() {

        ReadAllQuery query = new ReadAllQuery(Agent.class);
        query.addBatchReadAttribute("houses");
        query.setSelectionCriteria(new ExpressionBuilder().get("lastName").equal("Jordan"));
        Vector agents = (Vector)getSession().executeQuery(query);
        Agent agt = (Agent)agents.firstElement();
        Vector houses = agt.getHouses();
    }

    public void verify() {
    }
}

