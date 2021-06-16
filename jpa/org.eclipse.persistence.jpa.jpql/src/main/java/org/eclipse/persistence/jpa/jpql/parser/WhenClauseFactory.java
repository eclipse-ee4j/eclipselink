/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
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
 * This {@link WhenClauseFactory} creates a new {@link WhenClause} when the portion of the query to
 * parse starts with <b>WHEN</b>.
 *
 * @see WhenClause
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class WhenClauseFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link WhenClauseFactory}.
     */
    public static final String ID = Expression.WHEN;

    /**
     * Creates a new <code>WhenClauseFactory</code>.
     */
    public WhenClauseFactory() {
        super(ID, Expression.WHEN);
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

        expression = new WhenClause(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
