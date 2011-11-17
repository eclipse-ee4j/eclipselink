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

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.parser.EncapsulatedIdentificationVariableExpression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableBNF;

/**
 * This object represents an identification variable that is wrapped by a function.
 * <p>
 * <div nowrap><b>BNF:</b> <code>&lt;identifier&gt;(identification_variable)</code><p>
 *
 * @see EncapsulatedIdentificationVariableExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class EncapsulatedIdentificationVariableExpressionStateObject extends AbstractSingleEncapsulatedExpressionStateObject {

	/**
	 * Notifies the identification variable property has changed.
	 */
	public static final String IDENTIFICATION_VARIABLE_PROPERTY = "identificationVariable";

	/**
	 * Creates a new <code>EncapsulatedIdentificationVariableExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected EncapsulatedIdentificationVariableExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>EncapsulatedIdentificationVariableExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param identificationVariable The identification variable
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected EncapsulatedIdentificationVariableExpressionStateObject(StateObject parent,
	                                                                  String identificationVariable) {

		super(parent);
		setIdentificationVariable(identificationVariable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EncapsulatedIdentificationVariableExpression getExpression() {
		return (EncapsulatedIdentificationVariableExpression) super.getExpression();
	}

	/**
	 * Returns the identification variable.
	 *
	 * @return The name of the identification variable
	 */
	public String getIdentificationVariable() {
		return getStateObject().getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getQueryBNFId() {
		return IdentificationVariableBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IdentificationVariableStateObject getStateObject() {
		return (IdentificationVariableStateObject) super.getStateObject();
	}

	/**
	 * Determines whether the identification variable has been defined.
	 *
	 * @return <code>true</code> if the identification variable has been defined; <code>false</code>
	 * otherwise
	 */
	public boolean hasIdentificationVariable() {
		return ExpressionTools.stringIsNotEmpty(getIdentificationVariable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		setStateObject(new IdentificationVariableStateObject(this));
	}

	/**
	 * Sets the identification variable for which the <code><b>INDEX</b></code> function returns an
	 * integer value corresponding to the position of its argument in an ordered list.
	 *
	 * @param identificationVariable the name of the identification variable
	 */
	public void setIdentificationVariable(String identificationVariable) {
		String oldIdentificationVariable = getIdentificationVariable();
		getStateObject().setText(identificationVariable);
		firePropertyChanged(IDENTIFICATION_VARIABLE_PROPERTY, oldIdentificationVariable, identificationVariable);
	}
}