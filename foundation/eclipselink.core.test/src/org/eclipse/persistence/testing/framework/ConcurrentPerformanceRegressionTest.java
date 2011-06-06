/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.framework;

import java.util.*;

/**
 * This test compares the concurrency of an operation defined run().
 * It compares the performance of performing the task with 1, 2, 4, and 16 threads.
 * On a single CPU machine all of the results should be similar,
 * on a multi-CPU machine the 2,4,16 should be faster if the task can be performed concurrently.
 */
public abstract class ConcurrentPerformanceRegressionTest extends PerformanceRegressionTestCase {
    public static int DEFAULT_THREADS = 32;
    protected int maxThreads;
    protected Exception caughtException;
    protected List workerThreads;

    public ConcurrentPerformanceRegressionTest() {
        this.maxThreads = DEFAULT_THREADS;
    }

    /**
     * Return the maximum number of threads the test should run to.
     */
    public int getMaxThreads() {
        return maxThreads;
    }

    /**
     * Set the maximum number of threads the test should run to.
     */
    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    /**
     * Increment the iteration count.
     * This is maintained by the test to allow concurrent tests to manage the count.
     */
    public synchronized void incrementIterations() {
        this.iterations++;
    }

    /**
     * Reset the iteration count.
     * This is maintained by the test to allow concurrent tests to manage the count.
     */
    public synchronized void resetIterations() {
        this.iterations = 0;
    }

    /**
     * Start 1 thread test.
     */
    public void startTest() {
        startTest(getMaxThreads());
    }

    /**
     * Start each worker thread.
     */
    public void startTest(int numberOfThreads) {
        caughtException = null;
        // Spawn worker threads.
        for (int index = 0; index < (numberOfThreads - 1); index++) {
            WorkerThread thread = (WorkerThread)getWorkerThreads().get(index);
            thread.resumeExecution();
        }
    }

    /**
     * Allows any test specific setup before starting the test run.
     */
    public void endTest() {
        // Suspend the worker threads,
        // suspend all no matter what number of threads was to even out performance.
        for (int index = 0; index < getMaxThreads(); index++) {
            WorkerThread thread = (WorkerThread)getWorkerThreads().get(index);
            thread.suspendExecution();
        }
        // Let workers finish.
        Thread.yield();
        // Wait for workers to finish,
        // wait all no matter what number of threads was to even out performance.
        for (int index = 0; index < getMaxThreads(); index++) {
            WorkerThread thread = (WorkerThread)getWorkerThreads().get(index);
            thread.joinExecution();
        }
    }

    public List getWorkerThreads() {
        return workerThreads;
    }

    /**
     * Start the worker threads.
     */
    public void setup() {
        this.workerThreads = new ArrayList(getMaxThreads());
        for (int index = 0; index < getMaxThreads(); index++) {
            WorkerThread thread = new WorkerThread();
            this.workerThreads.add(thread);
            thread.start();
        }
    }

    /**
     * Count REPEATS runs of the run method with 1 thread.
     */
    public void test() throws Exception {
        test(getMaxThreads());
    }

    /**
     * Count REPEATS runs of the run method with n threads.
     */
    public void test(int numberOfThreads) throws Exception {
        if (caughtException != null) {
            throw caughtException;
        }
        runTask();
    }

    /**
     * Stop the worker threads.
     */
    public void reset() {
        // Stop the worker threads.
        for (int index = 0; index < getWorkerThreads().size(); index++) {
            WorkerThread thread = (WorkerThread)getWorkerThreads().get(index);
            thread.stopExecution();
        }
        // Let workers finish.
        Thread.yield();
        this.workerThreads = null;
    }

    /**
     * Perform the task.
     */
    public abstract void runTask() throws Exception;

    /**
     * Defines the work thread.
     * A worker thread calls the run method in a loop,
     * until it is suspended.
     */
    protected class WorkerThread extends Thread {
        protected boolean isSuspended = true;
        protected boolean isDead = false;

        /**
         * After the next completion of the run method, suspend execution.
         * The thread is still alive, but waiting to be signaled to resumed.
         */
        public void stopExecution() {
            isDead = true;
        }

        /**
         * After the next completion of the run method, suspend execution.
         * The thread is still alive, but waiting to be signaled to resumed.
         */
        public void suspendExecution() {
            isSuspended = true;
        }

        /**
         * Wait for the thread to suspend.
         * Synchronize will wait for wait to release synchornization.
         */
        public synchronized void joinExecution() {
            if (!isSuspended) {
                throw new RuntimeException("Must suspend first");
            }
        }

        /**
         * Continue running the run method.
         */
        public synchronized void resumeExecution() {
            isSuspended = false;
            try {
                notify();
            } catch (Exception exception) {
                throw new RuntimeException(exception.getMessage());
            }
        }

        /**
         * Run the test run method in a loop until killed.
         */
        public synchronized void run() {
            try {
                while (!isDead) {
                    // Allows the thread to suspend itself when the current test is done.
                    if (isSuspended) {
                        wait();
                    }
                    ConcurrentPerformanceRegressionTest.this.runTask();
                    ConcurrentPerformanceRegressionTest.this.incrementIterations();
                }
            } catch (Exception exception) {
                ConcurrentPerformanceRegressionTest.this.caughtException = exception;
            }
        }
    }
}
