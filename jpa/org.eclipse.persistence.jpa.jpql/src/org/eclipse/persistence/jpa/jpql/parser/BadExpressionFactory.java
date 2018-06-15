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
 * This {@link BadExpressionFactory} creates a new {@link BadExpression} when the portion of the
 * query to parse is malformed.
 *
 * @see AbsExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class BadExpressionFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link BadExpressionFactory}.
     */
    public static final String ID = "bad";

    /**
     * Creates a new <code>BadExpressionFactory</code>.
     */
    public BadExpressionFactory() {
        super(ID);
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

        expression = new BadExpression(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
