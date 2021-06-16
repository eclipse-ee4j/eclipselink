/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
