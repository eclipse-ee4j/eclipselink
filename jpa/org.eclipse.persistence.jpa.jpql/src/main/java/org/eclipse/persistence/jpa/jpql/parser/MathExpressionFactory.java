/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     04/21/2022: Tomas Kraus
//       - Issue 1474: Update JPQL Grammar for JPA 2.2, 3.0 and 3.1
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * Math functions expressions factories.
 */
public abstract class MathExpressionFactory extends ExpressionFactory {

    public static final class Ceiling extends MathExpressionFactory {

        /** The unique identifier of this {@link MathExpressionFactory.Ceiling}. */
        public static final String ID = Expression.CEILING;

        public Ceiling() {
            super(ID);
        }

        @Override
        protected AbstractExpression buildExpression(
                AbstractExpression parent, WordParser wordParser, String word,
                JPQLQueryBNF queryBNF, AbstractExpression expression, boolean tolerant
        ) {
            return super.buildExpression(
                    parent, wordParser, word, queryBNF, new MathExpression.Ceiling(parent), tolerant
            );
        }

    }

    public static final class Floor extends MathExpressionFactory {

        /** The unique identifier of this {@link MathExpressionFactory.Floor}. */
        public static final String ID = Expression.FLOOR;

        public Floor() {
            super(ID);
        }

        @Override
        protected AbstractExpression buildExpression(
                AbstractExpression parent, WordParser wordParser, String word,
                JPQLQueryBNF queryBNF, AbstractExpression expression, boolean tolerant
        ) {
            return super.buildExpression(
                    parent, wordParser, word, queryBNF, new MathExpression.Floor(parent), tolerant
            );
        }

    }

    /**
     * Creates a new instance of math function expression factory.
     *
     * @param id The unique identifier of math function expression factory
     *           and also math function expression identifier
     */
    MathExpressionFactory(String id) {
        super(id, id);
    }

    @Override
    protected AbstractExpression buildExpression(
            AbstractExpression parent, WordParser wordParser, String word,
            JPQLQueryBNF queryBNF, AbstractExpression expression, boolean tolerant
    ) {
        expression.parse(wordParser, tolerant);
        return expression;
    }


}
