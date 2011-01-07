/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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


/**
 * Adapt a JDK regular expression pattern to the StringMatcher
 * interface.
 */
public class RegExStringMatcherAdapter
	implements StringMatcher, Serializable
{
	/** The regular expression pattern used to match strings. */
	private Pattern pattern;

	private static final long serialVersionUID = 1L;


	// ********** constructors **********

	/**
	 * Construct an adapter for the specified regular expression.
	 */
	public RegExStringMatcherAdapter(String regularExpression) {
		this(regularExpression, 0);
	}

	/**
	 * Construct an adapter for the specified regular expression and flags.
	 */
	public RegExStringMatcherAdapter(String regularExpression, int flags) {
		super();
		this.setPatternString(regularExpression, flags);
	}

	/**
	 * Construct an adapter for the specified regular expression pattern.
	 */
	public RegExStringMatcherAdapter(Pattern pattern) {
		super();
		this.setPattern(pattern);
	}


	// ********** StringMatcher implementation **********

	/**
	 * @see StringMatcher#setPatternString(String)
	 */
	public void setPatternString(String patternString) {
		this.setPatternString(patternString, 0);
	}

	/**
	 * @see StringMatcher#matches(String)
	 */
	public synchronized boolean matches(String string) {
		return this.pattern.matcher(string).matches();
	}


	// ********** other public API **********

	public synchronized Pattern getPattern() {
		return this.pattern;
	}

	public synchronized void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public void setPatternString(String patternString, int flags) {
		this.setPattern(this.buildPattern(patternString, flags));
	}


	// ********** internal behavior **********

	/**
	 * Build and return a regular expression pattern that can be used
	 * to match strings.
	 */
	protected Pattern buildPattern(String regularExpression, int flags) {
		return Pattern.compile(regularExpression, flags);
	}

}
