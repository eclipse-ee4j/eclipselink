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
package org.eclipse.persistence.testing.models.collections;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestSystem;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

public class CollectionsSystem extends TestSystem {
    public CollectionsSystem() {
        project = new CollectionsProject();
    }

    @Override
    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new CollectionsProject();
        }
        session.addDescriptors(project);
    }

    @Override
    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);

        schemaManager.replaceObject(org.eclipse.persistence.testing.models.collections.MenuItem.tableDefinition());
        schemaManager.replaceObject(Menu.tableDefinition());
        schemaManager.replaceObject(org.eclipse.persistence.testing.models.collections.Person.tableDefinition());
        schemaManager.replaceObject(Location.tableDefinition());
        schemaManager.replaceObject(Location.relationTableDefinition());
        schemaManager.replaceObject(Location.relation2TableDefinition());
        schemaManager.replaceObject(Diner.relationTableDefinition());
        schemaManager.replaceObject(Restaurant.tableDefinition());
        schemaManager.replaceObject(Restaurant.sloganTableDefinition());
        schemaManager.replaceObject(Restaurant.servicesTableDefinition());
        schemaManager.replaceObject(Restaurant.licensesTableDefinition());
        schemaManager.createSequences();
    }

    @Override
    public void populate(DatabaseSession session) {
        PopulationManager manager = PopulationManager.getDefaultManager();
        UnitOfWork uow = session.acquireUnitOfWork();

        // Locations
        Location location1 = Location.example1();
        Location location2 = Location.example2();
        Location location3 = Location.example3();
        Location location4 = Location.example4();
        Location location5 = Location.example5();
        Location location6 = Location.example6();

        //Diners
        Diner diner1 = Diner.example1();
        Diner diner2 = Diner.example2();
        Diner diner3 = Diner.example3();

        // Register restaurants.
        Restaurant instance1 = Restaurant.example1();
        instance1.addLocation(location1);
        instance1.addLocation(location2);
        instance1.addDiner(diner1);
        instance1.addDiner(diner2);
        manager.registerObject(instance1, "example1");

        Restaurant instance2 = Restaurant.example2();
        instance2.addLocation(location2);
        instance2.addLocation(location3);
        instance2.addLocation(location4);
        instance2.addDiner(diner2);
        instance2.addDiner(diner3);
        manager.registerObject(instance2, "example2");

        uow.registerObject(instance1);
        uow.registerObject(instance2);
        uow.commit();

    }
}
