/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.framework.junit;

import java.util.Map;
import java.util.Hashtable;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import javax.persistence.*;
import junit.framework.*;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.server.JEEPlatform;
import org.eclipse.persistence.testing.framework.server.ServerPlatform;
import org.eclipse.persistence.testing.framework.server.TestRunner;

/**
 * This is the superclass for all TopLink JUnit tests
 * Provides convenience methods for transactional access as well as to access
 * login information and to create any sessions required for setup.
 *
 * Assumes the existence of a test.properties file on the classpath that defines the
 * following properties:
 *
 * db.platform
 * db.user
 * db.pwd
 * db.url
 * db.driver
 * 
 * If you are using the TestingBrowser, these properties come from the login panel instead.
 * If you are running the test in JEE the properties come from the server config.
 * This class should be used for all EntityManager operations to allow tests to be run in the server.
 */
public abstract class JUnitTestCase extends TestCase {

    private static Map emfNamedPersistenceUnits = null;
    
    /** Determine if the test is running on a JEE server, or in JSE. */
    protected static Boolean isOnServer;
    
    /** Determine if the data-source is JTA, or non-JTA. */
    public static Boolean isJTA =true;
    
    /** Allow a JEE server platform to be set. */
    protected static ServerPlatform serverPlatform;
        
    /** Sets if the test should be run on the client or server. */
    public Boolean shouldRunTestOnServer;
    
    /** System variable to set the tests to run on the server. */
    public static final String RUN_ON_SERVER = "server.run";
    
    static {
        emfNamedPersistenceUnits = new Hashtable();
    }
    
    protected static boolean isInitialzied;
    
    /** Allow OSGi specific behavior. */
    public static boolean isOSGi = false;
    
    /**
     * This is a hack to enable weaving in Spring tests.
     * The Spring agent does not load persistence units in premain
     * So it must be forced to do so before any domain classes are loaded,
     * otherwise weaving will not work.
     */
    public static void initializePlatform() {
        if (isInitialzied) {
            return;
        }
        ServerPlatform platform = getServerPlatform();
        if (platform != null) {
            platform.initialize();
        }
        isInitialzied = true;
    }
    
    public JUnitTestCase() {
        super();
        initializePlatform();
    }

    public JUnitTestCase(String name) {
        super(name);
        initializePlatform();
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
     * Return if the data-source is JTA or not.
     */
    public static boolean isJTA() {
        String property =System.getProperty("is.JTA");
        if (property != null && property.toUpperCase().equals("FALSE")) {
            isJTA = false;
        } else {
            isJTA = true;
        }
        return isJTA;
    }
    
    /**
     * Return if the tests were run using weaving, agent or static.
     */
    public static boolean isWeavingEnabled() {
        if("false".equals(JUnitTestCase.getServerSession().getProperty("eclipselink.weaving"))) {
            return false;
        }
        return System.getProperty("TEST_NO_WEAVING") == null;
    }
    
    /**
     * Return if the tests were run using weaving, agent or static.
     */
    public static boolean isWeavingEnabled(String persistenceUnitName) {
        if("false".equals(JUnitTestCase.getServerSession(persistenceUnitName).getProperty("eclipselink.weaving"))) {
            return false;
        }
        return System.getProperty("TEST_NO_WEAVING") == null;
    }
    
    /**
     * Return if the test is running against JPA 1.0. Any test that uses 2.0
     * functionality should call this method to avoid been run against a 1.0
     * container.
     */
    public static boolean isJPA10() {
        try {
            LockModeType.valueOf("NONE");
        } catch (Exception e) {
           return true;
        }
        
        return false;
    }
    
    /**
     * Return if the test is running on a JEE server, or in JSE.
     */
    public static boolean isOnServer() {
        if (isOnServer == null) {
            if (System.getProperty("TEST_SERVER_PLATFORM") != null) {
                isOnServer = true;
            } else {
                isOnServer = false;                
            }
        }
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
            String platformClass = System.getProperty("TEST_SERVER_PLATFORM");
            if (platformClass == null) {
                serverPlatform = new JEEPlatform();
            } else {
                try {
                    serverPlatform = (ServerPlatform)Class.forName(platformClass).newInstance();
                } catch (Exception notFound) {
                    throw new RuntimeException(notFound);
                }
            }
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
        if (isOnServer() && isJTA()) {
            return getServerPlatform().isTransactionActive();
        } else {
            return entityManager.getTransaction().isActive();
        }
    }

    /**
     * Return if the transaction is roll back only.
     * This allows the same code to be used on the server where JTA is used.
     */
    public boolean getRollbackOnly(EntityManager entityManager) {
        if (isOnServer() && isJTA()) {
            return getServerPlatform().getRollbackOnly();
        } else {
            return entityManager.getTransaction().getRollbackOnly();
        }
    }
    
    /**
     * Begin a transaction on the entity manager.
     * This allows the same code to be used on the server where JTA is used.
     */
    public void beginTransaction(EntityManager entityManager) {
        if (isOnServer() && isJTA()) {
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
        if (isOnServer() && isJTA()) {
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
        if (isOnServer() && isJTA()) {
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
        if (isOnServer() && isJTA()) {
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
        if (isOnServer() && isJTA()) {
            return getServerPlatform().getEntityManager(persistenceUnitName);
        } else {
            return getEntityManagerFactory(persistenceUnitName).createEntityManager();
        }
    }

    /**
     * Create a new entity manager for the persistence unit using the properties 
     * and a default persistence unit name..
     * The properties will only be used the first time this entity manager is accessed.
     * If in JEE this will create or return the active managed entity manager.
     */
    public static EntityManager createEntityManager(Map properties) {
        if (isOnServer()) {
            return getServerPlatform().getEntityManager("default");
        } else {
        	// Set properties on both the em factory and the em
            return getEntityManagerFactory("default", properties).createEntityManager(properties);
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

    public static ServerSession getServerSession() {
        return ((org.eclipse.persistence.jpa.JpaEntityManager)getEntityManagerFactory().createEntityManager()).getServerSession();               
    }
    
    public static ServerSession getServerSession(String persistenceUnitName) {
        return ((org.eclipse.persistence.jpa.JpaEntityManager)getEntityManagerFactory(persistenceUnitName).createEntityManager()).getServerSession();        
    }
    
    public static EntityManagerFactory getEntityManagerFactory(String persistenceUnitName) {
        return getEntityManagerFactory(persistenceUnitName,  JUnitTestCaseHelper.getDatabaseProperties());
    }
    
    public static EntityManagerFactory getEntityManagerFactory(String persistenceUnitName, Map properties) {
        if (isOnServer()) {
            return getServerPlatform().getEntityManagerFactory(persistenceUnitName);
        } else {
            // Set class loader for OSGi testing.
            if (isOSGi && (properties.get(PersistenceUnitProperties.CLASSLOADER) == null)) {
                try {
                    properties.put(PersistenceUnitProperties.CLASSLOADER, JUnitTestCase.class.getClassLoader());
                } catch (Exception ignore) {
                    System.out.println(ignore);
                }
            }
            EntityManagerFactory emfNamedPersistenceUnit = (EntityManagerFactory)emfNamedPersistenceUnits.get(persistenceUnitName);
            if (emfNamedPersistenceUnit == null){
                emfNamedPersistenceUnit = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
                emfNamedPersistenceUnits.put(persistenceUnitName, emfNamedPersistenceUnit);

                // Force uppercase for Postgres.
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
    
    public static EntityManagerFactory getEntityManagerFactory(Map properties) {
        return getEntityManagerFactory("default", properties);
    }
    
    public static boolean doesEntityManagerFactoryExist() {
        return doesEntityManagerFactoryExist("default");
    }

    public static boolean doesEntityManagerFactoryExist(String persistenceUnitName) {
        EntityManagerFactory emf = (EntityManagerFactory)emfNamedPersistenceUnits.get(persistenceUnitName);
        return emf != null && emf.isOpen();
    }

    public static void closeEntityManagerFactory() {
        closeEntityManagerFactory("default");
    }

    public static void closeEntityManagerFactory(String persistenceUnitName) {
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
        String url = System.getProperty("server.url");
        if (url == null) {
            fail("System property 'server.url' must be set.");
        }
        properties.put("java.naming.provider.url", url);
        Context context = new InitialContext(properties);
        TestRunner runner;
        Throwable exception = null;
        String testrunner = System.getProperty("server.testrunner");
        if (testrunner == null) {
            fail("System property 'server.testrunner' must be set.");
        }


        runner = (TestRunner) PortableRemoteObject.narrow(context.lookup(testrunner), TestRunner.class);
        exception = runner.runTest(getClass().getName(), getName(), getServerProperties());
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
     * server's vm.  Should be used with caution.
     */
    protected Properties getServerProperties() {
        return null;
    }
    
    /**
     * Verifies that the object was merged to the cache, and written to the database correctly.
     */
    public void verifyObject(Object writtenObject) {
        Object readObject = getServerSession().readObject(writtenObject);
        if (!getServerSession().compareObjects(readObject, writtenObject)) {
            fail("Object: " + readObject + " does not match object that was written: " + writtenObject + ". See log (on finest) for what did not match.");
        }
    }

    
    /**
     * Verifies that the object was merged to the cache, and written to the database correctly.
     */
    public void verifyObjectInCacheAndDatabase(Object writtenObject) {
        Object readObject = getServerSession().readObject(writtenObject);
        if (!getServerSession().compareObjects(readObject, writtenObject)) {
            fail("Object from cache: " + readObject + " does not match object that was written: " + writtenObject + ". See log (on finest) for what did not match.");
        }
        clearCache();
        readObject = getServerSession().readObject(writtenObject);
        if (!getServerSession().compareObjects(readObject, writtenObject)) {
            fail("Object from database: " + readObject + " does not match object that was written: " + writtenObject + ". See log (on finest) for what did not match.");
        }
    }
    
    /**
     * Allow printing off stack traces for exceptions that cause test failures when the session log level is set appropriately.  
     * Logs at at the warning level
     */
    public void logThrowable(Throwable exception){
        getServerSession().getSessionLog().logThrowable(SessionLog.WARNING, exception);
    }

    /**
     * Return if pessimistic locking/select for update is supported for this test platform.
     * Currently testing supports select for update on Oracle, MySQL, SQLServer, TimesTen.
     * Some of the other platforms may have some support for select for update, but the databases we test with
     * for these do not have sufficient support to pass the tests.
     * TODO: Need to recheck tests on DB2 as it has some support for this.
     * Derby has some support, but does not work with joins (2008-12-01).
     */
    public boolean isSelectForUpateSupported(){
        return isSelectForUpateSupported("default");
    }

    public boolean isSelectForUpateSupported(String puName) {
        DatabasePlatform platform = getServerSession(puName).getPlatform();
        // DB2, Derby, Symfoware and Firebird support pessimistic locking only for a single-table queries.
        // PostgreSQL supports for update, but not on outerjoins, which the test uses.
        // H2 supports pessimistic locking, but has table lock issues with multiple connections used in the tests.
        if (platform.isFirebird() || platform.isH2() || platform.isHSQL() || platform.isAccess() || platform.isSQLAnywhere() || platform.isDerby() || platform.isPostgreSQL() || platform.isSymfoware()) {
            warning("This database does not support FOR UPDATE.");
            return false;
        }
        return true;
    }

    /**
     * @return true if database supports pessimistic write lock false other wise
     */
    public boolean isPessimisticWriteLockSupported() {
        DatabasePlatform platform = getServerSession().getPlatform();
        if (platform.isSybase()) { //Sybase supports getting Pessimistic Read locks but does not support getting Perssimistic Write locks
            warning("This database does not support Pessimistic Write Lock.");
            return false;
        }
        return true;
    }


    /**
     * Return if pessimistic locking/select for update nowait is supported for this test platform.
     * Currently testing supports nowait on Oracle, SQLServer.
     * PostgreSQL also supports NOWAIT, but doesn't support the outer joins used in the tests.
     */
    public boolean isSelectForUpateNoWaitSupported(){
        return isSelectForUpateNoWaitSupported("default");
    }

    public boolean isSelectForUpateNoWaitSupported(String puName) {
        DatabasePlatform platform = getServerSession(puName).getPlatform();
        if (!(platform.isOracle() || platform.isSQLServer())) {
            warning("This database does not support NOWAIT.");
            return false;
        }
        return true;
    }
    
    /**
     * Return if stored procedures are supported for the database platform for the test database.
     */
    public boolean supportsStoredProcedures(){
        return supportsStoredProcedures("default");
    }

    public boolean supportsStoredProcedures(String puName) {
        DatabasePlatform platform = getServerSession(puName).getPlatform();
        // PostgreSQL has some level of support for "stored functions", but output parameters do not work as of 8.2.
        // TODO: DB2 should be in this list.
        if (platform.isOracle() || platform.isSybase() || platform.isMySQL() || platform.isSQLServer() || platform.isSymfoware()) {
            return true;
        }
        warning("This database does not support stored procedure creation.");
        return false;
    }
}
