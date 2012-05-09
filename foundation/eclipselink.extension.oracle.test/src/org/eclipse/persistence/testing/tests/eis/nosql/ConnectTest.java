/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.eis.nosql;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.eis.*;
import org.eclipse.persistence.nosql.adapters.nosql.OracleNoSQLConnectionSpec;
import org.eclipse.persistence.nosql.adapters.nosql.OracleNoSQLPlatform;
import org.eclipse.persistence.testing.framework.TestCase;

/**
 * Simple connect test. This tests connecting TopLink EIS to the Oracle NoSQL database.
 */
public class ConnectTest extends TestCase {
    public ConnectTest() {
        setName("ConnectTest");
        setDescription("This tests connecting TopLink EIS to the Oracle NoSQL database.");
    }

    public void test() throws Exception {
        testDatabaseSession();
        testServerSession();
    }

    public void testDatabaseSession() throws Exception {
        EISLogin login = new EISLogin(new OracleNoSQLPlatform());
        OracleNoSQLConnectionSpec spec = new OracleNoSQLConnectionSpec();
        login.setConnectionSpec(spec);

        login.setProperty(OracleNoSQLConnectionSpec.STORE, "kvstore");
        login.setProperty(OracleNoSQLConnectionSpec.HOST, "localhost:5000");

        DatabaseSession session = new Project(login).createDatabaseSession();
        session.setSessionLog(getSession().getSessionLog());
        session.login();
        session.logout();
    }

    public void testServerSession() throws Exception {
        EISLogin login = new EISLogin(new OracleNoSQLPlatform());
        OracleNoSQLConnectionSpec spec = new OracleNoSQLConnectionSpec();
        login.setConnectionSpec(spec);

        login.setProperty(OracleNoSQLConnectionSpec.STORE, "kvstore");
        login.setProperty(OracleNoSQLConnectionSpec.HOST, "localhost:5000");

        Server session = new Project(login).createServerSession();
        session.setSessionLog(getSession().getSessionLog());
        session.login();
        session.logout();
    }
}
