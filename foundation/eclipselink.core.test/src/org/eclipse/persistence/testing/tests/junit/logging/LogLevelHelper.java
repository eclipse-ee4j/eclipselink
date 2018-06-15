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
package org.eclipse.persistence.testing.tests.junit.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eclipse.persistence.logging.LogLevel;

/**
 * jUnit tests helper that allows {@link org.eclipse.persistence.logging.LogLevel}
 * methods access.
 */
public class LogLevelHelper {

    /**
     * Get {@code LogLevel} name corresponding to provided log level ID.
     * @param id Log level ID.
     * {@code LogLevel} instance corresponding to provided log level ID.
     */
    public static String logIdToName(final int id) {
        return LogLevel.toValue(id).getName();
    }

    /**
     * Test {@link LogLevel} enumeration length (items count).
     */
    public static void testLength() {
        assertEquals("Log level length value is incorrect", LogLevel.values().length, LogLevel.length);
    }

    /**
     * Test {@code LogLevel.toValue(String)} method.
     */
    public static void testToValueString() {
        // Check valid names.
        for (LogLevel level : LogLevel.values()) {
            String name = level.getName();
            String lower = name.toLowerCase();
            String upper = name.toUpperCase();
            LogLevel levelFromName = LogLevel.toValue(name);
            LogLevel levelFromLower = LogLevel.toValue(lower);
            LogLevel levelFromUpper = LogLevel.toValue(upper);
            assertEquals("Log level was not found for name: " + name, level, levelFromName);
            assertEquals("Log level was not found for name: " + lower, level, levelFromLower);
            assertEquals("Log level was not found for name: " + upper, level, levelFromUpper);
        }
        // Check some invalid names.
        final String[] invalidNames = new String[] {
                null, "", " " + LogLevel.ALL.getName(), LogLevel.ALL.getName() + " ", "unknown", "something", "AL",
                "ONFIG", "EVER"};
        for (String invalidName : invalidNames) {
            LogLevel level = LogLevel.toValue(invalidName);
            String levelName = level != null ? level.getName() : "null";
            assertEquals("Log level \"" + levelName + "\" was found for name: " + invalidName, null, level);
        }
    }

    /**
     * Test {@code LogLevel.toValue(int)} method.
     */
    public static void testToValueInt() {
        // Check valid IDs.
        for (LogLevel level : LogLevel.values()) {
            int id = level.getId();
            LogLevel levelValue = LogLevel.toValue(id);
            assertEquals("Log level was not found for ID: " + Integer.toString(id), level, levelValue);
        }
        // Check some invalid IDs.
        final int[] invalidIds = new int[] { -2, -1, LogLevel.length, LogLevel.length + 1};
        for (int id : invalidIds) {
            try {
                LogLevel.toValue(-1);
                fail("LogLevel.toValue(" + Integer.toString(id) + ") shall throw IllegalArgumentException");
            } catch (IllegalArgumentException ex) {
                // This exception is expected for illegal IDs.
            }
        }
    }

    /**
     * Test {@code LogLevel.toValue(int, LogLevel)} method.
     */
    public static void testToValueIntFallBack() {
        // Check valid IDs with fall back value different from expected value.
        for (LogLevel level : LogLevel.values()) {
            int id = level.getId();
            LogLevel levelValue = level == LogLevel.ALL
                    ? LogLevel.toValue(id, LogLevel.OFF) : LogLevel.toValue(id, LogLevel.ALL);
            assertEquals("Log level was not found for ID: " + Integer.toString(id), level, levelValue);
        }
        // Check some invalid IDs, expecting LogLevel.ALL as fall back.
        final int[] invalidIds = new int[] { -2, -1, LogLevel.length, LogLevel.length + 1};
        for (int id : invalidIds) {
            LogLevel level = LogLevel.toValue(id, LogLevel.ALL);
            assertEquals("Expected log level " + LogLevel.ALL.getName() + " for ID: " + Integer.toString(id),
                    level, LogLevel.ALL);
        }
    }

    /**
     * Test {@code LogLevel.shouldLog(LogLevel)} method.
     */
    public static void testShouldLogOnLogLevel() {
        for (LogLevel level : LogLevel.values()) {
            for (LogLevel toLog : LogLevel.values()) {
                boolean result = level.shouldLog(toLog);
                if (toLog.getId() >= level.getId()) {
                    assertEquals(
                            "Message with " + toLog.getName() + " level should be logged on level "
                            + level.getName(), true, result);
                } else  {
                    assertEquals(
                            "Message with " + toLog.getName() + " level should not be logged on level "
                            + level.getName(), false, result);
                }
            }
        }
    }

    /**
     * Test {@code LogLevel.shouldLog(LogLevel)} method.
     */
    public static void testShouldLogOnId() {
        for (LogLevel level : LogLevel.values()) {
            for (LogLevel toLog : LogLevel.values()) {
                boolean result = level.shouldLog(toLog.getId());
                if (toLog.getId() >= level.getId()) {
                    assertEquals(
                            "Message with " + toLog.getName() + " level should be logged on level "
                            + level.getName(), true, result);
                } else  {
                    assertEquals(
                            "Message with " + toLog.getName() + " level should not be logged on level "
                            + level.getName(), false, result);
                }
            }
        }
    }

}
