/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.transaction;

import javax.transaction.*;

import org.eclipse.persistence.exceptions.TransactionException;

/**
 * <p>
 * <b>Purpose</b>: TransactionController implementation for JTA 1.0
 * <p>
 * <b>Description</b>: Implements the required behavior for controlling JTA 1.0
 * transactions. Specific JTA implementations may need to extend this class
 * when special controller behavior is necessary.
 * <p>
 * The JTA TransactionManager must be obtained and set on the
 * instance in order for a Synchronization listener to be registered against
 * the transaction. This can be done either by extending this class and defining
 * acquireTransactionManager() to return the manager for the server, or by using
 * this class and explicitly calling the setTransactionManager() method on it
 * after the fact.
 * e.g.
 *           TransactionManager mgr = controller.jndiLookup("java:comp/TransactionManager");
 *           controller.setTransactionManager(mgr);
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
 * The default listener factory creates instances of JTATransactionListener.
 * <p>
 * @see JTASynchronizationListener
 * @see AbstractTransactionController
 */
public class JTATransactionController extends AbstractTransactionController {
    
    // Allows transaction manager to be set statically.
    protected static TransactionManager defaultTransactionManager;
    
    // Primary point of integration with JTA 
    protected TransactionManager transactionManager;

    /**
     * PUBLIC:
     * Return a new controller for use with a JTA 1.0 compliant TransactionManager.
     */
    public JTATransactionController() {
        super();
        this.listenerFactory = new JTASynchronizationListener();
        try {
            this.transactionManager = acquireTransactionManager();
        } catch (Exception ex) {
            throw TransactionException.errorObtainingTransactionManager(ex);
        }
    }
    
    /**
     * PUBLIC:
     * Return a new controller for use with a JTA 1.0 compliant TransactionManager.
     */
    public JTATransactionController(TransactionManager transactionManager) {
        super();
        this.listenerFactory = new JTASynchronizationListener();
        this.transactionManager = transactionManager;
    }

    /**
     * INTERNAL:
     * Register the specified synchronization listener with the given active
     * transaction.
     *
     * @param listener The synchronization listener created for this transaction
     * @param txn The active transaction for which notification is being requested
     */
    protected void registerSynchronization_impl(AbstractSynchronizationListener listener, Object txn) throws Exception {
        ((Transaction)txn).registerSynchronization((Synchronization)listener);
    }

    /**
     * INTERNAL:
     * Return the active external transaction, or null if none is currently
     * active for this thread.
     *
     * @return The active transaction object or id, or null if no transaction is active
     */
    protected Object getTransaction_impl() throws Exception {
        return getTransactionManager().getTransaction();
    }

    /**
     * INTERNAL:
     * Return a key for the specified external transaction object.
     * The key is just something that can be inserted into a hashtable (must support
     * hashCode() and equals() methods).
     *
     * @param transaction The transaction to which the returned key applies (may be null)
     * @return A key for the passed in transaction, or null if no transaction specified
     */
    protected Object getTransactionKey_impl(Object transaction) throws Exception {
        // Use the transaction itself as the key
        return transaction;
    }

    /**
     * INTERNAL:
     * Return the transaction status as an object. We will pass around Integers that
     * wrap the int JTA status values.
     *
     * @return The current transaction status
     */
    protected Object getTransactionStatus_impl() throws Exception {
        return Integer.valueOf(getTransactionManager().getStatus());
    }

    /**
     * INTERNAL:
     * Begin an external transaction.
     */
    protected void beginTransaction_impl() throws Exception {
        getTransactionManager().begin();
    }

    /**
     * INTERNAL:
     * Commit the external transaction.
     */
    protected void commitTransaction_impl() throws Exception {
        getTransactionManager().commit();
    }

    /**
     * INTERNAL:
     * Roll back the external transaction.
     */
    protected void rollbackTransaction_impl() throws Exception {
        getTransactionManager().rollback();
    }

    /**
     * INTERNAL:
     * Mark the external transaction for rollback.
     */
    protected void markTransactionForRollback_impl() throws Exception {
        getTransactionManager().setRollbackOnly();
    }

    /**
     * INTERNAL:
     * Return true if the status indicates that a transaction can be started. This
     * would normally mean that no transaction is currently active.
     *
     * @param status The current transaction status
     * @return true if the current state allows for a transaction to be started
     */
    protected boolean canBeginTransaction_impl(Object status) {
        return getIntStatus(status) == Status.STATUS_NO_TRANSACTION;
    }

    /**
     * INTERNAL:
     * Return true if the status indicates that a transaction can be committed. This
     * would normally mean that a transaction is currently active.
     *
     * @param status The current transaction status
     * @return true if the current state allows for a transaction to be committed
     */
    protected boolean canCommitTransaction_impl(Object status) {
        return getIntStatus(status) == Status.STATUS_ACTIVE;
    }

    /**
     * INTERNAL:
     * Return true if the status indicates that a transaction can be rolled back. This
     * would normally mean that a transaction is currently active.
     *
     * @param status The current transaction status
     * @return true if the current state allows for a transaction to be rolled back
     */
    protected boolean canRollbackTransaction_impl(Object status) {
        return getIntStatus(status) == Status.STATUS_ACTIVE;
    }

    /**
     * INTERNAL:
     * Return true if the status indicates that the SQL should be issued to the db.
     * This would normally mean that a transaction was active.
     *
     * @param status The current transaction status
     * @return true if the current state allows for the SQL to be sent to the database
     */
    protected boolean canIssueSQLToDatabase_impl(Object status) {
        int stat = getIntStatus(status);
        return ((stat == Status.STATUS_ACTIVE) || (stat == Status.STATUS_PREPARING));
    }

    /**
     * INTERNAL:
     * Return true if the status indicates that the unit of work should be merged
     * into the shared cache. This would normally mean that the transaction was
     * committed successfully.
     *
     * @param status The current transaction status
     * @return true if the current state dictates that the unit of work should be merged
     */
    protected boolean canMergeUnitOfWork_impl(Object status) {
        return getIntStatus(status) == Status.STATUS_COMMITTED;
    }

    /**
     * INTERNAL:
     * Return true if the transaction is rolled back.
     */
    public boolean isRolledBack_impl(Object status) {
        return getIntStatus(status) == Status.STATUS_ROLLEDBACK;
    }

    /**
     * INTERNAL:
     * Obtain and return the JTA TransactionManager on this platform.
     * By default try java:comp JNDI lookup.
     *
     * This method can be can be overridden by subclasses to obtain the
     * transaction manager by whatever means is appropriate to the server.
     * This method is invoked by the constructor to initialize the transaction
     * manager at instance-creation time. Alternatively the transaction manager
     * can be set directly on the controller instance using the
     * setTransactionManager() method after the instance has been created.
     *
     * @return The TransactionManager for the transaction system
     */
    protected TransactionManager acquireTransactionManager() throws Exception {
        if (defaultTransactionManager != null) {
            return defaultTransactionManager;
        }
        return null;
    }

    /**
     * INTERNAL:
     * Convenience method to return the int value of the transaction status.
     * Assumes that the status object is an Integer.
     */
    protected int getIntStatus(Object status) {
        return ((Integer)status).intValue();
    }

    /**
     * PUBLIC:
     * Return the transaction manager used to control the JTA transactions.
     *
     * @return The JTA TransactionManager that is used to obtain transaction
     * state information and control the active transaction.
     */
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    /**
     * PUBLIC:
     * Set the transaction manager used to control the JTA transactions.
     *
     * @param mgr A valid JTA TransactionManager that can be
     * accessed by this controller to obtain transaction state information and
     * control the active transaction.
     */
    public void setTransactionManager(TransactionManager mgr) {
        transactionManager = mgr;
    }

    /**
     * INTERNAL:
     * Convert the status to a string for tracing.
     */
    static String[] codes = { "STATUS_ACTIVE", "MARKED_ROLLBACK", "PREPARED", "COMMITTED", "ROLLEDBACK", "UNKNOWN", "NO_TRANSACTION", "PREPARING", "COMMITTING", "ROLLING_BACK" };

    protected String statusToString_impl(Object status) {
        int statusCode = getIntStatus(status);
        return codes[statusCode];
    }

    public static TransactionManager getDefaultTransactionManager() {
        return defaultTransactionManager;
    }

    /**
     * PUBLIC:
     * Set the JTA transaction manager to be used.
     * This can be called directly before login to configure JTA integration manually, or using Spring injection.
     */
    public static void setDefaultTransactionManager(TransactionManager defaultTransactionManager) {
        JTATransactionController.defaultTransactionManager = defaultTransactionManager;
    }
}
