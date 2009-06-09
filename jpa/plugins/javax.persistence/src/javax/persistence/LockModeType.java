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

/**
 * Six lock mode types are defined: OPTIMISTIC, OPTIMISTIC_FORCE_INCREMENT,
 * PESSIMISTIC_READ, PESSIMISTIC_WRITE, PESSIMISTIC_FORCE_INCREMENT, NONE. The
 * lock mode type values READ and WRITE are synonyms of OPTIMISTIC and
 * OPTIMISTIC_FORCE_INCREMENT respectively. The latter are to be preferred for
 * new applications.
 * <p>
 * Lock modes <b>OPTIMISTIC</b> and <b>OPTIMISTIC_FORCE_INCREMENT</b> are used
 * for optimistic locking. The semantics of requesting locks of type
 * LockModeType.OPTIMISTIC and LockMode- Type.OPTIMISTIC_FORCE_INCREMENT are the
 * following.
 * <p>
 * If transaction T1 calls lock(entity, LockModeType.OPTIMISTIC) on a versioned
 * object, the entity manager must ensure that neither of the following
 * phenomena can occur:
 * <ul>
 * <li>P1 (Dirty read): Transaction T1 modifies a row. Another transaction T2
 * then reads that row and obtains the modified value, before T1 has committed
 * or rolled back. Transaction T2 eventually commits successfully; it does not
 * matter whether T1 commits or rolls back and whether it does so before or
 * after T2 commits.
 * <li>P2 (Non-repeatable read): Transaction T1 reads a row. Another transaction
 * T2 then modifies or deletes that row, before T1 has committed. Both
 * transactions eventually commit successfully.
 * </ul>
 * <p>
 * Lock modes <b>PESSIMISTIC_READ</b>, <b>PESSIMISTIC_WRITE</b>, and
 * <b>PESSIMISTIC_FORCE_INCREMENT</b> are used to immediately obtain long-term
 * database locks. The semantics of requesting locks of type
 * LockModeType.PESSIMISTIC_READ, LockMode- Type.PESSIMISTIC_WRITE, and
 * LockModeType.PESSIMISTIC_FORCE_INCREMENT are the following.
 * <p>
 * If transaction T1 calls lock(entity, LockModeType.PESSIMISTIC_READ) or
 * lock(entity, LockModeType.PESSIMISTIC_WRITE)on an object, the entity manager
 * must ensure that neither of the following phenomena can occur:
 * <ul>
 * <li>P1 (Dirty read): Transaction T1 modifies a row. Another transaction T2
 * then reads that row and obtains the modified value, before T1 has committed
 * or rolled back.
 * <li>P2 (Non-repeatable read): Transaction T1 reads a row. Another transaction
 * T2 then modifies or deletes that row, before T1 has committed or rolled back.
 * </ul>
 * Any such lock must be obtained immediately and retained until transaction T1
 * completes (commits or rolls back).
 * </p>
 * @see EntityManager#find(Class, Object, LockModeType)
 * @see EntityManager#find(Class, Object, LockModeType, java.util.Map)
 * @see EntityManager#getLockMode(Object)
 * @see EntityManager#lock(Object, LockModeType)
 * @see EntityManager#lock(Object, LockModeType, java.util.Map)
 * @see EntityManager#refresh(Object, LockModeType)
 * @see EntityManager#refresh(Object, LockModeType, java.util.Map)
 * @see NamedQuery#lockMode()
 * @see Query#getLockMode()
 * 
 * @since Java Persistence 2.0
 */
public enum LockModeType {

    /**
     * Read lock READ is synonymous with OPTIMISTIC.
     */
    READ,

    /**
     * Write lock WRITE is synonymous with OPTIMISTIC_FORCE_INCREMENT.
     */
    WRITE,

    /**
     * Equivalent to READ lock
     * 
     * @since Java Persistence 2.0
     */
    OPTIMISTIC,

    /**
     * Equivalent to WRITE lock
     * 
     * @since Java Persistence 2.0
     */
    OPTIMISTIC_FORCE_INCREMENT,

    /**
     * 
     * @since Java Persistence 2.0
     */
    PESSIMISTIC_READ,

    /**
     * 
     * @since Java Persistence 2.0
     */
    PESSIMISTIC_WRITE,

    /**
     * 
     * @since Java Persistence 2.0
     */
    PESSIMISTIC_FORCE_INCREMENT,

    /**
     * No locking
     * 
     * @since Java Persistence 2.0
     */
    NONE
}
