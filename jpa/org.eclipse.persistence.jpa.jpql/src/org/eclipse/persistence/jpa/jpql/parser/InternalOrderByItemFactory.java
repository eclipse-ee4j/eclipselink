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
 * This {@link InternalOrderByItemFactory} creates either a {@link StateFieldPathExpression} or
 * an {@link IdentificationVariable}.
 *
 * @see OrderByItem
 *
 * @version 2.5.1
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class InternalOrderByItemFactory extends ExpressionFactory {

    /**
     * The unique identifier of this <code>InternalOrderByItemFactory</code>.
     */
    public static final String ID = "internal_orderby_item";

    /**
     * Creates a new <code>InternalOrderByItemFactory</code>.
     */
    public InternalOrderByItemFactory() {
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

        if (word.indexOf(AbstractExpression.DOT) > -1) {
            expression = new StateFieldPathExpression(parent, word);
            expression.parse(wordParser, tolerant);
        }
        else {
            ExpressionFactory factory = getExpressionRegistry().getExpressionFactory(LiteralExpressionFactory.ID);
            expression = factory.buildExpression(parent, wordParser, word, queryBNF, expression, tolerant);
        }

        return expression;
    }
}
