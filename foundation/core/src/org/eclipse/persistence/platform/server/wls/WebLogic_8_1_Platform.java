/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.platform.server.wls;

import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing WebLogic81 specific behaviour.
 *
 */
public class WebLogic_8_1_Platform extends WebLogicPlatform {
    /**
     * INTERNAL:
     * Default Constructor: All behaviour for the default constructor is inherited
     */
    public WebLogic_8_1_Platform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
    }

    /**
     * INTERNAL:  This method is used to unwrap the oracle connection wrapped by
     * the application server.  TopLink needs this unwrapped connection for certain
     * Oracle Specific support. (ie TIMESTAMPTZ)
     */
    public java.sql.Connection unwrapOracleConnection(java.sql.Connection connection){
       	// TODO: Replace with reflection
    	throw new RuntimeException("TODO: Replace with reflective access");
 /*
    	try {
            if (connection instanceof weblogic.jdbc.wrapper.Connection){
                return ((weblogic.jdbc.wrapper.Connection)connection).getVendorConnection();
            }
            return connection;
        } catch (java.sql.SQLException e) {
            ((DatabaseSessionImpl)getDatabaseSession()).log(SessionLog.WARNING, SessionLog.CONNECTION, "cannot_unwrap_connection", e);
            return connection;            
        }
        */
    }
}
