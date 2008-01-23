/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.transaction;

import javax.persistence.EntityTransaction;
import javax.persistence.TransactionRequiredException;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.jpa.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.transaction.AbstractTransactionController;
import org.eclipse.persistence.exceptions.TransactionException;

/**
 * INTERNAL:
 * JDK 1.5 specific version of JTATransactionWrapper. Differs from the JDK 1.4 version
 * in that it implements a different version of the TransactionWrapper interface, 
 * uses a different EntityManager, and returns a different EntityTransaction version.
 * 
 * @see org.eclipse.persistence.internal.jpa.transactionJTATransactionWrapper
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
     * If this methiod returns without exception then a transaction exists.
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
        if (txnKey != null && this.entityManager.shouldPropagatePersistenceContext()){
            this.txnController.getUnitsOfWork().remove(txnKey);
        }      
        localUOW.release();
        localUOW = null;
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

    /**
     * INTERNAL:
     * THis method is used to get the active UnitOfWork.  It is special in that it will
     * return the required RepeatableWriteUnitOfWork required by the EntityManager.  Once 
     * RepeatableWrite is merged into existing UnitOfWork this code can go away.
     */
    public RepeatableWriteUnitOfWork getTransactionalUnitOfWork(Object transaction){
        if (transaction == null){
            return null;
        }
        if (this.entityManager.shouldPropagatePersistenceContext()){
            Object newTxnKey = this.txnController.getTransactionKey(transaction);
            if (this.txnKey == newTxnKey){
                return this.localUOW;
            }
            this.txnKey = newTxnKey;
            this.localUOW = (RepeatableWriteUnitOfWork)this.txnController.lookupActiveUnitOfWork(transaction);
            if (this.localUOW == null){
                this.localUOW = new RepeatableWriteUnitOfWork(entityManager.getServerSession().acquireClientSession());
                this.localUOW.registerWithTransactionIfRequired();
                this.localUOW.setShouldCascadeCloneToJoinedRelationship(true);
                this.txnController.getUnitsOfWork().put(newTxnKey, this.localUOW);
            }
        }else if (this.localUOW == null){
            this.localUOW = new RepeatableWriteUnitOfWork(entityManager.getServerSession().acquireClientSession());
            this.localUOW.registerWithTransactionIfRequired();
            this.localUOW.setShouldCascadeCloneToJoinedRelationship(true);
        }
        return this.localUOW;
    }
    
    protected void throwUserTransactionException() {
        throw TransactionException.entityTransactionWithJTANotAllowed();
    }

    protected void throwCheckTransactionFailedException() {
        throw new TransactionRequiredException(TransactionException.externalTransactionNotActive().getMessage());
    }
    
    // From old parent.
    //protected void throwCheckTransactionFailedException() {
      //  throw TransactionException.externalTransactionNotActive();
    //}

    public void registerUnitOfWorkWithTxn(UnitOfWorkImpl uow){
        uow.registerWithTransactionIfRequired();
    }
    
    /**
     * We should only flush the entity manager before the query if the query is
     * joined to a transaction
     */
    public boolean shouldFlushBeforeQuery(UnitOfWorkImpl uow){
        return uow.isSynchronized();
    }
}
