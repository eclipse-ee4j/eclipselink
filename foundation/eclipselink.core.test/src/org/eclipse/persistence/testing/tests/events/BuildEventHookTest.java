/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.events;

import org.eclipse.persistence.testing.models.events.Address;
import org.eclipse.persistence.testing.models.events.EmailAccount;
import org.eclipse.persistence.testing.models.events.Phone;
import org.eclipse.persistence.testing.framework.*;

public class BuildEventHookTest extends EventHookTestCase {
    public void setup() {
        super.setup();
        getDatabaseSession().writeObject(getEmailAccount());
        getDatabaseSession().writeObject(getPhoneNumber());
        getDatabaseSession().writeObject(getAddress());
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test() {
        setEmailAccount((EmailAccount)getSession().readObject(getEmailAccount()));
        setPhoneNumber((Phone)getSession().readObject(getPhoneNumber()));
        setAddress((Address)getSession().readObject(getAddress()));
    }

    protected void verify() {
        if (!getEmailAccount().postBuildExecuted) {
            throw new TestErrorException("Event hook failed. The post build method on " + getEmailAccount() + " failed to execute.");
        }

        if (!getPhoneNumber().postBuildExecuted) {
            throw new TestErrorException("Event hook failed. The post build method on " + getPhoneNumber() + " failed to execute.");
        }

        if (!getAddressListener().postBuildExecuted) {
            throw new TestErrorException("Event hook failed. The post build method on " + getAddressListener() + " failed to execute.");
        }
    }
}