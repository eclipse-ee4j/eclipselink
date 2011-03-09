/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.jpa.internal.jpql.parser.WordParser;

/**
 * A utility class containing various methods.
 *
 * @version 2.3
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
	 * Retrieves the first word from the given text starting at the specified position.
	 *
	 * @param text The text from which the first word will be retrieved
	 * @param position The position of the cursor where to start retreiving the word
	 * @return The first word contained in the text, if none could be found, then an empty string is
	 * returned
	 */
	public static String parseLiteral(WordParser wordParser) {

		int endIndex = wordParser.position() + 1;

		for (int length = wordParser.length(); endIndex < length; endIndex++) {
			char character = wordParser.character(endIndex);

			if (ExpressionTools.isQuote(character)) {
				endIndex++;

				// The single quote is escaped by '
				if ((endIndex < length) && ExpressionTools.isQuote(wordParser.character(endIndex))) {
					continue;
				}

				// Reached the end of the string literal
				break;
			}
		}

		return wordParser.substring(wordParser.position(), endIndex);
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

				// Query 2 does not have a whitespace but query 1 does
				if (Character.isWhitespace(character1) &&
				   !Character.isWhitespace(character2)) {

					index1++;
					position2--;
				}
				// Query 1 does not have a whitespace but the query 2 does
				else if (!Character.isWhitespace(character1) &&
				          Character.isWhitespace(character2)) {

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

				// Now verify that query2 doesn't have more whitespace
				while ((index1 < queryLength1) && (index2 < queryLength2)) {

					character1 = Character.toUpperCase(query1.charAt(index1));
					character2 = Character.toUpperCase(query2.charAt(index2));

					// Query 2 does not have a whitespace but query 1 does
					if (Character.isWhitespace(character1) &&
					   !Character.isWhitespace(character2)) {

						position2--;
						break;
					}
					else {
						break;
					}
				}

				break;
			}
		}

		return position2;
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
	 * Returns the string literal without the single quotes. Any two consecutive single quotes will
	 * be converted into a single quote.
	 *
	 * @param text The original text to unquote if it has ' at the beginning and the end
	 * @return The unquoted text
	 */
	public static String unquotedText(String text) {

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