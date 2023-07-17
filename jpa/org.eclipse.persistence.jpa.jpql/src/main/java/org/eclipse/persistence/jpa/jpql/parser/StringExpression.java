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
 * This expression represents a String expression, which means the first and second expressions
 * are aggregated with a String sign.
 *
 * @see ConcatPipesExpression
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class StringExpression extends CompoundExpression {

    /**
     * Creates a new <code>StringExpression</code>.
     *
     * @param parent The parent of this expression
     */
    protected StringExpression(AbstractExpression parent, String identifier) {
        super(parent, identifier);
    }

    @Override
    public JPQLQueryBNF findQueryBNF(Expression expression) {
        return getParent().findQueryBNF(expression);
    }

    /**
     * Returns the string sign this expression is actually representing.
     *
     * @return The single character value of the string sign
     */
    public final String getStringSign() {
        return getText();
    }

    @Override
    public String getLeftExpressionQueryBNFId() {
        return StringExpressionBNF.ID;
    }

    @Override
    public final JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(StringTermBNF.ID);
    }

    @Override
    public final String getRightExpressionQueryBNFId() {
        return StringTermBNF.ID;
    }

    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

        String character = word.substring(0,1);

        // Concat will create a chain of operations
        if ("||".equals(character)) {
            return false;
        }

        return (expression != null);
    }

    @Override
    protected final String parseIdentifier(WordParser wordParser) {
        return getText();
    }
}
