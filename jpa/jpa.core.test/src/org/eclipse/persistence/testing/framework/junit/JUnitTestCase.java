/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


package org.eclipse.persistence.testing.framework.junit;

import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;

import junit.framework.*;
import javax.persistence.*;

import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.jpa.config.PersistenceUnitProperties;
import org.eclipse.persistence.testing.framework.ServerPlatform;

/**
 * This is the superclass for all TopLink JUnit tests
 * Provides convenience methods for transactional access as well as to access
 * login information and to create any sessions required for setup.
 *
 * Assumes the existence of a titl.properties file on the classpath that defines the
 * following properties:
 *
 * login.databaseplatform
 * login.username
 * login.password
 * login.databaseURL
 * login.driverClass
 * 
 * If you are using the TestingBrowser, these properties come from the login panel instead.
 * If you are running the test in JEE the properties comr from the server config.
 * This class should be used for all EntityManager operations to allow tests to be run in the server.
 */
public abstract class JUnitTestCase extends TestCase {

    private static EntityManagerFactory emf = null;
    private static Map emfNamedPersistenceUnits = null;
    
    /** Determine if the test is running on a JEE server, or in JSE. */
    protected static boolean isOnServer = false;
    
    /** Allow a JEE server platform to be set. */
    protected static ServerPlatform serverPlatform;
    
    static {
        emfNamedPersistenceUnits = new Hashtable();
    }
    
    public JUnitTestCase() {
        super();
    }

    public JUnitTestCase(String name) {
        super(name);
    }
    
    /**
     * Return if the test is running on a JEE server, or in JSE.
     */
    public static boolean isOnServer() {
        return isOnServer;
    }
    
    /**
     * Set if the test is running on a JEE server, or in JSE.
     */
    public static void setIsOnServer(boolean value) {
        isOnServer = value;
    }
    
    /**
     * Return the server platform if running in JEE.
     */
    public static ServerPlatform getServerPlatform() {
        return serverPlatform;
    }
    
    /**
     * Set the server platform, this should be done by the test executor
     * when running a test in the server.
     */
    public static void setServerPlatform(ServerPlatform value) {
        serverPlatform = value;
    }
    
    public static void clearCache() {
         try {
            getServerSession().getIdentityMapAccessor().initializeAllIdentityMaps();
         } catch (Exception ex) {
            throw new  RuntimeException("An exception occurred trying clear the cache.", ex);
        }   
    }
    
    public static void clearCache(String persistenceUnitName) {
         try {
            getServerSession(persistenceUnitName).getIdentityMapAccessor().initializeAllIdentityMaps();
         } catch (Exception ex) {
            throw new  RuntimeException("An exception occurred trying clear the cache.", ex);
        }   
    }
        
    /**
     * Close the entity manager.
     * This allows the same code to be used on the server where managed entity managers are not closed.
     */
    public static void closeEntityManager(EntityManager entityManager) {
        if (!isOnServer()) {
            entityManager.close();
        }
    }
        
    /**
     * Begin a transaction on the entity manager.
     * This allows the same code to be used on the server where JTA is used.
     */
    public static void beginTransaction(EntityManager entityManager) {
        if (isOnServer()) {
            getServerPlatform().beginTransaction();
        } else {
            entityManager.getTransaction().begin();
        }
    }
        
    /**
     * Commit a transaction on the entity manager.
     * This allows the same code to be used on the server where JTA is used.
     */
    public static void commitTransaction(EntityManager entityManager) {
        if (isOnServer()) {
            getServerPlatform().commitTransaction();
        } else {
            entityManager.getTransaction().commit();
        }
    }
        
    /**
     * Rollback a transaction on the entity manager.
     * This allows the same code to be used on the server where JTA is used.
     */
    public static void rollbackTransaction(EntityManager entityManager) {
        if (isOnServer()) {
            getServerPlatform().rollbackTransaction();
        } else {
            entityManager.getTransaction().rollback();
        }
    }
    
    /**
     * Create a new entity manager for the "default" persistence unit.
     * If in JEE this will create or return the active managed entity manager.
     */
    public static EntityManager createEntityManager() {
        if (isOnServer()) {
            return getServerPlatform().getEntityManager("default");
        } else {
            return getEntityManagerFactory().createEntityManager();
        }
    }

    /**
     * Create a new entity manager for the persistence unit.
     * If in JEE this will create or return the active managed entity manager.
     */
    public static EntityManager createEntityManager(String persistenceUnitName) {
        if (isOnServer()) {
            return getServerPlatform().getEntityManager(persistenceUnitName);
        } else {
            return getEntityManagerFactory(persistenceUnitName).createEntityManager();
        }
    }
    
    /**
     * Create a new entity manager for the persistence unit using the properties.
     * The properties will only be used the first time this entity manager is accessed.
     * If in JEE this will create or return the active managed entity manager.
     */
    public static EntityManager createEntityManager(String persistenceUnitName, Map properties) {
        if (isOnServer()) {
            return getServerPlatform().getEntityManager(persistenceUnitName);
        } else {
            return getEntityManagerFactory(persistenceUnitName, properties).createEntityManager();
        }      
    }

    public static ServerSession getServerSession(){
        return ((org.eclipse.persistence.internal.jpa.EntityManagerImpl)createEntityManager()).getServerSession();               
    }
    
    public static ServerSession getServerSession(String persistenceUnitName){
        return ((org.eclipse.persistence.internal.jpa.EntityManagerImpl)createEntityManager(persistenceUnitName)).getServerSession();               
    }
    
    public static EntityManagerFactory getEntityManagerFactory(String persistenceUnitName){
        return getEntityManagerFactory(persistenceUnitName,  JUnitTestCaseHelper.getDatabaseProperties());
    }
    
    public static EntityManagerFactory getEntityManagerFactory(String persistenceUnitName, Map properties){
        EntityManagerFactory emfNamedPersistenceUnit = (EntityManagerFactory)emfNamedPersistenceUnits.get(persistenceUnitName);
        if (emfNamedPersistenceUnit == null){
            emfNamedPersistenceUnit = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
            emfNamedPersistenceUnits.put(persistenceUnitName, emfNamedPersistenceUnit);
        }
        return emfNamedPersistenceUnit;
    }
    
    public static EntityManagerFactory getEntityManagerFactory(){
        if (emf == null){
            emf = Persistence.createEntityManagerFactory("default", JUnitTestCaseHelper.getDatabaseProperties());
        }
        return emf;
    }
    
    public static boolean doesEntityManagerFactoryExist() {
        return emf != null && emf.isOpen();
    }

    public static void closeEntityManagerFactory() {
        if(emf != null) {
            if(emf.isOpen()) {
                emf.close();
            }
            emf = null;
        }
    }

    public static void closeEntityManagerFactoryNamedPersistenceUnit(String persistenceUnitName) {
        EntityManagerFactory emfNamedPersistenceUnit = (EntityManagerFactory)emfNamedPersistenceUnits.get(persistenceUnitName);
        if(emfNamedPersistenceUnit != null) {
            if(emfNamedPersistenceUnit.isOpen()) {
                emfNamedPersistenceUnit.close();
            }
            emfNamedPersistenceUnits.remove(persistenceUnitName);
        }
    }

    public static Platform getDbPlatform() {
        return getServerSession().getDatasourcePlatform();
    }
   
    public void setUp() {
    }
    
    public void tearDown() {
    }
    
    /**
     * Launch the JUnit TestRunner UI.
     */
    public static void main(String[] args) {
        // Run JUnit.
        junit.swingui.TestRunner.main(args);
    }
}
