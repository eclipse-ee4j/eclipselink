/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
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
