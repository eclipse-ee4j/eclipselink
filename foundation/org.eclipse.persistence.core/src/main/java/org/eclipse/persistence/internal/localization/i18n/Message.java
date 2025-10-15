/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.internal.localization.i18n;

import java.util.function.Supplier;

import org.eclipse.persistence.internal.localization.LoggingLocalization;
import org.eclipse.persistence.internal.localization.TraceLocalization;

/**
 * Logging messages supplier factory.
 * Provides messages to be translated using specific localization message resource.
 */
public class Message {

    /**
     * Creates trace message supplier with no message arguments.
     * The message will be translated using {@link TraceLocalization} message resources.
     * Trace messages are being used for logging levels {@code >= SessionLog.CONFIG}.
     *
     * @param key the message key
     * @return the trace message {@link Supplier}
     */
    public static Supplier<String> trace(String key) {
        return new Trace(key, null);
    }

    /**
     * Creates trace message supplier with single message argument.
     * The message will be translated using {@link TraceLocalization} message resources.
     * Trace messages are being used for logging levels {@code >= SessionLog.CONFIG}.
     *
     * @param key the message key
     * @param argument the message arguments
     * @return the trace message {@link Supplier}
     */
    public static Supplier<String> trace(String key, Object argument) {
        return new Trace(key, new Object[] {argument});
    }

    /**
     * Creates trace message supplier with two message arguments.
     * The message will be translated using {@link TraceLocalization} message resources.
     * Trace messages are being used for logging levels {@code >= SessionLog.CONFIG}.
     *
     * @param key the message key
     * @param argument1 the 1st message argument
     * @param argument2 the 2nd message argument
     * @return the trace message {@link Supplier}
     */
    public static Supplier<String> trace(String key, Object argument1, Object argument2) {
        return new Trace(key, new Object[] {argument1, argument2});
    }

    /**
     * Creates trace message supplier with three message arguments.
     * The message will be translated using {@link TraceLocalization} message resources.
     * Trace messages are being used for logging levels {@code >= SessionLog.CONFIG}.
     *
     * @param key the message key
     * @param argument1 the 1st message argument
     * @param argument2 the 2nd message argument
     * @param argument3 the 3rd message argument
     * @return the trace message {@link Supplier}
     */
    public static Supplier<String> trace(String key, Object argument1, Object argument2, Object argument3) {
        return new Trace(key, new Object[] {argument1, argument2, argument3});
    }


    /**
     * Creates trace message supplier with four message arguments.
     * The message will be translated using {@link TraceLocalization} message resources.
     * Trace messages are being used for logging levels {@code >= SessionLog.CONFIG}.
     *
     * @param key the message key
     * @param argument1 the 1st message argument
     * @param argument2 the 2nd message argument
     * @param argument3 the 3rd message argument
     * @param argument4 the 4th message argument
     * @return the trace message {@link Supplier}
     */
    public static Supplier<String> trace(String key, Object argument1, Object argument2, Object argument3, Object argument4) {
        return new Trace(key, new Object[] {argument1, argument2, argument3, argument4});
    }

    /**
     * Creates trace message supplier with message arguments array.
     * The message will be translated using {@link TraceLocalization} message resources.
     * Trace messages are being used for logging levels {@code >= SessionLog.CONFIG}.
     *
     * @param key the message key
     * @param arguments the message arguments
     * @return the trace message {@link Supplier}
     */
    public static Supplier<String> trace(String key, Object[] arguments) {
        return new Trace(key, arguments);
    }

    /**
     * Creates logging message supplier with no message arguments.
     * The message will be translated using {@link LoggingLocalization} message resources.
     * Logging messages are being used for logging levels {@code <= SessionLog.FINE}.
     *
     * @param key the message key
     * @return the trace message {@link Supplier}
     */
    public static Supplier<String> logging(String key) {
        return new Logging(key, null);
    }

    /**
     * Creates logging message supplier with single message argument.
     * The message will be translated using {@link LoggingLocalization} message resources.
     * Logging messages are being used for logging levels {@code <= SessionLog.FINE}.
     *
     * @param key the message key
     * @param argument the message arguments
     * @return the trace message {@link Supplier}
     */
    public static Supplier<String> logging(String key, Object argument) {
        return new Logging(key, new Object[] {argument});
    }

    /**
     * Creates logging message supplier with two message arguments.
     * The message will be translated using {@link LoggingLocalization} message resources.
     * Logging messages are being used for logging levels {@code <= SessionLog.FINE}.
     *
     * @param key the message key
     * @param argument1 the 1st message argument
     * @param argument2 the 2nd message argument
     * @return the trace message {@link Supplier}
     */
    public static Supplier<String> logging(String key, Object argument1, Object argument2) {
        return new Logging(key, new Object[] {argument1, argument2});
    }

    /**
     * Creates logging message supplier with three message arguments.
     * The message will be translated using {@link LoggingLocalization} message resources.
     * Logging messages are being used for logging levels {@code <= SessionLog.FINE}.
     *
     * @param key the message key
     * @param argument1 the 1st message argument
     * @param argument2 the 2nd message argument
     * @param argument3 the 3rd message argument
     * @return the trace message {@link Supplier}
     */
    public static Supplier<String> logging(String key, Object argument1, Object argument2, Object argument3) {
        return new Logging(key, new Object[] {argument1, argument2, argument3});
    }

    /**
     * Creates logging message supplier with four message arguments.
     * The message will be translated using {@link LoggingLocalization} message resources.
     * Logging messages are being used for logging levels {@code <= SessionLog.FINE}.
     *
     * @param key the message key
     * @param argument1 the 1st message argument
     * @param argument2 the 2nd message argument
     * @param argument3 the 3rd message argument
     * @param argument4 the 4th message argument
     * @return the trace message {@link Supplier}
     */
    public static Supplier<String> logging(String key, Object argument1, Object argument2, Object argument3, Object argument4) {
        return new Logging(key, new Object[] {argument1, argument2, argument3, argument4});
    }

    /**
     * Creates logging message supplier with message arguments array.
     * The message will be translated using {@link LoggingLocalization} message resources.
     * Logging messages are being used for logging levels {@code <= SessionLog.FINE}.
     *
     * @param key the message key
     * @param arguments the message arguments
     * @return the trace message {@link Supplier}
     */
    public static Supplier<String> logging(String key, Object[] arguments) {
        return new Logging(key, arguments);
    }

    // Trace message supplier.
    private record Trace(String key, Object[] arguments) implements Supplier<String> {

        @Override
        public String get() {
            return TraceLocalization.buildMessage(key, arguments);
        }
    }

    // Logging message supplier.
    private record Logging(String key, Object[] arguments) implements Supplier<String> {

        @Override
        public String get() {
            return LoggingLocalization.buildMessage(key, arguments);
        }
    }

}
