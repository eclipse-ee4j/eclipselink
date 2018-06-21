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

import java.util.*;

/**
 * Allows to iterate over {@link CacheKey} instances stored in the {@link IdentityMap}.
 */
public class IdentityMapKeyEnumeration extends AbstractIdentityMapEnumeration<CacheKey> {

    /**
     * Creates an instance of {@link CacheKey} instances enumeration.
     * Checking of read lock on the {@link CacheKey} instances is turned on.
     *
     * @param keys {@link Collection} of {@link CacheKey} instances to be iterated
     */
    public IdentityMapKeyEnumeration(Collection<CacheKey> keys) {
        super(keys, true);
    }

    /**
     * Creates an instance of {@link CacheKey} instances enumeration.
     *
     * @param keys {@link Collection} of {@link CacheKey} instances to be iterated
     * @param shouldCheckReadLocks value of {@code true} if read lock on the {@link CacheKey}
     *        instances should be checked or {@code false} otherwise
     */
    public IdentityMapKeyEnumeration(Collection<CacheKey> keys, boolean shouldCheckReadLocks) {
        super(keys, shouldCheckReadLocks);
    }

    /**
     * Get next element of {@link CacheKey} enumeration if this enumeration
     * object has at least one more element to provide.
     *
     * @return the next element of this enumeration
     * @exception  NoSuchElementException  if no more elements exist
     */
    @Override
    public CacheKey nextElement() {
        return getNextElement();
    }

}
