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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.rmi.PortableRemoteObject;

import javax.persistence.*;
import junit.framework.*;

import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.jpa.config.PersistenceUnitProperties;
import org.eclipse.persistence.testing.framework.server.JEEPlatform;
import org.eclipse.persistence.testing.framework.server.ServerPlatform;
import org.eclipse.persistence.testing.framework.server.TestRunner;

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
 * If you are running the test in JEE the properties come from the server config.
 * This class should be used for all EntityManager operations to allow tests to be run in the server.
 */
public abstract class JUnitTestCase extends TestCase {

    private static EntityManagerFactory emf = null;
    private static Map emfNamedPersistenceUnits = null;
    
    /** Determine if the test is running on a JEE server, or in JSE. */
    protected static boolean isOnServer = false;
    
    /** Allow a JEE server platform to be set. */
    protected static ServerPlatform serverPlatform;
    
    /** Environment location for TestRunner session bean. */
    public static String testRunnerName = "java:comp/env/ejb/TestRunner";
    public static String shortTestRunnerName = "ejb/TestRunner";
    
    /** Sets if the test should be run on the client or server. */
    public Boolean shouldRunTestOnServer;
    
    /** System variable to set the tests to run on the server. */
    public static final String RUN_ON_SERVER = "server.run";
    
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
     * Return the name of the persistence context this test uses.
     * This allow a subclass test to set this only in one place.
     */
    public String getPersistenceUnitName() {
        return "default";
    }
    
    /**
     * Return if the test should run on the server.
     */
    public boolean shouldRunTestOnServer() {
        if (shouldRunTestOnServer == null) {
            String property = System.getProperty(RUN_ON_SERVER);
            if (property != null) {
                shouldRunTestOnServer = property.toUpperCase().equals("TRUE");
            } else {
                shouldRunTestOnServer = false;
            }
        }
        return shouldRunTestOnServer;
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
        if (serverPlatform == null) {
            serverPlatform = new JEEPlatform();
        }
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
    public void closeEntityManager(EntityManager entityManager) {
        if (!isOnServer()) {
            entityManager.close();
        }
    }
    
    /**
     * Return if the transaction is active.
     * This allows the same code to be used on the server where JTA is used.
     */
    public boolean isTransactionActive(EntityManager entityManager) {
        if (isOnServer()) {
            return getServerPlatform().isTransactionActive();
        } else {
            return entityManager.getTransaction().isActive();
        }
    }
    
    /**
     * Begin a transaction on the entity manager.
     * This allows the same code to be used on the server where JTA is used.
     */
    public void beginTransaction(EntityManager entityManager) {
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
    public void commitTransaction(EntityManager entityManager) {
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
    public void rollbackTransaction(EntityManager entityManager) {
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
        return ((org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl)getEntityManagerFactory()).getServerSession();               
    }
    
    public static ServerSession getServerSession(String persistenceUnitName){
        return ((org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl)getEntityManagerFactory(persistenceUnitName)).getServerSession();               
    }
    
    public static EntityManagerFactory getEntityManagerFactory(String persistenceUnitName){
        return getEntityManagerFactory(persistenceUnitName,  JUnitTestCaseHelper.getDatabaseProperties());
    }
    
    public static EntityManagerFactory getEntityManagerFactory(String persistenceUnitName, Map properties) {
        if (isOnServer()) {
            return getServerPlatform().getEntityManagerFactory(persistenceUnitName);
        } else {
            EntityManagerFactory emfNamedPersistenceUnit = (EntityManagerFactory)emfNamedPersistenceUnits.get(persistenceUnitName);
            if (emfNamedPersistenceUnit == null){
                emfNamedPersistenceUnit = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
                emfNamedPersistenceUnits.put(persistenceUnitName, emfNamedPersistenceUnit);
            }
            return emfNamedPersistenceUnit;
        }
    }
    
    public static EntityManagerFactory getEntityManagerFactory() {
        if (isOnServer()) {
            return getServerPlatform().getEntityManagerFactory("default");
        } else {
            if (emf == null) {
                emf = Persistence.createEntityManagerFactory("default", JUnitTestCaseHelper.getDatabaseProperties());
            }
            return emf;
        }
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
     * Used to output a warning.  This does not fail the test, but provides output for someone to review.
     */
    public void warning(String warning) {
        System.out.println("WARNING: " + warning);
    }

    /**
     * Intercept test case invocation and delegate it to a remote server.
     */
    public void runBare() throws Throwable {
        if (shouldRunTestOnServer()) {
            runBareClient();
        } else {
            super.runBare();
        }
    }

    /**
     * Runs a test by delegating method invocation to the application server.
     */
    public void runBareClient() throws Throwable {
        Properties properties = new Properties();
        // TODO: make url platform independent.
        String url = System.getProperty("server.url");
        if (url == null) {
            fail("System property 'server.url' must be set.");
        }
        properties.put("java.naming.provider.url", url);
        Context context = new InitialContext(properties);
        TestRunner runner;
        Throwable exception = null;

        try {
            runner = (TestRunner) PortableRemoteObject.narrow(context.lookup(testRunnerName), TestRunner.class);
            exception = runner.runTest(getClass().getName(), getName(), getServerProperties());
        } catch (NameNotFoundException notFoundException) {
            try {
                runner = (TestRunner) PortableRemoteObject.narrow(context.lookup(shortTestRunnerName), TestRunner.class);
                exception = runner.runTest(getClass().getName(), getName(), getServerProperties());
            } catch (NameNotFoundException notFoundException2) {
                exception = new Error("Both lookups failed: " + testRunnerName, notFoundException);
            }
        }
        if (exception != null) {
            throw exception;
        }
    }
    
    public void runBareServer() throws Throwable {
        setIsOnServer(true);
        super.runBare();
    }
    
    /**
     * Used by subclasses to pass any properties into the
     * server's vm.  Should be used with caution!
     */
    protected Properties getServerProperties() {
        return null;
    }
    

}
