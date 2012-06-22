/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
        boolean orig_FAST_TABLE_CREATOR = SchemaManager.FAST_TABLE_CREATOR;
        // on Symfoware, to avoid table locking issues, don't drop old tables
        if (useFastTableCreatorAfterInitialCreate) {
            SchemaManager.FAST_TABLE_CREATOR = true;
        }
        try {
            dropTableConstraints(session);
            //drop tables and then create 'default' tables.
            new SchemaManager(session).replaceDefaultTables();
        } finally {
            if (useFastTableCreatorAfterInitialCreate) {
                SchemaManager.FAST_TABLE_CREATOR = orig_FAST_TABLE_CREATOR;
            }
        }
    }
}
