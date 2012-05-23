/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.clientserver;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.DatabaseLogin;

public class CMServer {
    public org.eclipse.persistence.sessions.server.Server serverSession;
    public boolean errorOccured;

    public CMServer(DatabaseLogin login) {
        this.serverSession = new Project(login).createServerSession(0, 1);
        this.serverSession.addConnectionPool("master", login, 1, 1);
    }

    public void copyDescriptors(Session session) {
        Vector descriptors = new Vector();
        for (Iterator iterator = session.getDescriptors().values().iterator(); iterator.hasNext();) {
            descriptors.addElement(iterator.next());
        }
        serverSession.addDescriptors(descriptors);
    }

    public void login() {
        this.serverSession.login();
    }

    public void logout() {
        this.serverSession.logout();
    }
}
