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

import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.parser.SubExpression;

/**
 * This expression wraps a sub-expression within parenthesis.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= (sub_expression)</code><p>
 *
 * @see SubExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class SubExpressionStateObject extends AbstractSingleEncapsulatedExpressionStateObject {

	/**
	 * The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF
	 * JPQLQueryBNF} that is used to parse the encapsulated expression.
	 */
	private String queryBNFId;

	/**
	 * Creates a new <code>SubExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SubExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>SubExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param stateObject The {@link StateObject} representing the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SubExpressionStateObject(StateObject parent, StateObject stateObject) {
		super(parent, stateObject);
	}

	/**
	 * Creates a new <code>SubExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param jpqlFragment The portion of the query representing the encapsulated expression
	 * @param queryBNFId  The unique identifier of the {@link org.eclipse.persistence.jpa.jpql.parser
	 * .JPQLQueryBNF JPQLQueryBNF} that is used to parse the encapsulated expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SubExpressionStateObject(StateObject parent, String jpqlFragment, String queryBNFId) {
		super(parent);
		this.queryBNFId = queryBNFId;
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
	public SubExpression getExpression() {
		return (SubExpression) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return ExpressionTools.EMPTY_STRING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getQueryBNFId() {
		return queryBNFId;
	}

	/**
	 * Keeps a reference of the {@link SubExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link SubExpression parsed object} representing an encapsulated
	 * expression
	 */
	public void setExpression(SubExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets the unique identifier of the {@link org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF
	 * JPQLQueryBNF} that is used to parse the encapsulated expression.
	 *
	 * @param queryBNFId The non-<code>null</code> ID of the BNF
	 * @exception NullPointerException The query BNF ID cannot be <code>null</code>
	 */
	public void setQueryBNFId(String queryBNFId) {
		Assert.isNotNull(queryBNFId, "The query BNF ID cannot be null");
		this.queryBNFId = queryBNFId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStateObject(StateObject stateObject) {
		super.setStateObject(stateObject);
	}
}