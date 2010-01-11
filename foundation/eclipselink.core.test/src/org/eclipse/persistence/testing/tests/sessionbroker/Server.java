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

import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.testing.framework.OracleDBPlatformHelper;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.sessions.server.ServerSession;


public class Server {
    public SessionBroker sSessionBroker;
    public boolean errorOccured;

    public Server() {
        this.sSessionBroker = buildServerBroker();

    }

    public static SessionBroker buildServerBroker() {
        SessionBroker sSessionBroker = new SessionBroker();

        ServerSession ssession1 = new ServerSession(getLogin1());
        ServerSession ssession2 = new ServerSession(getLogin2());
        ssession1.useExclusiveReadConnectionPool(5, 5);
        ssession2.useExclusiveReadConnectionPool(5, 5);

        ssession1.addDescriptors(new EmployeeProject1());
        ssession2.addDescriptors(new EmployeeProject2());

        sSessionBroker.registerSession("broker1", ssession1);
        sSessionBroker.registerSession("broker2", ssession2);

        // Set session for join table.
        ((ManyToManyMapping)ssession1.getDescriptor(Employee.class).getMappingForAttributeName("projects")).setSessionName("broker2");
        // Disable delete verify.
        ((OneToOneMapping)ssession1.getDescriptor(Employee.class).getMappingForAttributeName("address")).setShouldVerifyDelete(false);

        return sSessionBroker;
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

    public static DatabaseLogin getLogin2() {
        //Oracle 11.1
        DatabaseLogin login = new DatabaseLogin();
        try {
            login.usePlatform(OracleDBPlatformHelper.getInstance().getOracle9Platform());
        } catch (Exception e) {
        }
        login.useOracleThinJDBCDriver();
        login.setDatabaseURL("tlsvrdb7.ca.oracle.com:1521:toplink");
        login.setUserName("QA8");
        login.setPassword("password");
        login.useNativeSequencing();
        login.getDefaultSequence().setPreallocationSize(1);

        return login;
    }

    public void login() {
        this.sSessionBroker.login();
    }

    public void logout() {
        this.sSessionBroker.logout();
    }
}
