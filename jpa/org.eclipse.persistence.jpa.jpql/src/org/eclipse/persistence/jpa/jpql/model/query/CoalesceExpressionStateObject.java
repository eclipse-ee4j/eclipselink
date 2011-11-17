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
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import org.eclipse.persistence.jpa.jpql.model.IListChangeListener;
import org.eclipse.persistence.jpa.jpql.parser.CoalesceExpression;
import org.eclipse.persistence.jpa.jpql.parser.ScalarExpressionBNF;
import org.eclipse.persistence.jpa.jpql.util.iterator.CloneListIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * A <code><b>COALESCE</b></code> expression returns <code>null</code> if all its arguments evaluate
 * to <code>null</code>, and the value of the first non-<code>null</code> argument otherwise.
 * <p>
 * The return type is the type returned by the arguments if they are all of the same type, otherwise
 * it is undetermined.
 *
 * <div nowrap><b>BNF:</b> <code>coalesce_expression::= COALESCE(scalar_expression {, scalar_expression}+)</code>
 *
 * @see CoalesceExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class CoalesceExpressionStateObject extends AbstractEncapsulatedExpressionStateObject
                                           implements ListHolderStateObject<StateObject> {


	/**
	 * The state objects representing the scalar expressions.
	 */
	private List<StateObject> items;

	/**
	 * Notifies the content of the state object list has changed.
	 */
	public static final String STATE_OBJECTS_LIST = "stateObjects";

	/**
	 * Creates a new <code>CoalesceExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CoalesceExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>CoalesceExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param items The list of {@link StateObject StateObjects} representing the encapsulated
	 * expressions
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CoalesceExpressionStateObject(StateObject parent, List<StateObject> items) {
		super(parent);
		this.items.addAll(parent(items));
	}

	/**
	 * Creates a new <code>CoalesceExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param items The list of {@link StateObject StateObjects} representing the encapsulated
	 * expressions
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CoalesceExpressionStateObject(StateObject parent, StateObject... items) {
		super(parent);
		Collections.addAll(this.items, parent(items));
	}

	/**
	 * Creates a new <code>CoalesceExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param items The list of expression to parse into their {@link StateObject}
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CoalesceExpressionStateObject(StateObject parent, String... items) {
		super(parent);
		parseItemInternal(items);
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
	public StateObject addItem(StateObject item) {
		getChangeSupport().addItem(this, items, STATE_OBJECTS_LIST, parent(item));
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addItems(List<? extends StateObject> items) {
		getChangeSupport().addItems(this, this.items, STATE_OBJECTS_LIST, parent(items));
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
	 * {@inheritDoc}
	 */
	@Override
	public CoalesceExpression getExpression() {
		return (CoalesceExpression) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return COALESCE;
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
	public boolean hasItems() {
		return !items.isEmpty();
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
		getChangeSupport().moveDown(this, items, STATE_OBJECTS_LIST, item);
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	public StateObject moveUp(StateObject item) {
		getChangeSupport().moveUp(this, items, STATE_OBJECTS_LIST, item);
		return item;
	}

	/**
	 * Parses the given JPQL fragment, which represents a single encapsulated expression, and creates
	 * the {@link StateObject}.
	 *
	 * @param jpqlFragment The portion of the query representing a single encapsulated expression
	 */
	public void parseItem(String jpqlFragment) {
		StateObject stateObject = buildStateObject(jpqlFragment, ScalarExpressionBNF.ID);
		addItem(stateObject);
	}

	/**
	 * Parses the given list of JPQL fragments, which represents each of the encapsulated expressions,
	 * and creates the {@link StateObject StateObjects}.
	 *
	 * @param items The list of expression to parse into their {@link StateObject}
	 */
	protected void parseItemInternal(String... items) {
		for (String item : items) {
			StateObject stateObject = buildStateObject(item, ScalarExpressionBNF.ID);
			this.items.add(parent(stateObject));
		}
	}

	/**
	 * Parses the given JPQL fragments, which represent individual expression, and creates the
	 * {@link StateObject StateObjects}.
	 *
	 * @param items The portion of the query representing a single encapsulated expression
	 */
	public void parseItems(ListIterator<String> items) {
		while (items.hasNext()) {
			StateObject stateObject = buildStateObject(items.next(), ScalarExpressionBNF.ID);
			addItem(parent(stateObject));
		}
	}

	/**
	 * Parses the given JPQL fragments, which represent individual expression, and creates the
	 * {@link StateObject StateObjects}.
	 *
	 * @param items The portion of the query representing a single encapsulated expression
	 */
	public void parseItems(String... items) {
		for (String item : items) {
			StateObject stateObject = buildStateObject(item, ScalarExpressionBNF.ID);
			addItem(parent(stateObject));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeItem(StateObject item) {
		getChangeSupport().removeItem(this, items, STATE_OBJECTS_LIST, item);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeItems(Collection<StateObject> items) {
		getChangeSupport().removeItems(this, this.items, STATE_OBJECTS_LIST, items);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeListChangeListener(String listName, IListChangeListener<StateObject> listener) {
		getChangeSupport().removeListChangeListener(listName, listener);
	}

	/**
	 * Keeps a reference of the {@link CoalesceExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link CoalesceExpression parsed object} representing a <code><b>CASE</b></code>
	 * expression
	 */
	public void setExpression(CoalesceExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextEncapsulatedExpression(Appendable writer) throws IOException {
		toStringItems(writer, items, true);
	}
}