/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Vikram Bhatia - initial API and implementation
package org.eclipse.persistence.testing.models.vehicle;

import java.util.Vector;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestSystem;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;


/**
 * <b>Purpose</b>: To define system behavior.
 * <p><b>Responsibilities</b>:    <ul>
 * <li> Login and return an initialize database session.
 * <li> Create and populate the database.
 * </ul>
 */
public class VehicleSystem extends TestSystem {
    /**
     * Use the default VehicleProject.
     */
    public VehicleSystem() {
        this.project = new VehicleProject();
    }

    public void createTables(DatabaseSession session) {
        new VehicleTableCreator().replaceTables(session);
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new VehicleProject();
        }

        session.addDescriptors(project);
    }

    /**
     * This method will instantiate all of the example instances and insert them into the database
     * using the given session.
     */
    public void populate(DatabaseSession session) {
        VehiclePopulator system = new VehiclePopulator();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        system.buildExamples();
        Vector allObjects = new Vector();
        PopulationManager.getDefaultManager().addAllObjectsForClass(SportsCar.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(CarOwner.class, allObjects);
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();
    }
}
