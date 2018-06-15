/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The <b>SELECT</b> clause queries data from entities.
 *
 * <div><b>BNF:</b> <code>select_clause ::= SELECT [DISTINCT] select_expression {, select_expression}*</code><p></div>
 *
 * @see AbstractSelectClause
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class SelectClause extends AbstractSelectClause {

    /**
     * Creates a new <code>SelectClause</code>.
     *
     * @param parent The parent of this expression
     */
    public SelectClause(AbstractExpression parent) {
        super(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(SelectClauseBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSelectItemQueryBNFId() {
        return InternalSelectExpressionBNF.ID;
    }
}
