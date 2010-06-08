/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.events.Address;
import org.eclipse.persistence.testing.models.events.EmailAccount;
import org.eclipse.persistence.testing.models.events.Phone;
import org.eclipse.persistence.testing.framework.*;

public class CloneAndMergeEventHookTest extends EventHookTestCase {
    public void setup() {
        super.setup();
        getDatabaseSession().writeObject(getEmailAccount());
        getDatabaseSession().writeObject(getPhoneNumber());
        getDatabaseSession().writeObject(getAddress());
    }

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

        if (!getEmailAccount().postMergeExecuted) {
            throw new TestErrorException("Event hook failed. The post clone method on " + getEmailAccount() + " failed to execute.");
        }

        if (!getPhoneNumber().postMergeExecuted) {
            throw new TestErrorException("Event hook failed. The post clone method on " + getPhoneNumber() + " failed to execute.");
        }

        if (!getAddressListener().postMergeExecuted) {
            throw new TestErrorException("Event hook failed. The post clone method on " + getAddressListener() + " failed to execute.");
        }
    }
}
