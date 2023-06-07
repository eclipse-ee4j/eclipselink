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

import jakarta.resource.*;
import jakarta.resource.cci.*;
import oracle.jakarta.AQ.AQSession;
import org.eclipse.persistence.exceptions.ValidationException;

/**
 * Connection to Oracle AQ JCA adapter.
 * This connection wraps a AQ native session (which wraps a JDBC connection).
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class AQConnection implements Connection {
    protected AQConnectionSpec spec;
    protected AQTransaction transaction;
    protected AQSession session;
    protected java.sql.Connection databaseConnection;

    /**
     * Create the connection on a native AQ session.
     * The session must be connected to a JDBC connection.
     */
    public AQConnection(AQSession session, java.sql.Connection connection, AQConnectionSpec spec) {
        this.session = session;
        this.databaseConnection = connection;
        this.transaction = new AQTransaction(this);
        this.spec = spec;
    }

    /**
     * Return the JDBC database connection.
     */
    public java.sql.Connection getDatabaseConnection() {
        return databaseConnection;
    }

    /**
     * Return the AQ native session.
     */
    public AQSession getSession() {
        return session;
    }

    /**
     * Close the AQ native session and the database connection.
     */
    @Override
    public void close() throws ResourceException {
        try {
            getSession().close();
            getDatabaseConnection().close();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }

    @Override
    public Interaction createInteraction() {
        return new AQInteraction(this);
    }

    public AQConnectionSpec getConnectionSpec() {
        return spec;
    }

    @Override
    public LocalTransaction getLocalTransaction() {
        return transaction;
    }

    public AQTransaction getAQTransaction() {
        return transaction;
    }

    @Override
    public ConnectionMetaData getMetaData() {
        return new AQConnectionMetaData(this);
    }

    /**
     * Result sets are not supported.
     */
    @Override
    public ResultSetInfo getResultSetInfo() {
        throw ValidationException.operationNotSupported("getResultSetInfo");
    }
}
