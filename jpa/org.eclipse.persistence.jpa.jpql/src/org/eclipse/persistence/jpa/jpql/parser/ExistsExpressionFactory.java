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
 * This {@link ExistsExpressionFactory} creates a new {@link ExistsExpression} when the portion of
 * the query to parse starts with <b>EXISTS</b>.
 *
 * @see ExistsExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class ExistsExpressionFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link ExistsExpressionFactory}.
     */
    public static final String ID = Expression.EXISTS;

    /**
     * Creates a new <code>ExistsExpressionFactory</code>.
     */
    public ExistsExpressionFactory() {
        super(ID, Expression.EXISTS);
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

        expression = new ExistsExpression(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
