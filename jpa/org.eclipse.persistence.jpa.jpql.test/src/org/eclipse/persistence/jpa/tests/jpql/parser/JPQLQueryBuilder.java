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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.eclipse.persistence.jpa.jpql.WordParser;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryFormatter.IdentifierStyle;
import org.eclipse.persistence.jpa.jpql.parser.ExpressionRegistry;
import org.eclipse.persistence.jpa.jpql.parser.IdentifierRole;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLStatementBNF;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;
import static org.junit.Assert.*;

/**
 * This builder creates the parsed tree representation of a JPQL query and verifies the generated
 * strings ({@link org.eclipse.persistence.jpa.jpql.parser.Expression#toActualText()
 * Expression.toActualText()} and {@link org.eclipse.persistence.jpa.jpql.parser.Expression#toParsedText()
 * Expression.toParsedText()}) were generated correctly.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused" /* For the extra import statement, see bug 330740 */})
public final class JPQLQueryBuilder {

	/**
	 * Parses the given JPQL query and tests its generated string with the given query, which will be
	 * formatted first.
	 *
	 * @param query The JPQL query to parse into a parsed tree
	 * @param jpqlGrammar The JPQL grammar that defines how to parse the given JPQL query
	 * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
	 * to parse invalid or incomplete queries
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String jpqlQuery, JPQLGrammar jpqlGrammar, boolean tolerant) {
		return buildQuery(jpqlQuery, jpqlGrammar, JPQLQueryStringFormatter.DEFAULT, tolerant);
	}

	/**
	 * Parses the given JPQL query and tests its generated string with the given query, which will be
	 * formatted first.
	 *
	 * @param query The JPQL query to parse into a parsed tree
	 * @param jpqlGrammar The JPQL grammar that defines how to parse the given JPQL query
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
	 * to parse invalid or incomplete queries
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String jpqlQuery,
	                                        JPQLGrammar jpqlGrammar,
	                                        JPQLQueryStringFormatter formatter,
	                                        boolean tolerant) {

		return buildQuery(jpqlQuery, jpqlGrammar, JPQLStatementBNF.ID, formatter, tolerant);
	}

	/**
	 * Parses the given JPQL query and tests its generated string with the given query, which will be
	 * formatted first.
	 *
	 * @param query The JPQL query to parse into a parsed tree
	 * @param jpqlGrammar The JPQL grammar that defines how to parse the given JPQL query
	 * @param jpqlQueryBNFId The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.
	 * parser.JPQLQueryBNF JPQLQueryBNF}
	 * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
	 * to parse invalid or incomplete queries
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String jpqlQuery,
	                                        JPQLGrammar jpqlGrammar,
	                                        String jpqlQueryBNFId,
	                                        boolean tolerant) {

		return buildQuery(jpqlQuery, jpqlGrammar, jpqlQueryBNFId, JPQLQueryStringFormatter.DEFAULT, tolerant);
	}

	/**
	 * Parses the given JPQL query and tests its generated string with the given query, which will be
	 * formatted first. Both the parsed and actual generated strings will be tested.
	 *
	 * @param jpqlQuery The JPQL query to parse into a parsed tree
	 * @param jpqlGrammar The JPQL grammar that defines how to parse the given JPQL query
	 * @param jpqlQueryBNFId The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.
	 * parser.JPQLQueryBNF JPQLQueryBNF}
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
	 * to parse invalid or incomplete queries
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String jpqlQuery,
	                                        JPQLGrammar jpqlGrammar,
	                                        String jpqlQueryBNFId,
	                                        JPQLQueryStringFormatter formatter,
	                                        boolean tolerant) {

		// Format the JPQL query to reflect how the parsed tree outputs the query back as a string
		String parsedJPQLQuery = toParsedText(jpqlQuery, jpqlGrammar);
		String actualJPQLQuery = toActualText(jpqlQuery, jpqlGrammar);

		// Format the JPQL query with this formatter so the invoker can tweak the default formatting
		parsedJPQLQuery = formatter.format(parsedJPQLQuery);
		actualJPQLQuery = formatter.format(actualJPQLQuery);

		// Parse the JPQL query
		JPQLExpression jpqlExpression = new JPQLExpression(jpqlQuery, jpqlGrammar, jpqlQueryBNFId, tolerant);

		// Make sure the JPQL query was correctly parsed and the
		// generated string matches the original JPQL query
		assertEquals(parsedJPQLQuery, jpqlExpression.toParsedText());
		assertEquals(actualJPQLQuery, jpqlExpression.toActualText());

		// If the JPQL query is parsed with tolerance turned off, then the query should
		// be completely parsed and there should not be any unknown ending statement
		if (!tolerant && (jpqlQueryBNFId == JPQLStatementBNF.ID)) {
			assertFalse(
				"A valid JPQL query cannot have an unknown ending fragment:" + jpqlQueryBNFId,
				jpqlExpression.hasUnknownEndingStatement()
			);
		}

		return jpqlExpression;
	}

	/**
	 * Formats the given JPQL query by converting it to what {@link org.eclipse.persistence.jpa.jpql.
	 * parser.Expression#toActualText() Expression.toActualText()} would return.
	 * <p>
	 * For instance, "Select e   From Employee e" will be converted to "Select e From Employee e".
	 *
	 * @param jpqlQuery The string to format
	 * @param jpqlGrammar The {@link JPQLGrammar} is used to properly format the string
	 * @return The converted string
	 * @see #toParsedText(String, JPQLGrammar)
	 * @see #toText(String, JPQLGrammar)
	 */
	public static String toActualText(String jpqlQuery, JPQLGrammar jpqlGrammar) {
		return toText(jpqlQuery, jpqlGrammar, true, IdentifierStyle.UPPERCASE);
	}

	/**
	 * Formats the given JPQL query by converting it to what {@link org.eclipse.persistence.jpa.jpql.
	 * parser.Expression#toParsedText() Expression.toParsedText()} would return.
	 * <p>
	 * For instance, "Select e   From Employee e" will be converted to "SELECT e FROM Employee e".
	 *
	 * @param jpqlQuery The string to format
	 * @param jpqlGrammar The {@link JPQLGrammar} is used to properly format the string
	 * @return The formatted JPQL query
	 * @see #toActualText(String, JPQLGrammar)
	 * @see #toText(String, JPQLGrammar)
	 */
	public static String toParsedText(String jpqlQuery, JPQLGrammar jpqlGrammar) {
		return toText(jpqlQuery, jpqlGrammar, false, IdentifierStyle.UPPERCASE);
	}

	/**
	 * Formats the given JPQL query by replacing multiple whitespace with a single whitespace. The
	 * JPQL identifiers will be converted to uppercase if <em>exactMatch</em> is <code>true</code>
	 * otherwise they will remain unchanged.
	 *
	 * @param jpqlQuery The string to format
	 * @param jpqlGrammar The {@link JPQLGrammar} is used to properly format the string
	 * @param exactMatch
	 * @param style
	 * @return The formatted JPQL query
	 * @see #toActualText(String, JPQLGrammar)
	 * @see #toParsedText(String, JPQLGrammar)
	 */
	public static String toText(String jpqlQuery,
	                            JPQLGrammar jpqlGrammar,
	                            boolean exactMatch,
	                            IdentifierStyle style) {

		ExpressionRegistry registry = jpqlGrammar.getExpressionRegistry();
		StringBuilder sb = new StringBuilder();
		WordParser wordParser = new WordParser(jpqlQuery);
		boolean singleQuoteParsed = false;
		Boolean fromClause = null;
		int whitespaceParsed = 0;

		for (int index = 0, count = jpqlQuery.length(); index < count; index++) {

			char character = jpqlQuery.charAt(index);

			// '
			if (character == SINGLE_QUOTE) {

				// Entering string literal
				if (!singleQuoteParsed) {
					singleQuoteParsed = true;
				}
				else {
					// Make sure the single quote is not escaped
					char nextCharacter = (index + 1 < count) ? jpqlQuery.charAt(index + 1) : '\0';

					// Exiting the string literal
					if (nextCharacter != SINGLE_QUOTE) {
						singleQuoteParsed = false;
					}
					// Skip the escaped '
					else {
						sb.append(character);
						index++;
					}
				}

				whitespaceParsed  = 0;
			}
			// Anything outside of string literal
			else if (!singleQuoteParsed) {

				// Will skip whitespace after the first one but not inside string literals
				if (Character.isWhitespace(character)) {

					// Leading whitespace is always removed
					if (sb.length() == 0) {
						continue;
					}

					// Make sure the whitespace is a real space
					character = SPACE;

					// '( ' will always be converted to '('
					char previousCharacter = (index > 0) ? jpqlQuery.charAt(index - 1) : '\0';

					if (previousCharacter == LEFT_PARENTHESIS) {

						// Skip any subsequent whitespace
						while (index < count) {
							previousCharacter = jpqlQuery.charAt(index + 1);
							if (!Character.isWhitespace(previousCharacter)) {
								break;
							}
							index++;
						}

						// Function without a closing parenthesis should have a whitespace after (
						// Example: "... ABS( FROM Employee e"
						if (!wordParser.startsWithIdentifier(FROM,     index + 1) &&
						    !wordParser.startsWithIdentifier(WHERE,    index + 1) &&
						    !wordParser.startsWithIdentifier(GROUP_BY, index + 1) &&
						    !wordParser.startsWithIdentifier(ORDER_BY, index + 1)) {

							continue;
						}
					}

					whitespaceParsed++;

					// Capitalize JPQL identifiers
					if (!exactMatch && (whitespaceParsed == 1)) {
						String identifier = wordParser.partialWord(index);
						int length = identifier.length();

						if ((length > 0) && registry.isIdentifier(identifier)) {

							// The word "ORDER"/"GROUP" is not the entity name but the identifier ORDER BY
							if ((fromClause == Boolean.TRUE) &&
							    (identifier.equalsIgnoreCase("ORDER") ||
							     identifier.equalsIgnoreCase("GROUP")) &&
							    (wordParser.startsWithIgnoreCase(ORDER_BY, index - 5) ||
							     wordParser.startsWithIgnoreCase(GROUP_BY, index - 5))) {

								fromClause = null;
							}

							// Special case where Order/Group should not be capitalized when it's the entity name
							if (!((fromClause == Boolean.TRUE) &&
							      (identifier.equalsIgnoreCase("ORDER") ||
							       identifier.equalsIgnoreCase("GROUP")))) {

								identifier = style.formatIdentifier(identifier);
								int offset = sb.length();
								sb.replace(offset - length, offset, identifier);
							}

							// The FROM clause should be parsed soon
							if ((fromClause == null) && SELECT.equalsIgnoreCase(identifier)) {
								fromClause = Boolean.FALSE;
							}
							// Entering the FROM clause
							else if ((fromClause == Boolean.FALSE) && FROM.equalsIgnoreCase(identifier)) {
								fromClause = Boolean.TRUE;
							}
							// Exiting the FROM clause
							else if ((fromClause == Boolean.TRUE) &&
							         (WHERE  .equalsIgnoreCase(identifier) ||
							          HAVING .equalsIgnoreCase(identifier) ||
							          "ORDER".equalsIgnoreCase(identifier) ||
							          "GROUP".equalsIgnoreCase(identifier))) {

								fromClause = null;
							}
						}
					}

					// Skip any subsequent whitespace
					if (whitespaceParsed > 1) {
						continue;
					}
				}
				// '('
				else if (character == LEFT_PARENTHESIS) {
					String previousWord = wordParser.partialWord(index - whitespaceParsed);

					// Remove the previous character, which is a whitespace and only if
					// it's after a function like "ABS (", which will be converted to "ABS("
					// but "WHERE (" will remain "WHERE (" (same with NEW)
					if (!NEW.equalsIgnoreCase(previousWord)) {

						if ((whitespaceParsed > 0) && !previousWord.equalsIgnoreCase(NEW)) {
							IdentifierRole role = registry.getIdentifierRole(previousWord);

							if ((role == IdentifierRole.FUNCTION) || IN.equalsIgnoreCase(previousWord)) {
								int offset = sb.length();
								sb.delete(offset - 1, offset);
							}
						}
						else {
							int length = previousWord.length();

							// Capitalize JPQL identifiers
							if (!exactMatch && (length > 0) && registry.isIdentifier(previousWord)) {
								previousWord = style.formatIdentifier(previousWord);
								int offset = sb.length();
								sb.replace(offset - length, offset, previousWord);
							}
						}
					}

					whitespaceParsed = 0;
				}
				// ')'
				// ','
				// Remove any whitespace before ')' or ','
				else if (character == RIGHT_PARENTHESIS ||
				         character == COMMA) {

					if (whitespaceParsed > 0) {
						int offset = sb.length();
						sb.delete(offset - 1, offset);
					}
					// Capitalize JPQL identifiers
					else if (!exactMatch) {
						String identifier = wordParser.partialWord(index);
						int length = identifier.length();

						if ((length > 0) && registry.isIdentifier(identifier)) {
							identifier = style.formatIdentifier(identifier);
							int offset = sb.length();
							sb.replace(offset - length, offset, identifier);
						}
					}

					// Add a whitespace after ','
					if ((character == COMMA) && (index + 1 < count)) {
						char nextCharacter = jpqlQuery.charAt(index + 1);

						// But not if the next character is ')' or ','
						if ((nextCharacter != COMMA) &&
						    (nextCharacter != RIGHT_PARENTHESIS) &&
						    !Character.isWhitespace(nextCharacter)) {

							sb.append(character);
							character = SPACE;
						}
					}

					whitespaceParsed = 0;
				}
				// Add a whitespace before and after *, /
				else if ((character == '*') ||
				         (character == '/')) {

					// Add a whitespace before
					if (whitespaceParsed == 0) {
						sb.append(' ');
					}

					// Add a whitespace after
					if (index + 1 < count) {
						char nextCharacter = jpqlQuery.charAt(index + 1);
						if (!Character.isWhitespace(nextCharacter)) {
							sb.append(character);
							character = SPACE;
						}
					}

					whitespaceParsed = 0;
				}
				// Add a whitespace before and after <, <=, =, >=, >, <>
				else if ((character == '=') ||
				         (character == '<') ||
				         (character == '>')) {

					char previousCharacter = jpqlQuery.charAt(index - 1);

					// Add a whitespace before
					if ((previousCharacter != '>') &&
					    (previousCharacter != '<') &&
					    (previousCharacter != '!') && /* Add support for something like !"#%/ */
					    (whitespaceParsed == 0)) {

						sb.append(' ');
					}

					// Add a whitespace after
					if (index + 1 < count) {
						char nextCharacter = jpqlQuery.charAt(index + 1);

						// Don't add a whitespace if it's <=, >= or <>
						if ((nextCharacter != '=') &&
						    (nextCharacter != '>') &&
						    !Character.isWhitespace(nextCharacter)) {

							sb.append(character);
							character = SPACE;
						}
					}

					whitespaceParsed = 0;
				}
				else {
					whitespaceParsed = 0;
				}
			}

			sb.append(character);

			// At the end of the query, make sure the last JPQL identifier is capitalized if required
			if (!exactMatch &&
			    (index + 1 == count) &&
			    (character != SINGLE_QUOTE) &&
			    (whitespaceParsed == 0)) {

				String previousWord = wordParser.partialWord(index + 1);
				int length = previousWord.length();

				// Capitalize JPQL identifiers
				if ((length > 0) && registry.isIdentifier(previousWord)) {
					previousWord = style.formatIdentifier(previousWord);
					int offset = sb.length();
					sb.replace(offset - length, offset, previousWord);
				}
			}
		}

		return sb.toString();
	}
}