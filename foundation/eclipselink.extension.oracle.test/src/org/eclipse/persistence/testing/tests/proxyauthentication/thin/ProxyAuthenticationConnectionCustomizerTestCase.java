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
package org.eclipse.persistence.testing.tests.proxyauthentication.thin;

import java.util.Map;
import java.util.Properties;

import oracle.jdbc.OracleConnection;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.ConnectionPolicy;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.sessions.ExclusiveIsolatedClientSession;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.ValueReadQuery;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.framework.oracle.SessionExchanger;

// Test verifying that connectionUser and proxyUser are used as expected.
public class ProxyAuthenticationConnectionCustomizerTestCase extends TestCase {
    // true -> DatabaseSession;  false -> ServerSession.
    boolean shouldUseDatabaseSession;
    // indicates whether external connection pooling should be used.
    boolean shouldUseExternalConnectionPooling;
    
    Map mainSessionProxyProperties;
    String expectedMainSessionUser;

    // (valid on ServerSession only) indicates whether EclusiveIsolatedClientSession should be used.
    boolean shoulUseExclusiveIsolatedSession;
    
    Map clientSessionProxyProperties;
    String expectedClientSessionUser;
    
    // Substitutes the original session with the one required for the test; restores the original one after the test.
    SessionExchanger exchanger = new SessionExchanger();
    
    // Set to true to test for Bug 267880: JPA/ProxyAuthentication tests failed with "java.sql.SQLException: Closed Statement":
    // the bug caused tests using internal connection pools to fail.
    boolean shouldEnableStatementCaching = true;
    
    private String writeUser;
    class Listener extends SessionEventAdapter {
        public void outputParametersDetected(SessionEvent event) {
            writeUser = (String)((Map)event.getResult()).get("OUT");
        }
    }
    SessionEventListener listener = new Listener();

    static ValueReadQuery readQuery;
    static DataModifyQuery writeQuery;
    static {
        // Used to return the schema name used by reading connection. 
        readQuery = new ValueReadQuery();
        readQuery.setSQLString("SELECT SYS_CONTEXT ('USERENV', 'CURRENT_SCHEMA') FROM DUAL");
        
        // Used to return (through event) the schema name used by writing connection.
        writeQuery = new DataModifyQuery();
        writeQuery.setSQLString("BEGIN SELECT SYS_CONTEXT ('USERENV', 'CURRENT_SCHEMA') INTO ###OUT FROM DUAL; END;");
        writeQuery.setIsUserDefined(true);
    }
    
    String errorMsg = "";
    
    public static ProxyAuthenticationConnectionCustomizerTestCase createDatabaseSessionTest(boolean shouldUseExternalConnectionPooling, Map databaseSessionProxyProperties) {
        return new ProxyAuthenticationConnectionCustomizerTestCase(shouldUseExternalConnectionPooling, databaseSessionProxyProperties);
    }
    
    public static ProxyAuthenticationConnectionCustomizerTestCase createServerSessionTest(boolean shouldUseExternalConnectionPooling, Map serverSessionProxyProperties, boolean shoulUseExclusiveIsolatedSession, Map clientSessionProxyProperties) {
        return new ProxyAuthenticationConnectionCustomizerTestCase(shouldUseExternalConnectionPooling, serverSessionProxyProperties, shoulUseExclusiveIsolatedSession, clientSessionProxyProperties);
    }
    
    protected ProxyAuthenticationConnectionCustomizerTestCase(boolean shouldUseExternalConnectionPooling, Map databaseSessionProxyProperties) {
        this(true, shouldUseExternalConnectionPooling, databaseSessionProxyProperties, false, null);
    }
    
    protected ProxyAuthenticationConnectionCustomizerTestCase(boolean shouldUseExternalConnectionPooling, Map serverSessionProxyProperties, boolean shoulUseExclusiveIsolatedSession, Map clientSessionProxyProperties) {
        this(false, shouldUseExternalConnectionPooling, serverSessionProxyProperties, shoulUseExclusiveIsolatedSession, clientSessionProxyProperties);
    }
    
    protected ProxyAuthenticationConnectionCustomizerTestCase(boolean shouldUseDatabaseSession, boolean shouldUseExternalConnectionPooling, Map mainSessionProxyProperties, boolean shoulUseExclusiveIsolatedSession, Map clientSessionProxyProperties) {
        this.shouldUseDatabaseSession = shouldUseDatabaseSession;
        this.shouldUseExternalConnectionPooling = shouldUseExternalConnectionPooling;
        this.mainSessionProxyProperties = mainSessionProxyProperties;
        // in case no proxy properties specified on the main session it uses connectionUser. 
        expectedMainSessionUser = ProxyAuthenticationUsersAndProperties.connectionUser;
        if(mainSessionProxyProperties != null) {
            expectedMainSessionUser = getExpectedUserName(mainSessionProxyProperties);
        }
        this.shoulUseExclusiveIsolatedSession = shoulUseExclusiveIsolatedSession;
        if(!shouldUseDatabaseSession) {
            this.clientSessionProxyProperties = clientSessionProxyProperties;
            // in case no proxy properties specified on the ClientSession it uses the same use as the ServerSession. 
            expectedClientSessionUser = expectedMainSessionUser;
            if(clientSessionProxyProperties != null) {
                expectedClientSessionUser = getExpectedUserName(clientSessionProxyProperties);
            }
        }

        // generate the test name
        String mainPropsStr = mainSessionProxyProperties != null ? " Proxy properties on" : "No proxy properties on";
        String sessionStr = shouldUseDatabaseSession ? " DatabaseSession" : " ServerSession";
        String poolingStr = shouldUseExternalConnectionPooling ? " usingExternalConnectionPooling" : "";
        if(shouldUseDatabaseSession) {
            this.setName(mainPropsStr + sessionStr + poolingStr);
        } else {
            String clientPropsStr = "; no proxy properties on";
            if(clientSessionProxyProperties != null) {
                if(mainSessionProxyProperties == null) {
                    clientPropsStr = "; proxy properties on";
                } else {
                    clientPropsStr = expectedClientSessionUser.equals(ProxyAuthenticationUsersAndProperties.connectionUser) ? "; canceled on" : "; overridden on";
                }
            }
            String clientSessionStr = shoulUseExclusiveIsolatedSession ? " ExclusiveIsolatedClientSession" : " ClientSession";
            this.setName(mainPropsStr + sessionStr + poolingStr + clientPropsStr + clientSessionStr);
        }
    }
    
    // If the session's proxyProperties PersistenceUnitProperties.ORACLE_PROXY_TYPE property has an empty string value
    // then the session should use connectionUser;
    // otherwise it should use proxyUser specified in the proxyProperties.
    static String getExpectedUserName(Map proxyProperties) {
        Object proxytype = proxyProperties.get(PersistenceUnitProperties.ORACLE_PROXY_TYPE);
        boolean proxytypeIsAnEmptyString = proxytype instanceof String && ((String)proxytype).length()==0;
        if(proxytypeIsAnEmptyString) {
            // PersistenceUnitProperties.ORACLE_PROXY_TYPE -> "" means that proxyProperies are to be ignored and therefore connectionUser should be used.
            return ProxyAuthenticationUsersAndProperties.connectionUser;
        } else {
            return (String)proxyProperties.get(OracleConnection.PROXY_USER_NAME);
        }
    }
    
    public void setup() {
        if (!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("Supports Oracle platform only");
        }
        Properties loginProperties = ProxyAuthenticationUsersAndProperties.connectionProperties;
        // In case of ServerSession with internal connection pools each pool will use just one connection.
        getExecutor().setSession(exchanger.createNewSession((DatabaseSession)getSession(), shouldUseDatabaseSession, shouldUseExternalConnectionPooling, loginProperties, mainSessionProxyProperties, 1, 1, 1, 1));
        getSession().getEventManager().addListener(listener);
        if(!shouldUseDatabaseSession) {
            if(shoulUseExclusiveIsolatedSession) {
                ((ServerSession)getSession()).getDefaultConnectionPolicy().setExclusiveMode(ConnectionPolicy.ExclusiveMode.Always);

            }
        }
    }
    
    String getReadUser(Session session) {
        return (String)session.executeQuery(readQuery);
    }

    String getWriteUser(Session session) {
        session.executeQuery(writeQuery);
        String user = writeUser;
        writeUser = null;
        return user;
    }

    public void test() {
        if(this.shouldUseDatabaseSession) {
            testDatabaseSession();
        } else {
            testServerSession();
        }
    }
    
    void testDatabaseSession() {
        // DatabaseSession was created with proxy properties - should always use proxyUser.
        verifyUser("DatabaseSession read", getReadUser(getSession()), ProxyAuthenticationUsersAndProperties.proxyUser);
        verifyUser("DatabaseSession wrote", getWriteUser(getSession()), ProxyAuthenticationUsersAndProperties.proxyUser);
    }
    
    void testServerSession() {
        ServerSession ss = (ServerSession)getSession();
        if(shouldEnableStatementCaching) {
            ss.getPlatform().setShouldCacheAllStatements(true);
        }
        verifyUser("ServerSession first read", getReadUser(ss), expectedMainSessionUser);
        
        // The first ClientSession created without proxy properties - should always use the same user as ServerSession.
        ClientSession cs1 = ss.acquireClientSession();
        if(shoulUseExclusiveIsolatedSession) {
            // verify that IsolatedSession is used if required.
            if(!(cs1 instanceof ExclusiveIsolatedClientSession)) {
                throw new TestProblemException("The ClientSession must be ExclusiveIsolatedClientSession");
            }
        }
        verifyUser("ClientSession1 before transaction read", getReadUser(cs1), expectedMainSessionUser);
        cs1.beginTransaction();
        verifyUser("ClientSession1in transaction read", getReadUser(cs1), expectedMainSessionUser);
        verifyUser("ClientSession1 wrote", getWriteUser(cs1), expectedMainSessionUser);
        cs1.rollbackTransaction();
        verifyUser("ClientSession1 after transaction read", getReadUser(cs1), expectedMainSessionUser);
        cs1.release();
        
        // The second ClientSession created with proxy properties.
        ClientSession cs2 = ss.acquireClientSession(clientSessionProxyProperties);
        if(shoulUseExclusiveIsolatedSession) {
            verifyUser("ExclusiveIsolatedClientSession2 before transaction read", getReadUser(cs2), expectedClientSessionUser);
        } else {
            verifyUser("ClientSession2 before transaction read", getReadUser(cs2), expectedMainSessionUser);
        }
        cs2.beginTransaction();
        verifyUser("ClientSession2 in transaction read", getReadUser(cs2), expectedClientSessionUser);
        verifyUser("ClientSession2 wrote", getWriteUser(cs2), expectedClientSessionUser);
        cs2.rollbackTransaction();
        if(shoulUseExclusiveIsolatedSession) {
            verifyUser("ExclusiveIsolatedClientSession2 after transaction read", getReadUser(cs2), expectedClientSessionUser);
        } else {
            verifyUser("ClientSession2 after transaction read", getReadUser(cs2), expectedMainSessionUser);
        }
        cs2.release();

        // verify that ServerSession still uses the correct user.
        // Because there is one one connection in each connection pool (internal pool case) this would fail in case proxy customizer
        // wasn't removed by cs2.
        verifyUser("ServerSession second read", getReadUser(ss), expectedMainSessionUser);
        
        // The third ClientSession created without proxy properties again - should always use the same user as ServerSession.
        // Because there is one one connection in each connection pool (internal pool case) this would fail in case proxy customizer
        // wasn't removed by cs2.
        ClientSession cs3 = ss.acquireClientSession();
        verifyUser("ClientSession3 before transaction read", getReadUser(cs3), expectedMainSessionUser);
        cs3.beginTransaction();
        verifyUser("ClientSession3 in transaction read", getReadUser(cs3), expectedMainSessionUser);
        verifyUser("ClientSession3 wrote", getWriteUser(cs3), expectedMainSessionUser);
        cs3.rollbackTransaction();
        verifyUser("ClientSession3 after transaction read", getReadUser(cs3), expectedMainSessionUser);
        cs3.release();
        
    }
    
    void verifyUser(String msg, String user, String expectedUser) {
        if(!user.equals(expectedUser)) {
            errorMsg += msg + " through wrong user " + user + " - " + expectedUser + " was expected \n";
        }
    }

    public void verify() {
        if(errorMsg.length() > 0) {
            throw new TestErrorException(errorMsg);
        }
    }

    public void reset() {
        getSession().getEventManager().removeListener(listener);
        getExecutor().setSession(exchanger.returnOriginalSession());
        errorMsg = "";
    }
}
