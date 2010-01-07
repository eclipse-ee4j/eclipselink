/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import javax.transaction.*;
import org.eclipse.persistence.internal.jpa.ExceptionFactory;
import org.eclipse.persistence.internal.jpa.jdbc.DataSourceImpl;

/**
 * Implementation of JTA Transaction manager class.
 *
 * Currently support is limited to enlisting a single tx data source
 */
public class TransactionManagerImpl implements TransactionManager, UserTransaction {
    // Not null when a transaction is active
    TransactionImpl tx;

    /************************/
    /***** Internal API *****/
    /************************/
    private void debug(String s) {
        System.out.println(s);
    }

    /*
     * Used to create the single instance
     */
    public TransactionManagerImpl() {
        this.tx = null;
    }

    /*
     * Return true if a transaction has been explicitly begun
     */
    public boolean isTransactionActive() {
        return tx != null;
    }

    /*
     * Return a Connection if a transaction is active, otherwise return null
     */
    public Connection getConnection(DataSourceImpl ds, String user, String password) throws SQLException {
        return (tx == null) ? null : tx.getConnection(ds, user, password);
    }

    /************************************************************/
    /***** Supported TransactionManager/UserTransaction API *****/
    /************************************************************/
    public void begin() throws NotSupportedException, SystemException {
        debug("Tx - begin");

        if (isTransactionActive()) {
            throw new ExceptionFactory().txActiveException();
        }

        // New transaction created by virtue of Transaction existence
        tx = new TransactionImpl();
    }

    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        debug("Tx - commit");

        if (!isTransactionActive()) {
            throw new ExceptionFactory().txNotActiveException();
        }
        try{
            tx.commit();
        }finally{
            tx = null;
        }
    }

    public int getStatus() throws SystemException {
        return (!isTransactionActive()) ? Status.STATUS_NO_TRANSACTION : tx.getStatus();
    }

    public Transaction getTransaction() throws SystemException {
        return tx;

    }

    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        debug("Tx - rollback");

        if (!isTransactionActive()) {
            throw new ExceptionFactory().txNotActiveException();
        }
        try{
            tx.rollback();
        }finally{
            tx = null;
        }
    }

    public void setRollbackOnly() throws IllegalStateException, SystemException {
        debug("Tx - rollback");

        if (!isTransactionActive()) {
            throw new ExceptionFactory().txNotActiveException();
        }
        tx.setRollbackOnly();
    }

    /****************************************************************/
    /***** NOT supported TransactionManager/UserTransaction API *****/
    /****************************************************************/
    public Transaction suspend() throws SystemException {
        return null;
    }

    public void resume(Transaction transaction) throws InvalidTransactionException, IllegalStateException, SystemException {
        // Do nothing
    }

    public void setTransactionTimeout(int i) throws SystemException {
        // Do nothing
    }
}
