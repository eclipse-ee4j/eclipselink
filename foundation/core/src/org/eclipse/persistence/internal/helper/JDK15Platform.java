/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.helper;

import java.util.regex.Pattern;
import java.util.Hashtable;
import java.util.Map;


/**
 *  INTERNAL:
 *  Implements operations specific to JDK 1.5
 */
public class JDK15Platform implements JDKPlatform {

    /**
     * Get a concurrent Map that allow concurrent gets but block on put.
     */
    public Map getConcurrentMap(){
    	return new java.util.concurrent.ConcurrentHashMap();
    }

    /**
     * INTERNAL
     * Get the Map to store the query cache in
     */
    public java.util.Map getQueryCacheMap() {
        return new java.util.concurrent.ConcurrentHashMap();
    }
    
    /**
     * PERF: The regular expression compiled Pattern objects are cached
     * to avoid recompilation on every usage.
     */
    protected static Hashtable patternCache = new Hashtable();

    /**
     * INTERNAL:
     * An implementation of in memory queries with Like which uses the JDK 1.4
     * regular expression framework.
     */
    public Boolean conformLike(Object left, Object right) {
        if ((left == null) && (right == null)) {
            return Boolean.TRUE;
        } else if ((left == null) || (right == null)) {
            return Boolean.FALSE;
        }
        if (left instanceof String && right instanceof String) {
            // PERF: First check the pattern cache for the pattern.
            // Note that the original string is the key, to avoid having to translate it first.
            Pattern pattern = (Pattern)patternCache.get(right);
            if (pattern == null) {
                // Bug 3936427 - Replace regular expression reserved characters with escaped version of those characters
                // For instance replace ? with \?
                String convertedRight = ((String)right).replaceAll("\\?", "\\\\?");
                convertedRight = convertedRight.replaceAll("\\*", "\\\\*");
                convertedRight = convertedRight.replaceAll("\\.", "\\\\.");
                convertedRight = convertedRight.replaceAll("\\[", "\\\\[");
                convertedRight = convertedRight.replaceAll("\\)", "\\\\)");
                convertedRight = convertedRight.replaceAll("\\(", "\\\\(");
                convertedRight = convertedRight.replaceAll("\\{", "\\\\{");
                convertedRight = convertedRight.replaceAll("\\+", "\\\\+");
                convertedRight = convertedRight.replaceAll("\\^", "\\\\^");
                convertedRight = convertedRight.replaceAll("\\|", "\\\\|");

                // regular expressions to substitute SQL wildcards with regex wildcards

                // Use look behind operators to replace "%" which is not preceded by "\" with ".*"
                convertedRight = convertedRight.replaceAll("(?<!\\\\)%", ".*");
                
                // Use look behind operators to replace "_" which is not preceded by "\" with "."
                convertedRight = convertedRight.replaceAll("(?<!\\\\)_", ".");   
                
                // replace "\%" with "%"
                convertedRight = convertedRight.replaceAll("\\\\%", "%");

                // replace "\_" with "_"
                convertedRight = convertedRight.replaceAll("\\\\_", "_");

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
        return null;
    }

    /**
     * INTERNAL:
     * Get the milliseconds from a Calendar.  In JDK 1.4, this can be accessed directly from the calendar.
     */
    public long getTimeInMillis(java.util.Calendar calendar) {
        return calendar.getTimeInMillis();
    }

    /**
     * INTERNAL:
     * Set the milliseconds for a Calendar.  In JDK 1.4, this can be set directly in the calendar.
     */
    public void setTimeInMillis(java.util.Calendar calendar, long millis) {
        calendar.setTimeInMillis(millis);
    }

    /**
     * INTERNAL:
     * Use API first available in JDK 1.4 to set the cause of an exception.
     */
    public void setExceptionCause(Throwable exception, Throwable cause) {
        if (exception.getCause() == null) {
            exception.initCause(cause);
        }
    }

    /**
     * INTERNAL
     * return a boolean which determines where TopLink should include the TopLink-stored
     * Internal exception in it's stack trace.  For JDK 1.4 VMs with exception chaining
     * the Internal exception can be redundant and confusing.
     * @return boolean will return false since JDK 1.4 does supports exception chaining
     */
    public boolean shouldPrintInternalException() {
        return false;
    }
}
