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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.descriptors.partitioning.RoundRobinPartitioningPolicy;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.testing.framework.TestWarningException;

/**
 * This model is used to test the unit of work on an isolated client/server session.
 */
public class UnitOfWorkPartitionedIsolatedAlwaysTestModel extends UnitOfWorkIsolatedAlwaysTestModel {

    public void setup() {
        if (getSession().getPlatform().isSybase()) {
            throw new TestWarningException("This cannot be run on Sybase as it conflicts with its transaction isolation level.");
        }
        super.setup();
    }
    
    /**
     * Simulate a database cluster by having multiple pools to the same database.
     */
    public Server buildServerSession() {
        Server server = 
            getSession().getProject().clone().createServerSession(1, 3);
        server.addConnectionPool("node2", getSession().getLogin(), 1, 3);
        server.addConnectionPool("node3", getSession().getLogin(), 1, 3);
        server.setSessionLog(getSession().getSessionLog());
        server.setPartitioningPolicy(new RoundRobinPartitioningPolicy("default", "node2", "node3"));
        server.login();
        return server;
    }
    
}
