/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.transaction.JTATransactionController;

public class ClientSessionConstructorValidationTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    private ServerSession server;
    private ClientSession client;

    public ClientSessionConstructorValidationTest() {
        super();
        setDescription("Verifies that the Client Session is constructed properly");
    }

    public void test() {
        this.server = new ServerSession(getSession().getLogin(), 1, 1);
        this.server.setExternalTransactionController(new JTATransactionController());
        this.server.login();
        this.client = this.server.acquireClientSession();
    }

    public void verify() {
        if (this.server.getExternalTransactionController().equals(this.client.getExternalTransactionController())) {
        } else {
            throw new TestErrorException("ExternalTransactionController not copied from parent session to client session");
        }
    }

    public void reset() {
        if(this.server != null) {
            this.server.logout();
            this.server = null;
        }
    }
}
