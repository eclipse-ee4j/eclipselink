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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.sessionsxml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;


/**
 * SessionEventManager used in SessionsXMLSessionEventListenerTest
 * 
 * @author Guy Pelletier
 * @version 1.0
 * @date July 15, 2004
 */
public class SessionBrokerEventListener extends SessionEventAdapter {
    public static final String FILENAME = "SessionBrokerEventListener";

    public SessionBrokerEventListener() {
    }

    /**
     * PUBLIC:
     * This Event is raised before the session logs in.
     */
    public void preLogin(SessionEvent event) {
        File f = new File(FILENAME);

        try {
            FileWriter writer = new FileWriter(f);
            writer.write("Timestamp: " + System.currentTimeMillis());
            writer.close();
        } catch (IOException e) {
            // swallow the exception and one will be thrown from the test anyway
        }
    }
}
