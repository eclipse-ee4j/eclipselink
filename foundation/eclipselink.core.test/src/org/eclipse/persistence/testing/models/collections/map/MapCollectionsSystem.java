/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.collections.map;

import java.util.Vector;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestSystem;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

public class MapCollectionsSystem extends TestSystem {
    
    public MapCollectionsSystem() {
        project = new MapCollectionsProject();
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new MapCollectionsProject();
        }
        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);

        schemaManager.replaceObject(DirectEntityMapHolder.tableDefinition());
        schemaManager.replaceObject(DirectEntityMapHolder.relationTableDefinition());
        schemaManager.replaceObject(DirectEntity1MMapHolder.tableDefinition());
        schemaManager.replaceObject(EntityMapValue.tableDefinition());
        schemaManager.replaceObject(AggregateEntityMapHolder.tableDefinition());
        schemaManager.replaceObject(AggregateEntityMapHolder.relationTableDefinition());
        schemaManager.replaceObject(EntityEntityMapHolder.tableDefinition());
        schemaManager.replaceObject(EntityEntityMapHolder.relationTableDefinition());
        schemaManager.replaceObject(EntityMapKey.tableDefinition());
        schemaManager.replaceObject(DirectDirectMapHolder.tableDefinition());
        schemaManager.replaceObject(DirectDirectMapHolder.relationTableDefinition());
        schemaManager.replaceObject(AggregateDirectMapHolder.tableDefinition());
        schemaManager.replaceObject(AggregateDirectMapHolder.relationTableDefinition());
        schemaManager.replaceObject(EntityDirectMapHolder.tableDefinition());
        schemaManager.replaceObject(EntityDirectMapHolder.relationTableDefinition());
        schemaManager.replaceObject(AggregateAggregateMapHolder.tableDefinition());
        schemaManager.replaceObject(AggregateAggregateMapHolder.relationTableDefinition());
        schemaManager.replaceObject(DirectAggregateMapHolder.tableDefinition());
        schemaManager.replaceObject(DirectAggregateMapHolder.relationTableDefinition());
        schemaManager.replaceObject(EntityAggregateMapHolder.tableDefinition());
        schemaManager.replaceObject(EntityAggregateMapHolder.relationTableDefinition());
        schemaManager.replaceObject(DEOTMMapValue.tableDefinition());
        schemaManager.replaceObject(AggregateEntity1MMapHolder.tableDefinition());
        schemaManager.replaceObject(AEOTMMapValue.tableDefinition());
        schemaManager.replaceObject(EntityEntity1MMapHolder.tableDefinition());
        schemaManager.replaceObject(EEOTMMapValue.tableDefinition());
        schemaManager.replaceObject(DirectEntityU1MMapHolder.tableDefinition());
        schemaManager.replaceObject(AggregateEntityU1MMapHolder.tableDefinition());
        schemaManager.replaceObject(EntityEntityU1MMapHolder.tableDefinition());
        schemaManager.createSequences();
    }
    
    public void populate(DatabaseSession session) {
        MapPopulator system = new MapPopulator();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        system.buildExamples();
        Vector allObjects = new Vector();
        PopulationManager.getDefaultManager().addAllObjectsForClass(EntityEntity1MMapHolder.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(EntityDirectMapHolder.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(EntityAggregateMapHolder.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(DirectEntityU1MMapHolder.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(DirectEntityMapHolder.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(DirectEntity1MMapHolder.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(DirectDirectMapHolder.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(DirectAggregateMapHolder.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(AggregateEntityU1MMapHolder.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(AggregateEntityMapHolder.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(AggregateEntity1MMapHolder.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(AggregateDirectMapHolder.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(AggregateAggregateMapHolder.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(EntityEntityMapHolder.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(EntityEntityU1MMapHolder.class, allObjects);
        
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();
    }

}
