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
 * This {@link DateTimeFactory} creates a new {@link DateTime} when the portion of the query to
 * parse starts with <b>CURRENT_DATE</b>, <b>CURRENT_TIME</b>, <b>CURRENT_TIMESTAMP</b> or with the
 * JDBC escape format used for date/time/timestamp.
 *
 * @see DateTime
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class DateTimeFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link DateTimeFactory}.
     */
    public static final String ID = "functions_returning_datetime";

    /**
     * Creates a new <code>DateTimeFactory</code>.
     */
    public DateTimeFactory() {
        super(ID, Expression.CURRENT_DATE,
                  Expression.CURRENT_TIME,
                  Expression.CURRENT_TIMESTAMP,
                  "{");
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

        expression = new DateTime(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
