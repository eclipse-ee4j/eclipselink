/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.helper;

import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 *  INTERNAL:
 *  JavaPlatform abstracts the version of the JDK we are using.  It allows any operation
 *  which is dependent on JDK version to be called from a single place and then delegates
 *  the call to its JDKPlatform
 *  @see JDPlatform
 *  @author Tom Ware
 */
public class JavaPlatform {

    /**
     * PERF: The like expression compiled Pattern objects are cached
     * to avoid re-compilation on every usage.
     */
    private static final ConcurrentHashMap patternCache = new ConcurrentHashMap();

    /**
     * PERF: The regular expression compiled Pattern objects are cached
     * to avoid re-compilation on every usage.
     */
    private static final ConcurrentHashMap regexpPatternCache = new ConcurrentHashMap();

    /**
     * INTERNAL:
     * An implementation of in memory queries with Like which uses the
     * regular expression framework.
     */
    public static Boolean conformLike(Object left, Object right) {
        if ((left == null) && (right == null)) {
            return Boolean.TRUE;
        } else if ((left == null) || (right == null)) {
            return Boolean.FALSE;
        }
        left = String.valueOf(left);
        right = String.valueOf(right);
        // PERF: First check the pattern cache for the pattern.
        // Note that the original string is the key, to avoid having to translate it first.
        Pattern pattern = (Pattern)patternCache.get(right);
        if (pattern == null) {
            // Bug 3936427 - Replace regular expression reserved characters with escaped version of those characters
            // For instance replace ? with \?
            String convertedRight = Helper.convertLikeToRegex((String)right);

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
    public static Boolean conformRegexp(Object left, Object right) {
        if ((left == null) && (right == null)) {
            return Boolean.TRUE;
        } else if ((left == null) || (right == null)) {
            return Boolean.FALSE;
        }
        left = String.valueOf(left);
        right = String.valueOf(right);
        // PERF: First check the pattern cache for the pattern.
        // Note that the original string is the key, to avoid having to translate it first.
        Pattern pattern = (Pattern)regexpPatternCache.get(right);
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
     * INTERNAL:
     * Indicates whether the passed object implements java.sql.SQLXML introduced in jdk 1.6
     */
    public static boolean isSQLXML(Object object) {
        return (object instanceof SQLXML);
    }

    /**
     * INTERNAL:
     * Casts the passed object to SQLXML and calls getString and free methods
     */
    public static String getStringAndFreeSQLXML(Object sqlXml) throws SQLException {
        String str = ((SQLXML)sqlXml).getString();
        ((SQLXML)sqlXml).free();
        return str;
    }

}
