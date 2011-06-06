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
 * Andrei Ilitchev May 28, 2008. Bug 224964: Provide support for Proxy Authentication through JPA.
 *	   Changed the was Proxy Authentication supported in case of thin driver, but support for oci case remains the same.
 *     That caused re-arranging of the tests: before the fix all the tests were directly in proxiauthentication package;
 *     now the old tests (minus thin-specific setup) were moved into the new proxyauthentication.oci package,
 *     and the new tests defined in the new proxyauthentication.thin package.
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.proxyauthentication.oci;

import java.sql.SQLException;

import oracle.jdbc.pool.OracleOCIConnectionPool;

import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.platform.database.oracle.OracleOCIProxyConnector;

public class OCIProxyTestHelper extends ProxyTestHelper {
    OracleOCIConnectionPool ociPool;

    public OCIProxyTestHelper() {
        PROXYTYPE_USER_NAME = OracleOCIConnectionPool.PROXYTYPE_USER_NAME;
        PROXY_USER_NAME = OracleOCIConnectionPool.PROXY_USER_NAME;
        //        PROXY_USER_PASSWORD = OracleOCIConnectionPool.PROXY_PASSWORD;
    }

    public JNDIConnector createProxyConnector(Session session) throws SQLException {
        // Note that OCI client is required.
        // Logical name of the pool. This needs to be one in the tnsnames.ora configuration file.
        //        String ociUrl = "jdbc:oracle:oci:@ORCL.CA.ORACLE.COM";
        String ociUrl = session.getLogin().getConnectionString();
        String user = session.getLogin().getUserName();
        String password = session.getLogin().getPassword();
        oracleDataSource = new OracleOCIConnectionPool(user, password, ociUrl, null);

        OracleOCIProxyConnector connector = new OracleOCIProxyConnector();

        return connector;
    }
}
