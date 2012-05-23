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
import org.eclipse.persistence.jpa.jpql.parser.ExistsExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubqueryBNF;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * An <code><b>EXISTS</b></code> expression is a predicate that is <code>true</code> only if the
 * result of the subquery consists of one or more values and that is <code>false</code> otherwise.
 *
 * <div nowrap><b>BNF:</b> <code>exists_expression ::= [NOT] EXISTS(subquery)</code><p>
 *
 * @see ExistsExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class ExistsExpressionStateObject extends AbstractSingleEncapsulatedExpressionStateObject {

	/**
	 * Determines whether the <code><b>NOT</b></code> identifier is part of the expression or not.
	 */
	private boolean not;

	/**
	 * Notifies the visibility of the <code><b>NOT</b></code> identifier has changed.
	 */
	public static String NOT_PROPERTY = "not";

	/**
	 * Creates a new <code>ExistsExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public ExistsExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>ExistsExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param not Determines whether the <code><b>NOT</b></code> identifier is part of the expression
	 * or not
	 * @param stateObject The {@link StateObject} representing the subquery
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public ExistsExpressionStateObject(StateObject parent, boolean not, StateObject stateObject) {
		super(parent, stateObject);
		this.not = not;
	}

	/**
	 * Creates a new <code>ExistsExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param not Determines whether the <code><b>NOT</b></code> identifier is part of the expression
	 * or not
	 * @param jpqlFragment The portion of the query representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public ExistsExpressionStateObject(StateObject parent, boolean not, String jpqlFragment) {
		super(parent, jpqlFragment);
		this.not = not;
	}

	/**
	 * Creates a new <code>ExistsExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateObject The {@link StateObject} representing the subquery
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public ExistsExpressionStateObject(StateObject parent, StateObject stateObject) {
		this(parent, false, stateObject);
	}

	/**
	 * Creates a new <code>ExistsExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param jpqlFragment The portion of the query representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public ExistsExpressionStateObject(StateObject parent, String jpqlFragment) {
		super(parent, jpqlFragment);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Makes sure the <code><b>NOT</b></code> identifier is specified.
	 *
	 * @return This object
	 */
	public ExistsExpressionStateObject addNot() {
		if (!not) {
			setNot(true);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExistsExpression getExpression() {
		return (ExistsExpression) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return EXISTS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getQueryBNFId() {
		return SubqueryBNF.ID;
	}

	/**
	 * Determines whether the <code><b>NOT</b></code> identifier is used or not.
	 *
	 * @return <code>true</code> if the <code><b>NOT</b></code> identifier is part of the expression;
	 * <code>false</code> otherwise
	 */
	public boolean hasNot() {
		return not;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			ExistsExpressionStateObject exists = (ExistsExpressionStateObject) stateObject;
			return not == exists.not;
		}

		return false;
	}

	/**
	 * Makes sure the <code><b>NOT</b></code> identifier is not specified.
	 */
	public void removeNot() {
		if (not) {
			setNot(false);
		}
	}

	/**
	 * Keeps a reference of the {@link ExistsExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link ExistsExpression parsed object} representing an <code><b>EXISTS</b></code>
	 * expression
	 */
	public void setExpression(ExistsExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets whether the <code><b>NOT</b></code> identifier should be part of the expression or not.
	 *
	 * @param not <code>true</code> if the <code><b>NOT</b></code> identifier should be part of the
	 * expression; <code>false</code> otherwise
	 */
	public void setNot(boolean not) {
		boolean oldNot = this.not;
		this.not = not;
		firePropertyChanged(NOT_PROPERTY, oldNot, not);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStateObject(StateObject stateObject) {
		super.setStateObject(stateObject);
	}

	/**
	 * Changes the visibility state of the <code><b>NOT</b></code> identifier.
	 */
	public void toggleNot() {
		setNot(!not);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextEncapsulatedExpression(Appendable writer) throws IOException {
		writer.append(not ? NOT_EXISTS : EXISTS);
		writer.append(LEFT_PARENTHESIS);
		super.toTextEncapsulatedExpression(writer);
		writer.append(RIGHT_PARENTHESIS);
	}
}