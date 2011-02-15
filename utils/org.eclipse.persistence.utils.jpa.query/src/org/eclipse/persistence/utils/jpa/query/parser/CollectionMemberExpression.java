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
 * This expression tests whether the designated value is a member of the
 * collection specified by the collection-valued path expression. If the
 * collection-valued path expression designates an empty collection, the value
 * of the <b>MEMBER OF</b> expression is <b>FALSE</b> and the value of the
 * <b>NOT MEMBER OF</b> expression is <b>TRUE</b>. Otherwise, if the value of
 * the collection-valued path expression or single-valued association-field path
 * expression in the collection member expression is <b>NULL</b> or unknown, the
 * value of the collection member expression is unknown.
 * <p>
 * <div nowrap><b>BNF:</b> <code>collection_member_expression ::= entity_or_value_expression [NOT] MEMBER [OF] collection_valued_path_expression</code><p>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class CollectionMemberExpression extends AbstractExpression
{
	/**
	 * The {@link Expression} representing the collection-valued path expression.
	 */
	private AbstractExpression collectionValuedPathExpression;

	/**
	 * The {@link Expression} representing the entity expression.
	 */
	private AbstractExpression entityExpression;

	/**
	 * Determines whether the identifier <b>NOT</b> was parsed.
	 */
	private boolean hasNot;

	/**
	 * Determines whether the identifier <b>OF</b> was parsed.
	 */
	private boolean hasOf;

	/**
	 * Determines whether a whitespace was parsed after <b>MEMBER</b>.
	 */
	private boolean hasSpaceAfterMember;

	/**
	 * Determines whether a whitespace was parsed after <b>OF</b>.
	 */
	private boolean hasSpaceAfterOf;

	/**
	 * Creates a new <code>CollectionMemberExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param expression The entity expression that was parsed before parsing
	 * this one
	 */
	CollectionMemberExpression(AbstractExpression parent,
	                           AbstractExpression expression)
	{
		super(parent);

		if (expression != null)
		{
			this.entityExpression = expression;
			this.entityExpression.setParent(this);
		}
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
		children.add(getEntityExpression());
		children.add(getCollectionValuedPathExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children)
	{
		// Entity expression
		if (entityExpression != null)
		{
			children.add(entityExpression);
		}

		// 'NOT'
		if (hasNot)
		{
			if (hasEntityExpression())
			{
				children.add(buildStringExpression(SPACE));
			}

			children.add(buildStringExpression(NOT));
		}

		if (hasNot || hasEntityExpression())
		{
			children.add(buildStringExpression(SPACE));
		}

		// 'MEMBER'
		children.add(buildStringExpression(MEMBER));

		if (hasSpaceAfterMember)
		{
			children.add(buildStringExpression(SPACE));
		}

		// 'OF'
		if (hasOf)
		{
			children.add(buildStringExpression(OF));
		}

		if (hasSpaceAfterOf)
		{
			children.add(buildStringExpression(SPACE));
		}

		// Collection-valued path expression
		if (collectionValuedPathExpression != null)
		{
			children.add(collectionValuedPathExpression);
		}
	}

	/**
	 * Returns the {@link Expression} representing the collection-valued path
	 * expression.
	 *
	 * @return The expression that was parsed representing the collection valued
	 * path expression
	 */
	public Expression getCollectionValuedPathExpression()
	{
		if (collectionValuedPathExpression == null)
		{
			collectionValuedPathExpression = buildNullExpression();
		}

		return collectionValuedPathExpression;
	}

	/**
	 * Returns the {@link Expression} representing the entity expression.
	 *
	 * @return The expression that was parsed representing the entity expression
	 */
	public Expression getEntityExpression()
	{
		if (entityExpression == null)
		{
			entityExpression = buildNullExpression();
		}

		return entityExpression;
	}

	/**
	 * Returns the identifier for this expression that may include <b>NOT</b> and
	 * <b>OF</b> if it was parsed.
	 *
	 * @return Either <b>MEMBER</b>, <b>NOT MEMBER</b>, <b>NOT MEMBER OF</b> or
	 * <b>MEMBER OF</b>
	 */
	public String getIdentifier()
	{
		if (hasNot && hasOf)
		{
			return NOT_MEMBER_OF;
		}

		if (hasNot)
		{
			return NOT_MEMBER;
		}

		if (hasOf)
		{
			return MEMBER_OF;
		}

		return MEMBER;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(CollectionMemberExpressionBNF.ID);
	}

	/**
	 * Determines whether the collection-valued path expression was parsed.
	 *
	 * @return <code>true</code> if the collection-valued path expression was
	 * parsed; <code>false</code> otherwise
	 */
	public boolean hasCollectionValuedPathExpression()
	{
		return collectionValuedPathExpression != null &&
		      !collectionValuedPathExpression.isNull();
	}

	/**
	 * Determines whether the entity expression was parsed.
	 *
	 * @return <code>true</code> if the entity expression was parsed;
	 * <code>false</code> otherwise
	 */
	public boolean hasEntityExpression()
	{
		return entityExpression != null &&
		      !entityExpression.isNull();
	}

	/**
	 * Determines whether the identifier <b>NOT</b> was parsed.
	 *
	 * @return <code>true</code> if the identifier <b>NOT</b> was parsed;
	 * <code>false</code> otherwise
	 */
	public boolean hasNot()
	{
		return hasNot;
	}

	/**
	 * Determines whether the identifier <b>OF</b> was parsed.
	 *
	 * @return <code>true</code> if the identifier <b>OF</b> was parsed;
	 * <code>false</code> otherwise
	 */
	public boolean hasOf()
	{
		return hasOf;
	}

	/**
	 * Determines whether a whitespace was found after <b>MEMBER</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>MEMBER</b>;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterMember()
	{
		return hasSpaceAfterMember;
	}

	/**
	 * Determines whether a whitespace was found after <b>OF</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>OF</b>;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterOf()
	{
		return hasSpaceAfterOf;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		// Parse 'NOT'
		hasNot = wordParser.startsWithIgnoreCase('N');

		if (hasNot)
		{
			wordParser.moveForward(NOT);
			wordParser.skipLeadingWhitespace();
		}

		// Parse 'MEMBER'
		wordParser.moveForward(MEMBER);

		hasSpaceAfterMember = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'OF'
		hasOf = wordParser.startsWithIdentifier(OF);

		if (hasOf)
		{
			wordParser.moveForward(OF);
			hasSpaceAfterOf = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse the collection-valued path expression
		collectionValuedPathExpression = parse
		(
			wordParser,
			queryBNF(CollectionValuedPathExpressionBNF.ID),
			tolerant
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer)
	{
		// Entity expression
		if (entityExpression != null)
		{
			entityExpression.toParsedText(writer);
		}

		// 'NOT'
		if (hasNot)
		{
			if (hasEntityExpression())
			{
				writer.append(SPACE);
			}

			writer.append(NOT);
		}

		if (hasNot || hasEntityExpression())
		{
			writer.append(SPACE);
		}

		// 'MEMBER'
		writer.append(MEMBER);

		if (hasSpaceAfterMember)
		{
			writer.append(SPACE);
		}

		// 'OF'
		if (hasOf)
		{
			writer.append(OF);
		}

		if (hasSpaceAfterOf)
		{
			writer.append(SPACE);
		}

		// Collection-valued path expression
		if (collectionValuedPathExpression != null)
		{
			collectionValuedPathExpression.toParsedText(writer);
		}
	}
}