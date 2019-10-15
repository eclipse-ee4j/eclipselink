/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * A <code><b>START WITH</b></code> clause is optional and specifies the root row(s) of the hierarchy.
 * If this clause is omitted, then Oracle uses all rows in the table as root rows. The <code><b>START
 * WITH</b></code> condition can contain a subquery, but it cannot contain a scalar subquery expression.
 *
 * <div><b>BNF:</b> <code>start_with_clause ::= <b>START WITH</b> conditional_expression</code><p></div>
 *
 * @see HierarchicalQueryClause
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public final class StartWithClause extends AbstractConditionalClause {

    /**
     * Creates a new <code>StartWithClause</code>.
     *
     * @param parent The parent of this expression
     */
    public StartWithClause(AbstractExpression parent) {
        super(parent, Expression.START_WITH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        acceptUnknownVisitor(visitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(StartWithClauseBNF.ID);
    }
}
