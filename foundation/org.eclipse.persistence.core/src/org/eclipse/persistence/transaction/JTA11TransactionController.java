/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     10/24/2017-3.0 Tomas Kraus
//       - 526419: Modify EclipseLink to reflect changes in JTA 1.1.
package org.eclipse.persistence.transaction;

import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;

import org.eclipse.persistence.exceptions.TransactionException;
import org.eclipse.persistence.logging.SessionLog;

/**
  * <p>
  * <b>Purpose</b>: TransactionController extensions for JTA 1.1
  * <p>
  * <b>Description</b>: Implements the required behavior for controlling JTA 1.1
  * transactions. Specific JTA implementations may need to extend this class
  * when special controller behavior is necessary.
  * <p>
  * The JTA {@link TransactionSynchronizationRegistry} must be obtained and set on the
  * instance in order for a Synchronization listener to be registered against
  * the transaction. This can be done either by extending this class and defining
  * {@code acquireTransactionSynchronizationRegistry()} to return the manager for the server, or by using
  * this class and explicitly calling the {@code setTransactionSynchronizationRegistry()} method on it
  * after the fact.
  * e.g.
  *           TransactionSynchronizationRegistry tsr = controller.jndiLookup("java:comp/setTransactionSynchronizationRegistry");
  *           controller.setTransactionManager(tsr);
  * <p>
  * If a different listener needs to be used for synchronization, the
  * SynchronizationListenerFactory should be set on the controller instance.
  * The listener subclass should implement the factory interface, so that
  * setting the factory is simply a matter of assigning an instance of the
  * listener.
  * e.g.
  *          controller.setSynchronizationListenerFactory(
  *              new DifferentServerSynchronizationListener());
  *
  * The default listener factory creates instances of {@code JTATransactionListener}.
  *
  * @see JTASynchronizationListener
  * @see AbstractTransactionController
  * @since 2.7.1
  */
public class JTA11TransactionController extends JTATransactionController {

    // Static content

    /** Common JNDI name of {@code TransactionSynchronizationRegistry} instance. */
    public static final String JNDI_TRANSACTION_SYNCHRONIZATION_REGISTRY = "java:comp/TransactionSynchronizationRegistry";

    /** Allows {@code TransactionSynchronizationRegistry} to be set statically. */
    protected static TransactionSynchronizationRegistry defaultTsr;

    /**
     * PUBLIC:
     * Get the default JTA 1.1 synchronization objects registry being used.
     * @return synchronization objects registry being used or {@code null} if no default value was set yet.
     */
    public static TransactionSynchronizationRegistry getDefaultTransactionSynchronizationRegistry() {
        return defaultTsr;
    }

    /**
     * PUBLIC:
     * Set the default JTA 1.1 synchronization objects registry to be used.
     * This can be called directly before login to configure JTA integration manually, or using Spring injection.
     *
     * @param tsr synchronization objects registry to set
     */
    public static void setDefaultTransactionSynchronizationRegistry(final TransactionSynchronizationRegistry tsr) {
        defaultTsr = tsr;
    }

    // Instance content

    /** Primary point of integration with JTA 1.1. */
    protected TransactionSynchronizationRegistry tsr;

    /**
     * PUBLIC:
     * Return a new controller for use with acquired JTA 1.1 compliant {@link TransactionSynchronizationRegistry}.
     */
    public JTA11TransactionController() {
        super();
        try {
            this.tsr = acquireTransactionSynchronizationRegistry();
        } catch (Exception ex) {
            throw TransactionException.errorObtainingTsr(ex);
        }
    }

    /**
     * PUBLIC:
     * Return a new controller for use with supplied JTA 1.1 compliant synchronization objects registry.
     *
     * @param supplTm  supplied JTA 1.0 compliant transaction manager.
     * @param supplTsr supplied JTA 1.1 compliant synchronization objects registry
     */
    public JTA11TransactionController(final TransactionSynchronizationRegistry supplTsr, final TransactionManager supplTm) {
        super(supplTm);
        this.tsr = supplTsr;
    }

    /**
     * INTERNAL:
     * Obtain and return the JTA 1.1 {@link TransactionSynchronizationRegistry} on this platform.
     *
     * This method can be can be overridden by subclasses to obtain the
     * {@code TransactionSynchronizationRegistry} by whatever means is appropriate to the server.
     * This method is invoked by the constructor to initialize the synchronization objects registry
     * at instance-creation time. Alternatively the synchronization objects registry
     * can be set directly on the controller instance using the setTransactionManager() method
     * after the instance has been created.
     *
     * @return the {@code TransactionSynchronizationRegistry} for the transaction system or {@code null}
     *         if no default value was found
     */
    protected TransactionSynchronizationRegistry acquireTransactionSynchronizationRegistry() {
        if (defaultTsr != null) {
            return defaultTsr;
        }
        try {
            return (TransactionSynchronizationRegistry)jndiLookup(JNDI_TRANSACTION_SYNCHRONIZATION_REGISTRY);
        } catch (TransactionException ex) {
            session.log(SessionLog.WARNING, SessionLog.TRANSACTION, "jta_tsr_lookup_failure", ex.getLocalizedMessage());
        }
        return null;
    }

    /**
     * PUBLIC:
     * Return the synchronization objects registry used to control the JTA 1.1 transactions.
     *
     * @return the JTA 1.1 {@link TransactionSynchronizationRegistry} that is used to obtain transaction
     *         state information and control the active transaction
     */
    public TransactionSynchronizationRegistry getTransactionSynchronizationRegistry() {
        return tsr;
    }

    /**
     * PUBLIC:
     * Set the synchronization objects registry used to control the JTA 1.1 transactions.
     *
     * @param supplTsr a valid JTA 1.1 {@link TransactionSynchronizationRegistry} that can be accessed by this controller
     *        to obtain transaction state information and control the active transaction
     */
    public void setTransactionManager(final TransactionSynchronizationRegistry supplTsr) {
        tsr = supplTsr;
    }

    /**
     * INTERNAL:
     * Register the specified synchronization listener with the given active transaction.
     *
     * @param listener the synchronization listener created for this transaction
     * @param txn the active transaction for which notification is being requested
     */
    @Override
    protected void registerSynchronization_impl(final AbstractSynchronizationListener listener, final Object txn) throws Exception {
        tsr.registerInterposedSynchronization(listener);
    }

    /**
     * INTERNAL:
     * Return a key for the specified external transaction object.
     * The key is just something that can be inserted into a hash table (must support {@code hashCode()}
     * and {@code equals()} methods).
     *
     * @param transaction The transaction to which the returned key applies (may be null)
     * @return an opaque object to represent the transaction bound to the current thread at the time this method
     *         is called, or {@code null} if no transaction specified
     */
    @Override
    protected Object getTransactionKey_impl(final Object transaction) throws Exception {
        return tsr.getTransactionKey();
    }


    /**
     * INTERNAL:
     * Return the transaction status as an object. We will pass around {@code Integer}s that wrap the {@code int}
     * JTA status values.
     *
     * @return the status of the transaction bound to the current thread at the time this method is called
     */
    @Override
    protected Object getTransactionStatus_impl() throws Exception {
        return tsr.getTransactionStatus();
    }

    /**
     * INTERNAL:
     * Mark the external transaction for rollback.
     */
    @Override
    protected void markTransactionForRollback_impl() throws Exception {
        tsr.setRollbackOnly();
    }

    /**
     * INTERNAL:
     * Check whether the transaction is rolled back.
     * @param status {@code true} if the transaction is rolled back or {@code false} otherwise
     */
    @Override
    public boolean isRolledBack_impl(final Object status) {
        return tsr.getRollbackOnly();
    }

}
