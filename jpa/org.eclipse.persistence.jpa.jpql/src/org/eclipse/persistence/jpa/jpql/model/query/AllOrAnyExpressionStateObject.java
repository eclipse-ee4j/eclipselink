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

import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.parser.AllOrAnyExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubqueryBNF;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * An <code><b>ALL</b></code> conditional expression is a predicate that is <code>true</code> if the
 * comparison operation is <code>true</code> for all values in the result of the subquery or the
 * result of the subquery is empty. An <code><b>ALL</b></code> conditional expression is
 * <code>false</code> if the result of the comparison is <code>false</code> for at least one row,
 * and is unknown if neither <code>true</code> nor <code>false</code>.
 * <p>
 * An <code><b>ANY</b></code> conditional expression is a predicate that is <code>true</code> if the
 * comparison operation is <code>true</code> for some value in the result of the subquery. An
 * <code><b>ANY</b></code> conditional expression is <code>false</code> if the result of the
 * subquery is empty or if the comparison operation is <code>false</code> for every value in the
 * esult of the subquery, and is unknown if neither <code>true</code> nor <code>false</code>. The
 * keyword <code><b>SOME</b></code> is synonymous with <code><b>ANY</b></code>. The comparison
 * operators used with <code><b>ALL</b></code> or <code><b>ANY</b></code> conditional expressions
 * are =, <, <=, >, >=, <>. The result of the subquery must be like that of the other argument to
 * the comparison operator in type.
 * <p>
 * <div nowrap><b>BNF:</b> <code>all_or_any_expression ::= {ALL|ANY|SOME}(subquery)</code><p>
 *
 * @see AllOrAnyExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class AllOrAnyExpressionStateObject extends AbstractSingleEncapsulatedExpressionStateObject {

	/**
	 * The actual JPQL identifier: <code><b>ALL</b></code>, <code><b>ANY</b></code> or
	 * <code><b>SOME</b></code>.
	 */
	private String identifier;

	/**
	 * Notifies the identifier property has changed.
	 */
	public static final String IDENTIFIER_PROPERTY = "identifier";

	/**
	 * Creates a new <code>AllOrAnyExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param identifier One of the three possible JPQL identifiers: <code><b>ALL</b></code>,
	 * <code><b>ANY</b></code> or <code><b>SOME</b></code>
	 * @exception org.eclipse.persistence.jpa.jpql.Assert.AssertException The given JPQL identifier
	 * is one from the possible choices
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public AllOrAnyExpressionStateObject(StateObject parent, String identifier) {
		super(parent);
		validateIdentifier(identifier);
		this.identifier = identifier;
	}

	/**
	 * Creates a new <code>AllOrAnyExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param identifier One of the three possible JPQL identifiers: <code><b>ALL</b></code>,
	 * <code><b>ANY</b></code> or <code><b>SOME</b></code>
	 * @param stateObject The {@link StateObject} representing the encapsulated expression
	 * @exception org.eclipse.persistence.jpa.jpql.Assert.AssertException The given JPQL identifier
	 * is one from the possible choices
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public AllOrAnyExpressionStateObject(StateObject parent,
	                                     String identifier,
	                                     StateObject stateObject) {

		super(parent, stateObject);

		validateIdentifier(identifier);
		this.identifier = identifier;
	}

	/**
	 * Creates a new <code>AllOrAnyExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param identifier One of the three possible JPQL identifiers: <code><b>ALL</b></code>,
	 * <code><b>ANY</b></code> or <code><b>SOME</b></code>
	 * @param jpqlFragment The portion of the query representing the encapsulated expression
	 * @exception org.eclipse.persistence.jpa.jpql.Assert.AssertException The given JPQL identifier
	 * is one from the possible choices
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public AllOrAnyExpressionStateObject(StateObject parent, String identifier, String jpqlFragment) {
		super(parent, jpqlFragment);
		validateIdentifier(identifier);
		this.identifier = identifier;
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
	public AllOrAnyExpression getExpression() {
		return (AllOrAnyExpression) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getQueryBNFId() {
		return SubqueryBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			AllOrAnyExpressionStateObject allOrAny = (AllOrAnyExpressionStateObject) stateObject;
			return identifier.equals(allOrAny.identifier);
		}

		return false;
	}

	/**
	 * Keeps a reference of the {@link AllOrAnyExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into {@link
	 * StateObject StateObjects}.
	 *
	 * @param expression The {@link AllOrAnyExpression parsed object} representing an <code><b>ALL</b></code>,
	 * <code><b>ANY</b></code> or <code><b>SOME</b></code> expression
	 */
	public void setExpression(AllOrAnyExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets the JPQL identifier to the given one.
	 *
	 * @param identifier One of the three possible JPQL identifiers: <code><b>ALL</b></code>,
	 * <code><b>ANY</b></code> or <code><b>SOME</b></code>
	 * @exception org.eclipse.persistence.jpa.jpql.Assert.AssertException The given JPQL identifier
	 * is one from the possible choices
	 */
	public void setIdentifier(String identifier) {

		validateIdentifier(identifier);

		String oldIdentifier = this.identifier;
		this.identifier = identifier;
		firePropertyChanged(IDENTIFIER_PROPERTY, oldIdentifier, identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStateObject(StateObject stateObject) {
		super.setStateObject(stateObject);
	}

	private void validateIdentifier(String identifier) {
		Assert.isValid(identifier, "The identifier is not valid: " + identifier, ALL, ANY, SOME);
	}
}