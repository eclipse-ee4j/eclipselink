/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//      Tomas Kraus - Initial API and implementation.
package org.eclipse.persistence.logging;

import java.util.HashMap;
import java.util.Map;

/**
 * Log levels for EclipseLink logging.
 * The EclipseLink logging levels available are:<br>
 * <table summary="">
 * <tr><td>ALL</td>    <td>&nbsp;</td><td>=&nbsp;0</td>
 *     <td>This level currently logs at the same level as FINEST.</td></tr>
 * <tr><td>FINEST</td> <td>&nbsp;</td><td>=&nbsp;1</td>
 *     <td>This level enables logging of more debugging information than the FINER setting, such as a very detailed
 *         information about certain features (for example, sequencing). You may want to use this log level during
 *         debugging and testing, but not at production.</td></tr>
 * <tr><td>FINER</td>  <td>&nbsp;</td><td>=&nbsp;2</td>
 *     <td>This level enables logging of more debugging information than the FINE setting. For example, the transaction
 *         information is logged at this level. You may want to use this log level during debugging and testing,
 *         but not at production.</td></tr>
 * <tr><td>FINE</td>   <td>&nbsp;</td><td>=&nbsp;3</td>
 *     <td>This level enables logging of the first level of the debugging information and SQL. You may want to use
 *         this log level during debugging and testing, but not at production.</td></tr>
 * <tr><td>CONFIG</td> <td>&nbsp;</td><td>=&nbsp;4</td>
 *     <td>This level enables logging of such configuration details as your database login information and some metadata
 *         information. You may want to use the CONFIG log level at deployment time.</td></tr>
 * <tr><td>INFO</td>   <td>&nbsp;</td><td>=&nbsp;5</td>
 *     <td>This level enables the standard output. The contents of this output is very limited. It is the default
 *         logging level if a logging level is not set.</td></tr>
 * <tr><td>WARNING</td><td>&nbsp;</td><td>=&nbsp;6</td>
 *     <td>This level enables logging of issues that have a potential to cause problems. For example, a setting that
 *         is picked by the application and not by the user.</td></tr>
 * <tr><td>SEVERE</td> <td>&nbsp;</td><td>=&nbsp;7</td>
 *     <td>This level enables reporting of failure cases only. Usually, if the failure occurs, the application
 *         stops.</td></tr>
 * <tr><td>OFF</td>    <td>&nbsp;</td><td>=&nbsp;8</td>
 *     <td>This setting disables the generation of the log output. You may want to set logging to OFF during production
 *         to avoid the overhead of logging.</td></tr>
 * </table>
 */
public enum LogLevel {

    /** Log everything. */
    ALL(    (byte)0x00, "ALL"),
    /** Finest (the most detailed) logging level. */
    FINEST( (byte)0x01, "FINEST"),
    /** Finer logging level. */
    FINER(  (byte)0x02, "FINER"),
    /** Fine logging level. */
    FINE(   (byte)0x03, "FINE"),
    /** Configuration information logging level. */
    CONFIG( (byte)0x04, "CONFIG"),
    /** Informational messages. */
    INFO(   (byte)0x05, "INFO"),
    /** Exceptions that are not fatal. */
    WARNING((byte)0x06, "WARNING"),
    /** Fatal exceptions. */
    SEVERE( (byte)0x07, "SEVERE"),
    /** Logging is turned off. */
    OFF(    (byte)0x08, "OFF");

    /** Logging levels enumeration length. */
    public static final int length = LogLevel.values().length;

    /** {@link Map} for {@link String} to {@link LogLevel} case insensitive lookup. */
    private static final Map<String, LogLevel> stringValuesMap = new HashMap<String, LogLevel>(2 * length);

    // Initialize String to LogLevel case insensitive lookup Map.
    static {
        for (LogLevel logLevel : LogLevel.values()) {
            stringValuesMap.put(logLevel.name.toUpperCase(), logLevel);
        }
    }

    /** Array for id to {@link LogLevel} lookup. */
    private static final LogLevel idValues[] = new LogLevel[length];

    // Initialize id to LogLevel lookup array.
    static {
        for (LogLevel logLevel : LogLevel.values()) {
            idValues[logLevel.id] = logLevel;
        }
    }

    /**
     * Returns {@link LogLevel} object holding the value of the specified {@link String}.
     * @param name The {@link String} to be parsed.
     * @return {@link LogLevel} object holding the value represented by the string argument or {@code null} when
     *         there exists no corresponding {@link LogLevel} object to provided argument value.
     */
    public static final LogLevel toValue(final String name) {
        return name != null ? stringValuesMap.get(name.toUpperCase()) : null;
    }

    /**
     * Returns {@link LogLevel} object holding the value of the specified {@link String}.
     * @param name The {@link String} to be parsed.
     * @param fallBack {@link LogLevel} object to return on ID lookup failure.
     * @return {@link LogLevel} object holding the value represented by the string argument or {@code fallBack} when
     *         there exists no corresponding {@link LogLevel} object to provided argument value.
     */
    public static final LogLevel toValue(final String name, final LogLevel fallBack) {
        if (name != null) {
            final LogLevel level = stringValuesMap.get(name.toUpperCase());
            return level != null ? level : fallBack;
        } else {
            return fallBack;
        }
    }

    /**
     * Returns {@link LogLevel} object holding the value of the specified {@link LogLevel} ID.
     * @param id {@link LogLevel} ID.
     * @return {@link LogLevel} object holding the value represented by the {@code id} argument.
     * @throws IllegalArgumentException when {@link LogLevel} ID is out of valid {@link LogLevel} IDs range.
     */
    public static final LogLevel toValue(final int id) {
        if (id < 0 || id >= length) {
            throw new IllegalArgumentException(
                    "Log level ID " + id + "is out of range <0, " + Integer.toString(length) + ">.");
        }
        return idValues[id];
    }

    /**
     * Returns {@link LogLevel} object holding the value of the specified {@link LogLevel} ID.
     * @param id       {@link LogLevel} ID.
     * @param fallBack {@link LogLevel} object to return on ID lookup failure.
     * @return {@link LogLevel} object holding the value represented by the {@code id} argument or {@code fallBack}
     *         when provided ID is not valid {@link LogLevel} ID.
     * @throws IllegalArgumentException when {@link LogLevel} ID is out of valid {@link LogLevel} IDs range.
     */
    public static final LogLevel toValue(final int id, final LogLevel fallBack) {
        if (id >= 0 && id < length) {
            return idValues[id];
        }
        return fallBack;
    }

    // Holds value of SessionLog logging levels constants (e.g. ALL, FINES, FINER, ...).
    /** Logging level ID. Continuous integer sequence starting from 0. */
    private final byte id;

    /** Logging level name. */
    private final String name;

    /**
     * Creates an instance of logging level.
     * @param id   Logging level ID.
     * @param name Logging level name.
     */
    private LogLevel(final byte id, final String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Get logging level ID.
     * @return Logging level ID.
     */
    public byte getId() {
        return id;
    }

    /**
     * Get logging level name.
     * @return Logging level name.
     */
    public String getName() {
        return name;
    }

    /**
     * Check if a message of the given level would actually be logged under this logging level.
     * @param level Message logging level.
     * @return Value of {@code true} if the given message logging level will be logged or {@code false} otherwise.
     */
    public boolean shouldLog(final LogLevel level) {
        return this.id <= level.id;
    }

    /**
     * Check if a message of the given level ID would actually be logged under this logging level.
     * @param id Message logging level.
     * @return Value of {@code true} if the given message logging level will be logged or {@code false} otherwise.
     */
    public boolean shouldLog(final byte id) {
        return this.id <= id;
    }

}
