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

/**
 * A select statement must always have a <b>SELECT</b> and a <b>FROM</b> clause.
 * <p>
 * <div nowrap><b>BNF:</b> <code>select_statement ::= select_clause from_clause [where_clause] [groupby_clause] [having_clause] [orderby_clause]</code><p>
 *
 * @see SelectClause
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class SelectStatement extends AbstractSelectStatement
{
	/**
	 * Determines whether there is a whitespace after the select statement parsed
	 * by the superclass and the <b>ORDER BY</b> identifier.
	 */
	private boolean hasSpaceBeforeGroupBy;

	/**
	 * The <b>ORDER BY</b> expression.
	 */
	private AbstractExpression orderByClause;

	/**
	 * Creates a new <code>SelectStatement</code>.
	 *
	 * @param parent The parent of this expression
	 */
	SelectStatement(AbstractExpression parent)
	{
		super(parent);
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
		super.addChildrenTo(children);

		if (orderByClause != null)
		{
			children.add(orderByClause);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children)
	{
		super.addOrderedChildrenTo(children);

		if (hasSpaceBeforeGroupBy)
		{
			children.add(buildStringExpression(SPACE));
		}

		// 'ORDER BY' clause
		if (orderByClause != null)
		{
			children.add(orderByClause);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	FromClause buildFromClause()
	{
		return new FromClause(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	SelectClause buildSelectClause()
	{
		return new SelectClause(this);
	}

	/**
	 * Returns the {@link Expression} representing the <b>ORDER BY</b> clause.
	 *
	 * @return Either the {@link OrderByClause} or a {@link NullExpression} if
	 * the clause is not defined
	 */
	public Expression getOrderByClause()
	{
		if (orderByClause == null)
		{
			orderByClause = buildNullExpression();
		}

		return orderByClause;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(SelectStatementBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SelectClause getSelectClause()
	{
		return (SelectClause) super.getSelectClause();
	}

	/**
	 * Determines whether the <b>ORDER BY</b> clause is defined.
	 *
	 * @return <code>true</code> if the query that got parsed had the <b>ORDER BY</b>
	 * clause
	 */
	public boolean hasOrderByClause()
	{
		return orderByClause != null &&
		      !orderByClause.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed before the <b>ORDER BY</b>
	 * clause. In some cases, the space could be owned by a child of the previous
	 * clause.
	 *
	 * @return <code>true</code> if there was a whitespace before the <b>ORDER
	 * BY</b>; <code>false</code> otherwise
	 */
	public boolean hasSpaceBeforeGroupBy()
	{
		return hasSpaceBeforeGroupBy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		super.parse(wordParser, tolerant);

		hasSpaceBeforeGroupBy = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'ORDER BY'
		if (wordParser.startsWithIdentifier(ORDER_BY))
		{
			orderByClause = new OrderByClause(this);
			orderByClause.parse(wordParser, tolerant);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer)
	{
		super.toParsedText(writer);

		if (hasSpaceBeforeGroupBy)
		{
			writer.append(SPACE);
		}

		// 'ORDER BY' clause
		if (hasOrderByClause())
		{
			orderByClause.toParsedText(writer);
		}
	}
}