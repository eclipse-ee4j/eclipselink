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
 *     05/28/2008-1.0M8 Andrei Ilitchev 
 *        - New file introduced for bug 224964: Provide support for Proxy Authentication through JPA. 
 ******************************************************************************/  
package org.eclipse.persistence.testing.framework.oracle;

import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import oracle.jdbc.pool.OracleDataSource;

import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.internal.sessions.AbstractSession;

import org.eclipse.persistence.testing.framework.TestProblemException;

// Intended usage:
// 1.1 Obtains a session from the TestExecutor, logs it out, and keeps;
// 1.2 Using the original session's login and the passed parameters
// creates session that uses externalConnectionPools;
// 1.3 Logs the new session into TestExecutor so that it could be used by tests.
// 2.1 After done with the new session - kill it, shutdown all the data sources created for it;
// 2.2 Login the original session and set it back into TestExecutor.
//// Example: to create a new DatabaseSession that use externalConnectionPooling:
// SessionExchange exchange = new SessionExchange();
// testExecutor.setSession(exchange.createDatabaseSession((DatabaseSession)testExecutor.getSession()), true);
//// after the testing with the new session is completed:
// testExecutor.setSession(exchange.returnOriginalSession());

public class SessionExchanger {
    // indicates whether the original session should be disconnected
    public boolean shouldLogoutOriginalSession = true;
    // indicates whether the new session should be connected
    public boolean shouldLoginNewSession = true;
    
    DatabaseSession originalSession;
    DatabaseSession newSession;
    boolean hasLoggedOutOriginalSession;
    OracleDataSource dataSource;    

    // pass the original session and params for the new one:
    //   useDatabaseSession - "true" means new session is DatabaseSession; "false" - ServerSession;
    //   useExternalConnectionPooling - indicates whether the new session use external connection pooling or not. 
    // returns the new session
    public DatabaseSession createNewSession(DatabaseSession originalSession, boolean useDatabaseSession, boolean useExternalConnectionPooling) {
        return createNewSession(originalSession, useDatabaseSession, useExternalConnectionPooling, null, null);
    }

    // additional property parameters:
    //   loginProperties - if not null set into the new session's login;
    //   propertyProperties - if not null set into the new session;
    // returns the new session
    public DatabaseSession createNewSession(DatabaseSession originalSession, boolean useDatabaseSession, boolean useExternalConnectionPooling, Properties loginProperties, Map sessionProperties) {
        if(useExternalConnectionPooling) {
            return createNewSession(originalSession, useDatabaseSession, useExternalConnectionPooling, loginProperties, sessionProperties, -1, -1, -1, -1);
        } else {
            return createNewSession(originalSession, useDatabaseSession, useExternalConnectionPooling, loginProperties, sessionProperties, 2, 2, 2, 2);
        }
    }

    // additional min / max parameters used only in case of ServerSession:
    //   in external pooling case only minWriteConnection and maxWriteConnection used - they determine the min / max number of connections in the created data source,
    //        pass -1 value for minWriteConnection and/or maxWriteConnection if no min and/or max number of connections should be set in the data source.
    //   in internal pooling case the min / max values used for write and read connection pools.
    // returns the new session
    public DatabaseSession createNewSession(DatabaseSession originalSession, boolean useDatabaseSession, boolean useExternalConnectionPooling, Properties loginProperties, Map sessionProperties, 
                                            int minWriteConnections, int maxWriteConnections, int minReadConnections, int maxReadConnections) 
    {
        setup(originalSession);
        DatabaseLogin login = cloneAndSetDataSource(originalSession.getLogin(), useExternalConnectionPooling, minWriteConnections, maxWriteConnections);
        if(useDatabaseSession) {
            newSession = new DatabaseSessionImpl(login);            
        } else {
            if(useExternalConnectionPooling) {
                newSession = new ServerSession(login);
            } else {
                newSession = new ServerSession(login, minWriteConnections, minWriteConnections);
            }
            if(!useExternalConnectionPooling) {
                ServerSession ss = (ServerSession)newSession;
                if(ss.getReadConnectionPool().getMaxNumberOfConnections() != maxReadConnections) {
                    ss.getReadConnectionPool().setMaxNumberOfConnections(maxReadConnections);
                }
                if(ss.getReadConnectionPool().getMinNumberOfConnections() != minReadConnections) {
                    ss.getReadConnectionPool().setMinNumberOfConnections(minReadConnections);
                }
            }
        }
        setProperties(loginProperties, sessionProperties);
        newSession.setSessionLog(originalSession.getSessionLog());
        newSession.setLogLevel(originalSession.getLogLevel());
        if(shouldLoginNewSession) {
            newSession.login();
        }
        return newSession;
    }

    // disconnects the temporary session, closes its datasource (if any);
    // in case the original session was disconnected in setup - reconnects it;
    // returns the original session
    public DatabaseSession returnOriginalSession() {
        try {
            clearNewSession();
            return originalSession;
        } finally {
            try {
                if(originalSession != null && hasLoggedOutOriginalSession) {
                    hasLoggedOutOriginalSession = false;
                    originalSession.login();
                }
            } finally {
                originalSession = null;
            }
        }
    }
    
    public boolean isBusy() {
        return newSession != null;
    }
    
    void setup(DatabaseSession originalSession) {
        if(isBusy()) {
            throw new TestProblemException("SessionExchanger: new session has been already created");
        }
        this.originalSession = originalSession;
        if(shouldLogoutOriginalSession) {
            if(originalSession.isConnected()) {
                originalSession.logout();
                hasLoggedOutOriginalSession = true;
            }
        }
    }

    void clearNewSession() {
        try {
            if(newSession != null) {
                if(newSession.isConnected()) {
                    newSession.logout();
                }
            }
        } finally {
            newSession = null;
            if(dataSource != null) {
                try {
                    dataSource.close();
                } catch (SQLException ex) {
                    throw new TestProblemException("Exception thrown while closing OracleDataSource:\n", ex);
                } finally {
                    dataSource = null;
                }
            }
        }
    }

    DatabaseLogin cloneAndSetDataSource(DatabaseLogin login, boolean useExternalConnectionPooling, int minConnections, int maxConnections) {
        DatabaseLogin cloneLogin = (DatabaseLogin)login.clone();
        if(useExternalConnectionPooling) {
            createDataSource(login.getConnectionString(), minConnections, maxConnections);
            cloneLogin.setConnector(new JNDIConnector(dataSource));
            cloneLogin.setUsesExternalConnectionPooling(true);
        }
        return cloneLogin;
    }
    
    void setProperties(Properties loginProperties, Map sessionProperties) {
        if(loginProperties != null) {
            newSession.getLogin().setProperties(loginProperties);
        }
        if(sessionProperties != null) {
            ((AbstractSession)newSession).setProperties(sessionProperties);
        }
    }
    
    // create a data source using the supplied connection string
    void createDataSource(String connectionString, int minConnections, int maxConnections) {
        try {
            dataSource = new OracleDataSource();
            Properties props = new Properties();
            if(minConnections >= 0) {
                props.setProperty("MinLimit", Integer.toString(minConnections));
                props.setProperty("InitialLimit", Integer.toString(minConnections));
            }
            if(maxConnections >= 0) {
                props.setProperty("MaxLimit", Integer.toString(maxConnections));
            }
            if(!props.isEmpty()) {
                dataSource.setConnectionCacheProperties(props);
            }
        } catch (SQLException ex) {
            throw new TestProblemException("Failed to create OracleDataSource with " + connectionString + ".\n", ex);
        }
        dataSource.setURL(connectionString);
    }
}
