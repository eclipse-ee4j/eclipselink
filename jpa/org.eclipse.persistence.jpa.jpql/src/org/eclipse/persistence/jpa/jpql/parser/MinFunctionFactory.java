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
 * This {@link MinFunctionFactory} creates a new {@link MinFunction} when the portion of the query
 * to parse starts with <b>MIN</b>.
 *
 * @see MinFunction
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class MinFunctionFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link MinFunctionFactory}.
     */
    public static final String ID = Expression.MIN;

    /**
     * Creates a new <code>MinFunctionFactory</code>.
     */
    public MinFunctionFactory() {
        super(ID, Expression.MIN);
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

        expression = new MinFunction(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
