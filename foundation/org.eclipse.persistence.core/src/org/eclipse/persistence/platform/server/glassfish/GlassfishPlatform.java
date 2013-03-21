/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     06/30/2010-2.1.1 Michael O'Brien 
 *       - 316513: Enable JMX MBean functionality for JBoss, Glassfish and WebSphere in addition to WebLogic
 *       Move JMX MBean generic registration code up from specific platforms
 *       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/316513</link>        
 ******************************************************************************/  
package org.eclipse.persistence.platform.server.glassfish;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.services.glassfish.MBeanGlassfishRuntimeServices;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.transaction.glassfish.GlassfishTransactionController;
import org.eclipse.persistence.platform.server.JMXEnabledPlatform;
import org.eclipse.persistence.platform.server.JMXServerPlatformBase;
import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.JavaLog;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing Glassfish server behavior.
 *
 * This platform overrides:
 *
 * getExternalTransactionControllerClass(): to use an Glassfish controller class
 *
 */
public class GlassfishPlatform extends JMXServerPlatformBase implements JMXEnabledPlatform {

    /**
     * The following constants and attributes are used to determine the module and application name
     * to satisfy the requirements for 248746 where we provide an identifier pair for JMX sessions.
     * Each application can have several modules.
     * 1) Application name - the persistence unit associated with the session (a 1-1 relationship)
     * 2) Module name - the ejb or war jar name (there is a 1-many relationship for module:session(s)) 
     */
    static {
        /** Override by subclass: Search String in application server ClassLoader for the application:persistence_unit name */
        APP_SERVER_CLASSLOADER_APPLICATION_PU_SEARCH_STRING_PREFIX = "URLEntry : file:/";
        /** Override by subclass: Search String in application server session for ejb modules */
        APP_SERVER_CLASSLOADER_MODULE_EJB_SEARCH_STRING_PREFIX = "_jar/";
        /** Override by subclass: Search String in application server session for war modules */
        APP_SERVER_CLASSLOADER_MODULE_WAR_SEARCH_STRING_PREFIX = "_war/";
        APP_SERVER_CLASSLOADER_APPLICATION_PU_SEARCH_STRING_POSTFIX = "]";
        APP_SERVER_CLASSLOADER_MODULE_EJB_WAR_SEARCH_STRING_POSTFIX = "postfix,match~not;required^";
        
    }
    
    /**
     * INTERNAL:
     * Default Constructor: All behavior for the default constructor is inherited
     */
    public GlassfishPlatform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
        this.enableRuntimeServices();        
        // Create the JMX MBean specific to this platform for later registration
        this.prepareServerSpecificServicesMBean();
    }

    /**
     * INTERNAL: getExternalTransactionControllerClass(): Answer the class of external transaction controller to use
     * for SUN AS9. This is read-only.
     *
     * @return Class externalTransactionControllerClass
     *
     * @see org.eclipse.persistence.transaction.JTATransactionController
     * @see ServerPlatformBase#isJTAEnabled()
     * @see ServerPlatformBase#disableJTA()
     * @see ServerPlatformBase#initializeExternalTransactionController()
     */
    public Class getExternalTransactionControllerClass() {
    	if (externalTransactionControllerClass == null){
    		externalTransactionControllerClass = GlassfishTransactionController.class;
    	}
        return externalTransactionControllerClass;
    }


    public java.sql.Connection unwrapConnection(final Connection connection)  {
        Connection unwrappedConnection;

        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            unwrappedConnection = AccessController.doPrivileged(new PrivilegedAction<Connection>() {
                public Connection run() {
                    return unwrapGlassFishConnectionHelper(connection);
                }
            });
        } else {
            unwrappedConnection = unwrapGlassFishConnectionHelper(connection);
        }

        if (unwrappedConnection == null) {
            unwrappedConnection = super.unwrapConnection(connection);
        }
        return unwrappedConnection;
    }

    /**
     * @return unwrapped GlassFish connection, null if connection can not be unwrapped.
     */
    private Connection unwrapGlassFishConnectionHelper(Connection connection) {
        // Currently "GlassFish" creates a separate instance of jdbc connector classloader
        // for each application. The connection wrapper passed here is created using this class loader. Hence caching
        // the class will not help.
        // If GlassFish behavior changes, both reflective call below should be cached. 
        Connection unwrappedConnection = null;
        try {
            Class connectionWrapperClass = connection.getClass().getClassLoader().loadClass("com.sun.gjc.spi.base.ConnectionHolder");
            if(connectionWrapperClass.isInstance(connection) ) {
                Method unwrapMethod = connectionWrapperClass.getDeclaredMethod("getConnection");
                unwrappedConnection = (Connection) unwrapMethod.invoke(connection);
            }
        } catch (Exception e) {
            getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, SessionLog.SERVER, e);
        }

        return unwrappedConnection;
    }

    public SessionLog getServerLog() {
        return  new JavaLog();
    }
    
    @Override
    public boolean isRuntimeServicesEnabledDefault() {
        return true;
    }
    
    /**
     * INTERNAL: 
     * prepareServerSpecificServicesMBean(): Server specific implementation of the
     * creation and deployment of the JMX MBean to provide runtime services for the
     * databaseSession.
     *
     * Default is to do nothing.
     * Implementing platform classes must override this function and supply
     * the server specific MBean instance for later registration by calling it in the constructor.  
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     * @see #registerMBean()
     */
    public void prepareServerSpecificServicesMBean() {
        // No check for an existing cached MBean - we will replace it if it exists
        if(shouldRegisterRuntimeBean) {
            this.setRuntimeServicesMBean(new MBeanGlassfishRuntimeServices(getDatabaseSession()));
        }
    }

    /**
     * INTERNAL: 
     * serverSpecificRegisterMBean(): Server specific implementation of the
     * creation and deployment of the JMX MBean to provide runtime services for my
     * databaseSession.
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     * @see #registerMBean()
     */
    @Override
    public void serverSpecificRegisterMBean() {
       super.serverSpecificRegisterMBean();
        // get and cache module and application name during registration
        initializeApplicationNameAndModuleName();
    }
        
}    

