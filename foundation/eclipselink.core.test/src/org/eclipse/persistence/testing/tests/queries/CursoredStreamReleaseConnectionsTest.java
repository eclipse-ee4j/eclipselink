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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.Server;

/**
 * Test the cursored stream feature by performing a cursor read on the database
 * and comparing the contents to a normal query read.
    */
public class CursoredStreamReleaseConnectionsTest extends TestCase {
    protected int size;
    protected boolean useUOW;
    protected Server serverSession;
    protected ClientSession clientSession;

    public CursoredStreamReleaseConnectionsTest(boolean useUOW) {
        super();
        this.useUOW = useUOW;
        setDescription("This test verifies that the connections are released after executing a query using cursed stream");
        if (useUOW) {
            this.setName(getName() + " -UnitOfWork");
        } else {
            this.setName(getName() + " -ClientSession");
        }
    }

    public void setup() {
        org.eclipse.persistence.sessions.Project proj = new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject();
        proj.setDatasourceLogin(getSession().getDatasourceLogin().clone());
        serverSession = proj.createServerSession();
        serverSession.setSessionLog(getSession().getSessionLog());
        serverSession.login();
    }

    public void reset() {
        if (clientSession != null) {
            this.clientSession.release();
        }
        this.serverSession.logout();
    }

    public void test() {
        CursoredStream stream = null;

        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            ReportQuery query = new ReportQuery(Employee.class, builder);
            query.addAttribute("lastName");
            query.useCursoredStream();
            query.retrievePrimaryKeys();
            clientSession = serverSession.acquireClientSession();
            if (useUOW) {
                Session uow = clientSession.acquireUnitOfWork();
                stream = (CursoredStream)uow.executeQuery(query);
            } else {
                stream = (CursoredStream)clientSession.executeQuery(query);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * Verify if number of query objects matches number of cursor objects
     */
    public void verify() {
        int total = serverSession.getReadConnectionPool().getTotalNumberOfConnections();
        int totalAvailable = serverSession.getReadConnectionPool().getConnectionsAvailable().size();
        int totalUsedConnections = total - totalAvailable;

        if (totalUsedConnections != 0) {
            throw new TestErrorException("Not all of the connections were released.");
        }
    }
}
