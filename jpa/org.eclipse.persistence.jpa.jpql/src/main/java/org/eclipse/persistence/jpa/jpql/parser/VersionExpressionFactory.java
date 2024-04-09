/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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
 * This {@link VersionExpressionFactory} creates a new {@link VersionExpression} when the portion of
 * the query to parse starts with <b>VERSION</b>.
 *
 * @see VersionExpression
 *
 * @since 5.0
 * @author Radek Felcman
 */
public final class VersionExpressionFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link VersionExpressionFactory}.
     */
    public static final String ID = Expression.VERSION;

    /**
     * Creates a new <code>VersionExpressionFactory</code>.
     */
    public VersionExpressionFactory() {
        super(ID, Expression.VERSION);
    }

    @Override
    protected AbstractExpression buildExpression(AbstractExpression parent,
                                                 WordParser wordParser,
                                                 String word,
                                                 JPQLQueryBNF queryBNF,
                                                 AbstractExpression expression,
                                                 boolean tolerant) {

        if(super.isIdentifier(wordParser, word)) {
        expression = new VersionExpression(parent);
        expression.parse(wordParser, tolerant);
        } else {
            expression = null;
        }
        return expression;
    }
}
