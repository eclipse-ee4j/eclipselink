/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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
 * This {@link IdExpressionFactory} creates a new {@link IdExpression} when the portion of
 * the query to parse starts with <b>OBJECT</b>.
 *
 * @see IdExpression
 *
 * @since 5.0
 * @author Radek Felcman
 */
public final class IdExpressionFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link IdExpressionFactory}.
     */
    public static final String ID = Expression.ID;

    /**
     * Creates a new <code>IdExpressionFactory</code>.
     */
    public IdExpressionFactory() {
        super(ID, Expression.ID);
    }

    @Override
    protected AbstractExpression buildExpression(AbstractExpression parent,
                                                 WordParser wordParser,
                                                 String word,
                                                 JPQLQueryBNF queryBNF,
                                                 AbstractExpression expression,
                                                 boolean tolerant) {

        expression = new IdExpression(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
