/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.schemaframework;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

/**
 * The auto table generator test system object.
 */
public class AutoTableGeneratorEmployeeSystem extends EmployeeSystem {
    public AutoTableGeneratorEmployeeSystem() {
        super();
    }

    /**
     * Drop tables/constraints, and then create default tables via schema
     * manager
     */
    public void createTables(DatabaseSession session) {
        dropTableConstraints(session);
        //drop tables and then create 'default' tables.
        new SchemaManager((DatabaseSession)session).replaceDefaultTables();
    }
}