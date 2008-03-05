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

import oracle.jdbc.OracleConnection;
import oracle.jdbc.pool.OracleDataSource;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.platform.database.oracle.OracleJDBC10_1_0_2ProxyConnector;

import org.eclipse.persistence.testing.framework.*;

public class JDBC10_1_0_2ProxyTestHelper extends ProxyTestHelper {
    public JDBC10_1_0_2ProxyTestHelper() {
        PROXYTYPE_USER_NAME = Integer.toString(OracleConnection.PROXYTYPE_USER_NAME);
        PROXY_USER_NAME = OracleConnection.PROXY_USER_NAME;
        //        PROXY_USER_PASSWORD = OracleConnection.PROXY_USER_PASSWORD;
    }

    public JNDIConnector createProxyConnector(Session session) throws SQLException {

        oracleDataSource = new OracleDataSource();
        try {
            oracleDataSource.setURL(((AbstractSession)session).getAccessor().getConnection().getMetaData().getURL());
        } catch (java.sql.SQLException se) {
            se.printStackTrace();
            throw new TestErrorException("There is SQLException");
        }

        OracleJDBC10_1_0_2ProxyConnector connector = new OracleJDBC10_1_0_2ProxyConnector();

        return connector;
    }
}
