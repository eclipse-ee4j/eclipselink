/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     ailitchev - 2010/08/19
//          Bug 322960 - TWO TESTS IN CUSTOMFEATURESJUNITTESTSUITE FAILED WITH 11.2.0.2 DRIVER
//     Tomas Kraus - 2017/10/11
//          Bug 525854 - Fix Java SE platform detection and clean up platform code
package org.eclipse.persistence.expressions;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 *  INTERNAL:
 *  Utilities for in-memory conforming.
 *  @author Tom Ware
 */
final class ConformUtils {

    /**
     * PERF: The like expression compiled Pattern objects are cached
     * to avoid re-compilation on every usage.
     */
    private static final ConcurrentHashMap<Object, Pattern> patternCache = new ConcurrentHashMap<>();

    /**
     * PERF: The regular expression compiled Pattern objects are cached
     * to avoid re-compilation on every usage.
     */
    private static final ConcurrentHashMap<Object, Pattern> regexpPatternCache = new ConcurrentHashMap<>();

    /**
     * INTERNAL:
     * An implementation of in memory queries with Like which uses the
     * regular expression framework.
     */
    static Boolean conformLike(Object left, Object right) {
        if ((left == null) && (right == null)) {
            return Boolean.TRUE;
        } else if ((left == null) || (right == null)) {
            return Boolean.FALSE;
        }
        left = String.valueOf(left);
        right = String.valueOf(right);
        // PERF: First check the pattern cache for the pattern.
        // Note that the original string is the key, to avoid having to translate it first.
        Pattern pattern = patternCache.get(right);
        if (pattern == null) {
            // Bug 3936427 - Replace regular expression reserved characters with escaped version of those characters
            // For instance replace ? with \?
            String convertedRight = convertLikeToRegex((String)right);

            pattern = Pattern.compile(convertedRight);
            // Ensure cache does not grow beyond 100.
            if (patternCache.size() > 100) {
                patternCache.remove(patternCache.keySet().iterator().next());
            }
            patternCache.put(right, pattern);
        }
        return pattern.matcher((String)left).matches();
    }

    /**
     * INTERNAL:
     * An implementation of in memory queries with Regexp which uses the
     * regular expression framework.
     */
    static Boolean conformRegexp(Object left, Object right) {
        if ((left == null) && (right == null)) {
            return Boolean.TRUE;
        } else if ((left == null) || (right == null)) {
            return Boolean.FALSE;
        }
        left = String.valueOf(left);
        right = String.valueOf(right);
        // PERF: First check the pattern cache for the pattern.
        // Note that the original string is the key, to avoid having to translate it first.
        Pattern pattern = regexpPatternCache.get(right);
        if (pattern == null) {
            pattern = Pattern.compile((String)right);
            // Ensure cache does not grow beyond 100.
            if (regexpPatternCache.size() > 100) {
                regexpPatternCache.remove(regexpPatternCache.keySet().iterator().next());
            }
            regexpPatternCache.put(right, pattern);
        }
        return pattern.matcher((String)left).matches();
    }

    /**
     * Convert the SQL like pattern to a regex pattern.
     */
    static String convertLikeToRegex(String like) {
        // Bug 3936427 - Replace regular expression reserved characters with escaped version of those characters
        // For instance replace ? with \?
        String pattern = like.replaceAll("\\?", "\\\\?");
        pattern = pattern.replaceAll("\\*", "\\\\*");
        pattern = pattern.replaceAll("\\.", "\\\\.");
        pattern = pattern.replaceAll("\\[", "\\\\[");
        pattern = pattern.replaceAll("\\)", "\\\\)");
        pattern = pattern.replaceAll("\\(", "\\\\(");
        pattern = pattern.replaceAll("\\{", "\\\\{");
        pattern = pattern.replaceAll("\\+", "\\\\+");
        pattern = pattern.replaceAll("\\^", "\\\\^");
        pattern = pattern.replaceAll("\\|", "\\\\|");

        // regular expressions to substitute SQL wildcards with regex wildcards

        // Use look behind operators to replace "%" which is not preceded by "\" with ".*"
        pattern = pattern.replaceAll("(?<!\\\\)%", ".*");

        // Use look behind operators to replace "_" which is not preceded by "\" with "."
        pattern = pattern.replaceAll("(?<!\\\\)_", ".");

        // replace "\%" with "%"
        pattern = pattern.replaceAll("\\\\%", "%");

        // replace "\_" with "_"
        pattern = pattern.replaceAll("\\\\_", "_");
        // regex requires ^ and $ if pattern must start at start and end at end of string as like requires.
        pattern = "^" + pattern + "$";
        return pattern;
    }
}
