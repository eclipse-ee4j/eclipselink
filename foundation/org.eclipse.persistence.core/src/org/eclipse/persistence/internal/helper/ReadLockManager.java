/*
 * Copyright (c) 2021, 2024 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.persistence.internal.helper.type.ReadLockAcquisitionMetadata;

/**
 * This essential class tracks metadata about threads acquiring and releasing read locks. Such information becomes
 * crucial during system deadlocks, allowing us to analyze the resources desired by all threads participating in the
 * EclipseLink concurrency layer.
 *
 * When deadlocks occur, we need insights into: - Threads stuck while attempting to acquire write locks. - Threads
 * engaged in object building and waiting within the release deferred lock process. - Threads that have acquired cache
 * keys for reading but have not yet released them.
 *
 * This metadata contributes to the big picture of understanding all activities within the concurrency layer.
 *
 * Refer to {@link org.eclipse.persistence.internal.helper.ConcurrencyManager#READ_LOCK_MANAGERS}, a static hash map
 * that provides details for each individual thread working in the concurrency layer, about the read locks it is
 * currently acquiring for reading.
 */
public class ReadLockManager {

    public static final int FIRST_INDEX_OF_COLLECTION = 0;

    /**
     * This vector of read locks is essentially a basket of cache keys (classes that extend concurrency manager) that a
     * specific thread has acquired for reading. We need to create this additional logic in order to be able to reduce
     * the readers count on a cache key whenever
     *
     */
    protected final Vector<ConcurrencyManager> readLocks = new Vector<>(1);

    /**
     * We have seen that the read locks vector we have is very insufficient. We keep it for now due to the tracing code
     * that is making use of it. But we also want to have a new tracing map with a lot more metadata
     */
    protected final Map<Long, List<ReadLockAcquisitionMetadata>> mapThreadToReadLockAcquisitionMetadata = new HashMap<>();

    /**
     * This is a field that will never be cleared. It will be getting ADDs if any problem is detected when we try remove
     * a read lock from our tracing.
     */
    protected final List<String> removeReadLockProblemsDetected = new ArrayList<>();

    private final Lock instanceLock  = new ReentrantLock();

    /**
     * add a concurrency manager as deferred locks to the DLM
     */
    public void addReadLock(ConcurrencyManager concurrencyManager) {
        instanceLock.lock();
        try {
            // (a) paranoia code - make sure we are not being provided a null concurrency manager ever
            if(concurrencyManager == null) {
                return ;
            }

            // (b) Basic variable initializaton
            final Thread currentThread = Thread.currentThread();
            final long currentThreadId = currentThread.getId();
            ReadLockAcquisitionMetadata readLockAcquisitionMetadata = ConcurrencyUtil.SINGLETON.createReadLockAcquisitionMetadata(concurrencyManager);

            // (c) update the read locks owned by the current thread
            this.readLocks.add(FIRST_INDEX_OF_COLLECTION, concurrencyManager);

            // (d) make sure our call to get the list of ReadLockAcquisitionMetadata  mapThreadToReadLockAcquisitionMetadata  for our current thread
            // is never null.
            if (!mapThreadToReadLockAcquisitionMetadata.containsKey(currentThreadId)) {
                List<ReadLockAcquisitionMetadata> newList = Collections.synchronizedList(new ArrayList<ReadLockAcquisitionMetadata>());
                mapThreadToReadLockAcquisitionMetadata.put(currentThreadId, newList);
            }
            List<ReadLockAcquisitionMetadata> acquiredReadLocksInCurrentTransactionList = mapThreadToReadLockAcquisitionMetadata.get(currentThreadId);

//            acquiredReadLocksInCurrentTransactionList.add(FIRST_INDEX_OF_COLLECTION, readLockAcquisitionMetadata);
            // NOTE:
            // This if different than null check here should really not be needed
            // This if check is a piece of paranoioa code.
            // However we are adding it here because of a Null pointer exception
            // org.eclipse.persistence.internal.helper.ReadLockManager.removeReadLock(ReadLockManager.java:84)
            // whereby the conclusion of that NPException is that somehow our
            // list of ReadLockAcquisitionMetadata was holding NULL entries within it
            // this if will certainly not hurt us
            if(readLockAcquisitionMetadata!=null) {
                acquiredReadLocksInCurrentTransactionList.add(FIRST_INDEX_OF_COLLECTION, readLockAcquisitionMetadata);
            }
        } finally {
            instanceLock.unlock();
        }
    }

    /**
     * During normal operation of the concurrency manager, each time a cache key is decrement in the number of readers,
     * so must the corresponding read lock manager of the thread be told let go of the cache key object acquired for
     * reading.
     *
     * @param concurrencyManager
     *            the concurrency cache key that is about to be decrement in number of readers.
     */
    public void removeReadLock(ConcurrencyManager concurrencyManager) {
        instanceLock.lock();
        try {
            // (a) paranoia code - make sure we are not being provided a null concurrency manager ever
            if(concurrencyManager == null) {
                return ;
            }
            // (b) cleanup the concurrencyManager out of the vector of readlocks
            this.readLocks.remove(concurrencyManager);

            // (c) basic variable initialization to work on the cleanup of the ReadLockAcquisitionMetadata
            final Thread currentThread = Thread.currentThread();
            final long currentThreadId = currentThread.getId();
            boolean readLockManagerHasTracingAboutAddedReadLocksForCurrentThread = mapThreadToReadLockAcquisitionMetadata.containsKey(currentThreadId);

            // (d) make sure our call to get the list of ReadLockAcquisitionMetadata  mapThreadToReadLockAcquisitionMetadata  for our current thread
            // is never null.
            if (!readLockManagerHasTracingAboutAddedReadLocksForCurrentThread) {
                String errorMessage = ConcurrencyUtil.SINGLETON.readLockManagerProblem02ReadLockManageHasNoEntriesForThread(concurrencyManager, currentThreadId);
                removeReadLockProblemsDetected.add(errorMessage);
                return;
            }

            // (d) Search for the ReadLockAcquisitionMetadata associated to the current input cache key
            List<ReadLockAcquisitionMetadata> readLocksAcquiredDuringCurrentThread = mapThreadToReadLockAcquisitionMetadata.get(currentThreadId);
            ReadLockAcquisitionMetadata readLockAquisitionMetadataToRemove = null;
            boolean anyNullReadLockAcquisitionMetadataEntryFound = false;
            for (ReadLockAcquisitionMetadata currentReadLockAcquisitionMetadata : readLocksAcquiredDuringCurrentThread) {
                // (d.i)
                if(currentReadLockAcquisitionMetadata == null) {
                    // note: it should be impossible to enter this code area here
                    // the addReadLock appears to be rock solid, we cannot see any way to add a null value into the list of ReadLockAcquisitionMetadata
                    // however we have had a report of a null pointer exception doing currentReadLockAcquisitionMetadata getCacheKeyWhoseNumberOfReadersThreadIsIncrementing
                    // at:
                    // org.eclipse.persistence.internal.helper.ReadLockManager.removeReadLock(ReadLockManager.java:84) ~[eclipselink-2.7.6-weblogic.jar
                    // which means a NPE in the:
                    // line of code ConcurrencyManager currentCacheKeyObjectToCheck = currentReadLockAcquisitionMetadata.getCacheKeyWhoseNumberOfReadersThreadIsIncrementing();
                    removeReadLockProblemsDetected.add("removeReadLock: we have detected the exisence of a NULL currentReadLockAcquisitionMetadata in the list readLocksAcquiredDuringCurrentThread. "
                            + " this should be impossible. The add logic code tries to make sure there is no scenario that would allow this to ever happen. ");
                    anyNullReadLockAcquisitionMetadataEntryFound = true;
                    // skip the next ReadLockAcquisitionMetadata entry in the list
                    continue;
                }

                // (d.ii) check if the currentReadLockAcquisitionMetadata is the one we are trying to remove out the list
                ConcurrencyManager currentCacheKeyObjectToCheck = currentReadLockAcquisitionMetadata.getCacheKeyWhoseNumberOfReadersThreadIsIncrementing();
                boolean dtoToRemoveFound = concurrencyManager.getConcurrencyManagerId() == currentCacheKeyObjectToCheck.getConcurrencyManagerId();
                if (dtoToRemoveFound) {
                    readLockAquisitionMetadataToRemove = currentReadLockAcquisitionMetadata;
                    break;
                }
            }

            // (e) Paranoia code - if our list of ReadLockAcquisitionMetadata seems to be poisoned with null entries, get rid of those
            if(anyNullReadLockAcquisitionMetadataEntryFound) {
                readLocksAcquiredDuringCurrentThread.removeAll(Collections.singleton(null));
                readLocks.removeAll(Collections.singleton(null));
            }

            // (f) If we find no ReadLockAcquisitionMetadata associated to the input ConcurrencyManager there is a bug somewhere
            // for each read lock in our vector of read locks we should also have added acquisition metadata
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
        } finally {
            instanceLock.unlock();
        }
    }

    /**
     * Return a set of the deferred locks
     */
    public List<ConcurrencyManager> getReadLocks() {
        instanceLock.lock();
        try {
            // Simply returning an unmodifiable list is insufficient.
            // We must provide a completely independent list to avoid issues when producing massive dumps.
            // These dumps are iterated over in parallel with backend threads that are acquiring and releasing read locks.
            return Collections.unmodifiableList(new ArrayList<>(readLocks));
        } finally {
            instanceLock.unlock();
        }
    }

    /**
     * Allow the concurrency manager to directly pump a message stating that there was a problem while decrementing the
     * number of readers.
     *
     * @param problemDetected
     *            the detected problem
     */
    public void addRemoveReadLockProblemsDetected(String problemDetected) {
        instanceLock.lock();
        try {
            removeReadLockProblemsDetected.add(problemDetected);
        } finally {
            instanceLock.unlock();
        }
    }

    /** Getter for {@link #mapThreadToReadLockAcquisitionMetadata} returns a deep clone */
    public Map<Long, List<ReadLockAcquisitionMetadata>> getMapThreadToReadLockAcquisitionMetadata() {
        instanceLock.lock();
        try {
            // We cannot simply return an unmodifiable map here. There are two reasons for this:
            // 1. The code consuming this unmodifiable map might be surprised by changes to the map itself caused by this
            // read lock manager.
            // If threads continue to acquire and release read locks, it could impact this object.
            // 2. Additionally, the list values contained in the map could also be affected by threads that are reading and
            // releasing read locks.
            // Our approach should be to provide the ConcurrencyUtil with a safe deep clone of the data structure for its
            // massive dumps.

            // (a) Let's start by creating an independent result map that the caller can safely iterate over.
            Map<Long, List<ReadLockAcquisitionMetadata>> resultMap = new HashMap<>();

            // (b) depp clone the data strcuture
            for (Entry<Long, List<ReadLockAcquisitionMetadata>> currentEntry : mapThreadToReadLockAcquisitionMetadata.entrySet()) {
                ArrayList<ReadLockAcquisitionMetadata> deepCopyOfCurrentListOfMetadata = new ArrayList<>(currentEntry.getValue());
                resultMap.put(currentEntry.getKey(), Collections.unmodifiableList(deepCopyOfCurrentListOfMetadata) );
            }

            // (c) even if our result map is deep clone of our internal sate
            // we still want to return it as unmodifable so that callers do not have the illusion
            // they are able to hack the state of the read lock manager from the outside.
            // If any code tris to manipulate the returned clone they should get a blow up to be dispelled of any illiusions
            return Collections.unmodifiableMap(resultMap);
        } finally {
            instanceLock.unlock();
        }
    }

    /** Getter for {@link #removeReadLockProblemsDetected} */
    public List<String> getRemoveReadLockProblemsDetected() {
        instanceLock.lock();
        try {
            return Collections.unmodifiableList(new ArrayList<>(removeReadLockProblemsDetected)) ;
        } finally {
            instanceLock.unlock();
        }
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
    public boolean isEmpty() {
        instanceLock.lock();
        try {
            return readLocks.isEmpty() && removeReadLockProblemsDetected.isEmpty();
        } finally {
            instanceLock.unlock();
        }
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
    public ReadLockManager clone() {
        instanceLock.lock();
        try {
            ReadLockManager clone = new ReadLockManager();
            clone.readLocks.addAll(this.readLocks);
            for (Map.Entry<Long, List<ReadLockAcquisitionMetadata>> currentEntry : this.mapThreadToReadLockAcquisitionMetadata.entrySet()) {
                Long key = currentEntry.getKey();
                List<ReadLockAcquisitionMetadata> value = currentEntry.getValue();
                clone.mapThreadToReadLockAcquisitionMetadata.put(key, new ArrayList<>(value));
            }
            clone.removeReadLockProblemsDetected.addAll(this.removeReadLockProblemsDetected);
            return clone;
        } finally {
            instanceLock.unlock();
        }
    }

}
