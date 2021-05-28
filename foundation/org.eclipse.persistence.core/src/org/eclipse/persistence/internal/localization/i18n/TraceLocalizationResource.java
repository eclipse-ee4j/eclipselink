/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2017, 2021 IBM Corporation. All rights reserved.
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
//     09/13/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     11/07/2017 - Dalia Abo Sheasha
//       - 526957 : Split the logging and trace messages
//     05/11/2020-2.7.0 Jody Grassel
//       - 538296: Wrong month is returned if OffsetDateTime is used in JPA 2.2 code
package org.eclipse.persistence.internal.localization.i18n;

import java.util.ListResourceBundle;

/**
 * English ResourceBundle for TraceLocalization messages.  Traces are only localized.  They do not get translated.
 * These logs are picked up by AbstractSessionLog.getLog().log() when the level is below CONFIG=4 (FINE, FINER, FINEST, ALL)
 * @author Shannon Chen
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public class TraceLocalizationResource extends ListResourceBundle {
    static final Object[][] contents = {
        // FINEST, FINER and FINE level messages.
        { "login_successful", "{0} login successful" },
        { "logout_successful", "{0} logout successful" },
        { "acquire_unit_of_work_with_argument", "acquire unit of work: {0}" },
        { "external_transaction_has_begun_internally", "external transaction has begun internally" },
        { "external_transaction_has_committed_internally", "external transaction has committed internally" },
        { "initialize_all_identitymaps", "initialize all identitymaps" },
        { "initialize_identitymap", "initialize identitymap: {0}" },
        { "initialize_identitymaps", "initialize identitymaps" },
        { "external_transaction_has_rolled_back_internally", "external transaction has rolled back internally" },
        { "validate_cache", "validate cache." },
        { "cache_item_creation", "Entity ({0}) with Id ({1}) was stored in the cache by thread (Id: {2} Name: {3})" },
        { "cache_item_refresh", "Entity ({0}) with Id ({1}) was refreshed in the cache by thread (Id: {2} Name: {3})" },
        { "cache_item_removal", "Entity ({0}) with Id ({1}) was removed from the cache by thread (Id: {2} Name: {3})" },
        { "cache_item_invalidation", "Entity ({0}) with Id ({1}) was invalidated from the cache by thread (Id: {2} Name: {3})" },
        { "cache_class_invalidation", "Entities based on class ({0}) was invalidated from the cache by thread (Id: {1} Name: {2})" },
        { "cache_hit", "Cache hit for entity ({0}) with Id ({1})" },
        { "cache_miss", "Cache miss for entity ({0}) with Id ({1})" },
        { "stack_of_visited_objects_that_refer_to_the_corrupt_object", "stack of visited objects that refer to the corrupt object: {0}" },
        { "corrupt_object_referenced_through_mapping", "corrupt object referenced through mapping: {0}" },
        { "corrupt_object", "corrupt object: {0}" },
        { "begin_unit_of_work_flush", "begin unit of work flush" },
        { "end_unit_of_work_flush", "end unit of work flush" },
        { "begin_unit_of_work_commit", "begin unit of work commit" },
        { "end_unit_of_work_commit", "end unit of work commit" },
        { "resume_unit_of_work", "resume unit of work" },
        { "resuming_unit_of_work_from_failure", "resuming unit of work from failure" },
        { "release_unit_of_work", "release unit of work" },
        { "revert_unit_of_work", "revert unit of work" },
        { "validate_object_space", "validate object space." },
        { "execute_query", "Execute query {0}" },
        { "merge_clone", "Merge clone {0} " },
        { "merge_clone_with_references", "Merge clone with references {0}" },
        { "new_instance", "New instance {0}" },
        { "register_existing", "Register the existing object {0}" },
        { "register_new", "Register the new object {0}" },
        { "register_new_bean", "Register the new bean {0}" },
        { "register", "Register the object {0}" },
        { "revert", "Revert the object''s attributes {0}" },
        { "unregister", "Unregister the object {0}" },
        { "begin_batch_statements", "Begin batch statements" },
        { "end_batch_statements", "End Batch Statements" },
        { "query_column_meta_data_with_column", "query column meta data ({0}.{1}.{2}.{3})" },
        { "query_column_meta_data", "query table meta data ({0}.{1}.{2})" },
        { "reconnecting_to_external_connection_pool", "reconnecting to external connection pool" },
        { "connecting", "connecting({0})" },
        { "disconnect", "disconnect" },
        { "reconnecting", "reconnecting({0})" },
        { "begin_transaction", "begin transaction" },
        { "commit_transaction", "commit transaction" },
        { "rollback_transaction", "rollback transaction" },
        { "adapter_result", "Adapter result: {0}" },
        { "data_access_result", "Data access result: {0}" },
        { "acquire_unit_of_work", "acquire unit of work" },
        { "JTS_register", "JTS register" },
        { "JTS_after_completion", "After JTS Completion" },
        { "JTS_before_completion", "Before JTS Completion" },
        { "JTS_begin", "Begin JTS transaction" },
        { "JTS_commit_with_argument", "JTS#commit({0})" },
        { "JTS_rollback", "Rollback JTS transaction." },
        { "JTS_commit", "Commit JTS transaction." },
        { "JTS_after_completion_with_argument", "After JTS Completion ({0})" },
        { "TX_beforeCompletion", "TX beforeCompletion callback, status={0}" },
        { "TX_afterCompletion", "TX afterCompletion callback, status={0}" },
        { "TX_bind", "TX binding to tx mgr, status={0}" },
        { "TX_begin", "TX beginTransaction, status={0}" },
        { "TX_beginningTxn", "TX Internally starting" },
        { "TX_commit", "TX commitTransaction, status={0}" },
        { "TX_committingTxn", "TX Internally committing" },
        { "TX_rollback", "TX rollbackTransaction, status={0}" },
        { "TX_rollingBackTxn", "TX Internally rolling back" },
        { "lock_writer_header", "Current object locks:" },
        { "lock_writer_footer", "End of locked objects." },
        { "active_thread", "Thread : {0}" },
        { "locked_object", "Locked Object : {0}" },
        { "depth", "Depth : {0}" },
        { "cachekey_released", "This thread is no longer holding the lock.  It must not be a blocking thread."},
        { "cache_thread_info", "Cached entity ({0}) with Id ({1}) was prepared and stored into cache by another thread (id: {2} name: {3}), than current thread (id: {4} name: {5})" },
        { "deferred_locks", "Deferred lock on : {0}" },
        { "deferred_locks_released", "All deferred locks for thread \"{0}\" have been released." },
        { "acquiring_deferred_lock", "Thread \"{1}\" has acquired a deferred lock on object : {0} in order to avoid deadlock." },
        { "dead_lock_encountered_on_write", "Thread \"{1}\" encountered deadlock when attempting to lock : {0}.  Entering deadlock avoidance algorithm." },
        { "dead_lock_encountered_on_write_no_cache_key", "Thread \"{2}\" encountered deadlock when attempting to lock object of class: {0} with PK {1}.  Entering deadlock avoidance algorithm." },
        { "concurrency_manager_release_locks_acquired_by_thread_1", "releaseAllLocksAcquiredByThread: Thread \"{0}\"  .The Lock manager is null. This might be an acquire operation. So not possible to lockManager.releaseActiveLocksOnThread(). Cache Key:  \"{1}\"" },
        { "concurrency_manager_release_locks_acquired_by_thread_2", "releaseAllLocksAcquiredByThread: Release active locks on Thread \"{0}\"" },
        { "concurrency_manager_build_object_thread_complete_1", "isBuildObjectComplete ExpandedThread NR  {0}: {1} \n" },
        { "concurrency_manager_build_object_thread_complete_2", "\nAll threads in this stack are doing object building and needed to defer on one or more cache keys.\n"
                + "The last thread has deferred lock on ac cache key that is acquired by thread that is not yet finished with its work. \n\n"},
        { "concurrency_manager_build_object_thread_complete_3", "finalDeferredLockCausingTrouble:  {0} \n"
                + " This cache key had to be deferred by the last thread on the recursive stack. The thread was ACQUIRED. \n"},
        { "concurrency_manager_build_object_thread_complete_4", "activeThreadOnTheCacheKey: {0}  \n"
                + " hasDeferredLockManager: {1} \n "
                + " This is the thread that has acquired the cache key and has been considered to not yet be finished with its business. \n"
                + " When hasDeferredLockManager is true it typically means this thread is doing object building. \n"
                + " When hasDeferredLockManager is false it might an object building thread or it could be a thread doing a commit and acquiring final locks to merge its objects with changesets look at the stack trace to understand. \n"},
        { "concurrency_manager_allow_concurrency_exception_fired_up", "allowConcurrencyExceptionToBeFiredUp: is set to FALSE."
                + " No any exception be fired to avoid the risk of aborting the current thread not being sufficient to resolve any dead lock."
                + " and leaving the system in a worth shape where aver 3 retries the business transaction is not re-attempted and the recovery of the system becomes complicated. "
                + " We are going with the option of letting the system freeze. " },
        { "concurrency_util_stuck_thread_tiny_log_cache_key", "Stuck thread problem: unique tiny message number ({0}) \n"
                + " The Thread [{1}]  appears to be stuck (possible dead lock ongoing). \n"
                + " The thread is working in the context of (CacheKey) = ({2}) . \n"
                + " The thread has been stuck for: ({3} ms) \n "
                + " Bellow we will describe the ActiveLocks, DeferredLocks and ReadLocks for this thread. " },
        { "concurrency_util_owned_cache_key_null", "ObjectNull. Most likely not yet in server session cache and in the process of being created."},
        { "concurrency_util_owned_cache_key_is_cache_key", "--- CacheKey  ({0}):  (primaryKey: {1}) (object: {2}) (object hash code: {3}) (cacheKeyClass: {4}) (cacheKey hash code: {5}) (current cache key owner/activeThread: {6}) (getNumberOfReaders: {7}) "
                + " (concurrencyManagerId: {8}) (concurrencyManagerCreationDate: {9})"
                + "  (totalNumberOfTimeCacheKeyAcquiredForReading:  {10}) "
                + " (totalNumberOfTimeCacheKeyReleasedForReading:  {11}) "
                + " (totalNumberOfTimeCacheKeyReleasedForReadingBlewUpExceptionDueToCacheKeyHavingReachedCounterZero:  {12})  "
                + "(depth: {13}) ---"},
        { "concurrency_util_owned_cache_key_is_not_cache_key", "--- ConcurrencyManager: (ConcurrencyManagerClass: {0} ) (ConcurrencyManagerToString: {1}) (current cache key owner/activeThread: {2}) (concurrencyManagerId: {3}) (concurrencyManagerCreationDate: {4}) "
                + "  (totalNumberOfTimeCacheKeyAcquiredForReading:  {5}) "
                + " (totalNumberOfTimeCacheKeyReleasedForReading:  {6}) "
                + " (totalNumberOfTimeCacheKeyReleasedForReadingBlewUpExceptionDueToCacheKeyHavingReachedCounterZero:  {7}) "
                + "(depth: {8}) ---"},
        { "concurrency_util_header_current_cache_key", "Summary current cache key of thread {0} "},
        { "concurrency_util_header_active_locks_owned_by_thread", "Summary of active locks owned by thread {0} "},
        { "concurrency_util_header_deferred_locks_owned_by_thread", "Summary of deferred locks (could not be acquired and cause thread to wait for object building to complete) of thread {0} "},
        { "concurrency_util_header_reader_locks_owned_by_thread", "Summary of read locks acquired by thread {0} "},
        { "concurrency_util_summary_active_locks_on_thread_1", "Listing of all ACTIVE Locks.\n"
                + "Thread Name: {0} \n"},
        { "concurrency_util_summary_active_locks_on_thread_2", "0 Active locks. The lockManager for this thread is null. \n\n"},
        { "concurrency_util_summary_active_locks_on_thread_3", "{0} Active locks.\n"},
        { "concurrency_util_summary_active_locks_on_thread_4", "Active lock nr: {0} , Active cache key: {1}\n\n"},
        { "concurrency_util_summary_deferred_locks_on_thread_1", "Listing of all DEFERRED Locks.\n"
                + "Thread Name: {0} \n"},
        { "concurrency_util_summary_deferred_locks_on_thread_2", "0 deferred locks. The lockManager for this thread is null. \n\n"},
        { "concurrency_util_summary_deferred_locks_on_thread_3", "{0} Deferred locks.\n"},
        { "concurrency_util_summary_deferred_locks_on_thread_4", "Deferred lock nr: {0} , Deferred cache key: {1}\n\n"},
        { "concurrency_util_summary_read_locks_on_thread_step001_1", "Listing of all READ Locks. Step 001 - sparse summary loop over all read locks acquired:\n"
                + "Thread Name: {0} \n"},
        { "concurrency_util_summary_read_locks_on_thread_step001_2", "0 read locks. The lockManager for this thread is null. \n\n"},
        { "concurrency_util_summary_read_locks_on_thread_step001_3", "{0} Read locks.\n"},
        { "concurrency_util_summary_read_locks_on_thread_step001_4", "Read lock nr: {0} , Read cache key: {1}\n\n"},
        { "concurrency_util_summary_read_locks_on_thread_step002_1", "\nListing of all READ Locks. Step 002 - fat-detailed information about all read locks acquired:"},
        { "concurrency_util_summary_read_locks_on_thread_step002_2", "Read locks acquired by thread: {0} with id: {1} never released.\n"
                + "number of never released read locks: {2}"},
        { "concurrency_util_summary_read_locks_on_thread_step002_3", "\nRead lock nr: {0} Read lock Cache Key: {1}"
                + "\nRead lock nr: {0} dateOfReadLockAcquisition: {2}"
                + "\nRead lock nr: {0} numberOfReadersOnCacheKeyBeforeIncrementingByOne: {3}"
                + "\nRead lock nr: {0} currentThreadStackTraceInformationCpuTimeCostMs: {4}"},
        { "concurrency_util_summary_read_locks_on_thread_step002_4", "\nRead lock nr: {0}  stackTraceInformation:  same as stack trace id: {1}"},
        { "concurrency_util_summary_read_locks_on_thread_step002_5", "\nRead lock nr: {0} Stack trace id: {1} Start"
                + "\nRead lock nr: {0} stackTraceInformation: {2}"
                + "\nRead lock nr: {0} Stack trace id: {1} End"},
        { "concurrency_util_summary_read_locks_on_thread_step002_6", "Read locks problems detected by thread: {0} during release read locks"
                + "\n{1} removeReadLockProblemsDetected."},
        { "concurrency_util_summary_read_locks_on_thread_step002_7", "\nRelease read lock problem nr: {0} \n   {1}"},
        { "concurrency_util_enrich_thread_dump", "enrichGenerateThreadDump: Failed to generate thread dump with error: {0} "},
        { "concurrency_util_enrich_thread_dump_thread_info_1", "\"{0}\" "
                + "\n   java.lang.Thread.State: {1}"},
        { "concurrency_util_enrich_thread_dump_thread_info_2", "\n        at {0}"},
        { "concurrency_util_create_information_thread_dump", "Concurrency manager - Page 01 start - thread dump about all threads at time of event\n {0}"
                + "\nConcurrency manager - Page 01 end - thread dump about all threads at time of event\n"},
        { "concurrency_util_create_information_all_threads_acquire_cache_keys_1", "Concurrency manager - Page 02 start - information about threads waiting to acquire (write/deferred) cache keys "
                + "\nTotal number of threads waiting to acquire lock: {0}\n\n"},
        { "concurrency_util_create_information_all_threads_acquire_cache_keys_2", "[currentThreadNumber: {0}] [ThreadName: {1}]: Waiting to acquire (write/deferred): {2}\n"},
        { "concurrency_util_create_information_all_threads_acquire_cache_keys_3", "It seems, that trace was produced by the THREADS_TO_FAIL_TO_ACQUIRE_CACHE_KEYS - - org.eclipse.persistence.internal.helper.WriteLockManager.acquireRequiredLocks(MergeManager, UnitOfWorkChangeSet)"},
        { "concurrency_util_create_information_all_threads_acquire_cache_keys_4", "[methodNameThatGotStuckWaitingToAcquire: {0}] \n"},
        { "concurrency_util_create_information_all_threads_acquire_cache_keys_5", "Concurrency manager - Page 02 end - information about threads waiting to acquire (write/deferred) cache keys\n"},
        { "concurrency_util_create_information_all_threads_acquire_read_cache_keys_1", "Concurrency manager - Page 03 start - information about threads waiting to acquire read cache keys "
                + "\nTotal number of threads waiting to acquire read locks: {0} \n\n"},
        { "concurrency_util_create_information_all_threads_acquire_read_cache_keys_2", "[currentThreadNumber: {0}] [ThreadName: {1} ]: Waiting to acquire (read lock): {2}\n"},
        { "concurrency_util_create_information_all_threads_acquire_read_cache_keys_3", "[methodNameThatGotStuckWaitingToAcquire: {0}]  \n"},
        { "concurrency_util_create_information_all_threads_acquire_read_cache_keys_4", "Concurrency manager - Page 03 end - information about threads waiting to acquire read cache keys\n"},
        { "concurrency_util_create_information_all_threads_release_deferred_locks_1", "Concurrency manager - Page 04 start - information about threads waiting on release deferred locks (waiting for other thread to finish building the objects deferred) "
                + "\nTotal number of threads waiting to acquire lock: {0} \n\n"},
        { "concurrency_util_create_information_all_threads_release_deferred_locks_2", "[currentThreadNumber: {0}] [ThreadName: {1} ]\n"},
        { "concurrency_util_create_information_all_threads_release_deferred_locks_3", "Concurrency manager - Page 04 end - information about threads waiting on release deferred locks (waiting for other thread to finish building the objects deferred)\n"},
        { "concurrency_util_create_information_all_resources_acquired_deferred_1", "Concurrency manager - Page 05 start (currentThreadNumber: {0} of totalNumberOfThreads: {1})  - detailed information about specific thread "
                + "\nThread: {2}"
                + "\nThreadWaitingToReleaseDeferredLocks: {3}\n"},
        { "concurrency_util_create_information_all_resources_acquired_deferred_2", " waitingOnAcquireWritingCacheKey: true  waiting to acquire writing: {0}\n"},
        { "concurrency_util_create_information_all_resources_acquired_deferred_3", " waitingOnAcquireWritingCacheKey: false\n"},
        { "concurrency_util_create_information_all_resources_acquired_deferred_4", " waitingOnAcquireReadCacheKey: true   waiting to acquire reading: {0}\n"},
        { "concurrency_util_create_information_all_resources_acquired_deferred_5", " waitingOnAcquireReadCacheKey: false\n"},
        { "concurrency_util_create_information_all_resources_acquired_deferred_6", " writeManagerThreadPrimaryKeysWithChangesToBeMerged: true"
                + "\n writeManagerThreadPrimaryKeysWithChangesToBeMerged list: {0}\n"},
        { "concurrency_util_create_information_all_resources_acquired_deferred_7", " writeManagerThreadPrimaryKeysWithChangesToBeMerged: false\n"},
        { "concurrency_util_create_information_all_resources_acquired_deferred_8", " waitingToReleaseDeferredLocksJustification: \n {0} \n"},
        { "concurrency_util_create_information_all_resources_acquired_deferred_9", " waitingToReleaseDeferredLocksJustification: information not available. \n"},
        { "concurrency_util_create_information_all_resources_acquired_deferred_10", "Concurrency manager - Page 05 end (currentThreadNumber: {0} of totalNumberOfThreads: {1})  - detailed information about specific thread\n"},
        { "concurrency_util_read_lock_manager_problem01", "Remove cache key from read lock manager problem 01:"
                + "\n The current thread: {0} is about to decrement the currentNumberOfReaders from: {1}  to decrementedNumberOfReaders {2} "
                + "\n  on the cache key: {3}"
                + "\n however the readLockManager for the current thread is null."
                + "\n This should never happen. If the readLockManager is null either the we have not incremented the number readers in the past. "
                + "\n Or we have removed all the cache keys of the readLockManager belonging to this thread making the map become empty and be removed. "
                + "\n It should never be the case that we are bout the decrement the count of readers and are not tracing the cache key are we about to decrement."
                + "\n Stack trace as we detect the problem: {4}"
                + "\n CurrentDate: {5}"},
        { "concurrency_util_read_lock_manager_problem02", "Remove cache key from read lock manager problem 02:"
                + "\n removeReadLock PROBLEM Detected - mapThreadToReadLockAcquisitionMetadata does not have have threadId  . "
                + "\n The following problem is taking place. The Thread: {0} is about to decrease the number of readers on the cacheKey: {1} "
                + "\n However this ReadLockManager currently has no entries on the mapThreadToReadLockAcquisitionMetadata associated to the ongoing threadId:  {2}"
                + "\n as result in terms of tracing we will simply ignore this call to removeReadLock for the current cache key. "
                + "\n Problem is taking place in Thread: {2} "
                + "\n Stack trace as we detect the problem: {3}"
                + "\n CurrentDate: {4}"},
        { "concurrency_util_read_lock_manager_problem03", "Remove cache key from read lock manager problem 03:"
                + "\n removeReadLock problem detected - mapThreadToReadLockAcquisitionMetadata.readLocksAcquiredDuringCurrentThread does not have have threadId  . "
                + "\n The following problem is taking place. The Thread: {0} is about to decrease the number of readers on the cacheKey: {1}"
                + "\n However this ReadLockManager currently has no entries on the mapThreadToReadLockAcquisitionMetadata.readLocksAcquiredDuringCurrentThread associated to the ongoing threadId:  {2}"
                + "\n as result in terms of tracing we will simply ignore this call to removeReadLock for the current cache key. "
                + "\n Problem is taking place in Thread: {2} "
                + "\n Stack trace as we detect the problem: {3}"
                + "\n CurrentDate: {4}"},
        { "concurrency_util_read_lock_acquisition_metadata", "No stack trace take. isAllowTakingStackTraceDuringReadLockAcquisition is false."},
        { "concurrency_util_dump_concurrency_manager_information_step02_01", "Start full concurrency manager state (massive) dump No: {0}\n"},
        { "concurrency_util_dump_concurrency_manager_information_step02_02", "End full concurrency manager state (massive) dump No: {0}\n"},
        { "concurrency_util_dump__dead_lock_explanation_01", "dumpDeadLockExplanationIfPossible code is buggy. The algorithm to try to explain dead locks should not break. Instead we have caught an exception"},
        { "concurrency_util_dump__dead_lock_explanation_02", "Concurrency manager - Page 07 start - dead lock explanation\n"},
        { "concurrency_util_dump__dead_lock_explanation_03", "We were not able to determine to determine a set of threads that went into dead lock."},
        { "concurrency_util_dump__dead_lock_explanation_04", "Dead  lock result is comprised by  {0} thread entries where one of them is repeated. \n"},
        { "concurrency_util_dump__dead_lock_explanation_05", "Dead lock participantNR: {0} "
                + "\n {1} \n\n"},
        { "concurrency_util_dump__dead_lock_explanation_06", "deadlock algorithm took {0} ms to try to find deadlock."
                + "\nConcurrency manager - Page 07 end - dead lock explanation\n"},
        { "concurrency_util_cache_keys_threads_making_use_cache_key_01", "Concurrency manager - Page 06 start - information about cache keys and threads needing them "
                + "\nTotal number of cacheKeys to describe: {0} \n\n"},
        { "concurrency_util_cache_keys_threads_making_use_cache_key_02", "-------------- [currentCacheKeyNumber: {0} of {1}]--------------\n"},
        { "concurrency_util_cache_keys_threads_making_use_cache_key_03", "[currentCacheKeyNumber: {0}] [CacheKey: {1} ]:"
                + "\n[currentCacheKeyNumber: {0}] threadsThatAcquiredActiveLock: {2}"
                + "\n[currentCacheKeyNumber: {0}] threadsThatAcquiredDeferredLock: {3}"
                + "\n[currentCacheKeyNumber: {0}] threadsThatAcquiredReadLock:  {4}"
                + "\n[currentCacheKeyNumber: {0}] threadsKnownToBeStuckTryingToAcquireLock:  {5}"
                + "\n[currentCacheKeyNumber: {0}] threadsKnownToBeStuckTryingToAcquireLockForReading:  {6}"
                + "\n[currentCacheKeyNumber: {0}] threads doing object building with root on this cache key (not yet supported)...\n\n"},
        { "concurrency_util_cache_keys_threads_making_use_cache_key_04", "Concurrency manager - Page 06 end - information about cache keys and threads needing them\n"},
        { "explain_dead_lock_util_current_thread_blocked_active_thread_warning", "recursiveExplainPossibleDeadLockStep03Scenario03CurrentWriterVsCacheKeyActiveThread: nextCandidateThreadPartOfTheDeadLock is Thread: {0}  "
                + " This thread appears to be blocking the thread: {1}  from progressing because it is set as the active thread on the cacheKey: {2} "},
        { "explain_dead_lock_util_thread_stuck_deferred_locks", "recursiveExplainPossibleDeadLockStep04ExpandBasedOnThreadStuckOnReleaseDeferredLocks: currentCandidateThreadPartOfTheDeadLock is Thread: {0}  "
                + " This thread appears to be stuck in the releaseDeferredLock logic. "
                + " But our hacked implementation of the  isBuildObjectOnThreadComplete was not able to explain what thread and cache key are recursively "
                + " stopping the candidate thread to make progress... We expect this code spot to never be invoked. "
                + " Either this thread made progress or if it continues to be stuck in the releaseDeferredLock "
                + " we most likely have an implementation bug somewhere. "},
        { "XML_call", "XML call" },
        { "XML_data_call", "XML data call" },
        { "XML_data_delete", "XML data delete" },
        { "XML_data_insert", "XML data insert" },
        { "XML_data_read", "XML data read" },
        { "XML_data_update", "XML data update" },
        { "XML_delete", "XML delete" },
        { "XML_existence_check", "XML existence check" },
        { "XML_insert", "XML insert" },
        { "XML_read_all", "XML read all" },
        { "XML_read", "XML read" },
        { "XML_update", "XML update" },
        { "write_BLOB", "Writing BLOB value(size = {0} bytes) through the locator to the table field: {1}" },
        { "write_CLOB", "Writing CLOB value(size = {0} bytes) through the locator to the table field: {1}" },
        { "assign_sequence", "assign sequence to the object ({0} -> {1})" },
        { "assign_return_row", "Assign return row {0}" },
        { "compare_failed", "Compare failed: {0}:{1}:{2}" },
        { "added_unmapped_field_to_returning_policy", "Added unmapped field {0} to ReturningPolicy of {1}" },
        { "field_for_unsupported_mapping_returned", "Returned field {0} specified in ReturningPolicy of {1} mapped with unsupported mapping" },
        { "received_updates_from_remote_server", "Received updates from Remote Server" },
        { "received_remote_connection_from", "Received remote connection from {0}" },
        { "applying_changeset_from_remote_server", "Applying changeset from remote server {0}" },
        { "change_from_remote_server_older_than_current_version", "Change from Remote Server is older than current Version for {0}: {1}" },
        { "current_version_much_older_than_change_from_remote_server", "Current Version is much older than change from remote server for {0}: {1}" },
        { "Merging_from_remote_server", "Merging {0}: {1} from remote server" },
        { "initializing_local_discovery_communication_socket", "Initializing local discovery communication socket" },
        { "place_local_remote_session_dispatcher_into_naming_service", "Place local remote session dispatcher into naming service" },
        { "connecting_to_other_sessions", "connecting to other sessions" },
        { "done", "Done" },
        { "getting_local_initial_context", "Getting local initial context" },
        { "received_connection_from", "Received connection from {0}" },
        { "sending_changeset_to_network", "Sending changeSet to network" },
        { "failed_to_reconnect_remote_connection", "Failed to reconnect the remote connection on error" },
        { "dropping_connection", "Dropping connection: {0}" },
        { "attempting_to_reconnect_to_JMS_service", "Attempting to reconnect to JMS service" },
        { "retreived_remote_message_from_JMS_topic", "Retreived remote message from JMS topic: {0}" },
        { "retreived_remote_message_from_jgroup_channel", "Retreived remote message from JGroups channel: {0}" },
        { "processing_topLink_remote_command", "Processing TopLink remote command" },
        { "JMS_exception_thrown", "JMSException thrown" },
        { "announcement_sent_from", "Announcement sent from {0}" },
        { "announcement_received_from", "Announcement received from {0}" },
        { "reconnect_to_jms", "Reconnect to the JMS topic name {0}" },
        { "sequencing_connected", "sequencing connected, state is {0}" },
        { "sequencing_connected_several_states", "sequencing connected, several states are used" },
        { "sequence_without_state", "sequence {0}: preallocation size {1}" },
        { "sequence_with_state", "sequence {0}: preallocation size {1}, state {2}" },
        { "sequencing_disconnected", "sequencing disconnected" },
        { "sequencing_localPreallocation", "local sequencing preallocation for {0}: objects: {1} , first: {2}, last: {3}" },
        { "sequencing_afterTransactionCommitted", "local sequencing preallocation is copied to preallocation after transaction commit" },
        { "sequencing_afterTransactionRolledBack", "local sequencing preallocation is discarded after transaction roll back" },
        { "sequencing_preallocation", "sequencing preallocation for {0}: objects: {1} , first: {2}, last: {3}" },
        { "starting_rcm", "Starting Remote Command Manager {0}" },
        { "stopping_rcm", "Stopping Remote Command Manager {0}" },
        { "initializing_discovery_resources", "Initializing discovery resources - group={0} port={1}" },
        { "sending_announcement", "Sending service announcement..." },
        { "register_local_connection_in_jndi", "Registering local connection in JNDI under name {0}" },
        { "register_local_connection_in_registry", "Registering local connection in RMIRegistry under name {0}" },
        { "context_props_for_remote_lookup", "Remote context properties: {0}" },
        { "looking_up_remote_conn_in_jndi", "Looking up remote connection in JNDI under name {0} at URL {1}" },
        { "looking_up_remote_conn_in_registry", "Looking up remote connection in RMIRegistry at {0}" },
        { "unable_to_look_up_remote_conn_in_jndi", "Unable to look up remote connection in JNDI under name {0} at URL {1}" },
        { "unable_to_look_up_remote_conn_in_registry", "Unable to look up remote connection in RMIRegistry under name {0}" },
        { "converting_to_toplink_command", "Converting {0} to TopLink Command format" },
        { "converting_to_user_command", "Converting {0} from TopLink Command format to user format" },
        { "executing_merge_changeset", "Executing MergeChangeSet command from {0}" },
        { "received_remote_command", "Received remote command {0} from {1}" },
        { "processing_internal_command", "Executing internal RCM command {0} from {1}" },
        { "processing_remote_command", "Executing command {0} from {1}" },
        { "sync_propagation", "Propagating command synchronously" },
        { "async_propagation", "Propagating command asynchronously" },
        { "propagate_command_to", "Propagating command {0} to {1}" },
        { "discovery_manager_active", "RCM Discovery Manager active" },
        { "discovery_manager_stopped", "RCM Discovery Manager stopped" },
        { "announcement_sent", "RCM service announcement sent out to cluster" },
        { "announcement_received", "RCM service announcement received from {0}" },
        { "creating_session_broker", "Creating session broker: {0}" },
        { "creating_database_session", "Creating database session: {0}" },
        { "creating_server_session", "Creating server session: {0}" },
        { "EJB_create", "Create EJB ({0}) " },
        { "EJB_find_all", "Find all EJB objects ({0})" },
        { "EJB_find_all_by_name", "Find all EJB objects by named query ({0})" },
        { "EJB_find_one", "Find one EJB object ({0})" },
        { "EJB_find_one_by_name", "Find one EJB object by named query ({0})" },
        { "EJB_load", "Load EJB" },
        { "EJB_remove", "Remove EJB ({0})" },
        { "EJB_store", "Store EJB ({0})" },
        { "error_in_preInvoke", "Error in preInvoke." },
        { "unable_to_load_generated_subclass", "Unable to load generated subclass: {0}" },
        { "WebLogic_10_Platform_serverSpecificRegisterMBeans_enter", "WebLogic_10_Platform.serverSpecificRegisterMBeans enter" },
        { "WebLogic_10_Platform_serverSpecificRegisterMBeans_return", "WebLogic_10_Platform.serverSpecificRegisterMBeans return" },
        { "executeFinder_query", "executeFinder query: {0}, {1}" },
        { "executeFinder_finder_execution_results", "executeFinder - finder execution results: {0}" },
        { "PM_initialize_enter", "PersistenceManager.initialize enter for {0}" },
        { "PM_initialize_return", "PersistenceManager.initialize return for {0}" },
        { "PM_preDeploy_enter", "PersistenceManager.preDeploy enter for {0}" },
        { "PM_preDeploy_return", "PersistenceManager.preDeploy return for {0}" },
        { "PM_postDeploy_enter", "PersistenceManager.postDeploy enter for {0}" },
        { "PM_postDeploy_return", "PersistenceManager.postDeploy return for {0}" },
        { "createEJB_call", "createEJB call: {0}" },
        { "createEJB_return", "createEJB return: {0}" },
        { "removeEJB_call", "removeEJB call: {0}" },
        { "invokeHomeMethod_call", "invokeHomeMethod call: {0}({1})" },
        { "invokeHomeMethod_return", "invokeHomeMethod return" },
        { "ProjectDeployment_undeploy_enter", "ProjectDeployment.undeploy enter" },
        { "ProjectDeployment_undeploy_return", "ProjectDeployment.undeploy return" },
        { "ProjectDeployment_configureDescriptor_enter", "ProjectDeployment.configureDescriptor enter: {0}" },
        { "ProjectDeployment_configureDescriptor_return", "ProjectDeployment.configureDescriptor return" },
        { "ProjectDeployment_configureDescriptors_enter", "ProjectDeployment.configureDescriptors enter" },
        { "ProjectDeployment_configureDescriptors_return", "ProjectDeployment.configureDescriptors return" },
        { "configuring_descriptor", "configuring descriptor: {0}, {1}" },
        { "concrete_class", "concrete class: {0}" },
        { "setting_ref_class_of_foreign_ref_mapping", "setting ref class of foreign ref mapping: {0}, {1}" },
        { "setting_ref_class_of_aggregate_mapping", "setting ref class of aggregate mapping: {0}, {1}" },
        { "desc_has_inheritance_policy", "Descriptor has inheritance policy: {0}" },
        { "one_time_initialization_of_ProjectDeployment", "one-time initialization of ProjectDeployment" },
        { "generateBeanSubclass_call", "generateBeanSubclass call: {0}" },
        { "remote_and_local_homes", "remote and local homes: {0}, {1}" },
        { "generateBeanSubclass_return", "generateBeanSubclass return: {0}" },
        { "error_in_startBusinessCall", "Error in startBusinessCall." },
        { "error_in_endLocalTx", "Error in endLocalTx." },
        { "EJB20_Project_Deployment_adjustDescriptorsForUOW_enter", "UOWChangeSetFlagCodeGenerator.adjustDescriptorForUOWFlag enter" },
        { "EJB20_Codegeneration_For_UOW_Change_Policy_enter", "UOWChangePolicyCodeGenerator.generateCodeForUOWChangePolicy enter" },
        { "OBJECTCHANGEPOLICY_TURNED_ON", "Change tracking turned on for: {0}" },
        { "PM_DescriptorContents", "********** PersistenceManager.getPMDescriptorContents()" },
        { "project_class_used", "The project class [{0}] is being used." },
        { "pessimistic_lock_bean", "prepare pessimistic locking for bean {0}" },
        { "changetracker_interface_not_implemented", "Class [{0}] for attribute [{1}] does not implement ChangeTracker interface. This class is being reverted to DeferredChangeDetectionPolicy." },
        { "changetracker_interface_not_implemented_non_cmp", "Class [{0}] is being reverted to DeferredChangeDetectionPolicy since the attribute [{1}] " + "is a non-cmp field but the owinging class does not implement ChangeTracker interface." },
        { "acquire_client_session_broker", "acquire client session broker" },
        { "releasing_client_session_broker", "releasing client session broker" },
        { "client_released", "client released" },
        { "client_acquired", "client acquired: {0}" },
        { "tracking_pl_object", "track pessimistic locked object {0} with UnitOfWork {1}" },
        { "instantiate_pl_relationship", "instantiate pessimistic locking relationship when relationship is accessed in a new transaction." },
        { "descriptor_xml_not_in_jar", "The descriptor file ({0}) is not found in jar({1}) file, no migration therefore will be performed for this jar." },
        { "pessimistic_locking_migrated", "The native CMP setting 'pessimistic-locking' on entity({0}) has been migrated and supported." },
        { "read_only_migrated", "The native CMP setting 'read-only' on entity({0}) has been migrated and supported." },
        { "call_timeout_migrated", "Oc4j native CMP setting 'time-out' on entity({0}) has been migrated and supported." },
        { "verifiy_columns_version_locking_migrated", "Optimistic setting 'Version' on 'verifiy-columns' in entity ({0}) has been migrated." },
        { "verifiy_columns_timestamp_locking_migrated", "Optimistic setting 'Timestamp' on 'verifiy-columns' in entity ({0}) has been migrated." },
        { "verifiy_columns_changedField_locking_migrated", "Optimistic setting 'Modify' on 'verifiy-columns' in entity ({0}) has been migrated." },
        { "order_database_operations_supported", "WLS native CMP setting 'order-database-operations' has been supported and migrated" },
        { "pattern_syntax_error", "Regular expression syntax error, exception is: {0}" },
        { "weaver_user_impl_change_tracking", "Weaving for change tracking not required for class [{0}] because it already implements the ChangeTracker interface."},
        { "weaver_found_field_lock", "Weaving for change tracking not enabled for class [{0}] because it uses field-based optimisitic locking."},
        { "weaver_processing_class", "Class [{0}] registered to be processed by weaver."},
        { "begin_weaving_class", "Begin weaver class transformer processing class [{0}]."},
        { "end_weaving_class", "End weaver class transformer processing class [{0}]."},
        { "transform_missing_class_details", "Missing class details for [{0}]."},
        { "transform_existing_class_bytes", "Using existing class bytes for [{0}]."},
        { "weaved_lazy", "Weaved lazy (ValueHolder indirection) [{0}]."},
        { "weaved_fetchgroups", "Weaved fetch groups (FetchGroupTracker) [{0}]."},
        { "weaved_changetracker", "Weaved change tracking (ChangeTracker) [{0}]."},
        { "weaved_persistenceentity", "Weaved persistence (PersistenceEntity) [{0}]."},
        { "weaved_rest", "Weaved REST [{0}]."},
        { "cmp_init_invoke_predeploy", "JavaSECMPInitializer - predeploying {0}."},
        { "cmp_init_register_transformer", "JavaSECMPInitializer - registering transformer for {0}."},
        { "cmp_init_tempLoader_created", "JavaSECMPInitializer - created temporary ClassLoader: {0}."},
        { "cmp_init_shouldOverrideLoadClassForCollectionMembers", "JavaSECMPInitializer - override load class for collection members: {0}."},
        { "cmp_loading_entities_using_loader", "JavaSECMPInitializer - loading entities using ClassLoader: {0}."},
        { "cmp_init_transformer_is_null", "JavaSECMPInitializer - transformer is null."},
        { "cmp_init_globalInstrumentation_is_null", "JavaSECMPInitializer - global instrumentation is null."},
        { "cmp_init_invoke_deploy", "JavaSECMPInitializer - deploying {0}."},
        { "cmp_init_completed_deploy", "JavaSECMPInitializer - completed deploy of {0}."},
        { "cmp_init_initialize", "JavaSECMPInitializer - initializing {0}."},
        { "cmp_init_initialize_from_main", "JavaSECMPInitializer - initializing from main."},
        { "cmp_init_initialize_from_agent", "JavaSECMPInitializer - initializing from agent."},
        { "validation_factory_not_initialized", "Bean Validation Factory was not initialized: [{0}]."},
        { "searching_for_default_mapping_file", "Searching for mapping file: [{0}] at root URL: [{1}]."},
        { "found_default_mapping_file", "Found mapping file: [{0}] at root URL: [{1}]."},

        { "detect_server_platform","Detected server platform: {0}."},
        { "configured_server_platform", "Configured server platform: {0}"},
        { "dbPlatformHelper_detectedVendorPlatform", "Detected database platform: {0}"},
        { "dbPlatformHelper_regExprDbPlatform", "Database platform: {1}, regular expression: {0}"},
        { "dbPlatformHelper_patternSyntaxException", "Exception while using regular expression: {0}" },
        { "platform_ora_init_id_seq", "Init Oracle identity sequence {0} -> {1} for table {2}"},
        { "platform_ora_remove_id_seq", "Remove Oracle identity sequence {0} -> {1} for table {2}"},
        { "unknown_query_hint", "query {0}: unknown query hint {1} will be ignored"},
        { "query_hint", "query {0}: query hint {1}; value {2}"},
        { "property_value_specified", "property={0}; value={1}"},
        { "property_value_default", "property={0}; default value={1}"},
        { "handler_property_value_specified", "property={0}; value={1}; translated value={2}"},
        { "handler_property_value_default", "property={0}; default value={1}; translated value={2}"},
        { "predeploy_begin", "Begin predeploying Persistence Unit {0}; session {1}; state {2}; factoryCount {3}"},
        { "predeploy_end", "End predeploying Persistence Unit {0}; session {1}; state {2}; factoryCount {3}"},
        { "session_name_change", "Session change name: Persistence Unit {0}; old session {1}; new session {2}"},
        { "deploy_begin", "Begin deploying Persistence Unit {0}; session {1}; state {2}; factoryCount {3}"},
        { "deploy_end", "End deploying Persistence Unit {0}; session {1}; state {2}; factoryCount {3}"},
        { "undeploy_begin", "Begin undeploying Persistence Unit {0}; session {1}; state {2}; factoryCount {3}"},
        { "undeploy_end", "End undeploying Persistence Unit {0}; session {1}; state {2}; factoryCount {3}"},
        { "composite_member_begin_call", "Begin {0} on composite member Persistence Unit {1}; state {2}"},
        { "composite_member_end_call", "End {0} on composite member Persistence Unit {1}; state {2}"},
        { "loading_session_xml", "Loading persistence unit from sessions-xml file: {0}, session-name: {1}"},

        { "default_tables_created", "The table ({0}) is created."},
        { "cannot_create_table", "The table ({0}) could not be created due to exception: {1}" },
        { "cannot_add_field_to_table", "The field ({0}) could not be added to the table ({1}) due to exception: {2}" },
        { "identity_map_does_not_exist",  "Identity Map [{0}] does not exist" },
        { "identity_map_is_empty",  "Identity Map [{0}] is empty" },
        { "key_value",  "Key [{0}] => Value [{1}]" },
        { "no_identity_maps_in_this_session",  "There are no Identity Maps in this session" },
        { "identity_map_class",  "Identity Map [{0}] class = {1}" },
        { "identity_map_initialized",  "Identity Map [{0}] is initialized" },
        { "identity_map_invalidated",  "Identity Map [{0}] is invalidated" },
        { "no_classes_in_session", "No Classes in session." },

        { "creating_broadcast_connection", "{0}: creating connection." },
        { "broadcast_connection_created", "{0}: connection created." },
        { "failed_to_create_broadcast_connection", "{0}: failed to create connection." },
        { "broadcast_sending_message", "{0}: sending message {1}" },
        { "broadcast_sent_message", "{0}: has sent message {1}" },
        { "broadcast_closing_connection", "{0}: connection is closing." },
        { "broadcast_connection_closed", "{0}: connection closed." },
        { "broadcast_retreived_message", "{0}: has received message {1}" },
        { "broadcast_processing_remote_command", "{0}: processing message {1} sent by service id {2}: processing remote command {3}." },
        { "broadcast_connection_start_listening", "{0}: Start listening." },
        { "broadcast_connection_stop_listening", "{0}: Stop listening." },
        { "sdo_type_generation_processing_type", "{0}: Generating Type  [{1}]."},
        { "sdo_type_generation_processing_type_as", "{0}: Generating Type  [{1}] as [{2}]."},
        { "registered_mbean", "Registered MBean: {0} on server {1}" },
        { "unregistering_mbean", "Unregistering MBean: {0} on server {1}" },
        { "mbean_get_application_name", "The applicationName for the MBean attached to session [{0}] is [{1}]" },
        { "mbean_get_module_name", "The moduleName for the MBean attached to session [{0}] is [{1}]" },
        { "active_thread_is_different_from_current_thread", "Forcing the activeThread \"{0}\" on the mergeManager \"{1}\" to be the currentThread \"{2}\" because they are different." },
        { "dead_lock_encountered_on_write_no_cachekey", "Potential deadlock encountered while thread: {2} attempted to lock object of class: {0} with id: {1}, entering deadlock avoidance algorithm.  This is a notice only."},
        { "metamodel_attribute_class_type_is_null", "Metamodel processing: The class type is null for the attribute: {0}." },
        { "metamodel_mapping_type_is_unsupported", "Metamodel processing: The mapping type [{0}] in the attribute [{1}] is currently unsupported." },
        { "metamodel_descriptor_type_eis_or_xml_is_unsupported", "Metamodel processing: EIS or XML ClassDescriptor instances [{0}] are currently not supported." },
        { "connect_drivermanager_fail", "DriverManager connect failed, trying direct connect."},
        { "metamodel_unable_to_determine_element_type_in_absence_of_generic_parameters", "Metamodel processing: Unable to get the element type for the mapping [{0}] in the absence of generic parameters on mapping declaration." },
        { "metamodel_init_failed", "Initialization of the metamodel failed during deployment.  Ignoring exception: [{0}] " },
        { "metamodel_canonical_model_class_not_found", "Canonical Metamodel class [{0}] not found during initialization."},
        { "metamodel_canonical_model_class_found", "Canonical Metamodel class [{0}] found and instantiated during initialization."},
        { "metamodel_relationaldescriptor_javaclass_null_on_managedType", "Metamodel processing: The javaClass field is null for the relationalDescriptor [{0}] for the managedType [{1}]." },
        { "metamodel_attribute_getmember_is_null", "The java Member is null for the Attribute [{0}] with managedType [{1}] and descriptor [{2}]." },
        { "metamodel_typeImpl_javaClass_should_not_be_null", "The metamodel TypeImpl.javaClass field should not be set to null for the Type [{0}] with name [{1}]." },
        { "jmx_mbean_classloader_in_use", "EclipseLink JMX Runtime Services is referencing the [{0}] ClassLoader at: [{1}]" },
        { "metamodel_itentifiableType_javaclass_null_cannot_set_supertype", "Metamodel processing: Unable to set the superclass Hierarchy because the javaClass field is null for the relationalDescriptor [{0}] for the identifiableType [{1}]." },
        { "metamodel_relationaldescriptor_not_fully_initialized_yet", "Metamodel processing: The relationalDescriptor [{0}] for the managedType [{1}] is not fully initialized yet - the Metamodel instance will be incomplete before at least one entityManger session login (after a full deploy)." },
        { "no_weaved_vh_method_found_verify_weaving_and_module_order", "An expected weaving method [{0}] was not found on the accessor [{2}] on the mapping [{1}] - verify that the processing order of your modules places the one containing a persistence unit ahead of modules that use it in your deployment descriptor, or disable weaving for the persistence context or the mapping using FetchType.EAGER." },
        { "proxy_connection_customizer_already_proxy_session", "{0}:{1}: proxy session with unknown properties is already opened. Closing it."},
        { "proxy_connection_customizer_opened_proxy_session",  "{0}:{1}: opened proxy session."},
        { "proxy_connection_customizer_closing_proxy_session", "{0}:{1}: closing proxy session."},
        { "releasing_invalid_lock", "A lock has been encountered where the thread: {0} is no longer active.  The lock on object class :{1} id: {2} has been forcibly released"},
        { "acquire_connection", "Connection acquired from connection pool [{0}]." },
        { "release_connection", "Connection released to connection pool [{0}]." },
        { "failover", "Connection pool [{0}] is dead, failing over to poll [{1}]." },
        { "max_time_exceeded_for_acquirerequiredlocks_wait", "MAX TIME {0} seconds EXCEEDED FOR WRITELOCKMANAGER WAIT.  Waiting on Entity type: {1}with pk: {2} currently locked by thread: {3} with the following trace:\n"},
        { "dcn_registering", "Registering for database change event notification." },
        { "dcn_change_event", "Receieved database change event [{0}]." },
        { "dcn_invalidate", "Invalidating cache key [{0}] from database change event for class [{1}]." },
        { "dcn_register_table", "Registering table [{0}] for database change event notification." },
        { "dcn_unregister", "Removing registering for database change event notification." },

        { "exception_caught_closing_statement", "Exception caught when trying to close the query statement [{0}]." },
        //MOXy fine/finer/finest
        { "schema_factory", "SchemaFactory instance: {0}" },
        { "saxparser_factory", "SAXParserFactory instance: {0}" },
        { "xpath_factory", "XPathFactory instance: {0}" },
        { "transformer_factory", "TransformerFactory instance: {0}" },
        { "documentbuilder_factory", "DocumentBuilderFactory instance: {0}" },
        { "jaxp_sec_disabled", "Xml Security disabled, no JAXP {0} external access configuration necessary." },
        { "jaxp_sec_explicit", "Detected explicitly JAXP configuration, no JAXP {0} external access configuration necessary." },
        { "jaxp_sec_prop_supported", "Property {0} is supported and has been successfully set by used JAXP implementation." },
        { "jaxp_sec_prop_not_supported", "Property {0} is not supported by used JAXP implementation." },
        { "moxy_start_marshalling", "Marshalling \"{0}\" into {1} started"},
        { "moxy_start_unmarshalling", "Unmarshalling {0} into \"{1}\" by \"{2}\" started"},
        { "moxy_read_from_moxy_json_provider", "MOXyJsonProvider.readFrom(...) is called."},
        { "moxy_write_to_moxy_json_provider", "MOXyJsonProvider.writeTo(...) is called."},
        { "moxy_set_marshaller_property", "Setting marshaller property (name/value): {0}/{1}"},
        { "moxy_set_unmarshaller_property", "Setting unmarshaller property (name/value): {0}/{1}"},
        { "moxy_set_jaxb_context_property", "Setting JAXBContext property (name/value): {0}/{1}"},
        
        { "invalid_tzone", "Invalid timezone conversion property {0} value: {1}.  Will attempt to resolve default." },
        { "invalid_default_tzone", "Invalid timezone conversion property {0} value: {1}.  Defaulting to UTC." },
        { "using_conversion_tzone", "ConversionManager using default zone offset: {1}." }
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
