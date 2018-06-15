/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     04/11/2016-2.7 Tomas Kraus
//       - 490677: Initial API and implementation.
package org.eclipse.persistence.testing.tests.nosql;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SynchronizationType;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.IdentityMapAccessor;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.server.JEEPlatform;
import org.eclipse.persistence.testing.framework.server.ServerPlatform;

public class EntityManagerHelper {

    /** Logger. */
    private static final SessionLog LOG = AbstractSessionLog.getLog();

    /** Stores an information if this test is running on a JEE server, or in JSE. */
    static final boolean isOnServer = System.getProperty("TEST_SERVER_PLATFORM") != null;

    /** Stores an information if the data source is JTA or not. */
    static final boolean isJTA = Boolean.getBoolean(System.getProperty("is.JTA"));

    /** Application server platform. */
    static final ServerPlatform serverPlatform = isOnServer ? initServerPlatform() : null;

    /**
     * Initialize application server platform instance.
     * @return Application server platform instance or {@code null} if instance could not be initialized.
     */
    private static ServerPlatform initServerPlatform() {
        final String platformClass = System.getProperty("TEST_SERVER_PLATFORM");
        if (platformClass == null) {
            return new JEEPlatform();
        } else {
            try {
                return (ServerPlatform)Class.forName(platformClass).newInstance();
            } catch (Exception ex) {
                LOG.log(SessionLog.WARNING, String.format(
                        "Could not initiaize ServerPlatform: %s", ex.getLocalizedMessage()));
                return null;
            }
        }
    }

    /**
     * Create a new entity manager for the persistence unit using the properties. The properties will only be used
     * the first time this entity manager is accessed. If in JEE this will create or return the active managed entity
     * manager.
     * @param puName      Name of persistence unit used to create {@link EntityManager}.
     * @param descriptors Additional persistent classes descriptors. Will not be added when running on application
     *                    server.
     * @param mapped      Build mapped property for database host and port pair.
     * @return New {@link EntityManager} instance.
     */
    public static EntityManager createEntityManager(
            final String puName, final List<ClassDescriptor> descriptors, final boolean mapped) {
        final Map<String, String> emProperties = NoSQLProperties.createEMProperties(mapped);
        if (isOnServer && isJTA) {
            if (serverPlatform != null) {
                final EntityManagerFactory emf = serverPlatform.getEntityManagerFactory(puName);
                return emf.createEntityManager(emProperties);
            } else {
                LOG.log(SessionLog.WARNING, String.format(
                        "ServerPlatform is unknown, using default EntityManagerFactory to create EntityManager"));
                return JUnitTestCase.getEntityManagerFactory(puName, emProperties, descriptors)
                        .createEntityManager((SynchronizationType)null, emProperties);            }
        } else {
            return JUnitTestCase.getEntityManagerFactory(puName, emProperties, descriptors)
                    .createEntityManager((SynchronizationType)null, emProperties);
        }
    }

    /**
     * Create a new entity manager for the persistence unit using the properties. The properties will only be used
     * the first time this entity manager is accessed. If in JEE this will create or return the active managed entity
     * manager.
     * @param puName      Name of persistence unit used to create {@link EntityManager}.
     * @param mapped      Build mapped property for database host and port pair.
     * @return New {@link EntityManager} instance.
     */
    public static EntityManager createEntityManager(final String puName, final boolean mapped) {
        return createEntityManager(puName, null, mapped);
    }

    /**
     * Create a new entity manager for the persistence unit using the properties. The properties will only be used
     * the first time this entity manager is accessed. If in JEE this will create or return the active managed entity
     * manager.
     * @param puName      Name of persistence unit used to create {@link EntityManager}.
     * @return New {@link EntityManager} instance.
     */
    public static EntityManager createEntityManager(final String puName) {
        return createEntityManager(puName, null, false);
    }

    /**
     * Get database session from entity manager.
     * @param entityManager Entity manager holding the database session.
     * @return Database session from entity manager.
     */
    public static DatabaseSessionImpl getDatabaseSession(final EntityManager entityManager) {
        return ((org.eclipse.persistence.jpa.JpaEntityManager)entityManager).getDatabaseSession();
    }

    /**
     * Begin a transaction on the entity manager. This allows the same code to be used on the server where JTA is used,
     * and will join the EntityManager to the transaction.
     * @param entityManager Entity manager holding the transaction.
     */
    public static void beginTransaction(final EntityManager entityManager) {
        if (serverPlatform != null) {
            serverPlatform.beginTransaction();
            serverPlatform.joinTransaction(entityManager);
        } else {
            entityManager.getTransaction().begin();
        }
    }

    /**
     * Commit a transaction on the entity manager. This allows the same code to be used on the server where JTA is used.
     * @param entityManager Entity manager holding the transaction.
     */
    public static void commitTransaction(final EntityManager entityManager) {
        if (serverPlatform != null) {
            serverPlatform.commitTransaction();
        } else {
            entityManager.getTransaction().commit();
        }
    }

    /**
     * Roll back a transaction on the entity manager. This allows the same code to be used on the server where JTA
     * is used.
     * @param entityManager Entity manager holding the transaction.
     */
    public static void rollbackTransaction(final EntityManager entityManager) {
        if (serverPlatform != null) {
            serverPlatform.rollbackTransaction();
        } else {
            entityManager.getTransaction().rollback();
        }
    }

    /**
     * Check if the transaction in provided entity manager is active.
     * This allows the same code to be used on the server where JTA is used.
     * @param entityManager Entity manager holding the transaction.
     */
    public static boolean isTransactionActive(EntityManager entityManager) {
        if (serverPlatform != null) {
            return serverPlatform.isTransactionActive();
        } else {
            return entityManager.getTransaction().isActive();
        }
    }

    /**
     * Close the entity manager.
     * This allows the same code to be used on the server where managed entity managers are not closed.
     * @param entityManager Entity manager to be closed.
     */
    public static void closeEntityManager(EntityManager entityManager) {
        if (!isOnServer) {
            entityManager.close();
        }
    }

    /**
     * Close the entity manager.
     * If a transaction is active, then roll it back.
     * @param entityManager Entity manager to be closed.
     */
    public static void closeEntityManagerAndTransaction(EntityManager entityManager) {
        if (isTransactionActive(entityManager)) {
            rollbackTransaction(entityManager);
        }
        closeEntityManager(entityManager);
    }

    /**
     * Clear cache for current database session.
     * @param entityManager Entity manager holding the the database session.
     */
    public static void clearCache(final EntityManager entityManager) {
        DatabaseSession session = getDatabaseSession(entityManager);
        if (session != null) {
            IdentityMapAccessor accessor = session.getIdentityMapAccessor();
            if (accessor != null) {
                accessor.initializeAllIdentityMaps();
            } else {
                LOG.log(SessionLog.WARNING, "Could not get IdentityMapAccessor from session");
            }
        } else {
            LOG.log(SessionLog.WARNING, "Could not get session from EntityManager");
        }
    }

    /**
     * Compare persistence objects.
     * Test will fail when compared objects differ.
     * @param obj1 First persistence object.
     * @param obj2 Second persistence object.
     * @param entityManager Entity manager holding the the database session.
     */
    public static void compareObjects(Object obj1, Object obj2, final EntityManager entityManager) {
        AbstractSession dbs = getDatabaseSession(entityManager);
        assertTrue("Objects " + obj1 + " and " + obj2 + " are not equal", dbs.compareObjects(obj1, obj2));
    }

}
