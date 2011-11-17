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

import org.eclipse.persistence.jpa.jpql.parser.StringPrimaryBNF;
import org.eclipse.persistence.jpa.jpql.parser.UpperExpression;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>UPPER</b></code> function converts a string to upper case and it returns a string.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= UPPER(string_primary)</code><p>
 *
 * @see UpperExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class UpperExpressionStateObject extends AbstractSingleEncapsulatedExpressionStateObject {

	/**
	 * Creates a new <code>UpperExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public UpperExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>UpperExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateObject The {@link StateObject} representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public UpperExpressionStateObject(StateObject parent, StateObject stateObject) {
		super(parent, stateObject);
	}

	/**
	 * Creates a new <code>UpperExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param jpqlFragment The portion of the query representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public UpperExpressionStateObject(StateObject parent, String jpqlFragment) {
		super(parent, jpqlFragment);
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
	public UpperExpression getExpression() {
		return (UpperExpression) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return UPPER;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getQueryBNFId() {
		return StringPrimaryBNF.ID;
	}

	/**
	 * Keeps a reference of the {@link UpperExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link UpperExpression parsed object} representing an <code><b>UPPER</b></code>
	 * expression
	 */
	public void setExpression(UpperExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStateObject(StateObject stateObject) {
		super.setStateObject(stateObject);
	}
}