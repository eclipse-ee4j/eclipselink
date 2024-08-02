/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2024 Contributors to the Eclipse Foundation. All rights reserved.
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

    @Override
    protected AbstractExpression buildExpression(AbstractExpression parent,
                                                 WordParser wordParser,
                                                 String word,
                                                 JPQLQueryBNF queryBNF,
                                                 final AbstractExpression expression,
                                                 boolean tolerant) {

        switch (wordParser.getWordType()) {

            case NUMERIC_LITERAL: {
                NumericLiteral numericLiteral = new NumericLiteral(parent, word);
                numericLiteral.parse(wordParser, tolerant);
                return numericLiteral;
            }

            case STRING_LITERAL: {
                StringLiteral stringLiteral = new StringLiteral(parent, word);
                stringLiteral.parse(wordParser, tolerant);
                return stringLiteral;
            }

            case INPUT_PARAMETER: {
                InputParameter inputParameter = new InputParameter(parent, word);
                inputParameter.parse(wordParser, tolerant);
                return inputParameter;
            }

            default: {
                // Path expression
                if (word.indexOf(AbstractExpression.DOT) > -1) {
                    char character = word.charAt(0);
                    AbstractPathExpression pathExpression;

                    if ((expression != null) && (character == AbstractExpression.DOT)) {
                        if (isCollection()) {
                            pathExpression = new CollectionValuedPathExpression(parent, expression, word);
                        } else {
                            pathExpression = new StateFieldPathExpression(parent, expression, word);
                        }
                    } else {
                        if (isCollection()) {
                            pathExpression = new CollectionValuedPathExpression(parent, word);
                        } else {
                            pathExpression = new StateFieldPathExpression(parent, word);
                        }
                    }

                    pathExpression.parse(wordParser, tolerant);
                    return pathExpression;
                }

                // Checks for invalid JPQL queries

                if (tolerant && getExpressionRegistry().isIdentifier(word)) {
                    // Before creating the expression, check to make sure it's not a function: 'identifier('
                    AbstractExpression identifierExpression;
                    identifierExpression = getIdentifierExpression(parent, wordParser, word, queryBNF, expression, tolerant);
                    if (identifierExpression != null) {
                        return new BadExpression(parent, identifierExpression);
                    }
                }

            }
        }

        return buildExpression(parent, wordParser, word, expression, tolerant);
    }

    /**
     * Determines
     *
     */
    protected boolean isCollection() {
        return false;
    }

    private AbstractExpression getIdentifierExpression(AbstractExpression parent, WordParser wordParser, String word, JPQLQueryBNF queryBNF, AbstractExpression expression, boolean tolerant) {
        ExpressionFactory factory = getExpressionRegistry().expressionFactoryForIdentifier(word);
        // TODO: Before creating the expression, check to make sure it's not a function: 'identifier('
        if (factory != null) {
            final AbstractExpression identifierExpression = factory.buildExpression(parent, wordParser, word, queryBNF, expression, tolerant);

            // if an invalid expression came from the factory, e.g. a function without expected arguments, ignore it
            return AbstractExpression.revertExpressionIfInvalid(identifierExpression, wordParser, word);
        }
        return null;
    }
}
