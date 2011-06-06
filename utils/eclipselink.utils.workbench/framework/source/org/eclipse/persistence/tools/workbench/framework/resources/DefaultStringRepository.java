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
package org.eclipse.persistence.tools.workbench.framework.resources;

import java.text.MessageFormat;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * DefaultStringRepository extends SimpleStringRepository to
 * 1. strip out any mnemonic markers ('&') embedded in the strings
 * 	in the resource bundle
 * 2. format the strings in the resource with the arguments,
 * 	as defined by java.text.MessageFormat
 * 
 * @see java.text.MessageFormat
 * 
 * In the future, we might want to provide an escape character (e.g. '\')
 * that would allow the strings in the resource bundle to contain
 * ampersands that do not mark mnemonics (e.g. "Laurel \\& &Hardy").
 */
public class DefaultStringRepository extends ResourceBundleStringRepository {

	/**
	 * The character used to mark the mnemonic in a resource string.
	 * The character immediately following the ampersand is the
	 * string's "mnemonic" and will typically be underlined in the UI.
	 * The ampersand itself will not be displayed.
	 */
	public static final char MNEMONIC_CHAR = '&';


	// ********** constructors **********

	/**
	 * The strings in the specified resource bundle must
	 * 1. use an ampersand ('&') to mark their mnemonics
	 * 2. encode formatting information as defined by java.text.MessageFormat
	 */
	public DefaultStringRepository(String resourceClassName) {
		super(resourceClassName);
	}

	/**
	 * The strings in the specified resource bundle must
	 * 1. use an ampersand ('&') to mark their mnemonics
	 * 2. encode formatting information as defined by java.text.MessageFormat
	 */
	public DefaultStringRepository(Class resourceClass) {
		super(resourceClass);
	}


	// ********** overridden methods **********

	/**
	 * Return the string associated with the specified key,
	 * stripping off the first ampersand ('&'), if present.
	 */
	protected String get(String key) {
		String string = StringTools.removeFirstOccurrence(super.get(key), this.getMnemonicChar());
		return StringTools.replaceHTMLBreaks(string);
	}

	protected String format(String string, Object[] arguments) {
		if (arguments.length == 0) {
			return string;
		}
		return MessageFormat.format(string, arguments);
	}

	// ********** internal methods **********

	public char getMnemonicChar() {
		return MNEMONIC_CHAR;
	}

}
