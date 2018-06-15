/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.distributedservers.rcm.jms;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.tests.distributedservers.rcm.RCMDistributedServer;


public class JMSRCMDistributedServer extends RCMDistributedServer {
    public JMSRCMDistributedServer(DatabaseSession session) {
        super(session);
    }

    /**
     * This method starts the server and makes the dispatcher available
     */
    public void run() {
        JMSSetupHelper.getHelper().startCacheSynchronization((org.eclipse.persistence.internal.sessions.AbstractSession)session, false);
    }

    public void stopServer() {
        JMSSetupHelper.getHelper().stopCacheSynchronization((org.eclipse.persistence.internal.sessions.AbstractSession)session);
    }
}
