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
package org.eclipse.persistence.testing.tests.events;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.descriptors.DescriptorEventListener;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.events.Customer;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class UpdateAttributeTest extends AutoVerifyTestCase {
    public DescriptorEventListener listener;
    public Customer customer;

    public UpdateAttributeTest() {
        this.listener = new UpdateAttributeEventListener();
    }

    public void setup() throws Throwable {
        super.setup();
        this.customer = Customer.example1();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(this.customer);
        uow.commit();
        getSession().getDescriptor(Customer.class).getEventManager().addListener(this.listener);
    }

    public void test() {
        try {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            Customer clone = (Customer)uow.readObject(Customer.class);
            clone.name = "99999";
            uow.commit();
        } catch (DatabaseException ex) {
            //failure of this test only if an exception is thrown above.
            throw new TestErrorException("Duplicate column names mean bug 4436710 is still broken");
        }
    }

    public void reset() throws Throwable {
        super.reset();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteObject(this.customer);
        uow.commit();
        getSession().getDescriptor(Customer.class).getEventManager().removeListener(this.listener);
    }
}
