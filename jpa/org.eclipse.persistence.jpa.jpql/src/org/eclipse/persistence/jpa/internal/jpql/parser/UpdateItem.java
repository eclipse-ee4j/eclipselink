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

/**
 * The <code>new_value</code> specified for an update operation must be compatible in type with the
 * field to which it is assigned.
 *
 * <div nowrap><b>BNF:</b> <code>update_item ::= [identification_variable.]{state_field | single_valued_association_field} = new_value</code><p>
 *
 * @see UpdateClause
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class UpdateItem extends AbstractExpression {

	/**
	 * Determines whether the equal sign was parsed or not.
	 */
	private boolean hasEqualSign;

	/**
	 * Determines whether a whitespace was parsed after the equal sign or not.
	 */
	private boolean hasSpaceAfterEqualSign;

	/**
	 * Determines whether a whitespace was parsed before the equal sign or not
	 */
	private boolean hasSpaceAfterStateFieldPathExpression;

	/**
	 * The expression representing the new value.
	 */
	private AbstractExpression newValue;

	/**
	 * The expression representing the state field to have its value updated.
	 */
	private AbstractExpression stateFieldExpression;

	/**
	 * Creates a new <code>UpdateItem</code>.
	 *
	 * @param parent The parent of this expression
	 */
	UpdateItem(AbstractExpression parent) {
		super(parent);
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
		getStateFieldPathExpression().accept(visitor);
		getNewValue().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addChildrenTo(Collection<Expression> children) {
		children.add(getStateFieldPathExpression());
		children.add(getNewValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children) {

		// State field expression
		if (stateFieldExpression != null) {
			children.add(stateFieldExpression);
		}

		if (hasSpaceAfterStateFieldPathExpression) {
			children.add(buildStringExpression(SPACE));
		}

		// '='
		if (hasEqualSign) {
			children.add(buildStringExpression(EQUAL));
		}

		if (hasSpaceAfterEqualSign) {
			children.add(buildStringExpression(SPACE));
		}

		// New value
		if (newValue != null) {
			children.add(newValue);
		}
	}

	/**
	 * Returns the {@link Expression} representing the new value, which is the new value of the
	 * property.
	 *
	 * @return The expression for the new value
	 */
	public Expression getNewValue() {
		if (newValue == null) {
			newValue = buildNullExpression();
		}
		return newValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF() {
		return queryBNF(UpdateItemBNF.ID);
	}

	/**
	 * Returns the {@link Expression} representing the state field path expression, which is the
	 * property that should get updated.
	 *
	 * @return The expression for the state field path expression
	 */
	public Expression getStateFieldPathExpression() {
		if (stateFieldExpression == null) {
			stateFieldExpression = buildNullExpression();
		}
		return stateFieldExpression;
	}

	/**
	 * Determines whether the equal sign was parsed or not.
	 *
	 * @return <code>true</code> if the equal sign was parsed; <code>false</code> otherwise
	 */
	public boolean hasEqualSign() {
		return hasEqualSign;
	}

	/**
	 * Determines whether the new value section of the query was parsed.
	 *
	 * @return <code>true</code> the new value was parsed; <code>false</code> if nothing was parsed
	 */
	public boolean hasNewValue() {
		return newValue != null &&
		      !newValue.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after the equal sign or not.
	 *
	 * @return <code>true</code> if there was a whitespace after the equal sign; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterEqualSign() {
		return hasSpaceAfterEqualSign;
	}

	/**
	 * Determines whether a whitespace was parsed after the state field path expression not.
	 *
	 * @return <code>true</code> if there was a whitespace after the state field path expression;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterStateFieldPathExpression() {
		return hasSpaceAfterStateFieldPathExpression;
	}

	/**
	 * Determines whether the state field was parsed.
	 *
	 * @return <code>true</code> the state field was parsed; <code>false</code> otherwise
	 */
	public boolean hasStateFieldPathExpression() {
		return stateFieldExpression != null &&
		      !stateFieldExpression.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant) {

		// Parse state field
		if (tolerant) {
			stateFieldExpression = parse(
				wordParser,
				queryBNF(UpdateItemStateFieldPathExpressionBNF.ID),
				tolerant
			);
		}
		else {
			stateFieldExpression = new StateFieldPathExpression(
				this,
				wordParser.word()
			);

			stateFieldExpression.parse(wordParser, tolerant);
		}

		hasSpaceAfterStateFieldPathExpression = wordParser.skipLeadingWhitespace() > 0;

		// Parse '='
		hasEqualSign = wordParser.startsWith('=');

		if (hasEqualSign) {
			wordParser.moveForward(1);
			hasSpaceAfterEqualSign = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse new value
		newValue = parse(
			wordParser,
			queryBNF(NewValueBNF.ID),
			tolerant
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer) {

		// State field expression
		if (stateFieldExpression != null) {
			stateFieldExpression.toParsedText(writer);
		}

		if (hasSpaceAfterStateFieldPathExpression) {
			writer.append(SPACE);
		}

		// '='
		if (hasEqualSign) {
			writer.append(EQUAL);
		}

		if (hasSpaceAfterEqualSign) {
			writer.append(SPACE);
		}

		// New value
		if (newValue != null) {
			newValue.toParsedText(writer);
		}
	}
}