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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.types;

import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;

public class TypeTestSystem extends TestSystem {
    public void addDescriptors(DatabaseSession session) {
        ;
    }

    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);

        schemaManager.replaceObject(BooleanTester.tableDefinition(session));
        schemaManager.replaceObject(TimeDateTester.tableDefinition(session));
        schemaManager.replaceObject(StringTester.tableDefinition(session));
        schemaManager.replaceObject(NumericTester.tableDefinition(session));
        schemaManager.replaceObject(CLOBTester.tableDefinition(session));
        schemaManager.replaceObject(BLOBTester.tableDefinition(session));
    }

    public void populate(DatabaseSession session) {
        ;
    }
}
