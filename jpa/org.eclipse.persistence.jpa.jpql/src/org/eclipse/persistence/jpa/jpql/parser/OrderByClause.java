/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * The <b>ORDER BY</b> clause allows the objects or values that are returned by the query to be
 * ordered.
 * <p>
 * <div nowrap><b>BNF:</b> <code>orderby_clause ::= <b>ORDER BY</b> {@link OrderByItem orderby_item} {, {@link OrderByItem orderby_item}}*</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class OrderByClause extends AbstractExpression {

	/**
	 * Determines whether a whitespace was parsed after <b>ORDER BY</b>.
	 */
	private boolean hasSpaceAfterOrderBy;

	/**
	 * The actual <b></b> identifier found in the string representation of the JPQL query.
	 */
	private String identifier;

	/**
	 * The order by items.
	 */
	private AbstractExpression orderByItems;

	/**
	 * Creates a new <code>OrderByClause</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public OrderByClause(AbstractExpression parent) {
		super(parent, ORDER_BY);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void acceptChildren(ExpressionVisitor visitor) {
		getOrderByItems().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getOrderByItems());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {

		children.add(buildStringExpression(ORDER_BY));

		if (hasSpaceAfterOrderBy) {
			children.add(buildStringExpression(SPACE));
		}

		if (orderByItems != null) {
			children.add(orderByItems);
		}
	}

	/**
	 * Creates a new {@link CollectionExpression} that will wrap the single order by item.
	 *
	 * @return The single order by item represented by a temporary collection
	 */
	public CollectionExpression buildCollectionExpression() {

		List<AbstractExpression> children = new ArrayList<AbstractExpression>(1);
		children.add((AbstractExpression) getOrderByItems());

		List<Boolean> commas = new ArrayList<Boolean>(1);
		commas.add(Boolean.FALSE);

		List<Boolean> spaces = new ArrayList<Boolean>(1);
		spaces.add(Boolean.FALSE);

		return new CollectionExpression(this, children, spaces, commas, true);
	}

	/**
	 * Returns the actual <b>ORDER BY</b> found in the string representation of the JPQL query, which
	 * has the actual case that was used.
	 *
	 * @return The <b>ORDER BY</b> identifier that was actually parsed
	 */
	public String getActualIdentifier() {
		return identifier;
	}

	/**
	 * Returns the {@link Expression} representing the list of items to order.
	 *
	 * @return The expression representing the list of items to order
	 */
	public Expression getOrderByItems() {
		if (orderByItems == null) {
			orderByItems = buildNullExpression();
		}
		return orderByItems;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(OrderByClauseBNF.ID);
	}

	/**
	 * Determines whether the list of items to order was parsed.
	 *
	 * @return <code>true</code> the list of items to order was parsed; <code>false</code> otherwise
	 */
	public boolean hasOrderByItems() {
		return orderByItems != null &&
		      !orderByItems.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after <b>ORDER BY</b>.
	 *
	 * @return <code>true</code> if a whitespace was parsed after <b>ORDER BY</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterOrderBy() {
		return hasSpaceAfterOrderBy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// 'ORDER BY'
		identifier = wordParser.moveForward(ORDER_BY);

		hasSpaceAfterOrderBy = wordParser.skipLeadingWhitespace() > 0;

		// Group by items
		orderByItems = parse(wordParser, OrderByItemBNF.ID, tolerant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// 'ORDER BY'
		writer.append(actual ? identifier : ORDER_BY);

		if (hasSpaceAfterOrderBy) {
			writer.append(SPACE);
		}

		// Order by items
		if (orderByItems != null) {
			orderByItems.toParsedText(writer, actual);
		}
	}
}