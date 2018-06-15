/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.xdb;

import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.tools.schemaframework.*;

public class XMLTypeEmployeeSystem extends TestSystem {
    public XMLTypeEmployeeSystem() {
        project = new Employee_XMLProject();
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new Employee_XMLProject();
        }
        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);

        schemaManager.replaceObject(Employee_XML.tableDefinition());
        schemaManager.createSequences();

        String strCreateStoredFunction =
            "\ncreate or replace function STOREDFUNCTION_XMLTYPE\n" +
            "  return XMLTYPE\n" +
            "as\n" +
            "begin\n" +
            "  return XMLTYPE('<jb><data>BLAH</data></jb>');\n" +
            "end;";
        DataModifyQuery query = new DataModifyQuery(strCreateStoredFunction);
        query.setShouldBindAllParameters(false);
        session.executeQuery(query);
    }

    public void populate(DatabaseSession session) {
        PopulationManager manager = PopulationManager.getDefaultManager();

        Employee_XML example0 = Employee_XML.example0();
        Employee_XML example1 = Employee_XML.example1();
        example0.managedEmployees.addElement(example1);
        example1.manager = example0;
        session.writeObject(example0);
        manager.registerObject(example0, "example0");
        session.writeObject(example1);
        manager.registerObject(example1, "example1");
    }
}
