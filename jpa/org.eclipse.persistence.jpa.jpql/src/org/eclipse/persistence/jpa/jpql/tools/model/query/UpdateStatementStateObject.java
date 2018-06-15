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

import org.eclipse.persistence.jpa.jpql.parser.UpdateStatement;

/**
 * The <code><b>UPDATE</b></code> clause of a query consists of a conditional expression used to
 * select objects or values that satisfy the expression. The <code><b>UPDATE</b></code> clause
 * restricts the result of a select statement or the scope of an update operation.
 *
 * <div><b>BNF:</b> <code>update_statement ::= update_clause [where_clause]</code><p></div>
 *
 * @see UpdateStatement
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class UpdateStatementStateObject extends AbstractModifyStatementStateObject {

    /**
     * Creates a new <code>UpdateStatementStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public UpdateStatementStateObject(JPQLQueryStateObject parent) {
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
     * Adds a new item to the <code><b>UPDATE</b></code> clause.
     *
     * @param path The path of the value to update
     * @return The newly added {@link UpdateItemStateObject}
     */
    public UpdateItemStateObject addItem(String path) {
        return getModifyClause().addItem(path);
    }

    /**
     * Adds a new item to the <code><b>UPDATE</b></code> clause.
     *
     * @param path The path of the value to update
     * @param newValue The {@link StateObject} representation of the new value
     * @return The newly added {@link UpdateItemStateObject}
     */
    public UpdateItemStateObject addItem(String path, StateObject newValue) {
        return getModifyClause().addItem(path, newValue);
    }

    /**
     * Adds a new item to the <code><b>UPDATE</b></code> clause.
     *
     * @param path The path of the value to update
     * @param newValue The string representation of the new value to parse and to convert into a
     * {@link StateObject} representation
     * @return The newly added {@link UpdateItemStateObject}
     */
    public UpdateItemStateObject addItem(String path, String newValue) {
        return getModifyClause().addItem(path, newValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractModifyClauseStateObject buildModifyClause() {
        return new UpdateClauseStateObject(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateStatement getExpression() {
        return (UpdateStatement) super.getExpression();
    }

    /**
     * Returns the state object representing the <code><b>UPDATE</b></code> clause.
     *
     * @return The state object representing the <code><b>UPDATE</b></code> clause, which is never
     * <code>null</code>
     */
    @Override
    public UpdateClauseStateObject getModifyClause() {
        return (UpdateClauseStateObject) super.getModifyClause();
    }

    /**
     * Keeps a reference of the {@link UpdateStatement parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link UpdateStatement parsed object} representing an <code><b>UpdateStatement</b></code>
     * statement
     */
    public void setExpression(UpdateStatement expression) {
        super.setExpression(expression);
    }
}
