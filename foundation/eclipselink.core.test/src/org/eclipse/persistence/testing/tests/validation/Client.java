/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ClientSession;


public class Client extends Thread {
    protected ClientSession clientSession;
    protected Session serverSession;

    public Client(Server server, String name, Session session) {
        super(name);
        server.connect(this);
    }

    public void release() {
        this.clientSession.release();
    }

    public void run() {

        for (int i = 0; i < 5; i++) {
            release();
        }
    }
}
