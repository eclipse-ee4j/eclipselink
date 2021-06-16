/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
 * <div><b>BNF:</b> <code>start_with_clause ::= <b>START WITH</b> conditional_expression</code><p></p></div>
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
