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
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.aggregate.Agent;
import org.eclipse.persistence.testing.models.aggregate.Builder;
import org.eclipse.persistence.testing.models.aggregate.House;
import org.eclipse.persistence.testing.models.aggregate.RoomSellingPoint;

import java.util.Vector;

public class NestedAggregateCollectionAbstractTestCase extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public Class<?> cls;
    public boolean instantiationExceptionOccurred = false;

    // Must be Agent or Builder
    public NestedAggregateCollectionAbstractTestCase(Class<?> cls) {
        super();
        this.cls = cls;
        setName(getName() + AgentBuilderHelper.getNameInBrackets(cls));
    }

    @Override
    public void reset() {
        rollbackTransaction();
    }

    @Override
    public void setup() {
        beginTransaction();
    }

    @Override
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

    @Override
    public void verify() {
        if (instantiationExceptionOccurred) {
            throw new TestErrorException("EclipseLink attempted to merge an abstract class, when merging an aggregate collection.");
        }
    }
}
