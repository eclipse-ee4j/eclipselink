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
package org.eclipse.persistence.testing.models.forceupdate;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestSystem;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

import java.util.Vector;

public class FUVLSystem extends TestSystem {

    @Override
    public void addDescriptors(DatabaseSession session) {
        session.addDescriptors(new FUVLProject());
    }

    @Override
    public void createTables(DatabaseSession session) {
        new FUVLTableCreator().replaceTables(session);
    }

    @Override
    public void populate(DatabaseSession session) {
        FUVLPopulator system = new FUVLPopulator();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        system.buildExamples();
        Vector allObjects = new Vector();
        PopulationManager.getDefaultManager().addAllObjectsForClass(EmployeeTLIC.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(EmployeeTLIO.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(EmployeeVLIC.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(EmployeeVLIO.class, allObjects);
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();
    }
}
