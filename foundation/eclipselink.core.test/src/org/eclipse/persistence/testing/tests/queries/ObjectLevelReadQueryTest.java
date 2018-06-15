/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//    04/20/2015-2.7 Tomas Kraus - Concurrency test to trigger NPE in
//                                 checkForCustomQuery(AbstractSession, AbstractRecord).
package org.eclipse.persistence.testing.tests.queries;

import static org.eclipse.persistence.logging.SessionLog.INFO;
import static org.eclipse.persistence.logging.SessionLog.WARNING;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Test read queries using objects.
 */
public abstract class ObjectLevelReadQueryTest extends TestCase {

    /** Fail message for NPE in checkForCustomQuery method. */
    private static final String FAIL_NPE
        = "NullPointerException thrown in checkForCustomQuery(AbstractSession, AbstractRecord)";

    /** Logger used in test. */
    protected SessionLog log;

    /** Current database session. */
    protected Session session;

    /** Some entity to be used in query. */
    protected final Class entity = Employee.class;

    /** Some SQL call to be used in query. */
    protected final SQLCall call = new SQLCall("SELECT t0.EMP_ID FROM EMPLOYEE t0");

    /**
     * Creates an instance of jUnit tests for read queries using objects.
     */
    public ObjectLevelReadQueryTest() {
        super();
    }

    /**
     * Creates an instance of jUnit tests for read queries using objects.
     * @param name jUnit test name.
     */
    public ObjectLevelReadQueryTest(final String name) {
        super();
        setName(name);
    }

    /**
     * Initialize this test suite.
     */
    public void setup() {
        session = getSession();
        log = session.getSessionLog();
    }

    /**
     * Clean this test suite.
     */
    public void reset() {
        session = null;
        log = null;
    }

    /**
     * {@link TestCase#test()} method overwritten as public to avoid failures.
     */
    @Override
    public abstract void test() throws Throwable;

    /**
     * {@code [ObjectLevelReadQuery].isCustomQueryUsed} value changer
     * for {@link #testCheckForCustomQueryRaceConditions()}.
     */
    private static final class ValueChanger extends Thread {

        /** Logger used in test. */
        private final SessionLog log;

        /** Query where to change {@code isCustomQueryUsed} value. */
        private final ObjectLevelReadQuery query;

        /** Instance used for synchronization. */
        private final Object mutex;

        /** [ObjectLevelReadQuery].isCustomQueryUsed} field. */
        private final Field isCustomQueryUsed;

        /** Thread execution flag. */
        private boolean execute;

        /**
         * Creates an instance of value changer thread.
         * @param objectLevelReadQuery Query object where {@code isCustomQueryUsed} value will be changed.
         * @param syncMutex            Instance used for synchronization.
         * @param sessionLog           Logger used in test.
         */
        private ValueChanger(final ObjectLevelReadQuery objectLevelReadQuery, final Object syncMutex,
                final SessionLog sessionLog) {
            log = sessionLog;
            // Calling run() directly will do nothing.
            query = objectLevelReadQuery;
            mutex = syncMutex;
            execute = false;
            // Placeholder for initialization.
            Field isCustomQueryUsedLocal;
            try {
                isCustomQueryUsedLocal = DatabaseQuery.class.getDeclaredField("isCustomQueryUsed");
            } catch (NoSuchFieldException | SecurityException e) {
                log.logThrowable(WARNING, e);
                isCustomQueryUsedLocal = null;
            }
            if (isCustomQueryUsedLocal != null) {
                try {
                    isCustomQueryUsedLocal.setAccessible(true);
                } catch (SecurityException e) {
                    log.logThrowable(WARNING, e);
                    isCustomQueryUsedLocal = null;
                }
            }
            isCustomQueryUsed = isCustomQueryUsedLocal;
        }

        /**
         * Check if value changer initialization was successful.
         * @return Value of {@code true} when {@code isCustomQueryUsed} field is available or {@code false} otherwise.
         */
        private boolean isOk() {
            return isCustomQueryUsed != null;
        }

        /**
         * Start thread execution.
         */
        public void start() {
            execute = true;
            super.start();
        }

        /**
         * Request thread to finish.
         */
        public void finish() {
            execute = false;
        }

        /**
         * Thread code to be executed.
         */
        @Override
        public void run() {
            synchronized(mutex) {
                mutex.notify();
            }
            while(execute) {
                try {
                    isCustomQueryUsed.set(query, Boolean.TRUE);
                    isCustomQueryUsed.set(query, (Boolean)null);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    log.logThrowable(WARNING, e);
                    execute = false;
                }
            }
        }
    }

    /**
     * Get {@code [ObjectLevelReadQuery].checkForCustomQuery(AbstractSession, AbstractRecord)} method accessor.
     * @param c Class where to search for method.
     * @return {@code [ObjectLevelReadQuery].checkForCustomQuery(AbstractSession, AbstractRecord)} method accessor.
     */
    protected Method getCheckForCustomQueryMethod(final Class c) {
        Method method;
        try {
            method = c.getDeclaredMethod(
                    "checkForCustomQuery", AbstractSession.class, AbstractRecord.class);
            method.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException e) {
            log.logThrowable(WARNING, e);
            method = null;
        }
        return method;
    }

    /**
     * Call {@code [ObjectLevelReadQuery].checkForCustomQuery(AbstractSession, AbstractRecord)} method.
     * @param instance ObjectLevelReadQuery instance on which method is called.
     * @param mathod   {@code [ObjectLevelReadQuery].checkForCustomQuery(AbstractSession, AbstractRecord)}
     *                 method accessor.
     * @throws Throwable when any exception except NPE in invoked method occurs.
     */
    protected DatabaseQuery callCheckForCustomQueryMethod(final Object instance, final Method method) throws Throwable {
        DatabaseQuery databaseQuery;
        try {
            databaseQuery = (DatabaseQuery)method.invoke(instance, session, (AbstractRecord)null);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof NullPointerException) {
                log.log(WARNING, FAIL_NPE);
                e.printStackTrace();
                fail(FAIL_NPE);
            }
            log.logThrowable(WARNING, e);
            throw e;
        } catch (Throwable e) {
            log.logThrowable(WARNING, e);
            e.printStackTrace();
            throw e;
        }
        return databaseQuery;
    }

    /**
     * jUnit test to verify NPE in {@code checkForCustomQuery} method in {@link ReadAllQuery} class.
     */
    public static final class CustomQueryRaceConditionsInReadAllQueryTest extends ObjectLevelReadQueryTest {

        /**
         * Creates an instance of jUnit test.
         */
        public CustomQueryRaceConditionsInReadAllQueryTest() {
            super();
        }

        /**
         * Creates an instance of jUnit test.
         * @param name jUnit test name.
         */
        public CustomQueryRaceConditionsInReadAllQueryTest(final String name) {
            super(name);
        }

        /**
         * Test {@code [ReadAllQuery].checkForCustomQuery(AbstractSession, AbstractRecord)}
         * when {@code isCustomQueryUsed} is changed to null during query execution.
         * Reproduction scenario for bug# 436871 which works with high enough probability.
         * @throws Throwable when any exception except NPE in checkForCustomQuery occurs.
         */
        @Override
        public void test() throws Throwable {
            log.log(INFO, "Running checkCustomQueryRaceConditionsInReadAllQuery test.");
            final Method checkForCustomQuery = getCheckForCustomQueryMethod(ObjectLevelReadQuery.class);
            final ReadAllQuery query = new ReadAllQuery(entity, call);
            final Object mutex = new Object();
            final ValueChanger changer = new ValueChanger(query, mutex, log);
            assertTrue("Initialization of isCustomQueryUsed property value changer failed.", changer.isOk());
            try {
                synchronized (mutex) {
                    changer.start();
                    mutex.wait();
                }
            } catch (InterruptedException e) {
                log.logThrowable(WARNING, e);
            }
            query.setReferenceClass(entity);
            query.setDescriptor(session.getDescriptor(entity));
            // 100 iterations is enough to get exception in almost 100% of tests.
            for (int i = 0; i < 100; i++)
                callCheckForCustomQueryMethod(query, checkForCustomQuery);
            changer.finish();
            try {
                changer.join();
            } catch (InterruptedException e) {
                log.logThrowable(WARNING, e);
            }
        }

    }

    /**
     * jUnit test to verify NPE in {@code checkForCustomQuery} method in {@link ReadObjectQuery} class.
     */
    public static final class CustomQueryRaceConditionsInReadObjectQueryTest extends ObjectLevelReadQueryTest {

        /**
         * Creates an instance of jUnit test.
         */
        public CustomQueryRaceConditionsInReadObjectQueryTest() {
            super();
        }

        /**
         * Creates an instance of jUnit test.
         * @param name jUnit test name.
         */
        public CustomQueryRaceConditionsInReadObjectQueryTest(final String name) {
            super(name);
        }

        /**
         * Test {@code [ReadObjectQuery].checkForCustomQuery(AbstractSession, AbstractRecord)}
         * when {@code isCustomQueryUsed} is changed to null during query execution.
         * Reproduction scenario for bug# 436871 which works with high enough probability.
         * @throws Throwable when any exception except NPE in {@code  checkForCustomQuery} occurs.
         */
        @Override
        public void test() throws Throwable {
            log.log(INFO, "Running checkCustomQueryRaceConditionsInReadObjectQuery test.");
            final Method checkForCustomQuery = getCheckForCustomQueryMethod(ObjectLevelReadQuery.class);
            final ReadObjectQuery query = new ReadObjectQuery(entity, call);
            final Object mutex = new Object();
            final ValueChanger changer = new ValueChanger(query, mutex, log);
            assertTrue("Initialization of isCustomQueryUsed property value changer failed.", changer.isOk());
            try {
                synchronized (mutex) {
                    changer.start();
                    mutex.wait();
                }
            } catch (InterruptedException e) {
                log.logThrowable(WARNING, e);
            }
            query.setReferenceClass(entity);
            query.setDescriptor(session.getDescriptor(entity));
            // 100 iterations is enough to get exception in almost 100% of tests.
            for (int i = 0; i < 100; i++)
                callCheckForCustomQueryMethod(query, checkForCustomQuery);
            changer.finish();
            try {
                changer.join();
            } catch (InterruptedException e) {
                log.logThrowable(WARNING, e);
            }
        }

    }

}
