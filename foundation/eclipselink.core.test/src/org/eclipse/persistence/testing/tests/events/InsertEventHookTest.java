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

public class InsertEventHookTest extends EventHookTestCase {
    protected void test() {
        getDatabaseSession().insertObject(getEmailAccount());
        getDatabaseSession().insertObject(getPhoneNumber());
        getDatabaseSession().insertObject(getAddress());
    }

    protected void verify() {
        if (!getEmailAccount().preInsertExecuted) {
            throw new TestErrorException("Event hook failed. The pre insert method on " + getEmailAccount() + " failed to execute.");
        }

        if (!getEmailAccount().postInsertExecuted) {
            throw new TestErrorException("Event hook failed. The post insert method on " + getEmailAccount() + " failed to execute.");
        }

        if (!getEmailAccount().aboutToInsertExecuted) {
            throw new TestErrorException("Event hook failed. The about to insert method on " + getEmailAccount() + " failed to execute.");
        }

        if (!getPhoneNumber().preInsertExecuted) {
            throw new TestErrorException("Event hook failed. The pre insert method on " + getPhoneNumber() + " failed to execute.");
        }

        if (!getPhoneNumber().postInsertExecuted) {
            throw new TestErrorException("Event hook failed. The post insert method on " + getPhoneNumber() + " failed to execute.");
        }

        if (!getPhoneNumber().aboutToInsertExecuted) {
            throw new TestErrorException("Event hook failed. The about to insert method on " + getPhoneNumber() + " failed to execute.");
        }

        if (!getAddressListener().preInsertExecuted) {
            throw new TestErrorException("Event hook failed. The pre insert method on " + getAddressListener() + " failed to execute.");
        }

        if (!getAddressListener().postInsertExecuted) {
            throw new TestErrorException("Event hook failed. The post insert method on " + getAddressListener() + " failed to execute.");
        }

        if (!getAddressListener().aboutToInsertExecuted) {
            throw new TestErrorException("Event hook failed. The about to insert method on " + getAddressListener() + " failed to execute.");
        }
    }
}
