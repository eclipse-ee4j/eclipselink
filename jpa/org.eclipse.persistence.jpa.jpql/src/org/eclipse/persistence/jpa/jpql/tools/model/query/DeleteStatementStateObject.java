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

import org.eclipse.persistence.jpa.jpql.parser.DeleteStatement;

/**
 * Bulk delete operation apply to entities of a single entity class (together with its subclasses,
 * if any).
 *
 * <div><b>BNF:</b> <code>delete_statement ::= delete_clause [where_clause]</code><p></div>
 *
 * @see DeleteStatement
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class DeleteStatementStateObject extends AbstractModifyStatementStateObject {

    /**
     * Creates a new <code>DeleteStatementStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public DeleteStatementStateObject(JPQLQueryStateObject parent) {
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
    protected AbstractModifyClauseStateObject buildModifyClause() {
        return new DeleteClauseStateObject(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteStatement getExpression() {
        return (DeleteStatement) super.getExpression();
    }

    /**
     * Returns the state object representing the <code><b>DELETE</b></code> clause.
     *
     * @return The state object representing the <code><b>DELETE</b></code> clause, which is never
     * <code>null</code>
     */
    @Override
    public DeleteClauseStateObject getModifyClause() {
        return (DeleteClauseStateObject) super.getModifyClause();
    }

    /**
     * Keeps a reference of the {@link DeleteStatement parsed object} object, which should only be
     * done when this object is instantiated during the conversion of a parsed JPQL query into
     * {@link StateObject StateObjects}.
     *
     * @param expression The {@link DeleteStatement parsed object} representing a <code><b>DELETE</b></code>
     * statement
     */
    public void setExpression(DeleteStatement expression) {
        super.setExpression(expression);
    }
}
