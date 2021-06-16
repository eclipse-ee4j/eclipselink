/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This {@link CastExpressionFactory} creates a new {@link CastExpression} when the portion of the
 * query to parse starts with <b>CAST</b>.
 *
 * @see CastExpression
 *
 * @version 2.4
 * @since 2.4
 * @author James Sutherland
 */
public final class CastExpressionFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link CastExpressionFactory}.
     */
    public static final String ID = Expression.CAST;

    /**
     * Creates a new <code>TrimExpressionFactory</code>.
     */
    public CastExpressionFactory() {
        super(ID, Expression.CAST);
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

        expression = new CastExpression(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
