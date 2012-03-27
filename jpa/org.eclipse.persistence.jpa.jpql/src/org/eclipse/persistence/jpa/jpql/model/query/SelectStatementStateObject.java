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
import org.eclipse.persistence.jpa.jpql.model.ISelectExpressionStateObjectBuilder;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;

/**
 * This state object represents a select statement, which has at least a <code><b>SELECT</b></code>
 * clause and a <code><b>FROM</b></code> clause. The other clauses are optional.
 * <p>
 * <pre><code>BNF: select_statement ::= select_clause from_clause [where_clause] [groupby_clause] [having_clause] [orderby_clause]</code></pre>
 *
 * @see FromClauseStateObject
 * @see GroupByClauseStateObject
 * @see HavingClauseStateObject
 * @see OrderByClauseStateObject
 * @see SelectClauseStateObject
 * @see WhereClauseStateObject
 *
 * @see SelectStatement
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class SelectStatementStateObject extends AbstractSelectStatementStateObject {

	/**
	 * The state object representing the <code><b>ORDER BY</b></code> clause.
	 */
	private OrderByClauseStateObject orderByClause;

	/**
	 * Notify the state object representing the <code><b>ORDER BY</b></code> clause has changed.
	 */
	public static final String ORDER_BY_CLAUSE_PROPERTY = "orderByClause";

	/**
	 * Creates a new <code>SelectStatementStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SelectStatementStateObject(JPQLQueryStateObject parent) {
		super(parent);
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
		if (orderByClause != null) {
			children.add(orderByClause);
		}
	}

	/**
	 * Adds the <code><b>ORDER BY</b></code> clause. The clause is not added if it's already present.
	 *
	 * @return The state object representing the <code><b>ORDER BY</b></code> clause
	 */
	public OrderByClauseStateObject addOrderByClause() {
		if (!hasOrderByClause()) {
			setOrderByClause(new OrderByClauseStateObject(this));
		}
		return orderByClause;
	}

	/**
	 * Adds the <code><b>ORDER BY</b></code> clause and parses the given JPQL fragment, which should
	 * represent one or many ordering items. The clause is not added if it's already present.
	 *
	 * @param jpqlFragment The fragment of the JPQL to parse that represents the ordering items, the
	 * fragment cannot start with <code><b>ORDER BY</b></code>
	 * @return The {@link OrderByClauseStateObject}
	 */
	public OrderByClauseStateObject addOrderByClause(String jpqlFragment) {
		OrderByClauseStateObject stateObject = addOrderByClause();
		stateObject.parse(jpqlFragment);
		return stateObject;
	}

	/**
	 * Adds the given {@link StateObject} as a select item.
	 *
	 * @param stateObject The {@link StateObject} representing a select expression
	 */
	public void addSelectItem(StateObject stateObject) {
		getSelectClause().addItem(stateObject);
	}

	/**
	 * Adds the given {@link StateObject} as a select item.
	 *
	 * @param stateObject The {@link StateObject} representing the select expression
	 * @param resultVariable The result variable identifying the select expression
	 * @return The newly created {@link ResultVariableStateObject}
	 */
	public ResultVariableStateObject addSelectItem(StateObject stateObject, String resultVariable) {
		return getSelectClause().addItem(stateObject, resultVariable);
	}

	/**
	 * Adds the given path as a select item, which can either be an identification variable or a
	 * state-field path expression.
	 *
	 * @param path The select expression to parse as a select item
	 * @return The {@link StateObject} encapsulating the given path
	 */
	public StateObject addSelectItem(String path) {
		return getSelectClause().addItem(path);
	}

	/**
	 * Adds the given expression as a select item.
	 *
	 * @param jpqlFragment The select expression to parse as a select item
	 * @param resultVariable The result variable identifying the select expression
	 * @return The newly created {@link ResultVariableStateObject}
	 */
	public ResultVariableStateObject addSelectItem(String jpqlFragment, String resultVariable) {
		return getSelectClause().addItem(jpqlFragment, resultVariable);
	}

	/**
	 * Adds the given {@link StateObject} as a select item.
	 *
	 * @param stateObject The {@link StateObject} representing the select expression
	 * @param resultVariable The result variable identifying the select expression
	 * @return The newly created {@link ResultVariableStateObject}
	 */
	public ResultVariableStateObject addSelectItemAs(StateObject stateObject, String resultVariable) {
		return getSelectClause().addItemAs(stateObject, resultVariable);
	}

	/**
	 * Adds the given expression as a select item.
	 *
	 * @param jpqlFragment The portion of a JPQL query that represents a select expression
	 * @param resultVariable The result variable identifying the select expression
	 * @return The newly created {@link ResultVariableStateObject}
	 */
	public ResultVariableStateObject addSelectItemAs(String jpqlFragment, String resultVariable) {
		return getSelectClause().addItemAs(jpqlFragment, resultVariable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractFromClauseStateObject buildFromClause() {
		return new FromClauseStateObject(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractSelectClauseStateObject buildSelectClause() {
		return new SelectClauseStateObject(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SelectStatement getExpression() {
		return (SelectStatement) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FromClauseStateObject getFromClause() {
		return (FromClauseStateObject) super.getFromClause();
	}

	/**
	 * Returns the state object representing the <code><b>ORDER BY</b></code> clause.
	 *
	 * @return Either the actual state object representing the <code><b>ORDER BY</b></code> clause or
	 * <code>null</code> if it's not present
	 */
	public OrderByClauseStateObject getOrderByClause() {
		return orderByClause;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryStateObject getParent() {
		return (JPQLQueryStateObject) super.getParent();
	}

	/**
	 * Creates and returns a new {@link ISelectExpressionStateObjectBuilder} that can be used to
	 * programmatically create a single select expression and once the expression is complete,
	 * {@link ISelectExpressionStateObjectBuilder#commit()} will push the {@link StateObject}
	 * representation of that expression as this clause's select expression.
	 *
	 * @return A new builder that can be used to quickly create a select expression
	 */
	public ISelectExpressionStateObjectBuilder getSelectBuilder() {
		return getSelectClause().getBuilder();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SelectClauseStateObject getSelectClause() {
		return (SelectClauseStateObject) super.getSelectClause();
	}

	/**
	 * Returns the state object representing the <code><b>ORDER BY</b></code> clause.
	 *
	 * @return <code>true</code> if the <code><b>ORDER BY</b></code> clause is present;
	 * <code>false</code> otherwise
	 */
	public boolean hasOrderByClause() {
		return orderByClause != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			SelectStatementStateObject select = (SelectStatementStateObject) stateObject;
			return areEquivalent(orderByClause, select.orderByClause);
		}

		return false;
	}

	/**
	 * Removes the <code><b>ORDER BY</b></code> clause.
	 */
	public void removeOrderByClause() {
		setOrderByClause(null);
	}

	/**
	 * Keeps a reference of the {@link SelectStatement parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link SelectStatement parsed object} representing a <code><b>SELECT</b></code>
	 * statement
	 */
	public void setExpression(SelectStatement expression) {
		super.setExpression(expression);
	}

	private void setOrderByClause(OrderByClauseStateObject orderByClause) {
		OrderByClauseStateObject oldOrderByClause = this.orderByClause;
		this.orderByClause = orderByClause;
		firePropertyChanged(ORDER_BY_CLAUSE_PROPERTY, oldOrderByClause, orderByClause);
	}

	/**
	 * Either adds or removes the <code><b>ORDER BY</b></code> clause.
	 */
	public void toggleOrderByClause() {
		if (orderByClause != null) {
			removeOrderByClause();
		}
		else {
			addOrderByClause();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {

		super.toTextInternal(writer);

		if (orderByClause != null) {
			writer.append(SPACE);
			orderByClause.toString(writer);
		}
	}
}