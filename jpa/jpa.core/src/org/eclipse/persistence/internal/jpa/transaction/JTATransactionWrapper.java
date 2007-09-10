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
 * JDK 1.5 specific version of JTATransactionWrapper. Differs from the JDK 1.4 version
 * in that it implements a different version of the TransactionWrapper interface, 
 * uses a different EntityManager, and returns a different EntityTransaction version.
 * 
 * @see org.eclipse.persistence.internal.jpa.transactionJTATransactionWrapper
 */
public class JTATransactionWrapper 
    extends org.eclipse.persistence.internal.jpa.transaction.base.JTATransactionWrapper 
    implements TransactionWrapper
{

    public JTATransactionWrapper(EntityManagerImpl entityManager) {
        super(entityManager);
    }
    
  /**
   *  An ENtityTransaction cannot be used at the same time as a JTA transaction
   *  throw an exception
   */
    public EntityTransaction getTransaction(){
        throw new IllegalStateException(TransactionException.entityTransactionWithJTANotAllowed().getMessage());
    }

    protected void throwCheckTransactionFailedException() {
        throw new TransactionRequiredException(TransactionException.externalTransactionNotActive().getMessage());
    }
}
