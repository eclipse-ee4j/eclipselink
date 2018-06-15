/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.nosql.adapters.mongo;

import javax.resource.*;
import javax.resource.cci.*;

/**
 * Transaction to Mongo adapter.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class MongoTransaction implements LocalTransaction {
    protected boolean isInTransaction;
    protected Connection connection;

    /**
     * Default constructor.
     */
    public MongoTransaction(Connection connection) {
        this.connection = connection;
        this.isInTransaction = false;
    }

    /**
     * Record that a transaction has begun.
     */
    @Override
    public void begin() {
        this.isInTransaction = true;
    }

    /**
     * Return if currently within a transaction.
     */
    public boolean isInTransaction() {
        return isInTransaction;
    }

    /**
     * Commit the current transaction.
     */
    @Override
    public void commit() throws ResourceException {
        try {
            //this.connection.getDatabaseConnection().commit();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
        this.isInTransaction = false;
    }

    /**
     * Rollback the current transaction.
     */
    @Override
    public void rollback() throws ResourceException {
        try {
            //this.connection.getDatabaseConnection().rollback();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
        this.isInTransaction = false;
    }
}
