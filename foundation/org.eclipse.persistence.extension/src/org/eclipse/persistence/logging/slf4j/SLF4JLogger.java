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
//      Jaro Kuruc  - Initial API and implementation.
//      Tomas Kraus - EclipseLink 2.7 integration.
package org.eclipse.persistence.logging.slf4j;

import java.security.AccessController;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetSystemProperty;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.LogCategory;
import org.eclipse.persistence.logging.LogLevel;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EclipseLink logger bridge over SLF4J.
 */
public class SLF4JLogger extends AbstractSessionLog {

    /** Logger callback interface. */
    private static interface LoggerCall {
        void log(final Logger logger, final String msg, final Throwable t);
        void log(final Logger logger, final String message);
    }

    /** {@code TRACE} level log. */
    private static final class LogTrace implements LoggerCall {
        @Override
        public void log(final Logger logger, final String msg, final Throwable t) {
            logger.trace(msg, t);
        }
        @Override
        public void log(final Logger logger, final String message) {
            logger.trace(message);
        }
    }

    /** {@code DEBUG} level log. */
    private static final class LogDebug implements LoggerCall {
        @Override
        public void log(final Logger logger, final String msg, final Throwable t) {
            logger.debug(msg, t);
        }
        @Override
        public void log(final Logger logger, final String message) {
            logger.debug(message);
        }
    }

    /** {@code INFO} level log. */
    private static final class LogInfo implements LoggerCall {
        @Override
        public void log(final Logger logger, final String msg, final Throwable t) {
            logger.info(msg, t);
        }
        @Override
        public void log(final Logger logger, final String message) {
            logger.info(message);
        }
    }

    /** {@code WARN} level log. */
    private static final class LogWarn implements LoggerCall {
        @Override
        public void log(final Logger logger, final String msg, final Throwable t) {
            logger.warn(msg, t);
        }
        @Override
        public void log(final Logger logger, final String message) {
            logger.warn(message);
        }
    }

    /** {@code ERROR} level log. */
    private static final class LogError implements LoggerCall {
        @Override
        public void log(final Logger logger, final String msg, final Throwable t) {
            logger.error(msg, t);
        }
        @Override
        public void log(final Logger logger, final String message) {
            logger.error(message);
        }
    }

    /** Do not log anything. */
    private static final class LogNop implements LoggerCall {
        @Override
        public void log(final Logger logger, final String msg, final Throwable t) {
        }
        @Override
        public void log(final Logger logger, final String message) {
        }
    }

    /** The default session name in case there is session name is missing. */
    public static final String ECLIPSELINK_NAMESPACE = "org.eclipse.persistence";

    /** SLF4J logger calls mapping for EclipseLink logging levels. */
    private static final LoggerCall[] loggerCall = new LoggerCall[LogLevel.length];

    /** Loggers lookup array. */
    private static final Logger[] categoryLoggers = new Logger[LogCategory.length];

    static {
        // Initialize loggers lookup array.
        for (int i = 0; i < LogCategory.length; i++) {
            categoryLoggers[i] = null;
        }
        // Initialize SLF4J logger calls mapping for EclipseLink logging levels.
        loggerCall[LogLevel.ALL.getId()]     = loggerCall[LogLevel.FINEST.getId()] = new LogTrace();
        loggerCall[LogLevel.FINER.getId()]   = loggerCall[LogLevel.FINE.getId()]   = new LogDebug();
        loggerCall[LogLevel.CONFIG.getId()]  = loggerCall[LogLevel.INFO.getId()]   = new LogInfo();
        loggerCall[LogLevel.WARNING.getId()] = new LogWarn();
        loggerCall[LogLevel.SEVERE.getId()]  = new LogError();
        loggerCall[LogLevel.OFF.getId()]     = new LogNop();
    }

    /**
     * Retrieve Logger for the given category.
     * @param category EclipseLink logging category
     * @return Logger for the given logging category.
     */
    private static Logger getLogger(final LogCategory category) {
        final Logger logger = categoryLoggers[category.getId()];
        if (logger != null) {
            return logger;
        }
        return categoryLoggers[category.getId()] = LoggerFactory.getLogger(category.getNameSpace());
    }

    /** Logging levels for individual logging categories. */
    private final LogLevel[] logLevels;

    /**
     * Creates an instance of EclipseLink logger bridge over SLF4J
     */
    public SLF4JLogger() {
        super();
        // Set default logging levels for all logging categories.
        final byte defaultLevel = LogLevel.toValue(level).getId();
        logLevels = new LogLevel[LogCategory.length];
        for (LogCategory category : LogCategory.values()) {
            final int i = category.getId();
            switch(category) {
            case ALL:
                logLevels[i] = LogLevel.toValue(defaultLevel);
                break;
            default:
                final String property = PersistenceUnitProperties.CATEGORY_LOGGING_LEVEL_ + category.getName();
                final String logLevelStr = PrivilegedAccessHelper.shouldUsePrivilegedAccess()
                        ? AccessController.doPrivileged(new PrivilegedGetSystemProperty(property))
                        : System.getProperty(property);
                logLevels[i] = LogLevel.toValue(
                        logLevelStr != null ? translateStringToLoggingLevel(logLevelStr) : defaultLevel);
            }
        }
    }

    /**
     * Get the logging level for the default logging category.
     * @return level Current logging level for default the default logging category.
     */
    @Override
    public int getLevel() {
        return logLevels[LogCategory.ALL.getId()].getId();
    }

    /**
     * Get the logging level for the specified logging category.
     * @param categoryName The {@link String} representation of an EclipseLink logging category.
     * @return level Current logging level for default the default logging category.
     */
    @Override
    public int getLevel(final String categoryName) {
        final LogCategory category = LogCategory.toValue(categoryName);
        if (category == null) {
            throw new IllegalArgumentException("Unknown logging category name.");
        }
        return logLevels[category.getId()].getId();
    }

    /**
     * Set the logging level for the default logging category.
     * @param level The logging level to be set.
     */
    @Override
    public void setLevel(final int level) {
        super.setLevel(level);
        logLevels[LogCategory.ALL.getId()] = LogLevel.toValue(level);
        // TODO: Handle logging levels on SLF4J side too.
    }

    /**
     * Set the logging level for the specified logging category.
     * @param level        The logging level to be set.
     * @param categoryName The {@link String} representation of an EclipseLink logging category.
     */
    @Override
    public void setLevel(final int level, final String categoryName) {
        final LogCategory category = LogCategory.toValue(categoryName);
        if (category == null) {
            throw new IllegalArgumentException("Unknown logging category name.");
        }
        logLevels[category.getId()] = LogLevel.toValue(level);
        // TODO: Handle logging levels on SLF4J side too.
    }

    /**
     * Check if a message of the given level would actually be logged under logging level for the default logging
     * category.
     * @param level Message logging level.
     * @return Value of {@code true} if the given message logging level will be logged or {@code false} otherwise.
     */
    @Override
    public boolean shouldLog(final int level) {
        return logLevels[LogCategory.ALL.getId()].shouldLog((byte)level);
    }

    /**
     * Check if a message of the given level would actually be logged under logging level for the specified logging
     * category.
     * @param level Message logging level.
     * @param categoryName The {@link String} representation of an EclipseLink logging category.
     * @return Value of {@code true} if the given message logging level will be logged or {@code false} otherwise.
     */
    @Override
    public boolean shouldLog(final int level, final String categoryName) {
        final LogCategory category = LogCategory.toValue(categoryName);
        if (category == null) {
            throw new IllegalArgumentException("Unknown logging category name.");
        }
        return logLevels[category.getId()].shouldLog((byte)level);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(final SessionLogEntry logEntry) {
        if (logEntry == null) {
            return;
        }
        final LogCategory category = LogCategory.toValue(logEntry.getNameSpace());
        if (category == null) {
            throw new IllegalArgumentException("Unknown logging category name.");
        }
        final byte levelId = (byte)logEntry.getLevel();
        if (logLevels[category.getId()].shouldLog(levelId)) {
            final LogLevel level = LogLevel.toValue(levelId);
            final Logger logger = getLogger(category);
            if (logEntry.hasException()) {
                if (shouldLogExceptionStackTrace()) {
                    // Message is rendered on EclipseLink side. SLF4J gets final String. Exception is passed too.
                    loggerCall[level.getId()].log(logger, formatMessage(logEntry), logEntry.getException());
                } else {
                    // Exception message is rendered on EclipseLink side. SLF4J gets final String.
                    loggerCall[level.getId()].log(logger, logEntry.getException().toString());
                }
            } else {
                // Message is rendered on EclipseLink side. SLF4J gets final String.
                loggerCall[level.getId()].log(logger, formatMessage(logEntry));
            }
        }
    }

}
