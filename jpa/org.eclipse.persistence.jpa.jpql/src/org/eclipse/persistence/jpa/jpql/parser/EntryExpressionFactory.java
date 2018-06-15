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
 * This {@link EntryExpressionFactory} creates a new {@link EntryExpression} when the portion of the
 * query to parse starts with <b>ENTRY</b>.
 *
 * @see EntryExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class EntryExpressionFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link EntryExpressionFactory}.
     */
    public static final String ID = Expression.ENTRY;

    /**
     * Creates a new <code>EntryExpressionFactory</code>.
     */
    public EntryExpressionFactory() {
        super(ID, Expression.ENTRY);
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

        expression = new EntryExpression(parent);
        expression.parse(wordParser, tolerant);

        if (wordParser.character() == AbstractExpression.DOT) {
            expression = new StateFieldPathExpression(parent, expression);
            expression.parse(wordParser, tolerant);
        }

        return expression;
    }
}
