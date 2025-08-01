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
package org.eclipse.persistence.testing.tests.isolatedsession;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestSystem;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

public class IsolatedSessionSystem extends TestSystem {
    @Override
    public void addDescriptors(DatabaseSession session) {
        Project project = new IsolatedEmployeeProject();
        session.addDescriptors(project);
    }

    @Override
    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);
        schemaManager.replaceObject(IsolatedEmployee.buildIsolatedTableDefinition());
        schemaManager.replaceObject(IsolatedPhoneNumber.buildIsolatedTableDefinition());
        schemaManager.replaceObject(IsolatedAddress.buildIsolatedTableDefinition());
        schemaManager.replaceObject(IsolatedEmployee.buildISOLATEDRESPONSTable());
        schemaManager.replaceObject(IsolatedEmployee.buildISOLATEDSALARYTable());
        schemaManager.replaceObject(IsolatedParent.buildISOLATEDPARENTTable());
        schemaManager.replaceObject(IsolatedChild.buildISOLATEDCHILDTable());
        schemaManager.replaceObject(IsolatedDog.buildISOLATEDDOGTable());
        schemaManager.replaceObject(IsolatedBone.buildISOLATEDBONETable());
    }

    @Override
    public void populate(DatabaseSession session) {
        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerObject(IsolatedEmployee.buildEmployeeExample1());
        uow.registerObject(IsolatedEmployee.buildEmployeeExample2());
        uow.registerObject(IsolatedParent.buildIsolatedParentExample1());
        uow.registerObject(IsolatedParent.buildIsolatedParentExample2());
        uow.registerObject(IsolatedDog.buildIsolatedDogExample1());
        uow.commit();
    }
}
