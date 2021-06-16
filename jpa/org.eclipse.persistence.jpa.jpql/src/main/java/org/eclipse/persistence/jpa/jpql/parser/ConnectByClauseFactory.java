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
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This <code>ConnectByClauseFactory</code> creates a new {@link ConnectByClause} when the portion of
 * the query to parse starts with <b>CONNECT BY</b>.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public final class ConnectByClauseFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link ConnectByClauseFactory}.
     */
    public static final String ID = CONNECT_BY;

    /**
     * Creates a new <code>ConnectByClauseFactory</code>.
     */
    public ConnectByClauseFactory() {
        super(ID, CONNECT_BY);
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

        expression = new ConnectByClause(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
