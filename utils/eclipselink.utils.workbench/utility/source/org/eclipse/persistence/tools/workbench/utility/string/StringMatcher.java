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
package org.eclipse.persistence.tools.workbench.utility.string;

/**
 * This interface defines a simple API for allowing "pluggable"
 * string matchers that can be configured with a pattern string
 * then used to determine what strings match the pattern.
 */
public interface StringMatcher {

    /**
     * Set the pattern string used to determine future
     * matches. The format and semantics of the pattern
     * string are determined by the contract between the
     * client and the server.
     */
    void setPatternString(String patternString);

    /**
     * Return whether the specified string matches the
     * established pattern string. The semantics of a match
     * is determined by the contract between the
     * client and the server.
     */
    boolean matches(String string);


    StringMatcher NULL_INSTANCE =
        new StringMatcher() {
            public void setPatternString(String patternString) {
                // ignore the pattern string
            }
            public boolean matches(String string) {
                // everything is a match
                return true;
            }
            public String toString() {
                return "NullStringMatcher";
            }
        };

}
