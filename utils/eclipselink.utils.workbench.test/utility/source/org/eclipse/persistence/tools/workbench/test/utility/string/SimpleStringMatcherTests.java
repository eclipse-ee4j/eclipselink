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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.string.SimpleStringMatcher;

public class SimpleStringMatcherTests extends TestCase {

	public static Test suite() {
		return new TestSuite(SimpleStringMatcherTests.class);
	}
	
	public SimpleStringMatcherTests(String name) {
		super(name);
	}
	
	public void testNormalString() {
		SimpleStringMatcher matcher = new SimpleStringMatcher("foo", false);
		assertTrue(matcher.matches("foo"));
		assertTrue(matcher.matches("foo2"));
		assertTrue(matcher.matches("foobarfoobarfoobar"));

		assertFalse(matcher.matches("barfoo"));
		assertFalse(matcher.matches("FOO"));
		assertFalse(matcher.matches("foO"));
		assertFalse(matcher.matches("FOObarfoobarfoobar"));
	}
	
	public void testIgnoresCase() {
		SimpleStringMatcher matcher = new SimpleStringMatcher("foo", true);
		assertTrue(matcher.matches("foo"));
		assertTrue(matcher.matches("Foo"));
		assertTrue(matcher.matches("FOO"));
		assertTrue(matcher.matches("foo2"));
		assertTrue(matcher.matches("foobarfoobarfoobar"));

		assertFalse(matcher.matches("bar"));
		assertFalse(matcher.matches("barfoo"));
	}
	
	public void testAsteriskWildCard() {
		SimpleStringMatcher matcher = new SimpleStringMatcher("foo*bar");
		assertTrue(matcher.matches("foobar"));
		assertTrue(matcher.matches("FooBAR"));
		assertTrue(matcher.matches("FOObaR"));
		assertTrue(matcher.matches("foo2BAR"));
		assertTrue(matcher.matches("foo2234234234bar"));
		assertTrue(matcher.matches("FOObaRasdfas"));
		assertTrue(matcher.matches("foo2BARasfasd"));
		assertTrue(matcher.matches("foo2234234234barasdfasd"));

		assertFalse(matcher.matches("barfoo"));
		assertFalse(matcher.matches("123412"));
	}
	
	public void testQuestionMarkWildCard() {
		SimpleStringMatcher matcher = new SimpleStringMatcher("foo?foo");
		assertTrue(matcher.matches("foo2foo"));
		assertTrue(matcher.matches("foo,foo"));
		assertTrue(matcher.matches("Foo2foo"));
		assertTrue(matcher.matches("fooFfoo"));
		assertTrue(matcher.matches("FOO9FOO"));
		assertTrue(matcher.matches("Foo2foo123412"));
		assertTrue(matcher.matches("fooFfoo123412"));
		assertTrue(matcher.matches("FOO9FOO12342"));

		assertFalse(matcher.matches("foo"));
		assertFalse(matcher.matches("foofoo"));
		assertFalse(matcher.matches("foo22foo"));
		assertFalse(matcher.matches("Foo"));
		assertFalse(matcher.matches("FOO"));
		assertFalse(matcher.matches("foo2234234234"));
		assertFalse(matcher.matches("foo223423??234"));
		assertFalse(matcher.matches("bar"));
	}
	
	public void testRegExMetaChars() {
		SimpleStringMatcher matcher = new SimpleStringMatcher("foo(foo");
		assertTrue(matcher.matches("foo(foo"));

		assertFalse(matcher.matches("foo\\(foo"));

		matcher = new SimpleStringMatcher("foo[foo");
		assertTrue(matcher.matches("foo[foo"));

		matcher = new SimpleStringMatcher("foo{foo");
		assertTrue(matcher.matches("foo{foo"));

		matcher = new SimpleStringMatcher("foo\\foo");
		assertTrue(matcher.matches("foo\\foo"));

		matcher = new SimpleStringMatcher("foo^foo");
		assertTrue(matcher.matches("foo^foo"));

		matcher = new SimpleStringMatcher("foo$foo");
		assertTrue(matcher.matches("foo$foo"));

		matcher = new SimpleStringMatcher("foo|foo");
		assertTrue(matcher.matches("foo|foo"));

		matcher = new SimpleStringMatcher("foo)foo");
		assertTrue(matcher.matches("foo)foo"));

		matcher = new SimpleStringMatcher("foo*foo");
		assertTrue(matcher.matches("foo*foo"));

		matcher = new SimpleStringMatcher("foo+foo");
		assertTrue(matcher.matches("foo+foo"));

		matcher = new SimpleStringMatcher("foo.foo");
		assertTrue(matcher.matches("foo.foo"));
	}
	
}
