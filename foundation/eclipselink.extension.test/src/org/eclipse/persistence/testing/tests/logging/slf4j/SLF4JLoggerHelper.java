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
//      Tomas Kraus - Initial implementation
package org.eclipse.persistence.testing.tests.logging.slf4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.LogCategory;
import org.eclipse.persistence.logging.LogLevel;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.logging.slf4j.SLF4JLogger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * {@code SLF4JLogger} jUnit tests helper that allows {@link org.eclipse.persistence.logging.LogCategory}
 * and {@link org.eclipse.persistence.logging.LogLevel} methods access.
 */
public class SLF4JLoggerHelper {

    /** Current EclipseLink session. */
    private final AbstractSession session;

    /** Logback logger context. */
    private final LoggerContext loggerContext;

    /** SLF4J logger used in tests. */
    private final SLF4JLogger logger;

    /**
     * Creates an instance of {@code SLF4JLogger} jUnit tests helper.
     * @param session       Current EclipseLink session.
     * @param loggerContext Logback logger context.
     * @param logger        SLF4J logger used in tests.
     */
    public SLF4JLoggerHelper(
            final AbstractSession session, final LoggerContext loggerContext, final SLF4JLogger logger) {
        this.session = session;
        this.loggerContext = loggerContext;
        this.logger = logger;
    }

    /**
     * Create a new instance of {@link SessionLogEntry} class and set provided session, log level and logging category
     * to it.
     * @param level    Log level of the new log entry.
     * @param category Logging category of the new log entry.
     * @return The new instance of {@link SessionLogEntry} class with all provided values set.
     */
    private SessionLogEntry initLogEntry(
            final LogCategory category, final LogLevel level) {
        final SessionLogEntry logEntry = new SessionLogEntry((AbstractSession)session);
        logEntry.setLevel(level.getId());
        logEntry.setNameSpace(category.getName());
        return logEntry;
    }

    /**
     * Create a new instance of {@link SessionLogEntry} class and set provided session, log level, logging category
     * and {@link Throwable} to it.
     * @param level     Log level of the new log entry.
     * @param category  Logging category of the new log entry.
     * @param throwable {@link Throwable} argument of the new log entry.
     * @return The new instance of {@link SessionLogEntry} class with all provided values set.
     */
    private SessionLogEntry createLogEntry(
            final LogCategory category, final LogLevel level, final Throwable throwable) {
        final SessionLogEntry logEntry = initLogEntry(category, level);
        logEntry.setException(throwable);
        return logEntry;
    }

    /**
     * Create a new instance of {@link SessionLogEntry} class and set provided session, log level, logging category
     * and {@link String} message to it.
     * @param level    Log level of the new log entry.
     * @param category Logging category of the new log entry.
     * @param message  {@link String} message of the new log entry.
     * @return The new instance of {@link SessionLogEntry} class with all provided values set.
     */
    private SessionLogEntry createLogEntry(
            final LogCategory category, final LogLevel level, final String message) {
        final SessionLogEntry logEntry = initLogEntry(category, level);
        logEntry.setMessage(message);
        return logEntry;
    }

    /**
     * Create a new instance of {@link SessionLogEntry} class and set provided session, log level, logging category,
     * {@link String} message and {@link Throwable} to it.
     * @param level     Log level of the new log entry.
     * @param category  Logging category of the new log entry.
     * @param message   {@link String} message of the new log entry.
     * @param throwable {@link Throwable} argument of the new log entry.
     * @return The new instance of {@link SessionLogEntry} class with all provided values set.
     */
    private SessionLogEntry createLogEntry(
            final LogCategory category, final LogLevel level, final String message, final Throwable throwable) {
        final SessionLogEntry logEntry = initLogEntry(category, level);
        logEntry.setMessage(message);
        logEntry.setException(throwable);
        return logEntry;
    }

    /**
     * Test {@code SLF4JLogger} default log levels for all logging categories.
     * @param logger {@code SLF4JLogger} instance with default log levels.
     */
    public void testCategoryDefaultLevels(final SLF4JLogger logger) {
        final int configuredDefaultLevel = AbstractSessionLog.getDefaultLoggingLevel();
        for (LogCategory category : LogCategory.values()) {
            final int defaultLevel = logger.getLevel(category.getName());
            switch(category) {
            case ALL:
                assertEquals("SLF4J logging level " + Integer.toString(defaultLevel)
                        + " does not match configured/default level " + Integer.toString(configuredDefaultLevel)
                        + " for category " + category.getName(), configuredDefaultLevel, defaultLevel);
                break;
            default:
                assertEquals("SLF4J logging level " + Integer.toString(defaultLevel)
                        + " does not match configured/default level " + Integer.toString(configuredDefaultLevel)
                        + " for category " + category.getName(), configuredDefaultLevel, defaultLevel);
            }
        }
    }

    /** Log entry check callback. */
    private interface Check {
        /**
         * Callback method implemented in individual test method.
         * @param logEvent Logback log event.
         */
        void check(final ILoggingEvent logEvent);
    }

    /**
     * Process log entry check for specified logger category, log level.
     * @param category Logger logging category.
     * @param loggerLevel Logger log level.
     * @param messageLevel Log entry log level.
     * @param categoryLogger Logback logger for given logging category.
     * @param logEntry Log entry to be logged and verified.
     * @param check Additional log entry checks callback.
     */
    private void testLogEntry(
            final LogCategory category, final LogLevel loggerLevel, final LogLevel messageLevel,
            final Logger categoryLogger, final SessionLogEntry logEntry, final Check check) {
        final byte loggerLevelId = loggerLevel.getId();
        final byte messageLevelId = messageLevel.getId();
        final ListAppender<ILoggingEvent> appender = new ListAppender<ILoggingEvent>();
        appender.setContext(loggerContext);
        categoryLogger.addAppender(appender);
        appender.start();
        logger.log(logEntry);
        appender.stop();
        final int appenderSize = appender.list.size();
        final ILoggingEvent logEvent = appenderSize > 0 ? appender.list.get(appenderSize - 1) : null;
        // Message must be logged.
        if (messageLevel != LogLevel.OFF && loggerLevelId <= messageLevelId) {
            assertNotNull("Missing log message for logger category " + category.getName() + ", level "
                     + loggerLevel.getName() + " and message level " + messageLevel.getName() , logEvent);
            check.check(logEvent);
        // Message shall not be logged.
        } else {
            assertNull("Found log message for logger category " + category.getName() + ", level "
                    + loggerLevel.getName() + " and message level " + messageLevel.getName() , logEvent);
        }
    }

    /**
     * Test {@code SLF4JLogger.log(SessionLogEntry)} method with regular {@link String} message.
     * Matrix of logger level x category settings with all log entry log levels is being checked.
     */
    public void testLogMessage() {
        // Verify loggers for logger level x category matrix.
        for (LogCategory category : LogCategory.values()) {
            final String nameSpace = category.getNameSpace();
            final Logger categoryLogger = loggerContext.getLogger(nameSpace);
            categoryLogger.setLevel(Level.ALL);
            for (LogLevel loggerLevel : LogLevel.values()) {
                // Verify messages with all log levels.
                logger.setLevel(loggerLevel.getId(), category.getName());
                for (LogLevel messageLevel : LogLevel.values()) {
                    final String message = "Log message";
                    final SessionLogEntry logEntry = createLogEntry(category, messageLevel, message);
                    // Logback log event additional check.
                    final Check check = new Check() {
                        @Override public void check(final ILoggingEvent logEvent) {
                            assertEquals("Logged message \"" + message + "\" must be stored as a message.",
                                    message, logEvent.getMessage());
                            assertNull("There can't be any arguments for already rendered message.",
                                    logEvent.getArgumentArray());
                        }
                    };
                    testLogEntry(category, loggerLevel, messageLevel, categoryLogger, logEntry, check);
                }
            }
        }
    }

    /**
     * Test {@code SLF4JLogger.log(SessionLogEntry)} method with {@link Throwable} and stack trace logging turned off.
     * Matrix of logger level x category settings with all log entry log levels is being checked.
     */
    public void testLogExceptionWithoutStackTrace() {
        // Verify loggers for logger level x category matrix.
        for (LogCategory category : LogCategory.values()) {
            final String nameSpace = category.getNameSpace();
            final Logger categoryLogger = loggerContext.getLogger(nameSpace);
            categoryLogger.setLevel(Level.ALL);
            for (LogLevel loggerLevel : LogLevel.values()) {
                // Verify messages with all log levels.
                logger.setLevel(loggerLevel.getId(), category.getName());
                for (LogLevel messageLevel : LogLevel.values()) {
                    final Throwable exception = new RuntimeException("Exception message");
                    final SessionLogEntry logEntry = createLogEntry(category, messageLevel, exception);
                    // Logback log event additional check.
                    final Check check = new Check() {
                        @Override public void check(final ILoggingEvent logEvent) {
                            assertEquals("Logged exception message \"" + exception.getMessage()
                                    + "\" must be stored as a message.", exception.toString(), logEvent.getMessage());
                            assertNull("There can't be any arguments for already rendered message.",
                                    logEvent.getArgumentArray());
                        }
                    };
                    testLogEntry(category, loggerLevel, messageLevel, categoryLogger, logEntry, check);
                }
            }
        }
    }

    /**
     * Test {@code SLF4JLogger.log(SessionLogEntry)} method with {@link Throwable} and stack trace logging turned on.
     * Matrix of logger level x category settings with all log entry log levels is being checked.
     */
    public void testLogExceptionStackTrace() {
        // Verify loggers for logger level x category matrix.
        for (LogCategory category : LogCategory.values()) {
            final String nameSpace = category.getNameSpace();
            final Logger categoryLogger = loggerContext.getLogger(nameSpace);
            categoryLogger.setLevel(Level.ALL);
            for (LogLevel loggerLevel : LogLevel.values()) {
                // Verify messages with all log levels.
                logger.setLevel(loggerLevel.getId(), category.getName());
                for (LogLevel messageLevel : LogLevel.values()) {
                    final String message = "Log message";
                    final String exceptionMessage = "Exception message";
                    final Throwable exception = new RuntimeException(exceptionMessage);
                    // Log entry without log message.
                    final SessionLogEntry logEntry1 = createLogEntry(category, messageLevel, exception);
                    // Log entry with log message.
                    final SessionLogEntry logEntry2 = createLogEntry(category, messageLevel, message, exception);
                    // Logback log event additional check for exception without log message.
                    final Check check1 = new Check() {
                        @Override
                        public void check(final ILoggingEvent logEvent) {
                            final String eventMessage = logEvent.getMessage();
                            assertTrue("No message was passed so null or empty String must be stored as a message.",
                                    eventMessage == null || eventMessage.isEmpty());
                            assertEquals("Exception message must be stored in throwableProxy.",
                                    exceptionMessage, logEvent.getThrowableProxy().getMessage());
                        }
                    };
                    // Logback log event additional check for exception with log message.
                    final Check check2 = new Check() {
                        @Override
                        public void check(final ILoggingEvent logEvent) {
                            assertEquals("Logged message \"" + message + "\" must be stored as a message.",
                                    message, logEvent.getMessage());
                            assertEquals("Exception message must be stored in throwableProxy.",
                                    exceptionMessage, logEvent.getThrowableProxy().getMessage());
                        }
                    };
                    testLogEntry(category, loggerLevel, messageLevel, categoryLogger, logEntry1, check1);
                    testLogEntry(category, loggerLevel, messageLevel, categoryLogger, logEntry2, check2);
                }
            }
        }

    }

}
