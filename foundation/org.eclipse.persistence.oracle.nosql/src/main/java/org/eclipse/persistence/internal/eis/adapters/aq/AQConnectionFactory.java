/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.eis.adapters.aq;

import java.security.AccessController;
import java.sql.DriverManager;
import javax.sql.DataSource;
import javax.naming.*;
import jakarta.resource.*;
import jakarta.resource.cci.*;
import oracle.jakarta.AQ.AQSession;
import oracle.jakarta.AQ.AQDriverManager;
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

    @Override
    public Connection getConnection() throws ResourceException {
        return getConnection(new AQConnectionSpec());
    }

    @Override
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
                AccessController.doPrivileged(new PrivilegedClassForName<>("oracle.jakarta.AQ.AQOracleDriver", true, this.getClass().getClassLoader()));
            }else{
                PrivilegedAccessHelper.getClassForName("oracle.jakarta.AQ.AQOracleDriver", true, this.getClass().getClassLoader());
            }
            session = AQDriverManager.createAQSession(connection);
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }

        return new AQConnection(session, connection, aqSpec);
    }

    @Override
    public ResourceAdapterMetaData getMetaData() {
        return new AQAdapterMetaData();
    }

    @Override
    public RecordFactory getRecordFactory() {
        return new AQRecordFactory();
    }

    @Override
    public Reference getReference() {
        return new Reference(getClass().getName());
    }

    @Override
    public void setReference(Reference reference) {
    }
}
