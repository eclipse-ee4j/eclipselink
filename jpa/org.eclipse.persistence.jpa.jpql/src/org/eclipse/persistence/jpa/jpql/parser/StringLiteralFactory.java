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
 * This {@link StringLiteralFactory} is responsible to parse a sub-query starting with a single quote.
 *
 * @see StringLiteral
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class StringLiteralFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link StringLiteralFactory}.
     */
    public static final String ID = "string-literal";

    /**
     * Creates a new <code>StringLiteralFactory</code>.
     */
    public StringLiteralFactory() {
        super(ID, Expression.QUOTE);
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

        expression = new StringLiteral(parent, word);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
