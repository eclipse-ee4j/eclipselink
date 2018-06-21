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

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;

public class ClientServerEmployeeSystem extends TestSystem {
    public ClientServerEmployeeSystem() {
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new ClientServerEmployeeProject();
        }

        session.addDescriptors(project);
        session.addDescriptors(new DeadLockEmployeeProject());
    }

    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);
        schemaManager.replaceObject(EmployeeForClientServerSession.tableDefinition());
        new DeadLockEmployeeTableCreator().replaceTables(session);
        schemaManager.createSequences();
    }

    public void populate(DatabaseSession session) {
        DeadLockEmployeePopulator populator = new DeadLockEmployeePopulator();
        session.writeObject(populator.employeeExample1());
        session.writeObject(populator.employeeExample2());
        session.writeObject(populator.employeeExample3());
    }
}
