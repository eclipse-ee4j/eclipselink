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
package org.eclipse.persistence.internal.jpa.transaction;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;
import javax.transaction.Synchronization;
import javax.transaction.Transaction;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.transaction.AbstractTransactionController;
import org.eclipse.persistence.exceptions.TransactionException;

/**
 * INTERNAL:
 * JTA transaction wrapper.
 * Allows the EntityManager to transparently use JTA vs local transactions.
 */
public class JTATransactionWrapper extends TransactionWrapperImpl implements TransactionWrapper{

     //This is a quick reference for the external Transaction Controller
    protected AbstractTransactionController txnController;

    //flag that allows lazy initialization of the persistence context while still registering
    // with the transaction for after completion.
    private boolean isJoined = false;

    public JTATransactionWrapper(EntityManagerImpl entityManager) {
        super(entityManager);
        this.txnController = (AbstractTransactionController)entityManager.getDatabaseSession().getExternalTransactionController();
    }

    /**
     * INTERNAL:
     * This method will be used to check for a transaction and throws exception if none exists.
     * If this method returns without exception then a transaction exists.
     * This method must be called before accessing the localUOW.
     */
    @Override
    public Object checkForTransaction(boolean validateExistence){
        Object transaction = this.txnController.getTransaction();
        if (validateExistence && (transaction == null)){
            throwCheckTransactionFailedException();
        }
        return transaction;
    }

    /**
     * INTERNAL:
     * Internal clear the underlying data structures that this transaction owns.
     */
    @Override
    public void clear(){
        this.localUOW.release();
        this.localUOW = null;
    }

    /**
     *  An ENtityTransaction cannot be used at the same time as a JTA transaction
     *  throw an exception
     */
    @Override
    public EntityTransaction getTransaction(){
      throw new IllegalStateException(TransactionException.entityTransactionWithJTANotAllowed().getMessage());
    }

    /**
    * INTERNAL:
    * Mark the current transaction so that the only possible
    * outcome of the transaction is for the transaction to be
    * rolled back.
    * This is an internal method and if the txn is not active will do nothing
    */
    @Override
    public void setRollbackOnlyInternal() {
        if(txnController.getTransaction() != null) {
            txnController.markTransactionForRollback();
        }
    }

    protected void throwUserTransactionException() {
        throw TransactionException.entityTransactionWithJTANotAllowed();
    }

    protected void throwCheckTransactionFailedException() {
        throw new TransactionRequiredException(TransactionException.externalTransactionNotActive().getMessage());
    }

    @Override
    public boolean isJoinedToTransaction(UnitOfWorkImpl uow) {
        if (this.entityManager.hasActivePersistenceContext()) {
            return uow.getParent().hasExternalTransactionController() && uow.isSynchronized();
        }
        //We don't need to check if there is an active trans, as we now register with it when join is called
        //until we get an active context
        return isJoined;
    }

    @Override
    public void registerIfRequired(UnitOfWorkImpl uow){
        //EM already validated that there is a JTA transaction.
        if (this.entityManager.hasActivePersistenceContext()) {
            //we have a context initialized, so have it register with the transaction
            uow.registerWithTransactionIfRequired();
        } else if (!isJoined) {

//        JPA 3.2.4
//                In general, a persistence context will be synchronized to the database as described below. However, a
//                persistence context of type SynchronizationType.UNSYNCHRONIZED or an application-managed
//                persistence context that has been created outside the scope of the current transaction will only be
//                synchronized to the database if it has been joined to the current transaction by the application's use of
//                the EntityManager joinTransaction method.
//                ..
//                If there is no transaction active
//                or if the persistence context has not been joined to the current transaction, the persistence provider must
//                not flush to the database.
//          if (syncType == null {
//              App managed, so we need to start the active persistence Context.  Or do we?
//          } else if (syncType.equals(SynchronizationType.SYNCHRONIZED)) {
//              need to ensure we do not init the context until we need too
//          } else {
//              this is unsynchronized, so we need to start the active persistence Context.  Or do we?
//          }

            Object txn = checkForTransaction(true);
//            duplicating what is done in
//            TransactionController.registerSynchronizationListener(this, this.parent);
//            This will need to change if javax.transaction dependencies are to be removed from JPA. See TransactionImpl
            try {
                ((Transaction)txn).registerSynchronization(new Synchronization() {

                    @Override
                    public void beforeCompletion() {}
                    @Override
                    public void afterCompletion(int status) {
                        //let the wrapper know the listener is no longer registered to an active transaction
                        isJoined = false;
                    }
                });
            } catch (Exception e) {
                throw new PersistenceException(TransactionException.errorBindingToExternalTransaction(e).getMessage(), e);
            }
            isJoined = true;
        }
    }



}
