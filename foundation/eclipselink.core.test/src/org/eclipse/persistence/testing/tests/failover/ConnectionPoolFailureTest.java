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
 package org.eclipse.persistence.testing.tests.failover;

import java.util.List;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.tests.failover.emulateddriver.EmulatedConnection;
import org.eclipse.persistence.testing.tests.failover.emulateddriver.EmulatedDriver;

/**
 * This test will test TopLink Failover support within a TopLink Connection pool.
 * It is expected that upon a connection failure TopLink will remove all broken connections
 * From the TopLink Pool and then reattempt connection to the datasource.
 * 
 * @author gyorke
 *
 */public class ConnectionPoolFailureTest extends TestCase
{
    protected ServerSession session;

    public ConnectionPoolFailureTest()
    {
    }

    protected void setup()
    {
        if (getSession().getPlatform().isSymfoware()) {
            throwWarning("Test ConnectionPoolFailureTest is not supported on Symfoware, "
                    + "failover has not been implemented on this platform. (bug 304908)");
        }
        Project project = (Project)getSession().getProject().clone();
        DatabaseLogin login = (DatabaseLogin)project.getLogin().clone();
        login.useDirectDriverConnect();
        login.setDriverClass(EmulatedDriver.class);
        login.setConnectionString("jdbc:emulateddriver");
        project.setLogin(login);
        session = (ServerSession)project.createServerSession();
        session.setSessionLog(getSession().getSessionLog());
        session.login();
        String sql = getSession().getPlatform().getPingSQL();
        java.util.Vector rows = getSession().executeSQL(sql);
        ((EmulatedConnection)session.getAccessor().getConnection()).putRows(sql, rows);
        ReadObjectQuery query = new ReadObjectQuery(Address.class);
        getSession().executeQuery(query);
        sql = query.getSQLString();
        rows = getSession().executeSQL(sql);
        ((EmulatedConnection)session.getAccessor().getConnection()).putRows(sql, rows);
        List list = session.getReadConnectionPool().getConnectionsAvailable();
        for(int i = 0; i < list.size(); i++)
        {
            ((EmulatedConnection)((DatabaseAccessor)list.get(i)).getConnection()).causeCommError();
        }

    }

    protected void test()
    {
        try
        {
            session.acquireClientSession().readObject(Address.class);
        }
        catch(DatabaseException ex)
        {
            throw new TestErrorException("Should have reconnected an not thrown exception.");
        }
    }
    
    public void reset()
    {
        if(session != null) {
            try {
                session.logout();
            } finally {
                session = null;
            }
            
        }
    }
}
