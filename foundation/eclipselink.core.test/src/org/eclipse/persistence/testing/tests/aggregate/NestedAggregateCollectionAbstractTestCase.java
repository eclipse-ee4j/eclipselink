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
package org.eclipse.persistence.testing.tests.aggregate;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.Builder;
import org.eclipse.persistence.testing.models.aggregate.RoomSellingPoint;
import org.eclipse.persistence.testing.models.aggregate.Agent;
import org.eclipse.persistence.testing.models.aggregate.House;

public class NestedAggregateCollectionAbstractTestCase extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public Class cls;
    public boolean instantiationExceptionOccurred = false;

    // Must be Agent or Builder
    public NestedAggregateCollectionAbstractTestCase(Class cls) {
        super();
        this.cls = cls;
        setName(getName() + AgentBuilderHelper.getNameInBrackets(cls));
    }
    
    public void reset() {
        rollbackTransaction();
    }

    public void setup() {
        beginTransaction();
    }

    public void test() {
        try {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            Object instance;
            if(Agent.class.equals(cls)) {
                instance = Agent.example1();
            } else {
                instance = Builder.example1();
            }
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression exp = builder.get("firstName").equal(AgentBuilderHelper.getFirstName(instance));
            exp = exp.and(builder.get("lastName").equal(AgentBuilderHelper.getLastName(instance)));
            Object clone = uow.readObject(cls, exp);
            uow.commitAndResume();

            House h1 = (House)AgentBuilderHelper.getHouses(instance).get(0);
            Vector sellingPoints = h1.getSellingPoints();
            sellingPoints.add(RoomSellingPoint.example4());
            uow.deepMergeClone(instance);
            uow.commit();
        } catch (Exception exp) {
            instantiationExceptionOccurred = true;
        }
    }

    public void verify() {
        if (instantiationExceptionOccurred) {
            throw new TestErrorException("EclipseLink attempted to merge an abstract class, when merging an aggregate collection.");
        }
    }
}
