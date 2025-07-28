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
package org.eclipse.persistence.testing.models.optimisticlocking;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestSystem;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

import java.util.Vector;

public class OptimisticLockingSystem extends TestSystem {
    @Override
    public void addDescriptors(DatabaseSession session) {
        Vector descriptors = new Vector();
        descriptors.add(LockInCache.descriptor());
        descriptors.add(LockInObject.descriptor());
        descriptors.add(TimestampInCache.descriptor());
        descriptors.add(TimestampInObject.descriptor());
        descriptors.add(TimestampVersion.descriptor());
        descriptors.add(TimestampInAggregateObject.descriptor());
        //
        descriptors.add(ObjectVersion.descriptor());
        descriptors.add(LockInAggregateObject.descriptor());
        //
        descriptors.add(ChangedRow.descriptor());
        descriptors.add(ListItem.descriptor());
        descriptors.add(ListHolder.descriptor());
        (session).addDescriptors(descriptors);
        (session).addDescriptors(new RockBandProject());
        (session).addDescriptors(new AnimalProject());
        session.addDescriptors(new GamesConsoleProject());
    }

    @Override
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

    @Override
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
