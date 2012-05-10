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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.optimisticlocking;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.*;

public class OptimisticLockingSystem extends TestSystem {
    public void addDescriptors(DatabaseSession session) {
        Vector descriptors = new Vector();
        descriptors.addElement(LockInCache.descriptor());
        descriptors.addElement(LockInObject.descriptor());
        descriptors.addElement(TimestampInCache.descriptor());
        descriptors.addElement(TimestampInObject.descriptor());
        descriptors.addElement(TimestampVersion.descriptor());
        descriptors.addElement(TimestampInAggregateObject.descriptor());
        //
        descriptors.addElement(ObjectVersion.descriptor());
        descriptors.addElement(LockInAggregateObject.descriptor());
        //
        descriptors.addElement(ChangedRow.descriptor());
        descriptors.addElement(ListItem.descriptor());
        descriptors.addElement(ListHolder.descriptor());
        (session).addDescriptors(descriptors);
        (session).addDescriptors(new RockBandProject());
        (session).addDescriptors(new AnimalProject());
        session.addDescriptors(new GamesConsoleProject());
    }

    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);

        schemaManager.replaceObject(LockInCache.tableDefinition());
        schemaManager.replaceObject(LockInObject.tableDefinition());
        schemaManager.replaceObject(TimestampInObject.tableDefinition());
        schemaManager.replaceObject(TimestampInAggregateObject.tableDefinition());
        schemaManager.replaceObject(LockInAggregateObject.tableDefinition());
        schemaManager.replaceObject(TimestampInCache.tableDefinition());
        schemaManager.replaceObject(ChangedRow.tableDefinition());
        schemaManager.replaceObject(ListItem.tableDefinition());
        schemaManager.replaceObject(ListHolder.tableDefinition());
        schemaManager.createSequences();
        new RockBandTableCreator().replaceTables(session);
        new AnimalTableCreator().replaceTables(session);
        new GamesConsoleTableCreator().replaceTables(session);
    }

    public void populate(DatabaseSession session) {
        Object instance;
        PopulationManager manager = PopulationManager.getDefaultManager();

        instance = LockInCache.example1();
        session.writeObject(instance);
        manager.registerObject(instance, "Cache example1");

        instance = LockInCache.example2();
        session.writeObject(instance);
        manager.registerObject(instance, "Cache example2");

        instance = LockInCache.example3();
        session.writeObject(instance);
        manager.registerObject(instance, "Cache example3");

        instance = LockInObject.example1();
        session.writeObject(instance);
        manager.registerObject(instance, "Object example1");

        instance = LockInObject.example2();
        session.writeObject(instance);
        manager.registerObject(instance, "Object example2");

        instance = LockInObject.example3();
        session.writeObject(instance);
        manager.registerObject(instance, "Object example3");

        instance = TimestampInCache.example1();
        session.writeObject(instance);
        manager.registerObject(instance, "Cache TS example1");

        instance = TimestampInCache.example2();
        session.writeObject(instance);
        manager.registerObject(instance, "Cache TS example2");

        instance = TimestampInCache.example3();
        session.writeObject(instance);
        manager.registerObject(instance, "Cache TS example3");

        instance = TimestampInObject.example1();
        session.writeObject(instance);
        manager.registerObject(instance, "Object TS example1");

        instance = TimestampInObject.example2();
        session.writeObject(instance);
        manager.registerObject(instance, "Object TS example2");

        instance = TimestampInObject.example3();
        session.writeObject(instance);
        manager.registerObject(instance, "Object TS example3");

        instance = ChangedRow.example1();
        session.writeObject(instance);
        manager.registerObject(instance, "ChangedRow example1");

        instance = ChangedRow.example2();
        session.writeObject(instance);
        manager.registerObject(instance, "ChangedRow example2");

        instance = ChangedRow.example3();
        session.writeObject(instance);
        manager.registerObject(instance, "ChangedRow example3");

        instance = TimestampInAggregateObject.example1();
        session.writeObject(instance);
        manager.registerObject(instance, "Aggregate Object TS example1");

        instance = TimestampInAggregateObject.example2();
        session.writeObject(instance);
        manager.registerObject(instance, "Aggregate Object TS example2");

        instance = TimestampInAggregateObject.example3();
        session.writeObject(instance);
        manager.registerObject(instance, "Aggregate Object TS example3");

        instance = LockInAggregateObject.example1();
        session.writeObject(instance);
        manager.registerObject(instance, "Lock Object TS example1");

        instance = LockInAggregateObject.example2();
        session.writeObject(instance);
        manager.registerObject(instance, "Lock Object TS example2");

        instance = LockInAggregateObject.example3();
        session.writeObject(instance);
        manager.registerObject(instance, "Lock Object TS example3");

        UnitOfWork uow = session.acquireUnitOfWork();
        instance = uow.registerObject(RockBand.example1());
        manager.registerObject(instance, "RockBand example1");
        instance = uow.registerObject(RockBand.example2());
        manager.registerObject(instance, "RockBand example2");
        uow.commit();

    }
}
