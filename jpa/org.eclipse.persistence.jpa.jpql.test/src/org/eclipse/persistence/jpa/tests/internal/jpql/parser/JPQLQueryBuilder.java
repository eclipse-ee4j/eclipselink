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
package org.eclipse.persistence.jpa.tests.internal.jpql.parser;

import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.spi.IJPAVersion;

import static org.junit.Assert.*;

/**
 * This builder created the parsed tree representation of a JPQL query and verify {@link
 * Expression#toParsedText()} represents the same thing than.
 *
 * @version 2.3
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
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String query) {
		return buildQuery(query, IJPAVersion.DEFAULT_VERSION);
	}

	/**
	 * Parses the given JPQL query and tests its generated string with the given query, which will be
	 * formatted first.
	 *
	 * @param query The JPQL query to parse into a parsed tree
	 * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
	 * to parse invalid or incomplete queries
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String query, boolean tolerant) {
		return buildQuery(query, IJPAVersion.DEFAULT_VERSION, tolerant);
	}

	/**
	 * Parses the given JPQL query and tests its generated string with the given query, which will be
	 * formatted first.
	 *
	 * @param query The JPQL query to parse into a parsed tree
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
	 * to parse invalid or incomplete queries
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String query,
	                                        boolean tolerant,
	                                        JPQLQueryStringFormatter formatter) {

		return buildQuery(query, IJPAVersion.DEFAULT_VERSION, tolerant, formatter);
	}

	/**
	 * Parses the given JPQL query and tests its generated string with the given query, which will be
	 * formatted first.
	 *
	 * @param query The JPQL query to parse into a parsed tree
	 * @param version The JPA version used to parse the query
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String query, IJPAVersion version) {
		return buildQuery(query, version, JPQLQueryStringFormatter.DEFAULT);
	}

	/**
	 * Parses the given JPQL query and tests its generated string with the given query, which will be
	 * formatted first.
	 *
	 * @param query The JPQL query to parse into a parsed tree
	 * @param version The JPA version used to parse the query
	 * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
	 * to parse invalid or incomplete queries
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String query, IJPAVersion version, boolean tolerance) {
		return buildQuery(query, version, tolerance, JPQLQueryStringFormatter.DEFAULT);
	}

	/**
	 * Parses the given JPQL query and tests its generated string with the given query, which will be
	 * formatted first.
	 *
	 * @param query The JPQL query to parse into a parsed tree
	 * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
	 * to parse invalid or incomplete queries
	 * @param version The JPA version used to parse the query
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String query,
	                                        IJPAVersion version,
	                                        boolean tolerant,
	                                        JPQLQueryStringFormatter formatter) {

		// Remove any extra whitespace and make all identifiers upper case
		String realQuery = formatQuery(query);

		// For the JPQL query with this formatter so the invoker can tweak the default formatting
		realQuery = formatter.format(realQuery);

		// Create the parsed tree of the JPQL query
		JPQLExpression jpqlExpression = new JPQLExpression(query, version, tolerant);

		// Make sure the query was correctly parsed
		assertEquals(realQuery, jpqlExpression.toParsedText());

		// If the JPQL query is parsed in a non-tolerant mode, then the query should be completely
		// parsed and there should not be any unknown ending statement
		if (!tolerant) {
			assertFalse(jpqlExpression.hasUnknownEndingStatement());
		}

		return jpqlExpression;
	}

	/**
	 * Parses the given JPQL query and tests its generated string with the given query, which will be
	 * formatted first.
	 *
	 * @param query The JPQL query to parse into a parsed tree
	 * @param version The JPA version used to parse the query
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String query,
	                                        IJPAVersion version,
	                                        JPQLQueryStringFormatter formatter) {

		return buildQuery(query, version, true, formatter);
	}

	/**
	 * Parses the given JPQL query and tests its generated string with the given query, which will be
	 * formatted first.
	 *
	 * @param query The JPQL query to parse into a parsed tree
	 * @param formatter This formatter is used to personalized the formatting of the JPQL query
	 * before it is used to test the generated string
	 * @return The parsed tree representation of the given JPQL query
	 */
	public static JPQLExpression buildQuery(String query, JPQLQueryStringFormatter formatter) {
		return buildQuery(query, IJPAVersion.DEFAULT_VERSION, formatter);
	}

	public static String formatMinusSign(String query) {
		return query.replaceAll("\\s*\\-\\s*", " - ");
	}

	public static String formatPlusSign(String query) {
		return query.replaceAll("\\s*\\+\\s*", " + ");
	}

	/**
	 * Formats the given query by converting it to what JPQLExpression would
	 * returned. For instance, Select would be converted to SELECT.
	 * <p>
	 * <b>Note:</b> If JPQL identifiers are used inside of strings then they
	 * might be converted. For instance e.name = 'Pascal Null' will be converted
	 * to e.name = 'Pascal NULL'.
	 *
	 * @param query A JPA query to be formatted
	 * @return The formatted JPQL query
	 */
	public static String formatQuery(String query) {

		query = query.replaceAll("\r\n?",                                   " ");
		query = query.replaceAll("\\s+",                                    " ");
		query = query.replaceAll("\\',\\'",                                 "', '");
		query = query.replaceAll("[Ss][Ee][Ll][Ee][Cc][Tt] ",               "SELECT ");
		query = query.replaceAll("[Oo][Bb][Jj][Ee][Cc][Tt]",                "OBJECT");
		query = query.replaceAll("[Cc][Oo][Nn][Cc][Aa][Tt]",                "CONCAT");
		query = query.replaceAll("[Dd][Ii][Ss][Tt][Ii][Nn][Cc][Tt]",        "DISTINCT");
		query = query.replaceAll("[Gg][Rr][Oo][Uu][Pp]\\s+[Bb][Yy]",        "GROUP BY");
		query = query.replaceAll("[Oo][Rr][Dd][Ee][Rr]\\s+[Bb][Yy]",        "ORDER BY");
		query = query.replaceAll(" [Ff][Rr][Oo][Mm] ",                      " FROM ");
		query = query.replaceAll(" [Ww][Hh][Ee][Rr][Ee] ",                  " WHERE ");
		query = query.replaceAll(" [Ss][Uu][Mm]",                           " SUM");
		query = query.replaceAll(" [Hh][Aa][Vv][Ii][Nn][Gg] ",              " HAVING ");
		query = query.replaceAll(" [Aa][Nn][Dd] ",                          " AND ");
		query = query.replaceAll(" [Oo][Rr] ",                              " OR ");
		query = query.replaceAll(" [Cc][Oo][Uu][Nn][Tt]",                   " COUNT");
		query = query.replaceAll(" [Ee][Ss][Cc][Aa][Pp][Ee]",               " ESCAPE");
		query = query.replaceAll(" [Tt][Rr][Ii][Mm]",                       " TRIM");
		query = query.replaceAll(" [Ii][Nn]\\s?\\(",                        " IN(");
		query = query.replaceAll(" [Ii][Ss]\\s\\[Nn][Uu][Ll][Ll]",          " IS NULL");
		query = query.replaceAll(" [Nn][Uu][Ll][Ll]",                       " NULL");
		query = query.replaceAll(" [Bb][Ee][Tt][Ww][Ee][Ee][Nn]",           " BETWEEN");
		query = query.replaceAll(" [Ll][Ee][Ff][Tt]",                       " LEFT");
		query = query.replaceAll(" [Oo][Uu][Tt][Ee][Rr]",                   " OUTER");
		query = query.replaceAll(" [Ii][Nn][Nn][Ee][Rr]",                   " INNER");
		query = query.replaceAll(" [Jj][Oo][Ii][Nn]",                       " JOIN");
		query = query.replaceAll(" [Nn][Oo][Tt]",                           " NOT");
		query = query.replaceAll(" [Ll][Ii][Kk][Ee]",                       " LIKE");
		query = query.replaceAll(" [Ii][Ss]",                               " IS");
		query = query.replaceAll(" [Aa][Ss] ",                              " AS ");
		query = query.replaceAll("[Aa][Ll][Ll]\\s?\\(",                     "ALL(");
		query = query.replaceAll("[Aa][Nn][Yy]\\s?\\(",                     "ANY(");
		query = query.replaceAll("[Aa][Vv][Gg]\\s?\\(",                     "AVG(");
		query = query.replaceAll("[Cc][Oo][Uu][Nn][Tt]\\s?\\(",             "COUNT(");
		query = query.replaceAll("[Ee][Xx][Ii][Ss][Tt][Ss]\\s?\\(",         "EXISTS(");
		query = query.replaceAll("[Ll][Oo][Ww][Ee][Rr]\\s?\\(",             "LOWER(");
		query = query.replaceAll("[Mm][Ii][Nn]\\s?\\(",                     "MIN(");
		query = query.replaceAll("[Mm][Aa][Xx]\\s?\\(",                     "MAX(");
		query = query.replaceAll("[Nn][Ee][Ww] ",                           "NEW ");
		query = query.replaceAll("[Ss][Oo][Mm][Ee]\\s?\\(",                 "SOME(");
		query = query.replaceAll("[Ss][Uu][Bb][Ss][Tt][Rr][Ii][Nn][Gg]",    "SUBSTRING");
		query = query.replaceAll("[Ss][Uu][Mm]\\s?\\(",                     "SUM(");
		query = query.replaceAll("OBJECT\\s\\(",                            "OBJECT(");
		query = query.replaceAll("[Tt][Rr][Ii][Mm]\\s\\(",                  "TRIM(");
		query = query.replaceAll("[Uu][Pp][Pp][Ee][Rr]\\s?\\(",             "UPPER(");
		query = query.replaceAll("[Nn][uU][lL][lL][iI][Ff]\\s?\\(",         "NULLIF(");
		query = query.replaceAll("[Kk][Ee][Yy]\\s?\\(",                     "KEY(");
		query = query.replaceAll("[Vv][Aa][Ll][Uu][Ee]\\s?\\(",             "VALUE(");
		query = query.replaceAll("[Cc][Oo][Aa][Ll][Ee][Ss][Cc][Ee]\\s?\\(", "COALESCE(");
		query = query.replaceAll("[Ff][Uu][Nn][Cc]\\s?\\(",                 "FUNC(");
		query = query.replaceAll("\\s?/\\s?",                               " / ");
		query = query.replaceAll("\\s?\\*\\s?",                             " * ");
		query = query.replaceAll("\\)\\s,",                                 "),");
		query = query.replaceAll("\\(\\s?",                                 "(");
		query = query.replaceAll("\\s?\\)",                                 ")");

		query = query.replaceAll("\\)AND",       ") AND");
		query = query.replaceAll("\\)OR",        ") OR");
		query = query.replaceAll("AND\\(",       "AND (");
		query = query.replaceAll("OR\\(",        "OR (");
		query = query.replaceAll("WHERE\\(",     "WHERE (");
		query = query.replaceAll("\\)GROUP BY",  ") GROUP BY");
		query = query.replaceAll("\\)ORDER BY",  ") ORDER BY");
		query = query.replaceAll("\\)HAVING",    ") HAVING");
		query = query.replaceAll("\\)FROM",      ") FROM");
		query = query.replaceAll("\\)WHERE",     ") WHERE");
		query = query.replaceAll("\\(GROUP BY",  "( GROUP BY");
		query = query.replaceAll("\\(ORDER BY",  "( ORDER BY");
		query = query.replaceAll("\\(HAVING",    "( HAVING");
		query = query.replaceAll("\\(FROM",      "( FROM");
		query = query.replaceAll("\\(WHERE",     "( WHERE");

		StringBuilder sb = new StringBuilder(query);

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
	 * @param text The text to have the whitespace removed from the beginning of
	 * the string
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
	 * @param text The text to have the whitespace removed from the end of the
	 * string
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
	 * @param text The text to have the whitespace removed from the beginning
	 * and end of the string
	 * @return The number of whitespace removed
	 */
	public static int trimWhitespace(StringBuilder text) {
		int count = trimLeadingWhitespace(text);
		count += trimTrailingWhitespace(text);
		return count;
	}
}