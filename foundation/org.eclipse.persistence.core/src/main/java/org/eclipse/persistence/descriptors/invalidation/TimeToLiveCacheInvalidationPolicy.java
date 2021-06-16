/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.descriptors.invalidation;

import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy;

/**
 * PUBLIC:
 * A CacheInvalidationPolicy which allows objects to live for a specific amount of time
 * after they are read.  A TimeToLiveCacheInvalidationPolicy is instantiated with a specific
 * number of milliseconds.  This represents how long after an object is read it will expire.
 * @see CacheInvalidationPolicy
 */
public class TimeToLiveCacheInvalidationPolicy extends CacheInvalidationPolicy {
    /** Number of milliseconds before invalidation. */
    protected long timeToLive = 0;

    /**
     * INTERNAL:
     * Default Constructor for Project XML
     * Unless the timeToLive is set by method, objects will expire immediately
     */
    public TimeToLiveCacheInvalidationPolicy() {
    }

    /**
     * PUBLIC:
     * Construct a TimeToLiveCacheInvalidationPolicy
     * @param timeToLive the number of milliseconds an object affected by this policy will live.
     */
    public TimeToLiveCacheInvalidationPolicy(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    /**
     * INTERNAL:
     * Return the next expiry time.
     */
    @Override
    public long getExpiryTimeInMillis(CacheKey key) {
        if (this.isInvalidationRandomized) {
            // If using randomized invalidation, subtract 0-10% of the timeToLive
            int randomDelta = this.random.nextInt((int)this.timeToLive / 10);
            return key.getReadTime() + (this.timeToLive - randomDelta);
        } else {
            return key.getReadTime() + this.timeToLive;
        }
    }

    /**
     * PUBLIC:
     * Return the time-to-live specified for this policy.
     */
    public long getTimeToLive() {
        return timeToLive;
    }

    /**
     * INTERNAL:
     * Return true if this object is set as invalid or has expired.
     */
    @Override
    public boolean isInvalidated(CacheKey key, long currentTimeMillis) {
        if (key.getInvalidationState() == CacheKey.CACHE_KEY_INVALID) {
            return true;
        }
        return getExpiryTimeInMillis(key) <= currentTimeMillis;
    }

    /**
     * PUBLIC:
     * Set the time-to-live specified by this policy.
     */
    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    @Override
    public Object clone() {
        TimeToLiveCacheInvalidationPolicy clone = null;

        try {
            clone = (TimeToLiveCacheInvalidationPolicy)super.clone();
            clone.timeToLive = this.timeToLive;
        } catch (Exception exception) {
            throw new InternalError("clone failed");
        }

        return clone;
    }
}
