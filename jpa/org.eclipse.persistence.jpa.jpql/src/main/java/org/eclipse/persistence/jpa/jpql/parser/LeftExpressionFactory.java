/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
 * This {@link LeftExpressionFactory} creates a new {@link LeftExpression} when the
 * portion of the query to parse starts with <b>LEFT(</b>.
 *
 * @see LeftExpression
 *
 * @since 4.1
 * @author Radek Felcman
 */
public final class LeftExpressionFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link LeftExpressionFactory}.
     */
    public static final String ID = Expression.LEFT;

    /**
     * Creates a new <code>LeftExpressionFactory</code>.
     */
    public LeftExpressionFactory() {
        super(ID, Expression.LEFT);
    }

    @Override
    protected AbstractExpression buildExpression(AbstractExpression parent,
                                                 WordParser wordParser,
                                                 String word,
                                                 JPQLQueryBNF queryBNF,
                                                 AbstractExpression expression,
                                                 boolean tolerant) {

        expression = new LeftExpression(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
