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

import org.eclipse.persistence.testing.models.events.Address;
import org.eclipse.persistence.testing.models.events.EmailAccount;
import org.eclipse.persistence.testing.models.events.Phone;
import org.eclipse.persistence.testing.framework.*;

public class UpdateEventHookTest extends EventHookTestCase {
    public void setup() {
        super.setup();
        getDatabaseSession().insertObject(getEmailAccount());
        getDatabaseSession().insertObject(getPhoneNumber());
        getDatabaseSession().insertObject(getAddress());
    }

    protected void test() {
        EmailAccount emailAccount = getEmailAccount();
        Phone phone = getPhoneNumber();
        Address address = getAddress();

        emailAccount.setHostName("test.usenet.net");
        phone.phoneNo = new String("234-3453");
        address.address = new String("XYZ, I M Here");

        getDatabaseSession().updateObject(emailAccount);
        getDatabaseSession().updateObject(phone);
        getDatabaseSession().updateObject(address);
    }

    protected void verify() {
        if (!getEmailAccount().preUpdateExecuted) {
            throw new TestErrorException("Event hook failed. The pre update method on " + getEmailAccount() + " failed to execute.");
        }

        if (!getEmailAccount().postUpdateExecuted) {
            throw new TestErrorException("Event hook failed. The post update method on " + getEmailAccount() + " failed to execute.");
        }

        if (!getEmailAccount().aboutToUpdateExecuted) {
            throw new TestErrorException("Event hook failed. The about to update method on " + getEmailAccount() + " failed to execute.");
        }

        if (!getPhoneNumber().preUpdateExecuted) {
            throw new TestErrorException("Event hook failed. The pre update method on " + getPhoneNumber() + " failed to execute.");
        }

        if (!getPhoneNumber().postUpdateExecuted) {
            throw new TestErrorException("Event hook failed. The post update method on " + getPhoneNumber() + " failed to execute.");
        }

        if (!getPhoneNumber().aboutToUpdateExecuted) {
            throw new TestErrorException("Event hook failed. The about to update method on " + getPhoneNumber() + " failed to execute.");
        }

        if (!getAddressListener().preUpdateExecuted) {
            throw new TestErrorException("Event hook failed. The pre update method on " + getAddressListener() + " failed to execute.");
        }

        if (!getAddressListener().postUpdateExecuted) {
            throw new TestErrorException("Event hook failed. The post update method on " + getAddressListener() + " failed to execute.");
        }

        if (!getAddressListener().aboutToUpdateExecuted) {
            throw new TestErrorException("Event hook failed. The about to update method on " + getAddressListener() + " failed to execute.");
        }
    }
}
