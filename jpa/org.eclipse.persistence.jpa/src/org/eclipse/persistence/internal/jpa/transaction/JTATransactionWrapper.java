/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import javax.persistence.EntityTransaction;
import javax.persistence.TransactionRequiredException;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.transaction.AbstractTransactionController;
import org.eclipse.persistence.exceptions.TransactionException;

/**
 * INTERNAL:
 * JTA transaction wrapper.
 * Allows the EntityManager to transparently use JTA vs local transactions.
 */
public class JTATransactionWrapper extends TransactionWrapperImpl implements TransactionWrapper {

     //This is a quick reference for the external Transaction Controller
    protected AbstractTransactionController txnController;
    
    public JTATransactionWrapper(EntityManagerImpl entityManager) {
        super(entityManager);
        this.txnController = (AbstractTransactionController)entityManager.getServerSession().getExternalTransactionController();
    }

    /**
     * INTERNAL:
     * This method will be used to check for a transaction and throws exception if none exists.
     * If this method returns without exception then a transaction exists.
     * This method must be called before accessing the localUOW.
     */
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
    public void clear(){
        this.localUOW.release();
        this.localUOW = null;
    }
    
    /**
     *  An ENtityTransaction cannot be used at the same time as a JTA transaction
     *  throw an exception
     */
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

    public void registerUnitOfWorkWithTxn(UnitOfWorkImpl uow){
        uow.registerWithTransactionIfRequired();
    }
    
    public void verifyRegisterUnitOfWorkWithTxn(){
    }
    
    /**
     * We should only flush the entity manager before the query if the query is
     * joined to a transaction
     */
    public boolean shouldFlushBeforeQuery(UnitOfWorkImpl uow){
        return uow.isSynchronized();
    }
}
