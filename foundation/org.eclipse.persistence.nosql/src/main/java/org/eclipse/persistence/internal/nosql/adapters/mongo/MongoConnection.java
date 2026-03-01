/*
 * Copyright (c) 2011, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Gunnar Wagenknecht - isExternal support
package org.eclipse.persistence.internal.nosql.adapters.mongo;

import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;
import jakarta.resource.cci.ConnectionMetaData;
import jakarta.resource.cci.Interaction;
import jakarta.resource.cci.LocalTransaction;
import jakarta.resource.cci.ResultSetInfo;

import org.eclipse.persistence.exceptions.ValidationException;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Connection to Mongo
 * This connection wraps a Mongo DB.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class MongoConnection implements Connection {
    protected MongoJCAConnectionSpec spec;
    protected MongoTransaction transaction;
    protected MongoClient mongo;
    protected String databaseName;
    protected boolean isExternal;

    /**
     * Create the connection on a native AQ session.
     * The session must be connected to a JDBC connection.
     */
    public MongoConnection(MongoClient mongo, String databaseName, boolean isExternal, MongoJCAConnectionSpec spec) {
        this.mongo = mongo;
        this.databaseName = databaseName;
        this.transaction = new MongoTransaction(this);
        this.spec = spec;
        this.isExternal = isExternal;
    }

    public MongoDatabase getDB() {
        return mongo.getDatabase(databaseName);
    }

    public MongoClient getClient() {
        return this.mongo;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Close MongoDB client with all underlying cached resources.
     */
    @Override
    public void close() throws ResourceException {
        try {
            this.mongo.close();
        } catch (Exception exception) {
            ResourceException resourceException = new ResourceException(exception.toString());
            resourceException.initCause(exception);
            throw resourceException;
        }
    }

    @Override
    public Interaction createInteraction() {
        return new MongoInteraction(this);
    }

    public MongoJCAConnectionSpec getConnectionSpec() {
        return spec;
    }

    @Override
    public LocalTransaction getLocalTransaction() {
        return transaction;
    }

    public MongoTransaction getMongoTransaction() {
        return transaction;
    }

    @Override
    public ConnectionMetaData getMetaData() {
        return new MongoConnectionMetaData(this);
    }

    /**
     * Result sets are not supported.
     */
    @Override
    public ResultSetInfo getResultSetInfo() {
        throw ValidationException.operationNotSupported("getResultSetInfo");
    }

    public boolean isExternal() {
        return isExternal;
    }

    public void setExternal(boolean isExternal) {
        this.isExternal = isExternal;
    }
}
