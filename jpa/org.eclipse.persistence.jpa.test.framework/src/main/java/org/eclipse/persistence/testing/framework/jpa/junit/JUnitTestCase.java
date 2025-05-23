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
//     11/17/2010-2.2 Guy Pelletier
//       - 329008: Support dynamic context creation without persistence.xml
//     01/23/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     09/11/2017-2.1 Will Dazey
//       - 520387: multiple owning descriptors for an embeddable are not set
package org.eclipse.persistence.testing.framework.jpa.junit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import junit.framework.TestCase;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Connector;
import org.eclipse.persistence.sessions.DefaultConnector;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.jpa.server.JEEPlatform;
import org.eclipse.persistence.testing.framework.jpa.server.ServerPlatform;
import org.eclipse.persistence.testing.framework.jpa.server.TestRunner;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.transaction.JTA11TransactionController;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This is the superclass for all EclipseLink JUnit tests
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

    private static Map<String, EntityManagerFactory> emfNamedPersistenceUnits = null;

    /** Default persistence unit name. */
    private static final String DEFAULT_PU_NAME = "default";

    /** Determine if the test is running on a JEE server, or in JSE. */
    protected static Boolean isOnServer;

    /** Determine if the data-source is JTA, or non-JTA. */
    public static Boolean isJTA =true;

    /** Holds an information whether JTA 1.1 API is present. */
    protected final boolean isJTA11;

    /** Allow a JEE server platform to be set. */
    protected static ServerPlatform serverPlatform;

    /** Sets if the test should be run on the client or server. */
    public Boolean shouldRunTestOnServer;

    /** System variable to set the tests to run on the server. */
    public static final String RUN_ON_SERVER = "server.run";

    /** Persistence unit name associated with the test runner, null means single persistence unit */
    public String puName = null;

    static {
        emfNamedPersistenceUnits = new Hashtable<>();
    }

    protected static boolean isInitialzied;

    /** Allow OSGi specific behavior. */
    public static boolean isOSGi = false;

    /** Indicates whether SOP should be used */
    public static Boolean usesSOP;

    /** Indicates whether SOP should be recoverable. Ignored unless useSOP is true */
    public static Boolean isSOPRecoverable;

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

    /**
     * Check that JTA 1.1 API is present.
     *
     * @return value of {@code true} if {@code java:comp/TransactionSynchronizationRegistry}
     *         JNDI name is present or {@code false}  otherwise.
     */
    public static final boolean isJTA11() {
        Context context = null;
        try {
            context = new InitialContext();
            return (context.lookup(JTA11TransactionController.JNDI_TRANSACTION_SYNCHRONIZATION_REGISTRY) != null);
        } catch (NamingException ex) {
            AbstractSessionLog.getLog().log(SessionLog.FINER, "JTA 1.1 API was not found", null, false);
            return false;
        } finally {
            if (context != null) {
                try {
                    context.close();
                } catch (NamingException ex) {
                    AbstractSessionLog.getLog().log(SessionLog.WARNING, "NamingException when closing initial context: {0}", new String[] { ex.getMessage() }, false);
                }
            }
        }
    }

    /**
     * Lookup EJB by JNDI name.
     *
     * @param c EJB class.
     * @param jndi EJB JNDI name
     * @return EJB instance
     */
    public static <C> C lookupEJB(final Class<C> c, final String jndi) {
        Context context = null;
        try {
            context = new InitialContext();
            return c.cast(context.lookup(jndi));
        } catch (NamingException ex) {
            AbstractSessionLog.getLog().log(SessionLog.WARNING, "NamingException when looking up {0} {1}", new String[] { jndi, ex.getMessage() }, false);
            return null;
        } finally {
            if (context != null) {
                try {
                    context.close();
                } catch (NamingException ex) {
                    AbstractSessionLog.getLog().log(SessionLog.WARNING, "NamingException when closing initial context: {0}", new String[] { ex.getMessage() }, false);
                }
            }
        }
    }

    public JUnitTestCase() {
        super();
        isJTA11 = isJTA11();
        initializePlatform();
    }

    public JUnitTestCase(String name) {
        super(name);
        isJTA11 = isJTA11();
        initializePlatform();
    }

    /**
     * Return the name of the persistence context this test uses.
     * This allow a subclass test to set this only in one place.
     */
    public String getPersistenceUnitName() {
        return DEFAULT_PU_NAME;
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

    public boolean isWeavingForChangeTrackingEnabled() {
        return isWeavingForChangeTrackingEnabled(getPersistenceUnitName());
    }

    public boolean isWeavingForChangeTrackingEnabled(String persistenceUnitName) {
        Object changeTrackingWeaving = JUnitTestCase.getDatabaseSession(persistenceUnitName).getProperty("eclipselink.weaving.changetracking");

        if (changeTrackingWeaving == null) {
            changeTrackingWeaving = System.getProperty("eclipselink.weaving.changetracking");
        }

        if ("false".equals(changeTrackingWeaving)) {
            return false;
        }

        return true;
    }

    public boolean isWeavingForFetchGroupsEnabled() {
        return isWeavingForFetchGroupsEnabled(getPersistenceUnitName());
    }

    public boolean isWeavingForFetchGroupsEnabled(String persistenceUnitName) {
        Object fetchGroupsWeaving = JUnitTestCase.getDatabaseSession(persistenceUnitName).getProperty("eclipselink.weaving.fetchgroups");

        if (fetchGroupsWeaving == null) {
            fetchGroupsWeaving = System.getProperty("eclipselink.weaving.fetchgroups");
        }

        if ("false".equals(fetchGroupsWeaving)) {
            return false;
        }

        return true;
    }

    /**
     * Return if the tests were run using weaving, agent or static.
     */
    public boolean isWeavingEnabled() {
        return isWeavingEnabled(getPersistenceUnitName());
    }

    /**
     * Return if the tests were run using weaving, agent or static.
     */
    public static boolean isWeavingEnabled(String persistenceUnitName) {
        return System.getProperty("TEST_NO_WEAVING") == null;
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
     * Return if the Hermes parser is being used for JPQL.
     */
    public boolean isHermesParser() {
        return getDatabaseSession().getQueryBuilder().getClass().getName().contains("Hermes");
    }

    /**
     * Indicates whether SOP should be used.
     */
    public static boolean usesSOP() {
        if (usesSOP == null) {
            usesSOP = Boolean.valueOf(System.getProperty("sop"));
        }
        return usesSOP;
    }

    /**
     * Indicates whether SOP should be recoverable. Ignored unless useSOP is true.
     */
    public static boolean isSOPRecoverable() {
        if (isSOPRecoverable == null) {
            isSOPRecoverable = Boolean.valueOf(System.getProperty("sop.recoverable"));
        }
        return isSOPRecoverable;
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
                    serverPlatform = (ServerPlatform)Class.forName(platformClass).getConstructor().newInstance();
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

    public void clearCache() {
        try {
            getDatabaseSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        } catch (Exception ex) {
            throw new RuntimeException("An exception occurred trying clear the database session cache.", ex);
        }
    }

    public void clearServerSessionCache() {
        try {
            getServerSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        } catch (Exception ex) {
            throw new RuntimeException("An exception occurred trying clear the server session cache.", ex);
        }
    }

    public static void clearCache(String persistenceUnitName) {
        try {
            getDatabaseSession(persistenceUnitName).getIdentityMapAccessor().initializeAllIdentityMaps();
        } catch (Exception ex) {
            throw new RuntimeException("An exception occurred trying clear the database session cache.", ex);
        }
    }

    public static void clearServerSessionCache(String persistenceUnitName) {
        try {
            getServerSession(persistenceUnitName).getIdentityMapAccessor().initializeAllIdentityMaps();
        } catch (Exception ex) {
            throw new RuntimeException("An exception occurred trying clear the server session cache.", ex);
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
     * Close the entity manager.
     * If a transaction is active, then roll it back.
     */
    public void closeEntityManagerAndTransaction(EntityManager entityManager) {
        if (isTransactionActive(entityManager)) {
            rollbackTransaction(entityManager);
        }
        closeEntityManager(entityManager);
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
     * This allows the same code to be used on the server where JTA is used,
     * and will join the EntityManager to the transaction.
     */
    public void beginTransaction(EntityManager entityManager) {
        if (isOnServer() && isJTA()) {
            getServerPlatform().beginTransaction();
            //bug 404294 - the EM is required to join the transaction to be able to
            //    use transactions started after it was created.
            getServerPlatform().joinTransaction(entityManager);
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
     * Create a new entity manager for the test suites default persistence unit.
     * If in JEE this will create or return the active managed entity manager.
     */
    public EntityManager createEntityManager() {
        if (isOnServer() && isJTA()) {
            return getServerPlatform().getEntityManager(getPersistenceUnitName());
        } else {
            return getEntityManagerFactory(getPersistenceUnitName(), getPersistenceProperties()).createEntityManager();
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
    public EntityManager createEntityManager(Map properties) {
        if (isOnServer() && isJTA()) {
            return getServerPlatform().getEntityManager(getPersistenceUnitName());
        } else {
            return getEntityManagerFactory(getPersistenceUnitName(), properties).createEntityManager(properties);
        }
    }

    /**
     * Create a new entity manager for the persistence unit using the properties.
     * The properties will only be used the first time this entity manager is accessed.
     * If in JEE this will create or return the active managed entity manager.
     */
    public static EntityManager createEntityManager(String persistenceUnitName, Map properties) {
        return createEntityManager(persistenceUnitName, properties, null);
    }

    /**
     * Create a new entity manager for the persistence unit using the properties.
     * The properties will only be used the first time this entity manager is accessed.
     * If in JEE this will create or return the active managed entity manager.
     */
    public static EntityManager createEntityManager(String persistenceUnitName, Map properties, List<ClassDescriptor> descriptors) {
        if (isOnServer() && isJTA()) {
            return getServerPlatform().getEntityManager(persistenceUnitName);
        } else {
            return getEntityManagerFactory(persistenceUnitName, properties, descriptors).createEntityManager();
        }
    }

    public DatabaseSessionImpl getDatabaseSession() {
        return getEntityManagerFactory().createEntityManager().unwrap(JpaEntityManager.class).getDatabaseSession();
    }

    public static DatabaseSessionImpl getDatabaseSession(String persistenceUnitName) {
        return getEntityManagerFactory(persistenceUnitName).createEntityManager().unwrap(JpaEntityManager.class).getDatabaseSession();
    }

    public SessionBroker getSessionBroker() {
        return getEntityManagerFactory().createEntityManager().unwrap(JpaEntityManager.class).getSessionBroker();
    }

    public static SessionBroker getSessionBroker(String persistenceUnitName) {
        return getEntityManagerFactory(persistenceUnitName).createEntityManager().unwrap(JpaEntityManager.class).getSessionBroker();
    }

    public static ServerSession getServerSession() {
        return getEntityManagerFactory("default").createEntityManager().unwrap(JpaEntityManager.class).getServerSession();
    }

    public static ServerSession getServerSession(String persistenceUnitName) {
        return getEntityManagerFactory(persistenceUnitName).createEntityManager().unwrap(JpaEntityManager.class).getServerSession();
    }

    public static ServerSession getServerSession(String persistenceUnitName, Map properties) {
        return getEntityManagerFactory(persistenceUnitName, properties).createEntityManager().unwrap(JpaEntityManager.class).getServerSession();
    }

    public ServerSession getPersistenceUnitServerSession() {
        return getServerSession(getPersistenceUnitName());
    }

    public Map getPersistenceProperties() {
        return JUnitTestCaseHelper.getDatabaseProperties(getPersistenceUnitName());
    }

    public static EntityManagerFactory getEntityManagerFactory(String persistenceUnitName) {
        return getEntityManagerFactory(persistenceUnitName,  JUnitTestCaseHelper.getDatabaseProperties(persistenceUnitName), null);
    }

    public static EntityManagerFactory getEntityManagerFactory(String persistenceUnitName, Map properties) {
        return getEntityManagerFactory(persistenceUnitName, properties, null);
    }

    public static EntityManagerFactory getEntityManagerFactory(String persistenceUnitName, Map properties, List<ClassDescriptor> descriptors) {
        //properties.put("eclipselink.tuning", "ExaLogic");
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

            EntityManagerFactory emfNamedPersistenceUnit = emfNamedPersistenceUnits.get(persistenceUnitName);

            if (emfNamedPersistenceUnit == null) {

                // force closing of other persistence units to avoid Sybase running out of connections.
                if (!persistenceUnitName.equals("default")) {
                    if (emfNamedPersistenceUnits.containsKey("default") && (getServerSession().getPlatform().isSybase() || getServerSession().getPlatform().isMySQL())) {
                        Iterator<Map.Entry<String, EntityManagerFactory>> factories = emfNamedPersistenceUnits.entrySet().iterator();
                        while (factories.hasNext()) {
                            Map.Entry<String, EntityManagerFactory> entry = factories.next();
                            if (!entry.getKey().equals("default") && !entry.getKey().equals(persistenceUnitName)) {
                                System.out.println("Closing factory: " + entry.getKey());
                                entry.getValue().close();
                                factories.remove();
                            }
                        }
                    }
                }

                if (descriptors == null) {
                    emfNamedPersistenceUnit = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
                } else {
                    emfNamedPersistenceUnit = new EntityManagerFactoryImpl(persistenceUnitName, properties, descriptors);
                }

                emfNamedPersistenceUnits.put(persistenceUnitName, emfNamedPersistenceUnit);

                // Force uppercase for Postgres. - no longer needed with fix for 299926: Case insensitive table / column matching
            }

            return emfNamedPersistenceUnit;
        }
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return getEntityManagerFactory(getPersistenceUnitName());
    }

    public EntityManagerFactory getEntityManagerFactory(Map properties) {
        return getEntityManagerFactory(getPersistenceUnitName(), properties);
    }

    public boolean doesEntityManagerFactoryExist() {
        return doesEntityManagerFactoryExist(getPersistenceUnitName());
    }

    public static boolean doesEntityManagerFactoryExist(String persistenceUnitName) {
        EntityManagerFactory emf = emfNamedPersistenceUnits.get(persistenceUnitName);
        return emf != null && emf.isOpen();
    }

    public void closeEntityManagerFactory() {
        closeEntityManagerFactory(getPersistenceUnitName());
    }

    public static void closeEntityManagerFactory(String persistenceUnitName) {
        EntityManagerFactory emfNamedPersistenceUnit = emfNamedPersistenceUnits.get(persistenceUnitName);
        if(emfNamedPersistenceUnit != null) {
            if(emfNamedPersistenceUnit.isOpen()) {
                emfNamedPersistenceUnit.close();
            }
            emfNamedPersistenceUnits.remove(persistenceUnitName);
        }
    }

    public Platform getPlatform() {
        return getPlatform(getPersistenceUnitName());
    }

    public Platform getPlatform(Class<?> cls) {
        return getPlatform(getPersistenceUnitName(), cls);
    }

    public static Platform getPlatform(String puName) {
        return getDatabaseSession(puName).getPlatform();
    }

    public static Platform getPlatform(String puName, Class<?> cls) {
        return getDatabaseSession(puName).getPlatform(cls);
    }

    @Override
    public void setUp() {
    }

    @Override
    public void tearDown() {
    }

    /**
     * Used to output a warning.  This does not fail the test, but provides output for someone to review.
     */
    public static void warning(String warning) {
        System.out.println("WARNING: " + warning);
    }

    /**
     * Intercept test case invocation and delegate it to a remote server.
     */
    @Override
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
        properties.put(Context.PROVIDER_URL, url);
        String tmpUsr = System.getProperty("server.usr");
        if (tmpUsr != null && !tmpUsr.isEmpty()) {
            properties.put(Context.SECURITY_PRINCIPAL, tmpUsr);
        }
        String tmpPwd = System.getProperty("server.pwd");
        if (tmpPwd != null && !tmpPwd.isEmpty()) {
            properties.put(Context.SECURITY_CREDENTIALS, tmpPwd);
        }
        Context context = new InitialContext(properties);

        String testrunnerCtx = System.getProperty("server.testrunner.context");
        if (testrunnerCtx != null) {
            //find all test runners in given JNDI context
            final NamingEnumeration<NameClassPair> ctx = context.list(testrunnerCtx);
            List<String> testRunners = new ArrayList<>();
            while (ctx.hasMoreElements()) {
                final NameClassPair pair = ctx.next();
                final String clsName = pair.getClassName();
                final String name = pair.getName();
                if (name.contains("framework") && name.contains("TestRunner") ||
                   (clsName.contains("framework") && clsName.contains("TestRunner")) ||
                   (name.contains("TestRunner") && isTestRunnerInstance(context, testrunnerCtx, name))
                ) {
                    testRunners.add(name);
                }
            }
            if (testRunners.isEmpty()) {
                throw new RuntimeException("No TestRunner found");
            }
            if (testRunners.size() > 1) {
                Iterator<String> it = testRunners.iterator();
                while (it.hasNext()) {
                    String r = it.next();
                    if (r.contains("GenericTestRunner")) {
                        it.remove();
                    }
                }
            }
            for (String runner : testRunners) {
                Throwable t = null;
                TestRunner runnerBean = (TestRunner) context.lookup(testrunnerCtx + "/" + runner);
                t = runnerBean.runTest(getClass().getName(), getName(), getServerProperties());
                if (t != null) {
                    throw t;
                }
            }
        } else {
            fail("System property 'server.testrunner.context' must be set.");
        }
    }

    private boolean isTestRunnerInstance(Context context, String testRunnerCtxName, String nodeName) {
        try {
            return context.lookup(testRunnerCtxName + "/" + nodeName) instanceof TestRunner;
        }
        catch (Exception e) {
            return false;
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
        verifyObject(writtenObject, getPersistenceUnitName());
    }

    /**
     * Verifies that the object was merged to the cache, and written to the database correctly.
     */
    public static void verifyObject(Object writtenObject, String persistenceUnit) {
        Object readObject = getDatabaseSession(persistenceUnit).readObject(writtenObject);
        if (!getDatabaseSession(persistenceUnit).compareObjects(readObject, writtenObject)) {
            fail("Object: " + readObject + " does not match object that was written: " + writtenObject + ". See log (on finest) for what did not match.");
        }
    }

    /**
     * Verifies the object in a new EntityManager.
     */
    public void verifyObjectInEntityManager(Object writtenObject) {
        verifyObjectInEntityManager(writtenObject, getPersistenceUnitName());
    }

    /**
     * Verifies the object in a new EntityManager.
     */
    public void verifyObjectInEntityManager(Object writtenObject, String persistenceUnit) {
        EntityManager em = createEntityManager(persistenceUnit);
        try {
            Object readObject = em.find(writtenObject.getClass(), getServerSession(persistenceUnit).getId(writtenObject));
            if (!getServerSession(persistenceUnit).compareObjects(readObject, writtenObject)) {
                fail("Object: " + readObject + " does not match object that was written: " + writtenObject + ". See log (on finest) for what did not match.");
            }
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Verifies that the object was merged to the cache, and written to the database correctly.
     */
    public void verifyObjectInCacheAndDatabase(Object writtenObject) {
        verifyObjectInCacheAndDatabase(writtenObject, getPersistenceUnitName());
    }

    /**
     * Verifies that the object was merged to the cache, and written to the database correctly.
     */
    public static void verifyObjectInCacheAndDatabase(Object writtenObject, String persistenceUnit) {
        AbstractSession dbs = getDatabaseSession(persistenceUnit);
        Object readObject = dbs.readObject(writtenObject);
        if (!dbs.compareObjects(readObject, writtenObject)) {
            SessionLog oldLog = dbs.getSessionLog();
            dbs.setSessionLog(new DefaultSessionLog());
            dbs.setLogLevel(SessionLog.FINEST);
            StringWriter newLog = new StringWriter();
            dbs.setLog(newLog);
            dbs.compareObjects(readObject, writtenObject);
            dbs.setSessionLog(oldLog);
            fail("Object from cache: " + readObject + " does not match object that was written: " + writtenObject + ". " + newLog);
        }
        dbs.getIdentityMapAccessor().initializeAllIdentityMaps();
        readObject = dbs.readObject(writtenObject);
        if (!dbs.compareObjects(readObject, writtenObject)) {
            SessionLog oldLog = dbs.getSessionLog();
            dbs.setSessionLog(new DefaultSessionLog());
            dbs.setLogLevel(SessionLog.FINEST);
            StringWriter newLog = new StringWriter();
            dbs.setLog(newLog);
            dbs.compareObjects(readObject, writtenObject);
            dbs.setSessionLog(oldLog);
            fail("Object from database: " + readObject + " does not match object that was written: " + writtenObject + ". " + newLog);
        }
    }

    /**
     * Compare objects.
     */
    public void compareObjects(Object obj1, Object obj2) {
        compareObjects(obj1, obj2, getPersistenceUnitName());
    }

    /**
     * Compare objects.
     */
    public static void compareObjects(Object obj1, Object obj2, String persistenceUnit) {
        AbstractSession dbs = getDatabaseSession(persistenceUnit);
        if (!dbs.compareObjects(obj1, obj2)) {
            fail("Objects " + obj1 + " and " + obj2 + " are not equal. See log (on finest) for what did not match.");
        }
    }

    /**
     * Generic persist test.
     */
    public void verifyPersist(Object object) {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            em.persist(object);
            commitTransaction(em);
            beginTransaction(em);
            verifyObjectInCacheAndDatabase(object);
            rollbackTransaction(em);
            closeEntityManager(em);
        } catch (RuntimeException exception) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw exception;
        }
    }

    /**
     * Generic remove test.
     */
    public void verifyPersistAndRemove(Object object) {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            em.persist(object);
            commitTransaction(em);
            closeEntityManager(em);

            em = createEntityManager();
            beginTransaction(em);
            object = em.find(object.getClass(), getServerSession(getPersistenceUnitName()).getId(object));
            em.remove(object);
            commitTransaction(em);
            verifyDelete(object);
            closeEntityManager(em);
        }  catch (RuntimeException exception) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw exception;
        }
    }

    /**
     * Verifies that the object was deleted from the database correctly.
     */
    public void verifyDelete(Object writtenObject) {
        verifyDelete(writtenObject, getPersistenceUnitName());
    }

    /**
     * Verifies that the object was deleted from the database correctly.
     */
    public void verifyDelete(Object writtenObject, String persistenceUnit) {
        AbstractSession dbs = getDatabaseSession(persistenceUnit);
        boolean ok;
        if (dbs.isServerSession()) {
            ok = ((ServerSession)dbs).acquireClientSession().verifyDelete(writtenObject);
        } else if (dbs.isSessionBroker()) {
            ok = ((SessionBroker)dbs).acquireClientSessionBroker().verifyDelete(writtenObject);
        } else {
            ok = dbs.verifyDelete(writtenObject);
        }
        if (!ok) {
            fail("Object not deleted from the database correctly: " + writtenObject);
        }
    }

    /**
     * Allow printing off stack traces for exceptions that cause test failures when the session log level is set appropriately.
     * Logs at at the warning level
     */
    public void logThrowable(Throwable exception){
        getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, exception);
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
        return isSelectForUpateSupported(getPersistenceUnitName());
    }

    public static boolean isSelectForUpateSupported(String puName) {
        AbstractSession dbSession = getDatabaseSession(puName);
        if (dbSession.isBroker()) {
            for (AbstractSession memberSession : ((SessionBroker)dbSession).getSessionsByName().values()) {
                if (!isSelectForUpateSupported(memberSession.getPlatform())) {
                    return false;
                }
            }
            return true;
        } else {
            return isSelectForUpateSupported(dbSession.getPlatform());
        }
    }

    public static boolean isSelectForUpateSupported(DatabasePlatform platform) {
        // DB2, Derby, Symfoware (bug 304903) and Firebird support pessimistic locking only for a single-table queries.
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
        return isPessimisticWriteLockSupported(getPersistenceUnitName());
    }

    public static boolean isPessimisticWriteLockSupported(String puName) {
        AbstractSession dbSession = getDatabaseSession(puName);
        if (dbSession.isBroker()) {
            for(AbstractSession memberSession : ((SessionBroker)dbSession).getSessionsByName().values()) {
                if (!isPessimisticWriteLockSupported(memberSession.getPlatform())) {
                    return false;
                }
            }
            return true;
        } else {
            return isPessimisticWriteLockSupported(dbSession.getPlatform());
        }
    }

    public static boolean isPessimisticWriteLockSupported(DatabasePlatform platform) {
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
    public static boolean isSelectForUpateNoWaitSupported(String puName) {
        AbstractSession dbSession = getDatabaseSession(puName);
        if (dbSession.isBroker()) {
            for (AbstractSession memberSession : ((SessionBroker)dbSession).getSessionsByName().values()) {
                if (!isSelectForUpateNoWaitSupported(memberSession.getPlatform())) {
                    return false;
                }
            }
            return true;
        } else {
            return isSelectForUpateNoWaitSupported(dbSession.getPlatform());
        }
    }
    public boolean isSelectForUpateNoWaitSupported(){
        return isSelectForUpateNoWaitSupported(getPersistenceUnitName());
    }

    public static boolean isSelectForUpateNoWaitSupported(Platform platform) {
        if (platform.isOracle() || platform.isSQLServer()) {
            return true;
        }
        warning("This database does not support NOWAIT.");
        return false;
    }

    public static boolean supportsSequenceObjects(String puName) {
        DatabasePlatform platform = getDatabaseSession(puName).getPlatform();
        return platform.supportsSequenceObjects();
    }

    public boolean supportsSequenceObjects() {
        DatabasePlatform platform = getDatabaseSession(getPersistenceUnitName()).getPlatform();
        return platform.supportsSequenceObjects();
    }

    /**
     * Return if stored procedures are supported for the database platform for the test database.
     */
    public boolean supportsStoredProcedures(){
        return supportsStoredProcedures(getPersistenceUnitName());
    }

    public static boolean supportsStoredProcedures(String puName) {
        DatabasePlatform platform = getDatabaseSession(puName).getPlatform();
        // PostgreSQL has some level of support for "stored functions", but output parameters do not work as of 8.2.
        // Sybase supports stored procedures, but has issues with output parameters and transaction mode (INOUT and chaining).
        // TODO: DB2 should be in this list.
        if (platform.isOracle() || platform.isMySQL() || platform.isSQLServer()) {
            return true;
        }

        warning("This database does not support stored procedure creation.");
        return false;
    }

    /**
     * Return if stored functions are supported for the database platform for the test database.
     */
    public boolean supportsStoredFunctions(){
        return supportsStoredFunctions(getPersistenceUnitName());
    }

    public static boolean supportsStoredFunctions(String puName) {
        DatabasePlatform platform = getDatabaseSession(puName).getPlatform();
        // PostgreSQL has some level of support for "stored functions", but output parameters do not work as of 8.2.
        // TODO: DB2 should be in this list.
        if (platform.isOracle() || platform.isMySQL()) {
            return true;
        }
        warning("This database does not support stored function creation.");
        return false;
    }

    public void setPuName(String name){
        puName = name;
    }

    public String getPuName() {
        return puName;
    }

    /**
     * Indicates whether two sessions are connected to the same db
     */
    public static boolean usingTheSameDatabase(AbstractSession session1, AbstractSession session2) {
        Connector conn1 = session1.getLogin().getConnector();
        Connector conn2 = session2.getLogin().getConnector();
        if (conn1 instanceof DefaultConnector && conn2 instanceof DefaultConnector) {
            return ((DefaultConnector)conn1).getDatabaseURL().equals(((DefaultConnector)conn2).getDatabaseURL());
        } else if (conn1 instanceof JNDIConnector && conn2 instanceof JNDIConnector) {
            String name1 = ((JNDIConnector)conn1).getName();
            String name2 = ((JNDIConnector)conn2).getName();
            if (name1 != null && name1.equals(name2)) {
                return true;
            }
            return ((JNDIConnector)conn1).getDataSource().equals(((JNDIConnector)conn2).getDataSource());
        }
        return false;
    }
}
