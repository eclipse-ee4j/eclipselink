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
