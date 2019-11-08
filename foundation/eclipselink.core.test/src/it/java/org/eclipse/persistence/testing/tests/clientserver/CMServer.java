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
