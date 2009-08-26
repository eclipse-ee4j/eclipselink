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
 * Thrown by the persistence provider when an pessimistic locking conflict
 * occurs as a result of a lock timeout. This exception may be thrown as part 
 * of an API call, a flush or at commit time. The current transaction, if one 
 * is active, will be not be marked for rollback.
 *
 * @since Java Persistence 2.0
 */
public class LockTimeoutException extends PersistenceException {
    /** The object that caused the exception */
    Object entity;

    /** 
     * Constructs a new <code>LockTimeoutException</code> exception 
     * with <code>null</code> as its detail message.
     */
    public LockTimeoutException() {
        super();
    }

    /** 
     * Constructs a new <code>LockTimeoutException</code> exception 
     * with the specified detail message.
     * @param   message   the detail message.
     */
    public LockTimeoutException(String message) {
        super(message);
    }

    /** 
     * Constructs a new <code>LockTimeoutException</code> exception 
     * with the specified detail message and cause.
     * @param   message   the detail message.
     * @param   cause     the cause.
     */
    public LockTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    /** 
     * Constructs a new <code>LockTimeoutException</code> exception 
     * with the specified cause.
     * @param   cause     the cause.
     */
    public LockTimeoutException(Throwable cause) {
        super(cause);
    }

    /** 
     * Constructs a new <code>LockTimeoutException</code> exception 
     * with the specified entity.
     * @param   entity     the entity.
     */
    public LockTimeoutException(Object entity) {
        this.entity = entity;
    }

    /** 
     * Constructs a new <code>LockTimeoutException</code> exception 
     * with the specified detail message, cause, and entity.
     * @param   message   the detail message.
     * @param   cause     the cause.
     * @param   entity     the entity.
     */
    public LockTimeoutException(String message, Throwable cause, Object entity) {
        super(message, cause);
        this.entity = entity;
    }
    
    /**
     * Returns the entity that caused this exception.
     * @return the entity.
     */
    public Object getEntity() {
        return this.entity;
    }
}


