/*
 * Copyright (c) 2009, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Jaro Kuruc  - Initial API and implementation
//      Tomas Kraus - EclipseLink 2.7 integration
package org.eclipse.persistence.testing.tests.logging.slf4j;

import static org.junit.Assert.assertEquals;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.slf4j.SLF4JLogger;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.junit.LogTestExecution;
import org.eclipse.persistence.testing.tests.junit.logging.LogLevelHelper;
import org.eclipse.persistence.testing.tests.logging.LogTestSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

/**
 * Unit tests for EclipseLink Logger bridge over SLF4J.
 */
public class SLF4JLoggerTest {

    /** Log the test being currently executed. */
    @Rule public LogTestExecution logExecution = new LogTestExecution();

    /** Current EclipseLink session. */
    protected Session session;

    /** Logback logger context. */
    protected LoggerContext loggerContext;

    /**
     * Creates an instance of jUnit tests.
     */
    public SLF4JLoggerTest() {
    }

    /**
     * Setup unit test.
     */
    @Before
    public void setup() {
        session = new LogTestSession();
        session.setName("");
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();
    }

    /**
     * Clean this test suite.
     */
    @After
    public void reset() {
        session = null;
        loggerContext = null;
    }

    /**
     * Test {@code SLF4JLogger.getLevel()} method.
     */
    @Test
    public void testGetLevel() {
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

    /**
     * Test {@code SLF4JLogger.log(SessionLogEntry)} method with regular {@link String} message.
     */
    @Test
    public void testLogMessage() {
        final SLF4JLogger logger = new SLF4JLogger();
        final SLF4JLoggerHelper helper = new SLF4JLoggerHelper(
                (AbstractSession)session, loggerContext, logger);
        logger.setSession(session);
        helper.testLogMessage();
    }

    /**
     * Test {@code SLF4JLogger.log(SessionLogEntry)} method with {@link Throwable} and stack trace logging turned off.
     */
    @Test
    public void testLogExceptionWithoutStackTrace() {
        final SLF4JLogger logger = new SLF4JLogger();
        final SLF4JLoggerHelper helper = new SLF4JLoggerHelper(
                (AbstractSession)session, loggerContext, logger);
        logger.setSession(session);
        logger.setShouldLogExceptionStackTrace(false);
        helper.testLogExceptionWithoutStackTrace();
    }

    /**
     * Test {@code SLF4JLogger.log(SessionLogEntry)} method with {@link Throwable} and stack trace logging turned on.
     */
    @Test
    public void testLogExceptionStackTrace() {
        final SLF4JLogger logger = new SLF4JLogger();
        final SLF4JLoggerHelper helper = new SLF4JLoggerHelper(
                (AbstractSession)session, loggerContext, logger);
        logger.setSession(session);
        logger.setShouldLogExceptionStackTrace(true);
        helper.testLogExceptionStackTrace();
    }

}
