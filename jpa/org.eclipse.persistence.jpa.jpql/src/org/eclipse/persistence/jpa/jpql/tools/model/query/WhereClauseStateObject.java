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

import org.eclipse.persistence.jpa.jpql.parser.WhereClause;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>WHERE</b></code> clause of a query consists of a conditional expression used to
 * select objects or values that satisfy the expression. The <code><b>WHERE</b></code> clause
 * restricts the result of a select statement or the scope of an update or delete operation.
 *
 * <div><b>BNF:</b> <code>where_clause ::= WHERE conditional_expression</code><p></div>
 *
 * @see WhereClause
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class WhereClauseStateObject extends AbstractConditionalClauseStateObject {

    /**
     * Creates a new <code>WhereClauseStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public WhereClauseStateObject(AbstractModifyStatementStateObject parent) {
        super(parent);
    }

    /**
     * Creates a new <code>WhereClauseStateObject</code>.
     *
     * @param parent The parent of this state object, which cannot be <code>null</code>
     * @exception NullPointerException The given parent cannot be <code>null</code>
     */
    public WhereClauseStateObject(AbstractSelectStatementStateObject parent) {
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
    public WhereClause getExpression() {
        return (WhereClause) super.getExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdentifier() {
        return WHERE;
    }

    /**
     * Keeps a reference of the {@link WhereClause parsed object} object, which should only be done
     * when this object is instantiated during the conversion of a parsed JPQL query into {@link
     * StateObject StateObjects}.
     *
     * @param expression The {@link WhereClause parsed object} representing a <code><b>Where</b></code>
     * clause
     */
    public void setExpression(WhereClause expression) {
        super.setExpression(expression);
    }
}
