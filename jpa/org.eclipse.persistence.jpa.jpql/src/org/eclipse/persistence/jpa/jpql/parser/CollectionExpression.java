/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.parser;

import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * A <code>CollectionExpression</code> wraps many expression which they are separated by spaces
 * and/or commas.
 *
 * <div nowrap><b>BNF:</b> <code>expression ::= child_item {, child_item }*</code>
 * <br>
 * or
 * <br>
 * <div nowrap><b>BNF:</b> <code>expression ::= child_item { child_item }*</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class CollectionExpression extends AbstractExpression {

	/**
	 * The {@link Expression Expressions} that forms a collection within another expression.
	 */
	private List<AbstractExpression> children;

	/**
	 * The list of flags used to determine where to separate two child {@link Expression Expressions}
	 * with a comma or with a space only.
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
	 * @param spaces The list of flags used to determine when to add a space after an {@link Expression}
	 * @param commas The list of flags used to determine when to add a comma after an {@link Expression}
	 */
	public CollectionExpression(AbstractExpression parent,
	                            List<AbstractExpression> children,
	                            List<Boolean> commas,
	                            List<Boolean> spaces) {

		this(parent, children, commas, spaces, false);
	}

	/**
	 * Creates a new <code>CollectionExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param children The list of children that are regrouped together
	 * @param commas The list of flags used to determine when to add a comma after an {@link Expression}
	 * @param spaces The list of flags used to determine when to add a space after an {@link Expression}
	 * @param temporary Flag used to determine if this expression is temporarily used, which means
	 * the children will not be parented to this object
	 */
	public CollectionExpression(AbstractExpression parent,
	                            List<AbstractExpression> children,
	                            List<Boolean> commas,
	                            List<Boolean> spaces,
	                            boolean temporary) {
		super(parent);

		this.children = children;
		this.commas   = commas;
		this.spaces   = spaces;

		if (!temporary) {
			updateBackpointers();
		}
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
		for (Expression child : children()) {
			child.accept(visitor);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {

		// Make sure all children are non null
		for (int index = 0, childCount = this.children.size(); index < childCount; index++) {
			getChildInternal(index);
		}

		children.addAll(this.children);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {

		children();

		for (int index = 0, count = this.children.size(); index < count; index++) {
			Expression expression = getChild(index);
			children.add(expression);

			// Write ','
			if (hasComma(index)) {
				children.add(buildStringExpression(COMMA));
			}

			// Write whitespace
			if (hasSpace(index)) {
				children.add(buildStringExpression(SPACE));
			}
		}
	}

	/**
	 * Returns the count of child {@link Expression expressions}.
	 *
	 * @return The total count of {@link Expression expressions} aggregated with spaces and/or commas
	 */
	public int childrenSize() {
		children();
		return children.size();
	}

	/**
	 * Determines whether this {@link CollectionExpression} ends with a comma, which means the last
	 * {@link Expression} is a "<code>null</code>" expression.
	 *
	 * @return <code>true</code> if the string representation of this {@link CollectionExpression}
	 * ends with a comma (the ending space is not checked)
	 */
	public boolean endsWithComma() {

		children();

		if (children.get(children.size() - 1).isNull()) {
			return commas.get(commas.size() - 2);
		}

		return false;
	}

	/**
	 * Determines whether this {@link CollectionExpression} ends with a space, which means the last
	 * {@link Expression} is a "<code>null</code>" expression.
	 *
	 * @return <code>true</code> if the string representation of this {@link CollectionExpression}
	 * ends with a space (the ending comma is not checked)
	 */
	public boolean endsWithSpace() {

		children();

		if (children.get(children.size() - 1).isNull()) {
			return spaces.get(spaces.size() - 2);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF findQueryBNF(AbstractExpression expression) {
		return getParent().findQueryBNF(expression);
	}

	/**
	 * Retrieves the child {@link Expression} at the given position.
	 *
	 * @param index The position of the child {@link Expression} to retrieve
	 * @return The child {@link Expression} at the given position
	 */
	public Expression getChild(int index) {
		return getChildInternal(index);
	}

	private AbstractExpression getChildInternal(int index) {

		AbstractExpression child = children.get(index);

		if (child == null) {
			child = buildNullExpression();
			children.set(index, child);
		}

		return child;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getParent().getQueryBNF();
	}

	/**
	 * Determines whether a comma was parsed at the given position. The index is the position of the
	 * comma that is following the child at the same position.
	 *
	 * @param index The index of the child {@link Expression} to verify if there is a comma following it
	 * @return <code>true</code> if a comma is following the child {@link Expression} at the given
	 * index; <code>false</code> otherwise
	 */
	public boolean hasComma(int index) {
		children();
		return (index < commas.size()) && commas.get(index);
	}

	/**
	 * Determines whether a space was parsed at the given position. The index is the position of the
	 * space that is following the child at the same position, which is after a comma, if one was
	 * also parsed at that location.
	 *
	 * @param index The index of the child {@link Expression} to verify if there is a space following
	 * it, which could be after a comma, if one was parsed
	 * @return <code>true</code> if a space is following the child {@link Expression} at the given
	 * index; <code>false</code> otherwise
	 */
	public boolean hasSpace(int index) {
		children();
		return (index < spaces.size()) && (spaces.get(index) || hasComma(index) && (index + 1 < children.size() ? !getChildInternal(index + 1).isNull() : false));
	}

	/**
	 * Retrieves the index of the given <code>Expression</code>.
	 *
	 * @param expression The <code>Expression</code> that might be a child of this expression
	 * @return The index in the collection of the given <code>Expression</code> or -1 if it is not a child
	 */
	public int indexOf(Expression expression) {
		children();
		return children.indexOf(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {
		throw new IllegalAccessError("This method shouln't be invoked, text=" + wordParser);
	}

	/**
	 * Returns a string representation of this {@link Expression} and its children. The expression
	 * should contain whitespace even if the beautified version would not have any. For instance,
	 * "SELECT e " should be returned where {@link Expression#toParsedText()} would return "SELECT e".
	 *
	 * @param endIndex The index used to determine when to create the string representation, which
	 * is exclusive
	 * @return The string representation of this {@link Expression}
	 */
	public String toActualText(int endIndex) {
		StringBuilder writer = new StringBuilder();
		toParsedText(writer, endIndex, true);
		return writer.toString();
	}

	/**
	 * Generates a string representation of this {@link CollectionExpression}.
	 *
	 * @param endIndex The index used to determine when to create the string representation, which
	 * is exclusive
	 * @return The string representation of this {@link Expression}
	 */
	public String toParsedText(int endIndex) {
		StringBuilder writer = new StringBuilder();
		toParsedText(writer, endIndex, false);
		return writer.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {
		toParsedText(writer, childrenSize(), actual);
	}

	private void toParsedText(StringBuilder writer, int endIndex, boolean actual) {

		for (int index = 0, count = children.size(); index < count; index++) {

			AbstractExpression expression = children.get(index);

			// Write the child expression
			if (expression != null) {
				expression.toParsedText(writer, actual);
			}

			// Write ','
			if (commas.get(index)) {
				writer.append(COMMA);

				// If there is a space, then add it
				if (spaces.get(index)) {
					writer.append(SPACE);
				}
				// Otherwise check if the next expression is not null, if it's null,
				// then a space will not be added
				else if (index + 1 < count) {
					AbstractExpression nextExpression = children.get(index + 1);

					if ((nextExpression != null) && !nextExpression.isNull()) {
						writer.append(SPACE);
					}
				}
			}
			// Write ' '
			else  if (spaces.get(index)) {
				writer.append(SPACE);
			}
		}
	}

	private void updateBackpointers() {
		for (AbstractExpression child : children) {
			if (child != null) {
				child.setParent(this);
			}
		}
	}
}