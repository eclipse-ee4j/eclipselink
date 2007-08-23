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

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.aggregate.Agent;
import org.eclipse.persistence.testing.models.aggregate.Customer;

public class AggregateCollectionClearTest extends TransactionalTestCase {
    public Agent agent;

    public AggregateCollectionClearTest() {
        setDescription("Verifies that when aggregate collections get cleared that the changes are merged.  CR 3013");
    }

    public void setup() {
        super.setup();
        java.util.Vector agents = getSession().readAllObjects(Agent.class);
        int index = 0;
        while ((index < agents.size()) && ((agent == null) || (((Customer)agent.getCustomers().get(0)) == null))) {
            agent = (Agent)agents.get(index);
            ++index;
        }
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Agent agentClone = (Agent)uow.readObject(agent);
        agentClone.getCustomers().clear();
        uow.commit();
    }

    public void verify() {
        Agent agent1 = (Agent)getSession().readObject(agent);
        if (!agent1.getCustomers().isEmpty()) {
            throw new TestErrorException("Did not merge change");
        }
    }

    public void reset() {
        super.reset();
    }
}