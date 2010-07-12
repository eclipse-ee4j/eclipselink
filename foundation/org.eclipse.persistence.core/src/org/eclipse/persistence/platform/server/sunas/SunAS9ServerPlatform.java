/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.platform.server.sunas;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.services.glassfish.MBeanGlassfishRuntimeServices;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.transaction.sunas.SunAS9TransactionController;
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
 * This is the concrete subclass responsible for representing SunAS9-specific server behavior.
 *
 * This platform overrides:
 *
 * getExternalTransactionControllerClass(): to use an SunAS9-specific controller class
 *
 */
public class SunAS9ServerPlatform extends JMXServerPlatformBase implements JMXEnabledPlatform {

    /**
     * INTERNAL:
     * Default Constructor: All behavior for the default constructor is inherited
     */
    public SunAS9ServerPlatform(DatabaseSession newDatabaseSession) {
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
    		externalTransactionControllerClass = SunAS9TransactionController.class;
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
            getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, e);
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

}    

