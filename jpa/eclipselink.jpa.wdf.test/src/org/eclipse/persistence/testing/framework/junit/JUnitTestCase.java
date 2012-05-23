/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle, SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     SAP - use in eclipselink.jpa.wdf.test package
 ******************************************************************************/
package org.eclipse.persistence.testing.framework.junit;

import java.util.Hashtable;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.server.JEEPlatform;
import org.eclipse.persistence.testing.framework.server.ServerPlatform;

/**
 * This is the superclass for all TopLink JUnit tests Provides convenience methods for transactional access as well as to access
 * login information and to create any sessions required for setup.
 * 
 * Assumes the existence of a test.properties file on the classpath that defines the following properties:
 * 
 * db.platform db.user db.pwd db.url db.driver
 * 
 * If you are using the TestingBrowser, these properties come from the login panel instead. If you are running the test in JEE
 * the properties come from the server config. This class should be used for all EntityManager operations to allow tests to be
 * run in the server.
 */
public abstract class JUnitTestCase {

    private static Map<String, EntityManagerFactory> emfNamedPersistenceUnits = null;

    /** Determine if the test is running on a JEE server, or in JSE. */
    protected static boolean isOnServer = false;

    /** Allow a JEE server platform to be set. */
    protected static ServerPlatform serverPlatform;

    // /** Sets if the test should be run on the client or server. */
    // public Boolean shouldRunTestOnServer;
    //
    // /** System variable to set the tests to run on the server. */
    // public static final String RUN_ON_SERVER = "server.run";
    //
    static {
        emfNamedPersistenceUnits = new Hashtable<String, EntityManagerFactory>();
    }

    /**
     * Return if the test is running on a JEE server, or in JSE.
     */
    public static boolean isOnServer() {
        return isOnServer;
    }

    /**
     * Return the server platform if running in JEE.
     */
    public static ServerPlatform getServerPlatform() {
        if (serverPlatform == null) {
            serverPlatform = new JEEPlatform();
        }
        return serverPlatform;
    }

    /**
     * Close the entity manager. This allows the same code to be used on the server where managed entity managers are not
     * closed.
     */
    public void closeEntityManager(EntityManager entityManager) {
        if (!isOnServer()) {
            entityManager.close();
        }
    }

    /**
     * Return if the transaction is active. This allows the same code to be used on the server where JTA is used.
     */
    public boolean isTransactionActive(EntityManager entityManager) {
        if (isOnServer()) {
            return getServerPlatform().isTransactionActive();
        } else {
            return entityManager.getTransaction().isActive();
        }
    }

    /**
     * Return if the transaction is roll back only. This allows the same code to be used on the server where JTA is used.
     */
    public boolean getRollbackOnly(EntityManager entityManager) {
        if (isOnServer()) {
            return getServerPlatform().getRollbackOnly();
        } else {
            return entityManager.getTransaction().getRollbackOnly();
        }
    }

    /**
     * Begin a transaction on the entity manager. This allows the same code to be used on the server where JTA is used.
     */
    public void beginTransaction(EntityManager entityManager) {
        if (isOnServer()) {
            getServerPlatform().beginTransaction();
        } else {
            entityManager.getTransaction().begin();
        }
    }

    /**
     * Commit a transaction on the entity manager. This allows the same code to be used on the server where JTA is used.
     */
    public void commitTransaction(EntityManager entityManager) {
        if (isOnServer()) {
            getServerPlatform().commitTransaction();
        } else {
            entityManager.getTransaction().commit();
        }
    }

    /**
     * Rollback a transaction on the entity manager. This allows the same code to be used on the server where JTA is used.
     */
    public void rollbackTransaction(EntityManager entityManager) {
        if (isOnServer()) {
            getServerPlatform().rollbackTransaction();
        } else {
            entityManager.getTransaction().rollback();
        }
    }

    /**
     * Create a new entity manager for the "default" persistence unit. If in JEE this will create or return the active managed
     * entity manager.
     */
    public static EntityManager createEntityManager() {
        if (isOnServer()) {
            return getServerPlatform().getEntityManager("default");
        } else {
            return getEntityManagerFactory().createEntityManager();
        }
    }

    /**
     * Create a new entity manager for the persistence unit using the properties. The properties will only be used the first
     * time this entity manager is accessed. If in JEE this will create or return the active managed entity manager.
     */
    public static EntityManager createEntityManager(String persistenceUnitName, Map<String, String> properties) {
        if (isOnServer()) {
            return getServerPlatform().getEntityManager(persistenceUnitName);
        } else {
            return getEntityManagerFactory(persistenceUnitName, properties).createEntityManager();
        }
    }

    public static ServerSession getServerSession() {
        return ((org.eclipse.persistence.jpa.JpaEntityManager) getEntityManagerFactory().createEntityManager())
                .getServerSession();
    }

    public static ServerSession getServerSession(String persistenceUnitName) {
        return ((org.eclipse.persistence.jpa.JpaEntityManager) getEntityManagerFactory(persistenceUnitName)
                .createEntityManager()).getServerSession();
    }

    public static EntityManagerFactory getEntityManagerFactory(String persistenceUnitName) {
        return getEntityManagerFactory(persistenceUnitName, JUnitTestCaseHelper.getDatabaseProperties());
    }

    public static EntityManagerFactory getEntityManagerFactory(String persistenceUnitName, Map<String, String> properties) {
        if (isOnServer()) {
            return getServerPlatform().getEntityManagerFactory(persistenceUnitName);
        } else {
            EntityManagerFactory emfNamedPersistenceUnit = (EntityManagerFactory) emfNamedPersistenceUnits
                    .get(persistenceUnitName);
            if (emfNamedPersistenceUnit == null) {
                emfNamedPersistenceUnit = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
                emfNamedPersistenceUnits.put(persistenceUnitName, emfNamedPersistenceUnit);

                if (getServerSession(persistenceUnitName).getPlatform().isPostgreSQL()) {
                    getServerSession(persistenceUnitName).getLogin().setShouldForceFieldNamesToUpperCase(true);
                }
            }
            return emfNamedPersistenceUnit;
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return getEntityManagerFactory("default");
    }

    public static void closeEntityManagerFactory() {
        closeEntityManagerFactory("default");
    }

    public static void closeEntityManagerFactory(String persistenceUnitName) {
        EntityManagerFactory emfNamedPersistenceUnit = (EntityManagerFactory) emfNamedPersistenceUnits.get(persistenceUnitName);
        if (emfNamedPersistenceUnit != null) {
            if (emfNamedPersistenceUnit.isOpen()) {
                emfNamedPersistenceUnit.close();
            }
            emfNamedPersistenceUnits.remove(persistenceUnitName);
        }
    }

    public static Platform getDbPlatform() {
        return getServerSession().getDatasourcePlatform();
    }
}
