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
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.Agent;
import org.eclipse.persistence.testing.models.aggregate.House;

/**
 * Bug 2847621 - Test to ensure outer joins are working with aggregate collections
 */
public class AggregateCollectionOuterJoinTest extends AutoVerifyTestCase {
    protected Vector agents = null;

    public AggregateCollectionOuterJoinTest() {
        setDescription("Tests that outer joins work with aggregate collections.");
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        beginTransaction();
        // insert two new agents.  One will satisfy my expression with the outer join, one will not
        Agent bob = new Agent();
        bob.setFirstName("Bob");
        bob.setLastName("Smith");
        Agent frank = new Agent();
        frank.setFirstName("Frank");
        frank.setLastName("Jones");
        House house = new House();
        house.setLocation("50 O'Connor St.");
        frank.addHouse(house);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(bob);
        uow.registerObject(frank);
        uow.commit();
    }

    public void test() {
        // Read using an outer join on an agent's houses houses.
        ReadAllQuery query = new ReadAllQuery(Agent.class);
        ExpressionBuilder agent = new ExpressionBuilder();
        Expression exp = agent.get("firstName").equal("Bob");
        exp = exp.or(agent.anyOfAllowingNone("houses").get("location").equal("435 Carling Ave."));
        query.setSelectionCriteria(exp);
        agents = (Vector)getSession().executeQuery(query);
    }

    public void verify() {
        // We should get back the prepopulated agent plus one agent that was added in setup.
        if (agents.size() != 2) {
            throw new TestErrorException("Outer join is not working correctly with AggregateCollections.");
        }
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}