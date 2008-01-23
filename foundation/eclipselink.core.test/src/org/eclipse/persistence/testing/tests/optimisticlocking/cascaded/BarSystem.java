/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
