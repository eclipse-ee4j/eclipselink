/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     09/28/2015 - Will Dazey
//       - 478331 : Added support for defining local or server as the default locale for obtaining timestamps
package org.eclipse.persistence.jpa.test.locking;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.descriptors.TimestampLockingPolicy;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.locking.model.ClassDescriptorCustomizer;
import org.eclipse.persistence.jpa.test.locking.model.TimestampDog;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestTimestampVersionLocking {
    @Emf(name = "emf", createTables = DDLGen.DROP_CREATE, classes = { TimestampDog.class }, properties = { 
            @Property(name = "eclipselink.cache.shared.default", value = "false"), 
            @Property(name = "eclipselink.locking.timestamp.local.default", value = "true") })
    private EntityManagerFactory emf;

    @Emf(name = "emfFalse", createTables = DDLGen.DROP_CREATE, classes = { TimestampDog.class }, properties = { 
            @Property(name = "eclipselink.cache.shared.default", value = "false"), 
            @Property(name = "eclipselink.locking.timestamp.local.default", value = "false") })
    private EntityManagerFactory emfFalse;

    @Emf(name = "emfCustomized", createTables = DDLGen.DROP_CREATE, classes = { TimestampDog.class }, properties = { 
            @Property(name = "eclipselink.cache.shared.default", value = "false"), 
            @Property(name = "eclipselink.locking.timestamp.local.default", value = "true"), 
            @Property(name = "eclipselink.descriptor.customizer.TimestampDog", value = "org.eclipse.persistence.jpa.test.locking.model.ClassDescriptorCustomizer") })
    private EntityManagerFactory emfCustomized;

    @Emf(name = "emfFalseCustomized", createTables = DDLGen.DROP_CREATE, classes = { TimestampDog.class }, properties = { 
            @Property(name = "eclipselink.cache.shared.default", value = "false"), 
            @Property(name = "eclipselink.locking.timestamp.local.default", value = "false"), 
            @Property(name = "eclipselink.descriptor.customizer.TimestampDog", value = "org.eclipse.persistence.jpa.test.locking.model.ClassDescriptorCustomizer") })
    private EntityManagerFactory emfFalseCustomized;

    /**
     * Check that setting the property "true" will get the local system time
     * instead of the default behavior of contacting the server.
     * 
     * @throws Exception
     */
    @Test
    public void testLocalTimestampProperty() throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Get the session for this transaction
            Session session = ((EntityManagerImpl) em).getActiveSession();

            // Get the TimestampQuery that would be used by the platform to get the current time
            DatabaseQuery query = session.getDatasourcePlatform().getTimestampQuery();

            // Add a Listener to the session
            QueryListener listener = new QueryListener(query);
            session.getEventManager().addListener(listener);

            // Persist an Entity that will use Timestamp version locking and will trigger QueryListener
            TimestampDog dog = new TimestampDog();
            em.persist(dog);
            em.getTransaction().commit();

            // Make sure the query was not executed
            Assert.assertTrue("Query (" + listener.getQuery().getSQLString() + ") was executed unexpectedly.", !listener.wasQueryExecuted());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    /**
     * Check that setting the property "false" will get the server system time
     * just like the default behavior.
     * 
     * @throws Exception
     */
    @Test
    public void testLocalTimestampPropertyFalse() throws Exception {
        EntityManager em = emfFalse.createEntityManager();
        try {
            em.getTransaction().begin();

            // Get the session for this transaction
            Session session = ((EntityManagerImpl) em).getActiveSession();

            // Get the TimestampQuery that would be used by the platform to get the current time
            DatabaseQuery query = session.getDatasourcePlatform().getTimestampQuery();

            // Add a Listener to the session
            QueryListener listener = new QueryListener(query);
            session.getEventManager().addListener(listener);

            // Persist an Entity that will use Timestamp version locking and will trigger QueryListener
            TimestampDog dog = new TimestampDog();
            em.persist(dog);
            em.getTransaction().commit();

            // Make sure the query was not executed
            Assert.assertTrue("Query (" + listener.getQuery().getSQLString() + ") was executed unexpectedly.", listener.wasQueryExecuted());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    /**
     * Check that setting the value SERVER on the OptimisticLockingPolicy in a
     * DescriptorCustomizer will override the persistence property value.
     * 
     * @throws Exception
     */
    @Test
    public void testLocalTimestampPropertyCustomizer() throws Exception {
        // Have policy customized to use Server time
        ClassDescriptorCustomizer.USETIME = TimestampLockingPolicy.SERVER_TIME;
        EntityManager em = emfCustomized.createEntityManager();
        try {
            em.getTransaction().begin();

            // Get the session for this transaction
            Session session = ((EntityManagerImpl) em).getActiveSession();

            // Get the TimestampQuery that would be used by the platform to get the current time
            DatabaseQuery query = session.getDatasourcePlatform().getTimestampQuery();

            // Add a Listener to the session
            QueryListener listener = new QueryListener(query);
            session.getEventManager().addListener(listener);

            // Persist an Entity that will use Timestamp version locking and will trigger QueryListener
            TimestampDog dog = new TimestampDog();
            em.persist(dog);
            em.getTransaction().commit();

            // Make sure the query was executed
            Assert.assertTrue("Query (" + listener.getQuery().getSQLString() + ") was not executed as expected.", listener.wasQueryExecuted());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    /**
     * Check that setting the value LOCAL on the OptimisticLockingPolicy in a
     * DescriptorCustomizer will override the persistence property value.
     * 
     * @throws Exception
     */
    @Test
    public void testLocalTimestampPropertyFalseCustomizer() throws Exception {
        // Have policy customized to use local time
        ClassDescriptorCustomizer.USETIME = TimestampLockingPolicy.LOCAL_TIME;
        EntityManager em = emfFalseCustomized.createEntityManager();
        try {
            em.getTransaction().begin();

            // Get the session for this transaction
            Session session = ((EntityManagerImpl) em).getActiveSession();

            // Get the TimestampQuery that would be used by the platform to get the current time
            DatabaseQuery query = session.getDatasourcePlatform().getTimestampQuery();

            // Add a Listener to the session
            QueryListener listener = new QueryListener(query);
            session.getEventManager().addListener(listener);

            // Persist an Entity that will use Timestamp version locking and will trigger QueryListener
            TimestampDog dog = new TimestampDog();
            em.persist(dog);
            em.getTransaction().commit();

            // Make sure the query was not executed
            Assert.assertTrue("Query (" + listener.getQuery().getSQLString() + ") was executed unexpectedly.", !listener.wasQueryExecuted());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    /**
     * Add this to Session Event Manager to listen for query executions. This
     * class will listen for the execution of a specified query and track it.
     */
    public class QueryListener extends SessionEventAdapter {
        private DatabaseQuery listenFor;
        private boolean wasExecuted;

        public QueryListener(DatabaseQuery query) {
            this.setQuery(query);
        }

        public DatabaseQuery getQuery() {
            return this.listenFor;
        }

        public void setQuery(DatabaseQuery query) {
            this.listenFor = query;
            this.wasExecuted = false;
        }

        public boolean wasQueryExecuted() {
            return this.wasExecuted;
        }

        @Override
        public void postExecuteQuery(SessionEvent event) {
            DatabaseQuery query = event.getQuery();
            if (query != null) {
                this.wasExecuted = this.wasExecuted || query.equals(this.listenFor);
            }
        }
    }
}
