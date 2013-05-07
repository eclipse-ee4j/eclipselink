/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.distributedservers.rcm.jgroups;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.tests.distributedservers.rcm.RCMDistributedServer;


public class JGroupsDistributedServer extends RCMDistributedServer {
    public JGroupsDistributedServer(DatabaseSession session) {
        super(session);
    }

    /**
     * This method starts the server and makes the dispatcher available
     */
    @Override
    public void run() {
        JGroupsSetupHelper.getHelper().startCacheSynchronization((AbstractSession)session, false);
    }

    @Override
    public void stopServer() {
        JGroupsSetupHelper.getHelper().stopCacheSynchronization((AbstractSession)session);
    }
}
