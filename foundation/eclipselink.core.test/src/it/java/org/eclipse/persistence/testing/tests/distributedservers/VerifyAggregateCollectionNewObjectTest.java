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
package org.eclipse.persistence.testing.tests.distributedservers;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.aggregate.Agent;
import org.eclipse.persistence.testing.models.aggregate.Builder;
import org.eclipse.persistence.testing.models.aggregate.Customer;
import org.eclipse.persistence.testing.tests.aggregate.AgentBuilderHelper;

import java.util.List;


/**
 * Tests that a removed aggregate object from a collection does not cause a nullPointerException
 *  when propagated to distributed cache (CR 4080)
 */
public class VerifyAggregateCollectionNewObjectTest extends ComplexUpdateTest {

    public VerifyAggregateCollectionNewObjectTest() {
        super();
    }

    public VerifyAggregateCollectionNewObjectTest(Agent originalObject) {
        super(originalObject);
    }
    public VerifyAggregateCollectionNewObjectTest(Builder originalObject) {
        super(originalObject);
    }

    @Override
    protected void changeObject() {
        //trigger indirection;
        this.distributedCopy = getObjectFromDistributedSession(this.query);
        AgentBuilderHelper.getCustomers(this.distributedCopy);
        Object object = this.workingCopy;
        List customers = AgentBuilderHelper.getCustomers(object);
        // cr 4155 do not trigger indirection here.  Merge should now handle it
        Customer customerx = Customer.example3();
        AgentBuilderHelper.addCustomer(object, customerx);
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    @Override
    protected void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        this.objectFromDatabase = getSession().executeQuery(this.query);

        if (!(((AbstractSession)getSession()).compareObjects(this.objectToBeWritten, this.objectFromDatabase))) {
            throw new TestErrorException("The object inserted into the database, '" + this.objectFromDatabase + "' does not match the original, '" + this.objectToBeWritten + ".");
        }
        this.distributedCopy = getObjectFromDistributedSession(this.query);
        if (!(((AbstractSession)getSession()).compareObjects(this.distributedCopy, this.objectFromDatabase))) {
            throw new TestErrorException("The object inserted into the database, '" + this.objectFromDatabase + "' does not match the distributed Copy, '" + this.distributedCopy + ".");
        }
    }
}
