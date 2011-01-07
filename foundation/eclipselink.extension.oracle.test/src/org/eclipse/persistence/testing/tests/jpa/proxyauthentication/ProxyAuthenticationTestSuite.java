/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/28/2008-1.0M8 Andrei Ilitchev. 
 *       - New file introduced for bug 224964: Provide support for Proxy Authentication through JPA.
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.proxyauthentication;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import junit.framework.Test;
import junit.framework.TestSuite;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.pool.OracleDataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.ExclusiveConnectionMode;
import org.eclipse.persistence.internal.sessions.ExclusiveIsolatedClientSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;

import org.eclipse.persistence.platform.database.oracle.Oracle9Platform;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.server.ConnectionPolicy;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.tests.proxyauthentication.thin.ProxyAuthenticationUsersAndProperties;

/**
 * TestSuite to verifying that connectionUser and proxyUser are used as expected.
 * To run this test suite several users should be setup in the Oracle database,
 * see comment in ProxyAuthenticationUsersAndProperties.
 */
public class ProxyAuthenticationTestSuite extends JUnitTestCase {
    // the map passed to creteEMFactory method.
    Map factoryProperties;

    // indicates whether external connection pooling should be used.
    boolean shouldUseExternalConnectionPooling;
    // indicates whether EclusiveIsolatedClientSession should be used.
    boolean shoulUseExclusiveIsolatedSession;
    // datasource created in external connection pooling case.
    OracleDataSource dataSource;
    
    // writeUser is set by an event risen by ModifyQuery.
    private static String writeUser;
    public static class Listener extends SessionEventAdapter {
        public void outputParametersDetected(SessionEvent event) {
            writeUser = (String)((Map)event.getResult()).get("OUT");
        }
    }

    // custom sql strings used to query for read and write users.
    static String readQueryString;
    static String writeQueryString;
    static {
        // Used to return the schema name used by reading connection. 
        readQueryString = "SELECT SYS_CONTEXT ('USERENV', 'CURRENT_SCHEMA') FROM DUAL";
        
        // Used to return (through event) the schema name used by writing connection.
        writeQueryString = "BEGIN SELECT SYS_CONTEXT ('USERENV', 'CURRENT_SCHEMA') INTO ###OUT FROM DUAL; END;";
    }
    
    // Contains a non-empty string in case of an error after the test is complete.
    String errorMsg = "";
    
    // Before setup is attempted contains null; after that contains an empty string in case of success, error message otherwise.
    // If setup has failed then all the tests fail with this message.
    static String setupErrorMsg;

    public ProxyAuthenticationTestSuite() {
        super();
    }

    public ProxyAuthenticationTestSuite(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("Proxy Authentication JPA Test Suite");

        suite.addTest(new ProxyAuthenticationTestSuite("testInternalPool_EMFProxyProperties"));
        suite.addTest(new ProxyAuthenticationTestSuite("testInternalPool_EMFProxyProperties_ExclusiveIsolated"));
        suite.addTest(new ProxyAuthenticationTestSuite("testExternalPool_EMFProxyProperties"));
        suite.addTest(new ProxyAuthenticationTestSuite("testExternalPool_EMFProxyProperties_ExclusiveIsolated"));

        suite.addTest(new ProxyAuthenticationTestSuite("testInternalPool_EMProxyProperties"));
        suite.addTest(new ProxyAuthenticationTestSuite("testInternalPool_EMProxyProperties_ExclusiveIsolated"));
        suite.addTest(new ProxyAuthenticationTestSuite("testExternalPool_EMProxyProperties"));
        suite.addTest(new ProxyAuthenticationTestSuite("testExternalPool_EMProxyProperties_ExclusiveIsolated"));

        suite.addTest(new ProxyAuthenticationTestSuite("testInternalPool_EMFProxyProperties_EMProxyProperties"));
        suite.addTest(new ProxyAuthenticationTestSuite("testInternalPool_EMFProxyProperties_EMProxyProperties_ExclusiveIsolated"));
        suite.addTest(new ProxyAuthenticationTestSuite("testExternalPool_EMFProxyProperties_EMProxyProperties"));
        suite.addTest(new ProxyAuthenticationTestSuite("testExternalPool_EMFProxyProperties_EMProxyProperties_ExclusiveIsolated"));

        suite.addTest(new ProxyAuthenticationTestSuite("testInternalPool_EMFProxyProperties_EMCancelProxyProperties"));
        suite.addTest(new ProxyAuthenticationTestSuite("testInternalPool_EMFProxyProperties_EMCancelProxyProperties_ExclusiveIsolated"));
        suite.addTest(new ProxyAuthenticationTestSuite("testExternalPool_EMFProxyProperties_EMCancelProxyProperties"));
        suite.addTest(new ProxyAuthenticationTestSuite("testExternalPool_EMFProxyProperties_EMCancelProxyProperties_ExclusiveIsolated"));
        
        return suite;
    }
    
    public void setUp() {
        // runs for the first time - setup user names and properties used by the tests.
        if(setupErrorMsg == null) {
            if (isOnServer()) {
                setupErrorMsg = "ProxyAuthentication tests currently can't run on server.";
            } else if(!getServerSession().getPlatform().isOracle()) {
                setupErrorMsg = "ProxyAuthentication test has not run, it runs only on Oracle and Oracle9Platform or higher required.";
            } else if (! (getServerSession().getPlatform() instanceof Oracle9Platform)) {
                setupErrorMsg = "ProxyAuthentication test has not run, Oracle9Platform or higher required.";
            } else {
                // sets up all user names and properties used by the tests.
                ProxyAuthenticationUsersAndProperties.initialize();
                // verifies that all the users correctly setup in the db.
                // returns empty string in case of success, error message otherwise.
                setupErrorMsg = ProxyAuthenticationUsersAndProperties.verify(getServerSession());
            }
        }
        if(setupErrorMsg.length() > 0) {
            // setup has failed - can't run the test.
            fail("Setup Failure:\n" + setupErrorMsg);
        } else {
            // The test will re-create EMFactory using the necessary properties, close the original factory first.
            // This does nothing if the factory already closed.
            closeEntityManagerFactory();
            initializeFactoryProperties();
        }
    }
    
    public void tearDown() {
        // clean-up
        if(setupErrorMsg.length() > 0) {
            // setup failed, the test didn't run, nothing to clean up.
            return;
        }

        // reset errorMsg
        errorMsg = "";
        // the test has customized the factory - it should be closed.
        closeEntityManagerFactory();
        // close the data source if it has been created
        if(dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Exception thrown while closing OracleDataSource:\n", ex);
            } finally {
                dataSource = null;
            }
        }
    }
    
    // create a data source using the supplied connection string
    void createDataSource(String connectionString) {
        try {
            dataSource = new OracleDataSource();
            Properties props = new Properties();
            // the pool using just one connection would cause deadlock in case of a connection leak - good for the test.
            props.setProperty("MinLimit", "1");
            props.setProperty("MaxLimit", "1");
            props.setProperty("InitialLimit", "1");
            dataSource.setConnectionCacheProperties(props);
            dataSource.setURL(connectionString);
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to create OracleDataSource with " + connectionString + ".\n", ex);
        }
    }
    
    /**
     * 
     */
    public void testInternalPool_EMFProxyProperties() {
        internalTest(false, ProxyAuthenticationUsersAndProperties.proxyProperties, false, null);
    }
    public void testInternalPool_EMFProxyProperties_ExclusiveIsolated() {
        internalTest(false, ProxyAuthenticationUsersAndProperties.proxyProperties, true, null);
    }
    public void testExternalPool_EMFProxyProperties() {
        internalTest(true, ProxyAuthenticationUsersAndProperties.proxyProperties, false, null);
    }
    public void testExternalPool_EMFProxyProperties_ExclusiveIsolated() {
        internalTest(true, ProxyAuthenticationUsersAndProperties.proxyProperties, true, null);
    }
    
    /**
     * 
     */
    public void testInternalPool_EMProxyProperties() {
        internalTest(false, null, false, ProxyAuthenticationUsersAndProperties.proxyProperties);
    }
    public void testInternalPool_EMProxyProperties_ExclusiveIsolated() {
        internalTest(false, null, true, ProxyAuthenticationUsersAndProperties.proxyProperties);
    }
    public void testExternalPool_EMProxyProperties() {
        internalTest(true, null, false, ProxyAuthenticationUsersAndProperties.proxyProperties);
    }
    public void testExternalPool_EMProxyProperties_ExclusiveIsolated() {
        internalTest(true, null, true, ProxyAuthenticationUsersAndProperties.proxyProperties);
    }
    
    /**
     * 
     */
    public void testInternalPool_EMFProxyProperties_EMProxyProperties() {
        internalTest(false, ProxyAuthenticationUsersAndProperties.proxyProperties, false, ProxyAuthenticationUsersAndProperties.proxyProperties2);
    }
    public void testInternalPool_EMFProxyProperties_EMProxyProperties_ExclusiveIsolated() {
        internalTest(false, ProxyAuthenticationUsersAndProperties.proxyProperties, true, ProxyAuthenticationUsersAndProperties.proxyProperties2);
    }
    public void testExternalPool_EMFProxyProperties_EMProxyProperties() {
        internalTest(true, ProxyAuthenticationUsersAndProperties.proxyProperties, false, ProxyAuthenticationUsersAndProperties.proxyProperties2);
    }
    public void testExternalPool_EMFProxyProperties_EMProxyProperties_ExclusiveIsolated() {
        internalTest(true, ProxyAuthenticationUsersAndProperties.proxyProperties, true, ProxyAuthenticationUsersAndProperties.proxyProperties2);
    }
    
    /**
     * 
     */
    public void testInternalPool_EMFProxyProperties_EMCancelProxyProperties() {
        internalTest(false, ProxyAuthenticationUsersAndProperties.proxyProperties, false, ProxyAuthenticationUsersAndProperties.cancelProxyProperties);
    }
    public void testInternalPool_EMFProxyProperties_EMCancelProxyProperties_ExclusiveIsolated() {
        internalTest(false, ProxyAuthenticationUsersAndProperties.proxyProperties, true, ProxyAuthenticationUsersAndProperties.cancelProxyProperties);
    }
    public void testExternalPool_EMFProxyProperties_EMCancelProxyProperties() {
        internalTest(true, ProxyAuthenticationUsersAndProperties.proxyProperties, false, ProxyAuthenticationUsersAndProperties.cancelProxyProperties);
    }
    public void testExternalPool_EMFProxyProperties_EMCancelProxyProperties_ExclusiveIsolated() {
        internalTest(true, ProxyAuthenticationUsersAndProperties.proxyProperties, true, ProxyAuthenticationUsersAndProperties.cancelProxyProperties);
    }
    
    void initializeFactoryProperties() {
        // copy the original factory properties.
        factoryProperties = new HashMap(JUnitTestCaseHelper.getDatabaseProperties());
        
        // these properties used only in internal connection pool case.
        // the pool using just one connection would cause deadlock in case of a connection leak - good for the test.
        factoryProperties.put(PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_MIN, "1");
        factoryProperties.put(PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_MAX, "1");
        factoryProperties.put(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_MIN, "1");
        factoryProperties.put(PersistenceUnitProperties.JDBC_READ_CONNECTIONS_MAX, "1");
        
        // set them into factoryProperties. Note that this works in external connection pooling case, too (at least with OracleDataSource).
        factoryProperties.put(PersistenceUnitProperties.JDBC_USER, ProxyAuthenticationUsersAndProperties.connectionUser);
        factoryProperties.put(PersistenceUnitProperties.JDBC_PASSWORD, ProxyAuthenticationUsersAndProperties.connectionPassword);
        
        // add listener that handles output parameter event through which writeUser is obtained. 
        factoryProperties.put(PersistenceUnitProperties.SESSION_EVENT_LISTENER_CLASS, Listener.class.getName());
    }
    
    void internalTest(boolean shouldUseExternalConnectionPooling, Map serverSessionProxyProperties, boolean shoulUseExclusiveIsolatedSession, Map clientSessionProxyProperties) {
        if(shouldUseExternalConnectionPooling) {
            // create data source and add it to factoryProperties
            createDataSource((String)factoryProperties.get(PersistenceUnitProperties.JDBC_URL));
            factoryProperties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, dataSource);
        }
        
        if(shoulUseExclusiveIsolatedSession) {
            factoryProperties.put(PersistenceUnitProperties.EXCLUSIVE_CONNECTION_MODE, ExclusiveConnectionMode.Isolated);
        }
        
        // EMFactory uses proxyProperties
        if(serverSessionProxyProperties != null) {
            factoryProperties.putAll(serverSessionProxyProperties);
        }
        EntityManagerFactory factory = this.getEntityManagerFactory(factoryProperties);
        ServerSession ss = ((org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl)factory).getServerSession();
        
        if(shoulUseExclusiveIsolatedSession) {
            ss.getDefaultConnectionPolicy().setExclusiveMode(ConnectionPolicy.ExclusiveMode.Always);
        }
        
        String expectedMainSessionUser = ProxyAuthenticationUsersAndProperties.connectionUser;
        if(serverSessionProxyProperties != null) {
            expectedMainSessionUser = getExpectedUserName(serverSessionProxyProperties);
        }
        // in case no proxy properties specified on the ClientSession it uses the same use as the ServerSession. 
        String expectedClientSessionUser = expectedMainSessionUser;
        if(clientSessionProxyProperties != null) {
            expectedClientSessionUser = getExpectedUserName(clientSessionProxyProperties);
        }
        
        // The second ClientSession created without proxy properties.
        EntityManager em1 = factory.createEntityManager();
        boolean isExclusiveIsolated = ((UnitOfWorkImpl)((org.eclipse.persistence.internal.jpa.EntityManagerImpl)em1).getActiveSession()).getParent() instanceof ExclusiveIsolatedClientSession;
        if(isExclusiveIsolated != shoulUseExclusiveIsolatedSession) {
            if(isExclusiveIsolated) {
                fail("EntityManager is using ExclusiveIsolatedClientSession, ClientSession was expected");
            } else {
                fail("EntityManager is using ClientSession, ExclusiveIsolatedClientSession was expected");
            }
        }
        verifyUser("ClientSession1 before transaction read", getReadUser(em1), expectedMainSessionUser);
        beginTransaction(em1);
        verifyUser("ClientSession1 in transaction read", getReadUser(em1), expectedMainSessionUser);
        verifyUser("ClientSession1 wrote", getWriteUser(em1), expectedMainSessionUser);
        rollbackTransaction(em1);
        verifyUser("ClientSession1 after transaction read", getReadUser(em1), expectedMainSessionUser);
        em1.close();
        
        // The second ClientSession created with proxy properties.
        EntityManager em2 = factory.createEntityManager(clientSessionProxyProperties);
        if(shoulUseExclusiveIsolatedSession) {
            verifyUser("ExclusiveIsolatedClientSession2 before transaction read", getReadUser(em2), expectedClientSessionUser);
        } else {
            verifyUser("ClientSession2 before transaction read", getReadUser(em2), expectedMainSessionUser);
        }
        beginTransaction(em2);
        verifyUser("ClientSession2 in transaction read", getReadUser(em2), expectedClientSessionUser);
        verifyUser("ClientSession2 wrote", getWriteUser(em2), expectedClientSessionUser);
        rollbackTransaction(em2);
        if(shoulUseExclusiveIsolatedSession) {
            verifyUser("ExclusiveIsolatedClientSession2 after transaction read", getReadUser(em2), expectedClientSessionUser);
        } else {
            verifyUser("ClientSession2 after transaction read", getReadUser(em2), expectedMainSessionUser);
        }
        em2.close();

        // The third ClientSession created without proxy properties again - should always use the same user as ServerSession.
        // Because there is one one connection in each connection pool (internal pool case) this would fail in case proxy customizer
        // wasn't removed by cs2.
        EntityManager em3 = factory.createEntityManager();
        verifyUser("ClientSession3 before transaction read", getReadUser(em3), expectedMainSessionUser);
        beginTransaction(em3);
        verifyUser("ClientSession3 in transaction read", getReadUser(em3), expectedMainSessionUser);
        verifyUser("ClientSession3 wrote", getWriteUser(em3), expectedMainSessionUser);
        rollbackTransaction(em3);
        verifyUser("ClientSession3 after transaction read", getReadUser(em3), expectedMainSessionUser);
        em3.close();
        
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    String getReadUser(EntityManager em) {
        return (String)em.createNativeQuery(readQueryString).getSingleResult();
    }

    String getWriteUser(EntityManager em) {
        em.createNativeQuery(writeQueryString).executeUpdate();
        String user = writeUser;
        // make sure writeUser is not left for the next time writeQuery executed.
        writeUser = null;
        return user;
    }

    // If the session's proxyProperties PersistenceUnitProperties.ORACLE_PROXY_TYPE property has an empty string value
    // then the session should use connectionUser;
    // otherwise it should use proxyUser specified in the proxyProperties.
    String getExpectedUserName(Map proxyProperties) {
        Object proxytype = proxyProperties.get(PersistenceUnitProperties.ORACLE_PROXY_TYPE);
        boolean proxytypeIsAnEmptyString = proxytype instanceof String && ((String)proxytype).length()==0;
        if(proxytypeIsAnEmptyString) {
            // PersistenceUnitProperties.ORACLE_PROXY_TYPE -> "" means that proxyProperies are to be ignored and therefore connectionUser should be used.
            return ProxyAuthenticationUsersAndProperties.connectionUser;
        } else {
            return (String)proxyProperties.get(OracleConnection.PROXY_USER_NAME);
        }
    }
    
    void verifyUser(String msg, String user, String expectedUser) {
        if(!user.equalsIgnoreCase(expectedUser)) {
            errorMsg += msg + " through wrong user " + user + " - " + expectedUser + " was expected \n";
        }
    }
}
