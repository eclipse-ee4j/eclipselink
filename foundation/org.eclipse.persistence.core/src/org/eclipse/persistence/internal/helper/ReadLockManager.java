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
package org.eclipse.persistence.internal.helper;

import org.eclipse.persistence.internal.helper.type.ReadLockAcquisitionMetadata;

import java.util.*;

public class ReadLockManager {

    public static final int FIRST_INDEX_OF_COLLECTION = 0;

    /**
     * This vector of read locks is essentially a basket of cache keys (classes that extend concurrency manager) that a
     * specific thread has acquired for reading. We need to create this additional logic in order to be able to reduce
     * the readers count on a cache key whenever
     *
     */
    private final Vector<ConcurrencyManager> readLocks = new Vector<>(1);

    /**
     * We have seen that the read locks vector we have is very insufficient. We keep it for now due to the tracing code
     * that is making use of it. But we also want to have a new tracing map with a lot more metadata
     */
    private final Map<Long, List<ReadLockAcquisitionMetadata>> mapThreadToReadLockAcquisitionMetadata = new HashMap<>();

    /**
     * This is a field that will never be cleared. It will be getting ADDs if any problem is detected when we try remove
     * a read lock from our tracing.
     */
    private final List<String> removeReadLockProblemsDetected = new ArrayList<>();

    /**
     * add a concurrency manager as deferred locks to the DLM
     */
    public synchronized void addReadLock(ConcurrencyManager concurrencyManager) {
        final Thread currentThread = Thread.currentThread();
        final long currentThreadId = currentThread.getId();
        ReadLockAcquisitionMetadata readLockAcquisitionMetadata = ConcurrencyUtil.SINGLETON.createReadLockAcquisitionMetadata(concurrencyManager);

        this.readLocks.add(FIRST_INDEX_OF_COLLECTION, concurrencyManager);
        if(!mapThreadToReadLockAcquisitionMetadata.containsKey(currentThreadId)) {
            List<ReadLockAcquisitionMetadata> newList = Collections.synchronizedList(new ArrayList<ReadLockAcquisitionMetadata>());
            mapThreadToReadLockAcquisitionMetadata.put(currentThreadId, newList );
        }
        List<ReadLockAcquisitionMetadata> acquiredReadLocksInCurrentTransactionList = mapThreadToReadLockAcquisitionMetadata.get(currentThreadId);
        acquiredReadLocksInCurrentTransactionList.add(FIRST_INDEX_OF_COLLECTION, readLockAcquisitionMetadata);
    }

    /**
     * During normal operation of the concurrency manager, each time a cache key is decrement in the number of readers,
     * so must the corresponding read lock manager of the thread be told let go of the cache key object acquired for
     * reading.
     *
     * @param concurrencyManager
     *            the concurrency cache key that is about to be decrement in number of readers.
     */
    public synchronized void removeReadLock(ConcurrencyManager concurrencyManager) {
        final Thread currentThread = Thread.currentThread();
        final long currentThreadId = currentThread.getId();
        boolean readLockManagerHasTracingAboutAddedReadLocksForCurrentThread = mapThreadToReadLockAcquisitionMetadata.containsKey(currentThreadId);

        if (!readLockManagerHasTracingAboutAddedReadLocksForCurrentThread) {
            String errorMessage = ConcurrencyUtil.SINGLETON.readLockManagerProblem02ReadLockManageHasNoEntriesForThread(concurrencyManager, currentThreadId);
            removeReadLockProblemsDetected.add(errorMessage);
            return;
        }

        List<ReadLockAcquisitionMetadata> readLocksAcquiredDuringCurrentThread = mapThreadToReadLockAcquisitionMetadata.get(currentThreadId);
        ReadLockAcquisitionMetadata readLockAquisitionMetadataToRemove = null;
        for (ReadLockAcquisitionMetadata currentReadLockAcquisitionMetadata : readLocksAcquiredDuringCurrentThread) {
            ConcurrencyManager currentCacheKeyObjectToCheck = currentReadLockAcquisitionMetadata.getCacheKeyWhoseNumberOfReadersThreadIsIncrementing();
            boolean dtoToRemoveFound = concurrencyManager.getConcurrencyManagerId() == currentCacheKeyObjectToCheck.getConcurrencyManagerId();
            if (dtoToRemoveFound) {
                readLockAquisitionMetadataToRemove = currentReadLockAcquisitionMetadata;
                break;
            }
        }

        if (readLockAquisitionMetadataToRemove == null) {
            String errorMessage = ConcurrencyUtil.SINGLETON.readLockManagerProblem03ReadLockManageHasNoEntriesForThread(concurrencyManager, currentThreadId);
            removeReadLockProblemsDetected.add(errorMessage);
            return;
        }
        this.readLocks.remove(concurrencyManager);
        readLocksAcquiredDuringCurrentThread.remove(readLockAquisitionMetadataToRemove);

        if (readLocksAcquiredDuringCurrentThread.isEmpty()) {
            mapThreadToReadLockAcquisitionMetadata.remove(currentThreadId);
        }
    }

    /**
     * Return a set of the deferred locks
     */
    public synchronized List<ConcurrencyManager> getReadLocks() {
        return Collections.unmodifiableList(readLocks);
    }

    /**
     * Allow the concurrency manager to directly pump a message stating that there was a problem while decrementing the
     * number of readers.
     *
     * @param problemDetected
     *            the detected problem
     */
    public synchronized void addRemoveReadLockProblemsDetected(String problemDetected) {
        removeReadLockProblemsDetected.add(problemDetected);
    }

    /** Getter for {@link #mapThreadToReadLockAcquisitionMetadata} */
    public Map<Long, List<ReadLockAcquisitionMetadata>> getMapThreadToReadLockAcquisitionMetadata() {
        return mapThreadToReadLockAcquisitionMetadata;
    }

    /** Getter for {@link #removeReadLockProblemsDetected} */
    public List<String> getRemoveReadLockProblemsDetected() {
        return removeReadLockProblemsDetected;
    }

    /**
     * True if the tracing the data on the object has been completely removed. If this is the case it is perfectly fine
     * to remove the read lock manager from from the hash map of Thread To tis ReadLockManager Tracing.
     *
     * @return true if the current read lock manger contains no information about acquired locks that were never
     *         released or any errors detected while attempting to remove a cache key. If there is any error detected or
     *         any read lock acquired in the tracing we definitely do not want this object instance to be thrown out
     *         from our main tracing. It is probably revealing problems in read lock acquisition and released.
     */
    public synchronized boolean isEmpty() {
        return readLocks.isEmpty() && removeReadLockProblemsDetected.isEmpty();
    }

    /**
     * Create a new instance {@link ReadLockManager} that is in all regards
     * equal to the current instance.
     *
     * <P> USE CASE: <br>
     * This method is meant to be used by algorithms
     * that want to dump a snapshot of the current state of the system
     * or to go about doing
     */
    @SuppressWarnings("unchecked")
    @Override
    public synchronized ReadLockManager clone() {
        ReadLockManager clone = new ReadLockManager();
        clone.readLocks.addAll(this.readLocks);
        for (Map.Entry<Long, List<ReadLockAcquisitionMetadata>> currentEntry : this.mapThreadToReadLockAcquisitionMetadata.entrySet()) {
            Long key = currentEntry.getKey();
            List<ReadLockAcquisitionMetadata> value = currentEntry.getValue();
            clone.mapThreadToReadLockAcquisitionMetadata.put(key, new ArrayList<>(value));
        }
        clone.removeReadLockProblemsDetected.addAll(this.removeReadLockProblemsDetected);
        return clone;
    }

}
