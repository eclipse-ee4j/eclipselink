/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.eis.adapters.aq;

import java.security.AccessController;
import java.sql.DriverManager;
import javax.sql.DataSource;
import javax.naming.*;
import javax.resource.*;
import javax.resource.cci.*;
import oracle.AQ.*;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;

/**
 * Connection factory for AQ JCA adapter.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class AQConnectionFactory implements ConnectionFactory {

    /**
     * Default constructor.
     */
    public AQConnectionFactory() {
    }

    public Connection getConnection() throws ResourceException {
        return getConnection(new AQConnectionSpec());
    }

    public Connection getConnection(ConnectionSpec spec) throws ResourceException {
        AQConnectionSpec aqSpec;
        AQSession session;
        java.sql.Connection connection;
        try {
            aqSpec = (AQConnectionSpec)spec;
            if (aqSpec.hasDatasource()) {
                DataSource datasource = (DataSource)new InitialContext().lookup(aqSpec.getDatasource());
                connection = datasource.getConnection();
            } else {
                connection = DriverManager.getConnection(aqSpec.getURL(), aqSpec.getUser(), aqSpec.getPassword());
                connection.setAutoCommit(false);
            }
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                AccessController.doPrivileged(new PrivilegedClassForName("oracle.AQ.AQOracleDriver", true, this.getClass().getClassLoader()));
            }else{
                PrivilegedAccessHelper.getClassForName("oracle.AQ.AQOracleDriver", true, this.getClass().getClassLoader());
            }
            session = AQDriverManager.createAQSession(connection);
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }

        return new AQConnection(session, connection, aqSpec);
    }

    public ResourceAdapterMetaData getMetaData() {
        return new AQAdapterMetaData();
    }

    public RecordFactory getRecordFactory() {
        return new AQRecordFactory();
    }

    public Reference getReference() {
        return new Reference(getClass().getName());
    }

    public void setReference(Reference reference) {
    }
}
