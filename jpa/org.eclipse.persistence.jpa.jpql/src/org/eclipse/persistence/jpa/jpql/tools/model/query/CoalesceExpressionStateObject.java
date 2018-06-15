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

import static org.eclipse.persistence.jpa.jpql.parser.Expression.COALESCE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.jpa.jpql.parser.CoalesceExpression;
import org.eclipse.persistence.jpa.jpql.parser.ScalarExpressionBNF;
import org.eclipse.persistence.jpa.jpql.tools.model.IListChangeListener;
import org.eclipse.persistence.jpa.jpql.utility.iterable.ListIterable;
import org.eclipse.persistence.jpa.jpql.utility.iterable.SnapshotCloneListIterable;

/**
 * A <code><b>COALESCE</b></code> expression returns <code>null</code> if all its arguments evaluate
 * to <code>null</code>, and the value of the first non-<code>null</code> argument otherwise.
 * <p>
 * The return type is the type returned by the arguments if they are all of the same type, otherwise
 * it is undetermined.
 *
 * <div><b>BNF:</b> <code>coalesce_expression::= COALESCE(scalar_expression {, scalar_expression}+)</code></div>
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
        this.items = new ArrayList<>(parent(items));
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
        this.items = new ArrayList<>();
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
        this.items = new ArrayList<>();
        parseItemInternal(items);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public <S extends StateObject> S addItem(S item) {
        getChangeSupport().addItem(this, items, STATE_OBJECTS_LIST, parent(item));
        return item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addItems(List<? extends StateObject> items) {
        getChangeSupport().addItems(this, this.items, STATE_OBJECTS_LIST, parent(items));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListChangeListener(String listName, IListChangeListener<StateObject> listener) {
        getChangeSupport().addListChangeListener(listName, listener);
    }

    /**
     * Determines whether the children of this {@link StateObject} are equivalent to the children
     * of the given one, i.e. the information of the {@link StateObject StateObjects} is the same.
     *
     * @param stateObject The {@link StateObject} to compare its children to this one's children
     * @return <code>true</code> if both have equivalent children; <code>false</code> otherwise
     */
    protected boolean areChildrenEquivalent(CoalesceExpressionStateObject stateObject) {

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
    public boolean canMoveDown(StateObject item) {
        return getChangeSupport().canMoveDown(items, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public StateObject getItem(int index) {
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
        items = new ArrayList<StateObject>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEquivalent(StateObject stateObject) {
        return super.isEquivalent(stateObject) &&
               areChildrenEquivalent((CoalesceExpressionStateObject) stateObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListIterable<StateObject> items() {
        return new SnapshotCloneListIterable<StateObject>(items);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int itemsSize() {
        return items.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StateObject moveDown(StateObject item) {
        getChangeSupport().moveDown(this, items, STATE_OBJECTS_LIST, item);
        return item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public void removeItem(StateObject item) {
        getChangeSupport().removeItem(this, items, STATE_OBJECTS_LIST, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeItems(Collection<StateObject> items) {
        getChangeSupport().removeItems(this, this.items, STATE_OBJECTS_LIST, items);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
