/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.nosql.adapters.nosql;

import javax.naming.Reference;
import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;
import jakarta.resource.cci.ConnectionFactory;
import jakarta.resource.cci.ConnectionSpec;
import jakarta.resource.cci.RecordFactory;
import jakarta.resource.cci.ResourceAdapterMetaData;

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

    @Override
    public Connection getConnection() throws ResourceException {
        return getConnection(new OracleNoSQLJCAConnectionSpec());
    }

    @Override
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

    @Override
    public ResourceAdapterMetaData getMetaData() {
        return new OracleNoSQLAdapterMetaData();
    }

    @Override
    public RecordFactory getRecordFactory() {
        return new OracleNoSQLRecordFactory();
    }

    @Override
    public Reference getReference() {
        return new Reference(getClass().getName());
    }

    @Override
    public void setReference(Reference reference) {
    }
}
