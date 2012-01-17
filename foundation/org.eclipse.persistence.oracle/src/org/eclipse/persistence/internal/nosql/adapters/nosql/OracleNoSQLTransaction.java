/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.nosql.adapters.nosql;

import javax.resource.*;
import javax.resource.cci.*;

/**
 * Transaction to Oracle NoSQL JCA adapter.
 * Oracle NoSQL does not currently support transactions.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class OracleNoSQLTransaction implements LocalTransaction {
    protected boolean isInTransaction;
    protected OracleNoSQLConnection connection;

    /**
     * Default constructor.
     */
    public OracleNoSQLTransaction(OracleNoSQLConnection connection) {
        this.connection = connection;
        this.isInTransaction = false;
    }

    /**
     * Record that a transaction has begun.
     */
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
     * Rollback.  Not currently supported.
     */
    public void commit() throws ResourceException {
        try {
            //this.connection.getDatabaseConnection().commit();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
        this.isInTransaction = false;
    }

    /**
     * Rollback.  Not currently supported.
     */
    public void rollback() throws ResourceException {
        try {
            //this.connection.getDatabaseConnection().rollback();
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
        this.isInTransaction = false;
    }
}
