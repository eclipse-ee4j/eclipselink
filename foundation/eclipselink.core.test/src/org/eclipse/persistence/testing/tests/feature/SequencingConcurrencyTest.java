/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.feature;

import java.util.Comparator;
import java.util.Arrays;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.Server;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * This testcase test the thread-safeness of TopLink's sequencing. The type of sequencing specified in the login
 * info is tested (either native or table sequencing is used). To test the other kind of sequencing you must logout,
 * change you login info, login, and rerun the test.
 * The test start a number of threads. Each thread generates a number of sequence numbers. For this test the
 * preallocation size is set to 1. This maximizes the concurrency of the threads and increases the effectiveness
 * of this test.
 * The choice of number of threads and iterations must be large enough for the test to be effective. I got this test
 * to fail (with the 2.5.1.1 sequences bug) with 5 threads and 50 iterations.
 *
 * Caveat: This test cannot prove that sequencing is thread safe, it can only prove that it is not.
 *
 * author: Robert Campbell
 */
public class SequencingConcurrencyTest extends TestCase implements Comparator {
    public java.util.Vector sequences;
    public SequencingConcurrencyTest[] tests;
    public int nThreads;
    public boolean useServerSession;
    public boolean useSeparateConnection;
    public Server serverSession;
    public DatabaseSession dbSession;
    public int previousSequencePreallocationSize;
    public int sequencePreallocationSize;
    public boolean useSeparateConnectionOriginal;
    public boolean shouldLogMessages;
    public int originalLogLevel;
    public int nIterations;
    public Session session;
    public int threadNumber;
    public boolean handleException;
    public Exception exception;

    protected SequencingConcurrencyTest(int threadNumber, int nIterations, java.util.Vector sequences, Session session, boolean handleException) {
        this.threadNumber = threadNumber;
        this.nIterations = nIterations;
        this.sequences = sequences;
        this.session = session;
        this.handleException = handleException;
    }

    /**
     * To be called by testing framework.
     */
    public SequencingConcurrencyTest(int nThreads, int nIterations, boolean useServerSession, boolean useSeparateConnection) {
        this(nThreads, nIterations, useServerSession, useSeparateConnection, 1);
    }

    /**
     * To be called by testing framework.
     */
    public SequencingConcurrencyTest(int nThreads, int nIterations, boolean useServerSession, boolean useSeparateConnection, int sequencePreallocationSize) {
        this.nThreads = nThreads;
        this.nIterations = nIterations;
        this.useServerSession = useServerSession;
        this.useSeparateConnection = useSeparateConnection;
        String sessionUsed;
        if (useServerSession) {
            sessionUsed = "ServerSession";
        } else {
            sessionUsed = "DatabaseSession";
        }
        shouldLogMessages = false;
        this.sequencePreallocationSize = sequencePreallocationSize;
        setName(getName() + " " + sessionUsed + " separateConnection=" + useSeparateConnection + " seqPreallocSize=" + sequencePreallocationSize + " threads=" + nThreads + " iterations=" + nIterations);
    }

    /**
     * Compare the two BigDecimal, for using TOPSort.
     */
    public int compare(Object b1, Object b2) {
        java.math.BigDecimal big1 = new java.math.BigDecimal(((Number)b1).longValue());
        java.math.BigDecimal big2 = new java.math.BigDecimal(((Number)b2).longValue());
        return big1.compareTo(big2);
    }

    /**
     * Sets the logging and the sequence preallocation size the way it was.
     */
    public void reset() {
        if (useServerSession) {
            if (serverSession != null) {
                serverSession.logout();
                serverSession = null;
            }
        } else {
            if (dbSession != null) {
//	        dbSession.getSequencingControl().setPreallocationSize(this.previousSequencePreallocationSize);
                dbSession.getSequencingControl().initializePreallocated();
                if (useSeparateConnection != useSeparateConnectionOriginal) {
                    dbSession.getSequencingControl().setShouldUseSeparateConnection(useSeparateConnectionOriginal);
                    dbSession.getSequencingControl().resetSequencing();
                }
                dbSession.setLogLevel(originalLogLevel);
                dbSession = null;
            }
        }
    }

    public Runnable runnable() {
        return new Runnable() {

                /**
                 * Each thread does this: gets nIterations number of sequence numbers and puts them in a thread specific array.
                 */
                public void run() {
                    // Test
                    Number[] sequence = (Number[])sequences.elementAt(threadNumber);
                    try {
                        if (handleException) {
                            for (int i = 0; i < nIterations; i++) {
                                try {
                                    sequence[i] = (Number)((AbstractSession)session).getSequencing().getNextValue(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
                                } catch (org.eclipse.persistence.exceptions.ConcurrencyException ex) {
                                    if (ex.getErrorCode() == org.eclipse.persistence.exceptions.ConcurrencyException.SEQUENCING_MULTITHREAD_THRU_CONNECTION) {
                                        // that's an acceptable exception, try again.
                                        i--;
                                    } else {
                                        throw ex;
                                    }
                                }
                            }
                        } else {
                            for (int i = 0; i < nIterations; i++) {
                                sequence[i] = (Number)((AbstractSession)session).getSequencing().getNextValue(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
                            }
                        }
                    } catch (Exception ex2) {
                        exception = ex2;
                    } finally {
                        if (session.isClientSession()) {
                            session.release();
                        }
                    }
                }
            };
    }

    public void setup() {
        if (getAbstractSession().getDescriptor(Employee.class).getSequence().shouldAcquireValueAfterInsert()) {
            throw new org.eclipse.persistence.testing.framework.TestWarningException("Not a valid test against databases where the native sequencing is done entirely in the database.");
        }

        // Setup
        dbSession = (DatabaseSession)getSession();
        if (useServerSession) {
            int numConnections = java.lang.Math.min(nThreads, 5);
            serverSession = new Project(getSession().getDatasourceLogin().clone()).createServerSession(numConnections, numConnections);
            serverSession.addDescriptors(new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject());
//	    serverSession.getSequencingControl().setPreallocationSize(sequencePreallocationSize);
            serverSession.getSequencingControl().setShouldUseSeparateConnection(useSeparateConnection);
            serverSession.setSessionLog(getSession().getSessionLog());
            if (shouldLogMessages) {
                serverSession.setLogLevel(SessionLog.FINE);
            }
            serverSession.login();
        } else {
            originalLogLevel = dbSession.getLogLevel();
            if (shouldLogMessages) {
                dbSession.setLogLevel(SessionLog.FINE);
            }
//	    this.previousSequencePreallocationSize = dbSession.getSequencingControl().getPreallocationSize();
//	    dbSession.getSequencingControl().setPreallocationSize(sequencePreallocationSize);
            dbSession.getSequencingControl().resetSequencing();
            dbSession.getSequencingControl().initializePreallocated();
            useSeparateConnectionOriginal = dbSession.getSequencingControl().shouldUseSeparateConnection();
            if (useSeparateConnection != useSeparateConnectionOriginal) {
                dbSession.getSequencingControl().setShouldUseSeparateConnection(useSeparateConnection);
                dbSession.getSequencingControl().resetSequencing();
            }
        }

        // Setup the arrays of BigDecimals to be filled.
        sequences = new java.util.Vector(nThreads);
        for (int i = 0; i < nThreads; i++) {
            sequences.addElement(new Number[nIterations]);
        }
    }

    /**
     * Start each thread, then wait until all of them are finished.
     */
    public void test() {
        //
        Thread[] threads = new Thread[nThreads];
        tests = new SequencingConcurrencyTest[nThreads];
        Session writeSession = getSession();
        boolean handleException = !useServerSession && !useSeparateConnection;
        for (int i = 0; i < nThreads; i++) {
            if (useServerSession) {
                writeSession = serverSession.acquireClientSession();
            }
            tests[i] = new SequencingConcurrencyTest(i, nIterations, sequences, writeSession, handleException);
            threads[i] = new Thread(tests[i].runnable());
            threads[i].start();
        }

        // Join with all the threads so we don't proceed until all the threads have finished.
        for (int i = 0; i < nThreads; i++) {
            try {
                threads[i].join();
            } catch (Exception ex) {
                throw new org.eclipse.persistence.testing.framework.TestErrorException(ex.getMessage());
            }
        }
    }

    /**
     * Make sure that the sequence numbers generated contained no gaps or duplicates.
     */
    public void verify() {
        // Verify
        for (int i = 0; i < nThreads; i++) {
            if (tests[i].exception != null) {
                throw new org.eclipse.persistence.testing.framework.TestErrorException("exception in thread " + i + ", session(" + String.valueOf(System.identityHashCode(session)) + ") ", tests[i].exception);
            }
        }

        // Put all the sequences into one big array.
        Number[] big = new Number[nThreads * nIterations];
        for (int i = 0; i < nThreads; i++) {
            System.arraycopy(sequences.elementAt(i), 0, big, i * nIterations, nIterations);
        }
        try {
            // sort the array.
            Arrays.sort(big, this);

            // Verify that there are no duplicates or gaps in the array.
            Number previous = big[0];
            for (int i = 1; i < (nIterations * nThreads); i++) {
                Number current = big[i];
                if ((previous.intValue() + 1) != current.intValue()) {
                    throw new org.eclipse.persistence.testing.framework.TestErrorException("Gap in sequencing, or incorrect sequences generated.");
                }
                previous = current;
            }
        } catch (Exception ex) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException(ex.getMessage());
        }
    }
}
