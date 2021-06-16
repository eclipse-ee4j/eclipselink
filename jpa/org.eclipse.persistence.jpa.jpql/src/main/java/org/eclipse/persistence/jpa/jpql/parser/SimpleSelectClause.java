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
 * The <b>SELECT</b> statement queries data from entities. This version simply does not handle
 * <b>OBJECT</b> and <b>NEW</b> identifiers. It is used from within another expression.
 *
 * <div><b>BNF:</b> <code>simple_select_clause ::= SELECT [DISTINCT] simple_select_expression</code><p></p></div>
 *
 * @see AbstractSelectClause
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class SimpleSelectClause extends AbstractSelectClause {

    /**
     * Creates a new <code>SimpleSelectClause</code>.
     *
     * @param parent The parent of this expression
     */
    public SimpleSelectClause(AbstractExpression parent) {
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
        return getQueryBNF(SimpleSelectClauseBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSelectItemQueryBNFId() {
        return InternalSimpleSelectExpressionBNF.ID;
    }
}
