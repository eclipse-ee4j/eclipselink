/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
