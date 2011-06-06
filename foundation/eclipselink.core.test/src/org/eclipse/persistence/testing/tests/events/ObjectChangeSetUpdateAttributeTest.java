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
package org.eclipse.persistence.testing.tests.events;

import java.util.Vector;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.events.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.sessions.*;

public class ObjectChangeSetUpdateAttributeTest extends EventHookTestCase {
    public Customer customer;
    public ObjectChangeSet customerChangeSet;

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        beginTransaction();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        this.customer = Customer.example1();
        uow.registerObject(this.customer);
        uow.commit();
        Address.preUpdateCount = 0;
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Customer cloneCustomer = (Customer)uow.readObject(this.customer);
        cloneCustomer.postWrite = true;
        cloneCustomer.preUpdate = true;
        //bug 3489179 make sure there is no change records for this object for any
        //of the changes to be made in the event adaptor
        // there must be at least one change so that the events are thrown
        cloneCustomer.address.address = "No Place Special";
        uow.commit();
        this.customerChangeSet = (ObjectChangeSet)uow.getUnitOfWorkChangeSet().getObjectChangeSetForClone(cloneCustomer);
    }

    public void verify() {
        Customer originalCustomer = (Customer)getSession().readObject(this.customer);
        try {
            if (originalCustomer.name.equals("PreWrite")) {
                throw new TestErrorException("Failed to update attribute in direct to field");
            }
            if (!originalCustomer.associations.contains("PreUpdate")) {
                throw new TestErrorException("PreUpdateEvent failed to fire");
            }
            if (Address.preUpdateCount != 1) {
                throw new TestErrorException("PreUpdateEvent fired too many times");
            }
            if (!originalCustomer.associations.contains("PostWrite")) {
                throw new TestErrorException("Failed to update attribute in direct collection with addition");
            }
            if (originalCustomer.associations.contains("Mickey Mouse Club")) {
                throw new TestErrorException("Failed to update attribute in direct collection with removal");
            }
            if (((Vector)originalCustomer.orders.getValue()).size() != 3) {
                throw new TestErrorException("Failed to update attribute in collection");
            }
            if (this.customerChangeSet.getChanges().size() <= 2) {
                throw new TestErrorException("Failed to update the changeSet");
            }
            if (!originalCustomer.creditCard.number.equals("0")) {
                throw new TestErrorException("Failed to update the aggregate attribute");
            }
            if (this.customerChangeSet.getChangesForAttributeNamed("creditCard") == null) {
                throw new TestErrorException("Failed to update the changeset with aggregate attribute");
            }
        } finally {
            //Make sure that we set back the static value
            Address.preUpdateCount = 0;
        }
    }
}
