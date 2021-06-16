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
 * One of the aggregate functions. The return type of this function is a <code>Long</code>.
 *
 * <div><b>BNF:</b> <code>expression ::= COUNT ([DISTINCT] identification_variable |
 *                                                                state_field_path_expression |
 *                                                                single_valued_object_path_expression)</code><p></p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class CountFunction extends AggregateFunction {

    /**
     * Creates a new <code>CountFunction</code>.
     *
     * @param parent The parent of this expression
     */
    public CountFunction(AbstractExpression parent) {
        super(parent, COUNT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractExpression buildEncapsulatedExpression(WordParser wordParser, String word) {

        if (KEY.equalsIgnoreCase(word)) {
            ExpressionFactory factory = getExpressionFactory(KeyExpressionFactory.ID);
            return factory.buildExpression(this, wordParser, word, getQueryBNF(), null, false);
        }

        if (VALUE.equalsIgnoreCase(word)) {
            ExpressionFactory factory = getExpressionFactory(ValueExpressionFactory.ID);
            return factory.buildExpression(this, wordParser, word, getQueryBNF(), null, false);
        }

        if (word.indexOf(DOT) == -1) {
            return new IdentificationVariable(this, word);
        }

        return super.buildEncapsulatedExpression(wordParser, word);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEncapsulatedExpressionQueryBNFId() {
        return InternalCountBNF.ID;
    }
}
