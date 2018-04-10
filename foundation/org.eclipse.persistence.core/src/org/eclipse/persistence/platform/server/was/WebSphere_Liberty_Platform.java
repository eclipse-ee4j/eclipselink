/*******************************************************************************
 * Copyright (c) 2014, 2018  IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Rick Curtis - Add support for WebSphere Liberty.
 ******************************************************************************/
package org.eclipse.persistence.platform.server.was;

import java.sql.Connection;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.transaction.was.WebSphereLibertyTransactionController;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing WebSphere-specific server behavior.
 *
 * This platform has:
 * <ul>
 * <li>WebSphereLibertyTransactionController (JTA integration).
 * </ul>
 */
public class WebSphere_Liberty_Platform extends WebSphere_7_Platform {
    /**
     * INTERNAL: Default Constructor: All behavior for the default constructor is inherited
     */
    public WebSphere_Liberty_Platform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
    }

    /**
     * INTERNAL: getExternalTransactionControllerClass(): Answer the class of external transaction
     * controller to use for WebSphere Liberty. This is read-only.
     *
     * @return Class externalTransactionControllerClass
     *
     * @see org.eclipse.persistence.transaction.JTATransactionController
     * @see org.eclipse.persistence.platform.server.ServerPlatformBase#isJTAEnabled()
     * @see org.eclipse.persistence.platform.server.ServerPlatformBase#disableJTA()
     * @see org.eclipse.persistence.platform.server.ServerPlatformBase#initializeExternalTransactionController()
     */
    @Override
    public Class getExternalTransactionControllerClass() {
        if (externalTransactionControllerClass == null) {
            externalTransactionControllerClass = WebSphereLibertyTransactionController.class;
        }
        return externalTransactionControllerClass;
    }

    /**
     * This method overrides functionality exposed in the base WebSpherePlatform as Liberty doesn't
     * provide the same support.
     */
    @Override
    public Connection unwrapConnection(Connection connection) {
        try {
            return connection.getMetaData().getConnection();
        } catch (java.sql.SQLException e) {
            getAbstractSession().log(SessionLog.WARNING, SessionLog.CONNECTION, "cannot_unwrap_connection", e);
            return connection;
        }
    }
}
