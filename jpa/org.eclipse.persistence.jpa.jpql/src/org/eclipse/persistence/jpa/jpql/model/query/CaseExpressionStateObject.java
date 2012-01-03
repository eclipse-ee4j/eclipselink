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
import org.eclipse.persistence.jpa.jpql.model.Problem;
import org.eclipse.persistence.jpa.jpql.parser.CaseExpression;
import org.eclipse.persistence.jpa.jpql.parser.CaseOperandBNF;
import org.eclipse.persistence.jpa.jpql.parser.ElseExpressionBNF;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * A <code><b>CASE</b></code> predicate is used to calculate a condition and when it's <code>true</code>,
 * its <code><b>THEN</b></code> expression will be executed.
 *
 * <div nowrap><b>BNF:</b> <code>general_case_expression ::= CASE when_clause {when_clause}* ELSE scalar_expression END</code>
 * or
 * <div nowrap><b>BNF:</b> <code>simple_case_expression ::= CASE case_operand simple_when_clause {simple_when_clause}* ELSE scalar_expression END</code><p>
 *
 * @see WhenClauseStateObject
 * @see org.eclipse.persistence.jpa.jpql.parser.CaseExpression CaseExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class CaseExpressionStateObject extends AbstractListHolderStateObject<WhenClauseStateObject> {

	/**
	 * The {@link StateObject} representing the case operand that is following the <code><b>CASE</b></code>
	 * identifier.
	 */
	private StateObject caseOperandStateObject;

	/**
	 * The {@link StateObject} representing the scalar expression that is following the <code><b>ELSE</b></code>
	 * identifier.
	 */
	private StateObject elseStateObject;

	/**
	 * Notify the {@link StateObject} representing the case operand that follows the
	 * <code><b>CASE</b></code> identifier has changed.
	 */
	public static final String CASE_OPERAND_STATE_OBJECT_PROPERTY = "caseOperandStateObject";

	/**
	 * Notify the {@link StateObject} representing the else expression that follows the
	 * <code><b>ELSE</b></code> identifier has changed.
	 */
	public static final String ELSE_STATE_OBJECT_PROPERTY = "elseStateObject";

	/**
	 * Notify the list of {@link StateObject StateObjects} representing the when clauses that
	 * follow the <code><b>WHEN</b></code> has changed.
	 */
	public static final String WHEN_CLAUSE_STATE_OBJECT_LIST = "whenStateObjects";

	/**
	 * Creates a new <code>CaseExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CaseExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>CaseExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param whenClauseStateObjects The list of {@link WhenClauseStateObject WhenClauseStateObjects}
	 * that are representing the <code><b>WHEN</b></code> clauses
	 * @param elseStateObject The {@link StateObject} representing the scalar expression that is
	 * following the <code><b>ELSE</b></code> identifier
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CaseExpressionStateObject(StateObject parent,
	                                 List<WhenClauseStateObject> whenClauseStateObjects,
	                                 StateObject elseStateObject) {

		this(parent, null, whenClauseStateObjects, elseStateObject);
	}

	/**
	 * Creates a new <code>CaseExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param caseOperandStateObject The {@link StateObject} representing the case operand that is
	 * following the <code><b>CASE</b></code> identifier or <code>null</code> if none is declared
	 * @param whenClauseStateObjects The list of {@link WhenClauseStateObject WhenClauseStateObjects}
	 * that are representing the <code><b>WHEN</b></code> clauses
	 * @param elseStateObject The {@link StateObject} representing the scalar expression that is
	 * following the <code><b>ELSE</b></code> identifier
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CaseExpressionStateObject(StateObject parent,
	                                 StateObject caseOperandStateObject,
	                                 List<WhenClauseStateObject> whenClauseStateObjects,
	                                 StateObject elseStateObject) {

		super(parent, whenClauseStateObjects);
		this.elseStateObject        = parent(elseStateObject);
		this.caseOperandStateObject = parent(caseOperandStateObject);
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
		if (caseOperandStateObject != null) {
			children.add(caseOperandStateObject);
		}
		super.addChildren(children);
		if (elseStateObject != null) {
			children.add(elseStateObject);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addProblems(List<Problem> problems) {
		super.addProblems(problems);
		// No validation required
	}

	/**
	 * Adds a new <code>WHEN</code> clause.
	 *
	 * @return The newly created {@link WhenClauseStateObject}
	 */
	public WhenClauseStateObject addWhenClause() {
		WhenClauseStateObject whenClause = new WhenClauseStateObject(this);
		addItem(whenClause);
		return whenClause;
	}

	/**
	 * Adds the given two {@link StateObject StateObjects} as the <code>WHEN</code> expression and
	 * the <code>THEN</code> expression of the new <code>WHEN</code> clause.
	 *
	 * @param whenStateObject The {@link StateObject} representing the <code><b>WHEN</b></code>
	 * expression
	 * @param thenStateObject The {@link StateObject} representing the <code><b>THEN</b></code>
	 * expression
	 * @return The newly created {@link WhenClauseStateObject}
	 */
	public WhenClauseStateObject addWhenClause(StateObject whenStateObject,
	                                           StateObject thenStateObject) {

		WhenClauseStateObject whenClause = new WhenClauseStateObject(
			this,
			whenStateObject,
			thenStateObject
		);

		addItem(whenClause);
		return whenClause;
	}

	/**
	 * Adds the given two {@link StateObject StateObjects} as the <code>WHEN</code> expression and
	 * the <code>THEN</code> expression of the new <code>WHEN</code> clause.
	 *
	 * @param whenJpqlFragment The string representation of the <code><b>WHEN</b></code> to parse and
	 * to convert into a {@link StateObject} representation
	 * @param thenJpqlFragment The string representation of the <code><b>THEN</b></code> to parse and
	 * to convert into a {@link StateObject} representation
	 * @return The newly created {@link WhenClauseStateObject}
	 */
	public WhenClauseStateObject addWhenClause(String whenJpqlFragment, String thenJpqlFragment) {

		WhenClauseStateObject whenClause = new WhenClauseStateObject(this);
		whenClause.parseWhen(whenJpqlFragment);
		whenClause.parseThen(thenJpqlFragment);
		addItem(whenClause);
		return whenClause;
	}

	/**
	 * Returns the {@link StateObject} representing the case operand.
	 *
	 * @return The {@link StateObject} representing the case operand or <code>null</code> if it is
	 * not present
	 */
	public StateObject getCaseOperand() {
		return caseOperandStateObject;
	}

	/**
	 * Returns the {@link StateObject} representing the <code><b>ELSE</b></code> scalar expression.
	 *
	 * @return The {@link StateObject} representing the <code><b>ELSE</b></code> scalar expression
	 * or <code>null</code> if it is not present
	 */
	public StateObject getElse() {
		return elseStateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CaseExpression getExpression() {
		return (CaseExpression) super.getExpression();
	}

	/**
	 * Determines whether the {@link StateObject} representing the case operand is present.
	 *
	 * @return <code>true</code> the case operand exists; otherwise <code>false</code>
	 */
	public boolean hasCaseOperand() {
		return caseOperandStateObject != null;
	}

	/**
	 * Determines whether the {@link StateObject} representing the <code><b>ELSE</b></code> scalar
	 * expression is present.
	 *
	 * @return <code>true</code> the <code><b>ELSE</b></code> scalar expression exists; otherwise
	 * <code>false</code>
	 */
	public boolean hasElse() {
		return elseStateObject != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			CaseExpressionStateObject caseExpression = (CaseExpressionStateObject) stateObject;
			return areEquivalent(elseStateObject,        caseExpression.elseStateObject)        &&
			       areEquivalent(caseOperandStateObject, caseExpression.caseOperandStateObject) &&
			       areChildrenEquivalent(caseExpression);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String listName() {
		return WHEN_CLAUSE_STATE_OBJECT_LIST;
	}

	/**
	 * Parses the given JPQL fragment, which will represent the case operand. The JPQL fragment should
	 * not start with the identifier.
	 *
	 * @param jpqlFragment The string representation of the case operand to parse and to convert into
	 * a {@link StateObject} representation
	 */
	public void parseCaseOperand(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, CaseOperandBNF.ID);
		setCaseOperand(stateObject);
	}

	/**
	 * Parses the given JPQL fragment, which will represent the <code><b>ELSE</b></code> expression.
	 * The JPQL fragment should not start with the identifier.
	 *
	 * @param jpqlFragment The string representation of the <code><b>ELSE</b></code> to parse and to
	 * convert into a {@link StateObject} representation
	 */
	public void parseElse(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, ElseExpressionBNF.ID);
		setElse(stateObject);
	}

	/**
	 * Removes the case operand.
	 */
	public void removeCaseOperand() {
		setCaseOperand(null);
	}

	/**
	 * Sets the case operand to be the given {@link StateObject}.
	 *
	 * @param caseOperand The {@link StateObject} representing the case operand or
	 * <code>null</code> to remove it
	 */
	public void setCaseOperand(StateObject caseOperand) {
		StateObject oldCaseOperandStateObject = this.caseOperandStateObject;
		this.caseOperandStateObject = parent(caseOperand);
		firePropertyChanged(CASE_OPERAND_STATE_OBJECT_PROPERTY, oldCaseOperandStateObject, caseOperand);
	}

	/**
	 * Sets the <code><b>ELSE</b></code> scalar expression to be the given {@link StateObject}.
	 *
	 * @param elseStateObject The {@link StateObject} representing the <code><b>ELSE</b></code>
	 * scalar expression or <code>null</code> to remove it
	 */
	public void setElse(StateObject elseStateObject) {
		StateObject oldElseStateObject = this.elseStateObject;
		this.elseStateObject = parent(elseStateObject);
		firePropertyChanged(ELSE_STATE_OBJECT_PROPERTY, oldElseStateObject, elseStateObject);
	}

	/**
	 * Keeps a reference of the {@link CaseExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link CaseExpression parsed object} representing a <code><b>CASE</b></code>
	 * expression
	 */
	public void setExpression(CaseExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void toTextInternal(Appendable writer) throws IOException {

		// CASE
		writer.append(CASE);
		writer.append(SPACE);

		// Case operand
		if (hasCaseOperand()) {
			caseOperandStateObject.toString(writer);
			writer.append(SPACE);
		}

		// WHEN clauses
		if (hasItems()) {
			toStringItems(writer, false);
			writer.append(SPACE);
		}

		// ELSE scalar expression
		writer.append(ELSE);
		writer.append(SPACE);

		if (hasElse()) {
			elseStateObject.toString(writer);
			writer.append(SPACE);
		}

		// END
		writer.append(END);
	}
}