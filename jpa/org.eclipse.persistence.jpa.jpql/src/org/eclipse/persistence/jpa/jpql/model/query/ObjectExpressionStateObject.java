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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.model.Problem;
import org.eclipse.persistence.jpa.jpql.parser.ObjectExpression;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * Stand-alone identification variables in the <code><b>SELECT</b></code> clause may optionally be
 * qualified by the <code><b>OBJECT</b></code> operator. The <code><b>SELECT</b></code> clause must
 * not use the <code><b>OBJECT</b></code> operator to qualify path expressions.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= OBJECT(identification_variable)</code><p>
 *
 * @see ObjectExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class ObjectExpressionStateObject extends EncapsulatedIdentificationVariableExpressionStateObject {

	/**
	 * Creates a new <code>ObjectExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public ObjectExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>ObjectExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param identificationVariable The name of the identification variable
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public ObjectExpressionStateObject(StateObject parent, String identificationVariable) {
		super(parent, identificationVariable);
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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObjectExpression getExpression() {
		return (ObjectExpression) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return OBJECT;
	}

	/**
	 * Keeps a reference of the {@link ObjectExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link ObjectExpression parsed object} representing an <code><b>OBJECT</b></code>
	 * expression
	 */
	public void setExpression(ObjectExpression expression) {
		super.setExpression(expression);
	}
}