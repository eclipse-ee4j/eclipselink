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

import org.eclipse.persistence.jpa.jpql.parser.InputParameter;

/**
 * Either positional or named parameters may be used. Positional and named parameters may not be
 * mixed in a single query. Input parameters can only be used in the <code><b>WHERE</b></code>
 * clause or <code><b>HAVING</b></code> clause of a query.
 *
 * @see InputParameter
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class InputParameterStateObject extends SimpleStateObject {

	/**
	 * Creates a new <code>InputParameterStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public InputParameterStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>InputParameterStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param parameter The positional or named input parameter
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public InputParameterStateObject(StateObject parent, String parameter) {
		super(parent, parameter);
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
	public InputParameter getExpression() {
		return (InputParameter) super.getExpression();
	}

	/**
	 * Determines whether this parameter is a positional parameter, i.e. the parameter type is '?'.
	 *
	 * @return <code>true</code> if the parameter type is '?'; <code>false</code> if it's ':'
	 */
	public boolean isNamed() {
		if (hasText()) {
			return getText().charAt(0) == InputParameter.NAMED_PARAMETER.charAt(0);
		}
		return false;
	}

	/**
	 * Determines whether this parameter is a positional parameter, i.e. the parameter type is ':'.
	 *
	 * @return <code>true</code> if the parameter type is ':'; <code>false</code> if it's '?'
	 */
	public boolean isPositional() {
		if (hasText()) {
			return getText().charAt(0) == InputParameter.POSITIONAL_PARAMETER.charAt(0);
		}
		return false;
	}

	/**
	 * Keeps a reference of the {@link InputParameter parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link InputParameter parsed object} representing an input parameter
	 */
	public void setExpression(InputParameter expression) {
		super.setExpression(expression);
	}
}