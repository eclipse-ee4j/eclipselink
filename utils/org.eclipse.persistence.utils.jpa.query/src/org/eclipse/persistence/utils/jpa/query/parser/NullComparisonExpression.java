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
package org.eclipse.persistence.utils.jpa.query.parser;

import java.util.Collection;
import java.util.List;

/**
 * A null comparison tests whether or not the single-valued path expression or input parameter is a
 * <b>NULL</b> value.
 * <p>
 * <div nowrap><b>BNF:</b> <code>null_comparison_expression ::= {single_valued_path_expression | input_parameter} IS [NOT] NULL</code><p>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class NullComparisonExpression extends AbstractExpression {

	/**
	 * The expression tested for being <code>null</code> or not.
	 */
	private AbstractExpression expression;

	/**
	 * Determines whether 'NOT' is present in the query or not.
	 */
	private boolean hasNot;

	/**
	 * Creates a new <code>NullComparisonExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param expression The expression before the identifier
	 */
	NullComparisonExpression(AbstractExpression parent, AbstractExpression expression) {
		super(parent);
		updateExpression(expression);
	}

	/**
	 * Creates a new <code>NullComparisonExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param expression The expression before the identifier
	 */
	NullComparisonExpression(AbstractExpression parent,
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
		if (hasExpression()) {
			children.add(expression);
			children.add(buildStringExpression(SPACE));
		}

		// Identifier
		children.add(buildStringExpression(getIdentifier().toString()));
	}

	/**
	 * Returns the expression being tested for being <code>null</code>.
	 *
	 * @return Either the parsed expression or the <code>null</code>-expression
	 */
	public Expression getExpression() {
		if (expression == null) {
			expression = buildNullExpression();
		}
		return expression;
	}

	/**
	 * Returns the identifier for this expression that may include <b>NOT</b> if it was parsed.
	 *
	 * @return Either <b>IS NULL</b> or <b>IS NOT NULL</b>
	 */
	public Type getIdentifier() {
		return hasNot ? Type.IS_NOT_NULL : Type.IS_NULL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF() {
		return queryBNF(NullComparisonExpressionBNF.ID);
	}

	/**
	 * Determines whether the expression preceding the identifier was parsed.
	 *
	 * @return <code>true</code> the expression preceding the identifier was parsed;
	 * <code>false</code> otherwise
	 */
	public boolean hasExpression() {
		return expression != null &&
		      !expression.isNull();
	}

	/**
	 * Determines whether <b>NOT</b> is used in the query.
	 *
	 * @return <code>true</code> if <b>NOT</b> is present in the query; <code>false</code> otherwise
	 */
	public boolean hasNot() {
		return hasNot;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant) {

		String identifier = getText();

		if (identifier != null) {
			wordParser.moveForward(identifier);
			hasNot = (identifier == IS_NOT_NULL);
		}
		else {
			// 'IS'
			wordParser.moveForward(IS);

			wordParser.skipLeadingWhitespace();

			// 'NOT'
			hasNot = wordParser.startsWithIdentifier(NOT);

			if (hasNot) {
				wordParser.moveForward(NOT);
				wordParser.skipLeadingWhitespace();
			}

			// 'NULL'
			wordParser.moveForward(NULL);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer) {

		if (hasExpression()) {
			expression.toParsedText(writer);
			writer.append(SPACE);
		}

		writer.append(getIdentifier().toString());
	}

	private void updateExpression(AbstractExpression expression) {
		if (expression != null) {
			this.expression = expression;
			this.expression.setParent(this);
		}
	}

	/**
	 * This enumeration lists all the possible choices when using a null comparison expression.
	 */
	public enum Type {

		/**
		 * The constant for 'IS NOT NULL'.
		 */
		IS_NOT_NULL,

		/**
		 * The constant for 'IS NULL'.
		 */
		IS_NULL;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return name().replace(UNDERSCORE, SPACE);
		}
	}
}