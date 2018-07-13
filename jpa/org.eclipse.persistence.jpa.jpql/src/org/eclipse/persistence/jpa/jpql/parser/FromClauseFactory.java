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
 * This {@link FromClauseFactory} creates a new {@link FromClause} when the portion of the query to
 * parse starts with <b>FROM</b>.
 *
 * @see FromClause
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class FromClauseFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link FromClauseFactory}.
     */
    public static final String ID = Expression.FROM;

    /**
     * Creates a new <code>FromClauseFactory</code>.
     */
    public FromClauseFactory() {
        super(ID, Expression.FROM);
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

        expression = new FromClause(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
