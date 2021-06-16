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
package org.eclipse.persistence.testing.tests.stress;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.exceptions.*;

/**
 * Thread used to simulate a client.
 */
public class ClientThread extends Thread {
    Server server;

    public ClientThread(Server server) {
        super();
        this.server = server;
    }

    public void run() {
        Session client = server.acquireClientSession("default");
        client.readAllObjects(Employee.class);
        client.readAllObjects(org.eclipse.persistence.testing.models.employee.domain.Project.class);
        org.eclipse.persistence.testing.models.employee.domain.Project edit = (org.eclipse.persistence.testing.models.employee.domain.Project)(client.readAllObjects(LargeProject.class)).firstElement();
        UnitOfWork uow = client.acquireUnitOfWork();
        uow.readAllObjects(SmallProject.class);
        edit = (org.eclipse.persistence.testing.models.employee.domain.Project)uow.registerObject(edit);
        edit.setName((new Long(System.currentTimeMillis())).toString());
        try {
            uow.commit();
        } catch (OptimisticLockException exception) {
            //do de do.
        }
        client.release();
    }
}
