/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.utility.string;

import java.util.regex.Pattern;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.string.RegExStringMatcherAdapter;
import org.eclipse.persistence.tools.workbench.utility.string.StringMatcher;

public class RegularExpressionStringMatcherAdapterTests extends TestCase {

    public static Test suite() {
        return new TestSuite(RegularExpressionStringMatcherAdapterTests.class);
    }

    public RegularExpressionStringMatcherAdapterTests(String name) {
        super(name);
    }

    public void testRegEx() {
        StringMatcher matcher = new RegExStringMatcherAdapter("a*b");
        assertTrue(matcher.matches("ab"));
        assertTrue(matcher.matches("aaaaab"));

        assertFalse(matcher.matches("abc"));
        assertFalse(matcher.matches("AB"));
        assertFalse(matcher.matches("AAAAAB"));
    }

    public void testRegExCaseInsensitive() {
        StringMatcher matcher = new RegExStringMatcherAdapter("a*b", Pattern.CASE_INSENSITIVE);
        assertTrue(matcher.matches("ab"));
        assertTrue(matcher.matches("AB"));
        assertTrue(matcher.matches("aaaaab"));
        assertTrue(matcher.matches("AAAAAB"));

        assertFalse(matcher.matches("abc"));
    }

}
