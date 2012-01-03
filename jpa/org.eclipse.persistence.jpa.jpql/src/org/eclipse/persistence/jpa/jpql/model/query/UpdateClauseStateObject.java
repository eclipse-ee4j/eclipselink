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
import java.util.ListIterator;
import org.eclipse.persistence.jpa.jpql.model.IListChangeListener;
import org.eclipse.persistence.jpa.jpql.parser.InternalUpdateClauseBNF;
import org.eclipse.persistence.jpa.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.jpql.util.iterator.CloneListIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This is the <code><b>UPDATE</b></code> clause of the <code><b>UPDATE</b></code> statement.
 * <p>
 * An <code><b>UPDATE</b></code> statement provides bulk operations over sets of entities of a
 * single entity class (together with its subclasses, if any). Only one entity abstract schema type
 * may be specified in the <code><b>UPDATE</b></code> clause.
 * <p>
 * <div nowrap><b>BNF:</b> <code>update_clause ::= UPDATE abstract_schema_name [[AS] identification_variable] SET update_item {, update_item}*</code><p>
 *
 * @see UpdateClause
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class UpdateClauseStateObject extends AbstractModifyClauseStateObject
                                     implements ListHolderStateObject<UpdateItemStateObject> {

	/**
	 * The list of {@link UpdateItemStateObject} that represent the update items.
	 */
	private List<UpdateItemStateObject> items;

	/**
	 * Notify the list of {@link StateObject StateObjects} representing the update items.
	 */
	public static final String UPDATE_ITEMS_LIST = "items";

	/**
	 * Creates a new <code>UpdateClauseStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public UpdateClauseStateObject(UpdateStatementStateObject parent) {
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
	 * Adds a new <code><b>UPDATE</b></code> item to this clause.
	 *
	 * @return The newly added {@link UpdateItemStateObject}
	 */
	public UpdateItemStateObject addItem() {
		UpdateItemStateObject updateItem = new UpdateItemStateObject(this);
		addItem(updateItem);
		return updateItem;
	}

	/**
	 * Adds a new <code><b>UPDATE</b></code> item to this clause.
	 *
	 * @param paths The paths of the value to update
	 * @param newValue The {@link StateObject} representing the new value
	 * @return The newly added {@link UpdateItemStateObject}
	 */
	public UpdateItemStateObject addItem(ListIterator<String> paths, StateObject newValue) {
		UpdateItemStateObject updateItem = new UpdateItemStateObject(this);
		updateItem.setPaths(paths);
		updateItem.setNewValue(newValue);
		addItem(updateItem);
		return updateItem;
	}

	/**
	 * Adds a new <code><b>UPDATE</b></code> item to this clause.
	 *
	 * @param paths The paths of the value to update
	 * @param newValue The string representation of the new value to parse and to convert into a
	 * {@link StateObject} representation
	 * @return The newly added {@link UpdateItemStateObject}
	 */
	public UpdateItemStateObject addItem(ListIterator<String> paths, String newValue) {
		UpdateItemStateObject updateItem = new UpdateItemStateObject(this);
		updateItem.setPaths(paths);
		updateItem.parseNewValue(newValue);
		addItem(updateItem);
		return updateItem;
	}

	/**
	 * Adds a new <code><b>UPDATE</b></code> item to this clause.
	 *
	 * @param path The path of the value to update
	 * @return The newly added {@link UpdateItemStateObject}
	 */
	public UpdateItemStateObject addItem(String path) {
		UpdateItemStateObject updateItem = new UpdateItemStateObject(this);
		updateItem.setPath(path);
		addItem(updateItem);
		return updateItem;
	}

	/**
	 * Adds a new <code><b>UPDATE</b></code> item to this clause.
	 *
	 * @param path The path of the value to update
	 * @param newValue The {@link StateObject} representing the new value
	 * @return The newly added {@link UpdateItemStateObject}
	 */
	public UpdateItemStateObject addItem(String path, StateObject newValue) {
		UpdateItemStateObject updateItem = new UpdateItemStateObject(this);
		updateItem.setPath(path);
		updateItem.setNewValue(newValue);
		addItem(updateItem);
		return updateItem;
	}

	/**
	 * Adds a new <code><b>UPDATE</b></code> item to this clause.
	 *
	 * @param path The path of the value to update
	 * @param newValue The string representation of the new value to parse and to convert into a
	 * {@link StateObject} representation
	 * @return The newly added {@link UpdateItemStateObject}
	 */
	public UpdateItemStateObject addItem(String path, String newValue) {
		UpdateItemStateObject updateItem = new UpdateItemStateObject(this);
		updateItem.setPath(path);
		updateItem.parseNewValue(newValue);
		addItem(updateItem);
		return updateItem;
	}

	/**
	 * Adds a new <code><b>UPDATE</b></code> item to this clause.
	 *
	 * @param paths The paths of the value to update
	 * @param newValue The {@link StateObject} representing the new value
	 * @return The newly added {@link UpdateItemStateObject}
	 */
	public UpdateItemStateObject addItem(String[] paths, StateObject newValue) {
		UpdateItemStateObject updateItem = new UpdateItemStateObject(this);
		updateItem.setPaths(paths);
		updateItem.setNewValue(newValue);
		addItem(updateItem);
		return updateItem;
	}

	/**
	 * Adds a new <code><b>UPDATE</b></code> item to this clause.
	 *
	 * @param paths The paths of the value to update
	 * @param newValue The string representation of the new value to parse and to convert into a
	 * {@link StateObject} representation
	 * @return The newly added {@link UpdateItemStateObject}
	 */
	public UpdateItemStateObject addItem(String[] paths, String newValue) {
		UpdateItemStateObject updateItem = new UpdateItemStateObject(this);
		updateItem.setPaths(paths);
		updateItem.parseNewValue(newValue);
		addItem(updateItem);
		return updateItem;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public UpdateItemStateObject addItem(UpdateItemStateObject item) {
		getChangeSupport().addItem(this, items, UPDATE_ITEMS_LIST, parent(item));
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addItems(List<? extends UpdateItemStateObject> items) {
		getChangeSupport().addItems(this, this.items, UPDATE_ITEMS_LIST, parent(items));
	}

	/**
	 * {@inheritDoc}
	 */
	public void addListChangeListener(String listName, IListChangeListener<UpdateItemStateObject> listener) {
		getChangeSupport().addListChangeListener(listName, listener);
	}

	/**
	 * Determines whether the children of this {@link StateObject} are equivalent to the children
	 * of the given one, i.e. the information of the {@link StateObject StateObjects} is the same.
	 *
	 * @param stateObject The {@link StateObject} to compare its children to this one's children
	 * @return <code>true</code> if both have equivalent children; <code>false</code> otherwise
	 */
	protected boolean areChildrenEquivalent(UpdateClauseStateObject stateObject) {

		int size = itemsSize();

		if (size != stateObject.itemsSize()) {
			return false;
		}

		for (int index = size; --index >= 0; ) {

			StateObject child1 = getItem(index);
			StateObject child2 = stateObject.getItem(index);

			if (!child1.isEquivalent(child2)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canMoveDown(UpdateItemStateObject item) {
		return getChangeSupport().canMoveDown(items, item);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canMoveUp(UpdateItemStateObject item) {
		return getChangeSupport().canMoveUp(items, item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UpdateClause getExpression() {
		return (UpdateClause) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return UPDATE;
	}

	/**
	 * {@inheritDoc}
	 */
	public UpdateItemStateObject getItem(int index) {
		return items.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UpdateStatementStateObject getParent() {
		return (UpdateStatementStateObject) super.getParent();
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
		items = new ArrayList<UpdateItemStateObject>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {
		return super.isEquivalent(stateObject) &&
		       areChildrenEquivalent((UpdateClauseStateObject) stateObject);
	}

	/**
	 * Determines whether the identification variable has been defined.
	 *
	 * @return <code>true</code> if the identification variable has been specified; <code>false</code>
	 * otherwise, which means it has been generated based on the abstract schema name
	 */
	public boolean isIdentificationVariableDefined() {
		return !getIdentificationVariableStateObject().isVirtual();
	}

	/**
	 * {@inheritDoc}
	 */
	public IterableListIterator<UpdateItemStateObject> items() {
		return new CloneListIterator<UpdateItemStateObject>(items);
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
	public UpdateItemStateObject moveDown(UpdateItemStateObject item) {
		getChangeSupport().moveDown(this, items, UPDATE_ITEMS_LIST, item);
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	public UpdateItemStateObject moveUp(UpdateItemStateObject item) {
		getChangeSupport().moveUp(this, items, UPDATE_ITEMS_LIST, item);
		return item;
	}

	/**
	 * Parses the given fragment, which represents a single update item, and creates the {@link
	 * StateObject} equivalent.
	 *
	 * @param jpqlFragment The portion of the query representing a single update item
	 */
	public void parse(String jpqlFragment) {
		UpdateItemStateObject stateObject = buildStateObject(jpqlFragment, InternalUpdateClauseBNF.ID);
		addItem(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeItem(UpdateItemStateObject stateObject) {
		getChangeSupport().removeItem(this, this.items, UPDATE_ITEMS_LIST, stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeItems(Collection<UpdateItemStateObject> items) {
		getChangeSupport().removeItems(this, this.items, UPDATE_ITEMS_LIST, items);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeListChangeListener(String listName, IListChangeListener<UpdateItemStateObject> listener) {
		getChangeSupport().removeListChangeListener(listName, listener);
	}

	/**
	 * Keeps a reference of the {@link UpdateClause parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link UpdateClause parsed object} representing an <code><b>UPDATE</b></code>
	 * clause
	 */
	public void setExpression(UpdateClause expression) {
		super.setExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {
		super.toTextInternal(writer);
		writer.append(SPACE);
		writer.append(SET);
		writer.append(SPACE);
		toStringItems(writer, items, true);
	}
}