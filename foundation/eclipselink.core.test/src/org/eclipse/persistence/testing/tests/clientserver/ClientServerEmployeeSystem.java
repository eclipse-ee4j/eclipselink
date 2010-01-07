/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
