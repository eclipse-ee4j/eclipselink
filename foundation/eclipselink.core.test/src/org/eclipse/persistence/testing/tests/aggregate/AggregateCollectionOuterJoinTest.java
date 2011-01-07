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
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.Agent;
import org.eclipse.persistence.testing.models.aggregate.Builder;
import org.eclipse.persistence.testing.models.aggregate.House;

/**
 * Bug 2847621 - Test to ensure outer joins are working with aggregate collections
 */
public class AggregateCollectionOuterJoinTest extends AutoVerifyTestCase {
    Class cls;
    protected List objects = null;

    // Must be either Agent or Builder
    public AggregateCollectionOuterJoinTest(Class cls) {
        super();
        this.cls = cls;
        setName(getName() + AgentBuilderHelper.getNameInBrackets(cls));
        setDescription("Tests that outer joins work with aggregate collections.");
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        beginTransaction();
        // insert two new agents.  One will satisfy my expression with the outer join, one will not
        Object bob, frank;
        if(Agent.class.equals(cls)) {
            bob = new Agent();
            frank = new Agent();
        } else {
            bob = new Builder();
            frank = new Builder();
        }
        AgentBuilderHelper.setFirstName(bob, "Bob");
        AgentBuilderHelper.setLastName(bob, "Smith");
        AgentBuilderHelper.setFirstName(frank, "Frank");
        AgentBuilderHelper.setLastName(frank, "Jones");
        House house = new House();
        house.setLocation("50 O'Connor St.");
        AgentBuilderHelper.addHouse(frank, house);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(bob);
        uow.registerObject(frank);
        uow.commit();
    }

    public void test() {
        // Read using an outer join on an agent's houses houses.
        ReadAllQuery query = new ReadAllQuery(cls);
        ExpressionBuilder object = new ExpressionBuilder();
        Expression exp = object.get("firstName").equal("Bob");
        exp = exp.or(object.anyOfAllowingNone("houses").get("location").equal("435 Carling Ave."));
        query.setSelectionCriteria(exp);
        objects = (List)getSession().executeQuery(query);
    }

    public void verify() {
        // We should get back the prepopulated agent plus one agent that was added in setup.
        if (objects.size() != 2) {
            throw new TestErrorException("Outer join is not working correctly with AggregateCollections.");
        }
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}
