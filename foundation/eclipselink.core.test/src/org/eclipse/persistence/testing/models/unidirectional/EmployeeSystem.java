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
//     ailitchev - Uni-directional OneToMany
package org.eclipse.persistence.testing.models.unidirectional;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.TestSystem;

/**
 * <b>Purpose</b>: To define system behavior.
 * <p><b>Responsibilities</b>:    <ul>
 * <li> Login and return an initialize database session.
 * <li> Create and populate the database.
 * </ul>
 */
public class EmployeeSystem extends TestSystem {
    public org.eclipse.persistence.sessions.Project project;

    /**
     * Use the default EmployeeProject.
     */
    public EmployeeSystem() {
        this.project = new EmployeeProject();
    }

    public void createTables(DatabaseSession session) {
        new EmployeeTableCreator().replaceTables(session);
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new EmployeeProject();
        }

        session.addDescriptors(project);
    }

    /**
     * This method will instantiate all of the example instances and insert them into the database
     * using the given session.
     */
    public void populate(DatabaseSession session) {
        EmployeePopulator system = new EmployeePopulator();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        system.buildExamples();
        Vector allObjects = new Vector();
        PopulationManager.getDefaultManager().addAllObjectsForClass(Employee.class, allObjects);
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();
    }
}
