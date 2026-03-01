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
package org.eclipse.persistence.testing.tests.queries.report;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestSystem;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

public class ReportQuerySystem extends TestSystem {
    public ReportQuerySystem() {
        //For testing purposes only
        //    TestExecutor.usesReadProjectFile=true;
        project = new ReportQueryProject();
    }

    @Override
    public void addDescriptors(DatabaseSession session) {
        session.addDescriptors(project);
        session.addDescriptors(new BarBeerProject());
    }

    @Override
    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);
        schemaManager.replaceObject(ReportEmployee.tableDefinition());
        schemaManager.replaceObject(History.tableDefinition());
        schemaManager.replaceObject(Beer.tableDefinition());
        schemaManager.replaceObject(Bar.tableDefinition());
        schemaManager.replaceObject(Bar.beerTableDefinition());
        schemaManager.replaceObject(Person.tableDefinition());
        schemaManager.replaceObject(Brewer.tableDefinition());
        schemaManager.createSequences();
    }

    @Override
    public void populate(DatabaseSession session) {
        PopulationManager manager = PopulationManager.getDefaultManager();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        ReportEmployee employee = ReportEmployee.example1();
        unitOfWork.registerObject(employee);
        manager.registerObject(employee, "example1");
        unitOfWork.commit();
    }
}
