/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     06/02/2023: Radek Felcman
//       - Issue 1885: Implement new JPQLGrammar for upcoming Jakarta Persistence 3.2
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This {@link ExpressionFactory} creates a new expression when the portion of the query to parse
 * starts with a string identifier. It is possible the expression to parse is also a {@link StringLiteral}.
 *
 * @version 4.1
 * @since 4.1
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class StringExpressionFactory extends ExpressionFactory {

    /**
     * This {@link ExpressionVisitor} is used to check if the {@link Expression} passed to this
     * factory is an {@link ConcatPipesExpression}.
     */
    private StringExpressionVisitor visitor;

    /**
     * The unique identifier of this {@link StringExpressionFactory}.
     */
    public static final String ID = "||";

    /**
     * Creates a new <code>AbstractStringExpressionFactory</code>.
     */
    public StringExpressionFactory() {
        super(ID, Expression.CONCAT_PIPES);
    }

    /**
     * Creates the {@link Expression} this factory for which it is responsible.
     *
     * @param parent The parent of the new {@link Expression}
     * @param character The arithmetic character
     * @return A new {@link CompoundExpression}
     */
    private CompoundExpression buildExpression(AbstractExpression parent, String character) {
        if ("||".equals(character)) {
            return new ConcatPipesExpression(parent);
        }
        return null;
    }

    @Override
    protected final AbstractExpression buildExpression(AbstractExpression parent,
                                                       WordParser wordParser,
                                                       String word,
                                                       JPQLQueryBNF queryBNF,
                                                       AbstractExpression expression,
                                                       boolean tolerant) {

        String character = word.substring(0,1);
        //"|" implies concat operator which contains two characters
        if ("|".equals(character)) {
            character = word.substring(0,2);
        }

        // Concat operator '||'
        if ("||".equals(character)) {
            ConcatPipesExpression concatPipesExpression = new ConcatPipesExpression(parent);
            concatPipesExpression.setLeftExpression(expression);
            concatPipesExpression.parse(wordParser, tolerant);
            return concatPipesExpression;
        }

        // Create the StringExpression
        CompoundExpression compoundExpression = buildExpression(parent, character);
        compoundExpression.setLeftExpression(expression);
        compoundExpression.parse(wordParser, tolerant);
        return compoundExpression;
    }

    private StringExpressionVisitor visitor() {
        if (visitor == null) {
            visitor = new StringExpressionVisitor();
        }
        return visitor;
    }

    // Made static final for performance reasons.
    /**
     * This {@link ExpressionVisitor} is used to check if the {@link Expression} passed to this
     * factory is a {@link ConcatPipesExpression}.
     */
    private static final class StringExpressionVisitor extends AbstractExpressionVisitor {

        /**
         * This flag is turned on if the {@link Expression} visited is {@link OrExpression}.
         */
        boolean found;

        @Override
        public void visit(ConcatPipesExpression expression) {
            found = true;
        }
    }
}
