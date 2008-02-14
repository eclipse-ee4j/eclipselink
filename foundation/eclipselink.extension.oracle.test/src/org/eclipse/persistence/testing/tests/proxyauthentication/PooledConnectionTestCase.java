/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.proxyauthentication;

import java.util.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.*;

// ProxyAuthentication_FS: 4.3.1.1.	Realizations of Main use case:
// "ServerSession uses main connection; each ClientSession uses its own proxy connection". (see 3.1.2.1)
// All we have to do is to make sure that proxy properties are found in the login,
// which is used to connect ClientSession's write connection. 
// There are three alternative approaches to realize the use case.
//
// B.  ClientSession uses pooled connection – proxy properties set into Accessor's login.
//
// Proxy properties are set into writeAccessor's login.
// Caching of ClientSessions is required because postAcquireConnection event
// doesn't contain ClientSession - only parent serverSession
public class PooledConnectionTestCase extends ProxyAuthenticationConnectionTestCase {

    class Listener extends SessionEventAdapter {
        HashSet clientSessions = new HashSet();

        public void postAcquireClientSession(SessionEvent event) {
            clientSessions.add(event.getSession());
        }

        public void postAcquireConnection(SessionEvent event) {
            org.eclipse.persistence.internal.databaseaccess.DatasourceAccessor dsAccessor = (org.eclipse.persistence.internal.databaseaccess.DatasourceAccessor)event.getResult();
            Iterator it = clientSessions.iterator();
            while (it.hasNext()) {
                ClientSession cs = (ClientSession)it.next();
                if (dsAccessor == cs.getWriteConnection()) {
                    addProxyPropertiesToLogin(dsAccessor.getLogin());
                    break;
                }
            }
        }

        public void preReleaseClientSession(SessionEvent event) {
            clientSessions.remove(event.getSession());
        }

    }

    public PooledConnectionTestCase(Properties proxyProperties) {
        super(proxyProperties);
    }

    protected void proxySetup() {
        listener = new Listener();
    }

}

