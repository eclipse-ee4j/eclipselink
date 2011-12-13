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
import org.eclipse.persistence.jpa.jpql.model.IListChangeListener;
import org.eclipse.persistence.jpa.jpql.util.iterator.CloneListIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

/**
 * The abstraction definition of a {@link StateObject} that holds onto a list of children, the
 * methods defined in {@link ListHolderStateObject} are automatically handled here.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractListHolderStateObject<T extends StateObject> extends AbstractStateObject
                                                                           implements ListHolderStateObject<T> {

	/**
	 * The list of children owned by this one.
	 */
	private List<T> items;

	/**
	 * Creates a new <code>AbstractListHolderStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractListHolderStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>AbstractListHolderStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param items The list of {@link StateObject StateObjects} to add as children to this one
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractListHolderStateObject(StateObject parent, List<? extends T> items) {
		super(parent);
		this.items.addAll(items);
		parent(items);
	}

	/**
	 * Creates a new <code>AbstractListHolderStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param items The list of {@link StateObject StateObjects} to add as children to this one
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractListHolderStateObject(StateObject parent, T... items) {
		super(parent);
		Collections.addAll(this.items, parent(items));
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
	public <S extends T> S addItem(S item) {
		getChangeSupport().addItem(this, this.items, listName(), parent(item));
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addItems(List<? extends T> items) {
		getChangeSupport().addItems(this, this.items, listName(), parent(items));
	}

	/**
	 * {@inheritDoc}
	 */
	public void addListChangeListener(String listName, IListChangeListener<T> listener) {
		getChangeSupport().addListChangeListener(listName, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canMoveDown(T stateObject) {
		return getChangeSupport().canMoveDown(items, stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canMoveUp(T stateObject) {
		return getChangeSupport().canMoveUp(items, stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public T getItem(int index) {
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
		items = new ArrayList<T>();
	}

	/**
	 * {@inheritDoc}
	 */
	public IterableListIterator<? extends T> items() {
		return new CloneListIterator<T>(items);
	}

	/**
	 * {@inheritDoc}
	 */
	public int itemsSize() {
		return items.size();
	}

	/**
	 * Returns the name that is uniquely identifying the list.
	 *
	 * @return The unique name identifying the list
	 */
	protected abstract String listName();

	/**
	 * {@inheritDoc}
	 */
	public T moveDown(T item) {
		getChangeSupport().moveDown(this, items, listName(), item);
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	public T moveUp(T item) {
		getChangeSupport().moveUp(this, items, listName(), item);
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeItem(T stateObject) {
		getChangeSupport().removeItem(this, items, listName(), stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeItems(Collection<T> items) {
		getChangeSupport().removeItems(this, this.items, listName(), items);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeListChangeListener(String listName, IListChangeListener<T> listener) {
		getChangeSupport().removeListChangeListener(listName, listener);
	}

	/**
	 * Adds to the given writer a crude string representation of the children of this one.
	 *
	 * @param writer The writer used to print out the string representation
	 * @param useComma Determines whether a comma should be added after each item, except after the
	 * last one
	 * @throws IOException This should never happens, only required because {@link Appendable} is
	 * used instead of {@link StringBuilder} for instance
	 */
	protected void toStringItems(Appendable writer, boolean useComma) throws IOException {
		toStringItems(writer, items, useComma);
	}
}