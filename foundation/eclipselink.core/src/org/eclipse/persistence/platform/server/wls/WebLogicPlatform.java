/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import java.lang.reflect.Method;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.transaction.wls.WebLogicTransactionController;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing WebLogic-specific server behavior.
 * <p>
 * This platform overrides:
 * <ul>
 * <li>getExternalTransactionControllerClass(): to use the WebLogic-specific controller class
 * <li>getServerNameAndVersion(): to call the WebLogic library for this information
 * </ul>
 */
public class WebLogicPlatform extends ServerPlatformBase {

    /**
     * INTERNAL:
     * Default Constructor: All behavior for the default constructor is inherited
     */
    public WebLogicPlatform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
        this.disableRuntimeServices();
    }

    /**
     * INTERNAL:
     * Set the WLS version number through reflection.
     */
    public void initializeServerNameAndVersion() {
        try {
            Class clazz = PrivilegedAccessHelper.getClassForName("weblogic.version");
            Method method = PrivilegedAccessHelper.getMethod(clazz, "getBuildVersion", null, false);
            this.serverNameAndVersion =  (String)PrivilegedAccessHelper.invokeMethod(method, null, null);
        } catch (Exception exception) {
            getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, exception);
        }
    }

    /**
     * INTERNAL: getExternalTransactionControllerClass(): Answer the class of external transaction controller to use
     * for WebLogic. This is read-only.
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
    		externalTransactionControllerClass = WebLogicTransactionController.class;
    	}
        return externalTransactionControllerClass;
    }
}