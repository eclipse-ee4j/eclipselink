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
import org.eclipse.persistence.exceptions.TransactionException;

/**
 * INTERNAL:
 * JDK 1.5 specific version of EntityTransactionWrapper. Differs from the JDK 1.4 version
 * in that it implements a different version of the TransactionWrapper interface, 
 * uses a different EntityManager, and returns a different EntityTransaction version.
 * 
 * @see org.eclipse.persistence.internal.jpa.transaction.EntityTransactionWrapper
 */
public class EntityTransactionWrapper 
    extends org.eclipse.persistence.internal.jpa.transaction.base.EntityTransactionWrapper 
    implements TransactionWrapper
{

    public EntityTransactionWrapper(EntityManagerImpl entityManager) {
        super(entityManager);
    }
    
  /**
   * Lazy initialize the EntityTransaction.
   * There can only be one EntityTransaction at a time.
   */
    public EntityTransaction getTransaction(){
        if (entityTransaction == null){
            entityTransaction = new EntityTransactionImpl(this);
        }
        return (EntityTransaction)entityTransaction;
    }

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
        
    protected void throwCheckTransactionFailedException() {
        throw new TransactionRequiredException(TransactionException.transactionNotActive().getMessage());
    }
}
