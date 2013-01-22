/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * In a hierarchical query, if the rows of siblings of the same parent need to be ordered, then the
 * <code><b>ORDER SIBLINGS BY</b></code> clause should be used. Either <code><b>ORDER BY</b></code>
 * or <code><b>GROUP BY</b></code> should not be used, as they will destroy the hierarchical order
 * of the <code><b>CONNECT BY</b></code> results.
 * <p>
 * <div nowrap><b>BNF:</b> <code>order_sibling_by_clause ::= <b>ORDER SIBLINGS BY</b> {@link OrderByItem orderby_item} {, {@link OrderByItem orderby_item}}*</code><p>
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public final class OrderSiblingsByClause extends AbstractOrderByClause {

	/**
	 * Creates a new <code>OrderSiblingsByClause</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public OrderSiblingsByClause(AbstractExpression parent) {
		super(parent, ORDER_SIBLINGS_BY);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(ExpressionVisitor visitor) {
		acceptUnknownVisitor(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(OrderSiblingsByClauseBNF.ID);
	}
}