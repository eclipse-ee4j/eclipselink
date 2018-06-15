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

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This {@link WhereClauseFactory} creates a new {@link WhereClause} when the portion of the query
 * to parse starts with <b>WHERE</b>.
 *
 * @see WhereClause
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class WhereClauseFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link WhereClauseFactory}.
     */
    public static final String ID = Expression.WHERE;

    /**
     * Creates a new <code>WhereClauseFactory</code>.
     */
    public WhereClauseFactory() {
        super(ID, Expression.WHERE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractExpression buildExpression(AbstractExpression parent,
                                                 WordParser wordParser,
                                                 String word,
                                                 JPQLQueryBNF queryBNF,
                                                 AbstractExpression expression,
                                                 boolean tolerant) {

        expression = new WhereClause(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
