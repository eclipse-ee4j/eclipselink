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

import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLStatementBNF;

import static org.junit.Assert.*;

/**
 * This builder creates the parsed tree representation of a JPQL query and verify {@link
 * Expression#toParsedText()} represents the same thing than.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
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
	 * formatted first.
	 *
	 * @param query The JPQL query to parse into a parsed tree
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

		// Remove any extra whitespace and make all identifiers upper case
		String realQuery = formatQuery(jpqlQuery);

		// For the JPQL query with this formatter so the invoker can tweak the default formatting
		realQuery = formatter.format(realQuery);

		// Parse the JPQL query
		JPQLExpression jpqlExpression = new JPQLExpression(jpqlQuery, jpqlGrammar, jpqlQueryBNFId, tolerant);

		// Make sure the JPQL query was correctly parsed and the generation
		// of the string representation matches the original JPQL query
		assertEquals(realQuery, jpqlExpression.toParsedText());

		// If the JPQL query is parsed in a non-tolerant mode, then the query should be completely
		// parsed and there should not be any unknown ending statement
		if (!tolerant && (jpqlQueryBNFId == JPQLStatementBNF.ID)) {
			assertFalse(
				"A valid JPQL query cannot have an unknown ending fragment:" + jpqlQueryBNFId,
				jpqlExpression.hasUnknownEndingStatement()
			);
		}

		return jpqlExpression;
	}

	public static String formatMinusSign(String jpqlQuery) {
		return jpqlQuery.replaceAll("\\s*\\-\\s*", " - ");
	}

	public static String formatPlusSign(String jpqlQuery) {
		return jpqlQuery.replaceAll("\\s*\\+\\s*", " + ");
	}

	/**
	 * Formats the given query by converting it to what JPQLExpression would returned. For instance,
	 * Select would be converted to SELECT.
	 * <p>
	 * <b>Note:</b> If JPQL identifiers are used inside of strings then they might be converted. For
	 * instance e.name = 'Pascal Null' will be converted to e.name = 'Pascal NULL'.
	 *
	 * @param query A JPA query to be formatted
	 * @return The formatted JPQL query
	 */
	public static String formatQuery(String jpqlQuery) {

		jpqlQuery = jpqlQuery.replaceAll("\r\n?",                                   " ");
		jpqlQuery = jpqlQuery.replaceAll("\\s+",                                    " ");
		jpqlQuery = jpqlQuery.replaceAll("\\',\\'",                                 "', '");
		jpqlQuery = jpqlQuery.replaceAll("[Ss][Ee][Ll][Ee][Cc][Tt] ",               "SELECT ");
		jpqlQuery = jpqlQuery.replaceAll("[Oo][Bb][Jj][Ee][Cc][Tt]",                "OBJECT");
		jpqlQuery = jpqlQuery.replaceAll("[Cc][Oo][Nn][Cc][Aa][Tt]",                "CONCAT");
		jpqlQuery = jpqlQuery.replaceAll("[Dd][Ii][Ss][Tt][Ii][Nn][Cc][Tt]",        "DISTINCT");
		jpqlQuery = jpqlQuery.replaceAll("[Gg][Rr][Oo][Uu][Pp]\\s+[Bb][Yy]",        "GROUP BY");
		jpqlQuery = jpqlQuery.replaceAll("[Oo][Rr][Dd][Ee][Rr]\\s+[Bb][Yy]",        "ORDER BY");
		jpqlQuery = jpqlQuery.replaceAll(" [Ff][Rr][Oo][Mm] ",                      " FROM ");
		jpqlQuery = jpqlQuery.replaceAll(" [Ww][Hh][Ee][Rr][Ee] ",                  " WHERE ");
		jpqlQuery = jpqlQuery.replaceAll(" [Ss][Uu][Mm]",                           " SUM");
		jpqlQuery = jpqlQuery.replaceAll(" [Hh][Aa][Vv][Ii][Nn][Gg] ",              " HAVING ");
		jpqlQuery = jpqlQuery.replaceAll(" [Aa][Nn][Dd] ",                          " AND ");
		jpqlQuery = jpqlQuery.replaceAll(" [Oo][Rr] ",                              " OR ");
		jpqlQuery = jpqlQuery.replaceAll(" [Cc][Oo][Uu][Nn][Tt]",                   " COUNT");
		jpqlQuery = jpqlQuery.replaceAll(" [Ee][Ss][Cc][Aa][Pp][Ee]",               " ESCAPE");
		jpqlQuery = jpqlQuery.replaceAll(" [Tt][Rr][Ii][Mm]",                       " TRIM");
		jpqlQuery = jpqlQuery.replaceAll(" [Ii][Nn]\\s?\\(",                        " IN(");
		jpqlQuery = jpqlQuery.replaceAll(" [Ii][Nn] ",                              " IN ");
		jpqlQuery = jpqlQuery.replaceAll(" [Ii][Ss]\\s\\[Nn][Uu][Ll][Ll]",          " IS NULL");
		jpqlQuery = jpqlQuery.replaceAll(" [Nn][Uu][Ll][Ll]",                       " NULL");
		jpqlQuery = jpqlQuery.replaceAll(" [Bb][Ee][Tt][Ww][Ee][Ee][Nn]",           " BETWEEN");
		jpqlQuery = jpqlQuery.replaceAll(" [Ll][Ee][Ff][Tt]",                       " LEFT");
		jpqlQuery = jpqlQuery.replaceAll(" [Oo][Uu][Tt][Ee][Rr]",                   " OUTER");
		jpqlQuery = jpqlQuery.replaceAll(" [Ii][Nn][Nn][Ee][Rr]",                   " INNER");
		jpqlQuery = jpqlQuery.replaceAll(" [Jj][Oo][Ii][Nn]",                       " JOIN");
		jpqlQuery = jpqlQuery.replaceAll(" [Nn][Oo][Tt]",                           " NOT");
		jpqlQuery = jpqlQuery.replaceAll(" [Ll][Ii][Kk][Ee]",                       " LIKE");
		jpqlQuery = jpqlQuery.replaceAll(" [Ii][Ss]",                               " IS");
		jpqlQuery = jpqlQuery.replaceAll(" [Aa][Ss] ",                              " AS ");
		jpqlQuery = jpqlQuery.replaceAll("[Aa][Ll][Ll]\\s?\\(",                     "ALL(");
		jpqlQuery = jpqlQuery.replaceAll("[Aa][Nn][Yy]\\s?\\(",                     "ANY(");
		jpqlQuery = jpqlQuery.replaceAll("[Aa][Vv][Gg]\\s?\\(",                     "AVG(");
		jpqlQuery = jpqlQuery.replaceAll("[Cc][Oo][Uu][Nn][Tt]\\s?\\(",             "COUNT(");
		jpqlQuery = jpqlQuery.replaceAll("[Ee][Xx][Ii][Ss][Tt][Ss]\\s?\\(",         "EXISTS(");
		jpqlQuery = jpqlQuery.replaceAll("[Ii][Nn]\\s?\\(",                         "IN(");
		jpqlQuery = jpqlQuery.replaceAll("[Ll][Oo][Ww][Ee][Rr]\\s?\\(",             "LOWER(");
		jpqlQuery = jpqlQuery.replaceAll("[Mm][Ii][Nn]\\s?\\(",                     "MIN(");
		jpqlQuery = jpqlQuery.replaceAll("[Mm][Aa][Xx]\\s?\\(",                     "MAX(");
		jpqlQuery = jpqlQuery.replaceAll("[Nn][Ee][Ww] ",                           "NEW ");
		jpqlQuery = jpqlQuery.replaceAll("[Ss][Oo][Mm][Ee]\\s?\\(",                 "SOME(");
		jpqlQuery = jpqlQuery.replaceAll("[Ss][Uu][Bb][Ss][Tt][Rr][Ii][Nn][Gg]",    "SUBSTRING");
		jpqlQuery = jpqlQuery.replaceAll("[Ss][Uu][Mm]\\s?\\(",                     "SUM(");
		jpqlQuery = jpqlQuery.replaceAll("OBJECT\\s\\(",                            "OBJECT(");
		jpqlQuery = jpqlQuery.replaceAll("[Tt][Rr][Ii][Mm]\\s\\(",                  "TRIM(");
		jpqlQuery = jpqlQuery.replaceAll("[Uu][Pp][Pp][Ee][Rr]\\s?\\(",             "UPPER(");
		jpqlQuery = jpqlQuery.replaceAll("[Nn][uU][lL][lL][iI][Ff]\\s?\\(",         "NULLIF(");
		jpqlQuery = jpqlQuery.replaceAll("[Kk][Ee][Yy]\\s?\\(",                     "KEY(");
		jpqlQuery = jpqlQuery.replaceAll("[Vv][Aa][Ll][Uu][Ee]\\s?\\(",             "VALUE(");
		jpqlQuery = jpqlQuery.replaceAll("[Cc][Oo][Aa][Ll][Ee][Ss][Cc][Ee]\\s?\\(", "COALESCE(");
		jpqlQuery = jpqlQuery.replaceAll("[Ff][Uu][Nn][Cc]\\s?\\(",                 "FUNC(");
		jpqlQuery = jpqlQuery.replaceAll("[Tr][Ee][Aa][Tt]\\s?\\(",                 "TREAT(");
		jpqlQuery = jpqlQuery.replaceAll("\\s?/\\s?",                               " / ");
		jpqlQuery = jpqlQuery.replaceAll("\\s?\\*\\s?",                             " * ");
		jpqlQuery = jpqlQuery.replaceAll("\\)\\s,",                                 "),");
		jpqlQuery = jpqlQuery.replaceAll("\\(\\s?",                                 "(");
		jpqlQuery = jpqlQuery.replaceAll("\\s?\\)",                                 ")");

		jpqlQuery = jpqlQuery.replaceAll("\\)AND",       ") AND");
		jpqlQuery = jpqlQuery.replaceAll("\\)OR",        ") OR");
		jpqlQuery = jpqlQuery.replaceAll("AND\\(",       "AND (");
		jpqlQuery = jpqlQuery.replaceAll("OR\\(",        "OR (");
		jpqlQuery = jpqlQuery.replaceAll("WHERE\\(",     "WHERE (");
		jpqlQuery = jpqlQuery.replaceAll("\\)GROUP BY",  ") GROUP BY");
		jpqlQuery = jpqlQuery.replaceAll("\\)ORDER BY",  ") ORDER BY");
		jpqlQuery = jpqlQuery.replaceAll("\\)HAVING",    ") HAVING");
		jpqlQuery = jpqlQuery.replaceAll("\\)FROM",      ") FROM");
		jpqlQuery = jpqlQuery.replaceAll("\\)WHERE",     ") WHERE");
		jpqlQuery = jpqlQuery.replaceAll("\\(GROUP BY",  "( GROUP BY");
		jpqlQuery = jpqlQuery.replaceAll("\\(ORDER BY",  "( ORDER BY");
		jpqlQuery = jpqlQuery.replaceAll("\\(HAVING",    "( HAVING");
		jpqlQuery = jpqlQuery.replaceAll("\\(FROM",      "( FROM");
		jpqlQuery = jpqlQuery.replaceAll("\\(WHERE",     "( WHERE");

		StringBuilder sb = new StringBuilder(jpqlQuery);

		spaceOutEqualBefore(sb);
		spaceOutEqualAfter(sb);

		// Handle trailing and ending whitespace
		boolean endsWithWhiteSpace = false;

		if (sb.length() > 0) {
			endsWithWhiteSpace = sb.charAt(sb.length() - 1) == ' ';
		}

		trimWhitespace(sb);

		if (endsWithWhiteSpace) {
			sb.append(' ');
		}

		return sb.toString();
	}

	private static void spaceOutEqualAfter(StringBuilder sb) {

		// Replace "=\\S" to "= \\S"
		int index = sb.indexOf("=");

		while (index > -1) {
			// The previous character is a non-whitespace character and not an operator
			if (index + 1 < sb.length()) {
				char character = sb.charAt(index + 1);

				if ((character != '=') &&
				    !Character.isWhitespace(character)) {
					sb.insert(index + 1, ' ');
				}
			}

			index = sb.indexOf("=", index + 2);
		}
	}

	private static void spaceOutEqualBefore(StringBuilder sb) {

		// Replace "\\S=" to "\\S ="
		int index = sb.indexOf("=");

		while (index > -1) {
			// The previous character is a non-whitespace character and not an operator
			if (index > 0) {
				char character = sb.charAt(index - 1);

				if ((character != '=') &&
				    (character != '<') &&
				    (character != '>') &&
				    !Character.isWhitespace(character)) {
					sb.insert(index, ' ');
				}
			}

			index = sb.indexOf("=", index + 2);
		}
	}

	/**
	 * Removes the whitespace that starts the given text.
	 *
	 * @param text The text to have the whitespace removed from the beginning of the string
	 * @return The number of whitespace removed
	 */
	public static int trimLeadingWhitespace(StringBuilder text) {

		int count = 0;

		for (int index = 0; index < text.length(); index++) {

			if (!Character.isWhitespace(text.charAt(index))) {
				break;
			}

			text.delete(index, index + 1);
			index--;
			count++;
		}

		return count;
	}

	/**
	 * Removes the whitespace that ends the given text.
	 *
	 * @param text The text to have the whitespace removed from the end of the string
	 * @return The number of whitespace removed
	 */
	public static int trimTrailingWhitespace(StringBuilder sb) {

		int count = 0;

		for (int index = sb.length(); --index >= 0; ) {

			if (!Character.isWhitespace(sb.charAt(index))) {
				break;
			}

			sb.delete(index, index + 1);
			count++;
		}

		return count;
	}

	/**
	 * Removes the whitespace that starts and ends the given text.
	 *
	 * @param text The text to have the whitespace removed from the beginning and end of the string
	 * @return The number of whitespace removed
	 */
	public static int trimWhitespace(StringBuilder text) {
		int count = trimLeadingWhitespace(text);
		count += trimTrailingWhitespace(text);
		return count;
	}
}