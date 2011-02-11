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
 * The <b>SELECT</b> clause denotes the query result. More than one value may
 * be returned from the <b>SELECT</b> clause of a query. The <b>SELECT</b>
 * clause may contain one or more of the following elements: a single range
 * variable or identification variable that ranges over an entity abstract
 * schema type, a single-valued path expression, an aggregate select expression,
 * a constructor expression.
 * <p>
 * The <b>DISTINCT</b> keyword is used to specify that duplicate values must be
 * eliminated from the query result. If <b>DISTINCT</b> is not specified,
 * duplicate values are not eliminated. Stand-alone identification variables in
 * the <b>SELECT</b> clause may optionally be qualified by the <b>OBJECT</b>
 * operator. The <b>SELECT</b> clause must not use the <b>OBJECT</b> operator
 * to qualify path expressions.
 *
 * @see SelectClause
 * @see SimpleSelectClause
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public abstract class AbstractSelectClause extends AbstractExpression
{
	/**
	 * Determines whether the identifier <b>DISTINCT</b> was parsed.
	 */
	private boolean hasDistinct;

	/**
	 * Determines whether a whitespace was parsed after the <b>DISTINCT</b>.
	 */
	private boolean hasSpaceAfterDistinct;

	/**
	 * Determines whether a whitespace was parsed after the <b>SELECT</b>.
	 */
	private boolean hasSpaceAfterSelect;

	/**
	 * The actual expression of this select clause.
	 */
	private AbstractExpression selectExpression;

	/**
	 * Creates a new <code>SelectClause</code>.
	 *
	 * @param parent The parent of this expression
	 */
	AbstractSelectClause(AbstractExpression parent)
	{
		super(parent, SELECT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addChildrenTo(Collection<Expression> children)
	{
		children.add(getSelectExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children)
	{
		// 'SELECT'
		children.add(buildStringExpression(SELECT));

		if (hasSpaceAfterSelect)
		{
			children.add(buildStringExpression(SPACE));
		}

		// 'DISTINCT'
		if (hasDistinct)
		{
			children.add(buildStringExpression(DISTINCT));
		}

		if (hasSpaceAfterDistinct)
		{
			children.add(buildStringExpression(SPACE));
		}

		// Select expression
		if (selectExpression != null)
		{
			children.add(selectExpression);
		}
	}

	/**
	 * Returns the {@link Expression} representing the select items.
	 *
	 * @return The expression representing the select items
	 */
	public Expression getSelectExpression()
	{
		if (selectExpression == null)
		{
			selectExpression = buildNullExpression();
		}

		return selectExpression;
	}

	/**
	 * Determines whether the identifier <b>DISTINCT</b> was parsed or not.
	 *
	 * @return <code>true</code> if the identifier <b>DISTINCT</b> was parsed;
	 * <code>false</code> otherwise
	 */
	public boolean hasDistinct()
	{
		return hasDistinct;
	}

	/**
	 * Determines whether the list of select items was parsed.
	 *
	 * @return <code>true</code> the list of select items was parsed;
	 * <code>false</code> otherwise
	 */
	public boolean hasSelectExpression()
	{
		return selectExpression != null &&
		      !selectExpression.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after <b>DISTINCT</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>DISTINCT</b>;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterDistinct()
	{
		return hasSpaceAfterDistinct;
	}

	/**
	 * Determines whether a whitespace was parsed after <b>SELECT</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>SELECT</b>;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterSelect()
	{
		return hasSpaceAfterSelect;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		// Parse 'SELECT'
		wordParser.moveForward(SELECT);

		hasSpaceAfterSelect = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'DISTINCT'
		hasDistinct = wordParser.startsWithIdentifier(DISTINCT);

		if (hasDistinct)
		{
			wordParser.moveForward(DISTINCT);
			hasSpaceAfterDistinct = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse the select expression
		selectExpression = parse
		(
			wordParser,
			selectItemBNF(),
			tolerant
		);
	}

	/**
	 * Returns the BNF for the list of select items to parse.
	 *
	 * @return The BNF for the list of select items to parse
	 */
	abstract JPQLQueryBNF selectItemBNF();

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer)
	{
		// 'SELECT'
		writer.append(getText());

		if (hasSpaceAfterSelect)
		{
			writer.append(SPACE);
		}

		// 'DISTINCT'
		if (hasDistinct())
		{
			writer.append(DISTINCT);
		}

		if (hasSpaceAfterDistinct)
		{
			writer.append(SPACE);
		}

		// Select expression
		if (selectExpression != null)
		{
			selectExpression.toParsedText(writer);
		}
	}
}