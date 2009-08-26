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
 * Thrown by the persistence provider when an entity reference obtained by
 * {@link EntityManager#getReference EntityManager.getReference(Class,Object)}
 * is accessed but the entity does not exist. Also thrown when
 * {@link EntityManager#refresh EntityManager.refresh(Object)} is called and the
 * object no longer exists in the database. The current transaction, if one is
 * active, will be marked for rollback.
 * 
 * @see EntityManager#getReference(Class,Object)
 * @see EntityManager#refresh(Object)
 * @see EntityManager#refresh(Object, LockModeType)
 * @see EntityManager#refresh(Object, java.util.Map)
 * @see EntityManager#refresh(Object, LockModeType, java.util.Map)
 * @see EntityManager#lock(Object, LockModeType)
 * @see EntityManager#lock(Object, LockModeType, java.util.Map)
 * 
 * @since Java Persistence 1.0
 */
public class EntityNotFoundException extends PersistenceException {

	/**
	 * Constructs a new <code>EntityNotFoundException</code> exception with
	 * <code>null</code> as its detail message.
	 */
	public EntityNotFoundException() {
		super();
	}

	/**
	 * Constructs a new <code>EntityNotFoundException</code> exception with the
	 * specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 */
	public EntityNotFoundException(String message) {
		super(message);
	}

}
