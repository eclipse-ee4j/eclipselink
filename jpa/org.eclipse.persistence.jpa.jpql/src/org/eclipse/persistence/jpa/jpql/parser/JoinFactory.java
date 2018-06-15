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
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This {@link JoinFactory} creates a new {@link Join} when the portion of the query to parse starts
 * with <b>JOIN</b> or <b>FETCH JOIN</b>, respectively.
 *
 * @see Join
 *
 * @version 2.5.2
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JoinFactory extends ExpressionFactory {

    /**
     * The unique identifier of this {@link JoinFactory}.
     */
    public static final String ID = JOIN;

    /**
     * Creates a new <code>JoinFactory</code>.
     */
    public JoinFactory() {
        super(ID, LEFT,
                  INNER,
                  JOIN,
                  OUTER,
                  FETCH);
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

        int index = wordParser.position();

        // TODO: There must be a better way to parse all the JOIN identifiers with a generic
        //       parsing behavior without parsing something like "LEFT JOIN OUTER" has a single
        //       expression but multiple join expressions

        // JOIN and JOIN FETCH
        if (wordParser.startsWithIdentifier(JOIN, index)) {
            index += 4;
            index += wordParser.whitespaceCount(index);

            // JOIN FETCH
            if (wordParser.startsWithIdentifier(FETCH, index)) {
                expression = new Join(parent, JOIN_FETCH);
            }
            // JOIN
            else {
                expression = new Join(parent, JOIN);
            }
        }
        // LEFT
        else if (wordParser.startsWithIdentifier(LEFT)) {
            index += 4;
            index += wordParser.whitespaceCount(index);

            // LEFT OUTER
            if (wordParser.startsWithIdentifier(OUTER, index)) {
                index += 5;
                index += wordParser.whitespaceCount(index);

                if (wordParser.startsWithIdentifier(JOIN, index)) {
                    index += 4;
                    index += wordParser.whitespaceCount(index);

                    // LEFT OUTER JOIN FETCH
                    if (wordParser.startsWithIdentifier(FETCH, index)) {
                        expression = new Join(parent, LEFT_OUTER_JOIN_FETCH);
                    }
                    // LEFT OUTER JOIN
                    else {
                        expression = new Join(parent, LEFT_OUTER_JOIN);
                    }
                }
                // LEFT OUTER INNER
                else if (wordParser.startsWithIdentifier(INNER, index)) {
                    index += 5;
                    index += wordParser.whitespaceCount(index);

                    if (wordParser.startsWithIdentifier(JOIN, index)) {
                        index += 4;
                        index += wordParser.whitespaceCount(index);

                        // LEFT OUTER INNER JOIN FETCH
                        if (wordParser.startsWithIdentifier(FETCH, index)) {
                            expression = new Join(parent, "LEFT OUTER INNER JOIN FETCH");
                        }
                        // LEFT OUTER INNER JOIN
                        else {
                            expression = new Join(parent, "LEFT OUTER INNER JOIN");
                        }
                    }
                    else {
                        expression = new Join(parent, "LEFT OUTER INNER");
                    }
                }
                else {
                    expression = new Join(parent, "LEFT OUTER");
                }
            }
            // LEFT JOIN
            else if (wordParser.startsWithIdentifier(JOIN, index)) {
                index += 4;
                index += wordParser.whitespaceCount(index);

                // LEFT JOIN FETCH
                if (wordParser.startsWithIdentifier(FETCH, index)) {
                    expression = new Join(parent, LEFT_JOIN_FETCH);
                }
                // LEFT JOIN
                else {
                    expression = new Join(parent, LEFT_JOIN);
                }
            }
            // LEFT INNER
            else if (wordParser.startsWithIdentifier(INNER, index)) {
                index += 5;
                index += wordParser.whitespaceCount(index);

                // LEFT INNER JOIN
                if (wordParser.startsWithIdentifier(JOIN, index)) {
                    index += 5;
                    index += wordParser.whitespaceCount(index);

                    // LEFT INNER JOIN FETCH
                    if (wordParser.startsWithIdentifier(FETCH, index)) {
                        expression = new Join(parent, "LEFT INNER JOIN FETCH");
                    }
                    // LEFT INNER JOIN
                    else {
                        expression = new Join(parent, "LEFT INNER JOIN");
                    }
                }
            }
            // LEFT
            else {
                expression = new Join(parent, LEFT);
            }
        }
        // INNER JOIN and INNER JOIN FETCH
        else if (wordParser.startsWithIdentifier(INNER, index)) {
            index += 5;
            index += wordParser.whitespaceCount(index);

            if (wordParser.startsWithIdentifier(JOIN, index)) {
                index += 4;
                index += wordParser.whitespaceCount(index);

                // INNER JOIN FETCH
                if (wordParser.startsWithIdentifier(FETCH, index)) {
                    expression = new Join(parent, INNER_JOIN_FETCH);
                }
                // INNER JOIN
                else {
                    expression = new Join(parent, INNER_JOIN);
                }
            }
            // INNER
            else {
                expression = new Join(parent, INNER);
            }
        }
        // OUTER JOIN and OUTER JOIN FETCH
        // OUTER INNER JOIN and OUTER INNER JOIN FETCH
        else if (wordParser.startsWithIdentifier(OUTER, index)) {
            index += 5;
            index += wordParser.whitespaceCount(index);

            // OUTER JOIN and OUTER JOIN FETCH
            if (wordParser.startsWithIdentifier(JOIN, index)) {
                index += 4;
                index += wordParser.whitespaceCount(index);

                // OUTER JOIN FETCH
                if (wordParser.startsWithIdentifier(FETCH, index)) {
                    expression = new Join(parent, "OUTER JOIN FETCH");
                }
                // OUTER JOIN
                else {
                    expression = new Join(parent, "OUTER JOIN");
                }
            }
            // OUTER INNER JOIN and OUTER INNER JOIN FETCH
            else if (wordParser.startsWithIdentifier(INNER, index)) {
                index += 5;
                index += wordParser.whitespaceCount(index);

                if (wordParser.startsWithIdentifier(JOIN, index)) {
                    index += 4;
                    index += wordParser.whitespaceCount(index);

                    // OUTER INNER JOIN FETCH
                    if (wordParser.startsWithIdentifier(FETCH, index)) {
                        expression = new Join(parent, "OUTER INNER JOIN FETCH");
                    }
                    // INNER JOIN
                    else {
                        expression = new Join(parent, "OUTER INNER JOIN");
                    }
                }
            }
            // OUTER
            else {
                expression = new Join(parent, OUTER);
            }
        }
        else {
            return null;
        }

        expression.parse(wordParser, tolerant);
        return expression;
    }
}
