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
 * Andrei Ilitchev May 28, 2008. Bug 224964: Provide support for Proxy Authentication through JPA.
 *	   Changed the was Proxy Authentication supported in case of thin driver, but support for oci case remains the same.
 *     That caused re-arranging of the tests: before the fix all the tests were directly in proxiauthentication package;
 *     now the old tests (minus thin-specific setup) were moved into the new proxyauthentication.oci package,
 *     and the new tests defined in the new proxyauthentication.thin package.
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.proxyauthentication.oci;

import java.util.Properties;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.*;

// ProxyAuthentication_FS: 4.3.1.1.	Realizations of Main use case:
// "ServerSession uses main connection; each ClientSession uses its own proxy connection". (see 3.1.2.1)
// All we have to do is to make sure that proxy properties are found in the login,
// which is used to connect ClientSession's write connection. 
// There are three alternative approaches to realize the use case.
//
// A. Client Session uses non-pooled connection – proxy properties set on its login.
//
// Proxy properties could be set either before ClientSession is created (useEvent=false);
// or in postAcquireClientSession event (useEvent=true).
public class NonPooledConnectionTestCase extends ProxyAuthenticationConnectionTestCase {

    boolean useEvent;

    public NonPooledConnectionTestCase(Properties proxyProperties, boolean useEvent) {
        super(proxyProperties);
        this.useEvent = useEvent;
        String suffix;
        if (useEvent) {
            suffix = " proxy setup in Event";
        } else {
            suffix = " proxy setup before ClientSession created";
        }
        setName(getName() + suffix);
    }

    protected void proxySetup() {
        if (useEvent) {
            // Use this approach if ClientSession created with default connection policy:
            // cs = serverSession.acquireClientSession();
            listener = new SessionEventAdapter() {
                        public void postAcquireClientSession(SessionEvent event) {
                            ClientSession cs = (ClientSession)event.getSession();
                            ConnectionPolicy policy = cs.getConnectionPolicy(); //.clone();
                            cs.setConnectionPolicy(policy);
                            addProxyPropertiesToLogin(policy.getLogin());
                            // the following alters ConnectionPolicy and ClientSession
                            // so that the setup becomes identical to the one used in useEvent==false case.
                            policy.setIsLazy(false);
                            policy.setPoolName(null);
                            cs.setLogin(policy.getLogin());
                        }
                    };
        } else {
            // The most natural way of realizing this use case.
            // The custom connection policy is used to create ClientSession:
            // cs = serverSession.acquireClientSession(connectionPolicy);
            // Need clone because login will be altered (proxy properties will be added)
            Login login = getServerSession().getLogin().clone();
            addProxyPropertiesToLogin(login);
            connectionPolicy = new ConnectionPolicy(login);
        }
    }

}

