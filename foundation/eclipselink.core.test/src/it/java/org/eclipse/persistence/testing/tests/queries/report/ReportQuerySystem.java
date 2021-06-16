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
package org.eclipse.persistence.testing.tests.queries.report;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;

public class ReportQuerySystem extends TestSystem {
    public ReportQuerySystem() {
        //For testing purposes only
        //    TestExecutor.usesReadProjectFile=true;
        project = new ReportQueryProject();
    }

    public void addDescriptors(DatabaseSession session) {
        session.addDescriptors(project);
        session.addDescriptors(new BarBeerProject());
    }

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

    public void populate(DatabaseSession session) {
        PopulationManager manager = PopulationManager.getDefaultManager();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        ReportEmployee employee = ReportEmployee.example1();
        unitOfWork.registerObject(employee);
        manager.registerObject(employee, "example1");
        unitOfWork.commit();
    }
}
