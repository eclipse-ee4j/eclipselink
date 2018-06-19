/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.events;

import org.eclipse.persistence.testing.framework.*;

public class DeleteEventHookTest extends EventHookTestCase {
    public void setup() {
        super.setup();
        getDatabaseSession().insertObject(getEmailAccount());
        getDatabaseSession().insertObject(getPhoneNumber());
        getDatabaseSession().insertObject(getAddress());
    }

    protected void test() {
        getDatabaseSession().deleteObject(getEmailAccount());
        getDatabaseSession().deleteObject(getPhoneNumber());
        getDatabaseSession().deleteObject(getAddress());
    }

    protected void verify() {
        if (!getEmailAccount().preDeleteExecuted) {
            throw new TestErrorException("Event hook failed. The pre delete method on " + getEmailAccount() + " failed to execute.");
        }

        if (!getEmailAccount().aboutToDeleteExecuted) {
            throw new TestErrorException("Event hook failed. The about to delete method on " + getEmailAccount() + " failed to execute.");
        }

        if (!getEmailAccount().postDeleteExecuted) {
            throw new TestErrorException("Event hook failed. The post delete method on " + getEmailAccount() + " failed to execute.");
        }

        if (!getPhoneNumber().preDeleteExecuted) {
            throw new TestErrorException("Event hook failed. The pre delete method on " + getPhoneNumber() + " failed to execute.");
        }

        if (!getPhoneNumber().postDeleteExecuted) {
            throw new TestErrorException("Event hook failed. The post delete method on " + getPhoneNumber() + " failed to execute.");
        }
        if (!getPhoneNumber().aboutToDeleteExecuted) {
            throw new TestErrorException("Event hook failed. The about to delete method on " + getPhoneNumber() + " failed to execute.");
        }

        if (!getAddressListener().preDeleteExecuted) {
            throw new TestErrorException("Event hook failed. The pre delete method on " + getAddressListener() + " failed to execute.");
        }

        if (!getAddressListener().postDeleteExecuted) {
            throw new TestErrorException("Event hook failed. The post delete method on " + getAddressListener() + " failed to execute.");
        }
        if (!getAddressListener().aboutToDeleteExecuted) {
            throw new TestErrorException("Event hook failed. The about to delete method on " + getAddressListener() + " failed to execute.");
        }
    }
}
