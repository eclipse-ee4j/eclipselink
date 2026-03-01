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
package org.eclipse.persistence.testing.tests.stress;

import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;
import org.eclipse.persistence.testing.models.employee.domain.Project;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;

/**
 * Thread used to simulate a client.
 */
public class ClientThread extends Thread {
    Server server;

    public ClientThread(Server server) {
        super();
        this.server = server;
    }

    @Override
    public void run() {
        Session client = server.acquireClientSession("default");
        client.readAllObjects(Employee.class);
        client.readAllObjects(org.eclipse.persistence.testing.models.employee.domain.Project.class);
        org.eclipse.persistence.testing.models.employee.domain.Project edit = (Project) (client.readAllObjects(LargeProject.class)).get(0);
        UnitOfWork uow = client.acquireUnitOfWork();
        uow.readAllObjects(SmallProject.class);
        edit = (org.eclipse.persistence.testing.models.employee.domain.Project)uow.registerObject(edit);
        edit.setName((Long.valueOf(System.currentTimeMillis())).toString());
        try {
            uow.commit();
        } catch (OptimisticLockException exception) {
            //do de do.
        }
        client.release();
    }
}
