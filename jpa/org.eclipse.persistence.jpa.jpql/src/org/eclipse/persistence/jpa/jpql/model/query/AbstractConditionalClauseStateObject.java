/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.model.query;

import java.io.IOException;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.model.IConditionalExpressionStateObjectBuilder;
import org.eclipse.persistence.jpa.jpql.parser.ConditionalExpressionBNF;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;

/**
 * Conditional expressions are composed of other conditional expressions, comparison operations,
 * logical operations, path expressions that evaluate to boolean values, boolean literals, and
 * boolean input parameters. Arithmetic expressions can be used in comparison expressions.
 * Arithmetic expressions are composed of other arithmetic expressions, arithmetic operations, path
 * expressions that evaluate to numeric values, numeric literals, and numeric input parameters.
 * Arithmetic operations use numeric promotion. Standard bracketing () for ordering expression
 * evaluation is supported.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= identifier conditional_expression</code><p>
 *
 * @see org.eclipse.persistence.jpa.jpql.parser.AbstractConditionalClause AbstractConditionalClause
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractConditionalClauseStateObject extends AbstractStateObject {

	/**
	 * The builder is cached during the creation of the conditional expression.
	 */
	private IConditionalExpressionStateObjectBuilder builder;

	/**
	 * The state object representing the composition of the conditional expressions.
	 */
	private StateObject conditionalStateObject;

	/**
	 * Notifies the conditional expression property has changed.
	 */
	public static final String CONDITIONAL_STATE_OBJECT_PROPERTY = "conditionalStateObject";

	/**
	 * Creates a new <code>AbstractConditionalClauseStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractConditionalClauseStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>AbstractConditionalClauseStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param conditionalStateObject The {@link StateObject} representing the conditional expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractConditionalClauseStateObject(StateObject parent,
	                                               StateObject conditionalStateObject) {

		super(parent);
		this.conditionalStateObject = parent(conditionalStateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildren(List<StateObject> children) {
		super.addChildren(children);
		if (conditionalStateObject != null) {
			children.add(conditionalStateObject);
		}
	}

	/**
	 * Parses the given JPQL fragment as the right side of an <code><b>AND</b></code> expression. The
	 * current conditional expression will become the left side of the <code><b>AND</b></code>
	 * expression.
	 *
	 * @param jpqlFragment The portion of the query representing the right side of the
	 * <code><b>AND</b></code> expression
	 * @return The newly created {@link AndExpressionStateObject}
	 */
	public AndExpressionStateObject andParse(String jpqlFragment) {

		StateObject stateObject = buildStateObject(jpqlFragment, ConditionalExpressionBNF.ID);

		// Make sure the current conditional expression is encapsulated if it's an OR expression in
		// order to preserve logical operator precedence.
		// Example: A or B and we're adding C, it has to become (A or B) and C
		if (shouldEncapsulateORExpression(conditionalStateObject)) {
			conditionalStateObject = new SubExpressionStateObject(this, conditionalStateObject);
		}

		// Make sure the right side of the AND expression is encapsulated in order to preserve logical
		// operator precedence in the case it's an OR expression.
		// Example: A and we're adding B or C, it has to become A and (B or C)
		if (shouldEncapsulateORExpression(stateObject)) {
			stateObject = new SubExpressionStateObject(this, stateObject);
		}

		AndExpressionStateObject andStateObject = new AndExpressionStateObject(
			this,
			conditionalStateObject,
			stateObject
		);

		setConditional(andStateObject);
		return andStateObject;
	}

	/**
	 * Creates and returns a new {@link IConditionalStateObjectBuilder} that can be used to
	 * programmatically create a conditional expression and once the expression is complete,
	 * {@link IConditionalStateObjectBuilder#commit()} will push the {@link StateObject}
	 * representation of that expression as this clause's conditional expression.
	 *
	 * @return A new builder that can be used to quickly create a conditional expression
	 */
	public IConditionalExpressionStateObjectBuilder getBuilder() {
		if (builder == null) {
			builder = getQueryBuilder().buildStateObjectBuilder(this);
		}
		return builder;
	}

	/**
	 * Returns the state object representing the composition of the conditional expressions.
	 *
	 * @return The actual conditional expression
	 */
	public StateObject getConditional() {
		return conditionalStateObject;
	}

	/**
	 * Returns the JPQL identifier of this clause.
	 *
	 * @return The JPQL identifier of this conditional clause
	 */
	public abstract String getIdentifier();

	/**
	 * Determines whether the {@link StateObject} representing the conditional expression is present
	 * or not.
	 *
	 * @return <code>true</code> if the conditional expression is not <code>null</code>;
	 * <code>false</code> otherwise
	 */
	public boolean hasConditional() {
		return conditionalStateObject != null;
	}

	/**
	 * Parses the given JPQL fragment as the right side of an <code><b>OR</b></code> expression. The
	 * current conditional expression will become the left side of the <code><b>OR</b></code>
	 * expression.
	 *
	 * @param jpqlFragment The portion of the query representing the right side of the
	 * <code><b>OR</b></code> expression
	 * @return The newly created {@link OrExpressionStateObject}
	 */
	public OrExpressionStateObject orParse(String jpqlFragment) {

		OrExpressionStateObject orStateObject = new OrExpressionStateObject(
			this,
			conditionalStateObject,
			buildStateObject(jpqlFragment, ConditionalExpressionBNF.ID)
		);

		setConditional(orStateObject);
		return orStateObject;
	}

	/**
	 * Parses the given JPQL fragment, which represents a conditional expression, and creates the
	 * {@link StateObject}.
	 *
	 * @param jpqlFragment The portion of the query representing a conditional expression
	 */
	public void parse(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, ConditionalExpressionBNF.ID);
		setConditional(stateObject);
	}

	/**
	 * Sets the given {@link StateObject} to be the conditional expression of this clause.
	 *
	 * @param conditionalStateObject The new {@link StateObject} representing the conditional
	 * expression
	 */
	public void setConditional(StateObject conditionalStateObject) {

		builder = null;

		StateObject oldConditionalStateObject = this.conditionalStateObject;
		this.conditionalStateObject = parent(conditionalStateObject);
		firePropertyChanged(CONDITIONAL_STATE_OBJECT_PROPERTY, oldConditionalStateObject, conditionalStateObject);
	}

	protected boolean shouldEncapsulateORExpression(StateObject stateObject) {

		if (stateObject == null) {
			return false;
		}

		final boolean[] encapsulate = { false };

		StateObjectVisitor visitor = new AbstractStateObjectVisitor() {
			@Override
			public void visit(OrExpressionStateObject stateObject) {
				encapsulate[0] = true;
			}
		};

		stateObject.accept(visitor);
		return encapsulate[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {

		writer.append(getIdentifier());

		if (conditionalStateObject != null) {
			writer.append(SPACE);
			conditionalStateObject.toString(writer);
		}
	}
}