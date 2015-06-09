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
package org.eclipse.persistence.internal.jpa.weaving;

import org.eclipse.persistence.internal.localization.TraceLocalization;
import org.eclipse.persistence.internal.localization.i18n.TraceLocalizationResource;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

/**
 * INTERNAL:
 * Log to EclipseLink {@code weaver} logger and optionally to standard error output
 * when {@code org.eclipse.persistence.jpa.weaving.log.stderr} JVM system property is set.
 * Logger API is based on {@link SessionLog} with {code SessionLog.WEAVER} as EclipseLink logging category.
 * @since 2.6.1
 */
class WeaverLogger {

    /** Logger. */
    private static final SessionLog LOGGER = AbstractSessionLog.getLog();

    /** Name of property that turns standard error output on or off. */
    private static final String PROPERTY_NAME = "org.eclipse.persistence.jpa.weaving.log.stderr";

    /** Standard error output trigger. */
    private static final boolean doLogStdErr = Boolean.valueOf(System.getProperty(PROPERTY_NAME));

    /**
     * INTERNAL:
     * Check if a message of the given level would actually be logged by EclipseLink {@code weaver} logger
     * or to standard error output.
     * @param level The log request level value.
     * @return Value of {@code true} if message  will be logged or {@code false} otherwise.
     */
    static final boolean shouldLog(final int level) {
        return doLogStdErr || LOGGER.shouldLog(level, SessionLog.WEAVER);
    }

    /**
     * INTERNAL:
     * Log message with no arguments to EclipseLink {@code weaver} logger and standard error output.
     * @param level      The log request level value.
     * @param messageKey The {@link TraceLocalizationResource} log message key.
     */
    static final void log(final int level, final String messageKey) {
        LOGGER.log(level, SessionLog.WEAVER, messageKey, null);
        if (doLogStdErr) {
            System.err.println(TraceLocalization.buildMessage(messageKey));
        }
    }

    /**
     * INTERNAL:
     * Log message with a single argument to EclipseLink {@code weaver} logger and standard error output.
     * @param level      The log request level value.
     * @param messageKey The {@link TraceLocalizationResource} log message key.
     * @param argument   An argument of the log message.
     */
    static final void log(final int level, final String messageKey, final Object argument) {
        LOGGER.log(level, SessionLog.WEAVER, messageKey, argument);
        if (doLogStdErr) {
            System.err.println(TraceLocalization.buildMessage(messageKey, new Object[] {argument}));
        }
    }

    /**
     * INTERNAL:
     * Log message with arguments array to EclipseLink {@code weaver} logger and standard error output.
     * @param level      The log request level value.
     * @param messageKey {@link TraceLocalizationResource} message key.
     * @param arguments  Arguments of the log message.
     */
    static final void log(final int level, final String messageKey, final Object[] arguments) {
        LOGGER.log(level, SessionLog.WEAVER, messageKey, arguments);
        if (doLogStdErr) {
            System.err.println(TraceLocalization.buildMessage(messageKey, arguments));
        }
    }

    /**
     * INTERNAL:
     * Log {@link Throwable} to EclipseLink {@code weaver} logger and standard error output.
     * @param level     The log request level value.
     * @param exception {@link Throwable} to be logged.
     */
    static final void logThrowable(final int level, final Throwable throwable) {
        LOGGER.logThrowable(level, SessionLog.WEAVER, throwable);
        if (doLogStdErr) {
            for (StackTraceElement ste : throwable.getStackTrace()) {
                System.err.printf(" - %s", ste.toString());
                System.err.println();
            }
        }
    }

}
