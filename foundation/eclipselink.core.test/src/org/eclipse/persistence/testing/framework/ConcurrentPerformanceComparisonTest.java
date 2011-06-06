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
public abstract class ConcurrentPerformanceComparisonTest extends PerformanceComparisonTestCase {
    public static double NUMBER_OF_CPUS = 1.1; // 0.1 for hyper-threading.
    public static int DEFAULT_THREADS = 32;
    protected int maxThreads;
    protected Exception caughtException;
    protected List workerThreads;

    public ConcurrentPerformanceComparisonTest() {
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
        startTest(1);
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
        int threads = 2;
        while (threads <= getMaxThreads()) {
            this.addThreadTest(threads);
            threads = threads * 2;
        }
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
        test(1);
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
     * Verify the multi-threaded results are NUMBER_OF_CPU times better
     * than the single thread, allowing for the allowableDecrease.
     */
    public void verify() {
        PerformanceComparisonTestResult result = (PerformanceComparisonTestResult)getTestResult();
        for (int index = 0; index < result.percentageDifferences.size(); index++) {
            PerformanceComparisonTest test = (PerformanceComparisonTest)getTests().get(index);
            double allowable = 
                (Math.max(Math.min(NUMBER_OF_CPUS, index + 1), NUMBER_OF_CPUS) * 100) - 100 + (getAllowableDecrease() * 
                                                                                               (index + 1));
            test.setAllowableDecrease(allowable);
        }
        super.verify();
    }

    /**
     * Perform the task.
     */
    public abstract void runTask() throws Exception;

    /**
     * Spawn numberOfThreads to run the test's run method and count total number
     * of operations.
     */
    public void addThreadTest(final int numberOfThreads) {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
                public void startTest() {
                    ConcurrentPerformanceComparisonTest.this.startTest(numberOfThreads);
                }

                public void test() throws Exception {
                    ConcurrentPerformanceComparisonTest.this.test(numberOfThreads);
                }

                public void endTest() {
                    ConcurrentPerformanceComparisonTest.this.endTest();
                }
            };
        test.setName("ThreadTest:" + numberOfThreads);
        addTest(test);
    }

    /**
     * Defines the work thread.
     * A worker thread calls the run method in a loop,
     * until it is suspended.
     */
    protected class WorkerThread extends Thread {
        protected volatile boolean isSuspended = true;
        protected volatile boolean isDead = false;

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
                    ConcurrentPerformanceComparisonTest.this.runTask();
                    ConcurrentPerformanceComparisonTest.this.incrementIterations();
                }
            } catch (Exception exception) {
                ConcurrentPerformanceComparisonTest.this.caughtException = exception;
            }
        }
    }
}
