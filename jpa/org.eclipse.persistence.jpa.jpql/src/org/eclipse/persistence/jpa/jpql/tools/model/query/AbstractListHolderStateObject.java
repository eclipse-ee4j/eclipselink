/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.model.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.persistence.jpa.jpql.tools.model.IListChangeListener;
import org.eclipse.persistence.jpa.jpql.utility.iterable.ListIterable;
import org.eclipse.persistence.jpa.jpql.utility.iterable.SnapshotCloneListIterable;

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
        this.items = new ArrayList<T>(items);
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
        this.items = new ArrayList<>();
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
    @Override
    public <S extends T> S addItem(S item) {
        getChangeSupport().addItem(this, this.items, listName(), parent(item));
        return item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addItems(List<? extends T> items) {
        getChangeSupport().addItems(this, this.items, listName(), parent(items));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListChangeListener(String listName, IListChangeListener<T> listener) {
        getChangeSupport().addListChangeListener(listName, listener);
    }

    /**
     * Determines whether the children of this {@link StateObject} are equivalent to the children
     * of the given one, i.e. the information of the {@link StateObject StateObjects} is the same.
     *
     * @param stateObject The {@link StateObject} to compare its children to this one's children
     * @return <code>true</code> if both have equivalent children; <code>false</code> otherwise
     */
    protected boolean areChildrenEquivalent(AbstractListHolderStateObject<? extends StateObject> stateObject) {

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
    @Override
    public boolean canMoveDown(T stateObject) {
        return getChangeSupport().canMoveDown(items, stateObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canMoveUp(T stateObject) {
        return getChangeSupport().canMoveUp(items, stateObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getItem(int index) {
        return items.get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public ListIterable<? extends T> items() {
        return new SnapshotCloneListIterable<T>(items);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public T moveDown(T item) {
        getChangeSupport().moveDown(this, items, listName(), item);
        return item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T moveUp(T item) {
        getChangeSupport().moveUp(this, items, listName(), item);
        return item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeItem(T stateObject) {
        getChangeSupport().removeItem(this, items, listName(), stateObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeItems(Collection<T> items) {
        getChangeSupport().removeItems(this, this.items, listName(), items);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
