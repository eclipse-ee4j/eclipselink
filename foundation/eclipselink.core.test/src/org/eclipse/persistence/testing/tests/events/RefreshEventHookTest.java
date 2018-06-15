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

public class RefreshEventHookTest extends EventHookTestCase {
    public void setup() {
        super.setup();
        getDatabaseSession().insertObject(getEmailAccount());
        getDatabaseSession().insertObject(getPhoneNumber());
        getDatabaseSession().insertObject(getAddress());
    }

    protected void test() {
        getSession().refreshObject(getEmailAccount());
        getSession().refreshObject(getPhoneNumber());
        getSession().refreshObject(getAddress());
    }

    protected void verify() {
        if (!getEmailAccount().postRefreshExecuted) {
            throw new TestErrorException("Event hook failed. The post refresh method on " + getEmailAccount() + " failed to execute.");
        }

        if (!getPhoneNumber().postRefreshExecuted) {
            throw new TestErrorException("Event hook failed. The post refresh method on " + getPhoneNumber() + " failed to execute.");
        }

        if (!getAddressListener().postRefreshExecuted) {
            throw new TestErrorException("Event hook failed. The post refresh method on " + getAddressListener() + " failed to execute.");
        }
    }
}
