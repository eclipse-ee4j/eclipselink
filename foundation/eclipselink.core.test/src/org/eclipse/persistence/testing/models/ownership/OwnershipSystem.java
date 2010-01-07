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
package org.eclipse.persistence.testing.models.ownership;

import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.tools.schemaframework.*;

/**
 * This model is a complex ownership model. The complex ownership test cases
 * would use this model to test TopLink ownership feature.
 */
public class OwnershipSystem extends TestSystem {
    public OwnershipSystem() {
        project = new OwnershipProject();
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new OwnershipProject();
        }
        (session).addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        // Drop old constraints.
        DatabasePlatform platform = (DatabasePlatform)session.getDatasourcePlatform();
        boolean supportsAlterTableConstraints = platform
                .supportsForeignKeyConstraints()
                || (platform.supportsUniqueColumns() && !platform
                        .requiresUniqueConstraintCreationOnTableCreate());
        if (supportsAlterTableConstraints)
        try {
            session.executeNonSelectingSQL("Alter TABLE OBJECT_A DROP CONSTRAINT OWNER_A_ONE_TO_ONE_");
            session.executeNonSelectingSQL("Alter TABLE OBJECT_C DROP CONSTRAINT OWNER_C_ONE_TO_ONE_");
        } catch (Exception ignore) {
        }
        SchemaManager schemaManager = new SchemaManager(session);

        if (supportsAlterTableConstraints) {
        schemaManager.dropConstraints(ObjectA.tableDefinition());
        schemaManager.dropConstraints(ObjectB.tableDefinition());
        schemaManager.dropConstraints(ObjectC.tableDefinition());
        schemaManager.dropConstraints(ObjectD.tableDefinition());
        schemaManager.dropConstraints(ObjectE.tableDefinition());
        }
        schemaManager.replaceObject(ObjectA.tableDefinition());
        schemaManager.replaceObject(ObjectB.tableDefinition());
        schemaManager.replaceObject(ObjectC.tableDefinition());
        schemaManager.replaceObject(ObjectD.tableDefinition());
        schemaManager.replaceObject(ObjectE.tableDefinition());

        if (supportsAlterTableConstraints) {
        schemaManager.createConstraints(ObjectA.tableDefinition());
        schemaManager.createConstraints(ObjectB.tableDefinition());
        schemaManager.createConstraints(ObjectC.tableDefinition());
        schemaManager.createConstraints(ObjectD.tableDefinition());
        schemaManager.createConstraints(ObjectE.tableDefinition());
        }

        schemaManager.createSequences();
    }

    public void populate(DatabaseSession session) {
        Object instance;
        PopulationManager manager = PopulationManager.getDefaultManager();

        instance = ObjectA.example1();
        session.writeObject(instance);
        manager.registerObject(instance, "example1");

        instance = ObjectA.example2();
        session.writeObject(instance);
        manager.registerObject(instance, "example2");

        instance = ObjectA.example3();
        session.writeObject(instance);
        manager.registerObject(instance, "example3");
    }
}
