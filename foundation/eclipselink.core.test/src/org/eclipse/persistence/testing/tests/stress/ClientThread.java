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
