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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.model.IListChangeListener;
import org.eclipse.persistence.jpa.jpql.model.ISelectExpressionStateObjectBuilder;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectClauseInternalBNF;
import org.eclipse.persistence.jpa.jpql.parser.SelectExpressionBNF;
import org.eclipse.persistence.jpa.jpql.util.iterator.CloneListIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>SELECT</b></code> statement queries data from entities, which determines the type of
 * the objects or values to be selected.
 * <p>
 * <pre><code>BNF: select_clause ::= SELECT [DISTINCT] select_expression {, select_expression}*
 *     select_expression ::= single_valued_path_expression |
 *                           aggregate_expression |
 *                           identification_variable |
 *                           OBJECT(identification_variable) |
 *                           constructor_expression</code></pre>
 *
 * @see SelectStatementStateObject
 * @see SelectClause
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class SelectClauseStateObject extends AbstractSelectClauseStateObject
                                     implements ListHolderStateObject<StateObject> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SelectClause getExpression() {
		return (SelectClause) super.getExpression();
	}

	/**
	 * Keeps a reference of the {@link SelectClause parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link SelectClause parsed object} representing a <code>SELECT<b></b></code>
	 * clause
	 */
	public void setExpression(SelectClause expression) {
		super.setExpression(expression);
	}

	/**
	 * The builder is cached during the creation of the select expression.
	 */
	private ISelectExpressionStateObjectBuilder builder;

	/**
	 * The list of select expressions.
	 */
	private List<StateObject> items;

	/**
	 * A constant used to notify the list of select expressions has changed.
	 */
	public static final String SELECT_ITEMS_LIST = "selectItems";

	/**
	 * Creates a new <code>SelectClauseStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SelectClauseStateObject(SelectStatementStateObject parent) {
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
		children.addAll(items);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends StateObject> T addItem(T item) {
		getChangeSupport().addItem(this, items, SELECT_ITEMS_LIST, parent(item));
		return item;
	}

	/**
	 * Adds the given {@link StateObject} as a select item.
	 *
	 * @param stateObject The {@link StateObject} representing the select expression
	 * @param resultVariable The result variable identifying the select expression
	 * @return The newly created {@link ResultVariableStateObject}
	 */
	public ResultVariableStateObject addItem(StateObject stateObject, String resultVariable) {
		ResultVariableStateObject item = new ResultVariableStateObject(
			this,
			stateObject,
			false,
			resultVariable
		);
		addItem(item);
		return item;
	}

	/**
	 * Adds the given path as a select item, which can either be an identification variable or a
	 * state-field path expression.
	 *
	 * @param jpqlFragment The select expression to parse as a select item
	 * @return The {@link StateObject} encapsulating the given path
	 */
	public StateObject addItem(String jpqlFragment) {
		StateObject item = buildStateObject(jpqlFragment, SelectExpressionBNF.ID);
		addItem(item);
		return item;
	}

	/**
	 * Adds the given expression as a select item.
	 *
	 * @param jpqlFragment The select expression to parse as a select item
	 * @param resultVariable The result variable identifying the select expression
	 * @return The newly created {@link ResultVariableStateObject}
	 */
	public ResultVariableStateObject addItem(String jpqlFragment, String resultVariable) {
		ResultVariableStateObject item = new ResultVariableStateObject(this);
		item.parse(jpqlFragment);
		item.setResultVariable(resultVariable);
		addItem(item);
		return item;
	}

	/**
	 * Adds the given {@link StateObject} as a select item.
	 *
	 * @param stateObject The {@link StateObject} representing the select expression
	 * @param resultVariable The result variable identifying the select expression
	 * @return The newly created {@link ResultVariableStateObject}
	 */
	public ResultVariableStateObject addItemAs(StateObject stateObject, String resultVariable) {
		ResultVariableStateObject item = new ResultVariableStateObject(
			this,
			stateObject,
			true,
			resultVariable
		);
		addItem(item);
		return item;
	}

	/**
	 * Adds the given JPQL fragment as a select item.
	 *
	 * @param jpqlFragment The portion of a JPQL query that represents a select expression
	 * @param resultVariable The result variable identifying the select expression
	 * @return The newly created {@link ResultVariableStateObject}
	 */
	public ResultVariableStateObject addItemAs(String jpqlFragment, String resultVariable) {
		ResultVariableStateObject item = new ResultVariableStateObject(this);
		item.addAs();
		item.parse(jpqlFragment);
		item.setResultVariable(resultVariable);
		addItem(item);
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addItems(List<? extends StateObject> items) {
		getChangeSupport().addItems(this, this.items, SELECT_ITEMS_LIST, parent(items));
	}

	/**
	 * {@inheritDoc}
	 */
	public void addListChangeListener(String listName, IListChangeListener<StateObject> listener) {
		getChangeSupport().addListChangeListener(listName, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canMoveDown(StateObject item) {
		return getChangeSupport().canMoveDown(items, item);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canMoveUp(StateObject item) {
		return getChangeSupport().canMoveUp(items, item);
	}

	/**
	 * Creates and returns a new {@link ISelectExpressionBuilder} that can be used to
	 * programmatically create a single select expression and once the expression is complete,
	 * {@link ISelectExpressionBuilder#commit()} will push the {@link StateObject} representation of
	 * that expression as this clause's select expression.
	 *
	 * @return A new builder that can be used to quickly create a select expression
	 */
	public ISelectExpressionStateObjectBuilder getBuilder() {
		if (builder == null) {
			builder = getQueryBuilder().buildStateObjectBuilder(this);
		}
		return builder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FromClauseStateObject getFromClause() {
		return (FromClauseStateObject) super.getFromClause();
	}

	/**
	 * {@inheritDoc}
	 */
	public StateObject getItem(int index) {
		return items.get(index);
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
	public boolean hasItems() {
		return items.size() > 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasSelectItem() {
		return hasItems();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		items = new ArrayList<StateObject>();
	}

	/**
	 * {@inheritDoc}
	 */
	public IterableListIterator<StateObject> items() {
		return new CloneListIterator<StateObject>(items);
	}

	/**
	 * {@inheritDoc}
	 */
	public int itemsSize() {
		return items.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public StateObject moveDown(StateObject item) {
		getChangeSupport().moveDown(this, items, SELECT_ITEMS_LIST, item);
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	public StateObject moveUp(StateObject item) {
		getChangeSupport().moveUp(this, items, SELECT_ITEMS_LIST, item);
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parse(String jpqlFragment) {
		List<StateObject> stateObjects = buildStateObjects(jpqlFragment, SelectClauseInternalBNF.ID);
		addItems(stateObjects);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeItem(StateObject stateObject) {
		getChangeSupport().removeItem(this, items, SELECT_ITEMS_LIST, stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeItems(Collection<StateObject> items) {
		getChangeSupport().removeItems(this, this.items, SELECT_ITEMS_LIST, items);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeListChangeListener(String listName, IListChangeListener<StateObject> listener) {
		getChangeSupport().removeListChangeListener(listName, listener);
	}

	/**
	 * Sets the select expression to be those contained on the given collection. If any select items
	 * were already set, they will be removed.
	 *
	 * @param stateObjects The new select expressions
	 */
	public void setItems(List<StateObject> stateObjects) {
		builder = null;
		getChangeSupport().replaceItems(this, items, SELECT_ITEMS_LIST, stateObjects);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {

		// 'SELECT'
		writer.append(SELECT);

		// 'DISTINCT'
		if (hasDistinct()) {
			writer.append(SPACE);
			writer.append(DISTINCT);
		}

		// Select items
		if (hasItems()) {
			writer.append(SPACE);
			toStringItems(writer, items, true);
		}
	}
}