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

import org.eclipse.persistence.exceptions.ConcurrencyException;
import org.eclipse.persistence.internal.localization.TraceLocalization;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ConcurrencySemaphore {

    private final long MAX_TIME_PERMIT = ConcurrencyUtil.SINGLETON.getConcurrencySemaphoreMaxTimePermit();
    private final long TIMEOUT_BETWEEN_LOG_MESSAGES = ConcurrencyUtil.SINGLETON.getConcurrencySemaphoreLogTimeout();

    private ThreadLocal<Boolean> threadLocal;
    private int noOfThreads;
    private Semaphore semaphore;
    private String logMessageKey;
    private Object outerObject;

    /**
     * Constructor to create {@link #ConcurrencySemaphore}
     * @param threadLocalVarControlIfCurrentThreadHasAcquiredSemaphore
     *          Thread local variable that the code to acquire a semaphore can check to make sure it does not try to acquire
     *          twice the same semaphore (e.g. in case the object building algorithm is recursive).
     * @param noOfThreadsAllowedToExecuteInParallel
     *          Max number of threads to acquire semaphore.
     * @param semaphoreOfThreadsAllowedToExecuteInParallel
     *          Semaphore used to control.
     * @param outerObject
     *          Reference to outer object where is this semaphore used.
     * @param logMessageKey
     *          Log message key from {@link org.eclipse.persistence.internal.localization.TraceLocalization}
     */
    public ConcurrencySemaphore(ThreadLocal<Boolean> threadLocalVarControlIfCurrentThreadHasAcquiredSemaphore, int noOfThreadsAllowedToExecuteInParallel, Semaphore semaphoreOfThreadsAllowedToExecuteInParallel, Object outerObject, String logMessageKey) {
        this.threadLocal = threadLocalVarControlIfCurrentThreadHasAcquiredSemaphore;
        this.noOfThreads = noOfThreadsAllowedToExecuteInParallel;
        this.semaphore = semaphoreOfThreadsAllowedToExecuteInParallel;
        this.outerObject = outerObject;
        this.logMessageKey = logMessageKey;
    }

    /**
     * Do nothing if the semaphore has already been acquired by this thread in a higher recursive call or if the
     * configuration to acquire the slow down semaphore is not active. Otherwise, try to acquire the semaphore
     * @param useSemaphore
     *         TRUE to use semaphore, FALSE don't use it.
     * @return FALSE is returned if we do not want to be using the semaphore. FALSE is also returned if the current
     *         thread has acquire the semaphore in an upper level recursive stack call. TRUE is returned if and only
     *         using the semaphore is desired and we succeed acquiring the semaphore.
     *
     */
    public boolean acquireSemaphoreIfAppropriate(boolean useSemaphore) {
        // (a) If configuration is saying to not use semaphore and go vanilla there is nothing for us to do
        boolean useSemaphoreToSlowDown = useSemaphore;
        if (!useSemaphoreToSlowDown) {
            return false;
        }

        // (b) The project is afraid of dead locks and does not allow acquire semaphore at the same time
        // bottleneck slow down thread execution
        // scenario 1:
        // check if this thread has already acquired the semaphore in this call
        Boolean currentThreadHasAcquiredSemaphoreAlready = threadLocal.get();
        if (Boolean.TRUE.equals(currentThreadHasAcquiredSemaphoreAlready)) {
            // don't allow this thread to acquire a second time the same semaphore it has done it already
            return false;
        }
        // try to acquire the semaphore being careful with possible blow ups of thread interrupted
        // Scenario 2:
        // In this possibly recursive call stack this is the first time the current thread tries to acquire the semaphore to go build an object
        boolean successAcquiringSemaphore = false;
        // this thread will go nowhere until it manages to acquire semaphore that allows to continue with execution
        // this should not only reduce the risk of dead locks but also if dead locks occur in the concurrency manager layer we will have a lot fewer threads making noise (easier to analyze locks)
        // being part of the same dead lock
        final long startTimeAttemptingToAcquireSemaphoreMillis = System.currentTimeMillis();
        long dateWhenWeLastSpammedServerLogAboutNotBeingAbleToAcquireOurSemaphore = startTimeAttemptingToAcquireSemaphoreMillis;
        try {
            successAcquiringSemaphore = semaphore.tryAcquire(MAX_TIME_PERMIT, TimeUnit.MILLISECONDS);
            while (!successAcquiringSemaphore) {
                // (i) check if ten seconds or more have passed
                long whileCurrentTimeMillis = System.currentTimeMillis();
                long elapsedTime = whileCurrentTimeMillis - dateWhenWeLastSpammedServerLogAboutNotBeingAbleToAcquireOurSemaphore;
                if (elapsedTime > TIMEOUT_BETWEEN_LOG_MESSAGES) {
                    String outerObjectString = outerObject.toString();
                    String threadName = Thread.currentThread().getName();
                    // spam a message into the server log this will be helpful
                    String logMessage = TraceLocalization.buildMessage(logMessageKey, new Object[] {threadName, startTimeAttemptingToAcquireSemaphoreMillis, noOfThreads, outerObjectString});
                    AbstractSessionLog.getLog().log(SessionLog.SEVERE, SessionLog.CACHE, logMessage, threadName);
                    dateWhenWeLastSpammedServerLogAboutNotBeingAbleToAcquireOurSemaphore = whileCurrentTimeMillis;
                }
                // (ii) To avoid spamming the log every time lets update the data of when we last spammed the log
                successAcquiringSemaphore = semaphore.tryAcquire(MAX_TIME_PERMIT, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException interrupted) {
            // If we are interrupted while trying to do object building log that we have been interrupted here
            AbstractSessionLog.getLog().logThrowable(SessionLog.SEVERE, SessionLog.CACHE, interrupted);
            throw ConcurrencyException.waitWasInterrupted(interrupted.getMessage());
        } finally {
            // (d) Before we leave this method regardless of a blow up or not always store in the thread local variable the accurate state of the successAcquiringSemaphore
            threadLocal.set(successAcquiringSemaphore);
        }
        // (d) the final result
        return successAcquiringSemaphore;
    }

    /**
     * If the call to
     * {@link #acquireSemaphoreIfAppropriate(boolean)}
     * returned true implying the current thread acquire the semaphore, the same thread on the same method is mandated
     * to release the semaphore.
     * @param semaphoreWasAcquired
     *            flag that tells us if the current thread had successfully acquired semaphore if the flag is true then
     *            the semaphore will be released and given resources again.
     */
    public void releaseSemaphoreAllowOtherThreadsToStartDoingObjectBuilding(boolean semaphoreWasAcquired) {
        if (semaphoreWasAcquired) {
            // release the semaphore resource for the current thread
            semaphore.release();
            // ensure the thread local variable is cleaned up to indicate that the thread was not yet acquired
            // the semaphore and would need to do so
            threadLocal.set(Boolean.FALSE);
        }
    }
}
