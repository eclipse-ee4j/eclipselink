/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
// Andrei Ilitchev May 28, 2008. Bug 224964: Provide support for Proxy Authentication through JPA.
//     Changed the was Proxy Authentication supported in case of thin driver, but support for oci case remains the same.
//     That caused re-arranging of the tests: before the fix all the tests were directly in proxiauthentication package;
//     now the old tests (minus thin-specific setup) were moved into the new proxyauthentication.oci package,
//     and the new tests defined in the new proxyauthentication.thin package.
package org.eclipse.persistence.testing.tests.proxyauthentication.oci;

import java.sql.SQLException;

import java.util.Properties;

import oracle.jdbc.pool.OracleDataSource;

import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;

public abstract class ProxyTestHelper {
    public String PROXYTYPE_USER_NAME;
    public String PROXY_USER_NAME;
    //    public String PROXY_USER_PASSWORD;
    protected OracleDataSource oracleDataSource;

    protected abstract JNDIConnector createProxyConnector(Session session) throws SQLException;

    public void setProxyConnectorIntoLogin(DatabaseLogin login, Session session) throws SQLException {
        if (oracleDataSource != null) {
            return;
        }
        JNDIConnector connector = createProxyConnector(session);
        connector.setDataSource(oracleDataSource);
        login.setConnector(connector);
        login.setUsesExternalConnectionPooling(true);
    }

    public void close() throws SQLException {
        if (oracleDataSource != null) {
            oracleDataSource.close();
            oracleDataSource = null;
        }
    }

    public Properties createProxyProperties(String proxyUser) {
        Properties prop = new Properties();
        prop.setProperty("proxytype", PROXYTYPE_USER_NAME);
        prop.setProperty(PROXY_USER_NAME, proxyUser);
        //        prop.setProperty(PROXY_USER_PASSWORD, proxyPassword);
        return prop;
    }
}
