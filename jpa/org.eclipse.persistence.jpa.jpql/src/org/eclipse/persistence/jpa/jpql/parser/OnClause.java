/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * Returns an expression that allows a join <b>ON</b> clause to be defined.
 * <p>
 * New to EclipseLink 2.4 and JPA 2.1.
 *
 * <div><b>BNF:</b> <code>join_condition ::= ON conditional_expression</code></div>
 * <p>
 * Example: <code>SELECT e FROM Employee e LEFT JOIN e.projects p ON p.budget {@literal >} 10000</code>
 *
 * @version 2.4
 * @since 2.4
 * @author James Sutherland
 */
public final class OnClause extends AbstractConditionalClause {

    /**
     * Creates a new <code>OnClause</code>.
     *
     * @param parent The parent of this expression
     */
    public OnClause(AbstractExpression parent) {
        super(parent, ON);
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
        return getQueryBNF(OnClauseBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
        return word.equalsIgnoreCase(LEFT)  ||
               word.equalsIgnoreCase(OUTER) ||
               word.equalsIgnoreCase(FETCH) ||
               word.equalsIgnoreCase(INNER) ||
               word.equalsIgnoreCase(JOIN)  ||
               super.isParsingComplete(wordParser, word, expression);
    }
}
