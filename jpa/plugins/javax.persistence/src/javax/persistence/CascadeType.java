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
 * Defines the set of cascadable operations that are propagated 
 * to the associated entity.
 * The value <code>cascade=ALL</code> is equivalent to 
 * <code>cascade={PERSIST, MERGE, REMOVE, REFRESH, DETACH}</code>.
 *
 * @since Java Persistence 1.0
 */
public enum CascadeType { 

    /** Cascade all operations */
    ALL, 

    /** Cascade persist operation */
    PERSIST, 

    /** Cascade merge operation */
    MERGE, 

    /** Cascade remove operation */
    REMOVE,

    /** Cascade refresh operation */
    REFRESH,

    /**
     * Cascade detach operation
     *
     * @since Java Persistence 2.0
     * 
     */   
    DETACH
}
