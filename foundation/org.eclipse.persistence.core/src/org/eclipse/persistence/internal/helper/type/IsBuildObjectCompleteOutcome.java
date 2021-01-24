/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.helper.type;

import org.eclipse.persistence.internal.helper.ConcurrencyManager;

/**
 * Helper type that tells us for a given cache key what threads are having some sort of relationship to the cache key (e.g
 * acquired the cache key as an active thread, have a deferred lock on the cache key or have incremented the counter of
 * readers on the lock or simply are stuck waiting for the cache key to be available.
 */
public class IsBuildObjectCompleteOutcome {

    /**
     * Algorithm can return null when it detects that the object is fully built ant the thread should be allowed to
     * progresses.
     */
    public static final IsBuildObjectCompleteOutcome BUILD_OBJECT_IS_COMPLETE_TRUE = null;

    /**
     * This the thread that at some recursion depth is making the thread that needed to defer locks stop progressing
     */
    private final Thread threadBlockingTheDeferringThreadFromFinishing;

    /**
     * This is the cache key that is being owned by the blocking thread.
     */
    private final ConcurrencyManager cacheKeyOwnedByBlockingThread;

    /**
     * Create a new IsBuildObjectCompleteOutcome.
     *
     * @param threadBlockingTheDeferringThreadFromFinishing
     *            the thread that during going in deep in the recursion is discovered as blocking our initial thread
     * @param cacheKeyOwnedByBlockingThread
     *            the cache key that the blocking thread is currently owning and not releasing.
     */
    public IsBuildObjectCompleteOutcome(Thread threadBlockingTheDeferringThreadFromFinishing,
                                                         ConcurrencyManager cacheKeyOwnedByBlockingThread) {
        super();
        this.threadBlockingTheDeferringThreadFromFinishing = threadBlockingTheDeferringThreadFromFinishing;
        this.cacheKeyOwnedByBlockingThread = cacheKeyOwnedByBlockingThread;
    }

    /** Getter for {@link #threadBlockingTheDeferringThreadFromFinishing} */
    public Thread getThreadBlockingTheDeferringThreadFromFinishing() {
        return threadBlockingTheDeferringThreadFromFinishing;
    }

    /** Getter for {@link #cacheKeyOwnedByBlockingThread} */
    public ConcurrencyManager getCacheKeyOwnedByBlockingThread() {
        return cacheKeyOwnedByBlockingThread;
    }
}
