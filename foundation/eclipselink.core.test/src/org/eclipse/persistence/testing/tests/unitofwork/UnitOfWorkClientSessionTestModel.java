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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.models.inheritance.InheritanceSystem;
import org.eclipse.persistence.testing.models.insurance.InsuranceSystem;
import org.eclipse.persistence.testing.models.mapping.MappingSystem;
import org.eclipse.persistence.testing.models.ownership.OwnershipSystem;
import org.eclipse.persistence.testing.models.transparentindirection.IndirectListSystem;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.Server;


/**
 * This model is used to test the unit of work on a client/server session.
 */
public class UnitOfWorkClientSessionTestModel extends org.eclipse.persistence.testing.framework.TestModel {
    public Session originalSession;

    public void addRequiredSystems() {
        // H2 has locking issue with multiple connections.
        if (getSession().getPlatform().isH2()) {
            throw new TestWarningException("H2 has locking issue with multiple connections");
        }
        addRequiredSystem(new OwnershipSystem());
        addRequiredSystem(new IndirectListSystem());
        addRequiredSystem(new EmployeeSystem());
        addRequiredSystem(new InsuranceSystem());
        addRequiredSystem(new MappingSystem());
        addRequiredSystem(new UOWSystem());
        addRequiredSystem(new InheritanceSystem());
    }

    public void addTests() {
        addTest(new UnitOfWorkTestSuite());
        // bug 3128227
        addTest(new UnitOfWorkRollbackConnectionReleaseTest());
    }

    public Server buildServerSession() {
        Server server = 
            ((org.eclipse.persistence.sessions.Project)getSession().getProject().clone()).createServerSession(1, 1);
        server.useReadConnectionPool(1, 1);
        server.setSessionLog(getSession().getSessionLog());
        server.login();
        return server;
    }

    public void reset() {
        // Setup might not be run yet.
        if (originalSession != null) {
            ((ClientSession)getSession()).getParent().logout();
            getExecutor().setSession(originalSession);
        }
    }

    public void setup() {
        this.originalSession = getSession();
        Session client = buildServerSession().acquireClientSession();
        client.setSessionLog(this.originalSession.getSessionLog());

        getExecutor().setSession(client);
    }
}
