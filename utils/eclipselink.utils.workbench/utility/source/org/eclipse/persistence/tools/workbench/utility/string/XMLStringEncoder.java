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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * This encoder will replace any of a specified set of characters with an XML
 * "character reference": '/' => "&#x2f;"
 */
public final class XMLStringEncoder {

	/** The set of characters to be converted into XML character references. */
	private final char[] chars;

	/** Cache the value of the highest character in the set above. */
	private final char maxChar;


	// ********** constructors/initialization **********

	/**
	 * Construct an encoder that converts the specified set of characters
	 * into XML character references.
	 */
	public XMLStringEncoder(char[] chars) {
		super();
		if (chars == null) {
			throw new NullPointerException();
		}
		// the ampersand must be included since it is the escape character
		if (CollectionTools.contains(chars, '&')) {
			this.chars = chars;
		} else {
			this.chars = CollectionTools.add(chars, '&');
		}
		this.maxChar = this.calculateMaxInvalidFileNameChar();
	}

	/**
	 * Calculate the maximum value of the set of characters to be converted
	 * into XML character references. This will be used to short-circuit the
	 * search for a character in the set.
	 * @see #charIsToBeEncoded(char)
	 */
	private char calculateMaxInvalidFileNameChar() {
		char[] localChars = this.chars;
		char max = 0;
		for (int i = localChars.length; i-- > 0; ) {
			char c = localChars[i];
			if (max < c) {
				max = c;
			}
		}
		return max;
	}


	// ********** API **********

	/**
	 * Return the specified string with any characters in the set
	 * replaced with XML character references.
	 */
	public String encode(String s) {
		int len = s.length();
		// allow for a few encoded characters
		StringBuffer sb = new StringBuffer(len + 20);
		for (int i = 0; i < len; i++) {
			this.appendCharacterTo(s.charAt(i), sb);
		}
		return sb.toString();
	}

	/**
	 * Return the specified string with any XML character references
	 * replaced by the characters themselves.
	 */
	public String decode(String s) {
		StringBuffer sb = new StringBuffer(s.length());
		StringBuffer temp = new StringBuffer();	// performance tweak
		this.decodeTo(new StringReader(s), sb, temp);
		return sb.toString();
	}


	// ********** internal methods **********

	/**
	 * Append the specified character to the string buffer,
	 * converting it to an XML character reference if necessary.
	 */
	private void appendCharacterTo(char c, StringBuffer sb) {
		if (this.charIsToBeEncoded(c)) {
			this.appendCharacterReferenceTo(c, sb);
		} else {
			sb.append(c);
		}
	}

	/**
	 * Return whether the specified character is one of the characters
	 * to be converted to XML character references.
	 */
	private boolean charIsToBeEncoded(char c) {
		return (c <= this.maxChar) && CollectionTools.contains(this.chars, c);
	}

	/**
	 * Append the specified character's XML character reference to the
	 * specified string buffer (e.g. '/' => "&#x2f;").
	 */
	private void appendCharacterReferenceTo(char c, StringBuffer sb) {
		sb.append("&#x");
		sb.append(Integer.toString(c, 16));
		sb.append(';');
	}

	private void decodeTo(Reader reader, StringBuffer sb, StringBuffer temp) {
		try {
			this.decodeToUnhandled(reader, sb, temp);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void decodeToUnhandled(Reader reader, StringBuffer sb, StringBuffer temp) throws IOException {
		int c = reader.read();
		while (c != -1) {
			if (c == '&') {
				this.decodeCharacterReferenceTo(reader, sb, temp);
			} else {
				sb.append((char) c);
			}
			c = reader.read();
		}
		reader.close();
	}

	private void decodeCharacterReferenceTo(Reader reader, StringBuffer sb, StringBuffer temp) throws IOException {
		int c = reader.read();
		this.checkChar(c, '#');
		c = reader.read();
		this.checkChar(c, 'x');

		temp.setLength(0);
		c = reader.read();
		while (c != ';') {
			this.checkEndOfStream(c);
			temp.append((char) c);
			c = reader.read();
		}
		String charValue = temp.toString();
		if (charValue.length() == 0) {
			throw new IllegalStateException("missing numeric string");
		}
		sb.append((char) Integer.parseInt(charValue, 16));
	}

	private void checkChar(int c, int expected) {
		this.checkEndOfStream(c);
		if (c != expected) {
			throw new IllegalStateException("expected '" + (char) expected + "', but encountered '" + (char) c + "'");
		}
	}

	private void checkEndOfStream(int c) {
		if (c == -1) {
			throw new IllegalStateException("unexpected end of string");
		}
	}

}
