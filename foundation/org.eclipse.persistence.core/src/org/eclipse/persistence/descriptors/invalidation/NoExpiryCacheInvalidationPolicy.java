/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.descriptors.invalidation;

import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy;

/**
 * PUBLIC:
 * A cache invalidation policy in which no objects will expire.  The only way for objects
 * to become invalid in the cache is for them to be explicitly set to invalid through
 * method calls on the IdentityMapAccessor.  This is the default cache invalidation policy.
 * @see CacheInvalidationPolicy
 * @see org.eclipse.persistence.sessions.IdentityMapAccessor
 */
public class NoExpiryCacheInvalidationPolicy extends CacheInvalidationPolicy {

    /**
     * INTERNAL:
     * Since this policy implements no expiry, this will always return NO_EXPIRY
     */
    @Override
    public long getExpiryTimeInMillis(CacheKey key) {
        return NO_EXPIRY;
    }

    /**
     * INTERNAL:
     * Return the remaining life of this object
     * Override the default implementation.
     */
    @Override
    public long getRemainingValidTime(CacheKey key) {
        return NO_EXPIRY;
    }

    /**
     * INTERNAL:
     * This will return true if the object is set to be invalid, false otherwise.
     */
    @Override
    public boolean isInvalidated(CacheKey key) {
        return key.getInvalidationState() == CacheKey.CACHE_KEY_INVALID;
    }

    /**
     * INTERNAL:
     * This will return true if the object is set to be invalid, false otherwise.
     */
    @Override
    public boolean isInvalidated(CacheKey key, long currentTimeMillis) {
        return key.getInvalidationState() == CacheKey.CACHE_KEY_INVALID;
    }
}
