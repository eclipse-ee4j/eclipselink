/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.platform.server.was;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.transaction.was.*;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing WebSphere 5.0-specific server behaviour.
 *
 * This platform has:
 * - No JMX MBean runtime services
 *
 * This platform overrides:
 *
 * getExternalTransactionControllerClass(): to use an WebSphere 5.0-specific controller class
 */
public class WebSphere_5_0_Platform extends WebSpherePlatform {

    /**
     * INTERNAL:
     * Default Constructor: All behaviour for the default constructor is inherited
     */
    public WebSphere_5_0_Platform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
    }

    /**
     * INTERNAL: getExternalTransactionControllerClass(): Answer the class of external transaction controller to use
     * for WebSphere_5_0. This is read-only.
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
    		externalTransactionControllerClass = WebSphereTransactionController_5_0.class;
    	}
        return externalTransactionControllerClass;
    }
}