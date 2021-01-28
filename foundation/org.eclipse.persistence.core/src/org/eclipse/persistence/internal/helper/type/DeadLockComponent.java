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
import org.eclipse.persistence.internal.helper.ConcurrencyUtil;

import java.util.Map;

public class DeadLockComponent {

    Thread threadNotAbleToAccessResource;

    // Mutually exclusive boolean flags
    /**
     * One of the code spots a thread can get stuck is when it is waiting for
     * {@link org.eclipse.persistence.internal.helper.ConcurrencyManager#isBuildObjectOnThreadComplete(Thread, Map)} to
     * return true in the {@code CacheKey.releaseDeferredLock}
     *
     * <P>
     * TRUE VALUE: <br>
     * This is the least obvious of all scenario. When a thread is stuck on a deferred lock it wanted a key that it
     * could not get for object building. So it needs to make sure the key is no longer acquired by anybody or if
     * anybody is owning the key that this thread is also delcaring itself as finished and no longer to building
     * anything.
     *
     * <P>
     * we will need to re-write the
     * {@link org.eclipse.persistence.internal.helper.ConcurrencyManager#isBuildObjectOnThreadComplete(Thread, Map)}
     * to be able to know what thread and what cache key is being thorny point on object building.
     *
     */
    private boolean stuckOnReleaseDeferredLock;

    /**
     * The current thread wants to do an acquire on a cache key but is not able to get the cache key. So it is stuck.
     */
    private boolean stuckThreadAcquiringLockForWriting;

    /**
     * The current thread wants to do an acquire on a cache key but the cache key is not available for reading. So the
     * thread is stuck.
     */
    private boolean stuckThreadAcquiringLockForReading;

    // The cache key that is at the heart of the problems of the current thread.
    /**
     * Then a thread wants to acquire a specific cache key and cannot get it we can put explicit information about cache
     * key.
     */
    private ConcurrencyManager cacheKeyThreadWantsToAcquireButCannotGet;

    // Flags that would be set to true if we run out of happy path options to expand and realize
    // that the cache key our thread wants seems to be corrupted

    /**
     * after some explosions in the concurrent manager, we believe to already have seen the cache corrupted where a
     * cache key had an active thread that was no longer doing anything. This could lead to a dummy dead lock.
     */
    private boolean deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread;

    /**
     * We are able to track everyone that registers for READING. So if we see a cache key with a number of readers we
     * are not able to justify and have no other routes to expand we need to consider the possibility of being stuck due
     * to a cache key that has a number of readers greater than the known readers.
     */
    private boolean deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders;

    // Recursion unwinding.

    /**
     * The next thread in the dead lock sequence.
     */
    private DeadLockComponent nextThreadPartOfDeadLock;

    /**
     * Set to true on the very first DTO we build - at the moment when we finally disocver our dead lock.
     *
     */
    private boolean isFirstRepeatingThreadThatExplainsDeadLock;

    /**
     * Create a new DeadLockComponent.
     *
     * @param threadNotAbleToAccessResource
     * @param stuckOnReleaseDeferredLock
     * @param stuckThreadAcquiringLockForWriting
     * @param stuckThreadAcquiringLockForReading
     * @param cacheKeyThreadWantsToAcquireButCannotGet
     * @param deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread
     * @param deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders
     * @param nextThreadPartOfDeadLock
     */
    public DeadLockComponent(Thread threadNotAbleToAccessResource, boolean stuckOnReleaseDeferredLock,
                                              boolean stuckThreadAcquiringLockForWriting, boolean stuckThreadAcquiringLockForReading,
                                              ConcurrencyManager cacheKeyThreadWantsToAcquireButCannotGet,
                                              boolean deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread,
                                              boolean deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders,
                                              DeadLockComponent nextThreadPartOfDeadLock) {
        super();
        this.threadNotAbleToAccessResource = threadNotAbleToAccessResource;
        this.stuckOnReleaseDeferredLock = stuckOnReleaseDeferredLock;
        this.stuckThreadAcquiringLockForWriting = stuckThreadAcquiringLockForWriting;
        this.stuckThreadAcquiringLockForReading = stuckThreadAcquiringLockForReading;
        this.cacheKeyThreadWantsToAcquireButCannotGet = cacheKeyThreadWantsToAcquireButCannotGet;
        this.deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread = deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread;
        this.deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders = deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders;
        this.nextThreadPartOfDeadLock = nextThreadPartOfDeadLock;
        this.isFirstRepeatingThreadThatExplainsDeadLock = false;
    }

    /**
     * Constructor to be used when we start undoing our recursion due to having found a repeated thread that allows to
     * conclude we have discovered our dead lock. When we do this, we will not populate any additionla data on the DTO.
     *
     * Create a new DeadLockComponent.
     *
     * @param threadNotAbleToAccessResource
     */
    public DeadLockComponent(Thread threadNotAbleToAccessResource) {
        // our deadlock has been found
        this.threadNotAbleToAccessResource = threadNotAbleToAccessResource;
        this.isFirstRepeatingThreadThatExplainsDeadLock = true;
    }

    /** Getter for {@link #threadNotAbleToAccessResource} */
    public Thread getThreadNotAbleToAccessResource() {
        return threadNotAbleToAccessResource;
    }

    /** Setter for {@link #threadNotAbleToAccessResource} */
    public void setThreadNotAbleToAccessResource(Thread threadNotAbleToAccessResource) {
        this.threadNotAbleToAccessResource = threadNotAbleToAccessResource;
    }

    /** Getter for {@link #stuckOnReleaseDeferredLock} */
    public boolean isStuckOnReleaseDeferredLock() {
        return stuckOnReleaseDeferredLock;
    }

    /** Setter for {@link #stuckOnReleaseDeferredLock} */
    public void setStuckOnReleaseDeferredLock(boolean stuckOnReleaseDeferredLock) {
        this.stuckOnReleaseDeferredLock = stuckOnReleaseDeferredLock;
    }

    /** Getter for {@link #stuckThreadAcquiringLockForWriting} */
    public boolean isStuckThreadAcquiringLockForWriting() {
        return stuckThreadAcquiringLockForWriting;
    }

    /** Setter for {@link #stuckThreadAcquiringLockForWriting} */
    public void setStuckThreadAcquiringLockForWriting(boolean stuckThreadAcquiringLockForWriting) {
        this.stuckThreadAcquiringLockForWriting = stuckThreadAcquiringLockForWriting;
    }

    /** Getter for {@link #stuckThreadAcquiringLockForReading} */
    public boolean isStuckThreadAcquiringLockForReading() {
        return stuckThreadAcquiringLockForReading;
    }

    /** Setter for {@link #stuckThreadAcquiringLockForReading} */
    public void setStuckThreadAcquiringLockForReading(boolean stuckThreadAcquiringLockForReading) {
        this.stuckThreadAcquiringLockForReading = stuckThreadAcquiringLockForReading;
    }

    /** Getter for {@link #cacheKeyThreadWantsToAcquireButCannotGet} */
    public ConcurrencyManager getCacheKeyThreadWantsToAcquireButCannotGet() {
        return cacheKeyThreadWantsToAcquireButCannotGet;
    }

    /** Setter for {@link #cacheKeyThreadWantsToAcquireButCannotGet} */
    public void setCacheKeyThreadWantsToAcquireButCannotGet(
            ConcurrencyManager cacheKeyThreadWantsToAcquireButCannotGet) {
        this.cacheKeyThreadWantsToAcquireButCannotGet = cacheKeyThreadWantsToAcquireButCannotGet;
    }

    /** Getter for {@link #deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread} */
    public boolean isDeadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread() {
        return deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread;
    }

    /** Setter for {@link #deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread} */
    public void setDeadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread(
            boolean deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread) {
        this.deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread = deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread;
    }

    /** Getter for {@link #deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders} */
    public boolean isDeadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders() {
        return deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders;
    }

    /** Setter for {@link #deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders} */
    public void setDeadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders(
            boolean deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders) {
        this.deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders = deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders;
    }

    /** Getter for {@link #nextThreadPartOfDeadLock} */
    public DeadLockComponent getNextThreadPartOfDeadLock() {
        return nextThreadPartOfDeadLock;
    }

    /** Setter for {@link #nextThreadPartOfDeadLock} */
    public void setNextThreadPartOfDeadLock(DeadLockComponent nextThreadPartOfDeadLock) {
        this.nextThreadPartOfDeadLock = nextThreadPartOfDeadLock;
    }

    /** Getter for {@link #isFirstRepeatingThreadThatExplainsDeadLock} */
    public boolean isFirstRepeatingThreadThatExplainsDeadLock() {
        return isFirstRepeatingThreadThatExplainsDeadLock;
    }

    /** Setter for {@link #isFirstRepeatingThreadThatExplainsDeadLock} */
    public void setFirstRepeatingThreadThatExplainsDeadLock(boolean isFirstRepeatingThreadThatExplainsDeadLock) {
        this.isFirstRepeatingThreadThatExplainsDeadLock = isFirstRepeatingThreadThatExplainsDeadLock;
    }

    @Override
    public String toString() {
        return "\nDeadLockComponent [\n------->threadNotAbleToAccessResource="
                + threadNotAbleToAccessResource.getName() + "\n, stuckOnReleaseDeferredLock="
                + stuckOnReleaseDeferredLock
                + ", stuckThreadAcquiringLockForWriting=" + stuckThreadAcquiringLockForWriting
                + ", stuckThreadAcquiringLockForReading=" + stuckThreadAcquiringLockForReading
                + ",\n-------> cacheKeyThreadWantsToAcquireButCannotGet="
                + ConcurrencyUtil.SINGLETON
                .createToStringExplainingOwnedCacheKey(cacheKeyThreadWantsToAcquireButCannotGet)
                + "\n, deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread="
                + deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread
                + ", deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders="
                + deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders
                + ", isFirstRepeatingThreadThatExplainsDeadLock=" + isFirstRepeatingThreadThatExplainsDeadLock + "]\n";
    }
}
