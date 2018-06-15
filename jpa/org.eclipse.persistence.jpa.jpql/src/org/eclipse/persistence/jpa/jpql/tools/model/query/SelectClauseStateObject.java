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
import java.util.List;
import org.eclipse.persistence.jpa.jpql.parser.InternalSelectExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectExpressionBNF;
import org.eclipse.persistence.jpa.jpql.tools.model.IListChangeListener;
import org.eclipse.persistence.jpa.jpql.tools.model.ISelectExpressionStateObjectBuilder;
import org.eclipse.persistence.jpa.jpql.utility.iterable.ListIterable;
import org.eclipse.persistence.jpa.jpql.utility.iterable.SnapshotCloneListIterable;
import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;

/**
 * The <code><b>SELECT</b></code> statement queries data from entities, which determines the type of
 * the objects or values to be selected.
 *
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
     * {@inheritDoc}
     */
    @Override
    public <S extends StateObject> S addItem(S item) {
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
    @Override
    public void addItems(List<? extends StateObject> items) {
        getChangeSupport().addItems(this, this.items, SELECT_ITEMS_LIST, parent(items));
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
    protected boolean areChildrenEquivalent(SelectClauseStateObject stateObject) {

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
     * Creates and returns a new {@link ISelectExpressionStateObjectBuilder} that can be used to
     * programmatically create a single select expression and once the expression is complete,
     * {@link ISelectExpressionStateObjectBuilder#commit()} will push the {@link StateObject}
     * representation of that expression as this clause's select expression.
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
    public SelectClause getExpression() {
        return (SelectClause) super.getExpression();
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
    @Override
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
    @Override
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
    @Override
    public boolean isEquivalent(StateObject stateObject) {
        return super.isEquivalent(stateObject) &&
               areChildrenEquivalent((SelectClauseStateObject) stateObject);
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
        getChangeSupport().moveDown(this, items, SELECT_ITEMS_LIST, item);
        return item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StateObject moveUp(StateObject item) {
        getChangeSupport().moveUp(this, items, SELECT_ITEMS_LIST, item);
        return item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void parse(String jpqlFragment) {
        List<StateObject> stateObjects = buildStateObjects(jpqlFragment, InternalSelectExpressionBNF.ID);
        addItems(stateObjects);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeItem(StateObject stateObject) {
        getChangeSupport().removeItem(this, items, SELECT_ITEMS_LIST, stateObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeItems(Collection<StateObject> items) {
        getChangeSupport().removeItems(this, this.items, SELECT_ITEMS_LIST, items);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListChangeListener(String listName, IListChangeListener<StateObject> listener) {
        getChangeSupport().removeListChangeListener(listName, listener);
    }

    /**
     * Keeps a reference of the {@link SelectClause parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link SelectClause parsed object} representing a <code><b>SELECT</b></code>
     * clause
     */
    public void setExpression(SelectClause expression) {
        super.setExpression(expression);
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
