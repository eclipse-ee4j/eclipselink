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
package org.eclipse.persistence.utils.jpa.query.parser;

/**
 * A utility class containing various methods.
 *
 * @version 11.2.0
 * @since 11.0.0
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
		return character == AbstractExpression.SINGLE_QUOTE ||
		       character == AbstractExpression.DOUBLE_QUOTE;
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

	/**
	 * Re-adjusts the position by making sure it is pointing correctly in the generated query
	 * since the parser doesn't keep extra whitespace.
	 *
	 * @param parsedQuery The string representation of the query that is generated from the parsed
	 * tree representation of the given actual query
	 * @param actualQuery The string representation of the query, which may have multiple whitespace
	 * @param position The position in the given query that needs to be adjusted in the string query
	 * generated by the parsed tree
	 * @return The position in the generated string query once the given query has been parsed
	 */
	// TODO: Probably should be query1, position1, query2 since position1 will be adjusted to be at
	//       the same position within query 2
	public static int repositionCursor(CharSequence query1,
	                                   CharSequence query2,
	                                   int position) {

		// Nothing to adjust
		if (position <= 0) {
			return 0;
		}

		int parsedQueryLength = query1.length();
		int actualQueryLength = query2.length();

		// The query and the generated query have the same length, the position doesn't need to move
		// since the text is exactly the same
		if (parsedQueryLength == actualQueryLength) {
			return position;
		}

		int newPosition = position;
		int parsedIndex = 0;
		int actualIndex = 0;

		while ((actualIndex < actualQueryLength) && (parsedIndex < parsedQueryLength)) {

			char parsedCharacter = query1.charAt(parsedIndex);
			char actualCharacter = query2.charAt(actualIndex);

			// The actual query does not have a whitespace but the parsed query does
			if ((actualCharacter == AbstractExpression.SPACE) &&
			    (parsedCharacter != AbstractExpression.SPACE)) {

				actualIndex++;
				newPosition--;
			}
			// The actual query does not have a whitespace but the parsed query does
			else if ((actualCharacter != AbstractExpression.SPACE) &&
			         (parsedCharacter == AbstractExpression.SPACE)) {

				parsedIndex++;
				newPosition++;
			}
			// Continue with the next character
			else {
				actualIndex++;
				parsedIndex++;
			}

			// We're done adjusting the position, as long as the parsed and actual characters are the
			// same, if not, we still need to continue to get to two identical characters
			if (newPosition == parsedIndex) {
				break;
			}
		}

		return newPosition;
	}
}