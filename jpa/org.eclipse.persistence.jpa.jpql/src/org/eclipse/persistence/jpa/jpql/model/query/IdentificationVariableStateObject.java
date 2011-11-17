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
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;

/**
 * This state object represents a single identification variable, which is identifying TODO.
 *
 * @see IdentificationVariable
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class IdentificationVariableStateObject extends SimpleStateObject {

	/**
	 * Determines whether this identification variable is virtual, meaning it's not part of the query
	 * but is required for proper navigability.
	 */
	private boolean virtual;

	/**
	 * Notify a change in the defined property.
	 */
	public static final String DEFINED_PROPERTY = "defined";

	/**
	 * Creates a new <code>IdentificationVariableStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public IdentificationVariableStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>IdentificationVariableStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param variable The name of the identification variable
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public IdentificationVariableStateObject(StateObject parent, String variable) {
		super(parent, variable);
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
	protected void addProblems(List<Problem> problems) {
		super.addProblems(problems);

//		String text = getText();

		// An empty identification variable is never allowed
//		if (ExpressionTools.stringIsEmpty(text)) {
//			problems.add(buildProblem(StateObjectProblemConstants.IDENTIFICATION_VARIABLE_STATE_OBJECT_NO_TEXT));
//		}
//		// A JPQL identifier is never allowed
//		else if (false) { //.isIdentifier(text)) {
//			problems.add(buildProblem(StateObjectProblemConstants.IDENTIFICATION_VARIABLE_STATE_OBJECT_NOT_DEFINED));
//		}
//		// The identification variable contains some invalid characters
//		// TODO
//
//		// The variable is not defined in the FROM clause
//		else if (!defined) {
//			problems.add(buildProblem(StateObjectProblemConstants.IDENTIFICATION_VARIABLE_STATE_OBJECT_NOT_DEFINED));
//		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IdentificationVariable getExpression() {
		return (IdentificationVariable) super.getExpression();
	}

	/**
	 * Determines whether this identification variable is virtual, meaning it's not part of the query
	 * but is required for proper navigability.
	 *
	 * @return <code>true</code> if this identification variable was virtually created to fully
	 * qualify path expression; <code>false</code> if it was parsed
	 */
	public boolean isVirtual() {
		return virtual;
	}

//	public IType getType() {
//		return null;// getSelectStatement().getFromClause().getType(this);
//	}

	/**
	 * Keeps a reference of the {@link IdentificationVariable parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link IdentificationVariable parsed object} representing an
	 * identification variable
	 */
	public void setExpression(IdentificationVariable expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets whether this identification variable is virtual, meaning it's not part of the query but
	 * is required for proper navigability.
	 *
	 * @param virtual <code>true</code> if this identification variable was virtually created to
	 * fully qualify path expression; <code>false</code> if it was parsed
	 */
	public void setVirtual(boolean virtual) {
		this.virtual = virtual;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {
		if (!virtual) {
			super.toTextInternal(writer);
		}
	}
}