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
 * The API for this class and its comments are derived from the JPA 2.0 specification 
 * which is developed under the Java Community Process (JSR 317) and is copyright 
 * Sun Microsystems, Inc. 
 *
 * Contributors:
 *     dclarke - Java Persistence API 2.0 Public Draft
 *               Specification and licensing terms available from
 *               http://jcp.org/en/jsr/detail?id=317
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP) and is made available for testing and evaluation 
 * purposes only. The code is not compatible with any specification of the JCP.
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
     * Commit the current transaction, writing any non-flushed changes to the
     * database.
     * 
     * @throws IllegalStateException
     *             if isActive() is false.
     * @throws RollbackException
     *             if the commit fails.
     */
    public void commit();

    /**
     * Roll back the current transaction.
     * 
     * @throws IllegalStateException
     *             if isActive() is false.
     * @throws PersistenceException
     *             if an unexpected error condition is encountered.
     */
    public void rollback();

    /**
     * Mark the current transaction so that the only possible outcome of the
     * transaction is for the transaction to be rolled back.
     * 
     * @throws IllegalStateException
     *             if isActive() is false.
     */
    public void setRollbackOnly();

    /**
     * Determine whether the current transaction has been marked for rollback.
     * 
     * @throws IllegalStateException
     *             if isActive() is false.
     */
    public boolean getRollbackOnly();

    /**
     * Indicate whether a transaction is in progress.
     * 
     * @throws PersistenceException
     *             if an unexpected error condition is encountered.
     */
    public boolean isActive();
}