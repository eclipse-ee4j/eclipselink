/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * The <b>WHERE</b> clause of a query consists of a conditional expression used to select objects or
 * values that satisfy the expression. The <b>WHERE</b> clause restricts the result of a select
 * statement or the scope of an update or delete operation.
 *
 * <div><b>BNF:</b> <code>where_clause ::= WHERE conditional_expression</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class WhereClause extends AbstractConditionalClause {

    /**
     * Creates a new <code>WhereClause</code>.
     *
     * @param parent The parent of this expression
     */
    public WhereClause(AbstractExpression parent) {
        super(parent, WHERE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

        if (word.equalsIgnoreCase(SELECT) ||
            word.equalsIgnoreCase(FROM)) {

            return false;
        }

        return super.isParsingComplete(wordParser, word, expression);
    }

    /**
     * {@inheritDoc}
     */
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(WhereClauseBNF.ID);
    }
}
