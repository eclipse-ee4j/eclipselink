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
 * Thrown by the persistence provider when {@link EntityManager#persist(Object)
 * EntityManager.persist(Object)} is called and the entity already exists. The
 * current transaction, if one is active, will be marked for rollback.
 * <p>
 * If the entity already exists, the EntityExistsException may be thrown when
 * the persist operation is invoked, or the EntityExistsException or another
 * PersistenceException may be thrown at flush or commit time.
 * 
 * @see javax.persistence.EntityManager#persist(Object)
 * 
 * @since Java Persistence 1.0
 */
public class EntityExistsException extends PersistenceException {

    /**
     * Constructs a new <code>EntityExistsException</code> exception with
     * <code>null</code> as its detail message.
     */
    public EntityExistsException() {
        super();
    }

    /**
     * Constructs a new <code>EntityExistsException</code> exception with the
     * specified detail message.
     * 
     * @param message
     *            the detail message.
     */
    public EntityExistsException(String message) {
        super(message);
    }

    /**
     * Constructs a new <code>EntityExistsException</code> exception with the
     * specified detail message and cause.
     * 
     * @param message
     *            the detail message.
     * @param cause
     *            the cause.
     */
    public EntityExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new <code>EntityExistsException</code> exception with the
     * specified cause.
     * 
     * @param cause
     *            the cause.
     */
    public EntityExistsException(Throwable cause) {
        super(cause);
    }
}
