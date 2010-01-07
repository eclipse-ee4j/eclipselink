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

import java.util.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.*;

// ProxyAuthentication_FS: 4.3.1.2.	Realization of "DatabaseSession uses proxy connection" use case (see 3.1.2.2).
// In preLoginEvent user set proxy properties into DatabaseSession's (or ServerSession's) login.
public class MainLoginTestCase extends ProxyAuthenticationConnectionTestCase {

    Properties originalWriteProperties;
    Properties originalReadProperties;

    public MainLoginTestCase(Properties proxyProperties) {
        super(proxyProperties);
    }

    protected void proxySetup() {
        originalWriteProperties = (Properties)getServerSession().getLogin().getProperties().clone();
        originalReadProperties = (Properties)((DatabaseLogin)getServerSession().getReadConnectionPool().getLogin()).getProperties().clone();
        // To handle preLogin event relog the session
        getServerSession().logout();
        listener = new SessionEventAdapter() {
                    public void preLogin(SessionEvent event) {
                        ServerSession ss = (ServerSession)event.getSession();
                        addProxyPropertiesToLogin(ss.getLogin());
                        addProxyPropertiesToLogin(ss.getReadConnectionPool().getLogin());
                    }
                };
        getServerSession().getEventManager().addListener(listener);
        getServerSession().login();
        // Because getListeners() is a Vector listener will end up being added twice -
        // once here, once in the superclass setup method.
        // The (ugly) remedy - remove it twice, too - once here once in superclass reset method.
        getServerSession().getEventManager().getListeners().remove(listener);
    }

    public void reset() {
        super.reset();
        // restore the original properties
        getServerSession().logout();
        getServerSession().getLogin().setProperties(originalWriteProperties);
        getServerSession().getReadConnectionPool().getLogin().setProperties(originalReadProperties);
        getServerSession().login();
    }

    // both read and write connections are expected to use proxy user

    protected String readConnectionSchemaExpected() {
        return writeConnectionSchemaExpected();
    }
}
