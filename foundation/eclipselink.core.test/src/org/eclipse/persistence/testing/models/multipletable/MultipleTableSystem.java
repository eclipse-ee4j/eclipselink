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
package org.eclipse.persistence.testing.models.multipletable;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestSystem;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

/**
 * System for the MultipleTableModel
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date June 17, 2005
 */
public class MultipleTableSystem extends TestSystem {
    public MultipleTableSystem() {
        project = new MultipleTableProject();
    }

    public void addDescriptors(DatabaseSession session) {
        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        new MultipleTableTableCreator().replaceTables(session);
    }
    public void populate(DatabaseSession session) {
        PopulationManager manager = PopulationManager.getDefaultManager();
        UnitOfWork uow = session.acquireUnitOfWork();
        
        Cow cow1 = Cow.getCow1();
        manager.registerObject(cow1,"cow1");
        uow.registerObject(cow1);
        
        SuperCow superCow1 = SuperCow.getSuperCow1();
        manager.registerObject(superCow1, "superCow1");
        uow.registerObject(superCow1);
        
        Horse horse1 = Horse.getHorse1();
        manager.registerObject(horse1, "horse1");
        uow.registerObject(horse1);
        
        SuperHorse superHorse1 = SuperHorse.getSuperHorse1();
        manager.registerObject(superHorse1, "superHorse1");
        uow.registerObject(superHorse1);
        
        Swan swan1 = Swan.getSwan1();
        manager.registerObject(swan1, "cwan1");
        uow.registerObject(swan1);
        
        SuperSwan superSwan1 = SuperSwan.getSuperSwan1();
        manager.registerObject(superSwan1, "superSwan1");
        uow.registerObject(superSwan1);
        
        uow.commit();
    }
}
