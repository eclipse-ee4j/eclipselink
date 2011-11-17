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
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public abstract class AbstractRangeVariableDeclarationStateObject extends AbstractStateObject {

	/**
	 * Flag used to determine if the <code><b>AS</b></code> identifier is used or not.
	 */
	private boolean as;

	/**
	 * The state object representing the identification variable used to reach the "root" object.
	 */
	private IdentificationVariableStateObject identificationVariable;

	/**
	 * Flag used to determine if the identification variable is optional or not, which is <code>true</code>
	 * only when this is used by {@link UpdateClauseStateObject} or by {@link DeleteClauseStateObject}.
	 */
	private boolean identificationVariableOptional;

	/**
	 * The state object holding the "root" for objects which may not be reachable by navigation.
	 */
	private StateObject rootStateObject;

	/**
	 * Notifies the visibility of the <code><b>AS</b></code> identifier has changed.
	 */
	public static final String AS_PROPERTY = "as";

	/**
	 * Notifies the identification variable property has changed.
	 */
	public static final String IDENTIFICATION_VARIABLE_PROPERTY = "identificationVariable";

	/**
	 * Creates a new <code>RangeVariableDeclarationStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public AbstractRangeVariableDeclarationStateObject(AbstractIdentificationVariableDeclarationStateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>RangeVariableDeclarationStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public AbstractRangeVariableDeclarationStateObject(AbstractIdentificationVariableDeclarationStateObject parent,
	                                                   String root) {
		super(parent);
		setRootPath(root);
	}

	/**
	 * Creates a new <code>AbstractRangeVariableDeclarationStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public AbstractRangeVariableDeclarationStateObject(AbstractModifyClauseStateObject parent) {
		super(parent);
		setIdentificationVariableOptional(true);
	}

	/**
	 * Makes sure the <code><b>AS</b></code> identifier is specified.
	 *
	 * @return This object
	 */
	public AbstractRangeVariableDeclarationStateObject addAs() {
		if (!as) {
			setAs(true);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildren(List<StateObject> children) {
		super.addChildren(children);
		children.add(rootStateObject);
		children.add(identificationVariable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addProblems(List<Problem> problems) {
		super.addProblems(problems);
		// TODO

		if (!identificationVariableOptional) {
			// validate
		}
	}

	protected abstract StateObject buildRootStateObject();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RangeVariableDeclaration getExpression() {
		return (RangeVariableDeclaration) super.getExpression();
	}

	/**
	 * Returns the identification variable name that is ranging over the abstract schema type.
	 *
	 * @return The identification variable name
	 */
	public String getIdentificationVariable() {
		return identificationVariable.getText();
	}

	/**
	 * Returns the {@link IdentificationVariableStateObject} holding onto the identification variable.
	 *
	 * @return The {@link IdentificationVariableStateObject}, which is never <code>null</code>
	 */
	public IdentificationVariableStateObject getIdentificationVariableStateObject() {
		return identificationVariable;
	}

	/**
	 * Returns the "root" object for objects which may not be reachable by navigation.
	 *
	 * @return The "root" object
	 */
	public abstract String getRootPath();

	/**
	 * Returns the {@link StateObject} holding onto the abstract schema name.
	 *
	 * @return The {@link StateObject}, which is never <code>null</code>
	 */
	public StateObject getRootStateObject() {
		return rootStateObject;
	}

	/**
	 * Determines whether the <code><b>AS</b></code> identifier is used or not.
	 *
	 * @return <code>true</code> if the <code><b>AS</b></code> identifier is part of the expression;
	 * <code>false</code> otherwise
	 */
	public boolean hasAs() {
		return as;
	}

	/**
	 * Determines whether an identification variable was defined.
	 *
	 * @return <code>true</code> if an identification variable is defined; <code>false</code> otherwise
	 */
	public boolean hasIdentificationVariable() {
		return identificationVariable.hasText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		rootStateObject        = buildRootStateObject();
		identificationVariable = new IdentificationVariableStateObject(this);
	}

	/**
	 * Determines whether the identification variable is optional or not. The only time it is
	 * optional is when this model is used in a modify clause (<code><b>DELETE</b></code> or
	 * <code><b>UPDATE</b></code>).
	 *
	 * @return <code>true</code> if an identification variable is not required; <code>false</code> if
	 * it is required
	 */
	public boolean isIdentificationVariableOptional() {
		return identificationVariableOptional;
	}

	/**
	 * Determines whether this identification variable is virtual, meaning it's not part of the query
	 * but is required for proper navigability.
	 *
	 * @return <code>true</code> if this identification variable was virtually created to fully
	 * qualify path expression; <code>false</code> if it was parsed
	 */
	public boolean isIdentificationVariableVirtual() {
		return identificationVariable.isVirtual();
	}

	/**
	 * Sets whether the <code><b>AS</b></code> identifier is used or not.
	 *
	 * @param as <code>true</code> if the <code><b>AS</b></code> identifier is part of the
	 * expression; <code>false</code> otherwise
	 */
	public void setAs(boolean as) {
		boolean oldAs = this.as;
		this.as = as;
		firePropertyChanged(AS_PROPERTY, oldAs, as);
	}

	/**
	 * Keeps a reference of the {@link RangeVariableDeclaration parsed object} object, which should
	 * only be* done when this object is instantiated during the conversion of a parsed JPQL query
	 * into {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link RangeVariableDeclaration parsed object} representing a range
	 * variable declaration
	 */
	public void setExpression(RangeVariableDeclaration expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets the new identification variable that will range over the "root" object.
	 *
	 * @param identificationVariable The new identification variable
	 */
	public void setIdentificationVariable(String identificationVariable) {
		String oldIdentificationVariable = getIdentificationVariable();
		this.identificationVariable.setText(identificationVariable);
		firePropertyChanged(IDENTIFICATION_VARIABLE_PROPERTY, oldIdentificationVariable, identificationVariable);
	}

	/**
	 * Sets whether the identification variable is optional or not. The only time it is optional is
	 * when this model is used in a modify clause (<code><b>DELETE</b></code> or
	 * <code><b>UPDATE</b></code>).
	 *
	 * @param identificationVariableOptional <code>true</code> if an identification variable is not
	 * required; <code>false</code> if it is required
	 */
	protected void setIdentificationVariableOptional(boolean identificationVariableOptional) {
		this.identificationVariableOptional = identificationVariableOptional;
	}

	/**
	 * Sets the "root" object for objects which may not be reachable by navigation.
	 *
	 * @param root The "root" object
	 */
	public abstract void setRootPath(String root);

	/**
	 * Toggles the usage of the <code><b>AS</b></code> identifier.
	 */
	public void toggleAs() {
		setAs(!hasAs());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {

		rootStateObject.toString(writer);

		if (as) {
			writer.append(SPACE);
			writer.append(AS);

			if (!identificationVariable.isVirtual()) {
				writer.append(SPACE);
			}
		}

		if (!identificationVariable.isVirtual()) {

			if (rootStateObject != null && !as) {
				writer.append(SPACE);
			}

			identificationVariable.toString(writer);
		}
	}
}