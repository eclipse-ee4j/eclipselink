/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.distributedservers;

import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.tests.distributedservers.rcm.RCMDistributedServersModel;

public class DistributedSessionBrokerServersModel extends RCMDistributedServersModel {

    public DistributedSessionBrokerServersModel() {
        super();
        setDescription("This suite tests updating objects with changed parts for SessionBroker.");
    }

    boolean hasSetSessionBroker;

    /**
     * This method returns the system to the original state
     */
    public void reset() {
        super.reset();
        if (hasSetSessionBroker && getSession().isSessionBroker()) {
            getDatabaseSession().logout();
            DatabaseSession originalSession = (DatabaseSession)((SessionBroker)getSession()).getSessionsByName().values().iterator().next();
            originalSession.login();
            getExecutor().setSession(originalSession);
            hasSetSessionBroker = false;
        }
    }

    /**
     * This method sets up the distributed servers and the registry
     */
    public void setup() {
        if (!hasSetSessionBroker && !getSession().isSessionBroker()) {
            getDatabaseSession().logout();
            DatabaseSession originalSession = (DatabaseSession)getSession();
            SessionBroker broker = new SessionBroker();
            broker.registerSession(originalSession.getName(), originalSession);
            broker.setSessionLog(originalSession.getSessionLog());
            broker.login();
            getExecutor().setSession(broker);
            hasSetSessionBroker = true;
        }
        super.setup();
    }
}
