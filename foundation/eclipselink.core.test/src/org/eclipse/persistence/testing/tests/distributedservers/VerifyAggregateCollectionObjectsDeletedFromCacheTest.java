/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.distributedservers;

import java.util.Vector;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.aggregate.Agent;
import org.eclipse.persistence.testing.models.aggregate.Customer;
import org.eclipse.persistence.testing.models.aggregate.Dependant;


/**
 * Tests that a removed aggregate object from a collection does not cause a nullPointerException
 *  when propogated to distributed cache (CR 4080)
 * 
 */
public class VerifyAggregateCollectionObjectsDeletedFromCacheTest extends ComplexUpdateTest {

    public VerifyAggregateCollectionObjectsDeletedFromCacheTest() {
        super();
    }

    public VerifyAggregateCollectionObjectsDeletedFromCacheTest(Agent originalObject) {
        super(originalObject);
    }

    protected void changeObject() {
        if (this.workingCopy instanceof Agent) {
            Agent agent = (Agent)this.workingCopy;
            Vector customers = agent.getCustomers();

            Customer customerx = (Customer)customers.firstElement();
            Vector dependants = customerx.getDependants();
            customerx.removeDependant((Dependant)dependants.lastElement());
            agent.removeCustomer((Customer)customers.lastElement());

        } else {
            //do nothing for the time being
        }
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
