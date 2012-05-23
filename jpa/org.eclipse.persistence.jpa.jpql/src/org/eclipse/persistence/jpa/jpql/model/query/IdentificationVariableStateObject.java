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
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.model.Problem;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;

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
	 * The {@link IManagedType} mapped to the identification variable if the identification variable
	 * maps to an entity name.
	 */
	private IManagedType managedType;

	/**
	 *
	 */
	private IMapping mapping;

	/**
	 * The {@link IType} of the object being mapped to this identification variable.
	 */
	private IType type;

	/**
	 * The {@link ITypeDeclaration} of the object being mapped to this identification variable.
	 */
	private ITypeDeclaration typeDeclaration;

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
	 * Makes sure the current identification variable and the given one are the same. If they are
	 * not, then clears the cached values related to the type.
	 *
	 * @param text The new identification variable
	 */
	protected void checkIntegrity(String text) {
		if (ExpressionTools.valuesAreDifferent(getText(), text)) {
			clearResolvedObjects();
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
	public IdentificationVariable getExpression() {
		return (IdentificationVariable) super.getExpression();
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
		if ((managedType == null) && (mapping == null)) {
			managedType = resolveManagedType();
		}
		return managedType;
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	public IMapping getMapping() {
		if ((managedType == null) && (mapping == null)) {
			mapping = resolveMapping();
		}
		return mapping;
	}

	/**
	 * Returns the {@link IType} of the field handled by this object.
	 *
	 * @return Either the {@link IType} that was resolved by this state object or the {@link IType}
	 * for {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	public IType getType() {
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
		if (typeDeclaration == null) {
			typeDeclaration = resolveTypeDeclaration();
		}
		return typeDeclaration;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			IdentificationVariableStateObject variable = (IdentificationVariableStateObject) stateObject;
			return ExpressionTools.stringsAreEqualIgnoreCase(getText(), variable.getText());
		}

		return false;
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
	 * Resolves
	 *
	 * @return
	 */
	protected IMapping resolveMapping() {
		return null;
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
		return null;
	}

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
	 * {@inheritDoc}
	 */
	@Override
	public void setText(String text) {
		checkIntegrity(text);
		super.setText(text);
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