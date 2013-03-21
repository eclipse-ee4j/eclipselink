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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.naming.*;

import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.ExternalTransactionController;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.exceptions.ExceptionHandler;
import org.eclipse.persistence.exceptions.TransactionException;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.sequencing.SequencingCallback;
import org.eclipse.persistence.internal.sequencing.SequencingCallbackFactory;

/**
 * <p>
 * <b>Purpose</b>: Abstract implementation of an ExternalTransactionController.
 * <p>
 * <b>Description</b>: This class implements the ExternalTransactionController
 * interface. Concrete implementations of this class are responsible for performing
 * the coordination with the external transaction manager through whatever means
 * available to that manager. Different transaction services may do this in slightly
 * different ways, but most common ones (JTA and JTS/OTS) will use a synchronization
 * interface.
 * <p>
 * @see AbstractSynchronizationListener
 * @see org.eclipse.persistence.sessions.ExternalTransactionController
 */
public abstract class AbstractTransactionController implements ExternalTransactionController {

    /** Table of external transaction object keys and unit of work values */
    protected ConcurrentMap unitsOfWork;

    /** The session this controller is responsible for controlling */
    protected AbstractSession session;

    /** Generates listener instances for synchronization */
    protected SynchronizationListenerFactory listenerFactory;

    /** PERF: Cache the active uow in a thread local. */
    protected ThreadLocal activeUnitOfWorkThreadLocal;

    /** Table of external transaction object keys and sequencing listeners values. */
    /** Non-null only in case sequencing callbacks are used: numSessionsRequiringSequencingCallback > 0 */
    protected ConcurrentMap<Object, AbstractSynchronizationListener> sequencingListeners;

    /** Table of external transaction object keys and listeners that are currently in beforeCompletion. */
    /** Request for a new sequencing callback may be triggered by beforeCompletion of existing listener - */
    /** in this case avoid creating yet another listener for sequencing but rather use the listener */
    /** that has initiated the request */
    /** Non-null only in case sequencing callbacks are used: numSessionsRequiringSequencingCallback > 0 */
    protected ConcurrentMap<Object, AbstractSynchronizationListener> currentlyProcessedListeners;

    /** Indicates how many sessions require sequencing callbacks: */
    /** 0 - sequencing callbacks not used; */
    /** 1 - the session is DatabaseSession or ServerSession and requires sequencing callbacks, */
    /** or the session is a session broker with only one member requiring sequencing callbacks. */
    /** more - the session is a session broker with several members requiring sequencing callbacks. */
    protected int numSessionsRequiringSequencingCallback;
    
    /** Allow exception in before/after completion to be wrapped. */
    protected ExceptionHandler exceptionHandler;

    /**
     * INTERNAL:
     * Return a new controller.
     */
    public AbstractTransactionController() {
        this.unitsOfWork = new ConcurrentHashMap();
        this.activeUnitOfWorkThreadLocal = new ThreadLocal();
    }

    /**
     * Return the exception handler used to handle or wrap exceptions thrown in before/after completion.
     */
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * Set an exception handler to handle or wrap exceptions thrown in before/after completion.
     */
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * INTERNAL:
     * Associate the given unit of work and EclipseLink session with the active external
     * transaction. This may be done by whatever means supported by the transaction
     * manager (i.e. through a synchronization protocol).
     *
     * @param unitOfWork The unit of work that is to be bound to the active transaction
     * @param session The session ancestor of the unit of work
     */
    public void bindToCurrentTransaction(UnitOfWorkImpl unitOfWork, AbstractSession session) {
        Object status = getTransactionStatus();
        logTxStateTrace(unitOfWork, "TX_bind", status);
        try {
            Object txn = getTransaction();
            if (txn == null) {
                // If no external transaction is active then start one through the uow
                unitOfWork.beginTransaction();
                txn = getTransaction();
            }

            // If there still is no active txn then something is wrong
            if (txn == null) {
                throw TransactionException.externalTransactionNotActive();
            }

            // Create and register the synchronization listener
            AbstractSynchronizationListener listener = getListenerFactory().newSynchronizationListener(unitOfWork, session, txn, this);

            registerSynchronization_impl(listener, txn);
            unitOfWork.setSynchronized(true);

        } catch (Exception exception) {
            throw TransactionException.errorBindingToExternalTransaction(exception);
        }
    }

    /**
     * INTERNAL:
     * Begin an external transaction.
     *
     * @param session The session for which the transaction is being begun.
     */
    public void beginTransaction(AbstractSession session) {
        try {
            Object status = getTransactionStatus();
            logTxStateTrace(session, "TX_begin", status);

            // Make sure that we are in a state that we can actually start 
            // a transaction (e.g. ensure one is not already in progress)
            if (canBeginTransaction_impl(status)) {
                logTxTrace(session, "TX_beginningTxn", null);
                beginTransaction_impl();
                session.setWasJTSTransactionInternallyStarted(true);
            }
        } catch (Exception exception) {
            throw TransactionException.errorBeginningExternalTransaction(exception);
        }
    }

    /**
     * INTERNAL:
     * Commit the external transaction.
     *
     * @param session The session for which the transaction is being committed.
     */
    public void commitTransaction(AbstractSession session) {
        try {
            Object status = getTransactionStatus();
            logTxStateTrace(session, "TX_commit", status);

            if (canCommitTransaction_impl(status)) {
                logTxTrace(session, "TX_committingTxn", null);

                session.setWasJTSTransactionInternallyStarted(false);
                commitTransaction_impl();
            }
        } catch (Exception exception) {
            throw TransactionException.errorCommittingExternalTransaction(exception);
        }
    }

    /**
     * INTERNAL:
     * Roll back the external transaction.
     *
     * @param session The session for which the transaction is being rolled back.
     */
    public void rollbackTransaction(AbstractSession session) {
        try {
            Object status = getTransactionStatus();
            logTxStateTrace(session, "TX_rollback", status);

            session.setWasJTSTransactionInternallyStarted(false);

            // Only roll back if there is a transaction to roll back
            if ((canRollbackTransaction_impl(status)) && (getTransaction() != null)) {
                logTxTrace(session, "TX_rollingBackTxn", null);
                rollbackTransaction_impl();
            }
        } catch (Exception exception) {
            throw TransactionException.errorRollingBackExternalTransaction(exception);
        }
    }

    /**
     * INTERNAL:
     * Mark the external transaction for rollback.
     */
    public void markTransactionForRollback() {
        try {
            markTransactionForRollback_impl();
        } catch (Exception exception) {
            throw TransactionException.errorMarkingTransactionForRollback(exception);
        }
    }

    /**
     * INTERNAL:
     * Return the active external transaction object, or null if
     * none is active. This may be in whatever form the transaction system uses.
     */
    public Object getTransaction() {
        try {
            return getTransaction_impl();
        } catch (Exception exception) {
            throw TransactionException.errorGettingExternalTransaction(exception);
        }
    }

    /**
     * INTERNAL:
     * Return a key for the specified external transaction object.
     * The key is just something that can be inserted into a hashtable (must support
     * hashCode() and equals() methods).
     */
    public Object getTransactionKey(Object transaction) {
        try {
            return getTransactionKey_impl(transaction);
        } catch (Exception exception) {
            throw TransactionException.errorGettingExternalTransaction(exception);
        }
    }

    /**
     * INTERNAL:
     * Return the transaction status. This may be any type of status or value,
     * depending upon the transaction system.
     */
    public Object getTransactionStatus() {
        try {
            return getTransactionStatus_impl();
        } catch (Exception exception) {
            throw TransactionException.errorGettingExternalTransactionStatus(exception);
        }
    }

    /**
    * INTERNAL:
    * Used the EJB 3.0 to determine if a transaction is in a state where an EntityManager can
    * be closed
    */
    public boolean noTransactionOrRolledBackOrCommited() {
        try {
            Object status = getTransactionStatus();
            return canBeginTransaction_impl(status) || canMergeUnitOfWork_impl(status) || isRolledBack_impl(status);
        } catch (Exception exception) {
            throw TransactionException.errorGettingExternalTransactionStatus(exception);
        }
    }

    /**
     * INTERNAL:
     * Return true if the transaction is in the rolled back state.
     */
    public abstract boolean isRolledBack_impl(Object status);

    /**
     * INTERNAL:
     * Return true if there is a unit of work associated with the active external
     * transaction. Return false if no transaction is current, or if no uow has
     * been associated with the active transaction yet.
     */
    public boolean hasActiveUnitOfWork() {
        return this.lookupActiveUnitOfWork() != null;
    }

    /**
     * INTERNAL:
     * Return the active unit of work for the current external transaction.
     * If no transaction is active then return null. If a transaction is active
     * but no unit of work has been bound to it then create and return one.
     */
    public UnitOfWorkImpl getActiveUnitOfWork() {
        Object transaction = getTransaction();
        if (transaction == null) {
            return null;
        }
        
        UnitOfWorkImpl activeUnitOfWork = lookupActiveUnitOfWork(transaction);
        if (activeUnitOfWork == null) {
            // Txn is active but no UoW has been associated with it yet, so create one.
            activeUnitOfWork = getSession().acquireUnitOfWork();
            Object transactionKey = getTransactionKey(transaction);
            addUnitOfWork(transactionKey, activeUnitOfWork);
            activeUnitOfWork.setTransaction(transaction);
            this.activeUnitOfWorkThreadLocal.set(activeUnitOfWork);
        }
        return activeUnitOfWork;
    }

    /**
     * INTERNAL:
     * Return the unit of work associated with the active external transaction.
     * Return null if no transaction is active, or if no uow has been associated with
     * the active transaction yet.
     */
    public UnitOfWorkImpl lookupActiveUnitOfWork() {
        return lookupActiveUnitOfWork(getTransaction());
    }

    /**
     * INTERNAL:
     * Return the unit of work associated with the active external transaction.
     * Return null if no transaction is active, or if no uow has been associated with
     * the active transaction yet.
     */
    public UnitOfWorkImpl lookupActiveUnitOfWork(Object transaction) {
        if (transaction == null) {
            return null;
        }
        Object transactionKey = getTransactionKey(transaction);

        // PERF: Cache the active unit of work in a thread local.
        // This is just a heuristic, so uses == and no tx-key and direct access as extremely high throughput.
        UnitOfWorkImpl activeUnitOfWork = (UnitOfWorkImpl)this.activeUnitOfWorkThreadLocal.get();
        if (activeUnitOfWork != null) {
            if (transaction == activeUnitOfWork.getTransaction()) {
                return activeUnitOfWork;
            }
        }
        activeUnitOfWork = (UnitOfWorkImpl)getUnitsOfWork().get(transactionKey);
        if (activeUnitOfWork != null) {
            activeUnitOfWork.setTransaction(transaction);
        }
        this.activeUnitOfWorkThreadLocal.set(activeUnitOfWork);
        return activeUnitOfWork;
    }

    /**
     * INTERNAL:
     * Add a UnitOfWork object to the Hashtable keyed on the external transaction object.
     */
    public void addUnitOfWork(Object transactionKey, UnitOfWorkImpl activeUnitOfWork) {
        this.activeUnitOfWorkThreadLocal.set(null);
        getUnitsOfWork().put(transactionKey, activeUnitOfWork);
    }

    /**
     * INTERNAL:
     * Remove the unit of work associated with the transaction passed in.
     */
    public void removeUnitOfWork(Object transactionKey) {
        if (transactionKey != null) {
            getUnitsOfWork().remove(transactionKey);
        }
        this.activeUnitOfWorkThreadLocal.set(null);
    }

    /**
     * INTERNAL:
     * Return the manager's session.
     */
    public AbstractSession getSession() {
        return session;
    }

    /**
     * INTERNAL:
     * Set the manager's session.
     */
    public void setSession(AbstractSession session) {
        this.session = session;
        initializeSequencingListeners();
    }

    /**
     * INTERNAL:
     * Return the hashtable keyed on the external transaction objects with values
     * that are the associated units of work.
     */
    public Map getUnitsOfWork() {
        return unitsOfWork;
    }

    /**
     * INTERNAL:
     * Set the table of transactions to units of work.
     */
    protected void setUnitsOfWork(ConcurrentMap unitsOfWork) {
        this.unitsOfWork = unitsOfWork;
    }

    /**
     * INTERNAL:
     * Get the factory used to generate synchronization listeners.
     */
    public SynchronizationListenerFactory getListenerFactory() {
        return listenerFactory;
    }

    /**
     * INTERNAL:
     * Set the factory used to generate synchronization listeners. This should be
     * set if a listener other than the default one is being used.
     */
    public void setListenerFactory(SynchronizationListenerFactory factory) {
        listenerFactory = factory;
    }

    /**
     * INTERNAL:
     * Associate the given unit of work and EclipseLink session with the current external
     * transaction. This method is offered only for backwards compatibility.
     */
    public void registerSynchronizationListener(UnitOfWorkImpl uow, AbstractSession session) throws DatabaseException {
        this.bindToCurrentTransaction(uow, session);
    }

    /**
     * PUBLIC:
     * Look up a given name in JNDI. This can be used by a subclass or even an
     * application to look up transaction artifacts that are required for the
     * implementation.
     * <p>
     * The lookup assumes that it is taking place on the server side, and that the
     * InitialContext can be used without requiring any special properties.
     *
     * @param jndiName The name to look up
     * @return The object bound in JNDI to the specified name
     * @exception TransactionException Thrown in case of lookup failure
     */
    public Object jndiLookup(String jndiName) {
        Context context = null;
        Object jndiObject = null;
        try {
            context = new InitialContext();
            jndiObject = context.lookup(jndiName);
        } catch (NamingException ex) {
            throw TransactionException.jndiLookupException(jndiName, ex);
        } finally {
            try {
                context.close();
            } catch (Exception ex2) {/* ignore */
            }
        }
        return jndiObject;
    }

    /**
     * INTERNAL:
     * Initializes sequencing listeners.
     * There are two methods calling this method:
     * 1. setSession method - this could lead to initialization of sequencing listeners
     * only if sequencing already connected (that would happen if setSession is called
     * after session.login, which is normally not the case).
     * 2. in the very end of connecting sequencing or adding descriptors to sequencing,
     * after it's determined whether sequencing callbacks (and therefore listeners)
     * will be required.
     * 
     * In SessionBroker case each member's sequencing may call this method.
     * Note that the number of sessions requiring callbacks may never decrease,
     * therefore if isSequencingCallbackRequired method has returned true once,
     * it will always return true after that (unless clearSequencingListeners method is called).
     */
    public void initializeSequencingListeners() {
        if(session == null) {
            return;
        }
        AbstractSession parentSession = session;
        while(parentSession.getParent() != null) {
            parentSession = parentSession.getParent();
        }
        int newNumSessionsRequiringSequencingCallback = 0;
        // top parentSession must be DatabaseSessionImpl
        if(parentSession.isBroker()) {
            // it could be either SessionBroker
            newNumSessionsRequiringSequencingCallback = ((SessionBroker)parentSession).howManySequencingCallbacks();
        } else {
            // or DatabaseSessionImpl or ServerSession
            if(((DatabaseSessionImpl)parentSession).isSequencingCallbackRequired()) {
                newNumSessionsRequiringSequencingCallback = 1;
            }
        }
        // number of required sessions in not allowed to decrease.
        if (newNumSessionsRequiringSequencingCallback > numSessionsRequiringSequencingCallback) {
            // keep the old map if already exists, never remove existing map
            if (this.sequencingListeners == null) {
                this.sequencingListeners = new ConcurrentHashMap();
            }
            // keep the old map if already exists, never remove existing map
            if (this.currentlyProcessedListeners == null) {
                this.currentlyProcessedListeners = new ConcurrentHashMap();
            }
            this.numSessionsRequiringSequencingCallback = newNumSessionsRequiringSequencingCallback;
        }
    }

    /**
     * INTERNAL:
     * Returns sequencingCallback for the current active external transaction.
     * DatabaseSession is passed for the sake of SessionBroker case.
     * This method requires active external transaction.
     */
    public SequencingCallback getActiveSequencingCallback(DatabaseSession dbSession, SequencingCallbackFactory sequencingCallbackFactory) {
        Object transaction = getTransaction();
        // This method requires active external transaction.
        if (transaction == null) {
            throw TransactionException.externalTransactionNotActive();
        }
        Object transactionKey = getTransactionKey(transaction);

        AbstractSynchronizationListener listener = sequencingListeners.get(transactionKey);
        if(listener == null) {
            // In case this request was triggered from beforeCompletion method of existing listener -
            // find this listener.
            listener = currentlyProcessedListeners.get(transactionKey);
            if(listener == null) {
                // Create and register the new synchronization listener with uow==session==null.
                listener = getListenerFactory().newSynchronizationListener(null, null, transaction, this);
                try {
                    registerSynchronization_impl(listener, transaction);
                } catch (Exception exception) {
                    throw TransactionException.errorBindingToExternalTransaction(exception);
                }
            }
            sequencingListeners.put(transactionKey, listener);
        }
        return listener.getSequencingCallback(dbSession, sequencingCallbackFactory);
    }
    
    /**
     * INTERNAL:
     * Clears sequencing listeners.
     * Called by initializeSequencingListeners and by sequencing on disconnect.
     */
    public void clearSequencingListeners() {
        this.numSessionsRequiringSequencingCallback = 0;
        this.sequencingListeners = null;
        this.currentlyProcessedListeners = null;
    }

    /**
     * INTERNAL:
     * Indicates whether sequencing callback may be required.
     */
    public boolean isSequencingCallbackRequired() {
        return this.numSessionsRequiringSequencingCallback > 0;
    }

    /**
     * INTERNAL:
     * Indicates how many sessions require sequencing callbacks.
     */
    public int numSessionsRequiringSequencingCallback() {
        return this.numSessionsRequiringSequencingCallback;
    }

    /**
     * INTERNAL:
     * Clears sequencingCallbacks.
     * Called by initializeSequencingCallbacks and by sequencing on disconnect.
     */
    public void removeSequencingListener(Object transactionKey) {
        if (transactionKey != null) {
            sequencingListeners.remove(transactionKey);
        }
    }

    /*
     * INTERNAL:
     * Helper method to log trace statements to the transaction channel.
     */
    public void logTxTrace(AbstractSession session, String msgInd, Object[] args) {
        session.log(SessionLog.FINER, SessionLog.TRANSACTION, msgInd, args);
    }

    /*
     * INTERNAL:
     * Helper method to log transaction state to the transaction channel.
     */
    public void logTxStateTrace(AbstractSession session, String msgInd, Object status) {
        if (session.shouldLog(SessionLog.FINER, SessionLog.TRANSACTION)) {
            String statusString = statusToString_impl(status);
            Object[] args = { statusString };
            session.log(SessionLog.FINER, SessionLog.TRANSACTION, msgInd, args);
        }
    }

    //---------------------------------------------------------------------------------
    // The following methods must be implemented by subclass implementations.
    //---------------------------------------------------------------------------------

    /**
     * INTERNAL:
     * Register the specified synchronization listener with the given active
     * transaction.
     *
     * @param listener The synchronization listener created for this transaction
     * @param txn The active transaction for which notification is being requested
     */
    protected abstract void registerSynchronization_impl(AbstractSynchronizationListener listener, Object txn) throws Exception;

    /**
     * INTERNAL:
     * Return the active external transaction for the calling thread, or null if
     * none is currently active for this thread.
     *
     * @return The active transaction object, or null if no transaction is active
     */
    protected abstract Object getTransaction_impl() throws Exception;

    /**
     * INTERNAL:
     * Return a key for the specified external transaction object.
     * The key is just something that can be inserted into a hashtable (must support
     * hashCode() and equals() methods).
     *
     * @param transaction The transaction to which the returned key applies (may be null)
     * @return A key for the passed in transaction, or null if no transaction specified
     */
    protected abstract Object getTransactionKey_impl(Object transaction) throws Exception;

    /**
     * INTERNAL:
     * Return the transaction status. This may be any type of status or value,
     * depending upon the transaction system.
     *
     * @return The current transaction status
     */
    protected abstract Object getTransactionStatus_impl() throws Exception;

    /**
     * INTERNAL:
     * Begin an external transaction. Do this in a way appropriate to the
     * transaction subsystem.
     */
    protected abstract void beginTransaction_impl() throws Exception;

    /**
     * INTERNAL:
     * Commit the external transaction. Do this in a way appropriate to the
     * transaction subsystem.
     */
    protected abstract void commitTransaction_impl() throws Exception;

    /**
     * INTERNAL:
     * Roll back the external transaction. Do this in a way appropriate to the
     * transaction subsystem.
     */
    protected abstract void rollbackTransaction_impl() throws Exception;

    /**
     * INTERNAL:
     * Mark the external transaction for rollback. Do this in a way appropriate to the
     * transaction subsystem.
     */
    protected abstract void markTransactionForRollback_impl() throws Exception;

    /**
     * INTERNAL:
     * Return true if the status indicates that a transaction can be started. This
     * would normally mean that no transaction is currently active.
     * The status is interpreted by the transaction subsystem.
     *
     * @param status The current transaction status
     * @return true if the current state allows for a transaction to be started
     */
    protected abstract boolean canBeginTransaction_impl(Object status);

    /**
     * INTERNAL:
     * Return true if the status indicates that a transaction can be committed. This
     * would normally mean that a transaction is currently active.
     * The status is interpreted by the transaction subsystem.
     *
     * @param status The current transaction status
     * @return true if the current state allows for a transaction to be committed
     */
    protected abstract boolean canCommitTransaction_impl(Object status);

    /**
     * INTERNAL:
     * Return true if the status indicates that a transaction can be rolled back. This
     * would normally mean that a transaction is currently active.
     * The status is interpreted by the transaction subsystem.
     *
     * @param status The current transaction status
     * @return true if the current state allows for a transaction to be rolled back
     */
    protected abstract boolean canRollbackTransaction_impl(Object status);

    /**
     * INTERNAL:
     * Return true if the status indicates that the SQL should be issued to the db.
     * This would normally mean that a transaction was active and not being rolled
     * back or marked for rollback.
     * The status is interpreted by the transaction subsystem.
     *
     * @param status The current transaction status
     * @return true if the current state allows for the SQL to be sent to the database
     */
    protected abstract boolean canIssueSQLToDatabase_impl(Object status);

    /**
     * INTERNAL:
     * Return true if the status indicates that the unit of work should be merged
     * into the shared cache. This would normally mean that the transaction was
     * committed successfully.
     * The status is interpreted by the transaction subsystem.
     *
     * @param status The current transaction status
     * @return true if the current state dictates that the unit of work should be merged
     */
    protected abstract boolean canMergeUnitOfWork_impl(Object status);

    /**
     * INTERNAL:
     * Convert the status to a string for tracing.
     */
    protected abstract String statusToString_impl(Object status);
}
