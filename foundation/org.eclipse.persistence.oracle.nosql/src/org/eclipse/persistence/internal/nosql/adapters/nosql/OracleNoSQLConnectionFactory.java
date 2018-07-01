/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.nosql.adapters.nosql;

import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;
import javax.resource.cci.RecordFactory;
import javax.resource.cci.ResourceAdapterMetaData;

import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;

/**
 * Connection factory for Oracle NoSQL JCA adapter.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class OracleNoSQLConnectionFactory implements ConnectionFactory {

    /**
     * Default constructor.
     */
    public OracleNoSQLConnectionFactory() {
    }

    public Connection getConnection() throws ResourceException {
        return getConnection(new OracleNoSQLJCAConnectionSpec());
    }

    public Connection getConnection(ConnectionSpec spec) throws ResourceException {
        OracleNoSQLJCAConnectionSpec connectionSpec = (OracleNoSQLJCAConnectionSpec)spec;
        KVStore store = null;
        try {
            KVStoreFactory factory = new KVStoreFactory();
            String[] hosts = new String[connectionSpec.getHosts().size()];
            int index = 0;
            for (String host : connectionSpec.getHosts()) {
                hosts[index] = host;
                index++;
            }
            KVStoreConfig config = new KVStoreConfig(connectionSpec.getStore(), hosts);
            store = factory.getStore(config);
        } catch (Exception exception) {
            ResourceException resourceException = new ResourceException(exception.toString());
            resourceException.initCause(exception);
            throw resourceException;
        }

        return new OracleNoSQLConnection(store, connectionSpec);
    }

    public ResourceAdapterMetaData getMetaData() {
        return new OracleNoSQLAdapterMetaData();
    }

    public RecordFactory getRecordFactory() {
        return new OracleNoSQLRecordFactory();
    }

    public Reference getReference() {
        return new Reference(getClass().getName());
    }

    public void setReference(Reference reference) {
    }
}
