/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
 * This <code>StartWithClauseFactory</code> creates a new {@link StartWithClause} when the portion
 * of the query to parse starts with <b>START WITH</b>.
 *
 * @see StartWithClause
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public final class StartWithClauseFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link StartWithClauseFactory}.
     */
    public static final String ID = Expression.START_WITH;

    /**
     * Creates a new <code>StartWithClauseFactory</code>.
     */
    public StartWithClauseFactory() {
        super(ID, Expression.START_WITH);
    }

    @Override
    protected AbstractExpression buildExpression(AbstractExpression parent,
                                                 WordParser wordParser,
                                                 String word,
                                                 JPQLQueryBNF queryBNF,
                                                 AbstractExpression expression,
                                                 boolean tolerant) {

        expression = new StartWithClause(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
