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
 * This {@link SelectStatementFactory} creates a new {@link SelectStatement} when the portion of the
 * query to parse starts with <b>SELECT</b>.
 *
 * @see SelectStatement
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class SelectStatementFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link SelectStatementFactory}.
     */
    public static final String ID = "select-statement";

    /**
     * Creates a new <code>SelectStatementFactory</code>.
     */
    public SelectStatementFactory() {
        super(ID, Expression.SELECT);
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

        expression = new SelectStatement(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
