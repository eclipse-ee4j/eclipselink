/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors:
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
 *     		     Specification available from http://jcp.org/en/jsr/detail?id=317
 *     gyorke - Java Persistence 2.0 - Post Proposed Final Draft (March 13, 2009) Updates
 *               Specification available from http://jcp.org/en/jsr/detail?id=317
 *
 * Java(TM) Persistence API, Version 2.0 - EARLY ACCESS
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP).  The code is untested and presumed not to be a  
 * compatible implementation of JSR 317: Java(TM) Persistence API, Version 2.0.   
 * We encourage you to migrate to an implementation of the Java(TM) Persistence 
 * API, Version 2.0 Specification that has been tested and verified to be compatible 
 * as soon as such an implementation is available, and we encourage you to retain 
 * this notice in any implementation of Java(TM) Persistence API, Version 2.0 
 * Specification that you distribute.
 ******************************************************************************/
package javax.persistence;

public interface EntityTransaction {
    /**
     * Start a resource transaction.
     * 
     * @throws IllegalStateException
     *             if isActive() is true.
     */
    public void begin();

    /**
     * Commit the current resource transaction, writing any 
     * unflushed changes to the database.  
     * 
     * @throws IllegalStateException
     *             if isActive() is false.
     * @throws RollbackException
     *             if the commit fails.
     */
    public void commit();

    /**
     * Roll back the current resource transaction. 
     * 
     * @throws IllegalStateException
     *             if isActive() is false.
     * @throws PersistenceException
     *             if an unexpected error condition is encountered.
     */
    public void rollback();

    /**
     * Mark the current resource transaction so that the only 
     * possible outcome of the transaction is for the transaction 
     * to be rolled back. 
     * 
     * @throws IllegalStateException
     *             if isActive() is false.
     */
    public void setRollbackOnly();

    /**
     * Determine whether the current resource transaction has been 
     * marked for rollback.
     * 
     * @throws IllegalStateException
     *             if isActive() is false.
     */
    public boolean getRollbackOnly();

    /**
     * Indicate whether a resource transaction is in progress.
     * 
     * @throws PersistenceException
     *             if an unexpected error condition is encountered.
     */
    public boolean isActive();
}