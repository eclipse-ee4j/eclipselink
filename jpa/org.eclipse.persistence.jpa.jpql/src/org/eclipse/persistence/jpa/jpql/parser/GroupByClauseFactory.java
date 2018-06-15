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
 * This {@link GroupByClauseFactory} creates a new {@link GroupByClause} when the portion of the
 * query to parse starts with <b>GROUP BY</b>.
 *
 * @see GroupByClause
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class GroupByClauseFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link GroupByClauseFactory}.
     */
    public static final String ID = Expression.GROUP_BY;

    /**
     * Creates a new <code>GroupByClauseFactory</code>.
     */
    public GroupByClauseFactory() {
        super(ID, Expression.GROUP_BY);
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

        expression = new GroupByClause(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
