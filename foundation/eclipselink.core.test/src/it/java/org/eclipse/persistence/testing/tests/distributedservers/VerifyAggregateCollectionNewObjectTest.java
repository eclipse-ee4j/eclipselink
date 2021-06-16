/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.distributedservers;

import java.util.List;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.aggregate.Agent;
import org.eclipse.persistence.testing.models.aggregate.Builder;
import org.eclipse.persistence.testing.models.aggregate.Customer;
import org.eclipse.persistence.testing.tests.aggregate.AgentBuilderHelper;


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
