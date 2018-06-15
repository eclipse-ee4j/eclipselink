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
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class RangeDeclarationFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link OrderByClauseFactory}.
     */
    public static final String ID = "range_declaration";

    /**
     * Creates a new <code>RangeDeclarationFactory</code>.
     */
    public RangeDeclarationFactory() {
        super(ID);
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

        if (word.indexOf('.') != -1) {
            expression = new CollectionValuedPathExpression(parent, word);
        }
        else {
            expression = new AbstractSchemaName(parent, word);
        }

        expression.parse(wordParser, tolerant);
        return expression;
    }
}
