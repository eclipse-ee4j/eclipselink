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

/**
 * JDK 1.5 version of the EntityTransaction.  Differs from base version only in that
 * it takes a JDK 1.5 version of the EntityTransactionWrapper.
 *
 * @see org.eclipse.persistence.internal.jpa.transaction.EntityTransactionImpl
 */
public class EntityTransactionImpl 
    extends org.eclipse.persistence.internal.jpa.transaction.base.EntityTransactionImpl
    implements javax.persistence.EntityTransaction 
{
    public EntityTransactionImpl(EntityTransactionWrapper wrapper) {
        super(wrapper);
    }       
	/**
     * Commit the current transaction, writing any un-flushed changes to the database.
     * This can only be invoked if {@link #isActive()} returns <code>true</code>.
     * @throws IllegalStateException if isActive() is false.
     * @throws PersistenceException if the commit fails.
     */
    public void commit(){
      try{
        super.commit();
      }catch (org.eclipse.persistence.exceptions.EclipseLinkException tlException ) {
		//put here to avoid EJB3.0 dependencies in TopLink for jdk 1.4 
        throw new javax.persistence.RollbackException(tlException);
      }
    } 
}
