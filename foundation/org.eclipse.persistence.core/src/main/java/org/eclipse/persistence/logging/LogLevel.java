/*
 * Copyright (c) 2016, 2021 Oracle and/or its affiliates. All rights reserved.
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
 * The EclipseLink logging levels.
 * <ul>
 *     <li><b>ALL:</b> This level currently logs at the same level as FINEST.</li>
 *     <li><b>FINEST:</b> This level enables logging of more debugging information than the FINER setting, such as a very detailed
 *            information about certain features (for example, sequencing). You may want to use this log level during
 *            debugging and testing, but not at production.</li>
 *     <li><b>FINER:</b> This level enables logging of more debugging information than the FINE setting. For example, the transaction
 *            information is logged at this level. You may want to use this log level during debugging and testing,
 *            but not at production.</li>
 *     <li><b>FINE:</b> This level enables logging of the first level of the debugging information and SQL. You may want to use
 *            this log level during debugging and testing, but not at production.</li>
 *     <li><b>CONFIG:</b> This level enables logging of such configuration details as your database login information and some metadata
 *            information. You may want to use the CONFIG log level at deployment time.</li>
 *     <li><b>INFO:</b> This level enables the standard output. The contents of this output is very limited. It is the default
 *            logging level if a logging level is not set.</li>
 *     <li><b>WARNING:</b> This level enables logging of issues that have a potential to cause problems. For example, a setting that
 *            is picked by the application and not by the user.</li>
 *     <li><b>SEVERE:</b> This level enables reporting of failure cases only. Usually, if the failure occurs, the application
 *            stops.</li>
 *     <li><b>OFF:</b> This setting disables the generation of the log output. You may want to set logging to OFF during production
 *            to avoid the overhead of logging.</li>
 * </ul>
 * <p>
 * The logging level IDs in {@linkplain SessionLog}:<br>
 * <table>
 * <caption>Logging levels</caption>
 * <tr><td>{@linkplain SessionLog#ALL}</td>    <td>&nbsp;</td><td>= {@value SessionLog#ALL}</td>
 * <tr><td>{@linkplain SessionLog#FINEST}</td> <td>&nbsp;</td><td>= {@value SessionLog#FINEST}</td>
 * <tr><td>{@linkplain SessionLog#FINER}</td>  <td>&nbsp;</td><td>= {@value SessionLog#FINER}</td>
 * <tr><td>{@linkplain SessionLog#FINE}</td>   <td>&nbsp;</td><td>= {@value SessionLog#FINE}</td>
 * <tr><td>{@linkplain SessionLog#CONFIG}</td> <td>&nbsp;</td><td>= {@value SessionLog#CONFIG}</td>
 * <tr><td>{@linkplain SessionLog#INFO}</td>   <td>&nbsp;</td><td>= {@value SessionLog#INFO}</td>
 * <tr><td>{@linkplain SessionLog#WARNING}</td><td>&nbsp;</td><td>= {@value SessionLog#WARNING}</td>
 * <tr><td>{@linkplain SessionLog#SEVERE}</td> <td>&nbsp;</td><td>= {@value SessionLog#SEVERE}</td>
 * <tr><td>{@linkplain SessionLog#OFF}</td>    <td>&nbsp;</td><td>= {@value SessionLog#OFF}</td>
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

    /** {@linkplain Map} for {@linkplain String} to {@linkplain LogLevel} case insensitive lookup. */
    private static final Map<String, LogLevel> stringValuesMap = new HashMap<>(2 * length);

    // Initialize String to LogLevel case insensitive lookup Map.
    static {
        for (LogLevel logLevel : LogLevel.values()) {
            stringValuesMap.put(logLevel.name.toUpperCase(), logLevel);
        }
    }

    /** Array for id to {@linkplain LogLevel} lookup. */
    private static final LogLevel[] idValues = new LogLevel[length];

    // Initialize id to LogLevel lookup array.
    static {
        for (LogLevel logLevel : LogLevel.values()) {
            idValues[logLevel.id] = logLevel;
        }
    }

    /**
     * Returns {@linkplain LogLevel} object holding the value of the specified {@linkplain String}.
     * @param name The {@linkplain String} to be parsed.
     * @return {@linkplain LogLevel} object holding the value represented by the string argument or {@code null} when
     *         there exists no corresponding {@linkplain LogLevel} object to provided argument value.
     */
    public static LogLevel toValue(final String name) {
        return name != null ? stringValuesMap.get(name.toUpperCase()) : null;
    }

    /**
     * Returns {@linkplain LogLevel} object holding the value of the specified {@linkplain String}.
     * @param name The {@linkplain String} to be parsed.
     * @param fallBack {@linkplain LogLevel} object to return on ID lookup failure.
     * @return {@linkplain LogLevel} object holding the value represented by the string argument or {@code fallBack} when
     *         there exists no corresponding {@linkplain LogLevel} object to provided argument value.
     */
    public static LogLevel toValue(final String name, final LogLevel fallBack) {
        if (name != null) {
            final LogLevel level = stringValuesMap.get(name.toUpperCase());
            return level != null ? level : fallBack;
        } else {
            return fallBack;
        }
    }

    /**
     * Returns {@linkplain LogLevel} object holding the value of the specified {@linkplain LogLevel} ID.
     * @param id {@linkplain LogLevel} ID.
     * @return {@linkplain LogLevel} object holding the value represented by the {@code id} argument.
     * @throws IllegalArgumentException when {@linkplain LogLevel} ID is out of valid {@linkplain LogLevel} IDs range.
     */
    public static LogLevel toValue(final int id) {
        if (id < 0 || id >= length) {
            throw new IllegalArgumentException(
                    "Log level ID " + id + "is out of range <0, " + length + ">.");
        }
        return idValues[id];
    }

    /**
     * Returns {@linkplain LogLevel} object holding the value of the specified {@linkplain LogLevel} ID.
     * @param id       {@linkplain LogLevel} ID.
     * @param fallBack {@linkplain LogLevel} object to return on ID lookup failure.
     * @return {@linkplain LogLevel} object holding the value represented by the {@code id} argument or {@code fallBack}
     *         when provided ID is not valid {@linkplain LogLevel} ID.
     * @throws IllegalArgumentException when {@linkplain LogLevel} ID is out of valid {@linkplain LogLevel} IDs range.
     */
    public static LogLevel toValue(final int id, final LogLevel fallBack) {
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
    LogLevel(final byte id, final String name) {
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
