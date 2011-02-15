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
 * A <code>CollectionExpression</code> wraps many expression which they are
 * separated by spaces and/or commas.
 *
 * <div nowrap><b>BNF:</b> <code>expression ::= child_item {, child_item }*</code>
 * <br>
 * or
 * <br>
 * <div nowrap><b>BNF:</b> <code>expression ::= child_item { child_item }*</code><p>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class CollectionExpression extends AbstractExpression
{
	/**
	 * The <code>Expression</code>s that forms a collection within another
	 * expression.
	 */
	private List<AbstractExpression> children;

	/**
	 * The list of flags used to determine where to separate two child
	 * <code>Expression</code>s with a comma or with a space only.
	 */
	private List<Boolean> commas;

	/**
	 * Flag used to determine when a space is required after a comma.
	 */
	private List<Boolean> spaces;

	/**
	 * Creates a new <code>CollectionExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param children The list of children that are regrouped together
	 * @param spaces The list of flags used to determine when to add a
	 * space after an {@link Expression}
	 * @param commas The list of flags used to determine when to add a
	 * comma after an {@link Expression}
	 */
	CollectionExpression(AbstractExpression parent,
	                     List<AbstractExpression> children,
	                     List<Boolean> spaces,
	                     List<Boolean> commas)
	{
		super(parent);

		this.children          = children;
		this.commas = commas;
		this.spaces = spaces;

		updateChildrenBackpointer();
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
		// Make sure all children are non null
		for (int index = 0, childCount = this.children.size(); index < childCount; index++)
		{
			getChild(index);
		}

		children.addAll(this.children);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children)
	{
		for (int index = 0, count = this.children.size(); index < count; index++)
		{
			Expression expression = getChild(index);
			children.add((AbstractExpression) expression);

			boolean hasComma = hasComma(index);

			// Write ','
			if (hasComma)
			{
				children.add(buildStringExpression(COMMA));
			}

			// Write whitespace
			if (hasSpace(index) || (hasComma && (index + 1 < count)))
			{
				children.add(buildStringExpression(SPACE));
			}
		}
	}

	/**
	 * Returns the count of child {@link Expression expressions}.
	 *
	 * @return The total count of {@link Expression expressions} aggregated with
	 * spaces and/or commas
	 */
	public int childrenSize()
	{
		return children.size();
	}

	/**
	 * Returns the count of flags used to determine if two child expression are
	 * separated by a comma.
	 *
	 * @return The total count of flags used to add a comma between two
	 * children
	 */
	int commasSize()
	{
		return commas.size();
	}

	/**
	 * Determines whether this {@link Expression} ends with a comma, which means
	 * the last {@link Expression} is a "<code>null</code>" expression.
	 *
	 * @return <code>true</code> if the string representation of this
	 * {@link CollectionExpression} is a comma
	 */
	public boolean endsWithComma()
	{
		return hasComma(children.size() - 1);
	}

	/**
	 * Determines whether this {@link Expression} ends with a space, which means
	 * the last {@link Expression} is a "<code>null</code>" expression.
	 *
	 * @return <code>true</code> if the string representation of this
	 * {@link CollectionExpression} is a space
	 */
	public boolean endsWithSpace()
	{
		return hasSpace(children.size() - 1);
	}

	/**
	 * Retrieves the child {@link Expression} at the given position.
	 *
	 * @param index The position of the child {@link Expression} to retrieve
	 * @return The child {@link Expression} at the given position
	 */
	public Expression getChild(int index)
	{
		AbstractExpression child = children.get(index);

		if (child == null)
		{
			child = buildNullExpression();
			children.set(index, child);
		}

		return child;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF()
	{
		return getParent().getQueryBNF();
	}

	/**
	 * Determines whether a comma was parsed at the given position. The index is
	 * the position of the comma that is following the child at the same position.
	 *
	 * @param index The index of the child {@link Expression} to verify if there
	 * is a comma following it
	 * @return <code>true</code> if a comma is following the child {@link
	 * Expression} at the given index; <code>false</code> otherwise
	 */
	public boolean hasComma(int index)
	{
		return index < commas.size() ? commas.get(index) : false;
	}

	/**
	 * Determines whether a space was parsed at the given position. The index is
	 * the position of the space that is following the child at the same position,
	 * which is after a comma, if one was also parsed at that location.
	 *
	 * @param index The index of the child {@link Expression} to verify if there
	 * is a space following it, which could be after a comma, if one was parsed
	 * @return <code>true</code> if a space is following the child {@link
	 * Expression} at the given index; <code>false</code> otherwise
	 */
	public boolean hasSpace(int index)
	{
		return index < spaces.size() ? spaces.get(index) || (hasComma(index) && (index + 1 < childrenSize())) : false;
	}

	/**
	 * Retrieves the index of the given <code>Expression</code>.
	 *
	 * @param expression The <code>Expression</code> that might be a child of
	 * this expression
	 * @return The index in the collection of the given <code>Expression</code>
	 * or -1 if it is not a child
	 */
	public int indexOf(Expression expression)
	{
		return children.indexOf(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		throw new IllegalAccessError("This method shouln't be invoked, text=" + wordParser);
	}

	/**
	 * Returns the count of flags used to determine if two child expression are
	 * separated by a space.
	 *
	 * @return The total count of flags used to add a whitespace between two
	 * children
	 */
	int spacesSize()
	{
		return commas.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer)
	{
		for (int index = 0, count = children.size(); index < count; index++)
		{
			AbstractExpression expression = (AbstractExpression) getChild(index);
			expression.toParsedText(writer);

			// Write ','
			if (hasComma(index))
			{
				writer.append(COMMA);
			}

			// Write whitespace, even if no whitespace was parsed after a comma,
			// we still add one since the format is "..., ..."
			if (hasSpace(index))
			{
				writer.append(SPACE);
			}
		}
	}

	private void updateChildrenBackpointer()
	{
		for (AbstractExpression child : children)
		{
			if (child != null)
			{
				child.setParent(this);
			}
		}
	}
}