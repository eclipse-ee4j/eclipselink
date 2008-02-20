/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import org.eclipse.persistence.internal.jpa.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.internal.jpa.transaction.EntityTransactionImpl;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.exceptions.TransactionException;

/**
 * INTERNAL:
 * JDK 1.5 specific version of EntityTransactionWrapper. Differs from the JDK 1.4 version
 * in that it implements a different version of the TransactionWrapper interface, 
 * uses a different EntityManager, and returns a different EntityTransaction version.
 * 
 * @see org.eclipse.persistence.internal.jpa.transaction.EntityTransactionWrapper
 */
public class EntityTransactionWrapper extends TransactionWrapperImpl implements TransactionWrapper {
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
        return this.localUOW;
    }
    
    public EntityManagerImpl getEntityManager(){
        return entityManager;
    }

    /**
     * Lazy initialize the EntityTransaction.
     * There can only be one EntityTransaction at a time.
     */
      public EntityTransaction getTransaction(){
          if (entityTransaction == null){
              entityTransaction = new EntityTransactionImpl(this);
          }
          return entityTransaction;
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
    // From old parent
    //public void setRollbackOnlyInternal(){
      //  if (entityTransaction != null && entityTransaction.isActive()){
        //    entityTransaction.setRollbackOnly();
        //}
    //}
    
    /**
     * Mark the current transaction so that the only possible
     * outcome of the transaction is for the transaction to be
     * rolled back.
     * This is an internal method and if the txn is not active will do nothing
     */
     public void setRollbackOnlyInternal(){
         if (this.getTransaction().isActive()){
             this.getTransaction().setRollbackOnly();
         }
     }
        
    public boolean shouldFlushBeforeQuery(UnitOfWorkImpl uow){
        return true;
    }
    
    // From old parent
    //protected void throwCheckTransactionFailedException() {
      //  throw TransactionException.transactionNotActive();
    //}

    protected void throwCheckTransactionFailedException() {
        throw new TransactionRequiredException(TransactionException.transactionNotActive().getMessage());
    }    
}
