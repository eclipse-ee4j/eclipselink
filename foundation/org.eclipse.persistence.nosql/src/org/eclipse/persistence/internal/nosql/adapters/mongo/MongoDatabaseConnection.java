/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//     Gunnar Wagenknecht - isExternal support
package org.eclipse.persistence.internal.nosql.adapters.mongo;

import javax.resource.*;
import javax.resource.cci.*;

import org.eclipse.persistence.exceptions.ValidationException;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Connection to Mongo
 * This connection wraps a Mongo DB.
 *
 * @author James
 * @since EclipseLink 2.7
 */
public class MongoDatabaseConnection implements Connection {
    protected MongoJCAConnectionSpec spec;
    protected MongoTransaction transaction;
    protected MongoDatabase db;
    protected boolean isExternal;
    private MongoClient mongo;

    /**
     * Create the connection on a native AQ session.
     * The session must be connected to a JDBC connection.
     */
    public MongoDatabaseConnection(MongoClient mongo, MongoDatabase db, boolean isExternal, MongoJCAConnectionSpec spec) {
        this.mongo = mongo;
        this.db = db;
        this.transaction = new MongoTransaction(this);
        this.spec = spec;
        this.isExternal = isExternal;
    }

    public MongoClient getMongo() {
        return mongo;
    }

    public MongoDatabase getDB() {
        return db;
    }

    /**
     * Close the AQ native session and the database connection.
     */
    @Override
    public void close() throws ResourceException {
        try {
            this.getMongo().close();
        } catch (Exception exception) {
            ResourceException resourceException = new ResourceException(exception.toString());
            resourceException.initCause(exception);
            throw resourceException;
        }
    }

    @Override
    public Interaction createInteraction() {
        return new MongoDatabaseInteraction(this);
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
        return new MongoDatabaseConnectionMetaData(this);
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
