/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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

/**
 * This {@link JoinFactory} creates a new {@link Join} or {@link FetchJoin} when
 * the portion of the query to parse starts with <b>JOIN</b> or <b>FETCH JOIN</b>,
 * respectively.
 *
 * @see FetchJoin
 * @see Join
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
final class JoinFactory extends ExpressionFactory
{
	/**
	 * The unique identifier of this {@link JoinFactory}.
	 */
	static final String ID = Expression.JOIN;

	/**
	 * Creates a new <code>JoinFactory</code>.
	 */
	JoinFactory()
	{
		super(ID, Expression.LEFT,
		          Expression.INNER,
		          Expression.JOIN,
		          Expression.OUTER,
		          Expression.FETCH);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	AbstractExpression buildExpression(AbstractExpression parent,
	                                   WordParser wordParser,
	                                   String word,
	                                   JPQLQueryBNF queryBNF,
	                                   AbstractExpression expression,
	                                   boolean tolerant)
	{
		int index = wordParser.position();

		// JOIN and JOIN FETCH
		if (wordParser.startsWithIdentifier(Expression.JOIN, index))
		{
			index += 4;
			index += wordParser.whitespaceCount(index);

			// JOIN FETCH
			if (wordParser.startsWithIdentifier(Expression.FETCH, index))
			{
				expression = new JoinFetch(parent, Expression.JOIN_FETCH);
			}
			// JOIN
			else
			{
				expression = new Join(parent, Expression.JOIN);
			}
		}
		// LEFT
		else if (wordParser.startsWithIdentifier(Expression.LEFT))
		{
			index += 4;
			index += wordParser.whitespaceCount(index);

			// LEFT OUTER
			if (wordParser.startsWithIdentifier(Expression.OUTER, index))
			{
				index += 5;
				index += wordParser.whitespaceCount(index);

				if (wordParser.startsWithIdentifier(Expression.JOIN, index))
				{
					index += 4;
					index += wordParser.whitespaceCount(index);

					// LEFT OUTER JOIN FETCH
					if (wordParser.startsWithIdentifier(Expression.FETCH, index))
					{
						expression = new JoinFetch(parent, Expression.LEFT_OUTER_JOIN_FETCH);
					}
					// LEFT OUTER JOIN
					else
					{
						expression = new Join(parent, Expression.LEFT_OUTER_JOIN);
					}
				}
			}
			else if (wordParser.startsWithIdentifier(Expression.JOIN, index))
			{
				index += 4;
				index += wordParser.whitespaceCount(index);

				// LEFT JOIN FETCH
				if (wordParser.startsWithIdentifier(Expression.FETCH, index))
				{
					expression = new JoinFetch(parent, Expression.LEFT_JOIN_FETCH);
				}
				// LEFT JOIN
				else
				{
					expression = new Join(parent, Expression.LEFT_JOIN);
				}
			}
			else
			{
				expression = new UnknownExpression(parent, word);
			}
		}
		// INNER JOIN and INNER JOIN FETCH
		else if (wordParser.startsWithIdentifier(Expression.INNER, index))
		{
			index += 5;
			index += wordParser.whitespaceCount(index);

			// INNER JOIN FETCH
			if (wordParser.startsWithIdentifier(Expression.FETCH, index))
			{
				expression = new JoinFetch(parent, Expression.INNER_JOIN_FETCH);
			}
			// INNER JOIN
			else
			{
				expression = new Join(parent, Expression.INNER_JOIN);
			}
		}
		else
		{
			return null;
		}

		expression.parse(wordParser, tolerant);
		return expression;
	}
}