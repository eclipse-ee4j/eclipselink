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

import java.util.Collection;
import java.util.List;

/**
 * An orderby_item must be one of the following:
 * <ol>
 * <li> A {@link StateFieldPathExpression state_field_path_expression} that
 * evaluates to an orderable state field of an entity or embeddable class
 * abstract schema type designated in the SELECT clause by one of the following:
 *   <ul>
 *   <li>A general_identification_variable
 *   <li>A single_valued_object_path_expression
 *   </ul>
 * <li>A {@link StateFieldPathExpression state_field_path_expression} that
 * evaluates to the same state field of the same entity or embeddable abstract
 * schema type as a {@link StateFieldPathExpression state_field_path_expression}
 * in the <b>SELECT</b> clause
 * <li>A {@link ResultVariable result_variable} that refers to an orderable item
 * in the <b>SELECT</b> clause for which the same {@link ResultVariable result_variable}
 * has been specified. This may be the result of an aggregate_expression, a
 * {@link ScalarExpression scalar_expression}, or a {@link StateFieldPathExpression
 * state_field_path_expression} in the <b>SELECT</b> clause.
 * </ol>
 * <p>
 * The keyword <b>ASC</b> specifies that ascending ordering be used for the
 * associated orderby_item; the keyword <b>DESC</b> specifies that descending
 * ordering be used. Ascending ordering is the default.
 * <p>
 * JPA 1.0: <div nowrap><b>BNF:</b> <code>orderby_item ::= state_field_path_expression [ ASC | DESC ]</code>
 * <p>
 * JPA 2.0 <div nowrap><b>BNF:</b> <code>orderby_item ::= state_field_path_expression | result_variable [ ASC | DESC ]</code><p>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class OrderByItem extends AbstractExpression
{
	/**
	 * Determines whether a whitespace was parsed after the state field path
	 * expression.
	 */
	private boolean hasSpaceAfterStateFieldPathExpression;

	/**
	 * The keyword ASC specifies that ascending ordering be used; the keyword
	 * DESC specifies that descending ordering be used. Ascending ordering is the
	 * default.
	 */
	private Ordering ordering;

	/**
	 * The {@link Expression} representing the state field path expression.
	 */
	private AbstractExpression stateFieldPathExpression;

	/**
	 * Creates a new <code>OrderByItem</code>.
	 *
	 * @param parent The parent of this expression
	 */
	OrderByItem(AbstractExpression parent)
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
		children.add(getStateFieldPathExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children)
	{
		// State field path expression
		if (stateFieldPathExpression != null)
		{
			children.add(stateFieldPathExpression);
		}

		if (hasSpaceAfterStateFieldPathExpression)
		{
			children.add(buildStringExpression(SPACE));
		}

		// Ordering type
		if (ordering != Ordering.DEFAULT)
		{
			children.add(buildStringExpression(ordering.toString()));
		}
	}

	/**
	 * Returns the enum constant representing the ordering type.
	 *
	 * @return The constant representing the ordering, in the case the ordering
	 * was not parsed, then {@link Ordering#DEFAULT} is returned
	 */
	public Ordering getOrdering()
	{
		return ordering;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(OrderByItemBNF.ID);
	}

	/**
	 * Returns the {@link Expression} that represents the state field path
	 * expression.
	 *
	 * @return The expression that was parsed representing the state field path
	 * expression
	 */
	public Expression getStateFieldPathExpression()
	{
		if (stateFieldPathExpression == null)
		{
			stateFieldPathExpression = buildNullExpression();
		}

		return stateFieldPathExpression;
	}

	/**
	 * Determines whether a whitespace was parsed after the state field path
	 * expression.
	 *
	 * @return <code>true</code> if there was a whitespace after the state field
	 * path expression; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterStateFieldPathExpression()
	{
		return hasSpaceAfterStateFieldPathExpression;
	}

	/**
	 * Determines whether the state field path expression was parsed.
	 *
	 * @return <code>true</code> if the state field path expression was parsed;
	 * <code>false</code> otherwise
	 */
	public boolean hasStateFieldPathExpression()
	{
		return stateFieldPathExpression != null &&
		      !stateFieldPathExpression.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		// Parse the state field path expression
		if (tolerant)
		{
			stateFieldPathExpression = parse
			(
				wordParser,
				queryBNF(InternalOrderByItemBNF.ID),
				tolerant
			);
		}
		else
		{
			String word = wordParser.word();

			if (word.indexOf(DOT) > -1)
			{
				stateFieldPathExpression = new StateFieldPathExpression(this, word);
			}
			else
			{
				stateFieldPathExpression = new IdentificationVariable(this, word);
			}

			stateFieldPathExpression.parse(wordParser, tolerant);
		}

		hasSpaceAfterStateFieldPathExpression = wordParser.skipLeadingWhitespace() > 0;

		if (!wordParser.isTail())
		{
			String word = wordParser.word();

			// Parse 'ASC'
			if (word.equalsIgnoreCase(ASC))
			{
				ordering = Ordering.ASC;
				wordParser.moveForward(ASC.length());
			}
			// Parse 'DESC'
			else if (word.equalsIgnoreCase(DESC))
			{
				ordering = Ordering.DESC;
				wordParser.moveForward(DESC.length());
			}
			else
			{
				ordering = Ordering.DEFAULT;
			}
		}
		else
		{
			ordering = Ordering.DEFAULT;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer)
	{
		// State field path expression
		if (stateFieldPathExpression != null)
		{
			stateFieldPathExpression.toParsedText(writer);
		}

		if (hasSpaceAfterStateFieldPathExpression)
		{
			writer.append(SPACE);
		}

		// Ordering type
		if (ordering != Ordering.DEFAULT)
		{
			writer.append(ordering.toString());
		}
	}

	/**
	 * This enumeration lists all the possible choices for ordering an item.
	 */
	public enum Ordering
	{
		/**
		 * The constant for 'ASC', which tells to order the items in ascending
		 * ordering.
		 */
		ASC,

		/**
		 * The constant used when the ordering is not specify, the default is
		 * ascending ordering.
		 */
		DEFAULT,

		/**
		 * The constant for 'DESC', which tells to order the items in descending
		 * ordering.
		 */
		DESC
	}
}