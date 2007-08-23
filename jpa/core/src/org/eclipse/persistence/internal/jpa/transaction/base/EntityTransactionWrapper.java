/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.transaction.base;

import javax.persistence.TransactionRequiredException;

import org.eclipse.persistence.internal.jpa.base.*;
import org.eclipse.persistence.exceptions.TransactionException;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;

/**
 * INTERNAL:
 * Base EntityTransactionWrapper
 * The EntityTransactionWrapper is used to make in transparent to an EntityManager
 * what kind of transaction is being used.  Transaction type can either be JTATransaction
 * or EntityTransaciton and they are mutually exclusive.  This is the implementation for
 * EntityTransaction
 * 
 * @see org.eclipse.persistence.internal.jpa.transaction.EntityTransactionWrapper
 * @see org.eclipse.persistence.internal.jpa.transaction.jdk14.EntityTransactionWrapper
 */
public class EntityTransactionWrapper extends TransactionWrapperImpl {

    protected EntityTransactionImpl entityTransaction;

    public EntityTransactionWrapper(EntityManagerImpl entityManager) {
        super(entityManager);
    }
    

    /**
     * INTERNAL:
     * This method will be used to check for a transaction and throws exception if none exists.
     * If this methiod returns without exception then a transaction exists.
     * This method must be called before accessing the localUOW.
     */
    public Object checkForTransaction(boolean validateExistence){
        if (entityTransaction != null && entityTransaction.isActive()) {
            return entityTransaction;
        }
        if (validateExistence){
            throwCheckTransactionFailedException();
        }
        return null;
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
        if (this.localUOW == null){
            this.localUOW = new RepeatableWriteUnitOfWork(entityManager.getServerSession().acquireClientSession());
            this.localUOW.setShouldCascadeCloneToJoinedRelationship(true);
        }
        return (RepeatableWriteUnitOfWork)this.localUOW;
    }
    
    public EntityManagerImpl getEntityManager(){
        return entityManager;
    }

    public void registerUnitOfWorkWithTxn(UnitOfWorkImpl uow){
        throw new TransactionRequiredException(ExceptionLocalization.buildMessage("join_trans_called_on_entity_trans"));// no JTA transactions availab
    }
    
    /**
    * Mark the current transaction so that the only possible
    * outcome of the transaction is for the transaction to be
    * rolled back.
    * This is an internal method and if the txn is not active will do nothing
    */
    public void setRollbackOnlyInternal(){
        if (entityTransaction != null && entityTransaction.isActive()){
            entityTransaction.setRollbackOnly();
        }
    }
        
    public boolean shouldFlushBeforeQuery(UnitOfWorkImpl uow){
        return true;
    }
    
    protected void throwCheckTransactionFailedException() {
        throw TransactionException.transactionNotActive();
    }
}
