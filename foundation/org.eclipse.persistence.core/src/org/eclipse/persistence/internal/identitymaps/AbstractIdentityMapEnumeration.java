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
//     12/14/2017-3.0 Tomas Kraus
//       - 522635: ConcurrentModificationException when triggering lazy load from conforming query
package org.eclipse.persistence.internal.identitymaps;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Abstract {@link Enumeration} interface implementation for {@link IdentityMap}
 * interface. Allows to iterate over {@link CacheKey} instances stored in the map.
 *
 * @param <T> type of iterated {@link CacheKey} content
 */
public abstract class AbstractIdentityMapEnumeration<T> implements Enumeration<T> {

    /** {@link CacheKey} instances iterator. */
    protected final Iterator<CacheKey> cacheKeysIterator;

    /** Next key to be returned. */
    protected CacheKey nextKey;

    /** Value of {@code true} if readLocks should be checked or false otherwise. */
    protected boolean shouldCheckReadLocks;

    /**
     * Creates an instance of {@link CacheKey} content enumeration.
     *
     * @param keys {@link Collection} of {@link CacheKey} instances to be iterated
     * @param shouldCheckReadLocks value of {@code true} if read lock on the {@link CacheKey}
     *        instances should be checked or {@code false} otherwise
     */
    public AbstractIdentityMapEnumeration(Collection<CacheKey> keys, boolean shouldCheckReadLocks) {
        this.shouldCheckReadLocks = shouldCheckReadLocks;
        this.cacheKeysIterator = keys.iterator();
    }

    /**
     * Check whether this enumeration contains more elements.
     *
     * @return value of {@code true} if this enumeration object contains at least
     *         one more element to provide or {@code false} otherwise
     */
    @Override
    public boolean hasMoreElements() {
        this.nextKey = getNextCacheKey();
        return this.nextKey != null;
    }

    /**
     * Get next element of {@link CacheKey} content enumeration if this enumeration
     * object has at least one more element to provide.
     * It it expected that this method will be implemented using {@link #getNextElement()}
     * in child classes.
     *
     * @return the next element of this enumeration
     * @exception NoSuchElementException if no more elements exist
     */
    @Override
    public abstract T nextElement();

    /**
     * Get next {@link CacheKey} instance from iterator.
     *
     * @return next {@link CacheKey} instance or {@code null} if there is no more
     *         instance to provide
     */
    private CacheKey getNextCacheKey() {
        CacheKey key = null;
        while (cacheKeysIterator.hasNext() && (key == null)) {
            key = cacheKeysIterator.next();
        }
        return key;
    }

    /**
     * Get next element of {@link CacheKey} instances enumeration if this enumeration
     * object has at least one more element to provide.
     *
     * @return the next element of this enumeration
     * @exception NoSuchElementException  if no more elements exist
     */
    protected CacheKey getNextElement() {
        if (this.nextKey == null) {
            throw new NoSuchElementException("AbstractIdentityMapEnumeration nextElement");
        }
        // The read lock check is for avoidance of half built objects being returned.
        // bug 275724: Added shouldCheckReadLocks to avoid the read lock check when invalidating.
        if (shouldCheckReadLocks) {
            this.nextKey.checkReadLock();
        }
        return this.nextKey;
    }

}
