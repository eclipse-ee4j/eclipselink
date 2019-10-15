/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.identitymaps;

/**
 * <p><b>Purpose</b>: Provides the capability to insert CacheKeys into a Linked List.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Provide same capabilities as superclass.
 * <li> Maintain within linked list.
 * </ul>
 * @see CacheIdentityMap
 * @since TOPLink/Java 1.0
 */
public class LinkedCacheKey extends CacheKey {

    /** Handle on previous element in cache */
    protected LinkedCacheKey previous;

    /** Handle on next element in cache */
    protected LinkedCacheKey next;

    /**
     * Initialize the newly allocated instance of this class.
     * @param object is the domain object.
     * @param writeLockValue is the write lock value number.
     */
    public LinkedCacheKey(Object primaryKey, Object object, Object writeLockValue, long readTime, boolean isIsolated) {
        super(primaryKey, object, writeLockValue, readTime, isIsolated);
    }

    public LinkedCacheKey getNext() {
        return next;
    }

    public LinkedCacheKey getPrevious() {
        return previous;
    }

    public void setNext(LinkedCacheKey next) {
        this.next = next;
    }

    public void setPrevious(LinkedCacheKey previous) {
        this.previous = previous;
    }
}
