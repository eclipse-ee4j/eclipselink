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
package org.eclipse.persistence.testing.models.legacy;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TestSystem;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

public class LegacySystem extends TestSystem {
    public LegacySystem() {
        project = new LegacyProject();
    }

    @Override
    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new LegacyProject();
        }
        (session).addDescriptors(project);
    }

    @Override
    public void createTables(DatabaseSession session) {
        new LegacyTables().replaceTables(session);
    }

    @Override
    public void populate(DatabaseSession session) {
        Employee instance;
        PopulationManager manager = PopulationManager.getDefaultManager();

        instance = Employee.example1();
        session.writeObject(instance);
        manager.registerObject(instance, "example1");
        manager.registerObject(instance.computer, "example1");
        manager.registerObject(instance.shipments.get(0), "example1");
        manager.registerObject(((Shipment)instance.shipments.get(0)).orders.get(0), "example1");

        instance = Employee.example2();
        session.writeObject(instance);
        manager.registerObject(instance, "example2");
        manager.registerObject(instance.computer, "example2");
        manager.registerObject(instance.shipments.get(0), "example2");
        manager.registerObject(((Shipment)instance.shipments.get(0)).orders.get(0), "example2");

        instance = Employee.example3();
        session.writeObject(instance);
        manager.registerObject(instance, "example3");
        manager.registerObject(instance.computer, "example3");
        manager.registerObject(instance.shipments.get(0), "example3");
        manager.registerObject(((Shipment)instance.shipments.get(0)).orders.get(0), "example3");
    }
}
