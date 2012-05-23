/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.model.Problem;
import org.eclipse.persistence.jpa.jpql.parser.InternalWhenClauseBNF;
import org.eclipse.persistence.jpa.jpql.parser.ScalarExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.WhenClause;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * A <code><b>WHEN</b></code> predicate is used to calculate a condition and when it's true, its
 * <code><b>THEN</b></code> will be executed.
 * <p>
 * <div nowrap><b>BNF:</b> <code>when_clause ::= WHEN conditional_expression THEN scalar_expression</code><p>
 * or
 * <div nowrap><b>BNF:</b> <code>simple_when_clause ::= WHEN scalar_expression THEN scalar_expression</code><p>
 *
 * @see org.eclipse.persistence.jpa.jpql.parser.WhenClause WhenClause
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class WhenClauseStateObject extends AbstractConditionalClauseStateObject {

	/**
	 * The {@link StateObject} representing the scalar expression that is following the
	 * <code><b>THEN</b></code> identifier.
	 */
	private StateObject thenStateObject;

	/**
	 * Notify the {@link StateObject} representing the scalar expression that follows the
	 * <code><b>THEN</b></code> identifier has changed.
	 */
	public static final String THEN_STATE_OBJECT_PROPERTY = "thenStateObject";

	/**
	 * Creates a new <code>WhenClauseStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public WhenClauseStateObject(CaseExpressionStateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>WhenClauseStateObject</code>.
	 *
	 * @param parent The parent of this state object
	 * @param whenStateObject The {@link StateObject} representing the conditional expression that
	 * is following the <code><b>WHEN</b></code> identifier
	 * @param thenStateObject The {@link StateObject} representing the scalar expression that
	 * is following the <code><b>THEN</b></code> identifier
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public WhenClauseStateObject(CaseExpressionStateObject parent,
	                             StateObject whenStateObject,
	                             StateObject thenStateObject) {

		super(parent, whenStateObject);
		this.thenStateObject = parent(thenStateObject);
	}

	/**
	 * Creates a new <code>WhenClauseStateObject</code>.
	 *
	 * @param parent The parent of this state object
	 * @param whenJpqlFragment The string representation of the <code><b>WHEN</b></code> clause to
	 * parse and to convert into a {@link StateObject}
	 * @param thenJpqlFragment The string representation of the <code><b>THEN</b></code> expression
	 * to parse and to convert into a {@link StateObject}
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public WhenClauseStateObject(CaseExpressionStateObject parent,
	                             String whenJpqlFragment,
	                             String thenJpqlFragment) {

		super(parent);
		parseWhen(whenJpqlFragment);
		parseThen(thenJpqlFragment);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildren(List<StateObject> children) {
		super.addChildren(children);
		if (thenStateObject != null) {
			children.add(thenStateObject);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addProblems(List<Problem> problems) {

		super.addProblems(problems);

//		if (!hasWhenStateObject()) {
//			problems.add(buildProblem(StateObjectProblem.WhenClauseStateObject_MissingWhenStateObject));
//		}

		if (!hasThen()) {
			problems.add(buildProblem(StateObjectProblem.WhenClauseStateObject_MissingThenStateObject));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WhenClause getExpression() {
		return (WhenClause) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return WHEN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CaseExpressionStateObject getParent() {
		return (CaseExpressionStateObject) super.getParent();
	}

	/**
	 * Returns the {@link StateObject} representing the scalar expression that is following the
	 * <code><b>THEN</b></code> identifier.
	 *
	 * @return Either the {@link StateObject} representing the <code><b>THEN</b></code> expression
	 * or <code>null</code> if it's not defined
	 */
	public StateObject getThen() {
		return thenStateObject;
	}

	/**
	 * Determines whether the {@link StateObject} representing the scalar expression is present.
	 *
	 * @return <code>true</code> the scalar expression exists; otherwise <code>false</code>
	 */
	public boolean hasThen() {
		return thenStateObject != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			WhenClauseStateObject whenClause = (WhenClauseStateObject) stateObject;
			return areEquivalent(thenStateObject, whenClause.thenStateObject);
		}

		return false;
	}

	/**
	 * Parses the given JPQL fragment, which will represent the <code><b>THEN</b></code> expression.
	 * The JPQL fragment should not start with the identifier.
	 *
	 * @param jpqlFragment The string representation of the <code><b>THEN</b></code> expression
	 * to parse and to convert into a {@link StateObject}
	 */
	public void parseThen(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, ScalarExpressionBNF.ID);
		setThen(stateObject);
	}

	/**
	 * Parses the given JPQL fragment, which will represent the <code><b>WHEN</b></code> clause.
	 * The JPQL fragment should not start with the identifier.
	 *
	 * @param jpqlFragment The string representation of the <code><b>WHEN</b></code> clause to
	 * parse and to convert into a {@link StateObject}
	 */
	public void parseWhen(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, InternalWhenClauseBNF.ID);
		setConditional(stateObject);
	}

	/**
	 * Keeps a reference of the {@link WhenClause parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link WhenClause parsed object} representing a <code><b>WHEN</b></code>
	 * clause
	 */
	public void setExpression(WhenClause expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets the scalar expression to be the given {@link StateObject}.
	 *
	 * @param thenStateObject The {@link StateObject} representing the scalar expression
	 */
	public void setThen(StateObject thenStateObject) {
		StateObject oldThenStateObject = this.thenStateObject;
		this.thenStateObject = parent(thenStateObject);
		firePropertyChanged(THEN_STATE_OBJECT_PROPERTY, oldThenStateObject, thenStateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void toTextInternal(Appendable writer) throws IOException {

		super.toTextInternal(writer);
		writer.append(SPACE);

		// THEN scalar_expression
		writer.append(THEN);

		if (thenStateObject != null) {
			writer.append(SPACE);
			thenStateObject.toString(writer);
		}
	}
}