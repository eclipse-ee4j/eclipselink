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
package org.eclipse.persistence.testing.tests.distributedservers.rcm;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.platform.server.NoServerPlatform;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.sessions.coordination.TransportManager;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServer;


public class RCMDistributedServer extends DistributedServer {
    public RCMDistributedServer(DatabaseSession session) {
        super(session);
    }

    /**
     * This method starts the server and makes the dispatcher available
     * Creation date: (7/21/00 9:58:37 AM)
     */
    public void run() {

        RemoteCommandManager cm = new RemoteCommandManager((AbstractSession)session);
        // set propagate command synchronously for testing
        cm.setShouldPropagateAsynchronously(false);
        cm.getDiscoveryManager().setAnnouncementDelay(0);
        // ovewrite default to use RMI registry naming service  
        cm.getTransportManager().setNamingServiceType(TransportManager.REGISTRY_NAMING_SERVICE);
        // set full rmi URL of local host 
        cm.setUrl("rmi://localhost:1099");
        // turn on cache sync with RCM
        ((AbstractSession)session).setShouldPropagateChanges(true);
        cm.setServerPlatform(new NoServerPlatform(new DatabaseSessionImpl()));
        cm.initialize();

    }

    public void stopServer() {
        ((AbstractSession)this.session).getCommandManager().shutdown();
    }

}
