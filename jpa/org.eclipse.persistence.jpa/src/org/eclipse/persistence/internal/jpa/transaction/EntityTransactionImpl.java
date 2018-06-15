/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     07/13/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     08/24/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     09/13/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
package org.eclipse.persistence.internal.jpa.transaction;

import java.util.Map;
import java.util.WeakHashMap;

import javax.persistence.RollbackException;

import org.eclipse.persistence.exceptions.TransactionException;
import org.eclipse.persistence.internal.jpa.QueryImpl;
import org.eclipse.persistence.internal.jpa.transaction.EntityTransactionWrapper;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;

/**
 * JDK 1.5 version of the EntityTransaction.  Differs from base version only in that
 * it takes a JDK 1.5 version of the EntityTransactionWrapper.
 *
 * @see org.eclipse.persistence.internal.jpa.transaction.EntityTransactionImpl
 */
public class EntityTransactionImpl implements javax.persistence.EntityTransaction {
    /**
     * Keep a weak reference to the open queries that are executed in this entity manager.
     */
    protected WeakHashMap<QueryImpl, QueryImpl> openQueriesMap;

    protected EntityTransactionWrapper wrapper;

    protected boolean active = false;

    protected boolean rollbackOnly = false;

    protected TransactionFinalizer finalizer;

    /** PERF: Avoid finalization if not required by the application, and finalizers have major concurrency affects. */
    public static boolean isFinalizedRequired = false;

    public EntityTransactionImpl(EntityTransactionWrapper wrapper) {
        this.wrapper = wrapper;
        if (isFinalizedRequired) {
            this.finalizer = new TransactionFinalizer();
        }
    }

    /**
     * Within a transaction track any open queries that will need to be closed
     * on commit or rollback.
     */
    /**
     * Queries that leave the connection and are executed against this entity
     * manager will be added here. On rollback or commit any left over open
     * queries should be closed.
     *
     * @param query
     */
    public void addOpenQuery(QueryImpl query) {
        if (openQueriesMap == null) {
            openQueriesMap = new WeakHashMap<QueryImpl, QueryImpl>();
        }

        openQueriesMap.put(query, query);
    }

    /**
     * Start the current transaction. This can only be invoked if
     * {@link #isActive()} returns <code>false</code>.
     *
     * @throws IllegalStateException
     *             if isActive() is true.
     */
    @Override
    public void begin() {
        if (isActive()) {
            throw new IllegalStateException(TransactionException.transactionIsActive().getMessage());
        }
        //bug307445 : Throw IllegalStateException if entityManager was closed
        this.wrapper.getEntityManager().verifyOpen();

        // always extended
        this.wrapper.localUOW = this.wrapper.getEntityManager().getActivePersistenceContext(null);
        this.wrapper.localUOW.setShouldTerminateTransaction(false);

        this.active = true;
    }

    /**
     * Open queries within a transaction will be closed on commit or rollback
     * if they haven't already been closed.
     */
    protected void closeOpenQueries() {
        if (openQueriesMap != null) {
            for (QueryImpl openQuery : openQueriesMap.keySet()) {
                openQuery.close();
            }
        }
    }

    /**
     * Commit the current transaction, writing any un-flushed changes to the
     * database. This can only be invoked if {@link #isActive()} returns
     * <code>true</code>.
     *
     * @throws IllegalStateException
     *             if isActive() is false.
     */
    @Override
    public void commit() {
        if (!isActive()) {
            throw new IllegalStateException(TransactionException.transactionNotActive().getMessage());
        }

        // Make sure any open queries are closed.
        closeOpenQueries();

        try {
            if (this.wrapper.localUOW != null) {
                this.wrapper.localUOW.setShouldTerminateTransaction(true);
                if (!this.rollbackOnly) {
                    if (this.wrapper.localUOW.shouldResumeUnitOfWorkOnTransactionCompletion()) {
                        this.wrapper.localUOW.commitAndResume();
                        return;
                    } else {
                        this.wrapper.localUOW.commit();
                        // all change sets and are cleared, but the cache is
                        // kept
                        this.wrapper.localUOW.clearForClose(false);
                    }
                } else {
                    throw new RollbackException(ExceptionLocalization.buildMessage("rollback_because_of_rollback_only"));
                }
            }
        } catch (RuntimeException exception) {
            try {
                if (this.wrapper.localUOW != null) {
                    this.wrapper.getEntityManager().removeExtendedPersistenceContext();
                    this.wrapper.localUOW.release();
                    this.wrapper.localUOW.getParent().release();
                }
            } catch (Exception ignore) {} // Throw first exception.
            if (exception instanceof RollbackException) {
                throw exception;
            } else if (exception instanceof org.eclipse.persistence.exceptions.OptimisticLockException) {
                throw new RollbackException(new javax.persistence.OptimisticLockException(exception));
            } else {
                throw new RollbackException(exception);
            }
        } finally {
            this.active = false;
            this.rollbackOnly = false;
            this.wrapper.setLocalUnitOfWork(null);
        }
    }

    /**
     * Roll back the current transaction, discarding any changes that have
     * happened in this transaction. This can only be invoked if
     * {@link #isActive()} returns <code>true</code>.
     *
     * @throws IllegalStateException
     *             if isActive() is false.
     */
    @Override
    public void rollback() {
        if (!isActive()) {
            throw new IllegalStateException(TransactionException.transactionNotActive().getMessage());
        }

        // Make sure any open queries are closed.
        closeOpenQueries();

        try {
            if (wrapper.getLocalUnitOfWork() != null) {
                this.wrapper.localUOW.setShouldTerminateTransaction(true);
                this.wrapper.localUOW.release();
                this.wrapper.localUOW.getParent().release();
            }
        } finally {
            this.active = false;
            this.rollbackOnly = false;
            this.wrapper.getEntityManager().removeExtendedPersistenceContext();
            this.wrapper.setLocalUnitOfWork(null);
        }
    }

    /**
     * Mark the current transaction so that the only possible outcome of the
     * transaction is for the transaction to be rolled back.
     *
     * @throws IllegalStateException
     *             if isActive() is false.
     */
    @Override
    public void setRollbackOnly() {
        if (!isActive()) {
            throw new IllegalStateException(TransactionException.transactionNotActive().getMessage());
        }

        this.rollbackOnly = true;
    }

    /**
     * Return the weak reference to the open queries.
     */
    protected Map<QueryImpl, QueryImpl> getOpenQueriesMap() {
        if (openQueriesMap == null) {
            openQueriesMap = new WeakHashMap<QueryImpl, QueryImpl>();
        }

        return openQueriesMap;
    }

    /**
     * Determine whether the current transaction has been marked for rollback.
     *
     * @throws IllegalStateException
     *             if isActive() is false.
     */
    @Override
    public boolean getRollbackOnly() {
        if (!isActive()) {
            throw new IllegalStateException(TransactionException.transactionNotActive().getMessage());
        }
        return this.rollbackOnly;
    }

    /**
     * Check to see if the current transaction is in progress.
     */
    @Override
    public boolean isActive() {
        return this.active;
    }

    class TransactionFinalizer {

        /**
         * Here incase a user does not commit or rollback an enityTransaction
         * but just throws it away. If we do not rollback the txn the connection
         * will go back into the pool.
         */
        @Override
        protected void finalize() throws Throwable {
            if (isActive()) {
                rollback();
            }
        }

    }
}
