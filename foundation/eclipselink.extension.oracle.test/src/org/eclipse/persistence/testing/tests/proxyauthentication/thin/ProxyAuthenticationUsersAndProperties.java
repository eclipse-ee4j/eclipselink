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
 *     05/28/2008-1.0M8 Andrei Ilitchev. 
 *       - New file introduced for bug 224964: Provide support for Proxy Authentication through JPA.
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.proxyauthentication.thin;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import oracle.jdbc.OracleConnection;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.oracle.SessionExchanger;

/**
 * Initializes and holds user names and properties used by for thin and jpa ProxyAuthentication tests.
 */
public class ProxyAuthenticationUsersAndProperties {
    // specify connectionUser in a System property PA_CONNECTION_USER, otherwise connectionUserDefault is used.  
    public static final String PA_CONNECTION_USER = "pa.connection.user";
    public static String connectionUserDefault = "PA_CONN"; 

    // specify connectionPassword in a System property PA_CONNECTION_PASSWORD, otherwise connectionPasswordDefault is used.  
    public static final String PA_CONNECTION_PASSWORD = "pa.connection.password";
    public static String connectionPasswordDefault = "PA_CONN"; 

    // specify proxyUser in a System property PA_PROXYUSER, otherwise proxyUserDefault is used.  
    public static final String PA_PROXYUSER = "pa.proxyuser";
    public static String proxyUserDefault = "PA_PROXY";

    // specify proxyUser2 in a System property PA_PROXYUSER2, otherwise proxyUser2Default is used.  
    public static final String PA_PROXYUSER2 = "pa.proxyuser2";
    public static String proxyUser2Default = "PA_PROXY2";

    /** to setup Proxy Authentication users in Oracle db, need to execute in sqlPlus or EnterpriseManager
     * (sql in the following example uses default names):
    1 - Connect as sysdba
    connect sys/password as sysdba

    2 - Create connectionUser:
    create user PA_CONN identified by PA_CONN
    grant connect to PA_CONN

    3 - Create proxyUsers:
    create user PA_PROXY identified by PA_PROXY
    grant connect to PA_PROXY

    create user PA_PROXY2 identified by PA_PROXY2
    grant connect to PA_PROXY2

    4. Grant proxyUsers connection through connUser
    alter user PA_PROXY grant connect through PA_CONN
    alter user PA_PROXY2 grant connect through PA_CONN
    */

    public static String connectionUser; 
    public static String connectionPassword; 
    public static String proxyUser;
    public static String proxyUser2;
        
    public static Properties connectionProperties;
    public static Map proxyProperties;
    public static Map proxyProperties2;
    public static Map cancelProxyProperties;

    public static String getProperty(String property, String defaultValue) {
        String propertyValue = System.getProperty(property);
        
        if (propertyValue == null || propertyValue.equals("")) {
            return defaultValue;
        } else {
            return propertyValue;
        }
    }
    
    /*
     * Create all user names and properties.
     */
    public static void initialize() {
        // obtain user and password that should be used to connect to the db.
        connectionUser = getProperty(PA_CONNECTION_USER, "PA_CONN"); 
        connectionPassword = getProperty(PA_CONNECTION_PASSWORD, "PA_CONN"); 
        // connectionProperties used to connect to the db to test the users
        connectionProperties = new Properties();
        connectionProperties.setProperty("user", connectionUser);
        connectionProperties.setProperty("password", connectionPassword);
        
        // obtain proxyuser, put into proxyProperties.
        // proxyProperties could be used either by ServerSession or ClientSession (EMFactory or EntityManager).
        proxyUser = getProperty(PA_PROXYUSER, "PA_PROXY");
        proxyProperties = new HashMap(2);
        proxyProperties.put(PersistenceUnitProperties.ORACLE_PROXY_TYPE, OracleConnection.PROXYTYPE_USER_NAME);
        proxyProperties.put(OracleConnection.PROXY_USER_NAME, proxyUser);

        // obtain proxyuser2, put into proxyProperties2.
        // proxyProperties2 could be used by ClientSession (EntityManager) to override proxyProperties used by ServerSession (EMFactory).
        proxyUser2 = getProperty(PA_PROXYUSER2, "PA_PROXY2");
        proxyProperties2 = new HashMap(2);
        proxyProperties2.put(PersistenceUnitProperties.ORACLE_PROXY_TYPE, OracleConnection.PROXYTYPE_USER_NAME);
        proxyProperties2.put(OracleConnection.PROXY_USER_NAME, proxyUser2);

        // cancelProxyProperties could be used by ClientSession (EntityManager) to NOT to use proxyProperties used by ServerSession (EMFactory).
        cancelProxyProperties = new HashMap(1);
        cancelProxyProperties.put(PersistenceUnitProperties.ORACLE_PROXY_TYPE, "");
    }
    
    /*
     * Verify all the users correctly setup in the database.
     * Returns an empty string in case of success, otherwise returns the error message.
     */
    public static String verify(DatabaseSession dbSession) {
        String errorMsg = "";
        SessionExchanger exchanger = new SessionExchanger();
        DatabaseSession newSession = null;
        try {
            // create a simple database session that uses connectionProperties to connect.
            newSession = exchanger.createNewSession(dbSession, true, false, connectionProperties, null);
        } catch (Exception exception) {
            errorMsg = createErrorMsgConnectionFailed();
            errorMsg += createErrorMsgProxyFailed(true);
            errorMsg += createErrorMsgProxy2Failed(true);
        }

        // errorMsg.length() > 0 case:
        // if couldn't connect to connectionUser directly then there is 
        // no point in trying to connect proxyUsers through connectionUser.
        if(errorMsg.length() == 0) {
            // try to open proxy session using proxyUser
            try {
                Properties props = new Properties();
                props.setProperty(OracleConnection.PROXY_USER_NAME, proxyUser);
                OracleConnection oracleConnection = (oracle.jdbc.OracleConnection)((org.eclipse.persistence.internal.sessions.AbstractSession)newSession).getAccessor().getConnection(); 
                oracleConnection.openProxySession(OracleConnection.PROXYTYPE_USER_NAME, props);
                // close proxy session
                oracleConnection.close(OracleConnection.PROXY_SESSION);
            } catch (Exception exception) {
                errorMsg += createErrorMsgProxyFailed(false);
            }
    
            // try to open proxy session using proxyUser2
            try {
                Properties props = new Properties();
                props.setProperty(OracleConnection.PROXY_USER_NAME, proxyUser2);
                OracleConnection oracleConnection = (oracle.jdbc.OracleConnection)((org.eclipse.persistence.internal.sessions.AbstractSession)newSession).getAccessor().getConnection(); 
                oracleConnection.openProxySession(OracleConnection.PROXYTYPE_USER_NAME, props);
                // close proxy session
                oracleConnection.close(OracleConnection.PROXY_SESSION);
            } catch (Exception exception) {
                errorMsg += createErrorMsgProxy2Failed(false);
            }
        }

        // kill newSession, reconnect the original session.
        exchanger.returnOriginalSession();
        
        return errorMsg;
    }    

    static String createErrorMsgConnectionFailed() {
        // failed to connect using connectionUser / connectionPassword
        String str1 = "Failed to connect using user = "+ connectionUser + "; password = " +connectionPassword+".\n";
        String str2 = "Specify connectionUser in "+PA_CONNECTION_USER+" and connectionPassword in "+PA_CONNECTION_PASSWORD+" System properties.\n";
        String str3 = "Otherwise default connectionUser "+ connectionUserDefault +" and default connectionPassword "+ connectionPasswordDefault + " used.\n";
        String str4 = "In the db connectionUser should exist (authenticated by connectionPassword) and be authorized to connect:\n";
        String str5 = " create user "+connectionUser+" identified by "+connectionPassword+"\n";
        String str6 = " grant connect to "+connectionUser+"\n";
        return str1 + str2 + str3 + str4 + str5 + str6;
    }

    static String createErrorMsgProxyFailed(boolean connectionHasFailed) {
        // failed to open proxy session using proxyUser
        String str1 = "";
        if(!connectionHasFailed) {
            // printing this because connection was ok, but proxy connection failed.
            str1 = "Failed to open proxy session using proxyUser = "+ proxyUser+" on connection through "+connectionUser+".\n";
        }
        String str2 = "Specify proxyUser in "+PA_PROXYUSER+" System property. Otherwise default proxyUser "+ proxyUserDefault + " is used.\n";
        String str3 = "In the db proxyUser should exist and be authorized to connect both directly and through connectionUser:\n";
        String str4 = " create user "+proxyUser+" identified by "+proxyUser+"\n";
        String str5 = " grant connect to "+proxyUser+"\n";
        String str6 = " grant connect to "+proxyUser+" through "+connectionUser+"\n";
        return str1 + str2 + str3 + str4 + str5 + str6;
    }

    static String createErrorMsgProxy2Failed(boolean connectionHasFailed) {
        // failed to open proxy session using proxyUser2
        String str1 = "";
        if(!connectionHasFailed) {
            // printing this because connection was ok, but proxy connection failed.
            str1 = "Failed to open proxy session using proxyUser2 = "+ proxyUser2+" on connection through "+connectionUser+".\n";
        }
        String str2 = "Specify proxyUser2 in "+PA_PROXYUSER+" System property. Otherwise default proxyUser2 "+ proxyUser2Default + " is used.\n";
        String str3 = "In the db proxyUser2 should exist and be authorized to connect both directly and through connectionUser:\n";
        String str4 = " create user "+proxyUser2+" identified by "+proxyUser2+"\n";
        String str5 = " grant connect to "+proxyUser2+"\n";
        String str6 = " grant connect to "+proxyUser2+" through "+connectionUser+"\n";
        return str1 + str2 + str3 + str4 + str5 + str6;
    }
}
