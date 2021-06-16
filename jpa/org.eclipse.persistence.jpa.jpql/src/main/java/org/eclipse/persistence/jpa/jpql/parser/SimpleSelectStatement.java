/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
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
 * <div><b>BNFL</b> <code>subquery ::= simple_select_clause subquery_from_clause [where_clause] [groupby_clause] [having_clause]</code><p></p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class SimpleSelectStatement extends AbstractSelectStatement {

    /**
     * Creates a new <code>SimpleSelectStatement</code>.
     *
     * @param parent The parent of this expression
     */
    public SimpleSelectStatement(AbstractExpression parent) {
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
    protected SimpleFromClause buildFromClause() {
        return new SimpleFromClause(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SimpleSelectClause buildSelectClause() {
        return new SimpleSelectClause(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(SubqueryBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldManageSpaceAfterClause() {
        return false;
    }
}
