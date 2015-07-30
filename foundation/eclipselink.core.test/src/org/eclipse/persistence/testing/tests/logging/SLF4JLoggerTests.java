/*******************************************************************************
 * Copyright (c) 2009, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Jaro Kuruc  - Initial API and implementation
 *      Tomas Kraus - EclipseLink 2.7 integration
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.logging;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.LogLevelHelper;
import org.eclipse.persistence.logging.SLF4JLogger;
import org.eclipse.persistence.logging.SLF4JLoggerHelper;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

/**
 * Unit tests for EclipseLink Logger bridge over SLF4J.
 */
public abstract class SLF4JLoggerTests extends TestCase {

    /**
     * Add tests from this class into provided test suite.
     * @param suite Test suite where to add tests.
     */
    public static void addTests(final TestSuite suite) {
        suite.addTest(new TestGetLevel());
        suite.addTest(new TestLogMessage());
        suite.addTest(new TestLogExceptionWithoutStackTrace());
        suite.addTest(new TestLogExceptionStackTrace());
    }

    /** Current EclipseLink session. */
    protected Session session;

    /** Logback logger context. */
    protected LoggerContext loggerContext;

    /**
     * Creates an instance of jUnit tests for read queries using objects.
     */
    public SLF4JLoggerTests() {
        super();
    }

    /**
     * Creates an instance of jUnit tests for read queries using objects.
     * @param name jUnit test name.
     */
    public SLF4JLoggerTests(final String name) {
        super();
        setName(name);
    }

    /**
     * Setup unit test.
     */
    public void setup() {
        session = getSession();
        session.setName("");
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();
    }

    /**
     * Clean this test suite.
     */
    public void reset() {
        session = null;
        loggerContext = null;
    }

    /**
     * Test {@code SLF4JLogger.getLevel()} method.
     */
    public static final class TestGetLevel extends SLF4JLoggerTests {

        /**
         * Creates an instance of jUnit test.
         */
        public TestGetLevel() {
            super();
        }

        /**
         * Creates an instance of jUnit test.
         * @param name jUnit test name.
         */
        public TestGetLevel(final String name) {
            super(name);
        }

        /**
         * jUnit test method.
         */
        @Override
        public void test() {
            final int configuredDefaultLevel = AbstractSessionLog.getDefaultLoggingLevel();
            final SLF4JLogger logger = new SLF4JLogger();
            final SLF4JLoggerHelper helper = new SLF4JLoggerHelper((AbstractSession)session, loggerContext, logger);
            logger.setSession(session);
            final int defaultLevel = logger.getLevel();
            final String defaultLevelString = LogLevelHelper.logIdToName(defaultLevel);
            assertEquals("SLF4J logging level " + defaultLevelString + " did not match configured level "
            + Integer.toString(configuredDefaultLevel), configuredDefaultLevel, defaultLevel);
            helper.testCategoryDefaultLevels(logger);
        }

    }

    /**
     * Test {@code SLF4JLogger.log(SessionLogEntry)} method with regular {@link String} message.
     */
    public static final class TestLogMessage extends SLF4JLoggerTests {

        /**
         * Creates an instance of jUnit test.
         */
        public TestLogMessage() {
            super();
        }

        /**
         * Creates an instance of jUnit test.
         * @param name jUnit test name.
         */
        public TestLogMessage(final String name) {
            super(name);
        }

        /**
         * jUnit test method.
         */
        @Override
        public void test() {
            final SLF4JLogger logger = new SLF4JLogger();
            final SLF4JLoggerHelper helper = new SLF4JLoggerHelper(
                    (AbstractSession)session, loggerContext, logger);
            logger.setSession(session);
            helper.testLogMessage();
        }

    }

    /**
     * Test {@code SLF4JLogger.log(SessionLogEntry)} method with {@link Throwable} and stack trace logging turned off.
     */
    public static final class TestLogExceptionWithoutStackTrace extends SLF4JLoggerTests {

        /**
         * Creates an instance of jUnit test.
         */
        public TestLogExceptionWithoutStackTrace() {
            super();
        }

        /**
         * Creates an instance of jUnit test.
         * @param name jUnit test name.
         */
        public TestLogExceptionWithoutStackTrace(final String name) {
            super(name);
        }

        /**
         * jUnit test method.
         */
        @Override
        public void test() {
            final SLF4JLogger logger = new SLF4JLogger();
            final SLF4JLoggerHelper helper = new SLF4JLoggerHelper(
                    (AbstractSession)session, loggerContext, logger);
            logger.setSession(session);
            logger.setShouldLogExceptionStackTrace(false);
            helper.testLogExceptionWithoutStackTrace();
        }
    }

    /**
     * Test {@code SLF4JLogger.log(SessionLogEntry)} method with {@link Throwable} and stack trace logging turned on.
     */
    public static final class TestLogExceptionStackTrace extends SLF4JLoggerTests {
        /**
         * Creates an instance of jUnit test.
         */
        public TestLogExceptionStackTrace() {
            super();
        }

        /**
         * Creates an instance of jUnit test.
         * @param name jUnit test name.
         */
        public TestLogExceptionStackTrace(final String name) {
            super(name);
        }

        /**
         * jUnit test method.
         */
        @Override
        public void test() {
            final SLF4JLogger logger = new SLF4JLogger();
            final SLF4JLoggerHelper helper = new SLF4JLoggerHelper(
                    (AbstractSession)session, loggerContext, logger);
            logger.setSession(session);
            logger.setShouldLogExceptionStackTrace(true);
            helper.testLogExceptionStackTrace();
        }

    }

}
