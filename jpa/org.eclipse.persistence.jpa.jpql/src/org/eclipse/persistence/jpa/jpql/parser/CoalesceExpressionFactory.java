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
 * This {@link CoalesceExpressionFactory} creates a new {@link CoalesceExpression} when the portion
 * of the query to parse starts with <b>COALESCE</b>.
 *
 * @see CoalesceExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class CoalesceExpressionFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link CoalesceExpressionFactory}.
     */
    public static final String ID = Expression.COALESCE;

    /**
     * Creates a new <code>CoalesceExpressionFactory</code>.
     */
    public CoalesceExpressionFactory() {
        super(ID, Expression.COALESCE);
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

        expression = new CoalesceExpression(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
