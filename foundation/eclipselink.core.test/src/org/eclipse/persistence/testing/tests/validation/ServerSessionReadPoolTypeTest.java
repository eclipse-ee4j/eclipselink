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

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.server.ConnectionPool;
import org.eclipse.persistence.sessions.server.ExternalConnectionPool;
import org.eclipse.persistence.sessions.server.ServerSession;


public class ServerSessionReadPoolTypeTest extends AutoVerifyTestCase {
    protected ServerSession serverSession;

    public ServerSessionReadPoolTypeTest() {
        super();
        setDescription("This test validates the type of ServerSession's readConnectionPool");
    }

    public void setup() {
        DatabaseLogin login = (DatabaseLogin)getSession().getLogin().clone();
        serverSession = new ServerSession(login, 1, 1);
        serverSession.setSessionLog(getSession().getSessionLog());
        serverSession.login();
    }

    public void verify() {
        Class readPoolClass = serverSession.getReadConnectionPool().getClass();
        String readPoolClassName = Helper.getShortClassName(readPoolClass);
        if (serverSession.getLogin().shouldUseExternalConnectionPooling()) {
            if (!readPoolClass.equals(ExternalConnectionPool.class)) {
                throw new TestErrorException("In case external connection pooling is used, readConnectionPool by default should be an instance of ExternalConnectionPool class, not " + readPoolClassName);
            }
        } else {
            if (!readPoolClass.equals(ConnectionPool.class)) {
                throw new TestErrorException("In case external connection pooling is NOT used, readConnectionPool by default should be an instance of ConnectionPool class, not " + readPoolClassName);
            }
        }
    }

    public void reset() {
        if (serverSession != null) {
            serverSession.logout();
            serverSession = null;
        }
    }
}
