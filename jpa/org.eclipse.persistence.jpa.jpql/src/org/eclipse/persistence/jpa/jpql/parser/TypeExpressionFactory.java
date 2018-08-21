/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * This {@link TypeExpressionFactory} creates a new {@link TypeExpression} when the portion of the
 * query to parse starts with <b>TYPE</b>.
 *
 * @see TypeExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class TypeExpressionFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link TypeExpressionFactory}.
     */
    public static final String ID = Expression.TYPE;

    /**
     * Creates a new <code>TypeExpressionFactory</code>.
     */
    public TypeExpressionFactory() {
        super(ID, Expression.TYPE);
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

        expression = new TypeExpression(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
