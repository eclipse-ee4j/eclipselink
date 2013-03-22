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
 ******************************************************************************/  
package org.eclipse.persistence.platform.server.oc4j;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.transaction.oc4j.Oc4jTransactionController;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.platform.server.ServerPlatformBase;

import org.eclipse.persistence.internal.databaseaccess.Platform;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing Oc4j-specific server behavior.
 *
 * This platform overrides:
 *
 * getExternalTransactionControllerClass(): to use an Oc4j-specific controller class
 * initializeServerNameAndVersion(): to call an Oc4j library for this information
 *
 */
public class Oc4jPlatform extends ServerPlatformBase {

    /**
     * INTERNAL:
     * Default Constructor: All behavior for the default constructor is inherited
     */
    public Oc4jPlatform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
    }

    /**
     * INTERNAL: getExternalTransactionControllerClass(): Answer the class of external transaction controller to use
     * for Oc4j. This is read-only.
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
    		externalTransactionControllerClass = Oc4jTransactionController.class;
    	}
        return externalTransactionControllerClass;
    }

    /**
     * INTERNAL:  This method is used to unwrap the oracle connection wrapped by
     * the application server.  TopLink needs this unwrapped connection for certain
     * Oracle Specific support. (ie TIMESTAMPTZ)
     */
    public java.sql.Connection unwrapConnection(java.sql.Connection connection){
        Platform platform = getDatabaseSession().getDatasourceLogin().getDatasourcePlatform();
        if(platform.isOracle() && ((OraclePlatform)platform).canUnwrapOracleConnection()) {
            return ((OraclePlatform)platform).unwrapOracleConnection(connection);
        } else {
            return super.unwrapConnection(connection);
        }
    }

    /**
     * INTERNAL:
     * Clears statement cache of the wrapper connection.
     * Required by Oracle proxy authentication: currently connection statement cache
     * becomes invalid on switching to/from proxy session.
     * This method is called by OracleJDBC_10_1_0_2ProxyConnectionCustomizer  
     * before opening proxy session and before closing it.
     */
    public void clearStatementCache(java.sql.Connection connection) {   
        Platform platform = getDatabaseSession().getDatasourceLogin().getDatasourcePlatform();
        if(platform.isOracle()) {
            ((OraclePlatform)platform).clearOracleConnectionCache(connection);
        }
    }
}
