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
