/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.parser.AbstractExpression;

/**
 * A utility class containing various methods related to the Hermes parser.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ExpressionTools {

	/**
	 * The constant of an empty array.
	 */
	public static final Object[] EMPTY_ARRAY = new Object[0];

	/**
	 * The constant for an empty string.
	 */
	public static final String EMPTY_STRING = "";

	/**
	 * The constant of an empty String array.
	 */
	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	/**
	 * ExpressionTools cannot be instantiated.
	 */
	private ExpressionTools() {
		super();
		throw new IllegalAccessError("ExpressionTools cannot be instantiated");
	}

	/**
	 * Converts the escape characters contained in the given {@link CharSequence} to their literal
	 * representation. For example, '\b' is converted to '\\b'.
	 *
	 * @param value The sequence of characters to convert any escape character
	 * @param position This is a one element array that needs to be adjusted when an escape
	 * character is converted
	 * @return The new sequence of characters that does not contain any escape character but it's
	 * literal representation
	 */
	public static String escape(CharSequence value, int[] position) {

		StringBuilder sb = new StringBuilder(value.length());
		int originalPosition = position[0];

		for (int index = 0, count = value.length(); index < count; index++) {
			char character = value.charAt(index);

			switch(character) {
				case '\b': sb.append("\\b");  if (index < originalPosition) position[0]++; break;
				case '\t': sb.append("\\t");  if (index < originalPosition) position[0]++; break;
				case '\n': sb.append("\\n");  if (index < originalPosition) position[0]++; break;
				case '\f': sb.append("\\f");  if (index < originalPosition) position[0]++; break;
				case '\r': sb.append("\\r");  if (index < originalPosition) position[0]++; break;
				case '\"': sb.append("\\\""); if (index < originalPosition) position[0]++; break;
				case '\\': sb.append("\\\\"); if (index < originalPosition) position[0]++; break;
				case '\0': sb.append("\\0");  if (index < originalPosition) position[0]++; break;
				case '\1': sb.append("\\1");  if (index < originalPosition) position[0]++; break;
				case '\2': sb.append("\\2");  if (index < originalPosition) position[0]++; break;
				case '\3': sb.append("\\3");  if (index < originalPosition) position[0]++; break;
				case '\4': sb.append("\\4");  if (index < originalPosition) position[0]++; break;
				case '\5': sb.append("\\5");  if (index < originalPosition) position[0]++; break;
				case '\6': sb.append("\\6");  if (index < originalPosition) position[0]++; break;
				case '\7': sb.append("\\7");  if (index < originalPosition) position[0]++; break;
				default:   sb.append(character);
			}
		}

		return sb.toString();
	}

	/**
	 * Determines whether the JPQL fragment is an expression of the form <code>&lt;IDENTIFIER&gt;(</code>.
	 *
	 * @param wordParser The text to parse based on the current position of the cursor
	 * @param identifier The identifier to verify if it's for an expression or for possibly for a
	 * variable name
	 * @return <code>true</code> if the identifier is followed by '('; <code>false</code> otherwise
	 */
	public static boolean isFunctionExpression(WordParser wordParser, String identifier) {

		// Skip the identifier
		int count = identifier.length();
		wordParser.moveForward(identifier);

		// Check to see if ( is following the identifier
		int whitespace = wordParser.skipLeadingWhitespace();
		boolean function = wordParser.startsWith(AbstractExpression.LEFT_PARENTHESIS);

		// Revert the changes
		wordParser.moveBackward(count + whitespace);

		return function;
	}

	/**
	 * Determines whether the given character is the character used to identify an input parameter,
	 * either a named parameter or position parameter.
	 *
	 * @param character The character to check if it's a parameter
	 * @return <code>true</code> if the given character is either : or ?; <code>false</code> otherwise
	 */
	public static boolean isParameter(char character) {
		return character == ':' ||
		       character == '?';
	}

	/**
	 * Determines whether the given character is the single or double quote.
	 *
	 * @param character The character to check if it's a quote
	 * @return <code>true</code> if the given character is either ' or "; <code>false</code> otherwise
	 */
	public static boolean isQuote(char character) {
		return character == '\'' ||
		       character == '\"';
	}

	/**
	 * Returns the given string literal by wrapping it with single quotes. Any single quote within
	 * the string will be escaped with another single quote.
	 *
	 * @param text The original text to quote
	 * @return The quoted text
	 * @since 2.4
	 */
	public static String quote(String text) {
		text = text.replace("'", "''");
		text = "'" + text + "'";
		return text;
	}

	/**
	 * Re-adjusts the given position, which is based on <em>query1</em>, by making sure it is
	 * pointing at the same position within <em>query2</em>. Usually the position is moved because
	 * the two queries don't have the same amount of whitespace.
	 *
	 * @param query1 The query associated to the position
	 * @param position1 The position within <em>query1</em>
	 * @param query2 The query for which the position might need adjustment
	 * @return The adjusted position by moving it based on the difference between <em>query1</em> and
	 * <em>query2</em>
	 */
	public static int repositionCursor(CharSequence query1, int position1, CharSequence query2) {

		// Nothing to adjust
		if (position1 <= 0) {
			return 0;
		}

		int queryLength1 = query1.length();
		int queryLength2 = query2.length();

		// Queries 1 and 2 have the same length, the position doesn't need to move
		if (queryLength1 == queryLength2) {
			return position1;
		}

		int position2 = position1;
		int index1 = 0;
		int index2 = 0;

		while ((index1 < queryLength1) && (index2 < queryLength2)) {

			char character1 = Character.toUpperCase(query1.charAt(index1));
			char character2 = Character.toUpperCase(query2.charAt(index2));

			if (character1 != character2) {

				boolean whitespace1 = Character.isWhitespace(character1);
				boolean whitespace2 = Character.isWhitespace(character2);

				// Query 2 does not have a whitespace but query 1 does
				if (whitespace1 && !whitespace2) {
					index1++;
					position2--;
				}
				// Query 1 does not have a whitespace but the query 2 does
				else if (!whitespace1 && whitespace2) {
					index2++;
					position2++;
				}
				// Continue with the next character
				else {
					index1++;
					index2++;
				}
			}
			// Continue with the next character
			else {
				index1++;
				index2++;
			}

			// The new position is most likely adjusted
			if (position2 == index2) {

				// Nothing more to adjust
				if (character1 == character2) {
					break;
				}

				// Now verify that query2 doesn't have more whitespace
				while (index2 < queryLength2) {

					character2 = Character.toUpperCase(query2.charAt(index2));

					// Query 2 does not have a whitespace but query 1 does
					if (Character.isWhitespace(character2)) {
						position2++;
						index2++;
					}
					else {
						break;
					}
				}

				break;
			}
		}

		return Math.min(position2, queryLength2);
	}

	/**
	 * Determines whether the specified string is <code>null</code>, empty, or contains only
	 * whitespace characters.
	 *
	 * @param text The sequence of character to test if it is <code>null</code> or only contains
	 * whitespace
	 * @return <code>true</code> if the given string is <code>null</code> or only contains whitespace;
	 * <code>false</code> otherwise
	 */
	public static boolean stringIsEmpty(CharSequence text) {

		if ((text == null) || (text.length() == 0)) {
			return true;
		}

		for (int i = text.length(); i-- > 0;) {
			if (!Character.isWhitespace(text.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Determines whether the specified string is NOT <code>null</code>, NOT empty, or contains at
	 * least one non-whitespace character.
	 *
	 * @param text The sequence of character to test if it is NOT <code>null</code> or does not only
	 * contain whitespace
	 * @return <code>true</code> if the given string is NOT <code>null</code> or has at least one
	 * non-whitespace character; <code>false</code> otherwise
	 */
	public static boolean stringIsNotEmpty(CharSequence text) {
		return !stringIsEmpty(text);
	}

	/**
	 * Determines whether the two sequence of characters are different, with the appropriate
	 * <code>null</code> checks and the case is ignored.
	 *
	 * @param value1 The first value to check for equality and equivalency
	 * @param value2 The second value to check for equality and equivalency
	 * @return <code>true</code> if both values are different; <code>false</code> otherwise
	 */
	public static boolean stringsAreDifferentIgnoreCase(CharSequence value1, CharSequence value2) {
		return !stringsAreEqualIgnoreCase(value1, value2);
	}

	/**
	 * Determines whether the two sequence of characters are equal or equivalent, with the
	 * appropriate <code>null</code> checks and the case is ignored.
	 *
	 * @param value1 The first value to check for equality and equivalency
	 * @param value2 The second value to check for equality and equivalency
	 * @return <code>true</code> if both values are <code>null</code>, equal or equivalent;
	 * <code>false</code> otherwise
	 */
	public static boolean stringsAreEqualIgnoreCase(CharSequence value1, CharSequence value2) {

		// Both are equal or both are null
		if ((value1 == value2) || (value1 == null) && (value2 == null)) {
			return true;
		}

		// One is null but the other is not
		if ((value1 == null) || (value2 == null)) {
			return false;
		}

		return value1.toString().equalsIgnoreCase(value2.toString());
	}

	/**
	 * Converts the string representation of the escape characters contained by the given {@link
	 * CharSequence} into the actual escape characters. For example, the string '\\b' is converted
	 * into the character value '\b'.
	 *
	 * @param value The sequence of characters to convert to an escaped version
	 * @param position This is a one element array that needs to be adjusted when an escape
	 * character is converted
	 * @return The new sequence of characters that contains escape characters rather than their
	 * string representation
	 */
	public static String unescape(CharSequence value, int[] position) {

		StringBuilder sb = new StringBuilder(value.length());
		int originalPosition = position[0];

		for (int index = 0, count = value.length(); index < count; index++) {

			char character = value.charAt(index);

			if ((character == '\\') && (index + 1 < count)) {
				character = value.charAt(++index);

				switch (character) {
					// Standard escape character
					case 'b':  sb.append("\b"); if (index <= originalPosition) position[0]--; break;
					case 't':  sb.append("\t"); if (index <= originalPosition) position[0]--; break;
					case 'n':  sb.append("\n"); if (index <= originalPosition) position[0]--; break;
					case 'f':  sb.append("\f"); if (index <= originalPosition) position[0]--; break;
					case 'r':  sb.append("\r"); if (index <= originalPosition) position[0]--; break;
					case '"':  sb.append("\""); if (index <= originalPosition) position[0]--; break;
					case '\\': sb.append("\\"); if (index <= originalPosition) position[0]--; break;
					case '0':  sb.append("\0"); if (index <= originalPosition) position[0]--; break;
					case '1':  sb.append("\1"); if (index <= originalPosition) position[0]--; break;
					case '2':  sb.append("\2"); if (index <= originalPosition) position[0]--; break;
					case '3':  sb.append("\3"); if (index <= originalPosition) position[0]--; break;
					case '4':  sb.append("\4"); if (index <= originalPosition) position[0]--; break;
					case '5':  sb.append("\5"); if (index <= originalPosition) position[0]--; break;
					case '6':  sb.append("\6"); if (index <= originalPosition) position[0]--; break;
					case '7':  sb.append("\7"); if (index <= originalPosition) position[0]--; break;

					// Unicode
					case 'u': {
						// Convert the hexadecimal digit into a char
						String hexadecimals = value.subSequence(index + 1, index + 5).toString();
						char unicode = (char) Integer.parseInt(hexadecimals, 16);
						sb.append(unicode);

						// Adjust the position and make sure if the position is within the unicode
						// value, then it's only adjusted to be at the beginning of the unicode value
						if ((originalPosition > index - 1) && (originalPosition <= index + 5)) {
							position[0] -= (originalPosition - index + 1);
						}
						else if (index <= originalPosition) {
							position[0] -= 5;
						}

						index += 4;
						break;
					}
					// Non-escape character
					default: {
						sb.append(character);
					}
				}
			}
			else {
				sb.append(character);
			}
		}

		return sb.toString();
	}

	/**
	 * Returns the string literal without the single or double quotes. Any two consecutive single
	 * quotes will be converted into a single quote.
	 *
	 * @param text The original text to unquote if it has ' at the beginning and the end
	 * @return The unquoted text
	 */
	public static String unquote(String text) {

		// Nothing to unquote
		if (stringIsEmpty(text)) {
			return text;
		}

		int startIndex = 0;
		int endIndex = text.length();

		// Skip the leading single quote
		if (isQuote(text.charAt(0))) {
			startIndex = 1;
		}

		// Skip the trailing single quote
		if ((endIndex - 1 > startIndex) && isQuote(text.charAt(endIndex - 1))) {
			endIndex--;
		}

		text = text.substring(startIndex, endIndex);
		text = text.replace("''", "'");
		return text;
	}

	/**
	 * Determines whether the values are different, with the appropriate <code>null</code> checks.
	 *
	 * @param value1 The first value to check for equality and equivalency
	 * @param value2 The second value to check for equality and equivalency
	 * @return <code>true</code> if both values are different; <code>true</code> if they are both
	 * <code>null</code>, equal or equivalent
	 */
	public static boolean valuesAreDifferent(Object value1, Object value2) {
		return !valuesAreEqual(value1, value2);
	}

	/**
	 * Determines whether the values are equal or equivalent, with the appropriate <code>null</code>
	 * checks.
	 *
	 * @param value1 The first value to check for equality and equivalency
	 * @param value2 The second value to check for equality and equivalency
	 * @return <code>true</code> if both values are <code>null</code>, equal or equivalent;
	 * <code>false</code> otherwise
	 */
	public static boolean valuesAreEqual(Object value1, Object value2) {

		// Both are equal or both are null
		if ((value1 == value2) || (value1 == null) && (value2 == null)) {
			return true;
		}

		// One is null but the other is not
		if ((value1 == null) || (value2 == null)) {
			return false;
		}

		return value1.equals(value2);
	}
}