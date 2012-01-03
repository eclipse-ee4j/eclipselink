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
import org.eclipse.persistence.jpa.jpql.parser.ConditionalPrimaryBNF;
import org.eclipse.persistence.jpa.jpql.parser.NotExpression;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * <div nowrap><b>BNF:</b> <code>expression ::= NOT conditional_primary</code><p>
 *
 * @see NotExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class NotExpressionStateObject extends AbstractStateObject {

	/**
	 * The {@link StateObject} representing the negated expression.
	 */
	private StateObject stateObject;

	/**
	 * Notifies the state object property has changed.
	 */
	public static final String STATE_OBJECT_PROPERTY = "stateObject";

	/**
	 * Creates a new <code>NotExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public NotExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>NotExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateObject The {@link StateObject} representing the negated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public NotExpressionStateObject(StateObject parent, StateObject stateObject) {
		super(parent);
		this.stateObject = parent(stateObject);
	}

	/**
	 * Creates a new <code>NotExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param jpqlFragment The portion of the JPQL query that follows <code><b>NOT</b></code>, which
	 * will be parsed and converted into a {@link StateObject}
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public NotExpressionStateObject(StateObject parent, String jpqlFragment) {
		super(parent);
		parse(jpqlFragment);
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
		super.addChildren(children);
		if (stateObject != null) {
			children.add(stateObject);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NotExpression getExpression() {
		return (NotExpression) super.getExpression();
	}

	/**
	 * Returns the {@link StateObject} representing the negated expression.
	 *
	 * @return The {@link StateObject} representing the negated expression
	 */
	public StateObject getStateObject() {
		return stateObject;
	}

	/**
	 * Determines whether the {@link StateObject} representing the encapsulated expression is
	 * present or not.
	 *
	 * @return <code>true</code> if the encapsulated {@link StateObject} is not <code>null</code>;
	 * <code>false</code> otherwise
	 */
	public boolean hasStateObject() {
		return stateObject != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			NotExpressionStateObject not = (NotExpressionStateObject) stateObject;
			return areEquivalent(stateObject, not.stateObject);
		}

		return false;
	}

	/**
	 * Parses the given JPQL fragment, which represents the negated expression, and creates the
	 * {@link StateObject}.
	 *
	 * @param jpqlFragment The portion of the query representing the negated expression
	 */
	public void parse(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, ConditionalPrimaryBNF.ID);
		setStateObject(stateObject);
	}

	/**
	 * Keeps a reference of the {@link NotExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link NotExpression parsed object} representing a <code><b>NOT</b></code>
	 * expression
	 */
	public void setExpression(NotExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets the given {@link StateObject} as the new negated expression.
	 *
	 * @param stateObject The {@link StateObject} representing the negated expression
	 */
	public void setStateObject(StateObject stateObject) {
		StateObject oldStateObject = this.stateObject;
		this.stateObject = parent(stateObject);
		firePropertyChanged(STATE_OBJECT_PROPERTY, oldStateObject, stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {
		writer.append(NOT);
		if (stateObject != null) {
			writer.append(SPACE);
			stateObject.toString(writer);
		}
	}
}