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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.internal.jpql.WordParser;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.spi.IJPAVersion;

/**
 * A JPQL expression is the parsed tree representation of a Java Persistence query. The parser
 * supports version 1.0 and 2.0 of the JPA specification.
 * <p>
 * A JPQL statement may be either a <b>SELECT</b> statement, an <b>UPDATE</b> statement, or a
 * <b>DELETE FROM</b> statement.
 * <p>
 * <div nowrap><b>BNF:</b> <code>QL_statement ::= {@link SelectStatement select_statement} |
 *                                                {@link UpdateStatement update_statement} |
 *                                                {@link DeleteStatement delete_statement}</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JPQLExpression extends AbstractExpression {

	/**
	 * The tree representation of the query.
	 */
	private AbstractExpression queryStatement;

	/**
	 * Determines if the parsing system should be tolerant, meaning if it should try to parse invalid
	 * or incomplete queries.
	 */
	private boolean tolerant;

	/**
	 * If the expression could not be fully parsed, meaning some unknown text is trailing the query,
	 * this will contain it.
	 */
	private AbstractExpression unknownEndingStatement;

	/**
	 * The version of the JPQL to support, which is the version of the Java Persistence specification.
	 */
	private IJPAVersion version;

	/**
	 * Creates a new <code>JPQLExpression</code>, which is the root of the JPQL parsed tree.
	 *
	 * @param query The string representation of the JPQL query to parse
	 * @param version The version of the JPQL to support, which is the version of the Java
	 * Persistence specification
	 */
	public JPQLExpression(CharSequence query, IJPAVersion version) {
		this(query, version, false);
	}

	/**
	 * Creates a new <code>JPQLExpression</code>, which is the root of the JPQL parsed tree.
	 *
	 * @param query The string representation of the JPQL query to parse
	 * @param version The version of the JPQL to support, which is the version of the Java
	 * Persistence specification
	 * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
	 * to parse invalid or incomplete queries
	 */
	public JPQLExpression(CharSequence query, IJPAVersion version, boolean tolerant) {
		this(version, tolerant);
		parse(new WordParser(query), tolerant);
	}

	/**
	 * Creates a new <code>JPQLExpression</code>, which is the root of the JPQL parsed tree.
	 *
	 * @param version The version of the JPQL to support, which is the version of the Java
	 * Persistence specification
	 * @param tolerant Determines if the parsing system should be tolerant, meaning if it should try
	 * to parse invalid or incomplete queries
	 */
	private JPQLExpression(IJPAVersion version, boolean tolerant) {
		super(null);

		checkVersion(version);

		this.tolerant = tolerant;
		this.version  = version;
	}

	/**
	 * Creates a new <code>Expression</code> by parsing the string, which represents a conditional
	 * expression.
	 *
	 * @param abstractSchemaName The abstract schema name for which the identification variable
	 * "this" represents
	 * @param conditionalExpression The string representation of the conditional expression to parse
	 * @param version The version of the JPQL to support, which is the version of the Java
	 * Persistence specification
	 * @param tolerant Determines whether the parsing system should be tolerant, meaning if it should
	 * try to parse invalid or incomplete queries
	 * @return The {@link Expression} representing the given conditional expression
	 * @return The parse tree representation of the given conditional expression
	 */
	public static Expression parseConditionalExpression(String abstractSchemaName,
	                                                    CharSequence conditionalExpression,
	                                                    IJPAVersion version,
	                                                    boolean tolerant) {

		JPQLExpression expression = new JPQLExpression(version, tolerant);
		expression.checkQuery(conditionalExpression);

		// Add the select statement
		SelectStatement selectStatement = expression.addSelectStatement();

		// Add the FROM clause
		FromClause fromClause = selectStatement.addFromClause();

		// Add the identification variable declaration
		fromClause.setIdentificationVariableDeclaration(abstractSchemaName, "this");

		// Add the WHERE clause
		WhereClause whereClause = selectStatement.addWhereClause();
		whereClause.parse(conditionalExpression, tolerant);

		// Now return the parsed condition expression
		return whereClause.getConditionalExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void acceptChildren(ExpressionVisitor visitor) {
		getQueryStatement().accept(visitor);
		getUnknownEndingStatement().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addChildrenTo(Collection<Expression> children) {
		children.add(getQueryStatement());
		children.add(getUnknownEndingStatement());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children) {

		if (queryStatement != null) {
			children.add(queryStatement);
		}

		if (unknownEndingStatement != null) {
			children.add(unknownEndingStatement);
		}
	}

	private SelectStatement addSelectStatement() {
		SelectStatement selectStatement = new SelectStatement(this);
		queryStatement = selectStatement;
		return selectStatement;
	}

	/**
	 * Creates an object where the {@link Expression} is the leaf at the given position.
	 *
	 * @param actualQuery The actual query is a string representation of the query that may contain
	 * extra whitespace
	 * @param position The position of the cursor in the actual query, which is used to retrieve the
	 * deepest {@link Expression}. The position will be adjusted to fit into the beautified version
	 * of the query
	 * @return A new {@link QueryPosition}
	 */
	public QueryPosition buildPosition(String actualQuery, int position) {

		// Adjust the position by not counting extra whitespace
		position = ExpressionTools.repositionCursor(actualQuery, position, toActualText());

		QueryPosition queryPosition = new QueryPosition(position);
		populatePosition(queryPosition, position);
		return queryPosition;
	}

	private AbstractExpression buildQueryStatement(WordParser wordParser) {
		switch (wordParser.character()) {
			case 'D': case 'd': return new DeleteStatement(this);
			case 'U': case 'u': return new UpdateStatement(this);
			default:            return new SelectStatement(this);
		}
	}

	private void checkQuery(CharSequence query) {
		if (query == null) {
			throw new NullPointerException("The query cannot be null");
		}
	}

	private void checkVersion(IJPAVersion version) {
		if (version == null) {
			throw new NullPointerException("The IJPAVersion cannot be null");
		}
	}

	/**
	 * Returns the deepest {@link Expression} for the given position.
	 *
	 * @param actualQuery The actual query is the text version of the query that may contain extra
	 * whitespace and different formatting than the trim down version generated by the parsed tree
	 * @param position The position in the actual query used to retrieve the {@link Expression}
	 * @return The {@link Expression} located at the given position in the given query
	 */
	public Expression getExpression(String actualQuery, int position) {
		QueryPosition queryPosition = buildPosition(actualQuery, position);
		return queryPosition.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IJPAVersion getJPAVersion() {
		return version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return queryBNF(JPQLStatementBNF.ID);
	}

	/**
	 * Returns the {@link Expression} representing the query, which is either a <b>SELECT</b>, a
	 * <b>DELETE</b> or an <b>UPDATE</b> clause.
	 *
	 * @return The expression representing the Java Persistence query
	 */
	public Expression getQueryStatement() {
		if (queryStatement == null) {
			queryStatement = buildNullExpression();
		}
		return queryStatement;
	}

	/**
	 * Returns the {@link Expression} that may contain a portion of the query that could not be
	 * parsed, this happens when the query is either incomplete or malformed.
	 *
	 * @return The expression used when the ending of the query is unknown or malformed
	 */
	public AbstractExpression getUnknownEndingStatement() {
		if (unknownEndingStatement == null) {
			unknownEndingStatement = buildNullExpression();
		}
		return unknownEndingStatement;
	}

	/**
	 * Returns the version of the JPQL to support, which is the version of the Java Persistence
	 * specification.
	 *
	 * @return The version that was used to parse the JPQL query
	 */
	public IJPAVersion getVersion() {
		return version;
	}

	/**
	 * Determines whether a query was parsed. The query may be incomplete but it started with one of
	 * the three clauses (<b>SELECT</b>, <b>DELETE FROM</b>, or <b>UPDATE</b>).
	 *
	 * @return <code>true</code> the query was parsed; <code>false</code> otherwise
	 */
	public boolean hasQueryStatement() {
		return queryStatement != null &&
		      !queryStatement.isNull();
	}

	/**
	 * Determines whether the query that got parsed had some malformed or unknown information.
	 *
	 * @return <code>true</code> if the query could not be parsed correctly
	 * because it is either incomplete or malformed
	 */
	public boolean hasUnknownEndingStatement() {
		return unknownEndingStatement != null &&
		      !unknownEndingStatement.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isTolerant() {
		return tolerant;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant) {

		// Skip leading whitespace
		wordParser.skipLeadingWhitespace();

		// Parse the query, which can be invalid/incomplete or complete and valid
		if (tolerant) {

			queryStatement = parseSingleExpression(wordParser, getQueryBNF(), tolerant);
			int count = wordParser.skipLeadingWhitespace();

			// The JPQL query is invalid or incomplete, the remaining will be added
			// to the unknown ending statement
			if ((queryStatement == null) || !wordParser.isTail()) {
				wordParser.moveBackward(count);
				unknownEndingStatement = buildUnknownExpression(wordParser.substring());
			}
			// The JPQL query has some ending whitespace, keep one (used by content assist)
			else if (!wordParser.isTail() || (tolerant && (count > 0))) {
				unknownEndingStatement = buildUnknownExpression(" ");
			}
		}
		// Quickly parse the valid query
		else {
			queryStatement = buildQueryStatement(wordParser);
			queryStatement.parse(wordParser, tolerant);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer, boolean includeVirtual) {

		if (queryStatement != null) {
			queryStatement.toParsedText(writer, includeVirtual);
		}

		if (unknownEndingStatement != null) {
			unknownEndingStatement.toParsedText(writer, includeVirtual);
		}
	}
}