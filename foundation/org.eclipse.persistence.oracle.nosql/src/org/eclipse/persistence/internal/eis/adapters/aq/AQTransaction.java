/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

import javax.resource.*;
import javax.resource.cci.*;

/**
 * Transaction to Oracle AQ JCA adapter.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class AQTransaction implements LocalTransaction {
    protected boolean isInTransaction;
    protected AQConnection connection;

    /**
     * Default constructor.
     */
    public AQTransaction(AQConnection connection) {
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
     * Write each of the transactional DOM records back to their files.
     */
    @Override
    public void commit() throws ResourceException {
        try {
            this.connection.getDatabaseConnection().commit();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
        this.isInTransaction = false;
    }

    /**
     * Throw away each of the DOM records in the transactional cache.
     */
    @Override
    public void rollback() throws ResourceException {
        try {
            this.connection.getDatabaseConnection().rollback();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
        this.isInTransaction = false;
    }
}
