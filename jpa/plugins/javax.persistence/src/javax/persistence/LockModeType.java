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
*     pkrogh -        Java Persistence API 2.0 Public Draft
*                     Specification and licensing terms available from
*                     http://jcp.org/en/jsr/detail?id=317
*
* EARLY ACCESS - PUBLIC DRAFT
* This is an implementation of an early-draft specification developed under the 
* Java Community Process (JCP) and is made available for testing and evaluation 
* purposes only. The code is not compatible with any specification of the JCP.
******************************************************************************/
package javax.persistence;

/**
 * Lock modes that can be specified by means of the 
 * {@link EntityManager#lock EntityManager.lock()} method.
 * 
 * <p> The semantics of requesting locks of type 
 * {@link LockModeType#READ LockModeType.READ} and {@link 
 * LockModeType#WRITE LockModeType.WRITE} are the following.
 *
 * <p> If transaction T1 calls lock(entity, {@link 
 * LockModeType#READ LockModeType.READ}) on a versioned object, 
 * the entity manager must ensure that neither of the following 
 * phenomena can occur:
 * <ul>
 *   <li> P1 (Dirty read): Transaction T1 modifies a row. 
 * Another transaction T2 then reads that row and obtains 
 * the modified value, before T1 has committed or rolled back. 
 * Transaction T2 eventually commits successfully; it does not 
 * matter whether T1 commits or rolls back and whether it does 
 * so before or after T2 commits.
 *   <li>
 *   </li> P2 (Non-repeatable read): Transaction T1 reads a row. 
 * Another transaction T2 then modifies or deletes that row, 
 * before T1 has committed. Both transactions eventually commit 
 * successfully.
 *   </li>
 * </ul>
 *
 * <p> Lock modes must always prevent the phenomena P1 and P2.
 *
 * <p> In addition, calling lock(entity, LockModeType.WRITE) on 
 * a versioned object, will also force an update (increment) to 
 * the entity's version column.
 *
 * <p> The persistence implementation is not required to support 
 * calling {@link EntityManager#lock EntityManager.lock()} on a 
 * non-versioned object. When it cannot support a such lock call, 
 * it must throw the {@link PersistenceException}.
 *
 *
 * @since Java Persistence API 1.0
 */
public enum LockModeType  {

    /** Read lock */
    READ,

    /** Write lock */
    WRITE,
    
    /** 
     * Equivalent to READ lock 
     * @since Java Persistence API 1.0
     */
    OPTIMISTIC,
    
    /** 
     * Equivalent to WRITE lock 
     * @since Java Persistence API 1.0
     */
    OPTIMISTIC_FORCE_INCREMENT,
    
    /** 
     * Read lock 
     * @since Java Persistence API 1.0
     */
    PESSIMISTIC,
    
    /** 
     * Write lock 
     * @since Java Persistence API 1.0
     */
    PESSIMISTIC_FORCE_INCREMENT,
    
    /** 
     * No locking 
     * @since Java Persistence API 1.0
     */
    NONE
}
