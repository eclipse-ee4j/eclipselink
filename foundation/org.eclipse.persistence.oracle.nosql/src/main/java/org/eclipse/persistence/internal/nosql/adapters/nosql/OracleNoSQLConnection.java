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

import jakarta.resource.*;
import jakarta.resource.cci.*;
import oracle.kv.KVStore;

import org.eclipse.persistence.exceptions.ValidationException;

/**
 * Connection to Oracle NoSQL
 * This connection wraps a KVStore.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class OracleNoSQLConnection implements Connection {
    protected OracleNoSQLJCAConnectionSpec spec;
    protected OracleNoSQLTransaction transaction;
    public KVStore getStore() {
        return store;
    }

    protected KVStore store;

    /**
     * Create the connection on a native AQ session.
     * The session must be connected to a JDBC connection.
     */
    public OracleNoSQLConnection(KVStore store, OracleNoSQLJCAConnectionSpec spec) {
        this.store = store;
        this.transaction = new OracleNoSQLTransaction(this);
        this.spec = spec;
    }

    /**
     * Close the AQ native session and the database connection.
     */
    @Override
    public void close() throws ResourceException {
        try {
            this.store.close();
        } catch (Exception exception) {
            ResourceException resourceException = new ResourceException(exception.toString());
            resourceException.initCause(exception);
            throw resourceException;
        }
    }

    @Override
    public Interaction createInteraction() {
        return new OracleNoSQLInteraction(this);
    }

    public OracleNoSQLJCAConnectionSpec getConnectionSpec() {
        return spec;
    }

    @Override
    public LocalTransaction getLocalTransaction() {
        return transaction;
    }

    public OracleNoSQLTransaction getOracleNoSQLTransaction() {
        return transaction;
    }

    @Override
    public ConnectionMetaData getMetaData() {
        return new OracleNoSQLConnectionMetaData(this);
    }

    /**
     * Result sets are not supported.
     */
    @Override
    public ResultSetInfo getResultSetInfo() {
        throw ValidationException.operationNotSupported("getResultSetInfo");
    }
}
