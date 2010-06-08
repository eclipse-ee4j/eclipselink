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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.sessions.server.ServerSession;


public class ConnectionSizeChangedAfterLogin extends ExceptionTest {
    public ConnectionSizeChangedAfterLogin() {
        super();
        setDescription("This test attempts to set the size of the read connection pool after logging in to the server session.");
    }

    protected void setup() {
        expectedException = ValidationException.cannotSetReadPoolSizeAfterLogin();
    }

    public void test() {
        ServerSession server = null;
        try {
            server = new ServerSession(getSession().getLogin());
            server.setLogLevel(getSession().getLogLevel());
            server.setLog(getSession().getLog());
            server.addConnectionPool("default", getSession().getLogin(), 3, 5);
            server.login();
            server.useReadConnectionPool(3, 3);
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        } finally {
            server.logout();
        }
    }
}
