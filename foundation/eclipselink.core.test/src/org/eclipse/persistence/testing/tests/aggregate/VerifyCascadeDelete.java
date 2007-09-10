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
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.models.aggregate.Agent;
import org.eclipse.persistence.testing.models.aggregate.Company;
import org.eclipse.persistence.testing.models.aggregate.Customer;

public class VerifyCascadeDelete extends TransactionalTestCase {
    public Company company;
    public Agent agent;
    private OneToOneMapping companyMapping;
    private boolean privateOwnedValue = false;

    public VerifyCascadeDelete() {
        setDescription("Verifies that deletes in an aggregate collections does not cascade to non-privately owned children");
    }

    public void setup() {
        super.setup();
        ClassDescriptor customerDescriptor = (ClassDescriptor)getSession().getProject().getDescriptors().get(Customer.class);
        companyMapping = (OneToOneMapping)customerDescriptor.getMappingForAttributeName("company");
        privateOwnedValue = companyMapping.isPrivateOwned();
        companyMapping.setIsPrivateOwned(false);
        java.util.Vector agents = getSession().readAllObjects(Agent.class);

        // Find an agent with a customer.
        for (int index = 0; index < agents.size(); index++) {
            agent = (Agent)agents.get(index);
            if (agent.getCustomers().size() > 0) {
                company = ((Customer)agent.getCustomers().get(0)).getCompany();
                // Found one.
                break;
            }
        }
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Agent agentClone = (Agent)uow.readObject(agent);
        agentClone.getCustomers().clear();
        uow.commit();
    }

    public void verify() {
        if (getSession().readObject(company) == null) {
            throw new TestErrorException("Cascaded the delete of a non-private part.");
        }
    }

    public void reset() {
        super.reset();
        companyMapping.setIsPrivateOwned(privateOwnedValue);
    }
}