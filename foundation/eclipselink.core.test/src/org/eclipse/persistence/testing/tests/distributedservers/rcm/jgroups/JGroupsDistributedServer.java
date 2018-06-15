/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
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
