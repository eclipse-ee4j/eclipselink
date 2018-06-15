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
 * This factory is responsible to return the right literal expression.
 *
 * @see StringLiteral
 * @see InputParameter
 * @see NumericLiteral
 * @see KeywordExpression
 * @see StateFieldPathExpression
 * @see IdentificationVariable
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class AbstractLiteralExpressionFactory extends ExpressionFactory {

    /**
     * Creates a new <code>AbstractLiteralExpressionFactory</code>.
     *
     * @param id The unique identifier of this {@link ExpressionFactory}
     */
    protected AbstractLiteralExpressionFactory(String id) {
        super(id);
    }

    /**
     * Creates the actual {@link AbstractExpression} this factory manages.
     *
     * @param parent The parent {@link AbstractExpression}
     * @param wordParser The text to parse based on the current position of the cursor
     * @param word The current word being parsed
     * @param expression During the parsing, it is possible the first part of an expression was
     * parsed which needs to be used as a sub-expression of the newly created expression
     * @return A new {@link AbstractExpression} representing the portion or the totality of the
     * text held by {@link WordParser} starting at the cursor position
     */
    protected abstract AbstractExpression buildExpression(AbstractExpression parent,
                                                          WordParser wordParser,
                                                          String word,
                                                          AbstractExpression expression,
                                                          boolean tolerant);

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

        switch (wordParser.getWordType()) {

            case NUMERIC_LITERAL: {
                expression = new NumericLiteral(parent, word);
                expression.parse(wordParser, tolerant);
                return expression;
            }

            case STRING_LITERAL: {
                expression = new StringLiteral(parent, word);
                expression.parse(wordParser, tolerant);
                return expression;
            }

            case INPUT_PARAMETER: {
                expression = new InputParameter(parent, word);
                expression.parse(wordParser, tolerant);
                return expression;
            }
        }

        // Path expression
        if (word.indexOf(AbstractExpression.DOT) > -1) {
            char character = word.charAt(0);

            if ((expression != null) && (character == AbstractExpression.DOT)) {
                if (isCollection()) {
                    expression = new CollectionValuedPathExpression(parent, expression, word);
                }
                else {
                    expression = new StateFieldPathExpression(parent, expression, word);
                }
            }
            else {
                if (isCollection()) {
                    expression = new CollectionValuedPathExpression(parent, word);
                }
                else {
                    expression = new StateFieldPathExpression(parent, word);
                }
            }

            expression.parse(wordParser, tolerant);
            return expression;
        }

        // Checks for invalid JPQL queries
        ExpressionRegistry registry = getExpressionRegistry();

        if (tolerant && registry.isIdentifier(word)) {
            ExpressionFactory factory = registry.expressionFactoryForIdentifier(word);
            // TODO: Before creating the expression, check to make sure it's not a function: 'identifier('
            if (factory != null) {
                expression = factory.buildExpression(parent, wordParser, word, queryBNF, expression, tolerant);

                if (expression != null) {
                    return new BadExpression(parent, expression);
                }
            }
        }

        return buildExpression(parent, wordParser, word, expression, tolerant);
    }

    /**
     * Determines
     *
     * @return
     */
    protected boolean isCollection() {
        return false;
    }
}
