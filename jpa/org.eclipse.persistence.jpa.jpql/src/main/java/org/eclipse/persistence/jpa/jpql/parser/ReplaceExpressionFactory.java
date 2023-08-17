/*
 * Copyright (c) 2006, 2023 Oracle and/or its affiliates. All rights reserved.
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
 * This {@link ReplaceExpressionFactory} creates a new {@link ReplaceExpression} when the
 * portion of the query to parse starts with <b>REPLACE</b>.
 *
 * @see ReplaceExpression
 *
 * @version 4.1
 * @since 4.1
 * @author Radek Felcman
 */
public final class ReplaceExpressionFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link ReplaceExpressionFactory}.
     */
    public static final String ID = Expression.REPLACE;

    /**
     * Creates a new <code>ReplaceExpressionFactory</code>.
     */
    public ReplaceExpressionFactory() {
        super(ID, Expression.REPLACE);
    }

    @Override
    protected AbstractExpression buildExpression(AbstractExpression parent,
                                                 WordParser wordParser,
                                                 String word,
                                                 JPQLQueryBNF queryBNF,
                                                 AbstractExpression expression,
                                                 boolean tolerant) {

        expression = new ReplaceExpression(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
