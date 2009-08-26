/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
package org.eclipse.persistence.platform.server.sunas;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.transaction.sunas.SunAS9TransactionController;
import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.JavaLog;

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
public class SunAS9ServerPlatform extends ServerPlatformBase {

    /**
     * INTERNAL:
     * Default Constructor: All behavior for the default constructor is inherited
     */
    public SunAS9ServerPlatform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
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

    public SessionLog getServerLog() {
        return  new JavaLog();
    }
}
