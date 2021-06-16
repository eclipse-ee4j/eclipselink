/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
