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

import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.sessions.DatabaseSession;

import java.sql.Connection;
import org.eclipse.persistence.transaction.was.WebSphereTransactionController;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing WebSphere-specific server behaviour.
 *
 * This platform has:
 * - No JMX MBean runtime services
 * - transaction controller classes overridden in its subclasses
 */
public class WebSpherePlatform extends ServerPlatformBase {

    /**
     * INTERNAL:
     * Default Constructor: All behaviour for the default constructor is inherited
     */
    public WebSpherePlatform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
        this.disableRuntimeServices();
    }
	
	public Connection unwrapOracleConnection(Connection connection) {
		// TODO: do reflectively
		throw new RuntimeException("TODO: DO Reflectively");
		/*
		 * if (connection instanceof
		 * com.ibm.ws.rsadapter.jdbc.WSJdbcConnection){ return
		 * (Connection)com.ibm.ws.rsadapter.jdbc.WSJdbcUtil.getNativeConnection((com.ibm.ws.rsadapter.jdbc.WSJdbcConnection)
		 * connection); } return connection;
		 */
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
}
