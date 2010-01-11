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
package org.eclipse.persistence.testing.models.readonly;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.testing.framework.TestSystem;

public class ReadOnlySystem extends TestSystem {
    public ReadOnlySystem() {
        //For testing purposes only
        //	TestExecutor.usesReadProjectFile=true;
        project = new ReadOnlyProject();
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new ReadOnlyProject();
        }
        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);

        schemaManager.replaceObject(Promoter.tableDefinition());
        schemaManager.replaceObject(Actor.tableDefinition());
        schemaManager.replaceObject(Actor.actorMovieJoinTableDefinition());
        schemaManager.replaceObject(Actor.actorCharityJoinTableDefinition());
        schemaManager.replaceObject(Address.tableDefinition());
        schemaManager.replaceObject(Charity.tableDefinition());
        schemaManager.replaceObject(HollywoodAgent.tableDefinition());
        schemaManager.replaceObject(Movie.tableDefinition());
        schemaManager.replaceObject(Country.tableDefinition());
        //	schemaManager.replaceObject(PhoneNumber.tableDefinition());
        schemaManager.replaceObject(DefaultReadOnlyTestClass.tableDefinition());

        schemaManager.createSequences();
    }

    public void populate(DatabaseSession session) {
        PopulationManager manager = PopulationManager.getDefaultManager();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        Movie movie1 = Movie.example1();
        Movie movie2 = Movie.example2();
        Movie movie3 = Movie.example3();
        Movie movie4 = Movie.example4();
        Movie movie5 = Movie.example5();

        unitOfWork.registerObject(movie1);
        unitOfWork.registerObject(movie2);
        unitOfWork.registerObject(movie3);
        unitOfWork.registerObject(movie4);
        unitOfWork.registerObject(movie5);

        manager.registerObject(movie1, "movie1");
        manager.registerObject(movie2, "movie2");
        manager.registerObject(movie3, "movie3");
        manager.registerObject(movie4, "movie4");
        manager.registerObject(movie4, "movie5");

        unitOfWork.registerAllObjects(Country.countries());
        unitOfWork.registerAllObjects(HollywoodAgent.hollywoodAgents());
        unitOfWork.registerAllObjects(Charity.charities());

        unitOfWork.commit();
    }
}
