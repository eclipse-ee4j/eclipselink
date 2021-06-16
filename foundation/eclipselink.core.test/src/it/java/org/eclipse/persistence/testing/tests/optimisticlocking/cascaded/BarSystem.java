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
package org.eclipse.persistence.testing.tests.optimisticlocking.cascaded;

import java.util.Vector;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.TestSystem;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

public class BarSystem extends TestSystem {
    protected Project project;

    public BarSystem () {}

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new BarProject(session);
        }

        (session).addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        new BarTableCreator().replaceTables(session);
    }

    public void populate(DatabaseSession session) {
        BarPopulator populator = new BarPopulator();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        populator.buildExamples();
        Vector allObjects = new Vector();
        PopulationManager.getDefaultManager().addAllObjectsForClass(Bar.class, allObjects);
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();
    }
}
