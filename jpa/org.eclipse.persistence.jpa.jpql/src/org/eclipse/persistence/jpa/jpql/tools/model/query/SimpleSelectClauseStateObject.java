/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.model.query;

import java.io.IOException;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectExpressionBNF;
import org.eclipse.persistence.jpa.jpql.tools.model.ISimpleSelectExpressionStateObjectBuilder;
import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <b>SELECT</b> statement queries data from entities. This version simply does not handle
 * <b>OBJECT</b> and <b>NEW</b> identifiers. It is used from within another expression.
 *
 * <div><b>BNF:</b> <code>simple_select_clause ::= SELECT [DISTINCT] simple_select_expression</code><p></div>
 *
 * @see SimpleSelectClause
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // unused used for the import statement: see bug 330740
public class SimpleSelectClauseStateObject extends AbstractSelectClauseStateObject {

    /**
     * The builder is cached during the creation of the select expression.
     */
    private ISimpleSelectExpressionStateObjectBuilder builder;

    /**
     * The state object representing the single select expression.
     */
    private StateObject stateObject;

    /**
     * Notifies the select expression property has changed.
     */
    public static final String SELECT_ITEM_PROPERTY = "stateObject";

    /**
     * Creates a new <code>SubQuerySelectClauseStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public SimpleSelectClauseStateObject(SimpleSelectStatementStateObject parent) {
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
        if (stateObject != null) {
            children.add(stateObject);
        }
    }

    /**
     * Creates and returns a new {@link ISimpleSelectExpressionStateObjectBuilder} that can be used
     * to programmatically create a single select expression and once the expression is complete,
     * {@link ISimpleSelectExpressionStateObjectBuilder#commit()} will push the {@link StateObject}
     * representation of that expression as this clause's select expression.
     *
     * @return A new builder that can be used to quickly create a select expression
     */
    public ISimpleSelectExpressionStateObjectBuilder getBuilder() {
        if (builder == null) {
            builder = getQueryBuilder().buildStateObjectBuilder(this);
        }
        return builder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleSelectClause getExpression() {
        return (SimpleSelectClause) super.getExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleFromClauseStateObject getFromClause() {
        return (SimpleFromClauseStateObject) super.getFromClause();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleSelectStatementStateObject getParent() {
        return (SimpleSelectStatementStateObject) super.getParent();
    }

    /**
     * Returns
     *
     * @return
     */
    public StateObject getSelectItem() {
        return stateObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasSelectItem() {
        return stateObject != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEquivalent(StateObject stateObject) {

        if (super.isEquivalent(stateObject)) {
            SimpleSelectClauseStateObject select = (SimpleSelectClauseStateObject) stateObject;
            return areEquivalent(this.stateObject, select.stateObject);
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void parse(String jpqlFragment) {
        StateObject stateObject = buildStateObject(jpqlFragment, SimpleSelectExpressionBNF.ID);
        setSelectItem(stateObject);
    }

    /**
     * Keeps a reference of the {@link SimpleSelectClause parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link SimpleSelectClause parsed object} representing a subquery
     * <code><b>SELECT</b></code> clause
     */
    public void setExpression(SimpleSelectClause expression) {
        super.setExpression(expression);
    }

    /**
     * Sets the given {@link StateObject} as this clause's select item.
     *
     * @param stateObject The {@link StateObject} representing the single select item
     */
    public void setSelectItem(StateObject stateObject) {
        StateObject oldStateObject = this.stateObject;
        this.stateObject = parent(stateObject);
        firePropertyChanged(SELECT_ITEM_PROPERTY, oldStateObject, stateObject);
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

        // Select expression
        if (stateObject != null) {
            writer.append(SPACE);
            stateObject.toString(writer);
        }
    }
}
