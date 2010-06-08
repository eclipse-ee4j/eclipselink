/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.util.regex.Pattern;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 *  INTERNAL:
 *  Implements operations specific to JDK 1.5
 */
public class JDK15Platform implements JDKPlatform {

    /**
     * PERF: The regular expression compiled Pattern objects are cached
     * to avoid re-compilation on every usage.
     */
    protected static ConcurrentHashMap patternCache = new ConcurrentHashMap();
	
    /**
     * Get a concurrent Map that allow concurrent gets but block on put.
     */
    public Map getConcurrentMap(){
    	return new java.util.concurrent.ConcurrentHashMap();
    }
    
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
        left = String.valueOf(left);
        right = String.valueOf(right);
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

}
