/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.models.aggregate;

import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestSystem;
import org.eclipse.persistence.testing.tests.dynamic.QuerySQLTracker;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

public class SimpleAggregateSystem extends TestSystem {

    public SimpleAggregateSystem() {
        project = new SimpleAggregateProject();
    }

    @Override
    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new SimpleAggregateProject();
        }
        session.addDescriptors(project);
    }

    @Override
    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);

        schemaManager.replaceObject(SimpleEntity.tableDefinition());
    }

    @Override
    public void populate(DatabaseSession session) {

        QuerySQLTracker qTracker = QuerySQLTracker.install(session);

        {
            UnitOfWork uow = session.acquireUnitOfWork();

            SimpleEntity instance = new SimpleEntity();
            instance.setId("1");
            instance.setField("constant");
            instance.setSimpleAggregate(new SimpleAggregate());
            session.writeObject(instance);
            uow.registerObject(instance);
            uow.commit();
        }

        {
            UnitOfWork uow = session.acquireUnitOfWork();

            ReadObjectQuery query = new ReadObjectQuery();
            final SimpleEntity queryInstance = new SimpleEntity();
            queryInstance.setId("1");
            query.setSelectionObject(queryInstance);
            final SimpleEntity instance = (SimpleEntity) uow.executeQuery(query);
            if (instance == null) {
                throw new RuntimeException("Object was not found");
            }
            if (instance.getSimpleAggregate() == null) {
                //throw new RuntimeException("SimpleAggregate is null");
            }
            instance.setSimpleAggregate(instance.getSimpleAggregate() == null ? new SimpleAggregate() : null);

            qTracker.reset();

            instance.setSimpleAggregate(null);
            uow.registerObject(instance);
            uow.commit();

            if (qTracker.getQueries().size() > 0) {
                throw new RuntimeException("Unexpected query was executed: " + qTracker.getQueries());
            }
        }

    }
}
