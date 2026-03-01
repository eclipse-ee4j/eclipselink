/*
 * Copyright (c) 2015, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015, 2024 IBM Corporation. All rights reserved.
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
//     09/28/2015 - Will Dazey
//       - 478331 : Added support for defining local or server as the default locale for obtaining timestamps
package org.eclipse.persistence.jpa.test.locking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;

import java.sql.Timestamp;

import org.eclipse.persistence.descriptors.TimestampLockingPolicy;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.locking.model.ClassDescriptorCustomizer;
import org.eclipse.persistence.jpa.test.locking.model.EcallRegistration;
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

    @Emf(name = "emf2", createTables = DDLGen.DROP_CREATE, classes = { EcallRegistration.class })
    private EntityManagerFactory emf2;

    /**
     * Check that setting the property "true" will get the local system time
     * instead of the default behavior of contacting the server.
     *
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
            Assert.assertFalse("Query (" + listener.getQuery().getSQLString() + ") was executed unexpectedly.", listener.wasQueryExecuted());
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
            Assert.assertFalse("Query (" + listener.getQuery().getSQLString() + ") was executed unexpectedly.", listener.wasQueryExecuted());
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }

    /**
     * Test bulk update queries do not reuse the same timestamp value across multiple executions
     */
    @Test
    public void testUpdateAllQueryWithTimestampLocking() {

        int flag1 = 0;
        int flag2 = 1;
        String pk1 = "11004";
        String pk2 = "11005";

        // Populate entities with initial timestamp version values
        EntityManager em = emf2.createEntityManager();
        try {
            em.getTransaction().begin();

            EcallRegistration e1 = new EcallRegistration(pk1, flag1);
            EcallRegistration e2 = new EcallRegistration(pk2, flag2);

            em.persist(e1);
            em.persist(e2);

            em.getTransaction().commit();
        } finally {
            if(em.isOpen()) {
                em.clear();
                em.close();
            }
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*
         * Update flag1 value via bulk Update query.
         * This should update the version locking timestamp value for entity1.
         */
        em = emf2.createEntityManager();
        try {
            flag1 = flag1++;

            em.getTransaction().begin();

            Query query = em.createNamedQuery("updateActiveEcallAvailableFlag", EcallRegistration.class);
            query.setParameter("flag", flag1);
            query.setParameter("pk", pk1);

            query.executeUpdate();

            em.getTransaction().commit();
        } finally {
            if(em.isOpen()) {
                em.clear();
                em.close();
            }
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*
         * Update flag2 value via bulk Update query.
         * This should update the version locking timestamp value for entity2.
         */
        em = emf2.createEntityManager();
        try {
            flag2 = flag2++;

            em.getTransaction().begin();

            Query query = em.createNamedQuery("updateActiveEcallAvailableFlag", EcallRegistration.class);
            query.setParameter("flag", flag2);
            query.setParameter("pk", pk2);

            query.executeUpdate();

            em.getTransaction().commit();
        } finally {
            if(em.isOpen()) {
                em.clear();
                em.close();
            }
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*
         *  Validate that even though both bulk updates used the same UpdateAllQuery, both entities have 
         *  different version locking timestamps in the database.
         */
        em = emf2.createEntityManager();
        try {
            em.getTransaction().begin();

            EcallRegistration e1 = em.find(EcallRegistration.class, pk1);
            EcallRegistration e2 = em.find(EcallRegistration.class, pk2);

            Assert.assertNotNull(e1);
            Assert.assertNotNull(e2);

            Assert.assertNotEquals(e1.getSysUpdateTimestamp(), e2.getSysUpdateTimestamp());
            Assert.assertTrue("Expected entity2.sysUpdateTimestamp [" + e2.getSysUpdateTimestamp() + "] "
                    + "to be after entity1.sysUpdateTimestamp [" + e1.getSysUpdateTimestamp() + "]", 
                    e2.getSysUpdateTimestamp().after(e1.getSysUpdateTimestamp()));

            em.getTransaction().commit();
        } finally {
            if(em.isOpen()) {
                em.clear();
                em.close();
            }
        }
    }

    /**
     * Test that bulk update queries do not update the managed entities version 
     * values as documented in the specification
     * 
     * JPA Spec section 4.10: Bulk Update and Delete Operations
     * 
     * Bulk update maps directly to a database update operation, bypassing optimistic locking checks. 
     * Portable applications must manually update the value of the version column, if desired, and/or 
     * manually validate the value of the version column.
     */
    @Test
    public void testTimestampLockingUpdateWithUpdateAllQuery() {

        int flag1 = 0;
        String pk1 = "11006";

        EntityManager em = emf2.createEntityManager();
        try {
            // Populate an entity
            em.getTransaction().begin();

            EcallRegistration persist1 = new EcallRegistration(pk1, flag1);

            em.persist(persist1);

            em.getTransaction().commit();

            // Find managed instance, record current timestamp version value
            em.getTransaction().begin();

            EcallRegistration find1 = em.find(EcallRegistration.class, pk1);
            Assert.assertNotNull(find1);

            Timestamp ver1 = find1.getSysUpdateTimestamp();
            Assert.assertNotNull(ver1);

            em.getTransaction().commit();

            // Execute UpdateAllQuery to update version locking field in db
            flag1 = flag1++;

            em.getTransaction().begin();

            Query query = em.createNamedQuery("updateActiveEcallAvailableFlag", EcallRegistration.class);
            query.setParameter("flag", flag1);
            query.setParameter("pk", pk1);

            query.executeUpdate();

            em.getTransaction().commit();

            // Verify that the UpdateAllQuery didn't update the managed instance
            em.getTransaction().begin();

            EcallRegistration find2 = em.find(EcallRegistration.class, pk1);
            Assert.assertNotNull(find2);

            Timestamp ver2 = find2.getSysUpdateTimestamp();
            Assert.assertNotNull(ver2);
            Assert.assertEquals(ver1, ver2);

            em.getTransaction().commit();
        } finally {
            if(em.isOpen()) {
                em.clear();
                em.close();
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
