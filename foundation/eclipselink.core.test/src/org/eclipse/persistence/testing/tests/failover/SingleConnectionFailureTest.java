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
 package org.eclipse.persistence.testing.tests.failover;

import java.util.Vector;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.tests.failover.emulateddriver.EmulatedConnection;
import org.eclipse.persistence.testing.tests.failover.emulateddriver.EmulatedDriver;

public class SingleConnectionFailureTest extends TestCase {
    
    protected DatabaseSession databaseSession ;

    protected void setup() {
        if (getSession().getPlatform().isSymfoware()) {
            throwWarning("Test SingleConnectionFailureTest is not supported on Symfoware, "
                    + "failover has not been implemented on this platform.");
        }
        Project project = (Project)getSession().getProject().clone();
        DatabaseLogin login = (DatabaseLogin)project.getLogin().clone();
        login.useDirectDriverConnect();
        login.setDriverClass(EmulatedDriver.class);
        login.setConnectionString("jdbc:emulateddriver");
        project.setLogin(login);
        this.databaseSession = project.createDatabaseSession();
        databaseSession.setSessionLog(getSession().getSessionLog());
        databaseSession.login();

        String sql = getSession().getPlatform().getPingSQL();
        Vector rows = getSession().executeSQL(sql);
        //this will actually store the results on the driver for subsequent connections.
        ((EmulatedConnection)((DatabaseSessionImpl)databaseSession).getAccessor().getConnection()).putRows(sql, rows);

        ReadObjectQuery query = new ReadObjectQuery(Address.class);
        getSession().executeQuery(query);
        sql = query.getSQLString();
        rows = getSession().executeSQL(sql);
        ((EmulatedConnection)((DatabaseSessionImpl)databaseSession).getAccessor().getConnection()).putRows(sql, rows);

        ((EmulatedConnection)((DatabaseSessionImpl)databaseSession).getAccessor().getConnection()).causeCommError();
    }

    protected void test() {
        try{
            this.databaseSession.readObject(Address.class);
        }catch (DatabaseException ex){
            throw new TestErrorException("Should have reconnected and not thrown exception.");
        }
    }

    public void reset() {
        if(this.databaseSession != null) {
            if(this.databaseSession.isConnected()) {
                this.databaseSession.logout();
            }
            this.databaseSession = null;
        }
    }
}
