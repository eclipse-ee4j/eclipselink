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
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.RoomSellingPoint;
import org.eclipse.persistence.testing.models.aggregate.Agent;
import org.eclipse.persistence.testing.models.aggregate.House;

public class NestedAggregateCollectionAbstractTestCase extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public boolean instantiationExceptionOccurred = false;

    public void reset() {
        rollbackTransaction();
    }

    public void setup() {
        beginTransaction();
    }

    public void test() {
        try {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            Agent instance = Agent.example1();
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression exp = builder.get("firstName").equal(instance.getFirstName());
            exp = exp.and(builder.get("lastName").equal(instance.getLastName()));
            Agent clone = (Agent)uow.readObject(Agent.class, exp);
            uow.commitAndResume();

            House h1 = (House)instance.getHouses().firstElement();
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