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
package org.eclipse.persistence.tools.workbench.utility.string;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;


// TODO the regex code is not very fast - we could probably do better,
// hand-coding the matching algorithm (eclipse StringMatcher?)
/**
 * This class implements a simple string-matching algorithm that is a little
 * more user-friendly than standard regular expressions. Instantiate a
 * string matcher with a filter pattern and then you can use the matcher
 * to determine whether another string (or object) matches the pattern.
 * You can also specify whether the matching should be case-sensitive.
 * 
 * The pattern can contain two "meta-characters":
 * 	'*' will match any set of zero or more characters
 * 	'?' will match any single character
 * 
 * Subclasses can override #prefix() and/or #suffix() to change what
 * strings are prepended or appended to the original pattern string.
 * This can offer a slight performance improvement over concatenating
 * strings before calling #setPatternString(String).
 * By default, a '*' is appended to every string.
 * 
 * This class also uses the string-matching algorithm to "filter" objects
 * (and, as a result, also implements the Filter interface).
 * A string converter is used to determine what string aspect of the
 * object is compared to the pattern. By default the string returned
 * by the object's #toString() method is passed to the pattern matcher.
 */
public class SimpleStringMatcher
	implements StringMatcher, Filter, Serializable
{

	/** An adapter that converts the objects into strings to be matched with the pattern. */
	private StringConverter stringConverter;

	/** The string used to construct the regular expression pattern. */
	private String patternString;

	/** Whether the matcher ignores case - the default is true. */
	private boolean ignoresCase;

	/** The regular expression pattern built from the pattern string. */
	private Pattern pattern;

	/** A list of the meta-characters we need to escape if found in the pattern string. */
	public static final char[] REG_EX_META_CHARS = { '(', '[', '{', '\\', '^', '$', '|', ')', '?', '*', '+', '.' };

	private static final long serialVersionUID = 1L;


	// ********** constructors **********

	/**
	 * Construct a string matcher with an pattern that will match
	 * any string and ignore case.
	 */
	public SimpleStringMatcher() {
		this("*");
	}

	/**
	 * Construct a string matcher with the specified pattern
	 * that will ignore case.
	 */
	public SimpleStringMatcher(String patternString) {
		this(patternString, true);
	}

	/**
	 * Construct a string matcher with the specified pattern that will
	 * ignore case as specified.
	 */
	public SimpleStringMatcher(String patternString, boolean ignoresCase) {
		super();
		this.patternString = patternString;
		this.ignoresCase = ignoresCase;
		this.initialize();
	}


	// ********** initialization **********

	protected void initialize() {
		this.stringConverter = StringConverter.DEFAULT_INSTANCE;
		this.rebuildPattern();
	}

	/**
	 * Given the current pattern string and case-sensitivity setting,
	 * re-build the regular expression pattern.
	 */
	protected synchronized void rebuildPattern() {
		this.pattern = this.buildPattern();
	}

	/**
	 * Given the current pattern string and case-sensitivity setting,
	 * build and return a regular expression pattern that can be used
	 * to match strings.
	 */
	protected Pattern buildPattern() {
		int patternFlags = 0x0;
		if (this.ignoresCase) {
			patternFlags = Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE;
		}
		return Pattern.compile(this.convertToRegEx(this.patternString), patternFlags);
	}


	// ********** StringMatcher implementation **********

	/**
	 * @see StringMatcher#setPatternString(String)
	 */
	public synchronized void setPatternString(String patternString) {
		this.patternString = patternString;
		this.rebuildPattern();
	}

	/**
	 * Return whether the specified string matches the pattern.
	 */
	public synchronized boolean matches(String string) {
		return this.pattern.matcher(string).matches();
	}


	// ********** Filter implementation **********

	/**
	 * @see Filter#accept(Object)
	 */
	public synchronized boolean accept(Object o) {
		return this.matches(this.stringConverter.convertToString(o));
	}


	// ********** accessors **********

	/**
	 * Return the string converter used to convert the objects
	 * passed to the matcher into strings.
	 */
	public synchronized StringConverter getStringConverter() {
		return this.stringConverter;
	}

	/**
	 * Set the string converter used to convert the objects
	 * passed to the matcher into strings.
	 */
	public synchronized void setStringConverter(StringConverter stringConverter) {
		this.stringConverter = stringConverter;
	}

	/**
	 * Return the original pattern string.
	 */
	public synchronized String getPatternString() {
		return this.patternString;
	}

	/**
	 * Return whether the matcher ignores case.
	 */
	public synchronized boolean ignoresCase() {
		return this.ignoresCase;
	}

	/**
	 * Set whether the matcher ignores case.
	 */
	public synchronized void setIgnoresCase(boolean ignoresCase) {
		this.ignoresCase = ignoresCase;
		this.rebuildPattern();
	}

	/**
	 * Return the regular expression pattern.
	 */
	public synchronized Pattern getPattern() {
		return this.pattern;
	}


	// ********** other public API **********

	/**
	 * Return the regular expression corresponding to
	 * the original pattern string.
	 */
	public synchronized String regularExpression() {
		return this.convertToRegEx(this.patternString);
	}


	// ********** converting **********

	/**
	 * Convert the specified string to a regular expression.
	 */
	protected String convertToRegEx(String string) {
		StringBuffer sb = new StringBuffer(string.length() + 10);
		this.convertToRegExOn(this.prefix(), sb);
		this.convertToRegExOn(string, sb);
		this.convertToRegExOn(this.suffix(), sb);
		return sb.toString();
	}

	/**
	 * Return any prefix that should be prepended to the original
	 * string. By default, there is no prefix.
	 */
	protected String prefix() {
		return "";
	}

	/**
	 * Return any suffix that should be appended to the original
	 * string. Since this class is typically used in UI situation where
	 * the user is typing in a pattern used to filter a list, the default
	 * suffix is a wildcard character.
	 */
	protected String suffix() {
		return "*";
	}

	/**
	 * Convert the specified string to a regular expression.
	 */
	protected void convertToRegExOn(String string, StringBuffer sb) {
		char[] charArray = string.toCharArray();
		int length = charArray.length;
		for (int i = 0; i < length; i++) {
			char c = charArray[i];
			// convert user-friendly meta-chars into regex meta-chars
			if (c == '*') {
				sb.append(".*");
				continue;
			}
			if (c == '?') {
				sb.append('.');
				continue;
			}
			// escape regex meta-chars
			if (CollectionTools.contains(REG_EX_META_CHARS, c)) {
				sb.append('\\');
			}
			sb.append(c);
		}
	}

}
