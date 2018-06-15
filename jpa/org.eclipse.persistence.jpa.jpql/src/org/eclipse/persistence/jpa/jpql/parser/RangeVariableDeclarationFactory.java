/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * This {@link RangeVariableDeclaration} creates a new {@link RangeVariableDeclaration}.
 *
 * @see RangeVariableDeclaration
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class RangeVariableDeclarationFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link RangeVariableDeclarationFactory}.
     */
    public static final String ID = "range_variable_declaration";

    /**
     * Creates a new <code>RangeVariableDeclarationFactory</code>.
     */
    public RangeVariableDeclarationFactory() {
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

        ExpressionRegistry registry = getExpressionRegistry();

        // When the tolerant mode is turned on, parse the invalid portion of the query,
        // Order/Group are two exceptions to this rule
        // (expression != null) skip this check when parsing the first range variable declaration
        if (tolerant &&
             expression != null             &&
             // TODO: TOTALLY REDO THIS PART TO HANDLE A GENERIC WAY TO PARSE COMPOSITE CLAUSE IDENTIFIERS
            !word.equalsIgnoreCase("order") &&
            !word.equalsIgnoreCase("group") &&
            !word.equalsIgnoreCase("start") &&
            !word.equalsIgnoreCase("connect") &&
            registry.isIdentifier(word)) {

            ExpressionFactory factory = registry.expressionFactoryForIdentifier(word);

            if (factory == null) {
                return null;
            }

            expression = factory.buildExpression(parent, wordParser, word, queryBNF, expression, tolerant);

            if (expression != null) {
                return new BadExpression(parent, expression);
            }
        }

        expression = new RangeVariableDeclaration(parent);
        expression.parse(wordParser, tolerant);
        return expression;
    }
}
