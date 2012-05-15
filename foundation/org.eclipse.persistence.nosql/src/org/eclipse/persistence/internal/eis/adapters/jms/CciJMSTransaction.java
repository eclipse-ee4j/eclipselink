/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.eis.adapters.jms;

import javax.jms.JMSException;
import javax.resource.cci.*;
import org.eclipse.persistence.eis.EISException;

/**
 * INTERNAL:
 * Transaction to Oracle JMS JCA adapter.
 *
 * @author Dave McCann
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class CciJMSTransaction implements LocalTransaction {
    protected boolean isInTransaction;// indicates if currently in a transaction
    protected CciJMSConnection connection;// the cci connection

    /**
     * Default constructor.
     */
    public CciJMSTransaction(CciJMSConnection conn) {
        connection = conn;
        isInTransaction = false;
    }

    /**
     * Record that a transaction has begun.
     *
     * @throws EISException
     */
    public void begin() throws EISException {
        try {
            if (!connection.getSession().getTransacted()) {
                throw EISException.invalidMethodInvocation();
            }
        } catch (JMSException ex) {
            throw EISException.transactedSessionTestError();
        }

        isInTransaction = true;
    }

    /**
     * Return if currently within a transaction.
     *
     * @return true if currently in a transaction, false otherwise
     */
    public boolean isInTransaction() {
        return isInTransaction;
    }

    /**
     * Write each of the transactional DOM records back to their files.
     *
     * @throws EISException
     */
    public void commit() throws EISException {
        try {
            connection.getSession().commit();
        } catch (Exception exception) {
            throw EISException.createException(exception);
        }
        isInTransaction = false;
    }

    /**
     * Throw away each of the DOM records in the transactional cache.
     *
     * @throws EISException
     */
    public void rollback() throws EISException {
        try {
            connection.getSession().rollback();
        } catch (Exception exception) {
            throw EISException.createException(exception);
        }
        isInTransaction = false;
    }
}
