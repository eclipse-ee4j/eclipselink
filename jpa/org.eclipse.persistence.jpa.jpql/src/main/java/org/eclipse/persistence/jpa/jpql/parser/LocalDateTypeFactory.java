/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     04/21/2022: Tomas Kraus
//       - Issue 317: Implement LOCAL DATE, LOCAL TIME and LOCAL DATETIME.
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This {@link LocalDateTypeFactory} creates a new {@link LocalDateTime} when the portion
 * of the query to parse starts with <b>LOCAL</b>.
 *
 * @see LocalExpression
 * @see LocalDateTime
 */
public class LocalDateTypeFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link LocalDateTypeFactory}.
     */
    public static final String ID = "local_datetime_type";

    /**
     * Creates a new <code>LocalDateTypeFactory</code>.
     */
    public LocalDateTypeFactory() {
        super(ID, Expression.DATE, Expression.DATETIME, Expression.TIME);
    }

    @Override
    protected AbstractExpression buildExpression(AbstractExpression parent,
                                                 WordParser wordParser,
                                                 String word,
                                                 JPQLQueryBNF queryBNF,
                                                 AbstractExpression expression,
                                                 boolean tolerant) {

        expression = new LocalDateTime(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }

}
