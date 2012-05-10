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

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.models.aggregate.Company;
import org.eclipse.persistence.testing.models.aggregate.Customer;

public class VerifyCascadeDelete extends TransactionalTestCase {
    public Class cls;
    public Company company;
    public Object object;
    private OneToOneMapping companyMapping;
    private boolean privateOwnedValue = false;

    // Must be Agent or Builder
    public VerifyCascadeDelete(Class cls) {
        super();
        this.cls = cls;
        setName(getName() + AgentBuilderHelper.getNameInBrackets(cls));
        setDescription("Verifies that deletes in an aggregate collections does not cascade to non-privately owned children");
    }

    public void setup() {
        super.setup();
        // AggregateCollectionMapping descriptors now cloned - should be obtained from the parent descriptor (Agent or Builder).
        ClassDescriptor parentDescriptor = getSession().getDescriptor(cls);
        ClassDescriptor customerDescriptor = ((AggregateCollectionMapping)parentDescriptor.getMappingForAttributeName("customers")).getReferenceDescriptor();
        companyMapping = (OneToOneMapping)customerDescriptor.getMappingForAttributeName("company");
        privateOwnedValue = companyMapping.isPrivateOwned();
        companyMapping.setIsPrivateOwned(false);
        java.util.List objects = getSession().readAllObjects(cls);

        // Find an agent with a customer.
        for (int index = 0; index < objects.size(); index++) {
            object = objects.get(index);
            if (AgentBuilderHelper.getCustomers(object).size() > 0) {
                company = ((Customer)AgentBuilderHelper.getCustomers(object).get(0)).getCompany();
                // Found one.
                break;
            }
        }
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Object objectClone = uow.readObject(object);
        AgentBuilderHelper.getCustomers(objectClone).clear();
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
