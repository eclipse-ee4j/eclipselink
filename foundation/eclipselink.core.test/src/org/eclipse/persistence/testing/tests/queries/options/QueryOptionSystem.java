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
package org.eclipse.persistence.testing.tests.queries.options;

import java.io.File;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.factories.*;

public class QueryOptionSystem extends TestSystem {
    public ClassDescriptor employeeDescriptor;

    public QueryOptionSystem() {

    org.eclipse.persistence.sessions.Project tempProject = new QueryOptionProject();
//    employeeDescriptor = (ClassDescriptor)tempProject.getDescriptors().get(QueryOptionEmployee.class);
//    buildRefreshRemoteIdentityMapResultsQuery();

    String fileName = "QueryOptionTestProject" + System.currentTimeMillis() + ".xml";
    XMLProjectWriter.write(fileName, tempProject);
    project = XMLProjectReader.read(fileName, getClass().getClassLoader());
    File file = new File(fileName);
    file.delete();
    }

  public void addDescriptors(DatabaseSession session) {
        session.addDescriptors(project);
    }

  public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);
        schemaManager.replaceObject(QueryOptionEmployee.tableDefinition());
        schemaManager.replaceObject(QueryOptionHistory.tableDefinition());
        schemaManager.createSequences();
    }

  public void populate(DatabaseSession session) {
        PopulationManager manager = PopulationManager.getDefaultManager();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        QueryOptionEmployee employee = QueryOptionEmployee.example1();
        unitOfWork.registerObject(employee);
        manager.registerObject(employee, "example1");
        unitOfWork.commit();
    }

    /*  public void buildRefreshRemoteIdentityMapResultsQuery()
      {
        ReadObjectQuery namedQuery = new ReadObjectQuery(QueryOptionEmployee.class);
        namedQuery.setShouldRefreshIdentityMapResult(true);
        namedQuery.setShouldRefreshRemoteIdentityMapResult(true);
        employeeDescriptor.getQueryManager().addQuery("refreshRemoteIdentityMapResultsQuery", namedQuery);
      }*/
}
