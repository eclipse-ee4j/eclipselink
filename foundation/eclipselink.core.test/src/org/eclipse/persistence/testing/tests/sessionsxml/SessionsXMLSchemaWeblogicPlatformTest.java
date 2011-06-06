/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     09/04/2008-1.1 Guy Pelletier 
 *       - 246130: ECLIPSELINK'S ECLIPSELINK_SESSIONS_1.0.XSD DOES NOT HAVE CURRENT WLS PLATFORMS
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.platform.server.wls.WebLogic_10_Platform;
import org.eclipse.persistence.platform.server.wls.WebLogic_9_Platform;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;

/**
 * Tests server platform tag for Weblogic 9 and 10.
 * 
 * @author Guy Pelletier
 * @version 1.0
 * @date September 4, 2008
 */
public class SessionsXMLSchemaWeblogicPlatformTest extends AutoVerifyTestCase {
    Exception m_exceptionCaught;
    
    DatabaseSession m_weblogic9Session;
    DatabaseSession m_weblogic10Session;

    public SessionsXMLSchemaWeblogicPlatformTest() {
        setDescription("Tests loading supported and non supported weblogic platforms from the schema.");
    }

    public void reset() {
        if (m_weblogic9Session != null && m_weblogic9Session.isConnected()) {
            m_weblogic9Session.logout();
            SessionManager.getManager().getSessions().remove(m_weblogic9Session);
            m_weblogic9Session = null;
        }
        
        if (m_weblogic10Session != null && m_weblogic10Session.isConnected()) {
            m_weblogic10Session.logout();
            SessionManager.getManager().getSessions().remove(m_weblogic10Session);
            m_weblogic10Session = null;
        }
    }

    protected void setup() {
        m_exceptionCaught = null;
    }

    public void test() {
        try {
            XMLSessionConfigLoader loader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaSessionWLSPlatform.xml");

            m_weblogic9Session = (DatabaseSession)SessionManager.getManager().getSession(loader, "Weblogic9Session", getClass().getClassLoader(), false, true);
            m_weblogic10Session = (DatabaseSession)SessionManager.getManager().getSession(loader, "Weblogic10Session", getClass().getClassLoader(), false, true);
            
        } catch (Exception e) {
            m_exceptionCaught = e;
        }
    }

    protected void verify() {
        if (m_exceptionCaught != null) {
            throw new TestErrorException("Loading of the session failed: " + m_exceptionCaught, m_exceptionCaught);
        }

        if (m_weblogic9Session == null) {
            throw new TestErrorException("Loaded weblogic 9 session was null");
        } else {
            if (! (m_weblogic9Session.getServerPlatform() instanceof WebLogic_9_Platform)) {
                throw new TestErrorException("The incorrect weblogic platform was set on the weblogic 9 session.");    
            }
        }
            
        if (m_weblogic10Session == null) {
            throw new TestErrorException("Loaded weblogic 10 session was null");
        } else {
            if (! (m_weblogic10Session.getServerPlatform() instanceof WebLogic_10_Platform)) {
                throw new TestErrorException("The incorrect weblogic platform was set on the weblogic 10 session.");    
            }
        }
    }
}
