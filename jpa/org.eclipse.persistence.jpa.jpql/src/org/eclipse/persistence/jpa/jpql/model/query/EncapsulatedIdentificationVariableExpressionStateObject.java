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
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;

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
	 * The cached identification variable that is used to make sure it's the same as the one stored
	 * in the {@link IdentificationVariableStateObject} itself.
	 */
	private String identificationVariable;

	/**
	 * The {@link IManagedType} mapped to the identification variable if the identification variable
	 * maps to an entity name.
	 */
	private IManagedType managedType;

	/**
	 * The cached {@link IType} of the value to resolve.
	 */
	private IType type;

	/**
	 * The cached {@link ITypeDeclaration} of the value to resolve.
	 */
	private ITypeDeclaration typeDeclaration;

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
	 * Makes sure the cached identification variable and the one owned by {@link
	 * IdentificationVariableStateObject} are the same. If they are not, then clears the cached
	 * values related to the type.
	 */
	protected void checkIntegrity() {
		if (ExpressionTools.stringsAreDifferentIgnoreCase(identificationVariable, getStateObject().getText())) {
			clearResolvedObjects();
			identificationVariable = getStateObject().getText();
		}
	}

	/**
	 * Clears the values related to the managed type and type.
	 */
	protected void clearResolvedObjects() {
		type            = null;
		managedType     = null;
		typeDeclaration = null;
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
		identificationVariable = getStateObject().getText();
		return identificationVariable;
	}

	/**
	 * Returns the {@link IManagedType} associated with the field handled by this object. If this
	 * object does not handle a field that has a {@link IManagedType}, then <code>null</code> should
	 * be returned.
	 * <p>
	 * For example: "<code><b>SELECT</b> e <b>FROM</b> Employee e</code>", the object for <i>e</i>
	 * would be returning the {@link IManagedType} for <i>Employee</i>.
	 *
	 * @return Either the {@link IManagedType}, if it could be resolved; <code>null</code> otherwise
	 */
	public IManagedType getManagedType() {

		if (managedType != null) {
			checkIntegrity();
		}

		if (managedType == null) {
			managedType = resolveManagedType();
		}

		return managedType;
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
	 * Returns the {@link IType} of the field handled by this object.
	 *
	 * @return Either the {@link IType} of the identification variable or the {@link IType} for
	 * {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	public IType getType() {

		if (type != null) {
			checkIntegrity();
		}

		if (type == null) {
			type = resolveType();
		}

		return type;
	}

	/**
	 * Returns the {@link ITypeDeclaration} of the field handled by this object.
	 *
	 * @return Either the {@link ITypeDeclaration} that was resolved by this object or the {@link
	 * ITypeDeclaration} for {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	public ITypeDeclaration getTypeDeclaration() {

		if (typeDeclaration != null) {
			checkIntegrity();
		}

		if (typeDeclaration == null) {
			typeDeclaration = resolveTypeDeclaration();
		}

		return typeDeclaration;
	}

	/**
	 * Determines whether the identification variable has been defined or not.
	 *
	 * @return <code>true</code> if the identification variable has been defined;
	 * <code>false</code> otherwise
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
	 * Retrieves the {@link IManagedType} that is mapped to the identification variable, if and only
	 * if the identification variable is used to declare an entity.
	 *
	 * @return Either the {@link IManagedType} declared by the identification variable or <code>null</code>
	 * if it could not be resolved
	 */
	protected IManagedType resolveManagedType() {
		return getManagedTypeProvider().getManagedType(getType());
	}

	/**
	 * Resolves the {@link IType} of the property handled by this object.
	 *
	 * @return Either the {@link IType} that was resolved by this object or the {@link IType} for
	 * {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	protected IType resolveType() {
		return getTypeDeclaration().getType();
	}

	/**
	 * Resolves the {@link ITypeDeclaration} of the property handled by this object.
	 *
	 * @return Either the {@link ITypeDeclaration} that was resolved by this object or the {@link
	 * ITypeDeclaration} for {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	protected ITypeDeclaration resolveTypeDeclaration() {
		return getStateObject().getTypeDeclaration();
	}

	/**
	 * Sets the identification variable of this encapsulated expression.
	 *
	 * @param identificationVariable The name of the identification variable
	 */
	public void setIdentificationVariable(String identificationVariable) {

		clearResolvedObjects();

		String oldIdentificationVariable = this.identificationVariable;
		this.identificationVariable = identificationVariable;
		getStateObject().setText(identificationVariable);
		firePropertyChanged(IDENTIFICATION_VARIABLE_PROPERTY, oldIdentificationVariable, identificationVariable);
	}
}