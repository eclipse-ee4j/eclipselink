/*******************************************************************************
 * Copyright (c) 2008 - 2012 Oracle Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Linda DeMichiel - Java Persistence 2.1
 *     Linda DeMichiel - Java Persistence 2.0
 *
 ******************************************************************************/ 
package javax.persistence;

/**
 * Specifies whether the persistence context is always automatically 
 * synchronized with the current transaction or whether the persistence context
 * must be explicitly joined to the current transaction by means of the
 * {@link EntityManager#joinTransaction} method.
 *
 * @since Java Persistence 2.1
 */
public enum SynchronizationType {

    /** Persistence context is automatically synchronized with the current transaction */
    SYNCHRONIZED,

    /** Persistence context must be explicitly joined to the current transaction */
    UNSYNCHRONIZED,
}
