/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * SessionEventManager used in SessionsXMLSessionEventListenerTest
 *
 * @author Guy Pelletier
 * @version 1.0
 */
public class ServerSessionEventListener extends SessionEventAdapter {
    public static final String FILENAME = "ServerSessionEventListener";

    public ServerSessionEventListener() {
    }

    /**
     * PUBLIC:
     * This Event is raised before the session logs in.
     */
    @Override
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
