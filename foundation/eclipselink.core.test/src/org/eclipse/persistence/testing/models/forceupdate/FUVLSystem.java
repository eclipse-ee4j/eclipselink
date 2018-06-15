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
package org.eclipse.persistence.testing.models.forceupdate;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;

public class FUVLSystem extends TestSystem {

    public void addDescriptors(DatabaseSession session) {
        session.addDescriptors(new FUVLProject());
    }

    public void createTables(DatabaseSession session) {
        new FUVLTableCreator().replaceTables(session);
    }

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
