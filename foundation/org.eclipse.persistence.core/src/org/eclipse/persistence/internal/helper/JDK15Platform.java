/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.helper;

import java.sql.SQLException;
import java.util.regex.Pattern;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  INTERNAL:
 *  Implements operations specific to JDK 1.5
 */
public class JDK15Platform implements JDKPlatform {

    /**
     * PERF: The like expression compiled Pattern objects are cached
     * to avoid re-compilation on every usage.
     */
    protected static ConcurrentHashMap patternCache = new ConcurrentHashMap();

    /**
     * PERF: The regular expression compiled Pattern objects are cached
     * to avoid re-compilation on every usage.
     */
    protected static ConcurrentHashMap regexpPatternCache = new ConcurrentHashMap();

    /**
     * INTERNAL:
     * An implementation of in memory queries with Like which uses the
     * regular expression framework.
     */
    public Boolean conformLike(Object left, Object right) {
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
        boolean match = pattern.matcher((String)left).matches();
        if (match) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
    
    /**
     * INTERNAL:
     * An implementation of in memory queries with Regexp which uses the
     * regular expression framework.
     */
    public Boolean conformRegexp(Object left, Object right) {
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
        boolean match = pattern.matcher((String)left).matches();
        if (match) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
    
    /**
     * Indicates whether the passed object implements java.sql.SQLXML introduced in jdk 1.6
     */
    public boolean isSQLXML(Object object) {
        return false;
    }

    /**
     * Casts the passed object to SQLXML and calls getString and free methods
     */
    public String getStringAndFreeSQLXML(Object sqlXml) throws SQLException { 
        return null;
    }
}
