/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.unwrappedconnection;

import java.util.Properties;

import javax.sql.DataSource;

import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.sessions.Connector;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.tests.customsqlstoredprocedures.CustomSQLTestModel;
import org.eclipse.persistence.testing.framework.TestWarningException;

public class UnwrapConnectionCustomSQLTestModel extends CustomSQLTestModel{
    protected Connector originalConnector;
    protected ServerPlatform originalServerPlatform;
    protected  boolean originalShouldUseExternalConnectionPooling;

    public UnwrapConnectionCustomSQLTestModel() {
        super("This model tests store procedure using unwrapped connection.");
    }
    
    /**
     * Revert the descriptors back to their old state.
     */
    public void reset() {
        DatabaseSession session = (DatabaseSession)getSession();
        session.logout();
        session.getLogin().setConnector(originalConnector);
        session.getLogin().dontUseExternalConnectionPooling();
        session.setServerPlatform(originalServerPlatform);
        if(originalShouldUseExternalConnectionPooling){
            session.getLogin().useExternalConnectionPooling();
        } else {
            session.getLogin().dontUseExternalConnectionPooling();
        }
        session.login();
        super.reset();
    }
    
    public void setup(){
        if (!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("WARNING: This model is not supposed to be run on databases other than Oracle.");
        }
        DatabaseSession session = (DatabaseSession)getSession();
        session.logout();
        
        originalConnector = session.getLogin().getConnector();// save the connector to restore later
        
        DataSource dataSource = new TestOracleDataSource(session.getLogin().getDriverClassName(), session.getLogin().getConnectionString(), (Properties)session.getLogin().getProperties().clone());
        session.getLogin().setConnector(new JNDIConnector(dataSource));
        
        originalServerPlatform= session.getServerPlatform();
        session.setServerPlatform(new TestServerPlatform(session));
        originalShouldUseExternalConnectionPooling = session.getLogin().shouldUseExternalConnectionPooling();
        session.getLogin().useExternalConnectionPooling();
        
        session.login();
        super.setup();
    }

}
