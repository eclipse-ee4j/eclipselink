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
 * The <b>HAVING</b> construct enables conditions to be specified that further restrict the query
 * result as restrictions upon the groups.
 *
 * <div><b>BNF:</b> <code>having_clause ::= HAVING conditional_expression</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class HavingClause extends AbstractConditionalClause {

    /**
     * Creates a new <code>HavingClause</code>.
     *
     * @param parent The parent of this expression
     */
    public HavingClause(AbstractExpression parent) {
        super(parent, HAVING);
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
        return getQueryBNF(HavingClauseBNF.ID);
    }
}
