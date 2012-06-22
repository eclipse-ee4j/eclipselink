/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.platform.database.oracle;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import oracle.jdbc.OracleConnection;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.databaseaccess.ConnectionCustomizer;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;

/**
 * PUBLIC:
 * This class allows connection to open proxy session.
 */
public class OracleJDBC_10_1_0_2ProxyConnectionCustomizer extends ConnectionCustomizer {
    protected OracleConnection oracleConnection;
    protected int proxyType;
    protected Properties proxyProperties;

    /**
     * INTERNAL:
     * Should be instantiated only if session.getProperty(PersistenceUnitProperties.ORACLE_PROXY_TYPE) != null.
     */
    public OracleJDBC_10_1_0_2ProxyConnectionCustomizer(Accessor accessor, Session session) {
        super(accessor, session);
    }
    
    /**
     * INTERNAL:
     * Applies customization to connection.
     * Called only if connection is not already customized (isActive()==false). 
     * The method may throw SQLException wrapped into DatabaseException.
     * isActive method called after this method should return true only in case
     * the connection was actually customized. 
     */
    public void customize() {
        // Lazily initialize proxy properties - customize method may be never called
        // in case of ClientSession using external connection pooling.
        if(proxyProperties == null) {
            buildProxyProperties();
        }

        Connection connection = accessor.getConnection();
        if(connection instanceof OracleConnection) {
            oracleConnection = (OracleConnection)connection;
        } else {
            connection = session.getServerPlatform().unwrapConnection(connection);
            if(connection instanceof OracleConnection) {
                oracleConnection = (OracleConnection)connection;
            } else {
                throw ValidationException.oracleJDBC10_1_0_2ProxyConnectorRequiresOracleConnection();
            }
        }
        try {
            clearConnectionCache();
            Object[] args = null;
            if (this.session.shouldLog(SessionLog.FINEST, SessionLog.CONNECTION)) {
                Properties logProperties = proxyProperties;
                if(proxyProperties.containsKey(OracleConnection.PROXY_USER_PASSWORD)) {
                    logProperties = (Properties)proxyProperties.clone();
                    logProperties.setProperty(OracleConnection.PROXY_USER_PASSWORD, "******");
                }
                args = new Object[]{ oracleConnection, logProperties };
            }
            if(oracleConnection.isProxySession()) {
                // Unexpectedly oracleConnection already has a proxy session - probably it was not closed when connection was returned back to connection pool.
                // That may happen on jta transaction rollback (especially triggered outside of user's thread - such as timeout)
                // when beforeCompletion is never issued
                // and application server neither closes proxySession nor allows access to connection in afterCompletion. 
                try {
                    if (args != null) {
                        ((AbstractSession)this.session).log(SessionLog.FINEST, SessionLog.CONNECTION, "proxy_connection_customizer_already_proxy_session", args);
                    }
                    oracleConnection.close(OracleConnection.PROXY_SESSION);
                } catch (SQLException exception) {
                    // Ignore
                    this.session.getSessionLog().logThrowable(SessionLog.WARNING, exception);
                }
            }
            oracleConnection.openProxySession(proxyType, proxyProperties); 
            if (args != null) {
                ((AbstractSession)this.session).log(SessionLog.FINEST, SessionLog.CONNECTION, "proxy_connection_customizer_opened_proxy_session", args);
            }
        } catch (SQLException exception) {
            oracleConnection = null;
            throw DatabaseException.sqlException(exception);
        } catch (NoSuchMethodError noSuchMethodError) {
            oracleConnection = null;
            throw ValidationException.oracleJDBC10_1_0_2ProxyConnectorRequiresOracleConnectionVersion();
        }
    }

    /**
     * INTERNAL:
     * Indicated whether the connection is currently customized.
     */
    public boolean isActive() {
        return oracleConnection != null;
    }
    
    /**
     * INTERNAL:
     * Clears customization from connection.
     * Called only if connection is customized (isActive()==true). 
     * If the method fails due to SQLException it should "eat" it
     * (just like DatasourceAccessor.closeConnection method).
     * isActive method called after this method should always return false.
     */
    public void clear() {
        try {
            clearConnectionCache();
            if (this.session.shouldLog(SessionLog.FINEST, SessionLog.CONNECTION)) {
                Properties logProperties = proxyProperties;
                if(proxyProperties.containsKey(OracleConnection.PROXY_USER_PASSWORD)) {
                    logProperties = (Properties)proxyProperties.clone();
                    logProperties.setProperty(OracleConnection.PROXY_USER_PASSWORD, "******");
                }
                Object[] args = new Object[]{ oracleConnection, logProperties };
                ((AbstractSession)this.session).log(SessionLog.FINEST, SessionLog.CONNECTION, "proxy_connection_customizer_closing_proxy_session", args);
            }
            oracleConnection.close(OracleConnection.PROXY_SESSION);
        } catch (SQLException exception) {
            // Ignore
            this.session.getSessionLog().logThrowable(SessionLog.WARNING, exception);
        } finally {
            oracleConnection = null;
        }
    }
    
    /**
     * INTERNAL:
     * Normally called only when customizer is in inactive state (isActive()==false)
     * and followed by setAccessor call on the clone.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError("clone not supported");
        }
    }
    
    /**
     * INTERNAL:
     * Two customizers considered equal if they produce the sane customization.
     */
    public boolean equals(Object obj) {
        if(obj instanceof OracleJDBC_10_1_0_2ProxyConnectionCustomizer) {
            return equals((OracleJDBC_10_1_0_2ProxyConnectionCustomizer)obj);
        } else {
            return false;
        }
    }
    
    /**
     * INTERNAL:
     * Two customizers considered equal if they produce the sane customization.
     */
    public boolean equals(OracleJDBC_10_1_0_2ProxyConnectionCustomizer customizer) {
        if(this == customizer) {
            return true;
        }
        if(this.proxyProperties == null) {
            buildProxyProperties();
        }
        if(customizer.proxyProperties == null) {
            customizer.buildProxyProperties();
        }
        return this.proxyType == customizer.proxyType && this.proxyProperties.equals(customizer.proxyProperties);
    }
    
    /**
     * INTERNAL:
     * Precondition: session.getProperty(PersistenceUnitProperties.ORACLE_PROXY_TYPE) != null
     */
    protected void buildProxyProperties() {
        Object proxyTypeValue = session.getProperty(PersistenceUnitProperties.ORACLE_PROXY_TYPE);
        try {
            proxyType = ((Integer)session.getPlatform().getConversionManager().convertObject(proxyTypeValue, Integer.class)).intValue();
        } catch (ConversionException conversionException) {
            throw ValidationException.oracleJDBC10_1_0_2ProxyConnectorRequiresIntProxytype();            
        }
        proxyProperties = new Properties();
        if(proxyType == OracleConnection.PROXYTYPE_USER_NAME) {
            String proxyUserName = (String)session.getProperty(OracleConnection.PROXY_USER_NAME);
            if(proxyUserName != null) {
                proxyProperties.setProperty(OracleConnection.PROXY_USER_NAME, proxyUserName);
            } else {
                ValidationException.expectedProxyPropertyNotFound("OracleConnection.PROXYTYPE_USER_NAME", OracleConnection.PROXY_USER_NAME);
            }
        } else if(proxyType == OracleConnection.PROXYTYPE_DISTINGUISHED_NAME) {
            String distinguishedName = (String)session.getProperty(OracleConnection.PROXY_DISTINGUISHED_NAME);
            if(distinguishedName != null) {
                proxyProperties.setProperty(OracleConnection.PROXY_DISTINGUISHED_NAME, distinguishedName);
            } else {
                ValidationException.expectedProxyPropertyNotFound("OracleConnection.PROXYTYPE_DISTINGUISHED_NAME", OracleConnection.PROXY_DISTINGUISHED_NAME);
            }
        } else if(proxyType == OracleConnection.PROXYTYPE_CERTIFICATE) {
            Object certificate = session.getProperty(OracleConnection.PROXY_CERTIFICATE);
            if(certificate != null) {
                proxyProperties.put(OracleConnection.PROXY_CERTIFICATE, certificate);
            } else {
                ValidationException.expectedProxyPropertyNotFound("OracleConnection.PROXYTYPE_CERTIFICATE", OracleConnection.PROXY_CERTIFICATE);
            }
        } else {
            ValidationException.unknownProxyType(proxyType, "OracleConnection.PROXYTYPE_USER_NAME", "OracleConnection.PROXYTYPE_DISTINGUISHED_NAME", "OracleConnection.PROXYTYPE_CERTIFICATE");
        }
        String proxyUserPassword = (String)session.getProperty(OracleConnection.PROXY_USER_PASSWORD);
        // set the value if it's not null and not an empty String
        if(proxyUserPassword != null && proxyUserPassword.length() > 0) {
            proxyProperties.setProperty(OracleConnection.PROXY_USER_PASSWORD, proxyUserPassword);
        }
        Object proxyRoles = session.getProperty(OracleConnection.PROXY_ROLES);
        // set the value if it's not null and not an empty String
        if(proxyRoles != null && !((proxyRoles instanceof String) && (((String)proxyRoles).length() == 0))) {
            proxyProperties.put(OracleConnection.PROXY_ROLES, proxyRoles);
        }
    }
    
    /**
     * INTERNAL:
     * Clears connection's both implicit and explicit caches.
     */
    protected void clearConnectionCache() {
        this.getSession().getServerPlatform().clearStatementCache(this.getAccessor().getConnection());
        ((DatabaseAccessor)this.getAccessor()).clearStatementCache((AbstractSession)this.getSession());
    }
}
