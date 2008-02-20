/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.proxyauthentication;

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
