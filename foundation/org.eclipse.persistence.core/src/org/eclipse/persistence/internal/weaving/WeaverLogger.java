/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.weaving;

import java.security.AccessController;

import org.eclipse.persistence.internal.localization.TraceLocalization;
import org.eclipse.persistence.internal.localization.i18n.TraceLocalizationResource;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetSystemProperty;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

/**
 * INTERNAL:
 * Log to EclipseLink {@code weaver} logger and optionally to standard error output
 * when {@code org.eclipse.persistence.jpa.weaving.log.stderr} JVM system property is set.
 * Logger API is based on {@link SessionLog} with {@code SessionLog.WEAVER} as EclipseLink logging category.
 * @since 2.6.1
 * @deprecated This is minimal temporary solution for 2.6. Will be removed in 2.7.
 */
public class WeaverLogger {

    /** Logger. */
    private static final SessionLog LOGGER = AbstractSessionLog.getLog();

    /** Name of property that turns standard error output on or off. */
    private static final String PROPERTY_NAME = "org.eclipse.persistence.jpa.weaving.log.stderr";

    /** {@link String} to separate logging category from message text in standard error output. */
    private static final String CATEGORY_SEPARATOR = ": ";

    /** {@link String} to prefix stack trace message in standard error output. */
    private static final String STACK_TRACE_PREFIX = " - ";

    /** Standard error output trigger. */
    private static final boolean doLogStdErr = initDoLogStdErr();

    /**
     * INTERNAL:
     * Initialize standard error output trigger.
     * @return Standard error output trigger value depending on {@code org.eclipse.persistence.jpa.weaving.log.stderr}
     *         system property value.
     */
    private static boolean initDoLogStdErr() {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            return Boolean.valueOf(AccessController.doPrivileged(new PrivilegedGetSystemProperty(PROPERTY_NAME)));
        } else {
            return Boolean.valueOf(System.getProperty(PROPERTY_NAME));
        }
    }

    /**
     * INTERNAL:
     * Check if a message of the given level would actually be logged by EclipseLink {@code weaver} logger
     * or to standard error output.
     * @param level    The log request level value.
     * @return Value of {@code true} if message  will be logged or {@code false} otherwise.
     */
    public static final boolean shouldLog(final int level) {
        return doLogStdErr || LOGGER.shouldLog(level, SessionLog.WEAVER);
    }

    /**
     * INTERNAL:
     * Check if a message of the given level would actually be logged by EclipseLink logger or to standard error output.
     * @param level    The log request level value.
     * @param category The EclipseLink logging category.
     * @return Value of {@code true} if message  will be logged or {@code false} otherwise.
     */
    public static final boolean shouldLog(final int level, final String category) {
        return doLogStdErr || LOGGER.shouldLog(level, category);
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
                TraceLocalization.buildMessage(messageKey) : TraceLocalization.buildMessage(messageKey, arguments);
        final int messageLength = message != null ? message.length() : 0;
        final StringBuilder sb = new StringBuilder(category.length() + CATEGORY_SEPARATOR.length() + messageLength);
        sb.append(category);
        sb.append(CATEGORY_SEPARATOR);
        if (messageLength > 0) {
            sb.append(message);
        }
        System.err.println(sb.toString());
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
     * Log message with no arguments to EclipseLink {@code weaver} logger and standard error output.
     * @param level      The log request level value.
     * @param messageKey The {@link TraceLocalizationResource} log message key.
     */
    public static final void log(final int level, final String messageKey) {
        LOGGER.log(level, SessionLog.WEAVER, messageKey, null);
        if (doLogStdErr) {
            logStdErr(SessionLog.WEAVER, messageKey);
        }
    }

    /**
     * INTERNAL:
     * Log message with arguments array to EclipseLink {@code weaver} logger and standard error output.
     * @param level      The log request level value.
     * @param messageKey {@link TraceLocalizationResource} message key.
     * @param arguments  Arguments of the log message.
     */
    public static final void log(final int level, final String messageKey, final Object... arguments) {
        LOGGER.log(level, SessionLog.WEAVER, messageKey, arguments);
        if (doLogStdErr) {
            logStdErr(SessionLog.WEAVER, messageKey, arguments);
        }
    }

    /**
     * INTERNAL:
     * Log {@link Throwable} to EclipseLink {@code weaver} logger and standard error output.
     * @param level     The log request level value.
     * @param exception {@link Throwable} to be logged.
     */
    public static final void logThrowable(final int level, final Throwable throwable) {
        LOGGER.logThrowable(level, SessionLog.WEAVER, throwable);
        if (doLogStdErr) {
            logThrowableStdErr(SessionLog.WEAVER, throwable);
        }
    }

}
