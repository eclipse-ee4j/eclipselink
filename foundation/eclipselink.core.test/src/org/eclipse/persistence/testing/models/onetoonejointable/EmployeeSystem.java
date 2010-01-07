/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.onetoonejointable;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.TestSystem;

public class EmployeeSystem extends TestSystem {
    public org.eclipse.persistence.sessions.Project project;

    /**
     * Use the default EmployeeProject.
     */
    public EmployeeSystem() {
        this.project = new EmployeeProject();
    }

    public void createTables(DatabaseSession session) {
        new EmployeeTableCreator().replaceTables(session);
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new EmployeeProject();
        }

        session.addDescriptors(project);
    }
        
    /**
     * This method will instantiate all of the example instances and insert them into the database
     * using the given session.
     */
    public void populate(DatabaseSession session) {
        EmployeePopulator system = new EmployeePopulator();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        system.buildExamples();
        Vector allObjects = new Vector();
        PopulationManager.getDefaultManager().addAllObjectsForClass(Employee.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(SmallProject.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(LargeProject.class, allObjects);
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();
    }
}