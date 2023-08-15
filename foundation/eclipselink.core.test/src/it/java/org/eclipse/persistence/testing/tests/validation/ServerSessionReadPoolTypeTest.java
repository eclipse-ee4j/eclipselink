/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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

    @Override
    public void setup() {
        DatabaseLogin login = (DatabaseLogin)getSession().getLogin().clone();
        serverSession = new ServerSession(login, 1, 1);
        serverSession.setSessionLog(getSession().getSessionLog());
        serverSession.login();
    }

    @Override
    public void verify() {
        Class<? extends ConnectionPool> readPoolClass = serverSession.getReadConnectionPool().getClass();
        String readPoolClassName = readPoolClass.getSimpleName();
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

    @Override
    public void reset() {
        if (serverSession != null) {
            serverSession.logout();
            serverSession = null;
        }
    }
}
