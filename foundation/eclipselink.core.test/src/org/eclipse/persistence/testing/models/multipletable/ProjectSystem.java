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
package org.eclipse.persistence.testing.models.multipletable;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;

public class ProjectSystem extends TestSystem {
    public ProjectSystem() {
        project = new ProjectProject();
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new ProjectProject();
        }
        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);

        schemaManager.replaceObject(BusinessProject.tableDefinition());
        schemaManager.replaceObject(LargeBusinessProject.tableDefinition());
        schemaManager.replaceObject(Budget.tableDefinition());
        schemaManager.createSequences();
    }

    public void populate(DatabaseSession session) {
        LargeBusinessProject instance;
        PopulationManager manager = PopulationManager.getDefaultManager();

        instance = LargeBusinessProject.example1();
        session.writeObject(instance);
        manager.registerObject(instance, "example1");
        manager.registerObject(instance.budget, "example1");

        instance = LargeBusinessProject.example2();
        session.writeObject(instance);
        manager.registerObject(instance, "example2");
        manager.registerObject(instance.budget, "example2");
    }
}
