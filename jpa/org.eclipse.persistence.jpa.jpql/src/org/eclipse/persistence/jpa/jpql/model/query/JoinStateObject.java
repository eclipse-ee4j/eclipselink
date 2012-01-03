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
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.parser.Join;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * A <code><b>JOIN</b></code> enables the fetching of an association as a side effect of the
 * execution of a query. A <code><b>JOIN</b></code> is specified over an entity and its related
 * entities.
 * <p>
 * <div nowrap><b>BNF:</b> <code>join ::= join_spec join_association_path_expression [AS] identification_variable</code><p>
 *
 * @see Join
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class JoinStateObject extends AbstractJoinStateObject {

	/**
	 * Flag used to determine if the <code><b>AS</b></code> identifier is used or not.
	 */
	private boolean as;

	/**
	 * The state object holding the identification variable.
	 */
	private IdentificationVariableStateObject identificationVariable;

	/**
	 * Notifies the visibility of the <code><b>AS</b></code> identifier has changed.
	 */
	public static final String AS_PROPERTY = "as";

	/**
	 * Notifies the <code><b>JOIN</b></code> property has changed.
	 */
	public static final String JOIN_TYPE_PROPERTY = "joinType";

	/**
	 * Creates a new <code>JoinStateObject</code>.
	 *
	 * @param parent The parent of this state object
	 * @param joinType One of the joining types: <code><b>LEFT JOIN</b></code>, <code><b>LEFT OUTER
	 * JOIN</b></code>, <code><b>INNER JOIN</b></code> or <code><b>JOIN</b></code>
	 */
	public JoinStateObject(AbstractIdentificationVariableDeclarationStateObject parent,
	                       String joinType) {

		this(parent, joinType, false);
	}

	/**
	 * Creates a new <code>JoinStateObject</code>.
	 *
	 * @param parent The parent of this state object
	 * @param joinType One of the joining types: <code><b>LEFT JOIN</b></code>, <code><b>LEFT OUTER
	 * JOIN</b></code>, <code><b>INNER JOIN</b></code> or <code><b>JOIN</b></code>
	 * @param as Determine whether the <code><b>AS</b></code> identifier is used or not
	 */
	public JoinStateObject(AbstractIdentificationVariableDeclarationStateObject parent,
	                       String joinType,
	                       boolean as) {

		super(parent, joinType);
		this.as = as;
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Makes sure the <code><b>AS</b></code> identifier is specified.
	 *
	 * @return This object
	 */
	public JoinStateObject addAs() {
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
		children.add(identificationVariable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Join getExpression() {
		return (Join) super.getExpression();
	}

	/**
	 * Returns the name of the identification variable that defines the join association path.
	 *
	 * @return The variable defining the join association path
	 */
	public String getIdentificationVariable() {
		return identificationVariable.getText();
	}

	/**
	 * Returns the state object holding the identification variable.
	 *
	 * @return The portion of the joining expression representing the identification variable
	 */
	public IdentificationVariableStateObject getIdentificationVariableStateObject() {
		return identificationVariable;
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
	 * Determines whether the identification variable has been defined.
	 *
	 * @return <code>true</code> if the identification has been defined; <code>false</code> otherwise
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
		identificationVariable = new IdentificationVariableStateObject(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			JoinStateObject join = (JoinStateObject) stateObject;
			return (as = join.as) && identificationVariable.isEquivalent(join.identificationVariable);
		}

		return false;
	}

	/**
	 * Makes sure the <code><b>AS</b></code> identifier is not specified.
	 */
	public void removeNot() {
		if (as) {
			setAs(false);
		}
	}

	/**
	 * Sets whether the <code><b>AS</b></code> identifier is used or not.
	 *
	 * @param hasAs <code>true</code> if the <code><b>AS</b></code> identifier is part of the
	 * expression; <code>false</code> otherwise
	 */
	public void setAs(boolean as) {
		boolean oldAs = this.as;
		this.as = as;
		firePropertyChanged(AS_PROPERTY, oldAs, as);
	}

	/**
	 * Keeps a reference of the {@link Join parsed object} object, which should only be done when
	 * this object is instantiated during the conversion of a parsed JPQL query into {@link
	 * StateObject StateObjects}.
	 *
	 * @param expression The {@link Join parsed object} representing a <code><b>JOIN</b></code>
	 * expression
	 */
	public void setExpression(Join expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets the name of the identification variable that defines the join association path.
	 *
	 * @param identificationVariable The new variable defining the join association path
	 */
	public void setIdentificationVariable(String identificationVariable) {
		this.identificationVariable.setText(identificationVariable);
	}

	/**
	 * Toggles the usage of the <code><b>AS</b></code> identifier.
	 */
	public void toggleAs() {
		setAs(!as);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {
		super.toTextInternal(writer);
		if (as) {
			writer.append(SPACE);
			writer.append(AS);
		}
		writer.append(SPACE);
		identificationVariable.toString(writer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateJoinType(String joinType) {
		Assert.isValid(joinType, "The join type is not valid", JOIN, LEFT_JOIN, LEFT_OUTER_JOIN, INNER_JOIN);
	}
}