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
package org.eclipse.persistence.testing.tests.feature;

import java.util.*;
import javax.sql.*;
import javax.naming.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * test the JNDIConnector
 */
public class JNDIConnectionTest extends AutoVerifyTestCase {
    Connector connector;

    public JNDIConnectionTest() {
        setDescription("Connect to the database using the JNDIConnector");
    }

    public void reset() {
        ((org.eclipse.persistence.sessions.DatabaseSession)getSession()).logout();
        getSession().getLogin().setConnector(connector);
        ((org.eclipse.persistence.sessions.DatabaseSession)getSession()).login();
    }

    protected void setup() {
        ((org.eclipse.persistence.sessions.DatabaseSession)getSession()).logout();

        DatabaseLogin login = getSession().getLogin();
        connector = login.getConnector();// save the connector to restore later

        String dataSourceName = "JNDI test DataSource";
        DataSource dataSource = new TestDataSource(login.getDriverClassName(), login.getConnectionString(), (Properties)login.getProperties().clone());
        Context context;
        try {
            context = new TestContext(dataSourceName, dataSource);
        } catch (NamingException e) {
            throw new RuntimeException("JNDI problem");
        }

        login.setConnector(new JNDIConnector(context, dataSourceName));
    }

    public void test() {
        ((org.eclipse.persistence.sessions.DatabaseSession)getSession()).login();
    }

    protected void verify() {
        if (!getSession().isConnected()) {
            throw new TestErrorException("Session not connected via JNDI-supplied DataSource.");
        }
    }
}
