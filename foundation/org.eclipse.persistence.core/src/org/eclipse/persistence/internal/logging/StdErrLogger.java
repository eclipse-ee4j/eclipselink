/*******************************************************************************
 * Copyright (c) 2015, 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/08/2015-2.6 Tomas Kraus
 *       - initial API and implementation.
 ******************************************************************************/
package org.eclipse.persistence.internal.logging;

import java.security.AccessController;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.internal.localization.LoggingLocalization;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetSystemProperty;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

/**
 * INTERNAL:
 * Log to EclipseLink logger and optionally to standard error output.
 * Standard error output logging is enabled by:
 * <ul><li>{@code org.eclipse.persistence.jpa.log.stderr} for all logger categories</li>
 * <li>{@code org.eclipse.persistence.jpa.<category>.log.stderr} for individual logger category</li></ul>
 * Logger API is based on {@link SessionLog}.
 * @since 2.7
 */
public class StdErrLogger {

    /** Logger. */
    private static final SessionLog LOGGER = AbstractSessionLog.getLog();

    /** Standard error output logger property name prefix. */
    private static final String PROPERTY_NAME_PREFIX = "eclipselink.log";

    /** Standard error output logger property name suffix. */
    private static final String PROPERTY_NAME_SUFFIX = "stderr";

    /** Standard error output logger property name components separator. */
    private static final char PROPERTY_NAME_SEPARATOR = '.';

    /** {@link String} to separate logging category from message text in standard error output. */
    private static final String CATEGORY_SEPARATOR = ": ";

    /** {@link String} to prefix stack trace message in standard error output. */
    private static final String STACK_TRACE_PREFIX = " - ";

    /** Standard error output triggers for specific EclipseLink logging categories. */
    private static final Map<String, Boolean> logEnabledCategory = initLogEnabledCategory();

    // PERF: Global standard error output trigger to check before getting from Map.
    /** Global standard error output trigger. EclipseLink logging categories are allowed to be sent to standard error
     *  output when value is set to {@code true}. No standard error output will be done when value is set
     *  to {@code false}. */
    private static final boolean logEnabled = logEnabledCategory != null;

    /**
     * INTERNAL:
     * Initialize {@logEnabledCategory} standard error output triggers for specific EclipseLink logging categories.
     * @return Standard error output triggers {@link Map} for specific EclipseLink logging categories or {@code null}
     *         if no standard error output was enabled.
     */
    private static Map<String, Boolean> initLogEnabledCategory() {
        final int PropertyExtensionsLength = PROPERTY_NAME_PREFIX.length() + PROPERTY_NAME_SUFFIX.length() + 2;
        final Map<String, Boolean> logEnabledMap = new HashMap<>(SessionLog.loggerCatagories.length);
        boolean globalTrigger;
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            globalTrigger = Boolean.valueOf(AccessController.doPrivileged(new PrivilegedGetSystemProperty(
                    PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + PROPERTY_NAME_SUFFIX)));
        } else {
            globalTrigger = Boolean.valueOf(System.getProperty(
                    PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + PROPERTY_NAME_SUFFIX));
        }
        boolean enable = globalTrigger;
        for (final String category : SessionLog.loggerCatagories) {
            if (globalTrigger) {
                logEnabledMap.put(category, Boolean.TRUE);
            } else {
                StringBuilder propertyKey = new StringBuilder(PropertyExtensionsLength + category.length());
                propertyKey.append(PROPERTY_NAME_PREFIX);
                propertyKey.append(PROPERTY_NAME_SEPARATOR);
                propertyKey.append(category);
                propertyKey.append(PROPERTY_NAME_SEPARATOR);
                propertyKey.append(PROPERTY_NAME_SUFFIX);
                boolean trigger;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    trigger = Boolean.valueOf(AccessController.doPrivileged(
                            new PrivilegedGetSystemProperty(propertyKey.toString())));
                } else {
                    trigger = Boolean.valueOf(System.getProperty(propertyKey.toString()));
                }
                logEnabledMap.put(category, Boolean.valueOf(trigger));
                enable = enable || trigger;
            }
        }
        if (!enable) {
            logEnabledMap.clear();
        }
        return enable ? logEnabledMap : null;
    }

    /**
     * INTERNAL:
     * Check if a message of the given category would actually be logged to standard error output.
     * @return Value of {@code true} when a message of the given {@code category} would actually be logged to standard
     *         error output or {@code false} otherwise.
     */
    private static final boolean shouldLogToStdErr(final String category) {
        final Boolean enabled = logEnabledCategory.get(category);
        return enabled != null ? enabled.booleanValue() : false;
    }

    /**
     * INTERNAL:
     * Log message to standard error output.
     * @param category   The EclipseLink logging category.
     * @param messageKey The {@link TraceLocalizationResource} log message key.
     * @param arguments  Arguments of the log message.
     */
    private static void logStdErr(
            final String category, final String messageKey, final Object... arguments) {
        final String message = arguments == null || arguments.length == 0 ?
                LoggingLocalization.buildMessage(messageKey) : LoggingLocalization.buildMessage(messageKey, arguments);
        final int messageLength = message != null ? message.length() : 0;
        final int categoryLength = category != null ? category.length() + CATEGORY_SEPARATOR.length() : 0;
        if (categoryLength > 0 || messageLength > 0) {
            final StringBuilder sb = new StringBuilder(categoryLength + messageLength);
            if (categoryLength > 0) {
                sb.append(category);
                sb.append(CATEGORY_SEPARATOR);
            }
            if (messageLength > 0) {
                sb.append(message);
            }
            System.err.println(sb.toString());
        }
    }

    /**
     * INTERNAL:
     * Log message to standard error output.
     * @param category  The EclipseLink logging category.
     * @param throwable {@link Throwable} to be logged.
     */
    private static void logThrowableStdErr(final String category, final Throwable throwable) {
        final int categoryLength = category != null ? category.length() + CATEGORY_SEPARATOR.length() : 0;
        for (StackTraceElement ste : throwable.getStackTrace()) {
            final String message = ste.toString();
            StringBuilder sb = new StringBuilder(categoryLength + STACK_TRACE_PREFIX.length() + message.length());
            if (categoryLength > 0) {
                sb.append(category);
                sb.append(CATEGORY_SEPARATOR);
            }
            sb.append(STACK_TRACE_PREFIX);
            sb.append(message);
            System.err.println(sb.toString());
        }
    }

    /**
     * INTERNAL:
     * Check if a message of the given {@code category} and {@code level} would actually be logged by EclipseLink logger
     * or to standard error output.
     * @param level    The log request level value.
     * @param category The EclipseLink logging category.
     * @return Value of {@code true} if message  will be logged or {@code false} otherwise.
     */
    public static final boolean shouldLog(final int level, final String category) {
        return (logEnabled && shouldLogToStdErr(category)) || LOGGER.shouldLog(level, category);
    }

    /**
     * INTERNAL:
     * Log message with no arguments to EclipseLink logger and standard error output.
     * @param level      The log request level value.
     * @param category   The EclipseLink logging category.
     * @param messageKey The {@link TraceLocalizationResource} log message key.
     */
    public static final void log(final int level, final String category, final String messageKey) {
        LOGGER.log(level, category, messageKey, null);
        if (logEnabled && shouldLogToStdErr(category)) {
            logStdErr(category, messageKey);
        }
    }

    /**
     * INTERNAL:
     * Log message with arguments array to EclipseLink logger and standard error output.
     * @param level      The log request level value.
     * @param category   The EclipseLink logging category.
     * @param messageKey {@link TraceLocalizationResource} message key.
     * @param arguments  Arguments of the log message.
     */
    public static final void log(
            final int level, final String category, final String messageKey, final Object... arguments) {
        LOGGER.log(level, category, messageKey, arguments);
        if (logEnabled && shouldLogToStdErr(category)) {
            logStdErr(category, messageKey, arguments);
        }
    }

    /**
     * INTERNAL:
     * Log {@link Throwable} to EclipseLink logger and standard error output.
     * @param level     The log request level value.
     * @param category  The EclipseLink logging category.
     * @param throwable {@link Throwable} to be logged.
     */
    public static final void logThrowable(final int level, final String category, final Throwable throwable) {
        LOGGER.logThrowable(level, category, throwable);
        if (logEnabled && shouldLogToStdErr(category)) {
            logThrowableStdErr(category, throwable);
        }
    }

}
