/*
 * Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.events;

import java.util.Vector;

import org.eclipse.persistence.config.CacheIsolationType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.events.Address;
import org.eclipse.persistence.testing.models.events.CreditCard;
import org.eclipse.persistence.testing.models.events.Customer;
import org.eclipse.persistence.testing.models.events.EmailAccount;
import org.eclipse.persistence.testing.models.events.Order;
import org.eclipse.persistence.testing.models.events.Phone;

public class CloneEventOnIsolatedSessionTest extends EventHookTestCase {
    Vector<ClassDescriptor> issolatedDescriptors;
    boolean hasIsolatedClasses = false;

    @Override
    public void setup() {
        //set all descriptors in this package as isolated
        issolatedDescriptors = new Vector();
        issolatedDescriptors.add(getSession().getDescriptor(Address.class));
        issolatedDescriptors.add(getSession().getDescriptor(Phone.class));
        issolatedDescriptors.add(getSession().getDescriptor(CreditCard.class));
        issolatedDescriptors.add(getSession().getDescriptor(Customer.class));
        issolatedDescriptors.add(getSession().getDescriptor(EmailAccount.class));
        issolatedDescriptors.add(getSession().getDescriptor(Order.class));
        for (ClassDescriptor descriptor : issolatedDescriptors){
            descriptor.setCacheIsolation(CacheIsolationType.ISOLATED);
            /// the value assigned by default during initialization for an isolated descriptor.
            descriptor.setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_CACHE_ALWAYS);

        }
        hasIsolatedClasses = getDatabaseSession().getProject().hasIsolatedClasses();
        getDatabaseSession().getProject().setHasIsolatedClasses(true);
        super.setup();
        getDatabaseSession().writeObject(getEmailAccount());
        getDatabaseSession().writeObject(getPhoneNumber());
        getDatabaseSession().writeObject(getAddress());
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    public void reset() {
        for (ClassDescriptor descriptor : issolatedDescriptors){
            descriptor.setCacheIsolation(CacheIsolationType.SHARED);
            /// the value assigned by default during initialization for a non-isolated descriptor.
            descriptor.setUnitOfWorkCacheIsolationLevel(ClassDescriptor.ISOLATE_NEW_DATA_AFTER_TRANSACTION);
        }
        getDatabaseSession().getProject().setHasIsolatedClasses(hasIsolatedClasses);

        super.reset();
    }

    @Override
    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        EmailAccount emailAccountCopy = (EmailAccount)uow.readObject(getEmailAccount());

        // Must change the object or no merge will happen
        emailAccountCopy.setHostName("localHost");
        Phone phoneNumberCopy = (Phone)uow.readObject(getPhoneNumber());
        phoneNumberCopy.phoneNo = "555-5555";
        Address addressCopy = (Address)uow.readObject(getAddress());
        addressCopy.address = "No Where";

        uow.commit();

        if (!emailAccountCopy.postCloneExecuted) {
            throw new TestErrorException("Event hook failed. The post clone method on " + emailAccountCopy + " failed to execute.");
        }

        if (!phoneNumberCopy.postCloneExecuted) {
            throw new TestErrorException("Event hook failed. The post clone method on " + phoneNumberCopy + " failed to execute.");
        }
    }


}
