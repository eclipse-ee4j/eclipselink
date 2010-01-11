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

import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.server.ServerSession;


//Bug#3911318  Make sure the server session broker (or client session broker) returns the correct
//size of DefaultReadOnlyClasses
public class ReadOnlyClassesInSessionBrokerTest extends AutoVerifyTestCase {

    int vectorSize = 0;
    SessionBroker sessionBroker;

    public ReadOnlyClassesInSessionBrokerTest() {
        setDescription("The test for DefaultReadOnlyClasses size in session broker");
    }

    public void setup() {
        ServerSession serverSession;
        Project project = new org.eclipse.persistence.testing.models.readonly.ReadOnlyProject();
        DatabaseLogin login = project.getLogin();
        login.usePlatform(new OraclePlatform());
        login.setDriverClassName("oracle.jdbc.OracleDriver");
        login.setConnectionString("jdbc:oracle:thin:@tlsvrdb7.ca.oracle.com:1521:toplink");
        login.setUserName("QA6");
        login.setPassword("password");

        serverSession = (ServerSession)project.createServerSession();
        this.sessionBroker = new SessionBroker();
        this.sessionBroker.registerSession("broker1", serverSession);
        this.sessionBroker.login();
    }

    public void test() {
        for (int i = 0; i < 10; i++) {
            vectorSize = getDefaultReadOnlyClassSize();
        }
    }

    public int getDefaultReadOnlyClassSize() {
        int size = 0;
        SessionBroker cSessionBroker = sessionBroker.acquireClientSessionBroker();
        size = sessionBroker.getDefaultReadOnlyClasses().size();
        cSessionBroker.release();

        return size;
    }

    public void verify() {
        if (vectorSize < 2 || vectorSize > 2) {
            throw new TestErrorException("DefaultReadOnlyClasses size should be 2, but " + vectorSize + 
                                         " is returned");
        }
    }

    public void reset() {
        this.sessionBroker.logout();
    }

}
