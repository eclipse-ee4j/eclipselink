/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This expression tests whether or not the collection designated by the collection-valued path
 * expression is empty (i.e, has no elements).
 * <p>
 * <div nowrap><b>BNF:</b> <code>empty_collection_comparison_expression ::= collection_valued_path_expression IS [NOT] EMPTY</code><p>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EmptyCollectionComparisonExpression extends AbstractExpression {

	/**
	 * The actual <b>EMPTY</b> identifier found in the string representation of the JPQL query.
	 */
	private String emptyIdentifier;

	/**
	 * The {@link Expression} that represents the collection-valued path expression.
	 */
	private AbstractExpression expression;

	/**
	 * Determines whether a whitespace was parsed after <b>IS</b>.
	 */
	private boolean hasSpaceAfterIs;

	/**
	 * The actual <b>IS</b> identifier found in the string representation of the JPQL query.
	 */
	private String isIdentifier;

	/**
	 * The actual <b>IS</b> identifier found in the string representation of the JPQL query.
	 */
	private String notIdentifier;

	/**
	 * Creates a new <code>NullComparisonExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param expression The {@link Expression} that represents the collection-valued path expression
	 */
	public EmptyCollectionComparisonExpression(AbstractExpression parent, AbstractExpression expression) {
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
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {

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
		if (notIdentifier != null) {
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
	 * Returns the actual <b>EMPTY</b> found in the string representation of the JPQL query, which
	 * has the actual case that was used.
	 *
	 * @return The <b>EMPTY</b> identifier that was actually parsed
	 */
	public String getActualEmptyIdentifier() {
		return emptyIdentifier;
	}

	/**
	 * Returns the actual <b>IS</b> found in the string representation of the JPQL query, which has
	 * the actual case that was used.
	 *
	 * @return The <b>IS</b> identifier that was actually parsed, or an empty string if it was not
	 * parsed
	 */
	public String getActualIsIdentifier() {
		return (isIdentifier != null) ? isIdentifier : ExpressionTools.EMPTY_STRING;
	}

	/**
	 * Returns the actual <b>NOT</b> found in the string representation of the JPQL query, which has
	 * the actual case that was used.
	 *
	 * @return The <b>NOT</b> identifier that was actually parsed, or an empty string if it was not
	 * parsed
	 */
	public String getActualNotIdentifier() {
		return (notIdentifier != null) ? notIdentifier : ExpressionTools.EMPTY_STRING;
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

		if ((isIdentifier != null) && (notIdentifier != null)) {
			return IS_NOT_EMPTY;
		}

		if ((isIdentifier == null) && (notIdentifier != null)) {
			return "NOT_EMPTY";
		}

		if (isIdentifier != null) {
			return IS_EMPTY;
		}

		return EMPTY;
	}

	/**
	 * {@inheritDoc}
	 */
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(EmptyCollectionComparisonExpressionBNF.ID);
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
		return notIdentifier != null;
	}

	/**
	 * Determines whether a whitespace was found after <b>IS</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>IS</b>; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterIs() {
		return hasSpaceAfterIs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		String identifier = getText();

		if (identifier != null) {

			// 'IS'
			isIdentifier = wordParser.moveForward(IS);
			hasSpaceAfterIs = true;

			wordParser.moveForward(1);

			// 'NOT'
			if (identifier == IS_NOT_EMPTY) {
				notIdentifier = wordParser.moveForward(NOT);
				wordParser.moveForward(1);
			}

			// 'EMPTY'
			emptyIdentifier = wordParser.moveForward(EMPTY);
		}
		else {

			// Parse 'IS'
			if (wordParser.startsWithIdentifier(IS)) {
				isIdentifier = wordParser.moveForward(IS);
				hasSpaceAfterIs = wordParser.skipLeadingWhitespace() > 0;
			}

			// Parse 'NOT'
			if (wordParser.startsWithIdentifier(NOT)) {
				notIdentifier = wordParser.moveForward(NOT);
				wordParser.skipLeadingWhitespace();
			}

			// Parse 'EMPTY'
			emptyIdentifier = wordParser.moveForward(EMPTY);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// Expression
		if (expression != null) {
			expression.toParsedText(writer, actual);
			writer.append(SPACE);
		}

		// 'IS'
		if (isIdentifier != null) {
			writer.append(actual ? isIdentifier : IS);
		}

		if (hasSpaceAfterIs) {
			writer.append(SPACE);
		}

		// 'NOT'
		if (notIdentifier != null) {
			writer.append(actual ? notIdentifier : NOT);
			writer.append(SPACE);
		}

		// 'EMPTY'
		writer.append(actual ? emptyIdentifier : EMPTY);
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