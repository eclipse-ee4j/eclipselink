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

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * The <b>WHERE</b> clause of a query consists of a conditional expression used to select objects or
 * values that satisfy the expression. The <b>WHERE</b> clause restricts the result of a select
 * statement or the scope of an update or delete operation.
 *
 * <div><b>BNF:</b> <code>where_clause ::= WHERE conditional_expression</code><p></p></div>
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
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(WhereClauseBNF.ID);
    }
}
