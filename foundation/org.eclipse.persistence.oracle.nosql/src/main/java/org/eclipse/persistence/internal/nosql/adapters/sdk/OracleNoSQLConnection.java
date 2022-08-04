/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.nosql.adapters.sdk;

import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;
import jakarta.resource.cci.ConnectionMetaData;
import jakarta.resource.cci.Interaction;
import jakarta.resource.cci.LocalTransaction;
import jakarta.resource.cci.ResultSetInfo;

import oracle.nosql.driver.NoSQLHandle;

import org.eclipse.persistence.exceptions.ValidationException;

/**
 * Connection to Oracle NoSQL
 * This connection wraps a NoSQLHandle.
 *
 * @author Radek Felcman
 * @since EclipseLink 4.0
 */
public class OracleNoSQLConnection implements Connection {
    private OracleNoSQLJCAConnectionSpec spec;
    private OracleNoSQLTransaction transaction;

    private NoSQLHandle noSQLHandle = null;

    /**
     * Create the connection on a native AQ session.
     * The session must be connected to a JDBC connection.
     */
    public OracleNoSQLConnection(NoSQLHandle noSQLHandle, OracleNoSQLJCAConnectionSpec spec) {
        this.noSQLHandle = noSQLHandle;
        this.transaction = new OracleNoSQLTransaction(this);
        this.spec = spec;
    }

    /**
     * Close the AQ native session and the database connection.
     */
    @Override
    public void close() throws ResourceException {
        try {
            this.noSQLHandle.close();
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
     * TODO check if Result sets are not supported.
     */
    @Override
    public ResultSetInfo getResultSetInfo() {
        throw ValidationException.operationNotSupported("getResultSetInfo");
    }

    public NoSQLHandle getNoSQLHandle() {
        return noSQLHandle;
    }
}
