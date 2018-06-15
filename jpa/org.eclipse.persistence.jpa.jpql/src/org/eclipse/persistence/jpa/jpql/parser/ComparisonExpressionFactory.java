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
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This {@link ComparisonExpressionFactory} creates a new {@link ComparisonExpression} when the
 * portion of the query to parse starts with <b>&lt;</b>, <b>&gt;</b>, <b>{@literal <>}</b>, <b>&lt;=</b>,
 * <b>&gt;=</b> or <b>=</b>.
 *
 * @see ComparisonExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ComparisonExpressionFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link ComparisonExpressionFactory}.
     */
    public static final String ID = "comparison";

    /**
     * Creates a new <code>ComparisonExpressionFactory</code>.
     */
    public ComparisonExpressionFactory() {
        super(ID, DIFFERENT,
                  EQUAL,
                  GREATER_THAN,
                  GREATER_THAN_OR_EQUAL,
                  LOWER_THAN,
                  LOWER_THAN_OR_EQUAL);
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

        String operator = null;

        // First look for known operator (<, <=, =, =>, <>)
        switch (wordParser.character()) {

            // <, <=, <>
            case '<': {
                switch (wordParser.character(wordParser.position() + 1)) {
                    case '=': {
                        operator = LOWER_THAN_OR_EQUAL;
                        break;
                    }
                    case '>': {
                        operator = DIFFERENT;
                        break;
                    }
                    default: {
                        operator = LOWER_THAN;
                        break;
                    }
                }
                break;
            }

            // >, >=
            case '>': {
                switch (wordParser.character(wordParser.position() + 1)) {
                    case '=': {
                        operator = GREATER_THAN_OR_EQUAL;
                        break;
                    }
                    default: {
                        operator = GREATER_THAN;
                        break;
                    }
                }
                break;
            }

            // =
            case '=': {
                operator = EQUAL;
                break;
            }
        }

        // Now look for additional identifiers
        if (operator == null) {
            for (String identifier : identifiers()) {
                if (wordParser.startsWith(identifier)) {
                    operator = identifier;
                    break;
                }
            }
        }

        // The operator was found, create the expression
        if (operator != null) {

            ComparisonExpression comparisonExpression = new ComparisonExpression(parent, operator);
            comparisonExpression.parse(wordParser, tolerant);

            if (expression != null) {
                comparisonExpression.setLeftExpression(expression);
            }

            return comparisonExpression;
        }

        return null;
    }
}
