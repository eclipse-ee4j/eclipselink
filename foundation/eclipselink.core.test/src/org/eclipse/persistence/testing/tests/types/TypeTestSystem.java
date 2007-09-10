/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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