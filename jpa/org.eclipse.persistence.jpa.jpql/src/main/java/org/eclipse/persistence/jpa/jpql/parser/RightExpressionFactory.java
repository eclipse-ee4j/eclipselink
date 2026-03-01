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
 * This {@link RightExpressionFactory} creates a new {@link RightExpression} when the
 * portion of the query to parse starts with <b>RIGHT</b>.
 *
 * @see RightExpression
 *
 * @since 4.1
 * @author Radek Felcman
 */
public final class RightExpressionFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link RightExpressionFactory}.
     */
    public static final String ID = Expression.RIGHT;

    /**
     * Creates a new <code>RightExpressionFactory</code>.
     */
    public RightExpressionFactory() {
        super(ID, Expression.RIGHT);
    }

    @Override
    protected AbstractExpression buildExpression(AbstractExpression parent,
                                                 WordParser wordParser,
                                                 String word,
                                                 JPQLQueryBNF queryBNF,
                                                 AbstractExpression expression,
                                                 boolean tolerant) {

        expression = new RightExpression(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
