/*******************************************************************************
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This expression represents a logical expression, which means the first and second expressions are
 * aggregated with either <b>AND</b> or <b>OR</b>.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class LogicalExpression extends CompoundExpression {

    /**
     * Creates a new <code>LogicalExpression</code>.
     *
     * @param parent The parent of this expression
     * @param identifier The identifier of this expression
     */
    protected LogicalExpression(AbstractExpression parent, String identifier) {
        super(parent, identifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(ConditionalExpressionBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
        return wordParser.character() == RIGHT_PARENTHESIS ||
               word.equalsIgnoreCase(OR)                   ||
               super.isParsingComplete(wordParser, word, expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String parseIdentifier(WordParser wordParser) {
        return getText();
    }
}
