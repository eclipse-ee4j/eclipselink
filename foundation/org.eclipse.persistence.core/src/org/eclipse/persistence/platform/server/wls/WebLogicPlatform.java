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
 ******************************************************************************/
package org.eclipse.persistence.platform.server.wls;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.transaction.wls.WebLogicTransactionController;

/**
 * PUBLIC:
 * 
 * This is the concrete subclass responsible for representing WebLogic-specific
 * server behavior.
 * <p>
 * This platform overrides:
 * <ul>
 * <li>getExternalTransactionControllerClass(): to use the WebLogic-specific
 * controller class
 * <li>getServerNameAndVersion(): to call the WebLogic library for this
 * information
 * </ul>
 */
public class WebLogicPlatform extends ServerPlatformBase {

    /**
     * Cached WLS connection class used to reflectively check connections and
     * unwrap them.
     */
    protected Class weblogicConnectionClass;

    /**
     * Cached WLConnection.getVendorConnection() Method used for
     * unwrapping connections.
     */
    protected Method vendorConnectionMethod;
    
    /**
     * Cached WLConnection.clearStatementCache() Method used for
     * clearing statement cache.
     */
    protected Method clearStatementCacheMethod;
    
    /**
     * INTERNAL: Default Constructor: All behavior for the default constructor
     * is inherited.
     */
    public WebLogicPlatform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
    }

    /**
     * INTERNAL: Set the WLS version number through reflection.
     */
    public void initializeServerNameAndVersion() {
        try {
            Class clazz = PrivilegedAccessHelper.getClassForName("weblogic.version");
            Method method = PrivilegedAccessHelper.getMethod(clazz, "getBuildVersion", null, false);
            this.serverNameAndVersion = (String) PrivilegedAccessHelper.invokeMethod(method, null, null);
        } catch (Exception exception) {
            getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, exception);
        }
    }

    /**
     * INTERNAL: 
     * getApplicationName(): Answer the name of the module (EAR name) that this session is associated with.
     * Answer "unknown" if there is no application name available.
     * Default behavior is to return "unknown" - we override this behavior here for WebLogic.
     * 
     * There are 4 levels of implementation.
     * 1) use the property override weblogic.applicationName, or
     * 2) perform a reflective weblogic.work.executeThreadRuntime.getApplicationName() call (build 10.3+), or
     * 3) extract the moduleName:persistence_unit from the weblogic classloader string representation (build 10.3), or
     * 3) defer to superclass - usually return "unknown"
     *
     * @return String applicationName
     */
    public String getApplicationName() {
        return DEFAULT_SERVER_NAME_AND_VERSION;
    }
    
    /**
     * INTERNAL: getExternalTransactionControllerClass(): Answer the class of
     * external transaction controller to use for WebLogic. This is read-only.
     * 
     * @return Class externalTransactionControllerClass
     * 
     * @see org.eclipse.persistence.transaction.JTATransactionController
     * @see ServerPlatformBase#isJTAEnabled()
     * @see ServerPlatformBase#disableJTA()
     * @see ServerPlatformBase#initializeExternalTransactionController()
     */
    public Class getExternalTransactionControllerClass() {
        if (externalTransactionControllerClass == null) {
            externalTransactionControllerClass = WebLogicTransactionController.class;
        }
        return externalTransactionControllerClass;
    }

    /**
     * Return the class (interface) for the WebLogic JDBC connection wrapper.
     */
    protected Class getWebLogicConnectionClass() {
        if (this.weblogicConnectionClass == null) {
            try {
                this.weblogicConnectionClass = (Class) getDatabaseSession().getPlatform().convertObject("weblogic.jdbc.extensions.WLConnection", Class.class);
            } catch (Throwable exception) {
                getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, exception);
                this.weblogicConnectionClass = void.class;
            }
        }
        return this.weblogicConnectionClass;
    }
    
    /**
     * Return the method for the WebLogic JDBC connection wrapper vendorConnection.
     */
    protected Method getVendorConnectionMethod() {
        if ((this.vendorConnectionMethod == null) && (!getWebLogicConnectionClass().equals(void.class))) {
            try {
                this.vendorConnectionMethod = PrivilegedAccessHelper.getDeclaredMethod(getWebLogicConnectionClass(), "getVendorConnection", new Class[0]);
            } catch (NoSuchMethodException exception) {
                getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, exception);
            }
        }

        return this.vendorConnectionMethod;
    }

    /**
     * Unwraps the WebLogic JDBC connection wrapping using the WebLogic API reflectively.
     */
    @Override
    public Connection unwrapConnection(Connection connection) {
        if (getWebLogicConnectionClass().isInstance(connection) && getVendorConnectionMethod() != null) {
            try {
                return (Connection) PrivilegedAccessHelper.invokeMethod(getVendorConnectionMethod(), connection);
            } catch (IllegalAccessException exception) {
                getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, exception);
            } catch (InvocationTargetException exception) {
                getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, exception);
            }
        }

        return super.unwrapConnection(connection);
    }

    /**
     * Return the method for the WebLogic connection clearStatementCache method.
     */
    protected Method getClearStatementCacheMethod() {
        if ((this.clearStatementCacheMethod == null) && (!getWebLogicConnectionClass().equals(void.class))) {
            try {
                this.clearStatementCacheMethod = PrivilegedAccessHelper.getDeclaredMethod(getWebLogicConnectionClass(), "clearStatementCache", new Class[0]);
            } catch (NoSuchMethodException exception) {
                getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, exception);
            }
        }

        return this.clearStatementCacheMethod;
    }

    /**
     * INTERNAL:
     * Clears statement cache of WebLogic connection using the WebLogic API reflectively.
     * Required by Oracle proxy authentication: currently connection statement cache
     * becomes invalid on switching to/from proxy session.
     * This method is called by OracleJDBC_10_1_0_2ProxyConnectionCustomizer  
     * before opening proxy session and before closing it.
     */
    public void clearStatementCache(Connection connection) {   
        if (getWebLogicConnectionClass().isInstance(connection) && getClearStatementCacheMethod() != null) {
            try {
                PrivilegedAccessHelper.invokeMethod(getClearStatementCacheMethod(), connection);
            } catch (IllegalAccessException exception) {
                getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, exception);
            } catch (InvocationTargetException exception) {
                getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, exception);
            }
        }
    }
}
