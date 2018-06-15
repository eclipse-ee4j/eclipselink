/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * This {@link OnClauseFactory} creates a new {@link OnClause} when the portion of the query to
 * parse starts with <b>ON</b>.
 *
 * @see WhereClause
 *
 * @version 2.4
 * @since 2.4
 * @author James
 */
public final class OnClauseFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link OnClauseFactory}.
     */
    public static final String ID = Expression.ON;

    /**
     * Creates a new <code>OnClauseFactory</code>.
     */
    public OnClauseFactory() {
        super(ID, Expression.ON);
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

        expression = new OnClause(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
