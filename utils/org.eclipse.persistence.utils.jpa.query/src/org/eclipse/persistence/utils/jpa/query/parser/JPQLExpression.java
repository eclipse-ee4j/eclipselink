/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.parser;

import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.utils.jpa.query.spi.IJPAVersion;

/**
 * A JPQL expression is the parsed tree representation of a Java Persistence
 * query. The parser supports version 1.0 and 2.0 of the JPA specification.
 * <p>
 * A JPQL statement may be either a <b>SELECT</b> statement, an <b>UPDATE</b>
 * statement, or a <b>DELETE FROM</b> statement.
 * <p>
 * <div nowrap><b>BNF:</b> <code>QL_statement ::= {@link SelectStatement select_statement} | {@link UpdateStatement update_statement} | {@link DeleteStatement delete_statement}</code><p>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JPQLExpression extends AbstractExpression
{
	/**
	 * The tree representation of the query.
	 */
	private AbstractExpression queryStatement;

	/**
	 * Determines if the parsing system should be tolerant, meaning if it should
	 * try to parse invalid or incomplete queries.
	 */
	private boolean tolerant;

	/**
	 * If the expression could not be fully parsed, meaning some unknown text is
	 * trailing the query, this will contain it.
	 */
	private AbstractExpression unknownEndingStatement;

	/**
	 * The version of the JPQL to support, which is the version of the Java
	 * Persistence specification.
	 */
	private IJPAVersion version;

	/**
	 * Creates a new <code>JPQLExpression</code>, which is the root of the JPQL
	 * parsed tree.
	 *
	 * @param query The string representation of the JPQL query to parse
	 * @param version The version of the JPQL to support, which is the version of
	 * the Java Persistence specification
	 */
	public JPQLExpression(CharSequence query, IJPAVersion version)
	{
		this(query, version, false);
	}

	/**
	 * Creates a new <code>JPQLExpression</code>, which is the root of the JPQL
	 * parsed tree.
	 *
	 * @param query The string representation of the JPQL query to parse
	 * @param version The version of the JPQL to support, which is the version of
	 * the Java Persistence specification
	 * @param tolerant Determines if the parsing system should be tolerant,
	 * meaning if it should try to parse invalid or incomplete queries
	 */
	public JPQLExpression(CharSequence query,
	                      IJPAVersion version,
	                      boolean tolerant)
	{
		this(version, tolerant);

		checkQuery(query);
		parse(new WordParser(query), tolerant);
	}

	/**
	 * Creates a new <code>JPQLExpression</code>, which is the root of the JPQL
	 * parsed tree.
	 *
	 * @param version The version of the JPQL to support, which is the version of
	 * the Java Persistence specification
	 * @param tolerant Determines if the parsing system should be tolerant,
	 * meaning if it should try to parse invalid or incomplete queries
	 */
	private JPQLExpression(IJPAVersion version, boolean tolerant)
	{
		super(null);

		checkVersion(version);

		this.tolerant = tolerant;
		this.version  = version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(ExpressionVisitor visitor)
	{
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addChildrenTo(Collection<Expression> children)
	{
		children.add(getQueryStatement());
		children.add(getUnknownEndingStatement());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children)
	{
		if (queryStatement != null)
		{
			children.add(queryStatement);
		}

		if (unknownEndingStatement != null)
		{
			children.add(unknownEndingStatement);
		}
	}

	/**
	 * Sets the query statement to be a delete statement.
	 *
	 * @return This expression's query statement
	 */
//	public DeleteStatement addDeleteStatement()
//	{
//		DeleteStatement deleteStatement = new DeleteStatement(this);
//		deleteStatement.addDeleteClause();
//		queryStatement = deleteStatement;
//		return deleteStatement;
//	}

	/**
	 * Creates an object where the {@link Expression} is the leaf at the given
	 * position.
	 *
	 * @param position The position of the cursor that will be used to retrieve
	 * the deepest {@link Expression}
	 * @return A new {@link QueryPosition}
	 */
	public QueryPosition buildPosition(int position)
	{
		QueryPosition queryPosition = new QueryPosition(position);
		populatePosition(queryPosition, position);
		return queryPosition;
	}

	/**
	 * Sets the query statement to be a select statement.
	 *
	 * @return This expression's query statement
	 */
//	public SelectStatement addSelectStatement()
//	{
//		queryStatement = new SelectStatement(this);
//		return (SelectStatement) queryStatement;
//	}

	/**
	 * Sets the query statement to be an 'UPDATE' statement.
	 *
	 * @return This expression representing the update statement
	 */
//	public UpdateStatement addUpdateStatement()
//	{
//		UpdateStatement updateStatement = new UpdateStatement(this);
//		updateStatement.addUpdateClause();
//
//		queryStatement = updateStatement;
//		return updateStatement;
//	}

	/**
	 * Creates an object where the {@link Expression} is the leaf at the given
	 * position.
	 *
	 * @param actualQuery The actual query is a string representation of the
	 * query that may contain extra whitespace
	 * @param position The position of the cursor in the actual query, which is
	 * used to retrieve the deepest {@link Expression}. The position will be
	 * adjusted to fit into the beautified version of the query
	 * @return A new {@link QueryPosition}
	 */
	public QueryPosition buildPosition(String actualQuery, int position)
	{
		// Adjust the position by not counting extra whitespace
		position = repositionCursor(actualQuery, position);

		QueryPosition queryPosition = new QueryPosition(position);
		populatePosition(queryPosition, position);

		return queryPosition;
	}

	private AbstractExpression buildQueryStatement(WordParser wordParser)
	{
		switch (wordParser.character())
		{
			case 'D': case 'd': return new DeleteStatement(this);
			case 'U': case 'u': return new UpdateStatement(this);
			default:            return new SelectStatement(this);
		}
	}

	private void checkQuery(CharSequence query)
	{
		if (query == null)
		{
			throw new NullPointerException("The query cannot be null");
		}
	}

	private void checkVersion(IJPAVersion version)
	{
		if (version == null)
		{
			throw new NullPointerException("The IJPAVersion cannot be null");
		}
	}

	/**
	 * Returns the deepest {@link Expression} for the given position.
	 *
	 * @param actualQuery The actual query is the text version of the query that
	 * may contain extra whitespace and different formatting than the trim down
	 * version generated by the parsed tree
	 * @param position The position in the actual query used to retrieve the
	 * {@link Expression}
	 * @return The {@link Expression} located at the given position in the given
	 * query
	 */
	public Expression getExpression(String actualQuery, int position)
	{
		QueryPosition queryPosition = buildPosition(actualQuery, position);
		return queryPosition.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IJPAVersion getJPAVersion()
	{
		return version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(JPQLStatementBNF.ID);
	}

	/**
	 * Returns the {@link Expression} representing the query, which is either
	 * a <b>SELECT</b>, a <b>DELETE</b> or an <b>UPDATE</b> clause.
	 *
	 * @return The expression representing the Java Persistence query
	 */
	public Expression getQueryStatement()
	{
		if (queryStatement == null)
		{
			queryStatement = buildNullExpression();
		}

		return queryStatement;
	}

	/**
	 * Returns the {@link Expression} that may contain a portion of the query
	 * that could not be parsed, this happens when the query is either incomplete
	 * or malformed.
	 *
	 * @return The expression used when the ending of the query is unknown or
	 * malformed
	 */
	public AbstractExpression getUnknownEndingStatement()
	{
		if (unknownEndingStatement == null)
		{
			unknownEndingStatement = buildNullExpression();
		}

		return unknownEndingStatement;
	}

	/**
	 * Returns the version of the JPQL to support, which is the version of the
	 * Java Persistence specification.
	 *
	 * @return The latest version of the specification
	 */
	public IJPAVersion getVersion()
	{
		return version;
	}

	/**
	 * Determines whether a query was parsed. The query may be incomplete but it
	 * started with one of the three clauses (<b>SELECT</b>, <b>DELETE FROM</b>,
	 * or <b>UPDATE</b>).
	 *
	 * @return <code>true</code> the query was parsed; <code>false</code> otherwise
	 */
	public boolean hasQueryStatement()
	{
		return queryStatement != null &&
		      !queryStatement.isNull();
	}

	/**
	 * Determines whether the query that got parsed had some malformed or unknown
	 * information.
	 *
	 * @return <code>true</code> if the query could not be parsed correctly
	 * because it is either incomplete or malformed
	 */
	public boolean hasUnknownEndingStatement()
	{
		return unknownEndingStatement != null &&
		      !unknownEndingStatement.isNull();
	}

	/**
	 * Determines whether the query is a delete statement, i.e. starts with
	 * <b>DELETE FROM</b>.
	 *
	 * @return <code>true</code> if the expression is a delete statement;
	 * <code>false</code> otherwise
	 */
	public boolean isDeleteStatement()
	{
		return (queryStatement instanceof DeleteStatement);
	}

	/**
	 * Determines whether the query is a select statement, i.e. starts with
	 * <b>SELECT</b>.
	 *
	 * @return <code>true</code> if the expression is a select statement;
	 * <code>false</code> otherwise
	 */
	public boolean isSelectStatement()
	{
		return (queryStatement instanceof SelectStatement);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isTolerant()
	{
		return tolerant;
	}

	/**
	 * Determines whether the query is an update statement, i.e. starts with
	 * <b>UPDATE</b>.
	 *
	 * @return <code>true</code> if the expression is an update statement;
	 * <code>false</code> otherwise
	 */
	public boolean isUpdateStatement()
	{
		return (queryStatement instanceof UpdateStatement);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		// Skip leading whitespace
		wordParser.skipLeadingWhitespace();

		// Parse the query
		if (tolerant)
		{
			queryStatement = parseSingleExpression(wordParser, getQueryBNF(), tolerant);

			if (!wordParser.isTail())
			{
				unknownEndingStatement = buildUnknownExpression(wordParser.substring());
			}
			else if (queryStatement != null &&
			         queryStatement.isUnknown())
			{
				unknownEndingStatement = queryStatement;
				queryStatement = null;
			}
		}
		else
		{
			queryStatement = buildQueryStatement(wordParser);
			queryStatement.parse(wordParser, tolerant);
		}
	}

	/**
	 * Re-adjusts the given position by making sure it's pointing correctly in
	 * the generated query since the parser doesn't keep extra whitespace.
	 *
	 * @param query The string representation of the query, which may have
	 * multiple whitespace
	 * @param position The position in the given query that needs to be adjusted
	 * in the string query generated by the parsed tree
	 * @return The position in the generated string query once the given query
	 * has been parsed
	 */
	final int repositionCursor(CharSequence query, int position)
	{
		if (position <= 0)
		{
			return 0;
		}

		String actualQuery = toParsedText();
		int actualLength   = actualQuery.length();
		int expectedLength = query.length();

		// The query and the generated query have the same length, the position
		// doesn't need to move since the text is exactly the same
		if (actualLength == expectedLength)
		{
			return position;
		}

		int newPosition   = position;
		int expectedIndex = 0;
		int actualIndex   = 0;

		while ((actualIndex < actualLength) && (expectedIndex < expectedLength))
		{
			char actualCharacter = actualQuery.charAt(actualIndex);
			char expectedCharacter = query.charAt(expectedIndex);

			// Keep the cursor at the same location on the actual query but
			// move the cursor to the next position on the expected query since
			// the character are not the same, which should be due to whitespace
			if (Character.toLowerCase(actualCharacter) !=
			    Character.toLowerCase(expectedCharacter))
			{
				newPosition--;
			}
			// Continue with the next character
			else
			{
				actualIndex++;
			}

			expectedIndex++;

			// We're done adjusting the position, as long as the actual and
			// expected characters are the same, if not, we still need to continue
			// to get to two identical characters
			if (newPosition == actualIndex)
			{
				break;
			}
		}

		// If the position is farther than the actual length, simply move
		// the cursor to the end of the actual length but keep 1 since it means
		// the actual query might have a whitespace at the end
		if (newPosition > actualLength)
		{
			int whitespaceCount = 0;

			for (int index = expectedLength; --index >= 0; )
			{
				char character = query.charAt(index);

				if (Character.isWhitespace(character))
				{
					whitespaceCount++;
				}
				else
				{
					break;
				}
			}

			if (--whitespaceCount > 0)
			{
				newPosition -= whitespaceCount;
			}
		}

		return newPosition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer)
	{
		if (queryStatement != null)
		{
			queryStatement.toParsedText(writer);
		}

		if (unknownEndingStatement != null)
		{
			unknownEndingStatement.toParsedText(writer);
		}
	}
}