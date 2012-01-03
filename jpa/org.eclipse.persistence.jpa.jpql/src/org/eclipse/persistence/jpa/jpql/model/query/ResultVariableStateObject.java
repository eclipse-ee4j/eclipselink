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
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.SelectClauseInternalBNF;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * A result variable may be used to name a select item in the query result.
 * <p>
 * <div nowrap><b>BNF:</b> <code>select_item ::= select_expression [[AS] result_variable]</code><p>
 *
 * @see ResultVariable
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class ResultVariableStateObject extends AbstractStateObject {

	/**
	 * Flag used to determine if the <code><b>AS</b></code> identifier is used or not.
	 */
	private boolean as;

	/**
	 * The result variable identifies the select expression, which can be used in the
	 * <code><b>ORDER BY</b></code> clause.
	 */
	private IdentificationVariableStateObject resultVariable;

	/**
	 * The state object representing the select expression that is named with a result variable.
	 */
	private StateObject stateObject;

	/**
	 * Notifies the visibility of the <code><b>AS</b></code> identifier has changed.
	 */
	public static final String AS_PROPERTY = "as";

	/**
	 * Notifies the result variable property has changed.
	 */
	public static final String RESULT_VARIABLE_PROPERTY = "resultVariable";

	/**
	 * Notifies the select state object property has changed.
	 */
	public static final String STATE_OBJECT_PROPERTY = "selectStateObject";

	/**
	 * Creates a new <code>ResultVariableStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public ResultVariableStateObject(SelectClauseStateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>ResultVariableStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param selectStateObject The {@link StateObject} representing the
	 * @param as Determines whether the <code><b>AS</b></code> identifier is used or not
	 * @param resultVariable
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public ResultVariableStateObject(SelectClauseStateObject parent,
	                                 StateObject stateObject,
	                                 boolean as,
	                                 String resultVariable) {

		super(parent);
		this.as             = as;
		this.stateObject    = parent(stateObject);
		this.resultVariable.setTextInternally(resultVariable);
	}

	/**
	 * Creates a new <code>ResultVariableStateObject</code>.
	 *
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 * @param selectStateObject The {@link StateObject} representing the
	 * @param resultVariable
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 */
	public ResultVariableStateObject(SelectClauseStateObject parent,
	                                 StateObject stateObject,
	                                 String resultVariable) {

		this(parent, stateObject, false, resultVariable);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Makes sure the <code><b>AS</b></code> identifier is used.
	 *
	 * @return This object
	 */
	public ResultVariableStateObject addAs() {
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
		if (stateObject != null) {
			children.add(stateObject);
		}
		children.add(resultVariable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResultVariable getExpression() {
		return (ResultVariable) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SelectClauseStateObject getParent() {
		return (SelectClauseStateObject) super.getParent();
	}

	/**
	 * Returns the result variable identifies the select expression, which can be used in the
	 * <code><b>ORDER BY</b></code> clause.
	 *
	 * @return The unique identifier declaring the select expression
	 */
	public String getResultVariable() {
		return resultVariable.getText();
	}

	/**
	 * Returns the {@link StateObject} representing a single select expression.
	 *
	 * @return The {@link StateObject} representing a single select expression
	 */
	public StateObject getStateObject() {
		return stateObject;
	}

	/**
	 * Determines whether the <code><b>AS</b></code> identifier is used.
	 *
	 * @return <code>true</code> if the <code><b>AS</b></code> identifier is used; <code>false</code>
	 * otherwise
	 */
	public boolean hasAs() {
		return as;
	}

	/**
	 * Determines whether the result variable has been defined.
	 *
	 * @return <code>true</code> if the result variable is defined; <code>false</code> otherwise
	 */
	public boolean hasResultVariable() {
		return resultVariable.hasText();
	}

	/**
	 * Determines whether the select item has been defined.
	 *
	 * @return <code>true</code> if there the selected expression has been defined; <code>false</code>
	 * if it's missing
	 */
	public boolean hasStateObject() {
		return stateObject != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		resultVariable = new IdentificationVariableStateObject(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			ResultVariableStateObject resultVariable = (ResultVariableStateObject) stateObject;
			return as == resultVariable.as &&
			       resultVariable.isEquivalent(resultVariable.resultVariable) &&
			       areEquivalent(stateObject, resultVariable.stateObject);
		}

		return false;
	}

	/**
	 * Parses the given JPQL fragment, which represents a single select expression, and creates the
	 * {@link StateObject}.
	 *
	 * @param jpqlFragment The portion of the query representing a single select expression
	 */
	public void parse(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, SelectClauseInternalBNF.ID);
		setStateObject(stateObject);
	}

	/**
	 * Makes sure the <code><b>AS</b></code> identifier is not used.
	 */
	public void removeAs() {
		if (as) {
			setAs(true);
		}
	}

	/**
	 * Sets whether the <code><b>AS</b></code> identifier should be used.
	 *
	 * @param as <code>true</code> if the <code><b>AS</b></code> identifier should be used part;
	 * <code>false</code> otherwise
	 */
	public void setAs(boolean as) {
		boolean oldAs = this.as;
		this.as = as;
		firePropertyChanged(AS_PROPERTY, oldAs, as);
	}

	/**
	 * Keeps a reference of the {@link ResultVariable parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link ResultVariable parsed object} representing a result variable
	 * expression
	 */
	public void setExpression(ResultVariable expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets the result variable that identifies the select expression, which can be used in the
	 * <code><b>ORDER BY</b></code> clause.
	 *
	 * @param resultVariable The unique identifier declaring the select expression
	 */
	public void setResultVariable(String resultVariable) {
		String oldResultVariable = getResultVariable();
		this.resultVariable.setText(resultVariable);
		firePropertyChanged(RESULT_VARIABLE_PROPERTY, oldResultVariable, resultVariable);
	}

	/**
	 * Sets the {@link StateObject} representing a single select expression.
	 *
	 * @param selectStateObject The {@link StateObject} representing a single select expression
	 */
	public void setStateObject(StateObject stateObject) {
		StateObject oldStateObject = this.stateObject;
		this.stateObject = parent(stateObject);
		firePropertyChanged(STATE_OBJECT_PROPERTY, oldStateObject, stateObject);
	}

	/**
	 * Toggles the visibility of the <code><b>AS</b></code> identifier; either adds it if it's not
	 * present otherwise removes it if it's present.
	 */
	public void toggleAs() {
		setAs(!as);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {

		if (stateObject != null) {
			stateObject.toString(writer);
			writer.append(SPACE);
		}

		if (as) {
			writer.append(AS);
		}

		if (hasResultVariable()) {
			writer.append(SPACE);
			resultVariable.toTextInternal(writer);
		}
	}
}