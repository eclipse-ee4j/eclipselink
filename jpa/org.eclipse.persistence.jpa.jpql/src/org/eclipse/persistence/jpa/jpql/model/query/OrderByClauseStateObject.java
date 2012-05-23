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
import org.eclipse.persistence.jpa.jpql.parser.InternalOrderByItemBNF;
import org.eclipse.persistence.jpa.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem.Ordering;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItemBNF;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>ORDER BY</b></code> clause allows the objects or values that are returned by the
 * query to be ordered.
 * <p>
 * <div nowrap><b>BNF:</b> <code>orderby_clause ::= <b>ORDER BY</b> {@link OrderByItemStateObject orderby_item} {, {@link OrderByItemStateObject orderby_item}}*</code><p>
 *
 * @see org.eclipse.persistence.jpa.jpql.parser.OrderByClause OrderByClause
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class OrderByClauseStateObject extends AbstractListHolderStateObject<OrderByItemStateObject> {

	/**
	 * Notify the list of {@link StateObject StateObjects} representing the <code><b>ORDER BY</b></code>
	 * items.
	 */
	public static final String ORDER_BY_ITEMS_LIST = "orderByItems";

	/**
	 * Creates a new <code>OrderByClauseStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public OrderByClauseStateObject(SelectStatementStateObject parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Adds a new <code><b>ORDER BY</b></code> item to this clause.
	 *
	 * @return The newly added {@link OrderByItemStateObject}
	 */
	public OrderByItemStateObject addItem() {
		return addItem(Ordering.DEFAULT);
	}

	/**
	 * Adds a new <code><b>ORDER BY</b></code> item to this clause.
	 *
	 * @param ordering The ascending order
	 * @return The newly added {@link OrderByItemStateObject}
	 */
	public OrderByItemStateObject addItem(Ordering ordering) {
		OrderByItemStateObject updateItem = new OrderByItemStateObject(this, ordering);
		addItem(updateItem);
		return updateItem;
	}

	/**
	 * Adds a new <code><b>ORDER BY</b></code> item to this clause.
	 *
	 * @return The newly added {@link OrderByItemStateObject}
	 */
	public OrderByItemStateObject addItem(String... paths) {
		return addOrderByItem(paths, Ordering.DEFAULT);
	}

	/**
	 * Adds a new <code><b>ORDER BY</b></code> item to this clause.
	 *
	 * @param variable The identification variable or the result variable
	 * @return The newly added {@link OrderByItemStateObject}
	 */
	public OrderByItemStateObject addItem(String variable) {
		return addItem(variable, Ordering.DEFAULT);
	}

	/**
	 * Adds a new <code><b>ORDER BY</b></code> item to this clause.
	 *
	 * @param path The identification variable or the result variable
	 * @param ordering The ascending order
	 * @return The newly added {@link OrderByItemStateObject}
	 */
	public OrderByItemStateObject addItem(String path, Ordering ordering) {
		OrderByItemStateObject orderByItem = addItem(ordering);
		orderByItem.setStateObject(buildStateObject(path, InternalOrderByItemBNF.ID));
		return orderByItem;
	}

	/**
	 * Adds a new <code><b>ORDER BY</b></code> item to this clause with <code><b>ASC</b></code>.
	 *
	 * @param variable The identification variable or the result variable
	 * @return The newly added {@link OrderByItemStateObject}
	 */
	public OrderByItemStateObject addItemAsc(String variable) {
		return addItem(variable, Ordering.ASC);
	}

	/**
	 * Adds a new <code><b>ORDER BY</b></code> item to this clause with <code><b>DESC</b></code>.
	 *
	 * @param variable The identification variable or the result variable
	 * @return The newly added {@link OrderByItemStateObject}
	 */
	public OrderByItemStateObject addItemDesc(String variable) {
		return addItem(variable, Ordering.DESC);
	}

	/**
	 * Adds a new <code><b>ORDER BY</b></code> item to this clause.
	 *
	 * @param paths The paths or the item
	 * @param ordering The ascending order
	 * @return The newly added {@link OrderByItemStateObject}
	 */
	public OrderByItemStateObject addOrderByItem(String[] paths, Ordering ordering) {

		StateFieldPathExpressionStateObject stateField = new StateFieldPathExpressionStateObject(this);
		stateField.setPaths(paths);

		OrderByItemStateObject updateItem = new OrderByItemStateObject(this, stateField, ordering);
		addItem(updateItem);
		return updateItem;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrderByClause getExpression() {
		return (OrderByClause) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SelectStatementStateObject getParent() {
		return (SelectStatementStateObject) super.getParent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {
		return super.isEquivalent(stateObject) &&
		       areChildrenEquivalent((OrderByClauseStateObject) stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String listName() {
		return ORDER_BY_ITEMS_LIST;
	}

	/**
	 * Parses the given JPQL fragment, which represents either a single or many ordering items, the
	 * fragment will be parsed and converted into {@link OrderByItemStateObject}.
	 *
	 * @param jpqlFragment The portion of the query to parse
	 */
	public void parse(String jpqlFragment) {
		// Note: No need to add the items here, they are automatically added by the builder
		buildStateObjects(jpqlFragment, OrderByItemBNF.ID);
	}

	/**
	 * Keeps a reference of the {@link OrderByClause parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link OrderByClause parsed object} representing an <code><b>ORDER
	 * BY</b></code clause
	 */
	public void setExpression(OrderByClause expression) {
		super.setExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {
		writer.append(ORDER_BY);
		if (hasItems()) {
			writer.append(SPACE);
			toStringItems(writer, true);
		}
	}
}