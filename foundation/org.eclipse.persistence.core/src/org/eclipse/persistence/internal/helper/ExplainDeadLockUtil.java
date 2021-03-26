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

import org.eclipse.persistence.internal.helper.type.CacheKeyToThreadRelationships;
import org.eclipse.persistence.internal.helper.type.ConcurrencyManagerState;
import org.eclipse.persistence.internal.helper.type.DeadLockComponent;
import org.eclipse.persistence.internal.helper.type.IsBuildObjectCompleteOutcome;
import org.eclipse.persistence.internal.localization.TraceLocalization;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

import java.io.StringWriter;
import java.util.*;

import static java.lang.String.format;

/**
 * The purpose of this class is to try explain the nature of a dead lock
 */
public class ExplainDeadLockUtil {

    public static final ExplainDeadLockUtil SINGLETON = new ExplainDeadLockUtil();

    private static final DeadLockComponent DEAD_LOCK_NOT_FOUND = null;

    private ExplainDeadLockUtil() {
    }

    /**
     * Given the concurrency manager state try to explain why we are facing a dead lock.
     *
     * @param concurrencyManagerState
     *            A clone we have assembled based on the concurrency manager and write lock manager state
     * @return A string that tries
     */
    public List<DeadLockComponent> explainPossibleDeadLockStartRecursion(ConcurrencyManagerState concurrencyManagerState) {
        // (a) start by initializing some basic variables
        final int maxNumberOfDifferentThreadsThatWillJustifyADeadLock = 5;
        final int recursionMaxDepth = maxNumberOfDifferentThreadsThatWillJustifyADeadLock + 1;
        final int initialRecursionDepth = 1;

        // (b) Initialize our candidate threads to justify the dead lock
        // we add all threads we know to be waiting to acquire write locks
        // stuck waiting for locks they deferred to be considered finished
        // stuck waiting for getting read locks
        final Set<Thread> threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain = new HashSet<>();
        final Set<Thread> allCandidateThreadsToExpand = new HashSet<>();
        allCandidateThreadsToExpand.addAll(concurrencyManagerState.getUnifiedMapOfThreadsStuckTryingToAcquireWriteLock().keySet());
        allCandidateThreadsToExpand.addAll(concurrencyManagerState.getSetThreadWaitingToReleaseDeferredLocksClone());
        allCandidateThreadsToExpand.addAll(concurrencyManagerState.getMapThreadToWaitOnAcquireReadLockClone().keySet());

        // (c) loop over each candidate thread and try to find a dad lock
        DeadLockComponent deadLockOutcome = null;
        for (Thread currentCandidateThreadPartOfTheDeadLock : allCandidateThreadsToExpand) {
            deadLockOutcome = recursiveExplainPossibleDeadLockStep01(
                    concurrencyManagerState, recursionMaxDepth, initialRecursionDepth,
                    currentCandidateThreadPartOfTheDeadLock, Collections.<Thread> emptyList(),
                    threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain);
            if (deadLockOutcome != null) {
                break;
            }
        }
        if (deadLockOutcome != null) {
            return createListExplainingDeadLock(deadLockOutcome);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * When the last (repeating thread) of a dead lock is discovered and we start the unwinding of the recursion, the
     * first DTO we create is flagged with
     * {@link org.eclipse.persistence.internal.helper.type.DeadLockComponent#isFirstRepeatingThreadThatExplainsDeadLock()}
     *
     * but it lacks all additional metadata. so we want enrich the missing metadata into this dto.
     *
     * @param deadLockExplanation
     *            The outcome of the explain dead lock algorithm. The last DTO on this data structure is lacking
     *            metadata and we want to get rid of it.
     *
     * @return return the dead lock explanation as a simple list that is easy to iterate over without going into
     *         infinite loops and where we get rid of the primal repeating thread of our recursion which is lacking
     *         metadata about the nature of the dead lock on the thread.
     */
    protected List<DeadLockComponent> createListExplainingDeadLock(
            DeadLockComponent deadLockExplanation) {
        // (a) Initialize some basic loop variables Loop over all the elements
        DeadLockComponent previousElement = null;
        DeadLockComponent currentElementToIterate = deadLockExplanation;

        // (b) initialize a helper map where we can quickly access the dead lock component dto by thread id
        Map<Thread, DeadLockComponent> helperMap = new HashMap<>();

        // (c) start our correction loop
        List<DeadLockComponent> deadLockAsSimpleList = new ArrayList<DeadLockComponent>();
        while (currentElementToIterate != null) {

            // (d) In this chaing of DTOs we built during our recursion
            // the first DTO we built at the highest depth of the recursion
            // is lacking all metadata. The DTO is created just stating the thread is being repeater therefore
            // we have a dead lock.
            // we simply get rid of this DTO primal DTO and use the equivalent DTO thta has all proper metadata
            boolean foundDtoRepresentingRepeatingThreadInADeadLock = currentElementToIterate.isFirstRepeatingThreadThatExplainsDeadLock();
            if (foundDtoRepresentingRepeatingThreadInADeadLock) {
                // (i) we load equivalent DTO in the hiearchy
                Thread repatedThreadInDeadLock = currentElementToIterate.getThreadNotAbleToAccessResource();
                DeadLockComponent equivalentDtoHavingAllProperMetadata = helperMap.get(repatedThreadInDeadLock);

                // (ii) we flag the equivalent DTO as being the thread that we see repeating in the dead lock
                equivalentDtoHavingAllProperMetadata.setFirstRepeatingThreadThatExplainsDeadLock(true);

                // (iii) We go to the previous element of the dead lock and instead of making it point to the primal DTO
                // we make it point to the equivalent DTO that has all the metadata
                // NOTE: the currentElementToIterate can
                if (previousElement != null) {
                    // when foundDtoRepresentingRepeatingThreadInADeadLock it should always be true
                    // that our previous element is not null
                    previousElement.setNextThreadPartOfDeadLock(currentElementToIterate);
                }

                // (iv) popuplate our result list
                deadLockAsSimpleList.add(equivalentDtoHavingAllProperMetadata);

            } else {
                // we are dealing with from depth 1 to N-1 (where n-1 is the deepest depth we went to before creating
                // the primal repeating thread DTo)
                deadLockAsSimpleList.add(currentElementToIterate);
                helperMap.put(currentElementToIterate.getThreadNotAbleToAccessResource(), currentElementToIterate);
            }

            // (e) Update the loop control variables
            previousElement = currentElementToIterate;
            currentElementToIterate = currentElementToIterate.getNextThreadPartOfDeadLock();
        }

        return deadLockAsSimpleList;
    }

    /**
     * The algorithm expands the current thread in depth. If the current thread is already part of the
     * threadPartOfCurrentDeadLockExpansion then we have found our dead lock. If we return null, we are empty handed ...
     * we cannot explain the dad lock.
     *
     * @param recursionMaxDepth
     *            how deep do we want our brute force algorithm to go. Probably 6 threads is good enough to discover
     *            most dead locks. Max depth should be set to number of threads we believe can logically form a dead
     *            lock + 1.
     * @param currentRecursionDepth
     *            how deep we are in the recursion
     * @param currentCandidateThreadPartOfTheDeadLock
     *            the current thread we want to expand
     * @param threadPartOfCurrentDeadLockExpansion
     *            a small optimization. If in the past we xplored expanding Thread A and now we are via a different
     *            routing trying to find a dead lock via thread A again we can immediately ignore expanding the thread.
     * @return NULL, if we reach a dead end without a dead lock. If a non result is returned means that the current
     *         thread is part of a dead lock identified..
     */
    protected DeadLockComponent recursiveExplainPossibleDeadLockStep01(
            final ConcurrencyManagerState concurrencyManagerStateDto,
            final int recursionMaxDepth,
            final int currentRecursionDepth, final Thread currentCandidateThreadPartOfTheDeadLock,
            List<Thread> threadPartOfCurrentDeadLockExpansion,
            Set<Thread> threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain) {

        // (a) Check some trivial stop cases
        // (i) check if we have found our dead lock
        // we can stop the recursion we have finally found a thread
        if (threadPartOfCurrentDeadLockExpansion.contains(currentCandidateThreadPartOfTheDeadLock)) {
            // implement logic to build the dto to be returned
            return new DeadLockComponent(currentCandidateThreadPartOfTheDeadLock);
        }
        // (ii) check If the current thread is one of those we have already expanded
        // in the past and reched no dead lock then there is no point in expanding
        // this thread a second time
        if (threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain.contains(currentCandidateThreadPartOfTheDeadLock)) {
            return DEAD_LOCK_NOT_FOUND;
        }

        // (b) If the current depth is not too deep we should at the current thread to basket of threads to not expand
        // again
        // E.g. if assume that all dead locks are justified by 5 threads or less then given a Thread A if we have
        // expanded the thread A
        // at depth 1 and came out with no result
        // we know that if now are going via Different Route expanding Thrad B and thread B wants to expand Thread A
        // there is no point in doing so again because thread A has already been expanded at route level
        // On the other hand if we assume a dead lock would be: Thread A, Thread B, Thread C and Thread A.
        // And we are currently are expanding Thread D, E, F, G, A, B (we do not find the dead lock because thread B is
        // being exapnded at depth 6 so the algorithm
        // is not allowed to go any further
        if (currentRecursionDepth == 1) {
            // make sure that if we ever ecounter this thread in the middle of an exapnsion
            // we do not waste time expanding it a second time when we expanded it at the root level
            // we went nowhere
            threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain.add(currentCandidateThreadPartOfTheDeadLock);
        }

        // (c) Finish the recursion when we reach the max depath allowed
        if (currentRecursionDepth >= recursionMaxDepth) {
            // if we say a dead lock can at most involve 6 different threads to justify it
            // then max depth would be seven. E.g. Thread 1,2,3,4,5,6, the7thThreadWouldBeoneOf[1-5]
            return DEAD_LOCK_NOT_FOUND;
        }

        // (d) We need to exapnd the current thread our trivial checks so far yielded nothing so now
        // we need to navigate in depth
        return recursiveExplainPossibleDeadLockStep02(concurrencyManagerStateDto, recursionMaxDepth,
                currentRecursionDepth,
                currentCandidateThreadPartOfTheDeadLock, threadPartOfCurrentDeadLockExpansion,
                threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain);

    }

   
    /**
     * Precondition the logic of {@link #recursiveExplainPossibleDeadLockStep01(ConcurrencyManagerState, int, int, Thread, List, Set)} has been
     * invoked and determined that we need to go deeper and expand the current thread.
     *
     * @param recursionMaxDepth
     *            the max depth the recursion is allowed to reach before deciding it is pointless to go any further. Max
     *            depth should be set to number of threads we believe can logically form a dead lock + 1.
     * @param currentRecursionDepth
     *            how deep we are in the recursion
     * @param currentCandidateThreadPartOfTheDeadLock
     *            the current thread we want to expand
     * @param threadPartOfCurrentDeadLockExpansion
     *            a small optimization. If in the past we xplored expanding Thread A and now we are via a different
     *            routing trying to find a dead lock via thread A again we can immediately ignore expanding the thread.
     * @return NULL, if we reach a dead end without a dead lock. If a non result is returned means that the current
     *         thread is part of a dead lock identified..
     */
    protected DeadLockComponent recursiveExplainPossibleDeadLockStep02(
            final ConcurrencyManagerState concurrencyManagerStateDto,
            final int recursionMaxDepth, final int currentRecursionDepth,
            final Thread currentCandidateThreadPartOfTheDeadLock, List<Thread> threadPartOfCurrentDeadLockExpansion,
            Set<Thread> threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain) {

        // We need to start by trying to understand what is the problem of the current thread
        // what resources does it want get and fail to get
        // (a) Are we facing the scenario that our thread wants a write lock but cannot get it
        Set<ConcurrencyManager> writeLocksCurrentThreadWantsToGetButFailsToGet = concurrencyManagerStateDto
                .getUnifiedMapOfThreadsStuckTryingToAcquireWriteLock().get(currentCandidateThreadPartOfTheDeadLock);
        if (writeLocksCurrentThreadWantsToGetButFailsToGet != null
                && !writeLocksCurrentThreadWantsToGetButFailsToGet.isEmpty()) {
            for (ConcurrencyManager cacheKeyCurrentThreadWantsForWritingButCannotGet : writeLocksCurrentThreadWantsToGetButFailsToGet) {
                DeadLockComponent currentResult = recursiveExplainPossibleDeadLockStep03ExpandBasedOnCacheKeyWantedForWriting(
                        concurrencyManagerStateDto, recursionMaxDepth, currentRecursionDepth,
                        currentCandidateThreadPartOfTheDeadLock, threadPartOfCurrentDeadLockExpansion,
                        threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
                        cacheKeyCurrentThreadWantsForWritingButCannotGet);
                if (currentResult != null) {
                    // dead lock can be explained via this expansion
                    return currentResult;
                }
            }
        }

        // (b) Deferred lock problem
        // could it be the current thread failed to acquire some locks and its waiting for other threads to finish
        boolean isCurrentThreadWaitingForDeferredLocksToBeResolved = concurrencyManagerStateDto
                .getSetThreadWaitingToReleaseDeferredLocksClone()
                .contains(currentCandidateThreadPartOfTheDeadLock);
        if(isCurrentThreadWaitingForDeferredLocksToBeResolved) {
            DeadLockComponent currentResult = recursiveExplainPossibleDeadLockStep04ExpandBasedOnThreadStuckOnReleaseDeferredLocks(
                    concurrencyManagerStateDto, recursionMaxDepth, currentRecursionDepth,
                    currentCandidateThreadPartOfTheDeadLock, threadPartOfCurrentDeadLockExpansion,
                    threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain);
            if (currentResult != null) {
                // dead lock can be explained via this expansion
                return currentResult;
            }
        }

        // (c) check if perhaps the current thread is strugling because it is trying to get some read lock and cannot
        // access it
        ConcurrencyManager cacheKeyCurrentThreadWantsForReadingButCannotGet = concurrencyManagerStateDto
                .getMapThreadToWaitOnAcquireReadLockClone().get(currentCandidateThreadPartOfTheDeadLock);
        if (cacheKeyCurrentThreadWantsForReadingButCannotGet != null) {
            DeadLockComponent currentResult = recursiveExplainPossibleDeadLockStep05ExpandBasedOnCacheKeyWantedForReading(
                    concurrencyManagerStateDto,
                    recursionMaxDepth, currentRecursionDepth, currentCandidateThreadPartOfTheDeadLock,
                    threadPartOfCurrentDeadLockExpansion, threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
                    cacheKeyCurrentThreadWantsForReadingButCannotGet);
            if (currentResult != null) {
                // dead lock can be explained via this expansion
                return currentResult;
            }
        }
        // This expansion did not allow us to discover any dead lock
        return DEAD_LOCK_NOT_FOUND;
    }

    /**
     * This a helper sanity check. Whenever we expand a threa we expect the thread must be starved fro some resource.
     *
     * @param concurrencyManagerStateDto
     *            the dto that holds the state of the cocurrency manager for our dead lock detection algorithm
     * @param currentCandidateThreadPartOfTheDeadLock
     *            the thread we are trying to evaluate
     * @return TRUE if we are aware of one or more resources that the current thread strives to acquire but cannot get
     *         false if we are clueless as to any desire for the thread.
     */
    protected boolean currentThreadIsKnownToBeWaitingForAnyResource(
            final ConcurrencyManagerState concurrencyManagerStateDto,
            final Thread currentCandidateThreadPartOfTheDeadLock) {
        boolean currentThreadExpansionEnteredAnyOfTheScenarios = false;
        Set<ConcurrencyManager> writeLocksCurrentThreadWantsToGetButFailsToGet = concurrencyManagerStateDto
                .getUnifiedMapOfThreadsStuckTryingToAcquireWriteLock().get(currentCandidateThreadPartOfTheDeadLock);
        if (writeLocksCurrentThreadWantsToGetButFailsToGet != null && !writeLocksCurrentThreadWantsToGetButFailsToGet.isEmpty()) {
            currentThreadExpansionEnteredAnyOfTheScenarios = true;
        }

        boolean isCurrentThreadWaitingForDeferredLocksToBeResolved = concurrencyManagerStateDto
                .getSetThreadWaitingToReleaseDeferredLocksClone()
                .contains(currentCandidateThreadPartOfTheDeadLock);
        if (isCurrentThreadWaitingForDeferredLocksToBeResolved) {
            currentThreadExpansionEnteredAnyOfTheScenarios = true;
        }

        ConcurrencyManager cacheKeyCurrentThreadWantsForReadingButCannotGet = concurrencyManagerStateDto
                .getMapThreadToWaitOnAcquireReadLockClone().get(currentCandidateThreadPartOfTheDeadLock);
        if (cacheKeyCurrentThreadWantsForReadingButCannotGet != null) {
            currentThreadExpansionEnteredAnyOfTheScenarios = true;
        }
        return currentThreadExpansionEnteredAnyOfTheScenarios;
    }

    /**
     * We are looking at thread that we know has registered itself as wanting to acquire a write lock and not managing
     * to make progress getting the write lock. We need to have a look at the {@link DeadLockComponent}
     * to try to find out who is owning the lock at the current point in time. We will onsider th posisble writers,
     * readers and in case of desperation the active thread on the cache key.
     *
     * @param concurrencyManagerStateDto
     *            state of concurrency manager
     * @param recursionMaxDepth
     *            how deep the recursion can go
     * @param currentRecursionDepth
     *            the current depeth
     * @param currentCandidateThreadPartOfTheDeadLock
     *            the current thread we are expanding and that may be involved in explaning a dead lock
     *
     * @param threadPartOfCurrentDeadLockExpansion
     *            the predecessor threads that are part of a dead lock expansion we are searching
     * @param threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain
     *            these are threads we already expanded at depth 1 and we know they took us nowhere. We just assume
     *            expanding these a second time is a waste of time.
     * @param cacheKeyCurrentThreadWantsForWritingButCannotGet
     *            this is the cache key we have identified as making a our candidate thread stuck and we want to explore
     *            who is owning this cache key
     * @return NULL if we get nowhere trying to find the dead lock otherwise a DTO explaining this thread and the
     *         successor threads that make the dead lock
     */
    protected DeadLockComponent recursiveExplainPossibleDeadLockStep03ExpandBasedOnCacheKeyWantedForWriting(
            final ConcurrencyManagerState concurrencyManagerStateDto,
            final int recursionMaxDepth, final int currentRecursionDepth,
            final Thread currentCandidateThreadPartOfTheDeadLock, List<Thread> threadPartOfCurrentDeadLockExpansion,
            Set<Thread> threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
            ConcurrencyManager cacheKeyCurrentThreadWantsForWritingButCannotGet) {

        // (c) Scenario 1: (current writer thread competing with other writers)
        // The current cache key is acquired for Writing by some other thread so it cannot be acquired for writing by
        // this thread we are trying to expand
        // NOTE: at most there should only be one thread on this list
        DeadLockComponent expansionResult = recursiveExplainPossibleDeadLockStep03Scenario01CurrentWriterVsOtherWritersWriter(concurrencyManagerStateDto,
                recursionMaxDepth, currentRecursionDepth, currentCandidateThreadPartOfTheDeadLock,
                threadPartOfCurrentDeadLockExpansion, threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
                cacheKeyCurrentThreadWantsForWritingButCannotGet);
        if(expansionResult != null) {
            return expansionResult;
        }

        // (c) Scenario 2: (current writer thread competing with other readers)
        // NOTE: The current cache key is perhaps owned by threads reading threads
        // making this writer not be able to acquire the cache key
        expansionResult = recursiveExplainPossibleDeadLockStep03Scenario02CurrentWriterVsOtherReader(
                concurrencyManagerStateDto, recursionMaxDepth, currentRecursionDepth,
                currentCandidateThreadPartOfTheDeadLock, threadPartOfCurrentDeadLockExpansion,
                threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
                cacheKeyCurrentThreadWantsForWritingButCannotGet);
        if (expansionResult != null) {
            return expansionResult;
        }

        // (d) Scenario 3:
        // We need to have a look at the active thread on the cache key as a last resort aspect to consider
        // Our primary strategy of getThreadsThatAcquiredActiveLock did not bare any fruits we will still consider the
        // cache key itself and check if it has any active thread
        expansionResult = recursiveExplainPossibleDeadLockStep03Scenario03CurrentWriterVsCacheKeyActiveThread(
                concurrencyManagerStateDto, recursionMaxDepth, currentRecursionDepth,
                currentCandidateThreadPartOfTheDeadLock, threadPartOfCurrentDeadLockExpansion,
                threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
                cacheKeyCurrentThreadWantsForWritingButCannotGet);
        if (expansionResult != null) {
            // perhaps we could log a warning here becuase if this scenario bares any fruits we do not really understand
            // what we are doing wrong in tracing the concurrency layer
            // we assume that each time a thread sets itself as the active thread on a cache key
            // we will wil trace this thread when it needs to acquire a lock and fails to get it
            return expansionResult;
        }

        // (e) We are out of ideas on how to expand further the current thread
        // we need to
        return DEAD_LOCK_NOT_FOUND;
    }

    /**
     * Expand the possibility of the current thread wanting to acquire for writing what some other already has acquired for writing.
     *
     */
    protected DeadLockComponent recursiveExplainPossibleDeadLockStep03Scenario01CurrentWriterVsOtherWritersWriter(
            final ConcurrencyManagerState concurrencyManagerStateDto,
            final int recursionMaxDepth, final int currentRecursionDepth,
            final Thread currentCandidateThreadPartOfTheDeadLock, List<Thread> threadPartOfCurrentDeadLockExpansion,
            Set<Thread> threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
            ConcurrencyManager cacheKeyCurrentThreadWantsForWritingButCannotGet) {

        boolean currentThreadWantsToAcquireForWriting = true;
        return recursiveExpansionCurrentThreadBeingBlockedByActiveWriters(concurrencyManagerStateDto, recursionMaxDepth,
                currentRecursionDepth, currentCandidateThreadPartOfTheDeadLock, threadPartOfCurrentDeadLockExpansion,
                threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
                cacheKeyCurrentThreadWantsForWritingButCannotGet, currentThreadWantsToAcquireForWriting);
    }

    /**
     * Expand the possibility of the current thread wanting to acquire for writing what some other already has acquired for writing.
     *
     */
    protected DeadLockComponent recursiveExplainPossibleDeadLockStep05Scenario01CurrentReaderVsOtherWriters(
            final ConcurrencyManagerState concurrencyManagerStateDto,
            final int recursionMaxDepth, final int currentRecursionDepth,
            final Thread currentCandidateThreadPartOfTheDeadLock, List<Thread> threadPartOfCurrentDeadLockExpansion,
            Set<Thread> threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
            ConcurrencyManager cacheKeyCurrentThreadWantsForWritingButCannotGet) {

        boolean currentThreadWantsToAcquireForWriting = true;
        return recursiveExpansionCurrentThreadBeingBlockedByActiveWriters(concurrencyManagerStateDto, recursionMaxDepth,
                currentRecursionDepth, currentCandidateThreadPartOfTheDeadLock, threadPartOfCurrentDeadLockExpansion,
                threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
                cacheKeyCurrentThreadWantsForWritingButCannotGet, currentThreadWantsToAcquireForWriting);
    }


    /**
     * Try to expand the current thread from the perspective that it wants a cache key that may be own for writing by a competitor thread.
     *
     * @param concurrencyManagerState
     *            state of the concurrency manager
     * @param recursionMaxDepth
     *            max allowed depth for recursion.
     * @param currentRecursionDepth
     *            the current recursion depth
     * @param currentCandidateThreadPartOfTheDeadLock
     *            the current thread we are expanding in dpeth
     * @param threadPartOfCurrentDeadLockExpansion
     *            the current thread of sets comrpising our dead lock expansion.
     * @param threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain
     *            threads we have expanded in the past and gave no fruits
     * @param cacheKeyThreadWantsToAcquireButCannotGet
     *            the cache key we know our current candidate wants and is not getting.
     * @param currentThreadWantsToAcquireForWriting
     *            a flag that tells us if our current candidate thread is strugling to acquire a lock with the purpose
     *            of writing or just for reading.
     * @return NULL if no dead lock is found. a value if a deadlock is found
     */
    protected DeadLockComponent recursiveExpansionCurrentThreadBeingBlockedByActiveWriters(
            final ConcurrencyManagerState concurrencyManagerState,
            final int recursionMaxDepth, final int currentRecursionDepth,
            final Thread currentCandidateThreadPartOfTheDeadLock, List<Thread> threadPartOfCurrentDeadLockExpansion,
            Set<Thread> threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
            ConcurrencyManager cacheKeyThreadWantsToAcquireButCannotGet,
            boolean currentThreadWantsToAcquireForWriting) {
        // (a) we start by finding out the dto that explains what threads are having some sort of involvement with the
        // cache key our thread wants to acquire
        CacheKeyToThreadRelationships cacheKeyToThreadRelationships = concurrencyManagerState
                .getMapOfCacheKeyToDtosExplainingThreadExpectationsOnCacheKey()
                .get(cacheKeyThreadWantsToAcquireButCannotGet);

        // (b) Start by preparing some basic immutable variables to go deeper one recursion level
        List<Thread> nexThreadPartOfCurrentDeadLockExpansion = new ArrayList<>(
                threadPartOfCurrentDeadLockExpansion);
        nexThreadPartOfCurrentDeadLockExpansion.add(currentCandidateThreadPartOfTheDeadLock);
        nexThreadPartOfCurrentDeadLockExpansion = Collections.unmodifiableList(nexThreadPartOfCurrentDeadLockExpansion);
        final int nextRecursionDepth = currentRecursionDepth + 1;

        // (c) Scenario 1: (current writer thread competing with other writers)
        // The current cache key is acquired for Writing by some other thread so it cannot be acquired for writing by
        // this thread we are trying to expand
        // NOTE: at most there should only be one thread on this list
        List<Thread> threadsThatHaveCurrentCacheKeyAsAnActiveLock = cacheKeyToThreadRelationships.getThreadsThatAcquiredActiveLock();
        for (Thread nextCandidateThreadPartOfTheDeadLock : threadsThatHaveCurrentCacheKeyAsAnActiveLock) {
            boolean isDifferentThread = !nextCandidateThreadPartOfTheDeadLock
                    .equals(currentCandidateThreadPartOfTheDeadLock);
            if (isDifferentThread) {

                // (i) Go deeper into the recursion expanding the next thread
                DeadLockComponent expansionResult = recursiveExplainPossibleDeadLockStep01(
                        concurrencyManagerState, recursionMaxDepth,
                        // go one level and thread deeper hunting for a dead lock
                        nextRecursionDepth, nextCandidateThreadPartOfTheDeadLock,
                        nexThreadPartOfCurrentDeadLockExpansion,
                        threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain);

                // (ii) check if we have found our dead lock
                if (expansionResult != null) {
                    if(currentThreadWantsToAcquireForWriting) {
                        return deadLockFoundCreateConcurrencyManagerStateWriterThreadCouldNotAcquireWriteLock(
                                expansionResult, currentCandidateThreadPartOfTheDeadLock,
                                cacheKeyThreadWantsToAcquireButCannotGet);
                    }  else {
                        return deadLockFoundCreateConcurrencyManagerStateReaderThreadCouldNotAcquireWriteLock(
                                expansionResult, currentCandidateThreadPartOfTheDeadLock,
                                cacheKeyThreadWantsToAcquireButCannotGet);
                    }

                }
            }
        }
        return DEAD_LOCK_NOT_FOUND;
    }


    /**
     * Expand the recursion exploring the possibility that the reason the current thread cannot acquire the cache key is
     * because there are readers on the cache key.
     *
     * @param concurrencyManagerState
     * @param recursionMaxDepth
     * @param currentRecursionDepth
     * @param currentCandidateThreadPartOfTheDeadLock
     * @param threadPartOfCurrentDeadLockExpansion
     * @param threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain
     * @param cacheKeyCurrentThreadWantsForWritingButCannotGet
     * @return NULL if dead lock not discovered. Otherwise dead lock component justifying the deadlock.
     */
    protected DeadLockComponent recursiveExplainPossibleDeadLockStep03Scenario02CurrentWriterVsOtherReader(
            final ConcurrencyManagerState concurrencyManagerState,
            final int recursionMaxDepth, final int currentRecursionDepth,
            final Thread currentCandidateThreadPartOfTheDeadLock, List<Thread> threadPartOfCurrentDeadLockExpansion,
            Set<Thread> threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
            ConcurrencyManager cacheKeyCurrentThreadWantsForWritingButCannotGet) {

        // (a) we start by finding out the dto that explains what threads are having some sort of involvement with the
        // cache key our thread wants to acquire
        CacheKeyToThreadRelationships cacheKeyToThreadRelationships = concurrencyManagerState
                .getMapOfCacheKeyToDtosExplainingThreadExpectationsOnCacheKey()
                .get(cacheKeyCurrentThreadWantsForWritingButCannotGet);

        // (b) Start by preparing some basic immutable variables to go deeper one recursion level
        List<Thread> nexThreadPartOfCurrentDeadLockExpansion = new ArrayList<>(
                threadPartOfCurrentDeadLockExpansion);
        nexThreadPartOfCurrentDeadLockExpansion.add(currentCandidateThreadPartOfTheDeadLock);
        nexThreadPartOfCurrentDeadLockExpansion = Collections.unmodifiableList(nexThreadPartOfCurrentDeadLockExpansion);
        final int nextRecursionDepth = currentRecursionDepth + 1;


        // (c) Scenario 2: (current writer thread competing with other readers)
        // NOTE: The current cache key is perhaps owned by threads reading threads
        // making this writer not be able to acquire the cache key
        List<Thread> threadsThatHaveCurrentCacheKeyAsAReadLockLock = cacheKeyToThreadRelationships
                .getThreadsThatAcquiredReadLock();
        for (Thread nextCandidateThreadPartOfTheDeadLock : threadsThatHaveCurrentCacheKeyAsAReadLockLock) {
            boolean isDifferentThread = !nextCandidateThreadPartOfTheDeadLock
                    .equals(currentCandidateThreadPartOfTheDeadLock);
            if(isDifferentThread) {

                // (i) Go deeper into the recursion expanding the next thread
                DeadLockComponent expansionResult = recursiveExplainPossibleDeadLockStep01(
                        concurrencyManagerState, recursionMaxDepth,
                        // go one level and thread deeper hunting for a dead lock
                        nextRecursionDepth, nextCandidateThreadPartOfTheDeadLock,
                        nexThreadPartOfCurrentDeadLockExpansion,
                        threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain);

                // (ii) check if we have found our dead lock
                if (expansionResult != null) {
                    return deadLockFoundCreateConcurrencyManagerStateWriterThreadCouldNotAcquireWriteLock(
                            expansionResult, currentCandidateThreadPartOfTheDeadLock,
                            cacheKeyCurrentThreadWantsForWritingButCannotGet);
                }
            }
        }
        return DEAD_LOCK_NOT_FOUND;
    }

    /**
     * In scenario 3 is when we start considering the possbility our data for detecting the dead lock is not ok or the
     * cache is corrupted. We consider quite simply the thread that is flagged as the active thread on the cache key
     * since this is the thread owning the cache key. However if we call for this scenario it means we did not find in
     * our DTO any reader or writer owning the thread. So if indeed the cache key our thread is trying to acquire has an
     * active thread and we do not know what this thread is currently doing - since the thread should either be in our
     * basket of writers or in our basket of readers, we are stuck.
     *
     *
     * @param concurrencyManagerStateDto
     * @param recursionMaxDepth
     * @param currentRecursionDepth
     * @param currentCandidateThreadPartOfTheDeadLock
     * @param threadPartOfCurrentDeadLockExpansion
     * @param threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain
     * @param cacheKeyCurrentThreadWantsForWritingButCannotGet
     * @return NULL if looking at the active thread of the wanted cache key does not bare any fruits, otherwise the dead
     *         lock component object are returned.
     */
    protected DeadLockComponent recursiveExplainPossibleDeadLockStep03Scenario03CurrentWriterVsCacheKeyActiveThread(
            final ConcurrencyManagerState concurrencyManagerStateDto,
            final int recursionMaxDepth, final int currentRecursionDepth,
            final Thread currentCandidateThreadPartOfTheDeadLock, List<Thread> threadPartOfCurrentDeadLockExpansion,
            Set<Thread> threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
            ConcurrencyManager cacheKeyCurrentThreadWantsForWritingButCannotGet) {
        boolean currentThreadWantsToAcquireForWriting = true;
        return recursiveExpansionCurrentThreadBeingBlockedByActiveThreadOnCacheKey(concurrencyManagerStateDto, recursionMaxDepth, currentRecursionDepth, currentCandidateThreadPartOfTheDeadLock, threadPartOfCurrentDeadLockExpansion,
                threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
                cacheKeyCurrentThreadWantsForWritingButCannotGet, currentThreadWantsToAcquireForWriting);
    }

    /**
     * Same as
     * {@link #recursiveExplainPossibleDeadLockStep03Scenario03CurrentWriterVsCacheKeyActiveThread(ConcurrencyManagerState, int, int, Thread, List, Set, ConcurrencyManager)}
     *
     * but in this case our candidate thread is trying to get the cache key with the purpose of READING and not for
     * writing.
     *
     * @param concurrencyManagerStateDto
     * @param recursionMaxDepth
     * @param currentRecursionDepth
     * @param currentCandidateThreadPartOfTheDeadLock
     * @param threadPartOfCurrentDeadLockExpansion
     * @param threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain
     * @param cacheKeyCurrentThreadWantsForWritingButCannotGet
     * @return
     */
    protected DeadLockComponent recursiveExplainPossibleDeadLockStep05Scenario02CurrentReaderVsCacheKeyActiveThread(
            final ConcurrencyManagerState concurrencyManagerStateDto,
            final int recursionMaxDepth, final int currentRecursionDepth,
            final Thread currentCandidateThreadPartOfTheDeadLock, List<Thread> threadPartOfCurrentDeadLockExpansion,
            Set<Thread> threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
            ConcurrencyManager cacheKeyCurrentThreadWantsForWritingButCannotGet) {
        boolean currentThreadWantsToAcquireForWriting = false;
        return recursiveExpansionCurrentThreadBeingBlockedByActiveThreadOnCacheKey(concurrencyManagerStateDto,
                recursionMaxDepth, currentRecursionDepth, currentCandidateThreadPartOfTheDeadLock,
                threadPartOfCurrentDeadLockExpansion, threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
                cacheKeyCurrentThreadWantsForWritingButCannotGet, currentThreadWantsToAcquireForWriting);
    }

    /**
     * Try to expand the current thread from the perspective that it wants a cache key that may be own for writing by a
     * competitor thread.
     *
     * @param concurrencyManagerState
     *            state of the concurrency manager
     * @param recursionMaxDepth
     *            max allowed depth for recursion.
     * @param currentRecursionDepth
     *            the current recursion depth
     * @param currentCandidateThreadPartOfTheDeadLock
     *            the current thread we are expanding in dpeth
     * @param threadPartOfCurrentDeadLockExpansion
     *            the current thread of sets comrpising our dead lock expansion.
     * @param threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain
     *            threads we have expanded in the past and gave no fruits
     * @param cacheKeyThreadWantsToAcquireButCannotGet
     *            the cache key we know our current candidate wants and is not getting.
     * @param currentThreadWantsToAcquireForWriting
     *            a flag that tells us if our current candidate thread is strugling to acquire a lock with the purpose
     *            of writing or just for reading.
     * @return NULL if no dead lock is found. a value if a deadlock is found
     */
    protected DeadLockComponent recursiveExpansionCurrentThreadBeingBlockedByActiveThreadOnCacheKey(
            final ConcurrencyManagerState concurrencyManagerState,
            final int recursionMaxDepth, final int currentRecursionDepth,
            final Thread currentCandidateThreadPartOfTheDeadLock, List<Thread> threadPartOfCurrentDeadLockExpansion,
            Set<Thread> threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
            ConcurrencyManager cacheKeyThreadWantsToAcquireButCannotGet,
            boolean currentThreadWantsToAcquireForWriting) {
        // (a) we start by finding out the dto that explains what threads are having some sort of involvement with the
        // cache key our thread wants to acquire
        CacheKeyToThreadRelationships cacheKeyToThreadRelationships = concurrencyManagerState
                .getMapOfCacheKeyToDtosExplainingThreadExpectationsOnCacheKey()
                .get(cacheKeyThreadWantsToAcquireButCannotGet);

        // (b) Start by preparing some basic immutable variables to go deeper one recursion level
        List<Thread> nexThreadPartOfCurrentDeadLockExpansion = new ArrayList<>(
                threadPartOfCurrentDeadLockExpansion);
        nexThreadPartOfCurrentDeadLockExpansion.add(currentCandidateThreadPartOfTheDeadLock);
        nexThreadPartOfCurrentDeadLockExpansion = Collections.unmodifiableList(nexThreadPartOfCurrentDeadLockExpansion);
        final int nextRecursionDepth = currentRecursionDepth + 1;
        List<Thread> threadsThatHaveCurrentCacheKeyAsAnActiveLock = cacheKeyToThreadRelationships
                .getThreadsThatAcquiredActiveLock();
        List<Thread> threadsThatHaveCurrentCacheKeyAsAReadLockLock = cacheKeyToThreadRelationships
                .getThreadsThatAcquiredReadLock();

        // (c) Scenario 3:
        // We need to have a look at the active thread on the cache key as a last resort aspect to consider
        // Our primary strategy of getThreadsThatAcquiredActiveLock did not bare any fruits we will still consider the
        // cache key itself and check if it has any active thread
        Thread activeThreadOnCacheKey = cacheKeyThreadWantsToAcquireButCannotGet.getActiveThread();
        boolean cacheKeyHasActiveThreadWeDidNotAnalyze =
                // the hey is being owned by somebody
                activeThreadOnCacheKey != null
                        // that somebody is not the thread we are expanding in this iteration
                        && !activeThreadOnCacheKey.equals(currentCandidateThreadPartOfTheDeadLock)
                        // that thread is not any of the threads we have already tried expanding during this expansion
                        // logic
                        && !(threadsThatHaveCurrentCacheKeyAsAnActiveLock.contains(activeThreadOnCacheKey)
                                || threadsThatHaveCurrentCacheKeyAsAReadLockLock.contains(activeThreadOnCacheKey));
        if (cacheKeyHasActiveThreadWeDidNotAnalyze) {
            // NOTE:
            // here we have abit of a problem we are facing the following scenario we know our current Thread A wants to
            // acquire the cachke key B
            // and we now know as well that cache key B is being owned by Thread B
            // But our CacheKeyToThreadRelationships was not aware that Thread B was owning owning
            // cache key B for writing
            // this could be something similar to the scenario where we have seen a dead lock where the Lock at the
            // center of the dead lock
            // had as an active thread a thread that was no longer working...
            //

            // (i) We are afraid of cache corruptionso we do a small sanity to check to try to understand if we are
            // "tracking" this new active
            // thread and are aware of any resources it needs
            Thread nextCandidateThreadPartOfTheDeadLock = activeThreadOnCacheKey;
            boolean sanityCheckToDoKnowOfAnyResourcesNeededByNextThreadToExpand = currentThreadIsKnownToBeWaitingForAnyResource(
                    concurrencyManagerState, nextCandidateThreadPartOfTheDeadLock);
            if (!sanityCheckToDoKnowOfAnyResourcesNeededByNextThreadToExpand) {
                // We are clueless - perhaps we should return here a DTO saying we might have just found our dead lock
                // this cache key has an active thread that seems to not be tracked by our
                // ConcurrencyManagerState
                //
                StringWriter writer = new StringWriter();
                writer.write(TraceLocalization.buildMessage("explain_dead_lock_util_current_thread_blocked_active_thread_warning",
                        new Object[] {nextCandidateThreadPartOfTheDeadLock.getName(),
                        currentCandidateThreadPartOfTheDeadLock.getName(),
                        ConcurrencyUtil.SINGLETON.createToStringExplainingOwnedCacheKey(
                                cacheKeyThreadWantsToAcquireButCannotGet)}));
                AbstractSessionLog.getLog().log(SessionLog.WARNING, SessionLog.CACHE, writer.toString(), new Object[] {}, false);
                return DEAD_LOCK_NOT_FOUND;
            } else {
                // The active thread on the cache key is needing some resources we are tracing
                // so that means that the active thread is probably also stuck itself
                // we want to go forward with the recursive expansion

                // (i) Go deeper into the recursion expanding the next thread
                DeadLockComponent expansionResult = recursiveExplainPossibleDeadLockStep01(
                        concurrencyManagerState, recursionMaxDepth,
                        // go one level and thread deeper hunting for a dead lock
                        nextRecursionDepth, nextCandidateThreadPartOfTheDeadLock,
                        nexThreadPartOfCurrentDeadLockExpansion,
                        threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain);

                // (ii) check if we have found our dead lock
                if (expansionResult != null) {
                    if (currentThreadWantsToAcquireForWriting) {
                        return deadLockFoundCreateConcurrencyManagerStateWriterThreadCouldNotAcquireWriteLock(
                                expansionResult, currentCandidateThreadPartOfTheDeadLock,
                                cacheKeyThreadWantsToAcquireButCannotGet);
                    } else {
                        return deadLockFoundCreateConcurrencyManagerStateReaderThreadCouldNotAcquireWriteLock(
                                expansionResult, currentCandidateThreadPartOfTheDeadLock,
                                cacheKeyThreadWantsToAcquireButCannotGet);
                    }
                }
            }

        }
        return DEAD_LOCK_NOT_FOUND;
    }



    /**
     * When a thead cannot move forward due to having deferred cache keys, it means that the thread could not go as deep
     * as it wanted during object building and hda to defer making some parts of the object. The thread is stuck because
     * the parts of the object it could not build are apparently not finished either.
     *
     * @param concurrencyManagerStateDto
     * @param recursionMaxDepth
     * @param currentRecursionDepth
     * @param currentCandidateThreadPartOfTheDeadLock
     * @param threadPartOfCurrentDeadLockExpansion
     * @param threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain
     * @return NULL if we are able to explain a dead lock via expanding this the current candidate thread Otherwiser a
     *         DTO component that explains the the dad lock
     */
    protected DeadLockComponent recursiveExplainPossibleDeadLockStep04ExpandBasedOnThreadStuckOnReleaseDeferredLocks(
            final ConcurrencyManagerState concurrencyManagerStateDto,
            final int recursionMaxDepth, final int currentRecursionDepth,
            final Thread currentCandidateThreadPartOfTheDeadLock, List<Thread> threadPartOfCurrentDeadLockExpansion,
            Set<Thread> threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain) {

        // (a) Run the auxiliary algorithm that can explain why a thread stuck in the release deferred locks
        // is not able to make progress.
        // To understand why we do this one would need to have a good look at the concurrenyc manager implementation
        // at the logic that of the logic of the releaseDeferredLock method
        IsBuildObjectCompleteOutcome result = isBuildObjectOnThreadComplete(concurrencyManagerStateDto, currentCandidateThreadPartOfTheDeadLock,  new IdentityHashMap());

        // (b) Our expectation is that the result of the step above is always different than null
        // after all if this candidate thread is stuck trying to release deferred locks there must be an explanation for it not making progress
        // the only case where it would make sense for this to be null is if the current candidate is actually making progress and
        // was stuck for only a short period
        if(result == null) {
            StringWriter writer = new StringWriter();
            writer.write(TraceLocalization.buildMessage("explain_dead_lock_util_thread_stuck_deferred_locks", new Object[] {currentCandidateThreadPartOfTheDeadLock.getName()}));
            AbstractSessionLog.getLog().log(SessionLog.WARNING, SessionLog.CACHE, writer.toString(), new Object[] {}, false);
            return DEAD_LOCK_NOT_FOUND;
        }

        // (c) At this point we have an idea of what thread is causing harm to us
        // so we will now try to expand the thread that is harming us from progressing further

        // (i) Start by preparing some basic immutable variables to go deeper one recursion level
        List<Thread> nexThreadPartOfCurrentDeadLockExpansion = new ArrayList<>(threadPartOfCurrentDeadLockExpansion);
        nexThreadPartOfCurrentDeadLockExpansion.add(currentCandidateThreadPartOfTheDeadLock);
        nexThreadPartOfCurrentDeadLockExpansion = Collections.unmodifiableList(nexThreadPartOfCurrentDeadLockExpansion);
        final int nextRecursionDepth = currentRecursionDepth + 1;
        final Thread nextCandidateThreadPartOfTheDeadLock = result.getThreadBlockingTheDeferringThreadFromFinishing();
        final ConcurrencyManager cacheKeyBlockingIsBuildObjectComplete = result.getCacheKeyOwnedByBlockingThread();

        // (i) Go deeper into the recursion expanding the next thread
        DeadLockComponent expansionResult = recursiveExplainPossibleDeadLockStep01(
                concurrencyManagerStateDto, recursionMaxDepth,
                // go one level and thread deeper hunting for a dead lock
                nextRecursionDepth, nextCandidateThreadPartOfTheDeadLock, nexThreadPartOfCurrentDeadLockExpansion,
                threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain);

        // (ii) If the thread we expanded when we went deeper leads to a dead lock explanation
        // continue unwinding the dead lock explanation
        if(expansionResult != null) {
            return deadLockFoundCreateConcurrencyManagerStateDeferredThreadCouldNotAcquireWriteLock(
                    expansionResult, currentCandidateThreadPartOfTheDeadLock, cacheKeyBlockingIsBuildObjectComplete);
        }

        // (iii) We reached a dead end we need to continue expanding other candidate threads
        return DEAD_LOCK_NOT_FOUND;
    }

    /**
     * In this case have a thread that wants to acquire for reading a cache key but it does not manage to acquire it
     * because the cache key is being owned by somebody else. So we need to see what the writer of this cache key is
     * doing.
     *
     * @param concurrencyManagerStateDto
     * @param recursionMaxDepth
     * @param currentRecursionDepth
     * @param currentCandidateThreadPartOfTheDeadLock
     * @param threadPartOfCurrentDeadLockExpansion
     * @param threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain
     * @param cacheKeyCurrentThreadWantsForReadingButCannotGet
     * @return
     */
    protected DeadLockComponent recursiveExplainPossibleDeadLockStep05ExpandBasedOnCacheKeyWantedForReading(
            final ConcurrencyManagerState concurrencyManagerStateDto,
            final int recursionMaxDepth, final int currentRecursionDepth,
            final Thread currentCandidateThreadPartOfTheDeadLock, List<Thread> threadPartOfCurrentDeadLockExpansion,
            Set<Thread> threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
            ConcurrencyManager cacheKeyCurrentThreadWantsForReadingButCannotGet) {


        // (c) Scenario 1: (current writer thread competing with other writers)
        // The current cache key is acquired for Writing by some other thread so it cannot be acquired for writing by
        // this thread we are trying to expand
        // NOTE: at most there should only be one thread on this list
        DeadLockComponent expansionResult = recursiveExplainPossibleDeadLockStep05Scenario01CurrentReaderVsOtherWriters(concurrencyManagerStateDto,
                recursionMaxDepth, currentRecursionDepth, currentCandidateThreadPartOfTheDeadLock,
                threadPartOfCurrentDeadLockExpansion, threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
                cacheKeyCurrentThreadWantsForReadingButCannotGet);
        if(expansionResult != null) {
            return expansionResult;
        }


        // (d) Scenario 2:
        // We need to have a look at the active thread on the cache key as a last resort aspect to consider
        // Our primary strategy of getThreadsThatAcquiredActiveLock did not bare any fruits we will still consider the
        // cache key itself and check if it has any active thread
        expansionResult = recursiveExplainPossibleDeadLockStep05Scenario02CurrentReaderVsCacheKeyActiveThread(
                concurrencyManagerStateDto, recursionMaxDepth, currentRecursionDepth,
                currentCandidateThreadPartOfTheDeadLock, threadPartOfCurrentDeadLockExpansion,
                threadsAlreadyExpandedInThePastThatWeDoNotWantToExpandAgain,
                cacheKeyCurrentThreadWantsForReadingButCannotGet);
        if (expansionResult != null) {
            // perhaps we could log a warning here becuase if this scenario bares any fruits we do not really understand
            // what we are doing wrong in tracing the concurrency layer
            // we assume that each time a thread sets itself as the active thread on a cache key
            // we will wil trace this thread when it needs to acquire a lock and fails to get it
            return expansionResult;
        }

        // (e) We are out of ideas on how to expand further the current thread
        // we need to
        return DEAD_LOCK_NOT_FOUND;
    }

    /**
     * This method is nothing more than copy paste code from the algorithm
     *
     * {@link ConcurrencyManager#isBuildObjectOnThreadComplete(Thread, Map, List, boolean)}
     *
     * We re-write this code to instead of returning true/false return an actual DTO object that can allow our dead lock
     * explanation algorithm to identify the next thread to expand to explain the dead lock.
     *
     * @param concurrencyManagerStateDto
     *            our representation of a cloned ste of the concurrency manager and write lock manager
     * @param thread
     *            the current thread to explore which might have deferred locks. At level 1 of the recursion the thread
     *            is certain to have deferred locks.
     * @param recursiveSet
     *            this is an hash map holding the threads in the current recusion algorithm that have been expanded
     *            already to avoid expanding more than once the same thread and going into an infinite dead lock.
     *
     */
    public static IsBuildObjectCompleteOutcome isBuildObjectOnThreadComplete(
            final ConcurrencyManagerState concurrencyManagerStateDto, Thread thread,
            Map recursiveSet) {
        if (recursiveSet.containsKey(thread)) {
            // if the thread we are consider as we go deeper in the recursion is thread in an upper stack of the
            // recursion
            // we can just return true since a thread A can not be responsible for getting itself stuck
            // simply return true and focus the attention on other threads not yet part of the recursive set
            // see if any of these is causing a blockage
            // return true
            return IsBuildObjectCompleteOutcome.BUILD_OBJECT_IS_COMPLETE_TRUE;
        }
        recursiveSet.put(thread, thread);

        // DeferredLockManager lockManager = getDeferredLockManager(thread);
        DeferredLockManager lockManager = concurrencyManagerStateDto.getDeferredLockManagerMapClone().get(thread);
        if (lockManager == null) {
            // return true
            return IsBuildObjectCompleteOutcome.BUILD_OBJECT_IS_COMPLETE_TRUE;
        }

        Vector deferredLocks = lockManager.getDeferredLocks();
        for (Enumeration deferredLocksEnum = deferredLocks.elements(); deferredLocksEnum.hasMoreElements();) {
            ConcurrencyManager deferedLock = (ConcurrencyManager) deferredLocksEnum.nextElement();
            Thread activeThread = null;
            if (deferedLock.isAcquired()) {
                activeThread = deferedLock.getActiveThread();

                // the active thread may be set to null at anypoint
                // if added for CR 2330
                if (activeThread != null) {
                    // DeferredLockManager currentLockManager = getDeferredLockManager(activeThread);
                    DeferredLockManager currentLockManager = concurrencyManagerStateDto.getDeferredLockManagerMapClone()
                            .get(thread);
                    if (currentLockManager == null) {
                        // return false;

                        return new IsBuildObjectCompleteOutcome(activeThread, deferedLock);
                    } else if (currentLockManager.isThreadComplete()) {
                        activeThread = deferedLock.getActiveThread();
                        // The lock may suddenly finish and no longer have an active thread.
                        if (activeThread != null) {
                            IsBuildObjectCompleteOutcome recursiveOutcome = isBuildObjectOnThreadComplete(
                                    concurrencyManagerStateDto, activeThread, recursiveSet);
                            if (recursiveOutcome != null) {
                                return new IsBuildObjectCompleteOutcome(activeThread, deferedLock);
                            }
                        }
                    } else {
                        return new IsBuildObjectCompleteOutcome(activeThread, deferedLock);
                    }
                }
            }
        }
        return IsBuildObjectCompleteOutcome.BUILD_OBJECT_IS_COMPLETE_TRUE;
    }

    // Create DTO Components when a dead lock is discovered

    /**
     * Create a dead lock dto component.
     *
     * @param nextThreadPartOfDeadLock
     *            the dto component of a dead lock that comes from a depper recursion expansion
     * @param threadNotAbleToAccessResource
     *            the thread at the current depth that is part of the dead lock
     * @param cacheKeyThreadWantsToAcquireButCannotGet
     *            the cache key that the thread wanted to acquire for writing
     * @return the DTO representing a component part of a dead lock
     */
    protected DeadLockComponent deadLockFoundCreateConcurrencyManagerStateWriterThreadCouldNotAcquireWriteLock(
            final DeadLockComponent nextThreadPartOfDeadLock, Thread threadNotAbleToAccessResource,
            ConcurrencyManager cacheKeyThreadWantsToAcquireButCannotGet) {
        boolean stuckOnReleaseDeferredLock = false;
        boolean stuckThreadAcquiringLockForWriting = true;
        boolean stuckThreadAcquiringLockForReading = false;
        boolean deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread = false;
        boolean deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders = false;
        return new DeadLockComponent(threadNotAbleToAccessResource, stuckOnReleaseDeferredLock,
                stuckThreadAcquiringLockForWriting, stuckThreadAcquiringLockForReading,
                cacheKeyThreadWantsToAcquireButCannotGet, deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread,
                deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders, nextThreadPartOfDeadLock);
    }

    /**
     * Dto component participating in a dead lock.
     *
     * @param nextThreadPartOfDeadLock
     *            the thread that was participating in the dead lock as we went deeper in the recursion.
     * @param threadNotAbleToAccessResource
     *            the thread at the current depth that is part of the dead lock
     * @param cacheKeyThreadWantsToAcquireButCannotGet
     *            the cache key that the thread wanted to acquire for writing
     * @return the DTO representing a component part of a dead lock
     */
    protected DeadLockComponent deadLockFoundCreateConcurrencyManagerStateReaderThreadCouldNotAcquireWriteLock(
            final DeadLockComponent nextThreadPartOfDeadLock, Thread threadNotAbleToAccessResource,
            ConcurrencyManager cacheKeyThreadWantsToAcquireButCannotGet) {
        boolean stuckOnReleaseDeferredLock = false;
        boolean stuckThreadAcquiringLockForWriting = false;
        boolean stuckThreadAcquiringLockForReading = true;
        boolean deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread = false;
        boolean deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders = false;
        return new DeadLockComponent(threadNotAbleToAccessResource, stuckOnReleaseDeferredLock,
                stuckThreadAcquiringLockForWriting, stuckThreadAcquiringLockForReading,
                cacheKeyThreadWantsToAcquireButCannotGet, deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread,
                deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders, nextThreadPartOfDeadLock);
    }

    /**
     * Create a deadlock component for a thread known to be stuck trying to release deferred locks.
     *
     * @param nextThreadPartOfDeadLock
     *
     * @param nextThreadPartOfDeadLock
     *            the thread that was participating in the dead lock as we went deeper in the recursion.
     * @param threadNotAbleToAccessResource
     *            the thread at the current depth that is part of the dead lock
     * @param cacheKeyBlockingIsBuildObjectComplete
     *            this is a cache key that is not necessarily desired by the blocked thread, but which is recrusively
     *            blocking the thread stuck in {@code isBuildObjectcomplete} logic. This is a bit complicated to
     *            explain, but essentially when a thread cannot acquire write lock it sometimes can defer on the lock.
     *            Later to finish object building the thread will be waiting to make sure that locks it deferred are
     *            considered to be all finished. When deferred lock is not finished it could be cause much deeper some
     *            other secondary object could not be built due to some cache key not accessible.. Therefore the
     *            cacheKeyBlockingIsBuildObjectComplete might be quite deep and far away from the cache key our thread
     *            had to defer... It is an element blocking our thread from finishing.
     * @return the DTO representing a component part of a dead lock
     */
    protected DeadLockComponent deadLockFoundCreateConcurrencyManagerStateDeferredThreadCouldNotAcquireWriteLock(
            final DeadLockComponent nextThreadPartOfDeadLock, Thread threadNotAbleToAccessResource,
            ConcurrencyManager cacheKeyBlockingIsBuildObjectComplete) {
        boolean stuckOnReleaseDeferredLock = true;
        boolean stuckThreadAcquiringLockForWriting = false;
        boolean stuckThreadAcquiringLockForReading = false;
        boolean deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread = false;
        boolean deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders = false;
        return new DeadLockComponent(threadNotAbleToAccessResource, stuckOnReleaseDeferredLock,
                stuckThreadAcquiringLockForWriting, stuckThreadAcquiringLockForReading,
                cacheKeyBlockingIsBuildObjectComplete, deadLockPotentiallyCausedByCacheKeyWithCorruptedActiveThread,
                deadLockPotentiallyCausedByCacheKeyWithCorruptedNumberOfReaders, nextThreadPartOfDeadLock);
    }
}
