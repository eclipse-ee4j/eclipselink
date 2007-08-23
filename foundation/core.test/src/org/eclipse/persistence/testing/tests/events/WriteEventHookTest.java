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

import org.eclipse.persistence.testing.framework.*;

public class WriteEventHookTest extends EventHookTestCase {
    protected void test() {
        getDatabaseSession().writeObject(getEmailAccount());
        getDatabaseSession().writeObject(getPhoneNumber());
        getDatabaseSession().writeObject(getAddress());
    }

    protected void verify() {
        if (!getEmailAccount().preWriteExecuted) {
            throw new TestErrorException("Event hook failed. The pre write method on " + getEmailAccount() + " failed to execute.");
        }

        if (!getEmailAccount().postWriteExecuted) {
            throw new TestErrorException("Event hook failed. The post write method on " + getEmailAccount() + " failed to execute.");
        }

        if (!getPhoneNumber().preWriteExecuted) {
            throw new TestErrorException("Event hook failed. The pre write method on " + getPhoneNumber() + " failed to execute.");
        }

        if (!getPhoneNumber().postWriteExecuted) {
            throw new TestErrorException("Event hook failed. The post write method on " + getPhoneNumber() + " failed to execute.");
        }

        if (!getAddressListener().preWriteExecuted) {
            throw new TestErrorException("Event hook failed. The pre write method on " + getAddressListener() + " failed to execute.");
        }

        if (!getAddressListener().postWriteExecuted) {
            throw new TestErrorException("Event hook failed. The post write method on " + getAddressListener() + " failed to execute.");
        }
    }
}