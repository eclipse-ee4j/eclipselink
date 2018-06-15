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
 * This {@link DeleteStatementFactory} creates a new {@link DeleteStatement} when the portion of the
 * query to parse starts with <b>DELETE FROM</b>.
 *
 * @see DeleteStatement
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class DeleteStatementFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link DeleteStatementFactory}.
     */
    public static final String ID = "delete-statement";

    /**
     * Creates a new <code>DeleteStatementFactory</code>.
     */
    public DeleteStatementFactory() {
        super(ID, Expression.DELETE,
                  Expression.DELETE_FROM);
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

        expression = new DeleteStatement(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
