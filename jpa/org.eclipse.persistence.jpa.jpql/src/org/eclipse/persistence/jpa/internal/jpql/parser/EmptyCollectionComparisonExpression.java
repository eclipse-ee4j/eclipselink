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

/**
 * This expression tests whether or not the collection designated by the collection-valued path
 * expression is empty (i.e, has no elements).
 * <p>
 * <div nowrap><b>BNF:</b> <code>empty_collection_comparison_expression ::= collection_valued_path_expression IS [NOT] EMPTY</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EmptyCollectionComparisonExpression extends AbstractExpression {

	/**
	 * The {@link Expression} that represents the collection-valued path expression.
	 */
	private AbstractExpression expression;

	/**
	 * Determines whether the identifier <b>IS</b> was parsed.
	 */
	private boolean hasIs;

	/**
	 * Determines whether the identifier <b>NOT</b> was parsed.
	 */
	private boolean hasNot;

	/**
	 * Determines whether a whitespace was parsed after <b>IS</b>.
	 */
	private boolean hasSpaceAfterIs;

	/**
	 * Creates a new <code>NullComparisonExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param expression The {@link Expression} that represents the collection-valued path expression
	 */
	EmptyCollectionComparisonExpression(AbstractExpression parent, AbstractExpression expression) {
		super(parent);
		updateExpression(expression);
	}

	/**
	 * Creates a new <code>NullComparisonExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param identifier The actual identifier, which is either {@link Expression#IS_EMPTY IS_EMPTY}
	 * or {@link Expression#IS_NOT_EMPTY IS_NOT_EMPTY}
	 * @param expression The {@link Expression} that represents the collection-valued path expression
	 */
	EmptyCollectionComparisonExpression(AbstractExpression parent,
	                                    String identifier,
	                                    AbstractExpression expression) {

		super(parent, identifier);
		updateExpression(expression);
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
		getExpression().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addChildrenTo(Collection<Expression> children) {
		children.add(getExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children) {

		// Expression
		if (expression != null) {
			children.add(expression);
		}

		if (hasExpression()) {
			children.add(buildStringExpression(SPACE));
		}

		// 'IS'
		children.add(buildStringExpression(IS));

		if (hasSpaceAfterIs) {
			children.add(buildStringExpression(SPACE));
		}

		// 'NOT'
		if (hasNot) {
			children.add(buildStringExpression(NOT));
		}

		children.add(buildStringExpression(SPACE));

		// 'EMPTY'
		children.add(buildStringExpression(EMPTY));
	}

	private StateFieldPathToCollectionValuedPathConverter buildConverter(AbstractExpression expression) {
		return new StateFieldPathToCollectionValuedPathConverter(this, expression);
	}

	/**
	 * Returns the {@link Expression} that represents the collection-valued path expression if it was
	 * parsed.
	 *
	 * @return The expression that was parsed representing the collection valued path expression
	 */
	public Expression getExpression() {
		if (expression == null) {
			expression = buildNullExpression();
		}
		return expression;
	}

	/**
	 * Returns the identifier for this expression that may include <b>NOT</b> if it was parsed. The
	 * identifier <b>IS</b> should always be part of the identifier but it is possible it is not
	 * present when this expression is invalid.
	 *
	 * @return Either <b>IS NOT EMPTY</b>, <b>NOT EMPTY</b>, <b>IS EMPTY</b> or <b>EMPTY</b>
	 */
	public String getIdentifier() {
		if ( hasIs && hasNot) return IS_NOT_EMPTY;
		if (!hasIs && hasNot) return "NOT_EMPTY";
		if ( hasIs)           return IS_EMPTY;
		return EMPTY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return queryBNF(EmptyCollectionComparisonExpressionBNF.ID);
	}

	/**
	 * Determines whether the expression was parsed.
	 *
	 * @return <code>true</code> if the expression was parsed; <code>false</code> otherwise
	 */
	public boolean hasExpression() {
		return expression != null &&
		      !expression.isNull();
	}

	/**
	 * Determines whether the identifier <b>NOT</b> was part of the query.
	 *
	 * @return <code>true</code> if the identifier <b>NOT</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasNot() {
		return hasNot;
	}

	/**
	 * Determines whether a whitespace was found after <b>IS</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>IS</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterIs() {
		return hasSpaceAfterIs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant) {

		String identifier = getText();

		if (identifier != null) {
			wordParser.moveForward(identifier);
			hasIs = true;
			hasSpaceAfterIs = true;
			hasNot = (identifier == IS_NOT_EMPTY);
		}
		else {
			// Parse 'IS'
			if (wordParser.startsWithIdentifier(IS)) {
				hasIs = true;
				wordParser.moveForward(IS);
				hasSpaceAfterIs = wordParser.skipLeadingWhitespace() > 0;
			}

			// Parse 'NOT'
			hasNot = wordParser.startsWithIdentifier(NOT);

			// Remove 'NOT'
			if (hasNot) {
				wordParser.moveForward(NOT);
				wordParser.skipLeadingWhitespace();
			}

			// Remove 'EMPTY'
			wordParser.moveForward(EMPTY);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer, boolean includeVirtual) {

		// Expression
		if (expression != null) {
			expression.toParsedText(writer, includeVirtual);
			writer.append(SPACE);
		}

		// Identifier
		writer.append(getIdentifier().toString());
	}

	private void updateExpression(AbstractExpression expression) {

		// Make sure a StateFieldPathExpression is converted into a
		// CollectionValuedPathExpression
		if (expression != null) {
			StateFieldPathToCollectionValuedPathConverter converter = buildConverter(expression);
			expression.accept(converter);

			this.expression = converter.expression;
			this.expression.setParent(this);

			// The old expression needs to be parented in order to be removed from
			// the parsing system
			expression.setParent(this);
		}
	}

	/**
	 * This visitor converts a {@link StateFieldPathExpression} into a {@link CollectionValuedPathExpression}.
	 * The parser only creates state field path expressions but this expression only supports a
	 * collection valued path expression.
	 */
	private static class StateFieldPathToCollectionValuedPathConverter extends AbstractExpressionVisitor {

		private AbstractExpression expression;
		private AbstractExpression parent;

		/**
		 * Creates a new <code>StateFieldPathToCollectionValuedPathConverter</code>.
		 *
		 * @param parent The parent of the expression
		 * @param expression The expression that might need to be converted
		 */
		StateFieldPathToCollectionValuedPathConverter(AbstractExpression parent,
		                                              AbstractExpression expression) {
			super();

			this.parent     = parent;
			this.expression = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression) {
			this.expression = new CollectionValuedPathExpression(parent, expression.getText());
		}
	}
}