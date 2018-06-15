/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.junit.failover;

import java.io.OutputStreamWriter;
import java.util.Vector;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.ArrayRecord;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.tests.junit.failover.emulateddriver.EmulatedConnection;
import org.eclipse.persistence.testing.tests.junit.failover.emulateddriver.EmulatedDriver;
import org.junit.After;
import org.junit.Before;

public abstract class FailoverBase<T extends DatabaseSession> {

    private T session;

    @Before
    public void prepare() {
        DatabaseLogin login = new DatabaseLogin();
        login.useDirectDriverConnect();
        login.setDriverClass(EmulatedDriver.class);
        login.setConnectionString("jdbc:emulateddriver");
        login.getPlatform().setPingSQL("SELECT 1");
        Project p = new Project(login);
        ClassDescriptor cd = Address.descriptor();
        p.addDescriptor(cd);
        session = createSession(p);
        SessionLog log = new DefaultSessionLog(new OutputStreamWriter(System.out));
        int logLevel = AbstractSessionLog.translateStringToLoggingLevel(System.getProperty(PersistenceUnitProperties.LOGGING_LEVEL, "INFO"));
        session.setSessionLog(log);
        session.setLogLevel(logLevel);
        session.login();

        // this will actually store the results on the driver for subsequent connections.
        EmulatedConnection con = (EmulatedConnection) ((DatabaseSessionImpl) session).getAccessor().getConnection();
        Vector<DatabaseField> pingFields = new Vector<DatabaseField>() {{ add(new DatabaseField("1"));}};
        con.putRows("SELECT 1", new Vector() {{ add(new ArrayRecord(pingFields,pingFields.toArray(new DatabaseField[0]), new Object[] { "1" })); }});
        con.putRows(Address.getSQL(), Address.getData(cd));
    }
    
    protected abstract T createSession(Project p);

    protected T getEmulatedSession() {
        return session;
    }
    
    @After
    public void reset() {
        if (session != null && session.isConnected()) {
            try {
                session.logout();
            } finally {
                session = null;
            }
        }
    }


}
