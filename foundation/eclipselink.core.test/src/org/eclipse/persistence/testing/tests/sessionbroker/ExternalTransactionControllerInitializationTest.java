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
package org.eclipse.persistence.testing.tests.sessionbroker;

import org.eclipse.persistence.platform.server.CustomServerPlatform;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.OracleDBPlatformHelper;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.transaction.JTATransactionController;


/**
 * Bug 3848021
 * Ensure SessionBroker.login() causes the external transaction controller to initialize
 */
public class ExternalTransactionControllerInitializationTest extends AutoVerifyTestCase {

    protected SessionBroker broker = null;

    public ExternalTransactionControllerInitializationTest() {
        setDescription("Ensure external transaction controllers are initialized at login time for SessionBrokers.");
    }

    public static DatabaseLogin getLogin1() {
        //Oracle 11.1
        DatabaseLogin login = new DatabaseLogin();
        try {
            login.usePlatform(OracleDBPlatformHelper.getInstance().getOracle9Platform());
        } catch (Exception e) {
        }
        login.useOracleThinJDBCDriver();
        login.setDatabaseURL("tlsvrdb7.ca.oracle.com:1521:toplink");
        login.setUserName("QA7");
        login.setPassword("password");
        login.useNativeSequencing();
        login.getDefaultSequence().setPreallocationSize(1);

        return login;
    }

    public void test() {
        broker = new SessionBroker();
        CustomServerPlatform platform = new CustomServerPlatform(broker);
        platform.setExternalTransactionControllerClass(JTATransactionController.class);
        broker.setServerPlatform(platform);
        broker.setSessionLog(getSession().getSessionLog());

        broker.login();
    }

    public void verify() {
        if (!broker.hasExternalTransactionController()) {
            throw new TestErrorException("SessionBroker's external transaction controller was not initialized on login.");
        }
    }

    public void reset() {
        if (broker != null) {
            broker.logout();
        }
    }
}

