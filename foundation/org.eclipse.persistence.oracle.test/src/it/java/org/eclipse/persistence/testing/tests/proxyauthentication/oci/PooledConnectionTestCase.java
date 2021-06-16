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
// Andrei Ilitchev May 28, 2008. Bug 224964: Provide support for Proxy Authentication through JPA.
//     Changed the was Proxy Authentication supported in case of thin driver, but support for oci case remains the same.
//     That caused re-arranging of the tests: before the fix all the tests were directly in proxiauthentication package;
//     now the old tests (minus thin-specific setup) were moved into the new proxyauthentication.oci package,
//     and the new tests defined in the new proxyauthentication.thin package.
package org.eclipse.persistence.testing.tests.proxyauthentication.oci;

import java.util.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.*;

// ProxyAuthentication_FS: 4.3.1.1.    Realizations of Main use case:
// "ServerSession uses main connection; each ClientSession uses its own proxy connection". (see 3.1.2.1)
// All we have to do is to make sure that proxy properties are found in the login,
// which is used to connect ClientSession's write connection.
// There are three alternative approaches to realize the use case.
//
// B.  ClientSession uses pooled connection - proxy properties set into Accessor's login.
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

