/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This <code>HierarchicalQueryClauseFactory</code> creates a new {@link HierarchicalQueryClause}
 * when the portion of the query to parse starts with either <code><b>VERSIONS</b></code> or
 * <code><b>AS OF</b></code>.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class HierarchicalQueryClauseFactory extends ExpressionFactory {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "hierarchical_query_clause";

    /**
     * Creates a new <code>HierarchicalQueryClauseFactory</code>.
     */
    public HierarchicalQueryClauseFactory() {
        super(ID, Expression.START_WITH, Expression.CONNECT_BY);
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

        expression = new HierarchicalQueryClause(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
