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
package org.eclipse.persistence.testing.tests.eis.aq;

import java.sql.Connection;
import java.sql.DriverManager;
import oracle.AQ.*;
import org.eclipse.persistence.testing.framework.TestCase;

/**
 * Test conecting directly through the AQ Java driver. Requires AQ installed on this database and
 * an aquser defined.
 */
public class JavaDirectConnectTest extends TestCase {
    protected Connection connection;

    public JavaDirectConnectTest() {
        this.setDescription("Test conecting directly through the AQ Java driver");
    }

    public void test() throws Exception {
        AQSession session = connect();
        session.close();
        connection.close();
    }

    public AQSession connect() throws Exception {
        String url = (String)getSession().getDatasourceLogin().getProperty("url");
        connection = DriverManager.getConnection(url, "aquser", "aquser");
        connection.setAutoCommit(false);
        Class.forName("oracle.AQ.AQOracleDriver");
        AQSession session = AQDriverManager.createAQSession(connection);

        return session;
    }
}
