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
package org.eclipse.persistence.platform.server.was;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.server.JMXServerPlatformBase;
import org.eclipse.persistence.sessions.DatabaseSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import org.eclipse.persistence.transaction.was.WebSphereTransactionController;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing WebSphere-specific server behavior.
 *
 * This platform has:
 * <ul>
 * <li> WebSphereTransactionController (JTA integration).
 * <li> DataSource connection unwrapping (Oracle JDBC API support)
 * </ul>
 */
public class WebSpherePlatform extends JMXServerPlatformBase {

    /**
     * Cached WAS connection class used to reflectively check connections and unwrap them.
     */
    protected Class websphereConnectionClass;

    /**
     * Cached WAS util class used to reflectively check connections and unwrap them.
     */
    protected Class websphereUtilClass;
    
    /**
     * Cached WAS util method used for unwrapping connections.
     */
    protected Method vendorConnectionMethod;
    
    /**
     * INTERNAL:
     * Default Constructor: All behavior for the default constructor is inherited
     */
    public WebSpherePlatform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
        this.disableRuntimeServices();
    }
    
    /**
     * INTERNAL: getExternalTransactionControllerClass(): Answer the class of 
     * external transaction controller to use for WebSphere. This is 
     * read-only.
     *
     * @return Class externalTransactionControllerClass
     *
     * @see org.eclipse.persistence.transaction.JTATransactionController
     * @see ServerPlatformBase.isJTAEnabled()
     * @see ServerPlatformBase.disableJTA()
     * @see ServerPlatformBase.initializeExternalTransactionController()
     */
    public Class getExternalTransactionControllerClass() {
    	if (externalTransactionControllerClass == null){
    		externalTransactionControllerClass = WebSphereTransactionController.class;
    	}
        return externalTransactionControllerClass;
    }


    /**
     * Return the class (interface) for the WebSphere JDBC connection wrapper.
     */
    protected Class getWebsphereUtilClass() {
        if (this.websphereUtilClass == null) {
            try {
                this.websphereUtilClass = (Class) getDatabaseSession().getPlatform().convertObject("com.ibm.ws.rsadapter.jdbc.WSJdbcUtil", Class.class);
            } catch (Throwable exception) {
                getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, SessionLog.SERVER, exception);
                this.websphereUtilClass = void.class;
            }
        }
        return this.websphereUtilClass;
    }
    
    /**
     * Return the class (interface) for the WebSphere JDBC connection wrapper.
     */
    protected Class getWebsphereConnectionClass() {
        if (this.websphereConnectionClass == null) {
            try {
                this.websphereConnectionClass = (Class) getDatabaseSession().getPlatform().convertObject("com.ibm.ws.rsadapter.jdbc.WSJdbcConnection", Class.class);
            } catch (Throwable exception) {
                getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, SessionLog.SERVER, exception);
                this.websphereConnectionClass = void.class;
            }
        }
        return this.websphereConnectionClass;
    }
    
    /**
     * Return the method for the WebSphere JDBC connection wrapper vendorConnection.
     */
    protected Method getVendorConnectionMethod() {
        if ((this.vendorConnectionMethod == null) && (!getWebsphereUtilClass().equals(void.class))) {
            try {
                Class args[] = new Class[1];
                args[0] = getWebsphereConnectionClass();
                this.vendorConnectionMethod = PrivilegedAccessHelper.getDeclaredMethod(getWebsphereUtilClass(), "getNativeConnection", args);
            } catch (NoSuchMethodException exception) {
                getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, SessionLog.SERVER, exception);
            }
        }

        return this.vendorConnectionMethod;
    }

    /**
     * Unwraps the WebSphere JDBC connection wrapping using the WebLogic API reflectively.
     */
    @Override
    public Connection unwrapConnection(Connection connection) {
        if (getWebsphereConnectionClass().isInstance(connection) && getVendorConnectionMethod() != null) {
            try {
                return (Connection) PrivilegedAccessHelper.invokeMethod(getVendorConnectionMethod(), null, new Object[]{connection});
            } catch (IllegalAccessException exception) {
                getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, SessionLog.SERVER, exception);
            } catch (InvocationTargetException exception) {
                getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, SessionLog.SERVER, exception);
            }
        }

        return super.unwrapConnection(connection);
    }
    
}
