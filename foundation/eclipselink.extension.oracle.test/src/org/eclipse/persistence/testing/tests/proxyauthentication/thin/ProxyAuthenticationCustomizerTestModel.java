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
 *   05/28/2008-1.0M8 Andrei Ilitchev. 
 *     - New file introduced for bug 224964: Provide support for Proxy Authentication through JPA.
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.proxyauthentication.thin;

import java.util.Map;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.*;

/**
 * TestModel for Proxy Authentication using Oracle thin driver (10.1.0.2 or later).
 * To run this model several users should be setup in the Oracle database,
 * see comment in ProxyAuthenticationUsersAndProperties.
 */
public class ProxyAuthenticationCustomizerTestModel extends TestModel {
    public ProxyAuthenticationCustomizerTestModel() {
        super();
    }
    
    static String getProperty(String property, String defaultValue) {
        String propertyValue = System.getProperty(property);
        
        if (propertyValue == null || propertyValue.equals("")) {
            return defaultValue;
        } else {
            return propertyValue;
        }
    }
    
    public void setup() {
        if (!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("Supports Oracle platform only");
        }
        // sets up all user names and properties used by the tests.
        ProxyAuthenticationUsersAndProperties.initialize();
        // verifies that all the users correctly setup in the db.
        String errorMsg = ProxyAuthenticationUsersAndProperties.verify((DatabaseSession)getSession());
        if(errorMsg.length() > 0) {
            throw new TestProblemException(errorMsg);
        }
    }
    
    public void addTests() {
        Map proxyProperties = ProxyAuthenticationUsersAndProperties.proxyProperties;
        Map proxyProperties2 = ProxyAuthenticationUsersAndProperties.proxyProperties2;
        Map cancelProxyProperties = ProxyAuthenticationUsersAndProperties.cancelProxyProperties;
        
        // DatabaseSession tests - proxy properties set on the DatabaseSession
        // useExternalConnectionPooling == false, databaseSessionProxyProperties
        addTest(ProxyAuthenticationConnectionCustomizerTestCase.createDatabaseSessionTest(false, proxyProperties));
        // useExternalConnectionPooling == true, databaseSessionProxyProperties
        addTest(ProxyAuthenticationConnectionCustomizerTestCase.createDatabaseSessionTest(true, proxyProperties));

        // ServerSession tests - proxy properties set on the ServerSession

        // Proxy properties defined only on the ServerSession.
        // useExternalConnectionPooling == false, serverSessionProxyProperties, shouldUseExclusiveisolatedClientsession==false, clientSessionProxyProperties
        addTest(ProxyAuthenticationConnectionCustomizerTestCase.createServerSessionTest(false, proxyProperties, false, null));
        // useExternalConnectionPooling == false, serverSessionProxyProperties, shouldUseExclusiveisolatedClientsession==true, clientSessionProxyProperties
        addTest(ProxyAuthenticationConnectionCustomizerTestCase.createServerSessionTest(false, proxyProperties, true, null));
        // useExternalConnectionPooling == true, serverSessionProxyProperties, shouldUseExclusiveisolatedClientsession==false, clientSessionProxyProperties
        addTest(ProxyAuthenticationConnectionCustomizerTestCase.createServerSessionTest(true, proxyProperties, false, null));
        // useExternalConnectionPooling == true, serverSessionProxyProperties, shouldUseExclusiveisolatedClientsession==true, clientSessionProxyProperties
        addTest(ProxyAuthenticationConnectionCustomizerTestCase.createServerSessionTest(true, proxyProperties, true, null));

        // Proxy properties defined only on ClientSession.
        // useExternalConnectionPooling == false, serverSessionProxyProperties, shouldUseExclusiveisolatedClientsession==false, clientSessionProxyProperties
        addTest(ProxyAuthenticationConnectionCustomizerTestCase.createServerSessionTest(false, null, false, proxyProperties));
        // useExternalConnectionPooling == false, serverSessionProxyProperties, shouldUseExclusiveisolatedClientsession==true, clientSessionProxyProperties
        addTest(ProxyAuthenticationConnectionCustomizerTestCase.createServerSessionTest(false, null, true, proxyProperties));
        // useExternalConnectionPooling == true, serverSessionProxyProperties, shouldUseExclusiveisolatedClientsession==false, clientSessionProxyProperties
        addTest(ProxyAuthenticationConnectionCustomizerTestCase.createServerSessionTest(true, null, false, proxyProperties));
        // useExternalConnectionPooling == true, serverSessionProxyProperties, shouldUseExclusiveisolatedClientsession==true, clientSessionProxyProperties
        addTest(ProxyAuthenticationConnectionCustomizerTestCase.createServerSessionTest(true, null, true, proxyProperties));

        // Proxy properties defined on the ServerSession, overridden on ClientSession
        // useExternalConnectionPooling == false, serverSessionProxyProperties, shouldUseExclusiveisolatedClientsession==false, clientSessionProxyProperties
        addTest(ProxyAuthenticationConnectionCustomizerTestCase.createServerSessionTest(false, proxyProperties, false, proxyProperties2));
        // useExternalConnectionPooling == false, serverSessionProxyProperties, shouldUseExclusiveisolatedClientsession==true, clientSessionProxyProperties
        addTest(ProxyAuthenticationConnectionCustomizerTestCase.createServerSessionTest(false, proxyProperties, true, proxyProperties2));
        // useExternalConnectionPooling == true, serverSessionProxyProperties, shouldUseExclusiveisolatedClientsession==false, clientSessionProxyProperties
        addTest(ProxyAuthenticationConnectionCustomizerTestCase.createServerSessionTest(true, proxyProperties, false, proxyProperties2));
        // useExternalConnectionPooling == true, serverSessionProxyProperties, shouldUseExclusiveisolatedClientsession==true, clientSessionProxyProperties
        addTest(ProxyAuthenticationConnectionCustomizerTestCase.createServerSessionTest(true, proxyProperties, true, proxyProperties2));

        // Proxy properties defined on the ServerSession, canceled on ClientSession
        // useExternalConnectionPooling == false, serverSessionProxyProperties, shouldUseExclusiveisolatedClientsession==false, clientSessionProxyProperties
        addTest(ProxyAuthenticationConnectionCustomizerTestCase.createServerSessionTest(false, proxyProperties, false, cancelProxyProperties));
        // useExternalConnectionPooling == false, serverSessionProxyProperties, shouldUseExclusiveisolatedClientsession==true, clientSessionProxyProperties
        addTest(ProxyAuthenticationConnectionCustomizerTestCase.createServerSessionTest(false, proxyProperties, true, cancelProxyProperties));
        // useExternalConnectionPooling == true, serverSessionProxyProperties, shouldUseExclusiveisolatedClientsession==false, clientSessionProxyProperties
        addTest(ProxyAuthenticationConnectionCustomizerTestCase.createServerSessionTest(true, proxyProperties, false, cancelProxyProperties));
        // useExternalConnectionPooling == true, serverSessionProxyProperties, shouldUseExclusiveisolatedClientsession==true, clientSessionProxyProperties
        addTest(ProxyAuthenticationConnectionCustomizerTestCase.createServerSessionTest(true, proxyProperties, true, cancelProxyProperties));
    }    
}
